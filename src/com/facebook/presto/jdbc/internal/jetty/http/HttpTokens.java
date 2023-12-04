/*    */ package com.facebook.presto.jdbc.internal.jetty.http;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface HttpTokens
/*    */ {
/*    */   public static final byte COLON = 58;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final byte TAB = 9;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final byte LINE_FEED = 10;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final byte CARRIAGE_RETURN = 13;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final byte SPACE = 32;
/*    */   
/*    */ 
/*    */ 
/* 32 */   public static final byte[] CRLF = { 13, 10 };
/*    */   public static final byte SEMI_COLON = 59;
/*    */   
/* 35 */   public static enum EndOfContent { UNKNOWN_CONTENT,  NO_CONTENT,  EOF_CONTENT,  CONTENT_LENGTH,  CHUNKED_CONTENT;
/*    */     
/*    */     private EndOfContent() {}
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpTokens.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */