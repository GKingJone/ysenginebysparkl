/*    */ package com.mysql.fabric.xmlrpc.base;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class Data
/*    */ {
/*    */   protected List<Value> value;
/*    */   
/*    */   public List<Value> getValue()
/*    */   {
/* 34 */     if (this.value == null) {
/* 35 */       this.value = new ArrayList();
/*    */     }
/* 37 */     return this.value;
/*    */   }
/*    */   
/*    */   public void addValue(Value v) {
/* 41 */     getValue().add(v);
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 46 */     StringBuilder sb = new StringBuilder();
/* 47 */     if (this.value != null) {
/* 48 */       sb.append("<data>");
/* 49 */       for (int i = 0; i < this.value.size(); i++) {
/* 50 */         sb.append(((Value)this.value.get(i)).toString());
/*    */       }
/* 52 */       sb.append("</data>");
/*    */     }
/* 54 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Data.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */