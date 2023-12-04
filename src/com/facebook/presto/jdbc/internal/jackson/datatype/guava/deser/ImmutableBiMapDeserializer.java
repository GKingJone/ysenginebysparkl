/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableBiMap;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*    */ 
/*    */ public class ImmutableBiMapDeserializer extends GuavaImmutableMapDeserializer<ImmutableBiMap<Object, Object>>
/*    */ {
/*    */   public ImmutableBiMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 13 */     super(type, keyDeser, typeDeser, deser);
/*    */   }
/*    */   
/*    */   protected com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder<Object, Object> createBuilder()
/*    */   {
/* 18 */     return ImmutableBiMap.builder();
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaMapDeserializer<ImmutableBiMap<Object, Object>> withResolved(KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 24 */     return new ImmutableBiMapDeserializer(this._mapType, keyDeser, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableBiMapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */