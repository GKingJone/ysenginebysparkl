/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*    */ 
/*    */ import javax.annotation.Nullable;
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
/*    */ 
/*    */ final class Preconditions
/*    */ {
/*    */   public static void checkArgument(boolean expression, String errorMessage)
/*    */   {
/* 31 */     if (!expression) {
/* 32 */       throw new IllegalArgumentException(errorMessage);
/*    */     }
/*    */   }
/*    */   
/*    */   public static int checkPositionIndex(int index, int size)
/*    */   {
/* 38 */     return checkPositionIndex(index, size, "index");
/*    */   }
/*    */   
/*    */ 
/*    */   public static int checkPositionIndex(int index, int size, @Nullable String desc)
/*    */   {
/* 44 */     if ((index < 0) || (index > size)) {
/* 45 */       throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
/*    */     }
/* 47 */     return index;
/*    */   }
/*    */   
/*    */   public static long checkPositionIndex(long index, long size)
/*    */   {
/* 52 */     return checkPositionIndex(index, size, "index");
/*    */   }
/*    */   
/*    */ 
/*    */   public static long checkPositionIndex(long index, long size, @Nullable String desc)
/*    */   {
/* 58 */     if ((index < 0L) || (index > size)) {
/* 59 */       throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
/*    */     }
/* 61 */     return index;
/*    */   }
/*    */   
/*    */   private static String badPositionIndex(long index, long size, String desc)
/*    */   {
/* 66 */     if (index < 0L) {
/* 67 */       return String.format("%s (%s) must not be negative", new Object[] { desc, Long.valueOf(index) });
/*    */     }
/* 69 */     if (size < 0L) {
/* 70 */       throw new IllegalArgumentException("negative size: " + size);
/*    */     }
/*    */     
/* 73 */     return String.format("%s (%s) must not be greater than size (%s)", new Object[] { desc, Long.valueOf(index), Long.valueOf(size) });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void checkPositionIndexes(int start, int end, int size)
/*    */   {
/* 80 */     if ((start < 0) || (end < start) || (end > size)) {
/* 81 */       throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
/*    */     }
/*    */   }
/*    */   
/*    */   private static String badPositionIndexes(int start, int end, int size)
/*    */   {
/* 87 */     if ((start < 0) || (start > size)) {
/* 88 */       return badPositionIndex(start, size, "start index");
/*    */     }
/* 90 */     if ((end < 0) || (end > size)) {
/* 91 */       return badPositionIndex(end, size, "end index");
/*    */     }
/*    */     
/* 94 */     return String.format("end index (%s) must not be less than start index (%s)", new Object[] { Integer.valueOf(end), Integer.valueOf(start) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Preconditions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */