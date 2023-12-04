/*    */ package com.mchange.v2.ser;
/*    */ 
/*    */ import java.io.InvalidClassException;
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
/*    */ public class UnsupportedVersionException
/*    */   extends InvalidClassException
/*    */ {
/*    */   public UnsupportedVersionException(String message)
/*    */   {
/* 31 */     super(message);
/*    */   }
/*    */   
/* 34 */   public UnsupportedVersionException(Object obj, int version) { this(obj.getClass().getName() + " -- unsupported version: " + version); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\ser\UnsupportedVersionException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */