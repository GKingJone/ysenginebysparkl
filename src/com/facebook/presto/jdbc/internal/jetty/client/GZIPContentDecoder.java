/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.ZipException;
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
/*     */ public class GZIPContentDecoder
/*     */   implements ContentDecoder
/*     */ {
/*  34 */   private final Inflater inflater = new Inflater(true);
/*     */   private final byte[] bytes;
/*     */   private byte[] output;
/*     */   private State state;
/*     */   private int size;
/*     */   private int value;
/*     */   private byte flags;
/*     */   
/*     */   public GZIPContentDecoder()
/*     */   {
/*  44 */     this(2048);
/*     */   }
/*     */   
/*     */   public GZIPContentDecoder(int bufferSize)
/*     */   {
/*  49 */     this.bytes = new byte[bufferSize];
/*  50 */     reset();
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
/*     */ 
/*     */ 
/*     */   public ByteBuffer decode(ByteBuffer buffer)
/*     */   {
/*     */     try
/*     */     {
/*  68 */       while (buffer.hasRemaining())
/*     */       {
/*  70 */         byte currByte = buffer.get();
/*  71 */         switch (this.state)
/*     */         {
/*     */ 
/*     */         case INITIAL: 
/*  75 */           buffer.position(buffer.position() - 1);
/*  76 */           this.state = State.ID;
/*  77 */           break;
/*     */         
/*     */ 
/*     */         case ID: 
/*  81 */           this.value += ((currByte & 0xFF) << 8 * this.size);
/*  82 */           this.size += 1;
/*  83 */           if (this.size == 2)
/*     */           {
/*  85 */             if (this.value != 35615)
/*  86 */               throw new ZipException("Invalid gzip bytes");
/*  87 */             this.state = State.CM;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case CM: 
/*  93 */           if ((currByte & 0xFF) != 8)
/*  94 */             throw new ZipException("Invalid gzip compression method");
/*  95 */           this.state = State.FLG;
/*  96 */           break;
/*     */         
/*     */ 
/*     */         case FLG: 
/* 100 */           this.flags = currByte;
/* 101 */           this.state = State.MTIME;
/* 102 */           this.size = 0;
/* 103 */           this.value = 0;
/* 104 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */         case MTIME: 
/* 109 */           this.size += 1;
/* 110 */           if (this.size == 4) {
/* 111 */             this.state = State.XFL;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case XFL: 
/* 117 */           this.state = State.OS;
/* 118 */           break;
/*     */         
/*     */ 
/*     */ 
/*     */         case OS: 
/* 123 */           this.state = State.FLAGS;
/* 124 */           break;
/*     */         
/*     */ 
/*     */         case FLAGS: 
/* 128 */           buffer.position(buffer.position() - 1);
/* 129 */           if ((this.flags & 0x4) == 4)
/*     */           {
/* 131 */             this.state = State.EXTRA_LENGTH;
/* 132 */             this.size = 0;
/* 133 */             this.value = 0;
/*     */           }
/* 135 */           else if ((this.flags & 0x8) == 8) {
/* 136 */             this.state = State.NAME;
/* 137 */           } else if ((this.flags & 0x10) == 16) {
/* 138 */             this.state = State.COMMENT;
/* 139 */           } else if ((this.flags & 0x2) == 2)
/*     */           {
/* 141 */             this.state = State.HCRC;
/* 142 */             this.size = 0;
/* 143 */             this.value = 0;
/*     */           }
/*     */           else {
/* 146 */             this.state = State.DATA; }
/* 147 */           break;
/*     */         
/*     */ 
/*     */         case EXTRA_LENGTH: 
/* 151 */           this.value += ((currByte & 0xFF) << 8 * this.size);
/* 152 */           this.size += 1;
/* 153 */           if (this.size == 2) {
/* 154 */             this.state = State.EXTRA;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case EXTRA: 
/* 160 */           this.value -= 1;
/* 161 */           if (this.value == 0)
/*     */           {
/*     */ 
/* 164 */             this.flags = ((byte)(this.flags & 0xFFFFFFFB));
/* 165 */             this.state = State.FLAGS;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case NAME: 
/* 172 */           if (currByte == 0)
/*     */           {
/*     */ 
/* 175 */             this.flags = ((byte)(this.flags & 0xFFFFFFF7));
/* 176 */             this.state = State.FLAGS;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case COMMENT: 
/* 183 */           if (currByte == 0)
/*     */           {
/*     */ 
/* 186 */             this.flags = ((byte)(this.flags & 0xFFFFFFEF));
/* 187 */             this.state = State.FLAGS;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case HCRC: 
/* 194 */           this.size += 1;
/* 195 */           if (this.size == 2)
/*     */           {
/*     */ 
/* 198 */             this.flags = ((byte)(this.flags & 0xFFFFFFFD));
/* 199 */             this.state = State.FLAGS;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case DATA: 
/* 205 */           buffer.position(buffer.position() - 1);
/*     */           for (;;)
/*     */           {
/* 208 */             int decoded = inflate(this.bytes);
/* 209 */             if (decoded == 0)
/*     */             {
/* 211 */               if (this.inflater.needsInput())
/*     */               {
/* 213 */                 if (buffer.hasRemaining())
/*     */                 {
/* 215 */                   byte[] input = new byte[buffer.remaining()];
/* 216 */                   buffer.get(input);
/* 217 */                   this.inflater.setInput(input);
/*     */                 }
/*     */                 else
/*     */                 {
/* 221 */                   if (this.output == null)
/*     */                     break;
/* 223 */                   ByteBuffer result = ByteBuffer.wrap(this.output);
/* 224 */                   this.output = null;
/* 225 */                   return result;
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 230 */                 if (this.inflater.finished())
/*     */                 {
/* 232 */                   int remaining = this.inflater.getRemaining();
/* 233 */                   buffer.position(buffer.limit() - remaining);
/* 234 */                   this.state = State.CRC;
/* 235 */                   this.size = 0;
/* 236 */                   this.value = 0;
/* 237 */                   break;
/*     */                 }
/*     */                 
/*     */ 
/* 241 */                 throw new ZipException("Invalid inflater state");
/*     */               }
/*     */               
/*     */ 
/*     */             }
/* 246 */             else if (this.output == null)
/*     */             {
/*     */ 
/* 249 */               this.output = Arrays.copyOf(this.bytes, decoded);
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 254 */               byte[] newOutput = Arrays.copyOf(this.output, this.output.length + decoded);
/* 255 */               System.arraycopy(this.bytes, 0, newOutput, this.output.length, decoded);
/* 256 */               this.output = newOutput;
/*     */             }
/*     */           }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         case CRC: 
/* 264 */           this.value += ((currByte & 0xFF) << 8 * this.size);
/* 265 */           this.size += 1;
/* 266 */           if (this.size == 4)
/*     */           {
/*     */ 
/* 269 */             this.state = State.ISIZE;
/* 270 */             this.size = 0;
/* 271 */             this.value = 0;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case ISIZE: 
/* 277 */           this.value += ((currByte & 0xFF) << 8 * this.size);
/* 278 */           this.size += 1;
/* 279 */           if (this.size == 4)
/*     */           {
/* 281 */             if (this.value != this.inflater.getBytesWritten()) {
/* 282 */               throw new ZipException("Invalid input size");
/*     */             }
/* 284 */             ByteBuffer result = this.output == null ? BufferUtil.EMPTY_BUFFER : ByteBuffer.wrap(this.output);
/* 285 */             reset();
/* 286 */             return result;
/*     */           }
/*     */           
/*     */           break;
/*     */         default: 
/* 291 */           throw new ZipException(); }
/*     */         
/*     */       }
/* 294 */       return BufferUtil.EMPTY_BUFFER;
/*     */     }
/*     */     catch (ZipException x)
/*     */     {
/* 298 */       throw new RuntimeException(x);
/*     */     }
/*     */   }
/*     */   
/*     */   private int inflate(byte[] bytes) throws ZipException
/*     */   {
/*     */     try
/*     */     {
/* 306 */       return this.inflater.inflate(bytes);
/*     */     }
/*     */     catch (DataFormatException x)
/*     */     {
/* 310 */       throw new ZipException(x.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/* 316 */     this.inflater.reset();
/* 317 */     Arrays.fill(this.bytes, (byte)0);
/* 318 */     this.output = null;
/* 319 */     this.state = State.INITIAL;
/* 320 */     this.size = 0;
/* 321 */     this.value = 0;
/* 322 */     this.flags = 0;
/*     */   }
/*     */   
/*     */   protected boolean isFinished()
/*     */   {
/* 327 */     return this.state == State.INITIAL;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Factory
/*     */     extends ContentDecoder.Factory
/*     */   {
/*     */     private final int bufferSize;
/*     */     
/*     */ 
/*     */     public Factory()
/*     */     {
/* 339 */       this(2048);
/*     */     }
/*     */     
/*     */     public Factory(int bufferSize)
/*     */     {
/* 344 */       super();
/* 345 */       this.bufferSize = bufferSize;
/*     */     }
/*     */     
/*     */ 
/*     */     public ContentDecoder newContentDecoder()
/*     */     {
/* 351 */       return new GZIPContentDecoder(this.bufferSize);
/*     */     }
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 357 */     INITIAL,  ID,  CM,  FLG,  MTIME,  XFL,  OS,  FLAGS,  EXTRA_LENGTH,  EXTRA,  NAME,  COMMENT,  HCRC,  DATA,  CRC,  ISIZE;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\GZIPContentDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */