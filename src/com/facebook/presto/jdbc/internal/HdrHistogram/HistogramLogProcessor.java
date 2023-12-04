/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HistogramLogProcessor
/*     */   extends Thread
/*     */ {
/*     */   public static final String versionString = "Histogram Log Processor version 2.1.9";
/*     */   private final HistogramLogProcessorConfiguration config;
/*     */   private HistogramLogReader logReader;
/*     */   
/*     */   private static class HistogramLogProcessorConfiguration
/*     */   {
/*  61 */     public boolean verbose = false;
/*  62 */     public String outputFileName = null;
/*  63 */     public String inputFileName = null;
/*  64 */     public String tag = null;
/*     */     
/*  66 */     public double rangeStartTimeSec = 0.0D;
/*  67 */     public double rangeEndTimeSec = Double.MAX_VALUE;
/*     */     
/*  69 */     public boolean logFormatCsv = false;
/*  70 */     public boolean listTags = false;
/*  71 */     public boolean allTags = false;
/*     */     
/*  73 */     public int percentilesOutputTicksPerHalf = 5;
/*  74 */     public Double outputValueUnitRatio = Double.valueOf(1000000.0D);
/*     */     
/*  76 */     public boolean error = false;
/*  77 */     public String errorMessage = "";
/*     */     
/*     */     public HistogramLogProcessorConfiguration(String[] args) {
/*  80 */       boolean askedForHelp = false;
/*     */       try {
/*  82 */         for (int i = 0; i < args.length; i++) {
/*  83 */           if (args[i].equals("-csv")) {
/*  84 */             this.logFormatCsv = true;
/*  85 */           } else if (args[i].equals("-v")) {
/*  86 */             this.verbose = true;
/*  87 */           } else if (args[i].equals("-listtags")) {
/*  88 */             this.listTags = true;
/*  89 */           } else if (args[i].equals("-alltags")) {
/*  90 */             this.allTags = true;
/*  91 */           } else if (args[i].equals("-i")) {
/*  92 */             this.inputFileName = args[(++i)];
/*  93 */           } else if (args[i].equals("-tag")) {
/*  94 */             this.tag = args[(++i)];
/*  95 */           } else if (args[i].equals("-start")) {
/*  96 */             this.rangeStartTimeSec = Double.parseDouble(args[(++i)]);
/*  97 */           } else if (args[i].equals("-end")) {
/*  98 */             this.rangeEndTimeSec = Double.parseDouble(args[(++i)]);
/*  99 */           } else if (args[i].equals("-o")) {
/* 100 */             this.outputFileName = args[(++i)];
/* 101 */           } else if (args[i].equals("-percentilesOutputTicksPerHalf")) {
/* 102 */             this.percentilesOutputTicksPerHalf = Integer.parseInt(args[(++i)]);
/* 103 */           } else if (args[i].equals("-outputValueUnitRatio")) {
/* 104 */             this.outputValueUnitRatio = Double.valueOf(Double.parseDouble(args[(++i)]));
/* 105 */           } else { if (args[i].equals("-h")) {
/* 106 */               askedForHelp = true;
/* 107 */               throw new Exception("Help: " + args[i]);
/*     */             }
/* 109 */             throw new Exception("Invalid args: " + args[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 114 */         this.error = true;
/* 115 */         this.errorMessage = "Error: Histogram Log Processor version 2.1.9 launched with the following args:\n";
/*     */         
/* 117 */         for (String arg : args) {
/* 118 */           this.errorMessage = (this.errorMessage + arg + " ");
/*     */         }
/* 120 */         if (!askedForHelp) {
/* 121 */           this.errorMessage = (this.errorMessage + "\nWhich was parsed as an error, indicated by the following exception:\n" + e);
/* 122 */           System.err.println(this.errorMessage);
/*     */         }
/*     */         
/* 125 */         String validArgs = "\"[-csv] [-v] [-i inputFileName] [-o outputFileName] [-tag tag] [-start rangeStartTimeSec] [-end rangeEndTimeSec] [-outputValueUnitRatio r] [-listtags]";
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 130 */         System.err.println("valid arguments = \"[-csv] [-v] [-i inputFileName] [-o outputFileName] [-tag tag] [-start rangeStartTimeSec] [-end rangeEndTimeSec] [-outputValueUnitRatio r] [-listtags]");
/*     */         
/* 132 */         System.err.println(" [-h]                        help\n [-v]                        Provide verbose error output\n [-csv]                      Use CSV format for output log files\n [-i logFileName]            File name of Histogram Log to process (default is standard input)\n [-o outputFileName]         File name to output to (default is standard output)\n [-tag tag]                  The tag (default no tag) of the histogram lines to be processed\n [-start rangeStartTimeSec]  The start time for the range in the file, in seconds (default 0.0)\n [-end rangeEndTimeSec]      The end time for the range in the file, in seconds (default is infinite)\n [-outputValueUnitRatio r]   The scaling factor by which to divide histogram recorded values units\n                             in output. [default = 1000000.0 (1 msec in nsec)]\n [-listtags]                 list all tags found on histogram lines the input file.");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */         System.exit(1);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void outputTimeRange(PrintStream log, String title) {
/* 151 */     log.format(Locale.US, "#[%s between %.3f and", new Object[] { title, Double.valueOf(this.config.rangeStartTimeSec) });
/* 152 */     if (this.config.rangeEndTimeSec < Double.MAX_VALUE) {
/* 153 */       log.format(" %.3f", new Object[] { Double.valueOf(this.config.rangeEndTimeSec) });
/*     */     } else {
/* 155 */       log.format(" %s", new Object[] { "<Infinite>" });
/*     */     }
/* 157 */     log.format(" seconds (relative to StartTime)]\n", new Object[0]);
/*     */   }
/*     */   
/*     */   private void outputStartTime(PrintStream log, Double startTime) {
/* 161 */     log.format(Locale.US, "#[StartTime: %.3f (seconds since epoch), %s]\n", new Object[] { startTime, new Date(
/* 162 */       (startTime.doubleValue() * 1000.0D)).toString() });
/*     */   }
/*     */   
/* 165 */   int lineNumber = 0;
/*     */   
/*     */   private EncodableHistogram getIntervalHistogram() {
/* 168 */     EncodableHistogram histogram = null;
/*     */     try {
/* 170 */       histogram = this.logReader.nextIntervalHistogram(this.config.rangeStartTimeSec, this.config.rangeEndTimeSec);
/*     */     } catch (RuntimeException ex) {
/* 172 */       System.err.println("Log file parsing error at line number " + this.lineNumber + ": line appears to be malformed.");
/*     */       
/* 174 */       if (this.config.verbose) {
/* 175 */         throw ex;
/*     */       }
/* 177 */       System.exit(1);
/*     */     }
/*     */     
/* 180 */     this.lineNumber += 1;
/* 181 */     return histogram;
/*     */   }
/*     */   
/*     */   private EncodableHistogram getIntervalHistogram(String tag) {
/*     */     EncodableHistogram histogram;
/* 186 */     if (tag == null) {
/*     */       EncodableHistogram histogram;
/* 188 */       do { histogram = getIntervalHistogram();
/* 189 */         if (histogram == null) break; } while (histogram.getTag() != null);
/*     */     } else {
/*     */       do {
/* 192 */         histogram = getIntervalHistogram();
/* 193 */       } while ((histogram != null) && (!tag.equals(histogram.getTag())));
/*     */     }
/* 195 */     return histogram;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/* 203 */     PrintStream timeIntervalLog = null;
/* 204 */     PrintStream histogramPercentileLog = System.out;
/* 205 */     Double firstStartTime = Double.valueOf(0.0D);
/* 206 */     boolean timeIntervalLogLegendWritten = false;
/*     */     
/* 208 */     if (this.config.listTags) {
/* 209 */       Set<String> tags = new TreeSet();
/*     */       
/* 211 */       boolean nullTagFound = false;
/* 212 */       EncodableHistogram histogram; String tag; while ((histogram = getIntervalHistogram()) != null) {
/* 213 */         tag = histogram.getTag();
/* 214 */         if (tag != null) {
/* 215 */           tags.add(histogram.getTag());
/*     */         } else {
/* 217 */           nullTagFound = true;
/*     */         }
/*     */       }
/* 220 */       System.out.println("Tags found in input file:");
/* 221 */       if (nullTagFound) {
/* 222 */         System.out.println("[NO TAG (default)]");
/*     */       }
/* 224 */       for (String tag : tags) {
/* 225 */         System.out.println(tag);
/*     */       }
/*     */       
/*     */       return;
/*     */     }
/*     */     String logFormat;
/*     */     String logFormat;
/* 232 */     if (this.config.logFormatCsv) {
/* 233 */       logFormat = "%.3f,%d,%.3f,%.3f,%.3f,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n";
/*     */     } else {
/* 235 */       logFormat = "%4.3f: I:%d ( %7.3f %7.3f %7.3f ) T:%d ( %7.3f %7.3f %7.3f %7.3f %7.3f %7.3f )\n";
/*     */     }
/*     */     try
/*     */     {
/* 239 */       if (this.config.outputFileName != null) {
/*     */         try {
/* 241 */           timeIntervalLog = new PrintStream(new FileOutputStream(this.config.outputFileName), false);
/* 242 */           outputTimeRange(timeIntervalLog, "Interval percentile log");
/*     */         } catch (FileNotFoundException ex) {
/* 244 */           System.err.println("Failed to open output file " + this.config.outputFileName);
/*     */         }
/* 246 */         String hgrmOutputFileName = this.config.outputFileName + ".hgrm";
/*     */         try {
/* 248 */           histogramPercentileLog = new PrintStream(new FileOutputStream(hgrmOutputFileName), false);
/* 249 */           outputTimeRange(histogramPercentileLog, "Overall percentile distribution");
/*     */         } catch (FileNotFoundException ex) {
/* 251 */           System.err.println("Failed to open percentiles histogram output file " + hgrmOutputFileName);
/*     */         }
/*     */       }
/*     */       
/* 255 */       EncodableHistogram intervalHistogram = getIntervalHistogram(this.config.tag);
/*     */       
/* 257 */       Histogram accumulatedRegularHistogram = null;
/* 258 */       DoubleHistogram accumulatedDoubleHistogram = null;
/*     */       
/* 260 */       if (intervalHistogram != null)
/*     */       {
/* 262 */         if ((intervalHistogram instanceof DoubleHistogram)) {
/* 263 */           accumulatedDoubleHistogram = ((DoubleHistogram)intervalHistogram).copy();
/* 264 */           accumulatedDoubleHistogram.reset();
/* 265 */           accumulatedDoubleHistogram.setAutoResize(true);
/*     */         } else {
/* 267 */           accumulatedRegularHistogram = ((Histogram)intervalHistogram).copy();
/* 268 */           accumulatedRegularHistogram.reset();
/* 269 */           accumulatedRegularHistogram.setAutoResize(true);
/*     */         }
/*     */       }
/*     */       
/* 273 */       while (intervalHistogram != null) {
/* 274 */         if ((intervalHistogram instanceof DoubleHistogram)) {
/* 275 */           if (accumulatedDoubleHistogram == null) {
/* 276 */             throw new IllegalStateException("Encountered a DoubleHistogram line in a log of Histograms.");
/*     */           }
/* 278 */           accumulatedDoubleHistogram.add((DoubleHistogram)intervalHistogram);
/*     */         } else {
/* 280 */           if (accumulatedRegularHistogram == null) {
/* 281 */             throw new IllegalStateException("Encountered a Histogram line in a log of DoubleHistograms.");
/*     */           }
/* 283 */           accumulatedRegularHistogram.add((Histogram)intervalHistogram);
/*     */         }
/*     */         
/* 286 */         if ((firstStartTime.doubleValue() == 0.0D) && (this.logReader.getStartTimeSec() != 0.0D)) {
/* 287 */           firstStartTime = Double.valueOf(this.logReader.getStartTimeSec());
/*     */           
/* 289 */           outputStartTime(histogramPercentileLog, firstStartTime);
/*     */           
/* 291 */           if (timeIntervalLog != null) {
/* 292 */             outputStartTime(timeIntervalLog, firstStartTime);
/*     */           }
/*     */         }
/*     */         
/* 296 */         if (timeIntervalLog != null) {
/* 297 */           if (!timeIntervalLogLegendWritten) {
/* 298 */             timeIntervalLogLegendWritten = true;
/* 299 */             if (this.config.logFormatCsv) {
/* 300 */               timeIntervalLog.println("\"Timestamp\",\"Int_Count\",\"Int_50%\",\"Int_90%\",\"Int_Max\",\"Total_Count\",\"Total_50%\",\"Total_90%\",\"Total_99%\",\"Total_99.9%\",\"Total_99.99%\",\"Total_Max\"");
/*     */             }
/*     */             else {
/* 303 */               timeIntervalLog.println("Time: IntervalPercentiles:count ( 50% 90% Max ) TotalPercentiles:count ( 50% 90% 99% 99.9% 99.99% Max )");
/*     */             }
/*     */           }
/*     */           
/* 307 */           if ((intervalHistogram instanceof DoubleHistogram)) {
/* 308 */             timeIntervalLog.format(Locale.US, logFormat, new Object[] {
/* 309 */               Double.valueOf(intervalHistogram.getEndTimeStamp() / 1000.0D - this.logReader.getStartTimeSec()), 
/*     */               
/* 311 */               Long.valueOf(((DoubleHistogram)intervalHistogram).getTotalCount()), 
/* 312 */               Double.valueOf(((DoubleHistogram)intervalHistogram).getValueAtPercentile(50.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 313 */               Double.valueOf(((DoubleHistogram)intervalHistogram).getValueAtPercentile(90.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 314 */               Double.valueOf(((DoubleHistogram)intervalHistogram).getMaxValue() / this.config.outputValueUnitRatio.doubleValue()), 
/*     */               
/* 316 */               Long.valueOf(accumulatedDoubleHistogram.getTotalCount()), 
/* 317 */               Double.valueOf(accumulatedDoubleHistogram.getValueAtPercentile(50.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 318 */               Double.valueOf(accumulatedDoubleHistogram.getValueAtPercentile(90.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 319 */               Double.valueOf(accumulatedDoubleHistogram.getValueAtPercentile(99.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 320 */               Double.valueOf(accumulatedDoubleHistogram.getValueAtPercentile(99.9D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 321 */               Double.valueOf(accumulatedDoubleHistogram.getValueAtPercentile(99.99D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 322 */               Double.valueOf(accumulatedDoubleHistogram.getMaxValue() / this.config.outputValueUnitRatio.doubleValue()) });
/*     */           }
/*     */           else {
/* 325 */             timeIntervalLog.format(Locale.US, logFormat, new Object[] {
/* 326 */               Double.valueOf(intervalHistogram.getEndTimeStamp() / 1000.0D - this.logReader.getStartTimeSec()), 
/*     */               
/* 328 */               Long.valueOf(((Histogram)intervalHistogram).getTotalCount()), 
/* 329 */               Double.valueOf(((Histogram)intervalHistogram).getValueAtPercentile(50.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 330 */               Double.valueOf(((Histogram)intervalHistogram).getValueAtPercentile(90.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 331 */               Double.valueOf(((Histogram)intervalHistogram).getMaxValue() / this.config.outputValueUnitRatio.doubleValue()), 
/*     */               
/* 333 */               Long.valueOf(accumulatedRegularHistogram.getTotalCount()), 
/* 334 */               Double.valueOf(accumulatedRegularHistogram.getValueAtPercentile(50.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 335 */               Double.valueOf(accumulatedRegularHistogram.getValueAtPercentile(90.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 336 */               Double.valueOf(accumulatedRegularHistogram.getValueAtPercentile(99.0D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 337 */               Double.valueOf(accumulatedRegularHistogram.getValueAtPercentile(99.9D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 338 */               Double.valueOf(accumulatedRegularHistogram.getValueAtPercentile(99.99D) / this.config.outputValueUnitRatio.doubleValue()), 
/* 339 */               Double.valueOf(accumulatedRegularHistogram.getMaxValue() / this.config.outputValueUnitRatio.doubleValue()) });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 344 */         intervalHistogram = getIntervalHistogram(this.config.tag);
/*     */       }
/*     */       
/* 347 */       if (accumulatedDoubleHistogram != null) {
/* 348 */         accumulatedDoubleHistogram.outputPercentileDistribution(histogramPercentileLog, this.config.percentilesOutputTicksPerHalf, this.config.outputValueUnitRatio, this.config.logFormatCsv);
/*     */       }
/*     */       else {
/* 351 */         if (accumulatedRegularHistogram == null)
/*     */         {
/*     */ 
/* 354 */           accumulatedRegularHistogram = new Histogram(1000000L, 2);
/*     */         }
/* 356 */         accumulatedRegularHistogram.outputPercentileDistribution(histogramPercentileLog, this.config.percentilesOutputTicksPerHalf, this.config.outputValueUnitRatio, this.config.logFormatCsv);
/*     */       }
/*     */     }
/*     */     finally {
/* 360 */       if (this.config.outputFileName != null) {
/* 361 */         timeIntervalLog.close();
/* 362 */         histogramPercentileLog.close();
/*     */       }
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
/*     */   public HistogramLogProcessor(String[] args)
/*     */     throws FileNotFoundException
/*     */   {
/* 385 */     setName("HistogramLogProcessor");
/* 386 */     this.config = new HistogramLogProcessorConfiguration(args);
/* 387 */     if (this.config.inputFileName != null) {
/* 388 */       this.logReader = new HistogramLogReader(this.config.inputFileName);
/*     */     } else {
/* 390 */       this.logReader = new HistogramLogReader(System.in);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 402 */       HistogramLogProcessor processor = new HistogramLogProcessor(args);
/* 403 */       processor.start();
/*     */     } catch (FileNotFoundException ex) {
/* 405 */       System.err.println("failed to open input file.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\HistogramLogProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */