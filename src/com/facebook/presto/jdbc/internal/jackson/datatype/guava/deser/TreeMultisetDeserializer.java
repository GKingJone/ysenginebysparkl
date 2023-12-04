/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.TreeMultiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ 
/*    */ public class TreeMultisetDeserializer extends GuavaMultisetDeserializer<TreeMultiset<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TreeMultisetDeserializer(com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 13 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected TreeMultiset<Object> createMultiset()
/*    */   {
/* 20 */     TreeMultiset<?> naturalOrder = TreeMultiset.create();
/* 21 */     return naturalOrder;
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaCollectionDeserializer<TreeMultiset<Object>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 27 */     return new TreeMultisetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\TreeMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */