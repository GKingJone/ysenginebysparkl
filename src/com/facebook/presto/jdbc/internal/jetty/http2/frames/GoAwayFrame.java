/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
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
/*    */ public class GoAwayFrame
/*    */   extends Frame
/*    */ {
/*    */   private final int lastStreamId;
/*    */   private final int error;
/*    */   private final byte[] payload;
/*    */   
/*    */   public GoAwayFrame(int lastStreamId, int error, byte[] payload)
/*    */   {
/* 34 */     super(FrameType.GO_AWAY);
/* 35 */     this.lastStreamId = lastStreamId;
/* 36 */     this.error = error;
/* 37 */     this.payload = payload;
/*    */   }
/*    */   
/*    */   public int getLastStreamId()
/*    */   {
/* 42 */     return this.lastStreamId;
/*    */   }
/*    */   
/*    */   public int getError()
/*    */   {
/* 47 */     return this.error;
/*    */   }
/*    */   
/*    */   public byte[] getPayload()
/*    */   {
/* 52 */     return this.payload;
/*    */   }
/*    */   
/*    */   public String tryConvertPayload()
/*    */   {
/* 57 */     if (this.payload == null)
/* 58 */       return "";
/* 59 */     ByteBuffer buffer = BufferUtil.toBuffer(this.payload);
/*    */     try
/*    */     {
/* 62 */       return BufferUtil.toUTF8String(buffer);
/*    */     }
/*    */     catch (Throwable x) {}
/*    */     
/* 66 */     return BufferUtil.toDetailString(buffer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 73 */     ErrorCode errorCode = ErrorCode.from(this.error);
/* 74 */     return String.format("%s,%d/%s/%s", new Object[] {
/* 75 */       super.toString(), 
/* 76 */       Integer.valueOf(this.lastStreamId), errorCode != null ? errorCode
/* 77 */       .toString() : String.valueOf(this.error), 
/* 78 */       tryConvertPayload() });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\GoAwayFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */