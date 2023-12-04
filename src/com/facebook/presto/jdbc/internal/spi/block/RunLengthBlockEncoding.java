/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
/*    */ import java.util.Objects;
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
/*    */ public class RunLengthBlockEncoding
/*    */   implements BlockEncoding
/*    */ {
/* 25 */   public static final BlockEncodingFactory<RunLengthBlockEncoding> FACTORY = new RunLengthBlockEncodingFactory(null);
/*    */   
/*    */   private static final String NAME = "RLE";
/*    */   private final BlockEncoding valueBlockEncoding;
/*    */   
/*    */   public RunLengthBlockEncoding(BlockEncoding valueBlockEncoding)
/*    */   {
/* 32 */     this.valueBlockEncoding = ((BlockEncoding)Objects.requireNonNull(valueBlockEncoding, "valueBlockEncoding is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 38 */     return "RLE";
/*    */   }
/*    */   
/*    */   public BlockEncoding getValueBlockEncoding()
/*    */   {
/* 43 */     return this.valueBlockEncoding;
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*    */   {
/* 49 */     RunLengthEncodedBlock rleBlock = (RunLengthEncodedBlock)block;
/*    */     
/*    */ 
/* 52 */     sliceOutput.writeInt(rleBlock.getPositionCount());
/*    */     
/*    */ 
/* 55 */     getValueBlockEncoding().writeBlock(sliceOutput, rleBlock.getValue());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RunLengthEncodedBlock readBlock(SliceInput sliceInput)
/*    */   {
/* 62 */     int positionCount = sliceInput.readInt();
/*    */     
/*    */ 
/* 65 */     Block value = getValueBlockEncoding().readBlock(sliceInput);
/*    */     
/* 67 */     return new RunLengthEncodedBlock(value, positionCount);
/*    */   }
/*    */   
/*    */ 
/*    */   public BlockEncodingFactory getFactory()
/*    */   {
/* 73 */     return FACTORY;
/*    */   }
/*    */   
/*    */ 
/*    */   private static class RunLengthBlockEncodingFactory
/*    */     implements BlockEncodingFactory<RunLengthBlockEncoding>
/*    */   {
/*    */     public String getName()
/*    */     {
/* 82 */       return "RLE";
/*    */     }
/*    */     
/*    */ 
/*    */     public RunLengthBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*    */     {
/* 88 */       BlockEncoding valueBlockEncoding = serde.readBlockEncoding(input);
/* 89 */       return new RunLengthBlockEncoding(valueBlockEncoding);
/*    */     }
/*    */     
/*    */ 
/*    */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, RunLengthBlockEncoding blockEncoding)
/*    */     {
/* 95 */       serde.writeBlockEncoding(output, blockEncoding.getValueBlockEncoding());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\RunLengthBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */