/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
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
/*     */ @GwtCompatible
/*     */ public final class Functions
/*     */ {
/*     */   public static Function<Object, String> toStringFunction()
/*     */   {
/*  56 */     return ToStringFunction.INSTANCE;
/*     */   }
/*     */   
/*     */   private static enum ToStringFunction implements Function<Object, String>
/*     */   {
/*  61 */     INSTANCE;
/*     */     
/*     */     private ToStringFunction() {}
/*     */     
/*  65 */     public String apply(Object o) { Preconditions.checkNotNull(o);
/*  66 */       return o.toString();
/*     */     }
/*     */     
/*     */     public String toString() {
/*  70 */       return "toString";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <E> Function<E, E> identity()
/*     */   {
/*  80 */     return IdentityFunction.INSTANCE;
/*     */   }
/*     */   
/*     */   private static enum IdentityFunction implements Function<Object, Object>
/*     */   {
/*  85 */     INSTANCE;
/*     */     
/*     */     private IdentityFunction() {}
/*     */     
/*     */     @Nullable
/*  90 */     public Object apply(@Nullable Object o) { return o; }
/*     */     
/*     */     public String toString()
/*     */     {
/*  94 */       return "identity";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <K, V> Function<K, V> forMap(Map<K, V> map)
/*     */   {
/* 103 */     return new FunctionForMapNoDefault(map);
/*     */   }
/*     */   
/*     */   private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {
/*     */     final Map<K, V> map;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 110 */     FunctionForMapNoDefault(Map<K, V> map) { this.map = ((Map)Preconditions.checkNotNull(map)); }
/*     */     
/*     */ 
/*     */     public V apply(@Nullable K key)
/*     */     {
/* 115 */       V result = this.map.get(key);
/* 116 */       Preconditions.checkArgument((result != null) || (this.map.containsKey(key)), "Key '%s' not present in map", new Object[] { key });
/* 117 */       return result;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/* 121 */       if ((o instanceof FunctionForMapNoDefault)) {
/* 122 */         FunctionForMapNoDefault<?, ?> that = (FunctionForMapNoDefault)o;
/* 123 */         return this.map.equals(that.map);
/*     */       }
/* 125 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 129 */       return this.map.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 133 */       return "forMap(" + this.map + ")";
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
/* 150 */   public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, @Nullable V defaultValue) { return new ForMapWithDefault(map, defaultValue); }
/*     */   
/*     */   private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable {
/*     */     final Map<K, ? extends V> map;
/*     */     final V defaultValue;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ForMapWithDefault(Map<K, ? extends V> map, @Nullable V defaultValue) {
/* 158 */       this.map = ((Map)Preconditions.checkNotNull(map));
/* 159 */       this.defaultValue = defaultValue;
/*     */     }
/*     */     
/*     */     public V apply(@Nullable K key)
/*     */     {
/* 164 */       V result = this.map.get(key);
/* 165 */       return (result != null) || (this.map.containsKey(key)) ? result : this.defaultValue;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/* 169 */       if ((o instanceof ForMapWithDefault)) {
/* 170 */         ForMapWithDefault<?, ?> that = (ForMapWithDefault)o;
/* 171 */         return (this.map.equals(that.map)) && (Objects.equal(this.defaultValue, that.defaultValue));
/*     */       }
/* 173 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 177 */       return Objects.hashCode(new Object[] { this.map, this.defaultValue });
/*     */     }
/*     */     
/*     */     public String toString() {
/* 181 */       return "forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
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
/* 197 */   public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f) { return new FunctionComposition(g, f); }
/*     */   
/*     */   private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable {
/*     */     private final Function<B, C> g;
/*     */     private final Function<A, ? extends B> f;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f) {
/* 205 */       this.g = ((Function)Preconditions.checkNotNull(g));
/* 206 */       this.f = ((Function)Preconditions.checkNotNull(f));
/*     */     }
/*     */     
/*     */     public C apply(@Nullable A a)
/*     */     {
/* 211 */       return (C)this.g.apply(this.f.apply(a));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 215 */       if ((obj instanceof FunctionComposition)) {
/* 216 */         FunctionComposition<?, ?, ?> that = (FunctionComposition)obj;
/* 217 */         return (this.f.equals(that.f)) && (this.g.equals(that.g));
/*     */       }
/* 219 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 223 */       return this.f.hashCode() ^ this.g.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 227 */       return this.g.toString() + "(" + this.f.toString() + ")";
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
/*     */   public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate)
/*     */   {
/* 240 */     return new PredicateFunction(predicate, null);
/*     */   }
/*     */   
/*     */   private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable {
/*     */     private final Predicate<T> predicate;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private PredicateFunction(Predicate<T> predicate) {
/* 248 */       this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */     }
/*     */     
/*     */     public Boolean apply(@Nullable T t)
/*     */     {
/* 253 */       return Boolean.valueOf(this.predicate.apply(t));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 257 */       if ((obj instanceof PredicateFunction)) {
/* 258 */         PredicateFunction<?> that = (PredicateFunction)obj;
/* 259 */         return this.predicate.equals(that.predicate);
/*     */       }
/* 261 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 265 */       return this.predicate.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 269 */       return "forPredicate(" + this.predicate + ")";
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
/*     */   public static <E> Function<Object, E> constant(@Nullable E value)
/*     */   {
/* 282 */     return new ConstantFunction(value);
/*     */   }
/*     */   
/*     */   private static class ConstantFunction<E> implements Function<Object, E>, Serializable {
/*     */     private final E value;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 289 */     public ConstantFunction(@Nullable E value) { this.value = value; }
/*     */     
/*     */ 
/*     */     public E apply(@Nullable Object from)
/*     */     {
/* 294 */       return (E)this.value;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 298 */       if ((obj instanceof ConstantFunction)) {
/* 299 */         ConstantFunction<?> that = (ConstantFunction)obj;
/* 300 */         return Objects.equal(this.value, that.value);
/*     */       }
/* 302 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 306 */       return this.value == null ? 0 : this.value.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 310 */       return "constant(" + this.value + ")";
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
/*     */   @Beta
/*     */   public static <T> Function<Object, T> forSupplier(Supplier<T> supplier)
/*     */   {
/* 324 */     return new SupplierFunction(supplier, null);
/*     */   }
/*     */   
/*     */   private static class SupplierFunction<T> implements Function<Object, T>, Serializable
/*     */   {
/*     */     private final Supplier<T> supplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private SupplierFunction(Supplier<T> supplier) {
/* 333 */       this.supplier = ((Supplier)Preconditions.checkNotNull(supplier));
/*     */     }
/*     */     
/*     */     public T apply(@Nullable Object input) {
/* 337 */       return (T)this.supplier.get();
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 341 */       if ((obj instanceof SupplierFunction)) {
/* 342 */         SupplierFunction<?> that = (SupplierFunction)obj;
/* 343 */         return this.supplier.equals(that.supplier);
/*     */       }
/* 345 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 349 */       return this.supplier.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 353 */       return "forSupplier(" + this.supplier + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Functions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */