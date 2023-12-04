/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.LinkedHashMultiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ 
/*    */ public class LinkedHashMultisetDeserializer extends GuavaMultisetDeserializer<LinkedHashMultiset<Object>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LinkedHashMultisetDeserializer(com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 13 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */   protected LinkedHashMultiset<Object> createMultiset()
/*    */   {
/* 18 */     return LinkedHashMultiset.create();
/*    */   }
/*    */   
/*    */ 
/*    */   public GuavaCollectionDeserializer<LinkedHashMultiset<Object>> withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
/*    */   {
/* 24 */     return new LinkedHashMultisetDeserializer(this._containerType, typeDeser, valueDeser);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\LinkedHashMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */