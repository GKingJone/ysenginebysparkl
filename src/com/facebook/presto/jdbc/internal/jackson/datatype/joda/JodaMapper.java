/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ 
/*    */ public class JodaMapper extends com.facebook.presto.jdbc.internal.jackson.databind.ObjectMapper
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JodaMapper()
/*    */   {
/* 11 */     registerModule(new JodaModule());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean getWriteDatesAsTimestamps()
/*    */   {
/* 21 */     return getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setWriteDatesAsTimestamps(boolean state)
/*    */   {
/* 31 */     configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, state);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\JodaMapper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */