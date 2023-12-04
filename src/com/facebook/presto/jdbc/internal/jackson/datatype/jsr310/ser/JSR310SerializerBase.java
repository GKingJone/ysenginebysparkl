/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class JSR310SerializerBase<T>
/*    */   extends StdSerializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected JSR310SerializerBase(Class<?> supportedType)
/*    */   {
/* 21 */     super(supportedType, false);
/*    */   }
/*    */   
/*    */ 
/*    */   public void serializeWithType(T value, JsonGenerator generator, SerializerProvider provider, TypeSerializer serializer)
/*    */     throws IOException
/*    */   {
/* 28 */     serializer.writeTypePrefixForScalar(value, generator);
/* 29 */     serialize(value, generator, provider);
/* 30 */     serializer.writeTypeSuffixForScalar(value, generator);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\JSR310SerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */