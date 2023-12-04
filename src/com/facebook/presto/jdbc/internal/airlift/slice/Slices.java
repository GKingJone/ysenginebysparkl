/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Objects;
/*     */ import sun.misc.Unsafe;
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
/*     */ public final class Slices
/*     */ {
/*  38 */   public static final Slice EMPTY_SLICE = new Slice();
/*     */   
/*     */ 
/*     */   private static final int MAX_ARRAY_SIZE = 2147483639;
/*     */   
/*     */   private static final int SLICE_ALLOC_THRESHOLD = 524288;
/*     */   
/*     */   private static final double SLICE_ALLOW_SKEW = 1.25D;
/*     */   
/*     */ 
/*     */   public static Slice ensureSize(Slice existingSlice, int minWritableBytes)
/*     */   {
/*  50 */     if (existingSlice == null) {
/*  51 */       return allocate(minWritableBytes);
/*     */     }
/*     */     
/*  54 */     if (minWritableBytes <= existingSlice.length()) {
/*  55 */       return existingSlice;
/*     */     }
/*     */     int newCapacity;
/*     */     int newCapacity;
/*  59 */     if (existingSlice.length() == 0) {
/*  60 */       newCapacity = 1;
/*     */     }
/*     */     else {
/*  63 */       newCapacity = existingSlice.length();
/*     */     }
/*  65 */     int minNewCapacity = minWritableBytes;
/*  66 */     while (newCapacity < minNewCapacity) {
/*  67 */       if (newCapacity < 524288) {
/*  68 */         newCapacity <<= 1;
/*     */       }
/*     */       else {
/*  71 */         newCapacity = (int)(newCapacity * 1.25D);
/*     */       }
/*     */     }
/*     */     
/*  75 */     Slice newSlice = allocate(newCapacity);
/*  76 */     newSlice.setBytes(0, existingSlice, 0, existingSlice.length());
/*  77 */     return newSlice;
/*     */   }
/*     */   
/*     */   public static Slice allocate(int capacity)
/*     */   {
/*  82 */     if (capacity == 0) {
/*  83 */       return EMPTY_SLICE;
/*     */     }
/*  85 */     Preconditions.checkArgument(capacity <= 2147483639, "Cannot allocate slice larger than 2147483639 bytes");
/*  86 */     return new Slice(new byte[capacity]);
/*     */   }
/*     */   
/*     */   public static Slice allocateDirect(int capacity)
/*     */   {
/*  91 */     if (capacity == 0) {
/*  92 */       return EMPTY_SLICE;
/*     */     }
/*  94 */     return wrappedBuffer(ByteBuffer.allocateDirect(capacity));
/*     */   }
/*     */   
/*     */   public static Slice copyOf(Slice slice)
/*     */   {
/*  99 */     return copyOf(slice, 0, slice.length());
/*     */   }
/*     */   
/*     */   public static Slice copyOf(Slice slice, int offset, int length)
/*     */   {
/* 104 */     Preconditions.checkPositionIndexes(offset, offset + length, slice.length());
/*     */     
/* 106 */     Slice copy = allocate(length);
/* 107 */     copy.setBytes(0, slice, offset, length);
/*     */     
/* 109 */     return copy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Slice wrappedBuffer(ByteBuffer buffer)
/*     */   {
/* 117 */     if (buffer.isDirect()) {
/* 118 */       long address = JvmUtils.getAddress(buffer);
/* 119 */       return new Slice(null, address + buffer.position(), buffer.limit() - buffer.position(), buffer.capacity(), buffer);
/*     */     }
/*     */     
/* 122 */     if (buffer.hasArray()) {
/* 123 */       int address = Unsafe.ARRAY_BYTE_BASE_OFFSET + buffer.arrayOffset() + buffer.position();
/* 124 */       return new Slice(buffer.array(), address, buffer.limit() - buffer.position(), buffer.array().length, null);
/*     */     }
/*     */     
/* 127 */     throw new IllegalArgumentException("cannot wrap " + buffer.getClass().getName());
/*     */   }
/*     */   
/*     */   public static Slice wrappedBuffer(byte... array)
/*     */   {
/* 132 */     if (array.length == 0) {
/* 133 */       return EMPTY_SLICE;
/*     */     }
/* 135 */     return new Slice(array);
/*     */   }
/*     */   
/*     */   public static Slice wrappedBuffer(byte[] array, int offset, int length)
/*     */   {
/* 140 */     if (length == 0) {
/* 141 */       return EMPTY_SLICE;
/*     */     }
/* 143 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedBooleanArray(boolean... array)
/*     */   {
/* 148 */     return wrappedBooleanArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedBooleanArray(boolean[] array, int offset, int length)
/*     */   {
/* 153 */     if (length == 0) {
/* 154 */       return EMPTY_SLICE;
/*     */     }
/* 156 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedShortArray(short... array)
/*     */   {
/* 161 */     return wrappedShortArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedShortArray(short[] array, int offset, int length)
/*     */   {
/* 166 */     if (length == 0) {
/* 167 */       return EMPTY_SLICE;
/*     */     }
/* 169 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedIntArray(int... array)
/*     */   {
/* 174 */     return wrappedIntArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedIntArray(int[] array, int offset, int length)
/*     */   {
/* 179 */     if (length == 0) {
/* 180 */       return EMPTY_SLICE;
/*     */     }
/* 182 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedLongArray(long... array)
/*     */   {
/* 187 */     return wrappedLongArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedLongArray(long[] array, int offset, int length)
/*     */   {
/* 192 */     if (length == 0) {
/* 193 */       return EMPTY_SLICE;
/*     */     }
/* 195 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedFloatArray(float... array)
/*     */   {
/* 200 */     return wrappedFloatArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedFloatArray(float[] array, int offset, int length)
/*     */   {
/* 205 */     if (length == 0) {
/* 206 */       return EMPTY_SLICE;
/*     */     }
/* 208 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedDoubleArray(double... array)
/*     */   {
/* 213 */     return wrappedDoubleArray(array, 0, array.length);
/*     */   }
/*     */   
/*     */   public static Slice wrappedDoubleArray(double[] array, int offset, int length)
/*     */   {
/* 218 */     if (length == 0) {
/* 219 */       return EMPTY_SLICE;
/*     */     }
/* 221 */     return new Slice(array, offset, length);
/*     */   }
/*     */   
/*     */   public static Slice copiedBuffer(String string, Charset charset)
/*     */   {
/* 226 */     Objects.requireNonNull(string, "string is null");
/* 227 */     Objects.requireNonNull(charset, "charset is null");
/*     */     
/* 229 */     return wrappedBuffer(string.getBytes(charset));
/*     */   }
/*     */   
/*     */   public static Slice utf8Slice(String string)
/*     */   {
/* 234 */     return copiedBuffer(string, StandardCharsets.UTF_8);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static Slice mapFileReadOnly(java.io.File file)
/*     */     throws java.io.IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: ldc -4
/*     */     //   3: invokestatic 221	java/util/Objects:requireNonNull	(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
/*     */     //   6: pop
/*     */     //   7: aload_0
/*     */     //   8: invokevirtual 257	java/io/File:exists	()Z
/*     */     //   11: ifne +15 -> 26
/*     */     //   14: new 259	java/io/FileNotFoundException
/*     */     //   17: dup
/*     */     //   18: aload_0
/*     */     //   19: invokevirtual 260	java/io/File:toString	()Ljava/lang/String;
/*     */     //   22: invokespecial 261	java/io/FileNotFoundException:<init>	(Ljava/lang/String;)V
/*     */     //   25: athrow
/*     */     //   26: new 263	java/io/RandomAccessFile
/*     */     //   29: dup
/*     */     //   30: aload_0
/*     */     //   31: ldc_w 265
/*     */     //   34: invokespecial 268	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
/*     */     //   37: astore_1
/*     */     //   38: aconst_null
/*     */     //   39: astore_2
/*     */     //   40: aload_1
/*     */     //   41: invokevirtual 272	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
/*     */     //   44: astore_3
/*     */     //   45: aconst_null
/*     */     //   46: astore 4
/*     */     //   48: aload_3
/*     */     //   49: getstatic 276	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
/*     */     //   52: lconst_0
/*     */     //   53: aload_0
/*     */     //   54: invokevirtual 279	java/io/File:length	()J
/*     */     //   57: invokevirtual 283	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
/*     */     //   60: astore 5
/*     */     //   62: aload 5
/*     */     //   64: invokestatic 72	com/facebook/presto/jdbc/internal/airlift/slice/Slices:wrappedBuffer	(Ljava/nio/ByteBuffer;)Lcom/facebook/presto/jdbc/internal/airlift/slice/Slice;
/*     */     //   67: astore 6
/*     */     //   69: aload_3
/*     */     //   70: ifnull +31 -> 101
/*     */     //   73: aload 4
/*     */     //   75: ifnull +22 -> 97
/*     */     //   78: aload_3
/*     */     //   79: invokevirtual 286	java/nio/channels/FileChannel:close	()V
/*     */     //   82: goto +19 -> 101
/*     */     //   85: astore 7
/*     */     //   87: aload 4
/*     */     //   89: aload 7
/*     */     //   91: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   94: goto +7 -> 101
/*     */     //   97: aload_3
/*     */     //   98: invokevirtual 286	java/nio/channels/FileChannel:close	()V
/*     */     //   101: aload_1
/*     */     //   102: ifnull +29 -> 131
/*     */     //   105: aload_2
/*     */     //   106: ifnull +21 -> 127
/*     */     //   109: aload_1
/*     */     //   110: invokevirtual 293	java/io/RandomAccessFile:close	()V
/*     */     //   113: goto +18 -> 131
/*     */     //   116: astore 7
/*     */     //   118: aload_2
/*     */     //   119: aload 7
/*     */     //   121: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   124: goto +7 -> 131
/*     */     //   127: aload_1
/*     */     //   128: invokevirtual 293	java/io/RandomAccessFile:close	()V
/*     */     //   131: aload 6
/*     */     //   133: areturn
/*     */     //   134: astore 5
/*     */     //   136: aload 5
/*     */     //   138: astore 4
/*     */     //   140: aload 5
/*     */     //   142: athrow
/*     */     //   143: astore 8
/*     */     //   145: aload_3
/*     */     //   146: ifnull +31 -> 177
/*     */     //   149: aload 4
/*     */     //   151: ifnull +22 -> 173
/*     */     //   154: aload_3
/*     */     //   155: invokevirtual 286	java/nio/channels/FileChannel:close	()V
/*     */     //   158: goto +19 -> 177
/*     */     //   161: astore 9
/*     */     //   163: aload 4
/*     */     //   165: aload 9
/*     */     //   167: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   170: goto +7 -> 177
/*     */     //   173: aload_3
/*     */     //   174: invokevirtual 286	java/nio/channels/FileChannel:close	()V
/*     */     //   177: aload 8
/*     */     //   179: athrow
/*     */     //   180: astore_3
/*     */     //   181: aload_3
/*     */     //   182: astore_2
/*     */     //   183: aload_3
/*     */     //   184: athrow
/*     */     //   185: astore 10
/*     */     //   187: aload_1
/*     */     //   188: ifnull +29 -> 217
/*     */     //   191: aload_2
/*     */     //   192: ifnull +21 -> 213
/*     */     //   195: aload_1
/*     */     //   196: invokevirtual 293	java/io/RandomAccessFile:close	()V
/*     */     //   199: goto +18 -> 217
/*     */     //   202: astore 11
/*     */     //   204: aload_2
/*     */     //   205: aload 11
/*     */     //   207: invokevirtual 292	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*     */     //   210: goto +7 -> 217
/*     */     //   213: aload_1
/*     */     //   214: invokevirtual 293	java/io/RandomAccessFile:close	()V
/*     */     //   217: aload 10
/*     */     //   219: athrow
/*     */     // Line number table:
/*     */     //   Java source line #240	-> byte code offset #0
/*     */     //   Java source line #242	-> byte code offset #7
/*     */     //   Java source line #243	-> byte code offset #14
/*     */     //   Java source line #246	-> byte code offset #26
/*     */     //   Java source line #247	-> byte code offset #40
/*     */     //   Java source line #246	-> byte code offset #45
/*     */     //   Java source line #248	-> byte code offset #48
/*     */     //   Java source line #249	-> byte code offset #62
/*     */     //   Java source line #250	-> byte code offset #69
/*     */     //   Java source line #249	-> byte code offset #131
/*     */     //   Java source line #246	-> byte code offset #134
/*     */     //   Java source line #250	-> byte code offset #143
/*     */     //   Java source line #246	-> byte code offset #180
/*     */     //   Java source line #250	-> byte code offset #185
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	220	0	file	java.io.File
/*     */     //   37	177	1	randomAccessFile	java.io.RandomAccessFile
/*     */     //   39	166	2	localThrowable6	Throwable
/*     */     //   44	130	3	channel	java.nio.channels.FileChannel
/*     */     //   180	4	3	localThrowable4	Throwable
/*     */     //   46	118	4	localThrowable7	Throwable
/*     */     //   60	3	5	byteBuffer	java.nio.MappedByteBuffer
/*     */     //   134	7	5	localThrowable2	Throwable
/*     */     //   67	65	6	localSlice	Slice
/*     */     //   85	5	7	localThrowable	Throwable
/*     */     //   116	4	7	localThrowable1	Throwable
/*     */     //   143	35	8	localObject1	Object
/*     */     //   161	5	9	localThrowable3	Throwable
/*     */     //   185	33	10	localObject2	Object
/*     */     //   202	4	11	localThrowable5	Throwable
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   78	82	85	java/lang/Throwable
/*     */     //   109	113	116	java/lang/Throwable
/*     */     //   48	69	134	java/lang/Throwable
/*     */     //   48	69	143	finally
/*     */     //   134	145	143	finally
/*     */     //   154	158	161	java/lang/Throwable
/*     */     //   40	101	180	java/lang/Throwable
/*     */     //   134	180	180	java/lang/Throwable
/*     */     //   40	101	185	finally
/*     */     //   134	187	185	finally
/*     */     //   195	199	202	java/lang/Throwable
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Slices.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */