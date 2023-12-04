/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileSerializer
/*    */   extends StdScalarSerializer<File>
/*    */ {
/*    */   public FileSerializer()
/*    */   {
/* 22 */     super(File.class);
/*    */   }
/*    */   
/*    */   public void serialize(File value, JsonGenerator g, SerializerProvider provider) throws IOException {
/* 26 */     g.writeString(value.getAbsolutePath());
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 31 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 38 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\FileSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */