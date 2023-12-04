/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SizeOf;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.util.Arrays;
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
/*     */ public class LongArrayBlockBuilder
/*     */   implements BlockBuilder
/*     */ {
/*  30 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(LongArrayBlockBuilder.class).instanceSize();
/*     */   
/*     */   private BlockBuilderStatus blockBuilderStatus;
/*     */   
/*     */   private int positionCount;
/*     */   
/*     */   private boolean[] valueIsNull;
/*     */   
/*     */   private long[] values;
/*     */   
/*     */   private int retainedSizeInBytes;
/*     */   
/*     */   public LongArrayBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  44 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*  45 */     this.values = new long[expectedEntries];
/*  46 */     this.valueIsNull = new boolean[expectedEntries];
/*     */     
/*  48 */     updateDataSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeLong(long value)
/*     */   {
/*  54 */     if (this.values.length <= this.positionCount) {
/*  55 */       growCapacity();
/*     */     }
/*     */     
/*  58 */     this.values[this.positionCount] = value;
/*     */     
/*  60 */     this.positionCount += 1;
/*  61 */     this.blockBuilderStatus.addBytes(9);
/*  62 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder closeEntry()
/*     */   {
/*  68 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder appendNull()
/*     */   {
/*  74 */     if (this.values.length <= this.positionCount) {
/*  75 */       growCapacity();
/*     */     }
/*     */     
/*  78 */     this.valueIsNull[this.positionCount] = true;
/*     */     
/*  80 */     this.positionCount += 1;
/*  81 */     this.blockBuilderStatus.addBytes(9);
/*  82 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Block build()
/*     */   {
/*  88 */     return new LongArrayBlock(this.positionCount, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(BlockBuilderStatus blockBuilderStatus)
/*     */   {
/*  94 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*     */     
/*  96 */     int newSize = BlockUtil.calculateBlockResetSize(this.positionCount);
/*  97 */     this.valueIsNull = new boolean[newSize];
/*  98 */     this.values = new long[newSize];
/*     */     
/* 100 */     this.positionCount = 0;
/*     */     
/* 102 */     updateDataSize();
/*     */   }
/*     */   
/*     */   private void growCapacity()
/*     */   {
/* 107 */     int newSize = BlockUtil.calculateNewArraySize(this.values.length);
/* 108 */     this.valueIsNull = Arrays.copyOf(this.valueIsNull, newSize);
/* 109 */     this.values = Arrays.copyOf(this.values, newSize);
/* 110 */     updateDataSize();
/*     */   }
/*     */   
/*     */   private void updateDataSize()
/*     */   {
/* 115 */     this.retainedSizeInBytes = BlockUtil.intSaturatedCast(INSTANCE_SIZE + SizeOf.sizeOf(this.valueIsNull) + SizeOf.sizeOf(this.values));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/* 122 */     return BlockUtil.intSaturatedCast(9L * this.positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/* 128 */     return this.retainedSizeInBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/* 134 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/* 140 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/* 146 */     checkReadablePosition(position);
/* 147 */     if (offset != 0) {
/* 148 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/* 150 */     return this.values[position];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public int getInt(int position, int offset)
/*     */   {
/* 158 */     checkReadablePosition(position);
/* 159 */     if (offset != 0) {
/* 160 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/* 162 */     return Math.toIntExact(this.values[position]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public short getShort(int position, int offset)
/*     */   {
/* 170 */     checkReadablePosition(position);
/* 171 */     if (offset != 0) {
/* 172 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*     */     
/* 175 */     short value = (short)(int)this.values[position];
/* 176 */     if (value != this.values[position]) {
/* 177 */       throw new ArithmeticException("short overflow");
/*     */     }
/* 179 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public byte getByte(int position, int offset)
/*     */   {
/* 187 */     checkReadablePosition(position);
/* 188 */     if (offset != 0) {
/* 189 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/*     */     
/* 192 */     byte value = (byte)(int)this.values[position];
/* 193 */     if (value != this.values[position]) {
/* 194 */       throw new ArithmeticException("byte overflow");
/*     */     }
/* 196 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 202 */     checkReadablePosition(position);
/* 203 */     return this.valueIsNull[position];
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 209 */     checkReadablePosition(position);
/* 210 */     blockBuilder.writeLong(this.values[position]);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 216 */     checkReadablePosition(position);
/* 217 */     return new LongArrayBlock(1, new boolean[] { this.valueIsNull[position] }, new long[] { this.values[position] });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 226 */     boolean[] newValueIsNull = new boolean[positions.size()];
/* 227 */     long[] newValues = new long[positions.size()];
/* 228 */     for (int i = 0; i < positions.size(); i++) {
/* 229 */       int position = ((Integer)positions.get(i)).intValue();
/* 230 */       checkReadablePosition(position);
/* 231 */       newValueIsNull[i] = this.valueIsNull[position];
/* 232 */       newValues[i] = this.values[position];
/*     */     }
/* 234 */     return new LongArrayBlock(positions.size(), newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 240 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 242 */     return new LongArrayBlock(positionOffset, length, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 248 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 250 */     boolean[] newValueIsNull = Arrays.copyOfRange(this.valueIsNull, positionOffset, positionOffset + length);
/* 251 */     long[] newValues = Arrays.copyOfRange(this.values, positionOffset, positionOffset + length);
/* 252 */     return new LongArrayBlock(length, newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 258 */     return new LongArrayBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 264 */     StringBuilder sb = new StringBuilder("LongArrayBlockBuilder{");
/* 265 */     sb.append("positionCount=").append(getPositionCount());
/* 266 */     sb.append('}');
/* 267 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 272 */     if ((position < 0) || (position >= getPositionCount())) {
/* 273 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LongArrayBlockBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */