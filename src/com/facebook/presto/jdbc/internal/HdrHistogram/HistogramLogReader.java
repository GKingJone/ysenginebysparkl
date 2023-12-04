/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Locale;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Scanner;
/*     */ import java.util.zip.DataFormatException;
/*     */ import javax.xml.bind.DatatypeConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HistogramLogReader
/*     */ {
/*     */   private final Scanner scanner;
/*  63 */   private double startTimeSec = 0.0D;
/*  64 */   private boolean observedStartTime = false;
/*  65 */   private double baseTimeSec = 0.0D;
/*  66 */   private boolean observedBaseTime = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogReader(String inputFileName)
/*     */     throws FileNotFoundException
/*     */   {
/*  74 */     this.scanner = new Scanner(new File(inputFileName));
/*  75 */     initScanner();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogReader(InputStream inputStream)
/*     */   {
/*  83 */     this.scanner = new Scanner(inputStream);
/*  84 */     initScanner();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogReader(File inputFile)
/*     */     throws FileNotFoundException
/*     */   {
/*  93 */     this.scanner = new Scanner(inputFile);
/*  94 */     initScanner();
/*     */   }
/*     */   
/*     */   private void initScanner()
/*     */   {
/*  99 */     this.scanner.useLocale(Locale.US);
/* 100 */     this.scanner.useDelimiter("[, \\r\\n]");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getStartTimeSec()
/*     */   {
/* 112 */     return this.startTimeSec;
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
/*     */ 
/*     */   public EncodableHistogram nextIntervalHistogram(double startTimeSec, double endTimeSec)
/*     */   {
/* 143 */     return nextIntervalHistogram(startTimeSec, endTimeSec, false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncodableHistogram nextAbsoluteIntervalHistogram(double absoluteStartTimeSec, double absoluteEndTimeSec)
/*     */   {
/* 177 */     return nextIntervalHistogram(absoluteStartTimeSec, absoluteEndTimeSec, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EncodableHistogram nextIntervalHistogram()
/*     */   {
/* 189 */     return nextIntervalHistogram(0.0D, 9.223372036854776E18D, true);
/*     */   }
/*     */   
/*     */   private EncodableHistogram nextIntervalHistogram(double rangeStartTimeSec, double rangeEndTimeSec, boolean absolute)
/*     */   {
/* 194 */     while (this.scanner.hasNextLine()) {
/*     */       try {
/* 196 */         if (this.scanner.hasNext("\\#.*"))
/*     */         {
/*     */ 
/* 199 */           if (this.scanner.hasNext("#\\[StartTime:")) {
/* 200 */             this.scanner.next("#\\[StartTime:");
/* 201 */             if (this.scanner.hasNextDouble()) {
/* 202 */               this.startTimeSec = this.scanner.nextDouble();
/* 203 */               this.observedStartTime = true;
/*     */             }
/* 205 */           } else if (this.scanner.hasNext("#\\[BaseTime:")) {
/* 206 */             this.scanner.next("#\\[BaseTime:");
/* 207 */             if (this.scanner.hasNextDouble()) {
/* 208 */               this.baseTimeSec = this.scanner.nextDouble();
/* 209 */               this.observedBaseTime = true;
/*     */             }
/*     */           }
/* 212 */           this.scanner.nextLine();
/*     */ 
/*     */ 
/*     */         }
/* 216 */         else if (this.scanner.hasNext("\"StartTimestamp\".*"))
/*     */         {
/* 218 */           this.scanner.nextLine();
/*     */         }
/*     */         else
/*     */         {
/* 222 */           String tagString = null;
/* 223 */           if (this.scanner.hasNext("Tag\\=.*")) {
/* 224 */             tagString = this.scanner.next("Tag\\=.*").substring(4);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 229 */           double logTimeStampInSec = this.scanner.nextDouble();
/*     */           
/* 231 */           if (!this.observedStartTime)
/*     */           {
/* 233 */             this.startTimeSec = logTimeStampInSec;
/* 234 */             this.observedStartTime = true;
/*     */           }
/* 236 */           if (!this.observedBaseTime)
/*     */           {
/* 238 */             if (logTimeStampInSec < this.startTimeSec - 3.1536E7D)
/*     */             {
/*     */ 
/* 241 */               this.baseTimeSec = this.startTimeSec;
/*     */             }
/*     */             else {
/* 244 */               this.baseTimeSec = 0.0D;
/*     */             }
/* 246 */             this.observedBaseTime = true;
/*     */           }
/*     */           
/* 249 */           double absoluteStartTimeStampSec = logTimeStampInSec + this.baseTimeSec;
/* 250 */           double offsetStartTimeStampSec = absoluteStartTimeStampSec - this.startTimeSec;
/*     */           
/* 252 */           double intervalLengthSec = this.scanner.nextDouble();
/* 253 */           double absoluteEndTimeStampSec = absoluteStartTimeStampSec + intervalLengthSec;
/*     */           
/* 255 */           double startTimeStampToCheckRangeOn = absolute ? absoluteStartTimeStampSec : offsetStartTimeStampSec;
/*     */           
/* 257 */           if (startTimeStampToCheckRangeOn < rangeStartTimeSec) {
/* 258 */             this.scanner.nextLine();
/*     */           }
/*     */           else
/*     */           {
/* 262 */             if (startTimeStampToCheckRangeOn > rangeEndTimeSec) {
/* 263 */               return null;
/*     */             }
/*     */             
/* 266 */             this.scanner.nextDouble();
/* 267 */             String compressedPayloadString = this.scanner.next();
/* 268 */             ByteBuffer buffer = ByteBuffer.wrap(
/* 269 */               DatatypeConverter.parseBase64Binary(compressedPayloadString));
/*     */             
/* 271 */             EncodableHistogram histogram = EncodableHistogram.decodeFromCompressedByteBuffer(buffer, 0L);
/*     */             
/* 273 */             histogram.setStartTimeStamp((absoluteStartTimeStampSec * 1000.0D));
/* 274 */             histogram.setEndTimeStamp((absoluteEndTimeStampSec * 1000.0D));
/* 275 */             histogram.setTag(tagString);
/*     */             
/* 277 */             this.scanner.nextLine();
/*     */             
/* 279 */             return histogram;
/*     */           }
/*     */         }
/* 282 */       } catch (NoSuchElementException ex) { return null;
/*     */       } catch (DataFormatException ex) {
/* 284 */         return null;
/*     */       }
/*     */     }
/* 287 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\HistogramLogReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */