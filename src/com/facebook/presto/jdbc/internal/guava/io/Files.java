/*     */ package com.facebook.presto.jdbc.internal.guava.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.TreeTraverser;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.HashCode;
/*     */ import com.facebook.presto.jdbc.internal.guava.hash.HashFunction;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class Files
/*     */ {
/*     */   private static final int TEMP_DIR_ATTEMPTS = 10000;
/*     */   
/*     */   public static BufferedReader newReader(File file, Charset charset)
/*     */     throws FileNotFoundException
/*     */   {
/*  83 */     Preconditions.checkNotNull(file);
/*  84 */     Preconditions.checkNotNull(charset);
/*  85 */     return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
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
/*     */   public static BufferedWriter newWriter(File file, Charset charset)
/*     */     throws FileNotFoundException
/*     */   {
/* 100 */     Preconditions.checkNotNull(file);
/* 101 */     Preconditions.checkNotNull(charset);
/* 102 */     return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ByteSource asByteSource(File file)
/*     */   {
/* 112 */     return new FileByteSource(file, null);
/*     */   }
/*     */   
/*     */   private static final class FileByteSource extends ByteSource
/*     */   {
/*     */     private final File file;
/*     */     
/*     */     private FileByteSource(File file) {
/* 120 */       this.file = ((File)Preconditions.checkNotNull(file));
/*     */     }
/*     */     
/*     */     public FileInputStream openStream() throws IOException
/*     */     {
/* 125 */       return new FileInputStream(this.file);
/*     */     }
/*     */     
/*     */     public long size() throws IOException
/*     */     {
/* 130 */       if (!this.file.isFile()) {
/* 131 */         throw new FileNotFoundException(this.file.toString());
/*     */       }
/* 133 */       return this.file.length();
/*     */     }
/*     */     
/*     */     public byte[] read() throws IOException
/*     */     {
/* 138 */       Closer closer = Closer.create();
/*     */       try {
/* 140 */         FileInputStream in = (FileInputStream)closer.register(openStream());
/* 141 */         return Files.readFile(in, in.getChannel().size());
/*     */       } catch (Throwable e) {
/* 143 */         throw closer.rethrow(e);
/*     */       } finally {
/* 145 */         closer.close();
/*     */       }
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 151 */       String str = String.valueOf(String.valueOf(this.file));return 20 + str.length() + "Files.asByteSource(" + str + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static byte[] readFile(InputStream in, long expectedSize)
/*     */     throws IOException
/*     */   {
/* 163 */     if (expectedSize > 2147483647L) {
/* 164 */       long l = expectedSize;throw new OutOfMemoryError(68 + "file is too large to fit in a byte array: " + l + " bytes");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 170 */     return expectedSize == 0L ? ByteStreams.toByteArray(in) : ByteStreams.toByteArray(in, (int)expectedSize);
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
/*     */   public static ByteSink asByteSink(File file, FileWriteMode... modes)
/*     */   {
/* 185 */     return new FileByteSink(file, modes, null);
/*     */   }
/*     */   
/*     */   private static final class FileByteSink extends ByteSink
/*     */   {
/*     */     private final File file;
/*     */     private final ImmutableSet<FileWriteMode> modes;
/*     */     
/*     */     private FileByteSink(File file, FileWriteMode... modes) {
/* 194 */       this.file = ((File)Preconditions.checkNotNull(file));
/* 195 */       this.modes = ImmutableSet.copyOf(modes);
/*     */     }
/*     */     
/*     */     public FileOutputStream openStream() throws IOException
/*     */     {
/* 200 */       return new FileOutputStream(this.file, this.modes.contains(FileWriteMode.APPEND));
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 205 */       String str1 = String.valueOf(String.valueOf(this.file));String str2 = String.valueOf(String.valueOf(this.modes));return 20 + str1.length() + str2.length() + "Files.asByteSink(" + str1 + ", " + str2 + ")";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CharSource asCharSource(File file, Charset charset)
/*     */   {
/* 216 */     return asByteSource(file).asCharSource(charset);
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
/*     */   public static CharSink asCharSink(File file, Charset charset, FileWriteMode... modes)
/*     */   {
/* 231 */     return asByteSink(file, modes).asCharSink(charset);
/*     */   }
/*     */   
/*     */   private static FileWriteMode[] modes(boolean append) {
/* 235 */     return append ? new FileWriteMode[] { FileWriteMode.APPEND } : new FileWriteMode[0];
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
/*     */   public static byte[] toByteArray(File file)
/*     */     throws IOException
/*     */   {
/* 250 */     return asByteSource(file).read();
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
/*     */   public static String toString(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 264 */     return asCharSource(file, charset).read();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void write(byte[] from, File to)
/*     */     throws IOException
/*     */   {
/* 275 */     asByteSink(to, new FileWriteMode[0]).write(from);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void copy(File from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 286 */     asByteSource(from).copyTo(to);
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
/*     */   public static void copy(File from, File to)
/*     */     throws IOException
/*     */   {
/* 303 */     Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", new Object[] { from, to });
/*     */     
/* 305 */     asByteSource(from).copyTo(asByteSink(to, new FileWriteMode[0]));
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
/*     */   public static void write(CharSequence from, File to, Charset charset)
/*     */     throws IOException
/*     */   {
/* 320 */     asCharSink(to, charset, new FileWriteMode[0]).write(from);
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
/*     */   public static void append(CharSequence from, File to, Charset charset)
/*     */     throws IOException
/*     */   {
/* 335 */     write(from, to, charset, true);
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
/*     */   private static void write(CharSequence from, File to, Charset charset, boolean append)
/*     */     throws IOException
/*     */   {
/* 351 */     asCharSink(to, charset, modes(append)).write(from);
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
/*     */   public static void copy(File from, Charset charset, Appendable to)
/*     */     throws IOException
/*     */   {
/* 366 */     asCharSource(from, charset).copyTo(to);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean equal(File file1, File file2)
/*     */     throws IOException
/*     */   {
/* 375 */     Preconditions.checkNotNull(file1);
/* 376 */     Preconditions.checkNotNull(file2);
/* 377 */     if ((file1 == file2) || (file1.equals(file2))) {
/* 378 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 386 */     long len1 = file1.length();
/* 387 */     long len2 = file2.length();
/* 388 */     if ((len1 != 0L) && (len2 != 0L) && (len1 != len2)) {
/* 389 */       return false;
/*     */     }
/* 391 */     return asByteSource(file1).contentEquals(asByteSource(file2));
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
/*     */   public static File createTempDir()
/*     */   {
/* 414 */     File baseDir = new File(System.getProperty("java.io.tmpdir"));
/* 415 */     long l = System.currentTimeMillis();String baseName = 21 + l + "-";
/*     */     
/* 417 */     for (int counter = 0; counter < 10000; counter++) {
/* 418 */       str1 = String.valueOf(String.valueOf(baseName));i = counter;tempDir = new File(baseDir, 11 + str1.length() + str1 + i);
/* 419 */       if (tempDir.mkdir()) {
/* 420 */         return tempDir;
/*     */       }
/*     */     }
/* 423 */     counter = String.valueOf(String.valueOf("Failed to create directory within 10000 attempts (tried "));File tempDir = String.valueOf(String.valueOf(baseName));String str1 = String.valueOf(String.valueOf(baseName));int i = 9999;throw new IllegalStateException(17 + counter.length() + tempDir.length() + str1.length() + counter + tempDir + "0 to " + str1 + i + ")");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void touch(File file)
/*     */     throws IOException
/*     */   {
/* 436 */     Preconditions.checkNotNull(file);
/* 437 */     if ((!file.createNewFile()) && (!file.setLastModified(System.currentTimeMillis())))
/*     */     {
/* 439 */       String str = String.valueOf(String.valueOf(file));throw new IOException(38 + str.length() + "Unable to update modification time of " + str);
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
/*     */   public static void createParentDirs(File file)
/*     */     throws IOException
/*     */   {
/* 454 */     Preconditions.checkNotNull(file);
/* 455 */     File parent = file.getCanonicalFile().getParentFile();
/* 456 */     if (parent == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 464 */       return;
/*     */     }
/* 466 */     parent.mkdirs();
/* 467 */     if (!parent.isDirectory()) {
/* 468 */       String str = String.valueOf(String.valueOf(file));throw new IOException(39 + str.length() + "Unable to create parent directories of " + str);
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
/*     */   public static void move(File from, File to)
/*     */     throws IOException
/*     */   {
/* 484 */     Preconditions.checkNotNull(from);
/* 485 */     Preconditions.checkNotNull(to);
/* 486 */     Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", new Object[] { from, to });
/*     */     
/*     */ 
/* 489 */     if (!from.renameTo(to)) {
/* 490 */       copy(from, to);
/* 491 */       if (!from.delete()) {
/* 492 */         if (!to.delete()) {
/* 493 */           str = String.valueOf(String.valueOf(to));throw new IOException(17 + str.length() + "Unable to delete " + str);
/*     */         }
/* 495 */         String str = String.valueOf(String.valueOf(from));throw new IOException(17 + str.length() + "Unable to delete " + str);
/*     */       }
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
/*     */   public static String readFirstLine(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 513 */     return asCharSource(file, charset).readFirstLine();
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
/*     */   public static List<String> readLines(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 535 */     (List)readLines(file, charset, new LineProcessor() {
/* 536 */       final List<String> result = Lists.newArrayList();
/*     */       
/*     */       public boolean processLine(String line)
/*     */       {
/* 540 */         this.result.add(line);
/* 541 */         return true;
/*     */       }
/*     */       
/*     */       public List<String> getResult()
/*     */       {
/* 546 */         return this.result;
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
/*     */   public static <T> T readLines(File file, Charset charset, LineProcessor<T> callback)
/*     */     throws IOException
/*     */   {
/* 564 */     return (T)asCharSource(file, charset).readLines(callback);
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
/*     */   public static <T> T readBytes(File file, ByteProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 580 */     return (T)asByteSource(file).read(processor);
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
/*     */   public static HashCode hash(File file, HashFunction hashFunction)
/*     */     throws IOException
/*     */   {
/* 594 */     return asByteSource(file).hash(hashFunction);
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
/*     */   public static MappedByteBuffer map(File file)
/*     */     throws IOException
/*     */   {
/* 614 */     Preconditions.checkNotNull(file);
/* 615 */     return map(file, MapMode.READ_ONLY);
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
/*     */   public static MappedByteBuffer map(File file, MapMode mode)
/*     */     throws IOException
/*     */   {
/* 638 */     Preconditions.checkNotNull(file);
/* 639 */     Preconditions.checkNotNull(mode);
/* 640 */     if (!file.exists()) {
/* 641 */       throw new FileNotFoundException(file.toString());
/*     */     }
/* 643 */     return map(file, mode, file.length());
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
/*     */ 
/*     */ 
/*     */   public static MappedByteBuffer map(File file, MapMode mode, long size)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 669 */     Preconditions.checkNotNull(file);
/* 670 */     Preconditions.checkNotNull(mode);
/*     */     
/* 672 */     Closer closer = Closer.create();
/*     */     try {
/* 674 */       RandomAccessFile raf = (RandomAccessFile)closer.register(new RandomAccessFile(file, mode == MapMode.READ_ONLY ? "r" : "rw"));
/*     */       
/* 676 */       return map(raf, mode, size);
/*     */     } catch (Throwable e) {
/* 678 */       throw closer.rethrow(e);
/*     */     } finally {
/* 680 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private static MappedByteBuffer map(RandomAccessFile raf, MapMode mode, long size) throws IOException
/*     */   {
/* 686 */     Closer closer = Closer.create();
/*     */     try {
/* 688 */       FileChannel channel = (FileChannel)closer.register(raf.getChannel());
/* 689 */       return channel.map(mode, 0L, size);
/*     */     } catch (Throwable e) {
/* 691 */       throw closer.rethrow(e);
/*     */     } finally {
/* 693 */       closer.close();
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public static String simplifyPath(String pathname)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokestatic 46	com/facebook/presto/jdbc/internal/guava/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: invokevirtual 265	java/lang/String:length	()I
/*     */     //   9: ifne +7 -> 16
/*     */     //   12: ldc_w 426
/*     */     //   15: areturn
/*     */     //   16: bipush 47
/*     */     //   18: invokestatic 432	com/facebook/presto/jdbc/internal/guava/base/Splitter:on	(C)Lcom/facebook/presto/jdbc/internal/guava/base/Splitter;
/*     */     //   21: invokevirtual 436	com/facebook/presto/jdbc/internal/guava/base/Splitter:omitEmptyStrings	()Lcom/facebook/presto/jdbc/internal/guava/base/Splitter;
/*     */     //   24: aload_0
/*     */     //   25: invokevirtual 440	com/facebook/presto/jdbc/internal/guava/base/Splitter:split	(Ljava/lang/CharSequence;)Ljava/lang/Iterable;
/*     */     //   28: astore_1
/*     */     //   29: new 442	java/util/ArrayList
/*     */     //   32: dup
/*     */     //   33: invokespecial 443	java/util/ArrayList:<init>	()V
/*     */     //   36: astore_2
/*     */     //   37: aload_1
/*     */     //   38: invokeinterface 449 1 0
/*     */     //   43: astore_3
/*     */     //   44: aload_3
/*     */     //   45: invokeinterface 454 1 0
/*     */     //   50: ifeq +117 -> 167
/*     */     //   53: aload_3
/*     */     //   54: invokeinterface 458 1 0
/*     */     //   59: checkcast 258	java/lang/String
/*     */     //   62: astore 4
/*     */     //   64: aload 4
/*     */     //   66: ldc_w 426
/*     */     //   69: invokevirtual 459	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   72: ifeq +6 -> 78
/*     */     //   75: goto -31 -> 44
/*     */     //   78: aload 4
/*     */     //   80: ldc_w 461
/*     */     //   83: invokevirtual 459	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   86: ifeq +69 -> 155
/*     */     //   89: aload_2
/*     */     //   90: invokeinterface 463 1 0
/*     */     //   95: ifle +47 -> 142
/*     */     //   98: aload_2
/*     */     //   99: aload_2
/*     */     //   100: invokeinterface 463 1 0
/*     */     //   105: iconst_1
/*     */     //   106: isub
/*     */     //   107: invokeinterface 467 2 0
/*     */     //   112: checkcast 258	java/lang/String
/*     */     //   115: ldc_w 461
/*     */     //   118: invokevirtual 459	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   121: ifne +21 -> 142
/*     */     //   124: aload_2
/*     */     //   125: aload_2
/*     */     //   126: invokeinterface 463 1 0
/*     */     //   131: iconst_1
/*     */     //   132: isub
/*     */     //   133: invokeinterface 470 2 0
/*     */     //   138: pop
/*     */     //   139: goto +25 -> 164
/*     */     //   142: aload_2
/*     */     //   143: ldc_w 461
/*     */     //   146: invokeinterface 473 2 0
/*     */     //   151: pop
/*     */     //   152: goto +12 -> 164
/*     */     //   155: aload_2
/*     */     //   156: aload 4
/*     */     //   158: invokeinterface 473 2 0
/*     */     //   163: pop
/*     */     //   164: goto -120 -> 44
/*     */     //   167: bipush 47
/*     */     //   169: invokestatic 478	com/facebook/presto/jdbc/internal/guava/base/Joiner:on	(C)Lcom/facebook/presto/jdbc/internal/guava/base/Joiner;
/*     */     //   172: aload_2
/*     */     //   173: invokevirtual 482	com/facebook/presto/jdbc/internal/guava/base/Joiner:join	(Ljava/lang/Iterable;)Ljava/lang/String;
/*     */     //   176: astore_3
/*     */     //   177: aload_0
/*     */     //   178: iconst_0
/*     */     //   179: invokevirtual 486	java/lang/String:charAt	(I)C
/*     */     //   182: bipush 47
/*     */     //   184: if_icmpne +33 -> 217
/*     */     //   187: ldc_w 488
/*     */     //   190: aload_3
/*     */     //   191: invokestatic 262	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */     //   194: dup
/*     */     //   195: invokevirtual 265	java/lang/String:length	()I
/*     */     //   198: ifeq +9 -> 207
/*     */     //   201: invokevirtual 491	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */     //   204: goto +12 -> 216
/*     */     //   207: pop
/*     */     //   208: new 258	java/lang/String
/*     */     //   211: dup_x1
/*     */     //   212: swap
/*     */     //   213: invokespecial 492	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */     //   216: astore_3
/*     */     //   217: aload_3
/*     */     //   218: ldc_w 494
/*     */     //   221: invokevirtual 498	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*     */     //   224: ifeq +12 -> 236
/*     */     //   227: aload_3
/*     */     //   228: iconst_3
/*     */     //   229: invokevirtual 502	java/lang/String:substring	(I)Ljava/lang/String;
/*     */     //   232: astore_3
/*     */     //   233: goto -16 -> 217
/*     */     //   236: aload_3
/*     */     //   237: ldc_w 504
/*     */     //   240: invokevirtual 459	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   243: ifeq +10 -> 253
/*     */     //   246: ldc_w 488
/*     */     //   249: astore_3
/*     */     //   250: goto +17 -> 267
/*     */     //   253: ldc_w 506
/*     */     //   256: aload_3
/*     */     //   257: invokevirtual 459	java/lang/String:equals	(Ljava/lang/Object;)Z
/*     */     //   260: ifeq +7 -> 267
/*     */     //   263: ldc_w 426
/*     */     //   266: astore_3
/*     */     //   267: aload_3
/*     */     //   268: areturn
/*     */     // Line number table:
/*     */     //   Java source line #719	-> byte code offset #0
/*     */     //   Java source line #720	-> byte code offset #5
/*     */     //   Java source line #721	-> byte code offset #12
/*     */     //   Java source line #725	-> byte code offset #16
/*     */     //   Java source line #727	-> byte code offset #29
/*     */     //   Java source line #730	-> byte code offset #37
/*     */     //   Java source line #731	-> byte code offset #64
/*     */     //   Java source line #732	-> byte code offset #75
/*     */     //   Java source line #733	-> byte code offset #78
/*     */     //   Java source line #734	-> byte code offset #89
/*     */     //   Java source line #735	-> byte code offset #124
/*     */     //   Java source line #737	-> byte code offset #142
/*     */     //   Java source line #740	-> byte code offset #155
/*     */     //   Java source line #742	-> byte code offset #164
/*     */     //   Java source line #745	-> byte code offset #167
/*     */     //   Java source line #746	-> byte code offset #177
/*     */     //   Java source line #747	-> byte code offset #187
/*     */     //   Java source line #750	-> byte code offset #217
/*     */     //   Java source line #751	-> byte code offset #227
/*     */     //   Java source line #753	-> byte code offset #236
/*     */     //   Java source line #754	-> byte code offset #246
/*     */     //   Java source line #755	-> byte code offset #253
/*     */     //   Java source line #756	-> byte code offset #263
/*     */     //   Java source line #759	-> byte code offset #267
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	269	0	pathname	String
/*     */     //   29	240	1	components	Iterable<String>
/*     */     //   37	232	2	path	List<String>
/*     */     //   44	123	3	i$	java.util.Iterator
/*     */     //   177	92	3	result	String
/*     */     //   64	100	4	component	String
/*     */   }
/*     */   
/*     */   public static String getFileExtension(String fullName)
/*     */   {
/* 770 */     Preconditions.checkNotNull(fullName);
/* 771 */     String fileName = new File(fullName).getName();
/* 772 */     int dotIndex = fileName.lastIndexOf('.');
/* 773 */     return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
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
/*     */   public static String getNameWithoutExtension(String file)
/*     */   {
/* 787 */     Preconditions.checkNotNull(file);
/* 788 */     String fileName = new File(file).getName();
/* 789 */     int dotIndex = fileName.lastIndexOf('.');
/* 790 */     return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
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
/*     */   public static TreeTraverser<File> fileTreeTraverser()
/*     */   {
/* 804 */     return FILE_TREE_TRAVERSER;
/*     */   }
/*     */   
/* 807 */   private static final TreeTraverser<File> FILE_TREE_TRAVERSER = new TreeTraverser()
/*     */   {
/*     */     public Iterable<File> children(File file)
/*     */     {
/* 811 */       if (file.isDirectory()) {
/* 812 */         File[] files = file.listFiles();
/* 813 */         if (files != null) {
/* 814 */           return Collections.unmodifiableList(Arrays.asList(files));
/*     */         }
/*     */       }
/*     */       
/* 818 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 823 */       return "Files.fileTreeTraverser()";
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Predicate<File> isDirectory()
/*     */   {
/* 833 */     return FilePredicate.IS_DIRECTORY;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Predicate<File> isFile()
/*     */   {
/* 842 */     return FilePredicate.IS_FILE;
/*     */   }
/*     */   
/*     */   private static abstract enum FilePredicate implements Predicate<File> {
/* 846 */     IS_DIRECTORY, 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 858 */     IS_FILE;
/*     */     
/*     */     private FilePredicate() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\Files.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */