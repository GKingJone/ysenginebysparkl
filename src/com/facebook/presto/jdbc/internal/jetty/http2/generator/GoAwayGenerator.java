/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Arrays;
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
/*    */ public class GoAwayGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   public GoAwayGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 35 */     super(headerGenerator);
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 41 */     GoAwayFrame goAwayFrame = (GoAwayFrame)frame;
/* 42 */     generateGoAway(lease, goAwayFrame.getLastStreamId(), goAwayFrame.getError(), goAwayFrame.getPayload());
/*    */   }
/*    */   
/*    */   public void generateGoAway(ByteBufferPool.Lease lease, int lastStreamId, int error, byte[] payload)
/*    */   {
/* 47 */     if (lastStreamId < 0) {
/* 48 */       throw new IllegalArgumentException("Invalid last stream id: " + lastStreamId);
/*    */     }
/*    */     
/* 51 */     int fixedLength = 8;
/*    */     
/*    */ 
/* 54 */     int maxPayloadLength = 16384 - fixedLength;
/* 55 */     if ((payload != null) && (payload.length > maxPayloadLength)) {
/* 56 */       payload = Arrays.copyOfRange(payload, 0, maxPayloadLength);
/*    */     }
/* 58 */     int length = fixedLength + (payload != null ? payload.length : 0);
/* 59 */     ByteBuffer header = generateHeader(lease, FrameType.GO_AWAY, length, 0, 0);
/*    */     
/* 61 */     header.putInt(lastStreamId);
/* 62 */     header.putInt(error);
/*    */     
/* 64 */     if (payload != null)
/*    */     {
/* 66 */       header.put(payload);
/*    */     }
/*    */     
/* 69 */     BufferUtil.flipToFlush(header, 0);
/* 70 */     lease.append(header, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\GoAwayGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */