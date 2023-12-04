/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.api;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
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
/*     */ public abstract interface Session
/*     */ {
/*     */   public abstract void newStream(HeadersFrame paramHeadersFrame, Promise<Stream> paramPromise, Stream.Listener paramListener);
/*     */   
/*     */   public abstract int priority(PriorityFrame paramPriorityFrame, Callback paramCallback);
/*     */   
/*     */   public abstract void settings(SettingsFrame paramSettingsFrame, Callback paramCallback);
/*     */   
/*     */   public abstract void ping(PingFrame paramPingFrame, Callback paramCallback);
/*     */   
/*     */   public abstract boolean close(int paramInt, String paramString, Callback paramCallback);
/*     */   
/*     */   public abstract boolean isClosed();
/*     */   
/*     */   public abstract Collection<Stream> getStreams();
/*     */   
/*     */   public abstract Stream getStream(int paramInt);
/*     */   
/*     */   public static abstract interface Listener
/*     */   {
/*     */     public abstract Map<Integer, Integer> onPreface(Session paramSession);
/*     */     
/*     */     public abstract Stream.Listener onNewStream(Stream paramStream, HeadersFrame paramHeadersFrame);
/*     */     
/*     */     public abstract void onSettings(Session paramSession, SettingsFrame paramSettingsFrame);
/*     */     
/*     */     public abstract void onPing(Session paramSession, PingFrame paramPingFrame);
/*     */     
/*     */     public abstract void onReset(Session paramSession, ResetFrame paramResetFrame);
/*     */     
/*     */     public abstract void onClose(Session paramSession, GoAwayFrame paramGoAwayFrame);
/*     */     
/*     */     public abstract boolean onIdleTimeout(Session paramSession);
/*     */     
/*     */     public abstract void onFailure(Session paramSession, Throwable paramThrowable);
/*     */     
/*     */     public static class Adapter
/*     */       implements Listener
/*     */     {
/*     */       public Map<Integer, Integer> onPreface(Session session)
/*     */       {
/* 226 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public Stream.Listener onNewStream(Stream stream, HeadersFrame frame)
/*     */       {
/* 232 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void onSettings(Session session, SettingsFrame frame) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void onPing(Session session, PingFrame frame) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void onReset(Session session, ResetFrame frame) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void onClose(Session session, GoAwayFrame frame) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean onIdleTimeout(Session session)
/*     */       {
/* 258 */         return true;
/*     */       }
/*     */       
/*     */       public void onFailure(Session session, Throwable failure) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\api\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */