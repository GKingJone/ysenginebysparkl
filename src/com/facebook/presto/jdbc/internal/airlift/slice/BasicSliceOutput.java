/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicSliceOutput
/*     */   extends SliceOutput
/*     */ {
/*  34 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(BasicSliceOutput.class).instanceSize();
/*     */   
/*     */   private final Slice slice;
/*     */   private int size;
/*     */   
/*     */   protected BasicSliceOutput(Slice slice)
/*     */   {
/*  41 */     this.slice = ((Slice)Objects.requireNonNull(slice, "slice is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  47 */     this.size = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(int position)
/*     */   {
/*  53 */     Preconditions.checkArgument(position >= 0, "position is negative");
/*  54 */     Preconditions.checkArgument(position <= this.size, "position is larger than size");
/*  55 */     this.size = position;
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  61 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainedSize()
/*     */   {
/*  67 */     return this.slice.getRetainedSize() + INSTANCE_SIZE;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWritable()
/*     */   {
/*  73 */     return writableBytes() > 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int writableBytes()
/*     */   {
/*  79 */     return this.slice.length() - this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeByte(int value)
/*     */   {
/*  85 */     this.slice.setByte(this.size, value);
/*  86 */     this.size += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeShort(int value)
/*     */   {
/*  92 */     this.slice.setShort(this.size, value);
/*  93 */     this.size += 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeInt(int value)
/*     */   {
/*  99 */     this.slice.setInt(this.size, value);
/* 100 */     this.size += 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeLong(long value)
/*     */   {
/* 106 */     this.slice.setLong(this.size, value);
/* 107 */     this.size += 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeFloat(float value)
/*     */   {
/* 113 */     this.slice.setFloat(this.size, value);
/* 114 */     this.size += 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeDouble(double value)
/*     */   {
/* 120 */     this.slice.setDouble(this.size, value);
/* 121 */     this.size += 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 127 */     this.slice.setBytes(this.size, source, sourceIndex, length);
/* 128 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(byte[] source)
/*     */   {
/* 134 */     writeBytes(source, 0, source.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(Slice source)
/*     */   {
/* 140 */     writeBytes(source, 0, source.length());
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(Slice source, int sourceIndex, int length)
/*     */   {
/* 146 */     this.slice.setBytes(this.size, source, sourceIndex, length);
/* 147 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void writeBytes(InputStream in, int length)
/*     */     throws IOException
/*     */   {
/* 154 */     this.slice.setBytes(this.size, in, length);
/* 155 */     this.size += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendLong(long value)
/*     */   {
/* 161 */     writeLong(value);
/* 162 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public SliceOutput appendDouble(double value)
/*     */   {
/* 168 */     writeDouble(value);
/* 169 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendInt(int value)
/*     */   {
/* 175 */     writeInt(value);
/* 176 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendShort(int value)
/*     */   {
/* 182 */     writeShort(value);
/* 183 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendByte(int value)
/*     */   {
/* 189 */     writeByte(value);
/* 190 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendBytes(byte[] source, int sourceIndex, int length)
/*     */   {
/* 196 */     write(source, sourceIndex, length);
/* 197 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendBytes(byte[] source)
/*     */   {
/* 203 */     writeBytes(source);
/* 204 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public BasicSliceOutput appendBytes(Slice slice)
/*     */   {
/* 210 */     writeBytes(slice);
/* 211 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice slice()
/*     */   {
/* 217 */     return this.slice.slice(0, this.size);
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice getUnderlyingSlice()
/*     */   {
/* 223 */     return this.slice;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 229 */     StringBuilder builder = new StringBuilder("BasicSliceOutput{");
/* 230 */     builder.append("size=").append(this.size);
/* 231 */     builder.append(", capacity=").append(this.slice.length());
/* 232 */     builder.append('}');
/* 233 */     return builder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString(Charset charset)
/*     */   {
/* 239 */     return this.slice.toString(0, this.size, charset);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\BasicSliceOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */