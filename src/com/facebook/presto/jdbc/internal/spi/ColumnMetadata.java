/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*     */ import java.util.Locale;
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
/*     */ public class ColumnMetadata
/*     */ {
/*     */   private final String name;
/*     */   private final Type type;
/*     */   private final String comment;
/*     */   private final boolean hidden;
/*     */   
/*     */   public ColumnMetadata(String name, Type type)
/*     */   {
/*  31 */     this(name, type, null, false);
/*     */   }
/*     */   
/*     */   public ColumnMetadata(String name, Type type, String comment, boolean hidden)
/*     */   {
/*  36 */     if ((name == null) || (name.isEmpty())) {
/*  37 */       throw new NullPointerException("name is null or empty");
/*     */     }
/*  39 */     if (type == null) {
/*  40 */       throw new NullPointerException("type is null");
/*     */     }
/*     */     
/*  43 */     this.name = name.toLowerCase(Locale.ENGLISH);
/*  44 */     this.type = type;
/*  45 */     this.comment = comment;
/*  46 */     this.hidden = hidden;
/*     */   }
/*     */   
/*     */   public String getName()
/*     */   {
/*  51 */     return this.name;
/*     */   }
/*     */   
/*     */   public Type getType()
/*     */   {
/*  56 */     return this.type;
/*     */   }
/*     */   
/*     */   public String getComment()
/*     */   {
/*  61 */     return this.comment;
/*     */   }
/*     */   
/*     */   public boolean isHidden()
/*     */   {
/*  66 */     return this.hidden;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  72 */     StringBuilder sb = new StringBuilder("ColumnMetadata{");
/*  73 */     sb.append("name='").append(this.name).append('\'');
/*  74 */     sb.append(", type=").append(this.type);
/*  75 */     if (this.comment != null) {
/*  76 */       sb.append(", comment='").append(this.comment).append('\'');
/*     */     }
/*  78 */     if (this.hidden) {
/*  79 */       sb.append(", hidden");
/*     */     }
/*  81 */     sb.append('}');
/*  82 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  88 */     return Objects.hash(new Object[] { this.name, this.type, this.comment, Boolean.valueOf(this.hidden) });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  94 */     if (this == obj) {
/*  95 */       return true;
/*     */     }
/*  97 */     if ((obj == null) || (getClass() != obj.getClass())) {
/*  98 */       return false;
/*     */     }
/* 100 */     ColumnMetadata other = (ColumnMetadata)obj;
/* 101 */     return (Objects.equals(this.name, other.name)) && 
/* 102 */       (Objects.equals(this.type, other.type)) && 
/* 103 */       (Objects.equals(this.comment, other.comment)) && 
/* 104 */       (Objects.equals(Boolean.valueOf(this.hidden), Boolean.valueOf(other.hidden)));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ColumnMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */