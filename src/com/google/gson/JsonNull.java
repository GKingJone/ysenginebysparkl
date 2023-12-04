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
/*    */ public final class JsonNull
/*    */   extends JsonElement
/*    */ {
/* 32 */   public static final JsonNull INSTANCE = new JsonNull();
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
/*    */   JsonNull deepCopy()
/*    */   {
/* 45 */     return INSTANCE;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 53 */     return JsonNull.class.hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 61 */     return (this == other) || ((other instanceof JsonNull));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\JsonNull.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */