/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertySerializerMap.SerializerAndMapResult;
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class StdKeySerializers
/*     */ {
/*  16 */   protected static final JsonSerializer<Object> DEFAULT_KEY_SERIALIZER = new StdKeySerializer();
/*     */   
/*  18 */   protected static final JsonSerializer<Object> DEFAULT_STRING_SERIALIZER = new StringKeySerializer();
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
/*     */   public static JsonSerializer<Object> getStdKeySerializer(SerializationConfig config, Class<?> rawKeyType, boolean useDefault)
/*     */   {
/*  38 */     if ((rawKeyType == null) || (rawKeyType == Object.class)) {
/*  39 */       return new Dynamic();
/*     */     }
/*  41 */     if (rawKeyType == String.class) {
/*  42 */       return DEFAULT_STRING_SERIALIZER;
/*     */     }
/*  44 */     if ((rawKeyType.isPrimitive()) || (Number.class.isAssignableFrom(rawKeyType)))
/*     */     {
/*     */ 
/*  47 */       return new Default(5, rawKeyType);
/*     */     }
/*  49 */     if (rawKeyType == Class.class) {
/*  50 */       return new Default(3, rawKeyType);
/*     */     }
/*  52 */     if (Date.class.isAssignableFrom(rawKeyType)) {
/*  53 */       return new Default(1, rawKeyType);
/*     */     }
/*  55 */     if (Calendar.class.isAssignableFrom(rawKeyType)) {
/*  56 */       return new Default(2, rawKeyType);
/*     */     }
/*     */     
/*  59 */     if (rawKeyType == java.util.UUID.class) {
/*  60 */       return new Default(5, rawKeyType);
/*     */     }
/*  62 */     if (useDefault) {
/*  63 */       return DEFAULT_KEY_SERIALIZER;
/*     */     }
/*  65 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JsonSerializer<Object> getFallbackKeySerializer(SerializationConfig config, Class<?> rawKeyType)
/*     */   {
/*  76 */     if (rawKeyType != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  81 */       if (rawKeyType == Enum.class) {
/*  82 */         return new Dynamic();
/*     */       }
/*  84 */       if (rawKeyType.isEnum()) {
/*  85 */         return new Default(4, rawKeyType);
/*     */       }
/*     */     }
/*  88 */     return DEFAULT_KEY_SERIALIZER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static JsonSerializer<Object> getDefault()
/*     */   {
/*  96 */     return DEFAULT_KEY_SERIALIZER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Default
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     static final int TYPE_DATE = 1;
/*     */     
/*     */ 
/*     */     static final int TYPE_CALENDAR = 2;
/*     */     
/*     */ 
/*     */     static final int TYPE_CLASS = 3;
/*     */     
/*     */ 
/*     */     static final int TYPE_ENUM = 4;
/*     */     
/*     */ 
/*     */     static final int TYPE_TO_STRING = 5;
/*     */     
/*     */     protected final int _typeId;
/*     */     
/*     */ 
/*     */     public Default(int typeId, Class<?> type)
/*     */     {
/* 123 */       super(false);
/* 124 */       this._typeId = typeId;
/*     */     }
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException
/*     */     {
/* 129 */       switch (this._typeId) {
/*     */       case 1: 
/* 131 */         provider.defaultSerializeDateKey((Date)value, g);
/* 132 */         break;
/*     */       case 2: 
/* 134 */         provider.defaultSerializeDateKey(((Calendar)value).getTimeInMillis(), g);
/* 135 */         break;
/*     */       case 3: 
/* 137 */         g.writeFieldName(((Class)value).getName());
/* 138 */         break;
/*     */       
/*     */       case 4: 
/* 141 */         String str = provider.isEnabled(com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING) ? value.toString() : ((Enum)value).name();
/*     */         
/* 143 */         g.writeFieldName(str);
/*     */         
/* 145 */         break;
/*     */       case 5: 
/*     */       default: 
/* 148 */         g.writeFieldName(value.toString());
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Dynamic
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     protected transient PropertySerializerMap _dynamicSerializers;
/*     */     
/*     */ 
/*     */     public Dynamic()
/*     */     {
/* 163 */       super(false);
/* 164 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/*     */     }
/*     */     
/*     */     Object readResolve()
/*     */     {
/* 169 */       this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
/* 170 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider)
/*     */       throws IOException
/*     */     {
/* 177 */       Class<?> cls = value.getClass();
/* 178 */       PropertySerializerMap m = this._dynamicSerializers;
/* 179 */       JsonSerializer<Object> ser = m.serializerFor(cls);
/* 180 */       if (ser == null) {
/* 181 */         ser = _findAndAddDynamic(m, cls, provider);
/*     */       }
/* 183 */       ser.serialize(value, g, provider);
/*     */     }
/*     */     
/*     */     public void acceptJsonFormatVisitor(com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper visitor, JavaType typeHint) throws com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException
/*     */     {
/* 188 */       visitStringFormat(visitor, typeHint);
/*     */     }
/*     */     
/*     */     protected JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider)
/*     */       throws com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException
/*     */     {
/* 194 */       PropertySerializerMap.SerializerAndMapResult result = map.findAndAddKeySerializer(type, provider, null);
/*     */       
/*     */ 
/*     */ 
/* 198 */       if (map != result.map) {
/* 199 */         this._dynamicSerializers = result.map;
/*     */       }
/* 201 */       return result.serializer;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class StringKeySerializer
/*     */     extends StdSerializer<Object>
/*     */   {
/*     */     public StringKeySerializer()
/*     */     {
/* 210 */       super(false);
/*     */     }
/*     */     
/*     */     public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 214 */       g.writeFieldName((String)value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\StdKeySerializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */