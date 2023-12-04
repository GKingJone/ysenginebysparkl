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
/*    */ public abstract class WrapperProperty
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
/*    */   public WrapperProperty(Property p)
/*    */   {
/* 33 */     this.p = p;
/*    */   }
/*    */   
/* 36 */   protected Property getInner() { return this.p; }
/*    */   
/*    */   public int getVariableModifiers() {
/* 39 */     return this.p.getVariableModifiers();
/*    */   }
/*    */   
/* 42 */   public String getName() { return this.p.getName(); }
/*    */   
/*    */   public String getSimpleTypeName() {
/* 45 */     return this.p.getSimpleTypeName();
/*    */   }
/*    */   
/* 48 */   public String getDefensiveCopyExpression() { return this.p.getDefensiveCopyExpression(); }
/*    */   
/*    */   public String getDefaultValueExpression() {
/* 51 */     return this.p.getDefaultValueExpression();
/*    */   }
/*    */   
/* 54 */   public int getGetterModifiers() { return this.p.getGetterModifiers(); }
/*    */   
/*    */   public int getSetterModifiers() {
/* 57 */     return this.p.getSetterModifiers();
/*    */   }
/*    */   
/* 60 */   public boolean isReadOnly() { return this.p.isReadOnly(); }
/*    */   
/*    */   public boolean isBound() {
/* 63 */     return this.p.isBound();
/*    */   }
/*    */   
/* 66 */   public boolean isConstrained() { return this.p.isConstrained(); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\WrapperProperty.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */