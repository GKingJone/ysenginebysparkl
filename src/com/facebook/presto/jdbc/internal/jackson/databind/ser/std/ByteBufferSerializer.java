/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class ByteBufferSerializer extends StdScalarSerializer<ByteBuffer>
/*    */ {
/*    */   public ByteBufferSerializer()
/*    */   {
/* 13 */     super(ByteBuffer.class);
/*    */   }
/*    */   
/*    */   public void serialize(ByteBuffer bbuf, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 19 */     if (bbuf.hasArray()) {
/* 20 */       gen.writeBinary(bbuf.array(), 0, bbuf.limit());
/* 21 */       return;
/*    */     }
/*    */     
/*    */ 
/* 25 */     ByteBuffer copy = bbuf.asReadOnlyBuffer();
/* 26 */     if (copy.position() > 0) {
/* 27 */       copy.rewind();
/*    */     }
/* 29 */     InputStream in = new com.facebook.presto.jdbc.internal.jackson.databind.util.ByteBufferBackedInputStream(copy);
/* 30 */     gen.writeBinary(in, copy.remaining());
/* 31 */     in.close();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\ByteBufferSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */