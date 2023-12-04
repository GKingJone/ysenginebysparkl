/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
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
/*    */ class EmptyImmutableSetMultimap
/*    */   extends ImmutableSetMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   private EmptyImmutableSetMultimap() {
/* 32 */     super(ImmutableMap.of(), 0, null);
/*    */   }
/*    */   
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\EmptyImmutableSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */