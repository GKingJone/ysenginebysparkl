/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
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
/* 256 */     return new ContainsPatternFromStringPredicate(pattern);
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
/*     */ 
/*     */   static abstract enum ObjectPredicate
/*     */     implements Predicate<Object>
/*     */   {
/* 277 */     ALWAYS_TRUE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */     ALWAYS_FALSE, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 295 */     IS_NULL, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 304 */     NOT_NULL;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private ObjectPredicate() {}
/*     */     
/*     */ 
/*     */ 
/*     */     <T> Predicate<T> withNarrowedType()
/*     */     {
/* 315 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class NotPredicate<T> implements Predicate<T>, Serializable {
/*     */     final Predicate<T> predicate;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     NotPredicate(Predicate<T> predicate) {
/* 324 */       this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t) {
/* 328 */       return !this.predicate.apply(t);
/*     */     }
/*     */     
/* 331 */     public int hashCode() { return this.predicate.hashCode() ^ 0xFFFFFFFF; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 334 */       if ((obj instanceof NotPredicate)) {
/* 335 */         NotPredicate<?> that = (NotPredicate)obj;
/* 336 */         return this.predicate.equals(that.predicate);
/*     */       }
/* 338 */       return false;
/*     */     }
/*     */     
/* 341 */     public String toString() { String str = String.valueOf(String.valueOf(this.predicate.toString()));return 16 + str.length() + "Predicates.not(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 346 */   private static final Joiner COMMA_JOINER = Joiner.on(',');
/*     */   
/*     */   private static class AndPredicate<T> implements Predicate<T>, Serializable {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private AndPredicate(List<? extends Predicate<? super T>> components) {
/* 353 */       this.components = components;
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/* 358 */       for (int i = 0; i < this.components.size(); i++) {
/* 359 */         if (!((Predicate)this.components.get(i)).apply(t)) {
/* 360 */           return false;
/*     */         }
/*     */       }
/* 363 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 367 */     public int hashCode() { return this.components.hashCode() + 306654252; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 370 */       if ((obj instanceof AndPredicate)) {
/* 371 */         AndPredicate<?> that = (AndPredicate)obj;
/* 372 */         return this.components.equals(that.components);
/*     */       }
/* 374 */       return false;
/*     */     }
/*     */     
/* 377 */     public String toString() { String str = String.valueOf(String.valueOf(Predicates.COMMA_JOINER.join(this.components)));return 16 + str.length() + "Predicates.and(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class OrPredicate<T> implements Predicate<T>, Serializable
/*     */   {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private OrPredicate(List<? extends Predicate<? super T>> components) {
/* 387 */       this.components = components;
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/* 392 */       for (int i = 0; i < this.components.size(); i++) {
/* 393 */         if (((Predicate)this.components.get(i)).apply(t)) {
/* 394 */           return true;
/*     */         }
/*     */       }
/* 397 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 401 */     public int hashCode() { return this.components.hashCode() + 87855567; }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 404 */       if ((obj instanceof OrPredicate)) {
/* 405 */         OrPredicate<?> that = (OrPredicate)obj;
/* 406 */         return this.components.equals(that.components);
/*     */       }
/* 408 */       return false;
/*     */     }
/*     */     
/* 411 */     public String toString() { String str = String.valueOf(String.valueOf(Predicates.COMMA_JOINER.join(this.components)));return 15 + str.length() + "Predicates.or(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable
/*     */   {
/*     */     private final T target;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private IsEqualToPredicate(T target)
/*     */     {
/* 422 */       this.target = target;
/*     */     }
/*     */     
/*     */     public boolean apply(T t) {
/* 426 */       return this.target.equals(t);
/*     */     }
/*     */     
/* 429 */     public int hashCode() { return this.target.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 432 */       if ((obj instanceof IsEqualToPredicate)) {
/* 433 */         IsEqualToPredicate<?> that = (IsEqualToPredicate)obj;
/* 434 */         return this.target.equals(that.target);
/*     */       }
/* 436 */       return false;
/*     */     }
/*     */     
/* 439 */     public String toString() { String str = String.valueOf(String.valueOf(this.target));return 20 + str.length() + "Predicates.equalTo(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   private static class InstanceOfPredicate implements Predicate<Object>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private InstanceOfPredicate(Class<?> clazz)
/*     */     {
/* 451 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable Object o) {
/* 455 */       return this.clazz.isInstance(o);
/*     */     }
/*     */     
/* 458 */     public int hashCode() { return this.clazz.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 461 */       if ((obj instanceof InstanceOfPredicate)) {
/* 462 */         InstanceOfPredicate that = (InstanceOfPredicate)obj;
/* 463 */         return this.clazz == that.clazz;
/*     */       }
/* 465 */       return false;
/*     */     }
/*     */     
/* 468 */     public String toString() { String str = String.valueOf(String.valueOf(this.clazz.getName()));return 23 + str.length() + "Predicates.instanceOf(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Class.isAssignableFrom")
/*     */   private static class AssignableFromPredicate implements Predicate<Class<?>>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private AssignableFromPredicate(Class<?> clazz)
/*     */     {
/* 480 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */     
/*     */     public boolean apply(Class<?> input) {
/* 484 */       return this.clazz.isAssignableFrom(input);
/*     */     }
/*     */     
/* 487 */     public int hashCode() { return this.clazz.hashCode(); }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 490 */       if ((obj instanceof AssignableFromPredicate)) {
/* 491 */         AssignableFromPredicate that = (AssignableFromPredicate)obj;
/* 492 */         return this.clazz == that.clazz;
/*     */       }
/* 494 */       return false;
/*     */     }
/*     */     
/* 497 */     public String toString() { String str = String.valueOf(String.valueOf(this.clazz.getName()));return 27 + str.length() + "Predicates.assignableFrom(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class InPredicate<T> implements Predicate<T>, Serializable
/*     */   {
/*     */     private final Collection<?> target;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     private InPredicate(Collection<?> target) {
/* 507 */       this.target = ((Collection)Preconditions.checkNotNull(target));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable T t)
/*     */     {
/*     */       try {
/* 513 */         return this.target.contains(t);
/*     */       } catch (NullPointerException e) {
/* 515 */         return false;
/*     */       } catch (ClassCastException e) {}
/* 517 */       return false;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj)
/*     */     {
/* 522 */       if ((obj instanceof InPredicate)) {
/* 523 */         InPredicate<?> that = (InPredicate)obj;
/* 524 */         return this.target.equals(that.target);
/*     */       }
/* 526 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 530 */       return this.target.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 534 */       String str = String.valueOf(String.valueOf(this.target));return 15 + str.length() + "Predicates.in(" + str + ")";
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
/* 546 */       this.p = ((Predicate)Preconditions.checkNotNull(p));
/* 547 */       this.f = ((Function)Preconditions.checkNotNull(f));
/*     */     }
/*     */     
/*     */     public boolean apply(@Nullable A a)
/*     */     {
/* 552 */       return this.p.apply(this.f.apply(a));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 556 */       if ((obj instanceof CompositionPredicate)) {
/* 557 */         CompositionPredicate<?, ?> that = (CompositionPredicate)obj;
/* 558 */         return (this.f.equals(that.f)) && (this.p.equals(that.p));
/*     */       }
/* 560 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 564 */       return this.f.hashCode() ^ this.p.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 568 */       String str1 = String.valueOf(String.valueOf(this.p.toString()));String str2 = String.valueOf(String.valueOf(this.f.toString()));return 2 + str1.length() + str2.length() + str1 + "(" + str2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Only used by other GWT-incompatible code.")
/*     */   private static class ContainsPatternPredicate
/*     */     implements Predicate<CharSequence>, Serializable
/*     */   {
/*     */     final Pattern pattern;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ContainsPatternPredicate(Pattern pattern)
/*     */     {
/* 581 */       this.pattern = ((Pattern)Preconditions.checkNotNull(pattern));
/*     */     }
/*     */     
/*     */     public boolean apply(CharSequence t)
/*     */     {
/* 586 */       return this.pattern.matcher(t).find();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 593 */       return Objects.hashCode(new Object[] { this.pattern.pattern(), Integer.valueOf(this.pattern.flags()) });
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object obj) {
/* 597 */       if ((obj instanceof ContainsPatternPredicate)) {
/* 598 */         ContainsPatternPredicate that = (ContainsPatternPredicate)obj;
/*     */         
/*     */ 
/*     */ 
/* 602 */         return (Objects.equal(this.pattern.pattern(), that.pattern.pattern())) && (Objects.equal(Integer.valueOf(this.pattern.flags()), Integer.valueOf(that.pattern.flags())));
/*     */       }
/*     */       
/* 605 */       return false;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 609 */       String patternString = Objects.toStringHelper(this.pattern).add("pattern", this.pattern.pattern()).add("pattern.flags", this.pattern.flags()).toString();
/*     */       
/*     */ 
/*     */ 
/* 613 */       String str1 = String.valueOf(String.valueOf(patternString));return 21 + str1.length() + "Predicates.contains(" + str1 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   @GwtIncompatible("Only used by other GWT-incompatible code.")
/*     */   private static class ContainsPatternFromStringPredicate
/*     */     extends ContainsPatternPredicate
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ContainsPatternFromStringPredicate(String string)
/*     */     {
/* 625 */       super();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 629 */       String str = String.valueOf(String.valueOf(this.pattern.pattern()));return 28 + str.length() + "Predicates.containsPattern(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 638 */     return Arrays.asList(new Predicate[] { first, second });
/*     */   }
/*     */   
/*     */   private static <T> List<T> defensiveCopy(T... array) {
/* 642 */     return defensiveCopy(Arrays.asList(array));
/*     */   }
/*     */   
/*     */   static <T> List<T> defensiveCopy(Iterable<T> iterable) {
/* 646 */     ArrayList<T> list = new ArrayList();
/* 647 */     for (T element : iterable) {
/* 648 */       list.add(Preconditions.checkNotNull(element));
/*     */     }
/* 650 */     return list;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Predicates.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */