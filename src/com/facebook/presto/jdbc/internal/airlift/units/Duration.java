/*     */ package com.facebook.presto.jdbc.internal.airlift.units;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Duration
/*     */   implements Comparable<Duration>
/*     */ {
/*  39 */   private static final Pattern PATTERN = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)\\s*$");
/*     */   private final double value;
/*     */   private final TimeUnit unit;
/*     */   
/*  43 */   public static Duration nanosSince(long start) { return succinctNanos(System.nanoTime() - start); }
/*     */   
/*     */ 
/*     */   public static Duration succinctNanos(long nanos)
/*     */   {
/*  48 */     return succinctDuration(nanos, TimeUnit.NANOSECONDS);
/*     */   }
/*     */   
/*     */   public static Duration succinctDuration(double value, TimeUnit unit)
/*     */   {
/*  53 */     return new Duration(value, unit).convertToMostSuccinctTimeUnit();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration(double value, TimeUnit unit)
/*     */   {
/*  61 */     Preconditions.checkArgument(!Double.isInfinite(value), "value is infinite");
/*  62 */     Preconditions.checkArgument(!Double.isNaN(value), "value is not a number");
/*  63 */     Preconditions.checkArgument(value >= 0.0D, "value is negative");
/*  64 */     Objects.requireNonNull(unit, "unit is null");
/*     */     
/*  66 */     this.value = value;
/*  67 */     this.unit = unit;
/*     */   }
/*     */   
/*     */   public long toMillis()
/*     */   {
/*  72 */     return roundTo(TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   public double getValue()
/*     */   {
/*  77 */     return this.value;
/*     */   }
/*     */   
/*     */   public TimeUnit getUnit()
/*     */   {
/*  82 */     return this.unit;
/*     */   }
/*     */   
/*     */   public double getValue(TimeUnit timeUnit)
/*     */   {
/*  87 */     Objects.requireNonNull(timeUnit, "timeUnit is null");
/*  88 */     return this.value * (millisPerTimeUnit(this.unit) * 1.0D / millisPerTimeUnit(timeUnit));
/*     */   }
/*     */   
/*     */   public long roundTo(TimeUnit timeUnit)
/*     */   {
/*  93 */     Objects.requireNonNull(timeUnit, "timeUnit is null");
/*  94 */     double rounded = Math.floor(getValue(timeUnit) + 0.5D);
/*  95 */     Preconditions.checkArgument(rounded <= 9.223372036854776E18D, "size is too large to be represented in requested unit as a long");
/*     */     
/*  97 */     return rounded;
/*     */   }
/*     */   
/*     */   public Duration convertTo(TimeUnit timeUnit)
/*     */   {
/* 102 */     Objects.requireNonNull(timeUnit, "timeUnit is null");
/* 103 */     return new Duration(getValue(timeUnit), timeUnit);
/*     */   }
/*     */   
/*     */   public Duration convertToMostSuccinctTimeUnit()
/*     */   {
/* 108 */     TimeUnit unitToUse = TimeUnit.NANOSECONDS;
/* 109 */     for (TimeUnit unitToTest : TimeUnit.values())
/*     */     {
/* 111 */       if (getValue(unitToTest) <= 0.9999D) break;
/* 112 */       unitToUse = unitToTest;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 118 */     return convertTo(unitToUse);
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonValue
/*     */   public String toString()
/*     */   {
/* 125 */     return toString(this.unit);
/*     */   }
/*     */   
/*     */   public String toString(TimeUnit timeUnit)
/*     */   {
/* 130 */     Objects.requireNonNull(timeUnit, "timeUnit is null");
/* 131 */     double magnitude = getValue(timeUnit);
/* 132 */     String timeUnitAbbreviation = timeUnitToString(timeUnit);
/* 133 */     return String.format(Locale.ENGLISH, "%.2f%s", new Object[] { Double.valueOf(magnitude), timeUnitAbbreviation });
/*     */   }
/*     */   
/*     */   @JsonCreator
/*     */   public static Duration valueOf(String duration)
/*     */     throws IllegalArgumentException
/*     */   {
/* 140 */     Objects.requireNonNull(duration, "duration is null");
/* 141 */     Preconditions.checkArgument(!duration.isEmpty(), "duration is empty");
/*     */     
/* 143 */     Matcher matcher = PATTERN.matcher(duration);
/* 144 */     if (!matcher.matches()) {
/* 145 */       throw new IllegalArgumentException("duration is not a valid data duration string: " + duration);
/*     */     }
/*     */     
/* 148 */     double value = Double.parseDouble(matcher.group(1));
/* 149 */     String unitString = matcher.group(2);
/*     */     
/* 151 */     TimeUnit timeUnit = valueOfTimeUnit(unitString);
/* 152 */     return new Duration(value, timeUnit);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Duration o)
/*     */   {
/* 158 */     return Double.compare(getValue(TimeUnit.MILLISECONDS), o.getValue(TimeUnit.MILLISECONDS));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 164 */     if (this == o) {
/* 165 */       return true;
/*     */     }
/* 167 */     if ((o == null) || (getClass() != o.getClass())) {
/* 168 */       return false;
/*     */     }
/*     */     
/* 171 */     Duration duration = (Duration)o;
/*     */     
/* 173 */     return compareTo(duration) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 179 */     double value = getValue(TimeUnit.MILLISECONDS);
/* 180 */     return Double.hashCode(value);
/*     */   }
/*     */   
/*     */   public static TimeUnit valueOfTimeUnit(String timeUnitString)
/*     */   {
/* 185 */     Objects.requireNonNull(timeUnitString, "timeUnitString is null");
/* 186 */     switch (timeUnitString) {
/*     */     case "ns": 
/* 188 */       return TimeUnit.NANOSECONDS;
/*     */     case "us": 
/* 190 */       return TimeUnit.MICROSECONDS;
/*     */     case "ms": 
/* 192 */       return TimeUnit.MILLISECONDS;
/*     */     case "s": 
/* 194 */       return TimeUnit.SECONDS;
/*     */     case "m": 
/* 196 */       return TimeUnit.MINUTES;
/*     */     case "h": 
/* 198 */       return TimeUnit.HOURS;
/*     */     case "d": 
/* 200 */       return TimeUnit.DAYS;
/*     */     }
/* 202 */     throw new IllegalArgumentException("Unknown time unit: " + timeUnitString);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String timeUnitToString(TimeUnit timeUnit)
/*     */   {
/* 208 */     Objects.requireNonNull(timeUnit, "timeUnit is null");
/* 209 */     switch (timeUnit) {
/*     */     case NANOSECONDS: 
/* 211 */       return "ns";
/*     */     case MICROSECONDS: 
/* 213 */       return "us";
/*     */     case MILLISECONDS: 
/* 215 */       return "ms";
/*     */     case SECONDS: 
/* 217 */       return "s";
/*     */     case MINUTES: 
/* 219 */       return "m";
/*     */     case HOURS: 
/* 221 */       return "h";
/*     */     case DAYS: 
/* 223 */       return "d";
/*     */     }
/* 225 */     throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
/*     */   }
/*     */   
/*     */ 
/*     */   private static double millisPerTimeUnit(TimeUnit timeUnit)
/*     */   {
/* 231 */     switch (timeUnit) {
/*     */     case NANOSECONDS: 
/* 233 */       return 1.0E-6D;
/*     */     case MICROSECONDS: 
/* 235 */       return 0.001D;
/*     */     case MILLISECONDS: 
/* 237 */       return 1.0D;
/*     */     case SECONDS: 
/* 239 */       return 1000.0D;
/*     */     case MINUTES: 
/* 241 */       return 60000.0D;
/*     */     case HOURS: 
/* 243 */       return 3600000.0D;
/*     */     case DAYS: 
/* 245 */       return 8.64E7D;
/*     */     }
/* 247 */     throw new IllegalArgumentException("Unsupported time unit " + timeUnit);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\Duration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */