/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import java.util.GregorianCalendar;
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
/*     */ 
/*     */ public class DateGenerator
/*     */ {
/*  32 */   private static final TimeZone __GMT = TimeZone.getTimeZone("GMT");
/*     */   
/*     */   static {
/*  35 */     __GMT.setID("GMT");
/*     */   }
/*     */   
/*  38 */   static final String[] DAYS = { "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
/*     */   
/*  40 */   static final String[] MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan" };
/*     */   
/*     */ 
/*     */ 
/*  44 */   private static final ThreadLocal<DateGenerator> __dateGenerator = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     protected DateGenerator initialValue()
/*     */     {
/*  49 */       return new DateGenerator();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*  54 */   public static final String __01Jan1970 = formatDate(0L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String formatDate(long date)
/*     */   {
/*  63 */     return ((DateGenerator)__dateGenerator.get()).doFormatDate(date);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void formatCookieDate(StringBuilder buf, long date)
/*     */   {
/*  73 */     ((DateGenerator)__dateGenerator.get()).doFormatCookieDate(buf, date);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String formatCookieDate(long date)
/*     */   {
/*  83 */     StringBuilder buf = new StringBuilder(28);
/*  84 */     formatCookieDate(buf, date);
/*  85 */     return buf.toString();
/*     */   }
/*     */   
/*  88 */   private final StringBuilder buf = new StringBuilder(32);
/*  89 */   private final GregorianCalendar gc = new GregorianCalendar(__GMT);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String doFormatDate(long date)
/*     */   {
/*  98 */     this.buf.setLength(0);
/*  99 */     this.gc.setTimeInMillis(date);
/*     */     
/* 101 */     int day_of_week = this.gc.get(7);
/* 102 */     int day_of_month = this.gc.get(5);
/* 103 */     int month = this.gc.get(2);
/* 104 */     int year = this.gc.get(1);
/* 105 */     int century = year / 100;
/* 106 */     year %= 100;
/*     */     
/* 108 */     int hours = this.gc.get(11);
/* 109 */     int minutes = this.gc.get(12);
/* 110 */     int seconds = this.gc.get(13);
/*     */     
/* 112 */     this.buf.append(DAYS[day_of_week]);
/* 113 */     this.buf.append(',');
/* 114 */     this.buf.append(' ');
/* 115 */     StringUtil.append2digits(this.buf, day_of_month);
/*     */     
/* 117 */     this.buf.append(' ');
/* 118 */     this.buf.append(MONTHS[month]);
/* 119 */     this.buf.append(' ');
/* 120 */     StringUtil.append2digits(this.buf, century);
/* 121 */     StringUtil.append2digits(this.buf, year);
/*     */     
/* 123 */     this.buf.append(' ');
/* 124 */     StringUtil.append2digits(this.buf, hours);
/* 125 */     this.buf.append(':');
/* 126 */     StringUtil.append2digits(this.buf, minutes);
/* 127 */     this.buf.append(':');
/* 128 */     StringUtil.append2digits(this.buf, seconds);
/* 129 */     this.buf.append(" GMT");
/* 130 */     return this.buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void doFormatCookieDate(StringBuilder buf, long date)
/*     */   {
/* 140 */     this.gc.setTimeInMillis(date);
/*     */     
/* 142 */     int day_of_week = this.gc.get(7);
/* 143 */     int day_of_month = this.gc.get(5);
/* 144 */     int month = this.gc.get(2);
/* 145 */     int year = this.gc.get(1);
/* 146 */     year %= 10000;
/*     */     
/* 148 */     int epoch = (int)(date / 1000L % 86400L);
/* 149 */     int seconds = epoch % 60;
/* 150 */     epoch /= 60;
/* 151 */     int minutes = epoch % 60;
/* 152 */     int hours = epoch / 60;
/*     */     
/* 154 */     buf.append(DAYS[day_of_week]);
/* 155 */     buf.append(',');
/* 156 */     buf.append(' ');
/* 157 */     StringUtil.append2digits(buf, day_of_month);
/*     */     
/* 159 */     buf.append('-');
/* 160 */     buf.append(MONTHS[month]);
/* 161 */     buf.append('-');
/* 162 */     StringUtil.append2digits(buf, year / 100);
/* 163 */     StringUtil.append2digits(buf, year % 100);
/*     */     
/* 165 */     buf.append(' ');
/* 166 */     StringUtil.append2digits(buf, hours);
/* 167 */     buf.append(':');
/* 168 */     StringUtil.append2digits(buf, minutes);
/* 169 */     buf.append(':');
/* 170 */     StringUtil.append2digits(buf, seconds);
/* 171 */     buf.append(" GMT");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\DateGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */