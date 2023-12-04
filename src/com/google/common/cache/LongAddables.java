/*    */ package com.google.common.cache;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Supplier;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class LongAddables
/*    */ {
/*    */   private static final Supplier<LongAddable> SUPPLIER;
/*    */   
/*    */   static
/*    */   {
/*    */     Supplier<LongAddable> supplier;
/*    */     try
/*    */     {
/* 37 */       new LongAdder();
/* 38 */       supplier = new Supplier()
/*    */       {
/*    */         public LongAddable get() {
/* 41 */           return new LongAdder();
/*    */         }
/*    */       };
/*    */     } catch (Throwable t) {
/* 45 */       supplier = new Supplier()
/*    */       {
/*    */         public LongAddable get() {
/* 48 */           return new PureJavaLongAddable(null);
/*    */         }
/*    */       };
/*    */     }
/* 52 */     SUPPLIER = supplier;
/*    */   }
/*    */   
/*    */   public static LongAddable create() {
/* 56 */     return (LongAddable)SUPPLIER.get();
/*    */   }
/*    */   
/*    */   private static final class PureJavaLongAddable extends AtomicLong implements LongAddable
/*    */   {
/*    */     public void increment() {
/* 62 */       getAndIncrement();
/*    */     }
/*    */     
/*    */     public void add(long x)
/*    */     {
/* 67 */       getAndAdd(x);
/*    */     }
/*    */     
/*    */     public long sum()
/*    */     {
/* 72 */       return get();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\LongAddables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */