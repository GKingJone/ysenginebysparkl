/*    */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*    */ public class SimpleFlowControlStrategy
/*    */   extends AbstractFlowControlStrategy
/*    */ {
/*    */   public SimpleFlowControlStrategy()
/*    */   {
/* 29 */     this(65535);
/*    */   }
/*    */   
/*    */   public SimpleFlowControlStrategy(int initialStreamSendWindow)
/*    */   {
/* 34 */     super(initialStreamSendWindow);
/*    */   }
/*    */   
/*    */ 
/*    */   public void onDataConsumed(ISession session, IStream stream, int length)
/*    */   {
/* 40 */     if (length <= 0) {
/* 41 */       return;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 47 */     WindowUpdateFrame sessionFrame = new WindowUpdateFrame(0, length);
/* 48 */     session.updateRecvWindow(length);
/* 49 */     if (LOG.isDebugEnabled()) {
/* 50 */       LOG.debug("Data consumed, increased session recv window by {} for {}", new Object[] { Integer.valueOf(length), session });
/*    */     }
/* 52 */     Frame[] streamFrame = Frame.EMPTY_ARRAY;
/* 53 */     if (stream != null)
/*    */     {
/* 55 */       if (stream.isClosed())
/*    */       {
/* 57 */         if (LOG.isDebugEnabled()) {
/* 58 */           LOG.debug("Data consumed, ignoring update stream recv window by {} for closed {}", new Object[] { Integer.valueOf(length), stream });
/*    */         }
/*    */       }
/*    */       else {
/* 62 */         streamFrame = new Frame[1];
/* 63 */         streamFrame[0] = new WindowUpdateFrame(stream.getId(), length);
/* 64 */         stream.updateRecvWindow(length);
/* 65 */         if (LOG.isDebugEnabled()) {
/* 66 */           LOG.debug("Data consumed, increased stream recv window by {} for {}", new Object[] { Integer.valueOf(length), stream });
/*    */         }
/*    */       }
/*    */     }
/* 70 */     session.frames(stream, Callback.NOOP, sessionFrame, streamFrame);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\SimpleFlowControlStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */