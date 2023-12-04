/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ import java.io.IOException;
/*    */ import java.util.OptionalInt;
/*    */ 
/*    */ 
/*    */ final class OptionalIntSerializer
/*    */   extends StdSerializer<OptionalInt>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   static final OptionalIntSerializer INSTANCE = new OptionalIntSerializer();
/*    */   
/*    */   public OptionalIntSerializer() {
/* 22 */     super(OptionalInt.class);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isEmpty(SerializerProvider provider, OptionalInt value)
/*    */   {
/* 28 */     return (value == null) || (!value.isPresent());
/*    */   }
/*    */   
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 35 */     JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
/* 36 */     if (v2 != null) {
/* 37 */       v2.numberType(JsonParser.NumberType.INT);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(OptionalInt value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 45 */     if (value.isPresent()) {
/* 46 */       gen.writeNumber(value.getAsInt());
/*    */     } else {
/* 48 */       gen.writeNull();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalIntSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */