/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import com.mchange.v2.coalesce.CoalesceChecker;
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
/*    */ public final class IdentityTokenizedCoalesceChecker
/*    */   implements CoalesceChecker
/*    */ {
/* 30 */   public static IdentityTokenizedCoalesceChecker INSTANCE = new IdentityTokenizedCoalesceChecker();
/*    */   
/*    */   public boolean checkCoalesce(Object a, Object b)
/*    */   {
/* 34 */     IdentityTokenized aa = (IdentityTokenized)a;
/* 35 */     IdentityTokenized bb = (IdentityTokenized)b;
/*    */     
/* 37 */     String ta = aa.getIdentityToken();
/* 38 */     String tb = bb.getIdentityToken();
/*    */     
/* 40 */     if ((ta == null) || (tb == null)) {
/* 41 */       throw new NullPointerException("[c3p0 bug] An IdentityTokenized object has no identity token set?!?! " + (ta == null ? ta : tb));
/*    */     }
/* 43 */     return ta.equals(tb);
/*    */   }
/*    */   
/*    */   public int coalesceHash(Object a)
/*    */   {
/* 48 */     String t = ((IdentityTokenized)a).getIdentityToken();
/* 49 */     return t != null ? t.hashCode() : 0;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\IdentityTokenizedCoalesceChecker.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */