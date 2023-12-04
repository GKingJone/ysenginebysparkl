/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WindowUpdateFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int streamId;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int windowDelta;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public WindowUpdateFrame(int streamId, int windowDelta)
/*    */   {
/* 28 */     super(FrameType.WINDOW_UPDATE);
/* 29 */     this.streamId = streamId;
/* 30 */     this.windowDelta = windowDelta;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 35 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public int getWindowDelta()
/*    */   {
/* 40 */     return this.windowDelta;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 46 */     return String.format("%s#%d,delta=%d", new Object[] { super.toString(), Integer.valueOf(this.streamId), Integer.valueOf(this.windowDelta) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\WindowUpdateFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */