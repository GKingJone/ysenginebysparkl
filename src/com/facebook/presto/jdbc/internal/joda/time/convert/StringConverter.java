/*     */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritableInterval;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.ISOPeriodFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
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
/*     */ class StringConverter
/*     */   extends AbstractConverter
/*     */   implements InstantConverter, PartialConverter, DurationConverter, PeriodConverter, IntervalConverter
/*     */ {
/*  44 */   static final StringConverter INSTANCE = new StringConverter();
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
/*     */   public long getInstantMillis(Object paramObject, Chronology paramChronology)
/*     */   {
/*  63 */     String str = (String)paramObject;
/*  64 */     DateTimeFormatter localDateTimeFormatter = ISODateTimeFormat.dateTimeParser();
/*  65 */     return localDateTimeFormatter.withChronology(paramChronology).parseMillis(str);
/*     */   }
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
/*     */   public int[] getPartialValues(ReadablePartial paramReadablePartial, Object paramObject, Chronology paramChronology, DateTimeFormatter paramDateTimeFormatter)
/*     */   {
/*  84 */     if (paramDateTimeFormatter.getZone() != null) {
/*  85 */       paramChronology = paramChronology.withZone(paramDateTimeFormatter.getZone());
/*     */     }
/*  87 */     long l = paramDateTimeFormatter.withChronology(paramChronology).parseMillis((String)paramObject);
/*  88 */     return paramChronology.get(paramReadablePartial, l);
/*     */   }
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
/*     */   public long getDurationMillis(Object paramObject)
/*     */   {
/* 102 */     String str1 = (String)paramObject;
/* 103 */     String str2 = str1;
/* 104 */     int i = str2.length();
/* 105 */     if ((i < 4) || ((str2.charAt(0) != 'P') && (str2.charAt(0) != 'p')) || ((str2.charAt(1) != 'T') && (str2.charAt(1) != 't')) || ((str2.charAt(i - 1) != 'S') && (str2.charAt(i - 1) != 's')))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 111 */       throw new IllegalArgumentException("Invalid format: \"" + str1 + '"');
/*     */     }
/* 113 */     str2 = str2.substring(2, i - 1);
/* 114 */     int j = -1;
/* 115 */     int k = 0;
/* 116 */     for (int m = 0; m < str2.length(); m++) {
/* 117 */       if ((str2.charAt(m) < '0') || (str2.charAt(m) > '9'))
/*     */       {
/* 119 */         if ((m == 0) && (str2.charAt(0) == '-'))
/*     */         {
/* 121 */           k = 1;
/* 122 */         } else { if (m > (k != 0 ? 1 : 0)) if ((str2.charAt(m) == '.') && (j == -1))
/*     */             {
/* 124 */               j = m; continue;
/*     */             }
/* 126 */           throw new IllegalArgumentException("Invalid format: \"" + str1 + '"');
/*     */         } }
/*     */     }
/* 129 */     long l1 = 0L;long l2 = 0L;
/* 130 */     int n = k != 0 ? 1 : 0;
/* 131 */     if (j > 0) {
/* 132 */       l2 = Long.parseLong(str2.substring(n, j));
/* 133 */       str2 = str2.substring(j + 1);
/* 134 */       if (str2.length() != 3) {
/* 135 */         str2 = (str2 + "000").substring(0, 3);
/*     */       }
/* 137 */       l1 = Integer.parseInt(str2);
/* 138 */     } else if (k != 0) {
/* 139 */       l2 = Long.parseLong(str2.substring(n, str2.length()));
/*     */     } else {
/* 141 */       l2 = Long.parseLong(str2);
/*     */     }
/* 143 */     if (k != 0) {
/* 144 */       return FieldUtils.safeAdd(FieldUtils.safeMultiply(-l2, 1000), -l1);
/*     */     }
/* 146 */     return FieldUtils.safeAdd(FieldUtils.safeMultiply(l2, 1000), l1);
/*     */   }
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
/*     */   public void setInto(ReadWritablePeriod paramReadWritablePeriod, Object paramObject, Chronology paramChronology)
/*     */   {
/* 162 */     String str = (String)paramObject;
/* 163 */     PeriodFormatter localPeriodFormatter = ISOPeriodFormat.standard();
/* 164 */     paramReadWritablePeriod.clear();
/* 165 */     int i = localPeriodFormatter.parseInto(paramReadWritablePeriod, str, 0);
/* 166 */     if (i < str.length()) {
/* 167 */       if (i < 0)
/*     */       {
/* 169 */         localPeriodFormatter.withParseType(paramReadWritablePeriod.getPeriodType()).parseMutablePeriod(str);
/*     */       }
/* 171 */       throw new IllegalArgumentException("Invalid format: \"" + str + '"');
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setInto(ReadWritableInterval paramReadWritableInterval, Object paramObject, Chronology paramChronology)
/*     */   {
/* 184 */     String str1 = (String)paramObject;
/*     */     
/* 186 */     int i = str1.indexOf('/');
/* 187 */     if (i < 0) {
/* 188 */       throw new IllegalArgumentException("Format requires a '/' separator: " + str1);
/*     */     }
/*     */     
/* 191 */     String str2 = str1.substring(0, i);
/* 192 */     if (str2.length() <= 0) {
/* 193 */       throw new IllegalArgumentException("Format invalid: " + str1);
/*     */     }
/* 195 */     String str3 = str1.substring(i + 1);
/* 196 */     if (str3.length() <= 0) {
/* 197 */       throw new IllegalArgumentException("Format invalid: " + str1);
/*     */     }
/*     */     
/* 200 */     DateTimeFormatter localDateTimeFormatter = ISODateTimeFormat.dateTimeParser();
/* 201 */     localDateTimeFormatter = localDateTimeFormatter.withChronology(paramChronology);
/* 202 */     PeriodFormatter localPeriodFormatter = ISOPeriodFormat.standard();
/* 203 */     long l1 = 0L;long l2 = 0L;
/* 204 */     Period localPeriod = null;
/* 205 */     Chronology localChronology = null;
/*     */     
/*     */ 
/* 208 */     int j = str2.charAt(0);
/* 209 */     DateTime localDateTime; if ((j == 80) || (j == 112)) {
/* 210 */       localPeriod = localPeriodFormatter.withParseType(getPeriodType(str2)).parsePeriod(str2);
/*     */     } else {
/* 212 */       localDateTime = localDateTimeFormatter.parseDateTime(str2);
/* 213 */       l1 = localDateTime.getMillis();
/* 214 */       localChronology = localDateTime.getChronology();
/*     */     }
/*     */     
/*     */ 
/* 218 */     j = str3.charAt(0);
/* 219 */     if ((j == 80) || (j == 112)) {
/* 220 */       if (localPeriod != null) {
/* 221 */         throw new IllegalArgumentException("Interval composed of two durations: " + str1);
/*     */       }
/* 223 */       localPeriod = localPeriodFormatter.withParseType(getPeriodType(str3)).parsePeriod(str3);
/* 224 */       paramChronology = paramChronology != null ? paramChronology : localChronology;
/* 225 */       l2 = paramChronology.add(localPeriod, l1, 1);
/*     */     } else {
/* 227 */       localDateTime = localDateTimeFormatter.parseDateTime(str3);
/* 228 */       l2 = localDateTime.getMillis();
/* 229 */       localChronology = localChronology != null ? localChronology : localDateTime.getChronology();
/* 230 */       paramChronology = paramChronology != null ? paramChronology : localChronology;
/* 231 */       if (localPeriod != null) {
/* 232 */         l1 = paramChronology.add(localPeriod, l2, -1);
/*     */       }
/*     */     }
/*     */     
/* 236 */     paramReadWritableInterval.setInterval(l1, l2);
/* 237 */     paramReadWritableInterval.setChronology(paramChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> getSupportedType()
/*     */   {
/* 247 */     return String.class;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\StringConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */