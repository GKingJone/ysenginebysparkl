/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.AsyncContentListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.BeginListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.FailureListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.HeaderListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.HeadersListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.SuccessListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingCallback.Action;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingNestedCallback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class ResponseNotifier
/*     */ {
/*  37 */   private static final Logger LOG = Log.getLogger(ResponseNotifier.class);
/*     */   
/*     */ 
/*     */   public void notifyBegin(List<ResponseListener> listeners, Response response)
/*     */   {
/*  42 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/*  44 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/*  45 */       if ((listener instanceof BeginListener)) {
/*  46 */         notifyBegin((BeginListener)listener, response);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyBegin(BeginListener listener, Response response)
/*     */   {
/*     */     try {
/*  54 */       listener.onBegin(response);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  58 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean notifyHeader(List<ResponseListener> listeners, Response response, HttpField field)
/*     */   {
/*  64 */     boolean result = true;
/*     */     
/*  66 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/*  68 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/*  69 */       if ((listener instanceof HeaderListener))
/*  70 */         result &= notifyHeader((HeaderListener)listener, response, field);
/*     */     }
/*  72 */     return result;
/*     */   }
/*     */   
/*     */   private boolean notifyHeader(HeaderListener listener, Response response, HttpField field)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       return listener.onHeader(response, field);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  83 */       LOG.info("Exception while notifying listener " + listener, x); }
/*  84 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void notifyHeaders(List<ResponseListener> listeners, Response response)
/*     */   {
/*  91 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/*  93 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/*  94 */       if ((listener instanceof HeadersListener)) {
/*  95 */         notifyHeaders((HeadersListener)listener, response);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyHeaders(HeadersListener listener, Response response)
/*     */   {
/*     */     try {
/* 103 */       listener.onHeaders(response);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 107 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void notifyContent(List<ResponseListener> listeners, Response response, ByteBuffer buffer, Callback callback)
/*     */   {
/* 116 */     ContentCallback contentCallback = new ContentCallback(listeners, response, buffer, callback, null);
/* 117 */     contentCallback.iterate();
/*     */   }
/*     */   
/*     */   private void notifyContent(AsyncContentListener listener, Response response, ByteBuffer buffer, Callback callback)
/*     */   {
/*     */     try
/*     */     {
/* 124 */       listener.onContent(response, buffer, callback);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 128 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifySuccess(List<ResponseListener> listeners, Response response)
/*     */   {
/* 135 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 137 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/* 138 */       if ((listener instanceof SuccessListener)) {
/* 139 */         notifySuccess((SuccessListener)listener, response);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifySuccess(SuccessListener listener, Response response)
/*     */   {
/*     */     try {
/* 147 */       listener.onSuccess(response);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 151 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyFailure(List<ResponseListener> listeners, Response response, Throwable failure)
/*     */   {
/* 158 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 160 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/* 161 */       if ((listener instanceof FailureListener)) {
/* 162 */         notifyFailure((FailureListener)listener, response, failure);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyFailure(FailureListener listener, Response response, Throwable failure)
/*     */   {
/*     */     try {
/* 170 */       listener.onFailure(response, failure);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 174 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyComplete(List<ResponseListener> listeners, Result result)
/*     */   {
/* 181 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 183 */       ResponseListener listener = (ResponseListener)listeners.get(i);
/* 184 */       if ((listener instanceof CompleteListener)) {
/* 185 */         notifyComplete((CompleteListener)listener, result);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyComplete(CompleteListener listener, Result result)
/*     */   {
/*     */     try {
/* 193 */       listener.onComplete(result);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 197 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   public void forwardSuccess(List<ResponseListener> listeners, Response response)
/*     */   {
/* 203 */     notifyBegin(listeners, response);
/* 204 */     for (Iterator<HttpField> iterator = response.getHeaders().iterator(); iterator.hasNext();)
/*     */     {
/* 206 */       HttpField field = (HttpField)iterator.next();
/* 207 */       if (!notifyHeader(listeners, response, field))
/* 208 */         iterator.remove();
/*     */     }
/* 210 */     notifyHeaders(listeners, response);
/* 211 */     if ((response instanceof ContentResponse))
/* 212 */       notifyContent(listeners, response, ByteBuffer.wrap(((ContentResponse)response).getContent()), Callback.NOOP);
/* 213 */     notifySuccess(listeners, response);
/*     */   }
/*     */   
/*     */   public void forwardSuccessComplete(List<ResponseListener> listeners, Request request, Response response)
/*     */   {
/* 218 */     forwardSuccess(listeners, response);
/* 219 */     notifyComplete(listeners, new Result(request, response));
/*     */   }
/*     */   
/*     */   public void forwardFailure(List<ResponseListener> listeners, Response response, Throwable failure)
/*     */   {
/* 224 */     notifyBegin(listeners, response);
/* 225 */     for (Iterator<HttpField> iterator = response.getHeaders().iterator(); iterator.hasNext();)
/*     */     {
/* 227 */       HttpField field = (HttpField)iterator.next();
/* 228 */       if (!notifyHeader(listeners, response, field))
/* 229 */         iterator.remove();
/*     */     }
/* 231 */     notifyHeaders(listeners, response);
/* 232 */     if ((response instanceof ContentResponse))
/* 233 */       notifyContent(listeners, response, ByteBuffer.wrap(((ContentResponse)response).getContent()), Callback.NOOP);
/* 234 */     notifyFailure(listeners, response, failure);
/*     */   }
/*     */   
/*     */   public void forwardFailureComplete(List<ResponseListener> listeners, Request request, Throwable requestFailure, Response response, Throwable responseFailure)
/*     */   {
/* 239 */     forwardFailure(listeners, response, responseFailure);
/* 240 */     notifyComplete(listeners, new Result(request, requestFailure, response, responseFailure));
/*     */   }
/*     */   
/*     */   private class ContentCallback extends IteratingNestedCallback
/*     */   {
/*     */     private final List<ResponseListener> listeners;
/*     */     private final Response response;
/*     */     private final ByteBuffer buffer;
/*     */     private int index;
/*     */     
/*     */     private ContentCallback(Response listeners, ByteBuffer response, Callback buffer)
/*     */     {
/* 252 */       super();
/* 253 */       this.listeners = listeners;
/* 254 */       this.response = response;
/*     */       
/* 256 */       this.buffer = buffer.slice();
/*     */     }
/*     */     
/*     */     protected IteratingCallback.Action process()
/*     */       throws Exception
/*     */     {
/* 262 */       if (this.index == this.listeners.size()) {
/* 263 */         return IteratingCallback.Action.SUCCEEDED;
/*     */       }
/* 265 */       ResponseListener listener = (ResponseListener)this.listeners.get(this.index);
/* 266 */       if ((listener instanceof AsyncContentListener))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 271 */         this.buffer.clear();
/* 272 */         ResponseNotifier.this.notifyContent((AsyncContentListener)listener, this.response, this.buffer, this);
/* 273 */         return IteratingCallback.Action.SCHEDULED;
/*     */       }
/*     */       
/*     */ 
/* 277 */       succeeded();
/* 278 */       return IteratingCallback.Action.SCHEDULED;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 285 */       this.index += 1;
/* 286 */       super.succeeded();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ResponseNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */