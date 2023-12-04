/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import java.util.List;
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
/*     */ public abstract class AbstractArrayElementBlock
/*     */   implements Block
/*     */ {
/*     */   protected final int start;
/*     */   
/*     */   protected AbstractArrayElementBlock(int start)
/*     */   {
/*  27 */     this.start = start;
/*     */   }
/*     */   
/*     */   protected abstract BlockBuilder getBlock();
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/*  34 */     if ((position < 0) || (position >= getPositionCount())) {
/*  35 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/*  42 */     checkReadablePosition(position);
/*  43 */     return getBlock().getLength(position + this.start);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  49 */     checkReadablePosition(position);
/*  50 */     return getBlock().getByte(position + this.start, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  56 */     checkReadablePosition(position);
/*  57 */     return getBlock().getShort(position + this.start, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  63 */     checkReadablePosition(position);
/*  64 */     return getBlock().getInt(position + this.start, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  70 */     checkReadablePosition(position);
/*  71 */     return getBlock().getLong(position + this.start, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/*  77 */     checkReadablePosition(position);
/*  78 */     return getBlock().getSlice(position + this.start, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/*  84 */     checkReadablePosition(position);
/*  85 */     return (T)getBlock().getObject(position + this.start, clazz);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/*  91 */     checkReadablePosition(position);
/*  92 */     return getBlock().bytesEqual(position + this.start, offset, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/*  98 */     checkReadablePosition(position);
/*  99 */     return getBlock().bytesCompare(position + this.start, offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 105 */     checkReadablePosition(position);
/* 106 */     getBlock().writeBytesTo(position + this.start, offset, length, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 112 */     checkReadablePosition(position);
/* 113 */     getBlock().writePositionTo(position + this.start, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/* 119 */     checkReadablePosition(position);
/* 120 */     return getBlock().equals(position + this.start, offset, otherBlock, otherPosition, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 126 */     checkReadablePosition(position);
/* 127 */     return getBlock().hash(position + this.start, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int leftPosition, int leftOffset, int leftLength, Block rightBlock, int rightPosition, int rightOffset, int rightLength)
/*     */   {
/* 133 */     checkReadablePosition(leftPosition);
/* 134 */     return getBlock().compareTo(leftPosition + this.start, leftOffset, leftLength, rightBlock, rightPosition, rightOffset, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 140 */     checkReadablePosition(position);
/* 141 */     return getBlock().getSingleValueBlock(position + this.start);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 147 */     checkReadablePosition(position);
/* 148 */     return getBlock().isNull(position + this.start);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 155 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 161 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int position, int length)
/*     */   {
/* 167 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int position, int length)
/*     */   {
/* 173 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\AbstractArrayElementBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */