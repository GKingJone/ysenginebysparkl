/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.LeakDetector;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.LeakDetector.LeakInfo;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class LeakTrackingByteBufferPool
/*     */   extends ContainerLifeCycle
/*     */   implements ByteBufferPool
/*     */ {
/*  32 */   private static final Logger LOG = Log.getLogger(LeakTrackingByteBufferPool.class);
/*     */   
/*  34 */   private final LeakDetector<ByteBuffer> leakDetector = new LeakDetector()
/*     */   {
/*     */     public String id(ByteBuffer resource)
/*     */     {
/*  38 */       return BufferUtil.toIDString(resource);
/*     */     }
/*     */     
/*     */ 
/*     */     protected void leaked(LeakDetector<ByteBuffer>.LeakInfo leakInfo)
/*     */     {
/*  44 */       LeakTrackingByteBufferPool.this.leaked.incrementAndGet();
/*  45 */       LeakTrackingByteBufferPool.this.leaked(leakInfo);
/*     */     }
/*     */   };
/*     */   
/*  49 */   private static final boolean NOISY = Boolean.getBoolean(LeakTrackingByteBufferPool.class.getName() + ".NOISY");
/*     */   private final ByteBufferPool delegate;
/*  51 */   private final AtomicLong leakedReleases = new AtomicLong(0L);
/*  52 */   private final AtomicLong leakedAcquires = new AtomicLong(0L);
/*  53 */   private final AtomicLong leaked = new AtomicLong(0L);
/*     */   
/*     */   public LeakTrackingByteBufferPool(ByteBufferPool delegate)
/*     */   {
/*  57 */     this.delegate = delegate;
/*  58 */     addBean(this.leakDetector);
/*  59 */     addBean(delegate);
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer acquire(int size, boolean direct)
/*     */   {
/*  65 */     ByteBuffer buffer = this.delegate.acquire(size, direct);
/*  66 */     boolean leaked = this.leakDetector.acquired(buffer);
/*  67 */     if ((NOISY) || (!leaked))
/*     */     {
/*  69 */       this.leakedAcquires.incrementAndGet();
/*  70 */       LOG.info(String.format("ByteBuffer acquire %s leaked.acquired=%s", new Object[] { this.leakDetector.id(buffer), leaked ? "normal" : "LEAK" }), new Throwable("LeakStack.Acquire"));
/*     */     }
/*     */     
/*  73 */     return buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void release(ByteBuffer buffer)
/*     */   {
/*  79 */     if (buffer == null)
/*  80 */       return;
/*  81 */     boolean leaked = this.leakDetector.released(buffer);
/*  82 */     if ((NOISY) || (!leaked))
/*     */     {
/*  84 */       this.leakedReleases.incrementAndGet();
/*  85 */       LOG.info(String.format("ByteBuffer release %s leaked.released=%s", new Object[] { this.leakDetector.id(buffer), leaked ? "normal" : "LEAK" }), new Throwable("LeakStack.Release"));
/*     */     }
/*     */     
/*  88 */     this.delegate.release(buffer);
/*     */   }
/*     */   
/*     */   public void clearTracking()
/*     */   {
/*  93 */     this.leakedAcquires.set(0L);
/*  94 */     this.leakedReleases.set(0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLeakedAcquires()
/*     */   {
/* 102 */     return this.leakedAcquires.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLeakedReleases()
/*     */   {
/* 110 */     return this.leakedReleases.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLeakedResources()
/*     */   {
/* 118 */     return this.leaked.get();
/*     */   }
/*     */   
/*     */   protected void leaked(LeakDetector<ByteBuffer>.LeakInfo leakInfo)
/*     */   {
/* 123 */     LOG.warn("ByteBuffer " + leakInfo.getResourceDescription() + " leaked at:", leakInfo.getStackFrames());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\LeakTrackingByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */