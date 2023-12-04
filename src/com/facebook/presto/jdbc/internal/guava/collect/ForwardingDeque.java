/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ForwardingDeque<E>
/*     */   extends ForwardingQueue<E>
/*     */   implements Deque<E>
/*     */ {
/*     */   protected abstract Deque<E> delegate();
/*     */   
/*     */   public void addFirst(E e)
/*     */   {
/*  47 */     delegate().addFirst(e);
/*     */   }
/*     */   
/*     */   public void addLast(E e)
/*     */   {
/*  52 */     delegate().addLast(e);
/*     */   }
/*     */   
/*     */   public Iterator<E> descendingIterator()
/*     */   {
/*  57 */     return delegate().descendingIterator();
/*     */   }
/*     */   
/*     */   public E getFirst()
/*     */   {
/*  62 */     return (E)delegate().getFirst();
/*     */   }
/*     */   
/*     */   public E getLast()
/*     */   {
/*  67 */     return (E)delegate().getLast();
/*     */   }
/*     */   
/*     */   public boolean offerFirst(E e)
/*     */   {
/*  72 */     return delegate().offerFirst(e);
/*     */   }
/*     */   
/*     */   public boolean offerLast(E e)
/*     */   {
/*  77 */     return delegate().offerLast(e);
/*     */   }
/*     */   
/*     */   public E peekFirst()
/*     */   {
/*  82 */     return (E)delegate().peekFirst();
/*     */   }
/*     */   
/*     */   public E peekLast()
/*     */   {
/*  87 */     return (E)delegate().peekLast();
/*     */   }
/*     */   
/*     */   public E pollFirst()
/*     */   {
/*  92 */     return (E)delegate().pollFirst();
/*     */   }
/*     */   
/*     */   public E pollLast()
/*     */   {
/*  97 */     return (E)delegate().pollLast();
/*     */   }
/*     */   
/*     */   public E pop()
/*     */   {
/* 102 */     return (E)delegate().pop();
/*     */   }
/*     */   
/*     */   public void push(E e)
/*     */   {
/* 107 */     delegate().push(e);
/*     */   }
/*     */   
/*     */   public E removeFirst()
/*     */   {
/* 112 */     return (E)delegate().removeFirst();
/*     */   }
/*     */   
/*     */   public E removeLast()
/*     */   {
/* 117 */     return (E)delegate().removeLast();
/*     */   }
/*     */   
/*     */   public boolean removeFirstOccurrence(Object o)
/*     */   {
/* 122 */     return delegate().removeFirstOccurrence(o);
/*     */   }
/*     */   
/*     */   public boolean removeLastOccurrence(Object o)
/*     */   {
/* 127 */     return delegate().removeLastOccurrence(o);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingDeque.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */