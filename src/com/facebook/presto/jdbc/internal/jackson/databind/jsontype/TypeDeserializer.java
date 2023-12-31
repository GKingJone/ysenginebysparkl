/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TypeDeserializer
/*     */ {
/*     */   public abstract TypeDeserializer forProperty(BeanProperty paramBeanProperty);
/*     */   
/*     */   public abstract JsonTypeInfo.As getTypeInclusion();
/*     */   
/*     */   public abstract String getPropertyName();
/*     */   
/*     */   public abstract TypeIdResolver getTypeIdResolver();
/*     */   
/*     */   public abstract Class<?> getDefaultImpl();
/*     */   
/*     */   public abstract Object deserializeTypedFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException;
/*     */   
/*     */   public abstract Object deserializeTypedFromArray(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException;
/*     */   
/*     */   public abstract Object deserializeTypedFromScalar(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException;
/*     */   
/*     */   public abstract Object deserializeTypedFromAny(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*     */     throws IOException;
/*     */   
/*     */   public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, JavaType baseType)
/*     */     throws IOException
/*     */   {
/* 137 */     return deserializeIfNatural(p, ctxt, baseType.getRawClass());
/*     */   }
/*     */   
/*     */ 
/*     */   public static Object deserializeIfNatural(JsonParser p, DeserializationContext ctxt, Class<?> base)
/*     */     throws IOException
/*     */   {
/* 144 */     JsonToken t = p.getCurrentToken();
/* 145 */     if (t == null) {
/* 146 */       return null;
/*     */     }
/* 148 */     switch (t) {
/*     */     case VALUE_STRING: 
/* 150 */       if (base.isAssignableFrom(String.class)) {
/* 151 */         return p.getText();
/*     */       }
/*     */       break;
/*     */     case VALUE_NUMBER_INT: 
/* 155 */       if (base.isAssignableFrom(Integer.class)) {
/* 156 */         return Integer.valueOf(p.getIntValue());
/*     */       }
/*     */       
/*     */       break;
/*     */     case VALUE_NUMBER_FLOAT: 
/* 161 */       if (base.isAssignableFrom(Double.class)) {
/* 162 */         return Double.valueOf(p.getDoubleValue());
/*     */       }
/*     */       break;
/*     */     case VALUE_TRUE: 
/* 166 */       if (base.isAssignableFrom(Boolean.class)) {
/* 167 */         return Boolean.TRUE;
/*     */       }
/*     */       break;
/*     */     case VALUE_FALSE: 
/* 171 */       if (base.isAssignableFrom(Boolean.class)) {
/* 172 */         return Boolean.FALSE;
/*     */       }
/*     */       break;
/*     */     }
/* 176 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\TypeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */