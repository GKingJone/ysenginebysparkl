/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HeadersFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int streamId;
/*    */   private final MetaData metaData;
/*    */   private final PriorityFrame priority;
/*    */   private final boolean endStream;
/*    */   
/*    */   public HeadersFrame(MetaData metaData, PriorityFrame priority, boolean endStream)
/*    */   {
/* 41 */     this(0, metaData, priority, endStream);
/*    */   }
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
/*    */   public HeadersFrame(int streamId, MetaData metaData, PriorityFrame priority, boolean endStream)
/*    */   {
/* 56 */     super(FrameType.HEADERS);
/* 57 */     this.streamId = streamId;
/* 58 */     this.metaData = metaData;
/* 59 */     this.priority = priority;
/* 60 */     this.endStream = endStream;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 65 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public MetaData getMetaData()
/*    */   {
/* 70 */     return this.metaData;
/*    */   }
/*    */   
/*    */   public PriorityFrame getPriority()
/*    */   {
/* 75 */     return this.priority;
/*    */   }
/*    */   
/*    */   public boolean isEndStream()
/*    */   {
/* 80 */     return this.endStream;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     return String.format("%s#%d{end=%b}", new Object[] { super.toString(), Integer.valueOf(this.streamId), Boolean.valueOf(this.endStream) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\HeadersFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */