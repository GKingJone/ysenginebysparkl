/*     */ package com.facebook.presto.jdbc.internal.jol.layouters;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jol.datamodel.DataModel;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassData;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.ClassLayout;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.FieldData;
/*     */ import com.facebook.presto.jdbc.internal.jol.info.FieldLayout;
/*     */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HotSpotLayouter
/*     */   implements Layouter
/*     */ {
/*     */   private final DataModel model;
/*     */   private final boolean takeHierarchyGaps;
/*     */   private final boolean takeSuperGaps;
/*     */   private final boolean autoAlign;
/*     */   
/*     */   public HotSpotLayouter(DataModel model)
/*     */   {
/*  52 */     this(model, false, false, false);
/*     */   }
/*     */   
/*     */   public HotSpotLayouter(DataModel model, boolean takeHierarchyGaps, boolean takeSuperGaps, boolean autoAlign) {
/*  56 */     this.model = model;
/*  57 */     this.takeHierarchyGaps = takeHierarchyGaps;
/*  58 */     this.takeSuperGaps = takeSuperGaps;
/*  59 */     this.autoAlign = autoAlign;
/*     */   }
/*     */   
/*     */   public ClassLayout layout(ClassData cd)
/*     */   {
/*  64 */     SortedSet<FieldLayout> result = new TreeSet();
/*     */     int instanceSize;
/*  66 */     if (cd.isArray())
/*     */     {
/*  68 */       int base = this.model.headerSize() + this.model.sizeOf("int");
/*  69 */       int scale = this.model.sizeOf(cd.arrayComponentType());
/*     */       
/*  71 */       instanceSize = base + cd.arrayLength() * scale;
/*     */       
/*  73 */       instanceSize = VMSupport.align(instanceSize, this.autoAlign ? Math.max(4, scale) : 8);
/*  74 */       base = VMSupport.align(base, Math.max(4, scale));
/*     */       
/*  76 */       result.add(new FieldLayout(FieldData.create(cd.arrayClass(), "length", "int"), this.model.headerSize(), this.model.sizeOf("int")));
/*  77 */       result.add(new FieldLayout(FieldData.create(cd.arrayClass(), "<elements>", cd.arrayComponentType()), base, scale * cd.arrayLength()));
/*  78 */       return new ClassLayout(cd, result, this.model.headerSize(), instanceSize, false);
/*     */     }
/*     */     
/*  81 */     List<String> hierarchy = cd.classHierarchy();
/*     */     
/*  83 */     BitSet claimed = new BitSet();
/*     */     
/*  85 */     claimed.set(0, this.model.headerSize());
/*     */     
/*  87 */     for (String k : hierarchy)
/*     */     {
/*  89 */       fields = cd.fieldsFor(k);
/*     */       
/*  91 */       SortedSet<FieldLayout> current = new TreeSet();
/*  92 */       int size; for (size : new int[] { 8, 4, 2, 1 }) {
/*  93 */         for (FieldData f : fields) {
/*  94 */           int fSize = this.model.sizeOf(f.typeClass());
/*  95 */           if (fSize == size)
/*     */           {
/*  97 */             for (int t = 0; t < Integer.MAX_VALUE; t++)
/*  98 */               if (claimed.get(t * size, (t + 1) * size).isEmpty()) {
/*  99 */                 claimed.set(t * size, (t + 1) * size);
/* 100 */                 current.add(new FieldLayout(f, t * size, size));
/* 101 */                 break;
/*     */               }
/*     */           }
/*     */         }
/*     */       }
/* 106 */       result.addAll(current);
/*     */       
/* 108 */       if (!this.takeSuperGaps)
/*     */       {
/* 110 */         if (this.takeHierarchyGaps)
/*     */         {
/* 112 */           int lastSet = claimed.length() - 1;
/* 113 */           claimed.set(0, lastSet);
/*     */         }
/*     */         else {
/* 116 */           int lastSet = claimed.length() - 1;
/* 117 */           claimed.set(0, VMSupport.align(lastSet, 4));
/*     */         } } }
/*     */     Collection<FieldData> fields;
/*     */     int instanceSize;
/*     */     int instanceSize;
/* 122 */     if (this.autoAlign) {
/* 123 */       int a = 4;
/* 124 */       for (FieldLayout f : result) {
/* 125 */         a = Math.max(a, this.model.sizeOf(f.typeClass()));
/*     */       }
/* 127 */       instanceSize = VMSupport.align(claimed.length(), a);
/*     */     } else {
/* 129 */       instanceSize = VMSupport.align(claimed.length());
/*     */     }
/*     */     
/* 132 */     return new ClassLayout(cd, result, this.model.headerSize(), instanceSize, true);
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 137 */     return "VM Layout Simulation (" + this.model + (this.takeHierarchyGaps ? ", hierarchy gaps" : "") + (this.takeSuperGaps ? ", super gaps" : "") + (this.autoAlign ? ", autoalign" : "") + ")";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\layouters\HotSpotLayouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */