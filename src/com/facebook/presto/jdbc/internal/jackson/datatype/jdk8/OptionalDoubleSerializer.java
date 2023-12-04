/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jdk8;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ import java.io.IOException;
/*    */ import java.util.OptionalDouble;
/*    */ 
/*    */ 
/*    */ public class OptionalDoubleSerializer
/*    */   extends StdSerializer<OptionalDouble>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   static final OptionalDoubleSerializer INSTANCE = new OptionalDoubleSerializer();
/*    */   
/*    */   public OptionalDoubleSerializer() {
/* 22 */     super(OptionalDouble.class);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider provider, OptionalDouble value)
/*    */   {
/* 27 */     return (value == null) || (!value.isPresent());
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 33 */     JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
/* 34 */     if (v2 != null) {
/* 35 */       v2.numberType(JsonParser.NumberType.DOUBLE);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(OptionalDouble value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 43 */     if (value.isPresent()) {
/* 44 */       gen.writeNumber(value.getAsDouble());
/*    */     } else {
/* 46 */       gen.writeNull();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jdk8\OptionalDoubleSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */