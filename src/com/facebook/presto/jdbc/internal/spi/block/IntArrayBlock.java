/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SizeOf;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.Arrays;
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
/*     */ public class IntArrayBlock
/*     */   implements Block
/*     */ {
/*  28 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(IntArrayBlock.class).instanceSize();
/*     */   
/*     */   private final int arrayOffset;
/*     */   
/*     */   private final int positionCount;
/*     */   private final boolean[] valueIsNull;
/*     */   private final int[] values;
/*     */   private final int sizeInBytes;
/*     */   private final int retainedSizeInBytes;
/*     */   
/*     */   public IntArrayBlock(int positionCount, boolean[] valueIsNull, int[] values)
/*     */   {
/*  40 */     this(0, positionCount, valueIsNull, values);
/*     */   }
/*     */   
/*     */   IntArrayBlock(int arrayOffset, int positionCount, boolean[] valueIsNull, int[] values)
/*     */   {
/*  45 */     if (arrayOffset < 0) {
/*  46 */       throw new IllegalArgumentException("arrayOffset is negative");
/*     */     }
/*  48 */     this.arrayOffset = arrayOffset;
/*  49 */     if (positionCount < 0) {
/*  50 */       throw new IllegalArgumentException("positionCount is negative");
/*     */     }
/*  52 */     this.positionCount = positionCount;
/*     */     
/*  54 */     if (values.length - arrayOffset < positionCount) {
/*  55 */       throw new IllegalArgumentException("values length is less than positionCount");
/*     */     }
/*  57 */     this.values = values;
/*     */     
/*  59 */     if (valueIsNull.length - arrayOffset < positionCount) {
/*  60 */       throw new IllegalArgumentException("isNull length is less than positionCount");
/*     */     }
/*  62 */     this.valueIsNull = valueIsNull;
/*     */     
/*  64 */     this.sizeInBytes = BlockUtil.intSaturatedCast(5L * positionCount);
/*  65 */     this.retainedSizeInBytes = BlockUtil.intSaturatedCast(INSTANCE_SIZE + SizeOf.sizeOf(valueIsNull) + SizeOf.sizeOf(values));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  71 */     return this.sizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  77 */     return this.retainedSizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  83 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/*  89 */     return 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/*  95 */     checkReadablePosition(position);
/*  96 */     if (offset != 0) {
/*  97 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*  99 */     return this.values[(position + this.arrayOffset)];
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 105 */     checkReadablePosition(position);
/* 106 */     return this.valueIsNull[(position + this.arrayOffset)];
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 112 */     checkReadablePosition(position);
/* 113 */     blockBuilder.writeInt(this.values[(position + this.arrayOffset)]);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 119 */     checkReadablePosition(position);
/* 120 */     return new IntArrayBlock(1, new boolean[] { this.valueIsNull[(position + this.arrayOffset)] }, new int[] { this.values[(position + this.arrayOffset)] });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 129 */     boolean[] newValueIsNull = new boolean[positions.size()];
/* 130 */     int[] newValues = new int[positions.size()];
/* 131 */     for (int i = 0; i < positions.size(); i++) {
/* 132 */       int position = ((Integer)positions.get(i)).intValue();
/* 133 */       checkReadablePosition(position);
/* 134 */       newValueIsNull[i] = this.valueIsNull[(position + this.arrayOffset)];
/* 135 */       newValues[i] = this.values[(position + this.arrayOffset)];
/*     */     }
/* 137 */     return new IntArrayBlock(positions.size(), newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 143 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 145 */     return new IntArrayBlock(positionOffset + this.arrayOffset, length, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 151 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 153 */     positionOffset += this.arrayOffset;
/* 154 */     boolean[] newValueIsNull = Arrays.copyOfRange(this.valueIsNull, positionOffset, positionOffset + length);
/* 155 */     int[] newValues = Arrays.copyOfRange(this.values, positionOffset, positionOffset + length);
/* 156 */     return new IntArrayBlock(length, newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 162 */     return new IntArrayBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 168 */     StringBuilder sb = new StringBuilder("IntArrayBlock{");
/* 169 */     sb.append("positionCount=").append(getPositionCount());
/* 170 */     sb.append('}');
/* 171 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 176 */     if ((position < 0) || (position >= getPositionCount())) {
/* 177 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\IntArrayBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */