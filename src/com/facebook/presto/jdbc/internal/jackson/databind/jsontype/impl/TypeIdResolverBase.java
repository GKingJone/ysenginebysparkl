/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DatabindContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*    */ import java.io.IOException;
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
/*    */ public abstract class TypeIdResolverBase
/*    */   implements TypeIdResolver
/*    */ {
/*    */   protected final TypeFactory _typeFactory;
/*    */   protected final JavaType _baseType;
/*    */   
/*    */   protected TypeIdResolverBase()
/*    */   {
/* 34 */     this(null, null);
/*    */   }
/*    */   
/*    */   protected TypeIdResolverBase(JavaType baseType, TypeFactory typeFactory) {
/* 38 */     this._baseType = baseType;
/* 39 */     this._typeFactory = typeFactory;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void init(JavaType bt) {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String idFromBaseType()
/*    */   {
/* 53 */     return idFromValueAndType(null, this._baseType.getRawClass());
/*    */   }
/*    */   
/*    */ 
/*    */   public JavaType typeFromId(DatabindContext context, String id)
/*    */     throws IOException
/*    */   {
/* 60 */     throw new IllegalStateException("Sub-class " + getClass().getName() + " MUST implement " + "`typeFromId(DatabindContext,String)");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getDescForKnownTypeIds()
/*    */   {
/* 70 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\TypeIdResolverBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */