/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Objects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ByFunctionOrdering<F, T>
/*    */   extends Ordering<F>
/*    */   implements Serializable
/*    */ {
/*    */   final Function<F, ? extends T> function;
/*    */   final Ordering<T> ordering;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   ByFunctionOrdering(Function<F, ? extends T> function, Ordering<T> ordering)
/*    */   {
/* 41 */     this.function = ((Function)Preconditions.checkNotNull(function));
/* 42 */     this.ordering = ((Ordering)Preconditions.checkNotNull(ordering));
/*    */   }
/*    */   
/*    */   public int compare(F left, F right) {
/* 46 */     return this.ordering.compare(this.function.apply(left), this.function.apply(right));
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 50 */     if (object == this) {
/* 51 */       return true;
/*    */     }
/* 53 */     if ((object instanceof ByFunctionOrdering)) {
/* 54 */       ByFunctionOrdering<?, ?> that = (ByFunctionOrdering)object;
/* 55 */       return (this.function.equals(that.function)) && (this.ordering.equals(that.ordering));
/*    */     }
/*    */     
/* 58 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 62 */     return Objects.hashCode(new Object[] { this.function, this.ordering });
/*    */   }
/*    */   
/*    */   public String toString() {
/* 66 */     String str1 = String.valueOf(String.valueOf(this.ordering));String str2 = String.valueOf(String.valueOf(this.function));return 13 + str1.length() + str2.length() + str1 + ".onResultOf(" + str2 + ")";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ByFunctionOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */