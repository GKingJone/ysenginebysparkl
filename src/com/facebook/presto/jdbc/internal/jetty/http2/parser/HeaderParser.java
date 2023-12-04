/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeaderParser
/*     */ {
/*  32 */   private State state = State.LENGTH;
/*     */   
/*     */   private int cursor;
/*     */   private int length;
/*     */   private int type;
/*     */   private int flags;
/*     */   private int streamId;
/*     */   
/*     */   protected void reset()
/*     */   {
/*  42 */     this.state = State.LENGTH;
/*  43 */     this.cursor = 0;
/*     */     
/*  45 */     this.length = 0;
/*  46 */     this.type = 0;
/*  47 */     this.flags = 0;
/*  48 */     this.streamId = 0;
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
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  62 */     while (buffer.hasRemaining())
/*     */     {
/*  64 */       switch (this.state)
/*     */       {
/*     */ 
/*     */       case LENGTH: 
/*  68 */         int octet = buffer.get() & 0xFF;
/*  69 */         this.length = ((this.length << 8) + octet);
/*  70 */         if (++this.cursor == 3)
/*     */         {
/*  72 */           this.length &= 0xFFFFFF;
/*  73 */           this.state = State.TYPE;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case TYPE: 
/*  79 */         this.type = (buffer.get() & 0xFF);
/*  80 */         this.state = State.FLAGS;
/*  81 */         break;
/*     */       
/*     */ 
/*     */       case FLAGS: 
/*  85 */         this.flags = (buffer.get() & 0xFF);
/*  86 */         this.state = State.STREAM_ID;
/*  87 */         break;
/*     */       
/*     */ 
/*     */       case STREAM_ID: 
/*  91 */         if (buffer.remaining() >= 4)
/*     */         {
/*  93 */           this.streamId = buffer.getInt();
/*     */           
/*  95 */           this.streamId &= 0x7FFFFFFF;
/*  96 */           return true;
/*     */         }
/*     */         
/*     */ 
/* 100 */         this.state = State.STREAM_ID_BYTES;
/* 101 */         this.cursor = 4;
/*     */         
/* 103 */         break;
/*     */       
/*     */ 
/*     */       case STREAM_ID_BYTES: 
/* 107 */         int currByte = buffer.get() & 0xFF;
/* 108 */         this.cursor -= 1;
/* 109 */         this.streamId += (currByte << 8 * this.cursor);
/* 110 */         if (this.cursor == 0)
/*     */         {
/*     */ 
/* 113 */           this.streamId &= 0x7FFFFFFF;
/* 114 */           return true;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       default: 
/* 120 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/* 129 */     return this.length;
/*     */   }
/*     */   
/*     */   public int getFrameType()
/*     */   {
/* 134 */     return this.type;
/*     */   }
/*     */   
/*     */   public boolean hasFlag(int bit)
/*     */   {
/* 139 */     return (this.flags & bit) == bit;
/*     */   }
/*     */   
/*     */   public int getStreamId()
/*     */   {
/* 144 */     return this.streamId;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 149 */     LENGTH,  TYPE,  FLAGS,  STREAM_ID,  STREAM_ID_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\HeaderParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */