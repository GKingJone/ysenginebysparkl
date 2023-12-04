/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.Statement;
/*     */ import java.util.Properties;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VersionFSHierarchyMaker
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  39 */     if (args.length < 3) {
/*  40 */       usage();
/*  41 */       System.exit(1);
/*     */     }
/*     */     
/*  44 */     String jdbcUrl = null;
/*     */     
/*  46 */     String jvmVersion = removeWhitespaceChars(System.getProperty("java.version"));
/*  47 */     String jvmVendor = removeWhitespaceChars(System.getProperty("java.vendor"));
/*  48 */     String osName = removeWhitespaceChars(System.getProperty("os.name"));
/*  49 */     String osArch = removeWhitespaceChars(System.getProperty("os.arch"));
/*  50 */     String osVersion = removeWhitespaceChars(System.getProperty("os.version"));
/*     */     
/*  52 */     jdbcUrl = System.getProperty("com.mysql.jdbc.testsuite.url");
/*     */     
/*  54 */     String mysqlVersion = "MySQL" + args[2] + "_";
/*     */     try
/*     */     {
/*  57 */       Properties props = new Properties();
/*  58 */       props.setProperty("allowPublicKeyRetrieval", "true");
/*  59 */       Connection conn = new NonRegisteringDriver().connect(jdbcUrl, props);
/*     */       
/*  61 */       ResultSet rs = conn.createStatement().executeQuery("SELECT VERSION()");
/*  62 */       rs.next();
/*  63 */       mysqlVersion = mysqlVersion + removeWhitespaceChars(rs.getString(1));
/*     */     } catch (Throwable t) {
/*  65 */       mysqlVersion = mysqlVersion + "no-server-running-on-" + removeWhitespaceChars(jdbcUrl);
/*     */     }
/*     */     
/*  68 */     String jvmSubdirName = jvmVendor + "-" + jvmVersion;
/*  69 */     String osSubdirName = osName + "-" + osArch + "-" + osVersion;
/*     */     
/*  71 */     File baseDir = new File(args[0]);
/*  72 */     File mysqlVersionDir = new File(baseDir, mysqlVersion);
/*  73 */     File osVersionDir = new File(mysqlVersionDir, osSubdirName);
/*  74 */     File jvmVersionDir = new File(osVersionDir, jvmSubdirName);
/*     */     
/*  76 */     jvmVersionDir.mkdirs();
/*     */     
/*  78 */     FileOutputStream pathOut = null;
/*     */     try
/*     */     {
/*  81 */       String propsOutputPath = args[1];
/*  82 */       pathOut = new FileOutputStream(propsOutputPath);
/*  83 */       String baseDirStr = baseDir.getAbsolutePath();
/*  84 */       String jvmVersionDirStr = jvmVersionDir.getAbsolutePath();
/*     */       
/*  86 */       if (jvmVersionDirStr.startsWith(baseDirStr)) {
/*  87 */         jvmVersionDirStr = jvmVersionDirStr.substring(baseDirStr.length() + 1);
/*     */       }
/*     */       
/*  90 */       pathOut.write(jvmVersionDirStr.getBytes());
/*     */     } finally {
/*  92 */       if (pathOut != null) {
/*  93 */         pathOut.flush();
/*  94 */         pathOut.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static String removeWhitespaceChars(String input) {
/* 100 */     if (input == null) {
/* 101 */       return input;
/*     */     }
/*     */     
/* 104 */     int strLen = input.length();
/*     */     
/* 106 */     StringBuilder output = new StringBuilder(strLen);
/*     */     
/* 108 */     for (int i = 0; i < strLen; i++) {
/* 109 */       char c = input.charAt(i);
/* 110 */       if ((!Character.isDigit(c)) && (!Character.isLetter(c))) {
/* 111 */         if (Character.isWhitespace(c)) {
/* 112 */           output.append("_");
/*     */         } else {
/* 114 */           output.append(".");
/*     */         }
/*     */       } else {
/* 117 */         output.append(c);
/*     */       }
/*     */     }
/*     */     
/* 121 */     return output.toString();
/*     */   }
/*     */   
/*     */   private static void usage() {
/* 125 */     System.err.println("Creates a fs hierarchy representing MySQL version, OS version and JVM version.");
/* 126 */     System.err.println("Stores the full path as 'outputDirectory' property in file 'directoryPropPath'");
/* 127 */     System.err.println();
/* 128 */     System.err.println("Usage: java VersionFSHierarchyMaker baseDirectory directoryPropPath jdbcUrlIter");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\VersionFSHierarchyMaker.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */