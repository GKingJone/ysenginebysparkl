/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request.BeginListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.util.BufferingResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class HttpRedirector
/*     */ {
/*  62 */   private static final Logger LOG = Log.getLogger(HttpRedirector.class);
/*     */   
/*     */   private static final String SCHEME_REGEXP = "(^https?)";
/*     */   private static final String AUTHORITY_REGEXP = "([^/\\?#]+)";
/*     */   private static final String DESTINATION_REGEXP = "((^https?)://([^/\\?#]+))?";
/*     */   private static final String PATH_REGEXP = "([^\\?#]*)";
/*     */   private static final String QUERY_REGEXP = "([^#]*)";
/*     */   private static final String FRAGMENT_REGEXP = "(.*)";
/*  70 */   private static final Pattern URI_PATTERN = Pattern.compile("((^https?)://([^/\\?#]+))?([^\\?#]*)([^#]*)(.*)");
/*  71 */   private static final String ATTRIBUTE = HttpRedirector.class.getName() + ".redirects";
/*     */   
/*     */   private final HttpClient client;
/*     */   private final ResponseNotifier notifier;
/*     */   
/*     */   public HttpRedirector(HttpClient client)
/*     */   {
/*  78 */     this.client = client;
/*  79 */     this.notifier = new ResponseNotifier();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isRedirect(Response response)
/*     */   {
/*  88 */     switch (response.getStatus())
/*     */     {
/*     */     case 301: 
/*     */     case 302: 
/*     */     case 303: 
/*     */     case 307: 
/*     */     case 308: 
/*  95 */       return true;
/*     */     }
/*  97 */     return false;
/*     */   }
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
/*     */   public Result redirect(Request request, Response response)
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 113 */     final AtomicReference<Result> resultRef = new AtomicReference();
/* 114 */     final CountDownLatch latch = new CountDownLatch(1);
/* 115 */     Request redirect = redirect(request, response, new BufferingResponseListener()
/*     */     {
/*     */ 
/*     */       public void onComplete(Result result)
/*     */       {
/* 120 */         resultRef.set(new Result(result.getRequest(), result
/* 121 */           .getRequestFailure(), new HttpContentResponse(result
/* 122 */           .getResponse(), getContent(), getMediaType(), getEncoding()), result
/* 123 */           .getResponseFailure()));
/* 124 */         latch.countDown();
/*     */       }
/*     */     });
/*     */     
/*     */     try
/*     */     {
/* 130 */       latch.await();
/* 131 */       Result result = (Result)resultRef.get();
/* 132 */       if (result.isFailed())
/* 133 */         throw new ExecutionException(result.getFailure());
/* 134 */       return result;
/*     */ 
/*     */     }
/*     */     catch (InterruptedException x)
/*     */     {
/* 139 */       redirect.abort(x);
/* 140 */       throw x;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Request redirect(Request request, Response response, CompleteListener listener)
/*     */   {
/* 154 */     if (isRedirect(response))
/*     */     {
/* 156 */       String location = response.getHeaders().get("Location");
/* 157 */       URI newURI = extractRedirectURI(response);
/* 158 */       if (newURI != null)
/*     */       {
/* 160 */         if (LOG.isDebugEnabled())
/* 161 */           LOG.debug("Redirecting to {} (Location: {})", new Object[] { newURI, location });
/* 162 */         return redirect(request, response, listener, newURI);
/*     */       }
/*     */       
/*     */ 
/* 166 */       fail(request, response, new HttpResponseException("Invalid 'Location' header: " + location, response));
/* 167 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 172 */     fail(request, response, new HttpResponseException("Cannot redirect: " + response, response));
/* 173 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public URI extractRedirectURI(Response response)
/*     */   {
/* 186 */     String location = response.getHeaders().get("location");
/* 187 */     if (location != null)
/* 188 */       return sanitize(location);
/* 189 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private URI sanitize(String location)
/*     */   {
/*     */     try
/*     */     {
/* 200 */       return new URI(location);
/*     */     }
/*     */     catch (URISyntaxException x)
/*     */     {
/* 204 */       Matcher matcher = URI_PATTERN.matcher(location);
/* 205 */       if (matcher.matches())
/*     */       {
/* 207 */         String scheme = matcher.group(2);
/* 208 */         String authority = matcher.group(3);
/* 209 */         String path = matcher.group(4);
/* 210 */         String query = matcher.group(5);
/* 211 */         if (query.length() == 0)
/* 212 */           query = null;
/* 213 */         String fragment = matcher.group(6);
/* 214 */         if (fragment.length() == 0) {
/* 215 */           fragment = null;
/*     */         }
/*     */         try {
/* 218 */           return new URI(scheme, authority, path, query, fragment);
/*     */         }
/*     */         catch (URISyntaxException localURISyntaxException1) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 225 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private Request redirect(Request request, Response response, CompleteListener listener, URI newURI)
/*     */   {
/* 231 */     if (!newURI.isAbsolute())
/*     */     {
/* 233 */       URI requestURI = request.getURI();
/* 234 */       if (requestURI == null)
/*     */       {
/* 236 */         String uri = request.getScheme() + "://" + request.getHost();
/* 237 */         int port = request.getPort();
/* 238 */         if (port > 0)
/* 239 */           uri = uri + ":" + port;
/* 240 */         requestURI = URI.create(uri);
/*     */       }
/* 242 */       newURI = requestURI.resolve(newURI);
/*     */     }
/*     */     
/* 245 */     int status = response.getStatus();
/* 246 */     switch (status)
/*     */     {
/*     */ 
/*     */     case 301: 
/* 250 */       String method = request.getMethod();
/* 251 */       if ((HttpMethod.GET.is(method)) || (HttpMethod.HEAD.is(method)) || (HttpMethod.PUT.is(method)))
/* 252 */         return redirect(request, response, listener, newURI, method);
/* 253 */       if (HttpMethod.POST.is(method))
/* 254 */         return redirect(request, response, listener, newURI, HttpMethod.GET.asString());
/* 255 */       fail(request, response, new HttpResponseException("HTTP protocol violation: received 301 for non GET/HEAD/POST/PUT request", response));
/* 256 */       return null;
/*     */     
/*     */ 
/*     */     case 302: 
/* 260 */       String method = request.getMethod();
/* 261 */       if ((HttpMethod.HEAD.is(method)) || (HttpMethod.PUT.is(method))) {
/* 262 */         return redirect(request, response, listener, newURI, method);
/*     */       }
/* 264 */       return redirect(request, response, listener, newURI, HttpMethod.GET.asString());
/*     */     
/*     */ 
/*     */     case 303: 
/* 268 */       String method = request.getMethod();
/* 269 */       if (HttpMethod.HEAD.is(method)) {
/* 270 */         return redirect(request, response, listener, newURI, method);
/*     */       }
/* 272 */       return redirect(request, response, listener, newURI, HttpMethod.GET.asString());
/*     */     
/*     */ 
/*     */ 
/*     */     case 307: 
/*     */     case 308: 
/* 278 */       return redirect(request, response, listener, newURI, request.getMethod());
/*     */     }
/*     */     
/*     */     
/* 282 */     fail(request, response, new HttpResponseException("Unhandled HTTP status code " + status, response));
/* 283 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Request redirect(Request request, Response response, CompleteListener listener, URI location, String method)
/*     */   {
/* 290 */     HttpRequest httpRequest = (HttpRequest)request;
/* 291 */     HttpConversation conversation = httpRequest.getConversation();
/* 292 */     Integer redirects = (Integer)conversation.getAttribute(ATTRIBUTE);
/* 293 */     if (redirects == null)
/* 294 */       redirects = Integer.valueOf(0);
/* 295 */     if (redirects.intValue() < this.client.getMaxRedirects())
/*     */     {
/* 297 */       redirects = Integer.valueOf(redirects.intValue() + 1);
/* 298 */       conversation.setAttribute(ATTRIBUTE, redirects);
/* 299 */       return sendRedirect(httpRequest, response, listener, location, method);
/*     */     }
/*     */     
/*     */ 
/* 303 */     fail(request, response, new HttpResponseException("Max redirects exceeded " + redirects, response));
/* 304 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private Request sendRedirect(final HttpRequest httpRequest, Response response, CompleteListener listener, URI location, String method)
/*     */   {
/*     */     try
/*     */     {
/* 312 */       Request redirect = this.client.copyRequest(httpRequest, location);
/*     */       
/*     */ 
/* 315 */       redirect.method(method);
/*     */       
/* 317 */       redirect.onRequestBegin(new BeginListener()
/*     */       {
/*     */ 
/*     */         public void onBegin(Request redirect)
/*     */         {
/* 322 */           Throwable cause = httpRequest.getAbortCause();
/* 323 */           if (cause != null) {
/* 324 */             redirect.abort(cause);
/*     */           }
/*     */         }
/* 327 */       });
/* 328 */       redirect.send(listener);
/* 329 */       return redirect;
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 333 */       fail(httpRequest, response, x); }
/* 334 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void fail(Request request, Response response, Throwable failure)
/*     */   {
/* 340 */     HttpConversation conversation = ((HttpRequest)request).getConversation();
/* 341 */     conversation.updateResponseListeners(null);
/* 342 */     List<ResponseListener> listeners = conversation.getResponseListeners();
/* 343 */     this.notifier.notifyFailure(listeners, response, failure);
/* 344 */     this.notifier.notifyComplete(listeners, new Result(request, response, failure));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpRedirector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */