/*    */ package com.mysql.fabric;
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
/*    */ public enum ServerRole
/*    */ {
/* 30 */   FAULTY,  SPARE,  SECONDARY,  PRIMARY,  CONFIGURING;
/*    */   
/*    */   private ServerRole() {}
/* 33 */   public static ServerRole getFromConstant(Integer constant) { return values()[constant.intValue()]; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ServerRole.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */