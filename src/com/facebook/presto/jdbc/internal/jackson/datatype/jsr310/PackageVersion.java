/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Versioned;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.util.VersionUtil;
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
/*    */ public final class PackageVersion
/*    */   implements Versioned
/*    */ {
/* 30 */   public static final Version VERSION = VersionUtil.parseVersion("2.8.1", "com.facebook.presto.jdbc.internal.jackson.datatype", "jackson-datatype-jsr310");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Version version()
/*    */   {
/* 37 */     return VERSION;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\PackageVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */