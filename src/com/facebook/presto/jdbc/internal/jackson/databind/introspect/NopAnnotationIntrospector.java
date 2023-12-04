/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.Version;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion;
/*    */ import java.io.Serializable;
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
/*    */ public abstract class NopAnnotationIntrospector
/*    */   extends AnnotationIntrospector
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 23 */   public static final NopAnnotationIntrospector instance = new NopAnnotationIntrospector()
/*    */   {
/*    */     private static final long serialVersionUID = 1L;
/*    */     
/*    */     public Version version() {
/* 28 */       return PackageVersion.VERSION;
/*    */     }
/*    */   };
/*    */   
/*    */   public Version version()
/*    */   {
/* 34 */     return Version.unknownVersion();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\NopAnnotationIntrospector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */