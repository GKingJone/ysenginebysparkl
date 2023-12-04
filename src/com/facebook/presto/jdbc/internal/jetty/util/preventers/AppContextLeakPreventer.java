/*    */ package com.facebook.presto.jdbc.internal.jetty.util.preventers;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import javax.imageio.ImageIO;
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
/*    */ public class AppContextLeakPreventer
/*    */   extends AbstractLeakPreventer
/*    */ {
/*    */   public void prevent(ClassLoader loader)
/*    */   {
/* 37 */     if (LOG.isDebugEnabled())
/* 38 */       LOG.debug("Pinning classloader for AppContext.getContext() with " + loader, new Object[0]);
/* 39 */     ImageIO.getUseCache();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\preventers\AppContextLeakPreventer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */