/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.LinkedListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Multimap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GuavaMultimapDeserializer<T extends Multimap<Object, Object>>
/*     */   extends JsonDeserializer<T>
/*     */   implements ContextualDeserializer
/*     */ {
/*  32 */   private static final List<String> METHOD_NAMES = ImmutableList.of("copyOf", "create");
/*     */   
/*     */   private final MapLikeType type;
/*     */   
/*     */   private final KeyDeserializer keyDeserializer;
/*     */   
/*     */   private final TypeDeserializer elementTypeDeserializer;
/*     */   
/*     */   private final JsonDeserializer<?> elementDeserializer;
/*     */   
/*     */   private final Method creatorMethod;
/*     */   
/*     */   public GuavaMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*     */   {
/*  46 */     this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, findTransformer(type.getRawClass()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public GuavaMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer, Method creatorMethod)
/*     */   {
/*  53 */     this.type = type;
/*  54 */     this.keyDeserializer = keyDeserializer;
/*  55 */     this.elementTypeDeserializer = elementTypeDeserializer;
/*  56 */     this.elementDeserializer = elementDeserializer;
/*  57 */     this.creatorMethod = creatorMethod;
/*     */   }
/*     */   
/*     */   private static Method findTransformer(Class<?> rawType)
/*     */   {
/*  62 */     if ((rawType == LinkedListMultimap.class) || (rawType == ListMultimap.class) || (rawType == Multimap.class))
/*     */     {
/*  64 */       return null;
/*     */     }
/*     */     
/*     */ 
/*  68 */     for (String methodName : METHOD_NAMES) {
/*     */       try {
/*  70 */         Method m = rawType.getMethod(methodName, new Class[] { Multimap.class });
/*  71 */         if (m != null) {
/*  72 */           return m;
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  81 */     for (String methodName : METHOD_NAMES) {
/*     */       try {
/*  83 */         Method m = rawType.getMethod(methodName, new Class[] { Multimap.class });
/*  84 */         if (m != null) {
/*  85 */           return m;
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  93 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract T createMultimap();
/*     */   
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 105 */     KeyDeserializer kd = this.keyDeserializer;
/* 106 */     if (kd == null) {
/* 107 */       kd = ctxt.findKeyDeserializer(this.type.getKeyType(), property);
/*     */     }
/* 109 */     JsonDeserializer<?> ed = this.elementDeserializer;
/* 110 */     if (ed == null) {
/* 111 */       ed = ctxt.findContextualValueDeserializer(this.type.getContentType(), property);
/*     */     }
/*     */     
/* 114 */     TypeDeserializer etd = this.elementTypeDeserializer;
/* 115 */     if ((etd != null) && (property != null)) {
/* 116 */       etd = etd.forProperty(property);
/*     */     }
/* 118 */     return _createContextual(this.type, kd, etd, ed, this.creatorMethod);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract JsonDeserializer<?> _createContextual(MapLikeType paramMapLikeType, KeyDeserializer paramKeyDeserializer, TypeDeserializer paramTypeDeserializer, JsonDeserializer<?> paramJsonDeserializer, Method paramMethod);
/*     */   
/*     */ 
/*     */ 
/*     */   public T deserialize(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 130 */     if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
/* 131 */       return deserializeFromSingleValue(jp, ctxt);
/*     */     }
/*     */     
/*     */ 
/* 135 */     return deserializeContents(jp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private T deserializeContents(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 143 */     T multimap = createMultimap();
/*     */     
/* 145 */     expect(jp, JsonToken.START_OBJECT);
/*     */     
/* 147 */     while (jp.nextToken() != JsonToken.END_OBJECT) { Object key;
/*     */       Object key;
/* 149 */       if (this.keyDeserializer != null) {
/* 150 */         key = this.keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);
/*     */       } else {
/* 152 */         key = jp.getCurrentName();
/*     */       }
/*     */       
/* 155 */       jp.nextToken();
/* 156 */       expect(jp, JsonToken.START_ARRAY);
/*     */       
/* 158 */       while (jp.nextToken() != JsonToken.END_ARRAY) { Object value;
/*     */         Object value;
/* 160 */         if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
/* 161 */           value = null; } else { Object value;
/* 162 */           if (this.elementTypeDeserializer != null) {
/* 163 */             value = this.elementDeserializer.deserializeWithType(jp, ctxt, this.elementTypeDeserializer);
/*     */           } else
/* 165 */             value = this.elementDeserializer.deserialize(jp, ctxt);
/*     */         }
/* 167 */         multimap.put(key, value);
/*     */       }
/*     */     }
/* 170 */     if (this.creatorMethod == null) {
/* 171 */       return multimap;
/*     */     }
/*     */     try
/*     */     {
/* 175 */       return (Multimap)this.creatorMethod.invoke(null, new Object[] { multimap });
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 178 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     } catch (IllegalArgumentException e) {
/* 180 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     } catch (IllegalAccessException e) {
/* 182 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     }
/*     */   }
/*     */   
/*     */   private T deserializeFromSingleValue(JsonParser jp, DeserializationContext ctxt)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 189 */     T multimap = createMultimap();
/*     */     
/* 191 */     expect(jp, JsonToken.START_OBJECT);
/*     */     
/* 193 */     while (jp.nextToken() != JsonToken.END_OBJECT) { Object key;
/*     */       Object key;
/* 195 */       if (this.keyDeserializer != null) {
/* 196 */         key = this.keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);
/*     */       } else {
/* 198 */         key = jp.getCurrentName();
/*     */       }
/*     */       
/* 201 */       jp.nextToken();
/*     */       
/*     */ 
/* 204 */       if (jp.currentToken() == JsonToken.START_ARRAY)
/*     */       {
/* 206 */         while (jp.nextToken() != JsonToken.END_ARRAY)
/*     */         {
/* 208 */           Object value = getCurrentTokenValue(jp, ctxt);
/*     */           
/* 210 */           multimap.put(key, value);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 216 */       Object value = getCurrentTokenValue(jp, ctxt);
/*     */       
/* 218 */       multimap.put(key, Collections.singletonList(value));
/*     */     }
/*     */     
/* 221 */     if (this.creatorMethod == null) {
/* 222 */       return multimap;
/*     */     }
/*     */     try
/*     */     {
/* 226 */       return (Multimap)this.creatorMethod.invoke(null, new Object[] { multimap });
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 229 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     } catch (IllegalArgumentException e) {
/* 231 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     } catch (IllegalAccessException e) {
/* 233 */       throw new JsonMappingException(jp, "Could not map to " + this.type, _peel(e));
/*     */     }
/*     */   }
/*     */   
/*     */   private Object getCurrentTokenValue(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
/*     */   {
/*     */     Object value;
/*     */     Object value;
/* 241 */     if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
/* 242 */       value = null; } else { Object value;
/* 243 */       if (this.elementTypeDeserializer != null) {
/* 244 */         value = this.elementDeserializer.deserializeWithType(jp, ctxt, this.elementTypeDeserializer);
/*     */       } else
/* 246 */         value = this.elementDeserializer.deserialize(jp, ctxt);
/*     */     }
/* 248 */     return value;
/*     */   }
/*     */   
/*     */   private void expect(JsonParser jp, JsonToken token) throws IOException {
/* 252 */     if (jp.getCurrentToken() != token) {
/* 253 */       throw new JsonMappingException(jp, "Expecting " + token + ", found " + jp.getCurrentToken(), jp.getCurrentLocation());
/*     */     }
/*     */   }
/*     */   
/*     */   private Throwable _peel(Throwable t)
/*     */   {
/* 259 */     while (t.getCause() != null) {
/* 260 */       t = t.getCause();
/*     */     }
/* 262 */     return t;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\multimap\GuavaMultimapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */