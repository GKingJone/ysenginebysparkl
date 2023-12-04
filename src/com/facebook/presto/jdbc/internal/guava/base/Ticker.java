/*    */ package com.facebook.presto.jdbc.internal.guava.base;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public abstract class Ticker
/*    */ {
/*    */   public abstract long read();
/*    */   
/*    */   public static Ticker systemTicker()
/*    */   {
/* 54 */     return SYSTEM_TICKER;
/*    */   }
/*    */   
/* 57 */   private static final Ticker SYSTEM_TICKER = new Ticker()
/*    */   {
/*    */     public long read() {
/* 60 */       return Platform.systemNanoTime();
/*    */     }
/*    */   };
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Ticker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */