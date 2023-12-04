/*    */ package com.mysql.fabric.proto.xmlrpc;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
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
/*    */ public class ResultSetParser
/*    */ {
/*    */   public List<Map<String, ?>> parse(Map<String, ?> info, List<List<Object>> rows)
/*    */   {
/* 44 */     List<String> fieldNames = (List)info.get("names");
/* 45 */     Map<String, Integer> fieldNameIndexes = new HashMap();
/* 46 */     for (int i = 0; i < fieldNames.size(); i++) {
/* 47 */       fieldNameIndexes.put(fieldNames.get(i), Integer.valueOf(i));
/*    */     }
/*    */     
/* 50 */     List<Map<String, ?>> result = new ArrayList(rows.size());
/* 51 */     for (List<Object> r : rows) {
/* 52 */       Map<String, Object> resultRow = new HashMap();
/* 53 */       for (Entry<String, Integer> f : fieldNameIndexes.entrySet()) {
/* 54 */         resultRow.put(f.getKey(), r.get(((Integer)f.getValue()).intValue()));
/*    */       }
/* 56 */       result.add(resultRow);
/*    */     }
/* 58 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\proto\xmlrpc\ResultSetParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */