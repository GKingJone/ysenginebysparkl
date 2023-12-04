/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.BlockBuilder;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ public class RecordPageSource
/*     */   implements ConnectorPageSource
/*     */ {
/*     */   private static final int ROWS_PER_REQUEST = 4096;
/*     */   private final RecordCursor cursor;
/*     */   private final List<Type> types;
/*     */   private final PageBuilder pageBuilder;
/*     */   private boolean closed;
/*     */   
/*     */   public RecordPageSource(RecordSet recordSet)
/*     */   {
/*  37 */     this(((RecordSet)Objects.requireNonNull(recordSet, "recordSet is null")).getColumnTypes(), recordSet.cursor());
/*     */   }
/*     */   
/*     */   public RecordPageSource(List<Type> types, RecordCursor cursor)
/*     */   {
/*  42 */     this.cursor = ((RecordCursor)Objects.requireNonNull(cursor, "cursor is null"));
/*  43 */     this.types = Collections.unmodifiableList(new ArrayList((Collection)Objects.requireNonNull(types, "types is null")));
/*  44 */     this.pageBuilder = new PageBuilder(this.types);
/*     */   }
/*     */   
/*     */   public RecordCursor getCursor()
/*     */   {
/*  49 */     return this.cursor;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytes()
/*     */   {
/*  55 */     return this.cursor.getTotalBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedBytes()
/*     */   {
/*  61 */     return this.cursor.getCompletedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getReadTimeNanos()
/*     */   {
/*  67 */     return this.cursor.getReadTimeNanos();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSystemMemoryUsage()
/*     */   {
/*  73 */     return this.cursor.getSystemMemoryUsage() + this.pageBuilder.getSizeInBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/*  79 */     this.closed = true;
/*  80 */     this.cursor.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinished()
/*     */   {
/*  86 */     return (this.closed) && (this.pageBuilder.isEmpty());
/*     */   }
/*     */   
/*     */ 
/*     */   public Page getNextPage()
/*     */   {
/*  92 */     if (!this.closed)
/*     */     {
/*  94 */       for (int i = 0; i < 4096; i++) {
/*  95 */         if (this.pageBuilder.isFull()) {
/*     */           break;
/*     */         }
/*     */         
/*  99 */         if (!this.cursor.advanceNextPosition()) {
/* 100 */           this.closed = true;
/* 101 */           break;
/*     */         }
/*     */         
/* 104 */         this.pageBuilder.declarePosition();
/* 105 */         for (int column = 0; column < this.types.size(); column++) {
/* 106 */           BlockBuilder output = this.pageBuilder.getBlockBuilder(column);
/* 107 */           if (this.cursor.isNull(column)) {
/* 108 */             output.appendNull();
/*     */           }
/*     */           else {
/* 111 */             Type type = (Type)this.types.get(column);
/* 112 */             Class<?> javaType = type.getJavaType();
/* 113 */             if (javaType == Boolean.TYPE) {
/* 114 */               type.writeBoolean(output, this.cursor.getBoolean(column));
/*     */             }
/* 116 */             else if (javaType == Long.TYPE) {
/* 117 */               type.writeLong(output, this.cursor.getLong(column));
/*     */             }
/* 119 */             else if (javaType == Double.TYPE) {
/* 120 */               type.writeDouble(output, this.cursor.getDouble(column));
/*     */             }
/* 122 */             else if (javaType == Slice.class) {
/* 123 */               Slice slice = this.cursor.getSlice(column);
/* 124 */               type.writeSlice(output, slice, 0, slice.length());
/*     */             }
/*     */             else {
/* 127 */               type.writeObject(output, this.cursor.getObject(column));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 135 */     if ((this.pageBuilder.isEmpty()) || ((!this.closed) && (!this.pageBuilder.isFull()))) {
/* 136 */       return null;
/*     */     }
/*     */     
/* 139 */     Page page = this.pageBuilder.build();
/* 140 */     this.pageBuilder.reset();
/*     */     
/* 142 */     return page;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\RecordPageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */