/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*    */ 
/*    */ import java.lang.reflect.ReflectPermission;
/*    */ import java.security.Permission;
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
/*    */ public class UnsafeSliceFactory
/*    */ {
/* 29 */   private static final Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 34 */   private static final UnsafeSliceFactory INSTANCE = new UnsafeSliceFactory();
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
/*    */   public static UnsafeSliceFactory getInstance()
/*    */   {
/* 48 */     SecurityManager sm = System.getSecurityManager();
/* 49 */     if (sm != null) {
/* 50 */       sm.checkPermission(ACCESS_PERMISSION);
/*    */     }
/* 52 */     return INSTANCE;
/*    */   }
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
/*    */   public Slice newSlice(long address, int size)
/*    */   {
/* 67 */     if (address <= 0L) {
/* 68 */       throw new IllegalArgumentException("Invalid address: " + address);
/*    */     }
/* 70 */     if (size == 0) {
/* 71 */       return Slices.EMPTY_SLICE;
/*    */     }
/* 73 */     return new Slice(null, address, size, 0, null);
/*    */   }
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
/*    */   public Slice newSlice(long address, int size, Object reference)
/*    */   {
/* 89 */     if (address <= 0L) {
/* 90 */       throw new IllegalArgumentException("Invalid address: " + address);
/*    */     }
/* 92 */     if (reference == null) {
/* 93 */       throw new NullPointerException("Object reference is null");
/*    */     }
/* 95 */     if (size == 0) {
/* 96 */       return Slices.EMPTY_SLICE;
/*    */     }
/* 98 */     return new Slice(null, address, size, size, reference);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\UnsafeSliceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */