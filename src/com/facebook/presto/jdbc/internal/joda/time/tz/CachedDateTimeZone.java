/*     */ package com.facebook.presto.jdbc.internal.joda.time.tz;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CachedDateTimeZone
/*     */   extends DateTimeZone
/*     */ {
/*     */   private static final long serialVersionUID = 5472298452022250685L;
/*     */   private static final int cInfoCacheMask;
/*     */   private final DateTimeZone iZone;
/*     */   
/*     */   static
/*     */   {
/*     */     Integer localInteger;
/*     */     try
/*     */     {
/*  39 */       localInteger = Integer.getInteger("com.facebook.presto.jdbc.internal.joda.time.tz.CachedDateTimeZone.size");
/*     */     } catch (SecurityException localSecurityException) {
/*  41 */       localInteger = null;
/*     */     }
/*     */     
/*     */     int i;
/*  45 */     if (localInteger == null)
/*     */     {
/*     */ 
/*  48 */       i = 512;
/*     */     } else {
/*  50 */       i = localInteger.intValue();
/*     */       
/*  52 */       i--;
/*  53 */       int j = 0;
/*  54 */       while (i > 0) {
/*  55 */         j++;
/*  56 */         i >>= 1;
/*     */       }
/*  58 */       i = 1 << j;
/*     */     }
/*     */     
/*  61 */     cInfoCacheMask = i - 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static CachedDateTimeZone forZone(DateTimeZone paramDateTimeZone)
/*     */   {
/*  68 */     if ((paramDateTimeZone instanceof CachedDateTimeZone)) {
/*  69 */       return (CachedDateTimeZone)paramDateTimeZone;
/*     */     }
/*  71 */     return new CachedDateTimeZone(paramDateTimeZone);
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
/*  84 */   private final transient Info[] iInfoCache = new Info[cInfoCacheMask + 1];
/*     */   
/*     */   private CachedDateTimeZone(DateTimeZone paramDateTimeZone) {
/*  87 */     super(paramDateTimeZone.getID());
/*  88 */     this.iZone = paramDateTimeZone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DateTimeZone getUncachedZone()
/*     */   {
/*  95 */     return this.iZone;
/*     */   }
/*     */   
/*     */   public String getNameKey(long paramLong) {
/*  99 */     return getInfo(paramLong).getNameKey(paramLong);
/*     */   }
/*     */   
/*     */   public int getOffset(long paramLong) {
/* 103 */     return getInfo(paramLong).getOffset(paramLong);
/*     */   }
/*     */   
/*     */   public int getStandardOffset(long paramLong) {
/* 107 */     return getInfo(paramLong).getStandardOffset(paramLong);
/*     */   }
/*     */   
/*     */   public boolean isFixed() {
/* 111 */     return this.iZone.isFixed();
/*     */   }
/*     */   
/*     */   public long nextTransition(long paramLong) {
/* 115 */     return this.iZone.nextTransition(paramLong);
/*     */   }
/*     */   
/*     */   public long previousTransition(long paramLong) {
/* 119 */     return this.iZone.previousTransition(paramLong);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 123 */     return this.iZone.hashCode();
/*     */   }
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 127 */     if (this == paramObject) {
/* 128 */       return true;
/*     */     }
/* 130 */     if ((paramObject instanceof CachedDateTimeZone)) {
/* 131 */       return this.iZone.equals(((CachedDateTimeZone)paramObject).iZone);
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Info getInfo(long paramLong)
/*     */   {
/* 140 */     int i = (int)(paramLong >> 32);
/* 141 */     Info[] arrayOfInfo = this.iInfoCache;
/* 142 */     int j = i & cInfoCacheMask;
/* 143 */     Info localInfo = arrayOfInfo[j];
/* 144 */     if ((localInfo == null) || ((int)(localInfo.iPeriodStart >> 32) != i)) {
/* 145 */       localInfo = createInfo(paramLong);
/* 146 */       arrayOfInfo[j] = localInfo;
/*     */     }
/* 148 */     return localInfo;
/*     */   }
/*     */   
/*     */   private Info createInfo(long paramLong) {
/* 152 */     long l1 = paramLong & 0xFFFFFFFF00000000;
/* 153 */     Info localInfo1 = new Info(this.iZone, l1);
/*     */     
/* 155 */     long l2 = l1 | 0xFFFFFFFF;
/* 156 */     Info localInfo2 = localInfo1;
/*     */     for (;;) {
/* 158 */       long l3 = this.iZone.nextTransition(l1);
/* 159 */       if ((l3 == l1) || (l3 > l2)) {
/*     */         break;
/*     */       }
/* 162 */       l1 = l3;
/* 163 */       localInfo2 = localInfo2.iNextInfo = new Info(this.iZone, l1);
/*     */     }
/*     */     
/* 166 */     return localInfo1;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class Info
/*     */   {
/*     */     public final long iPeriodStart;
/*     */     
/*     */     public final DateTimeZone iZoneRef;
/*     */     Info iNextInfo;
/*     */     private String iNameKey;
/* 177 */     private int iOffset = Integer.MIN_VALUE;
/* 178 */     private int iStandardOffset = Integer.MIN_VALUE;
/*     */     
/*     */     Info(DateTimeZone paramDateTimeZone, long paramLong) {
/* 181 */       this.iPeriodStart = paramLong;
/* 182 */       this.iZoneRef = paramDateTimeZone;
/*     */     }
/*     */     
/*     */     public String getNameKey(long paramLong) {
/* 186 */       if ((this.iNextInfo == null) || (paramLong < this.iNextInfo.iPeriodStart)) {
/* 187 */         if (this.iNameKey == null) {
/* 188 */           this.iNameKey = this.iZoneRef.getNameKey(this.iPeriodStart);
/*     */         }
/* 190 */         return this.iNameKey;
/*     */       }
/* 192 */       return this.iNextInfo.getNameKey(paramLong);
/*     */     }
/*     */     
/*     */     public int getOffset(long paramLong) {
/* 196 */       if ((this.iNextInfo == null) || (paramLong < this.iNextInfo.iPeriodStart)) {
/* 197 */         if (this.iOffset == Integer.MIN_VALUE) {
/* 198 */           this.iOffset = this.iZoneRef.getOffset(this.iPeriodStart);
/*     */         }
/* 200 */         return this.iOffset;
/*     */       }
/* 202 */       return this.iNextInfo.getOffset(paramLong);
/*     */     }
/*     */     
/*     */     public int getStandardOffset(long paramLong) {
/* 206 */       if ((this.iNextInfo == null) || (paramLong < this.iNextInfo.iPeriodStart)) {
/* 207 */         if (this.iStandardOffset == Integer.MIN_VALUE) {
/* 208 */           this.iStandardOffset = this.iZoneRef.getStandardOffset(this.iPeriodStart);
/*     */         }
/* 210 */         return this.iStandardOffset;
/*     */       }
/* 212 */       return this.iNextInfo.getStandardOffset(paramLong);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\CachedDateTimeZone.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */