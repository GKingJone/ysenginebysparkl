/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
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
/*    */ public final class DiscretePredicates
/*    */ {
/*    */   private final List<ColumnHandle> columns;
/*    */   private final List<TupleDomain<ColumnHandle>> predicates;
/*    */   
/*    */   public DiscretePredicates(List<ColumnHandle> columns, List<TupleDomain<ColumnHandle>> predicates)
/*    */   {
/* 31 */     Objects.requireNonNull(columns, "columns is null");
/* 32 */     if (columns.isEmpty()) {
/* 33 */       throw new IllegalArgumentException("columns is empty");
/*    */     }
/* 35 */     this.columns = Collections.unmodifiableList(new ArrayList(columns));
/* 36 */     this.predicates = Collections.unmodifiableList(new ArrayList((Collection)Objects.requireNonNull(predicates, "predicates is null")));
/*    */   }
/*    */   
/*    */   public List<ColumnHandle> getColumns()
/*    */   {
/* 41 */     return this.columns;
/*    */   }
/*    */   
/*    */   public List<TupleDomain<ColumnHandle>> getPredicates()
/*    */   {
/* 46 */     return this.predicates;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\DiscretePredicates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */