/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
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
/*     */ public class LogUtils
/*     */ {
/*     */   public static final String CALLER_INFORMATION_NOT_AVAILABLE = "Caller information not available";
/*  33 */   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */   
/*  35 */   private static final int LINE_SEPARATOR_LENGTH = LINE_SEPARATOR.length();
/*     */   
/*     */   public static Object expandProfilerEventIfNecessary(Object possibleProfilerEvent)
/*     */   {
/*  39 */     if ((possibleProfilerEvent instanceof ProfilerEvent)) {
/*  40 */       StringBuilder msgBuf = new StringBuilder();
/*     */       
/*  42 */       ProfilerEvent evt = (ProfilerEvent)possibleProfilerEvent;
/*     */       
/*  44 */       String locationInformation = evt.getEventCreationPointAsString();
/*     */       
/*  46 */       if (locationInformation == null) {
/*  47 */         locationInformation = Util.stackTraceToString(new Throwable());
/*     */       }
/*     */       
/*  50 */       msgBuf.append("Profiler Event: [");
/*     */       
/*  52 */       switch (evt.getEventType()) {
/*     */       case 4: 
/*  54 */         msgBuf.append("EXECUTE");
/*     */         
/*  56 */         break;
/*     */       
/*     */       case 5: 
/*  59 */         msgBuf.append("FETCH");
/*     */         
/*  61 */         break;
/*     */       
/*     */       case 1: 
/*  64 */         msgBuf.append("CONSTRUCT");
/*     */         
/*  66 */         break;
/*     */       
/*     */       case 2: 
/*  69 */         msgBuf.append("PREPARE");
/*     */         
/*  71 */         break;
/*     */       
/*     */       case 3: 
/*  74 */         msgBuf.append("QUERY");
/*     */         
/*  76 */         break;
/*     */       
/*     */       case 0: 
/*  79 */         msgBuf.append("WARN");
/*     */         
/*  81 */         break;
/*     */       
/*     */       case 6: 
/*  84 */         msgBuf.append("SLOW QUERY");
/*     */         
/*  86 */         break;
/*     */       
/*     */       default: 
/*  89 */         msgBuf.append("UNKNOWN");
/*     */       }
/*     */       
/*  92 */       msgBuf.append("] ");
/*  93 */       msgBuf.append(locationInformation);
/*  94 */       msgBuf.append(" duration: ");
/*  95 */       msgBuf.append(evt.getEventDuration());
/*  96 */       msgBuf.append(" ");
/*  97 */       msgBuf.append(evt.getDurationUnits());
/*  98 */       msgBuf.append(", connection-id: ");
/*  99 */       msgBuf.append(evt.getConnectionId());
/* 100 */       msgBuf.append(", statement-id: ");
/* 101 */       msgBuf.append(evt.getStatementId());
/* 102 */       msgBuf.append(", resultset-id: ");
/* 103 */       msgBuf.append(evt.getResultSetId());
/*     */       
/* 105 */       String evtMessage = evt.getMessage();
/*     */       
/* 107 */       if (evtMessage != null) {
/* 108 */         msgBuf.append(", message: ");
/* 109 */         msgBuf.append(evtMessage);
/*     */       }
/*     */       
/* 112 */       return msgBuf;
/*     */     }
/*     */     
/* 115 */     return possibleProfilerEvent;
/*     */   }
/*     */   
/*     */   public static String findCallingClassAndMethod(Throwable t) {
/* 119 */     String stackTraceAsString = Util.stackTraceToString(t);
/*     */     
/* 121 */     String callingClassAndMethod = "Caller information not available";
/*     */     
/* 123 */     int endInternalMethods = stackTraceAsString.lastIndexOf("com.mysql.jdbc");
/*     */     
/* 125 */     if (endInternalMethods != -1) {
/* 126 */       int endOfLine = -1;
/* 127 */       int compliancePackage = stackTraceAsString.indexOf("com.mysql.jdbc.compliance", endInternalMethods);
/*     */       
/* 129 */       if (compliancePackage != -1) {
/* 130 */         endOfLine = compliancePackage - LINE_SEPARATOR_LENGTH;
/*     */       } else {
/* 132 */         endOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endInternalMethods);
/*     */       }
/*     */       
/* 135 */       if (endOfLine != -1) {
/* 136 */         int nextEndOfLine = stackTraceAsString.indexOf(LINE_SEPARATOR, endOfLine + LINE_SEPARATOR_LENGTH);
/*     */         
/* 138 */         if (nextEndOfLine != -1) {
/* 139 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH, nextEndOfLine);
/*     */         } else {
/* 141 */           callingClassAndMethod = stackTraceAsString.substring(endOfLine + LINE_SEPARATOR_LENGTH);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 146 */     if ((!callingClassAndMethod.startsWith("\tat ")) && (!callingClassAndMethod.startsWith("at "))) {
/* 147 */       return "at " + callingClassAndMethod;
/*     */     }
/*     */     
/* 150 */     return callingClassAndMethod;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\log\LogUtils.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */