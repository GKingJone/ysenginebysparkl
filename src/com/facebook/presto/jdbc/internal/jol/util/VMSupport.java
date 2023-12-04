/*     */ package com.facebook.presto.jdbc.internal.jol.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassData;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import com.facebook.presto.jdbc.internal.jol.layouters.CurrentLayouter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.instrument.Instrumentation;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.RuntimeMBeanException;
/*     */ import javax.management.openmbean.CompositeDataSupport;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VMSupport
/*     */ {
/*     */   private static Instrumentation INSTRUMENTATION;
/*  81 */   public static final Unsafe U = (Unsafe)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Unsafe run() {
/*     */       try {
/*  85 */         Field unsafe = Unsafe.class.getDeclaredField("theUnsafe");
/*  86 */         unsafe.setAccessible(true);
/*  87 */         return (Unsafe)unsafe.get(null);
/*     */       } catch (NoSuchFieldException e) {
/*  89 */         throw new IllegalStateException(e);
/*     */       } catch (IllegalAccessException e) {
/*  91 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*  81 */   });
/*     */   public static final String VM_NAME;
/*     */   public static final int ADDRESS_SIZE;
/*     */   public static final int OBJ_ALIGNMENT;
/*     */   public static final int OBJ_HEADER_SIZE;
/*     */   public static final boolean USE_COMPRESSED_REFS;
/*     */   public static final int COMPRESSED_REF_SHIFT;
/*     */   public static final int REF_SIZE;
/*     */   public static final int BOOLEAN_SIZE;
/*     */   public static final int BYTE_SIZE;
/*     */   public static final int CHAR_SIZE;
/*     */   public static final int DOUBLE_SIZE;
/*     */   public static final int FLOAT_SIZE;
/*     */   public static final int INT_SIZE;
/*     */   public static final int LONG_SIZE;
/*     */   public static final int SHORT_SIZE;
/*  97 */   private static final ThreadLocal<Object[]> BUFFERS; private static final long OBJECT_ARRAY_BASE = U.arrayBaseOffset(Object[].class);
/*  98 */   static { BUFFERS = new ThreadLocal()
/*     */     {
/*     */       protected Object[] initialValue() {
/* 101 */         return new Object[1];
/*     */       }
/*     */     };
/*     */     int headerSize;
/*     */     try
/*     */     {
/* 107 */       long off1 = U.objectFieldOffset(HeaderClass.class.getField("b1"));
/* 108 */       headerSize = (int)off1;
/*     */     } catch (NoSuchFieldException e) {
/* 110 */       headerSize = -1;
/*     */     }
/*     */     
/* 113 */     VMOptions opts = VMOptions.access$000();
/*     */     
/* 115 */     ADDRESS_SIZE = U.addressSize();
/* 116 */     OBJ_HEADER_SIZE = headerSize;
/*     */     
/* 118 */     VM_NAME = opts.name;
/* 119 */     USE_COMPRESSED_REFS = opts.compressedRef;
/* 120 */     COMPRESSED_REF_SHIFT = opts.compressRefShift;
/* 121 */     OBJ_ALIGNMENT = opts.objectAlignment;
/* 122 */     REF_SIZE = opts.sizeReference;
/* 123 */     BOOLEAN_SIZE = opts.sizeBoolean;
/* 124 */     BYTE_SIZE = opts.sizeByte;
/* 125 */     CHAR_SIZE = opts.sizeChar;
/* 126 */     DOUBLE_SIZE = opts.sizeDouble;
/* 127 */     FLOAT_SIZE = opts.sizeFloat;
/* 128 */     INT_SIZE = opts.sizeInt;
/* 129 */     LONG_SIZE = opts.sizeLong;
/* 130 */     SHORT_SIZE = opts.sizeShort;
/*     */   }
/*     */   
/*     */   public static long toNativeAddress(long address) {
/* 134 */     if (USE_COMPRESSED_REFS) {
/* 135 */       return address << COMPRESSED_REF_SHIFT;
/*     */     }
/* 137 */     return address;
/*     */   }
/*     */   
/*     */   public static int align(int addr)
/*     */   {
/* 142 */     return align(addr, OBJ_ALIGNMENT);
/*     */   }
/*     */   
/*     */   public static int align(int addr, int align) {
/* 146 */     if (addr % align == 0) {
/* 147 */       return addr;
/*     */     }
/* 149 */     return (addr / align + 1) * align;
/*     */   }
/*     */   
/*     */   public static String vmDetails()
/*     */   {
/* 154 */     StringWriter sw = new StringWriter();
/* 155 */     PrintWriter out = new PrintWriter(sw);
/*     */     
/* 157 */     out.println("Running " + ADDRESS_SIZE * 8 + "-bit " + VM_NAME + " VM.");
/* 158 */     if (USE_COMPRESSED_REFS)
/* 159 */       out.println("Using compressed references with " + COMPRESSED_REF_SHIFT + "-bit shift.");
/* 160 */     out.println("Objects are " + OBJ_ALIGNMENT + " bytes aligned.");
/*     */     
/* 162 */     out.printf("%-19s: %d, %d, %d, %d, %d, %d, %d, %d, %d [bytes]%n", new Object[] { "Field sizes by type", 
/*     */     
/* 164 */       Integer.valueOf(REF_SIZE), 
/* 165 */       Integer.valueOf(BOOLEAN_SIZE), 
/* 166 */       Integer.valueOf(BYTE_SIZE), 
/* 167 */       Integer.valueOf(CHAR_SIZE), 
/* 168 */       Integer.valueOf(SHORT_SIZE), 
/* 169 */       Integer.valueOf(INT_SIZE), 
/* 170 */       Integer.valueOf(FLOAT_SIZE), 
/* 171 */       Integer.valueOf(LONG_SIZE), 
/* 172 */       Integer.valueOf(DOUBLE_SIZE) });
/*     */     
/*     */ 
/* 175 */     out.printf("%-19s: %d, %d, %d, %d, %d, %d, %d, %d, %d [bytes]%n", new Object[] { "Array element sizes", 
/*     */     
/* 177 */       Integer.valueOf(U.arrayIndexScale(Object[].class)), 
/* 178 */       Integer.valueOf(U.arrayIndexScale(boolean[].class)), 
/* 179 */       Integer.valueOf(U.arrayIndexScale(byte[].class)), 
/* 180 */       Integer.valueOf(U.arrayIndexScale(char[].class)), 
/* 181 */       Integer.valueOf(U.arrayIndexScale(short[].class)), 
/* 182 */       Integer.valueOf(U.arrayIndexScale(int[].class)), 
/* 183 */       Integer.valueOf(U.arrayIndexScale(float[].class)), 
/* 184 */       Integer.valueOf(U.arrayIndexScale(long[].class)), 
/* 185 */       Integer.valueOf(U.arrayIndexScale(double[].class)) });
/*     */     
/*     */ 
/* 188 */     out.close();
/* 189 */     return sw.toString();
/*     */   }
/*     */   
/*     */   private static Object instantiateType(int type) {
/* 193 */     switch (type) {
/* 194 */     case 0:  return new MyObject1();
/* 195 */     case 1:  return new MyObject2();
/* 196 */     case 2:  return new MyObject3();
/* 197 */     case 3:  return new MyObject4();
/* 198 */     case 4:  return new MyObject5();
/*     */     }
/* 200 */     throw new IllegalStateException();
/*     */   }
/*     */   
/*     */   private static int guessAlignment(int oopSize)
/*     */   {
/* 205 */     int COUNT = 100000;
/*     */     
/* 207 */     Random r = new Random();
/*     */     
/* 209 */     long min = -1L;
/* 210 */     for (int c = 0; c < 100000; c++) {
/* 211 */       Object o1 = instantiateType(r.nextInt(5));
/* 212 */       Object o2 = instantiateType(r.nextInt(5));
/*     */       
/* 214 */       long diff = Math.abs(addressOf(o2, oopSize) - addressOf(o1, oopSize));
/* 215 */       if (min == -1L) {
/* 216 */         min = diff;
/*     */       } else {
/* 218 */         min = MathUtil.gcd(min, diff);
/*     */       }
/*     */     }
/*     */     
/* 222 */     return (int)min;
/*     */   }
/*     */   
/*     */   public static long addressOf(Object o) {
/* 226 */     return addressOf(o, REF_SIZE);
/*     */   }
/*     */   
/*     */   public static long addressOf(Object o, int oopSize) {
/* 230 */     Object[] array = (Object[])BUFFERS.get();
/*     */     
/* 232 */     array[0] = o;
/*     */     long objectAddress;
/*     */     long objectAddress;
/* 235 */     switch (oopSize) {
/*     */     case 4: 
/* 237 */       objectAddress = U.getInt(array, OBJECT_ARRAY_BASE) & 0xFFFFFFFF;
/* 238 */       break;
/*     */     case 8: 
/* 240 */       objectAddress = U.getLong(array, OBJECT_ARRAY_BASE);
/* 241 */       break;
/*     */     default: 
/* 243 */       throw new Error("unsupported address size: " + oopSize);
/*     */     }
/*     */     long objectAddress;
/* 246 */     array[0] = null;
/*     */     
/* 248 */     return toNativeAddress(objectAddress);
/*     */   }
/*     */   
/*     */   public static void premain(String agentArgs, Instrumentation inst) {
/* 252 */     INSTRUMENTATION = inst;
/*     */   }
/*     */   
/*     */   public static SizeInfo tryExactObjectSize(Object o, ClassLayout layout) {
/* 256 */     return new SizeInfo(o, layout);
/*     */   }
/*     */   
/*     */   public static class SizeInfo {
/*     */     private final int size;
/*     */     private final boolean exactSizeAvail;
/*     */     
/*     */     public SizeInfo(Object o, ClassLayout layout) {
/* 264 */       this.exactSizeAvail = ((VMSupport.INSTRUMENTATION != null) && (o != null));
/* 265 */       this.size = (this.exactSizeAvail ? (int)VMSupport.INSTRUMENTATION.getObjectSize(o) : layout.instanceSize());
/*     */     }
/*     */     
/*     */     public int instanceSize() {
/* 269 */       return this.size;
/*     */     }
/*     */     
/*     */     public boolean exactSize() {
/* 273 */       return this.exactSizeAvail;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class VMOptions
/*     */   {
/*     */     private final String name;
/*     */     private final boolean compressedRef;
/*     */     private final int compressRefShift;
/*     */     private final int objectAlignment;
/*     */     private final int sizeReference;
/* 284 */     private final int sizeBoolean = getMinDiff(MyBooleans4.class);
/* 285 */     private final int sizeByte = getMinDiff(MyBytes4.class);
/* 286 */     private final int sizeShort = getMinDiff(MyShorts4.class);
/* 287 */     private final int sizeChar = getMinDiff(MyChars4.class);
/* 288 */     private final int sizeFloat = getMinDiff(MyFloats4.class);
/* 289 */     private final int sizeInt = getMinDiff(MyInts4.class);
/* 290 */     private final int sizeLong = getMinDiff(MyLongs4.class);
/* 291 */     private final int sizeDouble = getMinDiff(MyDoubles4.class);
/*     */     
/*     */     public static int getMinDiff(Class<?> klass) {
/*     */       try {
/* 295 */         int off1 = (int)VMSupport.U.objectFieldOffset(klass.getDeclaredField("f1"));
/* 296 */         int off2 = (int)VMSupport.U.objectFieldOffset(klass.getDeclaredField("f2"));
/* 297 */         int off3 = (int)VMSupport.U.objectFieldOffset(klass.getDeclaredField("f3"));
/* 298 */         int off4 = (int)VMSupport.U.objectFieldOffset(klass.getDeclaredField("f4"));
/* 299 */         return MathUtil.minDiff(new int[] { off1, off2, off3, off4 });
/*     */       } catch (NoSuchFieldException e) {
/* 301 */         throw new IllegalStateException("Infrastructure failure, klass = " + klass, e);
/*     */       }
/*     */     }
/*     */     
/*     */     public VMOptions(String name) {
/* 306 */       this.name = name;
/* 307 */       this.sizeReference = VMSupport.U.addressSize();
/* 308 */       this.objectAlignment = VMSupport.guessAlignment(this.sizeReference);
/* 309 */       this.compressedRef = false;
/* 310 */       this.compressRefShift = 1;
/*     */     }
/*     */     
/*     */     public VMOptions(String name, int align) {
/* 314 */       this.name = name;
/* 315 */       this.sizeReference = 4;
/* 316 */       this.objectAlignment = align;
/* 317 */       this.compressedRef = true;
/* 318 */       this.compressRefShift = MathUtil.log2p(align);
/*     */     }
/*     */     
/*     */     public VMOptions(String name, int align, int compRefShift) {
/* 322 */       this.name = name;
/* 323 */       this.sizeReference = 4;
/* 324 */       this.objectAlignment = align;
/* 325 */       this.compressedRef = true;
/* 326 */       this.compressRefShift = compRefShift;
/*     */     }
/*     */     
/*     */     private static VMOptions getOptions()
/*     */     {
/* 331 */       VMOptions hsOpts = getHotspotSpecifics();
/* 332 */       if (hsOpts != null) { return hsOpts;
/*     */       }
/*     */       
/* 335 */       VMOptions jrOpts = getJRockitSpecifics();
/* 336 */       if (jrOpts != null) { return jrOpts;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 343 */         long off1 = VMSupport.U.objectFieldOffset(CompressedOopsClass.class.getField("obj1"));
/* 344 */         long off2 = VMSupport.U.objectFieldOffset(CompressedOopsClass.class.getField("obj2"));
/* 345 */         oopSize = (int)Math.abs(off2 - off1);
/*     */       } catch (NoSuchFieldException e) { int oopSize;
/* 347 */         throw new IllegalStateException("Infrastructure failure", e);
/*     */       }
/*     */       int oopSize;
/* 350 */       if (oopSize != VMSupport.U.addressSize()) {
/* 351 */         return new VMOptions("Auto-detected", 3);
/*     */       }
/* 353 */       return new VMOptions("Auto-detected");
/*     */     }
/*     */     
/*     */     private static VMOptions getHotspotSpecifics()
/*     */     {
/* 358 */       String name = System.getProperty("java.vm.name");
/* 359 */       if ((!name.contains("HotSpot")) && (!name.contains("OpenJDK"))) {
/* 360 */         return null;
/*     */       }
/*     */       try
/*     */       {
/* 364 */         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
/*     */         try
/*     */         {
/* 367 */           ObjectName mbean = new ObjectName("com.sun.management:type=HotSpotDiagnostic");
/* 368 */           CompositeDataSupport compressedOopsValue = (CompositeDataSupport)server.invoke(mbean, "getVMOption", new Object[] { "UseCompressedOops" }, new String[] { "java.lang.String" });
/* 369 */           boolean compressedOops = Boolean.valueOf(compressedOopsValue.get("value").toString()).booleanValue();
/* 370 */           if (compressedOops)
/*     */           {
/* 372 */             CompositeDataSupport alignmentValue = (CompositeDataSupport)server.invoke(mbean, "getVMOption", new Object[] { "ObjectAlignmentInBytes" }, new String[] { "java.lang.String" });
/* 373 */             int align = Integer.valueOf(alignmentValue.get("value").toString()).intValue();
/* 374 */             return new VMOptions("HotSpot", align);
/*     */           }
/* 376 */           return new VMOptions("HotSpot");
/*     */         }
/*     */         catch (RuntimeMBeanException iae)
/*     */         {
/* 380 */           return new VMOptions("HotSpot");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 389 */         return null;
/*     */       }
/*     */       catch (RuntimeException re)
/*     */       {
/* 383 */         System.err.println("Failed to read HotSpot-specific configuration properly, please report this as the bug");
/* 384 */         re.printStackTrace();
/* 385 */         return null;
/*     */       } catch (Exception exp) {
/* 387 */         System.err.println("Failed to read HotSpot-specific configuration properly, please report this as the bug");
/* 388 */         exp.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/*     */     private static VMOptions getJRockitSpecifics()
/*     */     {
/* 394 */       String name = System.getProperty("java.vm.name");
/* 395 */       if (!name.contains("JRockit")) {
/* 396 */         return null;
/*     */       }
/*     */       try
/*     */       {
/* 400 */         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
/* 401 */         String str = (String)server.invoke(new ObjectName("oracle.jrockit.management:type=DiagnosticCommand"), "execute", new Object[] { "print_vm_state" }, new String[] { "java.lang.String" });
/* 402 */         String[] split = str.split("\n");
/* 403 */         for (String s : split) {
/* 404 */           if (s.contains("CompRefs")) {
/* 405 */             Pattern pattern = Pattern.compile("(.*?)References are compressed, with heap base (.*?) and shift (.*?)\\.");
/* 406 */             Matcher matcher = pattern.matcher(s);
/* 407 */             if (matcher.matches()) {
/* 408 */               return new VMOptions("JRockit (experimental)", 8, Integer.valueOf(matcher.group(3)).intValue());
/*     */             }
/* 410 */             return new VMOptions("JRockit (experimental)");
/*     */           }
/*     */         }
/*     */         
/* 414 */         return null;
/*     */       } catch (RuntimeException re) {
/* 416 */         System.err.println("Failed to read JRockit-specific configuration properly, please report this as the bug");
/* 417 */         re.printStackTrace();
/* 418 */         return null;
/*     */       } catch (Exception exp) {
/* 420 */         System.err.println("Failed to read JRockit-specific configuration properly, please report this as the bug");
/* 421 */         exp.printStackTrace(); }
/* 422 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static int sizeOf(Object o)
/*     */   {
/* 429 */     if (INSTRUMENTATION != null) {
/* 430 */       return align((int)INSTRUMENTATION.getObjectSize(o));
/*     */     }
/*     */     
/* 433 */     return new CurrentLayouter().layout(ClassData.parseInstance(o)).instanceSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String safeToString(Object o)
/*     */   {
/* 444 */     if (o == null) { return "null";
/*     */     }
/* 446 */     if (o.getClass().isArray()) {
/* 447 */       Class<?> type = o.getClass().getComponentType();
/* 448 */       if (type == Boolean.TYPE) return Arrays.toString((boolean[])o);
/* 449 */       if (type == Byte.TYPE) return Arrays.toString((byte[])o);
/* 450 */       if (type == Short.TYPE) return Arrays.toString((short[])o);
/* 451 */       if (type == Character.TYPE) return Arrays.toString((char[])o);
/* 452 */       if (type == Integer.TYPE) return Arrays.toString((int[])o);
/* 453 */       if (type == Float.TYPE) return Arrays.toString((float[])o);
/* 454 */       if (type == Long.TYPE) return Arrays.toString((long[])o);
/* 455 */       if (type == Double.TYPE) { return Arrays.toString((double[])o);
/*     */       }
/* 457 */       Object[] oos = (Object[])o;
/* 458 */       String[] strs = new String[oos.length];
/* 459 */       for (int i = 0; i < oos.length; i++) {
/* 460 */         strs[i] = (oos[i] == null ? "null" : safeToString(oos[i]));
/*     */       }
/* 462 */       return Arrays.toString(strs);
/*     */     }
/*     */     
/* 465 */     if (o.getClass().isPrimitive()) return o.toString();
/* 466 */     if (o.getClass() == Boolean.class) return o.toString();
/* 467 */     if (o.getClass() == Byte.class) return o.toString();
/* 468 */     if (o.getClass() == Short.class) return o.toString();
/* 469 */     if (o.getClass() == Character.class) return o.toString();
/* 470 */     if (o.getClass() == Integer.class) return o.toString();
/* 471 */     if (o.getClass() == Float.class) return o.toString();
/* 472 */     if (o.getClass() == Long.class) return o.toString();
/* 473 */     if (o.getClass() == Double.class) return o.toString();
/* 474 */     return "(object)";
/*     */   }
/*     */   
/*     */   static class CompressedOopsClass
/*     */   {
/*     */     public Object obj1;
/*     */     public Object obj2;
/*     */   }
/*     */   
/*     */   static class HeaderClass
/*     */   {
/*     */     public boolean b1;
/*     */   }
/*     */   
/*     */   static class MyObject1 {}
/*     */   
/*     */   static class MyObject2
/*     */   {
/*     */     private boolean b;
/*     */   }
/*     */   
/*     */   static class MyObject3
/*     */   {
/*     */     private int i;
/*     */   }
/*     */   
/*     */   static class MyObject4
/*     */   {
/*     */     private long l;
/*     */   }
/*     */   
/*     */   static class MyObject5
/*     */   {
/*     */     private Object o;
/*     */   }
/*     */   
/*     */   static class MyBooleans4
/*     */   {
/*     */     private boolean f1;
/*     */     private boolean f2;
/*     */     private boolean f3;
/*     */     private boolean f4;
/*     */   }
/*     */   
/*     */   static class MyBytes4
/*     */   {
/*     */     private byte f1;
/*     */     private byte f2;
/*     */     private byte f3;
/*     */     private byte f4;
/*     */   }
/*     */   
/*     */   static class MyShorts4
/*     */   {
/*     */     private short f1;
/*     */     private short f2;
/*     */     private short f3;
/*     */     private short f4;
/*     */   }
/*     */   
/*     */   static class MyChars4
/*     */   {
/*     */     private char f1;
/*     */     private char f2;
/*     */     private char f3;
/*     */     private char f4;
/*     */   }
/*     */   
/*     */   static class MyInts4
/*     */   {
/*     */     private int f1;
/*     */     private int f2;
/*     */     private int f3;
/*     */     private int f4;
/*     */   }
/*     */   
/*     */   static class MyFloats4
/*     */   {
/*     */     private float f1;
/*     */     private float f2;
/*     */     private float f3;
/*     */     private float f4;
/*     */   }
/*     */   
/*     */   static class MyLongs4
/*     */   {
/*     */     private long f1;
/*     */     private long f2;
/*     */     private long f3;
/*     */     private long f4;
/*     */   }
/*     */   
/*     */   static class MyDoubles4
/*     */   {
/*     */     private double f1;
/*     */     private double f2;
/*     */     private double f3;
/*     */     private double f4;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\util\VMSupport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */