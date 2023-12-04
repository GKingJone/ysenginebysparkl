/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
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
/*     */ public class ByteArrayISO8859Writer
/*     */   extends Writer
/*     */ {
/*     */   private byte[] _buf;
/*     */   private int _size;
/*  41 */   private ByteArrayOutputStream2 _bout = null;
/*  42 */   private OutputStreamWriter _writer = null;
/*  43 */   private boolean _fixed = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteArrayISO8859Writer()
/*     */   {
/*  50 */     this._buf = new byte['ࠀ'];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteArrayISO8859Writer(int capacity)
/*     */   {
/*  59 */     this._buf = new byte[capacity];
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteArrayISO8859Writer(byte[] buf)
/*     */   {
/*  65 */     this._buf = buf;
/*  66 */     this._fixed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getLock()
/*     */   {
/*  72 */     return this.lock;
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  78 */     return this._size;
/*     */   }
/*     */   
/*     */ 
/*     */   public int capacity()
/*     */   {
/*  84 */     return this._buf.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int spareCapacity()
/*     */   {
/*  90 */     return this._buf.length - this._size;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLength(int l)
/*     */   {
/*  96 */     this._size = l;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBuf()
/*     */   {
/* 102 */     return this._buf;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeTo(OutputStream out)
/*     */     throws IOException
/*     */   {
/* 109 */     out.write(this._buf, 0, this._size);
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(char c)
/*     */     throws IOException
/*     */   {
/* 116 */     ensureSpareCapacity(1);
/* 117 */     if ((c >= 0) && (c <= '')) {
/* 118 */       this._buf[(this._size++)] = ((byte)c);
/*     */     }
/*     */     else {
/* 121 */       char[] ca = { c };
/* 122 */       writeEncoded(ca, 0, 1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void write(char[] ca)
/*     */     throws IOException
/*     */   {
/* 131 */     ensureSpareCapacity(ca.length);
/* 132 */     for (int i = 0; i < ca.length; i++)
/*     */     {
/* 134 */       char c = ca[i];
/* 135 */       if ((c >= 0) && (c <= '')) {
/* 136 */         this._buf[(this._size++)] = ((byte)c);
/*     */       }
/*     */       else {
/* 139 */         writeEncoded(ca, i, ca.length - i);
/* 140 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void write(char[] ca, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 150 */     ensureSpareCapacity(length);
/* 151 */     for (int i = 0; i < length; i++)
/*     */     {
/* 153 */       char c = ca[(offset + i)];
/* 154 */       if ((c >= 0) && (c <= '')) {
/* 155 */         this._buf[(this._size++)] = ((byte)c);
/*     */       }
/*     */       else {
/* 158 */         writeEncoded(ca, offset + i, length - i);
/* 159 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void write(String s)
/*     */     throws IOException
/*     */   {
/* 169 */     if (s == null)
/*     */     {
/* 171 */       write("null", 0, 4);
/* 172 */       return;
/*     */     }
/*     */     
/* 175 */     int length = s.length();
/* 176 */     ensureSpareCapacity(length);
/* 177 */     for (int i = 0; i < length; i++)
/*     */     {
/* 179 */       char c = s.charAt(i);
/* 180 */       if ((c >= 0) && (c <= '')) {
/* 181 */         this._buf[(this._size++)] = ((byte)c);
/*     */       }
/*     */       else {
/* 184 */         writeEncoded(s.toCharArray(), i, length - i);
/* 185 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void write(String s, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 195 */     ensureSpareCapacity(length);
/* 196 */     for (int i = 0; i < length; i++)
/*     */     {
/* 198 */       char c = s.charAt(offset + i);
/* 199 */       if ((c >= 0) && (c <= '')) {
/* 200 */         this._buf[(this._size++)] = ((byte)c);
/*     */       }
/*     */       else {
/* 203 */         writeEncoded(s.toCharArray(), offset + i, length - i);
/* 204 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void writeEncoded(char[] ca, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 213 */     if (this._bout == null)
/*     */     {
/* 215 */       this._bout = new ByteArrayOutputStream2(2 * length);
/* 216 */       this._writer = new OutputStreamWriter(this._bout, StandardCharsets.ISO_8859_1);
/*     */     }
/*     */     else {
/* 219 */       this._bout.reset(); }
/* 220 */     this._writer.write(ca, offset, length);
/* 221 */     this._writer.flush();
/* 222 */     ensureSpareCapacity(this._bout.getCount());
/* 223 */     System.arraycopy(this._bout.getBuf(), 0, this._buf, this._size, this._bout.getCount());
/* 224 */     this._size += this._bout.getCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void flush() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void resetWriter()
/*     */   {
/* 235 */     this._size = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 246 */     this._buf = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void ensureSpareCapacity(int n)
/*     */     throws IOException
/*     */   {
/* 253 */     if (this._size + n > this._buf.length)
/*     */     {
/* 255 */       if (this._fixed)
/* 256 */         throw new IOException("Buffer overflow: " + this._buf.length);
/* 257 */       this._buf = Arrays.copyOf(this._buf, (this._buf.length + n) * 4 / 3);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getByteArray()
/*     */   {
/* 264 */     return Arrays.copyOf(this._buf, this._size);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ByteArrayISO8859Writer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */