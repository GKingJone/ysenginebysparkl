/*    */ package com.facebook.presto.jdbc.internal.jackson.core;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.io.CharacterEscapes;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.io.SerializedString;
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
/*    */ public class JsonpCharacterEscapes
/*    */   extends CharacterEscapes
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   private static final int[] asciiEscapes = ;
/* 20 */   private static final SerializedString escapeFor2028 = new SerializedString("\\u2028");
/* 21 */   private static final SerializedString escapeFor2029 = new SerializedString("\\u2029");
/*    */   
/* 23 */   private static final JsonpCharacterEscapes sInstance = new JsonpCharacterEscapes();
/*    */   
/*    */   public static JsonpCharacterEscapes instance() {
/* 26 */     return sInstance;
/*    */   }
/*    */   
/*    */ 
/*    */   public SerializableString getEscapeSequence(int ch)
/*    */   {
/* 32 */     switch (ch) {
/*    */     case 8232: 
/* 34 */       return escapeFor2028;
/*    */     case 8233: 
/* 36 */       return escapeFor2029;
/*    */     }
/* 38 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public int[] getEscapeCodesForAscii()
/*    */   {
/* 44 */     return asciiEscapes;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\JsonpCharacterEscapes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */