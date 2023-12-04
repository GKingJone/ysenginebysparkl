/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.io.Serializable;
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
/*    */ final class NaturalOrdering
/*    */   extends Ordering<Comparable>
/*    */   implements Serializable
/*    */ {
/* 30 */   static final NaturalOrdering INSTANCE = new NaturalOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/* 33 */   public int compare(Comparable left, Comparable right) { Preconditions.checkNotNull(left);
/* 34 */     Preconditions.checkNotNull(right);
/* 35 */     return left.compareTo(right);
/*    */   }
/*    */   
/*    */   public <S extends Comparable> Ordering<S> reverse() {
/* 39 */     return ReverseNaturalOrdering.INSTANCE;
/*    */   }
/*    */   
/*    */   private Object readResolve()
/*    */   {
/* 44 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 48 */     return "Ordering.natural()";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\NaturalOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */