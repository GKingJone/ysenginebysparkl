/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
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
/*     */ public class GoAwayBodyParser
/*     */   extends BodyParser
/*     */ {
/*  28 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int length;
/*     */   private int lastStreamId;
/*     */   private int error;
/*     */   private byte[] payload;
/*     */   
/*     */   public GoAwayBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  37 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  42 */     this.state = State.PREPARE;
/*  43 */     this.cursor = 0;
/*  44 */     this.length = 0;
/*  45 */     this.lastStreamId = 0;
/*  46 */     this.error = 0;
/*  47 */     this.payload = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  53 */     while (buffer.hasRemaining())
/*     */     {
/*  55 */       switch (this.state)
/*     */       {
/*     */ 
/*     */       case PREPARE: 
/*  59 */         this.state = State.LAST_STREAM_ID;
/*  60 */         this.length = getBodyLength();
/*  61 */         break;
/*     */       
/*     */ 
/*     */       case LAST_STREAM_ID: 
/*  65 */         if (buffer.remaining() >= 4)
/*     */         {
/*  67 */           this.lastStreamId = buffer.getInt();
/*  68 */           this.lastStreamId &= 0x7FFFFFFF;
/*  69 */           this.state = State.ERROR;
/*  70 */           this.length -= 4;
/*  71 */           if (this.length <= 0) {
/*  72 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_go_away_frame");
/*     */           }
/*     */         }
/*     */         else {
/*  76 */           this.state = State.LAST_STREAM_ID_BYTES;
/*  77 */           this.cursor = 4;
/*     */         }
/*  79 */         break;
/*     */       
/*     */ 
/*     */       case LAST_STREAM_ID_BYTES: 
/*  83 */         int currByte = buffer.get() & 0xFF;
/*  84 */         this.cursor -= 1;
/*  85 */         this.lastStreamId += (currByte << 8 * this.cursor);
/*  86 */         this.length -= 1;
/*  87 */         if ((this.cursor > 0) && (this.length <= 0))
/*  88 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_go_away_frame");
/*  89 */         if (this.cursor == 0)
/*     */         {
/*  91 */           this.lastStreamId &= 0x7FFFFFFF;
/*  92 */           this.state = State.ERROR;
/*  93 */           if (this.length == 0) {
/*  94 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_go_away_frame");
/*     */           }
/*     */         }
/*     */         
/*     */         break;
/*     */       case ERROR: 
/* 100 */         if (buffer.remaining() >= 4)
/*     */         {
/* 102 */           this.error = buffer.getInt();
/* 103 */           this.state = State.PAYLOAD;
/* 104 */           this.length -= 4;
/* 105 */           if (this.length < 0)
/* 106 */             return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_go_away_frame");
/* 107 */           if (this.length == 0) {
/* 108 */             return onGoAway(this.lastStreamId, this.error, null);
/*     */           }
/*     */         }
/*     */         else {
/* 112 */           this.state = State.ERROR_BYTES;
/* 113 */           this.cursor = 4;
/*     */         }
/* 115 */         break;
/*     */       
/*     */ 
/*     */       case ERROR_BYTES: 
/* 119 */         int currByte = buffer.get() & 0xFF;
/* 120 */         this.cursor -= 1;
/* 121 */         this.error += (currByte << 8 * this.cursor);
/* 122 */         this.length -= 1;
/* 123 */         if ((this.cursor > 0) && (this.length <= 0))
/* 124 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_go_away_frame");
/* 125 */         if (this.cursor == 0)
/*     */         {
/* 127 */           this.state = State.PAYLOAD;
/* 128 */           if (this.length == 0) {
/* 129 */             return onGoAway(this.lastStreamId, this.error, null);
/*     */           }
/*     */         }
/*     */         
/*     */         break;
/*     */       case PAYLOAD: 
/* 135 */         this.payload = new byte[this.length];
/* 136 */         if (buffer.remaining() >= this.length)
/*     */         {
/* 138 */           buffer.get(this.payload);
/* 139 */           return onGoAway(this.lastStreamId, this.error, this.payload);
/*     */         }
/*     */         
/*     */ 
/* 143 */         this.state = State.PAYLOAD_BYTES;
/* 144 */         this.cursor = this.length;
/*     */         
/* 146 */         break;
/*     */       
/*     */ 
/*     */       case PAYLOAD_BYTES: 
/* 150 */         this.payload[(this.payload.length - this.cursor)] = buffer.get();
/* 151 */         this.cursor -= 1;
/* 152 */         if (this.cursor == 0) {
/* 153 */           return onGoAway(this.lastStreamId, this.error, this.payload);
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/* 158 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 162 */     return false;
/*     */   }
/*     */   
/*     */   private boolean onGoAway(int lastStreamId, int error, byte[] payload)
/*     */   {
/* 167 */     GoAwayFrame frame = new GoAwayFrame(lastStreamId, error, payload);
/* 168 */     reset();
/* 169 */     notifyGoAway(frame);
/* 170 */     return true;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 175 */     PREPARE,  LAST_STREAM_ID,  LAST_STREAM_ID_BYTES,  ERROR,  ERROR_BYTES,  PAYLOAD,  PAYLOAD_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\GoAwayBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */