/*    */ package com.mchange.lang;
/*    */ 
/*    */ import com.mchange.v2.lang.VersionUtils;
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
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class PotentiallySecondaryException
/*    */   extends Exception
/*    */   implements PotentiallySecondary
/*    */ {
/*    */   static final String NESTED_MSG = ">>>>>>>>>> NESTED EXCEPTION >>>>>>>>";
/*    */   Throwable nested;
/*    */   
/*    */   public PotentiallySecondaryException(String msg, Throwable t)
/*    */   {
/* 41 */     super(msg);
/* 42 */     this.nested = t;
/*    */   }
/*    */   
/*    */   public PotentiallySecondaryException(Throwable t) {
/* 46 */     this("", t);
/*    */   }
/*    */   
/* 49 */   public PotentiallySecondaryException(String msg) { this(msg, null); }
/*    */   
/*    */   public PotentiallySecondaryException() {
/* 52 */     this("", null);
/*    */   }
/*    */   
/* 55 */   public Throwable getNestedThrowable() { return this.nested; }
/*    */   
/*    */   private void setNested(Throwable t)
/*    */   {
/* 59 */     this.nested = t;
/* 60 */     if (VersionUtils.isAtLeastJavaVersion14()) {
/* 61 */       initCause(t);
/*    */     }
/*    */   }
/*    */   
/*    */   public void printStackTrace(PrintWriter pw) {
/* 66 */     super.printStackTrace(pw);
/* 67 */     if ((!VersionUtils.isAtLeastJavaVersion14()) && (this.nested != null))
/*    */     {
/* 69 */       pw.println(">>>>>>>>>> NESTED EXCEPTION >>>>>>>>");
/* 70 */       this.nested.printStackTrace(pw);
/*    */     }
/*    */   }
/*    */   
/*    */   public void printStackTrace(PrintStream ps)
/*    */   {
/* 76 */     super.printStackTrace(ps);
/* 77 */     if ((!VersionUtils.isAtLeastJavaVersion14()) && (this.nested != null))
/*    */     {
/* 79 */       ps.println("NESTED_MSG");
/* 80 */       this.nested.printStackTrace(ps);
/*    */     }
/*    */   }
/*    */   
/*    */   public void printStackTrace()
/*    */   {
/* 86 */     if (VersionUtils.isAtLeastJavaVersion14()) {
/* 87 */       super.printStackTrace();
/*    */     } else {
/* 89 */       printStackTrace(System.err);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\lang\PotentiallySecondaryException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */