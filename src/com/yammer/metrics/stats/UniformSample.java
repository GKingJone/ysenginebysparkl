/*    */ package com.yammer.metrics.stats;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ import java.util.concurrent.atomic.AtomicLongArray;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UniformSample
/*    */   implements Sample
/*    */ {
/*    */   private static final int BITS_PER_LONG = 63;
/* 16 */   private final AtomicLong count = new AtomicLong();
/*    */   
/*    */ 
/*    */   private final AtomicLongArray values;
/*    */   
/*    */ 
/*    */ 
/*    */   public UniformSample(int reservoirSize)
/*    */   {
/* 25 */     this.values = new AtomicLongArray(reservoirSize);
/* 26 */     clear();
/*    */   }
/*    */   
/*    */   public void clear()
/*    */   {
/* 31 */     for (int i = 0; i < this.values.length(); i++) {
/* 32 */       this.values.set(i, 0L);
/*    */     }
/* 34 */     this.count.set(0L);
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 39 */     long c = this.count.get();
/* 40 */     if (c > this.values.length()) {
/* 41 */       return this.values.length();
/*    */     }
/* 43 */     return (int)c;
/*    */   }
/*    */   
/*    */   public void update(long value)
/*    */   {
/* 48 */     long c = this.count.incrementAndGet();
/* 49 */     if (c <= this.values.length()) {
/* 50 */       this.values.set((int)c - 1, value);
/*    */     } else {
/* 52 */       long r = nextLong(c);
/* 53 */       if (r < this.values.length()) {
/* 54 */         this.values.set((int)r, value);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private static long nextLong(long n)
/*    */   {
/*    */     long bits;
/*    */     
/*    */     long val;
/*    */     
/*    */     do
/*    */     {
/* 69 */       bits = ThreadLocalRandom.current().nextLong() & 0x7FFFFFFFFFFFFFFF;
/* 70 */       val = bits % n;
/* 71 */     } while (bits - val + (n - 1L) < 0L);
/* 72 */     return val;
/*    */   }
/*    */   
/*    */   public Snapshot getSnapshot()
/*    */   {
/* 77 */     int s = size();
/* 78 */     List<Long> copy = new ArrayList(s);
/* 79 */     for (int i = 0; i < s; i++) {
/* 80 */       copy.add(Long.valueOf(this.values.get(i)));
/*    */     }
/* 82 */     return new Snapshot(copy);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\UniformSample.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */