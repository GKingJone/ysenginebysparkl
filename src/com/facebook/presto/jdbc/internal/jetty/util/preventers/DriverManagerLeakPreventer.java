/*    */ package com.facebook.presto.jdbc.internal.jetty.util.preventers;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.sql.DriverManager;
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
/*    */ 
/*    */ public class DriverManagerLeakPreventer
/*    */   extends AbstractLeakPreventer
/*    */ {
/*    */   public void prevent(ClassLoader loader)
/*    */   {
/* 38 */     if (LOG.isDebugEnabled())
/* 39 */       LOG.debug("Pinning DriverManager classloader with " + loader, new Object[0]);
/* 40 */     DriverManager.getDrivers();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\preventers\DriverManagerLeakPreventer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */