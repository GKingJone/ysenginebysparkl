/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
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
/*     */ public class WindowUpdateBodyParser
/*     */   extends BodyParser
/*     */ {
/*  28 */   private State state = State.PREPARE;
/*     */   private int cursor;
/*     */   private int windowDelta;
/*     */   
/*     */   public WindowUpdateBodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  34 */     super(headerParser, listener);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  39 */     this.state = State.PREPARE;
/*  40 */     this.cursor = 0;
/*  41 */     this.windowDelta = 0;
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
/*     */       case PREPARE: 
/*  53 */         int length = getBodyLength();
/*  54 */         if (length != 4)
/*  55 */           return connectionFailure(buffer, ErrorCode.FRAME_SIZE_ERROR.code, "invalid_window_update_frame");
/*  56 */         this.state = State.WINDOW_DELTA;
/*  57 */         break;
/*     */       
/*     */ 
/*     */       case WINDOW_DELTA: 
/*  61 */         if (buffer.remaining() >= 4)
/*     */         {
/*  63 */           this.windowDelta = (buffer.getInt() & 0x7FFFFFFF);
/*  64 */           return onWindowUpdate(this.windowDelta);
/*     */         }
/*     */         
/*     */ 
/*  68 */         this.state = State.WINDOW_DELTA_BYTES;
/*  69 */         this.cursor = 4;
/*     */         
/*  71 */         break;
/*     */       
/*     */ 
/*     */       case WINDOW_DELTA_BYTES: 
/*  75 */         byte currByte = buffer.get();
/*  76 */         this.cursor -= 1;
/*  77 */         this.windowDelta += ((currByte & 0xFF) << 8 * this.cursor);
/*  78 */         if (this.cursor == 0)
/*     */         {
/*  80 */           this.windowDelta &= 0x7FFFFFFF;
/*  81 */           return onWindowUpdate(this.windowDelta);
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       default: 
/*  87 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/*  91 */     return false;
/*     */   }
/*     */   
/*     */   private boolean onWindowUpdate(int windowDelta)
/*     */   {
/*  96 */     WindowUpdateFrame frame = new WindowUpdateFrame(getStreamId(), windowDelta);
/*  97 */     reset();
/*  98 */     notifyWindowUpdate(frame);
/*  99 */     return true;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 104 */     PREPARE,  WINDOW_DELTA,  WINDOW_DELTA_BYTES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\WindowUpdateBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */