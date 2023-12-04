/*    */ package com.facebook.presto.jdbc.internal.spi.function;
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
/*    */ public enum OperatorType
/*    */ {
/* 18 */   ADD("+"), 
/* 19 */   SUBTRACT("-"), 
/* 20 */   MULTIPLY("*"), 
/* 21 */   DIVIDE("/"), 
/* 22 */   MODULUS("%"), 
/* 23 */   NEGATION("-"), 
/* 24 */   EQUAL("="), 
/* 25 */   NOT_EQUAL("<>"), 
/* 26 */   LESS_THAN("<"), 
/* 27 */   LESS_THAN_OR_EQUAL("<="), 
/* 28 */   GREATER_THAN(">"), 
/* 29 */   GREATER_THAN_OR_EQUAL(">="), 
/* 30 */   BETWEEN("BETWEEN"), 
/* 31 */   CAST("CAST"), 
/* 32 */   SUBSCRIPT("[]"), 
/* 33 */   HASH_CODE("HASH CODE"), 
/* 34 */   SATURATED_FLOOR_CAST("SATURATED FLOOR CAST");
/*    */   
/*    */   private final String operator;
/*    */   
/*    */   private OperatorType(String operator)
/*    */   {
/* 40 */     this.operator = operator;
/*    */   }
/*    */   
/*    */   public String getOperator()
/*    */   {
/* 45 */     return this.operator;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\OperatorType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */