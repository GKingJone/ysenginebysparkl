/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class UsingToStringOrdering
/*    */   extends Ordering<Object>
/*    */   implements Serializable
/*    */ {
/* 30 */   static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/* 33 */   public int compare(Object left, Object right) { return left.toString().compareTo(right.toString()); }
/*    */   
/*    */ 
/*    */   private Object readResolve()
/*    */   {
/* 38 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return "Ordering.usingToString()";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\UsingToStringOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */