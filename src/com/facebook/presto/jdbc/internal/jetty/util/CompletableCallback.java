/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CompletableCallback
/*     */   implements Callback
/*     */ {
/*  62 */   private final AtomicReference<State> state = new AtomicReference(State.IDLE);
/*     */   
/*     */ 
/*     */   public void succeeded()
/*     */   {
/*     */     for (;;)
/*     */     {
/*  69 */       State current = (State)this.state.get();
/*  70 */       switch (current)
/*     */       {
/*     */ 
/*     */       case IDLE: 
/*  74 */         if (this.state.compareAndSet(current, State.SUCCEEDED)) {
/*  75 */           return;
/*     */         }
/*     */         
/*     */         break;
/*     */       case COMPLETED: 
/*  80 */         if (this.state.compareAndSet(current, State.SUCCEEDED))
/*     */         {
/*  82 */           resume();
/*  83 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case FAILED: 
/*  89 */         return;
/*     */       
/*     */ 
/*     */       default: 
/*  93 */         throw new IllegalStateException(current.toString());
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable x)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 104 */       State current = (State)this.state.get();
/* 105 */       switch (current)
/*     */       {
/*     */ 
/*     */       case IDLE: 
/*     */       case COMPLETED: 
/* 110 */         if (this.state.compareAndSet(current, State.FAILED))
/*     */         {
/* 112 */           abort(x);
/* 113 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case FAILED: 
/* 119 */         return;
/*     */       
/*     */ 
/*     */       default: 
/* 123 */         throw new IllegalStateException(current.toString());
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void resume();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void abort(Throwable paramThrowable);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean tryComplete()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 153 */       State current = (State)this.state.get();
/* 154 */       switch (current)
/*     */       {
/*     */ 
/*     */       case IDLE: 
/* 158 */         if (this.state.compareAndSet(current, State.COMPLETED)) {
/* 159 */           return true;
/*     */         }
/*     */         
/*     */         break;
/*     */       case FAILED: 
/*     */       case SUCCEEDED: 
/* 165 */         return false;
/*     */       
/*     */       case COMPLETED: 
/*     */       default: 
/* 169 */         throw new IllegalStateException(current.toString());
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   private static enum State
/*     */   {
/* 177 */     IDLE,  SUCCEEDED,  FAILED,  COMPLETED;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\CompletableCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */