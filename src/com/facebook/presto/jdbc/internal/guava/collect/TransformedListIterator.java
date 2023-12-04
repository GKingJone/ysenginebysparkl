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
/*    */ @GwtCompatible
/*    */ abstract class TransformedListIterator<F, T>
/*    */   extends TransformedIterator<F, T>
/*    */   implements ListIterator<T>
/*    */ {
/*    */   TransformedListIterator(ListIterator<? extends F> backingIterator)
/*    */   {
/* 35 */     super(backingIterator);
/*    */   }
/*    */   
/*    */   private ListIterator<? extends F> backingIterator() {
/* 39 */     return Iterators.cast(this.backingIterator);
/*    */   }
/*    */   
/*    */   public final boolean hasPrevious()
/*    */   {
/* 44 */     return backingIterator().hasPrevious();
/*    */   }
/*    */   
/*    */   public final T previous()
/*    */   {
/* 49 */     return (T)transform(backingIterator().previous());
/*    */   }
/*    */   
/*    */   public final int nextIndex()
/*    */   {
/* 54 */     return backingIterator().nextIndex();
/*    */   }
/*    */   
/*    */   public final int previousIndex()
/*    */   {
/* 59 */     return backingIterator().previousIndex();
/*    */   }
/*    */   
/*    */   public void set(T element)
/*    */   {
/* 64 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */   public void add(T element)
/*    */   {
/* 69 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\TransformedListIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */