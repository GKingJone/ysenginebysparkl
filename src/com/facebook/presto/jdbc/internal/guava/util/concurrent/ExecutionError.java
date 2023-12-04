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
/*    */ @GwtCompatible
/*    */ public class ExecutionError
/*    */   extends Error
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   protected ExecutionError() {}
/*    */   
/*    */   protected ExecutionError(@Nullable String message)
/*    */   {
/* 46 */     super(message);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ExecutionError(@Nullable String message, @Nullable Error cause)
/*    */   {
/* 53 */     super(message, cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ExecutionError(@Nullable Error cause)
/*    */   {
/* 60 */     super(cause);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ExecutionError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */