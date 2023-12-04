/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.Map.Entry;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class EmptyImmutableBiMap
/*    */   extends ImmutableBiMap<Object, Object>
/*    */ {
/* 31 */   static final EmptyImmutableBiMap INSTANCE = new EmptyImmutableBiMap();
/*    */   
/*    */ 
/*    */   public ImmutableBiMap<Object, Object> inverse()
/*    */   {
/* 36 */     return this;
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 41 */     return 0;
/*    */   }
/*    */   
/*    */   public boolean isEmpty()
/*    */   {
/* 46 */     return true;
/*    */   }
/*    */   
/*    */   public Object get(@Nullable Object key)
/*    */   {
/* 51 */     return null;
/*    */   }
/*    */   
/*    */   public ImmutableSet<Map.Entry<Object, Object>> entrySet()
/*    */   {
/* 56 */     return ImmutableSet.of();
/*    */   }
/*    */   
/*    */   ImmutableSet<Map.Entry<Object, Object>> createEntrySet()
/*    */   {
/* 61 */     throw new AssertionError("should never be called");
/*    */   }
/*    */   
/*    */   public ImmutableSetMultimap<Object, Object> asMultimap()
/*    */   {
/* 66 */     return ImmutableSetMultimap.of();
/*    */   }
/*    */   
/*    */   public ImmutableSet<Object> keySet()
/*    */   {
/* 71 */     return ImmutableSet.of();
/*    */   }
/*    */   
/*    */   boolean isPartialView()
/*    */   {
/* 76 */     return false;
/*    */   }
/*    */   
/*    */   Object readResolve() {
/* 80 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\EmptyImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */