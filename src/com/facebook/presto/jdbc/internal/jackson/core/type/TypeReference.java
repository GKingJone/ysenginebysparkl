/*    */ package com.facebook.presto.jdbc.internal.jackson.core.type;
/*    */ 
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
/*    */ public abstract class TypeReference<T>
/*    */   implements Comparable<TypeReference<T>>
/*    */ {
/*    */   protected final Type _type;
/*    */   
/*    */   protected TypeReference()
/*    */   {
/* 33 */     Type superClass = getClass().getGenericSuperclass();
/* 34 */     if ((superClass instanceof Class)) {
/* 35 */       throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 44 */     this._type = ((java.lang.reflect.ParameterizedType)superClass).getActualTypeArguments()[0];
/*    */   }
/*    */   
/* 47 */   public Type getType() { return this._type; }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int compareTo(TypeReference<T> o)
/*    */   {
/* 55 */     return 0;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\type\TypeReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */