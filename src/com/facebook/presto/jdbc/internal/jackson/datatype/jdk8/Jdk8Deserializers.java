/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.Deserializers.Base;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*    */ import java.util.Optional;
/*    */ import java.util.OptionalDouble;
/*    */ import java.util.OptionalInt;
/*    */ import java.util.OptionalLong;
/*    */ 
/*    */ 
/*    */ class Jdk8Deserializers
/*    */   extends Deserializers.Base
/*    */ {
/*    */   public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
/*    */   {
/* 20 */     if (refType.hasRawClass(Optional.class)) {
/* 21 */       return new OptionalDeserializer(refType, contentTypeDeserializer, contentDeserializer);
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 27 */     if (refType.hasRawClass(OptionalInt.class)) {
/* 28 */       return OptionalIntDeserializer.INSTANCE;
/*    */     }
/* 30 */     if (refType.hasRawClass(OptionalLong.class)) {
/* 31 */       return OptionalLongDeserializer.INSTANCE;
/*    */     }
/* 33 */     if (refType.hasRawClass(OptionalDouble.class)) {
/* 34 */       return OptionalDoubleDeserializer.INSTANCE;
/*    */     }
/* 36 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8Deserializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */