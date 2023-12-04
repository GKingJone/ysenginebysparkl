/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.util.OptionalLong;
/*    */ 
/*    */ 
/*    */ public class OptionalLongDeserializer
/*    */   extends StdDeserializer<OptionalLong>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 14 */   static final OptionalLongDeserializer INSTANCE = new OptionalLongDeserializer();
/*    */   
/*    */   public OptionalLongDeserializer() {
/* 17 */     super(OptionalLong.class);
/*    */   }
/*    */   
/*    */   public OptionalLong getNullValue(DeserializationContext ctxt)
/*    */   {
/* 22 */     return OptionalLong.empty();
/*    */   }
/*    */   
/*    */   public OptionalLong deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
/*    */   {
/* 27 */     return OptionalLong.of(jp.getLongValue());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalLongDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */