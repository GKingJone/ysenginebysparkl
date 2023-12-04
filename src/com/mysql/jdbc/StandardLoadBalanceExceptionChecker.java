/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StandardLoadBalanceExceptionChecker
/*     */   implements LoadBalanceExceptionChecker
/*     */ {
/*     */   private List<String> sqlStateList;
/*     */   private List<Class<?>> sqlExClassList;
/*     */   
/*     */   public boolean shouldExceptionTriggerFailover(SQLException ex)
/*     */   {
/*  38 */     String sqlState = ex.getSQLState();
/*     */     Iterator<String> i;
/*  40 */     if (sqlState != null) {
/*  41 */       if (sqlState.startsWith("08"))
/*     */       {
/*  43 */         return true;
/*     */       }
/*  45 */       if (this.sqlStateList != null)
/*     */       {
/*  47 */         for (i = this.sqlStateList.iterator(); i.hasNext();) {
/*  48 */           if (sqlState.startsWith(((String)i.next()).toString())) {
/*  49 */             return true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  56 */     if ((ex instanceof CommunicationsException)) {
/*  57 */       return true;
/*     */     }
/*     */     Iterator<Class<?>> i;
/*  60 */     if (this.sqlExClassList != null)
/*     */     {
/*  62 */       for (i = this.sqlExClassList.iterator(); i.hasNext();) {
/*  63 */         if (((Class)i.next()).isInstance(ex)) {
/*  64 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  69 */     return false;
/*     */   }
/*     */   
/*     */   public void destroy() {}
/*     */   
/*     */   public void init(Connection conn, Properties props) throws SQLException
/*     */   {
/*  76 */     configureSQLStateList(props.getProperty("loadBalanceSQLStateFailover", null));
/*  77 */     configureSQLExceptionSubclassList(props.getProperty("loadBalanceSQLExceptionSubclassFailover", null));
/*     */   }
/*     */   
/*     */   private void configureSQLStateList(String sqlStates) {
/*  81 */     if ((sqlStates == null) || ("".equals(sqlStates))) {
/*  82 */       return;
/*     */     }
/*  84 */     List<String> states = StringUtils.split(sqlStates, ",", true);
/*  85 */     List<String> newStates = new ArrayList();
/*     */     
/*  87 */     for (String state : states) {
/*  88 */       if (state.length() > 0) {
/*  89 */         newStates.add(state);
/*     */       }
/*     */     }
/*  92 */     if (newStates.size() > 0) {
/*  93 */       this.sqlStateList = newStates;
/*     */     }
/*     */   }
/*     */   
/*     */   private void configureSQLExceptionSubclassList(String sqlExClasses) {
/*  98 */     if ((sqlExClasses == null) || ("".equals(sqlExClasses))) {
/*  99 */       return;
/*     */     }
/* 101 */     List<String> classes = StringUtils.split(sqlExClasses, ",", true);
/* 102 */     List<Class<?>> newClasses = new ArrayList();
/*     */     
/* 104 */     for (String exClass : classes) {
/*     */       try {
/* 106 */         Class<?> c = Class.forName(exClass);
/* 107 */         newClasses.add(c);
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/* 112 */     if (newClasses.size() > 0) {
/* 113 */       this.sqlExClassList = newClasses;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\StandardLoadBalanceExceptionChecker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */