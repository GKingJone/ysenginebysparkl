/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id;
/*    */ import java.util.HashSet;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@type")
/*    */ @JsonSubTypes({@com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.Type(value=ConstantProperty.class, name="constant"), @com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.Type(value=SortingProperty.class, name="sorting"), @com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.Type(value=GroupingProperty.class, name="grouping")})
/*    */ public abstract interface LocalProperty<E>
/*    */ {
/*    */   public abstract <T> Optional<LocalProperty<T>> translate(Function<E, Optional<T>> paramFunction);
/*    */   
/*    */   public abstract boolean isSimplifiedBy(LocalProperty<E> paramLocalProperty);
/*    */   
/*    */   public Optional<LocalProperty<E>> withConstants(Set<E> constants)
/*    */   {
/* 47 */     Set<E> set = new HashSet(getColumns());
/* 48 */     set.removeAll(constants);
/*    */     
/* 50 */     if (set.isEmpty()) {
/* 51 */       return Optional.empty();
/*    */     }
/*    */     
/* 54 */     return Optional.of(constrain(set));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public LocalProperty<E> constrain(Set<E> columns)
/*    */   {
/* 62 */     if (!columns.equals(getColumns())) {
/* 63 */       throw new IllegalArgumentException(String.format("Cannot constrain %s with %s", new Object[] { this, columns }));
/*    */     }
/* 65 */     return this;
/*    */   }
/*    */   
/*    */   public abstract Set<E> getColumns();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\LocalProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */