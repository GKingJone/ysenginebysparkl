/*     */ package com.facebook.presto.jdbc.internal.airlift.units;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*     */ import java.util.Locale;
/*     */ import java.util.Objects;
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
/*     */ public class DataSize
/*     */   implements Comparable<DataSize>
/*     */ {
/*  33 */   private static final Pattern PATTERN = Pattern.compile("^\\s*(\\d+(?:\\.\\d+)?)\\s*([a-zA-Z]+)\\s*$");
/*     */   private final double value;
/*     */   private final Unit unit;
/*     */   
/*  37 */   public static DataSize succinctBytes(long bytes) { return succinctDataSize(bytes, Unit.BYTE); }
/*     */   
/*     */ 
/*     */   public static DataSize succinctDataSize(double size, Unit unit)
/*     */   {
/*  42 */     return new DataSize(size, unit).convertToMostSuccinctDataSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DataSize(double size, Unit unit)
/*     */   {
/*  50 */     Preconditions.checkArgument(!Double.isInfinite(size), "size is infinite");
/*  51 */     Preconditions.checkArgument(!Double.isNaN(size), "size is not a number");
/*  52 */     Preconditions.checkArgument(size >= 0.0D, "size is negative");
/*  53 */     Objects.requireNonNull(unit, "unit is null");
/*     */     
/*  55 */     this.value = size;
/*  56 */     this.unit = unit;
/*     */   }
/*     */   
/*     */   public long toBytes()
/*     */   {
/*  61 */     double bytes = getValue(Unit.BYTE);
/*  62 */     Preconditions.checkState(bytes <= 9.223372036854776E18D, "size is too large to be represented in bytes as a long");
/*  63 */     return bytes;
/*     */   }
/*     */   
/*     */   public double getValue()
/*     */   {
/*  68 */     return this.value;
/*     */   }
/*     */   
/*     */   public Unit getUnit()
/*     */   {
/*  73 */     return this.unit;
/*     */   }
/*     */   
/*     */   public double getValue(Unit unit)
/*     */   {
/*  78 */     return this.value * (this.unit.getFactor() * 1.0D / unit.getFactor());
/*     */   }
/*     */   
/*     */   public long roundTo(Unit unit)
/*     */   {
/*  83 */     double rounded = Math.floor(getValue(unit) + 0.5D);
/*  84 */     Preconditions.checkArgument(rounded <= 9.223372036854776E18D, "size is too large to be represented in requested unit as a long");
/*     */     
/*  86 */     return rounded;
/*     */   }
/*     */   
/*     */   public DataSize convertTo(Unit unit)
/*     */   {
/*  91 */     Objects.requireNonNull(unit, "unit is null");
/*  92 */     return new DataSize(getValue(unit), unit);
/*     */   }
/*     */   
/*     */   public DataSize convertToMostSuccinctDataSize()
/*     */   {
/*  97 */     Unit unitToUse = Unit.BYTE;
/*  98 */     for (Unit unitToTest : Unit.values()) {
/*  99 */       if (getValue(unitToTest) < 1.0D) break;
/* 100 */       unitToUse = unitToTest;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 106 */     return convertTo(unitToUse);
/*     */   }
/*     */   
/*     */ 
/*     */   @JsonValue
/*     */   public String toString()
/*     */   {
/* 113 */     if (Math.floor(this.value) == this.value) {
/* 114 */       return Math.floor(this.value) + this.unit.getUnitString();
/*     */     }
/*     */     
/* 117 */     return String.format(Locale.ENGLISH, "%.2f%s", new Object[] { Double.valueOf(this.value), this.unit.getUnitString() });
/*     */   }
/*     */   
/*     */   @JsonCreator
/*     */   public static DataSize valueOf(String size)
/*     */     throws IllegalArgumentException
/*     */   {
/* 124 */     Objects.requireNonNull(size, "size is null");
/* 125 */     Preconditions.checkArgument(!size.isEmpty(), "size is empty");
/*     */     
/* 127 */     Matcher matcher = PATTERN.matcher(size);
/* 128 */     if (!matcher.matches()) {
/* 129 */       throw new IllegalArgumentException("size is not a valid data size string: " + size);
/*     */     }
/*     */     
/* 132 */     double value = Double.parseDouble(matcher.group(1));
/* 133 */     String unitString = matcher.group(2);
/*     */     
/* 135 */     for (Unit unit : Unit.values()) {
/* 136 */       if (unit.getUnitString().equals(unitString)) {
/* 137 */         return new DataSize(value, unit);
/*     */       }
/*     */     }
/*     */     
/* 141 */     throw new IllegalArgumentException("Unknown unit: " + unitString);
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(DataSize o)
/*     */   {
/* 147 */     return Double.compare(getValue(Unit.BYTE), o.getValue(Unit.BYTE));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 153 */     if (this == o) {
/* 154 */       return true;
/*     */     }
/* 156 */     if ((o == null) || (getClass() != o.getClass())) {
/* 157 */       return false;
/*     */     }
/*     */     
/* 160 */     DataSize dataSize = (DataSize)o;
/*     */     
/* 162 */     return compareTo(dataSize) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 168 */     double value = getValue(Unit.BYTE);
/* 169 */     return Double.hashCode(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public static enum Unit
/*     */   {
/* 175 */     BYTE(1L, "B"), 
/* 176 */     KILOBYTE(1024L, "kB"), 
/* 177 */     MEGABYTE(1048576L, "MB"), 
/* 178 */     GIGABYTE(1073741824L, "GB"), 
/* 179 */     TERABYTE(1099511627776L, "TB"), 
/* 180 */     PETABYTE(1125899906842624L, "PB");
/*     */     
/*     */     private final long factor;
/*     */     private final String unitString;
/*     */     
/*     */     private Unit(long factor, String unitString)
/*     */     {
/* 187 */       this.factor = factor;
/* 188 */       this.unitString = unitString;
/*     */     }
/*     */     
/*     */     long getFactor()
/*     */     {
/* 193 */       return this.factor;
/*     */     }
/*     */     
/*     */     public String getUnitString()
/*     */     {
/* 198 */       return this.unitString;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\units\DataSize.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */