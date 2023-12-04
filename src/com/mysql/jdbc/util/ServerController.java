/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.StringUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public class ServerController
/*     */ {
/*     */   public static final String BASEDIR_KEY = "basedir";
/*     */   public static final String DATADIR_KEY = "datadir";
/*     */   public static final String DEFAULTS_FILE_KEY = "defaults-file";
/*     */   public static final String EXECUTABLE_NAME_KEY = "executable";
/*     */   public static final String EXECUTABLE_PATH_KEY = "executablePath";
/*  73 */   private Process serverProcess = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  78 */   private Properties serverProps = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  83 */   private Properties systemProps = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServerController(String baseDir)
/*     */   {
/*  94 */     setBaseDir(baseDir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServerController(String basedir, String datadir) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setBaseDir(String baseDir)
/*     */   {
/* 116 */     getServerProps().setProperty("basedir", baseDir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDataDir(String dataDir)
/*     */   {
/* 126 */     getServerProps().setProperty("datadir", dataDir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Process start()
/*     */     throws IOException
/*     */   {
/* 139 */     if (this.serverProcess != null) {
/* 140 */       throw new IllegalArgumentException("Server already started");
/*     */     }
/* 142 */     this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
/*     */     
/* 144 */     return this.serverProcess;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stop(boolean forceIfNecessary)
/*     */     throws IOException
/*     */   {
/* 157 */     if (this.serverProcess != null)
/*     */     {
/* 159 */       String basedir = getServerProps().getProperty("basedir");
/*     */       
/* 161 */       StringBuilder pathBuf = new StringBuilder(basedir);
/*     */       
/* 163 */       if (!basedir.endsWith(File.separator)) {
/* 164 */         pathBuf.append(File.separator);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 169 */       pathBuf.append("bin");
/* 170 */       pathBuf.append(File.separator);
/* 171 */       pathBuf.append("mysqladmin shutdown");
/*     */       
/* 173 */       System.out.println(pathBuf.toString());
/*     */       
/* 175 */       Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
/*     */       
/* 177 */       int exitStatus = -1;
/*     */       try
/*     */       {
/* 180 */         exitStatus = mysqladmin.waitFor();
/*     */       }
/*     */       catch (InterruptedException ie) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 188 */       if ((exitStatus != 0) && (forceIfNecessary)) {
/* 189 */         forceStop();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void forceStop()
/*     */   {
/* 198 */     if (this.serverProcess != null) {
/* 199 */       this.serverProcess.destroy();
/* 200 */       this.serverProcess = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Properties getServerProps()
/*     */   {
/* 211 */     if (this.serverProps == null) {
/* 212 */       this.serverProps = new Properties();
/*     */     }
/*     */     
/* 215 */     return this.serverProps;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getCommandLine()
/*     */   {
/* 225 */     StringBuilder commandLine = new StringBuilder(getFullExecutablePath());
/* 226 */     commandLine.append(buildOptionalCommandLine());
/*     */     
/* 228 */     return commandLine.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getFullExecutablePath()
/*     */   {
/* 237 */     StringBuilder pathBuf = new StringBuilder();
/*     */     
/* 239 */     String optionalExecutablePath = getServerProps().getProperty("executablePath");
/*     */     
/* 241 */     if (optionalExecutablePath == null)
/*     */     {
/* 243 */       String basedir = getServerProps().getProperty("basedir");
/* 244 */       pathBuf.append(basedir);
/*     */       
/* 246 */       if (!basedir.endsWith(File.separator)) {
/* 247 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */       
/* 250 */       if (runningOnWindows()) {
/* 251 */         pathBuf.append("bin");
/*     */       } else {
/* 253 */         pathBuf.append("libexec");
/*     */       }
/*     */       
/* 256 */       pathBuf.append(File.separatorChar);
/*     */     } else {
/* 258 */       pathBuf.append(optionalExecutablePath);
/*     */       
/* 260 */       if (!optionalExecutablePath.endsWith(File.separator)) {
/* 261 */         pathBuf.append(File.separatorChar);
/*     */       }
/*     */     }
/*     */     
/* 265 */     String executableName = getServerProps().getProperty("executable", "mysqld");
/*     */     
/* 267 */     pathBuf.append(executableName);
/*     */     
/* 269 */     return pathBuf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String buildOptionalCommandLine()
/*     */   {
/* 279 */     StringBuilder commandLineBuf = new StringBuilder();
/*     */     Iterator<Object> iter;
/* 281 */     if (this.serverProps != null)
/*     */     {
/* 283 */       for (iter = this.serverProps.keySet().iterator(); iter.hasNext();) {
/* 284 */         String key = (String)iter.next();
/* 285 */         String value = this.serverProps.getProperty(key);
/*     */         
/* 287 */         if (!isNonCommandLineArgument(key)) {
/* 288 */           if ((value != null) && (value.length() > 0)) {
/* 289 */             commandLineBuf.append(" \"");
/* 290 */             commandLineBuf.append("--");
/* 291 */             commandLineBuf.append(key);
/* 292 */             commandLineBuf.append("=");
/* 293 */             commandLineBuf.append(value);
/* 294 */             commandLineBuf.append("\"");
/*     */           } else {
/* 296 */             commandLineBuf.append(" --");
/* 297 */             commandLineBuf.append(key);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 303 */     return commandLineBuf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean isNonCommandLineArgument(String propName)
/*     */   {
/* 312 */     return (propName.equals("executable")) || (propName.equals("executablePath"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized Properties getSystemProperties()
/*     */   {
/* 321 */     if (this.systemProps == null) {
/* 322 */       this.systemProps = System.getProperties();
/*     */     }
/*     */     
/* 325 */     return this.systemProps;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean runningOnWindows()
/*     */   {
/* 334 */     return StringUtils.indexOfIgnoreCase(getSystemProperties().getProperty("os.name"), "WINDOWS") != -1;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\ServerController.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */