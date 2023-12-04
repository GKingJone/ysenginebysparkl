/*    */ package com.facebook.presto.jdbc.internal.jetty.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Writer;
/*    */ import java.nio.charset.Charset;
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
/*    */ public class WriterOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   protected final Writer _writer;
/*    */   protected final Charset _encoding;
/* 38 */   private final byte[] _buf = new byte[1];
/*    */   
/*    */ 
/*    */   public WriterOutputStream(Writer writer, String encoding)
/*    */   {
/* 43 */     this._writer = writer;
/* 44 */     this._encoding = (encoding == null ? null : Charset.forName(encoding));
/*    */   }
/*    */   
/*    */ 
/*    */   public WriterOutputStream(Writer writer)
/*    */   {
/* 50 */     this._writer = writer;
/* 51 */     this._encoding = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 59 */     this._writer.close();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 67 */     this._writer.flush();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void write(byte[] b)
/*    */     throws IOException
/*    */   {
/* 75 */     if (this._encoding == null) {
/* 76 */       this._writer.write(new String(b));
/*    */     } else {
/* 78 */       this._writer.write(new String(b, this._encoding));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void write(byte[] b, int off, int len)
/*    */     throws IOException
/*    */   {
/* 86 */     if (this._encoding == null) {
/* 87 */       this._writer.write(new String(b, off, len));
/*    */     } else {
/* 89 */       this._writer.write(new String(b, off, len, this._encoding));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public synchronized void write(int b)
/*    */     throws IOException
/*    */   {
/* 97 */     this._buf[0] = ((byte)b);
/* 98 */     write(this._buf);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\WriterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */