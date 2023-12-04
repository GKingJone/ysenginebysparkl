/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ public class MemoryUtils
/*    */ {
/*    */   private static final int cacheLineBytes;
/*    */   
/*    */   static
/*    */   {
/* 32 */     int defaultValue = 64;
/* 33 */     int value = 64;
/*    */     try
/*    */     {
/* 36 */       value = Integer.parseInt((String)AccessController.doPrivileged(new PrivilegedAction()
/*    */       {
/*    */ 
/*    */         public String run()
/*    */         {
/* 41 */           return System.getProperty("com.facebook.presto.jdbc.internal.jetty.util.cacheLineBytes", String.valueOf(64));
/*    */         }
/*    */       }));
/*    */     }
/*    */     catch (Exception localException) {}
/*    */     
/*    */ 
/* 48 */     cacheLineBytes = value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static int getCacheLineBytes()
/*    */   {
/* 57 */     return cacheLineBytes;
/*    */   }
/*    */   
/*    */   public static int getIntegersPerCacheLine()
/*    */   {
/* 62 */     return getCacheLineBytes() >> 2;
/*    */   }
/*    */   
/*    */   public static int getLongsPerCacheLine()
/*    */   {
/* 67 */     return getCacheLineBytes() >> 3;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MemoryUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */