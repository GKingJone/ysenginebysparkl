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
/*    */ 
/*    */ 
/*    */ public final class RealType
/*    */   extends AbstractIntType
/*    */ {
/* 29 */   public static final RealType REAL = new RealType();
/*    */   
/*    */   private RealType()
/*    */   {
/* 33 */     super(TypeSignature.parseTypeSignature("real"));
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getObjectValue(ConnectorSession session, Block block, int position)
/*    */   {
/* 39 */     if (block.isNull(position)) {
/* 40 */       return null;
/*    */     }
/* 42 */     return Float.valueOf(Float.intBitsToFloat(block.getInt(position, 0)));
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equalTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 48 */     float leftValue = Float.intBitsToFloat(leftBlock.getInt(leftPosition, 0));
/* 49 */     float rightValue = Float.intBitsToFloat(rightBlock.getInt(rightPosition, 0));
/*    */     
/*    */ 
/*    */ 
/* 53 */     return leftValue == rightValue;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int compareTo(Block leftBlock, int leftPosition, Block rightBlock, int rightPosition)
/*    */   {
/* 61 */     float leftValue = Float.intBitsToFloat(leftBlock.getInt(leftPosition, 0));
/* 62 */     float rightValue = Float.intBitsToFloat(rightBlock.getInt(rightPosition, 0));
/* 63 */     return Float.compare(leftValue, rightValue);
/*    */   }
/*    */   
/*    */   public void writeLong(BlockBuilder blockBuilder, long value)
/*    */   {
/*    */     try
/*    */     {
/* 70 */       Math.toIntExact(value);
/*    */     }
/*    */     catch (ArithmeticException e) {
/* 73 */       throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, String.format("Value (%sb) is not a valid single-precision float", new Object[] { Long.toBinaryString(value).replace(' ', '0') }));
/*    */     }
/* 75 */     blockBuilder.writeInt((int)value).closeEntry();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 81 */     return other == REAL;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 87 */     return getClass().hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\RealType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */