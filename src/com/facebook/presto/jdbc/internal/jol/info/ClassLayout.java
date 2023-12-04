/*     */ package com.facebook.presto.jdbc.internal.jol.info;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.layouters.CurrentLayouter;
/*     */ import com.facebook.presto.jdbc.internal.jol.layouters.Layouter;
/*     */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
/*     */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport.SizeInfo;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Iterator;
/*     */ import java.util.SortedSet;
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
/*     */ public class ClassLayout
/*     */ {
/*     */   private final ClassData classData;
/*     */   private final SortedSet<FieldLayout> fields;
/*     */   private final int headerSize;
/*     */   private final int size;
/*     */   
/*     */   public static ClassLayout parseClass(Class<?> klass)
/*     */   {
/*  48 */     return parseClass(klass, new CurrentLayouter());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassLayout parseClass(Class<?> klass, Layouter layouter)
/*     */   {
/*  59 */     return layouter.layout(ClassData.parseClass(klass));
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
/*     */   public ClassLayout(ClassData classData, SortedSet<FieldLayout> fields, int headerSize, int instanceSize, boolean check)
/*     */   {
/*  77 */     this.classData = classData;
/*  78 */     this.fields = fields;
/*  79 */     this.headerSize = headerSize;
/*  80 */     this.size = instanceSize;
/*  81 */     if (check) {
/*  82 */       checkInvariants();
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkInvariants() {
/*  87 */     long lastOffset = 0L;
/*  88 */     for (FieldLayout f : this.fields) {
/*  89 */       if (f.offset() % f.size() != 0) {
/*  90 */         throw new IllegalStateException("Field " + f + " is not aligned");
/*     */       }
/*  92 */       if (f.offset() + f.size() > instanceSize()) {
/*  93 */         throw new IllegalStateException("Field " + f + " is overflowing the object of size " + instanceSize());
/*     */       }
/*  95 */       if (f.offset() < lastOffset) {
/*  96 */         throw new IllegalStateException("Field " + f + " overlaps with the previous field");
/*     */       }
/*  98 */       lastOffset = f.offset() + f.size();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SortedSet<FieldLayout> fields()
/*     */   {
/* 108 */     return this.fields;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int instanceSize()
/*     */   {
/* 117 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int headerSize()
/*     */   {
/* 126 */     return this.headerSize;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 131 */     StringBuilder sb = new StringBuilder();
/* 132 */     for (FieldLayout f : fields()) {
/* 133 */       sb.append(f).append("\n");
/*     */     }
/* 135 */     sb.append("size = ").append(this.size).append("\n");
/* 136 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toPrintable()
/*     */   {
/* 146 */     return toPrintable(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toPrintable(Object instance)
/*     */   {
/* 157 */     StringWriter sw = new StringWriter();
/* 158 */     PrintWriter pw = new PrintWriter(sw);
/*     */     
/* 160 */     int maxTypeLen = 5;
/* 161 */     for (Iterator localIterator1 = fields().iterator(); localIterator1.hasNext();) { f = (FieldLayout)localIterator1.next();
/* 162 */       maxTypeLen = Math.max(f.typeClass().length(), maxTypeLen);
/*     */     }
/*     */     FieldLayout f;
/* 165 */     int maxDescrLen = 30;
/* 166 */     for (FieldLayout f : fields()) {
/* 167 */       maxDescrLen = Math.max((f.hostClass() + "" + f.name()).length(), maxDescrLen);
/*     */     }
/*     */     
/* 170 */     pw.println(this.classData.name() + " object internals:");
/* 171 */     pw.printf(" %6s %5s %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", new Object[] { "OFFSET", "SIZE", "TYPE", "DESCRIPTION", "VALUE" });
/* 172 */     if (instance != null) {
/* 173 */       for (long off = 0L; off < headerSize(); off += 4L) {
/* 174 */         pw.printf(" %6d %5d %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", new Object[] { Long.valueOf(off), Integer.valueOf(4), "", "(object header)", 
/* 175 */           toHex(VMSupport.U.getByte(instance, off + 0L) & 0xFF) + " " + 
/* 176 */           toHex(VMSupport.U.getByte(instance, off + 1L) & 0xFF) + " " + 
/* 177 */           toHex(VMSupport.U.getByte(instance, off + 2L) & 0xFF) + " " + 
/* 178 */           toHex(VMSupport.U.getByte(instance, off + 3L) & 0xFF) + " " + "(" + 
/*     */           
/* 180 */           toBinary(VMSupport.U.getByte(instance, off + 0L) & 0xFF) + " " + 
/* 181 */           toBinary(VMSupport.U.getByte(instance, off + 1L) & 0xFF) + " " + 
/* 182 */           toBinary(VMSupport.U.getByte(instance, off + 2L) & 0xFF) + " " + 
/* 183 */           toBinary(VMSupport.U.getByte(instance, off + 3L) & 0xFF) + ")" });
/*     */       }
/*     */       
/*     */     } else {
/* 187 */       pw.printf(" %6d %5d %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", new Object[] { Integer.valueOf(0), Integer.valueOf(headerSize()), "", "(object header)", "N/A" });
/*     */     }
/*     */     
/* 190 */     int nextFree = headerSize();
/*     */     
/* 192 */     int interLoss = 0;
/* 193 */     int exterLoss = 0;
/*     */     
/* 195 */     for (FieldLayout f : fields()) {
/* 196 */       if (f.offset() > nextFree) {
/* 197 */         pw.printf(" %6d %5d %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", new Object[] { Integer.valueOf(nextFree), Integer.valueOf(f.offset() - nextFree), "", "(alignment/padding gap)", "N/A" });
/* 198 */         interLoss += f.offset() - nextFree;
/*     */       }
/* 200 */       pw.printf(" %6d %5d %" + maxTypeLen + "s %-" + maxDescrLen + "s %s%n", new Object[] {
/* 201 */         Integer.valueOf(f.offset()), 
/* 202 */         Integer.valueOf(f.size()), f
/* 203 */         .typeClass(), f
/* 204 */         .hostClass() + "." + f.name(), instance != null ? f
/* 205 */         .safeValue(instance) : "N/A" });
/*     */       
/*     */ 
/* 208 */       nextFree = f.offset() + f.size();
/*     */     }
/*     */     
/* 211 */     VMSupport.SizeInfo info = VMSupport.tryExactObjectSize(instance, this);
/*     */     
/* 213 */     if (info.instanceSize() != nextFree) {
/* 214 */       exterLoss = info.instanceSize() - nextFree;
/* 215 */       pw.printf(" %6d %5s %" + maxTypeLen + "s %s%n", new Object[] { Integer.valueOf(nextFree), Integer.valueOf(exterLoss), "", "(loss due to the next object alignment)" });
/*     */     }
/*     */     
/* 218 */     if (info.exactSize()) {
/* 219 */       pw.printf("Instance size: %d bytes (reported by VM agent)%n", new Object[] { Integer.valueOf(info.instanceSize()) });
/*     */     }
/* 221 */     else if (instance != null) {
/* 222 */       pw.printf("Instance size: %d bytes (estimated, add this JAR via -javaagent: to get accurate result)%n", new Object[] { Integer.valueOf(info.instanceSize()) });
/*     */     } else {
/* 224 */       pw.printf("Instance size: %d bytes (estimated, the sample instance is not available)%n", new Object[] { Integer.valueOf(info.instanceSize()) });
/*     */     }
/*     */     
/*     */ 
/* 228 */     pw.printf("Space losses: %d bytes internal + %d bytes external = %d bytes total%n", new Object[] { Integer.valueOf(interLoss), Integer.valueOf(exterLoss), Integer.valueOf(interLoss + exterLoss) });
/*     */     
/* 230 */     pw.close();
/*     */     
/* 232 */     return sw.toString();
/*     */   }
/*     */   
/*     */   private static String toBinary(int x)
/*     */   {
/* 237 */     String s = Integer.toBinaryString(x);
/* 238 */     int deficit = 8 - s.length();
/* 239 */     for (int c = 0; c < deficit; c++) {
/* 240 */       s = "0" + s;
/*     */     }
/*     */     
/* 243 */     return s.substring(0, 4) + " " + s.substring(4);
/*     */   }
/*     */   
/*     */   private static String toHex(int x)
/*     */   {
/* 248 */     String s = Integer.toHexString(x);
/* 249 */     int deficit = 2 - s.length();
/* 250 */     for (int c = 0; c < deficit; c++) {
/* 251 */       s = "0" + s;
/*     */     }
/* 253 */     return s;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\ClassLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */