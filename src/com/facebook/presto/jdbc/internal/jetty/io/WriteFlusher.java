/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.WritePendingException;
/*     */ import java.util.Arrays;
/*     */ import java.util.EnumMap;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
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
/*     */ public abstract class WriteFlusher
/*     */ {
/*  46 */   private static final Logger LOG = Log.getLogger(WriteFlusher.class);
/*  47 */   private static final boolean DEBUG = LOG.isDebugEnabled();
/*  48 */   private static final ByteBuffer[] EMPTY_BUFFERS = { BufferUtil.EMPTY_BUFFER };
/*  49 */   private static final EnumMap<StateType, Set<StateType>> __stateTransitions = new EnumMap(StateType.class);
/*  50 */   private static final State __IDLE = new IdleState(null);
/*  51 */   private static final State __WRITING = new WritingState(null);
/*  52 */   private static final State __COMPLETING = new CompletingState(null);
/*     */   private final EndPoint _endPoint;
/*  54 */   private final AtomicReference<State> _state = new AtomicReference();
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  59 */     __stateTransitions.put(StateType.IDLE, EnumSet.of(StateType.WRITING));
/*  60 */     __stateTransitions.put(StateType.WRITING, EnumSet.of(StateType.IDLE, StateType.PENDING, StateType.FAILED));
/*  61 */     __stateTransitions.put(StateType.PENDING, EnumSet.of(StateType.COMPLETING, StateType.IDLE));
/*  62 */     __stateTransitions.put(StateType.COMPLETING, EnumSet.of(StateType.IDLE, StateType.PENDING, StateType.FAILED));
/*  63 */     __stateTransitions.put(StateType.FAILED, EnumSet.of(StateType.IDLE));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected WriteFlusher(EndPoint endPoint)
/*     */   {
/*  90 */     this._state.set(__IDLE);
/*  91 */     this._endPoint = endPoint;
/*     */   }
/*     */   
/*     */   private static enum StateType
/*     */   {
/*  96 */     IDLE, 
/*  97 */     WRITING, 
/*  98 */     PENDING, 
/*  99 */     COMPLETING, 
/* 100 */     FAILED;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private StateType() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean updateState(State previous, State next)
/*     */   {
/* 112 */     if (!isTransitionAllowed(previous, next)) {
/* 113 */       throw new IllegalStateException();
/*     */     }
/* 115 */     boolean updated = this._state.compareAndSet(previous, next);
/* 116 */     if (DEBUG)
/* 117 */       LOG.debug("update {}:{}{}{}", new Object[] { this, previous, updated ? "-->" : "!->", next });
/* 118 */     return updated;
/*     */   }
/*     */   
/*     */   private void fail(PendingState pending)
/*     */   {
/* 123 */     State current = (State)this._state.get();
/* 124 */     if (current.getType() == StateType.FAILED)
/*     */     {
/* 126 */       FailedState failed = (FailedState)current;
/* 127 */       if (updateState(failed, __IDLE))
/*     */       {
/* 129 */         pending.fail(failed.getCause());
/* 130 */         return;
/*     */       }
/*     */     }
/* 133 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   private void ignoreFail()
/*     */   {
/* 138 */     State current = (State)this._state.get();
/* 139 */     while (current.getType() == StateType.FAILED)
/*     */     {
/* 141 */       if (updateState(current, __IDLE))
/* 142 */         return;
/* 143 */       current = (State)this._state.get();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isTransitionAllowed(State currentState, State newState)
/*     */   {
/* 149 */     Set<StateType> allowedNewStateTypes = (Set)__stateTransitions.get(currentState.getType());
/* 150 */     if (!allowedNewStateTypes.contains(newState.getType()))
/*     */     {
/* 152 */       LOG.warn("{}: {} -> {} not allowed", new Object[] { this, currentState, newState });
/* 153 */       return false;
/*     */     }
/* 155 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class State
/*     */   {
/*     */     private final StateType _type;
/*     */     
/*     */ 
/*     */     private State(StateType stateType)
/*     */     {
/* 167 */       this._type = stateType;
/*     */     }
/*     */     
/*     */     public StateType getType()
/*     */     {
/* 172 */       return this._type;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 178 */       return String.format("%s", new Object[] { this._type });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class IdleState
/*     */     extends State
/*     */   {
/*     */     private IdleState()
/*     */     {
/* 189 */       super(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class WritingState
/*     */     extends State
/*     */   {
/*     */     private WritingState()
/*     */     {
/* 200 */       super(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class FailedState
/*     */     extends State
/*     */   {
/*     */     private final Throwable _cause;
/*     */     
/*     */     private FailedState(Throwable cause)
/*     */     {
/* 212 */       super(null);
/* 213 */       this._cause = cause;
/*     */     }
/*     */     
/*     */     public Throwable getCause()
/*     */     {
/* 218 */       return this._cause;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class CompletingState
/*     */     extends State
/*     */   {
/*     */     private CompletingState()
/*     */     {
/* 231 */       super(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private class PendingState
/*     */     extends State
/*     */   {
/*     */     private final Callback _callback;
/*     */     
/*     */     private final ByteBuffer[] _buffers;
/*     */     
/*     */ 
/*     */     private PendingState(ByteBuffer[] buffers, Callback callback)
/*     */     {
/* 246 */       super(null);
/* 247 */       this._buffers = buffers;
/* 248 */       this._callback = callback;
/*     */     }
/*     */     
/*     */     public ByteBuffer[] getBuffers()
/*     */     {
/* 253 */       return this._buffers;
/*     */     }
/*     */     
/*     */     protected boolean fail(Throwable cause)
/*     */     {
/* 258 */       if (this._callback != null)
/*     */       {
/* 260 */         this._callback.failed(cause);
/* 261 */         return true;
/*     */       }
/* 263 */       return false;
/*     */     }
/*     */     
/*     */     protected void complete()
/*     */     {
/* 268 */       if (this._callback != null) {
/* 269 */         this._callback.succeeded();
/*     */       }
/*     */     }
/*     */     
/*     */     boolean isCallbackNonBlocking() {
/* 274 */       return (this._callback != null) && (this._callback.isNonBlocking());
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isCallbackNonBlocking()
/*     */   {
/* 280 */     State s = (State)this._state.get();
/* 281 */     return ((s instanceof PendingState)) && (((PendingState)s).isCallbackNonBlocking());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(Callback callback, ByteBuffer... buffers)
/*     */     throws WritePendingException
/*     */   {
/* 305 */     if (DEBUG) {
/* 306 */       LOG.debug("write: {} {}", new Object[] { this, BufferUtil.toDetailString(buffers) });
/*     */     }
/* 308 */     if (!updateState(__IDLE, __WRITING)) {
/* 309 */       throw new WritePendingException();
/*     */     }
/*     */     try
/*     */     {
/* 313 */       buffers = flush(buffers);
/*     */       
/*     */ 
/* 316 */       if (buffers != null)
/*     */       {
/* 318 */         if (DEBUG)
/* 319 */           LOG.debug("flushed incomplete", new Object[0]);
/* 320 */         PendingState pending = new PendingState(buffers, callback, null);
/* 321 */         if (updateState(__WRITING, pending)) {
/* 322 */           onIncompleteFlush();
/*     */         } else
/* 324 */           fail(pending);
/* 325 */         return;
/*     */       }
/*     */       
/*     */ 
/* 329 */       if (!updateState(__WRITING, __IDLE))
/* 330 */         ignoreFail();
/* 331 */       if (callback != null) {
/* 332 */         callback.succeeded();
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 336 */       if (DEBUG)
/* 337 */         LOG.debug("write exception", e);
/* 338 */       if (updateState(__WRITING, __IDLE))
/*     */       {
/* 340 */         if (callback != null) {
/* 341 */           callback.failed(e);
/*     */         }
/*     */       } else {
/* 344 */         fail(new PendingState(buffers, callback, null));
/*     */       }
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
/*     */   public void completeWrite()
/*     */   {
/* 359 */     if (DEBUG) {
/* 360 */       LOG.debug("completeWrite: {}", new Object[] { this });
/*     */     }
/* 362 */     State previous = (State)this._state.get();
/*     */     
/* 364 */     if (previous.getType() != StateType.PENDING) {
/* 365 */       return;
/*     */     }
/* 367 */     PendingState pending = (PendingState)previous;
/* 368 */     if (!updateState(pending, __COMPLETING)) {
/* 369 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 373 */       ByteBuffer[] buffers = pending.getBuffers();
/*     */       
/* 375 */       buffers = flush(buffers);
/*     */       
/*     */ 
/* 378 */       if (buffers != null)
/*     */       {
/* 380 */         if (DEBUG)
/* 381 */           LOG.debug("flushed incomplete {}", new Object[] { BufferUtil.toDetailString(buffers) });
/* 382 */         if (buffers != pending.getBuffers())
/* 383 */           pending = new PendingState(buffers, pending._callback, null);
/* 384 */         if (updateState(__COMPLETING, pending)) {
/* 385 */           onIncompleteFlush();
/*     */         } else
/* 387 */           fail(pending);
/* 388 */         return;
/*     */       }
/*     */       
/*     */ 
/* 392 */       if (!updateState(__COMPLETING, __IDLE))
/* 393 */         ignoreFail();
/* 394 */       pending.complete();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 398 */       if (DEBUG)
/* 399 */         LOG.debug("completeWrite exception", e);
/* 400 */       if (updateState(__COMPLETING, __IDLE)) {
/* 401 */         pending.fail(e);
/*     */       } else {
/* 403 */         fail(pending);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ByteBuffer[] flush(ByteBuffer[] buffers)
/*     */     throws IOException
/*     */   {
/* 415 */     boolean progress = true;
/* 416 */     while ((progress) && (buffers != null))
/*     */     {
/* 418 */       int before = buffers.length == 0 ? 0 : buffers[0].remaining();
/* 419 */       boolean flushed = this._endPoint.flush(buffers);
/* 420 */       int r = buffers.length == 0 ? 0 : buffers[0].remaining();
/*     */       
/* 422 */       if (LOG.isDebugEnabled()) {
/* 423 */         LOG.debug("Flushed={} {}/{}+{} {}", new Object[] { Boolean.valueOf(flushed), Integer.valueOf(before - r), Integer.valueOf(before), Integer.valueOf(buffers.length - 1), this });
/*     */       }
/* 425 */       if (flushed) {
/* 426 */         return null;
/*     */       }
/* 428 */       progress = before != r;
/*     */       
/* 430 */       int not_empty = 0;
/* 431 */       while (r == 0)
/*     */       {
/* 433 */         not_empty++; if (not_empty == buffers.length)
/*     */         {
/* 435 */           buffers = null;
/* 436 */           not_empty = 0;
/* 437 */           break;
/*     */         }
/* 439 */         progress = true;
/* 440 */         r = buffers[not_empty].remaining();
/*     */       }
/*     */       
/* 443 */       if (not_empty > 0) {
/* 444 */         buffers = (ByteBuffer[])Arrays.copyOfRange(buffers, not_empty, buffers.length);
/*     */       }
/*     */     }
/* 447 */     if (LOG.isDebugEnabled()) {
/* 448 */       LOG.debug("!fully flushed {}", new Object[] { this });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 453 */     return buffers == null ? EMPTY_BUFFERS : buffers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onFail(Throwable cause)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 466 */       State current = (State)this._state.get();
/* 467 */       switch (current.getType())
/*     */       {
/*     */       case IDLE: 
/*     */       case FAILED: 
/* 471 */         if (DEBUG)
/* 472 */           LOG.debug("ignored: {} {}", new Object[] { this, cause });
/* 473 */         return false;
/*     */       
/*     */       case PENDING: 
/* 476 */         if (DEBUG) {
/* 477 */           LOG.debug("failed: {} {}", new Object[] { this, cause });
/*     */         }
/* 479 */         PendingState pending = (PendingState)current;
/* 480 */         if (updateState(pending, __IDLE)) {
/* 481 */           return pending.fail(cause);
/*     */         }
/*     */         break;
/*     */       default: 
/* 485 */         if (DEBUG) {
/* 486 */           LOG.debug("failed: {} {}", new Object[] { this, cause });
/*     */         }
/* 488 */         if (updateState(current, new FailedState(cause, null))) {
/* 489 */           return false;
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void onClose() {
/* 497 */     if (this._state.get() == __IDLE)
/* 498 */       return;
/* 499 */     onFail(new ClosedChannelException());
/*     */   }
/*     */   
/*     */   boolean isIdle()
/*     */   {
/* 504 */     return ((State)this._state.get()).getType() == StateType.IDLE;
/*     */   }
/*     */   
/*     */   public boolean isInProgress()
/*     */   {
/* 509 */     switch (((State)this._state.get()).getType())
/*     */     {
/*     */     case PENDING: 
/*     */     case WRITING: 
/*     */     case COMPLETING: 
/* 514 */       return true;
/*     */     }
/* 516 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 523 */     return String.format("WriteFlusher@%x{%s}", new Object[] { Integer.valueOf(hashCode()), this._state.get() });
/*     */   }
/*     */   
/*     */   public String toStateString()
/*     */   {
/* 528 */     switch (((State)this._state.get()).getType())
/*     */     {
/*     */     case WRITING: 
/* 531 */       return "W";
/*     */     case PENDING: 
/* 533 */       return "P";
/*     */     case COMPLETING: 
/* 535 */       return "C";
/*     */     case IDLE: 
/* 537 */       return "-";
/*     */     case FAILED: 
/* 539 */       return "F";
/*     */     }
/* 541 */     return "?";
/*     */   }
/*     */   
/*     */   protected abstract void onIncompleteFlush();
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\WriteFlusher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */