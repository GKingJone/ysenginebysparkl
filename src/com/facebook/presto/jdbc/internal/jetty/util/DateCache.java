/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
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
/*     */ public class DateCache
/*     */ {
/*     */   public static final String DEFAULT_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
/*     */   private final String _formatString;
/*     */   private final String _tzFormatString;
/*     */   private final SimpleDateFormat _tzFormat;
/*     */   private final Locale _locale;
/*     */   private volatile Tick _tick;
/*     */   
/*     */   public static class Tick
/*     */   {
/*     */     final long _seconds;
/*     */     final String _string;
/*     */     
/*     */     public Tick(long seconds, String string)
/*     */     {
/*  59 */       this._seconds = seconds;
/*  60 */       this._string = string;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateCache()
/*     */   {
/*  71 */     this("EEE MMM dd HH:mm:ss zzz yyyy");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateCache(String format)
/*     */   {
/*  81 */     this(format, null, TimeZone.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */   public DateCache(String format, Locale l)
/*     */   {
/*  87 */     this(format, l, TimeZone.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */   public DateCache(String format, Locale l, String tz)
/*     */   {
/*  93 */     this(format, l, TimeZone.getTimeZone(tz));
/*     */   }
/*     */   
/*     */ 
/*     */   public DateCache(String format, Locale l, TimeZone tz)
/*     */   {
/*  99 */     this._formatString = format;
/* 100 */     this._locale = l;
/*     */     
/*     */ 
/* 103 */     int zIndex = this._formatString.indexOf("ZZZ");
/* 104 */     if (zIndex >= 0)
/*     */     {
/* 106 */       String ss1 = this._formatString.substring(0, zIndex);
/* 107 */       String ss2 = this._formatString.substring(zIndex + 3);
/* 108 */       int tzOffset = tz.getRawOffset();
/*     */       
/* 110 */       StringBuilder sb = new StringBuilder(this._formatString.length() + 10);
/* 111 */       sb.append(ss1);
/* 112 */       sb.append("'");
/* 113 */       if (tzOffset >= 0) {
/* 114 */         sb.append('+');
/*     */       }
/*     */       else {
/* 117 */         tzOffset = -tzOffset;
/* 118 */         sb.append('-');
/*     */       }
/*     */       
/* 121 */       int raw = tzOffset / 60000;
/* 122 */       int hr = raw / 60;
/* 123 */       int min = raw % 60;
/*     */       
/* 125 */       if (hr < 10)
/* 126 */         sb.append('0');
/* 127 */       sb.append(hr);
/* 128 */       if (min < 10)
/* 129 */         sb.append('0');
/* 130 */       sb.append(min);
/* 131 */       sb.append('\'');
/*     */       
/* 133 */       sb.append(ss2);
/* 134 */       this._tzFormatString = sb.toString();
/*     */     }
/*     */     else {
/* 137 */       this._tzFormatString = this._formatString;
/*     */     }
/* 139 */     if (this._locale != null)
/*     */     {
/* 141 */       this._tzFormat = new SimpleDateFormat(this._tzFormatString, this._locale);
/*     */     }
/*     */     else
/*     */     {
/* 145 */       this._tzFormat = new SimpleDateFormat(this._tzFormatString);
/*     */     }
/* 147 */     this._tzFormat.setTimeZone(tz);
/*     */     
/* 149 */     this._tick = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TimeZone getTimeZone()
/*     */   {
/* 156 */     return this._tzFormat.getTimeZone();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String format(Date inDate)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual 132	java/util/Date:getTime	()J
/*     */     //   4: ldc2_w 133
/*     */     //   7: ldiv
/*     */     //   8: lstore_2
/*     */     //   9: aload_0
/*     */     //   10: getfield 112	com/facebook/presto/jdbc/internal/jetty/util/DateCache:_tick	Lcom/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick;
/*     */     //   13: astore 4
/*     */     //   15: aload 4
/*     */     //   17: ifnull +13 -> 30
/*     */     //   20: lload_2
/*     */     //   21: aload 4
/*     */     //   23: getfield 138	com/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick:_seconds	J
/*     */     //   26: lcmp
/*     */     //   27: ifeq +28 -> 55
/*     */     //   30: aload_0
/*     */     //   31: dup
/*     */     //   32: astore 5
/*     */     //   34: monitorenter
/*     */     //   35: aload_0
/*     */     //   36: getfield 105	com/facebook/presto/jdbc/internal/jetty/util/DateCache:_tzFormat	Ljava/text/SimpleDateFormat;
/*     */     //   39: aload_1
/*     */     //   40: invokevirtual 140	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   43: aload 5
/*     */     //   45: monitorexit
/*     */     //   46: areturn
/*     */     //   47: astore 6
/*     */     //   49: aload 5
/*     */     //   51: monitorexit
/*     */     //   52: aload 6
/*     */     //   54: athrow
/*     */     //   55: aload 4
/*     */     //   57: getfield 145	com/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick:_string	Ljava/lang/String;
/*     */     //   60: areturn
/*     */     // Line number table:
/*     */     //   Java source line #167	-> byte code offset #0
/*     */     //   Java source line #169	-> byte code offset #9
/*     */     //   Java source line #172	-> byte code offset #15
/*     */     //   Java source line #175	-> byte code offset #30
/*     */     //   Java source line #177	-> byte code offset #35
/*     */     //   Java source line #178	-> byte code offset #47
/*     */     //   Java source line #181	-> byte code offset #55
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	61	0	this	DateCache
/*     */     //   0	61	1	inDate	Date
/*     */     //   8	13	2	seconds	long
/*     */     //   13	43	4	tick	Tick
/*     */     //   32	18	5	Ljava/lang/Object;	Object
/*     */     //   47	6	6	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   35	46	47	finally
/*     */     //   47	52	47	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String format(long inDate)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: lload_1
/*     */     //   1: ldc2_w 133
/*     */     //   4: ldiv
/*     */     //   5: lstore_3
/*     */     //   6: aload_0
/*     */     //   7: getfield 112	com/facebook/presto/jdbc/internal/jetty/util/DateCache:_tick	Lcom/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick;
/*     */     //   10: astore 5
/*     */     //   12: aload 5
/*     */     //   14: ifnull +13 -> 27
/*     */     //   17: lload_3
/*     */     //   18: aload 5
/*     */     //   20: getfield 138	com/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick:_seconds	J
/*     */     //   23: lcmp
/*     */     //   24: ifeq +39 -> 63
/*     */     //   27: new 128	java/util/Date
/*     */     //   30: dup
/*     */     //   31: lload_1
/*     */     //   32: invokespecial 153	java/util/Date:<init>	(J)V
/*     */     //   35: astore 6
/*     */     //   37: aload_0
/*     */     //   38: dup
/*     */     //   39: astore 7
/*     */     //   41: monitorenter
/*     */     //   42: aload_0
/*     */     //   43: getfield 105	com/facebook/presto/jdbc/internal/jetty/util/DateCache:_tzFormat	Ljava/text/SimpleDateFormat;
/*     */     //   46: aload 6
/*     */     //   48: invokevirtual 140	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   51: aload 7
/*     */     //   53: monitorexit
/*     */     //   54: areturn
/*     */     //   55: astore 8
/*     */     //   57: aload 7
/*     */     //   59: monitorexit
/*     */     //   60: aload 8
/*     */     //   62: athrow
/*     */     //   63: aload 5
/*     */     //   65: getfield 145	com/facebook/presto/jdbc/internal/jetty/util/DateCache$Tick:_string	Ljava/lang/String;
/*     */     //   68: areturn
/*     */     // Line number table:
/*     */     //   Java source line #193	-> byte code offset #0
/*     */     //   Java source line #195	-> byte code offset #6
/*     */     //   Java source line #198	-> byte code offset #12
/*     */     //   Java source line #201	-> byte code offset #27
/*     */     //   Java source line #202	-> byte code offset #37
/*     */     //   Java source line #204	-> byte code offset #42
/*     */     //   Java source line #205	-> byte code offset #55
/*     */     //   Java source line #208	-> byte code offset #63
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	69	0	this	DateCache
/*     */     //   0	69	1	inDate	long
/*     */     //   5	13	3	seconds	long
/*     */     //   10	54	5	tick	Tick
/*     */     //   35	12	6	d	Date
/*     */     //   39	19	7	Ljava/lang/Object;	Object
/*     */     //   55	6	8	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   42	54	55	finally
/*     */     //   55	60	55	finally
/*     */   }
/*     */   
/*     */   public String formatNow(long now)
/*     */   {
/* 221 */     long seconds = now / 1000L;
/*     */     
/* 223 */     Tick tick = this._tick;
/*     */     
/*     */ 
/* 226 */     if ((tick != null) && (tick._seconds == seconds))
/* 227 */       return tick._string;
/* 228 */     return formatTick(now)._string;
/*     */   }
/*     */   
/*     */ 
/*     */   public String now()
/*     */   {
/* 234 */     return formatNow(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */   public Tick tick()
/*     */   {
/* 240 */     return formatTick(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */   protected Tick formatTick(long now)
/*     */   {
/* 246 */     long seconds = now / 1000L;
/*     */     
/*     */ 
/* 249 */     synchronized (this)
/*     */     {
/*     */ 
/* 252 */       if ((this._tick == null) || (this._tick._seconds != seconds))
/*     */       {
/* 254 */         String s = this._tzFormat.format(new Date(now));
/* 255 */         return this._tick = new Tick(seconds, s);
/*     */       }
/* 257 */       return this._tick;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFormatString()
/*     */   {
/* 264 */     return this._formatString;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\DateCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */