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
/*     */ public abstract class AbstractFixedWidthBlock
/*     */   implements Block
/*     */ {
/*     */   protected final int fixedSize;
/*     */   
/*     */   protected AbstractFixedWidthBlock(int fixedSize)
/*     */   {
/*  27 */     if (fixedSize < 0) {
/*  28 */       throw new IllegalArgumentException("fixedSize is negative");
/*     */     }
/*  30 */     this.fixedSize = fixedSize;
/*     */   }
/*     */   
/*     */   protected abstract Slice getRawSlice();
/*     */   
/*     */   protected abstract boolean isEntryNull(int paramInt);
/*     */   
/*     */   public int getFixedSize()
/*     */   {
/*  39 */     return this.fixedSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/*  45 */     return this.fixedSize;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  51 */     checkReadablePosition(position);
/*  52 */     return getRawSlice().getByte(valueOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  58 */     checkReadablePosition(position);
/*  59 */     return getRawSlice().getShort(valueOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  65 */     checkReadablePosition(position);
/*  66 */     return getRawSlice().getInt(valueOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  72 */     checkReadablePosition(position);
/*  73 */     return getRawSlice().getLong(valueOffset(position) + offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/*  79 */     checkReadablePosition(position);
/*  80 */     return getRawSlice().slice(valueOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/*  86 */     checkReadablePosition(position);
/*  87 */     if (this.fixedSize < length) {
/*  88 */       return false;
/*     */     }
/*  90 */     int thisOffset = valueOffset(position) + offset;
/*  91 */     return otherBlock.bytesEqual(otherPosition, otherOffset, getRawSlice(), thisOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/*  97 */     checkReadablePosition(position);
/*  98 */     int thisOffset = valueOffset(position) + offset;
/*  99 */     return getRawSlice().equals(thisOffset, length, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 105 */     checkReadablePosition(position);
/* 106 */     if (isNull(position)) {
/* 107 */       return 0L;
/*     */     }
/*     */     
/* 110 */     return XxHash64.hash(getRawSlice(), valueOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int position, int offset, int length, Block otherBlock, int otherPosition, int otherOffset, int otherLength)
/*     */   {
/* 116 */     checkReadablePosition(position);
/* 117 */     if (this.fixedSize < length) {
/* 118 */       throw new IllegalArgumentException("Length longer than value length");
/*     */     }
/* 120 */     int thisOffset = valueOffset(position) + offset;
/* 121 */     return -otherBlock.bytesCompare(otherPosition, otherOffset, otherLength, getRawSlice(), thisOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/* 127 */     checkReadablePosition(position);
/* 128 */     return getRawSlice().compareTo(valueOffset(position) + offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 134 */     checkReadablePosition(position);
/* 135 */     blockBuilder.writeBytes(getRawSlice(), valueOffset(position) + offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 141 */     writeBytesTo(position, 0, getLength(position), blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 147 */     return new FixedWidthBlockEncoding(this.fixedSize);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 153 */     checkReadablePosition(position);
/*     */     
/* 155 */     Slice copy = Slices.copyOf(getRawSlice(), valueOffset(position), this.fixedSize);
/*     */     
/* 157 */     return new FixedWidthBlock(this.fixedSize, 1, copy, Slices.wrappedBooleanArray(new boolean[] { isNull(position) }));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 163 */     checkReadablePosition(position);
/* 164 */     return isEntryNull(position);
/*     */   }
/*     */   
/*     */   private int valueOffset(int position)
/*     */   {
/* 169 */     return position * this.fixedSize;
/*     */   }
/*     */   
/*     */   protected void checkReadablePosition(int position)
/*     */   {
/* 174 */     if ((position < 0) || (position >= getPositionCount())) {
/* 175 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\AbstractFixedWidthBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */