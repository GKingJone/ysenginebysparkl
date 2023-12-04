/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerationException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
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
/*    */ public class FailingSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/*    */   protected final String _msg;
/*    */   
/*    */   public FailingSerializer(String msg)
/*    */   {
/* 28 */     super(Object.class);
/* 29 */     this._msg = msg;
/*    */   }
/*    */   
/*    */   public void serialize(Object value, JsonGenerator g, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 35 */     throw new JsonGenerationException(this._msg, g);
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
/*    */   {
/* 40 */     return null;
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\FailingSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */