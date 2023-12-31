/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.ContainerSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.AsArraySerializerBase;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public final class IndexedListSerializer
/*     */   extends AsArraySerializerBase<List<?>>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public IndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer)
/*     */   {
/*  27 */     super(List.class, elemType, staticTyping, vts, valueSerializer);
/*     */   }
/*     */   
/*     */ 
/*     */   public IndexedListSerializer(IndexedListSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle)
/*     */   {
/*  33 */     super(src, property, vts, valueSerializer, unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IndexedListSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle)
/*     */   {
/*  40 */     return new IndexedListSerializer(this, property, vts, elementSerializer, unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty(SerializerProvider prov, List<?> value)
/*     */   {
/*  51 */     return (value == null) || (value.isEmpty());
/*     */   }
/*     */   
/*     */   public boolean hasSingleElement(List<?> value)
/*     */   {
/*  56 */     return value.size() == 1;
/*     */   }
/*     */   
/*     */   public ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts)
/*     */   {
/*  61 */     return new IndexedListSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void serialize(List<?> value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  69 */     int len = value.size();
/*  70 */     if ((len == 1) && (
/*  71 */       ((this._unwrapSingle == null) && (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))) || (this._unwrapSingle == Boolean.TRUE)))
/*     */     {
/*     */ 
/*  74 */       serializeContents(value, gen, provider);
/*  75 */       return;
/*     */     }
/*     */     
/*  78 */     gen.writeStartArray(len);
/*  79 */     serializeContents(value, gen, provider);
/*  80 */     gen.writeEndArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeContents(List<?> value, JsonGenerator jgen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  87 */     if (this._elementSerializer != null) {
/*  88 */       serializeContentsUsing(value, jgen, provider, this._elementSerializer);
/*  89 */       return;
/*     */     }
/*  91 */     if (this._valueTypeSerializer != null) {
/*  92 */       serializeTypedContents(value, jgen, provider);
/*  93 */       return;
/*     */     }
/*  95 */     int len = value.size();
/*  96 */     if (len == 0) {
/*  97 */       return;
/*     */     }
/*  99 */     int i = 0;
/*     */     try {
/* 101 */       PropertySerializerMap serializers = this._dynamicSerializers;
/* 102 */       for (; i < len; i++) {
/* 103 */         Object elem = value.get(i);
/* 104 */         if (elem == null) {
/* 105 */           provider.defaultSerializeNull(jgen);
/*     */         } else {
/* 107 */           Class<?> cc = elem.getClass();
/* 108 */           JsonSerializer<Object> serializer = serializers.serializerFor(cc);
/* 109 */           if (serializer == null)
/*     */           {
/* 111 */             if (this._elementType.hasGenericTypes()) {
/* 112 */               serializer = _findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
/*     */             }
/*     */             else {
/* 115 */               serializer = _findAndAddDynamic(serializers, cc, provider);
/*     */             }
/* 117 */             serializers = this._dynamicSerializers;
/*     */           }
/* 119 */           serializer.serialize(elem, jgen, provider);
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 123 */       wrapAndThrow(provider, e, value, i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeContentsUsing(List<?> value, JsonGenerator jgen, SerializerProvider provider, JsonSerializer<Object> ser)
/*     */     throws IOException
/*     */   {
/* 131 */     int len = value.size();
/* 132 */     if (len == 0) {
/* 133 */       return;
/*     */     }
/* 135 */     TypeSerializer typeSer = this._valueTypeSerializer;
/* 136 */     for (int i = 0; i < len; i++) {
/* 137 */       Object elem = value.get(i);
/*     */       try {
/* 139 */         if (elem == null) {
/* 140 */           provider.defaultSerializeNull(jgen);
/* 141 */         } else if (typeSer == null) {
/* 142 */           ser.serialize(elem, jgen, provider);
/*     */         } else {
/* 144 */           ser.serializeWithType(elem, jgen, provider, typeSer);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 148 */         wrapAndThrow(provider, e, value, i);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void serializeTypedContents(List<?> value, JsonGenerator jgen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/* 156 */     int len = value.size();
/* 157 */     if (len == 0) {
/* 158 */       return;
/*     */     }
/* 160 */     int i = 0;
/*     */     try {
/* 162 */       TypeSerializer typeSer = this._valueTypeSerializer;
/* 163 */       PropertySerializerMap serializers = this._dynamicSerializers;
/* 164 */       for (; i < len; i++) {
/* 165 */         Object elem = value.get(i);
/* 166 */         if (elem == null) {
/* 167 */           provider.defaultSerializeNull(jgen);
/*     */         } else {
/* 169 */           Class<?> cc = elem.getClass();
/* 170 */           JsonSerializer<Object> serializer = serializers.serializerFor(cc);
/* 171 */           if (serializer == null)
/*     */           {
/* 173 */             if (this._elementType.hasGenericTypes()) {
/* 174 */               serializer = _findAndAddDynamic(serializers, provider.constructSpecializedType(this._elementType, cc), provider);
/*     */             }
/*     */             else {
/* 177 */               serializer = _findAndAddDynamic(serializers, cc, provider);
/*     */             }
/* 179 */             serializers = this._dynamicSerializers;
/*     */           }
/* 181 */           serializer.serializeWithType(elem, jgen, provider, typeSer);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 186 */       wrapAndThrow(provider, e, value, i);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\IndexedListSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */