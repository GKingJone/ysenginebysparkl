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
/*     */ public class LongArrayBlock
/*     */   implements Block
/*     */ {
/*  28 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(LongArrayBlock.class).instanceSize();
/*     */   
/*     */   private final int arrayOffset;
/*     */   
/*     */   private final int positionCount;
/*     */   private final boolean[] valueIsNull;
/*     */   private final long[] values;
/*     */   private final int sizeInBytes;
/*     */   private final int retainedSizeInBytes;
/*     */   
/*     */   public LongArrayBlock(int positionCount, boolean[] valueIsNull, long[] values)
/*     */   {
/*  40 */     this(0, positionCount, valueIsNull, values);
/*     */   }
/*     */   
/*     */   LongArrayBlock(int arrayOffset, int positionCount, boolean[] valueIsNull, long[] values)
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
/*  64 */     this.sizeInBytes = BlockUtil.intSaturatedCast(9L * positionCount);
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
/*  89 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/*  95 */     checkReadablePosition(position);
/*  96 */     if (offset != 0) {
/*  97 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*  99 */     return this.values[(position + this.arrayOffset)];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public int getInt(int position, int offset)
/*     */   {
/* 107 */     checkReadablePosition(position);
/* 108 */     if (offset != 0) {
/* 109 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/* 111 */     return Math.toIntExact(this.values[(position + this.arrayOffset)]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public short getShort(int position, int offset)
/*     */   {
/* 119 */     checkReadablePosition(position);
/* 120 */     if (offset != 0) {
/* 121 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*     */     
/* 124 */     short value = (short)(int)this.values[(position + this.arrayOffset)];
/* 125 */     if (value != this.values[(position + this.arrayOffset)]) {
/* 126 */       throw new ArithmeticException("short overflow");
/*     */     }
/* 128 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public byte getByte(int position, int offset)
/*     */   {
/* 136 */     checkReadablePosition(position);
/* 137 */     if (offset != 0) {
/* 138 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*     */     
/* 141 */     byte value = (byte)(int)this.values[(position + this.arrayOffset)];
/* 142 */     if (value != this.values[(position + this.arrayOffset)]) {
/* 143 */       throw new ArithmeticException("byte overflow");
/*     */     }
/* 145 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 151 */     checkReadablePosition(position);
/* 152 */     return this.valueIsNull[(position + this.arrayOffset)];
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 158 */     checkReadablePosition(position);
/* 159 */     blockBuilder.writeLong(this.values[(position + this.arrayOffset)]);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 165 */     checkReadablePosition(position);
/* 166 */     return new LongArrayBlock(1, new boolean[] { this.valueIsNull[(position + this.arrayOffset)] }, new long[] { this.values[(position + this.arrayOffset)] });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 175 */     boolean[] newValueIsNull = new boolean[positions.size()];
/* 176 */     long[] newValues = new long[positions.size()];
/* 177 */     for (int i = 0; i < positions.size(); i++) {
/* 178 */       int position = ((Integer)positions.get(i)).intValue();
/* 179 */       checkReadablePosition(position);
/* 180 */       newValueIsNull[i] = this.valueIsNull[(position + this.arrayOffset)];
/* 181 */       newValues[i] = this.values[(position + this.arrayOffset)];
/*     */     }
/* 183 */     return new LongArrayBlock(positions.size(), newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 189 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 191 */     return new LongArrayBlock(positionOffset + this.arrayOffset, length, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 197 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 199 */     positionOffset += this.arrayOffset;
/* 200 */     boolean[] newValueIsNull = Arrays.copyOfRange(this.valueIsNull, positionOffset, positionOffset + length);
/* 201 */     long[] newValues = Arrays.copyOfRange(this.values, positionOffset, positionOffset + length);
/* 202 */     return new LongArrayBlock(length, newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 208 */     return new LongArrayBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     StringBuilder sb = new StringBuilder("LongArrayBlock{");
/* 215 */     sb.append("positionCount=").append(getPositionCount());
/* 216 */     sb.append('}');
/* 217 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 222 */     if ((position < 0) || (position >= getPositionCount())) {
/* 223 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LongArrayBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */