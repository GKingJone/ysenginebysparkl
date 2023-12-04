/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class ReadLineInputStream
/*     */   extends BufferedInputStream
/*     */ {
/*     */   boolean _seenCRLF;
/*     */   boolean _skipLF;
/*     */   
/*     */   public ReadLineInputStream(InputStream in)
/*     */   {
/*  38 */     super(in);
/*     */   }
/*     */   
/*     */   public ReadLineInputStream(InputStream in, int size)
/*     */   {
/*  43 */     super(in, size);
/*     */   }
/*     */   
/*     */   public String readLine() throws IOException
/*     */   {
/*  48 */     mark(this.buf.length);
/*     */     
/*     */     for (;;)
/*     */     {
/*  52 */       int b = super.read();
/*     */       
/*  54 */       if (this.markpos < 0) {
/*  55 */         throw new IOException("Buffer size exceeded: no line terminator");
/*     */       }
/*  57 */       if (b == -1)
/*     */       {
/*  59 */         int m = this.markpos;
/*  60 */         this.markpos = -1;
/*  61 */         if (this.pos > m) {
/*  62 */           return new String(this.buf, m, this.pos - m, StandardCharsets.UTF_8);
/*     */         }
/*  64 */         return null;
/*     */       }
/*     */       
/*  67 */       if (b == 13)
/*     */       {
/*  69 */         int p = this.pos;
/*     */         
/*     */ 
/*  72 */         if ((this._seenCRLF) && (this.pos < this.count))
/*     */         {
/*  74 */           if (this.buf[this.pos] == 10) {
/*  75 */             this.pos += 1;
/*     */           }
/*     */         } else
/*  78 */           this._skipLF = true;
/*  79 */         int m = this.markpos;
/*  80 */         this.markpos = -1;
/*  81 */         return new String(this.buf, m, p - m - 1, StandardCharsets.UTF_8);
/*     */       }
/*     */       
/*  84 */       if (b == 10)
/*     */       {
/*  86 */         if (this._skipLF)
/*     */         {
/*  88 */           this._skipLF = false;
/*  89 */           this._seenCRLF = true;
/*  90 */           this.markpos += 1;
/*     */         }
/*     */         else {
/*  93 */           int m = this.markpos;
/*  94 */           this.markpos = -1;
/*  95 */           return new String(this.buf, m, this.pos - m - 1, StandardCharsets.UTF_8);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized int read() throws IOException
/*     */   {
/* 103 */     int b = super.read();
/* 104 */     if (this._skipLF)
/*     */     {
/* 106 */       this._skipLF = false;
/* 107 */       if ((this._seenCRLF) && (b == 10))
/* 108 */         b = super.read();
/*     */     }
/* 110 */     return b;
/*     */   }
/*     */   
/*     */   public synchronized int read(byte[] buf, int off, int len)
/*     */     throws IOException
/*     */   {
/* 116 */     if ((this._skipLF) && (len > 0))
/*     */     {
/* 118 */       this._skipLF = false;
/* 119 */       if (this._seenCRLF)
/*     */       {
/* 121 */         int b = super.read();
/* 122 */         if (b == -1) {
/* 123 */           return -1;
/*     */         }
/* 125 */         if (b != 10)
/*     */         {
/* 127 */           buf[off] = ((byte)(0xFF & b));
/* 128 */           return 1 + super.read(buf, off + 1, len - 1);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 133 */     return super.read(buf, off, len);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ReadLineInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */