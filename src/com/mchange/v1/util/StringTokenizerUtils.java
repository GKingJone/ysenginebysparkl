/*    */ package com.mchange.v1.util;
/*    */ 
/*    */ import java.util.StringTokenizer;
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
/*    */ public final class StringTokenizerUtils
/*    */ {
/*    */   public static String[] tokenizeToArray(String str, String delim, boolean returntokens)
/*    */   {
/* 32 */     StringTokenizer st = new StringTokenizer(str, delim, returntokens);
/* 33 */     String[] strings = new String[st.countTokens()];
/* 34 */     for (int i = 0; st.hasMoreTokens(); i++)
/* 35 */       strings[i] = st.nextToken();
/* 36 */     return strings;
/*    */   }
/*    */   
/*    */   public static String[] tokenizeToArray(String str, String delim) {
/* 40 */     return tokenizeToArray(str, delim, false);
/*    */   }
/*    */   
/* 43 */   public static String[] tokenizeToArray(String str) { return tokenizeToArray(str, " \t\r\n"); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\StringTokenizerUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */