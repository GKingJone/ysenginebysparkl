/*     */ package com.mchange.v2.debug;
/*     */ 
/*     */ import com.mchange.lang.ThrowableUtils;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ public class ThreadNameStackTraceRecorder
/*     */ {
/*  32 */   static final String NL = System.getProperty("line.separator", "\r\n");
/*     */   
/*  34 */   Set set = new HashSet();
/*     */   String dumpHeader;
/*     */   String stackTraceHeader;
/*     */   
/*     */   public ThreadNameStackTraceRecorder(String dumpHeader)
/*     */   {
/*  40 */     this(dumpHeader, "Debug Stack Trace.");
/*     */   }
/*     */   
/*     */   public ThreadNameStackTraceRecorder(String dumpHeader, String stackTraceHeader) {
/*  44 */     this.dumpHeader = dumpHeader;
/*  45 */     this.stackTraceHeader = stackTraceHeader;
/*     */   }
/*     */   
/*     */   public synchronized Object record()
/*     */   {
/*  50 */     Record r = new Record(this.stackTraceHeader);
/*  51 */     this.set.add(r);
/*  52 */     return r;
/*     */   }
/*     */   
/*     */   public synchronized void remove(Object rec) {
/*  56 */     this.set.remove(rec);
/*     */   }
/*     */   
/*  59 */   public synchronized int size() { return this.set.size(); }
/*     */   
/*     */   public synchronized String getDump() {
/*  62 */     return getDump(null);
/*     */   }
/*     */   
/*     */   public synchronized String getDump(String locationSpecificNote) {
/*  66 */     DateFormat df = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss.SSSS");
/*     */     
/*  68 */     StringBuffer sb = new StringBuffer(2047);
/*  69 */     sb.append(NL);
/*  70 */     sb.append("----------------------------------------------------");
/*  71 */     sb.append(NL);
/*  72 */     sb.append(this.dumpHeader);
/*  73 */     sb.append(NL);
/*  74 */     if (locationSpecificNote != null)
/*     */     {
/*  76 */       sb.append(locationSpecificNote);
/*  77 */       sb.append(NL);
/*     */     }
/*  79 */     boolean first = true;
/*  80 */     for (Iterator ii = this.set.iterator(); ii.hasNext();)
/*     */     {
/*  82 */       if (first) {
/*  83 */         first = false;
/*     */       }
/*     */       else {
/*  86 */         sb.append("---");
/*  87 */         sb.append(NL);
/*     */       }
/*     */       
/*  90 */       Record r = (Record)ii.next();
/*  91 */       sb.append(df.format(new Date(r.time)));
/*  92 */       sb.append(" --> Thread Name: ");
/*  93 */       sb.append(r.threadName);
/*  94 */       sb.append(NL);
/*  95 */       sb.append("Stack Trace: ");
/*  96 */       sb.append(ThrowableUtils.extractStackTrace(r.stackTrace));
/*     */     }
/*  98 */     sb.append("----------------------------------------------------");
/*  99 */     sb.append(NL);
/* 100 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static final class Record implements Comparable
/*     */   {
/*     */     long time;
/*     */     String threadName;
/*     */     Throwable stackTrace;
/*     */     
/*     */     Record(String sth)
/*     */     {
/* 111 */       this.time = System.currentTimeMillis();
/* 112 */       this.threadName = Thread.currentThread().getName();
/* 113 */       this.stackTrace = new Exception(sth);
/*     */     }
/*     */     
/*     */     public int compareTo(Object o)
/*     */     {
/* 118 */       Record oo = (Record)o;
/* 119 */       if (this.time > oo.time)
/* 120 */         return 1;
/* 121 */       if (this.time < oo.time) {
/* 122 */         return -1;
/*     */       }
/*     */       
/* 125 */       int mine = System.identityHashCode(this);
/* 126 */       int yours = System.identityHashCode(oo);
/* 127 */       if (mine > yours)
/* 128 */         return 1;
/* 129 */       if (mine < yours)
/* 130 */         return -1;
/* 131 */       return 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\debug\ThreadNameStackTraceRecorder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */