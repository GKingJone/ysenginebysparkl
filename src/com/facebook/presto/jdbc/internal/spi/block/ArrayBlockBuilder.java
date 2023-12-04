/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.DynamicSliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
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
/*     */ public class ArrayBlockBuilder
/*     */   extends AbstractArrayBlock
/*     */   implements BlockBuilder
/*     */ {
/*  29 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(ArrayBlockBuilder.class).instanceSize() + BlockBuilderStatus.INSTANCE_SIZE;
/*     */   
/*     */   private BlockBuilderStatus blockBuilderStatus;
/*     */   
/*     */   private final BlockBuilder values;
/*     */   
/*     */   private SliceOutput offsets;
/*     */   
/*     */   private SliceOutput valueIsNull;
/*     */   private static final int OFFSET_BASE = 0;
/*     */   private int currentEntrySize;
/*     */   
/*     */   public ArrayBlockBuilder(BlockBuilder valuesBlock, BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  43 */     this(blockBuilderStatus, valuesBlock, new DynamicSliceOutput(expectedEntries * 4), new DynamicSliceOutput(expectedEntries));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayBlockBuilder(Type elementType, BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*     */   {
/*  52 */     this(blockBuilderStatus, elementType
/*     */     
/*  54 */       .createBlockBuilder(blockBuilderStatus, expectedEntries, expectedBytesPerEntry), new DynamicSliceOutput(expectedEntries * 4), new DynamicSliceOutput(expectedEntries));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ArrayBlockBuilder(Type elementType, BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  61 */     this(blockBuilderStatus, elementType
/*     */     
/*  63 */       .createBlockBuilder(blockBuilderStatus, expectedEntries), new DynamicSliceOutput(expectedEntries * 4), new DynamicSliceOutput(expectedEntries));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ArrayBlockBuilder(BlockBuilderStatus blockBuilderStatus, BlockBuilder values, SliceOutput offsets, SliceOutput valueIsNull)
/*     */   {
/*  73 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*  74 */     this.values = ((BlockBuilder)Objects.requireNonNull(values, "values is null"));
/*  75 */     this.offsets = ((SliceOutput)Objects.requireNonNull(offsets, "offset is null"));
/*  76 */     this.valueIsNull = ((SliceOutput)Objects.requireNonNull(valueIsNull));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  82 */     return this.valueIsNull.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  88 */     return this.values.getSizeInBytes() + this.offsets.size() + this.valueIsNull.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  94 */     return INSTANCE_SIZE + this.values.getRetainedSizeInBytes() + this.offsets.getRetainedSize() + this.valueIsNull.getRetainedSize();
/*     */   }
/*     */   
/*     */ 
/*     */   protected Block getValues()
/*     */   {
/* 100 */     return this.values;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Slice getOffsets()
/*     */   {
/* 106 */     return this.offsets.getUnderlyingSlice();
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getOffsetBase()
/*     */   {
/* 112 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Slice getValueIsNull()
/*     */   {
/* 118 */     return this.valueIsNull.getUnderlyingSlice();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeObject(Object value)
/*     */   {
/* 124 */     if (this.currentEntrySize != 0) {
/* 125 */       throw new IllegalStateException("Expected entry size to be exactly 0 but was " + this.currentEntrySize);
/*     */     }
/*     */     
/* 128 */     Block block = (Block)value;
/* 129 */     for (int i = 0; i < block.getPositionCount(); i++) {
/* 130 */       if (block.isNull(i)) {
/* 131 */         this.values.appendNull();
/*     */       }
/*     */       else {
/* 134 */         block.writePositionTo(i, this.values);
/* 135 */         this.values.closeEntry();
/*     */       }
/*     */     }
/*     */     
/* 139 */     this.currentEntrySize += 1;
/* 140 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayElementBlockWriter beginBlockEntry()
/*     */   {
/* 146 */     if (this.currentEntrySize != 0) {
/* 147 */       throw new IllegalStateException("Expected current entry size to be exactly 0 but was " + this.currentEntrySize);
/*     */     }
/* 149 */     this.currentEntrySize += 1;
/* 150 */     return new ArrayElementBlockWriter(this.values, this.values.getPositionCount());
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder closeEntry()
/*     */   {
/* 156 */     if (this.currentEntrySize != 1) {
/* 157 */       throw new IllegalStateException("Expected entry size to be exactly 1 but was " + this.currentEntrySize);
/*     */     }
/*     */     
/* 160 */     entryAdded(false);
/* 161 */     this.currentEntrySize = 0;
/* 162 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder appendNull()
/*     */   {
/* 168 */     if (this.currentEntrySize > 0) {
/* 169 */       throw new IllegalStateException("Current entry must be closed before a null can be written");
/*     */     }
/*     */     
/* 172 */     entryAdded(true);
/* 173 */     return this;
/*     */   }
/*     */   
/*     */   private void entryAdded(boolean isNull)
/*     */   {
/* 178 */     this.offsets.appendInt(this.values.getPositionCount());
/* 179 */     this.valueIsNull.appendByte(isNull ? 1 : 0);
/*     */     
/* 181 */     this.blockBuilderStatus.addBytes(5);
/*     */   }
/*     */   
/*     */ 
/*     */   public ArrayBlock build()
/*     */   {
/* 187 */     if (this.currentEntrySize > 0) {
/* 188 */       throw new IllegalStateException("Current entry must be closed before the block can be built");
/*     */     }
/* 190 */     return new ArrayBlock(this.values.build(), this.offsets.slice(), 0, this.valueIsNull.slice());
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(BlockBuilderStatus blockBuilderStatus)
/*     */   {
/* 196 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*     */     
/* 198 */     int newSize = BlockUtil.calculateBlockResetSize(getPositionCount());
/* 199 */     this.valueIsNull = new DynamicSliceOutput(newSize);
/* 200 */     this.offsets = new DynamicSliceOutput(newSize * 4);
/* 201 */     this.values.reset(blockBuilderStatus);
/*     */     
/* 203 */     this.currentEntrySize = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 209 */     StringBuilder sb = new StringBuilder("ArrayBlockBuilder{");
/* 210 */     sb.append("positionCount=").append(getPositionCount());
/* 211 */     sb.append('}');
/* 212 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ArrayBlockBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */