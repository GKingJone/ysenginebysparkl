/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*    */ 
/*    */ 
/*    */ public class ImmutableMapDeserializer
/*    */   extends GuavaImmutableMapDeserializer<ImmutableMap<Object, Object>>
/*    */ {
/*    */   public ImmutableMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 16 */     super(type, keyDeser, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */   public ImmutableMapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 22 */     return new ImmutableMapDeserializer(this._mapType, keyDeser, typeDeser, valueDeser);
/*    */   }
/*    */   
/*    */ 
/*    */   protected Builder<Object, Object> createBuilder()
/*    */   {
/* 28 */     return ImmutableMap.builder();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableMapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */