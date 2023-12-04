/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferDeserializer extends StdScalarDeserializer<ByteBuffer>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected ByteBufferDeserializer()
/*    */   {
/* 14 */     super(ByteBuffer.class);
/*    */   }
/*    */   
/*    */   public ByteBuffer deserialize(JsonParser parser, DeserializationContext cx) throws IOException {
/* 18 */     byte[] b = parser.getBinaryValue();
/* 19 */     return ByteBuffer.wrap(b);
/*    */   }
/*    */   
/*    */   public ByteBuffer deserialize(JsonParser jp, DeserializationContext ctxt, ByteBuffer intoValue)
/*    */     throws IOException
/*    */   {
/* 25 */     java.io.OutputStream out = new com.facebook.presto.jdbc.internal.jackson.databind.util.ByteBufferBackedOutputStream(intoValue);
/* 26 */     jp.readBinaryValue(ctxt.getBase64Variant(), out);
/* 27 */     out.close();
/* 28 */     return intoValue;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\ByteBufferDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */