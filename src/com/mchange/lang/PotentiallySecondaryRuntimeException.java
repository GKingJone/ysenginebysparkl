/*    */ package com.mchange.lang;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PotentiallySecondaryRuntimeException
/*    */   extends RuntimeException
/*    */   implements PotentiallySecondary
/*    */ {
/*    */   static final String NESTED_MSG = ">>>>>>>>>> NESTED EXCEPTION >>>>>>>>";
/*    */   Throwable nested;
/*    */   
/*    */   public PotentiallySecondaryRuntimeException(String msg, Throwable t)
/*    */   {
/* 36 */     super(msg);
/* 37 */     this.nested = t;
/*    */   }
/*    */   
/*    */   public PotentiallySecondaryRuntimeException(Throwable t) {
/* 41 */     this("", t);
/*    */   }
/*    */   
/* 44 */   public PotentiallySecondaryRuntimeException(String msg) { this(msg, null); }
/*    */   
/*    */   public PotentiallySecondaryRuntimeException() {
/* 47 */     this("", null);
/*    */   }
/*    */   
/* 50 */   public Throwable getNestedThrowable() { return this.nested; }
/*    */   
/*    */   public void printStackTrace(PrintWriter pw)
/*    */   {
/* 54 */     super.printStackTrace(pw);
/* 55 */     if (this.nested != null)
/*    */     {
/* 57 */       pw.println(">>>>>>>>>> NESTED EXCEPTION >>>>>>>>");
/* 58 */       this.nested.printStackTrace(pw);
/*    */     }
/*    */   }
/*    */   
/*    */   public void printStackTrace(PrintStream ps)
/*    */   {
/* 64 */     super.printStackTrace(ps);
/* 65 */     if (this.nested != null)
/*    */     {
/* 67 */       ps.println("NESTED_MSG");
/* 68 */       this.nested.printStackTrace(ps);
/*    */     }
/*    */   }
/*    */   
/*    */   public void printStackTrace() {
/* 73 */     printStackTrace(System.err);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\lang\PotentiallySecondaryRuntimeException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */