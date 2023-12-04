/*     */ package com.facebook.presto.jdbc.internal.guava.collect;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.SortedSet;
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
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingSortedSet<E>
/*     */   extends ForwardingSet<E>
/*     */   implements SortedSet<E>
/*     */ {
/*     */   protected abstract SortedSet<E> delegate();
/*     */   
/*     */   public Comparator<? super E> comparator()
/*     */   {
/*  67 */     return delegate().comparator();
/*     */   }
/*     */   
/*     */   public E first()
/*     */   {
/*  72 */     return (E)delegate().first();
/*     */   }
/*     */   
/*     */   public SortedSet<E> headSet(E toElement)
/*     */   {
/*  77 */     return delegate().headSet(toElement);
/*     */   }
/*     */   
/*     */   public E last()
/*     */   {
/*  82 */     return (E)delegate().last();
/*     */   }
/*     */   
/*     */   public SortedSet<E> subSet(E fromElement, E toElement)
/*     */   {
/*  87 */     return delegate().subSet(fromElement, toElement);
/*     */   }
/*     */   
/*     */   public SortedSet<E> tailSet(E fromElement)
/*     */   {
/*  92 */     return delegate().tailSet(fromElement);
/*     */   }
/*     */   
/*     */ 
/*     */   private int unsafeCompare(Object o1, Object o2)
/*     */   {
/*  98 */     Comparator<? super E> comparator = comparator();
/*  99 */     return comparator == null ? ((Comparable)o1).compareTo(o2) : comparator.compare(o1, o2);
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
/*     */   @Beta
/*     */   protected boolean standardContains(@Nullable Object object)
/*     */   {
/*     */     try
/*     */     {
/* 115 */       SortedSet<Object> self = this;
/* 116 */       Object ceiling = self.tailSet(object).first();
/* 117 */       return unsafeCompare(ceiling, object) == 0;
/*     */     } catch (ClassCastException e) {
/* 119 */       return false;
/*     */     } catch (NoSuchElementException e) {
/* 121 */       return false;
/*     */     } catch (NullPointerException e) {}
/* 123 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardRemove(@Nullable Object object)
/*     */   {
/*     */     try
/*     */     {
/* 138 */       SortedSet<Object> self = this;
/* 139 */       Iterator<Object> iterator = self.tailSet(object).iterator();
/* 140 */       if (iterator.hasNext()) {
/* 141 */         Object ceiling = iterator.next();
/* 142 */         if (unsafeCompare(ceiling, object) == 0) {
/* 143 */           iterator.remove();
/* 144 */           return true;
/*     */         }
/*     */       }
/*     */     } catch (ClassCastException e) {
/* 148 */       return false;
/*     */     } catch (NullPointerException e) {
/* 150 */       return false;
/*     */     }
/* 152 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   protected SortedSet<E> standardSubSet(E fromElement, E toElement)
/*     */   {
/* 164 */     return tailSet(fromElement).headSet(toElement);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingSortedSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */