/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
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
/*     */ final class ImmutableEnumSet<E extends Enum<E>>
/*     */   extends ImmutableSet<E>
/*     */ {
/*     */   private final transient EnumSet<E> delegate;
/*     */   private transient int hashCode;
/*     */   
/*     */   static <E extends Enum<E>> ImmutableSet<E> asImmutable(EnumSet<E> set)
/*     */   {
/*  35 */     switch (set.size()) {
/*     */     case 0: 
/*  37 */       return ImmutableSet.of();
/*     */     case 1: 
/*  39 */       return ImmutableSet.of(Iterables.getOnlyElement(set));
/*     */     }
/*  41 */     return new ImmutableEnumSet(set);
/*     */   }
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
/*     */   private ImmutableEnumSet(EnumSet<E> delegate)
/*     */   {
/*  56 */     this.delegate = delegate;
/*     */   }
/*     */   
/*     */   boolean isPartialView() {
/*  60 */     return false;
/*     */   }
/*     */   
/*     */   public UnmodifiableIterator<E> iterator() {
/*  64 */     return Iterators.unmodifiableIterator(this.delegate.iterator());
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  69 */     return this.delegate.size();
/*     */   }
/*     */   
/*     */   public boolean contains(Object object) {
/*  73 */     return this.delegate.contains(object);
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<?> collection) {
/*  77 */     return this.delegate.containsAll(collection);
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  81 */     return this.delegate.isEmpty();
/*     */   }
/*     */   
/*     */   public boolean equals(Object object) {
/*  85 */     return (object == this) || (this.delegate.equals(object));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  91 */     int result = this.hashCode;
/*  92 */     return result == 0 ? (this.hashCode = this.delegate.hashCode()) : result;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  96 */     return this.delegate.toString();
/*     */   }
/*     */   
/*     */   Object writeReplace()
/*     */   {
/* 101 */     return new EnumSerializedForm(this.delegate);
/*     */   }
/*     */   
/*     */   private static class EnumSerializedForm<E extends Enum<E>> implements Serializable
/*     */   {
/*     */     final EnumSet<E> delegate;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     EnumSerializedForm(EnumSet<E> delegate)
/*     */     {
/* 111 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     Object readResolve() {
/* 115 */       return new ImmutableEnumSet(this.delegate.clone(), null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ImmutableEnumSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */