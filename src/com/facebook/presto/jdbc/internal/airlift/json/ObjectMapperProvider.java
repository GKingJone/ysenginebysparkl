/*     */ package com.facebook.presto.jdbc.internal.airlift.json;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.inject.Provider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.Module;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.module.SimpleModule;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.GuavaModule;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jdk8.Jdk8Module;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.JodaModule;
/*     */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.JSR310Module;
/*     */ import com.google.inject.Inject;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ public class ObjectMapperProvider
/*     */   implements Provider<ObjectMapper>
/*     */ {
/*     */   private Map<Class<?>, JsonSerializer<?>> keySerializers;
/*     */   private Map<Class<?>, KeyDeserializer> keyDeserializers;
/*     */   private Map<Class<?>, JsonSerializer<?>> jsonSerializers;
/*     */   private Map<Class<?>, JsonDeserializer<?>> jsonDeserializers;
/*  51 */   private final Set<Module> modules = new HashSet();
/*     */   
/*     */   public ObjectMapperProvider()
/*     */   {
/*  55 */     this.modules.add(new Jdk8Module());
/*  56 */     this.modules.add(new JSR310Module());
/*  57 */     this.modules.add(new GuavaModule());
/*  58 */     this.modules.add(new JodaModule());
/*     */   }
/*     */   
/*     */   @Inject(optional=true)
/*     */   public void setJsonSerializers(Map<Class<?>, JsonSerializer<?>> jsonSerializers)
/*     */   {
/*  64 */     this.jsonSerializers = ImmutableMap.copyOf(jsonSerializers);
/*     */   }
/*     */   
/*     */   @Inject(optional=true)
/*     */   public void setJsonDeserializers(Map<Class<?>, JsonDeserializer<?>> jsonDeserializers)
/*     */   {
/*  70 */     this.jsonDeserializers = ImmutableMap.copyOf(jsonDeserializers);
/*     */   }
/*     */   
/*     */   @Inject(optional=true)
/*     */   public void setKeySerializers(@JsonKeySerde Map<Class<?>, JsonSerializer<?>> keySerializers)
/*     */   {
/*  76 */     this.keySerializers = keySerializers;
/*     */   }
/*     */   
/*     */   @Inject(optional=true)
/*     */   public void setKeyDeserializers(@JsonKeySerde Map<Class<?>, KeyDeserializer> keyDeserializers)
/*     */   {
/*  82 */     this.keyDeserializers = keyDeserializers;
/*     */   }
/*     */   
/*     */   @Inject(optional=true)
/*     */   public void setModules(Set<Module> modules)
/*     */   {
/*  88 */     this.modules.addAll(modules);
/*     */   }
/*     */   
/*     */ 
/*     */   public ObjectMapper get()
/*     */   {
/*  94 */     ObjectMapper objectMapper = new ObjectMapper();
/*     */     
/*     */ 
/*  97 */     objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
/*     */     
/*     */ 
/* 100 */     objectMapper.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
/*     */     
/*     */ 
/* 103 */     objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*     */     
/*     */ 
/* 106 */     objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
/*     */     
/*     */ 
/* 109 */     objectMapper.disable(new MapperFeature[] { MapperFeature.AUTO_DETECT_CREATORS });
/* 110 */     objectMapper.disable(new MapperFeature[] { MapperFeature.AUTO_DETECT_FIELDS });
/* 111 */     objectMapper.disable(new MapperFeature[] { MapperFeature.AUTO_DETECT_SETTERS });
/* 112 */     objectMapper.disable(new MapperFeature[] { MapperFeature.AUTO_DETECT_GETTERS });
/* 113 */     objectMapper.disable(new MapperFeature[] { MapperFeature.AUTO_DETECT_IS_GETTERS });
/* 114 */     objectMapper.disable(new MapperFeature[] { MapperFeature.USE_GETTERS_AS_SETTERS });
/* 115 */     objectMapper.disable(new MapperFeature[] { MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS });
/* 116 */     objectMapper.disable(new MapperFeature[] { MapperFeature.INFER_PROPERTY_MUTATORS });
/* 117 */     objectMapper.disable(new MapperFeature[] { MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS });
/*     */     SimpleModule module;
/* 119 */     if ((this.jsonSerializers != null) || (this.jsonDeserializers != null) || (this.keySerializers != null) || (this.keyDeserializers != null)) {
/* 120 */       module = new SimpleModule(getClass().getName(), new Version(1, 0, 0, null, null, null));
/* 121 */       if (this.jsonSerializers != null) {
/* 122 */         for (Entry<Class<?>, JsonSerializer<?>> entry : this.jsonSerializers.entrySet()) {
/* 123 */           addSerializer(module, (Class)entry.getKey(), (JsonSerializer)entry.getValue());
/*     */         }
/*     */       }
/* 126 */       if (this.jsonDeserializers != null) {
/* 127 */         for (Entry<Class<?>, JsonDeserializer<?>> entry : this.jsonDeserializers.entrySet()) {
/* 128 */           addDeserializer(module, (Class)entry.getKey(), (JsonDeserializer)entry.getValue());
/*     */         }
/*     */       }
/* 131 */       if (this.keySerializers != null) {
/* 132 */         for (Entry<Class<?>, JsonSerializer<?>> entry : this.keySerializers.entrySet()) {
/* 133 */           addKeySerializer(module, (Class)entry.getKey(), (JsonSerializer)entry.getValue());
/*     */         }
/*     */       }
/* 136 */       if (this.keyDeserializers != null) {
/* 137 */         for (Entry<Class<?>, KeyDeserializer> entry : this.keyDeserializers.entrySet()) {
/* 138 */           module.addKeyDeserializer((Class)entry.getKey(), (KeyDeserializer)entry.getValue());
/*     */         }
/*     */       }
/* 141 */       this.modules.add(module);
/*     */     }
/*     */     
/* 144 */     for (Module module : this.modules) {
/* 145 */       objectMapper.registerModule(module);
/*     */     }
/*     */     
/* 148 */     return objectMapper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private <T> void addSerializer(SimpleModule module, Class<?> type, JsonSerializer<?> jsonSerializer)
/*     */   {
/* 161 */     module.addSerializer(type, jsonSerializer);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> void addDeserializer(SimpleModule module, Class<?> type, JsonDeserializer<?> jsonDeserializer)
/*     */   {
/* 167 */     module.addDeserializer(type, jsonDeserializer);
/*     */   }
/*     */   
/*     */ 
/*     */   private <T> void addKeySerializer(SimpleModule module, Class<?> type, JsonSerializer<?> keySerializer)
/*     */   {
/* 173 */     module.addKeySerializer(type, keySerializer);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\ObjectMapperProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */