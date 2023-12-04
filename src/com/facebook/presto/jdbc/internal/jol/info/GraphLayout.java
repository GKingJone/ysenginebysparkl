/*     */ package com.facebook.presto.jdbc.internal.jol.info;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.util.Multiset;
/*     */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GraphLayout
/*     */ {
/*     */   public static GraphLayout parseInstance(Object root)
/*     */   {
/*  57 */     GraphWalker walker = new GraphWalker(root);
/*  58 */     GraphLayout data = new GraphLayout(root);
/*  59 */     walker.addVisitor(data.visitor());
/*  60 */     walker.walk();
/*  61 */     return data;
/*     */   }
/*     */   
/*  64 */   private static final Comparator<Class<?>> CLASS_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(Class<?> o1, Class<?> o2) {
/*  67 */       return o1.getName().compareTo(o2.getName());
/*     */     }
/*     */   };
/*     */   
/*  71 */   private final Set<Class<?>> classes = new TreeSet(CLASS_COMPARATOR);
/*  72 */   private final Multiset<Class<?>> classSizes = new Multiset();
/*  73 */   private final Multiset<Class<?>> classCounts = new Multiset();
/*  74 */   private final SortedMap<Long, GraphPathRecord> addresses = new TreeMap();
/*     */   private final String name;
/*     */   private final long rootAddress;
/*     */   private final int rootHC;
/*     */   private long totalCount;
/*     */   private long totalSize;
/*     */   
/*     */   public GraphLayout(Object root)
/*     */   {
/*  83 */     this.rootAddress = VMSupport.addressOf(root);
/*  84 */     this.rootHC = System.identityHashCode(root);
/*  85 */     this.name = root.getClass().getName();
/*     */   }
/*     */   
/*     */   private GraphVisitor visitor() {
/*  89 */     new GraphVisitor()
/*     */     {
/*     */       public void visit(GraphPathRecord gpr) {
/*  92 */         long addr = VMSupport.addressOf(gpr.obj());
/*  93 */         GraphLayout.this.addresses.put(Long.valueOf(addr), gpr);
/*     */         
/*  95 */         Class<?> klass = gpr.obj().getClass();
/*  96 */         GraphLayout.this.classes.add(klass);
/*  97 */         GraphLayout.this.classCounts.add(klass);
/*  98 */         GraphLayout.access$308(GraphLayout.this);
/*     */         try {
/* 100 */           int size = VMSupport.sizeOf(gpr.obj());
/* 101 */           GraphLayout.this.totalSize = (GraphLayout.this.totalSize + size);
/* 102 */           GraphLayout.this.classSizes.add(klass, size);
/*     */         } catch (Exception e) {
/* 104 */           GraphLayout.this.classSizes.add(klass, 0);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Multiset<Class<?>> getClassSizes()
/*     */   {
/* 116 */     return this.classSizes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Multiset<Class<?>> getClassCounts()
/*     */   {
/* 125 */     return this.classCounts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<Class<?>> getClasses()
/*     */   {
/* 134 */     return this.classes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long totalCount()
/*     */   {
/* 143 */     return this.totalCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long totalSize()
/*     */   {
/* 152 */     return this.totalSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long startAddress()
/*     */   {
/* 161 */     if (!this.addresses.isEmpty()) {
/* 162 */       return ((Long)this.addresses.firstKey()).longValue();
/*     */     }
/* 164 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long endAddress()
/*     */   {
/* 174 */     if (!this.addresses.isEmpty()) {
/* 175 */       return ((Long)this.addresses.lastKey()).longValue();
/*     */     }
/* 177 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SortedSet<Long> addresses()
/*     */   {
/* 188 */     return new TreeSet(this.addresses.keySet());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GraphPathRecord record(long address)
/*     */   {
/* 198 */     return (GraphPathRecord)this.addresses.get(Long.valueOf(address));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toFootprint()
/*     */   {
/* 207 */     StringWriter sw = new StringWriter();
/* 208 */     PrintWriter pw = new PrintWriter(sw);
/* 209 */     pw.println(this.name + " instance footprint:");
/* 210 */     pw.printf(" %9s %9s %9s   %s%n", new Object[] { "COUNT", "AVG", "SUM", "DESCRIPTION" });
/* 211 */     for (Class<?> key : getClasses()) {
/* 212 */       int count = getClassCounts().count(key);
/* 213 */       int size = getClassSizes().count(key);
/* 214 */       pw.printf(" %9d %9d %9d   %s%n", new Object[] { Integer.valueOf(count), Integer.valueOf(size / count), Integer.valueOf(size), key.getName() });
/*     */     }
/* 216 */     pw.printf(" %9d %9s %9d   %s%n", new Object[] { Long.valueOf(totalCount()), "", Long.valueOf(totalSize()), "(total)" });
/* 217 */     pw.println();
/* 218 */     pw.close();
/* 219 */     return sw.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toPrintable()
/*     */   {
/* 228 */     StringWriter sw = new StringWriter();
/* 229 */     PrintWriter pw = new PrintWriter(sw);
/*     */     
/* 231 */     long last = 0L;
/*     */     
/* 233 */     int typeLen = 1;
/* 234 */     for (Iterator localIterator = addresses().iterator(); localIterator.hasNext();) { long addr = ((Long)localIterator.next()).longValue();
/* 235 */       GraphPathRecord r = record(addr);
/* 236 */       typeLen = Math.max(typeLen, r.obj().getClass().getName().length());
/*     */     }
/*     */     
/* 239 */     pw.println(this.name + " object externals:");
/* 240 */     pw.printf(" %16s %10s %-" + typeLen + "s %-30s %s%n", new Object[] { "ADDRESS", "SIZE", "TYPE", "PATH", "VALUE" });
/* 241 */     for (localIterator = addresses().iterator(); localIterator.hasNext();) { long addr = ((Long)localIterator.next()).longValue();
/* 242 */       Object obj = record(addr).obj();
/* 243 */       int size = VMSupport.sizeOf(obj);
/*     */       
/* 245 */       if ((addr > last) && (last != 0L)) {
/* 246 */         pw.printf(" %16x %10d %-" + typeLen + "s %-30s %s%n", new Object[] { Long.valueOf(last), Long.valueOf(addr - last), "(something else)", "(somewhere else)", "(something else)" });
/*     */       }
/* 248 */       if (addr < last) {
/* 249 */         pw.printf(" %16x %10d %-" + typeLen + "s %-30s %s%n", new Object[] { Long.valueOf(last), Long.valueOf(addr - last), "**** OVERLAP ****", "**** OVERLAP ****", "**** OVERLAP ****" });
/*     */       }
/*     */       
/* 252 */       pw.printf(" %16x %10d %-" + typeLen + "s %-30s %s%n", new Object[] { Long.valueOf(addr), Integer.valueOf(size), obj.getClass().getName(), record(addr).path(), VMSupport.safeToString(obj) });
/* 253 */       last = addr + size;
/*     */     }
/* 255 */     pw.println();
/* 256 */     pw.close();
/* 257 */     return sw.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void toImage(String fileName)
/*     */     throws IOException
/*     */   {
/* 266 */     if (addresses().isEmpty()) { return;
/*     */     }
/* 268 */     long start = startAddress();
/* 269 */     long end = endAddress();
/*     */     
/* 271 */     int WIDTH = 1000;
/* 272 */     int HEIGHT = 320;
/* 273 */     int GRAPH_HEIGHT = 100;
/* 274 */     int SCALE_WIDTH = 30;
/* 275 */     int EXT_PAD = 50;
/* 276 */     int PAD = 20;
/*     */     
/* 278 */     BufferedImage image = new BufferedImage(1000, 320, 2);
/*     */     
/* 280 */     Graphics2D g = image.createGraphics();
/*     */     
/* 282 */     g.setColor(Color.WHITE);
/* 283 */     g.fillRect(0, 0, 1000, 320);
/*     */     
/* 285 */     int minDepth = Integer.MAX_VALUE;
/* 286 */     int maxDepth = Integer.MIN_VALUE;
/* 287 */     for (Iterator localIterator1 = addresses().iterator(); localIterator1.hasNext();) { long addr = ((Long)localIterator1.next()).longValue();
/* 288 */       GraphPathRecord p = record(addr);
/* 289 */       minDepth = Math.min(minDepth, p.depth());
/* 290 */       maxDepth = Math.max(maxDepth, p.depth());
/*     */     }
/*     */     
/* 293 */     Object depths = new Multiset();
/* 294 */     for (Iterator localIterator2 = addresses().iterator(); localIterator2.hasNext();) { long addr = ((Long)localIterator2.next()).longValue();
/* 295 */       GraphPathRecord r = record(addr);
/* 296 */       ((Multiset)depths).add(Integer.valueOf(r.depth()), VMSupport.sizeOf(r.obj()));
/*     */     }
/*     */     
/* 299 */     int lastX = 0;
/* 300 */     for (Iterator localIterator3 = addresses().iterator(); localIterator3.hasNext();) { long addr = ((Long)localIterator3.next()).longValue();
/* 301 */       Object obj = record(addr).obj();
/* 302 */       int size = VMSupport.sizeOf(obj);
/*     */       
/* 304 */       int x1 = 80 + (int)(870L * (addr - start) / (end - start));
/* 305 */       int x2 = 80 + (int)(870L * (addr + size - start) / (end - start));
/* 306 */       x1 = Math.max(x1, lastX);
/* 307 */       x2 = Math.max(x2, lastX);
/*     */       
/* 309 */       float relDepth = 1.0F * (record(addr).depth() - minDepth) / (maxDepth - minDepth + 1);
/* 310 */       g.setColor(Color.getHSBColor(relDepth, 1.0F, 0.9F));
/* 311 */       g.fillRect(x1, 50, x2 - x1, 100);
/*     */     }
/*     */     
/* 314 */     for (int depth = minDepth; depth <= maxDepth; depth++) {
/* 315 */       float relDepth = 1.0F * (depth - minDepth) / (maxDepth - minDepth + 1);
/* 316 */       g.setColor(Color.getHSBColor(relDepth, 1.0F, 0.9F));
/* 317 */       int y1 = 320 * (depth - minDepth) / (maxDepth - minDepth + 1);
/* 318 */       int y2 = 320 * (depth + 1 - minDepth) / (maxDepth - minDepth + 1);
/* 319 */       g.fillRect(0, y1, 30, y2 - y1);
/*     */     }
/*     */     
/* 322 */     lastX = 80;
/* 323 */     for (int depth = minDepth; depth <= maxDepth; depth++) {
/* 324 */       int w = (int)(870 * ((Multiset)depths).count(Integer.valueOf(depth)) / (end - start));
/*     */       
/* 326 */       float relDepth = 1.0F * (depth - minDepth) / (maxDepth - minDepth + 1);
/* 327 */       g.setColor(Color.getHSBColor(relDepth, 1.0F, 0.9F));
/* 328 */       g.fillRect(lastX, 170, w, 100);
/*     */       
/* 330 */       lastX += w;
/*     */     }
/*     */     
/* 333 */     g.setColor(Color.BLACK);
/* 334 */     g.setStroke(new BasicStroke(2.0F));
/* 335 */     g.drawRect(80, 50, 870, 100);
/* 336 */     g.drawRect(80, 170, 870, 100);
/*     */     
/* 338 */     g.setStroke(new BasicStroke(1.0F));
/* 339 */     g.drawLine(80, 290, 950, 290);
/* 340 */     g.drawLine(80, 285, 80, 295);
/* 341 */     g.drawLine(950, 285, 950, 295);
/*     */     
/* 343 */     Font font = new Font("Serif", 0, 18);
/* 344 */     g.setFont(font);
/*     */     
/* 346 */     String labelDense = (end - start) / 1024L + " Kb";
/*     */     
/* 348 */     g.setBackground(Color.WHITE);
/* 349 */     g.setColor(Color.BLACK);
/* 350 */     g.drawString(labelDense, 450, 310);
/*     */     
/* 352 */     g.drawString(String.format("0x%x, %s@%d", new Object[] { Long.valueOf(this.rootAddress), this.name, Integer.valueOf(this.rootHC) }), 80, 30);
/*     */     
/* 354 */     AffineTransform orig = g.getTransform();
/* 355 */     g.rotate(-Math.toRadians(90.0D), 75.0D, 150.0D);
/* 356 */     g.drawString("Actual:", 75, 150);
/* 357 */     g.setTransform(orig);
/*     */     
/* 359 */     g.rotate(-Math.toRadians(90.0D), 75.0D, 270.0D);
/* 360 */     g.drawString("Dense:", 75, 270);
/* 361 */     g.setTransform(orig);
/*     */     
/* 363 */     ImageIO.write(image, "png", new File(fileName));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\GraphLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */