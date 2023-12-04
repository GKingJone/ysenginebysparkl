/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.LongArrayBlockBuilder;
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
/*     */ public abstract class AbstractLongType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*     */   public AbstractLongType(TypeSignature signature)
/*     */   {
/*  30 */     super(signature, Long.TYPE);
/*     */   }
/*     */   
/*     */ 
/*     */   public final int getFixedSize()
/*     */   {
/*  36 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  42 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  48 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public final long getLong(Block block, int position)
/*     */   {
/*  54 */     return block.getLong(position, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public final Slice getSlice(Block block, int position)
/*     */   {
/*  60 */     return block.getSlice(position, 0, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public final void writeLong(BlockBuilder blockBuilder, long value)
/*     */   {
/*  66 */     blockBuilder.writeLong(value).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/*  72 */     if (block.isNull(position)) {
/*  73 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/*  76 */       blockBuilder.writeLong(block.getLong(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  83 */     long leftValue = leftBlock.getLong(leftPosition, 0);
/*  84 */     long rightValue = rightBlock.getLong(rightPosition, 0);
/*  85 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  91 */     return hash(block.getLong(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  97 */     long leftValue = leftBlock.getLong(leftPosition, 0);
/*  98 */     long rightValue = rightBlock.getLong(rightPosition, 0);
/*  99 */     return Long.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/* 105 */     return new LongArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/* 107 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / 8));
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/* 113 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 8);
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/* 119 */     return new LongArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public static long hash(long value)
/*     */   {
/* 125 */     return Long.rotateLeft(value * -4417276706812531889L, 31) * -7046029288634856825L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\AbstractLongType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */