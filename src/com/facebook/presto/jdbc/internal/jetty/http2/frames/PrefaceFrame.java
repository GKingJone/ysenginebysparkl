/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.frames;
/*    */ 
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ public class PrefaceFrame
/*    */   extends Frame
/*    */ {
/* 29 */   public static final byte[] PREFACE_PREAMBLE_BYTES = "PRI * HTTP/2.0\r\n\r\n"
/*    */   
/*    */ 
/* 32 */     .getBytes(StandardCharsets.US_ASCII);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 37 */   public static final byte[] PREFACE_BYTES = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 42 */     .getBytes(StandardCharsets.US_ASCII);
/*    */   
/*    */   public PrefaceFrame()
/*    */   {
/* 46 */     super(FrameType.PREFACE);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\frames\PrefaceFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */