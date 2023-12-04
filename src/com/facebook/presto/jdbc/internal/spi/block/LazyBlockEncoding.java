/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
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
/*    */ public class LazyBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/*    */   private final BlockEncoding delegate;
/*    */   
/*    */   public LazyBlockEncoding(BlockEncoding delegate)
/*    */   {
/* 27 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 33 */     return this.delegate.getName();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 40 */     this.delegate.writeBlock(sliceOutput, ((LazyBlock)block).getBlock());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Block readBlock(SliceInput sliceInput)
/*    */   {
/* 47 */     throw new UnsupportedOperationException();
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 53 */     return new LazyBlockEncodingFactory(this.delegate.getFactory());
/*    */   }
/*    */   
/*    */   public static class LazyBlockEncodingFactory
/*    */     implements BlockEncodingFactory<LazyBlockEncoding>
/*    */   {
/*    */     private final BlockEncodingFactory delegate;
/*    */     
/*    */     public LazyBlockEncodingFactory(BlockEncodingFactory delegate)
/*    */     {
/* 63 */       this.delegate = delegate;
/*    */     }
/*    */     
/*    */ 
/*    */     public String getName()
/*    */     {
/* 69 */       return this.delegate.getName();
/*    */     }
/*    */     
/*    */ 
/*    */     public LazyBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 75 */       throw new UnsupportedOperationException();
/*    */     }
/*    */     
/*    */ 
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, LazyBlockEncoding blockEncoding)
/*    */     {
/* 81 */       this.delegate.writeEncoding(serde, output, blockEncoding.delegate);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\LazyBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */