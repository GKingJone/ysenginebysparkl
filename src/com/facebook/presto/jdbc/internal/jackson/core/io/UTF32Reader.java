/*     */ package com.facebook.presto.jdbc.internal.jackson.core.io;
/*     */ 
/*     */ import java.io.CharConversionException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ public class UTF32Reader
/*     */   extends Reader
/*     */ {
/*     */   protected static final int LAST_VALID_UNICODE_CHAR = 1114111;
/*     */   protected static final char NC = '\000';
/*     */   protected final IOContext _context;
/*     */   protected InputStream _in;
/*     */   protected byte[] _buffer;
/*     */   protected int _ptr;
/*     */   protected int _length;
/*     */   protected final boolean _bigEndian;
/*  37 */   protected char _surrogate = '\000';
/*     */   
/*     */ 
/*     */ 
/*     */   protected int _charCount;
/*     */   
/*     */ 
/*     */ 
/*     */   protected int _byteCount;
/*     */   
/*     */ 
/*     */ 
/*     */   protected final boolean _managedBuffers;
/*     */   
/*     */ 
/*     */   protected char[] _tmpBuf;
/*     */   
/*     */ 
/*     */ 
/*     */   public UTF32Reader(IOContext ctxt, InputStream in, byte[] buf, int ptr, int len, boolean isBigEndian)
/*     */   {
/*  58 */     this._context = ctxt;
/*  59 */     this._in = in;
/*  60 */     this._buffer = buf;
/*  61 */     this._ptr = ptr;
/*  62 */     this._length = len;
/*  63 */     this._bigEndian = isBigEndian;
/*  64 */     this._managedBuffers = (in != null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  75 */     InputStream in = this._in;
/*     */     
/*  77 */     if (in != null) {
/*  78 */       this._in = null;
/*  79 */       freeBuffers();
/*  80 */       in.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  93 */     if (this._tmpBuf == null) {
/*  94 */       this._tmpBuf = new char[1];
/*     */     }
/*  96 */     if (read(this._tmpBuf, 0, 1) < 1) {
/*  97 */       return -1;
/*     */     }
/*  99 */     return this._tmpBuf[0];
/*     */   }
/*     */   
/*     */   public int read(char[] cbuf, int start, int len)
/*     */     throws IOException
/*     */   {
/* 105 */     if (this._buffer == null) return -1;
/* 106 */     if (len < 1) { return len;
/*     */     }
/* 108 */     if ((start < 0) || (start + len > cbuf.length)) {
/* 109 */       reportBounds(cbuf, start, len);
/*     */     }
/*     */     
/* 112 */     len += start;
/* 113 */     int outPtr = start;
/*     */     
/*     */ 
/* 116 */     if (this._surrogate != 0) {
/* 117 */       cbuf[(outPtr++)] = this._surrogate;
/* 118 */       this._surrogate = '\000';
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 124 */       int left = this._length - this._ptr;
/* 125 */       if ((left < 4) && 
/* 126 */         (!loadMore(left))) {
/* 127 */         return -1;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 133 */     while (outPtr < len) {
/* 134 */       int ptr = this._ptr;
/*     */       int ch;
/*     */       int ch;
/* 137 */       if (this._bigEndian) {
/* 138 */         ch = this._buffer[ptr] << 24 | (this._buffer[(ptr + 1)] & 0xFF) << 16 | (this._buffer[(ptr + 2)] & 0xFF) << 8 | this._buffer[(ptr + 3)] & 0xFF;
/*     */       }
/*     */       else {
/* 141 */         ch = this._buffer[ptr] & 0xFF | (this._buffer[(ptr + 1)] & 0xFF) << 8 | (this._buffer[(ptr + 2)] & 0xFF) << 16 | this._buffer[(ptr + 3)] << 24;
/*     */       }
/*     */       
/* 144 */       this._ptr += 4;
/*     */       
/*     */ 
/*     */ 
/* 148 */       if (ch > 65535) {
/* 149 */         if (ch > 1114111) {
/* 150 */           reportInvalid(ch, outPtr - start, "(above " + Integer.toHexString(1114111) + ") ");
/*     */         }
/*     */         
/* 153 */         ch -= 65536;
/* 154 */         cbuf[(outPtr++)] = ((char)(55296 + (ch >> 10)));
/*     */         
/* 156 */         ch = 0xDC00 | ch & 0x3FF;
/*     */         
/* 158 */         if (outPtr >= len) {
/* 159 */           this._surrogate = ((char)ch);
/* 160 */           break;
/*     */         }
/*     */       }
/* 163 */       cbuf[(outPtr++)] = ((char)ch);
/* 164 */       if (this._ptr >= this._length) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/* 169 */     len = outPtr - start;
/* 170 */     this._charCount += len;
/* 171 */     return len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportUnexpectedEOF(int gotBytes, int needed)
/*     */     throws IOException
/*     */   {
/* 181 */     int bytePos = this._byteCount + gotBytes;int charPos = this._charCount;
/*     */     
/* 183 */     throw new CharConversionException("Unexpected EOF in the middle of a 4-byte UTF-32 char: got " + gotBytes + ", needed " + needed + ", at char #" + charPos + ", byte #" + bytePos + ")");
/*     */   }
/*     */   
/*     */   private void reportInvalid(int value, int offset, String msg) throws IOException {
/* 187 */     int bytePos = this._byteCount + this._ptr - 1;int charPos = this._charCount + offset;
/*     */     
/* 189 */     throw new CharConversionException("Invalid UTF-32 character 0x" + Integer.toHexString(value) + msg + " at char #" + charPos + ", byte #" + bytePos + ")");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean loadMore(int available)
/*     */     throws IOException
/*     */   {
/* 199 */     this._byteCount += this._length - available;
/*     */     
/*     */ 
/* 202 */     if (available > 0) {
/* 203 */       if (this._ptr > 0) {
/* 204 */         System.arraycopy(this._buffer, this._ptr, this._buffer, 0, available);
/* 205 */         this._ptr = 0;
/*     */       }
/* 207 */       this._length = available;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 212 */       this._ptr = 0;
/* 213 */       int count = this._in == null ? -1 : this._in.read(this._buffer);
/* 214 */       if (count < 1) {
/* 215 */         this._length = 0;
/* 216 */         if (count < 0) {
/* 217 */           if (this._managedBuffers) {
/* 218 */             freeBuffers();
/*     */           }
/* 220 */           return false;
/*     */         }
/*     */         
/* 223 */         reportStrangeStream();
/*     */       }
/* 225 */       this._length = count;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 231 */     while (this._length < 4) {
/* 232 */       int count = this._in == null ? -1 : this._in.read(this._buffer, this._length, this._buffer.length - this._length);
/* 233 */       if (count < 1) {
/* 234 */         if (count < 0) {
/* 235 */           if (this._managedBuffers) {
/* 236 */             freeBuffers();
/*     */           }
/* 238 */           reportUnexpectedEOF(this._length, 4);
/*     */         }
/*     */         
/* 241 */         reportStrangeStream();
/*     */       }
/* 243 */       this._length += count;
/*     */     }
/* 245 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void freeBuffers()
/*     */   {
/* 254 */     byte[] buf = this._buffer;
/* 255 */     if (buf != null) {
/* 256 */       this._buffer = null;
/* 257 */       this._context.releaseReadIOBuffer(buf);
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportBounds(char[] cbuf, int start, int len) throws IOException {
/* 262 */     throw new ArrayIndexOutOfBoundsException("read(buf," + start + "," + len + "), cbuf[" + cbuf.length + "]");
/*     */   }
/*     */   
/*     */   private void reportStrangeStream() throws IOException {
/* 266 */     throw new IOException("Strange I/O stream, returned 0 bytes on read");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\io\UTF32Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */