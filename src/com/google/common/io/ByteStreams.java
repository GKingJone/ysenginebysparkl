/*      */ package com.google.common.io;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.collect.Iterables;
/*      */ import com.google.common.hash.HashCode;
/*      */ import com.google.common.hash.HashFunction;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.DataInput;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutput;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FilterInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.nio.channels.WritableByteChannel;
/*      */ import java.util.Arrays;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Beta
/*      */ public final class ByteStreams
/*      */ {
/*      */   private static final int BUF_SIZE = 4096;
/*      */   
/*      */   @Deprecated
/*      */   public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] b)
/*      */   {
/*   70 */     return asInputSupplier(ByteSource.wrap(b));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] b, int off, int len)
/*      */   {
/*   87 */     return asInputSupplier(ByteSource.wrap(b).slice(off, len));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static void write(byte[] from, OutputSupplier<? extends OutputStream> to)
/*      */     throws IOException
/*      */   {
/*  102 */     asByteSink(to).write(from);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static long copy(InputSupplier<? extends InputStream> from, OutputSupplier<? extends OutputStream> to)
/*      */     throws IOException
/*      */   {
/*  119 */     return asByteSource(from).copyTo(asByteSink(to));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static long copy(InputSupplier<? extends InputStream> from, OutputStream to)
/*      */     throws IOException
/*      */   {
/*  137 */     return asByteSource(from).copyTo(to);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static long copy(InputStream from, OutputSupplier<? extends OutputStream> to)
/*      */     throws IOException
/*      */   {
/*  156 */     return asByteSink(to).writeFrom(from);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static long copy(InputStream from, OutputStream to)
/*      */     throws IOException
/*      */   {
/*  170 */     Preconditions.checkNotNull(from);
/*  171 */     Preconditions.checkNotNull(to);
/*  172 */     byte[] buf = new byte['က'];
/*  173 */     long total = 0L;
/*      */     for (;;) {
/*  175 */       int r = from.read(buf);
/*  176 */       if (r == -1) {
/*      */         break;
/*      */       }
/*  179 */       to.write(buf, 0, r);
/*  180 */       total += r;
/*      */     }
/*  182 */     return total;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static long copy(ReadableByteChannel from, WritableByteChannel to)
/*      */     throws IOException
/*      */   {
/*  196 */     Preconditions.checkNotNull(from);
/*  197 */     Preconditions.checkNotNull(to);
/*  198 */     ByteBuffer buf = ByteBuffer.allocate(4096);
/*  199 */     long total = 0L;
/*  200 */     while (from.read(buf) != -1) {
/*  201 */       buf.flip();
/*  202 */       while (buf.hasRemaining()) {
/*  203 */         total += to.write(buf);
/*      */       }
/*  205 */       buf.clear();
/*      */     }
/*  207 */     return total;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] toByteArray(InputStream in)
/*      */     throws IOException
/*      */   {
/*  219 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  220 */     copy(in, out);
/*  221 */     return out.toByteArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static byte[] toByteArray(InputStream in, int expectedSize)
/*      */     throws IOException
/*      */   {
/*  232 */     byte[] bytes = new byte[expectedSize];
/*  233 */     int remaining = expectedSize;
/*      */     
/*  235 */     while (remaining > 0) {
/*  236 */       int off = expectedSize - remaining;
/*  237 */       int read = in.read(bytes, off, remaining);
/*  238 */       if (read == -1)
/*      */       {
/*      */ 
/*  241 */         return Arrays.copyOf(bytes, off);
/*      */       }
/*  243 */       remaining -= read;
/*      */     }
/*      */     
/*      */ 
/*  247 */     int b = in.read();
/*  248 */     if (b == -1) {
/*  249 */       return bytes;
/*      */     }
/*      */     
/*      */ 
/*  253 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream(null);
/*  254 */     out.write(b);
/*  255 */     copy(in, out);
/*      */     
/*  257 */     byte[] result = new byte[bytes.length + out.size()];
/*  258 */     System.arraycopy(bytes, 0, result, 0, bytes.length);
/*  259 */     out.writeTo(result, bytes.length);
/*  260 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final class FastByteArrayOutputStream
/*      */     extends ByteArrayOutputStream
/*      */   {
/*      */     void writeTo(byte[] b, int off)
/*      */     {
/*  273 */       System.arraycopy(this.buf, 0, b, off, this.count);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static byte[] toByteArray(InputSupplier<? extends InputStream> supplier)
/*      */     throws IOException
/*      */   {
/*  288 */     return asByteSource(supplier).read();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteArrayDataInput newDataInput(byte[] bytes)
/*      */   {
/*  296 */     return new ByteArrayDataInputStream(bytes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteArrayDataInput newDataInput(byte[] bytes, int start)
/*      */   {
/*  307 */     Preconditions.checkPositionIndex(start, bytes.length);
/*  308 */     return new ByteArrayDataInputStream(bytes, start);
/*      */   }
/*      */   
/*      */   private static class ByteArrayDataInputStream implements ByteArrayDataInput {
/*      */     final DataInput input;
/*      */     
/*      */     ByteArrayDataInputStream(byte[] bytes) {
/*  315 */       this.input = new DataInputStream(new ByteArrayInputStream(bytes));
/*      */     }
/*      */     
/*      */     ByteArrayDataInputStream(byte[] bytes, int start) {
/*  319 */       this.input = new DataInputStream(new ByteArrayInputStream(bytes, start, bytes.length - start));
/*      */     }
/*      */     
/*      */     public void readFully(byte[] b)
/*      */     {
/*      */       try {
/*  325 */         this.input.readFully(b);
/*      */       } catch (IOException e) {
/*  327 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public void readFully(byte[] b, int off, int len) {
/*      */       try {
/*  333 */         this.input.readFully(b, off, len);
/*      */       } catch (IOException e) {
/*  335 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public int skipBytes(int n) {
/*      */       try {
/*  341 */         return this.input.skipBytes(n);
/*      */       } catch (IOException e) {
/*  343 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public boolean readBoolean() {
/*      */       try {
/*  349 */         return this.input.readBoolean();
/*      */       } catch (IOException e) {
/*  351 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public byte readByte() {
/*      */       try {
/*  357 */         return this.input.readByte();
/*      */       } catch (EOFException e) {
/*  359 */         throw new IllegalStateException(e);
/*      */       } catch (IOException impossible) {
/*  361 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public int readUnsignedByte() {
/*      */       try {
/*  367 */         return this.input.readUnsignedByte();
/*      */       } catch (IOException e) {
/*  369 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public short readShort() {
/*      */       try {
/*  375 */         return this.input.readShort();
/*      */       } catch (IOException e) {
/*  377 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public int readUnsignedShort() {
/*      */       try {
/*  383 */         return this.input.readUnsignedShort();
/*      */       } catch (IOException e) {
/*  385 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public char readChar() {
/*      */       try {
/*  391 */         return this.input.readChar();
/*      */       } catch (IOException e) {
/*  393 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public int readInt() {
/*      */       try {
/*  399 */         return this.input.readInt();
/*      */       } catch (IOException e) {
/*  401 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public long readLong() {
/*      */       try {
/*  407 */         return this.input.readLong();
/*      */       } catch (IOException e) {
/*  409 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public float readFloat() {
/*      */       try {
/*  415 */         return this.input.readFloat();
/*      */       } catch (IOException e) {
/*  417 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public double readDouble() {
/*      */       try {
/*  423 */         return this.input.readDouble();
/*      */       } catch (IOException e) {
/*  425 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public String readLine() {
/*      */       try {
/*  431 */         return this.input.readLine();
/*      */       } catch (IOException e) {
/*  433 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     public String readUTF() {
/*      */       try {
/*  439 */         return this.input.readUTF();
/*      */       } catch (IOException e) {
/*  441 */         throw new IllegalStateException(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static ByteArrayDataOutput newDataOutput()
/*      */   {
/*  450 */     return new ByteArrayDataOutputStream();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteArrayDataOutput newDataOutput(int size)
/*      */   {
/*  460 */     Preconditions.checkArgument(size >= 0, "Invalid size: %s", new Object[] { Integer.valueOf(size) });
/*  461 */     return new ByteArrayDataOutputStream(size);
/*      */   }
/*      */   
/*      */   private static class ByteArrayDataOutputStream
/*      */     implements ByteArrayDataOutput
/*      */   {
/*      */     final DataOutput output;
/*      */     final ByteArrayOutputStream byteArrayOutputSteam;
/*      */     
/*      */     ByteArrayDataOutputStream()
/*      */     {
/*  472 */       this(new ByteArrayOutputStream());
/*      */     }
/*      */     
/*      */     ByteArrayDataOutputStream(int size) {
/*  476 */       this(new ByteArrayOutputStream(size));
/*      */     }
/*      */     
/*      */     ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputSteam) {
/*  480 */       this.byteArrayOutputSteam = byteArrayOutputSteam;
/*  481 */       this.output = new DataOutputStream(byteArrayOutputSteam);
/*      */     }
/*      */     
/*      */     public void write(int b) {
/*      */       try {
/*  486 */         this.output.write(b);
/*      */       } catch (IOException impossible) {
/*  488 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void write(byte[] b) {
/*      */       try {
/*  494 */         this.output.write(b);
/*      */       } catch (IOException impossible) {
/*  496 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void write(byte[] b, int off, int len) {
/*      */       try {
/*  502 */         this.output.write(b, off, len);
/*      */       } catch (IOException impossible) {
/*  504 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeBoolean(boolean v) {
/*      */       try {
/*  510 */         this.output.writeBoolean(v);
/*      */       } catch (IOException impossible) {
/*  512 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeByte(int v) {
/*      */       try {
/*  518 */         this.output.writeByte(v);
/*      */       } catch (IOException impossible) {
/*  520 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeBytes(String s) {
/*      */       try {
/*  526 */         this.output.writeBytes(s);
/*      */       } catch (IOException impossible) {
/*  528 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeChar(int v) {
/*      */       try {
/*  534 */         this.output.writeChar(v);
/*      */       } catch (IOException impossible) {
/*  536 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeChars(String s) {
/*      */       try {
/*  542 */         this.output.writeChars(s);
/*      */       } catch (IOException impossible) {
/*  544 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeDouble(double v) {
/*      */       try {
/*  550 */         this.output.writeDouble(v);
/*      */       } catch (IOException impossible) {
/*  552 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeFloat(float v) {
/*      */       try {
/*  558 */         this.output.writeFloat(v);
/*      */       } catch (IOException impossible) {
/*  560 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeInt(int v) {
/*      */       try {
/*  566 */         this.output.writeInt(v);
/*      */       } catch (IOException impossible) {
/*  568 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeLong(long v) {
/*      */       try {
/*  574 */         this.output.writeLong(v);
/*      */       } catch (IOException impossible) {
/*  576 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeShort(int v) {
/*      */       try {
/*  582 */         this.output.writeShort(v);
/*      */       } catch (IOException impossible) {
/*  584 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public void writeUTF(String s) {
/*      */       try {
/*  590 */         this.output.writeUTF(s);
/*      */       } catch (IOException impossible) {
/*  592 */         throw new AssertionError(impossible);
/*      */       }
/*      */     }
/*      */     
/*      */     public byte[] toByteArray() {
/*  597 */       return this.byteArrayOutputSteam.toByteArray();
/*      */     }
/*      */   }
/*      */   
/*  601 */   private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream()
/*      */   {
/*      */     public void write(int b) {}
/*      */     
/*      */ 
/*      */     public void write(byte[] b)
/*      */     {
/*  608 */       Preconditions.checkNotNull(b);
/*      */     }
/*      */     
/*      */     public void write(byte[] b, int off, int len) {
/*  612 */       Preconditions.checkNotNull(b);
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*  617 */       return "ByteStreams.nullOutputStream()";
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static OutputStream nullOutputStream()
/*      */   {
/*  627 */     return NULL_OUTPUT_STREAM;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static InputStream limit(InputStream in, long limit)
/*      */   {
/*  640 */     return new LimitedInputStream(in, limit);
/*      */   }
/*      */   
/*      */   private static final class LimitedInputStream extends FilterInputStream
/*      */   {
/*      */     private long left;
/*  646 */     private long mark = -1L;
/*      */     
/*      */     LimitedInputStream(InputStream in, long limit) {
/*  649 */       super();
/*  650 */       Preconditions.checkNotNull(in);
/*  651 */       Preconditions.checkArgument(limit >= 0L, "limit must be non-negative");
/*  652 */       this.left = limit;
/*      */     }
/*      */     
/*      */     public int available() throws IOException {
/*  656 */       return (int)Math.min(this.in.available(), this.left);
/*      */     }
/*      */     
/*      */     public synchronized void mark(int readLimit)
/*      */     {
/*  661 */       this.in.mark(readLimit);
/*  662 */       this.mark = this.left;
/*      */     }
/*      */     
/*      */     public int read() throws IOException {
/*  666 */       if (this.left == 0L) {
/*  667 */         return -1;
/*      */       }
/*      */       
/*  670 */       int result = this.in.read();
/*  671 */       if (result != -1) {
/*  672 */         this.left -= 1L;
/*      */       }
/*  674 */       return result;
/*      */     }
/*      */     
/*      */     public int read(byte[] b, int off, int len) throws IOException {
/*  678 */       if (this.left == 0L) {
/*  679 */         return -1;
/*      */       }
/*      */       
/*  682 */       len = (int)Math.min(len, this.left);
/*  683 */       int result = this.in.read(b, off, len);
/*  684 */       if (result != -1) {
/*  685 */         this.left -= result;
/*      */       }
/*  687 */       return result;
/*      */     }
/*      */     
/*      */     public synchronized void reset() throws IOException {
/*  691 */       if (!this.in.markSupported()) {
/*  692 */         throw new IOException("Mark not supported");
/*      */       }
/*  694 */       if (this.mark == -1L) {
/*  695 */         throw new IOException("Mark not set");
/*      */       }
/*      */       
/*  698 */       this.in.reset();
/*  699 */       this.left = this.mark;
/*      */     }
/*      */     
/*      */     public long skip(long n) throws IOException {
/*  703 */       n = Math.min(n, this.left);
/*  704 */       long skipped = this.in.skip(n);
/*  705 */       this.left -= skipped;
/*  706 */       return skipped;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static long length(InputSupplier<? extends InputStream> supplier)
/*      */     throws IOException
/*      */   {
/*  719 */     return asByteSource(supplier).size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static boolean equal(InputSupplier<? extends InputStream> supplier1, InputSupplier<? extends InputStream> supplier2)
/*      */     throws IOException
/*      */   {
/*  732 */     return asByteSource(supplier1).contentEquals(asByteSource(supplier2));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void readFully(InputStream in, byte[] b)
/*      */     throws IOException
/*      */   {
/*  747 */     readFully(in, b, 0, b.length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void readFully(InputStream in, byte[] b, int off, int len)
/*      */     throws IOException
/*      */   {
/*  766 */     int read = read(in, b, off, len);
/*  767 */     if (read != len) {
/*  768 */       throw new EOFException("reached end of stream after reading " + read + " bytes; " + len + " bytes expected");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void skipFully(InputStream in, long n)
/*      */     throws IOException
/*      */   {
/*  786 */     long toSkip = n;
/*  787 */     while (n > 0L) {
/*  788 */       long amt = in.skip(n);
/*  789 */       if (amt == 0L)
/*      */       {
/*  791 */         if (in.read() == -1) {
/*  792 */           long skipped = toSkip - n;
/*  793 */           throw new EOFException("reached end of stream after skipping " + skipped + " bytes; " + toSkip + " bytes expected");
/*      */         }
/*      */         
/*  796 */         n -= 1L;
/*      */       } else {
/*  798 */         n -= amt;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> T readBytes(InputSupplier<? extends InputStream> supplier, ByteProcessor<T> processor)
/*      */     throws IOException
/*      */   {
/*  817 */     Preconditions.checkNotNull(supplier);
/*  818 */     Preconditions.checkNotNull(processor);
/*      */     
/*  820 */     Closer closer = Closer.create();
/*      */     try {
/*  822 */       InputStream in = (InputStream)closer.register((Closeable)supplier.getInput());
/*  823 */       return (T)readBytes(in, processor);
/*      */     } catch (Throwable e) {
/*  825 */       throw closer.rethrow(e);
/*      */     } finally {
/*  827 */       closer.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> T readBytes(InputStream input, ByteProcessor<T> processor)
/*      */     throws IOException
/*      */   {
/*  842 */     Preconditions.checkNotNull(input);
/*  843 */     Preconditions.checkNotNull(processor);
/*      */     
/*  845 */     byte[] buf = new byte['က'];
/*      */     int read;
/*      */     do {
/*  848 */       read = input.read(buf);
/*  849 */     } while ((read != -1) && (processor.processBytes(buf, 0, read)));
/*  850 */     return (T)processor.getResult();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static HashCode hash(InputSupplier<? extends InputStream> supplier, HashFunction hashFunction)
/*      */     throws IOException
/*      */   {
/*  869 */     return asByteSource(supplier).hash(hashFunction);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int read(InputStream in, byte[] b, int off, int len)
/*      */     throws IOException
/*      */   {
/*  898 */     Preconditions.checkNotNull(in);
/*  899 */     Preconditions.checkNotNull(b);
/*  900 */     if (len < 0) {
/*  901 */       throw new IndexOutOfBoundsException("len is negative");
/*      */     }
/*  903 */     int total = 0;
/*  904 */     while (total < len) {
/*  905 */       int result = in.read(b, off + total, len - total);
/*  906 */       if (result == -1) {
/*      */         break;
/*      */       }
/*  909 */       total += result;
/*      */     }
/*  911 */     return total;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static InputSupplier<InputStream> slice(InputSupplier<? extends InputStream> supplier, long offset, long length)
/*      */   {
/*  932 */     return asInputSupplier(asByteSource(supplier).slice(offset, length));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static InputSupplier<InputStream> join(Iterable<? extends InputSupplier<? extends InputStream>> suppliers)
/*      */   {
/*  955 */     Preconditions.checkNotNull(suppliers);
/*  956 */     Iterable<ByteSource> sources = Iterables.transform(suppliers, new Function()
/*      */     {
/*      */       public ByteSource apply(InputSupplier<? extends InputStream> input)
/*      */       {
/*  960 */         return ByteStreams.asByteSource(input);
/*      */       }
/*  962 */     });
/*  963 */     return asInputSupplier(ByteSource.concat(sources));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static InputSupplier<InputStream> join(InputSupplier<? extends InputStream>... suppliers)
/*      */   {
/*  976 */     return join(Arrays.asList(suppliers));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static ByteSource asByteSource(InputSupplier<? extends InputStream> supplier)
/*      */   {
/*  997 */     Preconditions.checkNotNull(supplier);
/*  998 */     new ByteSource()
/*      */     {
/*      */       public InputStream openStream() throws IOException {
/* 1001 */         return (InputStream)this.val$supplier.getInput();
/*      */       }
/*      */       
/*      */       public String toString()
/*      */       {
/* 1006 */         return "ByteStreams.asByteSource(" + this.val$supplier + ")";
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static ByteSink asByteSink(OutputSupplier<? extends OutputStream> supplier)
/*      */   {
/* 1027 */     Preconditions.checkNotNull(supplier);
/* 1028 */     new ByteSink()
/*      */     {
/*      */       public OutputStream openStream() throws IOException {
/* 1031 */         return (OutputStream)this.val$supplier.getOutput();
/*      */       }
/*      */       
/*      */       public String toString()
/*      */       {
/* 1036 */         return "ByteStreams.asByteSink(" + this.val$supplier + ")";
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */   static <S extends InputStream> InputSupplier<S> asInputSupplier(ByteSource source)
/*      */   {
/* 1044 */     return (InputSupplier)Preconditions.checkNotNull(source);
/*      */   }
/*      */   
/*      */ 
/*      */   static <S extends OutputStream> OutputSupplier<S> asOutputSupplier(ByteSink sink)
/*      */   {
/* 1050 */     return (OutputSupplier)Preconditions.checkNotNull(sink);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\ByteStreams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */