/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
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
/*    */ public abstract class FrameGenerator
/*    */ {
/*    */   private final HeaderGenerator headerGenerator;
/*    */   
/*    */   protected FrameGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 33 */     this.headerGenerator = headerGenerator;
/*    */   }
/*    */   
/*    */   public abstract void generate(ByteBufferPool.Lease paramLease, Frame paramFrame);
/*    */   
/*    */   protected ByteBuffer generateHeader(ByteBufferPool.Lease lease, FrameType frameType, int length, int flags, int streamId)
/*    */   {
/* 40 */     return this.headerGenerator.generate(lease, frameType, 9 + length, length, flags, streamId);
/*    */   }
/*    */   
/*    */   public int getMaxFrameSize()
/*    */   {
/* 45 */     return this.headerGenerator.getMaxFrameSize();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\FrameGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */