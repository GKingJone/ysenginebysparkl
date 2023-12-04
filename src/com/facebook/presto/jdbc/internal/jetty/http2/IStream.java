/*    */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*    */ import java.io.Closeable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface IStream
/*    */   extends Stream, Closeable
/*    */ {
/* 40 */   public static final String CHANNEL_ATTRIBUTE = IStream.class.getName() + ".channel";
/*    */   
/*    */   public abstract boolean isLocal();
/*    */   
/*    */   public abstract ISession getSession();
/*    */   
/*    */   public abstract Listener getListener();
/*    */   
/*    */   public abstract void setListener(Listener paramListener);
/*    */   
/*    */   public abstract void process(Frame paramFrame, Callback paramCallback);
/*    */   
/*    */   public abstract boolean updateClose(boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/*    */   public abstract void close();
/*    */   
/*    */   public abstract int updateSendWindow(int paramInt);
/*    */   
/*    */   public abstract int updateRecvWindow(int paramInt);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\IStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */