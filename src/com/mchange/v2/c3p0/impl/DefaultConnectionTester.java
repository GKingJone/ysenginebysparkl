/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v1.db.sql.ResultSetUtils;
/*     */ import com.mchange.v1.db.sql.StatementUtils;
/*     */ import com.mchange.v2.c3p0.AbstractConnectionTester;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class DefaultConnectionTester
/*     */   extends AbstractConnectionTester
/*     */ {
/*  36 */   static final MLogger logger = MLog.getLogger(DefaultConnectionTester.class);
/*     */   
/*  38 */   static final int HASH_CODE = DefaultConnectionTester.class.getName().hashCode();
/*     */   
/*     */   static final Set INVALID_DB_STATES;
/*     */   
/*     */   static
/*     */   {
/*  44 */     Set temp = new HashSet();
/*  45 */     temp.add("08001");
/*  46 */     temp.add("08007");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */     INVALID_DB_STATES = Collections.unmodifiableSet(temp);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int activeCheckConnection(Connection c, String query, Throwable[] rootCauseOutParamHolder)
/*     */   {
/*  61 */     if (query == null) {
/*  62 */       return activeCheckConnectionNoQuery(c, rootCauseOutParamHolder);
/*     */     }
/*     */     
/*  65 */     Statement stmt = null;
/*  66 */     ResultSet rs = null;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/*  72 */       stmt = c.createStatement();
/*  73 */       rs = stmt.executeQuery(query);
/*     */       
/*  75 */       return 0;
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/*  79 */       if (logger.isLoggable(MLevel.FINE)) {
/*  80 */         logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception! [query=" + query + "]", e);
/*     */       }
/*  82 */       if (rootCauseOutParamHolder != null) {
/*  83 */         rootCauseOutParamHolder[0] = e;
/*     */       }
/*  85 */       state = e.getSQLState();
/*  86 */       int j; if (INVALID_DB_STATES.contains(state))
/*     */       {
/*  88 */         if (logger.isLoggable(MLevel.WARNING)) {
/*  89 */           logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception which occurred during a Connection test (test with query '" + query + "') implies that the database is invalid, " + "and the pool should refill itself with fresh Connections.", e);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*  94 */         return -8;
/*     */       }
/*     */       
/*  97 */       return -1;
/*     */     }
/*     */     catch (Exception e) {
/*     */       String state;
/* 101 */       if (logger.isLoggable(MLevel.FINE)) {
/* 102 */         logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception!", e);
/*     */       }
/* 104 */       if (rootCauseOutParamHolder != null) {
/* 105 */         rootCauseOutParamHolder[0] = e;
/*     */       }
/* 107 */       return -1;
/*     */     }
/*     */     finally
/*     */     {
/* 111 */       ResultSetUtils.attemptClose(rs);
/* 112 */       StatementUtils.attemptClose(stmt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int statusOnException(Connection c, Throwable t, String query, Throwable[] rootCauseOutParamHolder)
/*     */   {
/* 125 */     if (logger.isLoggable(MLevel.FINER)) {
/* 126 */       logger.log(MLevel.FINER, "Testing a Connection in response to an Exception:", t);
/*     */     }
/*     */     try
/*     */     {
/* 130 */       if ((t instanceof SQLException))
/*     */       {
/* 132 */         state = ((SQLException)t).getSQLState();
/* 133 */         if (INVALID_DB_STATES.contains(state))
/*     */         {
/* 135 */           if (logger.isLoggable(MLevel.WARNING)) {
/* 136 */             logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception tested by statusOnException() implies that the database is invalid, " + "and the pool should refill itself with fresh Connections.", t);
/*     */           }
/*     */           
/*     */ 
/* 140 */           i = -8;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */           return i;
/*     */         }
/* 143 */         i = activeCheckConnection(c, query, rootCauseOutParamHolder);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */         return i;
/*     */       }
/* 147 */       if (logger.isLoggable(MLevel.FINE))
/* 148 */         logger.log(MLevel.FINE, "Connection test failed because test-provoking Throwable is an unexpected, non-SQLException.", t);
/* 149 */       if (rootCauseOutParamHolder != null)
/* 150 */         rootCauseOutParamHolder[0] = t;
/* 151 */       String state = -1;
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
/* 171 */       return state;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 154 */       e = 
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
/* 171 */         e;
/* 156 */       if (logger.isLoggable(MLevel.FINE)) {
/* 157 */         logger.log(MLevel.FINE, "Connection " + c + " failed Connection test with an Exception!", e);
/*     */       }
/* 159 */       if (rootCauseOutParamHolder != null) {
/* 160 */         rootCauseOutParamHolder[0] = e;
/*     */       }
/* 162 */       int i = -1;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */       return i;
/*     */     } finally {}
/*     */   }
/*     */   
/* 175 */   private static String queryInfo(String query) { return "[query=" + query + "]"; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int activeCheckConnectionNoQuery(Connection c, Throwable[] rootCauseOutParamHolder)
/*     */   {
/* 182 */     ResultSet rs = null;
/*     */     try
/*     */     {
/* 185 */       rs = c.getMetaData().getTables(null, null, "PROBABLYNOT", new String[] { "TABLE" });
/*     */       
/*     */ 
/*     */ 
/* 189 */       return 0;
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 193 */       if (logger.isLoggable(MLevel.FINE)) {
/* 194 */         logger.log(MLevel.FINE, "Connection " + c + " failed default system-table Connection test with an Exception!", e);
/*     */       }
/* 196 */       if (rootCauseOutParamHolder != null) {
/* 197 */         rootCauseOutParamHolder[0] = e;
/*     */       }
/* 199 */       state = e.getSQLState();
/* 200 */       int j; if (INVALID_DB_STATES.contains(state))
/*     */       {
/* 202 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 203 */           logger.log(MLevel.WARNING, "SQL State '" + state + "' of Exception which occurred during a Connection test (fallback DatabaseMetaData test) implies that the database is invalid, " + "and the pool should refill itself with fresh Connections.", e);
/*     */         }
/*     */         
/*     */ 
/* 207 */         return -8;
/*     */       }
/*     */       
/* 210 */       return -1;
/*     */     }
/*     */     catch (Exception e) {
/*     */       String state;
/* 214 */       if (logger.isLoggable(MLevel.FINE)) {
/* 215 */         logger.log(MLevel.FINE, "Connection " + c + " failed default system-table Connection test with an Exception!", e);
/*     */       }
/* 217 */       if (rootCauseOutParamHolder != null) {
/* 218 */         rootCauseOutParamHolder[0] = e;
/*     */       }
/* 220 */       return -1;
/*     */     }
/*     */     finally
/*     */     {
/* 224 */       ResultSetUtils.attemptClose(rs);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 232 */     return (o != null) && (o.getClass() == DefaultConnectionTester.class);
/*     */   }
/*     */   
/* 235 */   public int hashCode() { return HASH_CODE; }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\DefaultConnectionTester.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */