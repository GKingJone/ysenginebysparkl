/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingIterator<T>
/*    */   extends ForwardingObject
/*    */   implements Iterator<T>
/*    */ {
/*    */   protected abstract Iterator<T> delegate();
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 43 */     return delegate().hasNext();
/*    */   }
/*    */   
/*    */   public T next()
/*    */   {
/* 48 */     return (T)delegate().next();
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 53 */     delegate().remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */