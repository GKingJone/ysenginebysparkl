/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.Serializers.Base;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*    */ import java.util.Optional;
/*    */ import java.util.OptionalDouble;
/*    */ import java.util.OptionalInt;
/*    */ import java.util.OptionalLong;
/*    */ 
/*    */ 
/*    */ class Jdk8Serializers
/*    */   extends Serializers.Base
/*    */ {
/*    */   public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType refType, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
/*    */   {
/* 20 */     Class<?> raw = refType.getRawClass();
/* 21 */     if (Optional.class.isAssignableFrom(raw)) {
/* 22 */       return new OptionalSerializer(refType, contentTypeSerializer, contentValueSerializer);
/*    */     }
/* 24 */     if (OptionalInt.class.isAssignableFrom(raw)) {
/* 25 */       return OptionalIntSerializer.INSTANCE;
/*    */     }
/* 27 */     if (OptionalLong.class.isAssignableFrom(raw)) {
/* 28 */       return OptionalLongSerializer.INSTANCE;
/*    */     }
/* 30 */     if (OptionalDouble.class.isAssignableFrom(raw)) {
/* 31 */       return OptionalDoubleSerializer.INSTANCE;
/*    */     }
/* 33 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\Jdk8Serializers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */