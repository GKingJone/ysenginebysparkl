/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.GatheringByteChannel;
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
/*     */ public class IO
/*     */ {
/*  48 */   private static final Logger LOG = Log.getLogger(IO.class);
/*     */   
/*     */ 
/*     */ 
/*     */   public static final String CRLF = "\r\n";
/*     */   
/*     */ 
/*     */ 
/*  56 */   public static final byte[] CRLF_BYTES = { 13, 10 };
/*     */   
/*     */   public static final int bufferSize = 65536;
/*     */   
/*     */ 
/*     */   static class Job
/*     */     implements Runnable
/*     */   {
/*     */     InputStream in;
/*     */     OutputStream out;
/*     */     Reader read;
/*     */     Writer write;
/*     */     
/*     */     Job(InputStream in, OutputStream out)
/*     */     {
/*  71 */       this.in = in;
/*  72 */       this.out = out;
/*  73 */       this.read = null;
/*  74 */       this.write = null;
/*     */     }
/*     */     
/*     */     Job(Reader read, Writer write) {
/*  78 */       this.in = null;
/*  79 */       this.out = null;
/*  80 */       this.read = read;
/*  81 */       this.write = write;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*  91 */         if (this.in != null) {
/*  92 */           IO.copy(this.in, this.out, -1L);
/*     */         } else {
/*  94 */           IO.copy(this.read, this.write, -1L);
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/*  98 */         IO.LOG.ignore(e);
/*     */         try {
/* 100 */           if (this.out != null)
/* 101 */             this.out.close();
/* 102 */           if (this.write != null) {
/* 103 */             this.write.close();
/*     */           }
/*     */         }
/*     */         catch (IOException e2) {
/* 107 */           IO.LOG.ignore(e2);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void copy(InputStream in, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 122 */     copy(in, out, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void copy(Reader in, Writer out)
/*     */     throws IOException
/*     */   {
/* 134 */     copy(in, out, -1L);
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
/*     */   public static void copy(InputStream in, OutputStream out, long byteCount)
/*     */     throws IOException
/*     */   {
/* 149 */     byte[] buffer = new byte[65536];
/* 150 */     int len = 65536;
/*     */     
/* 152 */     if (byteCount >= 0L)
/*     */     {
/* 154 */       while (byteCount > 0L)
/*     */       {
/* 156 */         int max = byteCount < 65536L ? (int)byteCount : 65536;
/* 157 */         len = in.read(buffer, 0, max);
/*     */         
/* 159 */         if (len == -1) {
/*     */           break;
/*     */         }
/* 162 */         byteCount -= len;
/* 163 */         out.write(buffer, 0, len);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/* 170 */       len = in.read(buffer, 0, 65536);
/* 171 */       if (len < 0)
/*     */         break;
/* 173 */       out.write(buffer, 0, len);
/*     */     }
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
/*     */ 
/*     */   public static void copy(Reader in, Writer out, long byteCount)
/*     */     throws IOException
/*     */   {
/* 190 */     char[] buffer = new char[65536];
/* 191 */     int len = 65536;
/*     */     
/* 193 */     if (byteCount >= 0L)
/*     */     {
/* 195 */       while (byteCount > 0L)
/*     */       {
/* 197 */         if (byteCount < 65536L) {
/* 198 */           len = in.read(buffer, 0, (int)byteCount);
/*     */         } else {
/* 200 */           len = in.read(buffer, 0, 65536);
/*     */         }
/* 202 */         if (len == -1) {
/*     */           break;
/*     */         }
/* 205 */         byteCount -= len;
/* 206 */         out.write(buffer, 0, len);
/*     */       }
/*     */     }
/* 209 */     if ((out instanceof PrintWriter))
/*     */     {
/* 211 */       PrintWriter pout = (PrintWriter)out;
/* 212 */       while (!pout.checkError())
/*     */       {
/* 214 */         len = in.read(buffer, 0, 65536);
/* 215 */         if (len == -1)
/*     */           break;
/* 217 */         out.write(buffer, 0, len);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       for (;;)
/*     */       {
/* 224 */         len = in.read(buffer, 0, 65536);
/* 225 */         if (len == -1)
/*     */           break;
/* 227 */         out.write(buffer, 0, len);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void copy(File from, File to)
/*     */     throws IOException
/*     */   {
/* 240 */     if (from.isDirectory()) {
/* 241 */       copyDir(from, to);
/*     */     } else {
/* 243 */       copyFile(from, to);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void copyDir(File from, File to) throws IOException
/*     */   {
/* 249 */     if (to.exists())
/*     */     {
/* 251 */       if (!to.isDirectory()) {
/* 252 */         throw new IllegalArgumentException(to.toString());
/*     */       }
/*     */     } else {
/* 255 */       to.mkdirs();
/*     */     }
/* 257 */     File[] files = from.listFiles();
/* 258 */     if (files != null)
/*     */     {
/* 260 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 262 */         String name = files[i].getName();
/* 263 */         if ((!".".equals(name)) && (!"..".equals(name)))
/*     */         {
/* 265 */           copy(files[i], new File(to, name));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void copyFile(File from, File to) throws IOException
/*     */   {
/* 273 */     InputStream in = new FileInputStream(from);Throwable localThrowable6 = null;
/* 274 */     try { OutputStream out = new FileOutputStream(to);Throwable localThrowable7 = null;
/*     */       try {
/* 276 */         copy(in, out);
/*     */       }
/*     */       catch (Throwable localThrowable1)
/*     */       {
/* 273 */         localThrowable7 = localThrowable1;throw localThrowable1; } finally {} } catch (Throwable localThrowable4) { localThrowable6 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally
/*     */     {
/* 277 */       if (in != null) { if (localThrowable6 != null) try { in.close(); } catch (Throwable localThrowable5) { localThrowable6.addSuppressed(localThrowable5); } else { in.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(InputStream in)
/*     */     throws IOException
/*     */   {
/* 289 */     return toString(in, (Charset)null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(InputStream in, String encoding)
/*     */     throws IOException
/*     */   {
/* 302 */     return toString(in, encoding == null ? null : Charset.forName(encoding));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(InputStream in, Charset encoding)
/*     */     throws IOException
/*     */   {
/* 314 */     StringWriter writer = new StringWriter();
/* 315 */     InputStreamReader reader = encoding == null ? new InputStreamReader(in) : new InputStreamReader(in, encoding);
/*     */     
/* 317 */     copy(reader, writer);
/* 318 */     return writer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String toString(Reader in)
/*     */     throws IOException
/*     */   {
/* 330 */     StringWriter writer = new StringWriter();
/* 331 */     copy(in, writer);
/* 332 */     return writer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean delete(File file)
/*     */   {
/* 344 */     if (!file.exists())
/* 345 */       return false;
/* 346 */     if (file.isDirectory())
/*     */     {
/* 348 */       File[] files = file.listFiles();
/* 349 */       for (int i = 0; (files != null) && (i < files.length); i++)
/* 350 */         delete(files[i]);
/*     */     }
/* 352 */     return file.delete();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void close(Closeable closeable)
/*     */   {
/*     */     try
/*     */     {
/* 364 */       if (closeable != null) {
/* 365 */         closeable.close();
/*     */       }
/*     */     }
/*     */     catch (IOException ignore) {
/* 369 */       LOG.ignore(ignore);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void close(InputStream is)
/*     */   {
/* 380 */     close(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void close(OutputStream os)
/*     */   {
/* 390 */     close(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void close(Reader reader)
/*     */   {
/* 400 */     close(reader);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void close(Writer writer)
/*     */   {
/* 410 */     close(writer);
/*     */   }
/*     */   
/*     */ 
/*     */   public static byte[] readBytes(InputStream in)
/*     */     throws IOException
/*     */   {
/* 417 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 418 */     copy(in, bout);
/* 419 */     return bout.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */   private static class NullOS
/*     */     extends OutputStream
/*     */   {
/*     */     public void close() {}
/*     */     
/*     */ 
/*     */     public void flush() {}
/*     */     
/*     */ 
/*     */     public void write(byte[] b) {}
/*     */     
/*     */ 
/*     */     public void write(byte[] b, int i, int l) {}
/*     */     
/*     */ 
/*     */     public void write(int b) {}
/*     */   }
/*     */   
/*     */   public static long write(GatheringByteChannel out, ByteBuffer[] buffers, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 444 */     long total = 0L;
/* 445 */     while (length > 0)
/*     */     {
/*     */ 
/* 448 */       long wrote = out.write(buffers, offset, length);
/*     */       
/*     */ 
/* 451 */       if (wrote == 0L) {
/*     */         break;
/*     */       }
/*     */       
/* 455 */       total += wrote;
/*     */       
/*     */ 
/* 458 */       for (int i = offset;; i++) { if (i >= buffers.length)
/*     */           break label74;
/* 460 */         if (buffers[i].hasRemaining())
/*     */         {
/*     */ 
/* 463 */           length -= i - offset;
/* 464 */           offset = i;
/* 465 */           break;
/*     */         } }
/*     */       label74:
/* 468 */       length = 0;
/*     */     }
/*     */     
/* 471 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static OutputStream getNullStream()
/*     */   {
/* 480 */     return __nullStream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InputStream getClosedStream()
/*     */   {
/* 489 */     return __closedStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 507 */   private static NullOS __nullStream = new NullOS(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class ClosedIS
/*     */     extends InputStream
/*     */   {
/* 517 */     public int read()
/* 517 */       throws IOException { return -1; }
/*     */   }
/*     */   
/* 520 */   private static ClosedIS __closedStream = new ClosedIS(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Writer getNullWriter()
/*     */   {
/* 528 */     return __nullWriter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PrintWriter getNullPrintWriter()
/*     */   {
/* 537 */     return __nullPrintWriter;
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
/* 559 */   private static NullWrite __nullWriter = new NullWrite(null);
/* 560 */   private static PrintWriter __nullPrintWriter = new PrintWriter(__nullWriter);
/*     */   
/*     */   private static class NullWrite
/*     */     extends Writer
/*     */   {
/*     */     public void close() {}
/*     */     
/*     */     public void flush() {}
/*     */     
/*     */     public void write(char[] b) {}
/*     */     
/*     */     public void write(char[] b, int o, int l) {}
/*     */     
/*     */     public void write(int b) {}
/*     */     
/*     */     public void write(String s) {}
/*     */     
/*     */     public void write(String s, int o, int l) {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */