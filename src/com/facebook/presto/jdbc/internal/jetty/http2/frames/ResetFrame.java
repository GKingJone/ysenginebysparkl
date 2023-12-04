/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*    */ import java.util.Locale;
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
/*    */ public class ResetFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int streamId;
/*    */   private final int error;
/*    */   
/*    */   public ResetFrame(int streamId, int error)
/*    */   {
/* 32 */     super(FrameType.RST_STREAM);
/* 33 */     this.streamId = streamId;
/* 34 */     this.error = error;
/*    */   }
/*    */   
/*    */   public int getStreamId()
/*    */   {
/* 39 */     return this.streamId;
/*    */   }
/*    */   
/*    */   public int getError()
/*    */   {
/* 44 */     return this.error;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 50 */     ErrorCode errorCode = ErrorCode.from(this.error);
/* 51 */     String reason = errorCode == null ? "error=" + this.error : errorCode.name().toLowerCase(Locale.ENGLISH);
/* 52 */     return String.format("%s#%d{%s}", new Object[] { super.toString(), Integer.valueOf(this.streamId), reason });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\ResetFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */