/*     */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.IdleTimeout;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class HTTP2Stream
/*     */   extends IdleTimeout
/*     */   implements IStream
/*     */ {
/*  44 */   private static final Logger LOG = Log.getLogger(HTTP2Stream.class);
/*     */   
/*  46 */   private final AtomicReference<ConcurrentMap<String, Object>> attributes = new AtomicReference();
/*  47 */   private final AtomicReference<CloseState> closeState = new AtomicReference(CloseState.NOT_CLOSED);
/*  48 */   private final AtomicInteger sendWindow = new AtomicInteger();
/*  49 */   private final AtomicInteger recvWindow = new AtomicInteger();
/*     */   private final ISession session;
/*     */   private final int streamId;
/*     */   private final boolean local;
/*     */   private volatile Listener listener;
/*     */   private volatile boolean localReset;
/*     */   private volatile boolean remoteReset;
/*     */   
/*     */   public HTTP2Stream(Scheduler scheduler, ISession session, int streamId, boolean local)
/*     */   {
/*  59 */     super(scheduler);
/*  60 */     this.session = session;
/*  61 */     this.streamId = streamId;
/*  62 */     this.local = local;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getId()
/*     */   {
/*  68 */     return this.streamId;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLocal()
/*     */   {
/*  74 */     return this.local;
/*     */   }
/*     */   
/*     */ 
/*     */   public ISession getSession()
/*     */   {
/*  80 */     return this.session;
/*     */   }
/*     */   
/*     */ 
/*     */   public void headers(HeadersFrame frame, Callback callback)
/*     */   {
/*  86 */     notIdle();
/*  87 */     this.session.frames(this, callback, frame, Frame.EMPTY_ARRAY);
/*     */   }
/*     */   
/*     */ 
/*     */   public void push(PushPromiseFrame frame, Promise<Stream> promise, Listener listener)
/*     */   {
/*  93 */     notIdle();
/*  94 */     this.session.push(this, promise, frame, listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void data(DataFrame frame, Callback callback)
/*     */   {
/* 100 */     notIdle();
/* 101 */     this.session.data(this, callback, frame);
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset(ResetFrame frame, Callback callback)
/*     */   {
/* 107 */     if (isReset())
/* 108 */       return;
/* 109 */     notIdle();
/* 110 */     this.localReset = true;
/* 111 */     this.session.frames(this, callback, frame, Frame.EMPTY_ARRAY);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getAttribute(String key)
/*     */   {
/* 117 */     return attributes().get(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setAttribute(String key, Object value)
/*     */   {
/* 123 */     attributes().put(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object removeAttribute(String key)
/*     */   {
/* 129 */     return attributes().remove(key);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReset()
/*     */   {
/* 135 */     return (this.localReset) || (this.remoteReset);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 141 */     return this.closeState.get() == CloseState.CLOSED;
/*     */   }
/*     */   
/*     */   public boolean isRemotelyClosed()
/*     */   {
/* 146 */     return this.closeState.get() == CloseState.REMOTELY_CLOSED;
/*     */   }
/*     */   
/*     */   public boolean isLocallyClosed()
/*     */   {
/* 151 */     return this.closeState.get() == CloseState.LOCALLY_CLOSED;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOpen()
/*     */   {
/* 157 */     return !isClosed();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void onIdleExpired(TimeoutException timeout)
/*     */   {
/* 163 */     if (LOG.isDebugEnabled()) {
/* 164 */       LOG.debug("Idle timeout {}ms expired on {}", new Object[] { Long.valueOf(getIdleTimeout()), this });
/*     */     }
/*     */     
/*     */ 
/* 168 */     close();
/*     */     
/*     */ 
/* 171 */     reset(new ResetFrame(getId(), ErrorCode.CANCEL_STREAM_ERROR.code), Callback.NOOP);
/*     */     
/*     */ 
/* 174 */     notifyTimeout(this, timeout);
/*     */   }
/*     */   
/*     */   private ConcurrentMap<String, Object> attributes()
/*     */   {
/* 179 */     ConcurrentMap<String, Object> map = (ConcurrentMap)this.attributes.get();
/* 180 */     if (map == null)
/*     */     {
/* 182 */       map = new ConcurrentHashMap();
/* 183 */       if (!this.attributes.compareAndSet(null, map))
/*     */       {
/* 185 */         map = (ConcurrentMap)this.attributes.get();
/*     */       }
/*     */     }
/* 188 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */   public Listener getListener()
/*     */   {
/* 194 */     return this.listener;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setListener(Listener listener)
/*     */   {
/* 200 */     this.listener = listener;
/*     */   }
/*     */   
/*     */ 
/*     */   public void process(Frame frame, Callback callback)
/*     */   {
/* 206 */     notIdle();
/* 207 */     switch (frame.getType())
/*     */     {
/*     */ 
/*     */     case HEADERS: 
/* 211 */       onHeaders((HeadersFrame)frame, callback);
/* 212 */       break;
/*     */     
/*     */ 
/*     */     case DATA: 
/* 216 */       onData((DataFrame)frame, callback);
/* 217 */       break;
/*     */     
/*     */ 
/*     */     case RST_STREAM: 
/* 221 */       onReset((ResetFrame)frame, callback);
/* 222 */       break;
/*     */     
/*     */ 
/*     */     case PUSH_PROMISE: 
/* 226 */       onPush((PushPromiseFrame)frame, callback);
/* 227 */       break;
/*     */     
/*     */ 
/*     */     default: 
/* 231 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   private void onHeaders(HeadersFrame frame, Callback callback)
/*     */   {
/* 238 */     if (updateClose(frame.isEndStream(), false))
/* 239 */       this.session.removeStream(this);
/* 240 */     callback.succeeded();
/*     */   }
/*     */   
/*     */   private void onData(DataFrame frame, Callback callback)
/*     */   {
/* 245 */     if (getRecvWindow() < 0)
/*     */     {
/*     */ 
/*     */ 
/* 249 */       this.session.close(ErrorCode.FLOW_CONTROL_ERROR.code, "stream_window_exceeded", Callback.NOOP);
/* 250 */       callback.failed(new IOException("stream_window_exceeded"));
/* 251 */       return;
/*     */     }
/*     */     
/*     */ 
/* 255 */     if (isRemotelyClosed())
/*     */     {
/* 257 */       reset(new ResetFrame(this.streamId, ErrorCode.STREAM_CLOSED_ERROR.code), Callback.NOOP);
/* 258 */       callback.failed(new EOFException("stream_closed"));
/* 259 */       return;
/*     */     }
/*     */     
/* 262 */     if (isReset())
/*     */     {
/*     */ 
/* 265 */       callback.failed(new IOException("stream_reset"));
/* 266 */       return;
/*     */     }
/*     */     
/* 269 */     if (updateClose(frame.isEndStream(), false))
/* 270 */       this.session.removeStream(this);
/* 271 */     notifyData(this, frame, callback);
/*     */   }
/*     */   
/*     */   private void onReset(ResetFrame frame, Callback callback)
/*     */   {
/* 276 */     this.remoteReset = true;
/* 277 */     close();
/* 278 */     this.session.removeStream(this);
/* 279 */     callback.succeeded();
/* 280 */     notifyReset(this, frame);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void onPush(PushPromiseFrame frame, Callback callback)
/*     */   {
/* 287 */     updateClose(true, true);
/* 288 */     callback.succeeded();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean updateClose(boolean update, boolean local)
/*     */   {
/* 294 */     if (LOG.isDebugEnabled()) {
/* 295 */       LOG.debug("Update close for {} close={} local={}", new Object[] { this, Boolean.valueOf(update), Boolean.valueOf(local) });
/*     */     }
/* 297 */     if (!update) {
/* 298 */       return false;
/*     */     }
/*     */     for (;;)
/*     */     {
/* 302 */       CloseState current = (CloseState)this.closeState.get();
/* 303 */       switch (current)
/*     */       {
/*     */ 
/*     */       case NOT_CLOSED: 
/* 307 */         CloseState newValue = local ? CloseState.LOCALLY_CLOSED : CloseState.REMOTELY_CLOSED;
/* 308 */         if (this.closeState.compareAndSet(current, newValue)) {
/* 309 */           return false;
/*     */         }
/*     */         
/*     */         break;
/*     */       case LOCALLY_CLOSED: 
/* 314 */         if (local)
/* 315 */           return false;
/* 316 */         close();
/* 317 */         return true;
/*     */       
/*     */ 
/*     */       case REMOTELY_CLOSED: 
/* 321 */         if (!local)
/* 322 */           return false;
/* 323 */         close();
/* 324 */         return true;
/*     */       
/*     */ 
/*     */       default: 
/* 328 */         return false;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   public int getSendWindow()
/*     */   {
/* 336 */     return this.sendWindow.get();
/*     */   }
/*     */   
/*     */   public int getRecvWindow()
/*     */   {
/* 341 */     return this.recvWindow.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public int updateSendWindow(int delta)
/*     */   {
/* 347 */     return this.sendWindow.getAndAdd(delta);
/*     */   }
/*     */   
/*     */ 
/*     */   public int updateRecvWindow(int delta)
/*     */   {
/* 353 */     return this.recvWindow.getAndAdd(delta);
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 359 */     this.closeState.set(CloseState.CLOSED);
/* 360 */     onClose();
/*     */   }
/*     */   
/*     */   private void notifyData(Stream stream, DataFrame frame, Callback callback)
/*     */   {
/* 365 */     Listener listener = this.listener;
/* 366 */     if (listener == null) {
/* 367 */       return;
/*     */     }
/*     */     try {
/* 370 */       listener.onData(stream, frame, callback);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 374 */       LOG.info("Failure while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyReset(Stream stream, ResetFrame frame)
/*     */   {
/* 380 */     Listener listener = this.listener;
/* 381 */     if (listener == null) {
/* 382 */       return;
/*     */     }
/*     */     try {
/* 385 */       listener.onReset(stream, frame);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 389 */       LOG.info("Failure while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyTimeout(Stream stream, Throwable failure)
/*     */   {
/* 395 */     Listener listener = this.listener;
/* 396 */     if (listener == null) {
/* 397 */       return;
/*     */     }
/*     */     try {
/* 400 */       listener.onTimeout(stream, failure);
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 404 */       LOG.info("Failure while notifying listener " + listener, x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 411 */     return String.format("%s@%x#%d{sendWindow=%s,recvWindow=%s,reset=%b,%s}", new Object[] { getClass().getSimpleName(), 
/* 412 */       Integer.valueOf(hashCode()), Integer.valueOf(getId()), this.sendWindow, this.recvWindow, Boolean.valueOf(isReset()), this.closeState });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\HTTP2Stream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */