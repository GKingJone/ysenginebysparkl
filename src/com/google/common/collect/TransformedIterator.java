/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.Iterator;
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
/*    */ abstract class TransformedIterator<F, T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   final Iterator<? extends F> backingIterator;
/*    */   
/*    */   TransformedIterator(Iterator<? extends F> backingIterator)
/*    */   {
/* 36 */     this.backingIterator = ((Iterator)Preconditions.checkNotNull(backingIterator));
/*    */   }
/*    */   
/*    */   abstract T transform(F paramF);
/*    */   
/*    */   public final boolean hasNext()
/*    */   {
/* 43 */     return this.backingIterator.hasNext();
/*    */   }
/*    */   
/*    */   public final T next()
/*    */   {
/* 48 */     return (T)transform(this.backingIterator.next());
/*    */   }
/*    */   
/*    */   public final void remove()
/*    */   {
/* 53 */     this.backingIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\TransformedIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */