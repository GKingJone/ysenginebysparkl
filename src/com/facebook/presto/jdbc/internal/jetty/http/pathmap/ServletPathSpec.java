/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
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
/*     */ public class ServletPathSpec
/*     */   extends PathSpec
/*     */ {
/*     */   public ServletPathSpec(String servletPathSpec)
/*     */   {
/*  28 */     assertValidServletPathSpec(servletPathSpec);
/*     */     
/*     */ 
/*  31 */     if ((servletPathSpec == null) || (servletPathSpec.length() == 0))
/*     */     {
/*  33 */       this.pathSpec = "";
/*  34 */       this.pathDepth = -1;
/*  35 */       this.specLength = 1;
/*  36 */       this.group = PathSpecGroup.ROOT;
/*  37 */       return;
/*     */     }
/*     */     
/*     */ 
/*  41 */     if ("/".equals(servletPathSpec))
/*     */     {
/*  43 */       this.pathSpec = "/";
/*  44 */       this.pathDepth = -1;
/*  45 */       this.specLength = 1;
/*  46 */       this.group = PathSpecGroup.DEFAULT;
/*  47 */       return;
/*     */     }
/*     */     
/*  50 */     this.specLength = servletPathSpec.length();
/*  51 */     this.pathDepth = 0;
/*  52 */     char lastChar = servletPathSpec.charAt(this.specLength - 1);
/*     */     
/*  54 */     if ((servletPathSpec.charAt(0) == '/') && (this.specLength > 1) && (lastChar == '*'))
/*     */     {
/*  56 */       this.group = PathSpecGroup.PREFIX_GLOB;
/*     */ 
/*     */     }
/*  59 */     else if (servletPathSpec.charAt(0) == '*')
/*     */     {
/*  61 */       this.group = PathSpecGroup.SUFFIX_GLOB;
/*     */     }
/*     */     else
/*     */     {
/*  65 */       this.group = PathSpecGroup.EXACT;
/*     */     }
/*     */     
/*  68 */     for (int i = 0; i < this.specLength; i++)
/*     */     {
/*  70 */       int cp = servletPathSpec.codePointAt(i);
/*  71 */       if (cp < 128)
/*     */       {
/*  73 */         char c = (char)cp;
/*  74 */         switch (c)
/*     */         {
/*     */         case '/': 
/*  77 */           this.pathDepth += 1;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*  83 */     this.pathSpec = servletPathSpec;
/*     */   }
/*     */   
/*     */   private void assertValidServletPathSpec(String servletPathSpec)
/*     */   {
/*  88 */     if ((servletPathSpec == null) || (servletPathSpec.equals("")))
/*     */     {
/*  90 */       return;
/*     */     }
/*     */     
/*  93 */     int len = servletPathSpec.length();
/*     */     
/*  95 */     if (servletPathSpec.charAt(0) == '/')
/*     */     {
/*     */ 
/*  98 */       if (len == 1)
/*     */       {
/* 100 */         return;
/*     */       }
/* 102 */       int idx = servletPathSpec.indexOf('*');
/* 103 */       if (idx < 0)
/*     */       {
/* 105 */         return;
/*     */       }
/*     */       
/* 108 */       if (idx != len - 1)
/*     */       {
/* 110 */         throw new IllegalArgumentException("Servlet Spec 12.2 violation: glob '*' can only exist at end of prefix based matches: bad spec \"" + servletPathSpec + "\"");
/*     */       }
/*     */     }
/* 113 */     else if (servletPathSpec.startsWith("*."))
/*     */     {
/*     */ 
/* 116 */       int idx = servletPathSpec.indexOf('/');
/*     */       
/* 118 */       if (idx >= 0)
/*     */       {
/* 120 */         throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have path separators: bad spec \"" + servletPathSpec + "\"");
/*     */       }
/*     */       
/* 123 */       idx = servletPathSpec.indexOf('*', 2);
/*     */       
/* 125 */       if (idx >= 1)
/*     */       {
/* 127 */         throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have multiple glob '*': bad spec \"" + servletPathSpec + "\"");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 132 */       throw new IllegalArgumentException("Servlet Spec 12.2 violation: path spec must start with \"/\" or \"*.\": bad spec \"" + servletPathSpec + "\"");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getPathInfo(String path)
/*     */   {
/* 140 */     if (this.group == PathSpecGroup.PREFIX_GLOB)
/*     */     {
/* 142 */       if (path.length() == this.specLength - 2)
/*     */       {
/* 144 */         return null;
/*     */       }
/* 146 */       return path.substring(this.specLength - 2);
/*     */     }
/*     */     
/* 149 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPathMatch(String path)
/*     */   {
/* 155 */     switch (this.group)
/*     */     {
/*     */     case EXACT: 
/* 158 */       if (this.pathSpec.equals(path))
/*     */       {
/* 160 */         return path;
/*     */       }
/*     */       
/*     */ 
/* 164 */       return null;
/*     */     
/*     */     case PREFIX_GLOB: 
/* 167 */       if (isWildcardMatch(path))
/*     */       {
/* 169 */         return path.substring(0, this.specLength - 2);
/*     */       }
/*     */       
/*     */ 
/* 173 */       return null;
/*     */     
/*     */     case SUFFIX_GLOB: 
/* 176 */       if (path.regionMatches(path.length() - (this.specLength - 1), this.pathSpec, 1, this.specLength - 1))
/*     */       {
/* 178 */         return path;
/*     */       }
/*     */       
/*     */ 
/* 182 */       return null;
/*     */     
/*     */     case DEFAULT: 
/* 185 */       return path;
/*     */     }
/* 187 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getRelativePath(String base, String path)
/*     */   {
/* 194 */     String info = getPathInfo(path);
/* 195 */     if (info == null)
/*     */     {
/* 197 */       info = path;
/*     */     }
/*     */     
/* 200 */     if (info.startsWith("./"))
/*     */     {
/* 202 */       info = info.substring(2);
/*     */     }
/* 204 */     if (base.endsWith("/"))
/*     */     {
/* 206 */       if (info.startsWith("/"))
/*     */       {
/* 208 */         path = base + info.substring(1);
/*     */       }
/*     */       else
/*     */       {
/* 212 */         path = base + info;
/*     */       }
/*     */     }
/* 215 */     else if (info.startsWith("/"))
/*     */     {
/* 217 */       path = base + info;
/*     */     }
/*     */     else
/*     */     {
/* 221 */       path = base + "/" + info;
/*     */     }
/* 223 */     return path;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean isWildcardMatch(String path)
/*     */   {
/* 229 */     int cpl = this.specLength - 2;
/* 230 */     if ((this.group == PathSpecGroup.PREFIX_GLOB) && (path.regionMatches(0, this.pathSpec, 0, cpl)))
/*     */     {
/* 232 */       if ((path.length() == cpl) || ('/' == path.charAt(cpl)))
/*     */       {
/* 234 */         return true;
/*     */       }
/*     */     }
/* 237 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean matches(String path)
/*     */   {
/* 243 */     switch (this.group)
/*     */     {
/*     */     case EXACT: 
/* 246 */       return this.pathSpec.equals(path);
/*     */     case PREFIX_GLOB: 
/* 248 */       return isWildcardMatch(path);
/*     */     case SUFFIX_GLOB: 
/* 250 */       return path.regionMatches(path.length() - this.specLength + 1, this.pathSpec, 1, this.specLength - 1);
/*     */     
/*     */     case ROOT: 
/* 253 */       return "/".equals(path);
/*     */     
/*     */     case DEFAULT: 
/* 256 */       return true;
/*     */     }
/* 258 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\ServletPathSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */