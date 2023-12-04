/*    */ package com.mchange.v2.codegen.bean;
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
/*    */ class SimplePropertyMask
/*    */   implements Property
/*    */ {
/*    */   Property p;
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
/*    */   SimplePropertyMask(Property p)
/*    */   {
/* 33 */     this.p = p;
/*    */   }
/*    */   
/* 36 */   public int getVariableModifiers() { return 2; }
/*    */   
/*    */   public String getName() {
/* 39 */     return this.p.getName();
/*    */   }
/*    */   
/* 42 */   public String getSimpleTypeName() { return this.p.getSimpleTypeName(); }
/*    */   
/*    */   public String getDefensiveCopyExpression() {
/* 45 */     return null;
/*    */   }
/*    */   
/* 48 */   public String getDefaultValueExpression() { return this.p.getDefaultValueExpression(); }
/*    */   
/*    */   public int getGetterModifiers() {
/* 51 */     return 1;
/*    */   }
/*    */   
/* 54 */   public int getSetterModifiers() { return 1; }
/*    */   
/*    */   public boolean isReadOnly() {
/* 57 */     return false;
/*    */   }
/*    */   
/* 60 */   public boolean isBound() { return false; }
/*    */   
/*    */   public boolean isConstrained() {
/* 63 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SimplePropertyMask.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */