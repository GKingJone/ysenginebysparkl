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
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class RegularImmutableMultiset<E>
/*    */   extends ImmutableMultiset<E>
/*    */ {
/*    */   private final transient ImmutableMap<E, Integer> map;
/*    */   private final transient int size;
/*    */   
/*    */   RegularImmutableMultiset(ImmutableMap<E, Integer> map, int size)
/*    */   {
/* 39 */     this.map = map;
/* 40 */     this.size = size;
/*    */   }
/*    */   
/*    */   boolean isPartialView()
/*    */   {
/* 45 */     return this.map.isPartialView();
/*    */   }
/*    */   
/*    */   public int count(@Nullable Object element)
/*    */   {
/* 50 */     Integer value = (Integer)this.map.get(element);
/* 51 */     return value == null ? 0 : value.intValue();
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 56 */     return this.size;
/*    */   }
/*    */   
/*    */   public boolean contains(@Nullable Object element)
/*    */   {
/* 61 */     return this.map.containsKey(element);
/*    */   }
/*    */   
/*    */   public ImmutableSet<E> elementSet()
/*    */   {
/* 66 */     return this.map.keySet();
/*    */   }
/*    */   
/*    */   Entry<E> getEntry(int index)
/*    */   {
/* 71 */     Map.Entry<E, Integer> mapEntry = (Map.Entry)this.map.entrySet().asList().get(index);
/* 72 */     return Multisets.immutableEntry(mapEntry.getKey(), ((Integer)mapEntry.getValue()).intValue());
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 77 */     return this.map.hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RegularImmutableMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */