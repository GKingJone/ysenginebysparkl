/*    */ package com.facebook.presto.jdbc.internal.guava.base;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ abstract class AbstractIterator<T>
/*    */   implements Iterator<T>
/*    */ {
/* 32 */   private State state = State.NOT_READY;
/*    */   private T next;
/*    */   protected abstract T computeNext();
/*    */   
/*    */   private static enum State {
/* 37 */     READY,  NOT_READY,  DONE,  FAILED;
/*    */     
/*    */ 
/*    */     private State() {}
/*    */   }
/*    */   
/*    */   protected final T endOfData()
/*    */   {
/* 45 */     this.state = State.DONE;
/* 46 */     return null;
/*    */   }
/*    */   
/*    */   public final boolean hasNext()
/*    */   {
/* 51 */     Preconditions.checkState(this.state != State.FAILED);
/* 52 */     switch (this.state) {
/*    */     case DONE: 
/* 54 */       return false;
/*    */     case READY: 
/* 56 */       return true;
/*    */     }
/*    */     
/* 59 */     return tryToComputeNext();
/*    */   }
/*    */   
/*    */   private boolean tryToComputeNext() {
/* 63 */     this.state = State.FAILED;
/* 64 */     this.next = computeNext();
/* 65 */     if (this.state != State.DONE) {
/* 66 */       this.state = State.READY;
/* 67 */       return true;
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */   
/*    */   public final T next()
/*    */   {
/* 74 */     if (!hasNext()) {
/* 75 */       throw new NoSuchElementException();
/*    */     }
/* 77 */     this.state = State.NOT_READY;
/* 78 */     T result = this.next;
/* 79 */     this.next = null;
/* 80 */     return result;
/*    */   }
/*    */   
/*    */   public final void remove() {
/* 84 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\AbstractIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */