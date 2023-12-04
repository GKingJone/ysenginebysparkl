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
/*     */ class AbstractHistogramLogReader
/*     */ {
/*     */   protected final Scanner scanner;
/*  22 */   private double startTimeSec = 0.0D;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AbstractHistogramLogReader(String inputFileName)
/*     */     throws FileNotFoundException
/*     */   {
/*  30 */     this.scanner = new Scanner(new File(inputFileName));
/*  31 */     initScanner();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AbstractHistogramLogReader(InputStream inputStream)
/*     */   {
/*  39 */     this.scanner = new Scanner(inputStream);
/*  40 */     initScanner();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AbstractHistogramLogReader(File inputFile)
/*     */     throws FileNotFoundException
/*     */   {
/*  49 */     this.scanner = new Scanner(inputFile);
/*  50 */     initScanner();
/*     */   }
/*     */   
/*     */   private void initScanner()
/*     */   {
/*  55 */     this.scanner.useLocale(Locale.US);
/*  56 */     this.scanner.useDelimiter("[ ,\\r\\n]");
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
/*  68 */     return this.startTimeSec;
/*     */   }
/*     */   
/*     */   protected void setStartTimeSec(double startTimeSec) {
/*  72 */     this.startTimeSec = startTimeSec;
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
/*     */   public EncodableHistogram nextIntervalHistogram(Double startTimeSec, Double endTimeSec)
/*     */   {
/* 103 */     return nextIntervalHistogram(startTimeSec, endTimeSec, false);
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
/*     */   public EncodableHistogram nextAbsoluteIntervalHistogram(Double absoluteStartTimeSec, Double absoluteEndTimeSec)
/*     */   {
/* 137 */     return nextIntervalHistogram(absoluteStartTimeSec, absoluteEndTimeSec, true);
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
/* 149 */     return nextIntervalHistogram(Double.valueOf(0.0D), Double.valueOf(9.223372036854776E18D), true);
/*     */   }
/*     */   
/*     */   private EncodableHistogram nextIntervalHistogram(Double rangeStartTimeSec, Double rangeEndTimeSec, boolean absolute)
/*     */   {
/* 154 */     while (this.scanner.hasNextLine()) {
/*     */       try {
/* 156 */         if (this.scanner.hasNext("\\#.*"))
/*     */         {
/* 158 */           if (this.scanner.hasNext("#\\[StartTime:")) {
/* 159 */             this.scanner.next("#\\[StartTime:");
/* 160 */             if (this.scanner.hasNextDouble()) {
/* 161 */               setStartTimeSec(this.scanner.nextDouble());
/*     */             }
/*     */           }
/* 164 */           this.scanner.nextLine();
/*     */ 
/*     */ 
/*     */         }
/* 168 */         else if (this.scanner.hasNext("\"StartTimestamp\".*"))
/*     */         {
/* 170 */           this.scanner.nextLine();
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 176 */           double offsetStartTimeStampSec = this.scanner.nextDouble();
/* 177 */           double absoluteStartTimeStampSec = getStartTimeSec() + offsetStartTimeStampSec;
/*     */           
/* 179 */           double intervalLengthSec = this.scanner.nextDouble();
/* 180 */           double offsetEndTimeStampSec = offsetStartTimeStampSec + intervalLengthSec;
/* 181 */           double absoluteEndTimeStampSec = getStartTimeSec() + offsetEndTimeStampSec;
/*     */           
/* 183 */           double startTimeStampToCheckRangeOn = absolute ? absoluteStartTimeStampSec : offsetStartTimeStampSec;
/*     */           
/* 185 */           if (startTimeStampToCheckRangeOn < rangeStartTimeSec.doubleValue()) {
/* 186 */             this.scanner.nextLine();
/*     */           }
/*     */           else
/*     */           {
/* 190 */             if (startTimeStampToCheckRangeOn > rangeEndTimeSec.doubleValue()) {
/* 191 */               return null;
/*     */             }
/*     */             
/* 194 */             this.scanner.nextDouble();
/* 195 */             String compressedPayloadString = this.scanner.next();
/* 196 */             ByteBuffer buffer = ByteBuffer.wrap(
/* 197 */               DatatypeConverter.parseBase64Binary(compressedPayloadString));
/*     */             
/* 199 */             EncodableHistogram histogram = Histogram.decodeFromCompressedByteBuffer(buffer, 0L);
/*     */             
/* 201 */             histogram.setStartTimeStamp((absoluteStartTimeStampSec * 1000.0D));
/* 202 */             histogram.setEndTimeStamp((absoluteEndTimeStampSec * 1000.0D));
/*     */             
/* 204 */             return histogram;
/*     */           }
/*     */         }
/* 207 */       } catch (NoSuchElementException ex) { return null;
/*     */       } catch (DataFormatException ex) {
/* 209 */         return null;
/*     */       }
/*     */     }
/* 212 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AbstractHistogramLogReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */