/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
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
/*    */ public abstract interface BlockBuilder
/*    */   extends Block
/*    */ {
/*    */   public BlockBuilder writeByte(int value)
/*    */   {
/* 26 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder writeShort(int value)
/*    */   {
/* 34 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder writeInt(int value)
/*    */   {
/* 42 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder writeLong(long value)
/*    */   {
/* 50 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder writeBytes(Slice source, int sourceIndex, int length)
/*    */   {
/* 58 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder writeObject(Object value)
/*    */   {
/* 66 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BlockBuilder beginBlockEntry()
/*    */   {
/* 75 */     throw new UnsupportedOperationException(getClass().getName());
/*    */   }
/*    */   
/*    */   public abstract BlockBuilder closeEntry();
/*    */   
/*    */   public abstract BlockBuilder appendNull();
/*    */   
/*    */   public abstract Block build();
/*    */   
/*    */   public abstract void reset(BlockBuilderStatus paramBlockBuilderStatus);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\BlockBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */