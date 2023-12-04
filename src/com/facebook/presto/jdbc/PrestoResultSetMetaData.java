/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
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
/*     */ public class PrestoResultSetMetaData
/*     */   implements ResultSetMetaData
/*     */ {
/*     */   private final List<ColumnInfo> columnInfo;
/*     */   
/*     */   PrestoResultSetMetaData(List<ColumnInfo> columnInfo)
/*     */   {
/*  38 */     this.columnInfo = ImmutableList.copyOf((Collection)Objects.requireNonNull(columnInfo, "columnInfo is null"));
/*     */   }
/*     */   
/*     */ 
/*     */   public int getColumnCount()
/*     */     throws SQLException
/*     */   {
/*  45 */     return this.columnInfo.size();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAutoIncrement(int column)
/*     */     throws SQLException
/*     */   {
/*  52 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCaseSensitive(int column)
/*     */     throws SQLException
/*     */   {
/*  59 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSearchable(int column)
/*     */     throws SQLException
/*     */   {
/*  66 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCurrency(int column)
/*     */     throws SQLException
/*     */   {
/*  73 */     return column(column).isCurrency();
/*     */   }
/*     */   
/*     */ 
/*     */   public int isNullable(int column)
/*     */     throws SQLException
/*     */   {
/*  80 */     ColumnInfo.Nullable nullable = column(column).getNullable();
/*  81 */     switch (nullable) {
/*     */     case NO_NULLS: 
/*  83 */       return 0;
/*     */     case NULLABLE: 
/*  85 */       return 1;
/*     */     case UNKNOWN: 
/*  87 */       return 2;
/*     */     }
/*  89 */     throw new SQLException("Unhandled nullable type: " + nullable);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSigned(int column)
/*     */     throws SQLException
/*     */   {
/*  96 */     return column(column).isSigned();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getColumnDisplaySize(int column)
/*     */     throws SQLException
/*     */   {
/* 103 */     return column(column).getColumnDisplaySize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getColumnLabel(int column)
/*     */     throws SQLException
/*     */   {
/* 110 */     return column(column).getColumnLabel();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getColumnName(int column)
/*     */     throws SQLException
/*     */   {
/* 117 */     return column(column).getColumnName();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPrecision(int column)
/*     */     throws SQLException
/*     */   {
/* 124 */     return column(column).getPrecision();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getScale(int column)
/*     */     throws SQLException
/*     */   {
/* 131 */     return column(column).getScale();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTableName(int column)
/*     */     throws SQLException
/*     */   {
/* 138 */     return column(column).getTableName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSchemaName(int column)
/*     */     throws SQLException
/*     */   {
/* 145 */     return column(column).getSchemaName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCatalogName(int column)
/*     */     throws SQLException
/*     */   {
/* 152 */     return column(column).getCatalogName();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getColumnType(int column)
/*     */     throws SQLException
/*     */   {
/* 159 */     return column(column).getColumnType();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getColumnTypeName(int column)
/*     */     throws SQLException
/*     */   {
/* 166 */     return column(column).getColumnTypeName();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReadOnly(int column)
/*     */     throws SQLException
/*     */   {
/* 173 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 180 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDefinitelyWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 187 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getColumnClassName(int column)
/*     */     throws SQLException
/*     */   {
/* 195 */     switch (column(column).getColumnType()) {
/*     */     case 2: 
/*     */     case 3: 
/* 198 */       return BigDecimal.class.getName();
/*     */     case -7: 
/*     */     case 16: 
/* 201 */       return Boolean.class.getName();
/*     */     case -6: 
/* 203 */       return Byte.class.getName();
/*     */     case 5: 
/* 205 */       return Short.class.getName();
/*     */     case 4: 
/* 207 */       return Integer.class.getName();
/*     */     case -5: 
/* 209 */       return Long.class.getName();
/*     */     case 7: 
/* 211 */       return Float.class.getName();
/*     */     case 6: 
/*     */     case 8: 
/* 214 */       return Double.class.getName();
/*     */     case -4: 
/*     */     case -3: 
/*     */     case -2: 
/* 218 */       return "byte[]";
/*     */     case 91: 
/* 220 */       return Date.class.getName();
/*     */     case 92: 
/* 222 */       return Time.class.getName();
/*     */     case 93: 
/* 224 */       return Timestamp.class.getName();
/*     */     case 2004: 
/* 226 */       return Blob.class.getName();
/*     */     case 2005: 
/* 228 */       return Clob.class.getName();
/*     */     }
/* 230 */     return String.class.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/* 238 */     if (isWrapperFor(iface)) {
/* 239 */       return this;
/*     */     }
/* 241 */     throw new SQLException("No wrapper for " + iface);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 248 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */   private ColumnInfo column(int column)
/*     */     throws SQLException
/*     */   {
/* 254 */     if ((column <= 0) || (column > this.columnInfo.size())) {
/* 255 */       throw new SQLException("Invalid column index: " + column);
/*     */     }
/* 257 */     return (ColumnInfo)this.columnInfo.get(column - 1);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoResultSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */