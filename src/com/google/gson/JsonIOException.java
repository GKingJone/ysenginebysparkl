/*    */ package com.google.gson;
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
/*    */ public final class JsonIOException
/*    */   extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
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
/*    */   public JsonIOException(String msg)
/*    */   {
/* 29 */     super(msg);
/*    */   }
/*    */   
/*    */   public JsonIOException(String msg, Throwable cause) {
/* 33 */     super(msg, cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonIOException(Throwable cause)
/*    */   {
/* 43 */     super(cause);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\JsonIOException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */