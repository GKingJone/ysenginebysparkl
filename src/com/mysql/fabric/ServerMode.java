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
/*    */ 
/*    */ public enum ServerMode
/*    */ {
/* 31 */   OFFLINE,  READ_ONLY,  WRITE_ONLY,  READ_WRITE;
/*    */   
/*    */   private ServerMode() {}
/* 34 */   public static ServerMode getFromConstant(Integer constant) { return values()[constant.intValue()]; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ServerMode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */