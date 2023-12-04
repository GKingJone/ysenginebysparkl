/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
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
/*    */ public class PingGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public PingGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 34 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 40 */     PingFrame pingFrame = (PingFrame)frame;
/* 41 */     generatePing(lease, pingFrame.getPayload(), pingFrame.isReply());
/*    */   }
/*    */   
/*    */   public void generatePing(ByteBufferPool.Lease lease, byte[] payload, boolean reply)
/*    */   {
/* 46 */     if (payload.length != 8) {
/* 47 */       throw new IllegalArgumentException("Invalid payload length: " + payload.length);
/*    */     }
/* 49 */     ByteBuffer header = generateHeader(lease, FrameType.PING, 8, reply ? 1 : 0, 0);
/*    */     
/* 51 */     header.put(payload);
/*    */     
/* 53 */     BufferUtil.flipToFlush(header, 0);
/* 54 */     lease.append(header, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\PingGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */