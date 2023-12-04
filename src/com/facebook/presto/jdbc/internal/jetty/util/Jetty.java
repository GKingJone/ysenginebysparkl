/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
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
/*    */ public class Jetty
/*    */ {
/*    */   public static final String VERSION;
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
/*    */   static
/*    */   {
/* 28 */     Package pkg = Jetty.class.getPackage();
/* 29 */     if ((pkg != null) && 
/* 30 */       ("Eclipse.org - Jetty".equals(pkg.getImplementationVendor())) && 
/* 31 */       (pkg.getImplementationVersion() != null)) {
/* 32 */       VERSION = pkg.getImplementationVersion();
/*    */     } else
/* 34 */       VERSION = System.getProperty("jetty.version", "9.3.z-SNAPSHOT"); }
/*    */   
/* 36 */   public static final String POWERED_BY = "<a href=\"http://eclipse.org/jetty\">Powered by Jetty:// " + VERSION + "</a>";
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Jetty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */