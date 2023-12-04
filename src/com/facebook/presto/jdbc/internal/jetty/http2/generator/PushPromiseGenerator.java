/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.hpack.HpackEncoder;
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
/*    */ public class PushPromiseGenerator
/*    */   extends FrameGenerator
/*    */ {
/*    */   private final HpackEncoder encoder;
/*    */   
/*    */   public PushPromiseGenerator(HeaderGenerator headerGenerator, HpackEncoder encoder)
/*    */   {
/* 38 */     super(headerGenerator);
/* 39 */     this.encoder = encoder;
/*    */   }
/*    */   
/*    */ 
/*    */   public void generate(ByteBufferPool.Lease lease, Frame frame)
/*    */   {
/* 45 */     PushPromiseFrame pushPromiseFrame = (PushPromiseFrame)frame;
/* 46 */     generatePushPromise(lease, pushPromiseFrame.getStreamId(), pushPromiseFrame.getPromisedStreamId(), pushPromiseFrame.getMetaData());
/*    */   }
/*    */   
/*    */   public void generatePushPromise(ByteBufferPool.Lease lease, int streamId, int promisedStreamId, MetaData metaData)
/*    */   {
/* 51 */     if (streamId < 0)
/* 52 */       throw new IllegalArgumentException("Invalid stream id: " + streamId);
/* 53 */     if (promisedStreamId < 0) {
/* 54 */       throw new IllegalArgumentException("Invalid promised stream id: " + promisedStreamId);
/*    */     }
/* 56 */     int maxFrameSize = getMaxFrameSize();
/*    */     
/* 58 */     int extraSpace = 4;
/* 59 */     maxFrameSize -= extraSpace;
/*    */     
/* 61 */     ByteBuffer hpacked = lease.acquire(maxFrameSize, false);
/* 62 */     BufferUtil.clearToFill(hpacked);
/* 63 */     this.encoder.encode(hpacked, metaData);
/* 64 */     int hpackedLength = hpacked.position();
/* 65 */     BufferUtil.flipToFlush(hpacked, 0);
/*    */     
/* 67 */     int length = hpackedLength + extraSpace;
/* 68 */     int flags = 4;
/*    */     
/* 70 */     ByteBuffer header = generateHeader(lease, FrameType.PUSH_PROMISE, length, flags, streamId);
/* 71 */     header.putInt(promisedStreamId);
/* 72 */     BufferUtil.flipToFlush(header, 0);
/*    */     
/* 74 */     lease.append(header, true);
/* 75 */     lease.append(hpacked, true);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\PushPromiseGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */