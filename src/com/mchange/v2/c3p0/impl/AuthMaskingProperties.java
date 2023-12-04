/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.Properties;
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
/*    */ public class AuthMaskingProperties
/*    */   extends Properties
/*    */ {
/*    */   public AuthMaskingProperties() {}
/*    */   
/*    */   public AuthMaskingProperties(Properties p)
/*    */   {
/* 35 */     super(p);
/*    */   }
/*    */   
/*    */   public static AuthMaskingProperties fromAnyProperties(Properties p) {
/* 39 */     AuthMaskingProperties out = new AuthMaskingProperties();
/* 40 */     for (Enumeration e = p.propertyNames(); e.hasMoreElements();)
/*    */     {
/* 42 */       String key = (String)e.nextElement();
/* 43 */       out.setProperty(key, p.getProperty(key));
/*    */     }
/* 45 */     return out;
/*    */   }
/*    */   
/*    */   private String normalToString() {
/* 49 */     return super.toString();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 53 */     boolean hasUser = get("user") != null;
/* 54 */     boolean hasPassword = get("password") != null;
/* 55 */     if ((hasUser) || (hasPassword))
/*    */     {
/* 57 */       AuthMaskingProperties clone = (AuthMaskingProperties)clone();
/* 58 */       if (hasUser)
/* 59 */         clone.put("user", "******");
/* 60 */       if (hasPassword)
/* 61 */         clone.put("password", "******");
/* 62 */       return clone.normalToString();
/*    */     }
/*    */     
/* 65 */     return normalToString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\AuthMaskingProperties.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */