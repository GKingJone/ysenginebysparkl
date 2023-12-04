/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker.Lock;
/*     */ import java.nio.channels.ClosedChannelException;
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
/*     */ public abstract class IteratingCallback
/*     */   implements Callback
/*     */ {
/*     */   private static enum State
/*     */   {
/*  67 */     IDLE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */     PROCESSING, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  79 */     PENDING, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  84 */     CALLED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */     SUCCEEDED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  95 */     FAILED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 100 */     CLOSED;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private State() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static enum Action
/*     */   {
/* 114 */     IDLE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     SCHEDULED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 125 */     SUCCEEDED;
/*     */     
/*     */     private Action() {} }
/* 128 */   private Locker _locker = new Locker();
/*     */   
/*     */   private State _state;
/*     */   private boolean _iterate;
/*     */   
/*     */   protected IteratingCallback()
/*     */   {
/* 135 */     this._state = State.IDLE;
/*     */   }
/*     */   
/*     */   protected IteratingCallback(boolean needReset)
/*     */   {
/* 140 */     this._state = (needReset ? State.SUCCEEDED : State.IDLE);
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
/*     */   protected abstract Action process()
/*     */     throws Exception;
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
/*     */   protected void onCompleteSuccess() {}
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
/*     */   protected void onCompleteFailure(Throwable cause) {}
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
/*     */   public void iterate()
/*     */   {
/* 191 */     boolean process = false;
/*     */     
/*     */ 
/*     */ 
/* 195 */     Lock lock = this._locker.lock();Throwable localThrowable6 = null;
/*     */     try {
/* 197 */       switch (this._state)
/*     */       {
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
/*     */       case PENDING: 
/*     */       case CALLED: 
/* 221 */         if (lock != null) if (localThrowable6 != null) try { lock.close(); } catch (Throwable localThrowable) { localThrowable6.addSuppressed(localThrowable); } else lock.close();
/*     */         break;
/*     */       case IDLE: 
/* 205 */         this._state = State.PROCESSING;
/* 206 */         process = true;
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
/* 221 */         if (lock != null) if (localThrowable6 != null) try { lock.close(); } catch (Throwable localThrowable1) { localThrowable6.addSuppressed(localThrowable1); } else lock.close();
/*     */         break;
/*     */       case PROCESSING: 
/* 210 */         this._iterate = true;
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
/* 221 */         if (lock != null) if (localThrowable6 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable6.addSuppressed(localThrowable2); } else lock.close(); break; case FAILED: case SUCCEEDED:  if (lock != null) if (localThrowable6 != null) try { lock.close(); } catch (Throwable localThrowable3) { localThrowable6.addSuppressed(localThrowable3); } else lock.close();
/*     */         break;
/*     */       case CLOSED: 
/*     */       default: 
/* 219 */         throw new IllegalStateException(toString());
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 195 */       localThrowable6 = localThrowable4;throw localThrowable4;
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
/*     */     }
/*     */     finally
/*     */     {
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
/* 221 */       if (lock != null) if (localThrowable6 != null) try { lock.close(); } catch (Throwable localThrowable5) { localThrowable6.addSuppressed(localThrowable5); } else lock.close();
/*     */     }
/* 223 */     if (process) {
/* 224 */       processing();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void processing()
/*     */   {
/* 232 */     boolean on_complete_success = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/*     */       try
/*     */       {
/* 241 */         action = process();
/*     */       }
/*     */       catch (Throwable x) {
/*     */         Action action;
/* 245 */         failed(x);
/* 246 */         break;
/*     */       }
/*     */       
/*     */       Action action;
/* 250 */       Lock lock = this._locker.lock();Throwable localThrowable9 = null;
/*     */       try {
/* 252 */         switch (this._state)
/*     */         {
/*     */ 
/*     */         case PROCESSING: 
/* 256 */           switch (action)
/*     */           {
/*     */ 
/*     */ 
/*     */           case IDLE: 
/* 261 */             if (this._iterate)
/*     */             {
/*     */ 
/* 264 */               this._iterate = false;
/* 265 */               this._state = State.PROCESSING;
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
/* 321 */               if (lock != null) if (localThrowable9 != null) { try { lock.close(); } catch (Throwable localThrowable1) {} localThrowable9.addSuppressed(localThrowable1); } else { lock.close();
/*     */                 }
/*     */             }
/*     */             else
/*     */             {
/* 270 */               this._state = State.IDLE;
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
/* 321 */               if (lock != null) if (localThrowable9 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable9.addSuppressed(localThrowable2); } else lock.close();
/*     */             }
/*     */             break;
/*     */           case SCHEDULED: 
/* 277 */             this._state = State.PENDING;
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
/*     */ 
/*     */ 
/*     */ 
/* 321 */             if (lock != null) if (localThrowable9 != null) try { lock.close(); } catch (Throwable localThrowable3) { localThrowable9.addSuppressed(localThrowable3); } else lock.close();
/*     */             break;
/*     */           case SUCCEEDED: 
/* 284 */             this._iterate = false;
/* 285 */             this._state = State.SUCCEEDED;
/* 286 */             on_complete_success = true;
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
/* 321 */             if (lock != null) if (localThrowable9 != null) try { lock.close(); } catch (Throwable localThrowable4) { localThrowable9.addSuppressed(localThrowable4); } else lock.close();
/*     */             break;
/*     */           default: 
/* 291 */             throw new IllegalStateException(String.format("%s[action=%s]", new Object[] { this, action }));
/*     */           }
/*     */           
/*     */           
/*     */           break;
/*     */         case CALLED: 
/* 297 */           switch (action)
/*     */           {
/*     */ 
/*     */ 
/*     */           case SCHEDULED: 
/* 302 */             this._state = State.PROCESSING;
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
/* 321 */             if (lock != null) if (localThrowable9 != null) { try { lock.close(); } catch (Throwable localThrowable5) {} localThrowable9.addSuppressed(localThrowable5); } else { lock.close();
/*     */               }
/*     */             break;
/*     */           default: 
/* 307 */             throw new IllegalStateException(String.format("%s[action=%s]", new Object[] { this, action }));
/*     */           }
/*     */           
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           break;
/*     */         case FAILED: 
/*     */         case SUCCEEDED: 
/*     */         case CLOSED: 
/* 321 */           if (lock != null) if (localThrowable9 != null) try { lock.close(); } catch (Throwable localThrowable6) { localThrowable9.addSuppressed(localThrowable6); } else lock.close();
/*     */           break;
/*     */         case PENDING: 
/*     */         case IDLE: 
/*     */         default: 
/* 319 */           throw new IllegalStateException(String.format("%s[action=%s]", new Object[] { this, action }));
/*     */         }
/*     */         
/*     */       }
/*     */       catch (Throwable localThrowable7)
/*     */       {
/* 250 */         localThrowable9 = localThrowable7;throw localThrowable7;
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
/*     */       }
/*     */       finally
/*     */       {
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
/* 321 */         if (lock != null) if (localThrowable9 != null) try { lock.close(); } catch (Throwable localThrowable8) { localThrowable9.addSuppressed(localThrowable8); } else lock.close();
/*     */       }
/*     */     }
/* 324 */     if (on_complete_success) {
/* 325 */       onCompleteSuccess();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void succeeded()
/*     */   {
/* 336 */     boolean process = false;
/* 337 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 339 */       switch (this._state)
/*     */       {
/*     */ 
/*     */       case PROCESSING: 
/* 343 */         this._state = State.CALLED;
/* 344 */         break;
/*     */       
/*     */ 
/*     */       case PENDING: 
/* 348 */         this._state = State.PROCESSING;
/* 349 */         process = true;
/* 350 */         break;
/*     */       
/*     */       case FAILED: 
/*     */       case CLOSED: 
/*     */         break;
/*     */       
/*     */       case CALLED: 
/*     */       case IDLE: 
/*     */       case SUCCEEDED: 
/*     */       default: 
/* 360 */         throw new IllegalStateException(toString());
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 337 */       localThrowable3 = localThrowable1;throw localThrowable1;
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
/*     */     }
/*     */     finally
/*     */     {
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
/* 363 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 364 */     if (process) {
/* 365 */       processing();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(Throwable x)
/*     */   {
/* 376 */     boolean failure = false;
/* 377 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 379 */       switch (this._state)
/*     */       {
/*     */       case CALLED: 
/*     */       case IDLE: 
/*     */       case FAILED: 
/*     */       case SUCCEEDED: 
/*     */       case CLOSED: 
/*     */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       case PENDING: 
/*     */       case PROCESSING: 
/* 392 */         this._state = State.FAILED;
/* 393 */         failure = true;
/* 394 */         break;
/*     */       
/*     */       default: 
/* 397 */         throw new IllegalStateException(toString());
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 377 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 399 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 400 */     if (failure) {
/* 401 */       onCompleteFailure(x);
/*     */     }
/*     */   }
/*     */   
/*     */   public void close() {
/* 406 */     boolean failure = false;
/* 407 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 409 */       switch (this._state)
/*     */       {
/*     */       case IDLE: 
/*     */       case FAILED: 
/*     */       case SUCCEEDED: 
/* 414 */         this._state = State.CLOSED;
/* 415 */         break;
/*     */       case CLOSED: 
/*     */         break;
/*     */       
/*     */       case PROCESSING: 
/*     */       default: 
/* 421 */         this._state = State.CLOSED;
/* 422 */         failure = true;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 407 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 424 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close();
/*     */     }
/* 426 */     if (failure) {
/* 427 */       onCompleteFailure(new ClosedChannelException());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isIdle()
/*     */   {
/* 436 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 438 */       return this._state == State.IDLE;
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 436 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 439 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isClosed() {
/* 444 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 446 */       return this._state == State.CLOSED;
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 444 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 447 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isFailed()
/*     */   {
/* 455 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 457 */       return this._state == State.FAILED;
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 455 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 458 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isSucceeded()
/*     */   {
/* 466 */     Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 468 */       return this._state == State.SUCCEEDED;
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 466 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 469 */       if (lock != null) { if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else { lock.close();
/*     */         }
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
/*     */   public boolean reset()
/*     */   {
/* 483 */     Lock lock = this._locker.lock();Throwable localThrowable5 = null;
/*     */     try { boolean bool;
/* 485 */       switch (this._state)
/*     */       {
/*     */       case IDLE: 
/* 488 */         return true;
/*     */       
/*     */       case FAILED: 
/*     */       case SUCCEEDED: 
/* 492 */         this._iterate = false;
/* 493 */         this._state = State.IDLE;
/* 494 */         return true;
/*     */       }
/*     */       
/* 497 */       return false;
/*     */     }
/*     */     catch (Throwable localThrowable6)
/*     */     {
/* 483 */       localThrowable5 = localThrowable6;throw localThrowable6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 499 */       if (lock != null) if (localThrowable5 != null) try { lock.close(); } catch (Throwable localThrowable4) { localThrowable5.addSuppressed(localThrowable4); } else lock.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 505 */     return String.format("%s[%s]", new Object[] { super.toString(), this._state });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IteratingCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */