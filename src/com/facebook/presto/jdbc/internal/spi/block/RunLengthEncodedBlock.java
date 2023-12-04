/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import com.facebook.presto.jdbc.internal.spi.predicate.Utils;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
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
/*     */ public class RunLengthEncodedBlock
/*     */   implements Block
/*     */ {
/*  30 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(RunLengthEncodedBlock.class).instanceSize();
/*     */   private final Block value;
/*     */   private final int positionCount;
/*     */   
/*  34 */   public static Block create(Type type, Object value, int positionCount) { Block block = Utils.nativeValueToBlock(type, value);
/*  35 */     return new RunLengthEncodedBlock(block, positionCount);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RunLengthEncodedBlock(Block value, int positionCount)
/*     */   {
/*  43 */     Objects.requireNonNull(value, "value is null");
/*  44 */     if (value.getPositionCount() != 1) {
/*  45 */       throw new IllegalArgumentException(String.format("Expected value to contain a single position but has %s positions", new Object[] { Integer.valueOf(value.getPositionCount()) }));
/*     */     }
/*     */     
/*     */ 
/*  49 */     if ((value instanceof RunLengthEncodedBlock)) {
/*  50 */       throw new IllegalArgumentException(String.format("Value can not be an instance of a %s", new Object[] { getClass().getName() }));
/*     */     }
/*     */     
/*  53 */     if (positionCount < 0) {
/*  54 */       throw new IllegalArgumentException("positionCount is negative");
/*     */     }
/*     */     
/*  57 */     this.value = value;
/*  58 */     this.positionCount = positionCount;
/*     */   }
/*     */   
/*     */   public Block getValue()
/*     */   {
/*  63 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPositionCount()
/*     */   {
/*  69 */     return this.positionCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSizeInBytes()
/*     */   {
/*  75 */     return this.value.getSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSizeInBytes()
/*     */   {
/*  81 */     return INSTANCE_SIZE + this.value.getRetainedSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public RunLengthBlockEncoding getEncoding()
/*     */   {
/*  87 */     return new RunLengthBlockEncoding(this.value.getEncoding());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/*  93 */     BlockUtil.checkValidPositions(positions, this.positionCount);
/*  94 */     return new RunLengthEncodedBlock(this.value.copyRegion(0, 1), positions.size());
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getRegion(int positionOffset, int length)
/*     */   {
/* 100 */     checkPositionIndexes(positionOffset, length);
/* 101 */     return new RunLengthEncodedBlock(this.value, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int positionOffset, int length)
/*     */   {
/* 107 */     checkPositionIndexes(positionOffset, length);
/* 108 */     return new RunLengthEncodedBlock(this.value.copyRegion(0, 1), length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/* 114 */     return this.value.getLength(0);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/* 120 */     return this.value.getByte(0, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/* 126 */     return this.value.getShort(0, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/* 132 */     return this.value.getInt(0, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/* 138 */     return this.value.getLong(0, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/* 144 */     return this.value.getSlice(0, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/* 150 */     return (T)this.value.getObject(0, clazz);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/* 156 */     return this.value.bytesEqual(0, offset, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/* 162 */     return this.value.bytesCompare(0, offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 168 */     this.value.writeBytesTo(0, offset, length, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/* 174 */     this.value.writePositionTo(position, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/* 180 */     return this.value.equals(0, offset, otherBlock, otherPosition, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 186 */     return this.value.hash(0, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int leftPosition, int leftOffset, int leftLength, Block rightBlock, int rightPosition, int rightOffset, int rightLength)
/*     */   {
/* 192 */     return this.value.compareTo(0, leftOffset, leftLength, rightBlock, rightPosition, rightOffset, rightLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 198 */     checkReadablePosition(position);
/* 199 */     return this.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 205 */     checkReadablePosition(position);
/* 206 */     return this.value.isNull(0);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 212 */     StringBuilder sb = new StringBuilder(getClass().getSimpleName());
/* 213 */     sb.append("positionCount=").append(this.positionCount);
/* 214 */     sb.append(", value=").append(this.value);
/* 215 */     sb.append('}');
/* 216 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public void assureLoaded()
/*     */   {
/* 222 */     this.value.assureLoaded();
/*     */   }
/*     */   
/*     */   private void checkPositionIndexes(int positionOffset, int length)
/*     */   {
/* 227 */     if ((positionOffset < 0) || (length < 0) || (positionOffset + length > this.positionCount)) {
/* 228 */       throw new IndexOutOfBoundsException("Invalid position " + positionOffset + " in block with " + this.positionCount + " positions");
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkReadablePosition(int position)
/*     */   {
/* 234 */     if ((position < 0) || (position >= this.positionCount)) {
/* 235 */       throw new IllegalArgumentException("position is not valid");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\RunLengthEncodedBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */