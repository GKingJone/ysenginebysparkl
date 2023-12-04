/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PageBuilderStatus
/*    */ {
/*    */   public static final int DEFAULT_MAX_PAGE_SIZE_IN_BYTES = 1048576;
/*    */   
/*    */ 
/*    */ 
/*    */   private final int maxPageSizeInBytes;
/*    */   
/*    */ 
/*    */ 
/*    */   private final int maxBlockSizeInBytes;
/*    */   
/*    */ 
/*    */ 
/*    */   private boolean full;
/*    */   
/*    */ 
/*    */ 
/*    */   private int currentSize;
/*    */   
/*    */ 
/*    */ 
/*    */   public PageBuilderStatus()
/*    */   {
/* 30 */     this(1048576, 65536);
/*    */   }
/*    */   
/*    */   public PageBuilderStatus(int maxPageSizeInBytes, int maxBlockSizeInBytes)
/*    */   {
/* 35 */     this.maxPageSizeInBytes = maxPageSizeInBytes;
/* 36 */     this.maxBlockSizeInBytes = maxBlockSizeInBytes;
/*    */   }
/*    */   
/*    */   public BlockBuilderStatus createBlockBuilderStatus()
/*    */   {
/* 41 */     return new BlockBuilderStatus(this, this.maxBlockSizeInBytes);
/*    */   }
/*    */   
/*    */   public int getMaxBlockSizeInBytes()
/*    */   {
/* 46 */     return this.maxBlockSizeInBytes;
/*    */   }
/*    */   
/*    */   public int getMaxPageSizeInBytes()
/*    */   {
/* 51 */     return this.maxPageSizeInBytes;
/*    */   }
/*    */   
/*    */   public boolean isEmpty()
/*    */   {
/* 56 */     return this.currentSize == 0;
/*    */   }
/*    */   
/*    */   public boolean isFull()
/*    */   {
/* 61 */     return (this.full) || (this.currentSize >= this.maxPageSizeInBytes);
/*    */   }
/*    */   
/*    */   void setFull()
/*    */   {
/* 66 */     this.full = true;
/*    */   }
/*    */   
/*    */   void addBytes(int bytes)
/*    */   {
/* 71 */     this.currentSize += bytes;
/*    */   }
/*    */   
/*    */   public int getSizeInBytes()
/*    */   {
/* 76 */     return this.currentSize;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 82 */     StringBuilder buffer = new StringBuilder("BlockBuilderStatus{");
/* 83 */     buffer.append("maxSizeInBytes=").append(this.maxPageSizeInBytes);
/* 84 */     buffer.append(", full=").append(this.full);
/* 85 */     buffer.append(", currentSize=").append(this.currentSize);
/* 86 */     buffer.append('}');
/* 87 */     return buffer.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\PageBuilderStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */