/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.collect.AbstractIterator;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class CharSource
/*     */   implements InputSupplier<Reader>
/*     */ {
/*     */   public abstract Reader openStream()
/*     */     throws IOException;
/*     */   
/*     */   @Deprecated
/*     */   public final Reader getInput()
/*     */     throws IOException
/*     */   {
/*  93 */     return openStream();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferedReader openBufferedStream()
/*     */     throws IOException
/*     */   {
/* 105 */     Reader reader = openStream();
/* 106 */     return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long copyTo(Appendable appendable)
/*     */     throws IOException
/*     */   {
/* 119 */     Preconditions.checkNotNull(appendable);
/*     */     
/* 121 */     Closer closer = Closer.create();
/*     */     try {
/* 123 */       Reader reader = (Reader)closer.register(openStream());
/* 124 */       return CharStreams.copy(reader, appendable);
/*     */     } catch (Throwable e) {
/* 126 */       throw closer.rethrow(e);
/*     */     } finally {
/* 128 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long copyTo(CharSink sink)
/*     */     throws IOException
/*     */   {
/* 139 */     Preconditions.checkNotNull(sink);
/*     */     
/* 141 */     Closer closer = Closer.create();
/*     */     try {
/* 143 */       Reader reader = (Reader)closer.register(openStream());
/* 144 */       Writer writer = (Writer)closer.register(sink.openStream());
/* 145 */       return CharStreams.copy(reader, writer);
/*     */     } catch (Throwable e) {
/* 147 */       throw closer.rethrow(e);
/*     */     } finally {
/* 149 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String read()
/*     */     throws IOException
/*     */   {
/* 159 */     Closer closer = Closer.create();
/*     */     try {
/* 161 */       Reader reader = (Reader)closer.register(openStream());
/* 162 */       return CharStreams.toString(reader);
/*     */     } catch (Throwable e) {
/* 164 */       throw closer.rethrow(e);
/*     */     } finally {
/* 166 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Nullable
/*     */   public String readFirstLine()
/*     */     throws IOException
/*     */   {
/* 180 */     Closer closer = Closer.create();
/*     */     try {
/* 182 */       BufferedReader reader = (BufferedReader)closer.register(openBufferedStream());
/* 183 */       return reader.readLine();
/*     */     } catch (Throwable e) {
/* 185 */       throw closer.rethrow(e);
/*     */     } finally {
/* 187 */       closer.close();
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
/*     */   public ImmutableList<String> readLines()
/*     */     throws IOException
/*     */   {
/* 202 */     Closer closer = Closer.create();
/*     */     try {
/* 204 */       BufferedReader reader = (BufferedReader)closer.register(openBufferedStream());
/* 205 */       List<String> result = Lists.newArrayList();
/*     */       String line;
/* 207 */       while ((line = reader.readLine()) != null) {
/* 208 */         result.add(line);
/*     */       }
/* 210 */       return ImmutableList.copyOf(result);
/*     */     } catch (Throwable e) {
/* 212 */       throw closer.rethrow(e);
/*     */     } finally {
/* 214 */       closer.close();
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
/*     */   @Beta
/*     */   public <T> T readLines(LineProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 234 */     Preconditions.checkNotNull(processor);
/*     */     
/* 236 */     Closer closer = Closer.create();
/*     */     try {
/* 238 */       Reader reader = (Reader)closer.register(openStream());
/* 239 */       return (T)CharStreams.readLines(reader, processor);
/*     */     } catch (Throwable e) {
/* 241 */       throw closer.rethrow(e);
/*     */     } finally {
/* 243 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws IOException
/*     */   {
/* 255 */     Closer closer = Closer.create();
/*     */     try {
/* 257 */       Reader reader = (Reader)closer.register(openStream());
/* 258 */       return reader.read() == -1;
/*     */     } catch (Throwable e) {
/* 260 */       throw closer.rethrow(e);
/*     */     } finally {
/* 262 */       closer.close();
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
/*     */   public static CharSource concat(Iterable<? extends CharSource> sources)
/*     */   {
/* 278 */     return new ConcatenatedCharSource(sources);
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
/*     */   public static CharSource concat(Iterator<? extends CharSource> sources)
/*     */   {
/* 300 */     return concat(ImmutableList.copyOf(sources));
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
/*     */   public static CharSource concat(CharSource... sources)
/*     */   {
/* 316 */     return concat(ImmutableList.copyOf(sources));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CharSource wrap(CharSequence charSequence)
/*     */   {
/* 327 */     return new CharSequenceCharSource(charSequence);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CharSource empty()
/*     */   {
/* 336 */     return EmptyCharSource.INSTANCE;
/*     */   }
/*     */   
/*     */   private static class CharSequenceCharSource extends CharSource
/*     */   {
/* 341 */     private static final Splitter LINE_SPLITTER = Splitter.on(Pattern.compile("\r\n|\n|\r"));
/*     */     
/*     */     private final CharSequence seq;
/*     */     
/*     */     protected CharSequenceCharSource(CharSequence seq)
/*     */     {
/* 347 */       this.seq = ((CharSequence)Preconditions.checkNotNull(seq));
/*     */     }
/*     */     
/*     */     public Reader openStream()
/*     */     {
/* 352 */       return new CharSequenceReader(this.seq);
/*     */     }
/*     */     
/*     */     public String read()
/*     */     {
/* 357 */       return this.seq.toString();
/*     */     }
/*     */     
/*     */     public boolean isEmpty()
/*     */     {
/* 362 */       return this.seq.length() == 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Iterable<String> lines()
/*     */     {
/* 371 */       new Iterable()
/*     */       {
/*     */         public Iterator<String> iterator() {
/* 374 */           new AbstractIterator() {
/* 375 */             Iterator<String> lines = CharSequenceCharSource.LINE_SPLITTER.split(CharSequenceCharSource.this.seq).iterator();
/*     */             
/*     */             protected String computeNext()
/*     */             {
/* 379 */               if (this.lines.hasNext()) {
/* 380 */                 String next = (String)this.lines.next();
/*     */                 
/* 382 */                 if ((this.lines.hasNext()) || (!next.isEmpty())) {
/* 383 */                   return next;
/*     */                 }
/*     */               }
/* 386 */               return (String)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */     public String readFirstLine()
/*     */     {
/* 395 */       Iterator<String> lines = lines().iterator();
/* 396 */       return lines.hasNext() ? (String)lines.next() : null;
/*     */     }
/*     */     
/*     */     public ImmutableList<String> readLines()
/*     */     {
/* 401 */       return ImmutableList.copyOf(lines());
/*     */     }
/*     */     
/*     */     public <T> T readLines(LineProcessor<T> processor) throws IOException
/*     */     {
/* 406 */       for (String line : lines()) {
/* 407 */         if (!processor.processLine(line)) {
/*     */           break;
/*     */         }
/*     */       }
/* 411 */       return (T)processor.getResult();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 416 */       CharSequence shortened = this.seq.subSequence(0, 12) + "...";
/* 417 */       return "CharSource.wrap(" + shortened + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class EmptyCharSource extends CharSequenceCharSource
/*     */   {
/* 423 */     private static final EmptyCharSource INSTANCE = new EmptyCharSource();
/*     */     
/*     */     private EmptyCharSource() {
/* 426 */       super();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 431 */       return "CharSource.empty()";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ConcatenatedCharSource extends CharSource
/*     */   {
/*     */     private final Iterable<? extends CharSource> sources;
/*     */     
/*     */     ConcatenatedCharSource(Iterable<? extends CharSource> sources) {
/* 440 */       this.sources = ((Iterable)Preconditions.checkNotNull(sources));
/*     */     }
/*     */     
/*     */     public Reader openStream() throws IOException
/*     */     {
/* 445 */       return new MultiReader(this.sources.iterator());
/*     */     }
/*     */     
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 450 */       for (CharSource source : this.sources) {
/* 451 */         if (!source.isEmpty()) {
/* 452 */           return false;
/*     */         }
/*     */       }
/* 455 */       return true;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 460 */       return "CharSource.concat(" + this.sources + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\CharSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */