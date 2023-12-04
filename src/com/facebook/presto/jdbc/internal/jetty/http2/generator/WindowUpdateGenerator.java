/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import java.nio.ByteBuffer;
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
/*    */ public class WindowUpdateGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public WindowUpdateGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 34 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 40 */     WindowUpdateFrame windowUpdateFrame = (WindowUpdateFrame)frame;
/* 41 */     generateWindowUpdate(lease, windowUpdateFrame.getStreamId(), windowUpdateFrame.getWindowDelta());
/*    */   }
/*    */   
/*    */   public void generateWindowUpdate(ByteBufferPool.Lease lease, int streamId, int windowUpdate)
/*    */   {
/* 46 */     if (windowUpdate < 0) {
/* 47 */       throw new IllegalArgumentException("Invalid window update: " + windowUpdate);
/*    */     }
/* 49 */     ByteBuffer header = generateHeader(lease, FrameType.WINDOW_UPDATE, 4, 0, streamId);
/* 50 */     header.putInt(windowUpdate);
/* 51 */     BufferUtil.flipToFlush(header, 0);
/* 52 */     lease.append(header, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\WindowUpdateGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */