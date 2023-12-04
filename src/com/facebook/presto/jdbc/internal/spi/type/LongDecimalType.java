/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.FixedWidthBlockBuilder;
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
/*     */ final class LongDecimalType
/*     */   extends DecimalType
/*     */ {
/*     */   private static final int SIGN_BIT_MASK = 128;
/*     */   
/*     */   LongDecimalType(int precision, int scale)
/*     */   {
/*  35 */     super(precision, scale, Slice.class);
/*  36 */     validatePrecisionScale(precision, scale, 38);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFixedSize()
/*     */   {
/*  42 */     return 16;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/*  48 */     return new FixedWidthBlockBuilder(
/*  49 */       getFixedSize(), blockBuilderStatus, 
/*     */       
/*  51 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / getFixedSize()));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  57 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/*  63 */     return new FixedWidthBlockBuilder(getFixedSize(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  69 */     if (block.isNull(position)) {
/*  70 */       return null;
/*     */     }
/*  72 */     Slice slice = block.getSlice(position, 0, getFixedSize());
/*  73 */     return new SqlDecimal(Decimals.decodeUnscaledValue(slice), getPrecision(), getScale());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  79 */     return leftBlock.equals(leftPosition, 0, rightBlock, rightPosition, 0, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  85 */     return block.hash(position, 0, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  91 */     byte left = leftBlock.getByte(leftPosition, 0);
/*  92 */     byte right = rightBlock.getByte(rightPosition, 0);
/*  93 */     if ((left & 0x80) != (right & 0x80))
/*     */     {
/*  95 */       return Byte.compare(left, right);
/*     */     }
/*     */     
/*  98 */     return leftBlock.compareTo(leftPosition, 0, getFixedSize(), rightBlock, rightPosition, 0, getFixedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/* 104 */     if (block.isNull(position)) {
/* 105 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 108 */       block.writeBytesTo(position, 0, getFixedSize(), blockBuilder);
/* 109 */       blockBuilder.closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value)
/*     */   {
/* 116 */     writeSlice(blockBuilder, value, 0, value.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeSlice(BlockBuilder blockBuilder, Slice value, int offset, int length)
/*     */   {
/* 122 */     blockBuilder.writeBytes(value, offset, length).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(Block block, int position)
/*     */   {
/* 128 */     return block.getSlice(position, 0, getFixedSize());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\LongDecimalType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */