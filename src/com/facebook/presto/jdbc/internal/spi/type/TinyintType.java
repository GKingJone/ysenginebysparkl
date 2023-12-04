/*     */ package com.facebook.presto.jdbc.internal.spi.type;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*     */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
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
/*     */ 
/*     */ public final class TinyintType
/*     */   extends AbstractType
/*     */   implements FixedWidthType
/*     */ {
/*  31 */   public static final TinyintType TINYINT = new TinyintType();
/*     */   
/*     */   private TinyintType()
/*     */   {
/*  35 */     super(TypeSignature.parseTypeSignature("tinyint"), Long.TYPE);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFixedSize()
/*     */   {
/*  41 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/*  47 */     return new ByteArrayBlockBuilder(blockBuilderStatus, 
/*     */     
/*  49 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / 1));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  55 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder createFixedSizeBlockBuilder(int positionCount)
/*     */   {
/*  61 */     return new ByteArrayBlockBuilder(new BlockBuilderStatus(), positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isComparable()
/*     */   {
/*  67 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOrderable()
/*     */   {
/*  73 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*     */   {
/*  79 */     if (block.isNull(position)) {
/*  80 */       return null;
/*     */     }
/*     */     
/*  83 */     return Byte.valueOf(block.getByte(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/*  89 */     int leftValue = leftBlock.getByte(leftPosition, 0);
/*  90 */     int rightValue = rightBlock.getByte(rightPosition, 0);
/*  91 */     return leftValue == rightValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(Block block, int position)
/*     */   {
/*  97 */     return hash(block.getByte(position, 0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*     */   {
/* 105 */     byte leftValue = leftBlock.getByte(leftPosition, 0);
/* 106 */     byte rightValue = rightBlock.getByte(rightPosition, 0);
/* 107 */     return Byte.compare(leftValue, rightValue);
/*     */   }
/*     */   
/*     */ 
/*     */   public void appendTo(Block block, int position, BlockBuilder blockBuilder)
/*     */   {
/* 113 */     if (block.isNull(position)) {
/* 114 */       blockBuilder.appendNull();
/*     */     }
/*     */     else {
/* 117 */       blockBuilder.writeByte(block.getByte(position, 0)).closeEntry();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(Block block, int position)
/*     */   {
/* 124 */     return block.getByte(position, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(BlockBuilder blockBuilder, long value)
/*     */   {
/* 130 */     if (value > 127L) {
/* 131 */       throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, String.format("Value %d exceeds MAX_BYTE", new Object[] { Long.valueOf(value) }));
/*     */     }
/* 133 */     if (value < -128L) {
/* 134 */       throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, String.format("Value %d is less than MIN_BYTE", new Object[] { Long.valueOf(value) }));
/*     */     }
/*     */     
/* 137 */     blockBuilder.writeByte((int)value).closeEntry();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 143 */     return other == TINYINT;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 149 */     return getClass().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public static long hash(byte value)
/*     */   {
/* 155 */     return Long.rotateLeft(value * -4417276706812531889L, 31) * -7046029288634856825L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TinyintType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */