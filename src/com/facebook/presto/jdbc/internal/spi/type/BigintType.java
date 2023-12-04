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
/*    */ public final class BigintType
/*    */   extends AbstractLongType
/*    */ {
/* 24 */   public static final BigintType BIGINT = new BigintType();
/*    */   
/*    */   private BigintType()
/*    */   {
/* 28 */     super(TypeSignature.parseTypeSignature("bigint"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 34 */     if (block.isNull(position)) {
/* 35 */       return null;
/*    */     }
/*    */     
/* 38 */     return Long.valueOf(block.getLong(position, 0));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 45 */     return other == BIGINT;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 51 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\BigintType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */