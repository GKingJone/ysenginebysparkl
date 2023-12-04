/*    */ package com.facebook.presto.jdbc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.Objects;
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
/*    */ final class LengthLimitedIterator<T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   private final Iterator<T> iterator;
/*    */   private final long limit;
/*    */   private long count;
/*    */   
/*    */   public LengthLimitedIterator(Iterator<T> iterator, long limit)
/*    */   {
/* 34 */     Preconditions.checkArgument(limit >= 0L, "limit is negative");
/* 35 */     this.iterator = ((Iterator)Objects.requireNonNull(iterator, "iterator is null"));
/* 36 */     this.limit = limit;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean hasNext()
/*    */   {
/* 42 */     return (this.count < this.limit) && (this.iterator.hasNext());
/*    */   }
/*    */   
/*    */ 
/*    */   public T next()
/*    */   {
/* 48 */     if (!hasNext()) {
/* 49 */       throw new NoSuchElementException();
/*    */     }
/*    */     
/* 52 */     this.count += 1L;
/* 53 */     return (T)this.iterator.next();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\LengthLimitedIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */