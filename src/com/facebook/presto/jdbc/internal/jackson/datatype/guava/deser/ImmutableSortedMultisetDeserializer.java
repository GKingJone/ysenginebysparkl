/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSortedMultiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ 
/*    */ public class ImmutableSortedMultisetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableSortedMultiset<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ImmutableSortedMultisetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 14 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.Builder<Object> createBuilder()
/*    */   {
/* 21 */     com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.Builder<Object> builder = ImmutableSortedMultiset.naturalOrder();
/* 22 */     return builder;
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaCollectionDeserializer<ImmutableSortedMultiset<Object>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 28 */     return new ImmutableSortedMultisetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableSortedMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */