/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.sql.SQLException;
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
/*    */ public class ResultSetUtil
/*    */ {
/*    */   public static StringBuilder appendResultSetSlashGStyle(StringBuilder appendTo, ResultSet rs)
/*    */     throws SQLException
/*    */   {
/* 36 */     ResultSetMetaData rsmd = rs.getMetaData();
/*    */     
/* 38 */     int numFields = rsmd.getColumnCount();
/* 39 */     int maxWidth = 0;
/*    */     
/* 41 */     String[] fieldNames = new String[numFields];
/*    */     
/* 43 */     for (int i = 0; i < numFields; i++) {
/* 44 */       fieldNames[i] = rsmd.getColumnLabel(i + 1);
/*    */       
/* 46 */       if (fieldNames[i].length() > maxWidth) {
/* 47 */         maxWidth = fieldNames[i].length();
/*    */       }
/*    */     }
/*    */     
/* 51 */     int rowCount = 1;
/*    */     
/* 53 */     while (rs.next()) {
/* 54 */       appendTo.append("*************************** ");
/* 55 */       appendTo.append(rowCount++);
/* 56 */       appendTo.append(". row ***************************\n");
/*    */       
/* 58 */       for (int i = 0; i < numFields; i++) {
/* 59 */         int leftPad = maxWidth - fieldNames[i].length();
/*    */         
/* 61 */         for (int j = 0; j < leftPad; j++) {
/* 62 */           appendTo.append(" ");
/*    */         }
/*    */         
/* 65 */         appendTo.append(fieldNames[i]);
/* 66 */         appendTo.append(": ");
/*    */         
/* 68 */         String stringVal = rs.getString(i + 1);
/*    */         
/* 70 */         if (stringVal != null) {
/* 71 */           appendTo.append(stringVal);
/*    */         } else {
/* 73 */           appendTo.append("NULL");
/*    */         }
/*    */         
/* 76 */         appendTo.append("\n");
/*    */       }
/*    */       
/* 79 */       appendTo.append("\n");
/*    */     }
/*    */     
/* 82 */     return appendTo;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\ResultSetUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */