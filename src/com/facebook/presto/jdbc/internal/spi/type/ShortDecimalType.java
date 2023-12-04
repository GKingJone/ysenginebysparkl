/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.LongArrayBlockBuilder;
/*     */ import java.math.BigInteger;
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
/*     */ 
/*     */ final class ShortDecimalType
/*     */   extends DecimalType
/*     */ {
/*     */   ShortDecimalType(int precision, int scale)
/*     */   {
/*  33 */     super(precision, scale, Long.TYPE);
/*  34 */     validatePrecisionScale(precision, scale, 17);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFixedSize()
/*     */   {
/*  40 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/*  46 */     return new LongArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/*  48 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / getFixedSize()));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  54 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/*  60 */     return new LongArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  66 */     if (block.isNull(position)) {
/*  67 */       return null;
/*     */     }
/*  69 */     long unscaledValue = block.getLong(position, 0);
/*  70 */     return new SqlDecimal(BigInteger.valueOf(unscaledValue), getPrecision(), getScale());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  76 */     long leftValue = leftBlock.getLong(leftPosition, 0);
/*  77 */     long rightValue = rightBlock.getLong(rightPosition, 0);
/*  78 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  84 */     return block.getLong(position, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  90 */     long leftValue = leftBlock.getLong(leftPosition, 0);
/*  91 */     long rightValue = rightBlock.getLong(rightPosition, 0);
/*  92 */     return Long.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/*  98 */     if (block.isNull(position)) {
/*  99 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 102 */       blockBuilder.writeLong(block.getLong(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(Block block, int position)
/*     */   {
/* 109 */     return block.getLong(position, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(BlockBuilder blockBuilder, long value)
/*     */   {
/* 115 */     blockBuilder.writeLong(value).closeEntry();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\ShortDecimalType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */