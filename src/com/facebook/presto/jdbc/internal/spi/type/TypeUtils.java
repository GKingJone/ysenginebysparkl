/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
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
/*    */ public final class TypeUtils
/*    */ {
/*    */   public static Object readNativeValue(Type type, Block block, int position)
/*    */   {
/* 32 */     Class<?> javaType = type.getJavaType();
/*    */     
/* 34 */     if (block.isNull(position)) {
/* 35 */       return null;
/*    */     }
/* 37 */     if (javaType == Long.TYPE) {
/* 38 */       return Long.valueOf(type.getLong(block, position));
/*    */     }
/* 40 */     if (javaType == Double.TYPE) {
/* 41 */       return Double.valueOf(type.getDouble(block, position));
/*    */     }
/* 43 */     if (javaType == Boolean.TYPE) {
/* 44 */       return Boolean.valueOf(type.getBoolean(block, position));
/*    */     }
/* 46 */     if (javaType == Slice.class) {
/* 47 */       return type.getSlice(block, position);
/*    */     }
/* 49 */     return type.getObject(block, position);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void writeNativeValue(Type type, BlockBuilder blockBuilder, Object value)
/*    */   {
/* 57 */     if (value == null) {
/* 58 */       blockBuilder.appendNull();
/*    */     }
/* 60 */     else if (type.getJavaType() == Boolean.TYPE) {
/* 61 */       type.writeBoolean(blockBuilder, ((Boolean)value).booleanValue());
/*    */     }
/* 63 */     else if (type.getJavaType() == Double.TYPE) {
/* 64 */       type.writeDouble(blockBuilder, ((Number)value).doubleValue());
/*    */     }
/* 66 */     else if (type.getJavaType() == Long.TYPE) {
/* 67 */       type.writeLong(blockBuilder, ((Number)value).longValue());
/*    */     }
/* 69 */     else if (type.getJavaType() == Slice.class) { Slice slice;
/*    */       Slice slice;
/* 71 */       if ((value instanceof byte[])) {
/* 72 */         slice = Slices.wrappedBuffer((byte[])value);
/*    */       } else { Slice slice;
/* 74 */         if ((value instanceof String)) {
/* 75 */           slice = Slices.utf8Slice((String)value);
/*    */         }
/*    */         else
/* 78 */           slice = (Slice)value;
/*    */       }
/* 80 */       type.writeSlice(blockBuilder, slice, 0, slice.length());
/*    */     }
/*    */     else {
/* 83 */       type.writeObject(blockBuilder, value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TypeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */