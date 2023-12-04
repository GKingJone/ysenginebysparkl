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
/*    */ public final class TimeWithTimeZoneType
/*    */   extends AbstractLongType
/*    */ {
/* 25 */   public static final TimeWithTimeZoneType TIME_WITH_TIME_ZONE = new TimeWithTimeZoneType();
/*    */   
/*    */   private TimeWithTimeZoneType()
/*    */   {
/* 29 */     super(TypeSignature.parseTypeSignature("time with time zone"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 35 */     if (block.isNull(position)) {
/* 36 */       return null;
/*    */     }
/*    */     
/* 39 */     return new SqlTimeWithTimeZone(block.getLong(position, 0));
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
/*    */   public long hash(Block block, int position)
/*    */   {
/* 52 */     return AbstractLongType.hash(DateTimeEncoding.unpackMillisUtc(block.getLong(position, 0)));
/*    */   }
/*    */   
/*    */ 
/*    */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 58 */     long leftValue = DateTimeEncoding.unpackMillisUtc(leftBlock.getLong(leftPosition, 0));
/* 59 */     long rightValue = DateTimeEncoding.unpackMillisUtc(rightBlock.getLong(rightPosition, 0));
/* 60 */     return Long.compare(leftValue, rightValue);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 67 */     return other == TIME_WITH_TIME_ZONE;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 73 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TimeWithTimeZoneType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */