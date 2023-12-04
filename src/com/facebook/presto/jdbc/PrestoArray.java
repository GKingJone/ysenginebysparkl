/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.primitives.Ints;
/*     */ import java.sql.Array;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class PrestoArray
/*     */   implements Array
/*     */ {
/*     */   private final String elementTypeName;
/*     */   private final int elementType;
/*     */   private final Object[] array;
/*     */   
/*     */   PrestoArray(String elementTypeName, int elementType, List<?> array)
/*     */   {
/*  37 */     this.elementTypeName = ((String)Objects.requireNonNull(elementTypeName, "elementType is null"));
/*  38 */     this.elementType = elementType;
/*  39 */     this.array = ((List)Objects.requireNonNull(array, "array is null")).toArray();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getBaseTypeName()
/*     */   {
/*  45 */     return this.elementTypeName;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBaseType()
/*     */   {
/*  51 */     return this.elementType;
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getArray()
/*     */   {
/*  57 */     return this.array.clone();
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getArray(Map<String, Class<?>> map)
/*     */     throws SQLException
/*     */   {
/*  64 */     throw new SQLFeatureNotSupportedException("getArray not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getArray(long index, int count)
/*     */     throws SQLException
/*     */   {
/*  71 */     int arrayOffset = Ints.saturatedCast(index - 1L);
/*  72 */     if ((index < 1L) || (count < 0) || (arrayOffset + count > this.array.length)) {
/*  73 */       throw new SQLException("Index out of bounds");
/*     */     }
/*  75 */     return Arrays.copyOfRange(this.array, arrayOffset, arrayOffset + count);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object getArray(long index, int count, Map<String, Class<?>> map)
/*     */     throws SQLException
/*     */   {
/*  82 */     throw new SQLFeatureNotSupportedException("getArray not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSet getResultSet()
/*     */     throws SQLException
/*     */   {
/*  89 */     throw new SQLFeatureNotSupportedException("getResultSet not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSet getResultSet(Map<String, Class<?>> map)
/*     */     throws SQLException
/*     */   {
/*  96 */     throw new SQLFeatureNotSupportedException("getResultSet not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSet getResultSet(long index, int count)
/*     */     throws SQLException
/*     */   {
/* 103 */     throw new SQLFeatureNotSupportedException("getResultSet not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map)
/*     */     throws SQLException
/*     */   {
/* 110 */     throw new SQLFeatureNotSupportedException("getResultSet not supported");
/*     */   }
/*     */   
/*     */   public void free() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */