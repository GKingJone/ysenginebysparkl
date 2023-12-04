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
/*    */ public abstract class WrapperClassInfo
/*    */   implements ClassInfo
/*    */ {
/*    */   ClassInfo inner;
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
/* 31 */   public WrapperClassInfo(ClassInfo info) { this.inner = info; }
/*    */   
/* 33 */   public String getPackageName() { return this.inner.getPackageName(); }
/* 34 */   public int getModifiers() { return this.inner.getModifiers(); }
/* 35 */   public String getClassName() { return this.inner.getClassName(); }
/* 36 */   public String getSuperclassName() { return this.inner.getSuperclassName(); }
/* 37 */   public String[] getInterfaceNames() { return this.inner.getInterfaceNames(); }
/* 38 */   public String[] getGeneralImports() { return this.inner.getGeneralImports(); }
/* 39 */   public String[] getSpecificImports() { return this.inner.getSpecificImports(); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\WrapperClassInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */