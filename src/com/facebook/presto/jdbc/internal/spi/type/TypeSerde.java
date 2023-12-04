/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceInput;
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.SliceOutput;
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ public final class TypeSerde
/*    */ {
/*    */   public static void writeType(SliceOutput sliceOutput, Type type)
/*    */   {
/* 30 */     if (sliceOutput == null) {
/* 31 */       throw new NullPointerException("sliceOutput is null");
/*    */     }
/* 33 */     if (type == null) {
/* 34 */       throw new NullPointerException("type is null");
/*    */     }
/*    */     
/* 37 */     writeLengthPrefixedString(sliceOutput, type.getTypeSignature().toString());
/*    */   }
/*    */   
/*    */   public static Type readType(TypeManager typeManager, SliceInput sliceInput)
/*    */   {
/* 42 */     if (sliceInput == null) {
/* 43 */       throw new NullPointerException("sliceInput is null");
/*    */     }
/*    */     
/* 46 */     String name = readLengthPrefixedString(sliceInput);
/* 47 */     Type type = typeManager.getType(TypeSignature.parseTypeSignature(name));
/* 48 */     if (type == null) {
/* 49 */       throw new IllegalArgumentException("Unknown type " + name);
/*    */     }
/* 51 */     return type;
/*    */   }
/*    */   
/*    */   private static String readLengthPrefixedString(SliceInput input)
/*    */   {
/* 56 */     int length = input.readInt();
/* 57 */     byte[] bytes = new byte[length];
/* 58 */     input.readBytes(bytes);
/* 59 */     return new String(bytes, StandardCharsets.UTF_8);
/*    */   }
/*    */   
/*    */   private static void writeLengthPrefixedString(SliceOutput output, String string)
/*    */   {
/* 64 */     byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
/* 65 */     output.writeInt(bytes.length);
/* 66 */     output.writeBytes(bytes);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\TypeSerde.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */