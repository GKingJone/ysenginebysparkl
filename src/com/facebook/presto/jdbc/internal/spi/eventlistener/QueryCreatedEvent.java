/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import java.time.Instant;
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
/*    */ public class QueryCreatedEvent
/*    */ {
/*    */   private final Instant createTime;
/*    */   private final QueryContext context;
/*    */   private final QueryMetadata metadata;
/*    */   
/*    */   public QueryCreatedEvent(Instant createTime, QueryContext context, QueryMetadata metadata)
/*    */   {
/* 30 */     this.createTime = ((Instant)Objects.requireNonNull(createTime, "createTime is null"));
/* 31 */     this.context = ((QueryContext)Objects.requireNonNull(context, "context is null"));
/* 32 */     this.metadata = ((QueryMetadata)Objects.requireNonNull(metadata, "metadata is null"));
/*    */   }
/*    */   
/*    */   public Instant getCreateTime()
/*    */   {
/* 37 */     return this.createTime;
/*    */   }
/*    */   
/*    */   public QueryContext getContext()
/*    */   {
/* 42 */     return this.context;
/*    */   }
/*    */   
/*    */   public QueryMetadata getMetadata()
/*    */   {
/* 47 */     return this.metadata;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryCreatedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */