/*    */ package com.mchange.v2.log;
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
/*    */ public class PackageNames
/*    */   implements NameTransformer
/*    */ {
/*    */   public String transformName(String name)
/*    */   {
/* 29 */     return null;
/*    */   }
/*    */   
/*    */   public String transformName(Class cl) {
/* 33 */     String fqcn = cl.getName();
/* 34 */     int i = fqcn.lastIndexOf('.');
/* 35 */     if (i <= 0) {
/* 36 */       return "";
/*    */     }
/* 38 */     return fqcn.substring(0, i);
/*    */   }
/*    */   
/*    */   public String transformName() {
/* 42 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\PackageNames.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */