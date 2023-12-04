/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.util.HashMap;
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
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ public final class HashMultiset<E>
/*    */   extends AbstractMapBasedMultiset<E>
/*    */ {
/*    */   @GwtIncompatible("Not needed in emulated source.")
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   public static <E> HashMultiset<E> create()
/*    */   {
/* 42 */     return new HashMultiset();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static <E> HashMultiset<E> create(int distinctElements)
/*    */   {
/* 53 */     return new HashMultiset(distinctElements);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static <E> HashMultiset<E> create(Iterable<? extends E> elements)
/*    */   {
/* 65 */     HashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
/*    */     
/* 67 */     Iterables.addAll(multiset, elements);
/* 68 */     return multiset;
/*    */   }
/*    */   
/*    */   private HashMultiset() {
/* 72 */     super(new HashMap());
/*    */   }
/*    */   
/*    */   private HashMultiset(int distinctElements) {
/* 76 */     super(Maps.newHashMapWithExpectedSize(distinctElements));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("java.io.ObjectOutputStream")
/*    */   private void writeObject(ObjectOutputStream stream)
/*    */     throws IOException
/*    */   {
/* 85 */     stream.defaultWriteObject();
/* 86 */     Serialization.writeMultiset(this, stream);
/*    */   }
/*    */   
/*    */   @GwtIncompatible("java.io.ObjectInputStream")
/*    */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*    */   {
/* 92 */     stream.defaultReadObject();
/* 93 */     int distinctElements = Serialization.readCount(stream);
/* 94 */     setBackingMap(Maps.newHashMapWithExpectedSize(distinctElements));
/*    */     
/* 96 */     Serialization.populateMultiset(this, stream, distinctElements);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\HashMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */