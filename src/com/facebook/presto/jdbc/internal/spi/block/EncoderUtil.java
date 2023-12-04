/*    */ package com.facebook.presto.jdbc.internal.spi.block;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
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
/*    */ final class EncoderUtil
/*    */ {
/*    */   public static void encodeNullsAsBits(SliceOutput sliceOutput, Block block)
/*    */   {
/* 31 */     int positionCount = block.getPositionCount();
/* 32 */     for (int position = 0; position < (positionCount & 0xFFFFFFF8); position += 8) {
/* 33 */       byte value = 0;
/* 34 */       value = (byte)(value | (block.isNull(position) ? 128 : 0));
/* 35 */       value = (byte)(value | (block.isNull(position + 1) ? 64 : 0));
/* 36 */       value = (byte)(value | (block.isNull(position + 2) ? 32 : 0));
/* 37 */       value = (byte)(value | (block.isNull(position + 3) ? 16 : 0));
/* 38 */       value = (byte)(value | (block.isNull(position + 4) ? 8 : 0));
/* 39 */       value = (byte)(value | (block.isNull(position + 5) ? 4 : 0));
/* 40 */       value = (byte)(value | (block.isNull(position + 6) ? 2 : 0));
/* 41 */       value = (byte)(value | (block.isNull(position + 7) ? 1 : 0));
/* 42 */       sliceOutput.appendByte(value);
/*    */     }
/*    */     
/*    */ 
/* 46 */     if ((positionCount & 0x7) > 0) {
/* 47 */       byte value = 0;
/* 48 */       int mask = 128;
/* 49 */       for (int position = positionCount & 0xFFFFFFF8; position < positionCount; position++) {
/* 50 */         value = (byte)(value | (block.isNull(position) ? mask : 0));
/* 51 */         mask >>>= 1;
/*    */       }
/* 53 */       sliceOutput.appendByte(value);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean[] decodeNullBits(SliceInput sliceInput, int positionCount)
/*    */   {
/* 63 */     boolean[] valueIsNull = new boolean[positionCount];
/* 64 */     for (int position = 0; position < (positionCount & 0xFFFFFFF8); position += 8) {
/* 65 */       byte value = sliceInput.readByte();
/* 66 */       valueIsNull[position] = ((value & 0x80) != 0 ? 1 : false);
/* 67 */       valueIsNull[(position + 1)] = ((value & 0x40) != 0 ? 1 : false);
/* 68 */       valueIsNull[(position + 2)] = ((value & 0x20) != 0 ? 1 : false);
/* 69 */       valueIsNull[(position + 3)] = ((value & 0x10) != 0 ? 1 : false);
/* 70 */       valueIsNull[(position + 4)] = ((value & 0x8) != 0 ? 1 : false);
/* 71 */       valueIsNull[(position + 5)] = ((value & 0x4) != 0 ? 1 : false);
/* 72 */       valueIsNull[(position + 6)] = ((value & 0x2) != 0 ? 1 : false);
/* 73 */       valueIsNull[(position + 7)] = ((value & 0x1) != 0 ? 1 : false);
/*    */     }
/*    */     
/*    */ 
/* 77 */     if ((positionCount & 0x7) > 0) {
/* 78 */       byte value = sliceInput.readByte();
/* 79 */       int mask = 128;
/* 80 */       for (int position = positionCount & 0xFFFFFFF8; position < positionCount; position++) {
/* 81 */         valueIsNull[position] = ((value & mask) != 0 ? 1 : false);
/* 82 */         mask >>>= 1;
/*    */       }
/*    */     }
/*    */     
/* 86 */     return valueIsNull;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\block\EncoderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */