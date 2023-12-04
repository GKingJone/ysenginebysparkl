/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.ByteArrayBlockBuilder;
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
/*     */ public final class BooleanType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*  28 */   public static final BooleanType BOOLEAN = new BooleanType();
/*     */   
/*     */   private BooleanType()
/*     */   {
/*  32 */     super(TypeSignature.parseTypeSignature("boolean"), Boolean.TYPE);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFixedSize()
/*     */   {
/*  38 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/*  44 */     return new ByteArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/*  46 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / 1));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  52 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/*  58 */     return new ByteArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  64 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  70 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  76 */     if (block.isNull(position)) {
/*  77 */       return null;
/*     */     }
/*     */     
/*  80 */     return Boolean.valueOf(block.getByte(position, 0) != 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  86 */     boolean leftValue = leftBlock.getByte(leftPosition, 0) != 0;
/*  87 */     boolean rightValue = rightBlock.getByte(rightPosition, 0) != 0;
/*  88 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  94 */     boolean value = block.getByte(position, 0) != 0;
/*  95 */     return value ? 1231L : 1237L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/* 101 */     boolean leftValue = leftBlock.getByte(leftPosition, 0) != 0;
/* 102 */     boolean rightValue = rightBlock.getByte(rightPosition, 0) != 0;
/* 103 */     return Boolean.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/* 109 */     if (block.isNull(position)) {
/* 110 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 113 */       blockBuilder.writeByte(block.getByte(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getBoolean(Block block, int position)
/*     */   {
/* 120 */     return block.getByte(position, 0) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBoolean(BlockBuilder blockBuilder, boolean value)
/*     */   {
/* 126 */     blockBuilder.writeByte(value ? 1 : 0).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 132 */     return other == BOOLEAN;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 138 */     return getClass().hashCode();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\BooleanType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */