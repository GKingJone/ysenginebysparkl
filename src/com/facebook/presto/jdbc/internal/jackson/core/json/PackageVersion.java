/*    */ package com.facebook.presto.jdbc.internal.jackson.core.json;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Versioned;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.util.VersionUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PackageVersion
/*    */   implements Versioned
/*    */ {
/* 13 */   public static final Version VERSION = VersionUtil.parseVersion("2.8.1", "com.facebook.presto.jdbc.internal.jackson.core", "jackson-core");
/*    */   
/*    */ 
/*    */   public Version version()
/*    */   {
/* 18 */     return VERSION;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\json\PackageVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */