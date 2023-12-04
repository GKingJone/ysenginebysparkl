/*    */ package com.mysql.jdbc.util;
/*    */ 
/*    */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*    */ import java.io.PrintStream;
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
/*    */ public class PropertiesDocGenerator
/*    */   extends ConnectionPropertiesImpl
/*    */ {
/*    */   static final long serialVersionUID = -4869689139143855383L;
/*    */   
/*    */   public static void main(String[] args)
/*    */     throws SQLException
/*    */   {
/* 38 */     System.out.println(new PropertiesDocGenerator().exposeAsXml());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\PropertiesDocGenerator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */