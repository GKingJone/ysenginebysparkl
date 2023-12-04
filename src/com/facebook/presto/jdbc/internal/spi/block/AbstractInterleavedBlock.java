/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
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
/*     */ public abstract class AbstractInterleavedBlock
/*     */   implements Block
/*     */ {
/*     */   private final int columns;
/*     */   
/*     */   protected abstract Block getBlock(int paramInt);
/*     */   
/*     */   protected abstract int toAbsolutePosition(int paramInt);
/*     */   
/*     */   public abstract InterleavedBlockEncoding getEncoding();
/*     */   
/*     */   protected AbstractInterleavedBlock(int columns)
/*     */   {
/*  35 */     if (columns <= 0) {
/*  36 */       throw new IllegalArgumentException("Number of blocks in InterleavedBlock must be positive");
/*     */     }
/*  38 */     this.columns = columns;
/*     */   }
/*     */   
/*     */   int getBlockCount()
/*     */   {
/*  43 */     return this.columns;
/*     */   }
/*     */   
/*     */   Block[] computeSerializableSubBlocks()
/*     */   {
/*  48 */     InterleavedBlock interleavedBlock = (InterleavedBlock)sliceRange(0, getPositionCount(), false);
/*  49 */     Block[] result = new Block[interleavedBlock.getBlockCount()];
/*  50 */     for (int i = 0; i < result.length; i++) {
/*  51 */       result[i] = interleavedBlock.getBlock(i);
/*     */     }
/*  53 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InterleavedBlockEncoding computeBlockEncoding()
/*     */   {
/*  61 */     BlockEncoding[] individualBlockEncodings = new BlockEncoding[this.columns];
/*  62 */     for (int i = 0; i < this.columns; i++) {
/*  63 */       Block block = getBlock(i);
/*  64 */       individualBlockEncodings[i] = block.getEncoding();
/*     */     }
/*  66 */     return new InterleavedBlockEncoding(individualBlockEncodings);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writePositionTo(int position, BlockBuilder blockBuilder)
/*     */   {
/*  72 */     position = toAbsolutePosition(position);
/*  73 */     int blockIndex = position % this.columns;
/*  74 */     int positionInBlock = position / this.columns;
/*     */     
/*  76 */     getBlock(blockIndex).writePositionTo(positionInBlock, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getByte(int position, int offset)
/*     */   {
/*  82 */     position = toAbsolutePosition(position);
/*  83 */     int blockIndex = position % this.columns;
/*  84 */     int positionInBlock = position / this.columns;
/*     */     
/*  86 */     return getBlock(blockIndex).getByte(positionInBlock, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public short getShort(int position, int offset)
/*     */   {
/*  92 */     position = toAbsolutePosition(position);
/*  93 */     int blockIndex = position % this.columns;
/*  94 */     int positionInBlock = position / this.columns;
/*     */     
/*  96 */     return getBlock(blockIndex).getShort(positionInBlock, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInt(int position, int offset)
/*     */   {
/* 102 */     position = toAbsolutePosition(position);
/* 103 */     int blockIndex = position % this.columns;
/* 104 */     int positionInBlock = position / this.columns;
/*     */     
/* 106 */     return getBlock(blockIndex).getInt(positionInBlock, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLong(int position, int offset)
/*     */   {
/* 112 */     position = toAbsolutePosition(position);
/* 113 */     int blockIndex = position % this.columns;
/* 114 */     int positionInBlock = position / this.columns;
/*     */     
/* 116 */     return getBlock(blockIndex).getLong(positionInBlock, offset);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getSlice(int position, int offset, int length)
/*     */   {
/* 122 */     position = toAbsolutePosition(position);
/* 123 */     int blockIndex = position % this.columns;
/* 124 */     int positionInBlock = position / this.columns;
/*     */     
/* 126 */     return getBlock(blockIndex).getSlice(positionInBlock, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getObject(int position, Class<T> clazz)
/*     */   {
/* 132 */     position = toAbsolutePosition(position);
/* 133 */     int blockIndex = position % this.columns;
/* 134 */     int positionInBlock = position / this.columns;
/*     */     
/* 136 */     return (T)getBlock(blockIndex).getObject(positionInBlock, clazz);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength(int position)
/*     */   {
/* 142 */     position = toAbsolutePosition(position);
/* 143 */     int blockIndex = position % this.columns;
/* 144 */     int positionInBlock = position / this.columns;
/*     */     
/* 146 */     return getBlock(blockIndex).getLength(positionInBlock);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(int position, int offset, Block otherBlock, int otherPosition, int otherOffset, int length)
/*     */   {
/* 152 */     position = toAbsolutePosition(position);
/* 153 */     int blockIndex = position % this.columns;
/* 154 */     int positionInBlock = position / this.columns;
/*     */     
/* 156 */     return getBlock(blockIndex).equals(positionInBlock, offset, otherBlock, otherPosition, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean bytesEqual(int position, int offset, Slice otherSlice, int otherOffset, int length)
/*     */   {
/* 162 */     position = toAbsolutePosition(position);
/* 163 */     int blockIndex = position % this.columns;
/* 164 */     int positionInBlock = position / this.columns;
/*     */     
/* 166 */     return getBlock(blockIndex).bytesEqual(positionInBlock, offset, otherSlice, otherOffset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long hash(int position, int offset, int length)
/*     */   {
/* 172 */     position = toAbsolutePosition(position);
/* 173 */     int blockIndex = position % this.columns;
/* 174 */     int positionInBlock = position / this.columns;
/*     */     
/* 176 */     return getBlock(blockIndex).hash(positionInBlock, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(int position, int offset, int length, Block otherBlock, int otherPosition, int otherOffset, int otherLength)
/*     */   {
/* 182 */     position = toAbsolutePosition(position);
/* 183 */     int blockIndex = position % this.columns;
/* 184 */     int positionInBlock = position / this.columns;
/*     */     
/* 186 */     return getBlock(blockIndex).compareTo(positionInBlock, offset, length, otherBlock, otherPosition, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public int bytesCompare(int position, int offset, int length, Slice otherSlice, int otherOffset, int otherLength)
/*     */   {
/* 192 */     position = toAbsolutePosition(position);
/* 193 */     int blockIndex = position % this.columns;
/* 194 */     int positionInBlock = position / this.columns;
/*     */     
/* 196 */     return getBlock(blockIndex).bytesCompare(positionInBlock, offset, length, otherSlice, otherOffset, otherLength);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytesTo(int position, int offset, int length, BlockBuilder blockBuilder)
/*     */   {
/* 202 */     position = toAbsolutePosition(position);
/* 203 */     int blockIndex = position % this.columns;
/* 204 */     int positionInBlock = position / this.columns;
/*     */     
/* 206 */     getBlock(blockIndex).writeBytesTo(positionInBlock, offset, length, blockBuilder);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block getSingleValueBlock(int position)
/*     */   {
/* 212 */     position = toAbsolutePosition(position);
/* 213 */     int blockIndex = position % this.columns;
/* 214 */     int positionInBlock = position / this.columns;
/*     */     
/*     */ 
/* 217 */     return getBlock(blockIndex).getSingleValueBlock(positionInBlock);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyPositions(List<Integer> positions)
/*     */   {
/* 223 */     if (positions.size() % this.columns != 0) {
/* 224 */       throw new IllegalArgumentException("Positions.size (" + positions.size() + ") is not evenly dividable by columns (" + this.columns + ")");
/*     */     }
/* 226 */     int positionsPerColumn = positions.size() / this.columns;
/*     */     
/* 228 */     List<List<Integer>> valuePositions = new ArrayList(this.columns);
/* 229 */     for (int i = 0; i < this.columns; i++) {
/* 230 */       valuePositions.add(new ArrayList(positionsPerColumn));
/*     */     }
/* 232 */     int ordinal = 0;
/* 233 */     for (Iterator localIterator = positions.iterator(); localIterator.hasNext();) { int position = ((Integer)localIterator.next()).intValue();
/* 234 */       position = toAbsolutePosition(position);
/* 235 */       if (ordinal % this.columns != position % this.columns) {
/* 236 */         throw new IllegalArgumentException("Position (" + position + ") is not congruent to ordinal (" + ordinal + ") modulo columns (" + this.columns + ")");
/*     */       }
/* 238 */       ((List)valuePositions.get(position % this.columns)).add(Integer.valueOf(position / this.columns));
/* 239 */       ordinal++;
/*     */     }
/* 241 */     Block[] blocks = new Block[this.columns];
/* 242 */     for (int i = 0; i < this.columns; i++) {
/* 243 */       blocks[i] = getBlock(i).copyPositions((List)valuePositions.get(i));
/*     */     }
/* 245 */     return new InterleavedBlock(blocks);
/*     */   }
/*     */   
/*     */ 
/*     */   public Block copyRegion(int position, int length)
/*     */   {
/* 251 */     validateRange(position, length);
/* 252 */     return sliceRange(position, length, true);
/*     */   }
/*     */   
/*     */   protected void validateRange(int position, int length)
/*     */   {
/* 257 */     int positionCount = getPositionCount();
/* 258 */     if ((position < 0) || (length < 0) || (position + length > positionCount) || (position % this.columns != 0) || (length % this.columns != 0)) {
/* 259 */       throw new IndexOutOfBoundsException("Invalid position (" + position + "), length (" + length + ") in InterleavedBlock with " + positionCount + " positions and " + this.columns + " columns");
/*     */     }
/*     */   }
/*     */   
/*     */   protected Block sliceRange(int position, int length, boolean compact)
/*     */   {
/* 265 */     position = toAbsolutePosition(position);
/* 266 */     Block[] resultBlocks = new Block[this.columns];
/* 267 */     int positionInBlock = position / this.columns;
/* 268 */     int subBlockLength = length / this.columns;
/* 269 */     for (int blockIndex = 0; blockIndex < this.columns; blockIndex++) {
/* 270 */       if (compact) {
/* 271 */         resultBlocks[blockIndex] = getBlock((blockIndex + position) % this.columns).copyRegion(positionInBlock, subBlockLength);
/*     */       }
/*     */       else {
/* 274 */         resultBlocks[blockIndex] = getBlock((blockIndex + position) % this.columns).getRegion(positionInBlock, subBlockLength);
/*     */       }
/*     */     }
/* 277 */     return new InterleavedBlock(resultBlocks);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNull(int position)
/*     */   {
/* 283 */     position = toAbsolutePosition(position);
/* 284 */     int blockIndex = position % this.columns;
/* 285 */     int positionInBlock = position / this.columns;
/*     */     
/* 287 */     return getBlock(blockIndex).isNull(positionInBlock);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\AbstractInterleavedBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */