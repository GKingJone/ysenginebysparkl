/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import javax.annotation.Nullable;
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
/*    */ final class ComparatorOrdering<T>
/*    */   extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Comparator<T> comparator;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   ComparatorOrdering(Comparator<T> comparator)
/*    */   {
/* 34 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*    */   }
/*    */   
/*    */   public int compare(T a, T b) {
/* 38 */     return this.comparator.compare(a, b);
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 42 */     if (object == this) {
/* 43 */       return true;
/*    */     }
/* 45 */     if ((object instanceof ComparatorOrdering)) {
/* 46 */       ComparatorOrdering<?> that = (ComparatorOrdering)object;
/* 47 */       return this.comparator.equals(that.comparator);
/*    */     }
/* 49 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 53 */     return this.comparator.hashCode();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 57 */     return this.comparator.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ComparatorOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */