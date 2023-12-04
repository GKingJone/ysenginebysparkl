/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
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
/*    */ public class ArrayBlock
/*    */   extends AbstractArrayBlock
/*    */ {
/* 24 */   private static final int INSTANCE_SIZE = ClassLayout.parseClass(ArrayBlock.class).instanceSize();
/*    */   
/*    */   private final Block values;
/*    */   private final Slice offsets;
/*    */   private final int offsetBase;
/*    */   private final Slice valueIsNull;
/*    */   
/*    */   public ArrayBlock(Block values, Slice offsets, int offsetBase, Slice valueIsNull)
/*    */   {
/* 33 */     this.values = ((Block)Objects.requireNonNull(values));
/* 34 */     this.offsets = ((Slice)Objects.requireNonNull(offsets));
/* 35 */     this.offsetBase = offsetBase;
/* 36 */     this.valueIsNull = ((Slice)Objects.requireNonNull(valueIsNull));
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPositionCount()
/*    */   {
/* 42 */     return this.valueIsNull.length();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getSizeInBytes()
/*    */   {
/* 48 */     return getValues().getSizeInBytes() + this.offsets.length() + this.valueIsNull.length();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getRetainedSizeInBytes()
/*    */   {
/* 54 */     return INSTANCE_SIZE + this.values.getRetainedSizeInBytes() + this.offsets.getRetainedSize() + this.valueIsNull.getRetainedSize();
/*    */   }
/*    */   
/*    */ 
/*    */   protected Block getValues()
/*    */   {
/* 60 */     return this.values;
/*    */   }
/*    */   
/*    */ 
/*    */   protected Slice getOffsets()
/*    */   {
/* 66 */     return this.offsets;
/*    */   }
/*    */   
/*    */ 
/*    */   protected int getOffsetBase()
/*    */   {
/* 72 */     return this.offsetBase;
/*    */   }
/*    */   
/*    */ 
/*    */   protected Slice getValueIsNull()
/*    */   {
/* 78 */     return this.valueIsNull;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 84 */     StringBuilder sb = new StringBuilder("ArrayBlock{");
/* 85 */     sb.append("positionCount=").append(getPositionCount());
/* 86 */     sb.append('}');
/* 87 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\ArrayBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */