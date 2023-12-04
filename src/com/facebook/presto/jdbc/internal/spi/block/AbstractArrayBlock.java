/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public abstract class AbstractArrayBlock
/*     */   implements Block
/*     */ {
/*     */   protected abstract Block getValues();
/*     */   
/*     */   protected abstract Slice getOffsets();
/*     */   
/*     */   protected abstract int getOffsetBase();
/*     */   
/*     */   protected abstract Slice getValueIsNull();
/*     */   
/*     */   public BlockEncoding getEncoding()
/*     */   {
/*  37 */     return new ArrayBlockEncoding(getValues().getEncoding());
/*     */   }
/*     */   
/*     */   private int getOffset(int position)
/*     */   {
/*  42 */     return position == 0 ? 0 : getOffsets().getInt((position - 1) * 4) - getOffsetBase();
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/*  48 */     SliceOutput newOffsets = Slices.allocate(positions.size() * 4).getOutput();
/*  49 */     SliceOutput newValueIsNull = Slices.allocate(positions.size()).getOutput();
/*     */     
/*  51 */     List<Integer> valuesPositions = new ArrayList();
/*  52 */     int countNewOffset = 0;
/*  53 */     for (Iterator localIterator = positions.iterator(); localIterator.hasNext();) { int position = ((Integer)localIterator.next()).intValue();
/*  54 */       if (isNull(position)) {
/*  55 */         newValueIsNull.appendByte(1);
/*  56 */         newOffsets.appendInt(countNewOffset);
/*     */       }
/*     */       else {
/*  59 */         newValueIsNull.appendByte(0);
/*  60 */         int positionStartOffset = getOffset(position);
/*  61 */         int positionEndOffset = getOffset(position + 1);
/*  62 */         countNewOffset += positionEndOffset - positionStartOffset;
/*  63 */         newOffsets.appendInt(countNewOffset);
/*  64 */         for (int j = positionStartOffset; j < positionEndOffset; j++) {
/*  65 */           valuesPositions.add(Integer.valueOf(j));
/*     */         }
/*     */       }
/*     */     }
/*  69 */     Block newValues = getValues().copyPositions(valuesPositions);
/*  70 */     return new ArrayBlock(newValues, newOffsets.slice(), 0, newValueIsNull.slice());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int position, int length)
/*     */   {
/*  76 */     return getRegion(position, length, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int position, int length)
/*     */   {
/*  82 */     return getRegion(position, length, true);
/*     */   }
/*     */   
/*     */   private Block getRegion(int position, int length, boolean compact)
/*     */   {
/*  87 */     int positionCount = getPositionCount();
/*  88 */     if ((position < 0) || (length < 0) || (position + length > positionCount)) {
/*  89 */       throw new IndexOutOfBoundsException("Invalid position " + position + " in block with " + positionCount + " positions");
/*     */     }
/*     */     
/*  92 */     int startValueOffset = getOffset(position);
/*  93 */     int endValueOffset = getOffset(position + length);
/*     */     int newOffsetBase;
/*     */     Block newValues;
/*     */     Slice newOffsets;
/*     */     Slice newValueIsNull;
/*  98 */     int newOffsetBase; if (compact) {
/*  99 */       Block newValues = getValues().copyRegion(startValueOffset, endValueOffset - startValueOffset);
/* 100 */       int[] newOffsetsArray = new int[length];
/* 101 */       for (int i = 0; i < length; i++) {
/* 102 */         newOffsetsArray[i] = (getOffset(position + i + 1) - getOffset(position));
/*     */       }
/* 104 */       Slice newOffsets = Slices.wrappedIntArray(newOffsetsArray);
/* 105 */       Slice newValueIsNull = Slices.copyOf(getValueIsNull(), position, length);
/* 106 */       newOffsetBase = 0;
/*     */     }
/*     */     else {
/* 109 */       if ((position == 0) && (length == positionCount))
/*     */       {
/* 111 */         return this;
/*     */       }
/*     */       
/* 114 */       newValues = getValues().getRegion(startValueOffset, endValueOffset - startValueOffset);
/* 115 */       newOffsets = getOffsets().slice(position * 4, length * 4);
/* 116 */       newValueIsNull = getValueIsNull().slice(position, length);
/* 117 */       newOffsetBase = startValueOffset + getOffsetBase();
/*     */     }
/*     */     
/* 120 */     return new ArrayBlock(newValues, newOffsets, newOffsetBase, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/* 126 */     return getOffset(position + 1) - getOffset(position);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/* 132 */     if (clazz != Block.class) {
/* 133 */       throw new IllegalArgumentException("clazz must be Block.class");
/*     */     }
/* 135 */     checkReadablePosition(position);
/*     */     
/* 137 */     int startValueOffset = getOffset(position);
/* 138 */     int endValueOffset = getOffset(position + 1);
/* 139 */     return (T)clazz.cast(getValues().getRegion(startValueOffset, endValueOffset - startValueOffset));
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 145 */     checkReadablePosition(position);
/* 146 */     BlockBuilder entryBuilder = blockBuilder.beginBlockEntry();
/* 147 */     int startValueOffset = getOffset(position);
/* 148 */     int endValueOffset = getOffset(position + 1);
/* 149 */     for (int i = startValueOffset; i < endValueOffset; i++) {
/* 150 */       if (getValues().isNull(i)) {
/* 151 */         entryBuilder.appendNull();
/*     */       }
/*     */       else {
/* 154 */         getValues().writePositionTo(i, entryBuilder);
/* 155 */         entryBuilder.closeEntry();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 163 */     checkReadablePosition(position);
/*     */     
/* 165 */     int startValueOffset = getOffset(position);
/* 166 */     int endValueOffset = getOffset(position + 1);
/*     */     
/* 168 */     Block newValues = getValues().copyRegion(startValueOffset, endValueOffset - startValueOffset);
/*     */     
/*     */ 
/* 171 */     Slice newOffsets = Slices.wrappedIntArray(new int[] { endValueOffset - startValueOffset });
/*     */     
/* 173 */     Slice newValueIsNull = Slices.copyOf(getValueIsNull(), position, 1);
/*     */     
/* 175 */     return new ArrayBlock(newValues, newOffsets, 0, newValueIsNull);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 181 */     checkReadablePosition(position);
/* 182 */     return getValueIsNull().getByte(position) != 0;
/*     */   }
/*     */   
/*     */   public <T> T apply(ArrayBlockFunction<T> function, int position)
/*     */   {
/* 187 */     checkReadablePosition(position);
/*     */     
/* 189 */     int startValueOffset = getOffset(position);
/* 190 */     int endValueOffset = getOffset(position + 1);
/* 191 */     return (T)function.apply(getValues(), startValueOffset, endValueOffset - startValueOffset);
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 196 */     if ((position < 0) || (position >= getPositionCount())) {
/* 197 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface ArrayBlockFunction<T>
/*     */   {
/*     */     public abstract T apply(Block paramBlock, int paramInt1, int paramInt2);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\AbstractArrayBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */