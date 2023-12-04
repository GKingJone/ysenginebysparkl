/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ 
/*     */ 
/*     */ public abstract class BodyParser
/*     */ {
/*  47 */   protected static final Logger LOG = Log.getLogger(BodyParser.class);
/*     */   
/*     */   private final HeaderParser headerParser;
/*     */   private final Parser.Listener listener;
/*     */   
/*     */   protected BodyParser(HeaderParser headerParser, Parser.Listener listener)
/*     */   {
/*  54 */     this.headerParser = headerParser;
/*  55 */     this.listener = listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean parse(ByteBuffer paramByteBuffer);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void emptyBody(ByteBuffer buffer)
/*     */   {
/*  71 */     connectionFailure(buffer, ErrorCode.PROTOCOL_ERROR.code, "invalid_frame");
/*     */   }
/*     */   
/*     */   protected boolean hasFlag(int bit)
/*     */   {
/*  76 */     return this.headerParser.hasFlag(bit);
/*     */   }
/*     */   
/*     */   protected boolean isPadding()
/*     */   {
/*  81 */     return this.headerParser.hasFlag(8);
/*     */   }
/*     */   
/*     */   protected boolean isEndStream()
/*     */   {
/*  86 */     return this.headerParser.hasFlag(1);
/*     */   }
/*     */   
/*     */   protected int getStreamId()
/*     */   {
/*  91 */     return this.headerParser.getStreamId();
/*     */   }
/*     */   
/*     */   protected int getBodyLength()
/*     */   {
/*  96 */     return this.headerParser.getLength();
/*     */   }
/*     */   
/*     */   protected void notifyData(DataFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 103 */       this.listener.onData(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 107 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyHeaders(HeadersFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 115 */       this.listener.onHeaders(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 119 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyPriority(PriorityFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 127 */       this.listener.onPriority(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 131 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyReset(ResetFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 139 */       this.listener.onReset(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 143 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifySettings(SettingsFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       this.listener.onSettings(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 155 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyPushPromise(PushPromiseFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 163 */       this.listener.onPushPromise(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 167 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyPing(PingFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 175 */       this.listener.onPing(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 179 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyGoAway(GoAwayFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       this.listener.onGoAway(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 191 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyWindowUpdate(WindowUpdateFrame frame)
/*     */   {
/*     */     try
/*     */     {
/* 199 */       this.listener.onWindowUpdate(frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 203 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean connectionFailure(ByteBuffer buffer, int error, String reason)
/*     */   {
/* 209 */     BufferUtil.clear(buffer);
/* 210 */     notifyConnectionFailure(error, reason);
/* 211 */     return false;
/*     */   }
/*     */   
/*     */   private void notifyConnectionFailure(int error, String reason)
/*     */   {
/*     */     try
/*     */     {
/* 218 */       this.listener.onConnectionFailure(error, reason);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 222 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\BodyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */