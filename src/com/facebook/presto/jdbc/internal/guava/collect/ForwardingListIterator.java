/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingListIterator<E>
/*    */   extends ForwardingIterator<E>
/*    */   implements ListIterator<E>
/*    */ {
/*    */   protected abstract ListIterator<E> delegate();
/*    */   
/*    */   public void add(E element)
/*    */   {
/* 43 */     delegate().add(element);
/*    */   }
/*    */   
/*    */   public boolean hasPrevious()
/*    */   {
/* 48 */     return delegate().hasPrevious();
/*    */   }
/*    */   
/*    */   public int nextIndex()
/*    */   {
/* 53 */     return delegate().nextIndex();
/*    */   }
/*    */   
/*    */   public E previous()
/*    */   {
/* 58 */     return (E)delegate().previous();
/*    */   }
/*    */   
/*    */   public int previousIndex()
/*    */   {
/* 63 */     return delegate().previousIndex();
/*    */   }
/*    */   
/*    */   public void set(E element)
/*    */   {
/* 68 */     delegate().set(element);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingListIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */