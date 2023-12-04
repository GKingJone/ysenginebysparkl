/*    */ package com.facebook.presto.jdbc.internal.jackson.core.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Serializable;
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
/*    */ public class RequestPayload
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected byte[] _payloadAsBytes;
/*    */   protected CharSequence _payloadAsText;
/*    */   protected String _charset;
/*    */   
/*    */   public RequestPayload(byte[] bytes, String charset)
/*    */   {
/* 28 */     if (bytes == null) {
/* 29 */       throw new IllegalArgumentException();
/*    */     }
/* 31 */     this._payloadAsBytes = bytes;
/* 32 */     this._charset = ((charset == null) || (charset.isEmpty()) ? "UTF-8" : charset);
/*    */   }
/*    */   
/*    */   public RequestPayload(CharSequence str) {
/* 36 */     if (str == null) {
/* 37 */       throw new IllegalArgumentException();
/*    */     }
/* 39 */     this._payloadAsText = str;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object getRawPayload()
/*    */   {
/* 49 */     if (this._payloadAsBytes != null) {
/* 50 */       return this._payloadAsBytes;
/*    */     }
/*    */     
/* 53 */     return this._payloadAsText;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 58 */     if (this._payloadAsBytes != null) {
/*    */       try {
/* 60 */         return new String(this._payloadAsBytes, this._charset);
/*    */       } catch (IOException e) {
/* 62 */         throw new RuntimeException(e);
/*    */       }
/*    */     }
/* 65 */     return this._payloadAsText.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\util\RequestPayload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */