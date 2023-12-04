/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class HistogramLogWriter
/*     */ {
/*     */   private static final String HISTOGRAM_LOG_FORMAT_VERSION = "1.3";
/*  51 */   private static Pattern containsDelimeterPattern = Pattern.compile(".[, \\r\\n].");
/*  52 */   private Matcher containsDelimeterMatcher = containsDelimeterPattern.matcher("");
/*     */   
/*     */   private final PrintStream log;
/*     */   
/*     */   private ByteBuffer targetBuffer;
/*     */   
/*  58 */   private long baseTime = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogWriter(String outputFileName)
/*     */     throws FileNotFoundException
/*     */   {
/*  66 */     this.log = new PrintStream(outputFileName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogWriter(File outputFile)
/*     */     throws FileNotFoundException
/*     */   {
/*  75 */     this.log = new PrintStream(outputFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogWriter(OutputStream outputStream)
/*     */   {
/*  83 */     this.log = new PrintStream(outputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramLogWriter(PrintStream printStream)
/*     */   {
/*  91 */     this.log = printStream;
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
/*     */   public synchronized void outputIntervalHistogram(double startTimeStampSec, double endTimeStampSec, EncodableHistogram histogram, double maxValueUnitRatio)
/*     */   {
/* 109 */     if ((this.targetBuffer == null) || (this.targetBuffer.capacity() < histogram.getNeededByteBufferCapacity())) {
/* 110 */       this.targetBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity()).order(ByteOrder.BIG_ENDIAN);
/*     */     }
/* 112 */     this.targetBuffer.clear();
/*     */     
/* 114 */     int compressedLength = histogram.encodeIntoCompressedByteBuffer(this.targetBuffer, 9);
/* 115 */     byte[] compressedArray = Arrays.copyOf(this.targetBuffer.array(), compressedLength);
/*     */     
/* 117 */     String tag = histogram.getTag();
/* 118 */     if (tag == null) {
/* 119 */       this.log.format(Locale.US, "%.3f,%.3f,%.3f,%s\n", new Object[] {
/* 120 */         Double.valueOf(startTimeStampSec), 
/* 121 */         Double.valueOf(endTimeStampSec - startTimeStampSec), 
/* 122 */         Double.valueOf(histogram.getMaxValueAsDouble() / maxValueUnitRatio), 
/* 123 */         DatatypeConverter.printBase64Binary(compressedArray) });
/*     */     }
/*     */     else {
/* 126 */       this.containsDelimeterMatcher.reset(tag);
/* 127 */       if (this.containsDelimeterMatcher.matches()) {
/* 128 */         throw new IllegalArgumentException("Tag string cannot contain commas, spaces, or line breaks");
/*     */       }
/* 130 */       this.log.format(Locale.US, "Tag=%s,%.3f,%.3f,%.3f,%s\n", new Object[] { tag, 
/*     */       
/* 132 */         Double.valueOf(startTimeStampSec), 
/* 133 */         Double.valueOf(endTimeStampSec - startTimeStampSec), 
/* 134 */         Double.valueOf(histogram.getMaxValueAsDouble() / maxValueUnitRatio), 
/* 135 */         DatatypeConverter.printBase64Binary(compressedArray) });
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
/*     */   public void outputIntervalHistogram(double startTimeStampSec, double endTimeStampSec, EncodableHistogram histogram)
/*     */   {
/* 154 */     outputIntervalHistogram(startTimeStampSec, endTimeStampSec, histogram, 1000000.0D);
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
/*     */   public void outputIntervalHistogram(EncodableHistogram histogram)
/*     */   {
/* 175 */     outputIntervalHistogram((histogram.getStartTimeStamp() - this.baseTime) / 1000.0D, 
/* 176 */       (histogram.getEndTimeStamp() - this.baseTime) / 1000.0D, histogram);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void outputStartTime(long startTimeMsec)
/*     */   {
/* 185 */     this.log.format(Locale.US, "#[StartTime: %.3f (seconds since epoch), %s]\n", new Object[] {
/* 186 */       Double.valueOf(startTimeMsec / 1000.0D), new Date(startTimeMsec)
/* 187 */       .toString() });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void outputBaseTime(long baseTimeMsec)
/*     */   {
/* 196 */     this.log.format(Locale.US, "#[BaseTime: %.3f (seconds since epoch)]\n", new Object[] {
/* 197 */       Double.valueOf(baseTimeMsec / 1000.0D) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void outputComment(String comment)
/*     */   {
/* 206 */     this.log.format("#%s\n", new Object[] { comment });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void outputLegend()
/*     */   {
/* 213 */     this.log.println("\"StartTimestamp\",\"Interval_Length\",\"Interval_Max\",\"Interval_Compressed_Histogram\"");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void outputLogFormatVersion()
/*     */   {
/* 220 */     outputComment("[Histogram log format version 1.3]");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBaseTime(long baseTimeMsec)
/*     */   {
/* 231 */     this.baseTime = baseTimeMsec;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getBaseTime()
/*     */   {
/* 239 */     return this.baseTime;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\HistogramLogWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */