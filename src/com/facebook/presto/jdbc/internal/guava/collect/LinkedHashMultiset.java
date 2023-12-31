/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.LinkedHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public final class LinkedHashMultiset<E>
/*     */   extends AbstractMapBasedMultiset<E>
/*     */ {
/*     */   @GwtIncompatible("not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   public static <E> LinkedHashMultiset<E> create()
/*     */   {
/*  52 */     return new LinkedHashMultiset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> LinkedHashMultiset<E> create(int distinctElements)
/*     */   {
/*  63 */     return new LinkedHashMultiset(distinctElements);
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
/*     */   public static <E> LinkedHashMultiset<E> create(Iterable<? extends E> elements)
/*     */   {
/*  76 */     LinkedHashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
/*     */     
/*  78 */     Iterables.addAll(multiset, elements);
/*  79 */     return multiset;
/*     */   }
/*     */   
/*     */   private LinkedHashMultiset() {
/*  83 */     super(new LinkedHashMap());
/*     */   }
/*     */   
/*     */   private LinkedHashMultiset(int distinctElements)
/*     */   {
/*  88 */     super(new LinkedHashMap(Maps.capacity(distinctElements)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/*  97 */     stream.defaultWriteObject();
/*  98 */     Serialization.writeMultiset(this, stream);
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 104 */     stream.defaultReadObject();
/* 105 */     int distinctElements = Serialization.readCount(stream);
/* 106 */     setBackingMap(new LinkedHashMap(Maps.capacity(distinctElements)));
/*     */     
/* 108 */     Serialization.populateMultiset(this, stream, distinctElements);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\LinkedHashMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */