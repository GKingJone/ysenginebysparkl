/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
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
/*    */ @GwtCompatible
/*    */ final class Present<T>
/*    */   extends Optional<T>
/*    */ {
/*    */   private final T reference;
/*    */   private static final long serialVersionUID = 0L;
/*    */   
/*    */   Present(T reference)
/*    */   {
/* 36 */     this.reference = reference;
/*    */   }
/*    */   
/*    */   public boolean isPresent() {
/* 40 */     return true;
/*    */   }
/*    */   
/*    */   public T get() {
/* 44 */     return (T)this.reference;
/*    */   }
/*    */   
/*    */   public T or(T defaultValue) {
/* 48 */     Preconditions.checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
/* 49 */     return (T)this.reference;
/*    */   }
/*    */   
/*    */   public Optional<T> or(Optional<? extends T> secondChoice) {
/* 53 */     Preconditions.checkNotNull(secondChoice);
/* 54 */     return this;
/*    */   }
/*    */   
/*    */   public T or(Supplier<? extends T> supplier) {
/* 58 */     Preconditions.checkNotNull(supplier);
/* 59 */     return (T)this.reference;
/*    */   }
/*    */   
/*    */   public T orNull() {
/* 63 */     return (T)this.reference;
/*    */   }
/*    */   
/*    */   public Set<T> asSet() {
/* 67 */     return Collections.singleton(this.reference);
/*    */   }
/*    */   
/*    */   public <V> Optional<V> transform(Function<? super T, V> function) {
/* 71 */     return new Present(Preconditions.checkNotNull(function.apply(this.reference), "the Function passed to Optional.transform() must not return null."));
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object object)
/*    */   {
/* 76 */     if ((object instanceof Present)) {
/* 77 */       Present<?> other = (Present)object;
/* 78 */       return this.reference.equals(other.reference);
/*    */     }
/* 80 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 84 */     return 1502476572 + this.reference.hashCode();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 88 */     return "Optional.of(" + this.reference + ")";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Present.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */