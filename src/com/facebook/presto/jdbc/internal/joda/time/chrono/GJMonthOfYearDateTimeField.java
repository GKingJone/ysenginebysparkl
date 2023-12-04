/*    */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*    */ 
/*    */ import java.util.Locale;
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
/*    */ final class GJMonthOfYearDateTimeField
/*    */   extends BasicMonthOfYearDateTimeField
/*    */ {
/*    */   private static final long serialVersionUID = -4748157875845286249L;
/*    */   
/*    */   GJMonthOfYearDateTimeField(BasicChronology paramBasicChronology)
/*    */   {
/* 38 */     super(paramBasicChronology, 2);
/*    */   }
/*    */   
/*    */   public String getAsText(int paramInt, Locale paramLocale)
/*    */   {
/* 43 */     return GJLocaleSymbols.forLocale(paramLocale).monthOfYearValueToText(paramInt);
/*    */   }
/*    */   
/*    */   public String getAsShortText(int paramInt, Locale paramLocale)
/*    */   {
/* 48 */     return GJLocaleSymbols.forLocale(paramLocale).monthOfYearValueToShortText(paramInt);
/*    */   }
/*    */   
/*    */   protected int convertText(String paramString, Locale paramLocale)
/*    */   {
/* 53 */     return GJLocaleSymbols.forLocale(paramLocale).monthOfYearTextToValue(paramString);
/*    */   }
/*    */   
/*    */   public int getMaximumTextLength(Locale paramLocale)
/*    */   {
/* 58 */     return GJLocaleSymbols.forLocale(paramLocale).getMonthMaxTextLength();
/*    */   }
/*    */   
/*    */   public int getMaximumShortTextLength(Locale paramLocale)
/*    */   {
/* 63 */     return GJLocaleSymbols.forLocale(paramLocale).getMonthMaxShortTextLength();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJMonthOfYearDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */