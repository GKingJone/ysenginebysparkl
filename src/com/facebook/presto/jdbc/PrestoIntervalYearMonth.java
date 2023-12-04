/*    */ package com.facebook.presto.jdbc;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.client.IntervalYearMonth;
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
/*    */ 
/*    */ public class PrestoIntervalYearMonth
/*    */ {
/*    */   private final int months;
/*    */   
/*    */   public PrestoIntervalYearMonth(int months)
/*    */   {
/* 27 */     this.months = months;
/*    */   }
/*    */   
/*    */   public PrestoIntervalYearMonth(int year, int months)
/*    */   {
/* 32 */     this.months = IntervalYearMonth.toMonths(year, months);
/*    */   }
/*    */   
/*    */   public int getMonths()
/*    */   {
/* 37 */     return this.months;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 43 */     return Objects.hash(new Object[] { Integer.valueOf(this.months) });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 49 */     if (this == obj) {
/* 50 */       return true;
/*    */     }
/* 52 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 53 */       return false;
/*    */     }
/* 55 */     PrestoIntervalYearMonth other = (PrestoIntervalYearMonth)obj;
/* 56 */     return Objects.equals(Integer.valueOf(this.months), Integer.valueOf(other.months));
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return IntervalYearMonth.formatMonths(this.months);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoIntervalYearMonth.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */