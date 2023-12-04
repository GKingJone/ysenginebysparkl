/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedOperation;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public abstract class AbstractFlowControlStrategy
/*     */   implements FlowControlStrategy, Dumpable
/*     */ {
/*  40 */   protected static final Logger LOG = Log.getLogger(FlowControlStrategy.class);
/*     */   
/*  42 */   private final AtomicLong sessionStall = new AtomicLong();
/*  43 */   private final AtomicLong sessionStallTime = new AtomicLong();
/*  44 */   private final Map<IStream, Long> streamsStalls = new ConcurrentHashMap();
/*  45 */   private final AtomicLong streamsStallTime = new AtomicLong();
/*     */   private int initialStreamSendWindow;
/*     */   private int initialStreamRecvWindow;
/*     */   
/*     */   public AbstractFlowControlStrategy(int initialStreamSendWindow)
/*     */   {
/*  51 */     this.initialStreamSendWindow = initialStreamSendWindow;
/*  52 */     this.initialStreamRecvWindow = 65535;
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The initial size of stream's flow control send window", readonly=true)
/*     */   public int getInitialStreamSendWindow()
/*     */   {
/*  58 */     return this.initialStreamSendWindow;
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The initial size of stream's flow control receive window", readonly=true)
/*     */   public int getInitialStreamRecvWindow()
/*     */   {
/*  64 */     return this.initialStreamRecvWindow;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onStreamCreated(IStream stream)
/*     */   {
/*  70 */     stream.updateSendWindow(this.initialStreamSendWindow);
/*  71 */     stream.updateRecvWindow(this.initialStreamRecvWindow);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onStreamDestroyed(IStream stream) {}
/*     */   
/*     */ 
/*     */   public void updateInitialStreamWindow(ISession session, int initialStreamWindow, boolean local)
/*     */   {
/*     */     int previousInitialStreamWindow;
/*     */     
/*  83 */     if (local)
/*     */     {
/*  85 */       int previousInitialStreamWindow = getInitialStreamRecvWindow();
/*  86 */       this.initialStreamRecvWindow = initialStreamWindow;
/*     */     }
/*     */     else
/*     */     {
/*  90 */       previousInitialStreamWindow = getInitialStreamSendWindow();
/*  91 */       this.initialStreamSendWindow = initialStreamWindow;
/*     */     }
/*  93 */     int delta = initialStreamWindow - previousInitialStreamWindow;
/*     */     
/*     */ 
/*  96 */     for (Stream stream : session.getStreams())
/*     */     {
/*  98 */       if (local)
/*     */       {
/* 100 */         ((IStream)stream).updateRecvWindow(delta);
/* 101 */         if (LOG.isDebugEnabled()) {
/* 102 */           LOG.debug("Updated initial stream recv window {} -> {} for {}", new Object[] { Integer.valueOf(previousInitialStreamWindow), Integer.valueOf(initialStreamWindow), stream });
/*     */         }
/*     */       }
/*     */       else {
/* 106 */         session.onWindowUpdate((IStream)stream, new WindowUpdateFrame(stream.getId(), delta));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onWindowUpdate(ISession session, IStream stream, WindowUpdateFrame frame)
/*     */   {
/* 114 */     int delta = frame.getWindowDelta();
/* 115 */     if (frame.getStreamId() > 0)
/*     */     {
/*     */ 
/* 118 */       if (stream != null)
/*     */       {
/* 120 */         int oldSize = stream.updateSendWindow(delta);
/* 121 */         if (LOG.isDebugEnabled())
/* 122 */           LOG.debug("Updated stream send window {} -> {} for {}", new Object[] { Integer.valueOf(oldSize), Integer.valueOf(oldSize + delta), stream });
/* 123 */         if (oldSize <= 0) {
/* 124 */           onStreamUnstalled(stream);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 129 */       int oldSize = session.updateSendWindow(delta);
/* 130 */       if (LOG.isDebugEnabled())
/* 131 */         LOG.debug("Updated session send window {} -> {} for {}", new Object[] { Integer.valueOf(oldSize), Integer.valueOf(oldSize + delta), session });
/* 132 */       if (oldSize <= 0) {
/* 133 */         onSessionUnstalled(session);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void onDataReceived(ISession session, IStream stream, int length)
/*     */   {
/* 140 */     int oldSize = session.updateRecvWindow(-length);
/* 141 */     if (LOG.isDebugEnabled()) {
/* 142 */       LOG.debug("Data received, updated session recv window {} -> {} for {}", new Object[] { Integer.valueOf(oldSize), Integer.valueOf(oldSize - length), session });
/*     */     }
/* 144 */     if (stream != null)
/*     */     {
/* 146 */       oldSize = stream.updateRecvWindow(-length);
/* 147 */       if (LOG.isDebugEnabled()) {
/* 148 */         LOG.debug("Data received, updated stream recv window {} -> {} for {}", new Object[] { Integer.valueOf(oldSize), Integer.valueOf(oldSize - length), stream });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void windowUpdate(ISession session, IStream stream, WindowUpdateFrame frame) {}
/*     */   
/*     */ 
/*     */   public void onDataSending(IStream stream, int length)
/*     */   {
/* 160 */     if (length == 0) {
/* 161 */       return;
/*     */     }
/* 163 */     ISession session = stream.getSession();
/* 164 */     int oldSessionWindow = session.updateSendWindow(-length);
/* 165 */     int newSessionWindow = oldSessionWindow - length;
/* 166 */     if (LOG.isDebugEnabled())
/* 167 */       LOG.debug("Sending, session send window {} -> {} for {}", new Object[] { Integer.valueOf(oldSessionWindow), Integer.valueOf(newSessionWindow), session });
/* 168 */     if (newSessionWindow <= 0) {
/* 169 */       onSessionStalled(session);
/*     */     }
/* 171 */     int oldStreamWindow = stream.updateSendWindow(-length);
/* 172 */     int newStreamWindow = oldStreamWindow - length;
/* 173 */     if (LOG.isDebugEnabled())
/* 174 */       LOG.debug("Sending, stream send window {} -> {} for {}", new Object[] { Integer.valueOf(oldStreamWindow), Integer.valueOf(newStreamWindow), stream });
/* 175 */     if (newStreamWindow <= 0) {
/* 176 */       onStreamStalled(stream);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void onDataSent(IStream stream, int length) {}
/*     */   
/*     */ 
/*     */   protected void onSessionStalled(ISession session)
/*     */   {
/* 186 */     this.sessionStall.set(System.nanoTime());
/* 187 */     if (LOG.isDebugEnabled()) {
/* 188 */       LOG.debug("Session stalled {}", new Object[] { session });
/*     */     }
/*     */   }
/*     */   
/*     */   protected void onStreamStalled(IStream stream) {
/* 193 */     this.streamsStalls.put(stream, Long.valueOf(System.nanoTime()));
/* 194 */     if (LOG.isDebugEnabled()) {
/* 195 */       LOG.debug("Stream stalled {}", new Object[] { stream });
/*     */     }
/*     */   }
/*     */   
/*     */   protected void onSessionUnstalled(ISession session) {
/* 200 */     this.sessionStallTime.addAndGet(System.nanoTime() - this.sessionStall.getAndSet(0L));
/* 201 */     if (LOG.isDebugEnabled()) {
/* 202 */       LOG.debug("Session unstalled {}", new Object[] { session });
/*     */     }
/*     */   }
/*     */   
/*     */   protected void onStreamUnstalled(IStream stream) {
/* 207 */     Long time = (Long)this.streamsStalls.remove(stream);
/* 208 */     if (time != null)
/* 209 */       this.streamsStallTime.addAndGet(System.nanoTime() - time.longValue());
/* 210 */     if (LOG.isDebugEnabled()) {
/* 211 */       LOG.debug("Stream unstalled {}", new Object[] { stream });
/*     */     }
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The time, in milliseconds, that the session flow control has stalled", readonly=true)
/*     */   public long getSessionStallTime() {
/* 217 */     return TimeUnit.NANOSECONDS.toMillis(this.sessionStallTime.get());
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The time, in milliseconds, that the streams flow control has stalled", readonly=true)
/*     */   public long getStreamsStallTime()
/*     */   {
/* 223 */     return TimeUnit.NANOSECONDS.toMillis(this.streamsStallTime.get());
/*     */   }
/*     */   
/*     */   @ManagedOperation(value="Resets the statistics", impact="ACTION")
/*     */   public void reset()
/*     */   {
/* 229 */     this.sessionStallTime.set(0L);
/* 230 */     this.streamsStallTime.set(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public String dump()
/*     */   {
/* 236 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 242 */     out.append(toString()).append(System.lineSeparator());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\AbstractFlowControlStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */