/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import java.util.Collections;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
/*    */ import java.util.Set;
/*    */ import java.util.function.Function;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ConstantProperty<E>
/*    */   implements LocalProperty<E>
/*    */ {
/*    */   private final E column;
/*    */   
/*    */   @JsonCreator
/*    */   public ConstantProperty(@JsonProperty("column") E column)
/*    */   {
/* 35 */     this.column = Objects.requireNonNull(column, "column is null");
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public E getColumn()
/*    */   {
/* 41 */     return (E)this.column;
/*    */   }
/*    */   
/*    */   public Set<E> getColumns()
/*    */   {
/* 46 */     return Collections.singleton(this.column);
/*    */   }
/*    */   
/*    */ 
/*    */   public <T> Optional<LocalProperty<T>> translate(Function<E, Optional<T>> translator)
/*    */   {
/* 52 */     Optional<T> translated = (Optional)translator.apply(this.column);
/*    */     
/* 54 */     if (translated.isPresent()) {
/* 55 */       return Optional.of(new ConstantProperty(translated.get()));
/*    */     }
/*    */     
/* 58 */     return Optional.empty();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSimplifiedBy(LocalProperty<E> known)
/*    */   {
/* 64 */     return ((known instanceof ConstantProperty)) && (known.equals(this));
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 70 */     return "C(" + this.column + ")";
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 76 */     if (this == o) {
/* 77 */       return true;
/*    */     }
/* 79 */     if ((o == null) || (getClass() != o.getClass())) {
/* 80 */       return false;
/*    */     }
/* 82 */     ConstantProperty<?> that = (ConstantProperty)o;
/* 83 */     return Objects.equals(this.column, that.column);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 89 */     return Objects.hash(new Object[] { this.column });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConstantProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */