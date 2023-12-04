/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
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
/*     */ public class RolloverFileOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private static Timer __rollover;
/*     */   static final String YYYY_MM_DD = "yyyy_mm_dd";
/*     */   static final String ROLLOVER_FILE_DATE_FORMAT = "yyyy_MM_dd";
/*     */   static final String ROLLOVER_FILE_BACKUP_FORMAT = "HHmmssSSS";
/*     */   static final int ROLLOVER_FILE_RETAIN_DAYS = 31;
/*     */   private RollTask _rollTask;
/*     */   private SimpleDateFormat _fileBackupFormat;
/*     */   private SimpleDateFormat _fileDateFormat;
/*     */   private String _filename;
/*     */   private File _file;
/*     */   private boolean _append;
/*     */   private int _retainDays;
/*     */   
/*     */   public RolloverFileOutputStream(String filename)
/*     */     throws IOException
/*     */   {
/*  71 */     this(filename, true, 31);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RolloverFileOutputStream(String filename, boolean append)
/*     */     throws IOException
/*     */   {
/*  84 */     this(filename, append, 31);
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
/*     */ 
/*     */ 
/*     */   public RolloverFileOutputStream(String filename, boolean append, int retainDays)
/*     */     throws IOException
/*     */   {
/* 100 */     this(filename, append, retainDays, TimeZone.getDefault());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone)
/*     */     throws IOException
/*     */   {
/* 119 */     this(filename, append, retainDays, zone, null, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone, String dateFormat, String backupFormat)
/*     */     throws IOException
/*     */   {
/* 141 */     super(null);
/*     */     
/* 143 */     if (dateFormat == null)
/* 144 */       dateFormat = "yyyy_MM_dd";
/* 145 */     this._fileDateFormat = new SimpleDateFormat(dateFormat);
/*     */     
/* 147 */     if (backupFormat == null)
/* 148 */       backupFormat = "HHmmssSSS";
/* 149 */     this._fileBackupFormat = new SimpleDateFormat(backupFormat);
/*     */     
/* 151 */     this._fileBackupFormat.setTimeZone(zone);
/* 152 */     this._fileDateFormat.setTimeZone(zone);
/*     */     
/* 154 */     if (filename != null)
/*     */     {
/* 156 */       filename = filename.trim();
/* 157 */       if (filename.length() == 0)
/* 158 */         filename = null;
/*     */     }
/* 160 */     if (filename == null) {
/* 161 */       throw new IllegalArgumentException("Invalid filename");
/*     */     }
/* 163 */     this._filename = filename;
/* 164 */     this._append = append;
/* 165 */     this._retainDays = retainDays;
/* 166 */     setFile();
/*     */     
/* 168 */     synchronized (RolloverFileOutputStream.class)
/*     */     {
/* 170 */       if (__rollover == null) {
/* 171 */         __rollover = new Timer(RolloverFileOutputStream.class.getName(), true);
/*     */       }
/* 173 */       this._rollTask = new RollTask(null);
/*     */       
/* 175 */       Calendar now = Calendar.getInstance();
/* 176 */       now.setTimeZone(zone);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 181 */       GregorianCalendar midnight = new GregorianCalendar(now.get(1), now.get(2), now.get(5), 23, 0);
/*     */       
/* 183 */       midnight.setTimeZone(zone);
/* 184 */       midnight.add(10, 1);
/* 185 */       __rollover.scheduleAtFixedRate(this._rollTask, midnight.getTime(), 86400000L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFilename()
/*     */   {
/* 192 */     return this._filename;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDatedFilename()
/*     */   {
/* 198 */     if (this._file == null)
/* 199 */       return null;
/* 200 */     return this._file.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRetainDays()
/*     */   {
/* 206 */     return this._retainDays;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void setFile()
/*     */     throws IOException
/*     */   {
/* 214 */     File file = new File(this._filename);
/* 215 */     this._filename = file.getCanonicalPath();
/* 216 */     file = new File(this._filename);
/* 217 */     File dir = new File(file.getParent());
/* 218 */     if ((!dir.isDirectory()) || (!dir.canWrite())) {
/* 219 */       throw new IOException("Cannot write log directory " + dir);
/*     */     }
/* 221 */     Date now = new Date();
/*     */     
/*     */ 
/* 224 */     String filename = file.getName();
/* 225 */     int i = filename.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
/* 226 */     if (i >= 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 231 */       file = new File(dir, filename.substring(0, i) + this._fileDateFormat.format(now) + filename.substring(i + "yyyy_mm_dd".length()));
/*     */     }
/*     */     
/* 234 */     if ((file.exists()) && (!file.canWrite())) {
/* 235 */       throw new IOException("Cannot write log file " + file);
/*     */     }
/*     */     
/* 238 */     if ((this.out == null) || (!file.equals(this._file)))
/*     */     {
/*     */ 
/* 241 */       this._file = file;
/* 242 */       if ((!this._append) && (file.exists()))
/* 243 */         file.renameTo(new File(file.toString() + "." + this._fileBackupFormat.format(now)));
/* 244 */       OutputStream oldOut = this.out;
/* 245 */       this.out = new FileOutputStream(file.toString(), this._append);
/* 246 */       if (oldOut != null) {
/* 247 */         oldOut.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void removeOldFiles()
/*     */   {
/* 255 */     if (this._retainDays > 0)
/*     */     {
/* 257 */       long now = System.currentTimeMillis();
/*     */       
/* 259 */       File file = new File(this._filename);
/* 260 */       File dir = new File(file.getParent());
/* 261 */       String fn = file.getName();
/* 262 */       int s = fn.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
/* 263 */       if (s < 0)
/* 264 */         return;
/* 265 */       String prefix = fn.substring(0, s);
/* 266 */       String suffix = fn.substring(s + "yyyy_mm_dd".length());
/*     */       
/* 268 */       String[] logList = dir.list();
/* 269 */       for (int i = 0; i < logList.length; i++)
/*     */       {
/* 271 */         fn = logList[i];
/* 272 */         if ((fn.startsWith(prefix)) && (fn.indexOf(suffix, prefix.length()) >= 0))
/*     */         {
/* 274 */           File f = new File(dir, fn);
/* 275 */           long date = f.lastModified();
/* 276 */           if ((now - date) / 86400000L > this._retainDays) {
/* 277 */             f.delete();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void write(byte[] buf)
/*     */     throws IOException
/*     */   {
/* 288 */     this.out.write(buf);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void write(byte[] buf, int off, int len)
/*     */     throws IOException
/*     */   {
/* 296 */     this.out.write(buf, off, len);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: ldc 2
/*     */     //   2: dup
/*     */     //   3: astore_1
/*     */     //   4: monitorenter
/*     */     //   5: aload_0
/*     */     //   6: invokespecial 308	java/io/FilterOutputStream:close	()V
/*     */     //   9: aload_0
/*     */     //   10: aconst_null
/*     */     //   11: putfield 239	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream:out	Ljava/io/OutputStream;
/*     */     //   14: aload_0
/*     */     //   15: aconst_null
/*     */     //   16: putfield 164	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream:_file	Ljava/io/File;
/*     */     //   19: goto +16 -> 35
/*     */     //   22: astore_2
/*     */     //   23: aload_0
/*     */     //   24: aconst_null
/*     */     //   25: putfield 239	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream:out	Ljava/io/OutputStream;
/*     */     //   28: aload_0
/*     */     //   29: aconst_null
/*     */     //   30: putfield 164	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream:_file	Ljava/io/File;
/*     */     //   33: aload_2
/*     */     //   34: athrow
/*     */     //   35: aload_0
/*     */     //   36: getfield 121	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream:_rollTask	Lcom/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream$RollTask;
/*     */     //   39: invokevirtual 311	com/facebook/presto/jdbc/internal/jetty/util/RolloverFileOutputStream$RollTask:cancel	()Z
/*     */     //   42: pop
/*     */     //   43: aload_1
/*     */     //   44: monitorexit
/*     */     //   45: goto +8 -> 53
/*     */     //   48: astore_3
/*     */     //   49: aload_1
/*     */     //   50: monitorexit
/*     */     //   51: aload_3
/*     */     //   52: athrow
/*     */     //   53: return
/*     */     // Line number table:
/*     */     //   Java source line #306	-> byte code offset #0
/*     */     //   Java source line #308	-> byte code offset #5
/*     */     //   Java source line #311	-> byte code offset #9
/*     */     //   Java source line #312	-> byte code offset #14
/*     */     //   Java source line #313	-> byte code offset #19
/*     */     //   Java source line #311	-> byte code offset #22
/*     */     //   Java source line #312	-> byte code offset #28
/*     */     //   Java source line #315	-> byte code offset #35
/*     */     //   Java source line #316	-> byte code offset #43
/*     */     //   Java source line #317	-> byte code offset #53
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	54	0	this	RolloverFileOutputStream
/*     */     //   3	47	1	Ljava/lang/Object;	Object
/*     */     //   22	12	2	localObject1	Object
/*     */     //   48	4	3	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   5	9	22	finally
/*     */     //   5	45	48	finally
/*     */     //   48	51	48	finally
/*     */   }
/*     */   
/*     */   private class RollTask
/*     */     extends TimerTask
/*     */   {
/*     */     private RollTask() {}
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 329 */         RolloverFileOutputStream.this.setFile();
/* 330 */         RolloverFileOutputStream.this.removeOldFiles();
/*     */ 
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */ 
/* 336 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\RolloverFileOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */