/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface ByteBufferPool
/*     */ {
/*     */   public abstract ByteBuffer acquire(int paramInt, boolean paramBoolean);
/*     */   
/*     */   public abstract void release(ByteBuffer paramByteBuffer);
/*     */   
/*     */   public static class Lease
/*     */   {
/*     */     private final ByteBufferPool byteBufferPool;
/*     */     private final List<ByteBuffer> buffers;
/*     */     private final List<Boolean> recycles;
/*     */     
/*     */     public Lease(ByteBufferPool byteBufferPool)
/*     */     {
/*  64 */       this.byteBufferPool = byteBufferPool;
/*  65 */       this.buffers = new ArrayList();
/*  66 */       this.recycles = new ArrayList();
/*     */     }
/*     */     
/*     */     public ByteBuffer acquire(int capacity, boolean direct)
/*     */     {
/*  71 */       ByteBuffer buffer = this.byteBufferPool.acquire(capacity, direct);
/*  72 */       BufferUtil.clearToFill(buffer);
/*  73 */       return buffer;
/*     */     }
/*     */     
/*     */     public void append(ByteBuffer buffer, boolean recycle)
/*     */     {
/*  78 */       this.buffers.add(buffer);
/*  79 */       this.recycles.add(Boolean.valueOf(recycle));
/*     */     }
/*     */     
/*     */     public void insert(int index, ByteBuffer buffer, boolean recycle)
/*     */     {
/*  84 */       this.buffers.add(index, buffer);
/*  85 */       this.recycles.add(index, Boolean.valueOf(recycle));
/*     */     }
/*     */     
/*     */     public List<ByteBuffer> getByteBuffers()
/*     */     {
/*  90 */       return this.buffers;
/*     */     }
/*     */     
/*     */     public long getTotalLength()
/*     */     {
/*  95 */       long length = 0L;
/*  96 */       for (int i = 0; i < this.buffers.size(); i++)
/*  97 */         length += ((ByteBuffer)this.buffers.get(i)).remaining();
/*  98 */       return length;
/*     */     }
/*     */     
/*     */     public int getSize()
/*     */     {
/* 103 */       return this.buffers.size();
/*     */     }
/*     */     
/*     */     public void recycle()
/*     */     {
/* 108 */       for (int i = 0; i < this.buffers.size(); i++)
/*     */       {
/* 110 */         ByteBuffer buffer = (ByteBuffer)this.buffers.get(i);
/* 111 */         if (((Boolean)this.recycles.get(i)).booleanValue())
/* 112 */           this.byteBufferPool.release(buffer);
/*     */       }
/* 114 */       this.buffers.clear();
/* 115 */       this.recycles.clear();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */