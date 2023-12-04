/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.InstantConverter;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DateTimeComparator
/*     */   implements Comparator<Object>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6097339773320178364L;
/*  54 */   private static final DateTimeComparator ALL_INSTANCE = new DateTimeComparator(null, null);
/*     */   
/*  56 */   private static final DateTimeComparator DATE_INSTANCE = new DateTimeComparator(DateTimeFieldType.dayOfYear(), null);
/*     */   
/*  58 */   private static final DateTimeComparator TIME_INSTANCE = new DateTimeComparator(null, DateTimeFieldType.dayOfYear());
/*     */   
/*     */ 
/*     */ 
/*     */   private final DateTimeFieldType iLowerLimit;
/*     */   
/*     */ 
/*     */ 
/*     */   private final DateTimeFieldType iUpperLimit;
/*     */   
/*     */ 
/*     */ 
/*     */   public static DateTimeComparator getInstance()
/*     */   {
/*  72 */     return ALL_INSTANCE;
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
/*     */   public static DateTimeComparator getInstance(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/*  87 */     return getInstance(paramDateTimeFieldType, null);
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
/*     */   public static DateTimeComparator getInstance(DateTimeFieldType paramDateTimeFieldType1, DateTimeFieldType paramDateTimeFieldType2)
/*     */   {
/* 106 */     if ((paramDateTimeFieldType1 == null) && (paramDateTimeFieldType2 == null)) {
/* 107 */       return ALL_INSTANCE;
/*     */     }
/* 109 */     if ((paramDateTimeFieldType1 == DateTimeFieldType.dayOfYear()) && (paramDateTimeFieldType2 == null)) {
/* 110 */       return DATE_INSTANCE;
/*     */     }
/* 112 */     if ((paramDateTimeFieldType1 == null) && (paramDateTimeFieldType2 == DateTimeFieldType.dayOfYear())) {
/* 113 */       return TIME_INSTANCE;
/*     */     }
/* 115 */     return new DateTimeComparator(paramDateTimeFieldType1, paramDateTimeFieldType2);
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
/*     */   public static DateTimeComparator getDateOnlyInstance()
/*     */   {
/* 130 */     return DATE_INSTANCE;
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
/*     */   public static DateTimeComparator getTimeOnlyInstance()
/*     */   {
/* 145 */     return TIME_INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DateTimeComparator(DateTimeFieldType paramDateTimeFieldType1, DateTimeFieldType paramDateTimeFieldType2)
/*     */   {
/* 156 */     this.iLowerLimit = paramDateTimeFieldType1;
/* 157 */     this.iUpperLimit = paramDateTimeFieldType2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFieldType getLowerLimit()
/*     */   {
/* 167 */     return this.iLowerLimit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFieldType getUpperLimit()
/*     */   {
/* 176 */     return this.iUpperLimit;
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
/*     */   public int compare(Object paramObject1, Object paramObject2)
/*     */   {
/* 192 */     InstantConverter localInstantConverter = ConverterManager.getInstance().getInstantConverter(paramObject1);
/* 193 */     Chronology localChronology1 = localInstantConverter.getChronology(paramObject1, (Chronology)null);
/* 194 */     long l1 = localInstantConverter.getInstantMillis(paramObject1, localChronology1);
/*     */     
/* 196 */     localInstantConverter = ConverterManager.getInstance().getInstantConverter(paramObject2);
/* 197 */     Chronology localChronology2 = localInstantConverter.getChronology(paramObject2, (Chronology)null);
/* 198 */     long l2 = localInstantConverter.getInstantMillis(paramObject2, localChronology2);
/*     */     
/* 200 */     if (this.iLowerLimit != null) {
/* 201 */       l1 = this.iLowerLimit.getField(localChronology1).roundFloor(l1);
/* 202 */       l2 = this.iLowerLimit.getField(localChronology2).roundFloor(l2);
/*     */     }
/*     */     
/* 205 */     if (this.iUpperLimit != null) {
/* 206 */       l1 = this.iUpperLimit.getField(localChronology1).remainder(l1);
/* 207 */       l2 = this.iUpperLimit.getField(localChronology2).remainder(l2);
/*     */     }
/*     */     
/* 210 */     if (l1 < l2)
/* 211 */       return -1;
/* 212 */     if (l1 > l2) {
/* 213 */       return 1;
/*     */     }
/* 215 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 226 */     return getInstance(this.iLowerLimit, this.iUpperLimit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 236 */     if ((paramObject instanceof DateTimeComparator)) {
/* 237 */       DateTimeComparator localDateTimeComparator = (DateTimeComparator)paramObject;
/* 238 */       return ((this.iLowerLimit == localDateTimeComparator.getLowerLimit()) || ((this.iLowerLimit != null) && (this.iLowerLimit.equals(localDateTimeComparator.getLowerLimit())))) && ((this.iUpperLimit == localDateTimeComparator.getUpperLimit()) || ((this.iUpperLimit != null) && (this.iUpperLimit.equals(localDateTimeComparator.getUpperLimit()))));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 243 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 252 */     return (this.iLowerLimit == null ? 0 : this.iLowerLimit.hashCode()) + 123 * (this.iUpperLimit == null ? 0 : this.iUpperLimit.hashCode());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 262 */     if (this.iLowerLimit == this.iUpperLimit) {
/* 263 */       return "DateTimeComparator[" + (this.iLowerLimit == null ? "" : this.iLowerLimit.getName()) + "]";
/*     */     }
/*     */     
/*     */ 
/* 267 */     return "DateTimeComparator[" + (this.iLowerLimit == null ? "" : this.iLowerLimit.getName()) + "-" + (this.iUpperLimit == null ? "" : this.iUpperLimit.getName()) + "]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\DateTimeComparator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */