/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public class FileBodyGenerator
/*    */   implements BodyGenerator
/*    */ {
/*    */   private final Path path;
/*    */   
/*    */   public FileBodyGenerator(Path path)
/*    */   {
/* 19 */     this.path = ((Path)Objects.requireNonNull(path, "path is null"));
/*    */   }
/*    */   
/*    */   public Path getPath()
/*    */   {
/* 24 */     return this.path;
/*    */   }
/*    */   
/*    */ 
/*    */   public void write(OutputStream out)
/*    */     throws Exception
/*    */   {
/* 31 */     Files.copy(this.path, out);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\FileBodyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */