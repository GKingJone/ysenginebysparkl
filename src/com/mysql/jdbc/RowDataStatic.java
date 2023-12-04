/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ public class RowDataStatic
/*     */   implements RowData
/*     */ {
/*     */   private Field[] metadata;
/*     */   private int index;
/*     */   ResultSetImpl owner;
/*     */   private List<ResultSetRow> rows;
/*     */   
/*     */   public RowDataStatic(List<ResultSetRow> rows)
/*     */   {
/*  47 */     this.index = -1;
/*  48 */     this.rows = rows;
/*     */   }
/*     */   
/*     */   public void addRow(ResultSetRow row) {
/*  52 */     this.rows.add(row);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void afterLast()
/*     */   {
/*  59 */     if (this.rows.size() > 0) {
/*  60 */       this.index = this.rows.size();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void beforeFirst()
/*     */   {
/*  68 */     if (this.rows.size() > 0) {
/*  69 */       this.index = -1;
/*     */     }
/*     */   }
/*     */   
/*     */   public void beforeLast() {
/*  74 */     if (this.rows.size() > 0) {
/*  75 */       this.index = (this.rows.size() - 2);
/*     */     }
/*     */   }
/*     */   
/*     */   public void close() {}
/*     */   
/*     */   public ResultSetRow getAt(int atIndex) throws SQLException
/*     */   {
/*  83 */     if ((atIndex < 0) || (atIndex >= this.rows.size())) {
/*  84 */       return null;
/*     */     }
/*     */     
/*  87 */     return ((ResultSetRow)this.rows.get(atIndex)).setMetadata(this.metadata);
/*     */   }
/*     */   
/*     */   public int getCurrentRowNumber() {
/*  91 */     return this.index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResultSetInternalMethods getOwner()
/*     */   {
/*  98 */     return this.owner;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/* 102 */     boolean hasMore = this.index + 1 < this.rows.size();
/*     */     
/* 104 */     return hasMore;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAfterLast()
/*     */   {
/* 111 */     return (this.index >= this.rows.size()) && (this.rows.size() != 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */   {
/* 118 */     return (this.index == -1) && (this.rows.size() != 0);
/*     */   }
/*     */   
/*     */   public boolean isDynamic() {
/* 122 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 126 */     return this.rows.size() == 0;
/*     */   }
/*     */   
/*     */   public boolean isFirst() {
/* 130 */     return this.index == 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isLast()
/*     */   {
/* 137 */     if (this.rows.size() == 0) {
/* 138 */       return false;
/*     */     }
/*     */     
/* 141 */     return this.index == this.rows.size() - 1;
/*     */   }
/*     */   
/*     */   public void moveRowRelative(int rowsToMove) {
/* 145 */     if (this.rows.size() > 0) {
/* 146 */       this.index += rowsToMove;
/* 147 */       if (this.index < -1) {
/* 148 */         beforeFirst();
/* 149 */       } else if (this.index > this.rows.size()) {
/* 150 */         afterLast();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public ResultSetRow next() throws SQLException {
/* 156 */     this.index += 1;
/*     */     
/* 158 */     if (this.index > this.rows.size()) {
/* 159 */       afterLast();
/* 160 */     } else if (this.index < this.rows.size()) {
/* 161 */       ResultSetRow row = (ResultSetRow)this.rows.get(this.index);
/*     */       
/* 163 */       return row.setMetadata(this.metadata);
/*     */     }
/*     */     
/* 166 */     return null;
/*     */   }
/*     */   
/*     */   public void removeRow(int atIndex) {
/* 170 */     this.rows.remove(atIndex);
/*     */   }
/*     */   
/*     */   public void setCurrentRow(int newIndex) {
/* 174 */     this.index = newIndex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOwner(ResultSetImpl rs)
/*     */   {
/* 181 */     this.owner = rs;
/*     */   }
/*     */   
/*     */   public int size() {
/* 185 */     return this.rows.size();
/*     */   }
/*     */   
/*     */   public boolean wasEmpty() {
/* 189 */     return (this.rows != null) && (this.rows.size() == 0);
/*     */   }
/*     */   
/*     */   public void setMetadata(Field[] metadata) {
/* 193 */     this.metadata = metadata;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\RowDataStatic.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */