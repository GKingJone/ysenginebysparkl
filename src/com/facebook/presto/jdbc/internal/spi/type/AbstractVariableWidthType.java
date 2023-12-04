/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilderStatus;
/*    */ import com.facebook.presto.jdbc.internal.spi.block.VariableWidthBlockBuilder;
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
/*    */ public abstract class AbstractVariableWidthType
/*    */   extends AbstractType
/*    */   implements VariableWidthType
/*    */ {
/*    */   private static final int EXPECTED_BYTES_PER_ENTRY = 32;
/*    */   
/*    */   protected AbstractVariableWidthType(TypeSignature signature, Class<?> javaType)
/*    */   {
/* 28 */     super(signature, javaType);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries, int expectedBytesPerEntry)
/*    */   {
/* 34 */     return new VariableWidthBlockBuilder(blockBuilderStatus, expectedBytesPerEntry == 0 ? expectedEntries : 
/*    */     
/* 36 */       Math.min(expectedEntries, blockBuilderStatus.getMaxBlockSizeInBytes() / expectedBytesPerEntry), expectedBytesPerEntry);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public BlockBuilder createBlockBuilder(BlockBuilderStatus blockBuilderStatus, int expectedEntries)
/*    */   {
/* 43 */     return createBlockBuilder(blockBuilderStatus, expectedEntries, 32);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\AbstractVariableWidthType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */