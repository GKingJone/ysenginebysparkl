/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class StdKeySerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/*    */   public StdKeySerializer()
/*    */   {
/* 22 */     super(Object.class);
/*    */   }
/*    */   
/*    */   public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException
/*    */   {
/* 27 */     Class<?> cls = value.getClass();
/*    */     String str;
/* 29 */     String str; if (cls == String.class) {
/* 30 */       str = (String)value; } else { String str;
/* 31 */       if (cls.isEnum())
/*    */       {
/*    */ 
/* 34 */         Enum<?> en = (Enum)value;
/*    */         String str;
/* 36 */         if (provider.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
/* 37 */           str = en.toString();
/*    */         } else
/* 39 */           str = en.name();
/*    */       } else {
/* 41 */         if ((value instanceof Date)) {
/* 42 */           provider.defaultSerializeDateKey((Date)value, g); return; }
/*    */         String str;
/* 44 */         if (cls == Class.class) {
/* 45 */           str = ((Class)value).getName();
/*    */         } else
/* 47 */           str = value.toString();
/*    */       } }
/* 49 */     g.writeFieldName(str);
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
/*    */   {
/* 54 */     return createSchemaNode("string");
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
/*    */   {
/* 59 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\StdKeySerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */