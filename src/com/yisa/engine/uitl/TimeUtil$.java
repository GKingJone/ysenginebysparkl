/*     */ package com.yisa.engine.uitl;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import scala.Predef.;
/*     */ 
/*     */ public final class TimeUtil$
/*     */ {
/*     */   public static final  MODULE$;
/*     */   
/*     */   static
/*     */   {
/*     */     new ();
/*     */   }
/*     */   
/*     */   public String getLCacheDataDateid(int days)
/*     */   {
/*  17 */     java.util.Calendar cal = java.util.Calendar.getInstance();
/*  18 */     cal.setTime(new java.util.Date());
/*  19 */     cal.add(5, -days);
/*  20 */     SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
/*  21 */     return format.format(cal.getTime());
/*     */   }
/*     */   
/*     */   public int getDateId() {
/*  25 */     SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
/*  26 */     java.util.Date today = new java.util.Date();
/*  27 */     return new scala.collection.immutable.StringOps(Predef..MODULE$.augmentString(format.format(today))).toInt();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDateId(String x)
/*     */   {
/*  34 */     SimpleDateFormat formatDateid = new SimpleDateFormat("yyyyMMdd");
/*     */     try
/*     */     {
/*  37 */       SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
/*  38 */       java.util.Date d = format.parse(x);
/*     */       
/*  40 */       return new scala.collection.immutable.StringOps(Predef..MODULE$.augmentString(formatDateid.format(d))).toInt();
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*  36 */       Predef..MODULE$.println("getDateId parse timestamp wrong");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  44 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOldDay(int times)
/*     */   {
/*  53 */     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
/*  54 */     java.util.Calendar cal = java.util.Calendar.getInstance();
/*  55 */     cal.add(5, -times);
/*  56 */     String yesterday = dateFormat.format(cal.getTime());
/*     */     
/*  58 */     return yesterday;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.util.Date getDate(String x)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 33	java/text/SimpleDateFormat
/*     */     //   3: dup
/*     */     //   4: ldc 76
/*     */     //   6: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   9: astore_2
/*     */     //   10: aload_1
/*     */     //   11: ldc 99
/*     */     //   13: astore 4
/*     */     //   15: dup
/*     */     //   16: ifnonnull +12 -> 28
/*     */     //   19: pop
/*     */     //   20: aload 4
/*     */     //   22: ifnull +14 -> 36
/*     */     //   25: goto +15 -> 40
/*     */     //   28: aload 4
/*     */     //   30: invokevirtual 103	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   33: ifeq +7 -> 40
/*     */     //   36: aconst_null
/*     */     //   37: pop
/*     */     //   38: aconst_null
/*     */     //   39: areturn
/*     */     //   40: aload_2
/*     */     //   41: aload_1
/*     */     //   42: invokevirtual 80	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   45: astore 5
/*     */     //   47: aload 5
/*     */     //   49: areturn
/*     */     //   50: astore_3
/*     */     //   51: getstatic 61	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   54: ldc 105
/*     */     //   56: invokevirtual 86	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   59: aconst_null
/*     */     //   60: areturn
/*     */     // Line number table:
/*     */     //   Java source line #67	-> byte code offset #0
/*     */     //   Java source line #70	-> byte code offset #10
/*     */     //   Java source line #71	-> byte code offset #36
/*     */     //   Java source line #73	-> byte code offset #40
/*     */     //   Java source line #74	-> byte code offset #47
/*     */     //   Java source line #77	-> byte code offset #50
/*     */     //   Java source line #69	-> byte code offset #50
/*     */     //   Java source line #79	-> byte code offset #59
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	61	0	this	
/*     */     //   0	61	1	x	String
/*     */     //   10	50	2	format	SimpleDateFormat
/*     */     //   47	14	5	d	java.util.Date
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   10	50	50	java/lang/Exception
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getTimeStringFormat(String x)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 33	java/text/SimpleDateFormat
/*     */     //   3: dup
/*     */     //   4: ldc 76
/*     */     //   6: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   9: astore_2
/*     */     //   10: new 33	java/text/SimpleDateFormat
/*     */     //   13: dup
/*     */     //   14: ldc 108
/*     */     //   16: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   19: astore_3
/*     */     //   20: aload_1
/*     */     //   21: ldc 99
/*     */     //   23: astore 5
/*     */     //   25: dup
/*     */     //   26: ifnonnull +12 -> 38
/*     */     //   29: pop
/*     */     //   30: aload 5
/*     */     //   32: ifnull +14 -> 46
/*     */     //   35: goto +15 -> 50
/*     */     //   38: aload 5
/*     */     //   40: invokevirtual 103	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   43: ifeq +7 -> 50
/*     */     //   46: aconst_null
/*     */     //   47: pop
/*     */     //   48: aconst_null
/*     */     //   49: areturn
/*     */     //   50: aload_3
/*     */     //   51: aload_2
/*     */     //   52: aload_1
/*     */     //   53: invokevirtual 80	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   56: invokevirtual 46	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   59: astore 6
/*     */     //   61: aload 6
/*     */     //   63: areturn
/*     */     //   64: astore 4
/*     */     //   66: getstatic 61	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   69: ldc 105
/*     */     //   71: invokevirtual 86	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   74: aconst_null
/*     */     //   75: areturn
/*     */     // Line number table:
/*     */     //   Java source line #88	-> byte code offset #0
/*     */     //   Java source line #89	-> byte code offset #10
/*     */     //   Java source line #92	-> byte code offset #20
/*     */     //   Java source line #93	-> byte code offset #46
/*     */     //   Java source line #95	-> byte code offset #50
/*     */     //   Java source line #96	-> byte code offset #61
/*     */     //   Java source line #99	-> byte code offset #64
/*     */     //   Java source line #91	-> byte code offset #64
/*     */     //   Java source line #102	-> byte code offset #74
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	76	0	this	
/*     */     //   0	76	1	x	String
/*     */     //   10	65	2	format	SimpleDateFormat
/*     */     //   20	55	3	format2	SimpleDateFormat
/*     */     //   61	15	6	timeFormat	String
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	64	64	java/lang/Exception
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public String getTimeStringFormatYMD(String x)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 33	java/text/SimpleDateFormat
/*     */     //   3: dup
/*     */     //   4: ldc 76
/*     */     //   6: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   9: astore_2
/*     */     //   10: new 33	java/text/SimpleDateFormat
/*     */     //   13: dup
/*     */     //   14: ldc 35
/*     */     //   16: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   19: astore_3
/*     */     //   20: aload_1
/*     */     //   21: ldc 99
/*     */     //   23: astore 5
/*     */     //   25: dup
/*     */     //   26: ifnonnull +12 -> 38
/*     */     //   29: pop
/*     */     //   30: aload 5
/*     */     //   32: ifnull +14 -> 46
/*     */     //   35: goto +15 -> 50
/*     */     //   38: aload 5
/*     */     //   40: invokevirtual 103	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   43: ifeq +7 -> 50
/*     */     //   46: aconst_null
/*     */     //   47: pop
/*     */     //   48: aconst_null
/*     */     //   49: areturn
/*     */     //   50: aload_3
/*     */     //   51: aload_2
/*     */     //   52: aload_1
/*     */     //   53: invokevirtual 80	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   56: invokevirtual 46	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
/*     */     //   59: astore 6
/*     */     //   61: aload 6
/*     */     //   63: areturn
/*     */     //   64: astore 4
/*     */     //   66: getstatic 61	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   69: ldc 105
/*     */     //   71: invokevirtual 86	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   74: aconst_null
/*     */     //   75: areturn
/*     */     // Line number table:
/*     */     //   Java source line #110	-> byte code offset #0
/*     */     //   Java source line #111	-> byte code offset #10
/*     */     //   Java source line #114	-> byte code offset #20
/*     */     //   Java source line #115	-> byte code offset #46
/*     */     //   Java source line #117	-> byte code offset #50
/*     */     //   Java source line #118	-> byte code offset #61
/*     */     //   Java source line #121	-> byte code offset #64
/*     */     //   Java source line #113	-> byte code offset #64
/*     */     //   Java source line #124	-> byte code offset #74
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	76	0	this	
/*     */     //   0	76	1	x	String
/*     */     //   10	65	2	format	SimpleDateFormat
/*     */     //   20	55	3	format2	SimpleDateFormat
/*     */     //   61	15	6	timeFormat	String
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   20	64	64	java/lang/Exception
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public java.sql.Timestamp getTimestamp(String x)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 33	java/text/SimpleDateFormat
/*     */     //   3: dup
/*     */     //   4: ldc 76
/*     */     //   6: invokespecial 38	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
/*     */     //   9: astore_2
/*     */     //   10: aload_1
/*     */     //   11: ldc 99
/*     */     //   13: astore 4
/*     */     //   15: dup
/*     */     //   16: ifnonnull +12 -> 28
/*     */     //   19: pop
/*     */     //   20: aload 4
/*     */     //   22: ifnull +14 -> 36
/*     */     //   25: goto +15 -> 40
/*     */     //   28: aload 4
/*     */     //   30: invokevirtual 103	java/lang/Object:equals	(Ljava/lang/Object;)Z
/*     */     //   33: ifeq +7 -> 40
/*     */     //   36: aconst_null
/*     */     //   37: pop
/*     */     //   38: aconst_null
/*     */     //   39: areturn
/*     */     //   40: aload_2
/*     */     //   41: aload_1
/*     */     //   42: invokevirtual 80	java/text/SimpleDateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
/*     */     //   45: astore 5
/*     */     //   47: new 115	java/sql/Timestamp
/*     */     //   50: dup
/*     */     //   51: aload 5
/*     */     //   53: invokevirtual 118	java/util/Date:getTime	()J
/*     */     //   56: invokespecial 121	java/sql/Timestamp:<init>	(J)V
/*     */     //   59: astore 6
/*     */     //   61: aload 6
/*     */     //   63: areturn
/*     */     //   64: astore_3
/*     */     //   65: getstatic 61	scala/Predef$:MODULE$	Lscala/Predef$;
/*     */     //   68: ldc 105
/*     */     //   70: invokevirtual 86	scala/Predef$:println	(Ljava/lang/Object;)V
/*     */     //   73: aconst_null
/*     */     //   74: areturn
/*     */     // Line number table:
/*     */     //   Java source line #133	-> byte code offset #0
/*     */     //   Java source line #136	-> byte code offset #10
/*     */     //   Java source line #137	-> byte code offset #36
/*     */     //   Java source line #139	-> byte code offset #40
/*     */     //   Java source line #140	-> byte code offset #47
/*     */     //   Java source line #141	-> byte code offset #61
/*     */     //   Java source line #144	-> byte code offset #64
/*     */     //   Java source line #135	-> byte code offset #64
/*     */     //   Java source line #147	-> byte code offset #73
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	75	0	this	
/*     */     //   0	75	1	x	String
/*     */     //   10	64	2	format	SimpleDateFormat
/*     */     //   47	28	5	d	java.util.Date
/*     */     //   61	14	6	t	java.sql.Timestamp
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   10	64	64	java/lang/Exception
/*     */   }
/*     */   
/*     */   public long getTimestampLong(String x)
/*     */   {
/*     */     try
/*     */     {
/* 156 */       SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
/* 157 */       java.util.Date d = format.parse(x);
/* 158 */       return d.getTime() / 1000L;
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 155 */       Predef..MODULE$.println("getTimestampLong parse timestamp wrong");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getStringFromTimestampLong(long x)
/*     */   {
/*     */     try
/*     */     {
/* 172 */       java.util.Date date = new java.util.Date(x);
/* 173 */       SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
/* 174 */       return format.format(date);
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/* 171 */       Predef..MODULE$.println("getTimestampLong parse timestamp wrong");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 180 */     return "";
/*     */   }
/*     */   
/* 183 */   private TimeUtil$() { MODULE$ = this; }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\engine\uitl\TimeUtil$.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */