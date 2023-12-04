/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ @GwtCompatible
/*     */ public final class Suppliers
/*     */ {
/*     */   public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier)
/*     */   {
/*  51 */     Preconditions.checkNotNull(function);
/*  52 */     Preconditions.checkNotNull(supplier);
/*  53 */     return new SupplierComposition(function, supplier);
/*     */   }
/*     */   
/*     */   private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
/*     */     final Function<? super F, T> function;
/*     */     final Supplier<F> supplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
/*  62 */       this.function = function;
/*  63 */       this.supplier = supplier;
/*     */     }
/*     */     
/*     */     public T get() {
/*  67 */       return (T)this.function.apply(this.supplier.get());
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/*  71 */       if ((obj instanceof SupplierComposition)) {
/*  72 */         SupplierComposition<?, ?> that = (SupplierComposition)obj;
/*  73 */         return (this.function.equals(that.function)) && (this.supplier.equals(that.supplier));
/*     */       }
/*  75 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/*  79 */       return Objects.hashCode(new Object[] { this.function, this.supplier });
/*     */     }
/*     */     
/*     */     public String toString() {
/*  83 */       String str1 = String.valueOf(String.valueOf(this.function));String str2 = String.valueOf(String.valueOf(this.supplier));return 21 + str1.length() + str2.length() + "Suppliers.compose(" + str1 + ", " + str2 + ")";
/*     */     }
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
/*     */   public static <T> Supplier<T> memoize(Supplier<T> delegate)
/*     */   {
/* 103 */     return (delegate instanceof MemoizingSupplier) ? delegate : new MemoizingSupplier((Supplier)Preconditions.checkNotNull(delegate));
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static class MemoizingSupplier<T>
/*     */     implements Supplier<T>, Serializable
/*     */   {
/*     */     final Supplier<T> delegate;
/*     */     volatile transient boolean initialized;
/*     */     transient T value;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     MemoizingSupplier(Supplier<T> delegate)
/*     */     {
/* 117 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     public T get()
/*     */     {
/* 122 */       if (!this.initialized) {
/* 123 */         synchronized (this) {
/* 124 */           if (!this.initialized) {
/* 125 */             T t = this.delegate.get();
/* 126 */             this.value = t;
/* 127 */             this.initialized = true;
/* 128 */             return t;
/*     */           }
/*     */         }
/*     */       }
/* 132 */       return (T)this.value;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 136 */       String str = String.valueOf(String.valueOf(this.delegate));return 19 + str.length() + "Suppliers.memoize(" + str + ")";
/*     */     }
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
/*     */   public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, long duration, TimeUnit unit)
/*     */   {
/* 162 */     return new ExpiringMemoizingSupplier(delegate, duration, unit);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable
/*     */   {
/*     */     final Supplier<T> delegate;
/*     */     final long durationNanos;
/*     */     volatile transient T value;
/*     */     volatile transient long expirationNanos;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ExpiringMemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
/* 175 */       this.delegate = ((Supplier)Preconditions.checkNotNull(delegate));
/* 176 */       this.durationNanos = unit.toNanos(duration);
/* 177 */       Preconditions.checkArgument(duration > 0L);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public T get()
/*     */     {
/* 187 */       long nanos = this.expirationNanos;
/* 188 */       long now = Platform.systemNanoTime();
/* 189 */       if ((nanos == 0L) || (now - nanos >= 0L)) {
/* 190 */         synchronized (this) {
/* 191 */           if (nanos == this.expirationNanos) {
/* 192 */             T t = this.delegate.get();
/* 193 */             this.value = t;
/* 194 */             nanos = now + this.durationNanos;
/*     */             
/*     */ 
/* 197 */             this.expirationNanos = (nanos == 0L ? 1L : nanos);
/* 198 */             return t;
/*     */           }
/*     */         }
/*     */       }
/* 202 */       return (T)this.value;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 208 */       String str = String.valueOf(String.valueOf(this.delegate));long l = this.durationNanos;return 62 + str.length() + "Suppliers.memoizeWithExpiration(" + str + ", " + l + ", NANOS)";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Supplier<T> ofInstance(@Nullable T instance)
/*     */   {
/* 219 */     return new SupplierOfInstance(instance);
/*     */   }
/*     */   
/*     */   private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
/*     */     final T instance;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     SupplierOfInstance(@Nullable T instance) {
/* 227 */       this.instance = instance;
/*     */     }
/*     */     
/*     */     public T get() {
/* 231 */       return (T)this.instance;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 235 */       if ((obj instanceof SupplierOfInstance)) {
/* 236 */         SupplierOfInstance<?> that = (SupplierOfInstance)obj;
/* 237 */         return Objects.equal(this.instance, that.instance);
/*     */       }
/* 239 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 243 */       return Objects.hashCode(new Object[] { this.instance });
/*     */     }
/*     */     
/*     */     public String toString() {
/* 247 */       String str = String.valueOf(String.valueOf(this.instance));return 22 + str.length() + "Suppliers.ofInstance(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate)
/*     */   {
/* 258 */     return new ThreadSafeSupplier((Supplier)Preconditions.checkNotNull(delegate));
/*     */   }
/*     */   
/*     */   private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {
/*     */     final Supplier<T> delegate;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ThreadSafeSupplier(Supplier<T> delegate) {
/* 266 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public T get()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 27	com/facebook/presto/jdbc/internal/guava/base/Suppliers$ThreadSafeSupplier:delegate	Lcom/facebook/presto/jdbc/internal/guava/base/Supplier;
/*     */       //   4: dup
/*     */       //   5: astore_1
/*     */       //   6: monitorenter
/*     */       //   7: aload_0
/*     */       //   8: getfield 27	com/facebook/presto/jdbc/internal/guava/base/Suppliers$ThreadSafeSupplier:delegate	Lcom/facebook/presto/jdbc/internal/guava/base/Supplier;
/*     */       //   11: invokeinterface 34 1 0
/*     */       //   16: aload_1
/*     */       //   17: monitorexit
/*     */       //   18: areturn
/*     */       //   19: astore_2
/*     */       //   20: aload_1
/*     */       //   21: monitorexit
/*     */       //   22: aload_2
/*     */       //   23: athrow
/*     */       // Line number table:
/*     */       //   Java source line #270	-> byte code offset #0
/*     */       //   Java source line #271	-> byte code offset #7
/*     */       //   Java source line #272	-> byte code offset #19
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	24	0	this	ThreadSafeSupplier<T>
/*     */       //   5	16	1	Ljava/lang/Object;	Object
/*     */       //   19	4	2	localObject1	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   7	18	19	finally
/*     */       //   19	22	19	finally
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 276 */       String str = String.valueOf(String.valueOf(this.delegate));return 32 + str.length() + "Suppliers.synchronizedSupplier(" + str + ")";
/*     */     }
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
/*     */   public static <T> Function<Supplier<T>, T> supplierFunction()
/*     */   {
/* 291 */     SupplierFunction<T> sf = SupplierFunctionImpl.INSTANCE;
/* 292 */     return sf;
/*     */   }
/*     */   
/*     */   private static abstract interface SupplierFunction<T> extends Function<Supplier<T>, T>
/*     */   {}
/*     */   
/* 298 */   private static enum SupplierFunctionImpl implements SupplierFunction<Object> { INSTANCE;
/*     */     
/*     */     private SupplierFunctionImpl() {}
/*     */     
/* 302 */     public Object apply(Supplier<Object> input) { return input.get(); }
/*     */     
/*     */     public String toString()
/*     */     {
/* 306 */       return "Suppliers.supplierFunction()";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Suppliers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */