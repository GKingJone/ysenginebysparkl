/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMultiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ 
/*    */ public class ImmutableMultisetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableMultiset<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ImmutableMultisetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 14 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */   protected com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.Builder<Object> createBuilder()
/*    */   {
/* 19 */     return ImmutableMultiset.builder();
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaCollectionDeserializer<ImmutableMultiset<Object>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 25 */     return new ImmutableMultisetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */