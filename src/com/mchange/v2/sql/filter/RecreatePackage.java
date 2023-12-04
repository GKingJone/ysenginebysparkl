/*    */ package com.mchange.v2.sql.filter;
/*    */ 
/*    */ import com.mchange.v1.lang.ClassUtils;
/*    */ import com.mchange.v2.codegen.intfc.DelegatorGenerator;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.FileWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.Writer;
/*    */ import java.sql.CallableStatement;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DatabaseMetaData;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.Statement;
/*    */ import javax.sql.DataSource;
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
/*    */ public final class RecreatePackage
/*    */ {
/* 35 */   static final Class[] intfcs = { Connection.class, ResultSet.class, DatabaseMetaData.class, Statement.class, PreparedStatement.class, CallableStatement.class, DataSource.class };
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
/*    */   public static void main(String[] argv)
/*    */   {
/*    */     try
/*    */     {
/* 51 */       dg = new DelegatorGenerator();
/* 52 */       String thisClassName = RecreatePackage.class.getName();
/* 53 */       pkg = thisClassName.substring(0, thisClassName.lastIndexOf('.'));
/* 54 */       for (i = 0; i < intfcs.length;)
/*    */       {
/* 56 */         intfcl = intfcs[i];
/* 57 */         String sin = ClassUtils.simpleClassName(intfcl);
/* 58 */         String sgenclass1 = "Filter" + sin;
/* 59 */         sgenclass2 = "SynchronizedFilter" + sin;
/*    */         
/* 61 */         w = null;
/*    */         try
/*    */         {
/* 64 */           w = new BufferedWriter(new FileWriter(sgenclass1 + ".java"));
/* 65 */           dg.setMethodModifiers(1);
/* 66 */           dg.writeDelegator(intfcl, pkg + '.' + sgenclass1, w);
/* 67 */           System.err.println(sgenclass1);
/*    */           
/*    */           try
/*    */           {
/* 71 */             if (w != null) w.close();
/*    */           } catch (Exception e) {
/* 73 */             e.printStackTrace();
/*    */           }
/*    */           
/*    */           try
/*    */           {
/* 78 */             w = new BufferedWriter(new FileWriter(sgenclass2 + ".java"));
/* 79 */             dg.setMethodModifiers(33);
/* 80 */             dg.writeDelegator(intfcl, pkg + '.' + sgenclass2, w);
/* 81 */             System.err.println(sgenclass2);
/*    */             
/*    */             try
/*    */             {
/* 85 */               if (w != null) w.close();
/*    */             } catch (Exception e) {
/* 87 */               e.printStackTrace();
/*    */             }
/* 54 */             i++;
/*    */ 
/*    */ 
/*    */ 
/*    */           }
/*    */           finally {}
/*    */ 
/*    */ 
/*    */         }
/*    */         finally
/*    */         {
/*    */ 
/*    */ 
/*    */           try
/*    */           {
/*    */ 
/*    */ 
/* 71 */             if (w != null) w.close();
/*    */           } catch (Exception e) {
/* 73 */             e.printStackTrace();
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */       DelegatorGenerator dg;
/*    */       
/*    */       String pkg;
/*    */       
/*    */       int i;
/*    */       
/*    */       Class intfcl;
/*    */       
/*    */       String sgenclass2;
/*    */       
/*    */       Writer w;
/*    */       
/* 92 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\RecreatePackage.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */