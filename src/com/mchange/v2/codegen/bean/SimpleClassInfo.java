/*    */ package com.mchange.v2.codegen.bean;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleClassInfo
/*    */   implements ClassInfo
/*    */ {
/*    */   String packageName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   int modifiers;
/*    */   
/*    */ 
/*    */ 
/*    */   String className;
/*    */   
/*    */ 
/*    */ 
/*    */   String superclassName;
/*    */   
/*    */ 
/*    */ 
/*    */   String[] interfaceNames;
/*    */   
/*    */ 
/*    */ 
/*    */   String[] generalImports;
/*    */   
/*    */ 
/*    */   String[] specificImports;
/*    */   
/*    */ 
/*    */ 
/* 38 */   public String getPackageName() { return this.packageName; }
/* 39 */   public int getModifiers() { return this.modifiers; }
/* 40 */   public String getClassName() { return this.className; }
/* 41 */   public String getSuperclassName() { return this.superclassName; }
/* 42 */   public String[] getInterfaceNames() { return this.interfaceNames; }
/* 43 */   public String[] getGeneralImports() { return this.generalImports; }
/* 44 */   public String[] getSpecificImports() { return this.specificImports; }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SimpleClassInfo(String packageName, int modifiers, String className, String superclassName, String[] interfaceNames, String[] generalImports, String[] specificImports)
/*    */   {
/* 54 */     this.packageName = packageName;
/* 55 */     this.modifiers = modifiers;
/* 56 */     this.className = className;
/* 57 */     this.superclassName = superclassName;
/* 58 */     this.interfaceNames = interfaceNames;
/* 59 */     this.generalImports = generalImports;
/* 60 */     this.specificImports = specificImports;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\SimpleClassInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */