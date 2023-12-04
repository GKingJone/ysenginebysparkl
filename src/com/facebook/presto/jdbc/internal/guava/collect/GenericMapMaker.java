/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Equivalence;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class GenericMapMaker<K0, V0>
/*     */ {
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker.RemovalListener<K0, V0> removalListener;
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> paramEquivalence);
/*     */   
/*     */   public abstract GenericMapMaker<K0, V0> initialCapacity(int paramInt);
/*     */   
/*     */   abstract GenericMapMaker<K0, V0> maximumSize(int paramInt);
/*     */   
/*     */   public abstract GenericMapMaker<K0, V0> concurrencyLevel(int paramInt);
/*     */   
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakKeys();
/*     */   
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakValues();
/*     */   
/*     */   @Deprecated
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public abstract GenericMapMaker<K0, V0> softValues();
/*     */   
/*     */   abstract GenericMapMaker<K0, V0> expireAfterWrite(long paramLong, TimeUnit paramTimeUnit);
/*     */   
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> expireAfterAccess(long paramLong, TimeUnit paramTimeUnit);
/*     */   
/*     */   @GwtIncompatible("To be supported")
/*     */   static enum NullListener
/*     */     implements MapMaker.RemovalListener<Object, Object>
/*     */   {
/*  53 */     INSTANCE;
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
/*     */     private NullListener() {}
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
/*     */     public void onRemoval(MapMaker.RemovalNotification<Object, Object> notification) {}
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
/*     */   @GwtIncompatible("To be supported")
/*     */   <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener()
/*     */   {
/* 131 */     return (MapMaker.RemovalListener)MoreObjects.firstNonNull(this.removalListener, NullListener.INSTANCE);
/*     */   }
/*     */   
/*     */   public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();
/*     */   
/*     */   @GwtIncompatible("MapMakerInternalMap")
/*     */   abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();
/*     */   
/*     */   @Deprecated
/*     */   abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> paramFunction);
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\GenericMapMaker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */