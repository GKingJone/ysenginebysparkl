/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DateParser
/*     */ {
/*  32 */   private static final TimeZone __GMT = TimeZone.getTimeZone("GMT");
/*     */   
/*     */   static {
/*  35 */     __GMT.setID("GMT");
/*     */   }
/*     */   
/*  38 */   static final String[] __dateReceiveFmt = { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy", "EEE, dd MMM yyyy HH:mm:ss", "EEE dd MMM yyyy HH:mm:ss zzz", "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz", "EEE MMM dd yyyy HH:mm:ss", "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss", "dd MMM yyyy HH:mm:ss zzz", "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz", "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz", "MMM dd HH:mm:ss yyyy", "EEE MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long parseDate(String date)
/*     */   {
/*  55 */     return ((DateParser)__dateParser.get()).parse(date);
/*     */   }
/*     */   
/*  58 */   private static final ThreadLocal<DateParser> __dateParser = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     protected DateParser initialValue()
/*     */     {
/*  63 */       return new DateParser();
/*     */     }
/*     */   };
/*     */   
/*  67 */   final SimpleDateFormat[] _dateReceive = new SimpleDateFormat[__dateReceiveFmt.length];
/*     */   
/*     */   private long parse(String dateVal)
/*     */   {
/*  71 */     for (int i = 0; i < this._dateReceive.length; i++)
/*     */     {
/*  73 */       if (this._dateReceive[i] == null)
/*     */       {
/*  75 */         this._dateReceive[i] = new SimpleDateFormat(__dateReceiveFmt[i], Locale.US);
/*  76 */         this._dateReceive[i].setTimeZone(__GMT);
/*     */       }
/*     */       
/*     */       try
/*     */       {
/*  81 */         Date date = (Date)this._dateReceive[i].parseObject(dateVal);
/*  82 */         return date.getTime();
/*     */       }
/*     */       catch (Exception localException) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  90 */     if (dateVal.endsWith(" GMT"))
/*     */     {
/*  92 */       String val = dateVal.substring(0, dateVal.length() - 4);
/*     */       
/*  94 */       for (SimpleDateFormat element : this._dateReceive)
/*     */       {
/*     */         try
/*     */         {
/*  98 */           Date date = (Date)element.parseObject(val);
/*  99 */           return date.getTime();
/*     */         }
/*     */         catch (Exception localException1) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 107 */     return -1L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\DateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */