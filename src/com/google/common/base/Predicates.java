/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Predicates
/*     */ {
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> alwaysTrue()
/*     */   {
/*  59 */     return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> alwaysFalse()
/*     */   {
/*  67 */     return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> isNull()
/*     */   {
/*  76 */     return ObjectPredicate.IS_NULL.withNarrowedType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> notNull()
/*     */   {
/*  85 */     return ObjectPredicate.NOT_NULL.withNarrowedType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Predicate<T> not(Predicate<T> predicate)
/*     */   {
/*  93 */     return new NotPredicate(predicate);
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
/*     */   public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components)
/*     */   {
/* 107 */     return new AndPredicate(defensiveCopy(components), null);
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
/*     */   public static <T> Predicate<T> and(Predicate<? super T>... components)
/*     */   {
/* 120 */     return new AndPredicate(defensiveCopy(components), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 131 */     return new AndPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)), null);
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
/*     */   public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components)
/*     */   {
/* 146 */     return new OrPredicate(defensiveCopy(components), null);
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
/*     */   public static <T> Predicate<T> or(Predicate<? super T>... components)
/*     */   {
/* 159 */     return new OrPredicate(defensiveCopy(components), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Predicate<T> or(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 170 */     return new OrPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Predicate<T> equalTo(@Nullable T target)
/*     */   {
/* 179 */     return target == null ? isNull() : new IsEqualToPredicate(target, null);
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
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   public static Predicate<Object> instanceOf(Class<?> clazz)
/*     */   {
/* 201 */     return new InstanceOfPredicate(clazz, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("Class.isAssignableFrom")
/*     */   @Beta
/*     */   public static Predicate<Class<?>> assignableFrom(Class<?> clazz)
/*     */   {
/* 214 */     return new AssignableFromPredicate(clazz, null);
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
/*     */   public static <T> Predicate<T> in(Collection<? extends T> target)
/*     */   {
/* 231 */     return new InPredicate(target, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function)
/*     */   {
/* 242 */     return new CompositionPredicate(predicate, function, null);
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
/*     */   @GwtIncompatible("java.util.regex.Pattern")
/*     */   public static Predicate<CharSequence> containsPattern(String pattern)
/*     */   {
/* 256 */     return new ContainsPatternPredicate(pattern);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GwtIncompatible("java.util.regex.Pattern")
/*     */   public static Predicate<CharSequence> contains(Pattern pattern)
/*     */   {
/* 269 */     return new ContainsPatternPredicate(pattern);
/*     */   }
/*     */   
/*     */ 
/*     */   static abstract enum ObjectPredicate
/*     */     implements Predicate<Object>
/*     */   {
/* 276 */     ALWAYS_TRUE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 281 */     ALWAYS_FALSE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 286 */     IS_NULL, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 291 */     NOT_NULL;
/*     */     
/*     */ 
/*     */     private ObjectPredicate() {}
/*     */     
/*     */ 
/*     */     <T> Predicate<T> withNarrowedType()
/*     */     {
/* 299 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class NotPredicate<T> implements Predicate<T>, Serializable {
/*     */     final Predicate<T> predicate;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     NotPredicate(Predicate<T> predicate) {
/* 308 */       this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t) {
/* 312 */       return !this.predicate.apply(t);
/*     */     }
/*     */     
/* 315 */     public int hashCode() { return this.predicate.hashCode() ^ 0xFFFFFFFF; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 318 */       if ((obj instanceof NotPredicate)) {
/* 319 */         NotPredicate<?> that = (NotPredicate)obj;
/* 320 */         return this.predicate.equals(that.predicate);
/*     */       }
/* 322 */       return false;
/*     */     }
/*     */     
/* 325 */     public String toString() { return "Not(" + this.predicate.toString() + ")"; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 330 */   private static final Joiner COMMA_JOINER = Joiner.on(",");
/*     */   
/*     */   private static class AndPredicate<T> implements Predicate<T>, Serializable {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private AndPredicate(List<? extends Predicate<? super T>> components) {
/* 337 */       this.components = components;
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/* 342 */       for (int i = 0; i < this.components.size(); i++) {
/* 343 */         if (!((Predicate)this.components.get(i)).apply(t)) {
/* 344 */           return false;
/*     */         }
/*     */       }
/* 347 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 351 */     public int hashCode() { return this.components.hashCode() + 306654252; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 354 */       if ((obj instanceof AndPredicate)) {
/* 355 */         AndPredicate<?> that = (AndPredicate)obj;
/* 356 */         return this.components.equals(that.components);
/*     */       }
/* 358 */       return false;
/*     */     }
/*     */     
/* 361 */     public String toString() { return "And(" + Predicates.COMMA_JOINER.join(this.components) + ")"; }
/*     */   }
/*     */   
/*     */   private static class OrPredicate<T> implements Predicate<T>, Serializable
/*     */   {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private OrPredicate(List<? extends Predicate<? super T>> components)
/*     */     {
/* 371 */       this.components = components;
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/* 376 */       for (int i = 0; i < this.components.size(); i++) {
/* 377 */         if (((Predicate)this.components.get(i)).apply(t)) {
/* 378 */           return true;
/*     */         }
/*     */       }
/* 381 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 385 */     public int hashCode() { return this.components.hashCode() + 87855567; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 388 */       if ((obj instanceof OrPredicate)) {
/* 389 */         OrPredicate<?> that = (OrPredicate)obj;
/* 390 */         return this.components.equals(that.components);
/*     */       }
/* 392 */       return false;
/*     */     }
/*     */     
/* 395 */     public String toString() { return "Or(" + Predicates.COMMA_JOINER.join(this.components) + ")"; }
/*     */   }
/*     */   
/*     */   private static class IsEqualToPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     private final T target;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private IsEqualToPredicate(T target)
/*     */     {
/* 406 */       this.target = target;
/*     */     }
/*     */     
/*     */     public boolean apply(T t) {
/* 410 */       return this.target.equals(t);
/*     */     }
/*     */     
/* 413 */     public int hashCode() { return this.target.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 416 */       if ((obj instanceof IsEqualToPredicate)) {
/* 417 */         IsEqualToPredicate<?> that = (IsEqualToPredicate)obj;
/* 418 */         return this.target.equals(that.target);
/*     */       }
/* 420 */       return false;
/*     */     }
/*     */     
/* 423 */     public String toString() { return "IsEqualTo(" + this.target + ")"; }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   private static class InstanceOfPredicate
/*     */     implements Predicate<Object>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private InstanceOfPredicate(Class<?> clazz)
/*     */     {
/* 435 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable Object o) {
/* 439 */       return this.clazz.isInstance(o);
/*     */     }
/*     */     
/* 442 */     public int hashCode() { return this.clazz.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 445 */       if ((obj instanceof InstanceOfPredicate)) {
/* 446 */         InstanceOfPredicate that = (InstanceOfPredicate)obj;
/* 447 */         return this.clazz == that.clazz;
/*     */       }
/* 449 */       return false;
/*     */     }
/*     */     
/* 452 */     public String toString() { return "IsInstanceOf(" + this.clazz.getName() + ")"; }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Class.isAssignableFrom")
/*     */   private static class AssignableFromPredicate
/*     */     implements Predicate<Class<?>>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private AssignableFromPredicate(Class<?> clazz)
/*     */     {
/* 464 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */     
/*     */     public boolean apply(Class<?> input) {
/* 468 */       return this.clazz.isAssignableFrom(input);
/*     */     }
/*     */     
/* 471 */     public int hashCode() { return this.clazz.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 474 */       if ((obj instanceof AssignableFromPredicate)) {
/* 475 */         AssignableFromPredicate that = (AssignableFromPredicate)obj;
/* 476 */         return this.clazz == that.clazz;
/*     */       }
/* 478 */       return false;
/*     */     }
/*     */     
/* 481 */     public String toString() { return "IsAssignableFrom(" + this.clazz.getName() + ")"; }
/*     */   }
/*     */   
/*     */   private static class InPredicate<T> implements Predicate<T>, Serializable
/*     */   {
/*     */     private final Collection<?> target;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private InPredicate(Collection<?> target)
/*     */     {
/* 491 */       this.target = ((Collection)Preconditions.checkNotNull(target));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/*     */       try {
/* 497 */         return this.target.contains(t);
/*     */       } catch (NullPointerException e) {
/* 499 */         return false;
/*     */       } catch (ClassCastException e) {}
/* 501 */       return false;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj)
/*     */     {
/* 506 */       if ((obj instanceof InPredicate)) {
/* 507 */         InPredicate<?> that = (InPredicate)obj;
/* 508 */         return this.target.equals(that.target);
/*     */       }
/* 510 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 514 */       return this.target.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 518 */       return "In(" + this.target + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable
/*     */   {
/*     */     final Predicate<B> p;
/*     */     final Function<A, ? extends B> f;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f)
/*     */     {
/* 530 */       this.p = ((Predicate)Preconditions.checkNotNull(p));
/* 531 */       this.f = ((Function)Preconditions.checkNotNull(f));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable A a)
/*     */     {
/* 536 */       return this.p.apply(this.f.apply(a));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 540 */       if ((obj instanceof CompositionPredicate)) {
/* 541 */         CompositionPredicate<?, ?> that = (CompositionPredicate)obj;
/* 542 */         return (this.f.equals(that.f)) && (this.p.equals(that.p));
/*     */       }
/* 544 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 548 */       return this.f.hashCode() ^ this.p.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 552 */       return this.p.toString() + "(" + this.f.toString() + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GwtIncompatible("Only used by other GWT-incompatible code.")
/*     */   private static class ContainsPatternPredicate
/*     */     implements Predicate<CharSequence>, Serializable
/*     */   {
/*     */     final Pattern pattern;
/*     */     
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */ 
/*     */     ContainsPatternPredicate(Pattern pattern)
/*     */     {
/* 568 */       this.pattern = ((Pattern)Preconditions.checkNotNull(pattern));
/*     */     }
/*     */     
/*     */     ContainsPatternPredicate(String patternStr) {
/* 572 */       this(Pattern.compile(patternStr));
/*     */     }
/*     */     
/*     */     public boolean apply(CharSequence t)
/*     */     {
/* 577 */       return this.pattern.matcher(t).find();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 584 */       return Objects.hashCode(new Object[] { this.pattern.pattern(), Integer.valueOf(this.pattern.flags()) });
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 588 */       if ((obj instanceof ContainsPatternPredicate)) {
/* 589 */         ContainsPatternPredicate that = (ContainsPatternPredicate)obj;
/*     */         
/*     */ 
/*     */ 
/* 593 */         return (Objects.equal(this.pattern.pattern(), that.pattern.pattern())) && (Objects.equal(Integer.valueOf(this.pattern.flags()), Integer.valueOf(that.pattern.flags())));
/*     */       }
/*     */       
/* 596 */       return false;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 600 */       return Objects.toStringHelper(this).add("pattern", this.pattern).add("pattern.flags", Integer.toHexString(this.pattern.flags())).toString();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 612 */     return Arrays.asList(new Predicate[] { first, second });
/*     */   }
/*     */   
/*     */   private static <T> List<T> defensiveCopy(T... array) {
/* 616 */     return defensiveCopy(Arrays.asList(array));
/*     */   }
/*     */   
/*     */   static <T> List<T> defensiveCopy(Iterable<T> iterable) {
/* 620 */     ArrayList<T> list = new ArrayList();
/* 621 */     for (T element : iterable) {
/* 622 */       list.add(Preconditions.checkNotNull(element));
/*     */     }
/* 624 */     return list;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Predicates.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */