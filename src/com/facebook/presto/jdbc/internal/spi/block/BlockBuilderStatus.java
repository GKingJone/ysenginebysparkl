/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Modifier;
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
/*    */ 
/*    */ public class BlockBuilderStatus
/*    */ {
/* 26 */   public static final int INSTANCE_SIZE = deepInstanceSize(BlockBuilderStatus.class);
/*    */   
/*    */   public static final int DEFAULT_MAX_BLOCK_SIZE_IN_BYTES = 65536;
/*    */   
/*    */   private final PageBuilderStatus pageBuilderStatus;
/*    */   
/*    */   private final int maxBlockSizeInBytes;
/*    */   
/*    */   private int currentSize;
/*    */   
/*    */   public BlockBuilderStatus()
/*    */   {
/* 38 */     this(new PageBuilderStatus(1048576, 65536), 65536);
/*    */   }
/*    */   
/*    */   BlockBuilderStatus(PageBuilderStatus pageBuilderStatus, int maxBlockSizeInBytes)
/*    */   {
/* 43 */     this.pageBuilderStatus = ((PageBuilderStatus)Objects.requireNonNull(pageBuilderStatus, "pageBuilderStatus must not be null"));
/* 44 */     this.maxBlockSizeInBytes = maxBlockSizeInBytes;
/*    */   }
/*    */   
/*    */   public int getMaxBlockSizeInBytes()
/*    */   {
/* 49 */     return this.maxBlockSizeInBytes;
/*    */   }
/*    */   
/*    */   public void addBytes(int bytes)
/*    */   {
/* 54 */     this.currentSize += bytes;
/* 55 */     this.pageBuilderStatus.addBytes(bytes);
/* 56 */     if (this.currentSize >= this.maxBlockSizeInBytes) {
/* 57 */       this.pageBuilderStatus.setFull();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 64 */     StringBuilder buffer = new StringBuilder("BlockBuilderStatus{");
/* 65 */     buffer.append("maxSizeInBytes=").append(this.maxBlockSizeInBytes);
/* 66 */     buffer.append(", currentSize=").append(this.currentSize);
/* 67 */     buffer.append('}');
/* 68 */     return buffer.toString();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private static int deepInstanceSize(Class<?> clazz)
/*    */   {
/* 76 */     if (clazz.isArray()) {
/* 77 */       throw new IllegalArgumentException(String.format("Cannot determine size of %s because it contains an array", new Object[] { clazz.getSimpleName() }));
/*    */     }
/* 79 */     if (clazz.isInterface()) {
/* 80 */       throw new IllegalArgumentException(String.format("%s is an interface", new Object[] { clazz.getSimpleName() }));
/*    */     }
/* 82 */     if (Modifier.isAbstract(clazz.getModifiers())) {
/* 83 */       throw new IllegalArgumentException(String.format("%s is abstract", new Object[] { clazz.getSimpleName() }));
/*    */     }
/* 85 */     if (!clazz.getSuperclass().equals(Object.class)) {
/* 86 */       throw new IllegalArgumentException(String.format("Cannot determine size of a subclass. %s extends from %s", new Object[] { clazz.getSimpleName(), clazz.getSuperclass().getSimpleName() }));
/*    */     }
/*    */     
/* 89 */     int size = ClassLayout.parseClass(clazz).instanceSize();
/* 90 */     for (Field field : clazz.getDeclaredFields()) {
/* 91 */       if (!field.getType().isPrimitive()) {
/* 92 */         size += deepInstanceSize(field.getType());
/*    */       }
/*    */     }
/* 95 */     return size;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\BlockBuilderStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */