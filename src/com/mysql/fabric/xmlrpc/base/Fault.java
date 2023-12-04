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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Fault
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Value getValue()
/*    */   {
/* 34 */     return this.value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setValue(Value value)
/*    */   {
/* 41 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 46 */     StringBuilder sb = new StringBuilder();
/* 47 */     if (this.value != null) {
/* 48 */       sb.append("<fault>");
/* 49 */       sb.append(this.value.toString());
/* 50 */       sb.append("</fault>");
/*    */     }
/* 52 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Fault.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */