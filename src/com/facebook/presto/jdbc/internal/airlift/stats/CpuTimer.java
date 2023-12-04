/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ @Beta
/*     */ public class CpuTimer
/*     */ {
/*  28 */   private static final ThreadMXBean THREAD_MX_BEAN = ;
/*     */   
/*     */   private final long wallStartTime;
/*     */   
/*     */   private final long cpuStartTime;
/*     */   private final long userStartTime;
/*     */   private long intervalWallStart;
/*     */   private long intervalCpuStart;
/*     */   private long intervalUserStart;
/*     */   
/*     */   public CpuTimer()
/*     */   {
/*  40 */     this.wallStartTime = System.nanoTime();
/*  41 */     this.cpuStartTime = THREAD_MX_BEAN.getCurrentThreadCpuTime();
/*  42 */     this.userStartTime = THREAD_MX_BEAN.getCurrentThreadUserTime();
/*     */     
/*  44 */     this.intervalWallStart = this.wallStartTime;
/*  45 */     this.intervalCpuStart = this.cpuStartTime;
/*  46 */     this.intervalUserStart = this.userStartTime;
/*     */   }
/*     */   
/*     */   public CpuDuration startNewInterval()
/*     */   {
/*  51 */     long currentWallTime = System.nanoTime();
/*  52 */     long currentCpuTime = THREAD_MX_BEAN.getCurrentThreadCpuTime();
/*  53 */     long currentUserTime = THREAD_MX_BEAN.getCurrentThreadUserTime();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  58 */     CpuDuration cpuDuration = new CpuDuration(nanosBetween(this.intervalWallStart, currentWallTime), nanosBetween(this.intervalCpuStart, currentCpuTime), nanosBetween(this.intervalUserStart, currentUserTime));
/*     */     
/*  60 */     this.intervalWallStart = currentWallTime;
/*  61 */     this.intervalCpuStart = currentCpuTime;
/*  62 */     this.intervalUserStart = currentUserTime;
/*     */     
/*  64 */     return cpuDuration;
/*     */   }
/*     */   
/*     */   public CpuDuration elapsedIntervalTime()
/*     */   {
/*  69 */     long currentWallTime = System.nanoTime();
/*  70 */     long currentCpuTime = THREAD_MX_BEAN.getCurrentThreadCpuTime();
/*  71 */     long currentUserTime = THREAD_MX_BEAN.getCurrentThreadUserTime();
/*     */     
/*  73 */     return new CpuDuration(
/*  74 */       nanosBetween(this.intervalWallStart, currentWallTime), 
/*  75 */       nanosBetween(this.intervalCpuStart, currentCpuTime), 
/*  76 */       nanosBetween(this.intervalUserStart, currentUserTime));
/*     */   }
/*     */   
/*     */   public CpuDuration elapsedTime()
/*     */   {
/*  81 */     long currentWallTime = System.nanoTime();
/*  82 */     long currentCpuTime = THREAD_MX_BEAN.getCurrentThreadCpuTime();
/*  83 */     long currentUserTime = THREAD_MX_BEAN.getCurrentThreadUserTime();
/*     */     
/*  85 */     return new CpuDuration(
/*  86 */       nanosBetween(this.wallStartTime, currentWallTime), 
/*  87 */       nanosBetween(this.cpuStartTime, currentCpuTime), 
/*  88 */       nanosBetween(this.userStartTime, currentUserTime));
/*     */   }
/*     */   
/*     */   private static Duration nanosBetween(long start, long end)
/*     */   {
/*  93 */     return new Duration(Math.abs(end - start), TimeUnit.NANOSECONDS);
/*     */   }
/*     */   
/*     */   public static class CpuDuration
/*     */   {
/*     */     private final Duration wall;
/*     */     private final Duration cpu;
/*     */     private final Duration user;
/*     */     
/*     */     public CpuDuration()
/*     */     {
/* 104 */       this.wall = new Duration(0.0D, TimeUnit.NANOSECONDS);
/* 105 */       this.cpu = new Duration(0.0D, TimeUnit.NANOSECONDS);
/* 106 */       this.user = new Duration(0.0D, TimeUnit.NANOSECONDS);
/*     */     }
/*     */     
/*     */     public CpuDuration(Duration wall, Duration cpu, Duration user)
/*     */     {
/* 111 */       this.wall = wall;
/* 112 */       this.cpu = cpu;
/* 113 */       this.user = user;
/*     */     }
/*     */     
/*     */     public Duration getWall()
/*     */     {
/* 118 */       return this.wall;
/*     */     }
/*     */     
/*     */     public Duration getCpu()
/*     */     {
/* 123 */       return this.cpu;
/*     */     }
/*     */     
/*     */     public Duration getUser()
/*     */     {
/* 128 */       return this.user;
/*     */     }
/*     */     
/*     */     public CpuDuration add(CpuDuration cpuDuration)
/*     */     {
/* 133 */       return new CpuDuration(
/* 134 */         addDurations(this.wall, cpuDuration.wall), 
/* 135 */         addDurations(this.cpu, cpuDuration.cpu), 
/* 136 */         addDurations(this.user, cpuDuration.user));
/*     */     }
/*     */     
/*     */     public CpuDuration subtract(CpuDuration cpuDuration)
/*     */     {
/* 141 */       return new CpuDuration(
/* 142 */         subtractDurations(this.wall, cpuDuration.wall), 
/* 143 */         subtractDurations(this.cpu, cpuDuration.cpu), 
/* 144 */         subtractDurations(this.user, cpuDuration.user));
/*     */     }
/*     */     
/*     */     private static Duration addDurations(Duration a, Duration b)
/*     */     {
/* 149 */       return new Duration(a.getValue(TimeUnit.NANOSECONDS) + b.getValue(TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS);
/*     */     }
/*     */     
/*     */     private static Duration subtractDurations(Duration a, Duration b)
/*     */     {
/* 154 */       return new Duration(Math.max(0.0D, a.getValue(TimeUnit.NANOSECONDS) - b.getValue(TimeUnit.NANOSECONDS)), TimeUnit.NANOSECONDS);
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 160 */       return 
/*     */       
/*     */ 
/*     */ 
/* 164 */         MoreObjects.toStringHelper(this).add("wall", this.wall).add("cpu", this.cpu).add("user", this.user).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\CpuTimer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */