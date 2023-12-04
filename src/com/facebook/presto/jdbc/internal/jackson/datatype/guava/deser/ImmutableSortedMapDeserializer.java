/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedMap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedMap.Builder;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*    */ 
/*    */ public class ImmutableSortedMapDeserializer extends GuavaImmutableMapDeserializer<ImmutableSortedMap<Object, Object>>
/*    */ {
/*    */   public ImmutableSortedMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 14 */     super(type, keyDeser, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder<Object, Object> createBuilder()
/*    */   {
/* 26 */     Builder<?, Object> naturalOrder = ImmutableSortedMap.naturalOrder();
/* 27 */     Builder<Object, Object> builder = naturalOrder;
/* 28 */     return builder;
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaMapDeserializer<ImmutableSortedMap<Object, Object>> withResolved(KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 34 */     return new ImmutableSortedMapDeserializer(this._mapType, keyDeser, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableSortedMapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */