/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
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
/*     */ public class PushPromiseBodyParser
/*     */   extends BodyParser
/*     */ {
/*     */   private final HeaderBlockParser headerBlockParser;
/*  31 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int length;
/*     */   private int paddingLength;
/*     */   private int streamId;
/*     */   
/*     */   public PushPromiseBodyParser(HeaderParser headerParser, Parser.Listener listener, HeaderBlockParser headerBlockParser)
/*     */   {
/*  39 */     super(headerParser, listener);
/*  40 */     this.headerBlockParser = headerBlockParser;
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  45 */     this.state = State.PREPARE;
/*  46 */     this.cursor = 0;
/*  47 */     this.length = 0;
/*  48 */     this.paddingLength = 0;
/*  49 */     this.streamId = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  55 */     boolean loop = false;
/*  56 */     while ((buffer.hasRemaining()) || (loop))
/*     */     {
/*  58 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  63 */         if (getStreamId() == 0) {
/*  64 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_push_promise_frame");
/*     */         }
/*     */         
/*  67 */         if (!hasFlag(4)) {
/*  68 */           return connectionFailure(buffer, ErrorCode.INTERNAL_ERROR.code, "unsupported_push_promise_frame");
/*     */         }
/*  70 */         this.length = getBodyLength();
/*     */         
/*  72 */         if (isPadding())
/*     */         {
/*  74 */           this.state = State.PADDING_LENGTH;
/*     */         }
/*     */         else
/*     */         {
/*  78 */           this.state = State.STREAM_ID;
/*     */         }
/*  80 */         break;
/*     */       
/*     */ 
/*     */       case PADDING_LENGTH: 
/*  84 */         this.paddingLength = (buffer.get() & 0xFF);
/*  85 */         this.length -= 1;
/*  86 */         this.length -= this.paddingLength;
/*  87 */         this.state = State.STREAM_ID;
/*  88 */         if (this.length < 4) {
/*  89 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_push_promise_frame");
/*     */         }
/*     */         
/*     */         break;
/*     */       case STREAM_ID: 
/*  94 */         if (buffer.remaining() >= 4)
/*     */         {
/*  96 */           this.streamId = buffer.getInt();
/*  97 */           this.streamId &= 0x7FFFFFFF;
/*  98 */           this.length -= 4;
/*  99 */           this.state = State.HEADERS;
/* 100 */           loop = this.length == 0;
/*     */         }
/*     */         else
/*     */         {
/* 104 */           this.state = State.STREAM_ID_BYTES;
/* 105 */           this.cursor = 4;
/*     */         }
/* 107 */         break;
/*     */       
/*     */ 
/*     */       case STREAM_ID_BYTES: 
/* 111 */         int currByte = buffer.get() & 0xFF;
/* 112 */         this.cursor -= 1;
/* 113 */         this.streamId += (currByte << 8 * this.cursor);
/* 114 */         this.length -= 1;
/* 115 */         if ((this.cursor > 0) && (this.length <= 0))
/* 116 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_push_promise_frame");
/* 117 */         if (this.cursor == 0)
/*     */         {
/* 119 */           this.streamId &= 0x7FFFFFFF;
/* 120 */           this.state = State.HEADERS;
/* 121 */           loop = this.length == 0;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case HEADERS: 
/* 127 */         MetaData metaData = this.headerBlockParser.parse(buffer, this.length);
/* 128 */         if (metaData != null)
/*     */         {
/* 130 */           this.state = State.PADDING;
/* 131 */           loop = this.paddingLength == 0;
/* 132 */           onPushPromise(this.streamId, metaData);
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case PADDING: 
/* 138 */         int size = Math.min(buffer.remaining(), this.paddingLength);
/* 139 */         buffer.position(buffer.position() + size);
/* 140 */         this.paddingLength -= size;
/* 141 */         if (this.paddingLength == 0)
/*     */         {
/* 143 */           reset();
/* 144 */           return true;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       default: 
/* 150 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 154 */     return false;
/*     */   }
/*     */   
/*     */   private void onPushPromise(int streamId, MetaData metaData)
/*     */   {
/* 159 */     PushPromiseFrame frame = new PushPromiseFrame(getStreamId(), streamId, metaData);
/* 160 */     notifyPushPromise(frame);
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 165 */     PREPARE,  PADDING_LENGTH,  STREAM_ID,  STREAM_ID_BYTES,  HEADERS,  PADDING;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\PushPromiseBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */