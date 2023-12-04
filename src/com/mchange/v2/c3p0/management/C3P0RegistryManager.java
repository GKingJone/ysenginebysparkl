/*    */ package com.mchange.v2.c3p0.management;
/*    */ 
/*    */ import com.mchange.v2.c3p0.C3P0Registry;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
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
/*    */ public class C3P0RegistryManager
/*    */   implements C3P0RegistryManagerMBean
/*    */ {
/*    */   public String[] getAllIdentityTokens()
/*    */   {
/* 35 */     Set tokens = C3P0Registry.allIdentityTokens();
/* 36 */     return (String[])tokens.toArray(new String[tokens.size()]);
/*    */   }
/*    */   
/*    */   public Set getAllIdentityTokenized() {
/* 40 */     return C3P0Registry.allIdentityTokenized();
/*    */   }
/*    */   
/* 43 */   public Set getAllPooledDataSources() { return C3P0Registry.allPooledDataSources(); }
/*    */   
/*    */   public int getAllIdentityTokenCount() {
/* 46 */     return C3P0Registry.allIdentityTokens().size();
/*    */   }
/*    */   
/* 49 */   public int getAllIdentityTokenizedCount() { return C3P0Registry.allIdentityTokenized().size(); }
/*    */   
/*    */   public int getAllPooledDataSourcesCount() {
/* 52 */     return C3P0Registry.allPooledDataSources().size();
/*    */   }
/*    */   
/* 55 */   public String[] getAllIdentityTokenizedStringified() { return stringifySet(C3P0Registry.allIdentityTokenized()); }
/*    */   
/*    */   public String[] getAllPooledDataSourcesStringified() {
/* 58 */     return stringifySet(C3P0Registry.allPooledDataSources());
/*    */   }
/*    */   
/* 61 */   public int getNumPooledDataSources() throws SQLException { return C3P0Registry.getNumPooledDataSources(); }
/*    */   
/*    */   public int getNumPoolsAllDataSources() throws SQLException {
/* 64 */     return C3P0Registry.getNumPoolsAllDataSources();
/*    */   }
/*    */   
/* 67 */   public String getC3p0Version() { return "0.9.1.2"; }
/*    */   
/*    */   private String[] stringifySet(Set s)
/*    */   {
/* 71 */     String[] out = new String[s.size()];
/* 72 */     int i = 0;
/* 73 */     for (Iterator ii = s.iterator(); ii.hasNext();)
/* 74 */       out[(i++)] = ii.next().toString();
/* 75 */     return out;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\C3P0RegistryManager.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */