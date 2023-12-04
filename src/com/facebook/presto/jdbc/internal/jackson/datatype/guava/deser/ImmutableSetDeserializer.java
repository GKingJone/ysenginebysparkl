/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.Builder;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ 
/*    */ public class ImmutableSetDeserializer
/*    */   extends GuavaImmutableCollectionDeserializer<ImmutableSet<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ImmutableSetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 16 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */   public ImmutableSetDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 22 */     return new ImmutableSetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */   
/*    */ 
/*    */   protected ImmutableCollection.Builder<Object> createBuilder()
/*    */   {
/* 28 */     return ImmutableSet.builder();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\ImmutableSetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */