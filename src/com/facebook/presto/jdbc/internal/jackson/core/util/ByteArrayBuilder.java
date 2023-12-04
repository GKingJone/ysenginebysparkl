/*     */ package com.facebook.presto.jdbc.internal.jackson.core.util;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.util.LinkedList;
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
/*     */ public final class ByteArrayBuilder
/*     */   extends OutputStream
/*     */ {
/*  26 */   public static final byte[] NO_BYTES = new byte[0];
/*     */   
/*     */ 
/*     */   private static final int INITIAL_BLOCK_SIZE = 500;
/*     */   
/*     */ 
/*     */   private static final int MAX_BLOCK_SIZE = 262144;
/*     */   
/*     */ 
/*     */   static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
/*     */   
/*     */   private final BufferRecycler _bufferRecycler;
/*     */   
/*  39 */   private final LinkedList<byte[]> _pastBlocks = new LinkedList();
/*     */   
/*     */   private int _pastLen;
/*     */   
/*     */   private byte[] _currBlock;
/*     */   private int _currBlockPtr;
/*     */   
/*  46 */   public ByteArrayBuilder() { this(null); }
/*  47 */   public ByteArrayBuilder(BufferRecycler br) { this(br, 500); }
/*  48 */   public ByteArrayBuilder(int firstBlockSize) { this(null, firstBlockSize); }
/*     */   
/*     */   public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
/*  51 */     this._bufferRecycler = br;
/*  52 */     this._currBlock = (br == null ? new byte[firstBlockSize] : br.allocByteBuffer(2));
/*     */   }
/*     */   
/*     */   public void reset() {
/*  56 */     this._pastLen = 0;
/*  57 */     this._currBlockPtr = 0;
/*     */     
/*  59 */     if (!this._pastBlocks.isEmpty()) {
/*  60 */       this._pastBlocks.clear();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void release()
/*     */   {
/*  70 */     reset();
/*  71 */     if ((this._bufferRecycler != null) && (this._currBlock != null)) {
/*  72 */       this._bufferRecycler.releaseByteBuffer(2, this._currBlock);
/*  73 */       this._currBlock = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void append(int i) {
/*  78 */     if (this._currBlockPtr >= this._currBlock.length) {
/*  79 */       _allocMore();
/*     */     }
/*  81 */     this._currBlock[(this._currBlockPtr++)] = ((byte)i);
/*     */   }
/*     */   
/*     */   public void appendTwoBytes(int b16) {
/*  85 */     if (this._currBlockPtr + 1 < this._currBlock.length) {
/*  86 */       this._currBlock[(this._currBlockPtr++)] = ((byte)(b16 >> 8));
/*  87 */       this._currBlock[(this._currBlockPtr++)] = ((byte)b16);
/*     */     } else {
/*  89 */       append(b16 >> 8);
/*  90 */       append(b16);
/*     */     }
/*     */   }
/*     */   
/*     */   public void appendThreeBytes(int b24) {
/*  95 */     if (this._currBlockPtr + 2 < this._currBlock.length) {
/*  96 */       this._currBlock[(this._currBlockPtr++)] = ((byte)(b24 >> 16));
/*  97 */       this._currBlock[(this._currBlockPtr++)] = ((byte)(b24 >> 8));
/*  98 */       this._currBlock[(this._currBlockPtr++)] = ((byte)b24);
/*     */     } else {
/* 100 */       append(b24 >> 16);
/* 101 */       append(b24 >> 8);
/* 102 */       append(b24);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] toByteArray()
/*     */   {
/* 112 */     int totalLen = this._pastLen + this._currBlockPtr;
/*     */     
/* 114 */     if (totalLen == 0) {
/* 115 */       return NO_BYTES;
/*     */     }
/*     */     
/* 118 */     byte[] result = new byte[totalLen];
/* 119 */     int offset = 0;
/*     */     
/* 121 */     for (byte[] block : this._pastBlocks) {
/* 122 */       int len = block.length;
/* 123 */       System.arraycopy(block, 0, result, offset, len);
/* 124 */       offset += len;
/*     */     }
/* 126 */     System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
/* 127 */     offset += this._currBlockPtr;
/* 128 */     if (offset != totalLen) {
/* 129 */       throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset + " bytes");
/*     */     }
/*     */     
/* 132 */     if (!this._pastBlocks.isEmpty()) {
/* 133 */       reset();
/*     */     }
/* 135 */     return result;
/*     */   }
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
/*     */   public byte[] resetAndGetFirstSegment()
/*     */   {
/* 149 */     reset();
/* 150 */     return this._currBlock;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] finishCurrentSegment()
/*     */   {
/* 159 */     _allocMore();
/* 160 */     return this._currBlock;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] completeAndCoalesce(int lastBlockLength)
/*     */   {
/* 173 */     this._currBlockPtr = lastBlockLength;
/* 174 */     return toByteArray();
/*     */   }
/*     */   
/* 177 */   public byte[] getCurrentSegment() { return this._currBlock; }
/* 178 */   public void setCurrentSegmentLength(int len) { this._currBlockPtr = len; }
/* 179 */   public int getCurrentSegmentLength() { return this._currBlockPtr; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(byte[] b)
/*     */   {
/* 189 */     write(b, 0, b.length);
/*     */   }
/*     */   
/*     */   public void write(byte[] b, int off, int len)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 196 */       int max = this._currBlock.length - this._currBlockPtr;
/* 197 */       int toCopy = Math.min(max, len);
/* 198 */       if (toCopy > 0) {
/* 199 */         System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
/* 200 */         off += toCopy;
/* 201 */         this._currBlockPtr += toCopy;
/* 202 */         len -= toCopy;
/*     */       }
/* 204 */       if (len <= 0) break;
/* 205 */       _allocMore();
/*     */     }
/*     */   }
/*     */   
/*     */   public void write(int b)
/*     */   {
/* 211 */     append(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void flush() {}
/*     */   
/*     */ 
/*     */   private void _allocMore()
/*     */   {
/* 225 */     this._pastLen += this._currBlock.length;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 233 */     int newSize = Math.max(this._pastLen >> 1, 1000);
/*     */     
/* 235 */     if (newSize > 262144) {
/* 236 */       newSize = 262144;
/*     */     }
/* 238 */     this._pastBlocks.add(this._currBlock);
/* 239 */     this._currBlock = new byte[newSize];
/* 240 */     this._currBlockPtr = 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\util\ByteArrayBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */