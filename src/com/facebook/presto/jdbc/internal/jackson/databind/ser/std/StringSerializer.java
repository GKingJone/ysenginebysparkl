/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public final class StringSerializer
/*    */   extends NonTypedScalarSerializerBase<Object>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public StringSerializer()
/*    */   {
/* 29 */     super(String.class, false);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @Deprecated
/*    */   public boolean isEmpty(Object value)
/*    */   {
/* 37 */     String str = (String)value;
/* 38 */     return (str == null) || (str.length() == 0);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider prov, Object value)
/*    */   {
/* 43 */     String str = (String)value;
/* 44 */     return (str == null) || (str.length() == 0);
/*    */   }
/*    */   
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException
/*    */   {
/* 49 */     gen.writeString((String)value);
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 54 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
/*    */   {
/* 59 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\StringSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */