/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.management.GarbageCollectorMXBean;
/*     */ import java.lang.management.LockInfo;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryMXBean;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.Map;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ 
/*     */ public class VirtualMachineMetrics
/*     */ {
/*     */   private static final int MAX_STACK_TRACE_DEPTH = 100;
/*  19 */   private static final VirtualMachineMetrics INSTANCE = new VirtualMachineMetrics(ManagementFactory.getMemoryMXBean(), ManagementFactory.getMemoryPoolMXBeans(), ManagementFactory.getOperatingSystemMXBean(), ManagementFactory.getThreadMXBean(), ManagementFactory.getGarbageCollectorMXBeans(), ManagementFactory.getRuntimeMXBean(), ManagementFactory.getPlatformMBeanServer());
/*     */   
/*     */   private final MemoryMXBean memory;
/*     */   
/*     */   private final java.util.List<MemoryPoolMXBean> memoryPools;
/*     */   
/*     */   private final java.lang.management.OperatingSystemMXBean os;
/*     */   
/*     */   private final ThreadMXBean threads;
/*     */   private final java.util.List<GarbageCollectorMXBean> garbageCollectors;
/*     */   private final java.lang.management.RuntimeMXBean runtime;
/*     */   private final javax.management.MBeanServer mBeanServer;
/*     */   
/*     */   public static VirtualMachineMetrics getInstance()
/*     */   {
/*  34 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public static class GarbageCollectorStats
/*     */   {
/*     */     private final long runs;
/*     */     private final long timeMS;
/*     */     
/*     */     private GarbageCollectorStats(long runs, long timeMS)
/*     */     {
/*  44 */       this.runs = runs;
/*  45 */       this.timeMS = timeMS;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getRuns()
/*     */     {
/*  54 */       return this.runs;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getTime(java.util.concurrent.TimeUnit unit)
/*     */     {
/*  64 */       return unit.convert(this.timeMS, java.util.concurrent.TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static class BufferPoolStats
/*     */   {
/*     */     private final long count;
/*     */     private final long memoryUsed;
/*     */     private final long totalCapacity;
/*     */     
/*     */     private BufferPoolStats(long count, long memoryUsed, long totalCapacity)
/*     */     {
/*  77 */       this.count = count;
/*  78 */       this.memoryUsed = memoryUsed;
/*  79 */       this.totalCapacity = totalCapacity;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getCount()
/*     */     {
/*  88 */       return this.count;
/*     */     }
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
/*     */     public long getMemoryUsed()
/*     */     {
/* 102 */       return this.memoryUsed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getTotalCapacity()
/*     */     {
/* 113 */       return this.totalCapacity;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   VirtualMachineMetrics(MemoryMXBean memory, java.util.List<MemoryPoolMXBean> memoryPools, java.lang.management.OperatingSystemMXBean os, ThreadMXBean threads, java.util.List<GarbageCollectorMXBean> garbageCollectors, java.lang.management.RuntimeMXBean runtime, javax.management.MBeanServer mBeanServer)
/*     */   {
/* 131 */     this.memory = memory;
/* 132 */     this.memoryPools = memoryPools;
/* 133 */     this.os = os;
/* 134 */     this.threads = threads;
/* 135 */     this.garbageCollectors = garbageCollectors;
/* 136 */     this.runtime = runtime;
/* 137 */     this.mBeanServer = mBeanServer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double totalInit()
/*     */   {
/* 146 */     return this.memory.getHeapMemoryUsage().getInit() + this.memory.getNonHeapMemoryUsage().getInit();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double totalUsed()
/*     */   {
/* 156 */     return this.memory.getHeapMemoryUsage().getUsed() + this.memory.getNonHeapMemoryUsage().getUsed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double totalMax()
/*     */   {
/* 166 */     return this.memory.getHeapMemoryUsage().getMax() + this.memory.getNonHeapMemoryUsage().getMax();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double totalCommitted()
/*     */   {
/* 175 */     return this.memory.getHeapMemoryUsage().getCommitted() + this.memory.getNonHeapMemoryUsage().getCommitted();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double heapInit()
/*     */   {
/* 184 */     return this.memory.getHeapMemoryUsage().getInit();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double heapUsed()
/*     */   {
/* 192 */     return this.memory.getHeapMemoryUsage().getUsed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double heapMax()
/*     */   {
/* 200 */     return this.memory.getHeapMemoryUsage().getMax();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double heapCommitted()
/*     */   {
/* 208 */     return this.memory.getHeapMemoryUsage().getCommitted();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double heapUsage()
/*     */   {
/* 217 */     MemoryUsage usage = this.memory.getHeapMemoryUsage();
/* 218 */     return usage.getUsed() / usage.getMax();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double nonHeapUsage()
/*     */   {
/* 228 */     MemoryUsage usage = this.memory.getNonHeapMemoryUsage();
/* 229 */     return usage.getUsed() / usage.getMax();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Double> memoryPoolUsage()
/*     */   {
/* 238 */     Map<String, Double> pools = new java.util.TreeMap();
/* 239 */     for (MemoryPoolMXBean pool : this.memoryPools) {
/* 240 */       double max = pool.getUsage().getMax() == -1L ? pool.getUsage().getCommitted() : pool.getUsage().getMax();
/*     */       
/*     */ 
/* 243 */       pools.put(pool.getName(), Double.valueOf(pool.getUsage().getUsed() / max));
/*     */     }
/* 245 */     return java.util.Collections.unmodifiableMap(pools);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double fileDescriptorUsage()
/*     */   {
/*     */     try
/*     */     {
/* 256 */       java.lang.reflect.Method getOpenFileDescriptorCount = this.os.getClass().getDeclaredMethod("getOpenFileDescriptorCount", new Class[0]);
/* 257 */       getOpenFileDescriptorCount.setAccessible(true);
/* 258 */       Long openFds = (Long)getOpenFileDescriptorCount.invoke(this.os, new Object[0]);
/* 259 */       java.lang.reflect.Method getMaxFileDescriptorCount = this.os.getClass().getDeclaredMethod("getMaxFileDescriptorCount", new Class[0]);
/* 260 */       getMaxFileDescriptorCount.setAccessible(true);
/* 261 */       Long maxFds = (Long)getMaxFileDescriptorCount.invoke(this.os, new Object[0]);
/* 262 */       return openFds.doubleValue() / maxFds.doubleValue();
/*     */     } catch (NoSuchMethodException e) {
/* 264 */       return NaN.0D;
/*     */     } catch (IllegalAccessException e) {
/* 266 */       return NaN.0D;
/*     */     } catch (java.lang.reflect.InvocationTargetException e) {}
/* 268 */     return NaN.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String version()
/*     */   {
/* 280 */     return System.getProperty("java.runtime.version");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String name()
/*     */   {
/* 290 */     return System.getProperty("java.vm.name");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long uptime()
/*     */   {
/* 299 */     return java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(this.runtime.getUptime());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int threadCount()
/*     */   {
/* 308 */     return this.threads.getThreadCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int daemonThreadCount()
/*     */   {
/* 317 */     return this.threads.getDaemonThreadCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, GarbageCollectorStats> garbageCollectors()
/*     */   {
/* 326 */     Map<String, GarbageCollectorStats> stats = new java.util.HashMap();
/* 327 */     for (GarbageCollectorMXBean gc : this.garbageCollectors) {
/* 328 */       stats.put(gc.getName(), new GarbageCollectorStats(gc.getCollectionCount(), gc.getCollectionTime(), null));
/*     */     }
/*     */     
/*     */ 
/* 332 */     return java.util.Collections.unmodifiableMap(stats);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public java.util.Set<String> deadlockedThreads()
/*     */   {
/* 341 */     long[] threadIds = this.threads.findDeadlockedThreads();
/* 342 */     if (threadIds != null) {
/* 343 */       java.util.Set<String> threads = new java.util.HashSet();
/* 344 */       for (ThreadInfo info : this.threads.getThreadInfo(threadIds, 100)) {
/* 345 */         StringBuilder stackTrace = new StringBuilder();
/* 346 */         for (StackTraceElement element : info.getStackTrace()) {
/* 347 */           stackTrace.append("\t at ").append(element.toString()).append('\n');
/*     */         }
/*     */         
/* 350 */         threads.add(String.format("%s locked on %s (owned by %s):\n%s", new Object[] { info.getThreadName(), info.getLockName(), info.getLockOwnerName(), stackTrace.toString() }));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 359 */       return java.util.Collections.unmodifiableSet(threads);
/*     */     }
/* 361 */     return java.util.Collections.emptySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<Thread.State, Double> threadStatePercentages()
/*     */   {
/* 370 */     Map<Thread.State, Double> conditions = new java.util.HashMap();
/* 371 */     for (Thread.State state : Thread.State.values()) {
/* 372 */       conditions.put(state, Double.valueOf(0.0D));
/*     */     }
/*     */     
/* 375 */     long[] allThreadIds = this.threads.getAllThreadIds();
/* 376 */     ThreadInfo[] allThreads = this.threads.getThreadInfo(allThreadIds);
/* 377 */     int liveCount = 0;
/* 378 */     for (ThreadInfo info : allThreads) {
/* 379 */       if (info != null) {
/* 380 */         Thread.State state = info.getThreadState();
/* 381 */         conditions.put(state, Double.valueOf(((Double)conditions.get(state)).doubleValue() + 1.0D));
/* 382 */         liveCount++;
/*     */       }
/*     */     }
/* 385 */     for (Thread.State state : new java.util.ArrayList(conditions.keySet())) {
/* 386 */       conditions.put(state, Double.valueOf(((Double)conditions.get(state)).doubleValue() / liveCount));
/*     */     }
/*     */     
/* 389 */     return java.util.Collections.unmodifiableMap(conditions);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void threadDump(java.io.OutputStream out)
/*     */   {
/* 398 */     ThreadInfo[] threads = this.threads.dumpAllThreads(true, true);
/* 399 */     PrintWriter writer = new PrintWriter(out, true);
/*     */     
/* 401 */     for (int ti = threads.length - 1; ti >= 0; ti--) {
/* 402 */       ThreadInfo t = threads[ti];
/* 403 */       writer.printf("%s id=%d state=%s", new Object[] { t.getThreadName(), Long.valueOf(t.getThreadId()), t.getThreadState() });
/*     */       
/*     */ 
/*     */ 
/* 407 */       LockInfo lock = t.getLockInfo();
/* 408 */       if ((lock != null) && (t.getThreadState() != Thread.State.BLOCKED)) {
/* 409 */         writer.printf("\n    - waiting on <0x%08x> (a %s)", new Object[] { Integer.valueOf(lock.getIdentityHashCode()), lock.getClassName() });
/*     */         
/*     */ 
/* 412 */         writer.printf("\n    - locked <0x%08x> (a %s)", new Object[] { Integer.valueOf(lock.getIdentityHashCode()), lock.getClassName() });
/*     */ 
/*     */       }
/* 415 */       else if ((lock != null) && (t.getThreadState() == Thread.State.BLOCKED)) {
/* 416 */         writer.printf("\n    - waiting to lock <0x%08x> (a %s)", new Object[] { Integer.valueOf(lock.getIdentityHashCode()), lock.getClassName() });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 421 */       if (t.isSuspended()) {
/* 422 */         writer.print(" (suspended)");
/*     */       }
/*     */       
/* 425 */       if (t.isInNative()) {
/* 426 */         writer.print(" (running in native)");
/*     */       }
/*     */       
/* 429 */       writer.println();
/* 430 */       if (t.getLockOwnerName() != null) {
/* 431 */         writer.printf("     owned by %s id=%d\n", new Object[] { t.getLockOwnerName(), Long.valueOf(t.getLockOwnerId()) });
/*     */       }
/*     */       
/* 434 */       StackTraceElement[] elements = t.getStackTrace();
/* 435 */       java.lang.management.MonitorInfo[] monitors = t.getLockedMonitors();
/*     */       
/* 437 */       for (int i = 0; i < elements.length; i++) {
/* 438 */         StackTraceElement element = elements[i];
/* 439 */         writer.printf("    at %s\n", new Object[] { element });
/* 440 */         for (int j = 1; j < monitors.length; j++) {
/* 441 */           java.lang.management.MonitorInfo monitor = monitors[j];
/* 442 */           if (monitor.getLockedStackDepth() == i) {
/* 443 */             writer.printf("      - locked %s\n", new Object[] { monitor });
/*     */           }
/*     */         }
/*     */       }
/* 447 */       writer.println();
/*     */       
/* 449 */       LockInfo[] locks = t.getLockedSynchronizers();
/* 450 */       if (locks.length > 0) {
/* 451 */         writer.printf("    Locked synchronizers: count = %d\n", new Object[] { Integer.valueOf(locks.length) });
/* 452 */         for (LockInfo l : locks) {
/* 453 */           writer.printf("      - %s\n", new Object[] { l });
/*     */         }
/* 455 */         writer.println();
/*     */       }
/*     */     }
/*     */     
/* 459 */     writer.println();
/* 460 */     writer.flush();
/*     */   }
/*     */   
/*     */   public Map<String, BufferPoolStats> getBufferPoolStats() {
/*     */     try {
/* 465 */       String[] attributes = { "Count", "MemoryUsed", "TotalCapacity" };
/*     */       
/* 467 */       javax.management.ObjectName direct = new javax.management.ObjectName("java.nio:type=BufferPool,name=direct");
/* 468 */       javax.management.ObjectName mapped = new javax.management.ObjectName("java.nio:type=BufferPool,name=mapped");
/*     */       
/* 470 */       AttributeList directAttributes = this.mBeanServer.getAttributes(direct, attributes);
/* 471 */       AttributeList mappedAttributes = this.mBeanServer.getAttributes(mapped, attributes);
/*     */       
/* 473 */       Map<String, BufferPoolStats> stats = new java.util.TreeMap();
/*     */       
/* 475 */       BufferPoolStats directStats = new BufferPoolStats(((Long)((Attribute)directAttributes.get(0)).getValue()).longValue(), ((Long)((Attribute)directAttributes.get(1)).getValue()).longValue(), ((Long)((Attribute)directAttributes.get(2)).getValue()).longValue(), null);
/*     */       
/*     */ 
/*     */ 
/* 479 */       stats.put("direct", directStats);
/*     */       
/* 481 */       BufferPoolStats mappedStats = new BufferPoolStats(((Long)((Attribute)mappedAttributes.get(0)).getValue()).longValue(), ((Long)((Attribute)mappedAttributes.get(1)).getValue()).longValue(), ((Long)((Attribute)mappedAttributes.get(2)).getValue()).longValue(), null);
/*     */       
/*     */ 
/*     */ 
/* 485 */       stats.put("mapped", mappedStats);
/*     */       
/* 487 */       return java.util.Collections.unmodifiableMap(stats);
/*     */     } catch (javax.management.JMException e) {}
/* 489 */     return java.util.Collections.emptyMap();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\VirtualMachineMetrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */