/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class SingletonImmutableList<E>
/*     */   extends ImmutableList<E>
/*     */ {
/*     */   final transient E element;
/*     */   
/*     */   SingletonImmutableList(E element)
/*     */   {
/*  40 */     this.element = Preconditions.checkNotNull(element);
/*     */   }
/*     */   
/*     */   public E get(int index)
/*     */   {
/*  45 */     Preconditions.checkElementIndex(index, 1);
/*  46 */     return (E)this.element;
/*     */   }
/*     */   
/*     */   public int indexOf(@Nullable Object object) {
/*  50 */     return this.element.equals(object) ? 0 : -1;
/*     */   }
/*     */   
/*     */   public UnmodifiableIterator<E> iterator() {
/*  54 */     return Iterators.singletonIterator(this.element);
/*     */   }
/*     */   
/*     */   public int lastIndexOf(@Nullable Object object) {
/*  58 */     return indexOf(object);
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  63 */     return 1;
/*     */   }
/*     */   
/*     */   public ImmutableList<E> subList(int fromIndex, int toIndex) {
/*  67 */     Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
/*  68 */     return fromIndex == toIndex ? ImmutableList.of() : this;
/*     */   }
/*     */   
/*     */   public ImmutableList<E> reverse() {
/*  72 */     return this;
/*     */   }
/*     */   
/*     */   public boolean contains(@Nullable Object object) {
/*  76 */     return this.element.equals(object);
/*     */   }
/*     */   
/*     */   public boolean equals(@Nullable Object object) {
/*  80 */     if (object == this) {
/*  81 */       return true;
/*     */     }
/*  83 */     if ((object instanceof List)) {
/*  84 */       List<?> that = (List)object;
/*  85 */       return (that.size() == 1) && (this.element.equals(that.get(0)));
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  93 */     return 31 + this.element.hashCode();
/*     */   }
/*     */   
/*     */   public String toString() {
/*  97 */     String elementToString = this.element.toString();
/*  98 */     return elementToString.length() + 2 + '[' + elementToString + ']';
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */   
/*     */   boolean isPartialView() {
/* 110 */     return false;
/*     */   }
/*     */   
/*     */   int copyIntoArray(Object[] dst, int offset)
/*     */   {
/* 115 */     dst[offset] = this.element;
/* 116 */     return offset + 1;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\SingletonImmutableList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */