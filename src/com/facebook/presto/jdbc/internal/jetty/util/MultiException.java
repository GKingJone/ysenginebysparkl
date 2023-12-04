/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class MultiException
/*     */   extends Exception
/*     */ {
/*     */   private List<Throwable> nested;
/*     */   
/*     */   public MultiException()
/*     */   {
/*  38 */     super("Multiple exceptions");
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(Throwable e)
/*     */   {
/*  44 */     if (e == null) {
/*  45 */       throw new IllegalArgumentException();
/*     */     }
/*  47 */     if (this.nested == null)
/*     */     {
/*  49 */       initCause(e);
/*  50 */       this.nested = new ArrayList();
/*     */     }
/*     */     else {
/*  53 */       addSuppressed(e);
/*     */     }
/*  55 */     if ((e instanceof MultiException))
/*     */     {
/*  57 */       MultiException me = (MultiException)e;
/*  58 */       this.nested.addAll(me.nested);
/*     */     }
/*     */     else {
/*  61 */       this.nested.add(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  67 */     return this.nested == null ? 0 : this.nested.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Throwable> getThrowables()
/*     */   {
/*  73 */     if (this.nested == null)
/*  74 */       return Collections.emptyList();
/*  75 */     return this.nested;
/*     */   }
/*     */   
/*     */ 
/*     */   public Throwable getThrowable(int i)
/*     */   {
/*  81 */     return (Throwable)this.nested.get(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ifExceptionThrow()
/*     */     throws Exception
/*     */   {
/*  94 */     if (this.nested == null) {
/*  95 */       return;
/*     */     }
/*  97 */     switch (this.nested.size())
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     case 1: 
/* 102 */       Throwable th = (Throwable)this.nested.get(0);
/* 103 */       if ((th instanceof Error))
/* 104 */         throw ((Error)th);
/* 105 */       if ((th instanceof Exception))
/* 106 */         throw ((Exception)th);
/*     */       break; }
/* 108 */     throw this;
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
/*     */ 
/*     */   public void ifExceptionThrowRuntime()
/*     */     throws Error
/*     */   {
/* 124 */     if (this.nested == null) {
/* 125 */       return;
/*     */     }
/* 127 */     switch (this.nested.size())
/*     */     {
/*     */     case 0: 
/*     */       break;
/*     */     case 1: 
/* 132 */       Throwable th = (Throwable)this.nested.get(0);
/* 133 */       if ((th instanceof Error))
/* 134 */         throw ((Error)th);
/* 135 */       if ((th instanceof RuntimeException)) {
/* 136 */         throw ((RuntimeException)th);
/*     */       }
/* 138 */       throw new RuntimeException(th);
/*     */     default: 
/* 140 */       throw new RuntimeException(this);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ifExceptionThrowMulti()
/*     */     throws MultiException
/*     */   {
/* 154 */     if (this.nested == null) {
/* 155 */       return;
/*     */     }
/* 157 */     if (this.nested.size() > 0) {
/* 158 */       throw this;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 165 */     StringBuilder str = new StringBuilder();
/* 166 */     str.append(MultiException.class.getSimpleName());
/* 167 */     if ((this.nested == null) || (this.nested.size() <= 0)) {
/* 168 */       str.append("[]");
/*     */     } else {
/* 170 */       str.append(this.nested);
/*     */     }
/* 172 */     return str.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\MultiException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */