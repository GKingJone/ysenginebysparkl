/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
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
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
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
/*     */ 
/*     */ public class RequestNotifier
/*     */ {
/*  30 */   private static final Logger LOG = Log.getLogger(ResponseNotifier.class);
/*     */   
/*     */   private final HttpClient client;
/*     */   
/*     */   public RequestNotifier(HttpClient client)
/*     */   {
/*  36 */     this.client = client;
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyQueued(Request request)
/*     */   {
/*  42 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/*  43 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/*  45 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/*  46 */       if ((listener instanceof QueuedListener))
/*  47 */         notifyQueued((QueuedListener)listener, request);
/*     */     }
/*  49 */     List<Listener> listeners = this.client.getRequestListeners();
/*  50 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/*  52 */       Listener listener = (Listener)listeners.get(i);
/*  53 */       notifyQueued(listener, request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyQueued(QueuedListener listener, Request request)
/*     */   {
/*     */     try
/*     */     {
/*  61 */       listener.onQueued(request);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  65 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyBegin(Request request)
/*     */   {
/*  72 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/*  73 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/*  75 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/*  76 */       if ((listener instanceof BeginListener))
/*  77 */         notifyBegin((BeginListener)listener, request);
/*     */     }
/*  79 */     List<Listener> listeners = this.client.getRequestListeners();
/*  80 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/*  82 */       Listener listener = (Listener)listeners.get(i);
/*  83 */       notifyBegin(listener, request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyBegin(BeginListener listener, Request request)
/*     */   {
/*     */     try
/*     */     {
/*  91 */       listener.onBegin(request);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  95 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyHeaders(Request request)
/*     */   {
/* 102 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/* 103 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/* 105 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/* 106 */       if ((listener instanceof HeadersListener))
/* 107 */         notifyHeaders((HeadersListener)listener, request);
/*     */     }
/* 109 */     List<Listener> listeners = this.client.getRequestListeners();
/* 110 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 112 */       Listener listener = (Listener)listeners.get(i);
/* 113 */       notifyHeaders(listener, request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyHeaders(HeadersListener listener, Request request)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       listener.onHeaders(request);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 125 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyCommit(Request request)
/*     */   {
/* 132 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/* 133 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/* 135 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/* 136 */       if ((listener instanceof CommitListener))
/* 137 */         notifyCommit((CommitListener)listener, request);
/*     */     }
/* 139 */     List<Listener> listeners = this.client.getRequestListeners();
/* 140 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 142 */       Listener listener = (Listener)listeners.get(i);
/* 143 */       notifyCommit(listener, request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyCommit(CommitListener listener, Request request)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       listener.onCommit(request);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 155 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyContent(Request request, ByteBuffer content)
/*     */   {
/* 162 */     content = content.slice();
/* 163 */     if (!content.hasRemaining()) {
/* 164 */       return;
/*     */     }
/* 166 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/* 167 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/* 169 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/* 170 */       if ((listener instanceof ContentListener))
/*     */       {
/*     */ 
/*     */ 
/* 174 */         content.clear();
/* 175 */         notifyContent((ContentListener)listener, request, content);
/*     */       }
/*     */     }
/* 178 */     List<Listener> listeners = this.client.getRequestListeners();
/* 179 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 181 */       Listener listener = (Listener)listeners.get(i);
/*     */       
/*     */ 
/* 184 */       content.clear();
/* 185 */       notifyContent(listener, request, content);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyContent(ContentListener listener, Request request, ByteBuffer content)
/*     */   {
/*     */     try
/*     */     {
/* 193 */       listener.onContent(request, content);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 197 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifySuccess(Request request)
/*     */   {
/* 204 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/* 205 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/* 207 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/* 208 */       if ((listener instanceof SuccessListener))
/* 209 */         notifySuccess((SuccessListener)listener, request);
/*     */     }
/* 211 */     List<Listener> listeners = this.client.getRequestListeners();
/* 212 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 214 */       Listener listener = (Listener)listeners.get(i);
/* 215 */       notifySuccess(listener, request);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifySuccess(SuccessListener listener, Request request)
/*     */   {
/*     */     try
/*     */     {
/* 223 */       listener.onSuccess(request);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 227 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void notifyFailure(Request request, Throwable failure)
/*     */   {
/* 234 */     List<RequestListener> requestListeners = request.getRequestListeners(null);
/* 235 */     for (int i = 0; i < requestListeners.size(); i++)
/*     */     {
/* 237 */       RequestListener listener = (RequestListener)requestListeners.get(i);
/* 238 */       if ((listener instanceof FailureListener))
/* 239 */         notifyFailure((FailureListener)listener, request, failure);
/*     */     }
/* 241 */     List<Listener> listeners = this.client.getRequestListeners();
/* 242 */     for (int i = 0; i < listeners.size(); i++)
/*     */     {
/* 244 */       Listener listener = (Listener)listeners.get(i);
/* 245 */       notifyFailure(listener, request, failure);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyFailure(FailureListener listener, Request request, Throwable failure)
/*     */   {
/*     */     try
/*     */     {
/* 253 */       listener.onFailure(request, failure);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 257 */       LOG.info("Exception while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\RequestNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */