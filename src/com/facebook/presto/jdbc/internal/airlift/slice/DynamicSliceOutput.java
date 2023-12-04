/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DynamicSliceOutput
/*     */   extends SliceOutput
/*     */ {
/*  33 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(DynamicSliceOutput.class).instanceSize();
/*     */   
/*     */   private Slice slice;
/*     */   private int size;
/*     */   
/*     */   public DynamicSliceOutput(int estimatedSize)
/*     */   {
/*  40 */     this.slice = Slices.allocate(estimatedSize);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  46 */     this.size = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(int position)
/*     */   {
/*  52 */     Preconditions.checkArgument(position >= 0, "position is negative");
/*  53 */     Preconditions.checkArgument(position <= this.size, "position is larger than size");
/*  54 */     this.size = position;
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  60 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSize()
/*     */   {
/*  66 */     return this.slice.getRetainedSize() + INSTANCE_SIZE;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWritable()
/*     */   {
/*  72 */     return writableBytes() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int writableBytes()
/*     */   {
/*  78 */     return this.slice.length() - this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeByte(int value)
/*     */   {
/*  84 */     this.slice = Slices.ensureSize(this.slice, this.size + 1);
/*  85 */     this.slice.setByte(this.size, value);
/*  86 */     this.size += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeShort(int value)
/*     */   {
/*  92 */     this.slice = Slices.ensureSize(this.slice, this.size + 2);
/*  93 */     this.slice.setShort(this.size, value);
/*  94 */     this.size += 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeInt(int value)
/*     */   {
/* 100 */     this.slice = Slices.ensureSize(this.slice, this.size + 4);
/* 101 */     this.slice.setInt(this.size, value);
/* 102 */     this.size += 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(long value)
/*     */   {
/* 108 */     this.slice = Slices.ensureSize(this.slice, this.size + 8);
/* 109 */     this.slice.setLong(this.size, value);
/* 110 */     this.size += 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFloat(float value)
/*     */   {
/* 116 */     this.slice = Slices.ensureSize(this.slice, this.size + 4);
/* 117 */     this.slice.setFloat(this.size, value);
/* 118 */     this.size += 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeDouble(double value)
/*     */   {
/* 124 */     this.slice = Slices.ensureSize(this.slice, this.size + 8);
/* 125 */     this.slice.setDouble(this.size, value);
/* 126 */     this.size += 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(byte[] source)
/*     */   {
/* 132 */     writeBytes(source, 0, source.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 138 */     this.slice = Slices.ensureSize(this.slice, this.size + length);
/* 139 */     this.slice.setBytes(this.size, source, sourceIndex, length);
/* 140 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(Slice source)
/*     */   {
/* 146 */     writeBytes(source, 0, source.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(Slice source, int sourceIndex, int length)
/*     */   {
/* 152 */     this.slice = Slices.ensureSize(this.slice, this.size + length);
/* 153 */     this.slice.setBytes(this.size, source, sourceIndex, length);
/* 154 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(InputStream in, int length)
/*     */     throws IOException
/*     */   {
/* 161 */     this.slice = Slices.ensureSize(this.slice, this.size + length);
/* 162 */     this.slice.setBytes(this.size, in, length);
/* 163 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeZero(int length)
/*     */   {
/* 169 */     this.slice = Slices.ensureSize(this.slice, this.size + length);
/* 170 */     super.writeZero(length);
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendLong(long value)
/*     */   {
/* 176 */     writeLong(value);
/* 177 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendDouble(double value)
/*     */   {
/* 183 */     writeDouble(value);
/* 184 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendInt(int value)
/*     */   {
/* 190 */     writeInt(value);
/* 191 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendShort(int value)
/*     */   {
/* 197 */     writeShort(value);
/* 198 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendByte(int value)
/*     */   {
/* 204 */     writeByte(value);
/* 205 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 211 */     write(source, sourceIndex, length);
/* 212 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendBytes(byte[] source)
/*     */   {
/* 218 */     writeBytes(source);
/* 219 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public DynamicSliceOutput appendBytes(Slice slice)
/*     */   {
/* 225 */     writeBytes(slice);
/* 226 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice slice()
/*     */   {
/* 232 */     return this.slice.slice(0, this.size);
/*     */   }
/*     */   
/*     */   public Slice copySlice()
/*     */   {
/* 237 */     Slice copy = Slices.allocate(this.size);
/* 238 */     this.slice.getBytes(0, copy);
/* 239 */     return copy;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getUnderlyingSlice()
/*     */   {
/* 245 */     return this.slice;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 251 */     StringBuilder builder = new StringBuilder("BasicSliceOutput{");
/* 252 */     builder.append("size=").append(this.size);
/* 253 */     builder.append(", capacity=").append(this.slice.length());
/* 254 */     builder.append('}');
/* 255 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString(Charset charset)
/*     */   {
/* 261 */     return this.slice.toString(0, this.size, charset);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\DynamicSliceOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */