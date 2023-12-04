/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.FlowControlStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.HTTP2Session;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.IStream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.generator.Generator;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
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
/*     */ public class HTTP2ClientSession
/*     */   extends HTTP2Session
/*     */ {
/*  37 */   private static final Logger LOG = Log.getLogger(HTTP2ClientSession.class);
/*     */   
/*     */   public HTTP2ClientSession(Scheduler scheduler, EndPoint endPoint, Generator generator, Session.Listener listener, FlowControlStrategy flowControl)
/*     */   {
/*  41 */     super(scheduler, endPoint, generator, listener, flowControl, 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onHeaders(HeadersFrame frame)
/*     */   {
/*  47 */     if (LOG.isDebugEnabled()) {
/*  48 */       LOG.debug("Received {}", new Object[] { frame });
/*     */     }
/*  50 */     int streamId = frame.getStreamId();
/*  51 */     IStream stream = getStream(streamId);
/*  52 */     if (stream == null)
/*     */     {
/*  54 */       if (LOG.isDebugEnabled()) {
/*  55 */         LOG.debug("Ignoring {}, stream #{} not found", new Object[] { frame, Integer.valueOf(streamId) });
/*     */       }
/*     */     }
/*     */     else {
/*  59 */       stream.process(frame, Callback.NOOP);
/*  60 */       notifyHeaders(stream, frame);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyHeaders(IStream stream, HeadersFrame frame)
/*     */   {
/*  66 */     Stream.Listener listener = stream.getListener();
/*  67 */     if (listener == null) {
/*  68 */       return;
/*     */     }
/*     */     try {
/*  71 */       listener.onHeaders(stream, frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  75 */       LOG.info("Failure while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onPushPromise(PushPromiseFrame frame)
/*     */   {
/*  82 */     if (LOG.isDebugEnabled()) {
/*  83 */       LOG.debug("Received {}", new Object[] { frame });
/*     */     }
/*  85 */     int streamId = frame.getStreamId();
/*  86 */     int pushStreamId = frame.getPromisedStreamId();
/*  87 */     IStream stream = getStream(streamId);
/*  88 */     if (stream == null)
/*     */     {
/*  90 */       if (LOG.isDebugEnabled()) {
/*  91 */         LOG.debug("Ignoring {}, stream #{} not found", new Object[] { frame, Integer.valueOf(streamId) });
/*     */       }
/*     */     }
/*     */     else {
/*  95 */       IStream pushStream = createRemoteStream(pushStreamId);
/*  96 */       pushStream.process(frame, Callback.NOOP);
/*  97 */       Stream.Listener listener = notifyPush(stream, pushStream, frame);
/*  98 */       pushStream.setListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */   private Stream.Listener notifyPush(IStream stream, IStream pushStream, PushPromiseFrame frame)
/*     */   {
/* 104 */     Stream.Listener listener = stream.getListener();
/* 105 */     if (listener == null) {
/* 106 */       return null;
/*     */     }
/*     */     try {
/* 109 */       return listener.onPush(pushStream, frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 113 */       LOG.info("Failure while notifying listener " + listener, x); }
/* 114 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\HTTP2ClientSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */