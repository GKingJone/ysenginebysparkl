/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.time.LocalDate;
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
/*    */ public final class SqlDate
/*    */ {
/*    */   private final int days;
/*    */   
/*    */   public SqlDate(int days)
/*    */   {
/* 27 */     this.days = days;
/*    */   }
/*    */   
/*    */   public int getDays()
/*    */   {
/* 32 */     return this.days;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 38 */     return this.days;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 44 */     if (this == obj) {
/* 45 */       return true;
/*    */     }
/* 47 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 48 */       return false;
/*    */     }
/* 50 */     SqlDate other = (SqlDate)obj;
/* 51 */     return Objects.equals(Integer.valueOf(this.days), Integer.valueOf(other.days));
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 58 */     return LocalDate.ofEpochDay(this.days).toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\SqlDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */