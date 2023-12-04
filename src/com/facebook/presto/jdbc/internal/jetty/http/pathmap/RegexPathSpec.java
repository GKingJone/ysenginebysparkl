/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*     */ 
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
/*     */ public class RegexPathSpec
/*     */   extends PathSpec
/*     */ {
/*     */   protected Pattern pattern;
/*     */   
/*     */   protected RegexPathSpec() {}
/*     */   
/*     */   public RegexPathSpec(String regex)
/*     */   {
/*  35 */     this.pathSpec = regex;
/*  36 */     boolean inGrouping = false;
/*  37 */     this.pathDepth = 0;
/*  38 */     this.specLength = this.pathSpec.length();
/*     */     
/*  40 */     StringBuilder signature = new StringBuilder();
/*  41 */     for (char c : this.pathSpec.toCharArray())
/*     */     {
/*  43 */       switch (c)
/*     */       {
/*     */       case '[': 
/*  46 */         inGrouping = true;
/*  47 */         break;
/*     */       case ']': 
/*  49 */         inGrouping = false;
/*  50 */         signature.append('g');
/*  51 */         break;
/*     */       case '*': 
/*  53 */         signature.append('g');
/*  54 */         break;
/*     */       case '/': 
/*  56 */         if (!inGrouping)
/*     */         {
/*  58 */           this.pathDepth += 1;
/*     */         }
/*     */         break;
/*     */       default: 
/*  62 */         if (!inGrouping)
/*     */         {
/*  64 */           if (Character.isLetterOrDigit(c))
/*     */           {
/*  66 */             signature.append('l');
/*     */           }
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*  72 */     this.pattern = Pattern.compile(this.pathSpec);
/*     */     
/*     */ 
/*  75 */     String sig = signature.toString();
/*     */     
/*  77 */     if (Pattern.matches("^l*$", sig))
/*     */     {
/*  79 */       this.group = PathSpecGroup.EXACT;
/*     */     }
/*  81 */     else if (Pattern.matches("^l*g+", sig))
/*     */     {
/*  83 */       this.group = PathSpecGroup.PREFIX_GLOB;
/*     */     }
/*  85 */     else if (Pattern.matches("^g+l+$", sig))
/*     */     {
/*  87 */       this.group = PathSpecGroup.SUFFIX_GLOB;
/*     */     }
/*     */     else
/*     */     {
/*  91 */       this.group = PathSpecGroup.MIDDLE_GLOB;
/*     */     }
/*     */   }
/*     */   
/*     */   public Matcher getMatcher(String path)
/*     */   {
/*  97 */     return this.pattern.matcher(path);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPathInfo(String path)
/*     */   {
/* 104 */     if (this.group == PathSpecGroup.PREFIX_GLOB)
/*     */     {
/* 106 */       Matcher matcher = getMatcher(path);
/* 107 */       if (matcher.matches())
/*     */       {
/* 109 */         if (matcher.groupCount() >= 1)
/*     */         {
/* 111 */           String pathInfo = matcher.group(1);
/* 112 */           if ("".equals(pathInfo))
/*     */           {
/* 114 */             return "/";
/*     */           }
/*     */           
/*     */ 
/* 118 */           return pathInfo;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 123 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPathMatch(String path)
/*     */   {
/* 129 */     Matcher matcher = getMatcher(path);
/* 130 */     if (matcher.matches())
/*     */     {
/* 132 */       if (matcher.groupCount() >= 1)
/*     */       {
/* 134 */         int idx = matcher.start(1);
/* 135 */         if (idx > 0)
/*     */         {
/* 137 */           if (path.charAt(idx - 1) == '/')
/*     */           {
/* 139 */             idx--;
/*     */           }
/* 141 */           return path.substring(0, idx);
/*     */         }
/*     */       }
/* 144 */       return path;
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */   
/*     */   public Pattern getPattern()
/*     */   {
/* 151 */     return this.pattern;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRelativePath(String base, String path)
/*     */   {
/* 158 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean matches(String path)
/*     */   {
/* 164 */     int idx = path.indexOf('?');
/* 165 */     if (idx >= 0)
/*     */     {
/*     */ 
/* 168 */       return getMatcher(path.substring(0, idx)).matches();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 173 */     return getMatcher(path).matches();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\RegexPathSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */