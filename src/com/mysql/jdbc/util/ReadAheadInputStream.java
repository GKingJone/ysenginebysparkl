/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReadAheadInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   private InputStream underlyingStream;
/*     */   private byte[] buf;
/*     */   protected int endOfCurrentData;
/*     */   protected int currentPosition;
/*  47 */   protected boolean doDebug = false;
/*     */   protected Log log;
/*     */   
/*     */   private void fill(int readAtLeastTheseManyBytes) throws IOException
/*     */   {
/*  52 */     checkClosed();
/*     */     
/*  54 */     this.currentPosition = 0;
/*     */     
/*  56 */     this.endOfCurrentData = this.currentPosition;
/*     */     
/*     */ 
/*     */ 
/*  60 */     int bytesToRead = Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes);
/*     */     
/*  62 */     int bytesAvailable = this.underlyingStream.available();
/*     */     
/*  64 */     if (bytesAvailable > bytesToRead)
/*     */     {
/*     */ 
/*     */ 
/*  68 */       bytesToRead = Math.min(this.buf.length - this.currentPosition, bytesAvailable);
/*     */     }
/*     */     
/*  71 */     if (this.doDebug) {
/*  72 */       StringBuilder debugBuf = new StringBuilder();
/*  73 */       debugBuf.append("  ReadAheadInputStream.fill(");
/*  74 */       debugBuf.append(readAtLeastTheseManyBytes);
/*  75 */       debugBuf.append("), buffer_size=");
/*  76 */       debugBuf.append(this.buf.length);
/*  77 */       debugBuf.append(", current_position=");
/*  78 */       debugBuf.append(this.currentPosition);
/*  79 */       debugBuf.append(", need to read ");
/*  80 */       debugBuf.append(Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes));
/*  81 */       debugBuf.append(" bytes to fill request,");
/*     */       
/*  83 */       if (bytesAvailable > 0) {
/*  84 */         debugBuf.append(" underlying InputStream reports ");
/*  85 */         debugBuf.append(bytesAvailable);
/*     */         
/*  87 */         debugBuf.append(" total bytes available,");
/*     */       }
/*     */       
/*  90 */       debugBuf.append(" attempting to read ");
/*  91 */       debugBuf.append(bytesToRead);
/*  92 */       debugBuf.append(" bytes.");
/*     */       
/*  94 */       if (this.log != null) {
/*  95 */         this.log.logTrace(debugBuf.toString());
/*     */       } else {
/*  97 */         System.err.println(debugBuf.toString());
/*     */       }
/*     */     }
/*     */     
/* 101 */     int n = this.underlyingStream.read(this.buf, this.currentPosition, bytesToRead);
/*     */     
/* 103 */     if (n > 0) {
/* 104 */       this.endOfCurrentData = (n + this.currentPosition);
/*     */     }
/*     */   }
/*     */   
/*     */   private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len) throws IOException {
/* 109 */     checkClosed();
/*     */     
/* 111 */     int avail = this.endOfCurrentData - this.currentPosition;
/*     */     
/* 113 */     if (this.doDebug) {
/* 114 */       StringBuilder debugBuf = new StringBuilder();
/* 115 */       debugBuf.append("ReadAheadInputStream.readIfNecessary(");
/* 116 */       debugBuf.append(Arrays.toString(b));
/* 117 */       debugBuf.append(",");
/* 118 */       debugBuf.append(off);
/* 119 */       debugBuf.append(",");
/* 120 */       debugBuf.append(len);
/* 121 */       debugBuf.append(")");
/*     */       
/* 123 */       if (avail <= 0) {
/* 124 */         debugBuf.append(" not all data available in buffer, must read from stream");
/*     */         
/* 126 */         if (len >= this.buf.length) {
/* 127 */           debugBuf.append(", amount requested > buffer, returning direct read() from stream");
/*     */         }
/*     */       }
/*     */       
/* 131 */       if (this.log != null) {
/* 132 */         this.log.logTrace(debugBuf.toString());
/*     */       } else {
/* 134 */         System.err.println(debugBuf.toString());
/*     */       }
/*     */     }
/*     */     
/* 138 */     if (avail <= 0)
/*     */     {
/* 140 */       if (len >= this.buf.length) {
/* 141 */         return this.underlyingStream.read(b, off, len);
/*     */       }
/*     */       
/* 144 */       fill(len);
/*     */       
/* 146 */       avail = this.endOfCurrentData - this.currentPosition;
/*     */       
/* 148 */       if (avail <= 0) {
/* 149 */         return -1;
/*     */       }
/*     */     }
/*     */     
/* 153 */     int bytesActuallyRead = avail < len ? avail : len;
/*     */     
/* 155 */     System.arraycopy(this.buf, this.currentPosition, b, off, bytesActuallyRead);
/*     */     
/* 157 */     this.currentPosition += bytesActuallyRead;
/*     */     
/* 159 */     return bytesActuallyRead;
/*     */   }
/*     */   
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException
/*     */   {
/* 164 */     checkClosed();
/* 165 */     if ((off | len | off + len | b.length - (off + len)) < 0)
/* 166 */       throw new IndexOutOfBoundsException();
/* 167 */     if (len == 0) {
/* 168 */       return 0;
/*     */     }
/*     */     
/* 171 */     int totalBytesRead = 0;
/*     */     for (;;)
/*     */     {
/* 174 */       int bytesReadThisRound = readFromUnderlyingStreamIfNecessary(b, off + totalBytesRead, len - totalBytesRead);
/*     */       
/*     */ 
/* 177 */       if (bytesReadThisRound <= 0) {
/* 178 */         if (totalBytesRead == 0) {
/* 179 */           totalBytesRead = bytesReadThisRound;
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 185 */         totalBytesRead += bytesReadThisRound;
/*     */         
/*     */ 
/* 188 */         if (totalBytesRead < len)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 193 */           if (this.underlyingStream.available() <= 0)
/*     */             break;
/*     */         }
/*     */       }
/*     */     }
/* 198 */     return totalBytesRead;
/*     */   }
/*     */   
/*     */   public int read() throws IOException
/*     */   {
/* 203 */     checkClosed();
/*     */     
/* 205 */     if (this.currentPosition >= this.endOfCurrentData) {
/* 206 */       fill(1);
/* 207 */       if (this.currentPosition >= this.endOfCurrentData) {
/* 208 */         return -1;
/*     */       }
/*     */     }
/*     */     
/* 212 */     return this.buf[(this.currentPosition++)] & 0xFF;
/*     */   }
/*     */   
/*     */   public int available() throws IOException
/*     */   {
/* 217 */     checkClosed();
/*     */     
/* 219 */     return this.underlyingStream.available() + (this.endOfCurrentData - this.currentPosition);
/*     */   }
/*     */   
/*     */   private void checkClosed() throws IOException
/*     */   {
/* 224 */     if (this.buf == null) {
/* 225 */       throw new IOException("Stream closed");
/*     */     }
/*     */   }
/*     */   
/*     */   public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo) {
/* 230 */     this(toBuffer, 4096, debug, logTo);
/*     */   }
/*     */   
/*     */   public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo) {
/* 234 */     this.underlyingStream = toBuffer;
/* 235 */     this.buf = new byte[bufferSize];
/* 236 */     this.doDebug = debug;
/* 237 */     this.log = logTo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 247 */     if (this.underlyingStream != null) {
/*     */       try {
/* 249 */         this.underlyingStream.close();
/*     */       } finally {
/* 251 */         this.underlyingStream = null;
/* 252 */         this.buf = null;
/* 253 */         this.log = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 265 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/* 275 */     checkClosed();
/* 276 */     if (n <= 0L) {
/* 277 */       return 0L;
/*     */     }
/*     */     
/* 280 */     long bytesAvailInBuffer = this.endOfCurrentData - this.currentPosition;
/*     */     
/* 282 */     if (bytesAvailInBuffer <= 0L)
/*     */     {
/* 284 */       fill((int)n);
/* 285 */       bytesAvailInBuffer = this.endOfCurrentData - this.currentPosition;
/* 286 */       if (bytesAvailInBuffer <= 0L) {
/* 287 */         return 0L;
/*     */       }
/*     */     }
/*     */     
/* 291 */     long bytesSkipped = bytesAvailInBuffer < n ? bytesAvailInBuffer : n;
/* 292 */     this.currentPosition = ((int)(this.currentPosition + bytesSkipped));
/* 293 */     return bytesSkipped;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\ReadAheadInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */