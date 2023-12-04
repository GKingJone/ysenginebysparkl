/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.FullJsonResponseHandler;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.FullJsonResponseHandler.JsonResponse;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpClient.HttpResponseFuture;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpStatus;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpStatus.Family;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpUriBuilder;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.Request.Builder;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.StaticBodyGenerator;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.StatusResponseHandler;
/*     */ import com.facebook.presto.jdbc.internal.airlift.http.client.StatusResponseHandler.StatusResponse;
/*     */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Splitter;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Sets;
/*     */ import java.io.Closeable;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.annotation.concurrent.ThreadSafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class StatementClient
/*     */   implements Closeable
/*     */ {
/*  76 */   private static final Splitter SESSION_HEADER_SPLITTER = Splitter.on('=').limit(2).trimResults();
/*  77 */   private static final String USER_AGENT_VALUE = StatementClient.class.getSimpleName() + "/" + 
/*     */   
/*  79 */     (String)MoreObjects.firstNonNull(StatementClient.class.getPackage().getImplementationVersion(), "unknown");
/*     */   
/*     */   private final HttpClient httpClient;
/*     */   private final FullJsonResponseHandler<QueryResults> responseHandler;
/*     */   private final boolean debug;
/*     */   private final String query;
/*  85 */   private final AtomicReference<QueryResults> currentResults = new AtomicReference();
/*  86 */   private final Map<String, String> setSessionProperties = new ConcurrentHashMap();
/*  87 */   private final Set<String> resetSessionProperties = Sets.newConcurrentHashSet();
/*  88 */   private final Map<String, String> addedPreparedStatements = new ConcurrentHashMap();
/*  89 */   private final Set<String> deallocatedPreparedStatements = Sets.newConcurrentHashSet();
/*  90 */   private final AtomicReference<String> startedtransactionId = new AtomicReference();
/*  91 */   private final AtomicBoolean clearTransactionId = new AtomicBoolean();
/*  92 */   private final AtomicBoolean closed = new AtomicBoolean();
/*  93 */   private final AtomicBoolean gone = new AtomicBoolean();
/*  94 */   private final AtomicBoolean valid = new AtomicBoolean(true);
/*     */   private final String timeZoneId;
/*     */   private final long requestTimeoutNanos;
/*     */   private final String user;
/*     */   
/*     */   public StatementClient(HttpClient httpClient, JsonCodec<QueryResults> queryResultsCodec, ClientSession session, String query)
/*     */   {
/* 101 */     Objects.requireNonNull(httpClient, "httpClient is null");
/* 102 */     Objects.requireNonNull(queryResultsCodec, "queryResultsCodec is null");
/* 103 */     Objects.requireNonNull(session, "session is null");
/* 104 */     Objects.requireNonNull(query, "query is null");
/*     */     
/* 106 */     this.httpClient = httpClient;
/* 107 */     this.responseHandler = FullJsonResponseHandler.createFullJsonResponseHandler(queryResultsCodec);
/* 108 */     this.debug = session.isDebug();
/* 109 */     this.timeZoneId = session.getTimeZoneId();
/* 110 */     this.query = query;
/* 111 */     this.requestTimeoutNanos = session.getClientRequestTimeout().roundTo(TimeUnit.NANOSECONDS);
/* 112 */     this.user = session.getUser();
/*     */     
/* 114 */     Request request = buildQueryRequest(session, query);
/* 115 */     FullJsonResponseHandler.JsonResponse<QueryResults> response = (FullJsonResponseHandler.JsonResponse)httpClient.execute(request, this.responseHandler);
/*     */     
/* 117 */     if ((response.getStatusCode() != HttpStatus.OK.code()) || (!response.hasValue())) {
/* 118 */       throw requestFailedException("starting query", request, response);
/*     */     }
/*     */     
/* 121 */     processResponse(response);
/*     */   }
/*     */   
/*     */ 
/*     */   private Request buildQueryRequest(ClientSession session, String query)
/*     */   {
/* 127 */     Request.Builder builder = prepareRequest(Request.Builder.preparePost(), HttpUriBuilder.uriBuilderFrom(session.getServer()).replacePath("/v1/statement").build()).setBodyGenerator(StaticBodyGenerator.createStaticBodyGenerator(query, StandardCharsets.UTF_8));
/*     */     
/* 129 */     if (session.getSource() != null) {
/* 130 */       builder.setHeader("X-Presto-Source", session.getSource());
/*     */     }
/* 132 */     if (session.getCatalog() != null) {
/* 133 */       builder.setHeader("X-Presto-Catalog", session.getCatalog());
/*     */     }
/* 135 */     if (session.getSchema() != null) {
/* 136 */       builder.setHeader("X-Presto-Schema", session.getSchema());
/*     */     }
/* 138 */     builder.setHeader("X-Presto-Time-Zone", session.getTimeZoneId());
/* 139 */     builder.setHeader("X-Presto-Language", session.getLocale().toLanguageTag());
/*     */     
/* 141 */     Map<String, String> property = session.getProperties();
/* 142 */     for (Iterator localIterator = property.entrySet().iterator(); localIterator.hasNext();) { entry = (Entry)localIterator.next();
/* 143 */       builder.addHeader("X-Presto-Session", (String)entry.getKey() + "=" + (String)entry.getValue());
/*     */     }
/*     */     Entry<String, String> entry;
/* 146 */     Object statements = session.getPreparedStatements();
/* 147 */     for (Entry<String, String> entry : ((Map)statements).entrySet()) {
/* 148 */       builder.addHeader("X-Presto-Prepared-Statement", urlEncode((String)entry.getKey()) + "=" + urlEncode((String)entry.getValue()));
/*     */     }
/*     */     
/* 151 */     builder.setHeader("X-Presto-Transaction-Id", session.getTransactionId() == null ? "NONE" : session.getTransactionId());
/*     */     
/* 153 */     return builder.build();
/*     */   }
/*     */   
/*     */   public String getQuery()
/*     */   {
/* 158 */     return this.query;
/*     */   }
/*     */   
/*     */   public String getTimeZoneId()
/*     */   {
/* 163 */     return this.timeZoneId;
/*     */   }
/*     */   
/*     */   public boolean isDebug()
/*     */   {
/* 168 */     return this.debug;
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */   {
/* 173 */     return this.closed.get();
/*     */   }
/*     */   
/*     */   public boolean isGone()
/*     */   {
/* 178 */     return this.gone.get();
/*     */   }
/*     */   
/*     */   public boolean isFailed()
/*     */   {
/* 183 */     return ((QueryResults)this.currentResults.get()).getError() != null;
/*     */   }
/*     */   
/*     */   public StatementStats getStats()
/*     */   {
/* 188 */     return ((QueryResults)this.currentResults.get()).getStats();
/*     */   }
/*     */   
/*     */   public QueryResults current()
/*     */   {
/* 193 */     Preconditions.checkState(isValid(), "current position is not valid (cursor past end)");
/* 194 */     return (QueryResults)this.currentResults.get();
/*     */   }
/*     */   
/*     */   public QueryResults finalResults()
/*     */   {
/* 199 */     Preconditions.checkState((!isValid()) || (isFailed()), "current position is still valid");
/* 200 */     return (QueryResults)this.currentResults.get();
/*     */   }
/*     */   
/*     */   public Map<String, String> getSetSessionProperties()
/*     */   {
/* 205 */     return ImmutableMap.copyOf(this.setSessionProperties);
/*     */   }
/*     */   
/*     */   public Set<String> getResetSessionProperties()
/*     */   {
/* 210 */     return ImmutableSet.copyOf(this.resetSessionProperties);
/*     */   }
/*     */   
/*     */   public Map<String, String> getAddedPreparedStatements()
/*     */   {
/* 215 */     return ImmutableMap.copyOf(this.addedPreparedStatements);
/*     */   }
/*     */   
/*     */   public Set<String> getDeallocatedPreparedStatements()
/*     */   {
/* 220 */     return ImmutableSet.copyOf(this.deallocatedPreparedStatements);
/*     */   }
/*     */   
/*     */   public String getStartedtransactionId()
/*     */   {
/* 225 */     return (String)this.startedtransactionId.get();
/*     */   }
/*     */   
/*     */   public boolean isClearTransactionId()
/*     */   {
/* 230 */     return this.clearTransactionId.get();
/*     */   }
/*     */   
/*     */   public boolean isValid()
/*     */   {
/* 235 */     return (this.valid.get()) && (!isGone()) && (!isClosed());
/*     */   }
/*     */   
/*     */   private Request.Builder prepareRequest(Request.Builder builder, URI nextUri)
/*     */   {
/* 240 */     builder.setHeader("X-Presto-User", this.user);
/* 241 */     builder.setHeader("User-Agent", USER_AGENT_VALUE)
/* 242 */       .setUri(nextUri);
/*     */     
/* 244 */     return builder;
/*     */   }
/*     */   
/*     */   public boolean advance()
/*     */   {
/* 249 */     URI nextUri = current().getNextUri();
/* 250 */     if ((isClosed()) || (nextUri == null)) {
/* 251 */       this.valid.set(false);
/* 252 */       return false;
/*     */     }
/*     */     
/* 255 */     Request request = prepareRequest(Request.Builder.prepareGet(), nextUri).build();
/*     */     
/* 257 */     Exception cause = null;
/* 258 */     long start = System.nanoTime();
/* 259 */     long attempts = 0L;
/*     */     
/*     */     do
/*     */     {
/* 263 */       if (attempts > 0L) {
/*     */         try {
/* 265 */           TimeUnit.MILLISECONDS.sleep(attempts * 100L);
/*     */         }
/*     */         catch (InterruptedException e) {
/*     */           try {
/* 269 */             close();
/*     */           }
/*     */           finally {
/* 272 */             Thread.currentThread().interrupt();
/*     */           }
/* 274 */           throw new RuntimeException("StatementClient thread was interrupted");
/*     */         }
/*     */       }
/* 277 */       attempts += 1L;
/*     */       
/*     */       try
/*     */       {
/* 281 */         response = (FullJsonResponseHandler.JsonResponse)this.httpClient.execute(request, this.responseHandler);
/*     */       } catch (RuntimeException e) {
/*     */         FullJsonResponseHandler.JsonResponse<QueryResults> response;
/* 284 */         cause = e;
/* 285 */         continue;
/*     */       }
/*     */       FullJsonResponseHandler.JsonResponse<QueryResults> response;
/* 288 */       if ((response.getStatusCode() == HttpStatus.OK.code()) && (response.hasValue())) {
/* 289 */         processResponse(response);
/* 290 */         return true;
/*     */       }
/*     */       
/* 293 */       if (response.getStatusCode() != HttpStatus.SERVICE_UNAVAILABLE.code()) {
/* 294 */         throw requestFailedException("fetching next", request, response);
/*     */       }
/*     */       
/* 297 */     } while ((System.nanoTime() - start < this.requestTimeoutNanos) && (!isClosed()));
/*     */     
/* 299 */     this.gone.set(true);
/* 300 */     throw new RuntimeException("Error fetching next", cause);
/*     */   }
/*     */   
/*     */   private void processResponse(FullJsonResponseHandler.JsonResponse<QueryResults> response)
/*     */   {
/* 305 */     for (String setSession : response.getHeaders("X-Presto-Set-Session")) {
/* 306 */       List<String> keyValue = SESSION_HEADER_SPLITTER.splitToList(setSession);
/* 307 */       if (keyValue.size() == 2)
/*     */       {
/*     */ 
/* 310 */         this.setSessionProperties.put(keyValue.get(0), keyValue.size() > 1 ? (String)keyValue.get(1) : ""); }
/*     */     }
/* 312 */     for (String clearSession : response.getHeaders("X-Presto-Clear-Session")) {
/* 313 */       this.resetSessionProperties.add(clearSession);
/*     */     }
/*     */     
/* 316 */     for (String entry : response.getHeaders("X-Presto-Added-Prepare")) {
/* 317 */       List<String> keyValue = SESSION_HEADER_SPLITTER.splitToList(entry);
/* 318 */       if (keyValue.size() == 2)
/*     */       {
/*     */ 
/* 321 */         this.addedPreparedStatements.put(urlDecode((String)keyValue.get(0)), urlDecode((String)keyValue.get(1))); }
/*     */     }
/* 323 */     for (String entry : response.getHeaders("X-Presto-Deallocated-Prepare")) {
/* 324 */       this.deallocatedPreparedStatements.add(urlDecode(entry));
/*     */     }
/*     */     
/* 327 */     String startedTransactionId = response.getHeader("X-Presto-Started-Transaction-Id");
/* 328 */     if (startedTransactionId != null) {
/* 329 */       this.startedtransactionId.set(startedTransactionId);
/*     */     }
/* 331 */     if (response.getHeader("X-Presto-Clear-Transaction-Id") != null) {
/* 332 */       this.clearTransactionId.set(true);
/*     */     }
/*     */     
/* 335 */     this.currentResults.set(response.getValue());
/*     */   }
/*     */   
/*     */   private RuntimeException requestFailedException(String task, Request request, FullJsonResponseHandler.JsonResponse<QueryResults> response)
/*     */   {
/* 340 */     this.gone.set(true);
/* 341 */     if (!response.hasValue()) {
/* 342 */       return new RuntimeException(
/* 343 */         String.format("Error %s at %s returned an invalid response: %s [Error: %s]", new Object[] { task, request.getUri(), response, response.getResponseBody() }), response
/* 344 */         .getException());
/*     */     }
/* 346 */     return new RuntimeException(String.format("Error %s at %s returned %s: %s", new Object[] { task, request.getUri(), Integer.valueOf(response.getStatusCode()), response.getStatusMessage() }));
/*     */   }
/*     */   
/*     */   public boolean cancelLeafStage(Duration timeout)
/*     */   {
/* 351 */     Preconditions.checkState(!isClosed(), "client is closed");
/*     */     
/* 353 */     URI uri = current().getPartialCancelUri();
/* 354 */     if (uri == null) {
/* 355 */       return false;
/*     */     }
/*     */     
/* 358 */     Request request = prepareRequest(Request.Builder.prepareDelete(), uri).build();
/*     */     
/* 360 */     HttpClient.HttpResponseFuture<StatusResponseHandler.StatusResponse> response = this.httpClient.executeAsync(request, StatusResponseHandler.createStatusResponseHandler());
/*     */     try {
/* 362 */       StatusResponseHandler.StatusResponse status = (StatusResponseHandler.StatusResponse)response.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
/* 363 */       return HttpStatus.familyForStatusCode(status.getStatusCode()) == HttpStatus.Family.SUCCESSFUL;
/*     */     }
/*     */     catch (InterruptedException e) {
/* 366 */       Thread.currentThread().interrupt();
/* 367 */       throw Throwables.propagate(e);
/*     */     }
/*     */     catch (ExecutionException e) {
/* 370 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */     catch (TimeoutException e) {}
/* 373 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */   {
/* 380 */     if (!this.closed.getAndSet(true)) {
/* 381 */       URI uri = ((QueryResults)this.currentResults.get()).getNextUri();
/* 382 */       if (uri != null) {
/* 383 */         Request request = prepareRequest(Request.Builder.prepareDelete(), uri).build();
/* 384 */         this.httpClient.executeAsync(request, StatusResponseHandler.createStatusResponseHandler());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static String urlEncode(String value)
/*     */   {
/*     */     try {
/* 392 */       return URLEncoder.encode(value, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 395 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static String urlDecode(String value)
/*     */   {
/*     */     try {
/* 402 */       return URLDecoder.decode(value, "UTF-8");
/*     */     }
/*     */     catch (UnsupportedEncodingException e) {
/* 405 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\StatementClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */