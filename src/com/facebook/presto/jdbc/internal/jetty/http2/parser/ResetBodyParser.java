/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
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
/*     */ public class ResetBodyParser
/*     */   extends BodyParser
/*     */ {
/*  28 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int error;
/*     */   
/*     */   public ResetBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  34 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  39 */     this.state = State.PREPARE;
/*  40 */     this.cursor = 0;
/*  41 */     this.error = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  47 */     while (buffer.hasRemaining())
/*     */     {
/*  49 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  54 */         if (getStreamId() == 0)
/*  55 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_rst_stream_frame");
/*  56 */         int length = getBodyLength();
/*  57 */         if (length != 4)
/*  58 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_rst_stream_frame");
/*  59 */         this.state = State.ERROR;
/*  60 */         break;
/*     */       
/*     */ 
/*     */       case ERROR: 
/*  64 */         if (buffer.remaining() >= 4)
/*     */         {
/*  66 */           return onReset(buffer.getInt());
/*     */         }
/*     */         
/*     */ 
/*  70 */         this.state = State.ERROR_BYTES;
/*  71 */         this.cursor = 4;
/*     */         
/*  73 */         break;
/*     */       
/*     */ 
/*     */       case ERROR_BYTES: 
/*  77 */         int currByte = buffer.get() & 0xFF;
/*  78 */         this.cursor -= 1;
/*  79 */         this.error += (currByte << 8 * this.cursor);
/*  80 */         if (this.cursor == 0) {
/*  81 */           return onReset(this.error);
/*     */         }
/*     */         
/*     */         break;
/*     */       default: 
/*  86 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/*  90 */     return false;
/*     */   }
/*     */   
/*     */   private boolean onReset(int error)
/*     */   {
/*  95 */     ResetFrame frame = new ResetFrame(getStreamId(), error);
/*  96 */     reset();
/*  97 */     notifyReset(frame);
/*  98 */     return true;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 103 */     PREPARE,  ERROR,  ERROR_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\ResetBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */