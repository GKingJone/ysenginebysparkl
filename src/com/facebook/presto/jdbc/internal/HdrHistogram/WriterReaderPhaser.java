/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ public class WriterReaderPhaser
/*     */ {
/*  47 */   private volatile long startEpoch = 0L;
/*  48 */   private volatile long evenEndEpoch = 0L;
/*  49 */   private volatile long oddEndEpoch = Long.MIN_VALUE;
/*     */   
/*  51 */   private final ReentrantLock readerLock = new ReentrantLock();
/*     */   
/*     */ 
/*  54 */   private static final AtomicLongFieldUpdater<WriterReaderPhaser> startEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "startEpoch");
/*     */   
/*  56 */   private static final AtomicLongFieldUpdater<WriterReaderPhaser> evenEndEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "evenEndEpoch");
/*     */   
/*  58 */   private static final AtomicLongFieldUpdater<WriterReaderPhaser> oddEndEpochUpdater = AtomicLongFieldUpdater.newUpdater(WriterReaderPhaser.class, "oddEndEpoch");
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
/*     */   public long writerCriticalSectionEnter()
/*     */   {
/*  74 */     return startEpochUpdater.getAndIncrement(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writerCriticalSectionExit(long criticalValueAtEnter)
/*     */   {
/*  92 */     if (criticalValueAtEnter < 0L) {
/*  93 */       oddEndEpochUpdater.getAndIncrement(this);
/*     */     } else {
/*  95 */       evenEndEpochUpdater.getAndIncrement(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void readerLock()
/*     */   {
/* 108 */     this.readerLock.lock();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void readerUnlock()
/*     */   {
/* 116 */     this.readerLock.unlock();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flipPhase(long yieldTimeNsec)
/*     */   {
/* 138 */     if (!this.readerLock.isHeldByCurrentThread()) {
/* 139 */       throw new IllegalStateException("flipPhase() can only be called while holding the readerLock()");
/*     */     }
/*     */     
/* 142 */     boolean nextPhaseIsEven = this.startEpoch < 0L;
/*     */     
/*     */     long initialStartValue;
/*     */     
/* 146 */     if (nextPhaseIsEven) {
/* 147 */       long initialStartValue = 0L;
/* 148 */       evenEndEpochUpdater.lazySet(this, initialStartValue);
/*     */     } else {
/* 150 */       initialStartValue = Long.MIN_VALUE;
/* 151 */       oddEndEpochUpdater.lazySet(this, initialStartValue);
/*     */     }
/*     */     
/*     */ 
/* 155 */     long startValueAtFlip = startEpochUpdater.getAndSet(this, initialStartValue);
/*     */     
/*     */ 
/* 158 */     boolean caughtUp = false;
/*     */     do {
/* 160 */       if (nextPhaseIsEven) {
/* 161 */         caughtUp = this.oddEndEpoch == startValueAtFlip;
/*     */       } else {
/* 163 */         caughtUp = this.evenEndEpoch == startValueAtFlip;
/*     */       }
/* 165 */       if (!caughtUp) {
/* 166 */         if (yieldTimeNsec == 0L) {
/* 167 */           Thread.yield();
/*     */         } else {
/*     */           try {
/* 170 */             TimeUnit.NANOSECONDS.sleep(yieldTimeNsec);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {}
/*     */         }
/*     */       }
/* 175 */     } while (!caughtUp);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flipPhase()
/*     */   {
/* 196 */     flipPhase(0L);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\WriterReaderPhaser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */