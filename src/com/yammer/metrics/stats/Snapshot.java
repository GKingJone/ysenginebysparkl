/*     */ package com.yammer.metrics.stats;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Snapshot
/*     */ {
/*     */   private static final double MEDIAN_Q = 0.5D;
/*     */   private static final double P75_Q = 0.75D;
/*     */   private static final double P95_Q = 0.95D;
/*     */   private static final double P98_Q = 0.98D;
/*     */   private static final double P99_Q = 0.99D;
/*     */   private static final double P999_Q = 0.999D;
/*     */   private final double[] values;
/*     */   
/*     */   public Snapshot(Collection<Long> values)
/*     */   {
/*  29 */     Object[] copy = values.toArray();
/*  30 */     this.values = new double[copy.length];
/*  31 */     for (int i = 0; i < copy.length; i++) {
/*  32 */       this.values[i] = ((Long)copy[i]).longValue();
/*     */     }
/*  34 */     Arrays.sort(this.values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Snapshot(double[] values)
/*     */   {
/*  43 */     this.values = new double[values.length];
/*  44 */     System.arraycopy(values, 0, this.values, 0, values.length);
/*  45 */     Arrays.sort(this.values);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getValue(double quantile)
/*     */   {
/*  55 */     if ((quantile < 0.0D) || (quantile > 1.0D)) {
/*  56 */       throw new IllegalArgumentException(quantile + " is not in [0..1]");
/*     */     }
/*     */     
/*  59 */     if (this.values.length == 0) {
/*  60 */       return 0.0D;
/*     */     }
/*     */     
/*  63 */     double pos = quantile * (this.values.length + 1);
/*     */     
/*  65 */     if (pos < 1.0D) {
/*  66 */       return this.values[0];
/*     */     }
/*     */     
/*  69 */     if (pos >= this.values.length) {
/*  70 */       return this.values[(this.values.length - 1)];
/*     */     }
/*     */     
/*  73 */     double lower = this.values[((int)pos - 1)];
/*  74 */     double upper = this.values[((int)pos)];
/*  75 */     return lower + (pos - Math.floor(pos)) * (upper - lower);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/*  84 */     return this.values.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double getMedian()
/*     */   {
/*  93 */     return getValue(0.5D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double get75thPercentile()
/*     */   {
/* 102 */     return getValue(0.75D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double get95thPercentile()
/*     */   {
/* 111 */     return getValue(0.95D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double get98thPercentile()
/*     */   {
/* 120 */     return getValue(0.98D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double get99thPercentile()
/*     */   {
/* 129 */     return getValue(0.99D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double get999thPercentile()
/*     */   {
/* 138 */     return getValue(0.999D);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double[] getValues()
/*     */   {
/* 147 */     return Arrays.copyOf(this.values, this.values.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dump(File output)
/*     */     throws IOException
/*     */   {
/* 157 */     PrintWriter writer = new PrintWriter(output);
/*     */     try {
/* 159 */       for (double value : this.values) {
/* 160 */         writer.printf("%f\n", new Object[] { Double.valueOf(value) });
/*     */       }
/*     */     } finally {
/* 163 */       writer.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\Snapshot.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */