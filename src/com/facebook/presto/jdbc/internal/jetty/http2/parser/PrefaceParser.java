/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.parser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PrefaceFrame;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrefaceParser
/*    */ {
/* 31 */   private static final Logger LOG = Log.getLogger(PrefaceParser.class);
/*    */   
/*    */   private final Parser.Listener listener;
/*    */   private int cursor;
/*    */   
/*    */   public PrefaceParser(Parser.Listener listener)
/*    */   {
/* 38 */     this.listener = listener;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected void directUpgrade()
/*    */   {
/* 49 */     if (this.cursor != 0)
/* 50 */       throw new IllegalStateException();
/* 51 */     this.cursor = PrefaceFrame.PREFACE_PREAMBLE_BYTES.length;
/*    */   }
/*    */   
/*    */   public boolean parse(ByteBuffer buffer)
/*    */   {
/* 56 */     while (buffer.hasRemaining())
/*    */     {
/* 58 */       int currByte = buffer.get();
/* 59 */       if (currByte != PrefaceFrame.PREFACE_BYTES[this.cursor])
/*    */       {
/* 61 */         BufferUtil.clear(buffer);
/* 62 */         notifyConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "invalid_preface");
/* 63 */         return false;
/*    */       }
/* 65 */       this.cursor += 1;
/* 66 */       if (this.cursor == PrefaceFrame.PREFACE_BYTES.length)
/*    */       {
/* 68 */         this.cursor = 0;
/* 69 */         if (LOG.isDebugEnabled())
/* 70 */           LOG.debug("Parsed preface bytes from {}", new Object[] { buffer });
/* 71 */         return true;
/*    */       }
/*    */     }
/* 74 */     return false;
/*    */   }
/*    */   
/*    */   protected void notifyConnectionFailure(int error, String reason)
/*    */   {
/*    */     try
/*    */     {
/* 81 */       this.listener.onConnectionFailure(error, reason);
/*    */     }
/*    */     catch (Throwable x)
/*    */     {
/* 85 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\parser\PrefaceParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */