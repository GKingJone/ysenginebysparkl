/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
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
/*    */ class EmptyImmutableListMultimap
/*    */   extends ImmutableListMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   private EmptyImmutableListMultimap() {
/* 32 */     super(ImmutableMap.of(), 0);
/*    */   }
/*    */   
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\EmptyImmutableListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */