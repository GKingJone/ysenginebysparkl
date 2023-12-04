/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.set;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.LinkedHashMultimap;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinkedHashMultimapDeserializer
/*    */   extends GuavaMultimapDeserializer<LinkedHashMultimap<Object, Object>>
/*    */ {
/*    */   public LinkedHashMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*    */   {
/* 22 */     super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*    */   }
/*    */   
/*    */ 
/*    */   public LinkedHashMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer, Method creatorMethod)
/*    */   {
/* 28 */     super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod);
/*    */   }
/*    */   
/*    */   protected LinkedHashMultimap<Object, Object> createMultimap()
/*    */   {
/* 33 */     return LinkedHashMultimap.create();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected JsonDeserializer<?> _createContextual(MapLikeType type, KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer, JsonDeserializer<?> elementDeserializer, Method method)
/*    */   {
/* 40 */     return new LinkedHashMultimapDeserializer(type, keyDeserializer, typeDeserializer, elementDeserializer, method);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\multimap\set\LinkedHashMultimapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */