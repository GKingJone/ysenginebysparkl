/*    */ package com.facebook.presto.jdbc.internal.jetty.util.preventers;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.awt.Toolkit;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AWTLeakPreventer
/*    */   extends AbstractLeakPreventer
/*    */ {
/*    */   public void prevent(ClassLoader loader)
/*    */   {
/* 43 */     if (LOG.isDebugEnabled())
/* 44 */       LOG.debug("Pinning classloader for java.awt.EventQueue using " + loader, new Object[0]);
/* 45 */     Toolkit.getDefaultToolkit();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\preventers\AWTLeakPreventer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */