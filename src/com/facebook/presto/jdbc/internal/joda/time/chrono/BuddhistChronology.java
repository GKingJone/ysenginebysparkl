/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.DelegatedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.DividedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.OffsetDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.RemainderDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.SkipUndoDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.UnsupportedDurationField;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ public final class BuddhistChronology
/*     */   extends AssembledChronology
/*     */ {
/*     */   private static final long serialVersionUID = -3474595157769370126L;
/*     */   public static final int BE = 1;
/*  66 */   private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("BE");
/*     */   
/*     */ 
/*     */   private static final int BUDDHIST_OFFSET = 543;
/*     */   
/*     */ 
/*  72 */   private static final ConcurrentHashMap<DateTimeZone, BuddhistChronology> cCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*  75 */   private static final BuddhistChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddhistChronology getInstanceUTC()
/*     */   {
/*  85 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddhistChronology getInstance()
/*     */   {
/*  94 */     return getInstance(DateTimeZone.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static BuddhistChronology getInstance(DateTimeZone paramDateTimeZone)
/*     */   {
/* 105 */     if (paramDateTimeZone == null) {
/* 106 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 108 */     Object localObject = (BuddhistChronology)cCache.get(paramDateTimeZone);
/* 109 */     if (localObject == null)
/*     */     {
/* 111 */       localObject = new BuddhistChronology(GJChronology.getInstance(paramDateTimeZone, null), null);
/*     */       
/* 113 */       DateTime localDateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, (Chronology)localObject);
/* 114 */       localObject = new BuddhistChronology(LimitChronology.getInstance((Chronology)localObject, localDateTime, null), "");
/* 115 */       BuddhistChronology localBuddhistChronology = (BuddhistChronology)cCache.putIfAbsent(paramDateTimeZone, localObject);
/* 116 */       if (localBuddhistChronology != null) {
/* 117 */         localObject = localBuddhistChronology;
/*     */       }
/*     */     }
/* 120 */     return (BuddhistChronology)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private BuddhistChronology(Chronology paramChronology, Object paramObject)
/*     */   {
/* 132 */     super(paramChronology, paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 139 */     Chronology localChronology = getBase();
/* 140 */     return localChronology == null ? getInstanceUTC() : getInstance(localChronology.getZone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withUTC()
/*     */   {
/* 151 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 161 */     if (paramDateTimeZone == null) {
/* 162 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 164 */     if (paramDateTimeZone == getZone()) {
/* 165 */       return this;
/*     */     }
/* 167 */     return getInstance(paramDateTimeZone);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 179 */     if (this == paramObject) {
/* 180 */       return true;
/*     */     }
/* 182 */     if ((paramObject instanceof BuddhistChronology)) {
/* 183 */       BuddhistChronology localBuddhistChronology = (BuddhistChronology)paramObject;
/* 184 */       return getZone().equals(localBuddhistChronology.getZone());
/*     */     }
/* 186 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 196 */     return "Buddhist".hashCode() * 11 + getZone().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 207 */     String str = "BuddhistChronology";
/* 208 */     DateTimeZone localDateTimeZone = getZone();
/* 209 */     if (localDateTimeZone != null) {
/* 210 */       str = str + '[' + localDateTimeZone.getID() + ']';
/*     */     }
/* 212 */     return str;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields) {
/* 216 */     if (getParam() == null)
/*     */     {
/* 218 */       paramFields.eras = UnsupportedDurationField.getInstance(DurationFieldType.eras());
/*     */       
/*     */ 
/* 221 */       Object localObject = paramFields.year;
/* 222 */       paramFields.year = new OffsetDateTimeField(new SkipUndoDateTimeField(this, (DateTimeField)localObject), 543);
/*     */       
/*     */ 
/*     */ 
/* 226 */       localObject = paramFields.yearOfEra;
/* 227 */       paramFields.yearOfEra = new DelegatedDateTimeField(paramFields.year, paramFields.eras, DateTimeFieldType.yearOfEra());
/*     */       
/*     */ 
/*     */ 
/* 231 */       localObject = paramFields.weekyear;
/* 232 */       paramFields.weekyear = new OffsetDateTimeField(new SkipUndoDateTimeField(this, (DateTimeField)localObject), 543);
/*     */       
/*     */ 
/* 235 */       localObject = new OffsetDateTimeField(paramFields.yearOfEra, 99);
/* 236 */       paramFields.centuryOfEra = new DividedDateTimeField((DateTimeField)localObject, paramFields.eras, DateTimeFieldType.centuryOfEra(), 100);
/*     */       
/* 238 */       paramFields.centuries = paramFields.centuryOfEra.getDurationField();
/*     */       
/* 240 */       localObject = new RemainderDateTimeField((DividedDateTimeField)paramFields.centuryOfEra);
/*     */       
/* 242 */       paramFields.yearOfCentury = new OffsetDateTimeField((DateTimeField)localObject, DateTimeFieldType.yearOfCentury(), 1);
/*     */       
/*     */ 
/* 245 */       localObject = new RemainderDateTimeField(paramFields.weekyear, paramFields.centuries, DateTimeFieldType.weekyearOfCentury(), 100);
/*     */       
/* 247 */       paramFields.weekyearOfCentury = new OffsetDateTimeField((DateTimeField)localObject, DateTimeFieldType.weekyearOfCentury(), 1);
/*     */       
/*     */ 
/* 250 */       paramFields.era = ERA_FIELD;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BuddhistChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */