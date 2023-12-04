/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PriorityFrame
/*    */   extends Frame
/*    */ {
/*    */   public static final int PRIORITY_LENGTH = 5;
/*    */   
/*    */ 
/*    */ 
/*    */   private final int streamId;
/*    */   
/*    */ 
/*    */ 
/*    */   private final int parentStreamId;
/*    */   
/*    */ 
/*    */ 
/*    */   private final int weight;
/*    */   
/*    */ 
/*    */ 
/*    */   private final boolean exclusive;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PriorityFrame(int parentStreamId, int weight, boolean exclusive)
/*    */   {
/* 32 */     this(0, parentStreamId, weight, exclusive);
/*    */   }
/*    */   
/*    */   public PriorityFrame(int streamId, int parentStreamId, int weight, boolean exclusive)
/*    */   {
/* 37 */     super(FrameType.PRIORITY);
/* 38 */     this.streamId = streamId;
/* 39 */     this.parentStreamId = parentStreamId;
/* 40 */     this.weight = weight;
/* 41 */     this.exclusive = exclusive;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 46 */     return this.streamId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public int getDependentStreamId()
/*    */   {
/* 55 */     return getParentStreamId();
/*    */   }
/*    */   
/*    */   public int getParentStreamId()
/*    */   {
/* 60 */     return this.parentStreamId;
/*    */   }
/*    */   
/*    */   public int getWeight()
/*    */   {
/* 65 */     return this.weight;
/*    */   }
/*    */   
/*    */   public boolean isExclusive()
/*    */   {
/* 70 */     return this.exclusive;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 76 */     return String.format("%s#%d/#%d{weight=%d,exclusive=%b}", new Object[] { super.toString(), Integer.valueOf(this.streamId), Integer.valueOf(this.parentStreamId), Integer.valueOf(this.weight), Boolean.valueOf(this.exclusive) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\PriorityFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */