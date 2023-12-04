/*     */ package com.google.common.collect;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.BlockingDeque;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ForwardingBlockingDeque<E>
/*     */   extends ForwardingDeque<E>
/*     */   implements BlockingDeque<E>
/*     */ {
/*     */   protected abstract BlockingDeque<E> delegate();
/*     */   
/*     */   public int remainingCapacity()
/*     */   {
/*  51 */     return delegate().remainingCapacity();
/*     */   }
/*     */   
/*     */   public void putFirst(E e) throws InterruptedException
/*     */   {
/*  56 */     delegate().putFirst(e);
/*     */   }
/*     */   
/*     */   public void putLast(E e) throws InterruptedException
/*     */   {
/*  61 */     delegate().putLast(e);
/*     */   }
/*     */   
/*     */   public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  66 */     return delegate().offerFirst(e, timeout, unit);
/*     */   }
/*     */   
/*     */   public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  71 */     return delegate().offerLast(e, timeout, unit);
/*     */   }
/*     */   
/*     */   public E takeFirst() throws InterruptedException
/*     */   {
/*  76 */     return (E)delegate().takeFirst();
/*     */   }
/*     */   
/*     */   public E takeLast() throws InterruptedException
/*     */   {
/*  81 */     return (E)delegate().takeLast();
/*     */   }
/*     */   
/*     */   public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  86 */     return (E)delegate().pollFirst(timeout, unit);
/*     */   }
/*     */   
/*     */   public E pollLast(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  91 */     return (E)delegate().pollLast(timeout, unit);
/*     */   }
/*     */   
/*     */   public void put(E e) throws InterruptedException
/*     */   {
/*  96 */     delegate().put(e);
/*     */   }
/*     */   
/*     */   public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 101 */     return delegate().offer(e, timeout, unit);
/*     */   }
/*     */   
/*     */   public E take() throws InterruptedException
/*     */   {
/* 106 */     return (E)delegate().take();
/*     */   }
/*     */   
/*     */   public E poll(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 111 */     return (E)delegate().poll(timeout, unit);
/*     */   }
/*     */   
/*     */   public int drainTo(Collection<? super E> c)
/*     */   {
/* 116 */     return delegate().drainTo(c);
/*     */   }
/*     */   
/*     */   public int drainTo(Collection<? super E> c, int maxElements)
/*     */   {
/* 121 */     return delegate().drainTo(c, maxElements);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ForwardingBlockingDeque.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */