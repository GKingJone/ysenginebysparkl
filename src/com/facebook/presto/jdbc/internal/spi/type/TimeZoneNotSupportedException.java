/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*    */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
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
/*    */ public class TimeZoneNotSupportedException
/*    */   extends PrestoException
/*    */ {
/*    */   private final String zoneId;
/*    */   
/*    */   public TimeZoneNotSupportedException(String zoneId)
/*    */   {
/* 27 */     super(StandardErrorCode.NOT_SUPPORTED, "Time zone not supported: " + zoneId);
/* 28 */     this.zoneId = zoneId;
/*    */   }
/*    */   
/*    */   public String getZoneId()
/*    */   {
/* 33 */     return this.zoneId;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimeZoneNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */