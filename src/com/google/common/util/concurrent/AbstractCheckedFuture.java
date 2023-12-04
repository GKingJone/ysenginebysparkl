/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
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
/*     */ @Beta
/*     */ public abstract class AbstractCheckedFuture<V, X extends Exception>
/*     */   extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
/*     */   implements CheckedFuture<V, X>
/*     */ {
/*     */   protected AbstractCheckedFuture(ListenableFuture<V> delegate)
/*     */   {
/*  41 */     super(delegate);
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
/*     */   protected abstract X mapException(Exception paramException);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public V checkedGet()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  78 */       return (V)get();
/*     */     } catch (InterruptedException e) {
/*  80 */       Thread.currentThread().interrupt();
/*  81 */       throw mapException(e);
/*     */     } catch (CancellationException e) {
/*  83 */       throw mapException(e);
/*     */     } catch (ExecutionException e) {
/*  85 */       throw mapException(e);
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
/*     */   public V checkedGet(long timeout, TimeUnit unit)
/*     */     throws TimeoutException, Exception
/*     */   {
/*     */     try
/*     */     {
/* 107 */       return (V)get(timeout, unit);
/*     */     } catch (InterruptedException e) {
/* 109 */       Thread.currentThread().interrupt();
/* 110 */       throw mapException(e);
/*     */     } catch (CancellationException e) {
/* 112 */       throw mapException(e);
/*     */     } catch (ExecutionException e) {
/* 114 */       throw mapException(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractCheckedFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */