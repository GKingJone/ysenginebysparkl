/*     */ package com.facebook.presto.jdbc.internal.jetty.http.pathmap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PathSpec
/*     */   implements Comparable<PathSpec>
/*     */ {
/*     */   protected String pathSpec;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PathSpecGroup group;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int pathDepth;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int specLength;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(PathSpec other)
/*     */   {
/*  35 */     int diff = this.group.ordinal() - other.group.ordinal();
/*  36 */     if (diff != 0)
/*     */     {
/*  38 */       return diff;
/*     */     }
/*     */     
/*     */ 
/*  42 */     diff = other.specLength - this.specLength;
/*  43 */     if (diff != 0)
/*     */     {
/*  45 */       return diff;
/*     */     }
/*     */     
/*     */ 
/*  49 */     return this.pathSpec.compareTo(other.pathSpec);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  55 */     if (this == obj)
/*     */     {
/*  57 */       return true;
/*     */     }
/*  59 */     if (obj == null)
/*     */     {
/*  61 */       return false;
/*     */     }
/*  63 */     if (getClass() != obj.getClass())
/*     */     {
/*  65 */       return false;
/*     */     }
/*  67 */     PathSpec other = (PathSpec)obj;
/*  68 */     if (this.pathSpec == null)
/*     */     {
/*  70 */       if (other.pathSpec != null)
/*     */       {
/*  72 */         return false;
/*     */       }
/*     */     }
/*  75 */     else if (!this.pathSpec.equals(other.pathSpec))
/*     */     {
/*  77 */       return false;
/*     */     }
/*  79 */     return true;
/*     */   }
/*     */   
/*     */   public PathSpecGroup getGroup()
/*     */   {
/*  84 */     return this.group;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPathDepth()
/*     */   {
/*  96 */     return this.pathDepth;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getPathInfo(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getPathMatch(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDeclaration()
/*     */   {
/* 124 */     return this.pathSpec;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract String getRelativePath(String paramString1, String paramString2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 141 */     int prime = 31;
/* 142 */     int result = 1;
/* 143 */     result = 31 * result + (this.pathSpec == null ? 0 : this.pathSpec.hashCode());
/* 144 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean matches(String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 159 */     StringBuilder str = new StringBuilder();
/* 160 */     str.append(getClass().getSimpleName()).append("[\"");
/* 161 */     str.append(this.pathSpec);
/* 162 */     str.append("\",pathDepth=").append(this.pathDepth);
/* 163 */     str.append(",group=").append(this.group);
/* 164 */     str.append("]");
/* 165 */     return str.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\pathmap\PathSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */