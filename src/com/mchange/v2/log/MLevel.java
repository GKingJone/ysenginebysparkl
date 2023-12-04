/*     */ package com.mchange.v2.log;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public final class MLevel
/*     */ {
/*     */   public static final MLevel ALL;
/*     */   public static final MLevel CONFIG;
/*     */   public static final MLevel FINE;
/*     */   public static final MLevel FINER;
/*     */   public static final MLevel FINEST;
/*     */   public static final MLevel INFO;
/*     */   public static final MLevel OFF;
/*     */   public static final MLevel SEVERE;
/*     */   public static final MLevel WARNING;
/*     */   private static final Map integersToMLevels;
/*     */   private static final Map namesToMLevels;
/*     */   Object level;
/*     */   int intval;
/*     */   String lvlstring;
/*     */   
/*     */   public static MLevel fromIntValue(int intval)
/*     */   {
/*  44 */     return (MLevel)integersToMLevels.get(new Integer(intval));
/*     */   }
/*     */   
/*  47 */   public static MLevel fromSeverity(String name) { return (MLevel)namesToMLevels.get(name); }
/*     */   
/*     */   static
/*     */   {
/*     */     Class lvlClass;
/*     */     boolean jdk14api;
/*     */     try
/*     */     {
/*  55 */       lvlClass = Class.forName("java.util.logging.Level");
/*  56 */       jdk14api = true;
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*  60 */       lvlClass = null;
/*  61 */       jdk14api = false;
/*     */     }
/*     */     
/*     */     MLevel all;
/*     */     
/*     */     MLevel config;
/*     */     
/*     */     MLevel fine;
/*     */     MLevel finer;
/*     */     MLevel finest;
/*     */     MLevel info;
/*     */     MLevel off;
/*     */     MLevel severe;
/*     */     MLevel warning;
/*     */     try
/*     */     {
/*  77 */       all = new MLevel(jdk14api ? lvlClass.getField("ALL").get(null) : null, Integer.MIN_VALUE, "ALL");
/*  78 */       config = new MLevel(jdk14api ? lvlClass.getField("CONFIG").get(null) : null, 700, "CONFIG");
/*  79 */       fine = new MLevel(jdk14api ? lvlClass.getField("FINE").get(null) : null, 500, "FINE");
/*  80 */       finer = new MLevel(jdk14api ? lvlClass.getField("FINER").get(null) : null, 400, "FINER");
/*  81 */       finest = new MLevel(jdk14api ? lvlClass.getField("FINEST").get(null) : null, 300, "FINEST");
/*  82 */       info = new MLevel(jdk14api ? lvlClass.getField("INFO").get(null) : null, 800, "INFO");
/*  83 */       off = new MLevel(jdk14api ? lvlClass.getField("OFF").get(null) : null, Integer.MAX_VALUE, "OFF");
/*  84 */       severe = new MLevel(jdk14api ? lvlClass.getField("SEVERE").get(null) : null, 900, "SEVERE");
/*  85 */       warning = new MLevel(jdk14api ? lvlClass.getField("WARNING").get(null) : null, 1000, "WARNING");
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  89 */       e.printStackTrace();
/*  90 */       throw new InternalError("Huh? java.util.logging.Level is here, but not its expected public fields?");
/*     */     }
/*     */     
/*  93 */     ALL = all;
/*  94 */     CONFIG = config;
/*  95 */     FINE = fine;
/*  96 */     FINER = finer;
/*  97 */     FINEST = finest;
/*  98 */     INFO = info;
/*  99 */     OFF = off;
/* 100 */     SEVERE = severe;
/* 101 */     WARNING = warning;
/*     */     
/* 103 */     Map tmp = new HashMap();
/* 104 */     tmp.put(new Integer(all.intValue()), all);
/* 105 */     tmp.put(new Integer(config.intValue()), config);
/* 106 */     tmp.put(new Integer(fine.intValue()), fine);
/* 107 */     tmp.put(new Integer(finer.intValue()), finer);
/* 108 */     tmp.put(new Integer(finest.intValue()), finest);
/* 109 */     tmp.put(new Integer(info.intValue()), info);
/* 110 */     tmp.put(new Integer(off.intValue()), off);
/* 111 */     tmp.put(new Integer(severe.intValue()), severe);
/* 112 */     tmp.put(new Integer(warning.intValue()), warning);
/*     */     
/* 114 */     integersToMLevels = Collections.unmodifiableMap(tmp);
/*     */     
/* 116 */     tmp = new HashMap();
/* 117 */     tmp.put(all.getSeverity(), all);
/* 118 */     tmp.put(config.getSeverity(), config);
/* 119 */     tmp.put(fine.getSeverity(), fine);
/* 120 */     tmp.put(finer.getSeverity(), finer);
/* 121 */     tmp.put(finest.getSeverity(), finest);
/* 122 */     tmp.put(info.getSeverity(), info);
/* 123 */     tmp.put(off.getSeverity(), off);
/* 124 */     tmp.put(severe.getSeverity(), severe);
/* 125 */     tmp.put(warning.getSeverity(), warning);
/*     */     
/* 127 */     namesToMLevels = Collections.unmodifiableMap(tmp);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 135 */     return this.intval;
/*     */   }
/*     */   
/* 138 */   public Object asJdk14Level() { return this.level; }
/*     */   
/*     */   public String getSeverity() {
/* 141 */     return this.lvlstring;
/*     */   }
/*     */   
/* 144 */   public String toString() { return getClass().getName() + getLineHeader(); }
/*     */   
/*     */   public String getLineHeader() {
/* 147 */     return "[" + this.lvlstring + ']';
/*     */   }
/*     */   
/*     */   private MLevel(Object level, int intval, String lvlstring) {
/* 151 */     this.level = level;
/* 152 */     this.intval = intval;
/* 153 */     this.lvlstring = lvlstring;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\log\MLevel.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */