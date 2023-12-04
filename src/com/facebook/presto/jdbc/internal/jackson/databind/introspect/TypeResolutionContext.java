/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface TypeResolutionContext
/*    */ {
/*    */   public abstract JavaType resolveType(Type paramType);
/*    */   
/*    */   public static class Basic
/*    */     implements TypeResolutionContext
/*    */   {
/*    */     private final TypeFactory _typeFactory;
/*    */     private final TypeBindings _bindings;
/*    */     
/*    */     public Basic(TypeFactory tf, TypeBindings b)
/*    */     {
/* 25 */       this._typeFactory = tf;
/* 26 */       this._bindings = b;
/*    */     }
/*    */     
/*    */     public JavaType resolveType(Type type)
/*    */     {
/* 31 */       return this._typeFactory.constructType(type, this._bindings);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\TypeResolutionContext.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */