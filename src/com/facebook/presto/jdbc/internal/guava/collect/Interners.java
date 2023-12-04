/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Equivalence;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public final class Interners
/*     */ {
/*     */   public static <E> Interner<E> newStrongInterner()
/*     */   {
/*  45 */     ConcurrentMap<E, E> map = new MapMaker().makeMap();
/*  46 */     new Interner() {
/*     */       public E intern(E sample) {
/*  48 */         E canonical = this.val$map.putIfAbsent(Preconditions.checkNotNull(sample), sample);
/*  49 */         return canonical == null ? sample : canonical;
/*     */       }
/*     */     };
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
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*  63 */   public static <E> Interner<E> newWeakInterner() { return new WeakInterner(null); }
/*     */   
/*     */   private static class WeakInterner<E> implements Interner<E> {
/*     */     private final MapMakerInternalMap<E, Dummy> map;
/*     */     
/*  68 */     private WeakInterner() { this.map = new MapMaker().weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap(); }
/*     */     
/*     */ 
/*     */ 
/*     */     public E intern(E sample)
/*     */     {
/*     */       for (;;)
/*     */       {
/*  76 */         MapMakerInternalMap.ReferenceEntry<E, Dummy> entry = this.map.getEntry(sample);
/*  77 */         if (entry != null) {
/*  78 */           E canonical = entry.getKey();
/*  79 */           if (canonical != null) {
/*  80 */             return canonical;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*  85 */         Dummy sneaky = (Dummy)this.map.putIfAbsent(sample, Dummy.VALUE);
/*  86 */         if (sneaky == null) {
/*  87 */           return sample;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static enum Dummy
/*     */     {
/*  99 */       VALUE;
/*     */       
/*     */ 
/*     */       private Dummy() {}
/*     */     }
/*     */   }
/*     */   
/*     */   public static <E> Function<E, E> asFunction(Interner<E> interner)
/*     */   {
/* 108 */     return new InternerFunction((Interner)Preconditions.checkNotNull(interner));
/*     */   }
/*     */   
/*     */   private static class InternerFunction<E> implements Function<E, E>
/*     */   {
/*     */     private final Interner<E> interner;
/*     */     
/*     */     public InternerFunction(Interner<E> interner) {
/* 116 */       this.interner = interner;
/*     */     }
/*     */     
/*     */     public E apply(E input) {
/* 120 */       return (E)this.interner.intern(input);
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 124 */       return this.interner.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object other) {
/* 128 */       if ((other instanceof InternerFunction)) {
/* 129 */         InternerFunction<?> that = (InternerFunction)other;
/* 130 */         return this.interner.equals(that.interner);
/*     */       }
/*     */       
/* 133 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\Interners.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */