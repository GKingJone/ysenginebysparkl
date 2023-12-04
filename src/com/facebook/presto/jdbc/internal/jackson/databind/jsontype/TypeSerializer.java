/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
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
/*     */ public abstract class TypeSerializer
/*     */ {
/*     */   public abstract TypeSerializer forProperty(BeanProperty paramBeanProperty);
/*     */   
/*     */   public abstract JsonTypeInfo.As getTypeInclusion();
/*     */   
/*     */   public abstract String getPropertyName();
/*     */   
/*     */   public abstract TypeIdResolver getTypeIdResolver();
/*     */   
/*     */   public abstract void writeTypePrefixForScalar(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTypePrefixForObject(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTypePrefixForArray(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTypeSuffixForScalar(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTypeSuffixForObject(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeTypeSuffixForArray(Object paramObject, JsonGenerator paramJsonGenerator)
/*     */     throws IOException;
/*     */   
/*     */   public void writeTypePrefixForScalar(Object value, JsonGenerator jgen, Class<?> type)
/*     */     throws IOException
/*     */   {
/* 136 */     writeTypePrefixForScalar(value, jgen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type)
/*     */     throws IOException
/*     */   {
/* 145 */     writeTypePrefixForObject(value, jgen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTypePrefixForArray(Object value, JsonGenerator jgen, Class<?> type)
/*     */     throws IOException
/*     */   {
/* 154 */     writeTypePrefixForArray(value, jgen);
/*     */   }
/*     */   
/*     */   public abstract void writeCustomTypePrefixForScalar(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException, JsonProcessingException;
/*     */   
/*     */   public abstract void writeCustomTypePrefixForObject(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeCustomTypePrefixForArray(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeCustomTypeSuffixForScalar(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeCustomTypeSuffixForObject(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException;
/*     */   
/*     */   public abstract void writeCustomTypeSuffixForArray(Object paramObject, JsonGenerator paramJsonGenerator, String paramString)
/*     */     throws IOException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\TypeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */