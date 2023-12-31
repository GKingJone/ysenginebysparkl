/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
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
/*     */ @GwtCompatible(serializable=true)
/*     */ public abstract class Optional<T>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   public static <T> Optional<T> absent()
/*     */   {
/*  78 */     return Absent.withType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <T> Optional<T> of(T reference)
/*     */   {
/*  85 */     return new Present(Preconditions.checkNotNull(reference));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Optional<T> fromNullable(@Nullable T nullableReference)
/*     */   {
/*  93 */     return nullableReference == null ? absent() : new Present(nullableReference);
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
/*     */   public abstract boolean isPresent();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract T get();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract T or(T paramT);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Optional<T> or(Optional<? extends T> paramOptional);
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
/*     */   public abstract T or(Supplier<? extends T> paramSupplier);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Nullable
/*     */   public abstract T orNull();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Set<T> asSet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract <V> Optional<V> transform(Function<? super T, V> paramFunction);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean equals(@Nullable Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract int hashCode();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String toString();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static <T> Iterable<T> presentInstances(Iterable<? extends Optional<? extends T>> optionals)
/*     */   {
/* 218 */     Preconditions.checkNotNull(optionals);
/* 219 */     new Iterable()
/*     */     {
/*     */       public Iterator<T> iterator() {
/* 222 */         new AbstractIterator() {
/* 223 */           private final Iterator<? extends Optional<? extends T>> iterator = (Iterator)Preconditions.checkNotNull(Optional.1.this.val$optionals.iterator());
/*     */           
/*     */ 
/*     */           protected T computeNext()
/*     */           {
/* 228 */             while (this.iterator.hasNext()) {
/* 229 */               Optional<? extends T> optional = (Optional)this.iterator.next();
/* 230 */               if (optional.isPresent()) {
/* 231 */                 return (T)optional.get();
/*     */               }
/*     */             }
/* 234 */             return (T)endOfData();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Optional.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */