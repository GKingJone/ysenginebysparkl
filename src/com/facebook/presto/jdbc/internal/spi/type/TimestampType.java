/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TimestampType
/*    */   extends AbstractLongType
/*    */ {
/* 28 */   public static final TimestampType TIMESTAMP = new TimestampType();
/*    */   
/*    */   private TimestampType()
/*    */   {
/* 32 */     super(TypeSignature.parseTypeSignature("timestamp"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 38 */     if (block.isNull(position)) {
/* 39 */       return null;
/*    */     }
/*    */     
/* 42 */     return new SqlTimestamp(block.getLong(position, 0), session.getTimeZoneKey());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 49 */     return other == TIMESTAMP;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 55 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimestampType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */