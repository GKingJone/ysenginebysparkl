/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
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
/* 274 */       return this.original + ".reverse()";
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
/*     */   public <C> Converter<A, C> andThen(Converter<B, C> secondConverter)
/*     */   {
/* 288 */     return new ConverterComposition(this, (Converter)Preconditions.checkNotNull(secondConverter));
/*     */   }
/*     */   
/*     */   private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
/*     */     final Converter<A, B> first;
/*     */     final Converter<B, C> second;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
/* 297 */       this.first = first;
/* 298 */       this.second = second;
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
/* 310 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     protected A doBackward(C c)
/*     */     {
/* 315 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     C correctedDoForward(@Nullable A a)
/*     */     {
/* 321 */       return (C)this.second.correctedDoForward(this.first.correctedDoForward(a));
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     A correctedDoBackward(@Nullable C c)
/*     */     {
/* 327 */       return (A)this.first.correctedDoBackward(this.second.correctedDoBackward(c));
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 332 */       if ((object instanceof ConverterComposition)) {
/* 333 */         ConverterComposition<?, ?, ?> that = (ConverterComposition)object;
/* 334 */         return (this.first.equals(that.first)) && (this.second.equals(that.second));
/*     */       }
/*     */       
/* 337 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 342 */       return 31 * this.first.hashCode() + this.second.hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 347 */       return this.first + ".andThen(" + this.second + ")";
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
/* 360 */     return (B)convert(a);
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
/* 376 */     return super.equals(object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static <T> Converter<T, T> identity()
/*     */   {
/* 386 */     return IdentityConverter.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class IdentityConverter<T>
/*     */     extends Converter<T, T>
/*     */     implements Serializable
/*     */   {
/* 394 */     static final IdentityConverter INSTANCE = new IdentityConverter();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     protected T doForward(T t) {
/* 398 */       return t;
/*     */     }
/*     */     
/*     */     protected T doBackward(T t)
/*     */     {
/* 403 */       return t;
/*     */     }
/*     */     
/*     */     public IdentityConverter<T> reverse()
/*     */     {
/* 408 */       return this;
/*     */     }
/*     */     
/*     */     public <S> Converter<T, S> andThen(Converter<T, S> otherConverter)
/*     */     {
/* 413 */       return (Converter)Preconditions.checkNotNull(otherConverter, "otherConverter");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 423 */       return "Converter.identity()";
/*     */     }
/*     */     
/*     */     private Object readResolve() {
/* 427 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Converter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */