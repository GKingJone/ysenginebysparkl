/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.XxHash64;
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
/*     */ public abstract class AbstractVariableWidthBlock
/*     */   implements Block
/*     */ {
/*     */   protected abstract Slice getRawSlice(int paramInt);
/*     */   
/*     */   protected abstract int getPositionOffset(int paramInt);
/*     */   
/*     */   protected abstract boolean isEntryNull(int paramInt);
/*     */   
/*     */   public BlockEncoding getEncoding()
/*     */   {
/*  34 */     return new VariableWidthBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  40 */     checkReadablePosition(position);
/*  41 */     return getRawSlice(position).getByte(getPositionOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  47 */     checkReadablePosition(position);
/*  48 */     return getRawSlice(position).getShort(getPositionOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  54 */     checkReadablePosition(position);
/*  55 */     return getRawSlice(position).getInt(getPositionOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  61 */     checkReadablePosition(position);
/*  62 */     return getRawSlice(position).getLong(getPositionOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/*  68 */     checkReadablePosition(position);
/*  69 */     return getRawSlice(position).slice(getPositionOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/*  75 */     checkReadablePosition(position);
/*  76 */     Slice rawSlice = getRawSlice(position);
/*  77 */     if (getLength(position) < length) {
/*  78 */       return false;
/*     */     }
/*  80 */     return otherBlock.bytesEqual(otherPosition, otherOffset, rawSlice, getPositionOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/*  86 */     checkReadablePosition(position);
/*  87 */     return getRawSlice(position).equals(getPositionOffset(position) + offset, length, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/*  93 */     checkReadablePosition(position);
/*  94 */     return XxHash64.hash(getRawSlice(position), getPositionOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int position, int offset, int length, Block otherBlock, int otherPosition, int otherOffset, int otherLength)
/*     */   {
/* 100 */     checkReadablePosition(position);
/* 101 */     Slice rawSlice = getRawSlice(position);
/* 102 */     if (getLength(position) < length) {
/* 103 */       throw new IllegalArgumentException("Length longer than value length");
/*     */     }
/* 105 */     return -otherBlock.bytesCompare(otherPosition, otherOffset, otherLength, rawSlice, getPositionOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/* 111 */     checkReadablePosition(position);
/* 112 */     return getRawSlice(position).compareTo(getPositionOffset(position) + offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 118 */     checkReadablePosition(position);
/* 119 */     blockBuilder.writeBytes(getRawSlice(position), getPositionOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 125 */     writeBytesTo(position, 0, getLength(position), blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 131 */     if (isNull(position)) {
/* 132 */       return new VariableWidthBlock(1, Slices.EMPTY_SLICE, new int[] { 0, 0 }, new boolean[] { true });
/*     */     }
/*     */     
/* 135 */     int offset = getPositionOffset(position);
/* 136 */     int entrySize = getLength(position);
/*     */     
/* 138 */     Slice copy = Slices.copyOf(getRawSlice(position), offset, entrySize);
/*     */     
/* 140 */     return new VariableWidthBlock(1, copy, new int[] { 0, copy.length() }, new boolean[] { false });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 146 */     checkReadablePosition(position);
/* 147 */     return isEntryNull(position);
/*     */   }
/*     */   
/*     */   protected void checkReadablePosition(int position)
/*     */   {
/* 152 */     if ((position < 0) || (position >= getPositionCount())) {
/* 153 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\AbstractVariableWidthBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */