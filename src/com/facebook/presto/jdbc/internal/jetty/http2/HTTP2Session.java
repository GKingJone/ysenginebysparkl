/*      */ package com.facebook.presto.jdbc.internal.jetty.http2;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session.Listener;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DisconnectFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.FrameType;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.GoAwayFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PingFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PriorityFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.generator.Generator;
/*      */ import com.facebook.presto.jdbc.internal.jetty.http2.parser.Parser.Listener;
/*      */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool.Lease;
/*      */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Atomics;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.CountingCallback;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*      */ import java.io.IOException;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @ManagedObject
/*      */ public abstract class HTTP2Session
/*      */   extends ContainerLifeCycle
/*      */   implements ISession, Parser.Listener
/*      */ {
/*   67 */   private static final Logger LOG = Log.getLogger(HTTP2Session.class);
/*      */   
/*   69 */   private final ConcurrentMap<Integer, IStream> streams = new ConcurrentHashMap();
/*   70 */   private final AtomicInteger streamIds = new AtomicInteger();
/*   71 */   private final AtomicInteger lastStreamId = new AtomicInteger();
/*   72 */   private final AtomicInteger localStreamCount = new AtomicInteger();
/*   73 */   private final AtomicInteger remoteStreamCount = new AtomicInteger();
/*   74 */   private final AtomicInteger sendWindow = new AtomicInteger();
/*   75 */   private final AtomicInteger recvWindow = new AtomicInteger();
/*   76 */   private final AtomicReference<CloseState> closed = new AtomicReference(CloseState.NOT_CLOSED);
/*      */   private final Scheduler scheduler;
/*      */   private final EndPoint endPoint;
/*      */   private final Generator generator;
/*      */   private final Session.Listener listener;
/*      */   private final FlowControlStrategy flowControl;
/*      */   private final HTTP2Flusher flusher;
/*      */   private int maxLocalStreams;
/*      */   private int maxRemoteStreams;
/*      */   private long streamIdleTimeout;
/*      */   private boolean pushEnabled;
/*      */   
/*      */   public HTTP2Session(Scheduler scheduler, EndPoint endPoint, Generator generator, Session.Listener listener, FlowControlStrategy flowControl, int initialStreamId)
/*      */   {
/*   90 */     this.scheduler = scheduler;
/*   91 */     this.endPoint = endPoint;
/*   92 */     this.generator = generator;
/*   93 */     this.listener = listener;
/*   94 */     this.flowControl = flowControl;
/*   95 */     this.flusher = new HTTP2Flusher(this);
/*   96 */     this.maxLocalStreams = -1;
/*   97 */     this.maxRemoteStreams = -1;
/*   98 */     this.streamIds.set(initialStreamId);
/*   99 */     this.streamIdleTimeout = endPoint.getIdleTimeout();
/*  100 */     this.sendWindow.set(65535);
/*  101 */     this.recvWindow.set(65535);
/*  102 */     this.pushEnabled = true;
/*      */   }
/*      */   
/*      */   protected void doStart()
/*      */     throws Exception
/*      */   {
/*  108 */     addBean(this.flowControl);
/*  109 */     super.doStart();
/*      */   }
/*      */   
/*      */   @ManagedAttribute(value="The flow control strategy", readonly=true)
/*      */   public FlowControlStrategy getFlowControlStrategy()
/*      */   {
/*  115 */     return this.flowControl;
/*      */   }
/*      */   
/*      */   public int getMaxLocalStreams()
/*      */   {
/*  120 */     return this.maxLocalStreams;
/*      */   }
/*      */   
/*      */   public void setMaxLocalStreams(int maxLocalStreams)
/*      */   {
/*  125 */     this.maxLocalStreams = maxLocalStreams;
/*      */   }
/*      */   
/*      */   public int getMaxRemoteStreams()
/*      */   {
/*  130 */     return this.maxRemoteStreams;
/*      */   }
/*      */   
/*      */   public void setMaxRemoteStreams(int maxRemoteStreams)
/*      */   {
/*  135 */     this.maxRemoteStreams = maxRemoteStreams;
/*      */   }
/*      */   
/*      */   @ManagedAttribute("The stream's idle timeout")
/*      */   public long getStreamIdleTimeout()
/*      */   {
/*  141 */     return this.streamIdleTimeout;
/*      */   }
/*      */   
/*      */   public void setStreamIdleTimeout(long streamIdleTimeout)
/*      */   {
/*  146 */     this.streamIdleTimeout = streamIdleTimeout;
/*      */   }
/*      */   
/*      */   public EndPoint getEndPoint()
/*      */   {
/*  151 */     return this.endPoint;
/*      */   }
/*      */   
/*      */   public Generator getGenerator()
/*      */   {
/*  156 */     return this.generator;
/*      */   }
/*      */   
/*      */ 
/*      */   public void onData(DataFrame frame)
/*      */   {
/*  162 */     if (LOG.isDebugEnabled()) {
/*  163 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*  165 */     int streamId = frame.getStreamId();
/*  166 */     final IStream stream = getStream(streamId);
/*      */     
/*      */ 
/*      */ 
/*  170 */     final int flowControlLength = frame.remaining() + frame.padding();
/*  171 */     this.flowControl.onDataReceived(this, stream, flowControlLength);
/*      */     
/*  173 */     if (stream != null)
/*      */     {
/*  175 */       if (getRecvWindow() < 0)
/*      */       {
/*  177 */         close(ErrorCode.FLOW_CONTROL_ERROR.code, "session_window_exceeded", Callback.NOOP);
/*      */       }
/*      */       else
/*      */       {
/*  181 */         stream.process(frame, new Callback()
/*      */         {
/*      */ 
/*      */           public void succeeded()
/*      */           {
/*  186 */             HTTP2Session.this.flowControl.onDataConsumed(HTTP2Session.this, stream, flowControlLength);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void failed(Throwable x)
/*      */           {
/*  194 */             HTTP2Session.this.flowControl.onDataConsumed(HTTP2Session.this, stream, flowControlLength);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  201 */       if (LOG.isDebugEnabled()) {
/*  202 */         LOG.debug("Ignoring {}, stream #{} not found", new Object[] { frame, Integer.valueOf(streamId) });
/*      */       }
/*      */       
/*  205 */       this.flowControl.onDataConsumed(this, null, flowControlLength);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public abstract void onHeaders(HeadersFrame paramHeadersFrame);
/*      */   
/*      */ 
/*      */   public void onPriority(PriorityFrame frame)
/*      */   {
/*  215 */     if (LOG.isDebugEnabled()) {
/*  216 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*      */   }
/*      */   
/*      */   public void onReset(ResetFrame frame)
/*      */   {
/*  222 */     if (LOG.isDebugEnabled()) {
/*  223 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*  225 */     IStream stream = getStream(frame.getStreamId());
/*  226 */     if (stream != null) {
/*  227 */       stream.process(frame, Callback.NOOP);
/*      */     } else {
/*  229 */       notifyReset(this, frame);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void onSettings(SettingsFrame frame)
/*      */   {
/*  236 */     onSettings(frame, true);
/*      */   }
/*      */   
/*      */   public void onSettings(SettingsFrame frame, boolean reply)
/*      */   {
/*  241 */     if (LOG.isDebugEnabled()) {
/*  242 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*  244 */     if (frame.isReply()) {
/*  245 */       return;
/*      */     }
/*      */     
/*  248 */     for (Entry<Integer, Integer> entry : frame.getSettings().entrySet())
/*      */     {
/*  250 */       int key = ((Integer)entry.getKey()).intValue();
/*  251 */       int value = ((Integer)entry.getValue()).intValue();
/*  252 */       switch (key)
/*      */       {
/*      */ 
/*      */       case 1: 
/*  256 */         if (LOG.isDebugEnabled())
/*  257 */           LOG.debug("Update HPACK header table size to {}", value);
/*  258 */         this.generator.setHeaderTableSize(value);
/*  259 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case 2: 
/*  264 */         if ((value != 0) && (value != 1))
/*      */         {
/*  266 */           onConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "invalid_settings_enable_push");
/*  267 */           return;
/*      */         }
/*  269 */         this.pushEnabled = (value == 1);
/*  270 */         break;
/*      */       
/*      */ 
/*      */       case 3: 
/*  274 */         this.maxLocalStreams = value;
/*  275 */         if (LOG.isDebugEnabled()) {
/*  276 */           LOG.debug("Update max local concurrent streams to {}", this.maxLocalStreams);
/*      */         }
/*      */         
/*      */         break;
/*      */       case 4: 
/*  281 */         if (LOG.isDebugEnabled())
/*  282 */           LOG.debug("Update initial window size to {}", value);
/*  283 */         this.flowControl.updateInitialStreamWindow(this, value, false);
/*  284 */         break;
/*      */       
/*      */ 
/*      */       case 5: 
/*  288 */         if (LOG.isDebugEnabled()) {
/*  289 */           LOG.debug("Update max frame size to {}", value);
/*      */         }
/*  291 */         if ((value < 16384) || (value > 16777215))
/*      */         {
/*  293 */           onConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "invalid_settings_max_frame_size");
/*  294 */           return;
/*      */         }
/*  296 */         this.generator.setMaxFrameSize(value);
/*  297 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case 6: 
/*  302 */         LOG.warn("NOT IMPLEMENTED max header list size to {}", new Object[] { Integer.valueOf(value) });
/*  303 */         break;
/*      */       
/*      */ 
/*      */       default: 
/*  307 */         LOG.debug("Unknown setting {}:{}", new Object[] { Integer.valueOf(key), Integer.valueOf(value) });
/*      */       }
/*      */       
/*      */     }
/*      */     
/*  312 */     notifySettings(this, frame);
/*      */     
/*  314 */     if (reply)
/*      */     {
/*  316 */       SettingsFrame replyFrame = new SettingsFrame(Collections.emptyMap(), true);
/*  317 */       settings(replyFrame, Callback.NOOP);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void onPing(PingFrame frame)
/*      */   {
/*  324 */     if (LOG.isDebugEnabled()) {
/*  325 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*  327 */     if (frame.isReply())
/*      */     {
/*  329 */       notifyPing(this, frame);
/*      */     }
/*      */     else
/*      */     {
/*  333 */       PingFrame reply = new PingFrame(frame.getPayload(), true);
/*  334 */       control(null, Callback.NOOP, reply);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void onGoAway(final GoAwayFrame frame)
/*      */   {
/*  358 */     if (LOG.isDebugEnabled()) {
/*  359 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*      */     for (;;)
/*      */     {
/*  363 */       CloseState current = (CloseState)this.closed.get();
/*  364 */       switch (current)
/*      */       {
/*      */ 
/*      */       case NOT_CLOSED: 
/*  368 */         if (this.closed.compareAndSet(current, CloseState.REMOTELY_CLOSED))
/*      */         {
/*      */ 
/*      */ 
/*  372 */           control(null, new Callback()
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  383 */             new DisconnectFrame
/*      */             {
/*      */ 
/*      */               public void succeeded()
/*      */               {
/*  377 */                 HTTP2Session.this.notifyClose(HTTP2Session.this, frame);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  383 */               public void failed(Throwable x) { HTTP2Session.this.notifyClose(HTTP2Session.this, frame); } }, new DisconnectFrame());
/*      */           
/*      */ 
/*  386 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       default: 
/*  392 */         if (LOG.isDebugEnabled())
/*  393 */           LOG.debug("Ignored {}, already closed", new Object[] { frame });
/*  394 */         return;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void onWindowUpdate(WindowUpdateFrame frame)
/*      */   {
/*  403 */     if (LOG.isDebugEnabled()) {
/*  404 */       LOG.debug("Received {}", new Object[] { frame });
/*      */     }
/*  406 */     int streamId = frame.getStreamId();
/*  407 */     if (streamId > 0)
/*      */     {
/*  409 */       IStream stream = getStream(streamId);
/*  410 */       if (stream != null) {
/*  411 */         onWindowUpdate(stream, frame);
/*      */       }
/*      */     }
/*      */     else {
/*  415 */       onWindowUpdate(null, frame);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void onConnectionFailure(int error, String reason)
/*      */   {
/*  422 */     close(error, reason, Callback.NOOP);
/*  423 */     notifyFailure(this, new IOException(String.format("%d/%s", new Object[] { Integer.valueOf(error), reason })));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void newStream(HeadersFrame frame, Promise<Stream> promise, Stream.Listener listener)
/*      */   {
/*      */     boolean queued;
/*      */     
/*  432 */     synchronized (this)
/*      */     {
/*  434 */       int streamId = frame.getStreamId();
/*  435 */       if (streamId <= 0)
/*      */       {
/*  437 */         streamId = this.streamIds.getAndAdd(2);
/*  438 */         PriorityFrame priority = frame.getPriority();
/*      */         
/*  440 */         priority = priority == null ? null : new PriorityFrame(streamId, priority.getParentStreamId(), priority.getWeight(), priority.isExclusive());
/*  441 */         frame = new HeadersFrame(streamId, frame.getMetaData(), priority, frame.isEndStream());
/*      */       }
/*  443 */       IStream stream = createLocalStream(streamId, promise);
/*  444 */       if (stream == null)
/*  445 */         return;
/*  446 */       stream.setListener(listener);
/*      */       
/*  448 */       ControlEntry entry = new ControlEntry(frame, stream, new PromiseCallback(promise, stream, null), null);
/*  449 */       queued = this.flusher.append(entry);
/*      */     }
/*      */     boolean queued;
/*  452 */     if (queued) {
/*  453 */       this.flusher.iterate();
/*      */     }
/*      */   }
/*      */   
/*      */   public int priority(PriorityFrame frame, Callback callback)
/*      */   {
/*  459 */     int streamId = frame.getStreamId();
/*  460 */     IStream stream = (IStream)this.streams.get(Integer.valueOf(streamId));
/*  461 */     if (stream == null)
/*      */     {
/*  463 */       streamId = this.streamIds.getAndAdd(2);
/*      */       
/*  465 */       frame = new PriorityFrame(streamId, frame.getParentStreamId(), frame.getWeight(), frame.isExclusive());
/*      */     }
/*  467 */     control(stream, callback, frame);
/*  468 */     return streamId;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void push(IStream stream, Promise<Stream> promise, PushPromiseFrame frame, Stream.Listener listener)
/*      */   {
/*      */     boolean queued;
/*      */     
/*  477 */     synchronized (this)
/*      */     {
/*  479 */       int streamId = this.streamIds.getAndAdd(2);
/*  480 */       frame = new PushPromiseFrame(frame.getStreamId(), streamId, frame.getMetaData());
/*      */       
/*  482 */       IStream pushStream = createLocalStream(streamId, promise);
/*  483 */       if (pushStream == null)
/*  484 */         return;
/*  485 */       pushStream.setListener(listener);
/*      */       
/*  487 */       ControlEntry entry = new ControlEntry(frame, pushStream, new PromiseCallback(promise, pushStream, null), null);
/*  488 */       queued = this.flusher.append(entry);
/*      */     }
/*      */     boolean queued;
/*  491 */     if (queued) {
/*  492 */       this.flusher.iterate();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void settings(SettingsFrame frame, Callback callback)
/*      */   {
/*  499 */     control(null, callback, frame);
/*      */   }
/*      */   
/*      */ 
/*      */   public void ping(PingFrame frame, Callback callback)
/*      */   {
/*  505 */     if (frame.isReply()) {
/*  506 */       callback.failed(new IllegalArgumentException());
/*      */     } else {
/*  508 */       control(null, callback, frame);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void reset(ResetFrame frame, Callback callback) {
/*  513 */     control(getStream(frame.getStreamId()), callback, frame);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean close(int error, String reason, Callback callback)
/*      */   {
/*      */     for (;;)
/*      */     {
/*  544 */       CloseState current = (CloseState)this.closed.get();
/*  545 */       switch (current)
/*      */       {
/*      */ 
/*      */       case NOT_CLOSED: 
/*  549 */         if (this.closed.compareAndSet(current, CloseState.LOCALLY_CLOSED))
/*      */         {
/*  551 */           byte[] payload = null;
/*  552 */           if (reason != null)
/*      */           {
/*      */ 
/*  555 */             reason = reason.substring(0, Math.min(reason.length(), 32));
/*  556 */             payload = reason.getBytes(StandardCharsets.UTF_8);
/*      */           }
/*  558 */           GoAwayFrame frame = new GoAwayFrame(this.lastStreamId.get(), error, payload);
/*  559 */           control(null, callback, frame);
/*  560 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       default: 
/*  566 */         if (LOG.isDebugEnabled())
/*  567 */           LOG.debug("Ignoring close {}/{}, already closed", new Object[] { Integer.valueOf(error), reason });
/*  568 */         callback.succeeded();
/*  569 */         return false;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isClosed()
/*      */   {
/*  578 */     return this.closed.get() != CloseState.NOT_CLOSED;
/*      */   }
/*      */   
/*      */   private void control(IStream stream, Callback callback, Frame frame)
/*      */   {
/*  583 */     frames(stream, callback, frame, Frame.EMPTY_ARRAY);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void frames(IStream stream, Callback callback, Frame frame, Frame... frames)
/*      */   {
/*  594 */     int length = frames.length;
/*  595 */     if (length == 0)
/*      */     {
/*  597 */       frame(new ControlEntry(frame, stream, callback, null), true);
/*      */     }
/*      */     else
/*      */     {
/*  601 */       callback = new CountingCallback(callback, 1 + length);
/*  602 */       frame(new ControlEntry(frame, stream, callback, null), false);
/*  603 */       for (int i = 1; i <= length; i++) {
/*  604 */         frame(new ControlEntry(frames[(i - 1)], stream, callback, null), i == length);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void data(IStream stream, Callback callback, DataFrame frame)
/*      */   {
/*  612 */     frame(new DataEntry(frame, stream, callback, null), true);
/*      */   }
/*      */   
/*      */   private void frame(HTTP2Flusher.Entry entry, boolean flush)
/*      */   {
/*  617 */     if (LOG.isDebugEnabled()) {
/*  618 */       LOG.debug("{} {}", new Object[] { flush ? "Sending" : "Queueing", entry.frame });
/*      */     }
/*  620 */     boolean queued = entry.frame.getType() == FrameType.PING ? this.flusher.prepend(entry) : this.flusher.append(entry);
/*  621 */     if ((queued) && (flush)) {
/*  622 */       this.flusher.iterate();
/*      */     }
/*      */   }
/*      */   
/*      */   protected IStream createLocalStream(int streamId, Promise<Stream> promise)
/*      */   {
/*      */     for (;;) {
/*  629 */       int localCount = this.localStreamCount.get();
/*  630 */       int maxCount = this.maxLocalStreams;
/*  631 */       if ((maxCount >= 0) && (localCount >= maxCount))
/*      */       {
/*  633 */         promise.failed(new IllegalStateException("Max local stream count " + maxCount + " exceeded"));
/*  634 */         return null;
/*      */       }
/*  636 */       if (this.localStreamCount.compareAndSet(localCount, localCount + 1)) {
/*      */         break;
/*      */       }
/*      */     }
/*  640 */     IStream stream = newStream(streamId, true);
/*  641 */     if (this.streams.putIfAbsent(Integer.valueOf(streamId), stream) == null)
/*      */     {
/*  643 */       stream.setIdleTimeout(getStreamIdleTimeout());
/*  644 */       this.flowControl.onStreamCreated(stream);
/*  645 */       if (LOG.isDebugEnabled())
/*  646 */         LOG.debug("Created local {}", new Object[] { stream });
/*  647 */       return stream;
/*      */     }
/*      */     
/*      */ 
/*  651 */     promise.failed(new IllegalStateException("Duplicate stream " + streamId));
/*  652 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected IStream createRemoteStream(int streamId)
/*      */   {
/*      */     for (;;)
/*      */     {
/*  661 */       int remoteCount = this.remoteStreamCount.get();
/*  662 */       int maxCount = getMaxRemoteStreams();
/*  663 */       if ((maxCount >= 0) && (remoteCount >= maxCount))
/*      */       {
/*  665 */         reset(new ResetFrame(streamId, ErrorCode.REFUSED_STREAM_ERROR.code), Callback.NOOP);
/*  666 */         return null;
/*      */       }
/*  668 */       if (this.remoteStreamCount.compareAndSet(remoteCount, remoteCount + 1)) {
/*      */         break;
/*      */       }
/*      */     }
/*  672 */     IStream stream = newStream(streamId, false);
/*      */     
/*      */ 
/*  675 */     if (this.streams.putIfAbsent(Integer.valueOf(streamId), stream) == null)
/*      */     {
/*  677 */       updateLastStreamId(streamId);
/*  678 */       stream.setIdleTimeout(getStreamIdleTimeout());
/*  679 */       this.flowControl.onStreamCreated(stream);
/*  680 */       if (LOG.isDebugEnabled())
/*  681 */         LOG.debug("Created remote {}", new Object[] { stream });
/*  682 */       return stream;
/*      */     }
/*      */     
/*      */ 
/*  686 */     close(ErrorCode.PROTOCOL_ERROR.code, "duplicate_stream", Callback.NOOP);
/*  687 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected IStream newStream(int streamId, boolean local)
/*      */   {
/*  693 */     return new HTTP2Stream(this.scheduler, this, streamId, local);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeStream(IStream stream)
/*      */   {
/*  699 */     IStream removed = (IStream)this.streams.remove(Integer.valueOf(stream.getId()));
/*  700 */     if (removed != null)
/*      */     {
/*  702 */       assert (removed == stream);
/*      */       
/*  704 */       boolean local = stream.isLocal();
/*  705 */       if (local) {
/*  706 */         this.localStreamCount.decrementAndGet();
/*      */       } else {
/*  708 */         this.remoteStreamCount.decrementAndGet();
/*      */       }
/*  710 */       this.flowControl.onStreamDestroyed(stream);
/*      */       
/*  712 */       if (LOG.isDebugEnabled()) {
/*  713 */         LOG.debug("Removed {} {}", new Object[] { local ? "local" : "remote", stream });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public Collection<Stream> getStreams()
/*      */   {
/*  720 */     List<Stream> result = new ArrayList();
/*  721 */     result.addAll(this.streams.values());
/*  722 */     return result;
/*      */   }
/*      */   
/*      */   @ManagedAttribute("The number of active streams")
/*      */   public int getStreamCount()
/*      */   {
/*  728 */     return this.streams.size();
/*      */   }
/*      */   
/*      */ 
/*      */   public IStream getStream(int streamId)
/*      */   {
/*  734 */     return (IStream)this.streams.get(Integer.valueOf(streamId));
/*      */   }
/*      */   
/*      */   @ManagedAttribute(value="The flow control send window", readonly=true)
/*      */   public int getSendWindow()
/*      */   {
/*  740 */     return this.sendWindow.get();
/*      */   }
/*      */   
/*      */   @ManagedAttribute(value="The flow control receive window", readonly=true)
/*      */   public int getRecvWindow()
/*      */   {
/*  746 */     return this.recvWindow.get();
/*      */   }
/*      */   
/*      */ 
/*      */   public int updateSendWindow(int delta)
/*      */   {
/*  752 */     return this.sendWindow.getAndAdd(delta);
/*      */   }
/*      */   
/*      */ 
/*      */   public int updateRecvWindow(int delta)
/*      */   {
/*  758 */     return this.recvWindow.getAndAdd(delta);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void onWindowUpdate(IStream stream, WindowUpdateFrame frame)
/*      */   {
/*  773 */     this.flusher.window(stream, frame);
/*      */   }
/*      */   
/*      */ 
/*      */   @ManagedAttribute(value="Whether HTTP/2 push is enabled", readonly=true)
/*      */   public boolean isPushEnabled()
/*      */   {
/*  780 */     return this.pushEnabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void onShutdown()
/*      */   {
/*  808 */     if (LOG.isDebugEnabled()) {
/*  809 */       LOG.debug("Shutting down {}", new Object[] { this });
/*      */     }
/*  811 */     switch ((CloseState)this.closed.get())
/*      */     {
/*      */ 
/*      */ 
/*      */     case NOT_CLOSED: 
/*  816 */       if (LOG.isDebugEnabled())
/*  817 */         LOG.debug("Abrupt close for {}", new Object[] { this });
/*  818 */       abort(new ClosedChannelException());
/*  819 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case LOCALLY_CLOSED: 
/*  825 */       control(null, Callback.NOOP, new DisconnectFrame());
/*  826 */       break;
/*      */     case REMOTELY_CLOSED: 
/*      */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean onIdleTimeout()
/*      */   {
/*  865 */     switch ((CloseState)this.closed.get())
/*      */     {
/*      */ 
/*      */     case NOT_CLOSED: 
/*  869 */       return notifyIdleTimeout(this);
/*      */     
/*      */ 
/*      */     case LOCALLY_CLOSED: 
/*      */     case REMOTELY_CLOSED: 
/*  874 */       abort(new TimeoutException("Idle timeout " + this.endPoint.getIdleTimeout() + " ms"));
/*  875 */       return false;
/*      */     }
/*      */     
/*      */     
/*  879 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void onFrame(Frame frame)
/*      */   {
/*  887 */     onConnectionFailure(ErrorCode.PROTOCOL_ERROR.code, "upgrade");
/*      */   }
/*      */   
/*      */   public void disconnect()
/*      */   {
/*  892 */     if (LOG.isDebugEnabled())
/*  893 */       LOG.debug("Disconnecting {}", new Object[] { this });
/*  894 */     this.endPoint.close();
/*      */   }
/*      */   
/*      */   private void terminate()
/*      */   {
/*      */     for (;;)
/*      */     {
/*  901 */       CloseState current = (CloseState)this.closed.get();
/*  902 */       switch (current)
/*      */       {
/*      */ 
/*      */       case NOT_CLOSED: 
/*      */       case LOCALLY_CLOSED: 
/*      */       case REMOTELY_CLOSED: 
/*  908 */         if (this.closed.compareAndSet(current, CloseState.CLOSED))
/*      */         {
/*  910 */           this.flusher.terminate();
/*  911 */           for (IStream stream : this.streams.values())
/*  912 */             stream.close();
/*  913 */           this.streams.clear();
/*  914 */           disconnect();
/*  915 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       default: 
/*  921 */         return;
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */   protected void abort(Throwable failure)
/*      */   {
/*  929 */     terminate();
/*  930 */     notifyFailure(this, failure);
/*      */   }
/*      */   
/*      */   public boolean isDisconnected()
/*      */   {
/*  935 */     return !this.endPoint.isOpen();
/*      */   }
/*      */   
/*      */   private void updateLastStreamId(int streamId)
/*      */   {
/*  940 */     Atomics.updateMax(this.lastStreamId, streamId);
/*      */   }
/*      */   
/*      */   protected Stream.Listener notifyNewStream(Stream stream, HeadersFrame frame)
/*      */   {
/*      */     try
/*      */     {
/*  947 */       return this.listener.onNewStream(stream, frame);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/*  951 */       LOG.info("Failure while notifying listener " + this.listener, x); }
/*  952 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void notifySettings(Session session, SettingsFrame frame)
/*      */   {
/*      */     try
/*      */     {
/*  960 */       this.listener.onSettings(session, frame);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/*  964 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyPing(Session session, PingFrame frame)
/*      */   {
/*      */     try
/*      */     {
/*  972 */       this.listener.onPing(session, frame);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/*  976 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyReset(Session session, ResetFrame frame)
/*      */   {
/*      */     try
/*      */     {
/*  984 */       this.listener.onReset(session, frame);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/*  988 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void notifyClose(Session session, GoAwayFrame frame)
/*      */   {
/*      */     try
/*      */     {
/*  996 */       this.listener.onClose(session, frame);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/* 1000 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean notifyIdleTimeout(Session session)
/*      */   {
/*      */     try
/*      */     {
/* 1008 */       return this.listener.onIdleTimeout(session);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/* 1012 */       LOG.info("Failure while notifying listener " + this.listener, x); }
/* 1013 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void notifyFailure(Session session, Throwable failure)
/*      */   {
/*      */     try
/*      */     {
/* 1021 */       this.listener.onFailure(session, failure);
/*      */     }
/*      */     catch (Throwable x)
/*      */     {
/* 1025 */       LOG.info("Failure while notifying listener " + this.listener, x);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1032 */     return String.format("%s@%x{l:%s <-> r:%s,queueSize=%d,sendWindow=%s,recvWindow=%s,streams=%d,%s}", new Object[] {
/* 1033 */       getClass().getSimpleName(), 
/* 1034 */       Integer.valueOf(hashCode()), 
/* 1035 */       getEndPoint().getLocalAddress(), 
/* 1036 */       getEndPoint().getRemoteAddress(), 
/* 1037 */       Integer.valueOf(this.flusher.getQueueSize()), this.sendWindow, this.recvWindow, 
/*      */       
/*      */ 
/* 1040 */       Integer.valueOf(this.streams.size()), this.closed });
/*      */   }
/*      */   
/*      */   private class ControlEntry
/*      */     extends HTTP2Flusher.Entry
/*      */   {
/*      */     private ControlEntry(Frame frame, IStream stream, Callback callback)
/*      */     {
/* 1048 */       super(stream, callback);
/*      */     }
/*      */     
/*      */     public Throwable generate(ByteBufferPool.Lease lease)
/*      */     {
/*      */       try
/*      */       {
/* 1055 */         HTTP2Session.this.generator.control(lease, this.frame);
/* 1056 */         if (HTTP2Session.LOG.isDebugEnabled())
/* 1057 */           HTTP2Session.LOG.debug("Generated {}", new Object[] { this.frame });
/* 1058 */         prepare();
/* 1059 */         return null;
/*      */       }
/*      */       catch (Throwable x)
/*      */       {
/* 1063 */         if (HTTP2Session.LOG.isDebugEnabled())
/* 1064 */           HTTP2Session.LOG.debug("Failure generating frame " + this.frame, x);
/* 1065 */         return x;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void prepare()
/*      */     {
/* 1084 */       switch (HTTP2Session.3.$SwitchMap$org$eclipse$jetty$http2$frames$FrameType[this.frame.getType().ordinal()])
/*      */       {
/*      */ 
/*      */       case 1: 
/* 1088 */         SettingsFrame settingsFrame = (SettingsFrame)this.frame;
/* 1089 */         Integer initialWindow = (Integer)settingsFrame.getSettings().get(Integer.valueOf(4));
/* 1090 */         if (initialWindow != null) {
/* 1091 */           HTTP2Session.this.flowControl.updateInitialStreamWindow(HTTP2Session.this, initialWindow.intValue(), true);
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       }
/*      */       
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void succeeded()
/*      */     {
/* 1104 */       switch (HTTP2Session.3.$SwitchMap$org$eclipse$jetty$http2$frames$FrameType[this.frame.getType().ordinal()])
/*      */       {
/*      */ 
/*      */       case 2: 
/* 1108 */         HeadersFrame headersFrame = (HeadersFrame)this.frame;
/* 1109 */         if (this.stream.updateClose(headersFrame.isEndStream(), true)) {
/* 1110 */           HTTP2Session.this.removeStream(this.stream);
/*      */         }
/*      */         
/*      */         break;
/*      */       case 3: 
/* 1115 */         if (this.stream != null)
/*      */         {
/* 1117 */           this.stream.close();
/* 1118 */           HTTP2Session.this.removeStream(this.stream);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 4: 
/* 1126 */         this.stream.updateClose(true, false);
/* 1127 */         break;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       case 5: 
/* 1133 */         HTTP2Session.this.getEndPoint().shutdownOutput();
/* 1134 */         break;
/*      */       
/*      */ 
/*      */       case 6: 
/* 1138 */         HTTP2Session.this.flowControl.windowUpdate(HTTP2Session.this, this.stream, (WindowUpdateFrame)this.frame);
/* 1139 */         break;
/*      */       
/*      */ 
/*      */       case 7: 
/* 1143 */         HTTP2Session.this.terminate();
/* 1144 */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1151 */       super.succeeded();
/*      */     }
/*      */   }
/*      */   
/*      */   private class DataEntry extends HTTP2Flusher.Entry
/*      */   {
/*      */     private int length;
/*      */     
/*      */     private DataEntry(DataFrame frame, IStream stream, Callback callback)
/*      */     {
/* 1161 */       super(stream, callback);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int dataRemaining()
/*      */     {
/* 1172 */       return ((DataFrame)this.frame).remaining();
/*      */     }
/*      */     
/*      */     public Throwable generate(ByteBufferPool.Lease lease)
/*      */     {
/*      */       try
/*      */       {
/* 1179 */         int flowControlLength = dataRemaining();
/*      */         
/* 1181 */         int sessionSendWindow = HTTP2Session.this.getSendWindow();
/* 1182 */         if (sessionSendWindow < 0) {
/* 1183 */           throw new IllegalStateException();
/*      */         }
/* 1185 */         int streamSendWindow = this.stream.updateSendWindow(0);
/* 1186 */         if (streamSendWindow < 0) {
/* 1187 */           throw new IllegalStateException();
/*      */         }
/* 1189 */         int window = Math.min(streamSendWindow, sessionSendWindow);
/*      */         
/* 1191 */         int length = this.length = Math.min(flowControlLength, window);
/* 1192 */         if (HTTP2Session.LOG.isDebugEnabled()) {
/* 1193 */           HTTP2Session.LOG.debug("Generated {}, length/window={}/{}", new Object[] { this.frame, Integer.valueOf(length), Integer.valueOf(window) });
/*      */         }
/* 1195 */         HTTP2Session.this.generator.data(lease, (DataFrame)this.frame, length);
/* 1196 */         HTTP2Session.this.flowControl.onDataSending(this.stream, length);
/* 1197 */         return null;
/*      */       }
/*      */       catch (Throwable x)
/*      */       {
/* 1201 */         if (HTTP2Session.LOG.isDebugEnabled())
/* 1202 */           HTTP2Session.LOG.debug("Failure generating frame " + this.frame, x);
/* 1203 */         return x;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void succeeded()
/*      */     {
/* 1210 */       HTTP2Session.this.flowControl.onDataSent(this.stream, this.length);
/*      */       
/* 1212 */       DataFrame dataFrame = (DataFrame)this.frame;
/* 1213 */       if (dataFrame.remaining() > 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1218 */         HTTP2Session.this.flusher.prepend(this);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1224 */         if (this.stream.updateClose(dataFrame.isEndStream(), true))
/* 1225 */           HTTP2Session.this.removeStream(this.stream);
/* 1226 */         super.succeeded();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static class PromiseCallback<C> implements Callback
/*      */   {
/*      */     private final Promise<C> promise;
/*      */     private final C value;
/*      */     
/*      */     private PromiseCallback(Promise<C> promise, C value)
/*      */     {
/* 1238 */       this.promise = promise;
/* 1239 */       this.value = value;
/*      */     }
/*      */     
/*      */ 
/*      */     public void succeeded()
/*      */     {
/* 1245 */       this.promise.succeeded(this.value);
/*      */     }
/*      */     
/*      */ 
/*      */     public void failed(Throwable x)
/*      */     {
/* 1251 */       this.promise.failed(x);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\HTTP2Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */