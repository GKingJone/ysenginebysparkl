/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public final class NullProvider
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Object _nullValue;
/*    */   private final boolean _isPrimitive;
/*    */   private final Class<?> _rawType;
/*    */   
/*    */   public NullProvider(JavaType type, Object nullValue)
/*    */   {
/* 25 */     this._nullValue = nullValue;
/* 26 */     this._isPrimitive = type.isPrimitive();
/* 27 */     this._rawType = type.getRawClass();
/*    */   }
/*    */   
/*    */   public Object nullValue(DeserializationContext ctxt) throws JsonProcessingException
/*    */   {
/* 32 */     if ((this._isPrimitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
/* 33 */       ctxt.reportMappingException("Can not map JSON null into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", new Object[] { this._rawType.getName() });
/*    */     }
/*    */     
/* 36 */     return this._nullValue;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\NullProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */