/*      */ package com.facebook.presto.jdbc.internal.jetty.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.lang.reflect.Field;
/*      */ import java.nio.BufferOverflowException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.MappedByteBuffer;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileChannel.MapMode;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.StandardOpenOption;
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
/*      */ 
/*      */ 
/*      */ public class BufferUtil
/*      */ {
/*      */   static final int TEMP_BUFFER_SIZE = 4096;
/*      */   static final byte SPACE = 32;
/*      */   static final byte MINUS = 45;
/*  104 */   static final byte[] DIGIT = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */   
/*      */ 
/*      */ 
/*  108 */   public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
/*      */   
/*      */ 
/*      */ 
/*      */   static final Field fdMappedByteBuffer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteBuffer allocate(int capacity)
/*      */   {
/*  119 */     ByteBuffer buf = ByteBuffer.allocate(capacity);
/*  120 */     buf.limit(0);
/*  121 */     return buf;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteBuffer allocateDirect(int capacity)
/*      */   {
/*  133 */     ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
/*  134 */     buf.limit(0);
/*  135 */     return buf;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void clear(ByteBuffer buffer)
/*      */   {
/*  146 */     if (buffer != null)
/*      */     {
/*  148 */       buffer.position(0);
/*  149 */       buffer.limit(0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void clearToFill(ByteBuffer buffer)
/*      */   {
/*  160 */     if (buffer != null)
/*      */     {
/*  162 */       buffer.position(0);
/*  163 */       buffer.limit(buffer.capacity());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int flipToFill(ByteBuffer buffer)
/*      */   {
/*  183 */     int position = buffer.position();
/*  184 */     int limit = buffer.limit();
/*  185 */     if (position == limit)
/*      */     {
/*  187 */       buffer.position(0);
/*  188 */       buffer.limit(buffer.capacity());
/*  189 */       return 0;
/*      */     }
/*      */     
/*  192 */     int capacity = buffer.capacity();
/*  193 */     if (limit == capacity)
/*      */     {
/*  195 */       buffer.compact();
/*  196 */       return 0;
/*      */     }
/*      */     
/*  199 */     buffer.position(limit);
/*  200 */     buffer.limit(capacity);
/*  201 */     return position;
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
/*      */   public static void flipToFlush(ByteBuffer buffer, int position)
/*      */   {
/*  217 */     buffer.limit(buffer.position());
/*  218 */     buffer.position(position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] toArray(ByteBuffer buffer)
/*      */   {
/*  229 */     if (buffer.hasArray())
/*      */     {
/*  231 */       byte[] array = buffer.array();
/*  232 */       int from = buffer.arrayOffset() + buffer.position();
/*  233 */       return Arrays.copyOfRange(array, from, from + buffer.remaining());
/*      */     }
/*      */     
/*      */ 
/*  237 */     byte[] to = new byte[buffer.remaining()];
/*  238 */     buffer.slice().get(to);
/*  239 */     return to;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isEmpty(ByteBuffer buf)
/*      */   {
/*  250 */     return (buf == null) || (buf.remaining() == 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean hasContent(ByteBuffer buf)
/*      */   {
/*  260 */     return (buf != null) && (buf.remaining() > 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isFull(ByteBuffer buf)
/*      */   {
/*  270 */     return (buf != null) && (buf.limit() == buf.capacity());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int length(ByteBuffer buffer)
/*      */   {
/*  280 */     return buffer == null ? 0 : buffer.remaining();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int space(ByteBuffer buffer)
/*      */   {
/*  290 */     if (buffer == null)
/*  291 */       return 0;
/*  292 */     return buffer.capacity() - buffer.limit();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean compact(ByteBuffer buffer)
/*      */   {
/*  302 */     if (buffer.position() == 0)
/*  303 */       return false;
/*  304 */     boolean full = buffer.limit() == buffer.capacity();
/*  305 */     buffer.compact().flip();
/*  306 */     return (full) && (buffer.limit() < buffer.capacity());
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
/*      */   public static int put(ByteBuffer from, ByteBuffer to)
/*      */   {
/*  319 */     int remaining = from.remaining();
/*  320 */     int put; if (remaining > 0)
/*      */     {
/*  322 */       if (remaining <= to.remaining())
/*      */       {
/*  324 */         to.put(from);
/*  325 */         int put = remaining;
/*  326 */         from.position(from.limit());
/*      */       }
/*  328 */       else if (from.hasArray())
/*      */       {
/*  330 */         int put = to.remaining();
/*  331 */         to.put(from.array(), from.arrayOffset() + from.position(), put);
/*  332 */         from.position(from.position() + put);
/*      */       }
/*      */       else
/*      */       {
/*  336 */         int put = to.remaining();
/*  337 */         ByteBuffer slice = from.slice();
/*  338 */         slice.limit(put);
/*  339 */         to.put(slice);
/*  340 */         from.position(from.position() + put);
/*      */       }
/*      */     }
/*      */     else {
/*  344 */       put = 0;
/*      */     }
/*  346 */     return put;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public static int flipPutFlip(ByteBuffer from, ByteBuffer to)
/*      */   {
/*  359 */     return append(to, from);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void append(ByteBuffer to, byte[] b, int off, int len)
/*      */     throws BufferOverflowException
/*      */   {
/*  372 */     int pos = flipToFill(to);
/*      */     try
/*      */     {
/*  375 */       to.put(b, off, len);
/*      */     }
/*      */     finally
/*      */     {
/*  379 */       flipToFlush(to, pos);
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
/*      */   public static int append(ByteBuffer to, ByteBuffer b)
/*      */   {
/*  409 */     int pos = flipToFill(to);
/*      */     try
/*      */     {
/*  412 */       return put(b, to);
/*      */     }
/*      */     finally
/*      */     {
/*  416 */       flipToFlush(to, pos);
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
/*      */   public static int fill(ByteBuffer to, byte[] b, int off, int len)
/*      */   {
/*  431 */     int pos = flipToFill(to);
/*      */     try
/*      */     {
/*  434 */       int remaining = to.remaining();
/*  435 */       int take = remaining < len ? remaining : len;
/*  436 */       to.put(b, off, take);
/*  437 */       return take;
/*      */     }
/*      */     finally
/*      */     {
/*  441 */       flipToFlush(to, pos);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void readFrom(File file, ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/*  449 */     RandomAccessFile raf = new RandomAccessFile(file, "r");Throwable localThrowable3 = null;
/*      */     try {
/*  451 */       FileChannel channel = raf.getChannel();
/*  452 */       long needed = raf.length();
/*      */       
/*  454 */       while ((needed > 0L) && (buffer.hasRemaining())) {
/*  455 */         needed -= channel.read(buffer);
/*      */       }
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/*  449 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*  456 */       if (raf != null) if (localThrowable3 != null) try { raf.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else raf.close();
/*      */     }
/*      */   }
/*      */   
/*      */   public static void readFrom(InputStream is, int needed, ByteBuffer buffer) throws IOException
/*      */   {
/*  462 */     ByteBuffer tmp = allocate(8192);
/*      */     
/*  464 */     while ((needed > 0) && (buffer.hasRemaining()))
/*      */     {
/*  466 */       int l = is.read(tmp.array(), 0, 8192);
/*  467 */       if (l < 0)
/*      */         break;
/*  469 */       tmp.position(0);
/*  470 */       tmp.limit(l);
/*  471 */       buffer.put(tmp);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void writeTo(ByteBuffer buffer, OutputStream out)
/*      */     throws IOException
/*      */   {
/*  478 */     if (buffer.hasArray())
/*      */     {
/*  480 */       out.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
/*      */       
/*  482 */       buffer.position(buffer.position() + buffer.remaining());
/*      */     }
/*      */     else
/*      */     {
/*  486 */       byte[] bytes = new byte['က'];
/*  487 */       while (buffer.hasRemaining()) {
/*  488 */         int byteCountToWrite = Math.min(buffer.remaining(), 4096);
/*  489 */         buffer.get(bytes, 0, byteCountToWrite);
/*  490 */         out.write(bytes, 0, byteCountToWrite);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toString(ByteBuffer buffer)
/*      */   {
/*  502 */     return toString(buffer, StandardCharsets.ISO_8859_1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toUTF8String(ByteBuffer buffer)
/*      */   {
/*  512 */     return toString(buffer, StandardCharsets.UTF_8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toString(ByteBuffer buffer, Charset charset)
/*      */   {
/*  523 */     if (buffer == null)
/*  524 */       return null;
/*  525 */     byte[] array = buffer.hasArray() ? buffer.array() : null;
/*  526 */     if (array == null)
/*      */     {
/*  528 */       byte[] to = new byte[buffer.remaining()];
/*  529 */       buffer.slice().get(to);
/*  530 */       return new String(to, 0, to.length, charset);
/*      */     }
/*  532 */     return new String(array, buffer.arrayOffset() + buffer.position(), buffer.remaining(), charset);
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
/*      */   public static String toString(ByteBuffer buffer, int position, int length, Charset charset)
/*      */   {
/*  546 */     if (buffer == null)
/*  547 */       return null;
/*  548 */     byte[] array = buffer.hasArray() ? buffer.array() : null;
/*  549 */     if (array == null)
/*      */     {
/*  551 */       ByteBuffer ro = buffer.asReadOnlyBuffer();
/*  552 */       ro.position(position);
/*  553 */       ro.limit(position + length);
/*  554 */       byte[] to = new byte[length];
/*  555 */       ro.get(to);
/*  556 */       return new String(to, 0, to.length, charset);
/*      */     }
/*  558 */     return new String(array, buffer.arrayOffset() + position, length, charset);
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
/*      */   public static int toInt(ByteBuffer buffer)
/*      */   {
/*  571 */     return toInt(buffer, buffer.position(), buffer.remaining());
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
/*      */   public static int toInt(ByteBuffer buffer, int position, int length)
/*      */   {
/*  589 */     int val = 0;
/*  590 */     boolean started = false;
/*  591 */     boolean minus = false;
/*      */     
/*  593 */     int limit = position + length;
/*      */     
/*  595 */     if (length <= 0) {
/*  596 */       throw new NumberFormatException(toString(buffer, position, length, StandardCharsets.UTF_8));
/*      */     }
/*  598 */     for (int i = position; i < limit; i++)
/*      */     {
/*  600 */       byte b = buffer.get(i);
/*  601 */       if (b <= 32)
/*      */       {
/*  603 */         if (started) {
/*      */           break;
/*      */         }
/*  606 */       } else if ((b >= 48) && (b <= 57))
/*      */       {
/*  608 */         val = val * 10 + (b - 48);
/*  609 */         started = true;
/*      */       } else {
/*  611 */         if ((b != 45) || (started))
/*      */           break;
/*  613 */         minus = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  619 */     if (started)
/*  620 */       return minus ? -val : val;
/*  621 */     throw new NumberFormatException(toString(buffer));
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
/*      */   public static int takeInt(ByteBuffer buffer)
/*      */   {
/*  634 */     int val = 0;
/*  635 */     boolean started = false;
/*  636 */     boolean minus = false;
/*      */     
/*  638 */     for (int i = buffer.position(); i < buffer.limit(); i++)
/*      */     {
/*  640 */       byte b = buffer.get(i);
/*  641 */       if (b <= 32)
/*      */       {
/*  643 */         if (started) {
/*      */           break;
/*      */         }
/*  646 */       } else if ((b >= 48) && (b <= 57))
/*      */       {
/*  648 */         val = val * 10 + (b - 48);
/*  649 */         started = true;
/*      */       } else {
/*  651 */         if ((b != 45) || (started))
/*      */           break;
/*  653 */         minus = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  659 */     if (started)
/*      */     {
/*  661 */       buffer.position(i);
/*  662 */       return minus ? -val : val;
/*      */     }
/*  664 */     throw new NumberFormatException(toString(buffer));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static long toLong(ByteBuffer buffer)
/*      */   {
/*  676 */     long val = 0L;
/*  677 */     boolean started = false;
/*  678 */     boolean minus = false;
/*      */     
/*  680 */     for (int i = buffer.position(); i < buffer.limit(); i++)
/*      */     {
/*  682 */       byte b = buffer.get(i);
/*  683 */       if (b <= 32)
/*      */       {
/*  685 */         if (started) {
/*      */           break;
/*      */         }
/*  688 */       } else if ((b >= 48) && (b <= 57))
/*      */       {
/*  690 */         val = val * 10L + (b - 48);
/*  691 */         started = true;
/*      */       } else {
/*  693 */         if ((b != 45) || (started))
/*      */           break;
/*  695 */         minus = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  701 */     if (started)
/*  702 */       return minus ? -val : val;
/*  703 */     throw new NumberFormatException(toString(buffer));
/*      */   }
/*      */   
/*      */   public static void putHexInt(ByteBuffer buffer, int n)
/*      */   {
/*  708 */     if (n < 0)
/*      */     {
/*  710 */       buffer.put((byte)45);
/*      */       
/*  712 */       if (n == Integer.MIN_VALUE)
/*      */       {
/*  714 */         buffer.put((byte)56);
/*  715 */         buffer.put((byte)48);
/*  716 */         buffer.put((byte)48);
/*  717 */         buffer.put((byte)48);
/*  718 */         buffer.put((byte)48);
/*  719 */         buffer.put((byte)48);
/*  720 */         buffer.put((byte)48);
/*  721 */         buffer.put((byte)48);
/*      */         
/*  723 */         return;
/*      */       }
/*  725 */       n = -n;
/*      */     }
/*      */     
/*  728 */     if (n < 16)
/*      */     {
/*  730 */       buffer.put(DIGIT[n]);
/*      */     }
/*      */     else
/*      */     {
/*  734 */       boolean started = false;
/*      */       
/*  736 */       for (int hexDivisor : hexDivisors)
/*      */       {
/*  738 */         if (n < hexDivisor)
/*      */         {
/*  740 */           if (started) {
/*  741 */             buffer.put((byte)48);
/*      */           }
/*      */         }
/*      */         else {
/*  745 */           started = true;
/*  746 */           int d = n / hexDivisor;
/*  747 */           buffer.put(DIGIT[d]);
/*  748 */           n -= d * hexDivisor;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void putDecInt(ByteBuffer buffer, int n)
/*      */   {
/*  756 */     if (n < 0)
/*      */     {
/*  758 */       buffer.put((byte)45);
/*      */       
/*  760 */       if (n == Integer.MIN_VALUE)
/*      */       {
/*  762 */         buffer.put((byte)50);
/*  763 */         n = 147483648;
/*      */       }
/*      */       else {
/*  766 */         n = -n;
/*      */       }
/*      */     }
/*  769 */     if (n < 10)
/*      */     {
/*  771 */       buffer.put(DIGIT[n]);
/*      */     }
/*      */     else
/*      */     {
/*  775 */       boolean started = false;
/*      */       
/*  777 */       for (int decDivisor : decDivisors)
/*      */       {
/*  779 */         if (n < decDivisor)
/*      */         {
/*  781 */           if (started) {
/*  782 */             buffer.put((byte)48);
/*      */           }
/*      */         }
/*      */         else {
/*  786 */           started = true;
/*  787 */           int d = n / decDivisor;
/*  788 */           buffer.put(DIGIT[d]);
/*  789 */           n -= d * decDivisor;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void putDecLong(ByteBuffer buffer, long n) {
/*  796 */     if (n < 0L)
/*      */     {
/*  798 */       buffer.put((byte)45);
/*      */       
/*  800 */       if (n == Long.MIN_VALUE)
/*      */       {
/*  802 */         buffer.put((byte)57);
/*  803 */         n = 223372036854775808L;
/*      */       }
/*      */       else {
/*  806 */         n = -n;
/*      */       }
/*      */     }
/*  809 */     if (n < 10L)
/*      */     {
/*  811 */       buffer.put(DIGIT[((int)n)]);
/*      */     }
/*      */     else
/*      */     {
/*  815 */       boolean started = false;
/*      */       
/*  817 */       for (long aDecDivisorsL : decDivisorsL)
/*      */       {
/*  819 */         if (n < aDecDivisorsL)
/*      */         {
/*  821 */           if (started) {
/*  822 */             buffer.put((byte)48);
/*      */           }
/*      */         }
/*      */         else {
/*  826 */           started = true;
/*  827 */           long d = n / aDecDivisorsL;
/*  828 */           buffer.put(DIGIT[((int)d)]);
/*  829 */           n -= d * aDecDivisorsL;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static ByteBuffer toBuffer(int value) {
/*  836 */     ByteBuffer buf = ByteBuffer.allocate(32);
/*  837 */     putDecInt(buf, value);
/*  838 */     return buf;
/*      */   }
/*      */   
/*      */   public static ByteBuffer toBuffer(long value)
/*      */   {
/*  843 */     ByteBuffer buf = ByteBuffer.allocate(32);
/*  844 */     putDecLong(buf, value);
/*  845 */     return buf;
/*      */   }
/*      */   
/*      */   public static ByteBuffer toBuffer(String s)
/*      */   {
/*  850 */     return toBuffer(s, StandardCharsets.ISO_8859_1);
/*      */   }
/*      */   
/*      */   public static ByteBuffer toBuffer(String s, Charset charset)
/*      */   {
/*  855 */     if (s == null)
/*  856 */       return EMPTY_BUFFER;
/*  857 */     return toBuffer(s.getBytes(charset));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static ByteBuffer toBuffer(byte[] array)
/*      */   {
/*  869 */     if (array == null)
/*  870 */       return EMPTY_BUFFER;
/*  871 */     return toBuffer(array, 0, array.length);
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
/*      */   public static ByteBuffer toBuffer(byte[] array, int offset, int length)
/*      */   {
/*  887 */     if (array == null)
/*  888 */       return EMPTY_BUFFER;
/*  889 */     return ByteBuffer.wrap(array, offset, length);
/*      */   }
/*      */   
/*      */   public static ByteBuffer toDirectBuffer(String s)
/*      */   {
/*  894 */     return toDirectBuffer(s, StandardCharsets.ISO_8859_1);
/*      */   }
/*      */   
/*      */   public static ByteBuffer toDirectBuffer(String s, Charset charset)
/*      */   {
/*  899 */     if (s == null)
/*  900 */       return EMPTY_BUFFER;
/*  901 */     byte[] bytes = s.getBytes(charset);
/*  902 */     ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
/*  903 */     buf.put(bytes);
/*  904 */     buf.flip();
/*  905 */     return buf;
/*      */   }
/*      */   
/*      */   public static ByteBuffer toMappedBuffer(File file) throws IOException
/*      */   {
/*  910 */     FileChannel channel = FileChannel.open(file.toPath(), new OpenOption[] { StandardOpenOption.READ });Throwable localThrowable3 = null;
/*      */     try {
/*  912 */       return channel.map(MapMode.READ_ONLY, 0L, file.length());
/*      */     }
/*      */     catch (Throwable localThrowable4)
/*      */     {
/*  910 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*      */     }
/*      */     finally {
/*  913 */       if (channel != null) if (localThrowable3 != null) try { channel.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else channel.close();
/*      */     }
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  919 */     Field fd = null;
/*      */     try
/*      */     {
/*  922 */       fd = MappedByteBuffer.class.getDeclaredField("fd");
/*  923 */       fd.setAccessible(true);
/*      */     }
/*      */     catch (Exception localException) {}
/*      */     
/*      */ 
/*  928 */     fdMappedByteBuffer = fd;
/*      */   }
/*      */   
/*      */   public static boolean isMappedBuffer(ByteBuffer buffer)
/*      */   {
/*  933 */     if (!(buffer instanceof MappedByteBuffer))
/*  934 */       return false;
/*  935 */     MappedByteBuffer mapped = (MappedByteBuffer)buffer;
/*      */     
/*  937 */     if (fdMappedByteBuffer != null)
/*      */     {
/*      */       try
/*      */       {
/*  941 */         if ((fdMappedByteBuffer.get(mapped) instanceof FileDescriptor)) {
/*  942 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Exception localException) {}
/*      */     }
/*      */     
/*  948 */     return false;
/*      */   }
/*      */   
/*      */   public static ByteBuffer toBuffer(Resource resource, boolean direct)
/*      */     throws IOException
/*      */   {
/*  954 */     int len = (int)resource.length();
/*  955 */     if (len < 0) {
/*  956 */       throw new IllegalArgumentException("invalid resource: " + String.valueOf(resource) + " len=" + len);
/*      */     }
/*  958 */     ByteBuffer buffer = direct ? allocateDirect(len) : allocate(len);
/*      */     
/*  960 */     int pos = flipToFill(buffer);
/*  961 */     if (resource.getFile() != null) {
/*  962 */       readFrom(resource.getFile(), buffer);
/*      */     }
/*      */     else {
/*  965 */       InputStream is = resource.getInputStream();Throwable localThrowable3 = null;
/*      */       try {
/*  967 */         readFrom(is, len, buffer);
/*      */       }
/*      */       catch (Throwable localThrowable1)
/*      */       {
/*  965 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*      */       }
/*      */       finally {
/*  968 */         if (is != null) if (localThrowable3 != null) try { is.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else is.close();
/*      */       } }
/*  970 */     flipToFlush(buffer, pos);
/*      */     
/*  972 */     return buffer;
/*      */   }
/*      */   
/*      */   public static String toSummaryString(ByteBuffer buffer)
/*      */   {
/*  977 */     if (buffer == null)
/*  978 */       return "null";
/*  979 */     StringBuilder buf = new StringBuilder();
/*  980 */     buf.append("[p=");
/*  981 */     buf.append(buffer.position());
/*  982 */     buf.append(",l=");
/*  983 */     buf.append(buffer.limit());
/*  984 */     buf.append(",c=");
/*  985 */     buf.append(buffer.capacity());
/*  986 */     buf.append(",r=");
/*  987 */     buf.append(buffer.remaining());
/*  988 */     buf.append("]");
/*  989 */     return buf.toString();
/*      */   }
/*      */   
/*      */   public static String toDetailString(ByteBuffer[] buffer)
/*      */   {
/*  994 */     StringBuilder builder = new StringBuilder();
/*  995 */     builder.append('[');
/*  996 */     for (int i = 0; i < buffer.length; i++)
/*      */     {
/*  998 */       if (i > 0) builder.append(',');
/*  999 */       builder.append(toDetailString(buffer[i]));
/*      */     }
/* 1001 */     builder.append(']');
/* 1002 */     return builder.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void idString(ByteBuffer buffer, StringBuilder out)
/*      */   {
/* 1012 */     out.append(buffer.getClass().getSimpleName());
/* 1013 */     out.append("@");
/* 1014 */     if ((buffer.hasArray()) && (buffer.arrayOffset() == 4))
/*      */     {
/* 1016 */       out.append('T');
/* 1017 */       byte[] array = buffer.array();
/* 1018 */       TypeUtil.toHex(array[0], out);
/* 1019 */       TypeUtil.toHex(array[1], out);
/* 1020 */       TypeUtil.toHex(array[2], out);
/* 1021 */       TypeUtil.toHex(array[3], out);
/*      */     }
/*      */     else {
/* 1024 */       out.append(Integer.toHexString(System.identityHashCode(buffer)));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toIDString(ByteBuffer buffer)
/*      */   {
/* 1034 */     StringBuilder buf = new StringBuilder();
/* 1035 */     idString(buffer, buf);
/* 1036 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toDetailString(ByteBuffer buffer)
/*      */   {
/* 1047 */     if (buffer == null) {
/* 1048 */       return "null";
/*      */     }
/* 1050 */     StringBuilder buf = new StringBuilder();
/* 1051 */     idString(buffer, buf);
/* 1052 */     buf.append("[p=");
/* 1053 */     buf.append(buffer.position());
/* 1054 */     buf.append(",l=");
/* 1055 */     buf.append(buffer.limit());
/* 1056 */     buf.append(",c=");
/* 1057 */     buf.append(buffer.capacity());
/* 1058 */     buf.append(",r=");
/* 1059 */     buf.append(buffer.remaining());
/* 1060 */     buf.append("]={");
/*      */     
/* 1062 */     appendDebugString(buf, buffer);
/*      */     
/* 1064 */     buf.append("}");
/*      */     
/* 1066 */     return buf.toString();
/*      */   }
/*      */   
/*      */   private static void appendDebugString(StringBuilder buf, ByteBuffer buffer)
/*      */   {
/*      */     try
/*      */     {
/* 1073 */       for (int i = 0; i < buffer.position(); i++)
/*      */       {
/* 1075 */         appendContentChar(buf, buffer.get(i));
/* 1076 */         if ((i == 16) && (buffer.position() > 32))
/*      */         {
/* 1078 */           buf.append("...");
/* 1079 */           i = buffer.position() - 16;
/*      */         }
/*      */       }
/* 1082 */       buf.append("<<<");
/* 1083 */       for (int i = buffer.position(); i < buffer.limit(); i++)
/*      */       {
/* 1085 */         appendContentChar(buf, buffer.get(i));
/* 1086 */         if ((i == buffer.position() + 16) && (buffer.limit() > buffer.position() + 32))
/*      */         {
/* 1088 */           buf.append("...");
/* 1089 */           i = buffer.limit() - 16;
/*      */         }
/*      */       }
/* 1092 */       buf.append(">>>");
/* 1093 */       int limit = buffer.limit();
/* 1094 */       buffer.limit(buffer.capacity());
/* 1095 */       for (int i = limit; i < buffer.capacity(); i++)
/*      */       {
/* 1097 */         appendContentChar(buf, buffer.get(i));
/* 1098 */         if ((i == limit + 16) && (buffer.capacity() > limit + 32))
/*      */         {
/* 1100 */           buf.append("...");
/* 1101 */           i = buffer.capacity() - 16;
/*      */         }
/*      */       }
/* 1104 */       buffer.limit(limit);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/* 1108 */       Log.getRootLogger().ignore(x);
/* 1109 */       buf.append("!!concurrent mod!!");
/*      */     }
/*      */   }
/*      */   
/*      */   private static void appendContentChar(StringBuilder buf, byte b)
/*      */   {
/* 1115 */     if (b == 92) {
/* 1116 */       buf.append("\\\\");
/* 1117 */     } else if (b >= 32) {
/* 1118 */       buf.append((char)b);
/* 1119 */     } else if (b == 13) {
/* 1120 */       buf.append("\\r");
/* 1121 */     } else if (b == 10) {
/* 1122 */       buf.append("\\n");
/* 1123 */     } else if (b == 9) {
/* 1124 */       buf.append("\\t");
/*      */     } else {
/* 1126 */       buf.append("\\x").append(TypeUtil.toHexString(b));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String toHexSummary(ByteBuffer buffer)
/*      */   {
/* 1138 */     if (buffer == null)
/* 1139 */       return "null";
/* 1140 */     StringBuilder buf = new StringBuilder();
/*      */     
/* 1142 */     buf.append("b[").append(buffer.remaining()).append("]=");
/* 1143 */     for (int i = buffer.position(); i < buffer.limit(); i++)
/*      */     {
/* 1145 */       TypeUtil.toHex(buffer.get(i), buf);
/* 1146 */       if ((i == buffer.position() + 24) && (buffer.limit() > buffer.position() + 32))
/*      */       {
/* 1148 */         buf.append("...");
/* 1149 */         i = buffer.limit() - 8;
/*      */       }
/*      */     }
/* 1152 */     return buf.toString();
/*      */   }
/*      */   
/*      */ 
/* 1156 */   private static final int[] decDivisors = { 1000000000, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
/*      */   
/*      */ 
/* 1159 */   private static final int[] hexDivisors = { 268435456, 16777216, 1048576, 65536, 4096, 256, 16, 1 };
/*      */   
/*      */ 
/* 1162 */   private static final long[] decDivisorsL = { 1000000000000000000L, 100000000000000000L, 10000000000000000L, 1000000000000000L, 100000000000000L, 10000000000000L, 1000000000000L, 100000000000L, 10000000000L, 1000000000L, 100000000L, 10000000L, 1000000L, 100000L, 10000L, 1000L, 100L, 10L, 1L };
/*      */   
/*      */ 
/*      */ 
/*      */   public static void putCRLF(ByteBuffer buffer)
/*      */   {
/* 1168 */     buffer.put((byte)13);
/* 1169 */     buffer.put((byte)10);
/*      */   }
/*      */   
/*      */   public static boolean isPrefix(ByteBuffer prefix, ByteBuffer buffer)
/*      */   {
/* 1174 */     if (prefix.remaining() > buffer.remaining())
/* 1175 */       return false;
/* 1176 */     int bi = buffer.position();
/* 1177 */     for (int i = prefix.position(); i < prefix.limit(); i++)
/* 1178 */       if (prefix.get(i) != buffer.get(bi++))
/* 1179 */         return false;
/* 1180 */     return true;
/*      */   }
/*      */   
/*      */   public static ByteBuffer ensureCapacity(ByteBuffer buffer, int capacity)
/*      */   {
/* 1185 */     if (buffer == null) {
/* 1186 */       return allocate(capacity);
/*      */     }
/* 1188 */     if (buffer.capacity() >= capacity) {
/* 1189 */       return buffer;
/*      */     }
/* 1191 */     if (buffer.hasArray()) {
/* 1192 */       return ByteBuffer.wrap(Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + capacity), buffer.position(), buffer.remaining());
/*      */     }
/* 1194 */     throw new UnsupportedOperationException();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public static void append(ByteBuffer to, byte b)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: invokestatic 132	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFill	(Ljava/nio/ByteBuffer;)I
/*      */     //   4: istore_2
/*      */     //   5: aload_0
/*      */     //   6: iload_1
/*      */     //   7: invokevirtual 145	java/nio/ByteBuffer:put	(B)Ljava/nio/ByteBuffer;
/*      */     //   10: pop
/*      */     //   11: aload_0
/*      */     //   12: iload_2
/*      */     //   13: invokestatic 134	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*      */     //   16: goto +11 -> 27
/*      */     //   19: astore_3
/*      */     //   20: aload_0
/*      */     //   21: iload_2
/*      */     //   22: invokestatic 134	com/facebook/presto/jdbc/internal/jetty/util/BufferUtil:flipToFlush	(Ljava/nio/ByteBuffer;I)V
/*      */     //   25: aload_3
/*      */     //   26: athrow
/*      */     //   27: return
/*      */     // Line number table:
/*      */     //   Java source line #390	-> byte code offset #0
/*      */     //   Java source line #393	-> byte code offset #5
/*      */     //   Java source line #397	-> byte code offset #11
/*      */     //   Java source line #398	-> byte code offset #16
/*      */     //   Java source line #397	-> byte code offset #19
/*      */     //   Java source line #399	-> byte code offset #27
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	28	0	to	ByteBuffer
/*      */     //   0	28	1	b	byte
/*      */     //   4	18	2	pos	int
/*      */     //   19	7	3	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   5	11	19	finally
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\BufferUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */