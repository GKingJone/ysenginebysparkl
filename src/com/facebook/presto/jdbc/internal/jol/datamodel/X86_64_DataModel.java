/*    */ package com.facebook.presto.jdbc.internal.jol.datamodel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X86_64_DataModel
/*    */   implements DataModel
/*    */ {
/*    */   public int headerSize()
/*    */   {
/* 36 */     return 16;
/*    */   }
/*    */   
/*    */   public int sizeOf(String klass)
/*    */   {
/* 41 */     if (klass.equals("byte")) return 1;
/* 42 */     if (klass.equals("boolean")) return 1;
/* 43 */     if (klass.equals("short")) return 2;
/* 44 */     if (klass.equals("char")) return 2;
/* 45 */     if (klass.equals("int")) return 4;
/* 46 */     if (klass.equals("float")) return 4;
/* 47 */     if (klass.equals("long")) return 8;
/* 48 */     if (klass.equals("double")) return 8;
/* 49 */     return 8;
/*    */   }
/*    */   
/*    */   public String toString()
/*    */   {
/* 54 */     return "X64 model";
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\datamodel\X86_64_DataModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */