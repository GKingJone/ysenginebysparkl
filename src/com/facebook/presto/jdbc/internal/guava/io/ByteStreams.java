/*     */ package com.facebook.presto.jdbc.internal.guava.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public final class ByteStreams
/*     */ {
/*     */   private static final int BUF_SIZE = 4096;
/*     */   
/*     */   public static long copy(InputStream from, OutputStream to)
/*     */     throws IOException
/*     */   {
/*  65 */     Preconditions.checkNotNull(from);
/*  66 */     Preconditions.checkNotNull(to);
/*  67 */     byte[] buf = new byte['က'];
/*  68 */     long total = 0L;
/*     */     for (;;) {
/*  70 */       int r = from.read(buf);
/*  71 */       if (r == -1) {
/*     */         break;
/*     */       }
/*  74 */       to.write(buf, 0, r);
/*  75 */       total += r;
/*     */     }
/*  77 */     return total;
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
/*     */   public static long copy(ReadableByteChannel from, WritableByteChannel to)
/*     */     throws IOException
/*     */   {
/*  91 */     Preconditions.checkNotNull(from);
/*  92 */     Preconditions.checkNotNull(to);
/*  93 */     ByteBuffer buf = ByteBuffer.allocate(4096);
/*  94 */     long total = 0L;
/*  95 */     while (from.read(buf) != -1) {
/*  96 */       buf.flip();
/*  97 */       while (buf.hasRemaining()) {
/*  98 */         total += to.write(buf);
/*     */       }
/* 100 */       buf.clear();
/*     */     }
/* 102 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] toByteArray(InputStream in)
/*     */     throws IOException
/*     */   {
/* 114 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 115 */     copy(in, out);
/* 116 */     return out.toByteArray();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] toByteArray(InputStream in, int expectedSize)
/*     */     throws IOException
/*     */   {
/* 127 */     byte[] bytes = new byte[expectedSize];
/* 128 */     int remaining = expectedSize;
/*     */     
/* 130 */     while (remaining > 0) {
/* 131 */       int off = expectedSize - remaining;
/* 132 */       int read = in.read(bytes, off, remaining);
/* 133 */       if (read == -1)
/*     */       {
/*     */ 
/* 136 */         return Arrays.copyOf(bytes, off);
/*     */       }
/* 138 */       remaining -= read;
/*     */     }
/*     */     
/*     */ 
/* 142 */     int b = in.read();
/* 143 */     if (b == -1) {
/* 144 */       return bytes;
/*     */     }
/*     */     
/*     */ 
/* 148 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream(null);
/* 149 */     out.write(b);
/* 150 */     copy(in, out);
/*     */     
/* 152 */     byte[] result = new byte[bytes.length + out.size()];
/* 153 */     System.arraycopy(bytes, 0, result, 0, bytes.length);
/* 154 */     out.writeTo(result, bytes.length);
/* 155 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final class FastByteArrayOutputStream
/*     */     extends ByteArrayOutputStream
/*     */   {
/*     */     void writeTo(byte[] b, int off)
/*     */     {
/* 168 */       System.arraycopy(this.buf, 0, b, off, this.count);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteArrayDataInput newDataInput(byte[] bytes)
/*     */   {
/* 177 */     return newDataInput(new ByteArrayInputStream(bytes));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteArrayDataInput newDataInput(byte[] bytes, int start)
/*     */   {
/* 188 */     Preconditions.checkPositionIndex(start, bytes.length);
/* 189 */     return newDataInput(new ByteArrayInputStream(bytes, start, bytes.length - start));
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
/*     */   public static ByteArrayDataInput newDataInput(ByteArrayInputStream byteArrayInputStream)
/*     */   {
/* 202 */     return new ByteArrayDataInputStream((ByteArrayInputStream)Preconditions.checkNotNull(byteArrayInputStream));
/*     */   }
/*     */   
/*     */   private static class ByteArrayDataInputStream implements ByteArrayDataInput {
/*     */     final DataInput input;
/*     */     
/*     */     ByteArrayDataInputStream(ByteArrayInputStream byteArrayInputStream) {
/* 209 */       this.input = new DataInputStream(byteArrayInputStream);
/*     */     }
/*     */     
/*     */     public void readFully(byte[] b) {
/*     */       try {
/* 214 */         this.input.readFully(b);
/*     */       } catch (IOException e) {
/* 216 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public void readFully(byte[] b, int off, int len) {
/*     */       try {
/* 222 */         this.input.readFully(b, off, len);
/*     */       } catch (IOException e) {
/* 224 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public int skipBytes(int n) {
/*     */       try {
/* 230 */         return this.input.skipBytes(n);
/*     */       } catch (IOException e) {
/* 232 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean readBoolean() {
/*     */       try {
/* 238 */         return this.input.readBoolean();
/*     */       } catch (IOException e) {
/* 240 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public byte readByte() {
/*     */       try {
/* 246 */         return this.input.readByte();
/*     */       } catch (EOFException e) {
/* 248 */         throw new IllegalStateException(e);
/*     */       } catch (IOException impossible) {
/* 250 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public int readUnsignedByte() {
/*     */       try {
/* 256 */         return this.input.readUnsignedByte();
/*     */       } catch (IOException e) {
/* 258 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public short readShort() {
/*     */       try {
/* 264 */         return this.input.readShort();
/*     */       } catch (IOException e) {
/* 266 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public int readUnsignedShort() {
/*     */       try {
/* 272 */         return this.input.readUnsignedShort();
/*     */       } catch (IOException e) {
/* 274 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public char readChar() {
/*     */       try {
/* 280 */         return this.input.readChar();
/*     */       } catch (IOException e) {
/* 282 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public int readInt() {
/*     */       try {
/* 288 */         return this.input.readInt();
/*     */       } catch (IOException e) {
/* 290 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public long readLong() {
/*     */       try {
/* 296 */         return this.input.readLong();
/*     */       } catch (IOException e) {
/* 298 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public float readFloat() {
/*     */       try {
/* 304 */         return this.input.readFloat();
/*     */       } catch (IOException e) {
/* 306 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public double readDouble() {
/*     */       try {
/* 312 */         return this.input.readDouble();
/*     */       } catch (IOException e) {
/* 314 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public String readLine() {
/*     */       try {
/* 320 */         return this.input.readLine();
/*     */       } catch (IOException e) {
/* 322 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public String readUTF() {
/*     */       try {
/* 328 */         return this.input.readUTF();
/*     */       } catch (IOException e) {
/* 330 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static ByteArrayDataOutput newDataOutput()
/*     */   {
/* 339 */     return newDataOutput(new ByteArrayOutputStream());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteArrayDataOutput newDataOutput(int size)
/*     */   {
/* 349 */     Preconditions.checkArgument(size >= 0, "Invalid size: %s", new Object[] { Integer.valueOf(size) });
/* 350 */     return newDataOutput(new ByteArrayOutputStream(size));
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
/*     */   public static ByteArrayDataOutput newDataOutput(ByteArrayOutputStream byteArrayOutputSteam)
/*     */   {
/* 369 */     return new ByteArrayDataOutputStream((ByteArrayOutputStream)Preconditions.checkNotNull(byteArrayOutputSteam));
/*     */   }
/*     */   
/*     */   private static class ByteArrayDataOutputStream
/*     */     implements ByteArrayDataOutput
/*     */   {
/*     */     final DataOutput output;
/*     */     final ByteArrayOutputStream byteArrayOutputSteam;
/*     */     
/*     */     ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputSteam)
/*     */     {
/* 380 */       this.byteArrayOutputSteam = byteArrayOutputSteam;
/* 381 */       this.output = new DataOutputStream(byteArrayOutputSteam);
/*     */     }
/*     */     
/*     */     public void write(int b) {
/*     */       try {
/* 386 */         this.output.write(b);
/*     */       } catch (IOException impossible) {
/* 388 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void write(byte[] b) {
/*     */       try {
/* 394 */         this.output.write(b);
/*     */       } catch (IOException impossible) {
/* 396 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void write(byte[] b, int off, int len) {
/*     */       try {
/* 402 */         this.output.write(b, off, len);
/*     */       } catch (IOException impossible) {
/* 404 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeBoolean(boolean v) {
/*     */       try {
/* 410 */         this.output.writeBoolean(v);
/*     */       } catch (IOException impossible) {
/* 412 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeByte(int v) {
/*     */       try {
/* 418 */         this.output.writeByte(v);
/*     */       } catch (IOException impossible) {
/* 420 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeBytes(String s) {
/*     */       try {
/* 426 */         this.output.writeBytes(s);
/*     */       } catch (IOException impossible) {
/* 428 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeChar(int v) {
/*     */       try {
/* 434 */         this.output.writeChar(v);
/*     */       } catch (IOException impossible) {
/* 436 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeChars(String s) {
/*     */       try {
/* 442 */         this.output.writeChars(s);
/*     */       } catch (IOException impossible) {
/* 444 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeDouble(double v) {
/*     */       try {
/* 450 */         this.output.writeDouble(v);
/*     */       } catch (IOException impossible) {
/* 452 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeFloat(float v) {
/*     */       try {
/* 458 */         this.output.writeFloat(v);
/*     */       } catch (IOException impossible) {
/* 460 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeInt(int v) {
/*     */       try {
/* 466 */         this.output.writeInt(v);
/*     */       } catch (IOException impossible) {
/* 468 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeLong(long v) {
/*     */       try {
/* 474 */         this.output.writeLong(v);
/*     */       } catch (IOException impossible) {
/* 476 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeShort(int v) {
/*     */       try {
/* 482 */         this.output.writeShort(v);
/*     */       } catch (IOException impossible) {
/* 484 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public void writeUTF(String s) {
/*     */       try {
/* 490 */         this.output.writeUTF(s);
/*     */       } catch (IOException impossible) {
/* 492 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */     
/*     */     public byte[] toByteArray() {
/* 497 */       return this.byteArrayOutputSteam.toByteArray();
/*     */     }
/*     */   }
/*     */   
/* 501 */   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream()
/*     */   {
/*     */     public void write(int b) {}
/*     */     
/*     */ 
/*     */     public void write(byte[] b)
/*     */     {
/* 508 */       Preconditions.checkNotNull(b);
/*     */     }
/*     */     
/*     */     public void write(byte[] b, int off, int len) {
/* 512 */       Preconditions.checkNotNull(b);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 517 */       return "ByteStreams.nullOutputStream()";
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static OutputStream nullOutputStream()
/*     */   {
/* 527 */     return NULL_OUTPUT_STREAM;
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
/*     */   public static InputStream limit(InputStream in, long limit)
/*     */   {
/* 540 */     return new LimitedInputStream(in, limit);
/*     */   }
/*     */   
/*     */   private static final class LimitedInputStream extends FilterInputStream
/*     */   {
/*     */     private long left;
/* 546 */     private long mark = -1L;
/*     */     
/*     */     LimitedInputStream(InputStream in, long limit) {
/* 549 */       super();
/* 550 */       Preconditions.checkNotNull(in);
/* 551 */       Preconditions.checkArgument(limit >= 0L, "limit must be non-negative");
/* 552 */       this.left = limit;
/*     */     }
/*     */     
/*     */     public int available() throws IOException {
/* 556 */       return (int)Math.min(this.in.available(), this.left);
/*     */     }
/*     */     
/*     */     public synchronized void mark(int readLimit)
/*     */     {
/* 561 */       this.in.mark(readLimit);
/* 562 */       this.mark = this.left;
/*     */     }
/*     */     
/*     */     public int read() throws IOException {
/* 566 */       if (this.left == 0L) {
/* 567 */         return -1;
/*     */       }
/*     */       
/* 570 */       int result = this.in.read();
/* 571 */       if (result != -1) {
/* 572 */         this.left -= 1L;
/*     */       }
/* 574 */       return result;
/*     */     }
/*     */     
/*     */     public int read(byte[] b, int off, int len) throws IOException {
/* 578 */       if (this.left == 0L) {
/* 579 */         return -1;
/*     */       }
/*     */       
/* 582 */       len = (int)Math.min(len, this.left);
/* 583 */       int result = this.in.read(b, off, len);
/* 584 */       if (result != -1) {
/* 585 */         this.left -= result;
/*     */       }
/* 587 */       return result;
/*     */     }
/*     */     
/*     */     public synchronized void reset() throws IOException {
/* 591 */       if (!this.in.markSupported()) {
/* 592 */         throw new IOException("Mark not supported");
/*     */       }
/* 594 */       if (this.mark == -1L) {
/* 595 */         throw new IOException("Mark not set");
/*     */       }
/*     */       
/* 598 */       this.in.reset();
/* 599 */       this.left = this.mark;
/*     */     }
/*     */     
/*     */     public long skip(long n) throws IOException {
/* 603 */       n = Math.min(n, this.left);
/* 604 */       long skipped = this.in.skip(n);
/* 605 */       this.left -= skipped;
/* 606 */       return skipped;
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
/*     */   public static void readFully(InputStream in, byte[] b)
/*     */     throws IOException
/*     */   {
/* 622 */     readFully(in, b, 0, b.length);
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
/*     */   public static void readFully(InputStream in, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 641 */     int read = read(in, b, off, len);
/* 642 */     if (read != len) {
/* 643 */       int i = read;int j = len;throw new EOFException(81 + "reached end of stream after reading " + i + " bytes; " + j + " bytes expected");
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
/*     */ 
/*     */   public static void skipFully(InputStream in, long n)
/*     */     throws IOException
/*     */   {
/* 661 */     long toSkip = n;
/* 662 */     while (n > 0L) {
/* 663 */       long amt = in.skip(n);
/* 664 */       if (amt == 0L)
/*     */       {
/* 666 */         if (in.read() == -1) {
/* 667 */           long skipped = toSkip - n;
/* 668 */           long l1 = skipped;long l2 = toSkip;throw new EOFException(100 + "reached end of stream after skipping " + l1 + " bytes; " + l2 + " bytes expected");
/*     */         }
/*     */         
/* 671 */         n -= 1L;
/*     */       } else {
/* 673 */         n -= amt;
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
/*     */ 
/*     */ 
/*     */   public static <T> T readBytes(InputStream input, ByteProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 689 */     Preconditions.checkNotNull(input);
/* 690 */     Preconditions.checkNotNull(processor);
/*     */     
/* 692 */     byte[] buf = new byte['က'];
/*     */     int read;
/*     */     do {
/* 695 */       read = input.read(buf);
/* 696 */     } while ((read != -1) && (processor.processBytes(buf, 0, read)));
/* 697 */     return (T)processor.getResult();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int read(InputStream in, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 726 */     Preconditions.checkNotNull(in);
/* 727 */     Preconditions.checkNotNull(b);
/* 728 */     if (len < 0) {
/* 729 */       throw new IndexOutOfBoundsException("len is negative");
/*     */     }
/* 731 */     int total = 0;
/* 732 */     while (total < len) {
/* 733 */       int result = in.read(b, off + total, len - total);
/* 734 */       if (result == -1) {
/*     */         break;
/*     */       }
/* 737 */       total += result;
/*     */     }
/* 739 */     return total;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\ByteStreams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */