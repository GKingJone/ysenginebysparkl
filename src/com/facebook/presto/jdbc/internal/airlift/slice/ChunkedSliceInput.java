/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Objects;
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
/*     */ public final class ChunkedSliceInput
/*     */   extends FixedLengthSliceInput
/*     */ {
/*     */   private final InternalLoader<?> loader;
/*     */   private final Slice buffer;
/*     */   private final long globalLength;
/*     */   private long globalPosition;
/*     */   private int bufferPosition;
/*     */   private int bufferLength;
/*     */   
/*     */   public ChunkedSliceInput(SliceLoader<?> loader, int bufferSize)
/*     */   {
/*  43 */     this.loader = new InternalLoader((SliceLoader)Objects.requireNonNull(loader, "loader is null"), bufferSize);
/*  44 */     this.buffer = this.loader.getBufferSlice();
/*  45 */     this.globalLength = loader.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public long length()
/*     */   {
/*  51 */     return this.globalLength;
/*     */   }
/*     */   
/*     */ 
/*     */   public long position()
/*     */   {
/*  57 */     return this.globalPosition + this.bufferPosition;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPosition(long position)
/*     */   {
/*  63 */     if ((position < 0L) || (position > this.globalLength)) {
/*  64 */       throw new IndexOutOfBoundsException("Invalid position " + position + " for slice with length " + this.globalLength);
/*     */     }
/*  66 */     if ((position >= this.globalPosition) && (position - this.globalPosition < this.bufferLength))
/*     */     {
/*  68 */       this.bufferPosition = ((int)(position - this.globalPosition));
/*  69 */       return;
/*     */     }
/*  71 */     this.globalPosition = position;
/*  72 */     this.bufferLength = 0;
/*  73 */     this.bufferPosition = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/*  79 */     return this.bufferPosition < this.bufferLength;
/*     */   }
/*     */   
/*     */ 
/*     */   public int available()
/*     */   {
/*  85 */     return this.bufferLength - this.bufferPosition;
/*     */   }
/*     */   
/*     */   public void ensureAvailable(int size)
/*     */   {
/*  90 */     if (available() >= size) {
/*  91 */       return;
/*     */     }
/*     */     
/*  94 */     Preconditions.checkArgument(size <= this.buffer.length(), "Size is larger than buffer");
/*  95 */     checkBound(position() + size, this.globalLength, "End of stream");
/*     */     
/*     */ 
/*  98 */     this.globalPosition += this.bufferPosition;
/*  99 */     this.bufferPosition = 0;
/*     */     
/*     */ 
/* 102 */     long readSize = Math.min(this.buffer.length(), this.globalLength - this.globalPosition);
/* 103 */     if (readSize > 2147483647L) {
/* 104 */       readSize = 2147483647L;
/*     */     }
/* 106 */     this.bufferLength = ((int)readSize);
/* 107 */     this.loader.load(this.globalPosition, this.bufferLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean readBoolean()
/*     */   {
/* 113 */     return readByte() != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int read()
/*     */   {
/* 119 */     if (position() >= this.globalLength) {
/* 120 */       return -1;
/*     */     }
/* 122 */     ensureAvailable(1);
/* 123 */     int result = this.buffer.getByte(this.bufferPosition) & 0xFF;
/* 124 */     this.bufferPosition += 1;
/* 125 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte readByte()
/*     */   {
/* 131 */     int value = read();
/* 132 */     if (value == -1) {
/* 133 */       throw new IndexOutOfBoundsException();
/*     */     }
/* 135 */     return (byte)value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedByte()
/*     */   {
/* 141 */     return readByte() & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public short readShort()
/*     */   {
/* 147 */     ensureAvailable(2);
/* 148 */     short v = this.buffer.getShort(this.bufferPosition);
/* 149 */     this.bufferPosition += 2;
/* 150 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedShort()
/*     */   {
/* 156 */     return readShort() & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readInt()
/*     */   {
/* 162 */     ensureAvailable(4);
/* 163 */     int v = this.buffer.getInt(this.bufferPosition);
/* 164 */     this.bufferPosition += 4;
/* 165 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public long readLong()
/*     */   {
/* 171 */     ensureAvailable(8);
/* 172 */     long v = this.buffer.getLong(this.bufferPosition);
/* 173 */     this.bufferPosition += 8;
/* 174 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public float readFloat()
/*     */   {
/* 180 */     ensureAvailable(4);
/* 181 */     float v = this.buffer.getFloat(this.bufferPosition);
/* 182 */     this.bufferPosition += 4;
/* 183 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public double readDouble()
/*     */   {
/* 189 */     ensureAvailable(8);
/* 190 */     double v = this.buffer.getDouble(this.bufferPosition);
/* 191 */     this.bufferPosition += 8;
/* 192 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice readSlice(int length)
/*     */   {
/* 198 */     if (length == 0) {
/* 199 */       return Slices.EMPTY_SLICE;
/*     */     }
/* 201 */     Slice slice = Slices.allocate(length);
/* 202 */     readBytes(slice);
/* 203 */     return slice;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(Slice destination, int destinationIndex, int length)
/*     */   {
/* 209 */     checkBound(position() + length, this.globalLength, "End of stream");
/*     */     
/* 211 */     while (length > 0) {
/* 212 */       int bytesToRead = Math.min(available(), length);
/* 213 */       this.buffer.getBytes(this.bufferPosition, destination, destinationIndex, bytesToRead);
/*     */       
/* 215 */       this.bufferPosition += bytesToRead;
/* 216 */       length -= bytesToRead;
/* 217 */       destinationIndex += bytesToRead;
/*     */       
/* 219 */       ensureAvailable(Math.min(length, this.buffer.length()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int read(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 226 */     if (length == 0) {
/* 227 */       return 0;
/*     */     }
/*     */     
/* 230 */     if (this.globalLength - position() == 0L) {
/* 231 */       return -1;
/*     */     }
/*     */     
/*     */ 
/* 235 */     length = (int)Math.min(length, this.globalLength - position());
/*     */     
/*     */ 
/* 238 */     readBytes(destination, destinationIndex, length);
/* 239 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 245 */     checkBound(position() + length, this.globalLength, "End of stream");
/*     */     
/* 247 */     while (length > 0) {
/* 248 */       int bytesToRead = Math.min(available(), length);
/* 249 */       this.buffer.getBytes(this.bufferPosition, destination, destinationIndex, bytesToRead);
/*     */       
/* 251 */       this.bufferPosition += bytesToRead;
/* 252 */       length -= bytesToRead;
/* 253 */       destinationIndex += bytesToRead;
/*     */       
/* 255 */       ensureAvailable(Math.min(length, this.buffer.length()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(OutputStream out, int length)
/*     */     throws IOException
/*     */   {
/* 263 */     checkBound(position() + length, this.globalLength, "End of stream");
/*     */     
/* 265 */     while (length > 0) {
/* 266 */       int bytesToRead = Math.min(available(), length);
/* 267 */       this.buffer.getBytes(this.bufferPosition, out, bytesToRead);
/*     */       
/* 269 */       this.bufferPosition += bytesToRead;
/* 270 */       length -= bytesToRead;
/*     */       
/* 272 */       ensureAvailable(Math.min(length, this.buffer.length()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long skip(long length)
/*     */   {
/* 280 */     if (available() >= length) {
/* 281 */       this.bufferPosition = ((int)(this.bufferPosition + length));
/* 282 */       return length;
/*     */     }
/*     */     
/*     */ 
/* 286 */     this.globalPosition += this.bufferPosition;
/* 287 */     this.bufferPosition = 0;
/* 288 */     this.bufferLength = 0;
/*     */     
/*     */ 
/* 291 */     length = Math.min(length, remaining());
/*     */     
/*     */ 
/* 294 */     this.globalPosition += length;
/*     */     
/* 296 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int skipBytes(int length)
/*     */   {
/* 302 */     return (int)skip(length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 308 */     this.globalPosition = this.globalLength;
/* 309 */     this.bufferPosition = 0;
/* 310 */     this.bufferLength = 0;
/* 311 */     this.loader.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 317 */     StringBuilder builder = new StringBuilder("SliceStreamInput{");
/* 318 */     builder.append("globalLength=").append(this.globalLength);
/* 319 */     builder.append(", globalPosition=").append(this.globalPosition);
/* 320 */     builder.append(", bufferLength=").append(this.bufferLength);
/* 321 */     builder.append(", bufferPosition=").append(this.bufferPosition);
/* 322 */     builder.append('}');
/* 323 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private static void checkBound(long index, long size, String message)
/*     */   {
/* 328 */     if (index > size) {
/* 329 */       throw new IndexOutOfBoundsException(message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class InternalLoader<T extends BufferReference>
/*     */   {
/*     */     private final SliceLoader<T> loader;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private final T bufferReference;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public InternalLoader(SliceLoader<T> loader, int bufferSize)
/*     */     {
/* 358 */       this.loader = loader;
/* 359 */       Preconditions.checkArgument(bufferSize >= 128, "Buffer size must be at least 128");
/* 360 */       this.bufferReference = loader.createBuffer(bufferSize);
/*     */     }
/*     */     
/*     */     public Slice getBufferSlice()
/*     */     {
/* 365 */       return this.bufferReference.getSlice();
/*     */     }
/*     */     
/*     */     public void load(long position, int length)
/*     */     {
/* 370 */       this.loader.load(position, this.bufferReference, length);
/*     */     }
/*     */     
/*     */     public void close()
/*     */     {
/* 375 */       this.loader.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface BufferReference
/*     */   {
/*     */     public abstract Slice getSlice();
/*     */   }
/*     */   
/*     */   public static abstract interface SliceLoader<B extends BufferReference>
/*     */     extends Closeable
/*     */   {
/*     */     public abstract B createBuffer(int paramInt);
/*     */     
/*     */     public abstract long getSize();
/*     */     
/*     */     public abstract void load(long paramLong, B paramB, int paramInt);
/*     */     
/*     */     public abstract void close();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\ChunkedSliceInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */