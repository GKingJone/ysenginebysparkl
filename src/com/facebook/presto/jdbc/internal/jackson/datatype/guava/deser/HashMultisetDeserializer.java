/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.HashMultiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HashMultisetDeserializer
/*    */   extends GuavaMultisetDeserializer<HashMultiset<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public HashMultisetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 17 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */   public HashMultisetDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 23 */     return new HashMultisetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */   
/*    */ 
/*    */   protected HashMultiset<Object> createMultiset()
/*    */   {
/* 29 */     return HashMultiset.create();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\HashMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */