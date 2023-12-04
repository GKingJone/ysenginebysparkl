/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPartitioningHandle;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ public class ConnectorNodePartitioning
/*    */ {
/*    */   private final ConnectorPartitioningHandle partitioningHandle;
/*    */   private final List<ColumnHandle> partitioningColumns;
/*    */   
/*    */   public ConnectorNodePartitioning(ConnectorPartitioningHandle partitioningHandle, List<ColumnHandle> partitioningColumns)
/*    */   {
/* 32 */     this.partitioningHandle = ((ConnectorPartitioningHandle)Objects.requireNonNull(partitioningHandle, "partitioningHandle is null"));
/* 33 */     this.partitioningColumns = Collections.unmodifiableList(new ArrayList((Collection)Objects.requireNonNull(partitioningColumns, "partitioningColumns is null")));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ConnectorPartitioningHandle getPartitioningHandle()
/*    */   {
/* 41 */     return this.partitioningHandle;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public List<ColumnHandle> getPartitioningColumns()
/*    */   {
/* 53 */     return this.partitioningColumns;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 59 */     if (this == o) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((o == null) || (getClass() != o.getClass())) {
/* 63 */       return false;
/*    */     }
/* 65 */     ConnectorNodePartitioning that = (ConnectorNodePartitioning)o;
/* 66 */     return (Objects.equals(this.partitioningHandle, that.partitioningHandle)) && 
/* 67 */       (Objects.equals(this.partitioningColumns, that.partitioningColumns));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 73 */     return Objects.hash(new Object[] { this.partitioningHandle, this.partitioningColumns });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorNodePartitioning.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */