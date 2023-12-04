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
/*    */ public class Array
/*    */ {
/*    */   protected Data data;
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
/*    */   public Data getData()
/*    */   {
/* 34 */     return this.data;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setData(Data value)
/*    */   {
/* 41 */     this.data = value;
/*    */   }
/*    */   
/*    */   public void addValue(Value v) {
/* 45 */     if (this.data == null) {
/* 46 */       this.data = new Data();
/*    */     }
/* 48 */     this.data.addValue(v);
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 53 */     StringBuilder sb = new StringBuilder("<array>");
/* 54 */     sb.append(this.data.toString());
/* 55 */     sb.append("</array>");
/* 56 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Array.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */