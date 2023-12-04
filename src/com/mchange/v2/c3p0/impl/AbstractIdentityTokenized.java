/*    */ package com.mchange.v2.c3p0.impl;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractIdentityTokenized
/*    */   implements IdentityTokenized
/*    */ {
/*    */   public boolean equals(Object o)
/*    */   {
/* 36 */     if (this == o) {
/* 37 */       return true;
/*    */     }
/* 39 */     if ((o instanceof IdentityTokenized)) {
/* 40 */       return getIdentityToken().equals(((IdentityTokenized)o).getIdentityToken());
/*    */     }
/* 42 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 46 */     return getIdentityToken().hashCode() ^ 0xFFFFFFFF;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\AbstractIdentityTokenized.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */