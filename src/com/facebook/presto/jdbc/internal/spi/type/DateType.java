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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DateType
/*    */   extends AbstractIntType
/*    */ {
/* 32 */   public static final DateType DATE = new DateType();
/*    */   
/*    */   private DateType()
/*    */   {
/* 36 */     super(TypeSignature.parseTypeSignature("date"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 42 */     if (block.isNull(position)) {
/* 43 */       return null;
/*    */     }
/*    */     
/* 46 */     int days = block.getInt(position, 0);
/* 47 */     return new SqlDate(days);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 54 */     return other == DATE;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 60 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\DateType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */