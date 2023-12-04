/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class MappedByteBufferPool
/*     */   implements ByteBufferPool
/*     */ {
/*  32 */   private final ConcurrentMap<Integer, Queue<ByteBuffer>> directBuffers = new ConcurrentHashMap();
/*  33 */   private final ConcurrentMap<Integer, Queue<ByteBuffer>> heapBuffers = new ConcurrentHashMap();
/*     */   private final int factor;
/*     */   
/*     */   public MappedByteBufferPool()
/*     */   {
/*  38 */     this(1024);
/*     */   }
/*     */   
/*     */   public MappedByteBufferPool(int factor)
/*     */   {
/*  43 */     this.factor = factor;
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer acquire(int size, boolean direct)
/*     */   {
/*  49 */     int bucket = bucketFor(size);
/*  50 */     ConcurrentMap<Integer, Queue<ByteBuffer>> buffers = buffersFor(direct);
/*     */     
/*  52 */     ByteBuffer result = null;
/*  53 */     Queue<ByteBuffer> byteBuffers = (Queue)buffers.get(Integer.valueOf(bucket));
/*  54 */     if (byteBuffers != null) {
/*  55 */       result = (ByteBuffer)byteBuffers.poll();
/*     */     }
/*  57 */     if (result == null)
/*     */     {
/*  59 */       int capacity = bucket * this.factor;
/*  60 */       result = newByteBuffer(capacity, direct);
/*     */     }
/*     */     
/*  63 */     BufferUtil.clear(result);
/*  64 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   protected ByteBuffer newByteBuffer(int capacity, boolean direct)
/*     */   {
/*  70 */     return direct ? BufferUtil.allocateDirect(capacity) : BufferUtil.allocate(capacity);
/*     */   }
/*     */   
/*     */ 
/*     */   public void release(ByteBuffer buffer)
/*     */   {
/*  76 */     if (buffer == null) {
/*  77 */       return;
/*     */     }
/*     */     
/*  80 */     assert (buffer.capacity() % this.factor == 0);
/*     */     
/*  82 */     int bucket = bucketFor(buffer.capacity());
/*  83 */     ConcurrentMap<Integer, Queue<ByteBuffer>> buffers = buffersFor(buffer.isDirect());
/*     */     
/*     */ 
/*  86 */     Queue<ByteBuffer> byteBuffers = (Queue)buffers.get(Integer.valueOf(bucket));
/*  87 */     if (byteBuffers == null)
/*     */     {
/*  89 */       byteBuffers = new ConcurrentLinkedQueue();
/*  90 */       Queue<ByteBuffer> existing = (Queue)buffers.putIfAbsent(Integer.valueOf(bucket), byteBuffers);
/*  91 */       if (existing != null) {
/*  92 */         byteBuffers = existing;
/*     */       }
/*     */     }
/*  95 */     BufferUtil.clear(buffer);
/*  96 */     byteBuffers.offer(buffer);
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 101 */     this.directBuffers.clear();
/* 102 */     this.heapBuffers.clear();
/*     */   }
/*     */   
/*     */   private int bucketFor(int size)
/*     */   {
/* 107 */     int bucket = size / this.factor;
/* 108 */     if (size % this.factor > 0)
/* 109 */       bucket++;
/* 110 */     return bucket;
/*     */   }
/*     */   
/*     */ 
/*     */   ConcurrentMap<Integer, Queue<ByteBuffer>> buffersFor(boolean direct)
/*     */   {
/* 116 */     return direct ? this.directBuffers : this.heapBuffers;
/*     */   }
/*     */   
/*     */   public static class Tagged extends MappedByteBufferPool
/*     */   {
/* 121 */     private final AtomicInteger tag = new AtomicInteger();
/*     */     
/*     */ 
/*     */     protected ByteBuffer newByteBuffer(int capacity, boolean direct)
/*     */     {
/* 126 */       ByteBuffer buffer = super.newByteBuffer(capacity + 4, direct);
/* 127 */       buffer.limit(buffer.capacity());
/* 128 */       buffer.putInt(this.tag.incrementAndGet());
/* 129 */       ByteBuffer slice = buffer.slice();
/* 130 */       BufferUtil.clear(slice);
/* 131 */       return slice;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\MappedByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */