/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.annotations.GwtIncompatible;
/*    */ import java.util.Comparator;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class ImmutableSortedAsList<E>
/*    */   extends RegularImmutableAsList<E>
/*    */   implements SortedIterable<E>
/*    */ {
/*    */   ImmutableSortedAsList(ImmutableSortedSet<E> backingSet, ImmutableList<E> backingList)
/*    */   {
/* 36 */     super(backingSet, backingList);
/*    */   }
/*    */   
/*    */   ImmutableSortedSet<E> delegateCollection()
/*    */   {
/* 41 */     return (ImmutableSortedSet)super.delegateCollection();
/*    */   }
/*    */   
/*    */   public Comparator<? super E> comparator() {
/* 45 */     return delegateCollection().comparator();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("ImmutableSortedSet.indexOf")
/*    */   public int indexOf(@Nullable Object target)
/*    */   {
/* 53 */     int index = delegateCollection().indexOf(target);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 60 */     return (index >= 0) && (get(index).equals(target)) ? index : -1;
/*    */   }
/*    */   
/*    */   @GwtIncompatible("ImmutableSortedSet.indexOf")
/*    */   public int lastIndexOf(@Nullable Object target) {
/* 65 */     return indexOf(target);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean contains(Object target)
/*    */   {
/* 71 */     return indexOf(target) >= 0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("super.subListUnchecked does not exist; inherited subList is valid if slow")
/*    */   ImmutableList<E> subListUnchecked(int fromIndex, int toIndex)
/*    */   {
/* 82 */     return new RegularImmutableSortedSet(super.subListUnchecked(fromIndex, toIndex), comparator()).asList();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ImmutableSortedAsList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */