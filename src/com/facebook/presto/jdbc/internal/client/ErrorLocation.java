/*    */ package com.facebook.presto.jdbc.internal.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import javax.annotation.concurrent.Immutable;
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
/*    */ @Immutable
/*    */ public class ErrorLocation
/*    */ {
/*    */   private final int lineNumber;
/*    */   private final int columnNumber;
/*    */   
/*    */   @JsonCreator
/*    */   public ErrorLocation(@JsonProperty("lineNumber") int lineNumber, @JsonProperty("columnNumber") int columnNumber)
/*    */   {
/* 35 */     Preconditions.checkArgument(lineNumber >= 1, "lineNumber must be at least one");
/* 36 */     Preconditions.checkArgument(columnNumber >= 1, "columnNumber must be at least one");
/*    */     
/* 38 */     this.lineNumber = lineNumber;
/* 39 */     this.columnNumber = columnNumber;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public int getLineNumber()
/*    */   {
/* 45 */     return this.lineNumber;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public int getColumnNumber()
/*    */   {
/* 51 */     return this.columnNumber;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 57 */     return 
/*    */     
/*    */ 
/* 60 */       MoreObjects.toStringHelper(this).add("lineNumber", this.lineNumber).add("columnNumber", this.columnNumber).toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\ErrorLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */