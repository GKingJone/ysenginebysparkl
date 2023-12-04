/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PeriodFormatter
/*     */ {
/*     */   private final PeriodPrinter iPrinter;
/*     */   private final PeriodParser iParser;
/*     */   private final Locale iLocale;
/*     */   private final PeriodType iParseType;
/*     */   
/*     */   public PeriodFormatter(PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser)
/*     */   {
/*  88 */     this.iPrinter = paramPeriodPrinter;
/*  89 */     this.iParser = paramPeriodParser;
/*  90 */     this.iLocale = null;
/*  91 */     this.iParseType = null;
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
/*     */   PeriodFormatter(PeriodPrinter paramPeriodPrinter, PeriodParser paramPeriodParser, Locale paramLocale, PeriodType paramPeriodType)
/*     */   {
/* 106 */     this.iPrinter = paramPeriodPrinter;
/* 107 */     this.iParser = paramPeriodParser;
/* 108 */     this.iLocale = paramLocale;
/* 109 */     this.iParseType = paramPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isPrinter()
/*     */   {
/* 119 */     return this.iPrinter != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodPrinter getPrinter()
/*     */   {
/* 128 */     return this.iPrinter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isParser()
/*     */   {
/* 137 */     return this.iParser != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodParser getParser()
/*     */   {
/* 146 */     return this.iParser;
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
/*     */   public PeriodFormatter withLocale(Locale paramLocale)
/*     */   {
/* 163 */     if ((paramLocale == getLocale()) || ((paramLocale != null) && (paramLocale.equals(getLocale())))) {
/* 164 */       return this;
/*     */     }
/* 166 */     return new PeriodFormatter(this.iPrinter, this.iParser, paramLocale, this.iParseType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 177 */     return this.iLocale;
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
/*     */   public PeriodFormatter withParseType(PeriodType paramPeriodType)
/*     */   {
/* 191 */     if (paramPeriodType == this.iParseType) {
/* 192 */       return this;
/*     */     }
/* 194 */     return new PeriodFormatter(this.iPrinter, this.iParser, this.iLocale, paramPeriodType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getParseType()
/*     */   {
/* 203 */     return this.iParseType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod)
/*     */   {
/* 214 */     checkPrinter();
/* 215 */     checkPeriod(paramReadablePeriod);
/*     */     
/* 217 */     getPrinter().printTo(paramStringBuffer, paramReadablePeriod, this.iLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod)
/*     */     throws IOException
/*     */   {
/* 227 */     checkPrinter();
/* 228 */     checkPeriod(paramReadablePeriod);
/*     */     
/* 230 */     getPrinter().printTo(paramWriter, paramReadablePeriod, this.iLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String print(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 240 */     checkPrinter();
/* 241 */     checkPeriod(paramReadablePeriod);
/*     */     
/* 243 */     PeriodPrinter localPeriodPrinter = getPrinter();
/* 244 */     StringBuffer localStringBuffer = new StringBuffer(localPeriodPrinter.calculatePrintedLength(paramReadablePeriod, this.iLocale));
/* 245 */     localPeriodPrinter.printTo(localStringBuffer, paramReadablePeriod, this.iLocale);
/* 246 */     return localStringBuffer.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkPrinter()
/*     */   {
/* 255 */     if (this.iPrinter == null) {
/* 256 */       throw new UnsupportedOperationException("Printing not supported");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkPeriod(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 266 */     if (paramReadablePeriod == null) {
/* 267 */       throw new IllegalArgumentException("Period must not be null");
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt)
/*     */   {
/* 292 */     checkParser();
/* 293 */     checkPeriod(paramReadWritablePeriod);
/*     */     
/* 295 */     return getParser().parseInto(paramReadWritablePeriod, paramString, paramInt, this.iLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Period parsePeriod(String paramString)
/*     */   {
/* 306 */     checkParser();
/*     */     
/* 308 */     return parseMutablePeriod(paramString).toPeriod();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MutablePeriod parseMutablePeriod(String paramString)
/*     */   {
/* 319 */     checkParser();
/*     */     
/* 321 */     MutablePeriod localMutablePeriod = new MutablePeriod(0L, this.iParseType);
/* 322 */     int i = getParser().parseInto(localMutablePeriod, paramString, 0, this.iLocale);
/* 323 */     if (i >= 0) {
/* 324 */       if (i >= paramString.length()) {
/* 325 */         return localMutablePeriod;
/*     */       }
/*     */     } else {
/* 328 */       i ^= 0xFFFFFFFF;
/*     */     }
/* 330 */     throw new IllegalArgumentException(FormatUtils.createErrorMessage(paramString, i));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkParser()
/*     */   {
/* 339 */     if (this.iParser == null) {
/* 340 */       throw new UnsupportedOperationException("Parsing not supported");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\PeriodFormatter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */