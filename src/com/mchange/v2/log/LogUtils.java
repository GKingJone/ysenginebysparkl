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
/*    */ 
/*    */ 
/*    */ public final class LogUtils
/*    */ {
/*    */   public static String createParamsList(Object[] params)
/*    */   {
/* 30 */     StringBuffer sb = new StringBuffer(511);
/* 31 */     appendParamsList(sb, params);
/* 32 */     return sb.toString();
/*    */   }
/*    */   
/*    */   public static void appendParamsList(StringBuffer sb, Object[] params)
/*    */   {
/* 37 */     sb.append("[params: ");
/* 38 */     int i = 0; for (int len = params.length; i < len; i++)
/*    */     {
/* 40 */       if (i != 0) sb.append(", ");
/* 41 */       sb.append(params[i]);
/*    */     }
/* 43 */     sb.append(']');
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\LogUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */