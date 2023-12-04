/*    */ package com.facebook.presto.jdbc.internal.jol.datamodel;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jol.util.VMSupport;
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
/*    */ public class CurrentDataModel
/*    */   implements DataModel
/*    */ {
/*    */   public int headerSize()
/*    */   {
/* 37 */     return VMSupport.OBJ_HEADER_SIZE;
/*    */   }
/*    */   
/*    */   public int sizeOf(String klass)
/*    */   {
/* 42 */     if (klass.equals("byte")) return VMSupport.BYTE_SIZE;
/* 43 */     if (klass.equals("boolean")) return VMSupport.BOOLEAN_SIZE;
/* 44 */     if (klass.equals("short")) return VMSupport.SHORT_SIZE;
/* 45 */     if (klass.equals("char")) return VMSupport.CHAR_SIZE;
/* 46 */     if (klass.equals("int")) return VMSupport.INT_SIZE;
/* 47 */     if (klass.equals("float")) return VMSupport.FLOAT_SIZE;
/* 48 */     if (klass.equals("long")) return VMSupport.LONG_SIZE;
/* 49 */     if (klass.equals("double")) return VMSupport.DOUBLE_SIZE;
/* 50 */     return VMSupport.REF_SIZE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\datamodel\CurrentDataModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */