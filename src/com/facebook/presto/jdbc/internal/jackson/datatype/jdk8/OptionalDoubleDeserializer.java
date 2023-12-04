/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.util.OptionalDouble;
/*    */ 
/*    */ 
/*    */ final class OptionalDoubleDeserializer
/*    */   extends StdDeserializer<OptionalDouble>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 14 */   static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();
/*    */   
/*    */   public OptionalDoubleDeserializer() {
/* 17 */     super(OptionalDouble.class);
/*    */   }
/*    */   
/*    */   public OptionalDouble getNullValue(DeserializationContext ctxt)
/*    */   {
/* 22 */     return OptionalDouble.empty();
/*    */   }
/*    */   
/*    */   public OptionalDouble deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
/*    */   {
/* 27 */     return OptionalDouble.of(jp.getValueAsDouble());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalDoubleDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */