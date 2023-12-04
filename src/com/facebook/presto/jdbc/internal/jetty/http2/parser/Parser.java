/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.hpack.HpackDecoder;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
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
/*     */ public class Parser
/*     */ {
/*  48 */   private static final Logger LOG = Log.getLogger(Parser.class);
/*     */   
/*     */   private final Listener listener;
/*     */   private final HeaderParser headerParser;
/*     */   private final BodyParser[] bodyParsers;
/*     */   private boolean continuation;
/*  54 */   private State state = State.HEADER;
/*     */   
/*     */   public Parser(ByteBufferPool byteBufferPool, Listener listener, int maxDynamicTableSize, int maxHeaderSize)
/*     */   {
/*  58 */     this.listener = listener;
/*  59 */     this.headerParser = new HeaderParser();
/*  60 */     this.bodyParsers = new BodyParser[FrameType.values().length];
/*     */     
/*  62 */     HeaderBlockParser headerBlockParser = new HeaderBlockParser(byteBufferPool, new HpackDecoder(maxDynamicTableSize, maxHeaderSize));
/*  63 */     HeaderBlockFragments headerBlockFragments = new HeaderBlockFragments();
/*     */     
/*  65 */     this.bodyParsers[FrameType.DATA.getType()] = new DataBodyParser(this.headerParser, listener);
/*  66 */     this.bodyParsers[FrameType.HEADERS.getType()] = new HeadersBodyParser(this.headerParser, listener, headerBlockParser, headerBlockFragments);
/*  67 */     this.bodyParsers[FrameType.PRIORITY.getType()] = new PriorityBodyParser(this.headerParser, listener);
/*  68 */     this.bodyParsers[FrameType.RST_STREAM.getType()] = new ResetBodyParser(this.headerParser, listener);
/*  69 */     this.bodyParsers[FrameType.SETTINGS.getType()] = new SettingsBodyParser(this.headerParser, listener);
/*  70 */     this.bodyParsers[FrameType.PUSH_PROMISE.getType()] = new PushPromiseBodyParser(this.headerParser, listener, headerBlockParser);
/*  71 */     this.bodyParsers[FrameType.PING.getType()] = new PingBodyParser(this.headerParser, listener);
/*  72 */     this.bodyParsers[FrameType.GO_AWAY.getType()] = new GoAwayBodyParser(this.headerParser, listener);
/*  73 */     this.bodyParsers[FrameType.WINDOW_UPDATE.getType()] = new WindowUpdateBodyParser(this.headerParser, listener);
/*  74 */     this.bodyParsers[FrameType.CONTINUATION.getType()] = new ContinuationBodyParser(this.headerParser, listener, headerBlockParser, headerBlockFragments);
/*     */   }
/*     */   
/*     */   private void reset()
/*     */   {
/*  79 */     this.headerParser.reset();
/*  80 */     this.state = State.HEADER;
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
/*     */   public void parse(ByteBuffer buffer)
/*     */   {
/*     */     try
/*     */     {
/*     */       do
/*     */       {
/*     */         do
/*     */         {
/* 100 */           switch (this.state)
/*     */           {
/*     */           }
/*     */           
/* 104 */         } while (parseHeader(buffer));
/* 105 */         return;
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 110 */       while (parseBody(buffer));
/* 111 */       return;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 116 */       throw new IllegalStateException();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*     */ 
/* 123 */       if (LOG.isDebugEnabled())
/* 124 */         LOG.debug(x);
/* 125 */       BufferUtil.clear(buffer);
/* 126 */       notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "parser_error");
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean parseHeader(ByteBuffer buffer)
/*     */   {
/* 132 */     if (!this.headerParser.parse(buffer)) {
/* 133 */       return false;
/*     */     }
/* 135 */     FrameType frameType = FrameType.from(getFrameType());
/* 136 */     if (LOG.isDebugEnabled()) {
/* 137 */       LOG.debug("Parsed {} frame header from {}", new Object[] { frameType, buffer });
/*     */     }
/* 139 */     if (this.continuation)
/*     */     {
/* 141 */       if (frameType != FrameType.CONTINUATION)
/*     */       {
/*     */ 
/* 144 */         BufferUtil.clear(buffer);
/* 145 */         notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "continuation_frame_expected");
/* 146 */         return false;
/*     */       }
/* 148 */       if (this.headerParser.hasFlag(4))
/*     */       {
/* 150 */         this.continuation = false;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 155 */     else if ((frameType == FrameType.HEADERS) && 
/* 156 */       (!this.headerParser.hasFlag(4)))
/*     */     {
/* 158 */       this.continuation = true;
/*     */     }
/*     */     
/* 161 */     this.state = State.BODY;
/* 162 */     return true;
/*     */   }
/*     */   
/*     */   protected boolean parseBody(ByteBuffer buffer)
/*     */   {
/* 167 */     int type = getFrameType();
/* 168 */     if ((type < 0) || (type >= this.bodyParsers.length))
/*     */     {
/* 170 */       BufferUtil.clear(buffer);
/* 171 */       notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "unknown_frame_type_" + type);
/* 172 */       return false;
/*     */     }
/*     */     
/* 175 */     BodyParser bodyParser = this.bodyParsers[type];
/* 176 */     if (this.headerParser.getLength() == 0)
/*     */     {
/* 178 */       bodyParser.emptyBody(buffer);
/*     */ 
/*     */ 
/*     */     }
/* 182 */     else if (!bodyParser.parse(buffer)) {
/* 183 */       return false;
/*     */     }
/* 185 */     if (LOG.isDebugEnabled())
/* 186 */       LOG.debug("Parsed {} frame body from {}", new Object[] { FrameType.from(type), buffer });
/* 187 */     reset();
/* 188 */     return true;
/*     */   }
/*     */   
/*     */   protected int getFrameType()
/*     */   {
/* 193 */     return this.headerParser.getFrameType();
/*     */   }
/*     */   
/*     */   protected boolean hasFlag(int bit)
/*     */   {
/* 198 */     return this.headerParser.hasFlag(bit);
/*     */   }
/*     */   
/*     */   protected void notifyConnectionFailure(int error, String reason)
/*     */   {
/*     */     try
/*     */     {
/* 205 */       this.listener.onConnectionFailure(error, reason);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 209 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static abstract interface Listener
/*     */   {
/*     */     public abstract void onData(DataFrame paramDataFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onHeaders(HeadersFrame paramHeadersFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onPriority(PriorityFrame paramPriorityFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onReset(ResetFrame paramResetFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onSettings(SettingsFrame paramSettingsFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onPushPromise(PushPromiseFrame paramPushPromiseFrame);
/*     */     
/*     */ 
/*     */ 
/*     */     public abstract void onPing(PingFrame paramPingFrame);
/*     */     
/*     */ 
/*     */     public abstract void onGoAway(GoAwayFrame paramGoAwayFrame);
/*     */     
/*     */ 
/*     */     public abstract void onWindowUpdate(WindowUpdateFrame paramWindowUpdateFrame);
/*     */     
/*     */ 
/*     */     public abstract void onConnectionFailure(int paramInt, String paramString);
/*     */     
/*     */ 
/*     */     public static class Adapter
/*     */       implements Listener
/*     */     {
/*     */       public void onData(DataFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onHeaders(HeadersFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onPriority(PriorityFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onReset(ResetFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onSettings(SettingsFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onPushPromise(PushPromiseFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onPing(PingFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onGoAway(GoAwayFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onWindowUpdate(WindowUpdateFrame frame) {}
/*     */       
/*     */ 
/*     */       public void onConnectionFailure(int error, String reason)
/*     */       {
/* 285 */         Parser.LOG.warn("Connection failure: {}/{}", new Object[] { Integer.valueOf(error), reason });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 292 */     HEADER,  BODY;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */