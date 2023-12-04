/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceUtf8;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import java.util.Objects;
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
/*     */ public final class Chars
/*     */ {
/*     */   public static boolean isCharType(Type type)
/*     */   {
/*  29 */     return type instanceof CharType;
/*     */   }
/*     */   
/*     */   public static Slice padSpaces(Slice slice, Type type)
/*     */   {
/*  34 */     Objects.requireNonNull(type, "type is null");
/*  35 */     if (!isCharType(type)) {
/*  36 */       throw new IllegalArgumentException("type must be the instance of CharType");
/*     */     }
/*  38 */     return padSpaces(slice, (CharType)CharType.class.cast(type));
/*     */   }
/*     */   
/*     */   public static Slice padSpaces(Slice slice, CharType charType)
/*     */   {
/*  43 */     Objects.requireNonNull(charType, "charType is null");
/*  44 */     return padSpaces(slice, charType.getLength());
/*     */   }
/*     */   
/*     */   public static Slice padSpaces(Slice slice, int length)
/*     */   {
/*  49 */     int textLength = SliceUtf8.countCodePoints(slice);
/*     */     
/*     */ 
/*  52 */     if (textLength > length) {
/*  53 */       throw new IllegalArgumentException("pad length is smaller than slice length");
/*     */     }
/*     */     
/*     */ 
/*  57 */     if (textLength == length) {
/*  58 */       return slice;
/*     */     }
/*     */     
/*     */ 
/*  62 */     int bufferSize = slice.length() + length - textLength;
/*  63 */     Slice buffer = Slices.allocate(bufferSize);
/*     */     
/*     */ 
/*  66 */     buffer.setBytes(0, slice);
/*     */     
/*     */ 
/*  69 */     for (int i = slice.length(); i < bufferSize; i++) {
/*  70 */       buffer.setByte(i, 32);
/*     */     }
/*     */     
/*  73 */     return buffer;
/*     */   }
/*     */   
/*     */   public static Slice trimSpacesAndTruncateToLength(Slice slice, Type type)
/*     */   {
/*  78 */     Objects.requireNonNull(type, "type is null");
/*  79 */     if (!isCharType(type)) {
/*  80 */       throw new IllegalArgumentException("type must be the instance of CharType");
/*     */     }
/*  82 */     return trimSpacesAndTruncateToLength(slice, (CharType)CharType.class.cast(type));
/*     */   }
/*     */   
/*     */   public static Slice trimSpacesAndTruncateToLength(Slice slice, CharType charType)
/*     */   {
/*  87 */     Objects.requireNonNull(charType, "charType is null");
/*  88 */     return trimSpacesAndTruncateToLength(slice, charType.getLength());
/*     */   }
/*     */   
/*     */   public static Slice trimSpacesAndTruncateToLength(Slice slice, int maxLength)
/*     */   {
/*  93 */     Objects.requireNonNull(slice, "slice is null");
/*  94 */     if (maxLength < 0) {
/*  95 */       throw new IllegalArgumentException("Max length must be greater or equal than zero");
/*     */     }
/*  97 */     return Varchars.truncateToLength(trimSpaces(slice), maxLength);
/*     */   }
/*     */   
/*     */   public static Slice trimSpaces(Slice slice)
/*     */   {
/* 102 */     Objects.requireNonNull(slice, "slice is null");
/* 103 */     return slice.slice(0, sliceLengthWithoutTrailingSpaces(slice));
/*     */   }
/*     */   
/*     */   private static int sliceLengthWithoutTrailingSpaces(Slice slice)
/*     */   {
/* 108 */     for (int i = slice.length(); i > 0; i--) {
/* 109 */       if (slice.getByte(i - 1) != 32) {
/* 110 */         return i;
/*     */       }
/*     */     }
/* 113 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\Chars.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */