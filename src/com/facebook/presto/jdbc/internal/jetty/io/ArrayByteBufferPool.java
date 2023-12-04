/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
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
/*     */ public class ArrayByteBufferPool
/*     */   implements ByteBufferPool
/*     */ {
/*     */   private final int _min;
/*     */   private final Bucket[] _direct;
/*     */   private final Bucket[] _indirect;
/*     */   private final int _inc;
/*     */   
/*     */   public ArrayByteBufferPool()
/*     */   {
/*  36 */     this(0, 1024, 65536);
/*     */   }
/*     */   
/*     */   public ArrayByteBufferPool(int minSize, int increment, int maxSize)
/*     */   {
/*  41 */     if (minSize >= increment)
/*  42 */       throw new IllegalArgumentException("minSize >= increment");
/*  43 */     if ((maxSize % increment != 0) || (increment >= maxSize))
/*  44 */       throw new IllegalArgumentException("increment must be a divisor of maxSize");
/*  45 */     this._min = minSize;
/*  46 */     this._inc = increment;
/*     */     
/*  48 */     this._direct = new Bucket[maxSize / increment];
/*  49 */     this._indirect = new Bucket[maxSize / increment];
/*     */     
/*  51 */     int size = 0;
/*  52 */     for (int i = 0; i < this._direct.length; i++)
/*     */     {
/*  54 */       size += this._inc;
/*  55 */       this._direct[i] = new Bucket(size);
/*  56 */       this._indirect[i] = new Bucket(size);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer acquire(int size, boolean direct)
/*     */   {
/*  63 */     Bucket bucket = bucketFor(size, direct);
/*  64 */     ByteBuffer buffer = bucket == null ? null : (ByteBuffer)bucket._queue.poll();
/*     */     
/*  66 */     if (buffer == null)
/*     */     {
/*  68 */       int capacity = bucket == null ? size : bucket._size;
/*  69 */       buffer = direct ? BufferUtil.allocateDirect(capacity) : BufferUtil.allocate(capacity);
/*     */     }
/*     */     
/*  72 */     return buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void release(ByteBuffer buffer)
/*     */   {
/*  78 */     if (buffer != null)
/*     */     {
/*  80 */       Bucket bucket = bucketFor(buffer.capacity(), buffer.isDirect());
/*  81 */       if (bucket != null)
/*     */       {
/*  83 */         BufferUtil.clear(buffer);
/*  84 */         bucket._queue.offer(buffer);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*  91 */     for (int i = 0; i < this._direct.length; i++)
/*     */     {
/*  93 */       this._direct[i]._queue.clear();
/*  94 */       this._indirect[i]._queue.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   private Bucket bucketFor(int size, boolean direct)
/*     */   {
/* 100 */     if (size <= this._min)
/* 101 */       return null;
/* 102 */     int b = (size - 1) / this._inc;
/* 103 */     if (b >= this._direct.length)
/* 104 */       return null;
/* 105 */     Bucket bucket = direct ? this._direct[b] : this._indirect[b];
/*     */     
/* 107 */     return bucket;
/*     */   }
/*     */   
/*     */   public static class Bucket
/*     */   {
/*     */     public final int _size;
/* 113 */     public final Queue<ByteBuffer> _queue = new ConcurrentLinkedQueue();
/*     */     
/*     */     Bucket(int size)
/*     */     {
/* 117 */       this._size = size;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 123 */       return String.format("Bucket@%x{%d,%d}", new Object[] { Integer.valueOf(hashCode()), Integer.valueOf(this._size), Integer.valueOf(this._queue.size()) });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Bucket[] bucketsFor(boolean direct)
/*     */   {
/* 131 */     return direct ? this._direct : this._indirect;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ArrayByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */