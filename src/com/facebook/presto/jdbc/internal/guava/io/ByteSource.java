/*     */ package com.facebook.presto.jdbc.internal.guava.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ascii;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.Funnels;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.HashCode;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.HashFunction;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.Hasher;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ByteSource
/*     */ {
/*     */   private static final int BUF_SIZE = 4096;
/*     */   
/*     */   public CharSource asCharSource(Charset charset)
/*     */   {
/*  73 */     return new AsCharSource(charset, null);
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
/*     */   public abstract InputStream openStream()
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public InputStream openBufferedStream()
/*     */     throws IOException
/*     */   {
/*  99 */     InputStream in = openStream();
/* 100 */     return (in instanceof BufferedInputStream) ? (BufferedInputStream)in : new BufferedInputStream(in);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteSource slice(long offset, long length)
/*     */   {
/* 112 */     return new SlicedByteSource(offset, length, null);
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
/* 123 */     Closer closer = Closer.create();
/*     */     try {
/* 125 */       InputStream in = (InputStream)closer.register(openStream());
/* 126 */       return in.read() == -1;
/*     */     } catch (Throwable e) {
/* 128 */       throw closer.rethrow(e);
/*     */     } finally {
/* 130 */       closer.close();
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
/*     */   public long size()
/*     */     throws IOException
/*     */   {
/* 150 */     Closer closer = Closer.create();
/*     */     long l;
/* 152 */     try { InputStream in = (InputStream)closer.register(openStream());
/* 153 */       return countBySkipping(in);
/*     */     }
/*     */     catch (IOException e) {}finally
/*     */     {
/* 157 */       closer.close();
/*     */     }
/*     */     
/* 160 */     closer = Closer.create();
/*     */     try {
/* 162 */       InputStream in = (InputStream)closer.register(openStream());
/* 163 */       return countByReading(in);
/*     */     } catch (Throwable e) {
/* 165 */       throw closer.rethrow(e);
/*     */     } finally {
/* 167 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private long countBySkipping(InputStream in)
/*     */     throws IOException
/*     */   {
/* 176 */     long count = 0L;
/*     */     
/*     */     for (;;)
/*     */     {
/* 180 */       long skipped = in.skip(Math.min(in.available(), Integer.MAX_VALUE));
/* 181 */       if (skipped <= 0L) {
/* 182 */         if (in.read() == -1)
/* 183 */           return count;
/* 184 */         if ((count == 0L) && (in.available() == 0))
/*     */         {
/*     */ 
/* 187 */           throw new IOException();
/*     */         }
/* 189 */         count += 1L;
/*     */       } else {
/* 191 */         count += skipped;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 196 */   private static final byte[] countBuffer = new byte['က'];
/*     */   
/*     */   private long countByReading(InputStream in) throws IOException {
/* 199 */     long count = 0L;
/*     */     long read;
/* 201 */     while ((read = in.read(countBuffer)) != -1L) {
/* 202 */       count += read;
/*     */     }
/* 204 */     return count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long copyTo(OutputStream output)
/*     */     throws IOException
/*     */   {
/* 215 */     Preconditions.checkNotNull(output);
/*     */     
/* 217 */     Closer closer = Closer.create();
/*     */     try {
/* 219 */       InputStream in = (InputStream)closer.register(openStream());
/* 220 */       return ByteStreams.copy(in, output);
/*     */     } catch (Throwable e) {
/* 222 */       throw closer.rethrow(e);
/*     */     } finally {
/* 224 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long copyTo(ByteSink sink)
/*     */     throws IOException
/*     */   {
/* 235 */     Preconditions.checkNotNull(sink);
/*     */     
/* 237 */     Closer closer = Closer.create();
/*     */     try {
/* 239 */       InputStream in = (InputStream)closer.register(openStream());
/* 240 */       OutputStream out = (OutputStream)closer.register(sink.openStream());
/* 241 */       return ByteStreams.copy(in, out);
/*     */     } catch (Throwable e) {
/* 243 */       throw closer.rethrow(e);
/*     */     } finally {
/* 245 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] read()
/*     */     throws IOException
/*     */   {
/* 255 */     Closer closer = Closer.create();
/*     */     try {
/* 257 */       InputStream in = (InputStream)closer.register(openStream());
/* 258 */       return ByteStreams.toByteArray(in);
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
/*     */   @Beta
/*     */   public <T> T read(ByteProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 277 */     Preconditions.checkNotNull(processor);
/*     */     
/* 279 */     Closer closer = Closer.create();
/*     */     try {
/* 281 */       InputStream in = (InputStream)closer.register(openStream());
/* 282 */       return (T)ByteStreams.readBytes(in, processor);
/*     */     } catch (Throwable e) {
/* 284 */       throw closer.rethrow(e);
/*     */     } finally {
/* 286 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HashCode hash(HashFunction hashFunction)
/*     */     throws IOException
/*     */   {
/* 296 */     Hasher hasher = hashFunction.newHasher();
/* 297 */     copyTo(Funnels.asOutputStream(hasher));
/* 298 */     return hasher.hash();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean contentEquals(ByteSource other)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokestatic 135	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: sipush 4096
/*     */     //   8: newarray <illegal type>
/*     */     //   10: astore_2
/*     */     //   11: sipush 4096
/*     */     //   14: newarray <illegal type>
/*     */     //   16: astore_3
/*     */     //   17: invokestatic 74	com/facebook/presto/jdbc/internal/guava/io/Closer:create	()Lcom/facebook/presto/jdbc/internal/guava/io/Closer;
/*     */     //   20: astore 4
/*     */     //   22: aload 4
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual 47	com/facebook/presto/jdbc/internal/guava/io/ByteSource:openStream	()Ljava/io/InputStream;
/*     */     //   28: invokevirtual 78	com/facebook/presto/jdbc/internal/guava/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
/*     */     //   31: checkcast 51	java/io/InputStream
/*     */     //   34: astore 5
/*     */     //   36: aload 4
/*     */     //   38: aload_1
/*     */     //   39: invokevirtual 47	com/facebook/presto/jdbc/internal/guava/io/ByteSource:openStream	()Ljava/io/InputStream;
/*     */     //   42: invokevirtual 78	com/facebook/presto/jdbc/internal/guava/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
/*     */     //   45: checkcast 51	java/io/InputStream
/*     */     //   48: astore 6
/*     */     //   50: aload 5
/*     */     //   52: aload_2
/*     */     //   53: iconst_0
/*     */     //   54: sipush 4096
/*     */     //   57: invokestatic 201	com/facebook/presto/jdbc/internal/guava/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
/*     */     //   60: istore 7
/*     */     //   62: aload 6
/*     */     //   64: aload_3
/*     */     //   65: iconst_0
/*     */     //   66: sipush 4096
/*     */     //   69: invokestatic 201	com/facebook/presto/jdbc/internal/guava/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
/*     */     //   72: istore 8
/*     */     //   74: iload 7
/*     */     //   76: iload 8
/*     */     //   78: if_icmpne +11 -> 89
/*     */     //   81: aload_2
/*     */     //   82: aload_3
/*     */     //   83: invokestatic 207	java/util/Arrays:equals	([B[B)Z
/*     */     //   86: ifne +14 -> 100
/*     */     //   89: iconst_0
/*     */     //   90: istore 9
/*     */     //   92: aload 4
/*     */     //   94: invokevirtual 85	com/facebook/presto/jdbc/internal/guava/io/Closer:close	()V
/*     */     //   97: iload 9
/*     */     //   99: ireturn
/*     */     //   100: iload 7
/*     */     //   102: sipush 4096
/*     */     //   105: if_icmpeq +14 -> 119
/*     */     //   108: iconst_1
/*     */     //   109: istore 9
/*     */     //   111: aload 4
/*     */     //   113: invokevirtual 85	com/facebook/presto/jdbc/internal/guava/io/Closer:close	()V
/*     */     //   116: iload 9
/*     */     //   118: ireturn
/*     */     //   119: goto -69 -> 50
/*     */     //   122: astore 5
/*     */     //   124: aload 4
/*     */     //   126: aload 5
/*     */     //   128: invokevirtual 89	com/facebook/presto/jdbc/internal/guava/io/Closer:rethrow	(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
/*     */     //   131: athrow
/*     */     //   132: astore 10
/*     */     //   134: aload 4
/*     */     //   136: invokevirtual 85	com/facebook/presto/jdbc/internal/guava/io/Closer:close	()V
/*     */     //   139: aload 10
/*     */     //   141: athrow
/*     */     // Line number table:
/*     */     //   Java source line #309	-> byte code offset #0
/*     */     //   Java source line #311	-> byte code offset #5
/*     */     //   Java source line #312	-> byte code offset #11
/*     */     //   Java source line #314	-> byte code offset #17
/*     */     //   Java source line #316	-> byte code offset #22
/*     */     //   Java source line #317	-> byte code offset #36
/*     */     //   Java source line #319	-> byte code offset #50
/*     */     //   Java source line #320	-> byte code offset #62
/*     */     //   Java source line #321	-> byte code offset #74
/*     */     //   Java source line #322	-> byte code offset #89
/*     */     //   Java source line #330	-> byte code offset #92
/*     */     //   Java source line #323	-> byte code offset #100
/*     */     //   Java source line #324	-> byte code offset #108
/*     */     //   Java source line #330	-> byte code offset #111
/*     */     //   Java source line #326	-> byte code offset #119
/*     */     //   Java source line #327	-> byte code offset #122
/*     */     //   Java source line #328	-> byte code offset #124
/*     */     //   Java source line #330	-> byte code offset #132
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	142	0	this	ByteSource
/*     */     //   0	142	1	other	ByteSource
/*     */     //   10	72	2	buf1	byte[]
/*     */     //   16	67	3	buf2	byte[]
/*     */     //   20	115	4	closer	Closer
/*     */     //   34	17	5	in1	InputStream
/*     */     //   122	5	5	e	Throwable
/*     */     //   48	15	6	in2	InputStream
/*     */     //   60	41	7	read1	int
/*     */     //   72	5	8	read2	int
/*     */     //   90	27	9	bool	boolean
/*     */     //   132	8	10	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   22	92	122	java/lang/Throwable
/*     */     //   100	111	122	java/lang/Throwable
/*     */     //   119	122	122	java/lang/Throwable
/*     */     //   22	92	132	finally
/*     */     //   100	111	132	finally
/*     */     //   119	134	132	finally
/*     */   }
/*     */   
/*     */   public static ByteSource concat(Iterable<? extends ByteSource> sources)
/*     */   {
/* 346 */     return new ConcatenatedByteSource(sources);
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
/*     */   public static ByteSource concat(Iterator<? extends ByteSource> sources)
/*     */   {
/* 368 */     return concat(ImmutableList.copyOf(sources));
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
/*     */   public static ByteSource concat(ByteSource... sources)
/*     */   {
/* 384 */     return concat(ImmutableList.copyOf(sources));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteSource wrap(byte[] b)
/*     */   {
/* 394 */     return new ByteArrayByteSource(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteSource empty()
/*     */   {
/* 403 */     return EmptyByteSource.INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */   private final class AsCharSource
/*     */     extends CharSource
/*     */   {
/*     */     private final Charset charset;
/*     */     
/*     */ 
/*     */     private AsCharSource(Charset charset)
/*     */     {
/* 415 */       this.charset = ((Charset)Preconditions.checkNotNull(charset));
/*     */     }
/*     */     
/*     */     public Reader openStream() throws IOException
/*     */     {
/* 420 */       return new InputStreamReader(ByteSource.this.openStream(), this.charset);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 425 */       String str1 = String.valueOf(String.valueOf(ByteSource.this.toString()));String str2 = String.valueOf(String.valueOf(this.charset));return 15 + str1.length() + str2.length() + str1 + ".asCharSource(" + str2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private final class SlicedByteSource
/*     */     extends ByteSource
/*     */   {
/*     */     private final long offset;
/*     */     private final long length;
/*     */     
/*     */     private SlicedByteSource(long offset, long length)
/*     */     {
/* 438 */       Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(offset) });
/* 439 */       Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(length) });
/* 440 */       this.offset = offset;
/* 441 */       this.length = length;
/*     */     }
/*     */     
/*     */     public InputStream openStream() throws IOException
/*     */     {
/* 446 */       return sliceStream(ByteSource.this.openStream());
/*     */     }
/*     */     
/*     */     public InputStream openBufferedStream() throws IOException
/*     */     {
/* 451 */       return sliceStream(ByteSource.this.openBufferedStream());
/*     */     }
/*     */     
/*     */     private InputStream sliceStream(InputStream in) throws IOException {
/* 455 */       if (this.offset > 0L) {
/*     */         try {
/* 457 */           ByteStreams.skipFully(in, this.offset);
/*     */         } catch (Throwable e) {
/* 459 */           Closer closer = Closer.create();
/* 460 */           closer.register(in);
/*     */           try {
/* 462 */             throw closer.rethrow(e);
/*     */           } finally {
/* 464 */             closer.close();
/*     */           }
/*     */         }
/*     */       }
/* 468 */       return ByteStreams.limit(in, this.length);
/*     */     }
/*     */     
/*     */     public ByteSource slice(long offset, long length)
/*     */     {
/* 473 */       Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(offset) });
/* 474 */       Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(length) });
/* 475 */       long maxLength = this.length - offset;
/* 476 */       return ByteSource.this.slice(this.offset + offset, Math.min(length, maxLength));
/*     */     }
/*     */     
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 481 */       return (this.length == 0L) || (super.isEmpty());
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 486 */       String str = String.valueOf(String.valueOf(ByteSource.this.toString()));long l1 = this.offset;long l2 = this.length;return 50 + str.length() + str + ".slice(" + l1 + ", " + l2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ByteArrayByteSource extends ByteSource
/*     */   {
/*     */     protected final byte[] bytes;
/*     */     
/*     */     protected ByteArrayByteSource(byte[] bytes) {
/* 495 */       this.bytes = ((byte[])Preconditions.checkNotNull(bytes));
/*     */     }
/*     */     
/*     */     public InputStream openStream()
/*     */     {
/* 500 */       return new ByteArrayInputStream(this.bytes);
/*     */     }
/*     */     
/*     */     public InputStream openBufferedStream() throws IOException
/*     */     {
/* 505 */       return openStream();
/*     */     }
/*     */     
/*     */     public boolean isEmpty()
/*     */     {
/* 510 */       return this.bytes.length == 0;
/*     */     }
/*     */     
/*     */     public long size()
/*     */     {
/* 515 */       return this.bytes.length;
/*     */     }
/*     */     
/*     */     public byte[] read()
/*     */     {
/* 520 */       return (byte[])this.bytes.clone();
/*     */     }
/*     */     
/*     */     public long copyTo(OutputStream output) throws IOException
/*     */     {
/* 525 */       output.write(this.bytes);
/* 526 */       return this.bytes.length;
/*     */     }
/*     */     
/*     */     public <T> T read(ByteProcessor<T> processor) throws IOException
/*     */     {
/* 531 */       processor.processBytes(this.bytes, 0, this.bytes.length);
/* 532 */       return (T)processor.getResult();
/*     */     }
/*     */     
/*     */     public HashCode hash(HashFunction hashFunction) throws IOException
/*     */     {
/* 537 */       return hashFunction.hashBytes(this.bytes);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 544 */       String str = String.valueOf(String.valueOf(Ascii.truncate(BaseEncoding.base16().encode(this.bytes), 30, "...")));return 17 + str.length() + "ByteSource.wrap(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class EmptyByteSource
/*     */     extends ByteArrayByteSource
/*     */   {
/* 551 */     private static final EmptyByteSource INSTANCE = new EmptyByteSource();
/*     */     
/*     */     private EmptyByteSource() {
/* 554 */       super();
/*     */     }
/*     */     
/*     */     public CharSource asCharSource(Charset charset)
/*     */     {
/* 559 */       Preconditions.checkNotNull(charset);
/* 560 */       return CharSource.empty();
/*     */     }
/*     */     
/*     */     public byte[] read()
/*     */     {
/* 565 */       return this.bytes;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 570 */       return "ByteSource.empty()";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ConcatenatedByteSource extends ByteSource
/*     */   {
/*     */     private final Iterable<? extends ByteSource> sources;
/*     */     
/*     */     ConcatenatedByteSource(Iterable<? extends ByteSource> sources) {
/* 579 */       this.sources = ((Iterable)Preconditions.checkNotNull(sources));
/*     */     }
/*     */     
/*     */     public InputStream openStream() throws IOException
/*     */     {
/* 584 */       return new MultiInputStream(this.sources.iterator());
/*     */     }
/*     */     
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 589 */       for (ByteSource source : this.sources) {
/* 590 */         if (!source.isEmpty()) {
/* 591 */           return false;
/*     */         }
/*     */       }
/* 594 */       return true;
/*     */     }
/*     */     
/*     */     public long size() throws IOException
/*     */     {
/* 599 */       long result = 0L;
/* 600 */       for (ByteSource source : this.sources) {
/* 601 */         result += source.size();
/*     */       }
/* 603 */       return result;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 608 */       String str = String.valueOf(String.valueOf(this.sources));return 19 + str.length() + "ByteSource.concat(" + str + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\ByteSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */