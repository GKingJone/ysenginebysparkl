/*    */ package com.facebook.presto.jdbc.internal.airlift.json;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.google.inject.Binder;
/*    */ import com.google.inject.Key;
/*    */ import com.google.inject.Scopes;
/*    */ import com.google.inject.TypeLiteral;
/*    */ import com.google.inject.binder.LinkedBindingBuilder;
/*    */ import com.google.inject.binder.ScopedBindingBuilder;
/*    */ import com.google.inject.internal.MoreTypes.ParameterizedTypeImpl;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class JsonCodecBinder
/*    */ {
/*    */   private final Binder binder;
/*    */   
/*    */   public static JsonCodecBinder jsonCodecBinder(Binder binder)
/*    */   {
/* 37 */     return new JsonCodecBinder(binder);
/*    */   }
/*    */   
/*    */   private JsonCodecBinder(Binder binder)
/*    */   {
/* 42 */     this.binder = ((Binder)Objects.requireNonNull(binder, "binder is null")).skipSources(new Class[] { getClass() });
/*    */   }
/*    */   
/*    */   public void bindJsonCodec(Class<?> type)
/*    */   {
/* 47 */     Preconditions.checkNotNull(type, "type is null");
/*    */     
/* 49 */     this.binder.bind(getJsonCodecKey(type)).toProvider(new JsonCodecProvider(type)).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   public void bindJsonCodec(TypeLiteral<?> type)
/*    */   {
/* 54 */     Preconditions.checkNotNull(type, "type is null");
/*    */     
/* 56 */     this.binder.bind(getJsonCodecKey(type.getType())).toProvider(new JsonCodecProvider(type.getType())).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   public void bindListJsonCodec(Class<?> type)
/*    */   {
/* 61 */     Preconditions.checkNotNull(type, "type is null");
/*    */     
/* 63 */     MoreTypes.ParameterizedTypeImpl listType = new MoreTypes.ParameterizedTypeImpl(null, List.class, new Type[] { type });
/* 64 */     this.binder.bind(getJsonCodecKey(listType)).toProvider(new JsonCodecProvider(listType)).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   public void bindListJsonCodec(JsonCodec<?> type)
/*    */   {
/* 69 */     Preconditions.checkNotNull(type, "type is null");
/*    */     
/* 71 */     MoreTypes.ParameterizedTypeImpl listType = new MoreTypes.ParameterizedTypeImpl(null, List.class, new Type[] { type.getType() });
/* 72 */     this.binder.bind(getJsonCodecKey(listType)).toProvider(new JsonCodecProvider(listType)).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   public void bindMapJsonCodec(Class<?> keyType, Class<?> valueType)
/*    */   {
/* 77 */     Preconditions.checkNotNull(keyType, "keyType is null");
/* 78 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*    */     
/* 80 */     MoreTypes.ParameterizedTypeImpl mapType = new MoreTypes.ParameterizedTypeImpl(null, Map.class, new Type[] { keyType, valueType });
/* 81 */     this.binder.bind(getJsonCodecKey(mapType)).toProvider(new JsonCodecProvider(mapType)).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   public void bindMapJsonCodec(Class<?> keyType, JsonCodec<?> valueType)
/*    */   {
/* 86 */     Preconditions.checkNotNull(keyType, "keyType is null");
/* 87 */     Preconditions.checkNotNull(valueType, "valueType is null");
/*    */     
/* 89 */     MoreTypes.ParameterizedTypeImpl mapType = new MoreTypes.ParameterizedTypeImpl(null, Map.class, new Type[] { keyType, valueType.getType() });
/* 90 */     this.binder.bind(getJsonCodecKey(mapType)).toProvider(new JsonCodecProvider(mapType)).in(Scopes.SINGLETON);
/*    */   }
/*    */   
/*    */   private Key<JsonCodec<?>> getJsonCodecKey(Type type)
/*    */   {
/* 95 */     return Key.get(new MoreTypes.ParameterizedTypeImpl(null, JsonCodec.class, new Type[] { type }));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\json\JsonCodecBinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */