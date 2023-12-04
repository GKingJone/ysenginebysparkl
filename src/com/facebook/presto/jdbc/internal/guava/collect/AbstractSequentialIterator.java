/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.NoSuchElementException;
/*    */ import javax.annotation.Nullable;
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
/*    */ public abstract class AbstractSequentialIterator<T>
/*    */   extends UnmodifiableIterator<T>
/*    */ {
/*    */   private T nextOrNull;
/*    */   
/*    */   protected AbstractSequentialIterator(@Nullable T firstOrNull)
/*    */   {
/* 53 */     this.nextOrNull = firstOrNull;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected abstract T computeNext(T paramT);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 66 */     return this.nextOrNull != null;
/*    */   }
/*    */   
/*    */   public final T next()
/*    */   {
/* 71 */     if (!hasNext()) {
/* 72 */       throw new NoSuchElementException();
/*    */     }
/*    */     try {
/* 75 */       return (T)this.nextOrNull;
/*    */     } finally {
/* 77 */       this.nextOrNull = computeNext(this.nextOrNull);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\AbstractSequentialIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */