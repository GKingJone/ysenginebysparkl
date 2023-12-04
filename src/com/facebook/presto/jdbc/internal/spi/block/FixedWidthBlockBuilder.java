/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.DynamicSliceOutput;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FixedWidthBlockBuilder
/*     */   extends AbstractFixedWidthBlock
/*     */   implements BlockBuilder
/*     */ {
/*  37 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(FixedWidthBlockBuilder.class).instanceSize() + BlockBuilderStatus.INSTANCE_SIZE;
/*     */   
/*     */   private BlockBuilderStatus blockBuilderStatus;
/*     */   
/*     */   private SliceOutput sliceOutput;
/*     */   private SliceOutput valueIsNull;
/*     */   private int positionCount;
/*     */   private int currentEntrySize;
/*     */   
/*     */   public FixedWidthBlockBuilder(int fixedSize, BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  48 */     super(fixedSize);
/*     */     
/*  50 */     this.blockBuilderStatus = blockBuilderStatus;
/*  51 */     this.sliceOutput = new DynamicSliceOutput(fixedSize * expectedEntries);
/*  52 */     this.valueIsNull = new DynamicSliceOutput(expectedEntries);
/*     */   }
/*     */   
/*     */   public FixedWidthBlockBuilder(int fixedSize, int positionCount)
/*     */   {
/*  57 */     super(fixedSize);
/*     */     
/*  59 */     Slice slice = Slices.allocate(fixedSize * positionCount);
/*     */     
/*  61 */     this.blockBuilderStatus = new BlockBuilderStatus();
/*  62 */     this.sliceOutput = slice.getOutput();
/*     */     
/*  64 */     this.valueIsNull = Slices.allocate(positionCount).getOutput();
/*     */   }
/*     */   
/*     */ 
/*     */   protected Slice getRawSlice()
/*     */   {
/*  70 */     return this.sliceOutput.getUnderlyingSlice();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  76 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  82 */     return BlockUtil.intSaturatedCast(this.sliceOutput.size() + this.valueIsNull.size());
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  88 */     return BlockUtil.intSaturatedCast(INSTANCE_SIZE + this.sliceOutput.getRetainedSize() + this.valueIsNull.getRetainedSize());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/*  94 */     BlockUtil.checkValidPositions(positions, this.positionCount);
/*     */     
/*  96 */     SliceOutput newSlice = Slices.allocate(positions.size() * this.fixedSize).getOutput();
/*  97 */     SliceOutput newValueIsNull = Slices.allocate(positions.size()).getOutput();
/*     */     
/*  99 */     for (Iterator localIterator = positions.iterator(); localIterator.hasNext();) { int position = ((Integer)localIterator.next()).intValue();
/* 100 */       newValueIsNull.appendByte(this.valueIsNull.getUnderlyingSlice().getByte(position));
/* 101 */       newSlice.appendBytes(getRawSlice().getBytes(position * this.fixedSize, this.fixedSize));
/*     */     }
/* 103 */     return new FixedWidthBlock(this.fixedSize, positions.size(), newSlice.slice(), newValueIsNull.slice());
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeByte(int value)
/*     */   {
/* 109 */     this.sliceOutput.writeByte(value);
/* 110 */     this.currentEntrySize += 1;
/* 111 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeShort(int value)
/*     */   {
/* 117 */     this.sliceOutput.writeShort(value);
/* 118 */     this.currentEntrySize += 2;
/* 119 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeInt(int value)
/*     */   {
/* 125 */     this.sliceOutput.writeInt(value);
/* 126 */     this.currentEntrySize += 4;
/* 127 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeLong(long value)
/*     */   {
/* 133 */     this.sliceOutput.writeLong(value);
/* 134 */     this.currentEntrySize += 8;
/* 135 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeBytes(Slice source, int sourceIndex, int length)
/*     */   {
/* 141 */     this.sliceOutput.writeBytes(source, sourceIndex, length);
/* 142 */     this.currentEntrySize += length;
/* 143 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder closeEntry()
/*     */   {
/* 149 */     if (this.currentEntrySize != this.fixedSize) {
/* 150 */       throw new IllegalStateException("Expected entry size to be exactly " + this.fixedSize + " but was " + this.currentEntrySize);
/*     */     }
/*     */     
/* 153 */     entryAdded(false);
/* 154 */     this.currentEntrySize = 0;
/* 155 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder appendNull()
/*     */   {
/* 161 */     if (this.currentEntrySize > 0) {
/* 162 */       throw new IllegalStateException("Current entry must be closed before a null can be written");
/*     */     }
/*     */     
/*     */ 
/* 166 */     this.sliceOutput.writeZero(this.fixedSize);
/*     */     
/* 168 */     entryAdded(true);
/*     */     
/* 170 */     return this;
/*     */   }
/*     */   
/*     */   private void entryAdded(boolean isNull)
/*     */   {
/* 175 */     this.valueIsNull.appendByte(isNull ? 1 : 0);
/*     */     
/* 177 */     this.positionCount += 1;
/* 178 */     this.blockBuilderStatus.addBytes(1 + this.fixedSize);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isEntryNull(int position)
/*     */   {
/* 184 */     return this.valueIsNull.getUnderlyingSlice().getByte(position) != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 190 */     int positionCount = getPositionCount();
/* 191 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > positionCount)) {
/* 192 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + positionCount + " positions");
/*     */     }
/*     */     
/* 195 */     Slice newSlice = this.sliceOutput.slice().slice(positionOffset * this.fixedSize, length * this.fixedSize);
/* 196 */     Slice newValueIsNull = this.valueIsNull.slice().slice(positionOffset, length);
/* 197 */     return new FixedWidthBlock(this.fixedSize, length, newSlice, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 203 */     int positionCount = getPositionCount();
/* 204 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > positionCount)) {
/* 205 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + positionCount + " positions");
/*     */     }
/*     */     
/* 208 */     Slice newSlice = Slices.copyOf(this.sliceOutput.getUnderlyingSlice(), positionOffset * this.fixedSize, length * this.fixedSize);
/* 209 */     Slice newValueIsNull = Slices.copyOf(this.valueIsNull.getUnderlyingSlice(), positionOffset, length);
/* 210 */     return new FixedWidthBlock(this.fixedSize, length, newSlice, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block build()
/*     */   {
/* 216 */     if (this.currentEntrySize > 0) {
/* 217 */       throw new IllegalStateException("Current entry must be closed before the block can be built");
/*     */     }
/* 219 */     return new FixedWidthBlock(this.fixedSize, this.positionCount, this.sliceOutput.slice(), this.valueIsNull.slice());
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(BlockBuilderStatus blockBuilderStatus)
/*     */   {
/* 225 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*     */     
/* 227 */     int newSize = BlockUtil.calculateBlockResetSize(this.positionCount);
/* 228 */     this.valueIsNull = new DynamicSliceOutput(newSize);
/* 229 */     this.sliceOutput = new DynamicSliceOutput(newSize * getFixedSize());
/*     */     
/* 231 */     this.positionCount = 0;
/* 232 */     this.currentEntrySize = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 238 */     StringBuilder sb = new StringBuilder("FixedWidthBlockBuilder{");
/* 239 */     sb.append("positionCount=").append(this.positionCount);
/* 240 */     sb.append(", fixedSize=").append(this.fixedSize);
/* 241 */     sb.append(", size=").append(this.sliceOutput.size());
/* 242 */     sb.append('}');
/* 243 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\FixedWidthBlockBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */