/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum ErrorCode
/*     */ {
/*  32 */   NO_ERROR(0), 
/*     */   
/*     */ 
/*     */ 
/*  36 */   PROTOCOL_ERROR(1), 
/*     */   
/*     */ 
/*     */ 
/*  40 */   INTERNAL_ERROR(2), 
/*     */   
/*     */ 
/*     */ 
/*  44 */   FLOW_CONTROL_ERROR(3), 
/*     */   
/*     */ 
/*     */ 
/*  48 */   SETTINGS_TIMEOUT_ERROR(4), 
/*     */   
/*     */ 
/*     */ 
/*  52 */   STREAM_CLOSED_ERROR(5), 
/*     */   
/*     */ 
/*     */ 
/*  56 */   FRAME_SIZE_ERROR(6), 
/*     */   
/*     */ 
/*     */ 
/*  60 */   REFUSED_STREAM_ERROR(7), 
/*     */   
/*     */ 
/*     */ 
/*  64 */   CANCEL_STREAM_ERROR(8), 
/*     */   
/*     */ 
/*     */ 
/*  68 */   COMPRESSION_ERROR(9), 
/*     */   
/*     */ 
/*     */ 
/*  72 */   HTTP_CONNECT_ERROR(10), 
/*     */   
/*     */ 
/*     */ 
/*  76 */   ENHANCE_YOUR_CALM_ERROR(11), 
/*     */   
/*     */ 
/*     */ 
/*  80 */   INADEQUATE_SECURITY_ERROR(12), 
/*     */   
/*     */ 
/*     */ 
/*  84 */   HTTP_1_1_REQUIRED_ERROR(13);
/*     */   
/*     */   public final int code;
/*     */   
/*     */   private ErrorCode(int code)
/*     */   {
/*  90 */     this.code = code;
/*  91 */     Codes.codes.put(Integer.valueOf(code), this);
/*     */   }
/*     */   
/*     */   public static ErrorCode from(int error)
/*     */   {
/*  96 */     return (ErrorCode)Codes.codes.get(Integer.valueOf(error));
/*     */   }
/*     */   
/*     */   private static class Codes
/*     */   {
/* 101 */     private static final Map<Integer, ErrorCode> codes = new HashMap();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\ErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */