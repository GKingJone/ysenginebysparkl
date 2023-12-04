/*    */ package com.facebook.presto.jdbc.internal.guava.base;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
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
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ final class FunctionalEquivalence<F, T>
/*    */   extends Equivalence<F>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   private final Function<F, ? extends T> function;
/*    */   private final Equivalence<T> resultEquivalence;
/*    */   
/*    */   FunctionalEquivalence(Function<F, ? extends T> function, Equivalence<T> resultEquivalence)
/*    */   {
/* 46 */     this.function = ((Function)Preconditions.checkNotNull(function));
/* 47 */     this.resultEquivalence = ((Equivalence)Preconditions.checkNotNull(resultEquivalence));
/*    */   }
/*    */   
/*    */   protected boolean doEquivalent(F a, F b) {
/* 51 */     return this.resultEquivalence.equivalent(this.function.apply(a), this.function.apply(b));
/*    */   }
/*    */   
/*    */   protected int doHash(F a) {
/* 55 */     return this.resultEquivalence.hash(this.function.apply(a));
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object obj) {
/* 59 */     if (obj == this) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((obj instanceof FunctionalEquivalence)) {
/* 63 */       FunctionalEquivalence<?, ?> that = (FunctionalEquivalence)obj;
/* 64 */       return (this.function.equals(that.function)) && (this.resultEquivalence.equals(that.resultEquivalence));
/*    */     }
/*    */     
/* 67 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 71 */     return Objects.hashCode(new Object[] { this.function, this.resultEquivalence });
/*    */   }
/*    */   
/*    */   public String toString() {
/* 75 */     String str1 = String.valueOf(String.valueOf(this.resultEquivalence));String str2 = String.valueOf(String.valueOf(this.function));return 13 + str1.length() + str2.length() + str1 + ".onResultOf(" + str2 + ")";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\FunctionalEquivalence.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */