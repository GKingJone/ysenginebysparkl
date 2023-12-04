/*    */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
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
/*    */ @Beta
/*    */ public abstract class ForwardingCheckedFuture<V, X extends Exception>
/*    */   extends ForwardingListenableFuture<V>
/*    */   implements CheckedFuture<V, X>
/*    */ {
/*    */   public V checkedGet()
/*    */     throws Exception
/*    */   {
/* 46 */     return (V)delegate().checkedGet();
/*    */   }
/*    */   
/*    */   public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception
/*    */   {
/* 51 */     return (V)delegate().checkedGet(timeout, unit);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected abstract CheckedFuture<V, X> delegate();
/*    */   
/*    */ 
/*    */ 
/*    */   @Beta
/*    */   public static abstract class SimpleForwardingCheckedFuture<V, X extends Exception>
/*    */     extends ForwardingCheckedFuture<V, X>
/*    */   {
/*    */     private final CheckedFuture<V, X> delegate;
/*    */     
/*    */ 
/*    */ 
/*    */     protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> delegate)
/*    */     {
/* 70 */       this.delegate = ((CheckedFuture)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */     
/*    */     protected final CheckedFuture<V, X> delegate()
/*    */     {
/* 75 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ForwardingCheckedFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */