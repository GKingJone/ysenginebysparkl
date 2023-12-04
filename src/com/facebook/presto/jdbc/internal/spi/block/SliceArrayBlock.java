/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SizeOf;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.Arrays;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class SliceArrayBlock
/*     */   extends AbstractVariableWidthBlock
/*     */ {
/*  32 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(SliceArrayBlock.class).instanceSize();
/*     */   
/*     */   private final int positionCount;
/*     */   private final Slice[] values;
/*     */   private final int sizeInBytes;
/*     */   private final int retainedSizeInBytes;
/*     */   
/*     */   public SliceArrayBlock(int positionCount, Slice[] values)
/*     */   {
/*  41 */     this(positionCount, values, false);
/*     */   }
/*     */   
/*     */   public SliceArrayBlock(int positionCount, Slice[] values, boolean valueSlicesAreDistinct)
/*     */   {
/*  46 */     this.positionCount = positionCount;
/*     */     
/*  48 */     if (values.length < positionCount) {
/*  49 */       throw new IllegalArgumentException("values length is less than positionCount");
/*     */     }
/*  51 */     this.values = values;
/*     */     
/*  53 */     this.sizeInBytes = getSliceArraySizeInBytes(values);
/*     */     
/*     */ 
/*  56 */     this.retainedSizeInBytes = (INSTANCE_SIZE + (valueSlicesAreDistinct ? this.sizeInBytes : getSliceArrayRetainedSizeInBytes(values)));
/*     */   }
/*     */   
/*     */   public Slice[] getValues()
/*     */   {
/*  61 */     return this.values;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Slice getRawSlice(int position)
/*     */   {
/*  67 */     return this.values[position];
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getPositionOffset(int position)
/*     */   {
/*  73 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isEntryNull(int position)
/*     */   {
/*  79 */     return this.values[position] == null;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/*  85 */     return new SliceArrayBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/*  91 */     BlockUtil.checkValidPositions(positions, this.positionCount);
/*     */     
/*  93 */     Slice[] newValues = new Slice[positions.size()];
/*  94 */     for (int i = 0; i < positions.size(); i++) {
/*  95 */       if (!isEntryNull(((Integer)positions.get(i)).intValue())) {
/*  96 */         newValues[i] = Slices.copyOf(this.values[((Integer)positions.get(i)).intValue()]);
/*     */       }
/*     */     }
/*  99 */     return new SliceArrayBlock(positions.size(), newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/* 105 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/* 111 */     return this.values[position].length();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/* 117 */     return this.sizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/* 123 */     return this.retainedSizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 129 */     int positionCount = getPositionCount();
/* 130 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > positionCount)) {
/* 131 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + positionCount + " positions");
/*     */     }
/*     */     
/* 134 */     Slice[] newValues = (Slice[])Arrays.copyOfRange(this.values, positionOffset, positionOffset + length);
/* 135 */     return new SliceArrayBlock(length, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 141 */     int positionCount = getPositionCount();
/* 142 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > positionCount)) {
/* 143 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + positionCount + " positions");
/*     */     }
/*     */     
/* 146 */     return new SliceArrayBlock(length, deepCopyAndCompact(this.values, positionOffset, length));
/*     */   }
/*     */   
/*     */   static Slice[] deepCopyAndCompact(Slice[] values, int positionOffset, int length)
/*     */   {
/* 151 */     Slice[] newValues = (Slice[])Arrays.copyOfRange(values, positionOffset, positionOffset + length);
/*     */     
/* 153 */     Map<Slice, Slice> distinctValues = new IdentityHashMap();
/* 154 */     for (int i = 0; i < newValues.length; i++) {
/* 155 */       Slice slice = newValues[i];
/* 156 */       if (slice != null)
/*     */       {
/*     */ 
/* 159 */         Slice distinct = (Slice)distinctValues.get(slice);
/* 160 */         if (distinct == null) {
/* 161 */           distinct = Slices.copyOf(slice);
/* 162 */           distinctValues.put(slice, distinct);
/*     */         }
/* 164 */         newValues[i] = distinct;
/*     */       } }
/* 166 */     return newValues;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 172 */     StringBuilder sb = new StringBuilder("SliceArrayBlock{");
/* 173 */     sb.append("positionCount=").append(getPositionCount());
/* 174 */     sb.append('}');
/* 175 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static int getSliceArraySizeInBytes(Slice[] values)
/*     */   {
/* 180 */     long sizeInBytes = SizeOf.sizeOf(values);
/* 181 */     for (Slice value : values) {
/* 182 */       if (value != null) {
/* 183 */         sizeInBytes += value.length();
/*     */       }
/*     */     }
/* 186 */     return BlockUtil.intSaturatedCast(sizeInBytes);
/*     */   }
/*     */   
/*     */   static int getSliceArrayRetainedSizeInBytes(Slice[] values)
/*     */   {
/* 191 */     long sizeInBytes = SizeOf.sizeOf(values);
/* 192 */     Map<Object, Boolean> uniqueRetained = new IdentityHashMap(values.length);
/* 193 */     for (Slice value : values) {
/* 194 */       if ((value != null) && (value.getBase() != null) && (uniqueRetained.put(value.getBase(), Boolean.valueOf(true)) == null)) {
/* 195 */         sizeInBytes += value.getRetainedSize();
/*     */       }
/*     */     }
/* 198 */     return BlockUtil.intSaturatedCast(sizeInBytes);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\SliceArrayBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */