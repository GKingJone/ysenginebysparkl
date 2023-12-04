/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ByteBufferBackedInputStream
/*    */   extends InputStream
/*    */ {
/*    */   protected final ByteBuffer _b;
/*    */   
/* 14 */   public ByteBufferBackedInputStream(ByteBuffer buf) { this._b = buf; }
/*    */   
/* 16 */   public int available() { return this._b.remaining(); }
/*    */   
/*    */   public int read() throws IOException {
/* 19 */     return this._b.hasRemaining() ? this._b.get() & 0xFF : -1;
/*    */   }
/*    */   
/*    */   public int read(byte[] bytes, int off, int len) throws IOException {
/* 23 */     if (!this._b.hasRemaining()) return -1;
/* 24 */     len = Math.min(len, this._b.remaining());
/* 25 */     this._b.get(bytes, off, len);
/* 26 */     return len;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\ByteBufferBackedInputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */