/*     */ package com.facebook.presto.jdbc.internal.jol.info;
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
/*     */ public class FieldLayout
/*     */   implements Comparable<FieldLayout>
/*     */ {
/*     */   private final FieldData f;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int size;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int offset;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FieldLayout(FieldData fieldData, int offset, int size)
/*     */   {
/*  45 */     this.f = fieldData;
/*  46 */     this.size = size;
/*  47 */     this.offset = offset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int offset()
/*     */   {
/*  56 */     return this.offset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/*  65 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String typeClass()
/*     */   {
/*  74 */     return this.f.typeClass();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String hostClass()
/*     */   {
/*  83 */     return this.f.hostClass();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String name()
/*     */   {
/*  92 */     return this.f.name();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String safeValue(Object object)
/*     */   {
/* 103 */     return this.f.safeValue(object);
/*     */   }
/*     */   
/*     */   public boolean equals(Object o)
/*     */   {
/* 108 */     if (this == o) return true;
/* 109 */     if ((o == null) || (getClass() != o.getClass())) { return false;
/*     */     }
/* 111 */     FieldLayout that = (FieldLayout)o;
/*     */     
/* 113 */     if (this.offset != that.offset) { return false;
/*     */     }
/* 115 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 120 */     return this.offset;
/*     */   }
/*     */   
/*     */   public int compareTo(FieldLayout o)
/*     */   {
/* 125 */     return Integer.valueOf(this.offset).compareTo(Integer.valueOf(o.offset));
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 130 */     return this.f.hostClass() + "." + this.f.name() + "@" + this.offset + "(" + this.size + ")";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\FieldLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */