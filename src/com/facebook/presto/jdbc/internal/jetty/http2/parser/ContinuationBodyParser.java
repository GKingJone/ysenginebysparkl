/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
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
/*     */ public class ContinuationBodyParser
/*     */   extends BodyParser
/*     */ {
/*     */   private final HeaderBlockParser headerBlockParser;
/*     */   private final HeaderBlockFragments headerBlockFragments;
/*  32 */   private State state = State.PREPARE;
/*     */   private int length;
/*     */   
/*     */   public ContinuationBodyParser(HeaderParser headerParser, Parser.Listener listener, HeaderBlockParser headerBlockParser, HeaderBlockFragments headerBlockFragments)
/*     */   {
/*  37 */     super(headerParser, listener);
/*  38 */     this.headerBlockParser = headerBlockParser;
/*  39 */     this.headerBlockFragments = headerBlockFragments;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void emptyBody(ByteBuffer buffer)
/*     */   {
/*  45 */     if (hasFlag(4)) {
/*  46 */       onHeaders();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean parse(ByteBuffer buffer)
/*     */   {
/*  52 */     while (buffer.hasRemaining())
/*     */     {
/*  54 */       switch (this.state)
/*     */       {
/*     */ 
/*     */ 
/*     */       case PREPARE: 
/*  59 */         if (getStreamId() == 0) {
/*  60 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_continuation_frame");
/*     */         }
/*  62 */         if (getStreamId() != this.headerBlockFragments.getStreamId()) {
/*  63 */           return connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_continuation_stream");
/*     */         }
/*  65 */         this.length = getBodyLength();
/*  66 */         this.state = State.FRAGMENT;
/*  67 */         break;
/*     */       
/*     */ 
/*     */       case FRAGMENT: 
/*  71 */         int remaining = buffer.remaining();
/*  72 */         if (remaining < this.length)
/*     */         {
/*  74 */           this.headerBlockFragments.storeFragment(buffer, remaining, false);
/*  75 */           this.length -= remaining;
/*     */         }
/*     */         else
/*     */         {
/*  79 */           boolean last = hasFlag(4);
/*  80 */           this.headerBlockFragments.storeFragment(buffer, this.length, last);
/*  81 */           reset();
/*  82 */           if (last)
/*  83 */             onHeaders();
/*  84 */           return true;
/*     */         }
/*     */       
/*     */ 
/*     */       default: 
/*  89 */         throw new IllegalStateException();
/*     */       }
/*     */       
/*     */     }
/*  93 */     return false;
/*     */   }
/*     */   
/*     */   private void onHeaders()
/*     */   {
/*  98 */     ByteBuffer headerBlock = this.headerBlockFragments.complete();
/*  99 */     MetaData metaData = this.headerBlockParser.parse(headerBlock, headerBlock.remaining());
/* 100 */     HeadersFrame frame = new HeadersFrame(getStreamId(), metaData, this.headerBlockFragments.getPriorityFrame(), this.headerBlockFragments.isEndStream());
/* 101 */     notifyHeaders(frame);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/* 106 */     this.state = State.PREPARE;
/* 107 */     this.length = 0;
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 112 */     PREPARE,  FRAGMENT;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\ContinuationBodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */