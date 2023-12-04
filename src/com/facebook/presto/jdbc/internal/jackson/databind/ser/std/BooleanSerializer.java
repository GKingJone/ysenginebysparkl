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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public final class BooleanSerializer
/*    */   extends NonTypedScalarSerializerBase<Boolean>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final boolean _forPrimitive;
/*    */   
/*    */   public BooleanSerializer(boolean forPrimitive)
/*    */   {
/* 35 */     super(Boolean.class);
/* 36 */     this._forPrimitive = forPrimitive;
/*    */   }
/*    */   
/*    */   public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException
/*    */   {
/* 41 */     jgen.writeBoolean(value.booleanValue());
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 46 */     return createSchemaNode("boolean", !this._forPrimitive);
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 52 */     if (visitor != null) {
/* 53 */       visitor.expectBooleanFormat(typeHint);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\BooleanSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */