/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class RuntimeIOException
/*    */   extends RuntimeException
/*    */ {
/*    */   public RuntimeIOException(IOException cause)
/*    */   {
/* 10 */     super(cause);
/*    */   }
/*    */   
/*    */   public RuntimeIOException(String message, IOException cause)
/*    */   {
/* 15 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\RuntimeIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */