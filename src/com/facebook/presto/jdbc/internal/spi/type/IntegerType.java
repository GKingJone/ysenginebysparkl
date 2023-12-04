/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*    */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*    */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
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
/*    */ public final class IntegerType
/*    */   extends AbstractIntType
/*    */ {
/* 27 */   public static final IntegerType INTEGER = new IntegerType();
/*    */   
/*    */   private IntegerType()
/*    */   {
/* 31 */     super(TypeSignature.parseTypeSignature("integer"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 37 */     if (block.isNull(position)) {
/* 38 */       return null;
/*    */     }
/*    */     
/* 41 */     return Integer.valueOf(block.getInt(position, 0));
/*    */   }
/*    */   
/*    */ 
/*    */   public final void writeLong(BlockBuilder blockBuilder, long value)
/*    */   {
/* 47 */     if (value > 2147483647L) {
/* 48 */       throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, String.format("Value %d exceeds MAX_INT", new Object[] { Long.valueOf(value) }));
/*    */     }
/* 50 */     if (value < -2147483648L) {
/* 51 */       throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, String.format("Value %d is less than MIN_INT", new Object[] { Long.valueOf(value) }));
/*    */     }
/*    */     
/* 54 */     blockBuilder.writeInt((int)value).closeEntry();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 61 */     return other == INTEGER;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 67 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\IntegerType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */