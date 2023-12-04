/*    */ package com.facebook.presto.jdbc.internal.guava.publicsuffix;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ @GwtCompatible
/*    */  enum PublicSuffixType
/*    */ {
/* 28 */   PRIVATE(':', ','), 
/*    */   
/* 30 */   ICANN('!', '?');
/*    */   
/*    */ 
/*    */   private final char innerNodeCode;
/*    */   
/*    */   private final char leafNodeCode;
/*    */   
/*    */   private PublicSuffixType(char innerNodeCode, char leafNodeCode)
/*    */   {
/* 39 */     this.innerNodeCode = innerNodeCode;
/* 40 */     this.leafNodeCode = leafNodeCode;
/*    */   }
/*    */   
/*    */   char getLeafNodeCode() {
/* 44 */     return this.leafNodeCode;
/*    */   }
/*    */   
/*    */   char getInnerNodeCode() {
/* 48 */     return this.innerNodeCode;
/*    */   }
/*    */   
/*    */   static PublicSuffixType fromCode(char code)
/*    */   {
/* 53 */     for (PublicSuffixType value : ) {
/* 54 */       if ((value.getInnerNodeCode() == code) || (value.getLeafNodeCode() == code)) {
/* 55 */         return value;
/*    */       }
/*    */     }
/* 58 */     ??? = code;throw new IllegalArgumentException(38 + "No enum corresponding to given code: " + ???);
/*    */   }
/*    */   
/*    */   static PublicSuffixType fromIsPrivate(boolean isPrivate) {
/* 62 */     return isPrivate ? PRIVATE : ICANN;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\publicsuffix\PublicSuffixType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */