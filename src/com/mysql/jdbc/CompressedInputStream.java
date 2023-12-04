/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import com.mysql.jdbc.log.NullLogger;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
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
/*     */ class CompressedInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private byte[] buffer;
/*     */   private InputStream in;
/*     */   private Inflater inflater;
/*     */   private ConnectionPropertiesImpl.BooleanConnectionProperty traceProtocol;
/*     */   private Log log;
/*  58 */   private byte[] packetHeaderBuffer = new byte[7];
/*     */   
/*     */ 
/*  61 */   private int pos = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CompressedInputStream(Connection conn, InputStream streamFromServer)
/*     */   {
/*  71 */     this.traceProtocol = ((ConnectionPropertiesImpl)conn).traceProtocol;
/*     */     try {
/*  73 */       this.log = conn.getLog();
/*     */     } catch (SQLException e) {
/*  75 */       this.log = new NullLogger(null);
/*     */     }
/*     */     
/*  78 */     this.in = streamFromServer;
/*  79 */     this.inflater = new Inflater();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/*  87 */     if (this.buffer == null) {
/*  88 */       return this.in.available();
/*     */     }
/*     */     
/*  91 */     return this.buffer.length - this.pos + this.in.available();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  99 */     this.in.close();
/* 100 */     this.buffer = null;
/* 101 */     this.inflater.end();
/* 102 */     this.inflater = null;
/* 103 */     this.traceProtocol = null;
/* 104 */     this.log = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getNextPacketFromServer()
/*     */     throws IOException
/*     */   {
/* 115 */     byte[] uncompressedData = null;
/*     */     
/* 117 */     int lengthRead = readFully(this.packetHeaderBuffer, 0, 7);
/*     */     
/* 119 */     if (lengthRead < 7) {
/* 120 */       throw new IOException("Unexpected end of input stream");
/*     */     }
/*     */     
/* 123 */     int compressedPacketLength = (this.packetHeaderBuffer[0] & 0xFF) + ((this.packetHeaderBuffer[1] & 0xFF) << 8) + ((this.packetHeaderBuffer[2] & 0xFF) << 16);
/*     */     
/*     */ 
/* 126 */     int uncompressedLength = (this.packetHeaderBuffer[4] & 0xFF) + ((this.packetHeaderBuffer[5] & 0xFF) << 8) + ((this.packetHeaderBuffer[6] & 0xFF) << 16);
/*     */     
/*     */ 
/* 129 */     boolean doTrace = this.traceProtocol.getValueAsBoolean();
/*     */     
/* 131 */     if (doTrace) {
/* 132 */       this.log.logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
/*     */     }
/*     */     
/* 135 */     if (uncompressedLength > 0) {
/* 136 */       uncompressedData = new byte[uncompressedLength];
/*     */       
/* 138 */       byte[] compressedBuffer = new byte[compressedPacketLength];
/*     */       
/* 140 */       readFully(compressedBuffer, 0, compressedPacketLength);
/*     */       
/* 142 */       this.inflater.reset();
/*     */       
/* 144 */       this.inflater.setInput(compressedBuffer);
/*     */       try
/*     */       {
/* 147 */         this.inflater.inflate(uncompressedData);
/*     */       } catch (DataFormatException dfe) {
/* 149 */         throw new IOException("Error while uncompressing packet from server.");
/*     */       }
/*     */     }
/*     */     else {
/* 153 */       if (doTrace) {
/* 154 */         this.log.logTrace("Packet didn't meet compression threshold, not uncompressing...");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 160 */       uncompressedData = new byte[compressedPacketLength];
/* 161 */       readFully(uncompressedData, 0, compressedPacketLength);
/*     */     }
/*     */     
/* 164 */     if (doTrace) {
/* 165 */       this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, compressedPacketLength));
/*     */     }
/*     */     
/* 168 */     if ((this.buffer != null) && (this.pos < this.buffer.length)) {
/* 169 */       if (doTrace) {
/* 170 */         this.log.logTrace("Combining remaining packet with new: ");
/*     */       }
/*     */       
/* 173 */       int remaining = this.buffer.length - this.pos;
/* 174 */       byte[] newBuffer = new byte[remaining + uncompressedData.length];
/*     */       
/* 176 */       int newIndex = 0;
/*     */       
/* 178 */       for (int i = this.pos; i < this.buffer.length; i++) {
/* 179 */         newBuffer[(newIndex++)] = this.buffer[i];
/*     */       }
/*     */       
/* 182 */       System.arraycopy(uncompressedData, 0, newBuffer, newIndex, uncompressedData.length);
/*     */       
/* 184 */       uncompressedData = newBuffer;
/*     */     }
/*     */     
/* 187 */     this.pos = 0;
/* 188 */     this.buffer = uncompressedData;
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
/*     */   private void getNextPacketIfRequired(int numBytes)
/*     */     throws IOException
/*     */   {
/* 204 */     if ((this.buffer == null) || (this.pos + numBytes > this.buffer.length)) {
/* 205 */       getNextPacketFromServer();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 215 */       getNextPacketIfRequired(1);
/*     */     } catch (IOException ioEx) {
/* 217 */       return -1;
/*     */     }
/*     */     
/* 220 */     return this.buffer[(this.pos++)] & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/* 228 */     return read(b, 0, b.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 236 */     if (b == null)
/* 237 */       throw new NullPointerException();
/* 238 */     if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0)) {
/* 239 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 242 */     if (len <= 0) {
/* 243 */       return 0;
/*     */     }
/*     */     try
/*     */     {
/* 247 */       getNextPacketIfRequired(len);
/*     */     } catch (IOException ioEx) {
/* 249 */       return -1;
/*     */     }
/*     */     
/* 252 */     System.arraycopy(this.buffer, this.pos, b, off, len);
/* 253 */     this.pos += len;
/*     */     
/* 255 */     return len;
/*     */   }
/*     */   
/*     */   private final int readFully(byte[] b, int off, int len) throws IOException {
/* 259 */     if (len < 0) {
/* 260 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 263 */     int n = 0;
/*     */     
/* 265 */     while (n < len) {
/* 266 */       int count = this.in.read(b, off + n, len - n);
/*     */       
/* 268 */       if (count < 0) {
/* 269 */         throw new EOFException();
/*     */       }
/*     */       
/* 272 */       n += count;
/*     */     }
/*     */     
/* 275 */     return n;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/* 283 */     long count = 0L;
/*     */     
/* 285 */     for (long i = 0L; i < n; i += 1L) {
/* 286 */       int bytesRead = read();
/*     */       
/* 288 */       if (bytesRead == -1) {
/*     */         break;
/*     */       }
/*     */       
/* 292 */       count += 1L;
/*     */     }
/*     */     
/* 295 */     return count;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CompressedInputStream.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */