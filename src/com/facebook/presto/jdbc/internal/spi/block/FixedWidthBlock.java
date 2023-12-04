/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class FixedWidthBlock
/*     */   extends AbstractFixedWidthBlock
/*     */ {
/*  30 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(FixedWidthBlock.class).instanceSize();
/*     */   
/*     */   private final int positionCount;
/*     */   private final Slice slice;
/*     */   private final Slice valueIsNull;
/*     */   
/*     */   public FixedWidthBlock(int fixedSize, int positionCount, Slice slice, Slice valueIsNull)
/*     */   {
/*  38 */     super(fixedSize);
/*     */     
/*  40 */     if (positionCount < 0) {
/*  41 */       throw new IllegalArgumentException("positionCount is negative");
/*     */     }
/*  43 */     this.positionCount = positionCount;
/*     */     
/*  45 */     this.slice = ((Slice)Objects.requireNonNull(slice, "slice is null"));
/*  46 */     if (slice.length() < fixedSize * positionCount) {
/*  47 */       throw new IllegalArgumentException("slice length is less n positionCount * fixedSize");
/*     */     }
/*     */     
/*  50 */     if (valueIsNull.length() < positionCount) {
/*  51 */       throw new IllegalArgumentException("valueIsNull length is less than positionCount");
/*     */     }
/*  53 */     this.valueIsNull = valueIsNull;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Slice getRawSlice()
/*     */   {
/*  59 */     return this.slice;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isEntryNull(int position)
/*     */   {
/*  65 */     return this.valueIsNull.getByte(position) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  71 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  77 */     return BlockUtil.intSaturatedCast(getRawSlice().length() + this.valueIsNull.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  83 */     return BlockUtil.intSaturatedCast(INSTANCE_SIZE + getRawSlice().getRetainedSize() + this.valueIsNull.getRetainedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/*  89 */     BlockUtil.checkValidPositions(positions, this.positionCount);
/*     */     
/*  91 */     SliceOutput newSlice = Slices.allocate(positions.size() * this.fixedSize).getOutput();
/*  92 */     SliceOutput newValueIsNull = Slices.allocate(positions.size()).getOutput();
/*     */     
/*  94 */     for (Iterator localIterator = positions.iterator(); localIterator.hasNext();) { int position = ((Integer)localIterator.next()).intValue();
/*  95 */       newSlice.writeBytes(this.slice, position * this.fixedSize, this.fixedSize);
/*  96 */       newValueIsNull.writeByte(this.valueIsNull.getByte(position));
/*     */     }
/*  98 */     return new FixedWidthBlock(this.fixedSize, positions.size(), newSlice.slice(), newValueIsNull.slice());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 104 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > this.positionCount)) {
/* 105 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + this.positionCount + " positions");
/*     */     }
/*     */     
/* 108 */     Slice newSlice = this.slice.slice(positionOffset * this.fixedSize, length * this.fixedSize);
/* 109 */     Slice newValueIsNull = this.valueIsNull.slice(positionOffset, length);
/* 110 */     return new FixedWidthBlock(this.fixedSize, length, newSlice, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 116 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > this.positionCount)) {
/* 117 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + this.positionCount + " positions");
/*     */     }
/*     */     
/* 120 */     Slice newSlice = Slices.copyOf(this.slice, positionOffset * this.fixedSize, length * this.fixedSize);
/* 121 */     Slice newValueIsNull = Slices.copyOf(this.valueIsNull, positionOffset, length);
/* 122 */     return new FixedWidthBlock(this.fixedSize, length, newSlice, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 128 */     StringBuilder sb = new StringBuilder("FixedWidthBlock{");
/* 129 */     sb.append("positionCount=").append(this.positionCount);
/* 130 */     sb.append(", fixedSize=").append(this.fixedSize);
/* 131 */     sb.append(", slice=").append(this.slice);
/* 132 */     sb.append('}');
/* 133 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\FixedWidthBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */