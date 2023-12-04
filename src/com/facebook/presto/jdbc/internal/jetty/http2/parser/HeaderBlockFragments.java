/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
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
/*    */ public class HeaderBlockFragments
/*    */ {
/*    */   private PriorityFrame priorityFrame;
/*    */   private boolean endStream;
/*    */   private int streamId;
/*    */   private ByteBuffer storage;
/*    */   
/*    */   public void storeFragment(ByteBuffer fragment, int length, boolean last)
/*    */   {
/* 34 */     if (this.storage == null)
/*    */     {
/* 36 */       int space = last ? length : length * 2;
/* 37 */       this.storage = ByteBuffer.allocate(space);
/*    */     }
/*    */     
/*    */ 
/* 41 */     if (this.storage.remaining() < length)
/*    */     {
/* 43 */       int space = last ? length : length * 2;
/* 44 */       int capacity = this.storage.position() + space;
/* 45 */       ByteBuffer newStorage = ByteBuffer.allocate(capacity);
/* 46 */       this.storage.flip();
/* 47 */       newStorage.put(this.storage);
/* 48 */       this.storage = newStorage;
/*    */     }
/*    */     
/*    */ 
/* 52 */     int limit = fragment.limit();
/* 53 */     fragment.limit(fragment.position() + length);
/* 54 */     this.storage.put(fragment);
/* 55 */     fragment.limit(limit);
/*    */   }
/*    */   
/*    */   public PriorityFrame getPriorityFrame()
/*    */   {
/* 60 */     return this.priorityFrame;
/*    */   }
/*    */   
/*    */   public void setPriorityFrame(PriorityFrame priorityFrame)
/*    */   {
/* 65 */     this.priorityFrame = priorityFrame;
/*    */   }
/*    */   
/*    */   public boolean isEndStream()
/*    */   {
/* 70 */     return this.endStream;
/*    */   }
/*    */   
/*    */   public void setEndStream(boolean endStream)
/*    */   {
/* 75 */     this.endStream = endStream;
/*    */   }
/*    */   
/*    */   public ByteBuffer complete()
/*    */   {
/* 80 */     ByteBuffer result = this.storage;
/* 81 */     this.storage = null;
/* 82 */     result.flip();
/* 83 */     return result;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 88 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public void setStreamId(int streamId)
/*    */   {
/* 93 */     this.streamId = streamId;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\HeaderBlockFragments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */