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
/*    */ public class PushPromiseFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int streamId;
/*    */   private final int promisedStreamId;
/*    */   private final MetaData metaData;
/*    */   
/*    */   public PushPromiseFrame(int streamId, int promisedStreamId, MetaData metaData)
/*    */   {
/* 31 */     super(FrameType.PUSH_PROMISE);
/* 32 */     this.streamId = streamId;
/* 33 */     this.promisedStreamId = promisedStreamId;
/* 34 */     this.metaData = metaData;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 39 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public int getPromisedStreamId()
/*    */   {
/* 44 */     return this.promisedStreamId;
/*    */   }
/*    */   
/*    */   public MetaData getMetaData()
/*    */   {
/* 49 */     return this.metaData;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return String.format("%s#%d/#%d", new Object[] { super.toString(), Integer.valueOf(this.streamId), Integer.valueOf(this.promisedStreamId) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\PushPromiseFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */