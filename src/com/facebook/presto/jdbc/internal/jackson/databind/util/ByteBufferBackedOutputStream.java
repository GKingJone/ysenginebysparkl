/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ public class ByteBufferBackedOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   protected final ByteBuffer _b;
/*    */   
/* 13 */   public ByteBufferBackedOutputStream(ByteBuffer buf) { this._b = buf; }
/*    */   
/* 15 */   public void write(int b) throws IOException { this._b.put((byte)b); }
/* 16 */   public void write(byte[] bytes, int off, int len) throws IOException { this._b.put(bytes, off, len); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\ByteBufferBackedOutputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */