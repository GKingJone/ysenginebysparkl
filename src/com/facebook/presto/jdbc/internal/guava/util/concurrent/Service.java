/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public abstract interface Service
/*     */ {
/*     */   public abstract Service startAsync();
/*     */   
/*     */   public abstract boolean isRunning();
/*     */   
/*     */   public abstract State state();
/*     */   
/*     */   public abstract Service stopAsync();
/*     */   
/*     */   public abstract void awaitRunning();
/*     */   
/*     */   public abstract void awaitRunning(long paramLong, TimeUnit paramTimeUnit)
/*     */     throws TimeoutException;
/*     */   
/*     */   public abstract void awaitTerminated();
/*     */   
/*     */   public abstract void awaitTerminated(long paramLong, TimeUnit paramTimeUnit)
/*     */     throws TimeoutException;
/*     */   
/*     */   public abstract Throwable failureCause();
/*     */   
/*     */   public abstract void addListener(Listener paramListener, Executor paramExecutor);
/*     */   
/*     */   @Beta
/*     */   public static abstract class Listener
/*     */   {
/*     */     public void starting() {}
/*     */     
/*     */     public void running() {}
/*     */     
/*     */     public void stopping(State from) {}
/*     */     
/*     */     public void terminated(State from) {}
/*     */     
/*     */     public void failed(State from, Throwable failure) {}
/*     */   }
/*     */   
/*     */   @Beta
/*     */   public static abstract enum State
/*     */   {
/* 189 */     NEW, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */     STARTING, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */     RUNNING, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */     STOPPING, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 226 */     TERMINATED, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */     FAILED;
/*     */     
/*     */     private State() {}
/*     */     
/*     */     abstract boolean isTerminal();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\Service.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */