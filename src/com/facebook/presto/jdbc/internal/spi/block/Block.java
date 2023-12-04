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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface Block
/*     */ {
/*     */   public abstract int getLength(int paramInt);
/*     */   
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  32 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  40 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  48 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  56 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/*  64 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/*  72 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/*  82 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/*  92 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 102 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void writePositionTo(int paramInt, BlockBuilder paramBlockBuilder);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/* 118 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 128 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(int leftPosition, int leftOffset, int leftLength, Block rightBlock, int rightPosition, int rightOffset, int rightLength)
/*     */   {
/* 139 */     throw new UnsupportedOperationException(getClass().getName());
/*     */   }
/*     */   
/*     */   public abstract Block getSingleValueBlock(int paramInt);
/*     */   
/*     */   public abstract int getPositionCount();
/*     */   
/*     */   public abstract int getSizeInBytes();
/*     */   
/*     */   public abstract int getRetainedSizeInBytes();
/*     */   
/*     */   public abstract BlockEncoding getEncoding();
/*     */   
/*     */   public abstract Block copyPositions(List<Integer> paramList);
/*     */   
/*     */   public abstract Block getRegion(int paramInt1, int paramInt2);
/*     */   
/*     */   public abstract Block copyRegion(int paramInt1, int paramInt2);
/*     */   
/*     */   public abstract boolean isNull(int paramInt);
/*     */   
/*     */   public void assureLoaded() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\Block.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */