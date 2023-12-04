/*    */ package com.facebook.presto.jdbc.internal.airlift.json;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.Module;
/*    */ import com.google.inject.Binder;
/*    */ import com.google.inject.TypeLiteral;
/*    */ import com.google.inject.binder.LinkedBindingBuilder;
/*    */ import com.google.inject.multibindings.MapBinder;
/*    */ import com.google.inject.multibindings.Multibinder;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JsonBinder
/*    */ {
/*    */   private final MapBinder<Class<?>, JsonSerializer<?>> keySerializerMapBinder;
/*    */   private final MapBinder<Class<?>, KeyDeserializer> keyDeserializerMapBinder;
/*    */   private final MapBinder<Class<?>, JsonSerializer<?>> serializerMapBinder;
/*    */   private final MapBinder<Class<?>, JsonDeserializer<?>> deserializerMapBinder;
/*    */   private final Multibinder<Module> moduleBinder;
/*    */   
/*    */   public static JsonBinder jsonBinder(Binder binder)
/*    */   {
/* 41 */     return new JsonBinder(binder);
/*    */   }
/*    */   
/*    */   private JsonBinder(Binder binder)
/*    */   {
/* 46 */     binder = ((Binder)Objects.requireNonNull(binder, "binder is null")).skipSources(new Class[] { getClass() });
/* 47 */     this.keySerializerMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral()new TypeLiteral {}, new TypeLiteral() {}, JsonKeySerde.class);
/* 48 */     this.keyDeserializerMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral()new TypeLiteral {}, new TypeLiteral() {}, JsonKeySerde.class);
/* 49 */     this.serializerMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral()new TypeLiteral {}, new TypeLiteral() {});
/* 50 */     this.deserializerMapBinder = MapBinder.newMapBinder(binder, new TypeLiteral()new TypeLiteral {}, new TypeLiteral() {});
/* 51 */     this.moduleBinder = Multibinder.newSetBinder(binder, Module.class);
/*    */   }
/*    */   
/*    */   public LinkedBindingBuilder<JsonSerializer<?>> addKeySerializerBinding(Class<?> type)
/*    */   {
/* 56 */     Preconditions.checkNotNull(type, "type is null");
/* 57 */     return this.keySerializerMapBinder.addBinding(type);
/*    */   }
/*    */   
/*    */   public LinkedBindingBuilder<KeyDeserializer> addKeyDeserializerBinding(Class<?> type)
/*    */   {
/* 62 */     Preconditions.checkNotNull(type, "type is null");
/* 63 */     return this.keyDeserializerMapBinder.addBinding(type);
/*    */   }
/*    */   
/*    */   public LinkedBindingBuilder<JsonSerializer<?>> addSerializerBinding(Class<?> type)
/*    */   {
/* 68 */     Preconditions.checkNotNull(type, "type is null");
/* 69 */     return this.serializerMapBinder.addBinding(type);
/*    */   }
/*    */   
/*    */   public LinkedBindingBuilder<JsonDeserializer<?>> addDeserializerBinding(Class<?> type)
/*    */   {
/* 74 */     Preconditions.checkNotNull(type, "type is null");
/* 75 */     return this.deserializerMapBinder.addBinding(type);
/*    */   }
/*    */   
/*    */   public LinkedBindingBuilder<Module> addModuleBinding()
/*    */   {
/* 80 */     return this.moduleBinder.addBinding();
/*    */   }
/*    */   
/*    */ 
/*    */   public <T> void bindSerializer(JsonSerializer<T> jsonSerializer)
/*    */   {
/* 86 */     Preconditions.checkNotNull(jsonSerializer, "jsonSerializer is null");
/*    */     
/* 88 */     Class<?> type = jsonSerializer.handledType();
/* 89 */     Preconditions.checkNotNull(type, "jsonSerializer.handledType is null");
/* 90 */     Preconditions.checkArgument(type == Object.class, "jsonSerializer.handledType can not be Object.class");
/* 91 */     this.serializerMapBinder.addBinding(type).toInstance(jsonSerializer);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */