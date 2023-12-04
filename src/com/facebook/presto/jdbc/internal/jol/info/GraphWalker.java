/*     */ package com.facebook.presto.jdbc.internal.jol.info;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GraphWalker
/*     */ {
/*     */   private final Set<Object> visited;
/*     */   private final Object root;
/*     */   private final Collection<GraphVisitor> visitors;
/*     */   
/*     */   public GraphWalker(Object root)
/*     */   {
/*  48 */     this.root = root;
/*  49 */     this.visited = Collections.newSetFromMap(new IdentityHashMap());
/*  50 */     this.visitors = new ArrayList();
/*     */   }
/*     */   
/*     */   public void addVisitor(GraphVisitor v) {
/*  54 */     this.visitors.add(v);
/*     */   }
/*     */   
/*     */   public void walk() {
/*  58 */     List<GraphPathRecord> curLayer = new ArrayList();
/*  59 */     List<GraphPathRecord> newLayer = new ArrayList();
/*     */     
/*  61 */     GraphPathRecord e = new GraphPathRecord("", this.root, 0);
/*  62 */     this.visited.add(this.root);
/*  63 */     visitObject(e);
/*  64 */     curLayer.add(e);
/*     */     
/*  66 */     while (!curLayer.isEmpty()) {
/*  67 */       newLayer.clear();
/*  68 */       for (GraphPathRecord next : curLayer) {
/*  69 */         for (GraphPathRecord ref : peelReferences(next)) {
/*  70 */           if ((ref != null) && 
/*  71 */             (this.visited.add(ref.obj()))) {
/*  72 */             visitObject(ref);
/*  73 */             newLayer.add(ref);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  78 */       curLayer.clear();
/*  79 */       curLayer.addAll(newLayer);
/*     */     }
/*     */   }
/*     */   
/*     */   private void visitObject(GraphPathRecord record) {
/*  84 */     for (GraphVisitor v : this.visitors) {
/*  85 */       v.visit(record);
/*     */     }
/*     */   }
/*     */   
/*     */   private List<GraphPathRecord> peelReferences(GraphPathRecord r) {
/*  90 */     List<GraphPathRecord> result = new ArrayList();
/*     */     
/*  92 */     Object o = r.obj();
/*  93 */     int c; if ((o.getClass().isArray()) && (!o.getClass().getComponentType().isPrimitive())) {
/*  94 */       c = 0;
/*  95 */       for (Object e : (Object[])o) {
/*  96 */         if (e != null) {
/*  97 */           result.add(new GraphPathRecord(r.path() + "[" + c + "]", e, r.depth() + 1));
/*     */         }
/*  99 */         c++;
/*     */       }
/*     */     }
/*     */     
/* 103 */     for (Field f : getAllFields(o.getClass())) {
/* 104 */       f.setAccessible(true);
/* 105 */       if ((!f.getType().isPrimitive()) && 
/* 106 */         (!Modifier.isStatic(f.getModifiers()))) {
/*     */         try
/*     */         {
/* 109 */           Object e = f.get(o);
/* 110 */           if (e != null) {
/* 111 */             result.add(new GraphPathRecord(r.path() + "." + f.getName(), e, r.depth() + 1));
/*     */           }
/*     */         } catch (IllegalAccessException e) {
/* 114 */           throw new IllegalStateException(e);
/*     */         }
/*     */       }
/*     */     }
/* 118 */     return result;
/*     */   }
/*     */   
/*     */   private Collection<Field> getAllFields(Class<?> klass) {
/* 122 */     List<Field> results = new ArrayList();
/*     */     
/* 124 */     Field[] arrayOfField1 = klass.getDeclaredFields();int i = arrayOfField1.length; Field f; for (Field localField1 = 0; localField1 < i; localField1++) { f = arrayOfField1[localField1];
/* 125 */       if (!Modifier.isStatic(f.getModifiers())) {
/* 126 */         results.add(f);
/*     */       }
/*     */     }
/*     */     
/* 130 */     Object superKlass = klass;
/* 131 */     while ((superKlass = ((Class)superKlass).getSuperclass()) != null) {
/* 132 */       Field[] arrayOfField2 = ((Class)superKlass).getDeclaredFields();localField1 = arrayOfField2.length; for (f = 0; f < localField1; f++) { Field f = arrayOfField2[f];
/* 133 */         if (!Modifier.isStatic(f.getModifiers())) {
/* 134 */           results.add(f);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 139 */     return results;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\GraphWalker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */