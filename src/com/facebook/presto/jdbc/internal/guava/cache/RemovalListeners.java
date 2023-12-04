/*    */ package com.facebook.presto.jdbc.internal.guava.cache;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.concurrent.Executor;
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
/*    */ @Beta
/*    */ public final class RemovalListeners
/*    */ {
/*    */   public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> listener, Executor executor)
/*    */   {
/* 46 */     Preconditions.checkNotNull(listener);
/* 47 */     Preconditions.checkNotNull(executor);
/* 48 */     new RemovalListener()
/*    */     {
/*    */       public void onRemoval(final RemovalNotification<K, V> notification) {
/* 51 */         this.val$executor.execute(new Runnable()
/*    */         {
/*    */           public void run() {
/* 54 */             RemovalListeners.1.this.val$listener.onRemoval(notification);
/*    */           }
/*    */         });
/*    */       }
/*    */     };
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\RemovalListeners.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */