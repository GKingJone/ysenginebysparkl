/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Booleans;
/*     */ import java.io.Serializable;
/*     */ import java.util.NoSuchElementException;
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
/*     */ @GwtCompatible
/*     */ abstract class Cut<C extends Comparable>
/*     */   implements Comparable<Cut<C>>, Serializable
/*     */ {
/*     */   final C endpoint;
/*     */   private static final long serialVersionUID = 0L;
/*     */   
/*     */   Cut(@Nullable C endpoint)
/*     */   {
/*  41 */     this.endpoint = endpoint;
/*     */   }
/*     */   
/*     */   abstract boolean isLessThan(C paramC);
/*     */   
/*     */   abstract BoundType typeAsLowerBound();
/*     */   
/*     */   abstract BoundType typeAsUpperBound();
/*     */   
/*     */   abstract Cut<C> withLowerBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
/*     */   
/*     */   abstract Cut<C> withUpperBoundType(BoundType paramBoundType, DiscreteDomain<C> paramDiscreteDomain);
/*     */   
/*     */   abstract void describeAsLowerBound(StringBuilder paramStringBuilder);
/*     */   
/*     */   abstract void describeAsUpperBound(StringBuilder paramStringBuilder);
/*     */   
/*     */   abstract C leastValueAbove(DiscreteDomain<C> paramDiscreteDomain);
/*     */   
/*     */   abstract C greatestValueBelow(DiscreteDomain<C> paramDiscreteDomain);
/*     */   
/*     */   Cut<C> canonical(DiscreteDomain<C> domain) {
/*  63 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public int compareTo(Cut<C> that)
/*     */   {
/*  69 */     if (that == belowAll()) {
/*  70 */       return 1;
/*     */     }
/*  72 */     if (that == aboveAll()) {
/*  73 */       return -1;
/*     */     }
/*  75 */     int result = Range.compareOrThrow(this.endpoint, that.endpoint);
/*  76 */     if (result != 0) {
/*  77 */       return result;
/*     */     }
/*     */     
/*  80 */     return Booleans.compare(this instanceof AboveValue, that instanceof AboveValue);
/*     */   }
/*     */   
/*     */   C endpoint()
/*     */   {
/*  85 */     return this.endpoint;
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/*  90 */     if ((obj instanceof Cut))
/*     */     {
/*  92 */       Cut<C> that = (Cut)obj;
/*     */       try {
/*  94 */         int compareResult = compareTo(that);
/*  95 */         return compareResult == 0;
/*     */       }
/*     */       catch (ClassCastException ignored) {}
/*     */     }
/*  99 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <C extends Comparable> Cut<C> belowAll()
/*     */   {
/* 108 */     return BelowAll.INSTANCE;
/*     */   }
/*     */   
/*     */   private static final class BelowAll
/*     */     extends Cut<Comparable<?>>
/*     */   {
/* 114 */     private static final BelowAll INSTANCE = new BelowAll();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 117 */     private BelowAll() { super(); }
/*     */     
/*     */     Comparable<?> endpoint() {
/* 120 */       throw new IllegalStateException("range unbounded on this side");
/*     */     }
/*     */     
/* 123 */     boolean isLessThan(Comparable<?> value) { return true; }
/*     */     
/*     */     BoundType typeAsLowerBound() {
/* 126 */       throw new IllegalStateException();
/*     */     }
/*     */     
/* 129 */     BoundType typeAsUpperBound() { throw new AssertionError("this statement should be unreachable"); }
/*     */     
/*     */     Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain)
/*     */     {
/* 133 */       throw new IllegalStateException();
/*     */     }
/*     */     
/*     */     Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 137 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */     
/* 140 */     void describeAsLowerBound(StringBuilder sb) { sb.append("(-∞"); }
/*     */     
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 143 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
/* 147 */       return domain.minValue();
/*     */     }
/*     */     
/*     */     Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
/* 151 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     Cut<Comparable<?>> canonical(DiscreteDomain<Comparable<?>> domain) {
/*     */       try {
/* 156 */         return Cut.belowValue(domain.minValue());
/*     */       } catch (NoSuchElementException e) {}
/* 158 */       return this;
/*     */     }
/*     */     
/*     */     public int compareTo(Cut<Comparable<?>> o) {
/* 162 */       return o == this ? 0 : -1;
/*     */     }
/*     */     
/* 165 */     public String toString() { return "-∞"; }
/*     */     
/*     */     private Object readResolve() {
/* 168 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <C extends Comparable> Cut<C> aboveAll()
/*     */   {
/* 179 */     return AboveAll.INSTANCE;
/*     */   }
/*     */   
/*     */   private static final class AboveAll extends Cut<Comparable<?>> {
/* 183 */     private static final AboveAll INSTANCE = new AboveAll();
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 186 */     private AboveAll() { super(); }
/*     */     
/*     */     Comparable<?> endpoint() {
/* 189 */       throw new IllegalStateException("range unbounded on this side");
/*     */     }
/*     */     
/* 192 */     boolean isLessThan(Comparable<?> value) { return false; }
/*     */     
/*     */     BoundType typeAsLowerBound() {
/* 195 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */     
/* 198 */     BoundType typeAsUpperBound() { throw new IllegalStateException(); }
/*     */     
/*     */     Cut<Comparable<?>> withLowerBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain)
/*     */     {
/* 202 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */     
/*     */     Cut<Comparable<?>> withUpperBoundType(BoundType boundType, DiscreteDomain<Comparable<?>> domain) {
/* 206 */       throw new IllegalStateException();
/*     */     }
/*     */     
/* 209 */     void describeAsLowerBound(StringBuilder sb) { throw new AssertionError(); }
/*     */     
/*     */     void describeAsUpperBound(StringBuilder sb) {
/* 212 */       sb.append("+∞)");
/*     */     }
/*     */     
/*     */     Comparable<?> leastValueAbove(DiscreteDomain<Comparable<?>> domain) {
/* 216 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     Comparable<?> greatestValueBelow(DiscreteDomain<Comparable<?>> domain) {
/* 220 */       return domain.maxValue();
/*     */     }
/*     */     
/* 223 */     public int compareTo(Cut<Comparable<?>> o) { return o == this ? 0 : 1; }
/*     */     
/*     */     public String toString() {
/* 226 */       return "+∞";
/*     */     }
/*     */     
/* 229 */     private Object readResolve() { return INSTANCE; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 235 */   static <C extends Comparable> Cut<C> belowValue(C endpoint) { return new BelowValue(endpoint); }
/*     */   
/*     */   private static final class BelowValue<C extends Comparable> extends Cut<C> {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 240 */     BelowValue(C endpoint) { super(); }
/*     */     
/*     */     boolean isLessThan(C value)
/*     */     {
/* 244 */       return Range.compareOrThrow(this.endpoint, value) <= 0;
/*     */     }
/*     */     
/* 247 */     BoundType typeAsLowerBound() { return BoundType.CLOSED; }
/*     */     
/*     */ 
/* 250 */     BoundType typeAsUpperBound() { return BoundType.OPEN; }
/*     */     
/*     */     Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 253 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 1: 
/* 255 */         return this;
/*     */       case 2: 
/* 257 */         C previous = domain.previous(this.endpoint);
/* 258 */         return previous == null ? Cut.belowAll() : new AboveValue(previous);
/*     */       }
/* 260 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 264 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 1: 
/* 266 */         C previous = domain.previous(this.endpoint);
/* 267 */         return previous == null ? Cut.aboveAll() : new AboveValue(previous);
/*     */       case 2: 
/* 269 */         return this;
/*     */       }
/* 271 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 275 */       sb.append('[').append(this.endpoint);
/*     */     }
/*     */     
/* 278 */     void describeAsUpperBound(StringBuilder sb) { sb.append(this.endpoint).append(')'); }
/*     */     
/*     */     C leastValueAbove(DiscreteDomain<C> domain) {
/* 281 */       return this.endpoint;
/*     */     }
/*     */     
/* 284 */     C greatestValueBelow(DiscreteDomain<C> domain) { return domain.previous(this.endpoint); }
/*     */     
/*     */     public int hashCode() {
/* 287 */       return this.endpoint.hashCode();
/*     */     }
/*     */     
/* 290 */     public String toString() { return "\\" + this.endpoint + "/"; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 296 */   static <C extends Comparable> Cut<C> aboveValue(C endpoint) { return new AboveValue(endpoint); }
/*     */   
/*     */   private static final class AboveValue<C extends Comparable> extends Cut<C> {
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/* 301 */     AboveValue(C endpoint) { super(); }
/*     */     
/*     */     boolean isLessThan(C value)
/*     */     {
/* 305 */       return Range.compareOrThrow(this.endpoint, value) < 0;
/*     */     }
/*     */     
/* 308 */     BoundType typeAsLowerBound() { return BoundType.OPEN; }
/*     */     
/*     */ 
/* 311 */     BoundType typeAsUpperBound() { return BoundType.CLOSED; }
/*     */     
/*     */     Cut<C> withLowerBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 314 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 2: 
/* 316 */         return this;
/*     */       case 1: 
/* 318 */         C next = domain.next(this.endpoint);
/* 319 */         return next == null ? Cut.belowAll() : belowValue(next);
/*     */       }
/* 321 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     Cut<C> withUpperBoundType(BoundType boundType, DiscreteDomain<C> domain) {
/* 325 */       switch (Cut.1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */       case 2: 
/* 327 */         C next = domain.next(this.endpoint);
/* 328 */         return next == null ? Cut.aboveAll() : belowValue(next);
/*     */       case 1: 
/* 330 */         return this;
/*     */       }
/* 332 */       throw new AssertionError();
/*     */     }
/*     */     
/*     */     void describeAsLowerBound(StringBuilder sb) {
/* 336 */       sb.append('(').append(this.endpoint);
/*     */     }
/*     */     
/* 339 */     void describeAsUpperBound(StringBuilder sb) { sb.append(this.endpoint).append(']'); }
/*     */     
/*     */     C leastValueAbove(DiscreteDomain<C> domain) {
/* 342 */       return domain.next(this.endpoint);
/*     */     }
/*     */     
/* 345 */     C greatestValueBelow(DiscreteDomain<C> domain) { return this.endpoint; }
/*     */     
/*     */     Cut<C> canonical(DiscreteDomain<C> domain) {
/* 348 */       C next = leastValueAbove(domain);
/* 349 */       return next != null ? belowValue(next) : Cut.aboveAll();
/*     */     }
/*     */     
/* 352 */     public int hashCode() { return this.endpoint.hashCode() ^ 0xFFFFFFFF; }
/*     */     
/*     */     public String toString() {
/* 355 */       return "/" + this.endpoint + "\\";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\Cut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */