/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleProperty
/*    */   implements Property
/*    */ {
/*    */   int variable_modifiers;
/*    */   
/*    */ 
/*    */ 
/*    */   String name;
/*    */   
/*    */ 
/*    */   String simpleTypeName;
/*    */   
/*    */ 
/*    */   String defensiveCopyExpression;
/*    */   
/*    */ 
/*    */   String defaultValueExpression;
/*    */   
/*    */ 
/*    */   int getter_modifiers;
/*    */   
/*    */ 
/*    */   int setter_modifiers;
/*    */   
/*    */ 
/*    */   boolean is_read_only;
/*    */   
/*    */ 
/*    */   boolean is_bound;
/*    */   
/*    */ 
/*    */   boolean is_constrained;
/*    */   
/*    */ 
/*    */ 
/* 41 */   public int getVariableModifiers() { return this.variable_modifiers; }
/* 42 */   public String getName() { return this.name; }
/* 43 */   public String getSimpleTypeName() { return this.simpleTypeName; }
/* 44 */   public String getDefensiveCopyExpression() { return this.defensiveCopyExpression; }
/* 45 */   public String getDefaultValueExpression() { return this.defaultValueExpression; }
/* 46 */   public int getGetterModifiers() { return this.getter_modifiers; }
/* 47 */   public int getSetterModifiers() { return this.setter_modifiers; }
/* 48 */   public boolean isReadOnly() { return this.is_read_only; }
/* 49 */   public boolean isBound() { return this.is_bound; }
/* 50 */   public boolean isConstrained() { return this.is_constrained; }
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
/*    */   public SimpleProperty(int variable_modifiers, String name, String simpleTypeName, String defensiveCopyExpression, String defaultValueExpression, int getter_modifiers, int setter_modifiers, boolean is_read_only, boolean is_bound, boolean is_constrained)
/*    */   {
/* 63 */     this.variable_modifiers = variable_modifiers;
/* 64 */     this.name = name;
/* 65 */     this.simpleTypeName = simpleTypeName;
/* 66 */     this.defensiveCopyExpression = defensiveCopyExpression;
/* 67 */     this.defaultValueExpression = defaultValueExpression;
/* 68 */     this.getter_modifiers = getter_modifiers;
/* 69 */     this.setter_modifiers = setter_modifiers;
/* 70 */     this.is_read_only = is_read_only;
/* 71 */     this.is_bound = is_bound;
/* 72 */     this.is_constrained = is_constrained;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleProperty(String name, String simpleTypeName, String defensiveCopyExpression, String defaultValueExpression, boolean is_read_only, boolean is_bound, boolean is_constrained)
/*    */   {
/* 83 */     this(2, name, simpleTypeName, defensiveCopyExpression, defaultValueExpression, 1, 1, is_read_only, is_bound, is_constrained);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SimpleProperty.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */