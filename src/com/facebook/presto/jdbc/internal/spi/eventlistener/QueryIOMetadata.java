/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
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
/*    */ public class QueryIOMetadata
/*    */ {
/*    */   private final List<QueryInputMetadata> inputs;
/*    */   private final Optional<QueryOutputMetadata> output;
/*    */   
/*    */   public QueryIOMetadata(List<QueryInputMetadata> inputs, Optional<QueryOutputMetadata> output)
/*    */   {
/* 29 */     this.inputs = ((List)Objects.requireNonNull(inputs, "inputs is null"));
/* 30 */     this.output = ((Optional)Objects.requireNonNull(output, "output is null"));
/*    */   }
/*    */   
/*    */   public List<QueryInputMetadata> getInputs()
/*    */   {
/* 35 */     return this.inputs;
/*    */   }
/*    */   
/*    */   public Optional<QueryOutputMetadata> getOutput()
/*    */   {
/* 40 */     return this.output;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryIOMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */