/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.BeginListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.CommitListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.ContentListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.FailureListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.HeadersListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.QueuedListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.RequestListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.SuccessListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.AsyncContentListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.BeginListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ContentListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.FailureListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.HeaderListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.HeadersListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.SuccessListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.util.FutureResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.util.PathContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Fields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Fields.Field;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ 
/*     */ public class HttpRequest implements Request
/*     */ {
/*  61 */   private static final URI NULL_URI = URI.create("null:0");
/*  62 */   static final String CONNECTION_ATTRIBUTE = HttpRequest.class.getName() + ".connection";
/*     */   
/*  64 */   private final HttpFields headers = new HttpFields();
/*  65 */   private final Fields params = new Fields(true);
/*  66 */   private final List<ResponseListener> responseListeners = new ArrayList();
/*  67 */   private final AtomicReference<Throwable> aborted = new AtomicReference();
/*     */   private final HttpClient client;
/*     */   private final HttpConversation conversation;
/*     */   private final String host;
/*     */   private final int port;
/*     */   private URI uri;
/*     */   private String scheme;
/*     */   private String path;
/*     */   private String query;
/*  76 */   private String method = HttpMethod.GET.asString();
/*  77 */   private HttpVersion version = HttpVersion.HTTP_1_1;
/*     */   private long idleTimeout;
/*     */   private long timeout;
/*     */   private ContentProvider content;
/*     */   private boolean followRedirects;
/*     */   private List<HttpCookie> cookies;
/*     */   private Map<String, Object> attributes;
/*     */   private List<RequestListener> requestListeners;
/*     */   
/*     */   protected HttpRequest(HttpClient client, HttpConversation conversation, URI uri)
/*     */   {
/*  88 */     this.client = client;
/*  89 */     this.conversation = conversation;
/*  90 */     this.scheme = uri.getScheme();
/*  91 */     this.host = client.normalizeHost(uri.getHost());
/*  92 */     this.port = HttpClient.normalizePort(this.scheme, uri.getPort());
/*  93 */     this.path = uri.getRawPath();
/*  94 */     this.query = uri.getRawQuery();
/*  95 */     extractParams(this.query);
/*     */     
/*  97 */     followRedirects(client.isFollowRedirects());
/*  98 */     this.idleTimeout = client.getIdleTimeout();
/*  99 */     HttpField acceptEncodingField = client.getAcceptEncodingField();
/* 100 */     if (acceptEncodingField != null)
/* 101 */       this.headers.put(acceptEncodingField);
/* 102 */     HttpField userAgentField = client.getUserAgentField();
/* 103 */     if (userAgentField != null) {
/* 104 */       this.headers.put(userAgentField);
/*     */     }
/*     */   }
/*     */   
/*     */   protected HttpConversation getConversation() {
/* 109 */     return this.conversation;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getScheme()
/*     */   {
/* 115 */     return this.scheme;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request scheme(String scheme)
/*     */   {
/* 121 */     this.scheme = scheme;
/* 122 */     this.uri = null;
/* 123 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getHost()
/*     */   {
/* 129 */     return this.host;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 135 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getMethod()
/*     */   {
/* 141 */     return this.method;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request method(HttpMethod method)
/*     */   {
/* 147 */     return method(method.asString());
/*     */   }
/*     */   
/*     */ 
/*     */   public Request method(String method)
/*     */   {
/* 153 */     this.method = ((String)Objects.requireNonNull(method)).toUpperCase(Locale.ENGLISH);
/* 154 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 160 */     return this.path;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request path(String path)
/*     */   {
/* 166 */     URI uri = newURI(path);
/* 167 */     if (uri == null)
/*     */     {
/* 169 */       this.path = path;
/* 170 */       this.query = null;
/*     */     }
/*     */     else
/*     */     {
/* 174 */       String rawPath = uri.getRawPath();
/* 175 */       if (rawPath == null)
/* 176 */         rawPath = "";
/* 177 */       this.path = rawPath;
/* 178 */       String query = uri.getRawQuery();
/* 179 */       if (query != null)
/*     */       {
/* 181 */         this.query = query;
/* 182 */         this.params.clear();
/* 183 */         extractParams(query);
/*     */       }
/* 185 */       if (uri.isAbsolute())
/* 186 */         this.path = buildURI(false).toString();
/*     */     }
/* 188 */     this.uri = null;
/* 189 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getQuery()
/*     */   {
/* 195 */     return this.query;
/*     */   }
/*     */   
/*     */ 
/*     */   public URI getURI()
/*     */   {
/* 201 */     if (this.uri == null)
/* 202 */       this.uri = buildURI(true);
/* 203 */     return this.uri == NULL_URI ? null : this.uri;
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpVersion getVersion()
/*     */   {
/* 209 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request version(HttpVersion version)
/*     */   {
/* 215 */     this.version = ((HttpVersion)Objects.requireNonNull(version));
/* 216 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request param(String name, String value)
/*     */   {
/* 222 */     return param(name, value, false);
/*     */   }
/*     */   
/*     */   private Request param(String name, String value, boolean fromQuery)
/*     */   {
/* 227 */     this.params.add(name, value);
/* 228 */     if (!fromQuery)
/*     */     {
/*     */ 
/* 231 */       if (this.query != null) {
/* 232 */         this.query = (this.query + "&" + urlEncode(name) + "=" + urlEncode(value));
/*     */       } else
/* 234 */         this.query = buildQuery();
/* 235 */       this.uri = null;
/*     */     }
/* 237 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Fields getParams()
/*     */   {
/* 243 */     return new Fields(this.params, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAgent()
/*     */   {
/* 249 */     return this.headers.get(HttpHeader.USER_AGENT);
/*     */   }
/*     */   
/*     */ 
/*     */   public Request agent(String agent)
/*     */   {
/* 255 */     this.headers.put(HttpHeader.USER_AGENT, agent);
/* 256 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request accept(String... accepts)
/*     */   {
/* 262 */     StringBuilder result = new StringBuilder();
/* 263 */     for (String accept : accepts)
/*     */     {
/* 265 */       if (result.length() > 0)
/* 266 */         result.append(", ");
/* 267 */       result.append(accept);
/*     */     }
/* 269 */     if (result.length() > 0)
/* 270 */       this.headers.put(HttpHeader.ACCEPT, result.toString());
/* 271 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request header(String name, String value)
/*     */   {
/* 277 */     if (value == null) {
/* 278 */       this.headers.remove(name);
/*     */     } else
/* 280 */       this.headers.add(name, value);
/* 281 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request header(HttpHeader header, String value)
/*     */   {
/* 287 */     if (value == null) {
/* 288 */       this.headers.remove(header);
/*     */     } else
/* 290 */       this.headers.add(header, value);
/* 291 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<HttpCookie> getCookies()
/*     */   {
/* 297 */     return this.cookies != null ? this.cookies : Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */   public Request cookie(HttpCookie cookie)
/*     */   {
/* 303 */     if (this.cookies == null)
/* 304 */       this.cookies = new ArrayList();
/* 305 */     this.cookies.add(cookie);
/* 306 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request attribute(String name, Object value)
/*     */   {
/* 312 */     if (this.attributes == null)
/* 313 */       this.attributes = new HashMap(4);
/* 314 */     this.attributes.put(name, value);
/* 315 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> getAttributes()
/*     */   {
/* 321 */     return this.attributes != null ? this.attributes : Collections.emptyMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpFields getHeaders()
/*     */   {
/* 327 */     return this.headers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T extends RequestListener> List<T> getRequestListeners(Class<T> type)
/*     */   {
/* 336 */     if ((type == null) || (this.requestListeners == null)) {
/* 337 */       return this.requestListeners != null ? this.requestListeners : Collections.emptyList();
/*     */     }
/* 339 */     ArrayList<T> result = new ArrayList();
/* 340 */     for (RequestListener listener : this.requestListeners)
/* 341 */       if (type.isInstance(listener))
/* 342 */         result.add(listener);
/* 343 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request listener(Listener listener)
/*     */   {
/* 349 */     return requestListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestQueued(final QueuedListener listener)
/*     */   {
/* 355 */     requestListener(new QueuedListener()
/*     */     {
/*     */ 
/*     */       public void onQueued(Request request)
/*     */       {
/* 360 */         listener.onQueued(request);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestBegin(final BeginListener listener)
/*     */   {
/* 368 */     requestListener(new BeginListener()
/*     */     {
/*     */ 
/*     */       public void onBegin(Request request)
/*     */       {
/* 373 */         listener.onBegin(request);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestHeaders(final HeadersListener listener)
/*     */   {
/* 381 */     requestListener(new HeadersListener()
/*     */     {
/*     */ 
/*     */       public void onHeaders(Request request)
/*     */       {
/* 386 */         listener.onHeaders(request);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestCommit(final CommitListener listener)
/*     */   {
/* 394 */     requestListener(new CommitListener()
/*     */     {
/*     */ 
/*     */       public void onCommit(Request request)
/*     */       {
/* 399 */         listener.onCommit(request);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestContent(final ContentListener listener)
/*     */   {
/* 407 */     requestListener(new ContentListener()
/*     */     {
/*     */ 
/*     */       public void onContent(Request request, ByteBuffer content)
/*     */       {
/* 412 */         listener.onContent(request, content);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestSuccess(final SuccessListener listener)
/*     */   {
/* 420 */     requestListener(new SuccessListener()
/*     */     {
/*     */ 
/*     */       public void onSuccess(Request request)
/*     */       {
/* 425 */         listener.onSuccess(request);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onRequestFailure(final FailureListener listener)
/*     */   {
/* 433 */     requestListener(new FailureListener()
/*     */     {
/*     */ 
/*     */       public void onFailure(Request request, Throwable failure)
/*     */       {
/* 438 */         listener.onFailure(request, failure);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private Request requestListener(RequestListener listener)
/*     */   {
/* 445 */     if (this.requestListeners == null)
/* 446 */       this.requestListeners = new ArrayList();
/* 447 */     this.requestListeners.add(listener);
/* 448 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseBegin(final Response.BeginListener listener)
/*     */   {
/* 454 */     this.responseListeners.add(new Response.BeginListener()
/*     */     {
/*     */ 
/*     */       public void onBegin(Response response)
/*     */       {
/* 459 */         listener.onBegin(response);
/*     */       }
/* 461 */     });
/* 462 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseHeader(final HeaderListener listener)
/*     */   {
/* 468 */     this.responseListeners.add(new HeaderListener()
/*     */     {
/*     */ 
/*     */       public boolean onHeader(Response response, HttpField field)
/*     */       {
/* 473 */         return listener.onHeader(response, field);
/*     */       }
/* 475 */     });
/* 476 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseHeaders(final Response.HeadersListener listener)
/*     */   {
/* 482 */     this.responseListeners.add(new Response.HeadersListener()
/*     */     {
/*     */ 
/*     */       public void onHeaders(Response response)
/*     */       {
/* 487 */         listener.onHeaders(response);
/*     */       }
/* 489 */     });
/* 490 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseContent(final Response.ContentListener listener)
/*     */   {
/* 496 */     this.responseListeners.add(new AsyncContentListener()
/*     */     {
/*     */ 
/*     */       public void onContent(Response response, ByteBuffer content, Callback callback)
/*     */       {
/*     */         try
/*     */         {
/* 503 */           listener.onContent(response, content);
/* 504 */           callback.succeeded();
/*     */         }
/*     */         catch (Throwable x)
/*     */         {
/* 508 */           callback.failed(x);
/*     */         }
/*     */       }
/* 511 */     });
/* 512 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseContentAsync(final AsyncContentListener listener)
/*     */   {
/* 518 */     this.responseListeners.add(new AsyncContentListener()
/*     */     {
/*     */ 
/*     */       public void onContent(Response response, ByteBuffer content, Callback callback)
/*     */       {
/* 523 */         listener.onContent(response, content, callback);
/*     */       }
/* 525 */     });
/* 526 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseSuccess(final Response.SuccessListener listener)
/*     */   {
/* 532 */     this.responseListeners.add(new Response.SuccessListener()
/*     */     {
/*     */ 
/*     */       public void onSuccess(Response response)
/*     */       {
/* 537 */         listener.onSuccess(response);
/*     */       }
/* 539 */     });
/* 540 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onResponseFailure(final Response.FailureListener listener)
/*     */   {
/* 546 */     this.responseListeners.add(new Response.FailureListener()
/*     */     {
/*     */ 
/*     */       public void onFailure(Response response, Throwable failure)
/*     */       {
/* 551 */         listener.onFailure(response, failure);
/*     */       }
/* 553 */     });
/* 554 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request onComplete(final CompleteListener listener)
/*     */   {
/* 560 */     this.responseListeners.add(new CompleteListener()
/*     */     {
/*     */ 
/*     */       public void onComplete(Result result)
/*     */       {
/* 565 */         listener.onComplete(result);
/*     */       }
/* 567 */     });
/* 568 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public ContentProvider getContent()
/*     */   {
/* 574 */     return this.content;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request content(ContentProvider content)
/*     */   {
/* 580 */     return content(content, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public Request content(ContentProvider content, String contentType)
/*     */   {
/* 586 */     if (contentType != null)
/* 587 */       header(HttpHeader.CONTENT_TYPE, contentType);
/* 588 */     this.content = content;
/* 589 */     return this;
/*     */   }
/*     */   
/*     */   public Request file(Path file)
/*     */     throws IOException
/*     */   {
/* 595 */     return file(file, "application/octet-stream");
/*     */   }
/*     */   
/*     */   public Request file(Path file, String contentType)
/*     */     throws IOException
/*     */   {
/* 601 */     return content(new PathContentProvider(contentType, file));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFollowRedirects()
/*     */   {
/* 607 */     return this.followRedirects;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request followRedirects(boolean follow)
/*     */   {
/* 613 */     this.followRedirects = follow;
/* 614 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getIdleTimeout()
/*     */   {
/* 620 */     return this.idleTimeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request idleTimeout(long timeout, TimeUnit unit)
/*     */   {
/* 626 */     this.idleTimeout = unit.toMillis(timeout);
/* 627 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeout()
/*     */   {
/* 633 */     return this.timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request timeout(long timeout, TimeUnit unit)
/*     */   {
/* 639 */     this.timeout = unit.toMillis(timeout);
/* 640 */     return this;
/*     */   }
/*     */   
/*     */   public ContentResponse send()
/*     */     throws InterruptedException, TimeoutException, ExecutionException
/*     */   {
/* 646 */     FutureResponseListener listener = new FutureResponseListener(this);
/* 647 */     send(this, listener);
/*     */     
/*     */     try
/*     */     {
/* 651 */       long timeout = getTimeout();
/* 652 */       if (timeout <= 0L) {
/* 653 */         return listener.get();
/*     */       }
/* 655 */       return listener.get(timeout, TimeUnit.MILLISECONDS);
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*     */ 
/* 661 */       abort(x);
/* 662 */       throw x;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void send(CompleteListener listener)
/*     */   {
/* 669 */     TimeoutCompleteListener timeoutListener = null;
/*     */     try
/*     */     {
/* 672 */       if (getTimeout() > 0L)
/*     */       {
/* 674 */         timeoutListener = new TimeoutCompleteListener(this);
/* 675 */         timeoutListener.schedule(this.client.getScheduler());
/* 676 */         this.responseListeners.add(timeoutListener);
/*     */       }
/* 678 */       send(this, listener);
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*     */ 
/* 684 */       if (timeoutListener != null)
/* 685 */         timeoutListener.cancel();
/* 686 */       throw x;
/*     */     }
/*     */   }
/*     */   
/*     */   private void send(HttpRequest request, CompleteListener listener)
/*     */   {
/* 692 */     if (listener != null)
/* 693 */       this.responseListeners.add(listener);
/* 694 */     this.client.send(request, this.responseListeners);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean abort(Throwable cause)
/*     */   {
/* 700 */     if (this.aborted.compareAndSet(null, Objects.requireNonNull(cause)))
/*     */     {
/* 702 */       if ((this.content instanceof Callback))
/* 703 */         ((Callback)this.content).failed(cause);
/* 704 */       return this.conversation.abort(cause);
/*     */     }
/* 706 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public Throwable getAbortCause()
/*     */   {
/* 712 */     return (Throwable)this.aborted.get();
/*     */   }
/*     */   
/*     */   private String buildQuery()
/*     */   {
/* 717 */     StringBuilder result = new StringBuilder();
/* 718 */     for (Iterator<Field> iterator = this.params.iterator(); iterator.hasNext();)
/*     */     {
/* 720 */       Field field = (Field)iterator.next();
/* 721 */       List<String> values = field.getValues();
/* 722 */       for (int i = 0; i < values.size(); i++)
/*     */       {
/* 724 */         if (i > 0)
/* 725 */           result.append("&");
/* 726 */         result.append(field.getName()).append("=");
/* 727 */         result.append(urlEncode((String)values.get(i)));
/*     */       }
/* 729 */       if (iterator.hasNext())
/* 730 */         result.append("&");
/*     */     }
/* 732 */     return result.toString();
/*     */   }
/*     */   
/*     */   private String urlEncode(String value)
/*     */   {
/* 737 */     if (value == null) {
/* 738 */       return "";
/*     */     }
/* 740 */     String encoding = "utf-8";
/*     */     try
/*     */     {
/* 743 */       return URLEncoder.encode(value, encoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 747 */       throw new UnsupportedCharsetException(encoding);
/*     */     }
/*     */   }
/*     */   
/*     */   private void extractParams(String query)
/*     */   {
/* 753 */     if (query != null)
/*     */     {
/* 755 */       for (String nameValue : query.split("&"))
/*     */       {
/* 757 */         String[] parts = nameValue.split("=");
/* 758 */         if (parts.length > 0)
/*     */         {
/* 760 */           String name = urlDecode(parts[0]);
/* 761 */           if (name.trim().length() != 0)
/*     */           {
/* 763 */             param(name, parts.length < 2 ? "" : urlDecode(parts[1]), true);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String urlDecode(String value) {
/* 771 */     String charset = "utf-8";
/*     */     try
/*     */     {
/* 774 */       return URLDecoder.decode(value, charset);
/*     */     }
/*     */     catch (UnsupportedEncodingException x)
/*     */     {
/* 778 */       throw new UnsupportedCharsetException(charset);
/*     */     }
/*     */   }
/*     */   
/*     */   private URI buildURI(boolean withQuery)
/*     */   {
/* 784 */     String path = getPath();
/* 785 */     String query = getQuery();
/* 786 */     if ((query != null) && (withQuery))
/* 787 */       path = path + "?" + query;
/* 788 */     URI result = newURI(path);
/* 789 */     if (result == null)
/* 790 */       return NULL_URI;
/* 791 */     if (!result.isAbsolute())
/* 792 */       result = URI.create(new Origin(getScheme(), getHost(), getPort()).asString() + path);
/* 793 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   private URI newURI(String uri)
/*     */   {
/*     */     try
/*     */     {
/* 801 */       if ("*".equals(uri))
/* 802 */         return null;
/* 803 */       URI result = new URI(uri);
/* 804 */       return result.isOpaque() ? null : result;
/*     */     }
/*     */     catch (URISyntaxException x) {}
/*     */     
/*     */ 
/*     */ 
/* 810 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 817 */     return String.format("%s[%s %s %s]@%x", new Object[] { HttpRequest.class.getSimpleName(), getMethod(), getPath(), getVersion(), Integer.valueOf(hashCode()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */