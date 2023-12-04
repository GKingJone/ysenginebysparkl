/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import java.lang.reflect.Type;
/*    */ import java.sql.Time;
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class SqlTimeSerializer extends StdScalarSerializer<Time>
/*    */ {
/*    */   public SqlTimeSerializer()
/*    */   {
/* 17 */     super(Time.class);
/*    */   }
/*    */   
/*    */   public void serialize(Time value, JsonGenerator g, SerializerProvider provider) throws java.io.IOException
/*    */   {
/* 22 */     g.writeString(value.toString());
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 27 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException
/*    */   {
/* 34 */     visitStringFormat(visitor, typeHint, com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\SqlTimeSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */