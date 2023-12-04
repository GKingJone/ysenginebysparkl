/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface Callback
/*     */ {
/*  33 */   public static final Callback NOOP = new Callback() {};
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void succeeded() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(Throwable x) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNonBlocking()
/*     */   {
/*  59 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static abstract interface NonBlocking
/*     */     extends Callback
/*     */   {
/*     */     public boolean isNonBlocking()
/*     */     {
/*  70 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Nested implements Callback
/*     */   {
/*     */     private final Callback callback;
/*     */     
/*     */     public Nested(Callback callback)
/*     */     {
/*  80 */       this.callback = callback;
/*     */     }
/*     */     
/*     */     public Nested(Nested nested)
/*     */     {
/*  85 */       this.callback = nested.callback;
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/*  91 */       this.callback.succeeded();
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/*  97 */       this.callback.failed(x);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isNonBlocking()
/*     */     {
/* 103 */       return this.callback.isNonBlocking();
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static class Adapter
/*     */     implements Callback
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Callback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */