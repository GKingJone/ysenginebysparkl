/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
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
/*    */ @GwtCompatible(serializable=true)
/*    */ final class NullsLastOrdering<T>
/*    */   extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Ordering<? super T> ordering;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   NullsLastOrdering(Ordering<? super T> ordering)
/*    */   {
/* 31 */     this.ordering = ordering;
/*    */   }
/*    */   
/*    */   public int compare(@Nullable T left, @Nullable T right) {
/* 35 */     if (left == right) {
/* 36 */       return 0;
/*    */     }
/* 38 */     if (left == null) {
/* 39 */       return 1;
/*    */     }
/* 41 */     if (right == null) {
/* 42 */       return -1;
/*    */     }
/* 44 */     return this.ordering.compare(left, right);
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> reverse()
/*    */   {
/* 49 */     return this.ordering.reverse().nullsFirst();
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> nullsFirst() {
/* 53 */     return this.ordering.nullsFirst();
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> nullsLast()
/*    */   {
/* 58 */     return this;
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 62 */     if (object == this) {
/* 63 */       return true;
/*    */     }
/* 65 */     if ((object instanceof NullsLastOrdering)) {
/* 66 */       NullsLastOrdering<?> that = (NullsLastOrdering)object;
/* 67 */       return this.ordering.equals(that.ordering);
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 73 */     return this.ordering.hashCode() ^ 0xC9177248;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 77 */     String str = String.valueOf(String.valueOf(this.ordering));return 12 + str.length() + str + ".nullsLast()";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\NullsLastOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */