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
/*    */ public final class TimestampWithTimeZoneType
/*    */   extends AbstractLongType
/*    */ {
/* 25 */   public static final TimestampWithTimeZoneType TIMESTAMP_WITH_TIME_ZONE = new TimestampWithTimeZoneType();
/*    */   
/*    */   private TimestampWithTimeZoneType()
/*    */   {
/* 29 */     super(TypeSignature.parseTypeSignature("timestamp with time zone"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 35 */     if (block.isNull(position)) {
/* 36 */       return null;
/*    */     }
/*    */     
/* 39 */     return new SqlTimestampWithTimeZone(block.getLong(position, 0));
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 45 */     long leftValue = DateTimeEncoding.unpackMillisUtc(leftBlock.getLong(leftPosition, 0));
/* 46 */     long rightValue = DateTimeEncoding.unpackMillisUtc(rightBlock.getLong(rightPosition, 0));
/* 47 */     return leftValue == rightValue;
/*    */   }
/*    */   
/*    */ 
/*    */   public long hash(Block block, int position)
/*    */   {
/* 53 */     return AbstractLongType.hash(DateTimeEncoding.unpackMillisUtc(block.getLong(position, 0)));
/*    */   }
/*    */   
/*    */ 
/*    */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 59 */     long leftValue = DateTimeEncoding.unpackMillisUtc(leftBlock.getLong(leftPosition, 0));
/* 60 */     long rightValue = DateTimeEncoding.unpackMillisUtc(rightBlock.getLong(rightPosition, 0));
/* 61 */     return Long.compare(leftValue, rightValue);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 68 */     return other == TIMESTAMP_WITH_TIME_ZONE;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 74 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimestampWithTimeZoneType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */