/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RegexSet
/*     */   extends AbstractSet<String>
/*     */   implements Predicate<String>
/*     */ {
/*  36 */   private final Set<String> _patterns = new HashSet();
/*  37 */   private final Set<String> _unmodifiable = Collections.unmodifiableSet(this._patterns);
/*     */   
/*     */   private Pattern _pattern;
/*     */   
/*     */   public Iterator<String> iterator()
/*     */   {
/*  43 */     return this._unmodifiable.iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  49 */     return this._patterns.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean add(String pattern)
/*     */   {
/*  55 */     boolean added = this._patterns.add(pattern);
/*  56 */     if (added)
/*  57 */       updatePattern();
/*  58 */     return added;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remove(Object pattern)
/*     */   {
/*  64 */     boolean removed = this._patterns.remove(pattern);
/*     */     
/*  66 */     if (removed)
/*  67 */       updatePattern();
/*  68 */     return removed;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  74 */     return this._patterns.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/*  80 */     this._patterns.clear();
/*  81 */     this._pattern = null;
/*     */   }
/*     */   
/*     */   private void updatePattern()
/*     */   {
/*  86 */     StringBuilder builder = new StringBuilder();
/*  87 */     builder.append("^(");
/*  88 */     for (String pattern : this._patterns)
/*     */     {
/*  90 */       if (builder.length() > 2)
/*  91 */         builder.append('|');
/*  92 */       builder.append('(');
/*  93 */       builder.append(pattern);
/*  94 */       builder.append(')');
/*     */     }
/*  96 */     builder.append(")$");
/*  97 */     this._pattern = Pattern.compile(builder.toString());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean test(String s)
/*     */   {
/* 103 */     return (this._pattern != null) && (this._pattern.matcher(s).matches());
/*     */   }
/*     */   
/*     */   public boolean matches(String s)
/*     */   {
/* 108 */     return (this._pattern != null) && (this._pattern.matcher(s).matches());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\RegexSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */