/*    */ package com.facebook.presto.jdbc.internal.guava.reflect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Proxy;
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
/*    */ @Beta
/*    */ public final class Reflection
/*    */ {
/*    */   public static String getPackageName(Class<?> clazz)
/*    */   {
/* 41 */     return getPackageName(clazz.getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static String getPackageName(String classFullName)
/*    */   {
/* 50 */     int lastDot = classFullName.lastIndexOf('.');
/* 51 */     return lastDot < 0 ? "" : classFullName.substring(0, lastDot);
/*    */   }
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
/*    */   public static void initialize(Class<?>... classes)
/*    */   {
/* 67 */     for (Class<?> clazz : classes) {
/*    */       try {
/* 69 */         Class.forName(clazz.getName(), true, clazz.getClassLoader());
/*    */       } catch (ClassNotFoundException e) {
/* 71 */         throw new AssertionError(e);
/*    */       }
/*    */     }
/*    */   }
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
/*    */   public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler)
/*    */   {
/* 88 */     Preconditions.checkNotNull(handler);
/* 89 */     Preconditions.checkArgument(interfaceType.isInterface(), "%s is not an interface", new Object[] { interfaceType });
/* 90 */     Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType }, handler);
/*    */     
/*    */ 
/*    */ 
/* 94 */     return (T)interfaceType.cast(object);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\reflect\Reflection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */