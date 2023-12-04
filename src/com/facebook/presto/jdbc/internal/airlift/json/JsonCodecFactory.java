/*     */ package com.facebook.presto.jdbc.internal.airlift.json;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.reflect.TypeParameter;
/*     */ import com.facebook.presto.jdbc.internal.guava.reflect.TypeToken;
/*     */ import com.facebook.presto.jdbc.internal.inject.Inject;
/*     */ import com.facebook.presto.jdbc.internal.inject.Provider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class JsonCodecFactory
/*     */ {
/*     */   private final Provider<ObjectMapper> objectMapperProvider;
/*     */   private final boolean prettyPrint;
/*     */   
/*     */   public JsonCodecFactory()
/*     */   {
/*  39 */     this(new ObjectMapperProvider());
/*     */   }
/*     */   
/*     */   @Inject
/*     */   public JsonCodecFactory(Provider<ObjectMapper> objectMapperProvider)
/*     */   {
/*  45 */     this(objectMapperProvider, false);
/*     */   }
/*     */   
/*     */   public JsonCodecFactory(Provider<ObjectMapper> objectMapperProvider, boolean prettyPrint)
/*     */   {
/*  50 */     this.objectMapperProvider = objectMapperProvider;
/*  51 */     this.prettyPrint = prettyPrint;
/*     */   }
/*     */   
/*     */   public JsonCodecFactory prettyPrint()
/*     */   {
/*  56 */     return new JsonCodecFactory(this.objectMapperProvider, true);
/*     */   }
/*     */   
/*     */   public <T> JsonCodec<T> jsonCodec(Class<T> type)
/*     */   {
/*  61 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*  63 */     return new JsonCodec(createObjectMapper(), type);
/*     */   }
/*     */   
/*     */   public <T> JsonCodec<T> jsonCodec(Type type)
/*     */   {
/*  68 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*  70 */     return new JsonCodec(createObjectMapper(), type);
/*     */   }
/*     */   
/*     */   public <T> JsonCodec<T> jsonCodec(TypeToken<T> type)
/*     */   {
/*  75 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*  77 */     return new JsonCodec(createObjectMapper(), type.getType());
/*     */   }
/*     */   
/*     */   public <T> JsonCodec<List<T>> listJsonCodec(Class<T> type)
/*     */   {
/*  82 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*     */ 
/*     */ 
/*  86 */     Type listType = new TypeToken()new TypeParameter {}
/*  85 */       .where(new TypeParameter() {}, type)
/*     */       
/*  86 */       .getType();
/*     */     
/*  88 */     return new JsonCodec(createObjectMapper(), listType);
/*     */   }
/*     */   
/*     */   public <T> JsonCodec<List<T>> listJsonCodec(JsonCodec<T> type)
/*     */   {
/*  93 */     Preconditions.checkNotNull(type, "type is null");
/*     */     
/*     */ 
/*     */ 
/*  97 */     Type listType = new TypeToken()new TypeParameter {}
/*  96 */       .where(new TypeParameter() {}, type.getTypeToken())
/*  97 */       .getType();
/*     */     
/*  99 */     return new JsonCodec(createObjectMapper(), listType);
/*     */   }
/*     */   
/*     */   public <K, V> JsonCodec<Map<K, V>> mapJsonCodec(Class<K> keyType, Class<V> valueType)
/*     */   {
/* 104 */     Preconditions.checkNotNull(keyType, "keyType is null");
/* 105 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 110 */     Type mapType = new TypeToken()new TypeParameter {}
/* 108 */       .where(new TypeParameter() {}, keyType)
/*     */       
/* 109 */       .where(new TypeParameter() {}, valueType)
/*     */       
/* 110 */       .getType();
/*     */     
/* 112 */     return new JsonCodec(createObjectMapper(), mapType);
/*     */   }
/*     */   
/*     */   public <K, V> JsonCodec<Map<K, V>> mapJsonCodec(Class<K> keyType, JsonCodec<V> valueType)
/*     */   {
/* 117 */     Preconditions.checkNotNull(keyType, "keyType is null");
/* 118 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 123 */     Type mapType = new TypeToken()new TypeParameter {}
/* 121 */       .where(new TypeParameter() {}, keyType)
/*     */       
/* 122 */       .where(new TypeParameter() {}, valueType.getTypeToken())
/* 123 */       .getType();
/*     */     
/* 125 */     return new JsonCodec(createObjectMapper(), mapType);
/*     */   }
/*     */   
/*     */   private ObjectMapper createObjectMapper()
/*     */   {
/* 130 */     ObjectMapper objectMapper = null;
/*     */     
/*     */ 
/* 133 */     RuntimeException lastException = null;
/* 134 */     for (int i = 0; (objectMapper == null) && (i < 10); i++) {
/*     */       try {
/* 136 */         objectMapper = (ObjectMapper)this.objectMapperProvider.get();
/*     */       }
/*     */       catch (RuntimeException e) {
/* 139 */         lastException = e;
/*     */       }
/*     */     }
/* 142 */     if (objectMapper == null) {
/* 143 */       throw lastException;
/*     */     }
/*     */     
/* 146 */     if (this.prettyPrint) {
/* 147 */       objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
/*     */     }
/*     */     else {
/* 150 */       objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
/*     */     }
/* 152 */     return objectMapper;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonCodecFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */