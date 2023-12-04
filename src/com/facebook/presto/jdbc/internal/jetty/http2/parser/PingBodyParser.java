/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
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
/*     */ public class PingBodyParser
/*     */   extends BodyParser
/*     */ {
/*  29 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private byte[] payload;
/*     */   
/*     */   public PingBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  35 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  40 */     this.state = State.PREPARE;
/*  41 */     this.cursor = 0;
/*  42 */     this.payload = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  48 */     while (buffer.hasRemaining())
/*     */     {
/*  50 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  55 */         if (getStreamId() != 0) {
/*  56 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_ping_frame");
/*     */         }
/*  58 */         if (getBodyLength() != 8)
/*  59 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_ping_frame");
/*  60 */         this.state = State.PAYLOAD;
/*  61 */         break;
/*     */       
/*     */ 
/*     */       case PAYLOAD: 
/*  65 */         this.payload = new byte[8];
/*  66 */         if (buffer.remaining() >= 8)
/*     */         {
/*  68 */           buffer.get(this.payload);
/*  69 */           return onPing(this.payload);
/*     */         }
/*     */         
/*     */ 
/*  73 */         this.state = State.PAYLOAD_BYTES;
/*  74 */         this.cursor = 8;
/*     */         
/*  76 */         break;
/*     */       
/*     */ 
/*     */       case PAYLOAD_BYTES: 
/*  80 */         this.payload[(8 - this.cursor)] = buffer.get();
/*  81 */         this.cursor -= 1;
/*  82 */         if (this.cursor == 0) {
/*  83 */           return onPing(this.payload);
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/*  88 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/*  92 */     return false;
/*     */   }
/*     */   
/*     */   private boolean onPing(byte[] payload)
/*     */   {
/*  97 */     PingFrame frame = new PingFrame(payload, hasFlag(1));
/*  98 */     reset();
/*  99 */     notifyPing(frame);
/* 100 */     return true;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 105 */     PREPARE,  PAYLOAD,  PAYLOAD_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\PingBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */