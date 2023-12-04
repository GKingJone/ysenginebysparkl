/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.DividedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.RemainderDateTimeField;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
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
/*     */ public final class ISOChronology
/*     */   extends AssembledChronology
/*     */ {
/*     */   private static final long serialVersionUID = -6212696554273812441L;
/*     */   private static final ISOChronology INSTANCE_UTC;
/*  57 */   private static final ConcurrentHashMap<DateTimeZone, ISOChronology> cCache = new ConcurrentHashMap();
/*     */   
/*  59 */   static { INSTANCE_UTC = new ISOChronology(GregorianChronology.getInstanceUTC());
/*  60 */     cCache.put(DateTimeZone.UTC, INSTANCE_UTC);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ISOChronology getInstanceUTC()
/*     */   {
/*  70 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ISOChronology getInstance()
/*     */   {
/*  79 */     return getInstance(DateTimeZone.getDefault());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ISOChronology getInstance(DateTimeZone paramDateTimeZone)
/*     */   {
/*  89 */     if (paramDateTimeZone == null) {
/*  90 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*  92 */     Object localObject = (ISOChronology)cCache.get(paramDateTimeZone);
/*  93 */     if (localObject == null) {
/*  94 */       localObject = new ISOChronology(ZonedChronology.getInstance(INSTANCE_UTC, paramDateTimeZone));
/*  95 */       ISOChronology localISOChronology = (ISOChronology)cCache.putIfAbsent(paramDateTimeZone, localObject);
/*  96 */       if (localISOChronology != null) {
/*  97 */         localObject = localISOChronology;
/*     */       }
/*     */     }
/* 100 */     return (ISOChronology)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ISOChronology(Chronology paramChronology)
/*     */   {
/* 110 */     super(paramChronology, null);
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
/* 121 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 131 */     if (paramDateTimeZone == null) {
/* 132 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 134 */     if (paramDateTimeZone == getZone()) {
/* 135 */       return this;
/*     */     }
/* 137 */     return getInstance(paramDateTimeZone);
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
/* 148 */     String str = "ISOChronology";
/* 149 */     DateTimeZone localDateTimeZone = getZone();
/* 150 */     if (localDateTimeZone != null) {
/* 151 */       str = str + '[' + localDateTimeZone.getID() + ']';
/*     */     }
/* 153 */     return str;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields) {
/* 157 */     if (getBase().getZone() == DateTimeZone.UTC)
/*     */     {
/* 159 */       paramFields.centuryOfEra = new DividedDateTimeField(ISOYearOfEraDateTimeField.INSTANCE, DateTimeFieldType.centuryOfEra(), 100);
/*     */       
/* 161 */       paramFields.centuries = paramFields.centuryOfEra.getDurationField();
/*     */       
/* 163 */       paramFields.yearOfCentury = new RemainderDateTimeField((DividedDateTimeField)paramFields.centuryOfEra, DateTimeFieldType.yearOfCentury());
/*     */       
/* 165 */       paramFields.weekyearOfCentury = new RemainderDateTimeField((DividedDateTimeField)paramFields.centuryOfEra, paramFields.weekyears, DateTimeFieldType.weekyearOfCentury());
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 179 */     if (this == paramObject) {
/* 180 */       return true;
/*     */     }
/* 182 */     if ((paramObject instanceof ISOChronology)) {
/* 183 */       ISOChronology localISOChronology = (ISOChronology)paramObject;
/* 184 */       return getZone().equals(localISOChronology.getZone());
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
/* 196 */     return "ISO".hashCode() * 11 + getZone().hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object writeReplace()
/*     */   {
/* 205 */     return new Stub(getZone());
/*     */   }
/*     */   
/*     */   private static final class Stub implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = -6212696554273812441L;
/*     */     private transient DateTimeZone iZone;
/*     */     
/*     */     Stub(DateTimeZone paramDateTimeZone) {
/* 214 */       this.iZone = paramDateTimeZone;
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/* 218 */       return ISOChronology.getInstance(this.iZone);
/*     */     }
/*     */     
/*     */     private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
/* 222 */       paramObjectOutputStream.writeObject(this.iZone);
/*     */     }
/*     */     
/*     */     private void readObject(ObjectInputStream paramObjectInputStream)
/*     */       throws IOException, ClassNotFoundException
/*     */     {
/* 228 */       this.iZone = ((DateTimeZone)paramObjectInputStream.readObject());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\ISOChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */