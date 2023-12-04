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
/*    */ final class NullsFirstOrdering<T>
/*    */   extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Ordering<? super T> ordering;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   NullsFirstOrdering(Ordering<? super T> ordering)
/*    */   {
/* 31 */     this.ordering = ordering;
/*    */   }
/*    */   
/*    */   public int compare(@Nullable T left, @Nullable T right) {
/* 35 */     if (left == right) {
/* 36 */       return 0;
/*    */     }
/* 38 */     if (left == null) {
/* 39 */       return -1;
/*    */     }
/* 41 */     if (right == null) {
/* 42 */       return 1;
/*    */     }
/* 44 */     return this.ordering.compare(left, right);
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> reverse()
/*    */   {
/* 49 */     return this.ordering.reverse().nullsLast();
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> nullsFirst()
/*    */   {
/* 54 */     return this;
/*    */   }
/*    */   
/*    */   public <S extends T> Ordering<S> nullsLast() {
/* 58 */     return this.ordering.nullsLast();
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 62 */     if (object == this) {
/* 63 */       return true;
/*    */     }
/* 65 */     if ((object instanceof NullsFirstOrdering)) {
/* 66 */       NullsFirstOrdering<?> that = (NullsFirstOrdering)object;
/* 67 */       return this.ordering.equals(that.ordering);
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 73 */     return this.ordering.hashCode() ^ 0x39153A74;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 77 */     String str = String.valueOf(String.valueOf(this.ordering));return 13 + str.length() + str + ".nullsFirst()";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\NullsFirstOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */