/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Splitter
/*     */ {
/*     */   private final CharMatcher trimmer;
/*     */   private final boolean omitEmptyStrings;
/*     */   private final Strategy strategy;
/*     */   private final int limit;
/*     */   
/*     */   private Splitter(Strategy strategy)
/*     */   {
/* 110 */     this(strategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */   private Splitter(Strategy strategy, boolean omitEmptyStrings, CharMatcher trimmer, int limit)
/*     */   {
/* 115 */     this.strategy = strategy;
/* 116 */     this.omitEmptyStrings = omitEmptyStrings;
/* 117 */     this.trimmer = trimmer;
/* 118 */     this.limit = limit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Splitter on(char separator)
/*     */   {
/* 130 */     return on(CharMatcher.is(separator));
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
/*     */   public static Splitter on(CharMatcher separatorMatcher)
/*     */   {
/* 144 */     Preconditions.checkNotNull(separatorMatcher);
/*     */     
/* 146 */     new Splitter(new Strategy()
/*     */     {
/*     */       public SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 149 */         new SplittingIterator(splitter, toSplit) {
/*     */           int separatorStart(int start) {
/* 151 */             return Splitter.1.this.val$separatorMatcher.indexIn(this.toSplit, start);
/*     */           }
/*     */           
/*     */           int separatorEnd(int separatorPosition) {
/* 155 */             return separatorPosition + 1;
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Splitter on(String separator)
/*     */   {
/* 171 */     Preconditions.checkArgument(separator.length() != 0, "The separator may not be the empty string.");
/*     */     
/*     */ 
/* 174 */     new Splitter(new Strategy()
/*     */     {
/*     */       public SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 177 */         new SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 179 */             int separatorLength = Splitter.2.this.val$separator.length();
/*     */             
/*     */ 
/* 182 */             int p = start;int last = this.toSplit.length() - separatorLength;
/* 183 */             label80: for (; p <= last; p++) {
/* 184 */               for (int i = 0; i < separatorLength; i++) {
/* 185 */                 if (this.toSplit.charAt(i + p) != Splitter.2.this.val$separator.charAt(i)) {
/*     */                   break label80;
/*     */                 }
/*     */               }
/* 189 */               return p;
/*     */             }
/* 191 */             return -1;
/*     */           }
/*     */           
/*     */           public int separatorEnd(int separatorPosition) {
/* 195 */             return separatorPosition + Splitter.2.this.val$separator.length();
/*     */           }
/*     */         };
/*     */       }
/*     */     });
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
/*     */   @GwtIncompatible("java.util.regex")
/*     */   public static Splitter on(Pattern separatorPattern)
/*     */   {
/* 216 */     Preconditions.checkNotNull(separatorPattern);
/* 217 */     Preconditions.checkArgument(!separatorPattern.matcher("").matches(), "The pattern may not match the empty string: %s", new Object[] { separatorPattern });
/*     */     
/*     */ 
/* 220 */     new Splitter(new Strategy()
/*     */     {
/*     */       public SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 223 */         final Matcher matcher = this.val$separatorPattern.matcher(toSplit);
/* 224 */         new SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 226 */             return matcher.find(start) ? matcher.start() : -1;
/*     */           }
/*     */           
/*     */           public int separatorEnd(int separatorPosition) {
/* 230 */             return matcher.end();
/*     */           }
/*     */         };
/*     */       }
/*     */     });
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
/*     */   @GwtIncompatible("java.util.regex")
/*     */   public static Splitter onPattern(String separatorPattern)
/*     */   {
/* 254 */     return on(Pattern.compile(separatorPattern));
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
/*     */   public static Splitter fixedLength(int length)
/*     */   {
/* 277 */     Preconditions.checkArgument(length > 0, "The length may not be less than 1");
/*     */     
/* 279 */     new Splitter(new Strategy()
/*     */     {
/*     */       public SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 282 */         new SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 284 */             int nextChunkStart = start + Splitter.4.this.val$length;
/* 285 */             return nextChunkStart < this.toSplit.length() ? nextChunkStart : -1;
/*     */           }
/*     */           
/*     */           public int separatorEnd(int separatorPosition) {
/* 289 */             return separatorPosition;
/*     */           }
/*     */         };
/*     */       }
/*     */     });
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
/*     */   @CheckReturnValue
/*     */   public Splitter omitEmptyStrings()
/*     */   {
/* 316 */     return new Splitter(this.strategy, true, this.trimmer, this.limit);
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
/*     */   @CheckReturnValue
/*     */   public Splitter limit(int limit)
/*     */   {
/* 340 */     Preconditions.checkArgument(limit > 0, "must be greater than zero: %s", new Object[] { Integer.valueOf(limit) });
/* 341 */     return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, limit);
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
/*     */   @CheckReturnValue
/*     */   public Splitter trimResults()
/*     */   {
/* 356 */     return trimResults(CharMatcher.WHITESPACE);
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
/*     */   @CheckReturnValue
/*     */   public Splitter trimResults(CharMatcher trimmer)
/*     */   {
/* 373 */     Preconditions.checkNotNull(trimmer);
/* 374 */     return new Splitter(this.strategy, this.omitEmptyStrings, trimmer, this.limit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterable<String> split(final CharSequence sequence)
/*     */   {
/* 386 */     Preconditions.checkNotNull(sequence);
/*     */     
/* 388 */     new Iterable() {
/*     */       public Iterator<String> iterator() {
/* 390 */         return Splitter.this.splittingIterator(sequence);
/*     */       }
/*     */       
/* 393 */       public String toString() { return ']'; }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Iterator<String> splittingIterator(CharSequence sequence)
/*     */   {
/* 402 */     return this.strategy.iterator(this, sequence);
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
/*     */   public List<String> splitToList(CharSequence sequence)
/*     */   {
/* 416 */     Preconditions.checkNotNull(sequence);
/*     */     
/* 418 */     Iterator<String> iterator = splittingIterator(sequence);
/* 419 */     List<String> result = new ArrayList();
/*     */     
/* 421 */     while (iterator.hasNext()) {
/* 422 */       result.add(iterator.next());
/*     */     }
/*     */     
/* 425 */     return Collections.unmodifiableList(result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @CheckReturnValue
/*     */   @Beta
/*     */   public MapSplitter withKeyValueSeparator(String separator)
/*     */   {
/* 437 */     return withKeyValueSeparator(on(separator));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @CheckReturnValue
/*     */   @Beta
/*     */   public MapSplitter withKeyValueSeparator(char separator)
/*     */   {
/* 449 */     return withKeyValueSeparator(on(separator));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @CheckReturnValue
/*     */   @Beta
/*     */   public MapSplitter withKeyValueSeparator(Splitter keyValueSplitter)
/*     */   {
/* 462 */     return new MapSplitter(this, keyValueSplitter, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static final class MapSplitter
/*     */   {
/*     */     private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
/*     */     
/*     */ 
/*     */     private final Splitter outerSplitter;
/*     */     
/*     */     private final Splitter entrySplitter;
/*     */     
/*     */ 
/*     */     private MapSplitter(Splitter outerSplitter, Splitter entrySplitter)
/*     */     {
/* 480 */       this.outerSplitter = outerSplitter;
/* 481 */       this.entrySplitter = ((Splitter)Preconditions.checkNotNull(entrySplitter));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Map<String, String> split(CharSequence sequence)
/*     */     {
/* 500 */       Map<String, String> map = new LinkedHashMap();
/* 501 */       for (String entry : this.outerSplitter.split(sequence)) {
/* 502 */         Iterator<String> entryFields = this.entrySplitter.splittingIterator(entry);
/*     */         
/* 504 */         Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/* 505 */         String key = (String)entryFields.next();
/* 506 */         Preconditions.checkArgument(!map.containsKey(key), "Duplicate key [%s] found.", new Object[] { key });
/*     */         
/* 508 */         Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/* 509 */         String value = (String)entryFields.next();
/* 510 */         map.put(key, value);
/*     */         
/* 512 */         Preconditions.checkArgument(!entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/*     */       }
/* 514 */       return Collections.unmodifiableMap(map);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static abstract interface Strategy
/*     */   {
/*     */     public abstract Iterator<String> iterator(Splitter paramSplitter, CharSequence paramCharSequence);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static abstract class SplittingIterator
/*     */     extends AbstractIterator<String>
/*     */   {
/*     */     final CharSequence toSplit;
/*     */     
/*     */     final CharMatcher trimmer;
/*     */     
/*     */     final boolean omitEmptyStrings;
/*     */     
/*     */ 
/*     */     abstract int separatorStart(int paramInt);
/*     */     
/*     */ 
/* 540 */     int offset = 0;
/*     */     
/*     */     abstract int separatorEnd(int paramInt);
/*     */     
/* 544 */     protected SplittingIterator(Splitter splitter, CharSequence toSplit) { this.trimmer = splitter.trimmer;
/* 545 */       this.omitEmptyStrings = splitter.omitEmptyStrings;
/* 546 */       this.limit = splitter.limit;
/* 547 */       this.toSplit = toSplit;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     int limit;
/*     */     
/*     */ 
/*     */     protected String computeNext()
/*     */     {
/* 557 */       int nextStart = this.offset;
/* 558 */       while (this.offset != -1) {
/* 559 */         int start = nextStart;
/*     */         
/*     */ 
/* 562 */         int separatorPosition = separatorStart(this.offset);
/* 563 */         int end; if (separatorPosition == -1) {
/* 564 */           int end = this.toSplit.length();
/* 565 */           this.offset = -1;
/*     */         } else {
/* 567 */           end = separatorPosition;
/* 568 */           this.offset = separatorEnd(separatorPosition);
/*     */         }
/* 570 */         if (this.offset == nextStart)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 578 */           this.offset += 1;
/* 579 */           if (this.offset >= this.toSplit.length()) {
/* 580 */             this.offset = -1;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 585 */           while ((start < end) && (this.trimmer.matches(this.toSplit.charAt(start)))) {
/* 586 */             start++;
/*     */           }
/* 588 */           while ((end > start) && (this.trimmer.matches(this.toSplit.charAt(end - 1)))) {
/* 589 */             end--;
/*     */           }
/*     */           
/* 592 */           if ((this.omitEmptyStrings) && (start == end))
/*     */           {
/* 594 */             nextStart = this.offset;
/*     */           }
/*     */           else
/*     */           {
/* 598 */             if (this.limit == 1)
/*     */             {
/*     */ 
/*     */ 
/* 602 */               end = this.toSplit.length();
/* 603 */               this.offset = -1;
/*     */               
/* 605 */               while ((end > start) && (this.trimmer.matches(this.toSplit.charAt(end - 1)))) {
/* 606 */                 end--;
/*     */               }
/*     */             }
/* 609 */             this.limit -= 1;
/*     */             
/*     */ 
/* 612 */             return this.toSplit.subSequence(start, end).toString();
/*     */           } } }
/* 614 */       return (String)endOfData();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Splitter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */