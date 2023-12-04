/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public abstract class Converter<A, B>
/*     */   implements Function<A, B>
/*     */ {
/*     */   private final boolean handleNullAutomatically;
/*     */   private transient Converter<B, A> reverse;
/*     */   
/*     */   protected Converter()
/*     */   {
/* 103 */     this(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Converter(boolean handleNullAutomatically)
/*     */   {
/* 110 */     this.handleNullAutomatically = handleNullAutomatically;
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
/*     */   protected abstract B doForward(A paramA);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract A doBackward(B paramB);
/*     */   
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
/*     */   public final B convert(@Nullable A a)
/*     */   {
/* 147 */     return (B)correctedDoForward(a);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   B correctedDoForward(@Nullable A a) {
/* 152 */     if (this.handleNullAutomatically)
/*     */     {
/* 154 */       return a == null ? null : Preconditions.checkNotNull(doForward(a));
/*     */     }
/* 156 */     return (B)doForward(a);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   A correctedDoBackward(@Nullable B b)
/*     */   {
/* 162 */     if (this.handleNullAutomatically)
/*     */     {
/* 164 */       return b == null ? null : Preconditions.checkNotNull(doBackward(b));
/*     */     }
/* 166 */     return (A)doBackward(b);
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
/*     */   public Iterable<B> convertAll(final Iterable<? extends A> fromIterable)
/*     */   {
/* 179 */     Preconditions.checkNotNull(fromIterable, "fromIterable");
/* 180 */     new Iterable() {
/*     */       public Iterator<B> iterator() {
/* 182 */         new Iterator() {
/* 183 */           private final Iterator<? extends A> fromIterator = Converter.1.this.val$fromIterable.iterator();
/*     */           
/*     */           public boolean hasNext()
/*     */           {
/* 187 */             return this.fromIterator.hasNext();
/*     */           }
/*     */           
/*     */           public B next()
/*     */           {
/* 192 */             return (B)Converter.this.convert(this.fromIterator.next());
/*     */           }
/*     */           
/*     */           public void remove()
/*     */           {
/* 197 */             this.fromIterator.remove();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Converter<B, A> reverse()
/*     */   {
/* 212 */     Converter<B, A> result = this.reverse;
/* 213 */     return result == null ? (this.reverse = new ReverseConverter(this)) : result;
/*     */   }
/*     */   
/*     */   private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {
/*     */     final Converter<A, B> original;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ReverseConverter(Converter<A, B> original) {
/* 221 */       this.original = original;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected A doForward(B b)
/*     */     {
/* 233 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     protected B doBackward(A a)
/*     */     {
/* 238 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     A correctedDoForward(@Nullable B b)
/*     */     {
/* 244 */       return (A)this.original.correctedDoBackward(b);
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     B correctedDoBackward(@Nullable A a)
/*     */     {
/* 250 */       return (B)this.original.correctedDoForward(a);
/*     */     }
/*     */     
/*     */     public Converter<A, B> reverse()
/*     */     {
/* 255 */       return this.original;
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 260 */       if ((object instanceof ReverseConverter)) {
/* 261 */         ReverseConverter<?, ?> that = (ReverseConverter)object;
/* 262 */         return this.original.equals(that.original);
/*     */       }
/* 264 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 269 */       return this.original.hashCode() ^ 0xFFFFFFFF;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 274 */       String str = String.valueOf(String.valueOf(this.original));return 10 + str.length() + str + ".reverse()";
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
/*     */   public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter)
/*     */   {
/* 288 */     return doAndThen(secondConverter);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter)
/*     */   {
/* 295 */     return new ConverterComposition(this, (Converter)Preconditions.checkNotNull(secondConverter));
/*     */   }
/*     */   
/*     */   private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
/*     */     final Converter<A, B> first;
/*     */     final Converter<B, C> second;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
/* 304 */       this.first = first;
/* 305 */       this.second = second;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected C doForward(A a)
/*     */     {
/* 317 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     protected A doBackward(C c)
/*     */     {
/* 322 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     C correctedDoForward(@Nullable A a)
/*     */     {
/* 328 */       return (C)this.second.correctedDoForward(this.first.correctedDoForward(a));
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     A correctedDoBackward(@Nullable C c)
/*     */     {
/* 334 */       return (A)this.first.correctedDoBackward(this.second.correctedDoBackward(c));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 339 */       if ((object instanceof ConverterComposition)) {
/* 340 */         ConverterComposition<?, ?, ?> that = (ConverterComposition)object;
/* 341 */         return (this.first.equals(that.first)) && (this.second.equals(that.second));
/*     */       }
/*     */       
/* 344 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 349 */       return 31 * this.first.hashCode() + this.second.hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 354 */       String str1 = String.valueOf(String.valueOf(this.first));String str2 = String.valueOf(String.valueOf(this.second));return 10 + str1.length() + str2.length() + str1 + ".andThen(" + str2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   @Nullable
/*     */   public final B apply(@Nullable A a)
/*     */   {
/* 367 */     return (B)convert(a);
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
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 383 */     return super.equals(object);
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
/*     */   public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction)
/*     */   {
/* 405 */     return new FunctionBasedConverter(forwardFunction, backwardFunction, null);
/*     */   }
/*     */   
/*     */   private static final class FunctionBasedConverter<A, B>
/*     */     extends Converter<A, B> implements Serializable
/*     */   {
/*     */     private final Function<? super A, ? extends B> forwardFunction;
/*     */     private final Function<? super B, ? extends A> backwardFunction;
/*     */     
/*     */     private FunctionBasedConverter(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction)
/*     */     {
/* 416 */       this.forwardFunction = ((Function)Preconditions.checkNotNull(forwardFunction));
/* 417 */       this.backwardFunction = ((Function)Preconditions.checkNotNull(backwardFunction));
/*     */     }
/*     */     
/*     */     protected B doForward(A a)
/*     */     {
/* 422 */       return (B)this.forwardFunction.apply(a);
/*     */     }
/*     */     
/*     */     protected A doBackward(B b)
/*     */     {
/* 427 */       return (A)this.backwardFunction.apply(b);
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 432 */       if ((object instanceof FunctionBasedConverter)) {
/* 433 */         FunctionBasedConverter<?, ?> that = (FunctionBasedConverter)object;
/* 434 */         return (this.forwardFunction.equals(that.forwardFunction)) && (this.backwardFunction.equals(that.backwardFunction));
/*     */       }
/*     */       
/* 437 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 442 */       return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 447 */       String str1 = String.valueOf(String.valueOf(this.forwardFunction));String str2 = String.valueOf(String.valueOf(this.backwardFunction));return 18 + str1.length() + str2.length() + "Converter.from(" + str1 + ", " + str2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Converter<T, T> identity()
/*     */   {
/* 456 */     return IdentityConverter.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class IdentityConverter<T>
/*     */     extends Converter<T, T>
/*     */     implements Serializable
/*     */   {
/* 464 */     static final IdentityConverter INSTANCE = new IdentityConverter();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     protected T doForward(T t) {
/* 468 */       return t;
/*     */     }
/*     */     
/*     */     protected T doBackward(T t)
/*     */     {
/* 473 */       return t;
/*     */     }
/*     */     
/*     */     public IdentityConverter<T> reverse()
/*     */     {
/* 478 */       return this;
/*     */     }
/*     */     
/*     */     <S> Converter<T, S> doAndThen(Converter<T, S> otherConverter)
/*     */     {
/* 483 */       return (Converter)Preconditions.checkNotNull(otherConverter, "otherConverter");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 493 */       return "Converter.identity()";
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/* 497 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Converter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */