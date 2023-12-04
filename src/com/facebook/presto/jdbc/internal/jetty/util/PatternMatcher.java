/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PatternMatcher
/*     */ {
/*     */   public abstract void matched(URI paramURI)
/*     */     throws Exception;
/*     */   
/*     */   public void match(Pattern pattern, URI[] uris, boolean isNullInclusive)
/*     */     throws Exception
/*     */   {
/*     */     int i;
/*  61 */     if (uris != null)
/*     */     {
/*  63 */       String[] patterns = pattern == null ? null : pattern.pattern().split(",");
/*     */       
/*  65 */       List<Pattern> subPatterns = new ArrayList();
/*  66 */       for (i = 0; (patterns != null) && (i < patterns.length); i++)
/*     */       {
/*  68 */         subPatterns.add(Pattern.compile(patterns[i]));
/*     */       }
/*  70 */       if (subPatterns.isEmpty()) {
/*  71 */         subPatterns.add(pattern);
/*     */       }
/*  73 */       if (subPatterns.isEmpty())
/*     */       {
/*  75 */         matchPatterns(null, uris, isNullInclusive);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  80 */         for (Pattern p : subPatterns)
/*     */         {
/*  82 */           matchPatterns(p, uris, isNullInclusive);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void matchPatterns(Pattern pattern, URI[] uris, boolean isNullInclusive)
/*     */     throws Exception
/*     */   {
/*  92 */     for (int i = 0; i < uris.length; i++)
/*     */     {
/*  94 */       URI uri = uris[i];
/*  95 */       String s = uri.toString();
/*  96 */       if ((pattern != null) || (!isNullInclusive)) { if (pattern != null)
/*     */         {
/*  98 */           if (!pattern.matcher(s).matches()) {} }
/*     */       } else {
/* 100 */         matched(uris[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\PatternMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */