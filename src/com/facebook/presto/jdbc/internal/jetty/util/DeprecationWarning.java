/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*    */ public class DeprecationWarning
/*    */   implements Decorator
/*    */ {
/* 26 */   private static final Logger LOG = Log.getLogger(DeprecationWarning.class);
/*    */   
/*    */ 
/*    */   public <T> T decorate(T o)
/*    */   {
/* 31 */     if (o == null)
/*    */     {
/* 33 */       return null;
/*    */     }
/*    */     
/* 36 */     Class<?> clazz = o.getClass();
/*    */     
/*    */     try
/*    */     {
/* 40 */       Deprecated depr = (Deprecated)clazz.getAnnotation(Deprecated.class);
/* 41 */       if (depr != null)
/*    */       {
/* 43 */         LOG.warn("Using @Deprecated Class {}", new Object[] { clazz.getName() });
/*    */       }
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 48 */       LOG.ignore(t);
/*    */     }
/*    */     
/* 51 */     verifyIndirectTypes(clazz.getSuperclass(), clazz, "Class");
/* 52 */     for (Class<?> ifaceClazz : clazz.getInterfaces())
/*    */     {
/* 54 */       verifyIndirectTypes(ifaceClazz, clazz, "Interface");
/*    */     }
/*    */     
/* 57 */     return o;
/*    */   }
/*    */   
/*    */ 
/*    */   private void verifyIndirectTypes(Class<?> superClazz, Class<?> clazz, String typeName)
/*    */   {
/*    */     try
/*    */     {
/* 65 */       while ((superClazz != null) && (superClazz != Object.class))
/*    */       {
/* 67 */         Deprecated supDepr = (Deprecated)superClazz.getAnnotation(Deprecated.class);
/* 68 */         if (supDepr != null)
/*    */         {
/* 70 */           LOG.warn("Using indirect @Deprecated {} {} - (seen from {})", new Object[] { typeName, superClazz.getName(), clazz });
/*    */         }
/*    */         
/* 73 */         superClazz = superClazz.getSuperclass();
/*    */       }
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 78 */       LOG.ignore(t);
/*    */     }
/*    */   }
/*    */   
/*    */   public void destroy(Object o) {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\DeprecationWarning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */