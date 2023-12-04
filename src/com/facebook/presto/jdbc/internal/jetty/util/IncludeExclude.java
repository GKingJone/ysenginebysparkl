/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IncludeExclude<ITEM>
/*     */ {
/*     */   private final Set<ITEM> _includes;
/*     */   private final Predicate<ITEM> _includePredicate;
/*     */   private final Set<ITEM> _excludes;
/*     */   private final Predicate<ITEM> _excludePredicate;
/*     */   
/*     */   private static class SetContainsPredicate<ITEM>
/*     */     implements Predicate<ITEM>
/*     */   {
/*     */     private final Set<ITEM> set;
/*     */     
/*     */     public SetContainsPredicate(Set<ITEM> set)
/*     */     {
/*  49 */       this.set = set;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean test(ITEM item)
/*     */     {
/*  55 */       return this.set.contains(item);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IncludeExclude()
/*     */   {
/*  64 */     this(HashSet.class);
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
/*     */   public <SET extends Set<ITEM>> IncludeExclude(Class<SET> setClass)
/*     */   {
/*     */     try
/*     */     {
/*  79 */       this._includes = ((Set)setClass.newInstance());
/*  80 */       this._excludes = ((Set)setClass.newInstance());
/*     */       
/*  82 */       if ((this._includes instanceof Predicate)) {
/*  83 */         this._includePredicate = ((Predicate)this._includes);
/*     */       } else {
/*  85 */         this._includePredicate = new SetContainsPredicate(this._includes);
/*     */       }
/*     */       
/*  88 */       if ((this._excludes instanceof Predicate)) {
/*  89 */         this._excludePredicate = ((Predicate)this._excludes);
/*     */       } else {
/*  91 */         this._excludePredicate = new SetContainsPredicate(this._excludes);
/*     */       }
/*     */     }
/*     */     catch (InstantiationException|IllegalAccessException e)
/*     */     {
/*  96 */       throw new RuntimeException(e);
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
/*     */   public <SET extends Set<ITEM>> IncludeExclude(Set<ITEM> includeSet, Predicate<ITEM> includePredicate, Set<ITEM> excludeSet, Predicate<ITEM> excludePredicate)
/*     */   {
/* 111 */     Objects.requireNonNull(includeSet, "Include Set");
/* 112 */     Objects.requireNonNull(includePredicate, "Include Predicate");
/* 113 */     Objects.requireNonNull(excludeSet, "Exclude Set");
/* 114 */     Objects.requireNonNull(excludePredicate, "Exclude Predicate");
/*     */     
/* 116 */     this._includes = includeSet;
/* 117 */     this._includePredicate = includePredicate;
/* 118 */     this._excludes = excludeSet;
/* 119 */     this._excludePredicate = excludePredicate;
/*     */   }
/*     */   
/*     */   public void include(ITEM element)
/*     */   {
/* 124 */     this._includes.add(element);
/*     */   }
/*     */   
/*     */   public void include(ITEM... element)
/*     */   {
/* 129 */     for (ITEM e : element) {
/* 130 */       this._includes.add(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void exclude(ITEM element) {
/* 135 */     this._excludes.add(element);
/*     */   }
/*     */   
/*     */   public void exclude(ITEM... element)
/*     */   {
/* 140 */     for (ITEM e : element) {
/* 141 */       this._excludes.add(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean matches(ITEM e) {
/* 146 */     if ((!this._includes.isEmpty()) && (!this._includePredicate.test(e)))
/* 147 */       return false;
/* 148 */     return !this._excludePredicate.test(e);
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/* 153 */     return this._includes.size() + this._excludes.size();
/*     */   }
/*     */   
/*     */   public Set<ITEM> getIncluded()
/*     */   {
/* 158 */     return this._includes;
/*     */   }
/*     */   
/*     */   public Set<ITEM> getExcluded()
/*     */   {
/* 163 */     return this._excludes;
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 168 */     this._includes.clear();
/* 169 */     this._excludes.clear();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 175 */     return String.format("%s@%x{i=%s,ip=%s,e=%s,ep=%s}", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()), this._includes, this._includePredicate, this._excludes, this._excludePredicate });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IncludeExclude.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */