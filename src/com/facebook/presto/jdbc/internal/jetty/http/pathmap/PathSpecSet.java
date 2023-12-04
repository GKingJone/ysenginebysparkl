/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathSpecSet
/*     */   implements Set<String>, Predicate<String>
/*     */ {
/*  36 */   private final Set<PathSpec> specs = new TreeSet();
/*     */   
/*     */ 
/*     */   public boolean test(String s)
/*     */   {
/*  41 */     for (PathSpec spec : this.specs)
/*     */     {
/*  43 */       if (spec.matches(s))
/*     */       {
/*  45 */         return true;
/*     */       }
/*     */     }
/*  48 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  54 */     return this.specs.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<String> iterator()
/*     */   {
/*  60 */     new Iterator()
/*     */     {
/*  62 */       private Iterator<PathSpec> iter = PathSpecSet.this.specs.iterator();
/*     */       
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/*  67 */         return this.iter.hasNext();
/*     */       }
/*     */       
/*     */ 
/*     */       public String next()
/*     */       {
/*  73 */         PathSpec spec = (PathSpec)this.iter.next();
/*  74 */         if (spec == null)
/*     */         {
/*  76 */           return null;
/*     */         }
/*  78 */         return spec.getDeclaration();
/*     */       }
/*     */       
/*     */ 
/*     */       public void remove()
/*     */       {
/*  84 */         throw new UnsupportedOperationException("Remove not supported by this Iterator");
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  92 */     return this.specs.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/*  98 */     if ((o instanceof PathSpec))
/*     */     {
/* 100 */       return this.specs.contains(o);
/*     */     }
/* 102 */     if ((o instanceof String))
/*     */     {
/* 104 */       return this.specs.contains(toPathSpec((String)o));
/*     */     }
/* 106 */     return false;
/*     */   }
/*     */   
/*     */   private PathSpec asPathSpec(Object o)
/*     */   {
/* 111 */     if (o == null)
/*     */     {
/* 113 */       return null;
/*     */     }
/* 115 */     if ((o instanceof PathSpec))
/*     */     {
/* 117 */       return (PathSpec)o;
/*     */     }
/* 119 */     if ((o instanceof String))
/*     */     {
/* 121 */       return toPathSpec((String)o);
/*     */     }
/* 123 */     return toPathSpec(o.toString());
/*     */   }
/*     */   
/*     */   private PathSpec toPathSpec(String rawSpec)
/*     */   {
/* 128 */     if ((rawSpec == null) || (rawSpec.length() < 1))
/*     */     {
/* 130 */       throw new RuntimeException("Path Spec String must start with '^', '/', or '*.': got [" + rawSpec + "]");
/*     */     }
/* 132 */     if (rawSpec.charAt(0) == '^')
/*     */     {
/* 134 */       return new RegexPathSpec(rawSpec);
/*     */     }
/*     */     
/*     */ 
/* 138 */     return new ServletPathSpec(rawSpec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 145 */     return toArray(new String[this.specs.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T[] toArray(T[] a)
/*     */   {
/* 151 */     int i = 0;
/* 152 */     for (PathSpec spec : this.specs)
/*     */     {
/* 154 */       a[(i++)] = spec.getDeclaration();
/*     */     }
/* 156 */     return a;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean add(String e)
/*     */   {
/* 162 */     return this.specs.add(toPathSpec(e));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remove(Object o)
/*     */   {
/* 168 */     return this.specs.remove(asPathSpec(o));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean containsAll(Collection<?> coll)
/*     */   {
/* 174 */     for (Object o : coll)
/*     */     {
/* 176 */       if (!this.specs.contains(asPathSpec(o)))
/* 177 */         return false;
/*     */     }
/* 179 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean addAll(Collection<? extends String> coll)
/*     */   {
/* 185 */     boolean ret = false;
/*     */     
/* 187 */     for (String s : coll)
/*     */     {
/* 189 */       ret |= add(s);
/*     */     }
/*     */     
/* 192 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean retainAll(Collection<?> coll)
/*     */   {
/* 198 */     List<PathSpec> collSpecs = new ArrayList();
/* 199 */     for (Object o : coll)
/*     */     {
/* 201 */       collSpecs.add(asPathSpec(o));
/*     */     }
/* 203 */     return this.specs.retainAll(collSpecs);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean removeAll(Collection<?> coll)
/*     */   {
/* 209 */     List<PathSpec> collSpecs = new ArrayList();
/* 210 */     for (Object o : coll)
/*     */     {
/* 212 */       collSpecs.add(asPathSpec(o));
/*     */     }
/* 214 */     return this.specs.removeAll(collSpecs);
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 220 */     this.specs.clear();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\PathSpecSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */