/*    */ package com.facebook.presto.jdbc.internal.jackson.databind;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ public abstract interface JsonSerializable
/*    */ {
/*    */   public abstract void serialize(JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws IOException;
/*    */   
/*    */   public abstract void serializeWithType(JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, TypeSerializer paramTypeSerializer)
/*    */     throws IOException;
/*    */   
/*    */   public static abstract class Base
/*    */     implements JsonSerializable
/*    */   {
/*    */     public boolean isEmpty(SerializerProvider serializers)
/*    */     {
/* 63 */       return false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\JsonSerializable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */