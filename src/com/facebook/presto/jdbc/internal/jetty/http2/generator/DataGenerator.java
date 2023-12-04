/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
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
/*    */ 
/*    */ 
/*    */ public class DataGenerator
/*    */ {
/*    */   private final HeaderGenerator headerGenerator;
/*    */   
/*    */   public DataGenerator(HeaderGenerator headerGenerator)
/*    */   {
/* 36 */     this.headerGenerator = headerGenerator;
/*    */   }
/*    */   
/*    */   public void generate(ByteBufferPool.Lease lease, DataFrame frame, int maxLength)
/*    */   {
/* 41 */     generateData(lease, frame.getStreamId(), frame.getData(), frame.isEndStream(), maxLength);
/*    */   }
/*    */   
/*    */   public void generateData(ByteBufferPool.Lease lease, int streamId, ByteBuffer data, boolean last, int maxLength)
/*    */   {
/* 46 */     if (streamId < 0) {
/* 47 */       throw new IllegalArgumentException("Invalid stream id: " + streamId);
/*    */     }
/* 49 */     int dataLength = data.remaining();
/* 50 */     int maxFrameSize = this.headerGenerator.getMaxFrameSize();
/* 51 */     if ((dataLength <= maxLength) && (dataLength <= maxFrameSize))
/*    */     {
/*    */ 
/* 54 */       generateFrame(lease, streamId, data, last);
/* 55 */       return;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 60 */     int length = Math.min(maxLength, dataLength);
/* 61 */     int frames = length / maxFrameSize;
/* 62 */     if (frames * maxFrameSize != length) {
/* 63 */       frames++;
/*    */     }
/* 65 */     int begin = data.position();
/* 66 */     int end = data.limit();
/* 67 */     for (int i = 1; i <= frames; i++)
/*    */     {
/* 69 */       int limit = begin + Math.min(maxFrameSize * i, length);
/* 70 */       data.limit(limit);
/* 71 */       ByteBuffer slice = data.slice();
/* 72 */       data.position(limit);
/* 73 */       generateFrame(lease, streamId, slice, (i == frames) && (last) && (limit == end));
/*    */     }
/* 75 */     data.limit(end);
/*    */   }
/*    */   
/*    */   private void generateFrame(ByteBufferPool.Lease lease, int streamId, ByteBuffer data, boolean last)
/*    */   {
/* 80 */     int length = data.remaining();
/*    */     
/* 82 */     int flags = 0;
/* 83 */     if (last) {
/* 84 */       flags |= 0x1;
/*    */     }
/* 86 */     ByteBuffer header = this.headerGenerator.generate(lease, FrameType.DATA, 9 + length, length, flags, streamId);
/*    */     
/* 88 */     BufferUtil.flipToFlush(header, 0);
/* 89 */     lease.append(header, true);
/*    */     
/* 91 */     lease.append(data, false);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\DataGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */