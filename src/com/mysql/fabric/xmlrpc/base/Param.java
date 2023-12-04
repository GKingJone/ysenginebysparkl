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
/*    */ public class Param
/*    */ {
/*    */   protected Value value;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Param() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Param(Value value)
/*    */   {
/* 34 */     this.value = value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Value getValue()
/*    */   {
/* 41 */     return this.value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(Value value)
/*    */   {
/* 48 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 53 */     StringBuilder sb = new StringBuilder("<param>");
/* 54 */     sb.append(this.value.toString());
/* 55 */     sb.append("</param>");
/* 56 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Param.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */