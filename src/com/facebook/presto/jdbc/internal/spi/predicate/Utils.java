/*    */ package com.facebook.presto.jdbc.internal.spi.predicate;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TypeUtils;
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
/*    */ public final class Utils
/*    */ {
/*    */   public static Block nativeValueToBlock(Type type, Object object)
/*    */   {
/* 32 */     if ((object != null) && (!Primitives.wrap(type.getJavaType()).isInstance(object))) {
/* 33 */       throw new IllegalArgumentException(String.format("Object '%s' does not match type %s", new Object[] { object, type.getJavaType() }));
/*    */     }
/* 35 */     BlockBuilder blockBuilder = type.createBlockBuilder(new BlockBuilderStatus(), 1);
/* 36 */     TypeUtils.writeNativeValue(type, blockBuilder, object);
/* 37 */     return blockBuilder.build();
/*    */   }
/*    */   
/*    */   static Object blockToNativeValue(Type type, Block block)
/*    */   {
/* 42 */     return TypeUtils.readNativeValue(type, block, 0);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */