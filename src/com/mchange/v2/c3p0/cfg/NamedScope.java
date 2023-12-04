/*    */ package com.mchange.v2.c3p0.cfg;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ class NamedScope
/*    */ {
/*    */   HashMap props;
/*    */   HashMap userNamesToOverrides;
/*    */   
/*    */   NamedScope()
/*    */   {
/* 37 */     this.props = new HashMap();
/* 38 */     this.userNamesToOverrides = new HashMap();
/*    */   }
/*    */   
/*    */   NamedScope(HashMap props, HashMap userNamesToOverrides)
/*    */   {
/* 43 */     this.props = props;
/* 44 */     this.userNamesToOverrides = userNamesToOverrides;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\cfg\NamedScope.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */