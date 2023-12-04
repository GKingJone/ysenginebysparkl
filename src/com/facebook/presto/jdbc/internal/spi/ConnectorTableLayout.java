/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
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
/*     */ public class ConnectorTableLayout
/*     */ {
/*     */   private final ConnectorTableLayoutHandle handle;
/*     */   private final Optional<List<ColumnHandle>> columns;
/*     */   private final TupleDomain<ColumnHandle> predicate;
/*     */   private final Optional<ConnectorNodePartitioning> nodePartitioning;
/*     */   private final Optional<Set<ColumnHandle>> streamPartitioningColumns;
/*     */   private final Optional<DiscretePredicates> discretePredicates;
/*     */   private final List<LocalProperty<ColumnHandle>> localProperties;
/*     */   
/*     */   public ConnectorTableLayout(ConnectorTableLayoutHandle handle)
/*     */   {
/*  38 */     this(handle, 
/*  39 */       Optional.empty(), 
/*  40 */       TupleDomain.all(), 
/*  41 */       Optional.empty(), 
/*  42 */       Optional.empty(), 
/*  43 */       Optional.empty(), 
/*  44 */       Collections.emptyList());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorTableLayout(ConnectorTableLayoutHandle handle, Optional<List<ColumnHandle>> columns, TupleDomain<ColumnHandle> predicate, Optional<ConnectorNodePartitioning> nodePartitioning, Optional<Set<ColumnHandle>> streamPartitioningColumns, Optional<DiscretePredicates> discretePredicates, List<LocalProperty<ColumnHandle>> localProperties)
/*     */   {
/*  56 */     Objects.requireNonNull(handle, "handle is null");
/*  57 */     Objects.requireNonNull(columns, "columns is null");
/*  58 */     Objects.requireNonNull(streamPartitioningColumns, "partitioningColumns is null");
/*  59 */     Objects.requireNonNull(nodePartitioning, "nodePartitioning is null");
/*  60 */     Objects.requireNonNull(predicate, "predicate is null");
/*  61 */     Objects.requireNonNull(discretePredicates, "discretePredicates is null");
/*  62 */     Objects.requireNonNull(localProperties, "localProperties is null");
/*     */     
/*  64 */     this.handle = handle;
/*  65 */     this.columns = columns;
/*  66 */     this.nodePartitioning = nodePartitioning;
/*  67 */     this.streamPartitioningColumns = streamPartitioningColumns;
/*  68 */     this.predicate = predicate;
/*  69 */     this.discretePredicates = discretePredicates;
/*  70 */     this.localProperties = localProperties;
/*     */   }
/*     */   
/*     */   public ConnectorTableLayoutHandle getHandle()
/*     */   {
/*  75 */     return this.handle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Optional<List<ColumnHandle>> getColumns()
/*     */   {
/*  83 */     return this.columns;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TupleDomain<ColumnHandle> getPredicate()
/*     */   {
/*  92 */     return this.predicate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Optional<ConnectorNodePartitioning> getNodePartitioning()
/*     */   {
/* 103 */     return this.nodePartitioning;
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
/*     */   public Optional<Set<ColumnHandle>> getStreamPartitioningColumns()
/*     */   {
/* 117 */     return this.streamPartitioningColumns;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Optional<DiscretePredicates> getDiscretePredicates()
/*     */   {
/* 127 */     return this.discretePredicates;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<LocalProperty<ColumnHandle>> getLocalProperties()
/*     */   {
/* 135 */     return this.localProperties;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 141 */     return Objects.hash(new Object[] { this.handle, this.columns, this.predicate, this.discretePredicates, this.streamPartitioningColumns, this.nodePartitioning, this.localProperties });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 147 */     if (this == obj) {
/* 148 */       return true;
/*     */     }
/* 150 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 151 */       return false;
/*     */     }
/* 153 */     ConnectorTableLayout other = (ConnectorTableLayout)obj;
/* 154 */     return (Objects.equals(this.handle, other.handle)) && 
/* 155 */       (Objects.equals(this.columns, other.columns)) && 
/* 156 */       (Objects.equals(this.predicate, other.predicate)) && 
/* 157 */       (Objects.equals(this.discretePredicates, other.discretePredicates)) && 
/* 158 */       (Objects.equals(this.streamPartitioningColumns, other.streamPartitioningColumns)) && 
/* 159 */       (Objects.equals(this.nodePartitioning, other.nodePartitioning)) && 
/* 160 */       (Objects.equals(this.localProperties, other.localProperties));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorTableLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */