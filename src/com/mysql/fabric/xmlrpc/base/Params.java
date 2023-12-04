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
/*    */ public class Params
/*    */ {
/*    */   protected List<Param> param;
/*    */   
/*    */   public List<Param> getParam()
/*    */   {
/* 34 */     if (this.param == null) {
/* 35 */       this.param = new ArrayList();
/*    */     }
/* 37 */     return this.param;
/*    */   }
/*    */   
/*    */   public void addParam(Param p) {
/* 41 */     getParam().add(p);
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 46 */     StringBuilder sb = new StringBuilder();
/* 47 */     if (this.param != null) {
/* 48 */       sb.append("<params>");
/* 49 */       for (int i = 0; i < this.param.size(); i++) {
/* 50 */         sb.append(((Param)this.param.get(i)).toString());
/*    */       }
/* 52 */       sb.append("</params>");
/*    */     }
/* 54 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\xmlrpc\base\Params.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */