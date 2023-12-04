/*     */ package com.facebook.presto.jdbc.internal.spi.predicate;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Range
/*     */ {
/*     */   private final Marker low;
/*     */   private final Marker high;
/*     */   
/*     */   @JsonCreator
/*     */   public Range(@JsonProperty("low") Marker low, @JsonProperty("high") Marker high)
/*     */   {
/*  38 */     Objects.requireNonNull(low, "value is null");
/*  39 */     Objects.requireNonNull(high, "value is null");
/*  40 */     if (!low.getType().equals(high.getType())) {
/*  41 */       throw new IllegalArgumentException(String.format("Marker types do not match: %s vs %s", new Object[] { low.getType(), high.getType() }));
/*     */     }
/*  43 */     if (low.getBound() == Marker.Bound.BELOW) {
/*  44 */       throw new IllegalArgumentException("low bound must be EXACTLY or ABOVE");
/*     */     }
/*  46 */     if (high.getBound() == Marker.Bound.ABOVE) {
/*  47 */       throw new IllegalArgumentException("high bound must be EXACTLY or BELOW");
/*     */     }
/*  49 */     if (low.compareTo(high) > 0) {
/*  50 */       throw new IllegalArgumentException("low must be less than or equal to high");
/*     */     }
/*  52 */     this.low = low;
/*  53 */     this.high = high;
/*     */   }
/*     */   
/*     */   public static Range all(Type type)
/*     */   {
/*  58 */     return new Range(Marker.lowerUnbounded(type), Marker.upperUnbounded(type));
/*     */   }
/*     */   
/*     */   public static Range greaterThan(Type type, Object low)
/*     */   {
/*  63 */     return new Range(Marker.above(type, low), Marker.upperUnbounded(type));
/*     */   }
/*     */   
/*     */   public static Range greaterThanOrEqual(Type type, Object low)
/*     */   {
/*  68 */     return new Range(Marker.exactly(type, low), Marker.upperUnbounded(type));
/*     */   }
/*     */   
/*     */   public static Range lessThan(Type type, Object high)
/*     */   {
/*  73 */     return new Range(Marker.lowerUnbounded(type), Marker.below(type, high));
/*     */   }
/*     */   
/*     */   public static Range lessThanOrEqual(Type type, Object high)
/*     */   {
/*  78 */     return new Range(Marker.lowerUnbounded(type), Marker.exactly(type, high));
/*     */   }
/*     */   
/*     */   public static Range equal(Type type, Object value)
/*     */   {
/*  83 */     return new Range(Marker.exactly(type, value), Marker.exactly(type, value));
/*     */   }
/*     */   
/*     */   public static Range range(Type type, Object low, boolean lowInclusive, Object high, boolean highInclusive)
/*     */   {
/*  88 */     Marker lowMarker = lowInclusive ? Marker.exactly(type, low) : Marker.above(type, low);
/*  89 */     Marker highMarker = highInclusive ? Marker.exactly(type, high) : Marker.below(type, high);
/*  90 */     return new Range(lowMarker, highMarker);
/*     */   }
/*     */   
/*     */   public Type getType()
/*     */   {
/*  95 */     return this.low.getType();
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Marker getLow()
/*     */   {
/* 101 */     return this.low;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Marker getHigh()
/*     */   {
/* 107 */     return this.high;
/*     */   }
/*     */   
/*     */   public boolean isSingleValue()
/*     */   {
/* 112 */     return (this.low.getBound() == Marker.Bound.EXACTLY) && (this.low.equals(this.high));
/*     */   }
/*     */   
/*     */   public Object getSingleValue()
/*     */   {
/* 117 */     if (!isSingleValue()) {
/* 118 */       throw new IllegalStateException("Range does not have just a single value");
/*     */     }
/* 120 */     return this.low.getValue();
/*     */   }
/*     */   
/*     */   public boolean isAll()
/*     */   {
/* 125 */     return (this.low.isLowerUnbounded()) && (this.high.isUpperUnbounded());
/*     */   }
/*     */   
/*     */   public boolean includes(Marker marker)
/*     */   {
/* 130 */     Objects.requireNonNull(marker, "marker is null");
/* 131 */     checkTypeCompatibility(marker);
/* 132 */     return (this.low.compareTo(marker) <= 0) && (this.high.compareTo(marker) >= 0);
/*     */   }
/*     */   
/*     */   public boolean contains(Range other)
/*     */   {
/* 137 */     checkTypeCompatibility(other);
/* 138 */     return (getLow().compareTo(other.getLow()) <= 0) && 
/* 139 */       (getHigh().compareTo(other.getHigh()) >= 0);
/*     */   }
/*     */   
/*     */   public Range span(Range other)
/*     */   {
/* 144 */     checkTypeCompatibility(other);
/* 145 */     Marker lowMarker = Marker.min(this.low, other.getLow());
/* 146 */     Marker highMarker = Marker.max(this.high, other.getHigh());
/* 147 */     return new Range(lowMarker, highMarker);
/*     */   }
/*     */   
/*     */   public boolean overlaps(Range other)
/*     */   {
/* 152 */     checkTypeCompatibility(other);
/* 153 */     return (getLow().compareTo(other.getHigh()) <= 0) && 
/* 154 */       (other.getLow().compareTo(getHigh()) <= 0);
/*     */   }
/*     */   
/*     */   public Range intersect(Range other)
/*     */   {
/* 159 */     checkTypeCompatibility(other);
/* 160 */     if (!overlaps(other)) {
/* 161 */       throw new IllegalArgumentException("Cannot intersect non-overlapping ranges");
/*     */     }
/* 163 */     Marker lowMarker = Marker.max(this.low, other.getLow());
/* 164 */     Marker highMarker = Marker.min(this.high, other.getHigh());
/* 165 */     return new Range(lowMarker, highMarker);
/*     */   }
/*     */   
/*     */   private void checkTypeCompatibility(Range range)
/*     */   {
/* 170 */     if (!getType().equals(range.getType())) {
/* 171 */       throw new IllegalArgumentException(String.format("Mismatched Range types: %s vs %s", new Object[] { getType(), range.getType() }));
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkTypeCompatibility(Marker marker)
/*     */   {
/* 177 */     if (!getType().equals(marker.getType())) {
/* 178 */       throw new IllegalArgumentException(String.format("Marker of %s does not match Range of %s", new Object[] { marker.getType(), getType() }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 185 */     return Objects.hash(new Object[] { this.low, this.high });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 191 */     if (this == obj) {
/* 192 */       return true;
/*     */     }
/* 194 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 195 */       return false;
/*     */     }
/* 197 */     Range other = (Range)obj;
/* 198 */     return (Objects.equals(this.low, other.low)) && 
/* 199 */       (Objects.equals(this.high, other.high));
/*     */   }
/*     */   
/*     */   public String toString(ConnectorSession session)
/*     */   {
/* 204 */     StringBuilder buffer = new StringBuilder();
/* 205 */     if (isSingleValue()) {
/* 206 */       buffer.append('[').append(this.low.getPrintableValue(session)).append(']');
/*     */     }
/*     */     else {
/* 209 */       buffer.append(this.low.getBound() == Marker.Bound.EXACTLY ? '[' : '(');
/* 210 */       buffer.append(this.low.isLowerUnbounded() ? "<min>" : this.low.getPrintableValue(session));
/* 211 */       buffer.append(", ");
/* 212 */       buffer.append(this.high.isUpperUnbounded() ? "<max>" : this.high.getPrintableValue(session));
/* 213 */       buffer.append(this.high.getBound() == Marker.Bound.EXACTLY ? ']' : ')');
/*     */     }
/* 215 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\Range.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */