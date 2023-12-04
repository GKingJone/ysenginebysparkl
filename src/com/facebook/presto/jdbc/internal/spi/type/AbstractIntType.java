/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.IntArrayBlockBuilder;
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
/*     */ public abstract class AbstractIntType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*     */   protected AbstractIntType(TypeSignature signature)
/*     */   {
/*  30 */     super(signature, Long.TYPE);
/*     */   }
/*     */   
/*     */ 
/*     */   public final int getFixedSize()
/*     */   {
/*  36 */     return 4;
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
/*  54 */     return block.getInt(position, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public final Slice getSlice(Block block, int position)
/*     */   {
/*  60 */     return block.getSlice(position, 0, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(BlockBuilder blockBuilder, long value)
/*     */   {
/*  66 */     blockBuilder.writeInt((int)value).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/*  72 */     if (block.isNull(position)) {
/*  73 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/*  76 */       blockBuilder.writeInt(block.getInt(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  83 */     int leftValue = leftBlock.getInt(leftPosition, 0);
/*  84 */     int rightValue = rightBlock.getInt(rightPosition, 0);
/*  85 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  91 */     return hash(block.getInt(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  99 */     int leftValue = leftBlock.getInt(leftPosition, 0);
/* 100 */     int rightValue = rightBlock.getInt(rightPosition, 0);
/* 101 */     return Integer.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/* 107 */     return new IntArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/* 109 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / 4));
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/* 115 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 4);
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/* 121 */     return new IntArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public static long hash(int value)
/*     */   {
/* 127 */     return Long.rotateLeft(value * -4417276706812531889L, 31) * -7046029288634856825L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\AbstractIntType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */