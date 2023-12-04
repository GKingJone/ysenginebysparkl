/*     */ package com.mchange.v2.codegen.bean;
/*     */ 
/*     */ import com.mchange.v2.codegen.IndentedWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ public class CloneableExtension
/*     */   implements GeneratorExtension
/*     */ {
/*     */   boolean export_public;
/*     */   boolean exception_swallowing;
/*  35 */   String mLoggerName = null;
/*     */   
/*     */   public boolean isExportPublic() {
/*  38 */     return this.export_public;
/*     */   }
/*     */   
/*  41 */   public void setExportPublic(boolean export_public) { this.export_public = export_public; }
/*     */   
/*     */   public boolean isExceptionSwallowing() {
/*  44 */     return this.exception_swallowing;
/*     */   }
/*     */   
/*  47 */   public void setExceptionSwallowing(boolean exception_swallowing) { this.exception_swallowing = exception_swallowing; }
/*     */   
/*     */   public String getMLoggerName() {
/*  50 */     return this.mLoggerName;
/*     */   }
/*     */   
/*  53 */   public void setMLoggerName(String mLoggerName) { this.mLoggerName = mLoggerName; }
/*     */   
/*     */   public CloneableExtension(boolean export_public, boolean exception_swallowing)
/*     */   {
/*  57 */     this.export_public = export_public;
/*  58 */     this.exception_swallowing = exception_swallowing;
/*     */   }
/*     */   
/*     */   public CloneableExtension() {
/*  62 */     this(true, false);
/*     */   }
/*     */   
/*  65 */   public Collection extraGeneralImports() { return this.mLoggerName == null ? Collections.EMPTY_SET : Arrays.asList(new String[] { "com.mchange.v2.log" }); }
/*     */   
/*     */   public Collection extraSpecificImports() {
/*  68 */     return Collections.EMPTY_SET;
/*     */   }
/*     */   
/*     */   public Collection extraInterfaceNames() {
/*  72 */     Set set = new HashSet();
/*  73 */     set.add("Cloneable");
/*  74 */     return set;
/*     */   }
/*     */   
/*     */   public void generate(ClassInfo info, Class superclassType, Property[] props, Class[] propTypes, IndentedWriter iw)
/*     */     throws IOException
/*     */   {
/*  80 */     if (this.export_public)
/*     */     {
/*  82 */       iw.print("public Object clone()");
/*  83 */       if (!this.exception_swallowing) {
/*  84 */         iw.println(" throws CloneNotSupportedException");
/*     */       } else
/*  86 */         iw.println();
/*  87 */       iw.println("{");
/*  88 */       iw.upIndent();
/*  89 */       if (this.exception_swallowing)
/*     */       {
/*  91 */         iw.println("try");
/*  92 */         iw.println("{");
/*  93 */         iw.upIndent();
/*     */       }
/*  95 */       iw.println("return super.clone();");
/*  96 */       if (this.exception_swallowing)
/*     */       {
/*  98 */         iw.downIndent();
/*  99 */         iw.println("}");
/* 100 */         iw.println("catch (CloneNotSupportedException e)");
/* 101 */         iw.println("{");
/* 102 */         iw.upIndent();
/* 103 */         if (this.mLoggerName == null) {
/* 104 */           iw.println("e.printStackTrace();");
/*     */         }
/*     */         else {
/* 107 */           iw.println("if ( " + this.mLoggerName + ".isLoggable( MLevel.FINE ) )");
/* 108 */           iw.upIndent();
/* 109 */           iw.println(this.mLoggerName + ".log( MLevel.FINE, \"Inconsistent clone() definitions between subclass and superclass! \", e );");
/* 110 */           iw.downIndent();
/*     */         }
/* 112 */         iw.println("throw new RuntimeException(\"Inconsistent clone() definitions between subclass and superclass! \" + e);");
/* 113 */         iw.downIndent();
/* 114 */         iw.println("}");
/*     */       }
/*     */       
/* 117 */       iw.downIndent();
/* 118 */       iw.println("}");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\codegen\bean\CloneableExtension.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */