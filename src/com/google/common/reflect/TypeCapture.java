/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
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


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\reflect\TypeCapture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */