/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AsArrayTypeSerializer
/*     */   extends TypeSerializerBase
/*     */ {
/*     */   public AsArrayTypeSerializer(TypeIdResolver idRes, BeanProperty property)
/*     */   {
/*  18 */     super(idRes, property);
/*     */   }
/*     */   
/*     */   public AsArrayTypeSerializer forProperty(BeanProperty prop)
/*     */   {
/*  23 */     return this._property == prop ? this : new AsArrayTypeSerializer(this._idResolver, prop);
/*     */   }
/*     */   
/*     */   public JsonTypeInfo.As getTypeInclusion() {
/*  27 */     return JsonTypeInfo.As.WRAPPER_ARRAY;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTypePrefixForObject(Object value, JsonGenerator jgen)
/*     */     throws IOException
/*     */   {
/*  37 */     String typeId = idFromValue(value);
/*     */     
/*  39 */     if (jgen.canWriteTypeId()) {
/*  40 */       if (typeId != null) {
/*  41 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/*  44 */       jgen.writeStartArray();
/*  45 */       jgen.writeString(typeId);
/*     */     }
/*  47 */     jgen.writeStartObject();
/*     */   }
/*     */   
/*     */   public void writeTypePrefixForObject(Object value, JsonGenerator jgen, Class<?> type) throws IOException
/*     */   {
/*  52 */     String typeId = idFromValueAndType(value, type);
/*     */     
/*  54 */     if (jgen.canWriteTypeId()) {
/*  55 */       if (typeId != null) {
/*  56 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/*  59 */       jgen.writeStartArray();
/*  60 */       jgen.writeString(typeId);
/*     */     }
/*  62 */     jgen.writeStartObject();
/*     */   }
/*     */   
/*     */   public void writeTypePrefixForArray(Object value, JsonGenerator jgen) throws IOException
/*     */   {
/*  67 */     String typeId = idFromValue(value);
/*  68 */     if (jgen.canWriteTypeId()) {
/*  69 */       if (typeId != null) {
/*  70 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/*  73 */       jgen.writeStartArray();
/*  74 */       jgen.writeString(typeId);
/*     */     }
/*  76 */     jgen.writeStartArray();
/*     */   }
/*     */   
/*     */   public void writeTypePrefixForArray(Object value, JsonGenerator jgen, Class<?> type) throws IOException
/*     */   {
/*  81 */     String typeId = idFromValueAndType(value, type);
/*  82 */     if (jgen.canWriteTypeId()) {
/*  83 */       if (typeId != null) {
/*  84 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/*  87 */       jgen.writeStartArray();
/*  88 */       jgen.writeString(typeId);
/*     */     }
/*  90 */     jgen.writeStartArray();
/*     */   }
/*     */   
/*     */   public void writeTypePrefixForScalar(Object value, JsonGenerator jgen) throws IOException
/*     */   {
/*  95 */     String typeId = idFromValue(value);
/*  96 */     if (jgen.canWriteTypeId()) {
/*  97 */       if (typeId != null) {
/*  98 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     }
/*     */     else {
/* 102 */       jgen.writeStartArray();
/* 103 */       jgen.writeString(typeId);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTypePrefixForScalar(Object value, JsonGenerator jgen, Class<?> type) throws IOException
/*     */   {
/* 109 */     String typeId = idFromValueAndType(value, type);
/* 110 */     if (jgen.canWriteTypeId()) {
/* 111 */       if (typeId != null) {
/* 112 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     }
/*     */     else {
/* 116 */       jgen.writeStartArray();
/* 117 */       jgen.writeString(typeId);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTypeSuffixForObject(Object value, JsonGenerator jgen)
/*     */     throws IOException
/*     */   {
/* 129 */     jgen.writeEndObject();
/* 130 */     if (!jgen.canWriteTypeId()) {
/* 131 */       jgen.writeEndArray();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTypeSuffixForArray(Object value, JsonGenerator jgen)
/*     */     throws IOException
/*     */   {
/* 138 */     jgen.writeEndArray();
/* 139 */     if (!jgen.canWriteTypeId()) {
/* 140 */       jgen.writeEndArray();
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeTypeSuffixForScalar(Object value, JsonGenerator jgen) throws IOException
/*     */   {
/* 146 */     if (!jgen.canWriteTypeId())
/*     */     {
/* 148 */       jgen.writeEndArray();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeCustomTypePrefixForObject(Object value, JsonGenerator jgen, String typeId)
/*     */     throws IOException
/*     */   {
/* 160 */     if (jgen.canWriteTypeId()) {
/* 161 */       if (typeId != null) {
/* 162 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/* 165 */       jgen.writeStartArray();
/* 166 */       jgen.writeString(typeId);
/*     */     }
/* 168 */     jgen.writeStartObject();
/*     */   }
/*     */   
/*     */   public void writeCustomTypePrefixForArray(Object value, JsonGenerator jgen, String typeId) throws IOException
/*     */   {
/* 173 */     if (jgen.canWriteTypeId()) {
/* 174 */       if (typeId != null) {
/* 175 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/* 178 */       jgen.writeStartArray();
/* 179 */       jgen.writeString(typeId);
/*     */     }
/* 181 */     jgen.writeStartArray();
/*     */   }
/*     */   
/*     */   public void writeCustomTypePrefixForScalar(Object value, JsonGenerator jgen, String typeId) throws IOException
/*     */   {
/* 186 */     if (jgen.canWriteTypeId()) {
/* 187 */       if (typeId != null) {
/* 188 */         jgen.writeTypeId(typeId);
/*     */       }
/*     */     } else {
/* 191 */       jgen.writeStartArray();
/* 192 */       jgen.writeString(typeId);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeCustomTypeSuffixForObject(Object value, JsonGenerator jgen, String typeId) throws IOException
/*     */   {
/* 198 */     if (!jgen.canWriteTypeId()) {
/* 199 */       writeTypeSuffixForObject(value, jgen);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeCustomTypeSuffixForArray(Object value, JsonGenerator jgen, String typeId) throws IOException
/*     */   {
/* 205 */     if (!jgen.canWriteTypeId()) {
/* 206 */       writeTypeSuffixForArray(value, jgen);
/*     */     }
/*     */   }
/*     */   
/*     */   public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator jgen, String typeId) throws IOException
/*     */   {
/* 212 */     if (!jgen.canWriteTypeId()) {
/* 213 */       writeTypeSuffixForScalar(value, jgen);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\jsontype\impl\AsArrayTypeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */