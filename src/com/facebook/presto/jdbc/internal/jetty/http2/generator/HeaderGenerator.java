/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.generator;
/*    */ 
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
/*    */ 
/*    */ public class HeaderGenerator
/*    */ {
/* 29 */   private int maxFrameSize = 16384;
/*    */   
/*    */   public ByteBuffer generate(ByteBufferPool.Lease lease, FrameType frameType, int capacity, int length, int flags, int streamId)
/*    */   {
/* 33 */     ByteBuffer header = lease.acquire(capacity, true);
/* 34 */     header.put((byte)((length & 0xFF0000) >>> 16));
/* 35 */     header.put((byte)((length & 0xFF00) >>> 8));
/* 36 */     header.put((byte)(length & 0xFF));
/* 37 */     header.put((byte)frameType.getType());
/* 38 */     header.put((byte)flags);
/* 39 */     header.putInt(streamId);
/* 40 */     return header;
/*    */   }
/*    */   
/*    */   public int getMaxFrameSize()
/*    */   {
/* 45 */     return this.maxFrameSize;
/*    */   }
/*    */   
/*    */   public void setMaxFrameSize(int maxFrameSize)
/*    */   {
/* 50 */     this.maxFrameSize = maxFrameSize;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\generator\HeaderGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */