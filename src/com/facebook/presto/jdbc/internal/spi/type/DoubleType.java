/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
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
/*     */ 
/*     */ 
/*     */ public final class DoubleType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*  30 */   public static final DoubleType DOUBLE = new DoubleType();
/*     */   
/*     */   private DoubleType()
/*     */   {
/*  34 */     super(TypeSignature.parseTypeSignature("double"), Double.TYPE);
/*     */   }
/*     */   
/*     */ 
/*     */   public final int getFixedSize()
/*     */   {
/*  40 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  46 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  52 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  58 */     if (block.isNull(position)) {
/*  59 */       return null;
/*     */     }
/*  61 */     return Double.valueOf(Double.longBitsToDouble(block.getLong(position, 0)));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  67 */     double leftValue = Double.longBitsToDouble(leftBlock.getLong(leftPosition, 0));
/*  68 */     double rightValue = Double.longBitsToDouble(rightBlock.getLong(rightPosition, 0));
/*     */     
/*     */ 
/*     */ 
/*  72 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  78 */     return AbstractLongType.hash(block.getLong(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  84 */     double leftValue = Double.longBitsToDouble(leftBlock.getLong(leftPosition, 0));
/*  85 */     double rightValue = Double.longBitsToDouble(rightBlock.getLong(rightPosition, 0));
/*  86 */     return Double.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/*  92 */     if (block.isNull(position)) {
/*  93 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/*  96 */       blockBuilder.writeLong(block.getLong(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public double getDouble(Block block, int position)
/*     */   {
/* 103 */     return Double.longBitsToDouble(block.getLong(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeDouble(BlockBuilder blockBuilder, double value)
/*     */   {
/* 109 */     blockBuilder.writeLong(Double.doubleToLongBits(value)).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/* 115 */     return new LongArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/* 117 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / 8));
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/* 123 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 8);
/*     */   }
/*     */   
/*     */ 
/*     */   public final BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/* 129 */     return new LongArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 136 */     return other == DOUBLE;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 142 */     return getClass().hashCode();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\DoubleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */