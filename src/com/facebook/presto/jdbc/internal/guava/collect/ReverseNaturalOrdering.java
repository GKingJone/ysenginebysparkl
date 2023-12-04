/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
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
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ReverseNaturalOrdering
/*    */   extends Ordering<Comparable>
/*    */   implements Serializable
/*    */ {
/* 31 */   static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/* 34 */   public int compare(Comparable left, Comparable right) { Preconditions.checkNotNull(left);
/* 35 */     if (left == right) {
/* 36 */       return 0;
/*    */     }
/*    */     
/* 39 */     return right.compareTo(left);
/*    */   }
/*    */   
/*    */   public <S extends Comparable> Ordering<S> reverse() {
/* 43 */     return Ordering.natural();
/*    */   }
/*    */   
/*    */ 
/*    */   public <E extends Comparable> E min(E a, E b)
/*    */   {
/* 49 */     return (Comparable)NaturalOrdering.INSTANCE.max(a, b);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E min(E a, E b, E c, E... rest) {
/* 53 */     return (Comparable)NaturalOrdering.INSTANCE.max(a, b, c, rest);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E min(Iterator<E> iterator) {
/* 57 */     return (Comparable)NaturalOrdering.INSTANCE.max(iterator);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E min(Iterable<E> iterable) {
/* 61 */     return (Comparable)NaturalOrdering.INSTANCE.max(iterable);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E max(E a, E b) {
/* 65 */     return (Comparable)NaturalOrdering.INSTANCE.min(a, b);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E max(E a, E b, E c, E... rest) {
/* 69 */     return (Comparable)NaturalOrdering.INSTANCE.min(a, b, c, rest);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E max(Iterator<E> iterator) {
/* 73 */     return (Comparable)NaturalOrdering.INSTANCE.min(iterator);
/*    */   }
/*    */   
/*    */   public <E extends Comparable> E max(Iterable<E> iterable) {
/* 77 */     return (Comparable)NaturalOrdering.INSTANCE.min(iterable);
/*    */   }
/*    */   
/*    */   private Object readResolve()
/*    */   {
/* 82 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 86 */     return "Ordering.natural().reverse()";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ReverseNaturalOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */