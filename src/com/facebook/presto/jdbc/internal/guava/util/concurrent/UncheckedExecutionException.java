/*    */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ @GwtCompatible
/*    */ public class UncheckedExecutionException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   protected UncheckedExecutionException() {}
/*    */   
/*    */   protected UncheckedExecutionException(@Nullable String message)
/*    */   {
/* 51 */     super(message);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UncheckedExecutionException(@Nullable String message, @Nullable Throwable cause)
/*    */   {
/* 58 */     super(message, cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UncheckedExecutionException(@Nullable Throwable cause)
/*    */   {
/* 65 */     super(cause);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\UncheckedExecutionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */