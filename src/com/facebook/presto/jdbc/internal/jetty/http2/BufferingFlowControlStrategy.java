/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Atomics;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ @ManagedObject
/*     */ public class BufferingFlowControlStrategy
/*     */   extends AbstractFlowControlStrategy
/*     */ {
/*  57 */   private final AtomicInteger maxSessionRecvWindow = new AtomicInteger(65535);
/*  58 */   private final AtomicInteger sessionLevel = new AtomicInteger();
/*  59 */   private final Map<IStream, AtomicInteger> streamLevels = new ConcurrentHashMap();
/*     */   private float bufferRatio;
/*     */   
/*     */   public BufferingFlowControlStrategy(float bufferRatio)
/*     */   {
/*  64 */     this(65535, bufferRatio);
/*     */   }
/*     */   
/*     */   public BufferingFlowControlStrategy(int initialStreamSendWindow, float bufferRatio)
/*     */   {
/*  69 */     super(initialStreamSendWindow);
/*  70 */     this.bufferRatio = bufferRatio;
/*     */   }
/*     */   
/*     */   @ManagedAttribute("The ratio between the receive buffer and the consume buffer")
/*     */   public float getBufferRatio()
/*     */   {
/*  76 */     return this.bufferRatio;
/*     */   }
/*     */   
/*     */   public void setBufferRatio(float bufferRatio)
/*     */   {
/*  81 */     this.bufferRatio = bufferRatio;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onStreamCreated(IStream stream)
/*     */   {
/*  87 */     super.onStreamCreated(stream);
/*  88 */     this.streamLevels.put(stream, new AtomicInteger());
/*     */   }
/*     */   
/*     */ 
/*     */   public void onStreamDestroyed(IStream stream)
/*     */   {
/*  94 */     this.streamLevels.remove(stream);
/*  95 */     super.onStreamDestroyed(stream);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDataConsumed(ISession session, IStream stream, int length)
/*     */   {
/* 101 */     if (length <= 0) {
/* 102 */       return;
/*     */     }
/* 104 */     float ratio = this.bufferRatio;
/*     */     
/* 106 */     WindowUpdateFrame windowFrame = null;
/* 107 */     int level = this.sessionLevel.addAndGet(length);
/* 108 */     int maxLevel = (int)(this.maxSessionRecvWindow.get() * ratio);
/* 109 */     if (level > maxLevel)
/*     */     {
/* 111 */       level = this.sessionLevel.getAndSet(0);
/* 112 */       session.updateRecvWindow(level);
/* 113 */       if (LOG.isDebugEnabled())
/* 114 */         LOG.debug("Data consumed, updated session recv window by {}/{} for {}", new Object[] { Integer.valueOf(level), Integer.valueOf(maxLevel), session });
/* 115 */       windowFrame = new WindowUpdateFrame(0, level);
/*     */ 
/*     */ 
/*     */     }
/* 119 */     else if (LOG.isDebugEnabled()) {
/* 120 */       LOG.debug("Data consumed, session recv window level {}/{} for {}", new Object[] { Integer.valueOf(level), Integer.valueOf(maxLevel), session });
/*     */     }
/*     */     
/* 123 */     Frame[] windowFrames = Frame.EMPTY_ARRAY;
/* 124 */     if (stream != null)
/*     */     {
/* 126 */       if (stream.isClosed())
/*     */       {
/* 128 */         if (LOG.isDebugEnabled()) {
/* 129 */           LOG.debug("Data consumed, ignoring update stream recv window by {} for closed {}", new Object[] { Integer.valueOf(length), stream });
/*     */         }
/*     */       }
/*     */       else {
/* 133 */         AtomicInteger streamLevel = (AtomicInteger)this.streamLevels.get(stream);
/* 134 */         if (streamLevel != null)
/*     */         {
/* 136 */           level = streamLevel.addAndGet(length);
/* 137 */           maxLevel = (int)(getInitialStreamRecvWindow() * ratio);
/* 138 */           if (level > maxLevel)
/*     */           {
/* 140 */             level = streamLevel.getAndSet(0);
/* 141 */             stream.updateRecvWindow(level);
/* 142 */             if (LOG.isDebugEnabled())
/* 143 */               LOG.debug("Data consumed, updated stream recv window by {}/{} for {}", new Object[] { Integer.valueOf(level), Integer.valueOf(maxLevel), stream });
/* 144 */             WindowUpdateFrame frame = new WindowUpdateFrame(stream.getId(), level);
/* 145 */             if (windowFrame == null) {
/* 146 */               windowFrame = frame;
/*     */             } else {
/* 148 */               windowFrames = new Frame[] { frame };
/*     */             }
/*     */             
/*     */           }
/* 152 */           else if (LOG.isDebugEnabled()) {
/* 153 */             LOG.debug("Data consumed, stream recv window level {}/{} for {}", new Object[] { Integer.valueOf(level), Integer.valueOf(maxLevel), session });
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 159 */     if (windowFrame != null) {
/* 160 */       session.frames(stream, Callback.NOOP, windowFrame, windowFrames);
/*     */     }
/*     */   }
/*     */   
/*     */   public void windowUpdate(ISession session, IStream stream, WindowUpdateFrame frame)
/*     */   {
/* 166 */     super.windowUpdate(session, stream, frame);
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
/* 195 */     if (frame.getStreamId() == 0)
/*     */     {
/* 197 */       int sessionWindow = session.updateRecvWindow(0);
/* 198 */       Atomics.updateMax(this.maxSessionRecvWindow, sessionWindow);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 205 */     return String.format("%s@%x[ratio=%.2f,sessionStallTime=%dms,streamsStallTime=%dms]", new Object[] {
/* 206 */       getClass().getSimpleName(), 
/* 207 */       Integer.valueOf(hashCode()), 
/* 208 */       Float.valueOf(this.bufferRatio), 
/* 209 */       Long.valueOf(getSessionStallTime()), 
/* 210 */       Long.valueOf(getStreamsStallTime()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\BufferingFlowControlStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */