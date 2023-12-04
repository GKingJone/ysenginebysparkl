/*    */ package com.facebook.presto.jdbc.internal.guava.reflect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
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
/*    */ abstract class TypeCapture<T>
/*    */ {
/*    */   final Type capture()
/*    */   {
/* 33 */     Type superclass = getClass().getGenericSuperclass();
/* 34 */     Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", new Object[] { superclass });
/*    */     
/* 36 */     return ((ParameterizedType)superclass).getActualTypeArguments()[0];
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\reflect\TypeCapture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */