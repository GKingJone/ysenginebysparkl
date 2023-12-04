/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingQueue<E>
/*     */   extends ForwardingCollection<E>
/*     */   implements Queue<E>
/*     */ {
/*     */   protected abstract Queue<E> delegate();
/*     */   
/*     */   public boolean offer(E o)
/*     */   {
/*  55 */     return delegate().offer(o);
/*     */   }
/*     */   
/*     */   public E poll()
/*     */   {
/*  60 */     return (E)delegate().poll();
/*     */   }
/*     */   
/*     */   public E remove()
/*     */   {
/*  65 */     return (E)delegate().remove();
/*     */   }
/*     */   
/*     */   public E peek()
/*     */   {
/*  70 */     return (E)delegate().peek();
/*     */   }
/*     */   
/*     */   public E element()
/*     */   {
/*  75 */     return (E)delegate().element();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean standardOffer(E e)
/*     */   {
/*     */     try
/*     */     {
/*  87 */       return add(e);
/*     */     } catch (IllegalStateException caught) {}
/*  89 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected E standardPeek()
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return (E)element();
/*     */     } catch (NoSuchElementException caught) {}
/* 104 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected E standardPoll()
/*     */   {
/*     */     try
/*     */     {
/* 117 */       return (E)remove();
/*     */     } catch (NoSuchElementException caught) {}
/* 119 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ForwardingQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */