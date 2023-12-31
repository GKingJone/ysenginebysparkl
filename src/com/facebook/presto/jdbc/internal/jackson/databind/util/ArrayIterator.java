/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ArrayIterator<T>
/*    */   implements Iterator<T>, Iterable<T>
/*    */ {
/*    */   private final T[] _a;
/*    */   private int _index;
/*    */   
/*    */   public ArrayIterator(T[] a)
/*    */   {
/* 17 */     this._a = a;
/* 18 */     this._index = 0;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 22 */     return this._index < this._a.length;
/*    */   }
/*    */   
/*    */   public T next() {
/* 26 */     if (this._index >= this._a.length) {
/* 27 */       throw new NoSuchElementException();
/*    */     }
/* 29 */     return (T)this._a[(this._index++)];
/*    */   }
/*    */   
/* 32 */   public void remove() { throw new UnsupportedOperationException(); }
/* 33 */   public Iterator<T> iterator() { return this; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\ArrayIterator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */