/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceUtf8;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*    */ import java.util.Objects;
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
/*    */ public final class Varchars
/*    */ {
/*    */   public static boolean isVarcharType(Type type)
/*    */   {
/* 28 */     return type instanceof VarcharType;
/*    */   }
/*    */   
/*    */   public static Slice truncateToLength(Slice slice, Type type)
/*    */   {
/* 33 */     Objects.requireNonNull(type, "type is null");
/* 34 */     if (!isVarcharType(type)) {
/* 35 */       throw new IllegalArgumentException("type must be the instance of VarcharType");
/*    */     }
/* 37 */     return truncateToLength(slice, (VarcharType)VarcharType.class.cast(type));
/*    */   }
/*    */   
/*    */   public static Slice truncateToLength(Slice slice, VarcharType varcharType)
/*    */   {
/* 42 */     Objects.requireNonNull(varcharType, "varcharType is null");
/* 43 */     return truncateToLength(slice, varcharType.getLength());
/*    */   }
/*    */   
/*    */   public static Slice truncateToLength(Slice slice, int maxLength)
/*    */   {
/* 48 */     Objects.requireNonNull(slice, "slice is null");
/* 49 */     if (maxLength < 0) {
/* 50 */       throw new IllegalArgumentException("Max length must be greater or equal than zero");
/*    */     }
/* 52 */     if (maxLength == 0) {
/* 53 */       return Slices.EMPTY_SLICE;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 58 */     int sizeInBytes = slice.length();
/* 59 */     if (sizeInBytes <= maxLength) {
/* 60 */       return slice;
/*    */     }
/* 62 */     int indexEnd = SliceUtf8.offsetOfCodePoint(slice, maxLength);
/* 63 */     if (indexEnd < 0) {
/* 64 */       return slice;
/*    */     }
/* 66 */     return slice.slice(0, indexEnd);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\Varchars.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */