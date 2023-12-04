/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorPartitioningHandle;
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
/*    */ public class ConnectorNewTableLayout
/*    */ {
/*    */   private final ConnectorPartitioningHandle partitioning;
/*    */   private final List<String> partitionColumns;
/*    */   
/*    */   public ConnectorNewTableLayout(ConnectorPartitioningHandle partitioning, List<String> partitionColumns)
/*    */   {
/* 29 */     this.partitioning = ((ConnectorPartitioningHandle)Objects.requireNonNull(partitioning, "partitioning is null"));
/* 30 */     this.partitionColumns = ((List)Objects.requireNonNull(partitionColumns, "partitionColumns is null"));
/*    */   }
/*    */   
/*    */   public ConnectorPartitioningHandle getPartitioning()
/*    */   {
/* 35 */     return this.partitioning;
/*    */   }
/*    */   
/*    */   public List<String> getPartitionColumns()
/*    */   {
/* 40 */     return this.partitionColumns;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorNewTableLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */