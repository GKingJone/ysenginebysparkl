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
/*    */ class PropertyComparator
/*    */ {
/*    */   public int compare(Object a, Object b)
/*    */   {
/* 30 */     Property aa = (Property)a;
/* 31 */     Property bb = (Property)b;
/*    */     
/* 33 */     return aa.getName().compareTo(bb.getName());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\PropertyComparator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */