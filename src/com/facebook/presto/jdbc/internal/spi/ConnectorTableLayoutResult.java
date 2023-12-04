/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
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
/*    */ public class ConnectorTableLayoutResult
/*    */ {
/*    */   private final ConnectorTableLayout layout;
/*    */   private final TupleDomain<ColumnHandle> unenforcedConstraint;
/*    */   
/*    */   public ConnectorTableLayoutResult(ConnectorTableLayout layout, TupleDomain<ColumnHandle> unenforcedConstraint)
/*    */   {
/* 27 */     this.layout = ((ConnectorTableLayout)Objects.requireNonNull(layout, "layout is null"));
/* 28 */     this.unenforcedConstraint = ((TupleDomain)Objects.requireNonNull(unenforcedConstraint, "unenforcedConstraint is null"));
/*    */   }
/*    */   
/*    */   public ConnectorTableLayout getTableLayout()
/*    */   {
/* 33 */     return this.layout;
/*    */   }
/*    */   
/*    */   public TupleDomain<ColumnHandle> getUnenforcedConstraint()
/*    */   {
/* 38 */     return this.unenforcedConstraint;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorTableLayoutResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */