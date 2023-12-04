/*    */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*    */ import java.io.IOException;
/*    */ import java.io.Writer;
/*    */ import java.util.Locale;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class DateTimePrinterInternalPrinter
/*    */   implements InternalPrinter
/*    */ {
/*    */   private final DateTimePrinter underlying;
/*    */   
/*    */   static InternalPrinter of(DateTimePrinter paramDateTimePrinter)
/*    */   {
/* 37 */     if ((paramDateTimePrinter instanceof InternalPrinterDateTimePrinter)) {
/* 38 */       return (InternalPrinter)paramDateTimePrinter;
/*    */     }
/* 40 */     if (paramDateTimePrinter == null) {
/* 41 */       return null;
/*    */     }
/* 43 */     return new DateTimePrinterInternalPrinter(paramDateTimePrinter);
/*    */   }
/*    */   
/*    */   private DateTimePrinterInternalPrinter(DateTimePrinter paramDateTimePrinter) {
/* 47 */     this.underlying = paramDateTimePrinter;
/*    */   }
/*    */   
/*    */   DateTimePrinter getUnderlying()
/*    */   {
/* 52 */     return this.underlying;
/*    */   }
/*    */   
/*    */   public int estimatePrintedLength()
/*    */   {
/* 57 */     return this.underlying.estimatePrintedLength();
/*    */   }
/*    */   
/*    */   public void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale) throws IOException
/*    */   {
/* 62 */     if ((paramAppendable instanceof StringBuffer)) {
/* 63 */       localObject = (StringBuffer)paramAppendable;
/* 64 */       this.underlying.printTo((StringBuffer)localObject, paramLong, paramChronology, paramInt, paramDateTimeZone, paramLocale);
/*    */     }
/* 66 */     if ((paramAppendable instanceof Writer)) {
/* 67 */       localObject = (Writer)paramAppendable;
/* 68 */       this.underlying.printTo((Writer)localObject, paramLong, paramChronology, paramInt, paramDateTimeZone, paramLocale);
/*    */     }
/* 70 */     Object localObject = new StringBuffer(estimatePrintedLength());
/* 71 */     this.underlying.printTo((StringBuffer)localObject, paramLong, paramChronology, paramInt, paramDateTimeZone, paramLocale);
/* 72 */     paramAppendable.append((CharSequence)localObject);
/*    */   }
/*    */   
/*    */   public void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale) throws IOException {
/* 76 */     if ((paramAppendable instanceof StringBuffer)) {
/* 77 */       localObject = (StringBuffer)paramAppendable;
/* 78 */       this.underlying.printTo((StringBuffer)localObject, paramReadablePartial, paramLocale);
/*    */     }
/* 80 */     if ((paramAppendable instanceof Writer)) {
/* 81 */       localObject = (Writer)paramAppendable;
/* 82 */       this.underlying.printTo((Writer)localObject, paramReadablePartial, paramLocale);
/*    */     }
/* 84 */     Object localObject = new StringBuffer(estimatePrintedLength());
/* 85 */     this.underlying.printTo((StringBuffer)localObject, paramReadablePartial, paramLocale);
/* 86 */     paramAppendable.append((CharSequence)localObject);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimePrinterInternalPrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */