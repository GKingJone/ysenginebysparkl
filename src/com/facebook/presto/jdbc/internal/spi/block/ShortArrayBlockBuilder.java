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
/*     */ public class ShortArrayBlockBuilder
/*     */   implements BlockBuilder
/*     */ {
/*  30 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(ShortArrayBlockBuilder.class).instanceSize();
/*     */   
/*     */   private BlockBuilderStatus blockBuilderStatus;
/*     */   
/*     */   private int positionCount;
/*     */   
/*     */   private boolean[] valueIsNull;
/*     */   
/*     */   private short[] values;
/*     */   
/*     */   private int retainedSizeInBytes;
/*     */   
/*     */   public ShortArrayBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*     */   {
/*  44 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*  45 */     this.values = new short[expectedEntries];
/*  46 */     this.valueIsNull = new boolean[expectedEntries];
/*     */     
/*  48 */     updateDataSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockBuilder writeShort(int value)
/*     */   {
/*  54 */     if (this.values.length <= this.positionCount) {
/*  55 */       growCapacity();
/*     */     }
/*     */     
/*  58 */     this.values[this.positionCount] = ((short)value);
/*     */     
/*  60 */     this.positionCount += 1;
/*  61 */     this.blockBuilderStatus.addBytes(3);
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
/*  81 */     this.blockBuilderStatus.addBytes(3);
/*  82 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Block build()
/*     */   {
/*  88 */     return new ShortArrayBlock(this.positionCount, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(BlockBuilderStatus blockBuilderStatus)
/*     */   {
/*  94 */     this.blockBuilderStatus = ((BlockBuilderStatus)Objects.requireNonNull(blockBuilderStatus, "blockBuilderStatus is null"));
/*     */     
/*  96 */     int newSize = BlockUtil.calculateBlockResetSize(this.positionCount);
/*  97 */     this.valueIsNull = new boolean[newSize];
/*  98 */     this.values = new short[newSize];
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
/* 122 */     return BlockUtil.intSaturatedCast(3L * this.positionCount);
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
/* 140 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/* 146 */     checkReadablePosition(position);
/* 147 */     if (offset != 0) {
/* 148 */       throw new IllegalArgumentException("offset must be zero");
/*     */     }
/* 150 */     return this.values[position];
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 156 */     checkReadablePosition(position);
/* 157 */     return this.valueIsNull[position];
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 163 */     checkReadablePosition(position);
/* 164 */     blockBuilder.writeShort(this.values[position]);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 170 */     checkReadablePosition(position);
/* 171 */     return new ShortArrayBlock(1, new boolean[] { this.valueIsNull[position] }, new short[] { this.values[position] });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 180 */     boolean[] newValueIsNull = new boolean[positions.size()];
/* 181 */     short[] newValues = new short[positions.size()];
/* 182 */     for (int i = 0; i < positions.size(); i++) {
/* 183 */       int position = ((Integer)positions.get(i)).intValue();
/* 184 */       checkReadablePosition(position);
/* 185 */       newValueIsNull[i] = this.valueIsNull[position];
/* 186 */       newValues[i] = this.values[position];
/*     */     }
/* 188 */     return new ShortArrayBlock(positions.size(), newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 194 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 196 */     return new ShortArrayBlock(positionOffset, length, this.valueIsNull, this.values);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 202 */     BlockUtil.checkValidRegion(getPositionCount(), positionOffset, length);
/*     */     
/* 204 */     boolean[] newValueIsNull = Arrays.copyOfRange(this.valueIsNull, positionOffset, positionOffset + length);
/* 205 */     short[] newValues = Arrays.copyOfRange(this.values, positionOffset, positionOffset + length);
/* 206 */     return new ShortArrayBlock(length, newValueIsNull, newValues);
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncoding getEncoding()
/*     */   {
/* 212 */     return new ShortArrayBlockEncoding();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 218 */     StringBuilder sb = new StringBuilder("ShortArrayBlockBuilder{");
/* 219 */     sb.append("positionCount=").append(getPositionCount());
/* 220 */     sb.append('}');
/* 221 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 226 */     if ((position < 0) || (position >= getPositionCount())) {
/* 227 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ShortArrayBlockBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */