/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.List;
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
/*    */ final class ExplicitOrdering<T>
/*    */   extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final ImmutableMap<T, Integer> rankMap;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   ExplicitOrdering(List<T> valuesInOrder)
/*    */   {
/* 32 */     this(buildRankMap(valuesInOrder));
/*    */   }
/*    */   
/*    */   ExplicitOrdering(ImmutableMap<T, Integer> rankMap) {
/* 36 */     this.rankMap = rankMap;
/*    */   }
/*    */   
/*    */   public int compare(T left, T right) {
/* 40 */     return rank(left) - rank(right);
/*    */   }
/*    */   
/*    */   private int rank(T value) {
/* 44 */     Integer rank = (Integer)this.rankMap.get(value);
/* 45 */     if (rank == null) {
/* 46 */       throw new IncomparableValueException(value);
/*    */     }
/* 48 */     return rank.intValue();
/*    */   }
/*    */   
/*    */   private static <T> ImmutableMap<T, Integer> buildRankMap(List<T> valuesInOrder)
/*    */   {
/* 53 */     ImmutableMap.Builder<T, Integer> builder = ImmutableMap.builder();
/* 54 */     int rank = 0;
/* 55 */     for (T value : valuesInOrder) {
/* 56 */       builder.put(value, Integer.valueOf(rank++));
/*    */     }
/* 58 */     return builder.build();
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object) {
/* 62 */     if ((object instanceof ExplicitOrdering)) {
/* 63 */       ExplicitOrdering<?> that = (ExplicitOrdering)object;
/* 64 */       return this.rankMap.equals(that.rankMap);
/*    */     }
/* 66 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 70 */     return this.rankMap.hashCode();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 74 */     String str = String.valueOf(String.valueOf(this.rankMap.keySet()));return 19 + str.length() + "Ordering.explicit(" + str + ")";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ExplicitOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */