/*     */ package com.facebook.presto.jdbc.internal.guava.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ByteSink
/*     */ {
/*     */   public CharSink asCharSink(Charset charset)
/*     */   {
/*  59 */     return new AsCharSink(charset, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract OutputStream openStream()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OutputStream openBufferedStream()
/*     */     throws IOException
/*     */   {
/*  85 */     OutputStream out = openStream();
/*  86 */     return (out instanceof BufferedOutputStream) ? (BufferedOutputStream)out : new BufferedOutputStream(out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  97 */     Preconditions.checkNotNull(bytes);
/*     */     
/*  99 */     Closer closer = Closer.create();
/*     */     try {
/* 101 */       OutputStream out = (OutputStream)closer.register(openStream());
/* 102 */       out.write(bytes);
/* 103 */       out.flush();
/*     */     } catch (Throwable e) {
/* 105 */       throw closer.rethrow(e);
/*     */     } finally {
/* 107 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long writeFrom(InputStream input)
/*     */     throws IOException
/*     */   {
/* 119 */     Preconditions.checkNotNull(input);
/*     */     
/* 121 */     Closer closer = Closer.create();
/*     */     try {
/* 123 */       OutputStream out = (OutputStream)closer.register(openStream());
/* 124 */       long written = ByteStreams.copy(input, out);
/* 125 */       out.flush();
/* 126 */       return written;
/*     */     } catch (Throwable e) {
/* 128 */       throw closer.rethrow(e);
/*     */     } finally {
/* 130 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private final class AsCharSink
/*     */     extends CharSink
/*     */   {
/*     */     private final Charset charset;
/*     */     
/*     */ 
/*     */     private AsCharSink(Charset charset)
/*     */     {
/* 143 */       this.charset = ((Charset)Preconditions.checkNotNull(charset));
/*     */     }
/*     */     
/*     */     public Writer openStream() throws IOException
/*     */     {
/* 148 */       return new OutputStreamWriter(ByteSink.this.openStream(), this.charset);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 153 */       String str1 = String.valueOf(String.valueOf(ByteSink.this.toString()));String str2 = String.valueOf(String.valueOf(this.charset));return 13 + str1.length() + str2.length() + str1 + ".asCharSink(" + str2 + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\ByteSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */