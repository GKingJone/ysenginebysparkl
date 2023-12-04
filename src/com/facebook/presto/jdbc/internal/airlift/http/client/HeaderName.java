/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.Objects;
/*    */ 
/*    */ public final class HeaderName
/*    */ {
/*    */   private final String original;
/*    */   private final String lowerCase;
/*    */   
/*    */   public static HeaderName of(String value)
/*    */   {
/* 13 */     return new HeaderName(value);
/*    */   }
/*    */   
/*    */   private HeaderName(String value)
/*    */   {
/* 18 */     Objects.requireNonNull(value, "value is null");
/* 19 */     this.original = value;
/* 20 */     this.lowerCase = value.toLowerCase(Locale.ENGLISH);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 26 */     if (this == obj) {
/* 27 */       return true;
/*    */     }
/* 29 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 30 */       return false;
/*    */     }
/* 32 */     HeaderName other = (HeaderName)obj;
/* 33 */     return this.lowerCase.equals(other.lowerCase);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 39 */     return this.lowerCase.hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return this.original;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HeaderName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */