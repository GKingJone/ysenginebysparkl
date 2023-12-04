/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.api;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
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
/*     */ public abstract interface Stream
/*     */ {
/*     */   public abstract int getId();
/*     */   
/*     */   public abstract Session getSession();
/*     */   
/*     */   public abstract void headers(HeadersFrame paramHeadersFrame, Callback paramCallback);
/*     */   
/*     */   public abstract void push(PushPromiseFrame paramPushPromiseFrame, Promise<Stream> paramPromise, Listener paramListener);
/*     */   
/*     */   public abstract void data(DataFrame paramDataFrame, Callback paramCallback);
/*     */   
/*     */   public abstract void reset(ResetFrame paramResetFrame, Callback paramCallback);
/*     */   
/*     */   public abstract Object getAttribute(String paramString);
/*     */   
/*     */   public abstract void setAttribute(String paramString, Object paramObject);
/*     */   
/*     */   public abstract Object removeAttribute(String paramString);
/*     */   
/*     */   public abstract boolean isReset();
/*     */   
/*     */   public abstract boolean isClosed();
/*     */   
/*     */   public abstract long getIdleTimeout();
/*     */   
/*     */   public abstract void setIdleTimeout(long paramLong);
/*     */   
/*     */   public static abstract interface Listener
/*     */   {
/*     */     public abstract void onHeaders(Stream paramStream, HeadersFrame paramHeadersFrame);
/*     */     
/*     */     public abstract Listener onPush(Stream paramStream, PushPromiseFrame paramPushPromiseFrame);
/*     */     
/*     */     public abstract void onData(Stream paramStream, DataFrame paramDataFrame, Callback paramCallback);
/*     */     
/*     */     public abstract void onReset(Stream paramStream, ResetFrame paramResetFrame);
/*     */     
/*     */     public abstract void onTimeout(Stream paramStream, Throwable paramThrowable);
/*     */     
/*     */     public static class Adapter
/*     */       implements Listener
/*     */     {
/*     */       public void onHeaders(Stream stream, HeadersFrame frame) {}
/*     */       
/*     */       public Listener onPush(Stream stream, PushPromiseFrame frame)
/*     */       {
/* 197 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public void onData(Stream stream, DataFrame frame, Callback callback)
/*     */       {
/* 203 */         callback.succeeded();
/*     */       }
/*     */       
/*     */       public void onReset(Stream stream, ResetFrame frame) {}
/*     */       
/*     */       public void onTimeout(Stream stream, Throwable x) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\api\Stream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */