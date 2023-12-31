/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.hash.Funnels;
/*     */ import com.google.common.hash.HashCode;
/*     */ import com.google.common.hash.HashFunction;
/*     */ import com.google.common.hash.Hasher;
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
/*     */ public abstract class ByteSource
/*     */   implements InputSupplier<InputStream>
/*     */ {
/*     */   private static final int BUF_SIZE = 4096;
/*     */   
/*     */   public CharSource asCharSource(Charset charset)
/*     */   {
/*  72 */     return new AsCharSource(charset, null);
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
/*     */   @Deprecated
/*     */   public final InputStream getInput()
/*     */     throws IOException
/*     */   {
/*  97 */     return openStream();
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
/*     */   public InputStream openBufferedStream()
/*     */     throws IOException
/*     */   {
/* 113 */     InputStream in = openStream();
/* 114 */     return (in instanceof BufferedInputStream) ? (BufferedInputStream)in : new BufferedInputStream(in);
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
/* 126 */     return new SlicedByteSource(offset, length, null);
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
/* 137 */     Closer closer = Closer.create();
/*     */     try {
/* 139 */       InputStream in = (InputStream)closer.register(openStream());
/* 140 */       return in.read() == -1;
/*     */     } catch (Throwable e) {
/* 142 */       throw closer.rethrow(e);
/*     */     } finally {
/* 144 */       closer.close();
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
/* 164 */     Closer closer = Closer.create();
/*     */     long l;
/* 166 */     try { InputStream in = (InputStream)closer.register(openStream());
/* 167 */       return countBySkipping(in);
/*     */     }
/*     */     catch (IOException e) {}finally
/*     */     {
/* 171 */       closer.close();
/*     */     }
/*     */     
/* 174 */     closer = Closer.create();
/*     */     try {
/* 176 */       InputStream in = (InputStream)closer.register(openStream());
/* 177 */       return countByReading(in);
/*     */     } catch (Throwable e) {
/* 179 */       throw closer.rethrow(e);
/*     */     } finally {
/* 181 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private long countBySkipping(InputStream in)
/*     */     throws IOException
/*     */   {
/* 190 */     long count = 0L;
/*     */     
/*     */     for (;;)
/*     */     {
/* 194 */       long skipped = in.skip(Math.min(in.available(), Integer.MAX_VALUE));
/* 195 */       if (skipped <= 0L) {
/* 196 */         if (in.read() == -1)
/* 197 */           return count;
/* 198 */         if ((count == 0L) && (in.available() == 0))
/*     */         {
/*     */ 
/* 201 */           throw new IOException();
/*     */         }
/* 203 */         count += 1L;
/*     */       } else {
/* 205 */         count += skipped;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 210 */   private static final byte[] countBuffer = new byte['က'];
/*     */   
/*     */   private long countByReading(InputStream in) throws IOException {
/* 213 */     long count = 0L;
/*     */     long read;
/* 215 */     while ((read = in.read(countBuffer)) != -1L) {
/* 216 */       count += read;
/*     */     }
/* 218 */     return count;
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
/* 229 */     Preconditions.checkNotNull(output);
/*     */     
/* 231 */     Closer closer = Closer.create();
/*     */     try {
/* 233 */       InputStream in = (InputStream)closer.register(openStream());
/* 234 */       return ByteStreams.copy(in, output);
/*     */     } catch (Throwable e) {
/* 236 */       throw closer.rethrow(e);
/*     */     } finally {
/* 238 */       closer.close();
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
/* 249 */     Preconditions.checkNotNull(sink);
/*     */     
/* 251 */     Closer closer = Closer.create();
/*     */     try {
/* 253 */       InputStream in = (InputStream)closer.register(openStream());
/* 254 */       OutputStream out = (OutputStream)closer.register(sink.openStream());
/* 255 */       return ByteStreams.copy(in, out);
/*     */     } catch (Throwable e) {
/* 257 */       throw closer.rethrow(e);
/*     */     } finally {
/* 259 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] read()
/*     */     throws IOException
/*     */   {
/* 269 */     Closer closer = Closer.create();
/*     */     try {
/* 271 */       InputStream in = (InputStream)closer.register(openStream());
/* 272 */       return ByteStreams.toByteArray(in);
/*     */     } catch (Throwable e) {
/* 274 */       throw closer.rethrow(e);
/*     */     } finally {
/* 276 */       closer.close();
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
/* 291 */     Preconditions.checkNotNull(processor);
/*     */     
/* 293 */     Closer closer = Closer.create();
/*     */     try {
/* 295 */       InputStream in = (InputStream)closer.register(openStream());
/* 296 */       return (T)ByteStreams.readBytes(in, processor);
/*     */     } catch (Throwable e) {
/* 298 */       throw closer.rethrow(e);
/*     */     } finally {
/* 300 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public HashCode hash(HashFunction hashFunction)
/*     */     throws IOException
/*     */   {
/* 310 */     Hasher hasher = hashFunction.newHasher();
/* 311 */     copyTo(Funnels.asOutputStream(hasher));
/* 312 */     return hasher.hash();
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean contentEquals(ByteSource other)
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokestatic 29	com/google/common/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: sipush 4096
/*     */     //   8: newarray <illegal type>
/*     */     //   10: astore_2
/*     */     //   11: sipush 4096
/*     */     //   14: newarray <illegal type>
/*     */     //   16: astore_3
/*     */     //   17: invokestatic 10	com/google/common/io/Closer:create	()Lcom/google/common/io/Closer;
/*     */     //   20: astore 4
/*     */     //   22: aload 4
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual 5	com/google/common/io/ByteSource:openStream	()Ljava/io/InputStream;
/*     */     //   28: invokevirtual 11	com/google/common/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
/*     */     //   31: checkcast 12	java/io/InputStream
/*     */     //   34: astore 5
/*     */     //   36: aload 4
/*     */     //   38: aload_1
/*     */     //   39: invokevirtual 5	com/google/common/io/ByteSource:openStream	()Ljava/io/InputStream;
/*     */     //   42: invokevirtual 11	com/google/common/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
/*     */     //   45: checkcast 12	java/io/InputStream
/*     */     //   48: astore 6
/*     */     //   50: aload 5
/*     */     //   52: aload_2
/*     */     //   53: iconst_0
/*     */     //   54: sipush 4096
/*     */     //   57: invokestatic 39	com/google/common/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
/*     */     //   60: istore 7
/*     */     //   62: aload 6
/*     */     //   64: aload_3
/*     */     //   65: iconst_0
/*     */     //   66: sipush 4096
/*     */     //   69: invokestatic 39	com/google/common/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
/*     */     //   72: istore 8
/*     */     //   74: iload 7
/*     */     //   76: iload 8
/*     */     //   78: if_icmpne +11 -> 89
/*     */     //   81: aload_2
/*     */     //   82: aload_3
/*     */     //   83: invokestatic 40	java/util/Arrays:equals	([B[B)Z
/*     */     //   86: ifne +14 -> 100
/*     */     //   89: iconst_0
/*     */     //   90: istore 9
/*     */     //   92: aload 4
/*     */     //   94: invokevirtual 14	com/google/common/io/Closer:close	()V
/*     */     //   97: iload 9
/*     */     //   99: ireturn
/*     */     //   100: iload 7
/*     */     //   102: sipush 4096
/*     */     //   105: if_icmpeq +14 -> 119
/*     */     //   108: iconst_1
/*     */     //   109: istore 9
/*     */     //   111: aload 4
/*     */     //   113: invokevirtual 14	com/google/common/io/Closer:close	()V
/*     */     //   116: iload 9
/*     */     //   118: ireturn
/*     */     //   119: goto -69 -> 50
/*     */     //   122: astore 5
/*     */     //   124: aload 4
/*     */     //   126: aload 5
/*     */     //   128: invokevirtual 16	com/google/common/io/Closer:rethrow	(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
/*     */     //   131: athrow
/*     */     //   132: astore 10
/*     */     //   134: aload 4
/*     */     //   136: invokevirtual 14	com/google/common/io/Closer:close	()V
/*     */     //   139: aload 10
/*     */     //   141: athrow
/*     */     // Line number table:
/*     */     //   Java source line #323	-> byte code offset #0
/*     */     //   Java source line #325	-> byte code offset #5
/*     */     //   Java source line #326	-> byte code offset #11
/*     */     //   Java source line #328	-> byte code offset #17
/*     */     //   Java source line #330	-> byte code offset #22
/*     */     //   Java source line #331	-> byte code offset #36
/*     */     //   Java source line #333	-> byte code offset #50
/*     */     //   Java source line #334	-> byte code offset #62
/*     */     //   Java source line #335	-> byte code offset #74
/*     */     //   Java source line #336	-> byte code offset #89
/*     */     //   Java source line #344	-> byte code offset #92
/*     */     //   Java source line #337	-> byte code offset #100
/*     */     //   Java source line #338	-> byte code offset #108
/*     */     //   Java source line #344	-> byte code offset #111
/*     */     //   Java source line #340	-> byte code offset #119
/*     */     //   Java source line #341	-> byte code offset #122
/*     */     //   Java source line #342	-> byte code offset #124
/*     */     //   Java source line #344	-> byte code offset #132
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
/* 360 */     return new ConcatenatedByteSource(sources);
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
/* 382 */     return concat(ImmutableList.copyOf(sources));
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
/* 398 */     return concat(ImmutableList.copyOf(sources));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteSource wrap(byte[] b)
/*     */   {
/* 408 */     return new ByteArrayByteSource(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteSource empty()
/*     */   {
/* 417 */     return EmptyByteSource.INSTANCE;
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
/* 429 */       this.charset = ((Charset)Preconditions.checkNotNull(charset));
/*     */     }
/*     */     
/*     */     public Reader openStream() throws IOException
/*     */     {
/* 434 */       return new InputStreamReader(ByteSource.this.openStream(), this.charset);
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 439 */       return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
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
/* 452 */       Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(offset) });
/* 453 */       Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(length) });
/* 454 */       this.offset = offset;
/* 455 */       this.length = length;
/*     */     }
/*     */     
/*     */     public InputStream openStream() throws IOException
/*     */     {
/* 460 */       return sliceStream(ByteSource.this.openStream());
/*     */     }
/*     */     
/*     */     public InputStream openBufferedStream() throws IOException
/*     */     {
/* 465 */       return sliceStream(ByteSource.this.openBufferedStream());
/*     */     }
/*     */     
/*     */     private InputStream sliceStream(InputStream in) throws IOException {
/* 469 */       if (this.offset > 0L) {
/*     */         try {
/* 471 */           ByteStreams.skipFully(in, this.offset);
/*     */         } catch (Throwable e) {
/* 473 */           Closer closer = Closer.create();
/* 474 */           closer.register(in);
/*     */           try {
/* 476 */             throw closer.rethrow(e);
/*     */           } finally {
/* 478 */             closer.close();
/*     */           }
/*     */         }
/*     */       }
/* 482 */       return ByteStreams.limit(in, this.length);
/*     */     }
/*     */     
/*     */     public ByteSource slice(long offset, long length)
/*     */     {
/* 487 */       Preconditions.checkArgument(offset >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(offset) });
/* 488 */       Preconditions.checkArgument(length >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(length) });
/* 489 */       long maxLength = this.length - offset;
/* 490 */       return ByteSource.this.slice(this.offset + offset, Math.min(length, maxLength));
/*     */     }
/*     */     
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 495 */       return (this.length == 0L) || (super.isEmpty());
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 500 */       return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ByteArrayByteSource extends ByteSource
/*     */   {
/*     */     protected final byte[] bytes;
/*     */     
/*     */     protected ByteArrayByteSource(byte[] bytes) {
/* 509 */       this.bytes = ((byte[])Preconditions.checkNotNull(bytes));
/*     */     }
/*     */     
/*     */     public InputStream openStream()
/*     */     {
/* 514 */       return new ByteArrayInputStream(this.bytes);
/*     */     }
/*     */     
/*     */     public InputStream openBufferedStream() throws IOException
/*     */     {
/* 519 */       return openStream();
/*     */     }
/*     */     
/*     */     public boolean isEmpty()
/*     */     {
/* 524 */       return this.bytes.length == 0;
/*     */     }
/*     */     
/*     */     public long size()
/*     */     {
/* 529 */       return this.bytes.length;
/*     */     }
/*     */     
/*     */     public byte[] read()
/*     */     {
/* 534 */       return (byte[])this.bytes.clone();
/*     */     }
/*     */     
/*     */     public long copyTo(OutputStream output) throws IOException
/*     */     {
/* 539 */       output.write(this.bytes);
/* 540 */       return this.bytes.length;
/*     */     }
/*     */     
/*     */     public <T> T read(ByteProcessor<T> processor) throws IOException
/*     */     {
/* 545 */       processor.processBytes(this.bytes, 0, this.bytes.length);
/* 546 */       return (T)processor.getResult();
/*     */     }
/*     */     
/*     */     public HashCode hash(HashFunction hashFunction) throws IOException
/*     */     {
/* 551 */       return hashFunction.hashBytes(this.bytes);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 558 */       return "ByteSource.wrap(" + BaseEncoding.base16().encode(this.bytes) + ")";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class EmptyByteSource extends ByteArrayByteSource
/*     */   {
/* 564 */     private static final EmptyByteSource INSTANCE = new EmptyByteSource();
/*     */     
/*     */     private EmptyByteSource() {
/* 567 */       super();
/*     */     }
/*     */     
/*     */     public CharSource asCharSource(Charset charset)
/*     */     {
/* 572 */       Preconditions.checkNotNull(charset);
/* 573 */       return CharSource.empty();
/*     */     }
/*     */     
/*     */     public byte[] read()
/*     */     {
/* 578 */       return this.bytes;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 583 */       return "ByteSource.empty()";
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ConcatenatedByteSource extends ByteSource
/*     */   {
/*     */     private final Iterable<? extends ByteSource> sources;
/*     */     
/*     */     ConcatenatedByteSource(Iterable<? extends ByteSource> sources) {
/* 592 */       this.sources = ((Iterable)Preconditions.checkNotNull(sources));
/*     */     }
/*     */     
/*     */     public InputStream openStream() throws IOException
/*     */     {
/* 597 */       return new MultiInputStream(this.sources.iterator());
/*     */     }
/*     */     
/*     */     public boolean isEmpty() throws IOException
/*     */     {
/* 602 */       for (ByteSource source : this.sources) {
/* 603 */         if (!source.isEmpty()) {
/* 604 */           return false;
/*     */         }
/*     */       }
/* 607 */       return true;
/*     */     }
/*     */     
/*     */     public long size() throws IOException
/*     */     {
/* 612 */       long result = 0L;
/* 613 */       for (ByteSource source : this.sources) {
/* 614 */         result += source.size();
/*     */       }
/* 616 */       return result;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 621 */       return "ByteSource.concat(" + this.sources + ")";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\io\ByteSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */