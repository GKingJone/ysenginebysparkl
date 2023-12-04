/*    */ package com.facebook.presto.jdbc.internal.jol.info;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GraphPathRecord
/*    */ {
/*    */   private final String path;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final Object obj;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int depth;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public GraphPathRecord(String path, Object obj, int depth)
/*    */   {
/* 38 */     this.path = path;
/* 39 */     this.obj = obj;
/* 40 */     this.depth = depth;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 45 */     if (this == o) return true;
/* 46 */     if ((o == null) || (getClass() != o.getClass())) { return false;
/*    */     }
/* 48 */     GraphPathRecord that = (GraphPathRecord)o;
/*    */     
/* 50 */     if (!this.obj.equals(that.obj)) { return false;
/*    */     }
/* 52 */     return true;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 57 */     return this.obj.hashCode();
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 62 */     return "GraphPathRecord{path='" + this.path + '\'' + ", obj=" + this.obj + '}';
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String path()
/*    */   {
/* 69 */     return this.path;
/*    */   }
/*    */   
/*    */   public Object obj() {
/* 73 */     return this.obj;
/*    */   }
/*    */   
/*    */   public int depth() {
/* 77 */     return this.depth;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\info\GraphPathRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */