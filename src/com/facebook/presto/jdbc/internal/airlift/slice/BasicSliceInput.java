/*     */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
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
/*     */ public final class BasicSliceInput
/*     */   extends FixedLengthSliceInput
/*     */ {
/*     */   private final Slice slice;
/*     */   private int position;
/*     */   
/*     */   public BasicSliceInput(Slice slice)
/*     */   {
/*  37 */     this.slice = ((Slice)Objects.requireNonNull(slice, "slice is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public long length()
/*     */   {
/*  43 */     return this.slice.length();
/*     */   }
/*     */   
/*     */ 
/*     */   public long position()
/*     */   {
/*  49 */     return this.position;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPosition(long position)
/*     */   {
/*  55 */     Preconditions.checkPositionIndex(position, this.slice.length());
/*  56 */     this.position = ((int)position);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReadable()
/*     */   {
/*  62 */     return this.position < this.slice.length();
/*     */   }
/*     */   
/*     */ 
/*     */   public int available()
/*     */   {
/*  68 */     return this.slice.length() - this.position;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean readBoolean()
/*     */   {
/*  74 */     return readByte() != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int read()
/*     */   {
/*  80 */     if (this.position >= this.slice.length()) {
/*  81 */       return -1;
/*     */     }
/*  83 */     int result = this.slice.getByte(this.position) & 0xFF;
/*  84 */     this.position += 1;
/*  85 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte readByte()
/*     */   {
/*  91 */     int value = read();
/*  92 */     if (value == -1) {
/*  93 */       throw new IndexOutOfBoundsException();
/*     */     }
/*  95 */     return (byte)value;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedByte()
/*     */   {
/* 101 */     return readByte() & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public short readShort()
/*     */   {
/* 107 */     short v = this.slice.getShort(this.position);
/* 108 */     this.position += 2;
/* 109 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readUnsignedShort()
/*     */   {
/* 115 */     return readShort() & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int readInt()
/*     */   {
/* 121 */     int v = this.slice.getInt(this.position);
/* 122 */     this.position += 4;
/* 123 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public long readLong()
/*     */   {
/* 129 */     long v = this.slice.getLong(this.position);
/* 130 */     this.position += 8;
/* 131 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public float readFloat()
/*     */   {
/* 137 */     float v = this.slice.getFloat(this.position);
/* 138 */     this.position += 4;
/* 139 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public double readDouble()
/*     */   {
/* 145 */     double v = this.slice.getDouble(this.position);
/* 146 */     this.position += 8;
/* 147 */     return v;
/*     */   }
/*     */   
/*     */ 
/*     */   public Slice readSlice(int length)
/*     */   {
/* 153 */     if (length == 0) {
/* 154 */       return Slices.EMPTY_SLICE;
/*     */     }
/* 156 */     Slice newSlice = this.slice.slice(this.position, length);
/* 157 */     this.position += length;
/* 158 */     return newSlice;
/*     */   }
/*     */   
/*     */ 
/*     */   public int read(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 164 */     if (length == 0) {
/* 165 */       return 0;
/*     */     }
/*     */     
/* 168 */     length = Math.min(length, available());
/* 169 */     if (length == 0) {
/* 170 */       return -1;
/*     */     }
/* 172 */     readBytes(destination, destinationIndex, length);
/* 173 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(byte[] destination, int destinationIndex, int length)
/*     */   {
/* 179 */     this.slice.getBytes(this.position, destination, destinationIndex, length);
/* 180 */     this.position += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(Slice destination, int destinationIndex, int length)
/*     */   {
/* 186 */     this.slice.getBytes(this.position, destination, destinationIndex, length);
/* 187 */     this.position += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public void readBytes(OutputStream out, int length)
/*     */     throws IOException
/*     */   {
/* 194 */     this.slice.getBytes(this.position, out, length);
/* 195 */     this.position += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public long skip(long length)
/*     */   {
/* 201 */     length = Math.min(length, available());
/* 202 */     this.position = ((int)(this.position + length));
/* 203 */     return length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int skipBytes(int length)
/*     */   {
/* 209 */     length = Math.min(length, available());
/* 210 */     this.position += length;
/* 211 */     return length;
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
/*     */   public Slice slice()
/*     */   {
/* 224 */     return this.slice.slice(this.position, this.slice.length() - this.position);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(Charset charset)
/*     */   {
/* 236 */     return this.slice.toString(this.position, available(), charset);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 242 */     StringBuilder builder = new StringBuilder("BasicSliceInput{");
/* 243 */     builder.append("position=").append(this.position);
/* 244 */     builder.append(", capacity=").append(this.slice.length());
/* 245 */     builder.append('}');
/* 246 */     return builder.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\BasicSliceInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */