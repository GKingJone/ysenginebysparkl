/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
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
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ final class RegularImmutableSet<E>
/*    */   extends ImmutableSet<E>
/*    */ {
/*    */   private final Object[] elements;
/*    */   @VisibleForTesting
/*    */   final transient Object[] table;
/*    */   private final transient int mask;
/*    */   private final transient int hashCode;
/*    */   
/*    */   RegularImmutableSet(Object[] elements, int hashCode, Object[] table, int mask)
/*    */   {
/* 39 */     this.elements = elements;
/* 40 */     this.table = table;
/* 41 */     this.mask = mask;
/* 42 */     this.hashCode = hashCode;
/*    */   }
/*    */   
/*    */   public boolean contains(Object target) {
/* 46 */     if (target == null) {
/* 47 */       return false;
/*    */     }
/* 49 */     for (int i = Hashing.smear(target.hashCode());; i++) {
/* 50 */       Object candidate = this.table[(i & this.mask)];
/* 51 */       if (candidate == null) {
/* 52 */         return false;
/*    */       }
/* 54 */       if (candidate.equals(target)) {
/* 55 */         return true;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public int size()
/*    */   {
/* 62 */     return this.elements.length;
/*    */   }
/*    */   
/*    */ 
/*    */   public UnmodifiableIterator<E> iterator()
/*    */   {
/* 68 */     return Iterators.forArray(this.elements);
/*    */   }
/*    */   
/*    */   int copyIntoArray(Object[] dst, int offset)
/*    */   {
/* 73 */     System.arraycopy(this.elements, 0, dst, offset, this.elements.length);
/* 74 */     return offset + this.elements.length;
/*    */   }
/*    */   
/*    */   ImmutableList<E> createAsList()
/*    */   {
/* 79 */     return new RegularImmutableAsList(this, this.elements);
/*    */   }
/*    */   
/*    */   boolean isPartialView()
/*    */   {
/* 84 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 88 */     return this.hashCode;
/*    */   }
/*    */   
/*    */   boolean isHashCodeFast() {
/* 92 */     return true;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RegularImmutableSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */