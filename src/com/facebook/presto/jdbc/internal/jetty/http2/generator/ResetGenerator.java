/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
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
/*    */ public class ResetGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public ResetGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 34 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 40 */     ResetFrame resetFrame = (ResetFrame)frame;
/* 41 */     generateReset(lease, resetFrame.getStreamId(), resetFrame.getError());
/*    */   }
/*    */   
/*    */   public void generateReset(ByteBufferPool.Lease lease, int streamId, int error)
/*    */   {
/* 46 */     if (streamId < 0) {
/* 47 */       throw new IllegalArgumentException("Invalid stream id: " + streamId);
/*    */     }
/* 49 */     ByteBuffer header = generateHeader(lease, FrameType.RST_STREAM, 4, 0, streamId);
/*    */     
/* 51 */     header.putInt(error);
/*    */     
/* 53 */     BufferUtil.flipToFlush(header, 0);
/* 54 */     lease.append(header, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\ResetGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */