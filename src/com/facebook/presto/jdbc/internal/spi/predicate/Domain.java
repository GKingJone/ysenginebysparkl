/*     */ package com.facebook.presto.jdbc.internal.spi.predicate;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Domain
/*     */ {
/*     */   private final ValueSet values;
/*     */   private final boolean nullAllowed;
/*     */   
/*     */   private Domain(ValueSet values, boolean nullAllowed)
/*     */   {
/*  47 */     this.values = ((ValueSet)Objects.requireNonNull(values, "values is null"));
/*  48 */     this.nullAllowed = nullAllowed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @JsonCreator
/*     */   public static Domain create(@JsonProperty("values") ValueSet values, @JsonProperty("nullAllowed") boolean nullAllowed)
/*     */   {
/*  56 */     return new Domain(values, nullAllowed);
/*     */   }
/*     */   
/*     */   public static Domain none(Type type)
/*     */   {
/*  61 */     return new Domain(ValueSet.none(type), false);
/*     */   }
/*     */   
/*     */   public static Domain all(Type type)
/*     */   {
/*  66 */     return new Domain(ValueSet.all(type), true);
/*     */   }
/*     */   
/*     */   public static Domain onlyNull(Type type)
/*     */   {
/*  71 */     return new Domain(ValueSet.none(type), true);
/*     */   }
/*     */   
/*     */   public static Domain notNull(Type type)
/*     */   {
/*  76 */     return new Domain(ValueSet.all(type), false);
/*     */   }
/*     */   
/*     */   public static Domain singleValue(Type type, Object value)
/*     */   {
/*  81 */     return new Domain(ValueSet.of(type, value, new Object[0]), false);
/*     */   }
/*     */   
/*     */   public Type getType()
/*     */   {
/*  86 */     return this.values.getType();
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public ValueSet getValues()
/*     */   {
/*  92 */     return this.values;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public boolean isNullAllowed()
/*     */   {
/*  98 */     return this.nullAllowed;
/*     */   }
/*     */   
/*     */   public boolean isNone()
/*     */   {
/* 103 */     return (this.values.isNone()) && (!this.nullAllowed);
/*     */   }
/*     */   
/*     */   public boolean isAll()
/*     */   {
/* 108 */     return (this.values.isAll()) && (this.nullAllowed);
/*     */   }
/*     */   
/*     */   public boolean isSingleValue()
/*     */   {
/* 113 */     return (!this.nullAllowed) && (this.values.isSingleValue());
/*     */   }
/*     */   
/*     */   public boolean isNullableSingleValue()
/*     */   {
/* 118 */     if (this.nullAllowed) {
/* 119 */       return this.values.isNone();
/*     */     }
/*     */     
/* 122 */     return this.values.isSingleValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOnlyNull()
/*     */   {
/* 128 */     return (this.values.isNone()) && (this.nullAllowed);
/*     */   }
/*     */   
/*     */   public Object getSingleValue()
/*     */   {
/* 133 */     if (!isSingleValue()) {
/* 134 */       throw new IllegalStateException("Domain is not a single value");
/*     */     }
/* 136 */     return this.values.getSingleValue();
/*     */   }
/*     */   
/*     */   public Object getNullableSingleValue()
/*     */   {
/* 141 */     if (!isNullableSingleValue()) {
/* 142 */       throw new IllegalStateException("Domain is not a nullable single value");
/*     */     }
/*     */     
/* 145 */     if (this.nullAllowed) {
/* 146 */       return null;
/*     */     }
/*     */     
/* 149 */     return this.values.getSingleValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean includesNullableValue(Object value)
/*     */   {
/* 155 */     return value == null ? this.nullAllowed : this.values.containsValue(value);
/*     */   }
/*     */   
/*     */   public boolean overlaps(Domain other)
/*     */   {
/* 160 */     checkCompatibility(other);
/* 161 */     return !intersect(other).isNone();
/*     */   }
/*     */   
/*     */   public boolean contains(Domain other)
/*     */   {
/* 166 */     checkCompatibility(other);
/* 167 */     return union(other).equals(this);
/*     */   }
/*     */   
/*     */   public Domain intersect(Domain other)
/*     */   {
/* 172 */     checkCompatibility(other);
/* 173 */     return new Domain(this.values.intersect(other.getValues()), (isNullAllowed()) && (other.isNullAllowed()));
/*     */   }
/*     */   
/*     */   public Domain union(Domain other)
/*     */   {
/* 178 */     checkCompatibility(other);
/* 179 */     return new Domain(this.values.union(other.getValues()), (isNullAllowed()) || (other.isNullAllowed()));
/*     */   }
/*     */   
/*     */   public static Domain union(List<Domain> domains)
/*     */   {
/* 184 */     if (domains.isEmpty()) {
/* 185 */       throw new IllegalArgumentException("domains cannot be empty for union");
/*     */     }
/* 187 */     if (domains.size() == 1) {
/* 188 */       return (Domain)domains.get(0);
/*     */     }
/*     */     
/* 191 */     boolean nullAllowed = false;
/* 192 */     List<ValueSet> valueSets = new ArrayList(domains.size());
/* 193 */     for (Domain domain : domains) {
/* 194 */       valueSets.add(domain.getValues());
/* 195 */       nullAllowed = (nullAllowed) || (domain.nullAllowed);
/*     */     }
/*     */     
/* 198 */     ValueSet unionedValues = ((ValueSet)valueSets.get(0)).union(valueSets.subList(1, valueSets.size()));
/*     */     
/* 200 */     return new Domain(unionedValues, nullAllowed);
/*     */   }
/*     */   
/*     */   public Domain complement()
/*     */   {
/* 205 */     return new Domain(this.values.complement(), !this.nullAllowed);
/*     */   }
/*     */   
/*     */   public Domain subtract(Domain other)
/*     */   {
/* 210 */     checkCompatibility(other);
/* 211 */     return new Domain(this.values.subtract(other.getValues()), (isNullAllowed()) && (!other.isNullAllowed()));
/*     */   }
/*     */   
/*     */   private void checkCompatibility(Domain domain)
/*     */   {
/* 216 */     if (!getType().equals(domain.getType())) {
/* 217 */       throw new IllegalArgumentException(String.format("Mismatched Domain types: %s vs %s", new Object[] { getType(), domain.getType() }));
/*     */     }
/* 219 */     if (this.values.getClass() != domain.values.getClass()) {
/* 220 */       throw new IllegalArgumentException(String.format("Mismatched Domain value set classes: %s vs %s", new Object[] { this.values.getClass(), domain.values.getClass() }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 227 */     return Objects.hash(new Object[] { this.values, Boolean.valueOf(this.nullAllowed) });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 233 */     if (this == obj) {
/* 234 */       return true;
/*     */     }
/* 236 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 237 */       return false;
/*     */     }
/* 239 */     Domain other = (Domain)obj;
/* 240 */     return (Objects.equals(this.values, other.values)) && (this.nullAllowed == other.nullAllowed);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString(ConnectorSession session)
/*     */   {
/* 246 */     return "[ " + (this.nullAllowed ? "NULL, " : "") + this.values.toString(session) + " ]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\Domain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */