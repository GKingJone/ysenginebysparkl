/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Objects;
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
/*    */ public class FixedPageSource
/*    */   implements ConnectorPageSource
/*    */ {
/*    */   private final Iterator<Page> pages;
/*    */   private final long totalBytes;
/*    */   private long completedBytes;
/*    */   private long memoryUsageBytes;
/*    */   private boolean closed;
/*    */   
/*    */   public FixedPageSource(Iterable<Page> pages)
/*    */   {
/* 33 */     this.pages = ((Iterable)Objects.requireNonNull(pages, "pages is null")).iterator();
/*    */     
/* 35 */     long totalSize = 0L;
/* 36 */     for (Page page : pages) {
/* 37 */       totalSize += page.getSizeInBytes();
/*    */     }
/* 39 */     this.totalBytes = totalSize;
/*    */     
/* 41 */     long memoryUsageBytes = 0L;
/* 42 */     for (Page page : pages) {
/* 43 */       memoryUsageBytes += page.getRetainedSizeInBytes();
/*    */     }
/* 45 */     this.memoryUsageBytes = memoryUsageBytes;
/*    */   }
/*    */   
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 52 */     this.closed = true;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getTotalBytes()
/*    */   {
/* 58 */     return this.totalBytes;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getCompletedBytes()
/*    */   {
/* 64 */     return this.completedBytes;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getReadTimeNanos()
/*    */   {
/* 70 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isFinished()
/*    */   {
/* 76 */     return (this.closed) || (!this.pages.hasNext());
/*    */   }
/*    */   
/*    */ 
/*    */   public Page getNextPage()
/*    */   {
/* 82 */     if (isFinished()) {
/* 83 */       return null;
/*    */     }
/* 85 */     Page page = (Page)this.pages.next();
/* 86 */     this.completedBytes += page.getSizeInBytes();
/* 87 */     return page;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getSystemMemoryUsage()
/*    */   {
/* 93 */     return this.memoryUsageBytes;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\FixedPageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */