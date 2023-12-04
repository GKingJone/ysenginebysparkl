/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.FixedWidthBlockBuilder;
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
/*    */ public abstract class AbstractFixedWidthType
/*    */   extends AbstractType
/*    */   implements FixedWidthType
/*    */ {
/*    */   private final int fixedSize;
/*    */   
/*    */   protected AbstractFixedWidthType(TypeSignature signature, Class<?> javaType, int fixedSize)
/*    */   {
/* 30 */     super(signature, javaType);
/* 31 */     this.fixedSize = fixedSize;
/*    */   }
/*    */   
/*    */ 
/*    */   public final int getFixedSize()
/*    */   {
/* 37 */     return this.fixedSize;
/*    */   }
/*    */   
/*    */ 
/*    */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*    */   {
/* 43 */     return new FixedWidthBlockBuilder(
/* 44 */       getFixedSize(), blockBuilderStatus, this.fixedSize == 0 ? expectedEntries : 
/*    */       
/* 46 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / this.fixedSize));
/*    */   }
/*    */   
/*    */ 
/*    */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*    */   {
/* 52 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, this.fixedSize);
/*    */   }
/*    */   
/*    */ 
/*    */   public final BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*    */   {
/* 58 */     return new FixedWidthBlockBuilder(getFixedSize(), positionCount);
/*    */   }
/*    */   
/*    */ 
/*    */   public final Slice getSlice(Block block, int position)
/*    */   {
/* 64 */     return block.getSlice(position, 0, getFixedSize());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\AbstractFixedWidthType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */