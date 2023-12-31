/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
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
/*     */ @GwtCompatible(emulated=true)
/*     */ final class LongAdder
/*     */   extends Striped64
/*     */   implements Serializable, LongAddable
/*     */ {
/*     */   private static final long serialVersionUID = 7249069246863182397L;
/*     */   
/*     */   final long fn(long v, long x)
/*     */   {
/*  56 */     return v + x;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(long x)
/*     */   {
/*     */     Striped64.Cell[] as;
/*     */     
/*     */ 
/*     */ 
/*     */     long b;
/*     */     
/*     */ 
/*  71 */     if (((as = this.cells) != null) || (!casBase(b = this.base, b + x))) {
/*  72 */       boolean uncontended = true;
/*  73 */       Striped64.HashCode hc; int h = (hc = (Striped64.HashCode)threadHashCode.get()).code;
/*  74 */       int n; Striped64.Cell a; long v; if ((as == null) || ((n = as.length) < 1) || ((a = as[(n - 1 & h)]) == null) || (!(uncontended = a.cas(v = a.value, v + x))))
/*     */       {
/*     */ 
/*  77 */         retryUpdate(x, hc, uncontended);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void increment()
/*     */   {
/*  85 */     add(1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void decrement()
/*     */   {
/*  92 */     add(-1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long sum()
/*     */   {
/* 105 */     long sum = this.base;
/* 106 */     Striped64.Cell[] as = this.cells;
/* 107 */     if (as != null) {
/* 108 */       int n = as.length;
/* 109 */       for (int i = 0; i < n; i++) {
/* 110 */         Striped64.Cell a = as[i];
/* 111 */         if (a != null)
/* 112 */           sum += a.value;
/*     */       }
/*     */     }
/* 115 */     return sum;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 126 */     internalReset(0L);
/*     */   }
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
/*     */   public long sumThenReset()
/*     */   {
/* 140 */     long sum = this.base;
/* 141 */     Striped64.Cell[] as = this.cells;
/* 142 */     this.base = 0L;
/* 143 */     if (as != null) {
/* 144 */       int n = as.length;
/* 145 */       for (int i = 0; i < n; i++) {
/* 146 */         Striped64.Cell a = as[i];
/* 147 */         if (a != null) {
/* 148 */           sum += a.value;
/* 149 */           a.value = 0L;
/*     */         }
/*     */       }
/*     */     }
/* 153 */     return sum;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 161 */     return Long.toString(sum());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 170 */     return sum();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 178 */     return (int)sum();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 186 */     return (float)sum();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double doubleValue()
/*     */   {
/* 194 */     return sum();
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream s) throws IOException
/*     */   {
/* 199 */     s.defaultWriteObject();
/* 200 */     s.writeLong(sum());
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
/*     */   {
/* 205 */     s.defaultReadObject();
/* 206 */     this.busy = 0;
/* 207 */     this.cells = null;
/* 208 */     this.base = s.readLong();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\LongAdder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */