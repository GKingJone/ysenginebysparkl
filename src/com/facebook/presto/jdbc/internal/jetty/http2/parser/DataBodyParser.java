/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
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
/*     */ public class DataBodyParser
/*     */   extends BodyParser
/*     */ {
/*  29 */   private State state = State.PREPARE;
/*     */   private int padding;
/*     */   private int paddingLength;
/*     */   private int length;
/*     */   
/*     */   public DataBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  36 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  41 */     this.state = State.PREPARE;
/*  42 */     this.padding = 0;
/*  43 */     this.paddingLength = 0;
/*  44 */     this.length = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void emptyBody(ByteBuffer buffer)
/*     */   {
/*  50 */     if (isPadding()) {
/*  51 */       connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_data_frame");
/*     */     } else {
/*  53 */       onData(BufferUtil.EMPTY_BUFFER, false, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  59 */     boolean loop = false;
/*  60 */     while ((buffer.hasRemaining()) || (loop))
/*     */     {
/*  62 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  67 */         if (getStreamId() == 0) {
/*  68 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_data_frame");
/*     */         }
/*  70 */         this.length = getBodyLength();
/*  71 */         this.state = (isPadding() ? State.PADDING_LENGTH : State.DATA);
/*  72 */         break;
/*     */       
/*     */ 
/*     */       case PADDING_LENGTH: 
/*  76 */         this.padding = 1;
/*  77 */         this.paddingLength = (buffer.get() & 0xFF);
/*  78 */         this.length -= 1;
/*  79 */         this.length -= this.paddingLength;
/*  80 */         this.state = State.DATA;
/*  81 */         loop = this.length == 0;
/*  82 */         if (this.length < 0) {
/*  83 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_data_frame_padding");
/*     */         }
/*     */         
/*     */         break;
/*     */       case DATA: 
/*  88 */         int size = Math.min(buffer.remaining(), this.length);
/*  89 */         int position = buffer.position();
/*  90 */         int limit = buffer.limit();
/*  91 */         buffer.limit(position + size);
/*  92 */         ByteBuffer slice = buffer.slice();
/*  93 */         buffer.limit(limit);
/*  94 */         buffer.position(position + size);
/*     */         
/*  96 */         this.length -= size;
/*  97 */         if (this.length == 0)
/*     */         {
/*  99 */           this.state = State.PADDING;
/* 100 */           loop = this.paddingLength == 0;
/*     */           
/*     */ 
/* 103 */           onData(slice, false, this.padding + this.paddingLength);
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 110 */           onData(slice, true, 0);
/*     */         }
/* 112 */         break;
/*     */       
/*     */ 
/*     */       case PADDING: 
/* 116 */         int size = Math.min(buffer.remaining(), this.paddingLength);
/* 117 */         buffer.position(buffer.position() + size);
/* 118 */         this.paddingLength -= size;
/* 119 */         if (this.paddingLength == 0)
/*     */         {
/* 121 */           reset();
/* 122 */           return true;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       default: 
/* 128 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/* 132 */     return false;
/*     */   }
/*     */   
/*     */   private void onData(ByteBuffer buffer, boolean fragment, int padding)
/*     */   {
/* 137 */     DataFrame frame = new DataFrame(getStreamId(), buffer, (!fragment) && (isEndStream()), padding);
/* 138 */     notifyData(frame);
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 143 */     PREPARE,  PADDING_LENGTH,  DATA,  PADDING;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\DataBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */