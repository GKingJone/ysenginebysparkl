/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.SortOrder;
/*     */ import java.util.Collections;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SortingProperty<E>
/*     */   implements LocalProperty<E>
/*     */ {
/*     */   private final E column;
/*     */   private final SortOrder order;
/*     */   
/*     */   @JsonCreator
/*     */   public SortingProperty(@JsonProperty("column") E column, @JsonProperty("order") SortOrder order)
/*     */   {
/*  39 */     Objects.requireNonNull(column, "column is null");
/*  40 */     Objects.requireNonNull(order, "order is null");
/*     */     
/*  42 */     this.column = column;
/*  43 */     this.order = order;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public E getColumn()
/*     */   {
/*  49 */     return (E)this.column;
/*     */   }
/*     */   
/*     */   public Set<E> getColumns()
/*     */   {
/*  54 */     return Collections.singleton(this.column);
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public SortOrder getOrder()
/*     */   {
/*  60 */     return this.order;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> Optional<LocalProperty<T>> translate(Function<E, Optional<T>> translator)
/*     */   {
/*  69 */     Optional<T> translated = (Optional)translator.apply(this.column);
/*     */     
/*  71 */     if (translated.isPresent()) {
/*  72 */       return Optional.of(new SortingProperty(translated.get(), this.order));
/*     */     }
/*     */     
/*  75 */     return Optional.empty();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSimplifiedBy(LocalProperty<E> known)
/*     */   {
/*  81 */     return ((known instanceof ConstantProperty)) || (known.equals(this));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     String ordering = "";
/*  88 */     String nullOrdering = "";
/*  89 */     switch (this.order) {
/*     */     case ASC_NULLS_FIRST: 
/*  91 */       ordering = "↑";
/*  92 */       nullOrdering = "←";
/*  93 */       break;
/*     */     case ASC_NULLS_LAST: 
/*  95 */       ordering = "↑";
/*  96 */       nullOrdering = "→";
/*  97 */       break;
/*     */     case DESC_NULLS_FIRST: 
/*  99 */       ordering = "↓";
/* 100 */       nullOrdering = "←";
/* 101 */       break;
/*     */     case DESC_NULLS_LAST: 
/* 103 */       ordering = "↓";
/* 104 */       nullOrdering = "→";
/*     */     }
/*     */     
/*     */     
/* 108 */     return "S" + ordering + nullOrdering + "(" + this.column + ")";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 114 */     if (this == o) {
/* 115 */       return true;
/*     */     }
/* 117 */     if ((o == null) || (getClass() != o.getClass())) {
/* 118 */       return false;
/*     */     }
/* 120 */     SortingProperty<?> that = (SortingProperty)o;
/* 121 */     return (Objects.equals(this.column, that.column)) && 
/* 122 */       (Objects.equals(this.order, that.order));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 128 */     return Objects.hash(new Object[] { this.column, this.order });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SortingProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */