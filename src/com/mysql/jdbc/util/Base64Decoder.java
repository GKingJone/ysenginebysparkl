/*    */ package com.mysql.jdbc.util;
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
/*    */ public class Base64Decoder
/*    */ {
/* 37 */   private static byte[] decoderMap = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
/*    */   
/*    */ 
/*    */   public static class IntWrapper
/*    */   {
/*    */     public int value;
/*    */     
/*    */     public IntWrapper(int value)
/*    */     {
/* 46 */       this.value = value;
/*    */     }
/*    */   }
/*    */   
/*    */   private static byte getNextValidByte(byte[] in, IntWrapper pos, int maxPos) {
/* 51 */     while (pos.value <= maxPos) {
/* 52 */       if ((in[pos.value] >= 0) && (decoderMap[in[pos.value]] >= 0)) {
/* 53 */         return in[(pos.value++)];
/*    */       }
/* 55 */       pos.value += 1;
/*    */     }
/*    */     
/* 58 */     return 61;
/*    */   }
/*    */   
/*    */   public static byte[] decode(byte[] in, int pos, int length) {
/* 62 */     IntWrapper offset = new IntWrapper(pos);
/* 63 */     byte[] sestet = new byte[4];
/*    */     
/* 65 */     int outLen = length * 3 / 4;
/* 66 */     byte[] octet = new byte[outLen];
/* 67 */     int octetId = 0;
/*    */     
/* 69 */     int maxPos = offset.value + length - 1;
/* 70 */     while (offset.value <= maxPos) {
/* 71 */       sestet[0] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 72 */       sestet[1] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 73 */       sestet[2] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 74 */       sestet[3] = decoderMap[getNextValidByte(in, offset, maxPos)];
/*    */       
/* 76 */       if (sestet[1] != -2) {
/* 77 */         octet[(octetId++)] = ((byte)(sestet[0] << 2 | sestet[1] >>> 4));
/*    */       }
/* 79 */       if (sestet[2] != -2) {
/* 80 */         octet[(octetId++)] = ((byte)((sestet[1] & 0xF) << 4 | sestet[2] >>> 2));
/*    */       }
/* 82 */       if (sestet[3] != -2) {
/* 83 */         octet[(octetId++)] = ((byte)((sestet[2] & 0x3) << 6 | sestet[3]));
/*    */       }
/*    */     }
/*    */     
/* 87 */     byte[] out = new byte[octetId];
/* 88 */     System.arraycopy(octet, 0, out, 0, octetId);
/* 89 */     return out;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\Base64Decoder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */