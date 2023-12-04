/*    */ package com.mysql.fabric.xmlrpc.base;
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
/*    */ public class MethodCall
/*    */ {
/*    */   protected String methodName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected Params params;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getMethodName()
/*    */   {
/* 35 */     return this.methodName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setMethodName(String value)
/*    */   {
/* 42 */     this.methodName = value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Params getParams()
/*    */   {
/* 49 */     return this.params;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setParams(Params value)
/*    */   {
/* 56 */     this.params = value;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 61 */     StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/* 62 */     sb.append("<methodCall>");
/* 63 */     sb.append("\t<methodName>" + this.methodName + "</methodName>");
/* 64 */     if (this.params != null) {
/* 65 */       sb.append(this.params.toString());
/*    */     }
/* 67 */     sb.append("</methodCall>");
/* 68 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\MethodCall.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */