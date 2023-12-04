/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.InstantConverter;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class BaseDateTime
/*     */   extends AbstractDateTime
/*     */   implements ReadableDateTime, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -6728882245981L;
/*     */   private volatile long iMillis;
/*     */   private volatile Chronology iChronology;
/*     */   
/*     */   public BaseDateTime()
/*     */   {
/*  61 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BaseDateTime(DateTimeZone paramDateTimeZone)
/*     */   {
/*  73 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(paramDateTimeZone));
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
/*     */   public BaseDateTime(Chronology paramChronology)
/*     */   {
/*  86 */     this(DateTimeUtils.currentTimeMillis(), paramChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BaseDateTime(long paramLong)
/*     */   {
/*  97 */     this(paramLong, ISOChronology.getInstance());
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
/*     */   public BaseDateTime(long paramLong, DateTimeZone paramDateTimeZone)
/*     */   {
/* 110 */     this(paramLong, ISOChronology.getInstance(paramDateTimeZone));
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
/*     */   public BaseDateTime(long paramLong, Chronology paramChronology)
/*     */   {
/* 125 */     this.iChronology = checkChronology(paramChronology);
/* 126 */     this.iMillis = checkInstant(paramLong, this.iChronology);
/*     */     
/* 128 */     if (this.iChronology.year().isSupported()) {
/* 129 */       this.iChronology.year().set(this.iMillis, this.iChronology.year().get(this.iMillis));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BaseDateTime(Object paramObject, DateTimeZone paramDateTimeZone)
/*     */   {
/* 151 */     InstantConverter localInstantConverter = ConverterManager.getInstance().getInstantConverter(paramObject);
/* 152 */     Chronology localChronology = checkChronology(localInstantConverter.getChronology(paramObject, paramDateTimeZone));
/* 153 */     this.iChronology = localChronology;
/* 154 */     this.iMillis = checkInstant(localInstantConverter.getInstantMillis(paramObject, localChronology), localChronology);
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
/*     */   public BaseDateTime(Object paramObject, Chronology paramChronology)
/*     */   {
/* 173 */     InstantConverter localInstantConverter = ConverterManager.getInstance().getInstantConverter(paramObject);
/* 174 */     this.iChronology = checkChronology(localInstantConverter.getChronology(paramObject, paramChronology));
/* 175 */     this.iMillis = checkInstant(localInstantConverter.getInstantMillis(paramObject, paramChronology), this.iChronology);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BaseDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 199 */     this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, ISOChronology.getInstance());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BaseDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, DateTimeZone paramDateTimeZone)
/*     */   {
/* 227 */     this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, ISOChronology.getInstance(paramDateTimeZone));
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
/*     */   public BaseDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, Chronology paramChronology)
/*     */   {
/* 257 */     this.iChronology = checkChronology(paramChronology);
/* 258 */     long l = this.iChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */     
/* 260 */     this.iMillis = checkInstant(l, this.iChronology);
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
/*     */   protected Chronology checkChronology(Chronology paramChronology)
/*     */   {
/* 274 */     return DateTimeUtils.getChronology(paramChronology);
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
/*     */   protected long checkInstant(long paramLong, Chronology paramChronology)
/*     */   {
/* 288 */     return paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMillis()
/*     */   {
/* 299 */     return this.iMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology getChronology()
/*     */   {
/* 308 */     return this.iChronology;
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
/*     */   protected void setMillis(long paramLong)
/*     */   {
/* 321 */     this.iMillis = checkInstant(paramLong, this.iChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setChronology(Chronology paramChronology)
/*     */   {
/* 333 */     this.iChronology = checkChronology(paramChronology);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\BaseDateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */