/*     */ package com.facebook.presto.jdbc.internal.spi.block;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeManager;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DictionaryBlockEncoding
/*     */   implements BlockEncoding
/*     */ {
/*  26 */   public static final BlockEncodingFactory<DictionaryBlockEncoding> FACTORY = new DictionaryBlockEncodingFactory();
/*     */   private static final String NAME = "DICTIONARY";
/*     */   private final BlockEncoding dictionaryEncoding;
/*     */   
/*     */   public DictionaryBlockEncoding(BlockEncoding dictionaryEncoding)
/*     */   {
/*  32 */     this.dictionaryEncoding = ((BlockEncoding)Objects.requireNonNull(dictionaryEncoding, "dictionaryEncoding is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  38 */     return "DICTIONARY";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void writeBlock(SliceOutput sliceOutput, Block block)
/*     */   {
/*  45 */     DictionaryBlock dictionaryBlock = (DictionaryBlock)block;
/*     */     
/*  47 */     dictionaryBlock = dictionaryBlock.compact();
/*     */     
/*     */ 
/*  50 */     int positionCount = dictionaryBlock.getPositionCount();
/*  51 */     sliceOutput.appendInt(positionCount);
/*     */     
/*     */ 
/*  54 */     Block dictionary = dictionaryBlock.getDictionary();
/*  55 */     this.dictionaryEncoding.writeBlock(sliceOutput, dictionary);
/*     */     
/*     */ 
/*  58 */     Slice ids = dictionaryBlock.getIds();
/*  59 */     sliceOutput
/*  60 */       .appendInt(ids.length())
/*  61 */       .writeBytes(ids);
/*     */     
/*     */ 
/*  64 */     sliceOutput.appendLong(dictionaryBlock.getDictionarySourceId().getMostSignificantBits());
/*  65 */     sliceOutput.appendLong(dictionaryBlock.getDictionarySourceId().getLeastSignificantBits());
/*  66 */     sliceOutput.appendLong(dictionaryBlock.getDictionarySourceId().getSequenceId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Block readBlock(SliceInput sliceInput)
/*     */   {
/*  73 */     int positionCount = sliceInput.readInt();
/*     */     
/*     */ 
/*  76 */     Block dictionaryBlock = this.dictionaryEncoding.readBlock(sliceInput);
/*     */     
/*     */ 
/*  79 */     int lengthIdsSlice = sliceInput.readInt();
/*  80 */     Slice ids = sliceInput.readSlice(lengthIdsSlice);
/*     */     
/*     */ 
/*  83 */     long mostSignificantBits = sliceInput.readLong();
/*  84 */     long leastSignificantBits = sliceInput.readLong();
/*  85 */     long sequenceId = sliceInput.readLong();
/*     */     
/*     */ 
/*  88 */     return new DictionaryBlock(positionCount, dictionaryBlock, ids, true, new DictionaryId(mostSignificantBits, leastSignificantBits, sequenceId));
/*     */   }
/*     */   
/*     */ 
/*     */   public BlockEncodingFactory getFactory()
/*     */   {
/*  94 */     return FACTORY;
/*     */   }
/*     */   
/*     */   public BlockEncoding getDictionaryEncoding()
/*     */   {
/*  99 */     return this.dictionaryEncoding;
/*     */   }
/*     */   
/*     */ 
/*     */   public static class DictionaryBlockEncodingFactory
/*     */     implements BlockEncodingFactory<DictionaryBlockEncoding>
/*     */   {
/*     */     public String getName()
/*     */     {
/* 108 */       return "DICTIONARY";
/*     */     }
/*     */     
/*     */ 
/*     */     public DictionaryBlockEncoding readEncoding(TypeManager manager, BlockEncodingSerde serde, SliceInput input)
/*     */     {
/* 114 */       BlockEncoding dictionaryEncoding = serde.readBlockEncoding(input);
/* 115 */       return new DictionaryBlockEncoding(dictionaryEncoding);
/*     */     }
/*     */     
/*     */ 
/*     */     public void writeEncoding(BlockEncodingSerde serde, SliceOutput output, DictionaryBlockEncoding blockEncoding)
/*     */     {
/* 121 */       serde.writeBlockEncoding(output, blockEncoding.getDictionaryEncoding());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\DictionaryBlockEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */