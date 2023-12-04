/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Iterator;
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
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class EnumMultiset<E extends Enum<E>>
/*     */   extends AbstractMapBasedMultiset<E>
/*     */ {
/*     */   private transient Class<E> type;
/*     */   @GwtIncompatible("Not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   public static <E extends Enum<E>> EnumMultiset<E> create(Class<E> type)
/*     */   {
/*  42 */     return new EnumMultiset(type);
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
/*     */   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements)
/*     */   {
/*  55 */     Iterator<E> iterator = elements.iterator();
/*  56 */     Preconditions.checkArgument(iterator.hasNext(), "EnumMultiset constructor passed empty Iterable");
/*  57 */     EnumMultiset<E> multiset = new EnumMultiset(((Enum)iterator.next()).getDeclaringClass());
/*  58 */     Iterables.addAll(multiset, elements);
/*  59 */     return multiset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements, Class<E> type)
/*     */   {
/*  70 */     EnumMultiset<E> result = create(type);
/*  71 */     Iterables.addAll(result, elements);
/*  72 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private EnumMultiset(Class<E> type)
/*     */   {
/*  79 */     super(WellBehavedMap.wrap(new EnumMap(type)));
/*  80 */     this.type = type;
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream) throws IOException {
/*  85 */     stream.defaultWriteObject();
/*  86 */     stream.writeObject(this.type);
/*  87 */     Serialization.writeMultiset(this, stream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  97 */     stream.defaultReadObject();
/*     */     
/*  99 */     Class<E> localType = (Class)stream.readObject();
/* 100 */     this.type = localType;
/* 101 */     setBackingMap(WellBehavedMap.wrap(new EnumMap(this.type)));
/* 102 */     Serialization.populateMultiset(this, stream);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\EnumMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */