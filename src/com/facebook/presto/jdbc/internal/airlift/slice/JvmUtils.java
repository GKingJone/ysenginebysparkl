/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*    */ 
/*    */ import java.lang.invoke.MethodHandle;
/*    */ import java.lang.invoke.MethodHandles;
/*    */ import java.lang.invoke.MethodHandles.Lookup;
/*    */ import java.lang.invoke.MethodType;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Field;
/*    */ import java.nio.Buffer;
/*    */ import java.nio.ByteBuffer;
/*    */ import sun.misc.Unsafe;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class JvmUtils
/*    */ {
/*    */   static final Unsafe unsafe;
/*    */   static final MethodHandle newByteBuffer;
/*    */   private static final Field ADDRESS_ACCESSOR;
/*    */   
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 44 */       Field field = Unsafe.class.getDeclaredField("theUnsafe");
/* 45 */       field.setAccessible(true);
/* 46 */       unsafe = (Unsafe)field.get(null);
/* 47 */       if (unsafe == null) {
/* 48 */         throw new RuntimeException("Unsafe access not available");
/*    */       }
/*    */       
/*    */ 
/* 52 */       assertArrayIndexScale("Boolean", Unsafe.ARRAY_BOOLEAN_INDEX_SCALE, 1);
/* 53 */       assertArrayIndexScale("Byte", Unsafe.ARRAY_BYTE_INDEX_SCALE, 1);
/* 54 */       assertArrayIndexScale("Short", Unsafe.ARRAY_SHORT_INDEX_SCALE, 2);
/* 55 */       assertArrayIndexScale("Int", Unsafe.ARRAY_INT_INDEX_SCALE, 4);
/* 56 */       assertArrayIndexScale("Long", Unsafe.ARRAY_LONG_INDEX_SCALE, 8);
/* 57 */       assertArrayIndexScale("Float", Unsafe.ARRAY_FLOAT_INDEX_SCALE, 4);
/* 58 */       assertArrayIndexScale("Double", Unsafe.ARRAY_DOUBLE_INDEX_SCALE, 8);
/*    */       
/*    */ 
/* 61 */       Class<?> directByteBufferClass = ClassLoader.getSystemClassLoader().loadClass("java.nio.DirectByteBuffer");
/* 62 */       Constructor<?> constructor = directByteBufferClass.getDeclaredConstructor(new Class[] { Long.TYPE, Integer.TYPE, Object.class });
/* 63 */       constructor.setAccessible(true);
/*    */       
/* 65 */       newByteBuffer = MethodHandles.lookup().unreflectConstructor(constructor).asType(MethodType.methodType(ByteBuffer.class, Long.TYPE, new Class[] { Integer.TYPE, Object.class }));
/*    */       
/* 67 */       ADDRESS_ACCESSOR = Buffer.class.getDeclaredField("address");
/* 68 */       ADDRESS_ACCESSOR.setAccessible(true);
/*    */     }
/*    */     catch (Exception e) {
/* 71 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   private static void assertArrayIndexScale(String name, int actualIndexScale, int expectedIndexScale)
/*    */   {
/* 77 */     if (actualIndexScale != expectedIndexScale) {
/* 78 */       throw new IllegalStateException(name + " array index scale must be " + expectedIndexScale + ", but is " + actualIndexScale);
/*    */     }
/*    */   }
/*    */   
/*    */   public static long getAddress(Buffer buffer)
/*    */   {
/*    */     try {
/* 85 */       return ((Long)ADDRESS_ACCESSOR.get(buffer)).longValue();
/*    */     }
/*    */     catch (IllegalAccessException e) {
/* 88 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\JvmUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */