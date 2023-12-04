/*    */ package com.facebook.presto.jdbc.internal.jetty.util.preventers;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.lang.reflect.Method;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GCThreadLeakPreventer
/*    */   extends AbstractLeakPreventer
/*    */ {
/*    */   public void prevent(ClassLoader loader)
/*    */   {
/*    */     try
/*    */     {
/* 50 */       Class<?> clazz = Class.forName("sun.misc.GC");
/* 51 */       Method requestLatency = clazz.getMethod("requestLatency", new Class[] { Long.TYPE });
/* 52 */       requestLatency.invoke(null, new Object[] { Long.valueOf(9223372036854775806L) });
/*    */     }
/*    */     catch (ClassNotFoundException e)
/*    */     {
/* 56 */       LOG.ignore(e);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 60 */       LOG.warn(e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\preventers\GCThreadLeakPreventer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */