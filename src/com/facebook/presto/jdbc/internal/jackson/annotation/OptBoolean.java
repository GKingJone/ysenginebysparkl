/*    */ package com.facebook.presto.jdbc.internal.jackson.annotation;
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
/*    */ public enum OptBoolean
/*    */ {
/* 23 */   TRUE, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 29 */   FALSE, 
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 37 */   DEFAULT;
/*    */   
/*    */   private OptBoolean() {}
/* 40 */   public Boolean asBoolean() { if (this == DEFAULT) return null;
/* 41 */     return this == TRUE ? Boolean.TRUE : Boolean.FALSE;
/*    */   }
/*    */   
/*    */   public boolean asPrimitive() {
/* 45 */     return this == TRUE;
/*    */   }
/*    */   
/*    */   public static OptBoolean fromBoolean(Boolean b) {
/* 49 */     if (b == null) {
/* 50 */       return DEFAULT;
/*    */     }
/* 52 */     return b.booleanValue() ? TRUE : FALSE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\OptBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */