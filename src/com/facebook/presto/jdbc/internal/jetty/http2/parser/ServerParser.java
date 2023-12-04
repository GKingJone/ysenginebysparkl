/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
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
/*     */ public class ServerParser
/*     */   extends Parser
/*     */ {
/*  33 */   private static final Logger LOG = Log.getLogger(ServerParser.class);
/*     */   
/*     */   private final Listener listener;
/*     */   private final PrefaceParser prefaceParser;
/*  37 */   private State state = State.PREFACE;
/*  38 */   private boolean notifyPreface = true;
/*     */   
/*     */   public ServerParser(ByteBufferPool byteBufferPool, Listener listener, int maxDynamicTableSize, int maxHeaderSize)
/*     */   {
/*  42 */     super(byteBufferPool, listener, maxDynamicTableSize, maxHeaderSize);
/*  43 */     this.listener = listener;
/*  44 */     this.prefaceParser = new PrefaceParser(listener);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public void directUpgrade()
/*     */   {
/*  61 */     if (this.state != State.PREFACE)
/*  62 */       throw new IllegalStateException();
/*  63 */     this.prefaceParser.directUpgrade();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void standardUpgrade()
/*     */   {
/*  71 */     if (this.state != State.PREFACE)
/*  72 */       throw new IllegalStateException();
/*  73 */     this.notifyPreface = false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void parse(ByteBuffer buffer)
/*     */   {
/*     */     try
/*     */     {
/*  81 */       if (LOG.isDebugEnabled()) {
/*  82 */         LOG.debug("Parsing {}", new Object[] { buffer });
/*     */       }
/*     */       for (;;)
/*     */       {
/*  86 */         switch (this.state)
/*     */         {
/*     */ 
/*     */         case PREFACE: 
/*  90 */           if (!this.prefaceParser.parse(buffer))
/*  91 */             return;
/*  92 */           if (this.notifyPreface)
/*  93 */             onPreface();
/*  94 */           this.state = State.SETTINGS;
/*  95 */           break;
/*     */         
/*     */ 
/*     */         case SETTINGS: 
/*  99 */           if (!parseHeader(buffer))
/* 100 */             return;
/* 101 */           if ((getFrameType() != FrameType.SETTINGS.getType()) || (hasFlag(1)))
/*     */           {
/* 103 */             BufferUtil.clear(buffer);
/* 104 */             notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "invalid_preface");
/* 105 */             return;
/*     */           }
/* 107 */           if (!parseBody(buffer))
/* 108 */             return;
/* 109 */           this.state = State.FRAMES;
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 115 */       super.parse(buffer);
/* 116 */       return;
/*     */       
/*     */ 
/*     */ 
/* 120 */       throw new IllegalStateException();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*     */ 
/* 127 */       LOG.debug(x);
/* 128 */       BufferUtil.clear(buffer);
/* 129 */       notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "parser_error");
/*     */     }
/*     */   }
/*     */   
/*     */   protected void onPreface()
/*     */   {
/* 135 */     notifyPreface();
/*     */   }
/*     */   
/*     */   private void notifyPreface()
/*     */   {
/*     */     try
/*     */     {
/* 142 */       this.listener.onPreface();
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 146 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static enum State
/*     */   {
/* 165 */     PREFACE,  SETTINGS,  FRAMES;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */   
/*     */   public static abstract interface Listener
/*     */     extends Parser.Listener
/*     */   {
/*     */     public abstract void onPreface();
/*     */     
/*     */     public static class Adapter
/*     */       extends Parser.Listener.Adapter
/*     */       implements Listener
/*     */     {
/*     */       public void onPreface() {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\ServerParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */