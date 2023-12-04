/*     */ package com.facebook.presto.jdbc.internal.guava.primitives;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Comparator;
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
/*     */ public final class UnsignedBytes
/*     */ {
/*     */   public static final byte MAX_POWER_OF_TWO = -128;
/*     */   public static final byte MAX_VALUE = -1;
/*     */   private static final int UNSIGNED_MASK = 255;
/*     */   
/*     */   public static int toInt(byte value)
/*     */   {
/*  75 */     return value & 0xFF;
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
/*     */   public static byte checkedCast(long value)
/*     */   {
/*  89 */     if (value >> 8 != 0L)
/*     */     {
/*  91 */       long l = value;throw new IllegalArgumentException(34 + "Out of range: " + l);
/*     */     }
/*  93 */     return (byte)(int)value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte saturatedCast(long value)
/*     */   {
/* 105 */     if (value > toInt((byte)-1)) {
/* 106 */       return -1;
/*     */     }
/* 108 */     if (value < 0L) {
/* 109 */       return 0;
/*     */     }
/* 111 */     return (byte)(int)value;
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
/*     */   public static int compare(byte a, byte b)
/*     */   {
/* 126 */     return toInt(a) - toInt(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte min(byte... array)
/*     */   {
/* 138 */     Preconditions.checkArgument(array.length > 0);
/* 139 */     int min = toInt(array[0]);
/* 140 */     for (int i = 1; i < array.length; i++) {
/* 141 */       int next = toInt(array[i]);
/* 142 */       if (next < min) {
/* 143 */         min = next;
/*     */       }
/*     */     }
/* 146 */     return (byte)min;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte max(byte... array)
/*     */   {
/* 158 */     Preconditions.checkArgument(array.length > 0);
/* 159 */     int max = toInt(array[0]);
/* 160 */     for (int i = 1; i < array.length; i++) {
/* 161 */       int next = toInt(array[i]);
/* 162 */       if (next > max) {
/* 163 */         max = next;
/*     */       }
/*     */     }
/* 166 */     return (byte)max;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static String toString(byte x)
/*     */   {
/* 176 */     return toString(x, 10);
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
/*     */   @Beta
/*     */   public static String toString(byte x, int radix)
/*     */   {
/* 191 */     Preconditions.checkArgument((radix >= 2) && (radix <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[] { Integer.valueOf(radix) });
/*     */     
/*     */ 
/* 194 */     return Integer.toString(toInt(x), radix);
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
/*     */   @Beta
/*     */   public static byte parseUnsignedByte(String string)
/*     */   {
/* 208 */     return parseUnsignedByte(string, 10);
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
/*     */   @Beta
/*     */   public static byte parseUnsignedByte(String string, int radix)
/*     */   {
/* 225 */     int parse = Integer.parseInt((String)Preconditions.checkNotNull(string), radix);
/*     */     
/* 227 */     if (parse >> 8 == 0) {
/* 228 */       return (byte)parse;
/*     */     }
/* 230 */     int i = parse;throw new NumberFormatException(25 + "out of range: " + i);
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
/*     */   public static String join(String separator, byte... array)
/*     */   {
/* 244 */     Preconditions.checkNotNull(separator);
/* 245 */     if (array.length == 0) {
/* 246 */       return "";
/*     */     }
/*     */     
/*     */ 
/* 250 */     StringBuilder builder = new StringBuilder(array.length * (3 + separator.length()));
/* 251 */     builder.append(toInt(array[0]));
/* 252 */     for (int i = 1; i < array.length; i++) {
/* 253 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 255 */     return builder.toString();
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
/*     */   public static Comparator<byte[]> lexicographicalComparator()
/*     */   {
/* 275 */     return LexicographicalComparatorHolder.BEST_COMPARATOR;
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
/* 280 */     return LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class LexicographicalComparatorHolder
/*     */   {
/* 292 */     static final String UNSAFE_COMPARATOR_NAME = String.valueOf(LexicographicalComparatorHolder.class.getName()).concat("$UnsafeComparator");
/*     */     
/*     */ 
/* 295 */     static final Comparator<byte[]> BEST_COMPARATOR = getBestComparator();
/*     */     
/*     */     @VisibleForTesting
/*     */     static enum UnsafeComparator implements Comparator<byte[]> {
/* 299 */       INSTANCE;
/*     */       
/* 301 */       static { BIG_ENDIAN = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
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
/* 329 */         theUnsafe = getUnsafe();
/*     */         
/* 331 */         BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);
/*     */         
/*     */ 
/* 334 */         if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
/* 335 */           throw new AssertionError();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       static final boolean BIG_ENDIAN;
/*     */       
/*     */       static final Unsafe theUnsafe;
/*     */       static final int BYTE_ARRAY_BASE_OFFSET;
/*     */       private static Unsafe getUnsafe()
/*     */       {
/*     */         try
/*     */         {
/* 348 */           return Unsafe.getUnsafe();
/*     */         } catch (SecurityException tryReflectionInstead) {
/*     */           try {
/* 351 */             (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */             {
/*     */               public Unsafe run() throws Exception {
/* 354 */                 Class<Unsafe> k = Unsafe.class;
/* 355 */                 for (Field f : k.getDeclaredFields()) {
/* 356 */                   f.setAccessible(true);
/* 357 */                   Object x = f.get(null);
/* 358 */                   if (k.isInstance(x))
/* 359 */                     return (Unsafe)k.cast(x);
/*     */                 }
/* 361 */                 throw new NoSuchFieldError("the Unsafe");
/*     */               }
/*     */             });
/* 364 */           } catch (PrivilegedActionException e) { throw new RuntimeException("Could not initialize intrinsics", e.getCause());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public int compare(byte[] left, byte[] right) {
/* 370 */         int minLength = Math.min(left.length, right.length);
/* 371 */         int minWords = minLength / 8;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 378 */         for (int i = 0; i < minWords * 8; i += 8) {
/* 379 */           long lw = theUnsafe.getLong(left, BYTE_ARRAY_BASE_OFFSET + i);
/* 380 */           long rw = theUnsafe.getLong(right, BYTE_ARRAY_BASE_OFFSET + i);
/* 381 */           if (lw != rw) {
/* 382 */             if (BIG_ENDIAN) {
/* 383 */               return UnsignedLongs.compare(lw, rw);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 393 */             int n = Long.numberOfTrailingZeros(lw ^ rw) & 0xFFFFFFF8;
/* 394 */             return (int)((lw >>> n & 0xFF) - (rw >>> n & 0xFF));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 399 */         for (int i = minWords * 8; i < minLength; i++) {
/* 400 */           int result = UnsignedBytes.compare(left[i], right[i]);
/* 401 */           if (result != 0) {
/* 402 */             return result;
/*     */           }
/*     */         }
/* 405 */         return left.length - right.length;
/*     */       }
/*     */       
/*     */       private UnsafeComparator() {} }
/*     */     
/* 410 */     static enum PureJavaComparator implements Comparator<byte[]> { INSTANCE;
/*     */       
/*     */       private PureJavaComparator() {}
/* 413 */       public int compare(byte[] left, byte[] right) { int minLength = Math.min(left.length, right.length);
/* 414 */         for (int i = 0; i < minLength; i++) {
/* 415 */           int result = UnsignedBytes.compare(left[i], right[i]);
/* 416 */           if (result != 0) {
/* 417 */             return result;
/*     */           }
/*     */         }
/* 420 */         return left.length - right.length;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     static Comparator<byte[]> getBestComparator()
/*     */     {
/*     */       try
/*     */       {
/* 430 */         Class<?> theClass = Class.forName(UNSAFE_COMPARATOR_NAME);
/*     */         
/*     */ 
/*     */ 
/* 434 */         return (Comparator)theClass.getEnumConstants()[0];
/*     */       }
/*     */       catch (Throwable t) {}
/*     */       
/* 438 */       return UnsignedBytes.lexicographicalComparatorJavaImpl();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\primitives\UnsignedBytes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */