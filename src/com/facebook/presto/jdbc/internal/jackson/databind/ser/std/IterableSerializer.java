/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class IterableSerializer extends AsArraySerializerBase<Iterable<?>>
/*     */ {
/*     */   public IterableSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts)
/*     */   {
/*  19 */     super(Iterable.class, elemType, staticTyping, vts, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public IterableSerializer(IterableSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle)
/*     */   {
/*  25 */     super(src, property, vts, valueSerializer, unwrapSingle);
/*     */   }
/*     */   
/*     */   public com.facebook.presto.jdbc.internal.jackson.databind.ser.ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts)
/*     */   {
/*  30 */     return new IterableSerializer(this, this._property, vts, this._elementSerializer, this._unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IterableSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle)
/*     */   {
/*  37 */     return new IterableSerializer(this, property, vts, elementSerializer, unwrapSingle);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty(SerializerProvider prov, Iterable<?> value)
/*     */   {
/*  43 */     return (value == null) || (!value.iterator().hasNext());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasSingleElement(Iterable<?> value)
/*     */   {
/*  49 */     if (value != null) {
/*  50 */       Iterator<?> it = value.iterator();
/*  51 */       if (it.hasNext()) {
/*  52 */         it.next();
/*  53 */         if (!it.hasNext()) {
/*  54 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*  58 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public final void serialize(Iterable<?> value, JsonGenerator gen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  65 */     if (((this._unwrapSingle == null) && (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED))) || (this._unwrapSingle == Boolean.TRUE))
/*     */     {
/*     */ 
/*  68 */       if (hasSingleElement(value)) {
/*  69 */         serializeContents(value, gen, provider);
/*  70 */         return;
/*     */       }
/*     */     }
/*  73 */     gen.writeStartArray();
/*  74 */     serializeContents(value, gen, provider);
/*  75 */     gen.writeEndArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public void serializeContents(Iterable<?> value, JsonGenerator jgen, SerializerProvider provider)
/*     */     throws IOException
/*     */   {
/*  82 */     Iterator<?> it = value.iterator();
/*  83 */     if (it.hasNext()) {
/*  84 */       TypeSerializer typeSer = this._valueTypeSerializer;
/*  85 */       JsonSerializer<Object> prevSerializer = null;
/*  86 */       Class<?> prevClass = null;
/*     */       do
/*     */       {
/*  89 */         Object elem = it.next();
/*  90 */         if (elem == null) {
/*  91 */           provider.defaultSerializeNull(jgen);
/*     */         }
/*     */         else {
/*  94 */           JsonSerializer<Object> currSerializer = this._elementSerializer;
/*  95 */           if (currSerializer == null)
/*     */           {
/*  97 */             Class<?> cc = elem.getClass();
/*  98 */             if (cc == prevClass) {
/*  99 */               currSerializer = prevSerializer;
/*     */             } else {
/* 101 */               currSerializer = provider.findValueSerializer(cc, this._property);
/* 102 */               prevSerializer = currSerializer;
/* 103 */               prevClass = cc;
/*     */             }
/*     */           }
/* 106 */           if (typeSer == null) {
/* 107 */             currSerializer.serialize(elem, jgen, provider);
/*     */           } else
/* 109 */             currSerializer.serializeWithType(elem, jgen, provider, typeSer);
/*     */         }
/* 111 */       } while (it.hasNext());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\IterableSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */