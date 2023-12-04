/*     */ package com.mchange.v2.c3p0.stmt;
/*     */ 
/*     */ import com.mchange.v1.util.ArrayUtils;
/*     */ import com.mchange.v2.lang.ObjectUtils;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.util.Arrays;
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
/*     */ abstract class StatementCacheKey
/*     */ {
/*     */   static final int SIMPLE = 0;
/*     */   static final int MEMORY_COALESCED = 1;
/*     */   static final int VALUE_IDENTITY = 2;
/*     */   Connection physicalConnection;
/*     */   String stmtText;
/*     */   boolean is_callable;
/*     */   int result_set_type;
/*     */   int result_set_concurrency;
/*     */   int[] columnIndexes;
/*     */   String[] columnNames;
/*     */   Integer autogeneratedKeys;
/*     */   Integer resultSetHoldability;
/*     */   
/*     */   public static synchronized StatementCacheKey find(Connection pcon, Method stmtProducingMethod, Object[] args)
/*     */   {
/*  41 */     switch (2)
/*     */     {
/*     */     case 0: 
/*  44 */       return SimpleStatementCacheKey._find(pcon, stmtProducingMethod, args);
/*     */     case 1: 
/*  46 */       return MemoryCoalescedStatementCacheKey._find(pcon, stmtProducingMethod, args);
/*     */     case 2: 
/*  48 */       return ValueIdentityStatementCacheKey._find(pcon, stmtProducingMethod, args);
/*     */     }
/*  50 */     throw new InternalError("StatementCacheKey.find() is misconfigured.");
/*     */   }
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
/*     */   StatementCacheKey() {}
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
/*     */   StatementCacheKey(Connection physicalConnection, String stmtText, boolean is_callable, int result_set_type, int result_set_concurrency, int[] columnIndexes, String[] columnNames, Integer autogeneratedKeys, Integer resultSetHoldability)
/*     */   {
/*  86 */     init(physicalConnection, stmtText, is_callable, result_set_type, result_set_concurrency, columnIndexes, columnNames, autogeneratedKeys, resultSetHoldability);
/*     */   }
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
/*     */   void init(Connection physicalConnection, String stmtText, boolean is_callable, int result_set_type, int result_set_concurrency, int[] columnIndexes, String[] columnNames, Integer autogeneratedKeys, Integer resultSetHoldability)
/*     */   {
/* 108 */     this.physicalConnection = physicalConnection;
/* 109 */     this.stmtText = stmtText;
/* 110 */     this.is_callable = is_callable;
/* 111 */     this.result_set_type = result_set_type;
/* 112 */     this.result_set_concurrency = result_set_concurrency;
/* 113 */     this.columnIndexes = columnIndexes;
/* 114 */     this.columnNames = columnNames;
/* 115 */     this.autogeneratedKeys = autogeneratedKeys;
/* 116 */     this.resultSetHoldability = resultSetHoldability;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static boolean equals(StatementCacheKey _this, Object o)
/*     */   {
/* 123 */     if (_this == o)
/* 124 */       return true;
/* 125 */     if ((o instanceof StatementCacheKey))
/*     */     {
/* 127 */       StatementCacheKey sck = (StatementCacheKey)o;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 133 */       return (sck.physicalConnection.equals(_this.physicalConnection)) && (sck.stmtText.equals(_this.stmtText)) && (sck.is_callable == _this.is_callable) && (sck.result_set_type == _this.result_set_type) && (sck.result_set_concurrency == _this.result_set_concurrency) && (Arrays.equals(sck.columnIndexes, _this.columnIndexes)) && (Arrays.equals(sck.columnNames, _this.columnNames)) && (ObjectUtils.eqOrBothNull(sck.autogeneratedKeys, _this.autogeneratedKeys)) && (ObjectUtils.eqOrBothNull(sck.resultSetHoldability, _this.resultSetHoldability));
/*     */     }
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
/* 145 */     return false;
/*     */   }
/*     */   
/*     */   static int hashCode(StatementCacheKey _this)
/*     */   {
/* 150 */     return _this.physicalConnection.hashCode() ^ _this.stmtText.hashCode() ^ (_this.is_callable ? 1 : 0) ^ _this.result_set_type ^ _this.result_set_concurrency ^ ArrayUtils.hashOrZeroArray(_this.columnIndexes) ^ ArrayUtils.hashOrZeroArray(_this.columnNames) ^ ObjectUtils.hashOrZero(_this.autogeneratedKeys) ^ ObjectUtils.hashOrZero(_this.resultSetHoldability);
/*     */   }
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
/*     */   public String toString()
/*     */   {
/* 164 */     StringBuffer out = new StringBuffer(128);
/* 165 */     out.append("[" + getClass().getName() + ": ");
/* 166 */     out.append("physicalConnection->" + this.physicalConnection);
/* 167 */     out.append(", stmtText->" + this.stmtText);
/* 168 */     out.append(", is_callable->" + this.is_callable);
/* 169 */     out.append(", result_set_type->" + this.result_set_type);
/* 170 */     out.append(", result_set_concurrency->" + this.result_set_concurrency);
/* 171 */     out.append(", columnIndexes->" + ArrayUtils.toString(this.columnIndexes));
/* 172 */     out.append(", columnNames->" + ArrayUtils.toString(this.columnNames));
/* 173 */     out.append(", autogeneratedKeys->" + this.autogeneratedKeys);
/* 174 */     out.append(", resultSetHoldability->" + this.resultSetHoldability);
/* 175 */     out.append(']');
/* 176 */     return out.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\StatementCacheKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */