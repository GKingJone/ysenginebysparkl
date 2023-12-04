/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import com.mchange.v2.c3p0.C3P0Registry;
/*    */ import java.io.ObjectStreamException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class IdentityTokenResolvable
/*    */   extends AbstractIdentityTokenized
/*    */ {
/*    */   public static Object doResolve(IdentityTokenized itd)
/*    */   {
/* 46 */     return C3P0Registry.reregister(itd);
/*    */   }
/*    */   
/*    */   protected Object readResolve() throws ObjectStreamException
/*    */   {
/* 51 */     Object out = doResolve(this);
/* 52 */     verifyResolve(out);
/*    */     
/*    */ 
/* 55 */     return out;
/*    */   }
/*    */   
/*    */   protected void verifyResolve(Object o)
/*    */     throws ObjectStreamException
/*    */   {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\IdentityTokenResolvable.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */