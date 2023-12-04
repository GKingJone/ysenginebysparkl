/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
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
/*     */ public class PriorityBodyParser
/*     */   extends BodyParser
/*     */ {
/*  28 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private boolean exclusive;
/*     */   private int parentStreamId;
/*     */   
/*     */   public PriorityBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  35 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  40 */     this.state = State.PREPARE;
/*  41 */     this.cursor = 0;
/*  42 */     this.exclusive = false;
/*  43 */     this.parentStreamId = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  49 */     while (buffer.hasRemaining())
/*     */     {
/*  51 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  56 */         if (getStreamId() == 0)
/*  57 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_priority_frame");
/*  58 */         int length = getBodyLength();
/*  59 */         if (length != 5)
/*  60 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_priority_frame");
/*  61 */         this.state = State.EXCLUSIVE;
/*  62 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       case EXCLUSIVE: 
/*  68 */         int currByte = buffer.get(buffer.position());
/*  69 */         this.exclusive = ((currByte & 0x80) == 128);
/*  70 */         this.state = State.PARENT_STREAM_ID;
/*  71 */         break;
/*     */       
/*     */ 
/*     */       case PARENT_STREAM_ID: 
/*  75 */         if (buffer.remaining() >= 4)
/*     */         {
/*  77 */           this.parentStreamId = buffer.getInt();
/*  78 */           this.parentStreamId &= 0x7FFFFFFF;
/*  79 */           this.state = State.WEIGHT;
/*     */         }
/*     */         else
/*     */         {
/*  83 */           this.state = State.PARENT_STREAM_ID_BYTES;
/*  84 */           this.cursor = 4;
/*     */         }
/*  86 */         break;
/*     */       
/*     */ 
/*     */       case PARENT_STREAM_ID_BYTES: 
/*  90 */         int currByte = buffer.get() & 0xFF;
/*  91 */         this.cursor -= 1;
/*  92 */         this.parentStreamId += (currByte << 8 * this.cursor);
/*  93 */         if (this.cursor == 0)
/*     */         {
/*  95 */           this.parentStreamId &= 0x7FFFFFFF;
/*  96 */           this.state = State.WEIGHT;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case WEIGHT: 
/* 103 */         if (getStreamId() == this.parentStreamId) {
/* 104 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_priority_frame");
/*     */         }
/* 106 */         int weight = (buffer.get() & 0xFF) + 1;
/* 107 */         return onPriority(this.parentStreamId, weight, this.exclusive);
/*     */       
/*     */ 
/*     */       default: 
/* 111 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 115 */     return false;
/*     */   }
/*     */   
/*     */   private boolean onPriority(int parentStreamId, int weight, boolean exclusive)
/*     */   {
/* 120 */     PriorityFrame frame = new PriorityFrame(getStreamId(), parentStreamId, weight, exclusive);
/* 121 */     reset();
/* 122 */     notifyPriority(frame);
/* 123 */     return true;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 128 */     PREPARE,  EXCLUSIVE,  PARENT_STREAM_ID,  PARENT_STREAM_ID_BYTES,  WEIGHT;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\PriorityBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */