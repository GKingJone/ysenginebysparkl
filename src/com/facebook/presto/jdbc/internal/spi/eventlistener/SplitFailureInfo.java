/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
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
/*    */ public class SplitFailureInfo
/*    */ {
/*    */   private final String failureType;
/*    */   private final String failureMessage;
/*    */   
/*    */   public SplitFailureInfo(String failureType, String failureMessage)
/*    */   {
/* 26 */     this.failureType = ((String)Objects.requireNonNull(failureType, "failureType is null"));
/* 27 */     this.failureMessage = ((String)Objects.requireNonNull(failureMessage, "failureMessage is null"));
/*    */   }
/*    */   
/*    */   public String getFailureType()
/*    */   {
/* 32 */     return this.failureType;
/*    */   }
/*    */   
/*    */   public String getFailureMessage()
/*    */   {
/* 37 */     return this.failureMessage;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\SplitFailureInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */