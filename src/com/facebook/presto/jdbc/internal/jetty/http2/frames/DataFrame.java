/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
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
/*    */ public class DataFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int streamId;
/*    */   private final ByteBuffer data;
/*    */   private final boolean endStream;
/*    */   private final int padding;
/*    */   
/*    */   public DataFrame(int streamId, ByteBuffer data, boolean endStream)
/*    */   {
/* 32 */     this(streamId, data, endStream, 0);
/*    */   }
/*    */   
/*    */   public DataFrame(int streamId, ByteBuffer data, boolean endStream, int padding)
/*    */   {
/* 37 */     super(FrameType.DATA);
/* 38 */     this.streamId = streamId;
/* 39 */     this.data = data;
/* 40 */     this.endStream = endStream;
/* 41 */     this.padding = padding;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 46 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public ByteBuffer getData()
/*    */   {
/* 51 */     return this.data;
/*    */   }
/*    */   
/*    */   public boolean isEndStream()
/*    */   {
/* 56 */     return this.endStream;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int remaining()
/*    */   {
/* 64 */     return this.data.remaining();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int padding()
/*    */   {
/* 72 */     return this.padding;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 78 */     return String.format("%s#%d{length:%d,end=%b}", new Object[] { super.toString(), Integer.valueOf(this.streamId), Integer.valueOf(this.data.remaining()), Boolean.valueOf(this.endStream) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\DataFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */