/*      */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.invoke.MethodHandle;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.Objects;
/*      */ import javax.annotation.Nullable;
/*      */ import sun.misc.Unsafe;
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
/*      */ public final class Slice
/*      */   implements Comparable<Slice>
/*      */ {
/*   59 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(Slice.class).instanceSize();
/*      */   private final Object base;
/*      */   private final long address;
/*      */   private final int size;
/*      */   
/*      */   @Deprecated
/*      */   public static Slice toUnsafeSlice(ByteBuffer byteBuffer)
/*      */   {
/*   67 */     return Slices.wrappedBuffer(byteBuffer);
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
/*      */   private final int retainedSize;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Object reference;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int hash;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice()
/*      */   {
/*  111 */     this.base = null;
/*  112 */     this.address = 0L;
/*  113 */     this.size = 0;
/*  114 */     this.retainedSize = INSTANCE_SIZE;
/*  115 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(byte[] base)
/*      */   {
/*  123 */     Objects.requireNonNull(base, "base is null");
/*  124 */     this.base = base;
/*  125 */     this.address = Unsafe.ARRAY_BYTE_BASE_OFFSET;
/*  126 */     this.size = base.length;
/*  127 */     this.retainedSize = (INSTANCE_SIZE + base.length);
/*  128 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(byte[] base, int offset, int length)
/*      */   {
/*  136 */     Objects.requireNonNull(base, "base is null");
/*  137 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  139 */     this.base = base;
/*  140 */     this.address = (Unsafe.ARRAY_BYTE_BASE_OFFSET + offset);
/*  141 */     this.size = length;
/*  142 */     this.retainedSize = (INSTANCE_SIZE + base.length);
/*  143 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(boolean[] base, int offset, int length)
/*      */   {
/*  151 */     Objects.requireNonNull(base, "base is null");
/*  152 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  154 */     this.base = base;
/*  155 */     this.address = (Unsafe.ARRAY_BOOLEAN_BASE_OFFSET + offset);
/*  156 */     this.size = (length * Unsafe.ARRAY_BOOLEAN_INDEX_SCALE);
/*  157 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_BOOLEAN_INDEX_SCALE);
/*  158 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(short[] base, int offset, int length)
/*      */   {
/*  166 */     Objects.requireNonNull(base, "base is null");
/*  167 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  169 */     this.base = base;
/*  170 */     this.address = (Unsafe.ARRAY_SHORT_BASE_OFFSET + offset);
/*  171 */     this.size = (length * Unsafe.ARRAY_SHORT_INDEX_SCALE);
/*  172 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_SHORT_INDEX_SCALE);
/*  173 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(int[] base, int offset, int length)
/*      */   {
/*  181 */     Objects.requireNonNull(base, "base is null");
/*  182 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  184 */     this.base = base;
/*  185 */     this.address = (Unsafe.ARRAY_INT_BASE_OFFSET + offset);
/*  186 */     this.size = (length * Unsafe.ARRAY_INT_INDEX_SCALE);
/*  187 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_INT_INDEX_SCALE);
/*  188 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(long[] base, int offset, int length)
/*      */   {
/*  196 */     Objects.requireNonNull(base, "base is null");
/*  197 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  199 */     this.base = base;
/*  200 */     this.address = (Unsafe.ARRAY_LONG_BASE_OFFSET + offset);
/*  201 */     this.size = (length * Unsafe.ARRAY_LONG_INDEX_SCALE);
/*  202 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_LONG_INDEX_SCALE);
/*  203 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(float[] base, int offset, int length)
/*      */   {
/*  211 */     Objects.requireNonNull(base, "base is null");
/*  212 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  214 */     this.base = base;
/*  215 */     this.address = (Unsafe.ARRAY_FLOAT_BASE_OFFSET + offset);
/*  216 */     this.size = (length * Unsafe.ARRAY_FLOAT_INDEX_SCALE);
/*  217 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_FLOAT_INDEX_SCALE);
/*  218 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(double[] base, int offset, int length)
/*      */   {
/*  226 */     Objects.requireNonNull(base, "base is null");
/*  227 */     Preconditions.checkPositionIndexes(offset, offset + length, base.length);
/*      */     
/*  229 */     this.base = base;
/*  230 */     this.address = (Unsafe.ARRAY_DOUBLE_BASE_OFFSET + offset);
/*  231 */     this.size = (length * Unsafe.ARRAY_DOUBLE_INDEX_SCALE);
/*  232 */     this.retainedSize = (INSTANCE_SIZE + base.length * Unsafe.ARRAY_DOUBLE_INDEX_SCALE);
/*  233 */     this.reference = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Slice(@Nullable Object base, long address, int size, int retainedSize, @Nullable Object reference)
/*      */   {
/*  241 */     if (address <= 0L) {
/*  242 */       throw new IllegalArgumentException(String.format("Invalid address: %s", new Object[] { Long.valueOf(address) }));
/*      */     }
/*  244 */     if (size <= 0) {
/*  245 */       throw new IllegalArgumentException(String.format("Invalid size: %s", new Object[] { Integer.valueOf(size) }));
/*      */     }
/*  247 */     Preconditions.checkArgument(address + size >= size, "Address + size is greater than 64 bits");
/*      */     
/*  249 */     this.reference = reference;
/*  250 */     this.base = base;
/*  251 */     this.address = address;
/*  252 */     this.size = size;
/*      */     
/*  254 */     this.retainedSize = retainedSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getBase()
/*      */   {
/*  263 */     return this.base;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getAddress()
/*      */   {
/*  272 */     return this.address;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int length()
/*      */   {
/*  280 */     return this.size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRetainedSize()
/*      */   {
/*  288 */     return this.retainedSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void fill(byte value)
/*      */   {
/*  296 */     int offset = 0;
/*  297 */     int length = this.size;
/*  298 */     long longValue = fillLong(value);
/*  299 */     while (length >= 8) {
/*  300 */       JvmUtils.unsafe.putLong(this.base, this.address + offset, longValue);
/*  301 */       offset += 8;
/*  302 */       length -= 8;
/*      */     }
/*      */     
/*  305 */     while (length > 0) {
/*  306 */       JvmUtils.unsafe.putByte(this.base, this.address + offset, value);
/*  307 */       offset++;
/*  308 */       length--;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void clear()
/*      */   {
/*  317 */     clear(0, this.size);
/*      */   }
/*      */   
/*      */   public void clear(int offset, int length)
/*      */   {
/*  322 */     while (length >= 8) {
/*  323 */       JvmUtils.unsafe.putLong(this.base, this.address + offset, 0L);
/*  324 */       offset += 8;
/*  325 */       length -= 8;
/*      */     }
/*      */     
/*  328 */     while (length > 0) {
/*  329 */       JvmUtils.unsafe.putByte(this.base, this.address + offset, (byte)0);
/*  330 */       offset++;
/*  331 */       length--;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte getByte(int index)
/*      */   {
/*  343 */     checkIndexLength(index, 1);
/*  344 */     return getByteUnchecked(index);
/*      */   }
/*      */   
/*      */   byte getByteUnchecked(int index)
/*      */   {
/*  349 */     return JvmUtils.unsafe.getByte(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public short getUnsignedByte(int index)
/*      */   {
/*  361 */     return (short)(getByte(index) & 0xFF);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public short getShort(int index)
/*      */   {
/*  373 */     checkIndexLength(index, 2);
/*  374 */     return getShortUnchecked(index);
/*      */   }
/*      */   
/*      */   short getShortUnchecked(int index)
/*      */   {
/*  379 */     return JvmUtils.unsafe.getShort(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getUnsignedShort(int index)
/*      */   {
/*  391 */     return getShort(index) & 0xFFFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getInt(int index)
/*      */   {
/*  403 */     checkIndexLength(index, 4);
/*  404 */     return getIntUnchecked(index);
/*      */   }
/*      */   
/*      */   int getIntUnchecked(int index)
/*      */   {
/*  409 */     return JvmUtils.unsafe.getInt(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getUnsignedInt(int index)
/*      */   {
/*  421 */     return getInt(index) & 0xFFFFFFFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLong(int index)
/*      */   {
/*  433 */     checkIndexLength(index, 8);
/*  434 */     return getLongUnchecked(index);
/*      */   }
/*      */   
/*      */   long getLongUnchecked(int index)
/*      */   {
/*  439 */     return JvmUtils.unsafe.getLong(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public float getFloat(int index)
/*      */   {
/*  451 */     checkIndexLength(index, 4);
/*  452 */     return JvmUtils.unsafe.getFloat(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public double getDouble(int index)
/*      */   {
/*  464 */     checkIndexLength(index, 8);
/*  465 */     return JvmUtils.unsafe.getDouble(this.base, this.address + index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getBytes(int index, Slice destination)
/*      */   {
/*  477 */     getBytes(index, destination, 0, destination.length());
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
/*      */   public void getBytes(int index, Slice destination, int destinationIndex, int length)
/*      */   {
/*  495 */     destination.setBytes(destinationIndex, this, index, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getBytes(int index, byte[] destination)
/*      */   {
/*  507 */     getBytes(index, destination, 0, destination.length);
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
/*      */   public void getBytes(int index, byte[] destination, int destinationIndex, int length)
/*      */   {
/*  525 */     checkIndexLength(index, length);
/*  526 */     Preconditions.checkPositionIndexes(destinationIndex, destinationIndex + length, destination.length);
/*      */     
/*  528 */     copyMemory(this.base, this.address + index, destination, Unsafe.ARRAY_BYTE_BASE_OFFSET + destinationIndex, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getBytes()
/*      */   {
/*  536 */     return getBytes(0, length());
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
/*      */   public byte[] getBytes(int index, int length)
/*      */   {
/*  549 */     byte[] bytes = new byte[length];
/*  550 */     getBytes(index, bytes, 0, length);
/*  551 */     return bytes;
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
/*      */   public void getBytes(int index, OutputStream out, int length)
/*      */     throws IOException
/*      */   {
/*  567 */     checkIndexLength(index, length);
/*      */     
/*  569 */     if ((this.base instanceof byte[])) {
/*  570 */       out.write((byte[])this.base, (int)(this.address - Unsafe.ARRAY_BYTE_BASE_OFFSET + index), length);
/*  571 */       return;
/*      */     }
/*      */     
/*  574 */     byte[] buffer = new byte['က'];
/*  575 */     while (length > 0) {
/*  576 */       int size = Math.min(buffer.length, length);
/*  577 */       getBytes(index, buffer, 0, size);
/*  578 */       out.write(buffer, 0, size);
/*  579 */       length -= size;
/*  580 */       index += size;
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
/*      */   public void setByte(int index, int value)
/*      */   {
/*  593 */     checkIndexLength(index, 1);
/*  594 */     setByteUnchecked(index, value);
/*      */   }
/*      */   
/*      */   void setByteUnchecked(int index, int value)
/*      */   {
/*  599 */     JvmUtils.unsafe.putByte(this.base, this.address + index, (byte)(value & 0xFF));
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
/*      */   public void setShort(int index, int value)
/*      */   {
/*  612 */     checkIndexLength(index, 2);
/*  613 */     setShortUnchecked(index, value);
/*      */   }
/*      */   
/*      */   void setShortUnchecked(int index, int value)
/*      */   {
/*  618 */     JvmUtils.unsafe.putShort(this.base, this.address + index, (short)(value & 0xFFFF));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setInt(int index, int value)
/*      */   {
/*  630 */     checkIndexLength(index, 4);
/*  631 */     setIntUnchecked(index, value);
/*      */   }
/*      */   
/*      */   void setIntUnchecked(int index, int value)
/*      */   {
/*  636 */     JvmUtils.unsafe.putInt(this.base, this.address + index, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLong(int index, long value)
/*      */   {
/*  648 */     checkIndexLength(index, 8);
/*  649 */     setLongUnchecked(index, value);
/*      */   }
/*      */   
/*      */   void setLongUnchecked(int index, long value)
/*      */   {
/*  654 */     JvmUtils.unsafe.putLong(this.base, this.address + index, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFloat(int index, float value)
/*      */   {
/*  666 */     checkIndexLength(index, 4);
/*  667 */     JvmUtils.unsafe.putFloat(this.base, this.address + index, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDouble(int index, double value)
/*      */   {
/*  679 */     checkIndexLength(index, 8);
/*  680 */     JvmUtils.unsafe.putDouble(this.base, this.address + index, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBytes(int index, Slice source)
/*      */   {
/*  692 */     setBytes(index, source, 0, source.length());
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
/*      */   public void setBytes(int index, Slice source, int sourceIndex, int length)
/*      */   {
/*  710 */     checkIndexLength(index, length);
/*  711 */     Preconditions.checkPositionIndexes(sourceIndex, sourceIndex + length, source.length());
/*      */     
/*  713 */     copyMemory(source.base, source.address + sourceIndex, this.base, this.address + index, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBytes(int index, byte[] source)
/*      */   {
/*  725 */     setBytes(index, source, 0, source.length);
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
/*      */   public void setBytes(int index, byte[] source, int sourceIndex, int length)
/*      */   {
/*  740 */     Preconditions.checkPositionIndexes(sourceIndex, sourceIndex + length, source.length);
/*  741 */     copyMemory(source, Unsafe.ARRAY_BYTE_BASE_OFFSET + sourceIndex, this.base, this.address + index, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBytes(int index, InputStream in, int length)
/*      */     throws IOException
/*      */   {
/*  754 */     checkIndexLength(index, length);
/*  755 */     if ((this.base instanceof byte[])) {
/*  756 */       byte[] bytes = (byte[])this.base;
/*  757 */       int offset = (int)(this.address - Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*  758 */       while (length > 0) {
/*  759 */         int bytesRead = in.read(bytes, offset, length);
/*  760 */         if (bytesRead < 0) {
/*  761 */           throw new IndexOutOfBoundsException("End of stream");
/*      */         }
/*  763 */         length -= bytesRead;
/*  764 */         offset += bytesRead;
/*      */       }
/*  766 */       return;
/*      */     }
/*      */     
/*  769 */     byte[] bytes = new byte['က'];
/*      */     
/*  771 */     while (length > 0) {
/*  772 */       int bytesRead = in.read(bytes, 0, Math.min(bytes.length, length));
/*  773 */       if (bytesRead < 0) {
/*  774 */         throw new IndexOutOfBoundsException("End of stream");
/*      */       }
/*  776 */       copyMemory(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET, this.base, this.address + index, bytesRead);
/*  777 */       length -= bytesRead;
/*  778 */       index += bytesRead;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Slice slice(int index, int length)
/*      */   {
/*  788 */     if ((index == 0) && (length == length())) {
/*  789 */       return this;
/*      */     }
/*  791 */     checkIndexLength(index, length);
/*  792 */     if (length == 0) {
/*  793 */       return Slices.EMPTY_SLICE;
/*      */     }
/*  795 */     return new Slice(this.base, this.address + index, length, this.retainedSize, this.reference);
/*      */   }
/*      */   
/*      */   public int indexOfByte(int b)
/*      */   {
/*  800 */     b &= 0xFF;
/*  801 */     for (int i = 0; i < this.size; i++) {
/*  802 */       if (getByteUnchecked(i) == b) {
/*  803 */         return i;
/*      */       }
/*      */     }
/*  806 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int indexOf(Slice slice)
/*      */   {
/*  816 */     return indexOf(slice, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int indexOf(Slice pattern, int offset)
/*      */   {
/*  826 */     if ((this.size == 0) || (offset >= this.size)) {
/*  827 */       return -1;
/*      */     }
/*      */     
/*  830 */     if (pattern.length() == 0) {
/*  831 */       return offset;
/*      */     }
/*      */     
/*      */ 
/*  835 */     if ((pattern.length() < 4) || (this.size < 8)) {
/*  836 */       return indexOfBruteForce(pattern, offset);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  841 */     int head = pattern.getIntUnchecked(0);
/*      */     
/*      */ 
/*  844 */     int firstByteMask = head & 0xFF;
/*  845 */     firstByteMask |= firstByteMask << 8;
/*  846 */     firstByteMask |= firstByteMask << 16;
/*      */     
/*  848 */     int lastValidIndex = this.size - pattern.length();
/*  849 */     int index = offset;
/*  850 */     while (index <= lastValidIndex)
/*      */     {
/*  852 */       int value = getIntUnchecked(index);
/*      */       
/*      */ 
/*      */ 
/*  856 */       int valueXor = value ^ firstByteMask;
/*  857 */       int hasZeroBytes = valueXor - 16843009 & (valueXor ^ 0xFFFFFFFF) & 0x80808080;
/*      */       
/*      */ 
/*  860 */       if (hasZeroBytes == 0) {
/*  861 */         index += 4;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  866 */         if ((value == head) && (equalsUnchecked(index, pattern, 0, pattern.length()))) {
/*  867 */           return index;
/*      */         }
/*      */         
/*  870 */         index++;
/*      */       }
/*      */     }
/*  873 */     return -1;
/*      */   }
/*      */   
/*      */   int indexOfBruteForce(Slice pattern, int offset)
/*      */   {
/*  878 */     if ((this.size == 0) || (offset >= this.size)) {
/*  879 */       return -1;
/*      */     }
/*      */     
/*  882 */     if (pattern.length() == 0) {
/*  883 */       return offset;
/*      */     }
/*      */     
/*  886 */     byte firstByte = pattern.getByteUnchecked(0);
/*  887 */     int lastValidIndex = this.size - pattern.length();
/*  888 */     int index = offset;
/*      */     for (;;)
/*      */     {
/*  891 */       if ((index < lastValidIndex) && (getByteUnchecked(index) != firstByte)) {
/*  892 */         index++;
/*      */       } else {
/*  894 */         if (index > lastValidIndex) {
/*      */           break;
/*      */         }
/*      */         
/*  898 */         if (equalsUnchecked(index, pattern, 0, pattern.length())) {
/*  899 */           return index;
/*      */         }
/*      */         
/*  902 */         index++;
/*      */       }
/*      */     }
/*  905 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int compareTo(Slice that)
/*      */   {
/*  917 */     if (this == that) {
/*  918 */       return 0;
/*      */     }
/*  920 */     return compareTo(0, this.size, that, 0, that.size);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int compareTo(int offset, int length, Slice that, int otherOffset, int otherLength)
/*      */   {
/*  930 */     if ((this == that) && (offset == otherOffset) && (length == otherLength)) {
/*  931 */       return 0;
/*      */     }
/*      */     
/*  934 */     checkIndexLength(offset, length);
/*  935 */     that.checkIndexLength(otherOffset, otherLength);
/*      */     
/*  937 */     long thisAddress = this.address + offset;
/*  938 */     long thatAddress = that.address + otherOffset;
/*      */     
/*  940 */     int compareLength = Math.min(length, otherLength);
/*  941 */     while (compareLength >= 8) {
/*  942 */       long thisLong = JvmUtils.unsafe.getLong(this.base, thisAddress);
/*  943 */       long thatLong = JvmUtils.unsafe.getLong(that.base, thatAddress);
/*      */       
/*  945 */       if (thisLong != thatLong) {
/*  946 */         return longBytesToLong(thisLong) < longBytesToLong(thatLong) ? -1 : 1;
/*      */       }
/*      */       
/*  949 */       thisAddress += 8L;
/*  950 */       thatAddress += 8L;
/*  951 */       compareLength -= 8;
/*      */     }
/*      */     
/*  954 */     while (compareLength > 0) {
/*  955 */       byte thisByte = JvmUtils.unsafe.getByte(this.base, thisAddress);
/*  956 */       byte thatByte = JvmUtils.unsafe.getByte(that.base, thatAddress);
/*      */       
/*  958 */       int v = compareUnsignedBytes(thisByte, thatByte);
/*  959 */       if (v != 0) {
/*  960 */         return v;
/*      */       }
/*  962 */       thisAddress += 1L;
/*  963 */       thatAddress += 1L;
/*  964 */       compareLength--;
/*      */     }
/*      */     
/*  967 */     return Integer.compare(length, otherLength);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object o)
/*      */   {
/*  977 */     if (this == o) {
/*  978 */       return true;
/*      */     }
/*  980 */     if (!(o instanceof Slice)) {
/*  981 */       return false;
/*      */     }
/*      */     
/*  984 */     Slice that = (Slice)o;
/*  985 */     if (length() != that.length()) {
/*  986 */       return false;
/*      */     }
/*      */     
/*  989 */     return equalsUnchecked(0, that, 0, length());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1000 */     if (this.hash != 0) {
/* 1001 */       return this.hash;
/*      */     }
/*      */     
/* 1004 */     this.hash = hashCode(0, this.size);
/* 1005 */     return this.hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode(int offset, int length)
/*      */   {
/* 1013 */     return (int)XxHash64.hash(this, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(int offset, int length, Slice that, int otherOffset, int otherLength)
/*      */   {
/* 1023 */     if (length != otherLength) {
/* 1024 */       return false;
/*      */     }
/*      */     
/* 1027 */     if ((this == that) && (offset == otherOffset)) {
/* 1028 */       return true;
/*      */     }
/*      */     
/* 1031 */     checkIndexLength(offset, length);
/* 1032 */     that.checkIndexLength(otherOffset, otherLength);
/*      */     
/* 1034 */     return equalsUnchecked(offset, that, otherOffset, length);
/*      */   }
/*      */   
/*      */   boolean equalsUnchecked(int offset, Slice that, int otherOffset, int length)
/*      */   {
/* 1039 */     long thisAddress = this.address + offset;
/* 1040 */     long thatAddress = that.address + otherOffset;
/*      */     
/* 1042 */     while (length >= 8) {
/* 1043 */       long thisLong = JvmUtils.unsafe.getLong(this.base, thisAddress);
/* 1044 */       long thatLong = JvmUtils.unsafe.getLong(that.base, thatAddress);
/*      */       
/* 1046 */       if (thisLong != thatLong) {
/* 1047 */         return false;
/*      */       }
/*      */       
/* 1050 */       thisAddress += 8L;
/* 1051 */       thatAddress += 8L;
/* 1052 */       length -= 8;
/*      */     }
/*      */     
/* 1055 */     while (length > 0) {
/* 1056 */       byte thisByte = JvmUtils.unsafe.getByte(this.base, thisAddress);
/* 1057 */       byte thatByte = JvmUtils.unsafe.getByte(that.base, thatAddress);
/* 1058 */       if (thisByte != thatByte) {
/* 1059 */         return false;
/*      */       }
/* 1061 */       thisAddress += 1L;
/* 1062 */       thatAddress += 1L;
/* 1063 */       length--;
/*      */     }
/*      */     
/* 1066 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BasicSliceInput getInput()
/*      */   {
/* 1075 */     return new BasicSliceInput(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SliceOutput getOutput()
/*      */   {
/* 1084 */     return new BasicSliceOutput(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(Charset charset)
/*      */   {
/* 1093 */     return toString(0, length(), charset);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toStringUtf8()
/*      */   {
/* 1102 */     return toString(StandardCharsets.UTF_8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toStringAscii()
/*      */   {
/* 1112 */     return toStringAscii(0, this.size);
/*      */   }
/*      */   
/*      */   public String toStringAscii(int index, int length)
/*      */   {
/* 1117 */     checkIndexLength(index, length);
/* 1118 */     if (length == 0) {
/* 1119 */       return "";
/*      */     }
/*      */     
/* 1122 */     if ((this.base instanceof byte[]))
/*      */     {
/* 1124 */       return new String((byte[])this.base, 0, (int)(this.address - Unsafe.ARRAY_BYTE_BASE_OFFSET + index), length);
/*      */     }
/*      */     
/* 1127 */     char[] chars = new char[length];
/* 1128 */     for (int pos = index; pos < length; pos++) {
/* 1129 */       chars[pos] = ((char)(getByteUnchecked(pos) & 0x7F));
/*      */     }
/* 1131 */     return new String(chars);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(int index, int length, Charset charset)
/*      */   {
/* 1140 */     if (length == 0) {
/* 1141 */       return "";
/*      */     }
/* 1143 */     if ((this.base instanceof byte[])) {
/* 1144 */       return new String((byte[])this.base, (int)(this.address - Unsafe.ARRAY_BYTE_BASE_OFFSET + index), length, charset);
/*      */     }
/*      */     
/* 1147 */     return StringDecoder.decodeString(toByteBuffer(index, length), charset);
/*      */   }
/*      */   
/*      */   public ByteBuffer toByteBuffer()
/*      */   {
/* 1152 */     return toByteBuffer(0, this.size);
/*      */   }
/*      */   
/*      */   public ByteBuffer toByteBuffer(int index, int length)
/*      */   {
/* 1157 */     checkIndexLength(index, length);
/*      */     
/* 1159 */     if ((this.base instanceof byte[])) {
/* 1160 */       return ByteBuffer.wrap((byte[])this.base, (int)(this.address - Unsafe.ARRAY_BYTE_BASE_OFFSET + index), length);
/*      */     }
/*      */     try
/*      */     {
/* 1164 */       return JvmUtils.newByteBuffer.invokeExact(this.address + index, length, this.reference);
/*      */     }
/*      */     catch (Throwable throwable) {
/* 1167 */       if ((throwable instanceof Error)) {
/* 1168 */         throw ((Error)throwable);
/*      */       }
/* 1170 */       if ((throwable instanceof RuntimeException)) {
/* 1171 */         throw ((RuntimeException)throwable);
/*      */       }
/* 1173 */       if ((throwable instanceof InterruptedException)) {
/* 1174 */         Thread.currentThread().interrupt();
/*      */       }
/* 1176 */       throw new RuntimeException(throwable);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1187 */     StringBuilder builder = new StringBuilder("Slice{");
/* 1188 */     if (this.base != null) {
/* 1189 */       builder.append("base=").append(identityToString(this.base)).append(", ");
/*      */     }
/* 1191 */     builder.append("address=").append(this.address);
/* 1192 */     builder.append(", length=").append(length());
/* 1193 */     builder.append('}');
/* 1194 */     return builder.toString();
/*      */   }
/*      */   
/*      */   private static String identityToString(Object o)
/*      */   {
/* 1199 */     if (o == null) {
/* 1200 */       return null;
/*      */     }
/* 1202 */     return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void copyMemory(Object src, long srcAddress, Object dest, long destAddress, int length)
/*      */   {
/* 1209 */     int bytesToCopy = length - length % 8;
/* 1210 */     JvmUtils.unsafe.copyMemory(src, srcAddress, dest, destAddress, bytesToCopy);
/* 1211 */     JvmUtils.unsafe.copyMemory(src, srcAddress + bytesToCopy, dest, destAddress + bytesToCopy, length - bytesToCopy);
/*      */   }
/*      */   
/*      */   private void checkIndexLength(int index, int length)
/*      */   {
/* 1216 */     Preconditions.checkPositionIndexes(index, index + length, length());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static long fillLong(byte value)
/*      */   {
/* 1225 */     return (value & 0xFF) << 56 | (value & 0xFF) << 48 | (value & 0xFF) << 40 | (value & 0xFF) << 32 | (value & 0xFF) << 24 | (value & 0xFF) << 16 | (value & 0xFF) << 8 | value & 0xFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int compareUnsignedBytes(byte thisByte, byte thatByte)
/*      */   {
/* 1237 */     return unsignedByteToInt(thisByte) - unsignedByteToInt(thatByte);
/*      */   }
/*      */   
/*      */   private static int unsignedByteToInt(byte thisByte)
/*      */   {
/* 1242 */     return thisByte & 0xFF;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static long longBytesToLong(long bytes)
/*      */   {
/* 1252 */     return Long.reverseBytes(bytes) ^ 0x8000000000000000;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Slice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */