/*     */ package com.facebook.presto.jdbc.internal.jetty.util.log;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.DateCache;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import java.io.PrintStream;
/*     */ import java.security.AccessControlException;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @ManagedObject("Jetty StdErr Logging Implementation")
/*     */ public class StdErrLog
/*     */   extends AbstractLogger
/*     */ {
/*  96 */   private static final String EOL = System.getProperty("line.separator");
/*     */   
/*  98 */   private static int __tagpad = Integer.parseInt(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.StdErrLog.TAG_PAD", "0"));
/*     */   
/*     */   private static DateCache _dateCache;
/* 101 */   private static final boolean __source = Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.SOURCE", Log.__props
/* 102 */     .getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.stderr.SOURCE", "false")));
/* 103 */   private static final boolean __long = Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.stderr.LONG", "false"));
/* 104 */   private static final boolean __escape = Boolean.parseBoolean(Log.__props.getProperty("com.facebook.presto.jdbc.internal.jetty.util.log.stderr.ESCAPE", "true"));
/*     */   
/*     */   static
/*     */   {
/* 108 */     String[] deprecatedProperties = { "DEBUG", "com.facebook.presto.jdbc.internal.jetty.util.log.DEBUG", "com.facebook.presto.jdbc.internal.jetty.util.log.stderr.DEBUG" };
/*     */     
/*     */ 
/*     */ 
/* 112 */     for (String deprecatedProp : deprecatedProperties)
/*     */     {
/* 114 */       if (System.getProperty(deprecatedProp) != null)
/*     */       {
/* 116 */         System.err.printf("System Property [%s] has been deprecated! (Use org.eclipse.jetty.LEVEL=DEBUG instead)%n", new Object[] { deprecatedProp });
/*     */       }
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 122 */       _dateCache = new DateCache("yyyy-MM-dd HH:mm:ss");
/*     */     }
/*     */     catch (Exception x)
/*     */     {
/* 126 */       x.printStackTrace(System.err);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setTagPad(int pad)
/*     */   {
/* 132 */     __tagpad = pad;
/*     */   }
/*     */   
/*     */ 
/* 136 */   private int _level = 2;
/*     */   
/*     */   private int _configuredLevel;
/* 139 */   private PrintStream _stderr = null;
/* 140 */   private boolean _source = __source;
/*     */   
/* 142 */   private boolean _printLongNames = __long;
/*     */   
/*     */   private final String _name;
/*     */   
/*     */   private final String _abbrevname;
/* 147 */   private boolean _hideStacks = false;
/*     */   
/*     */ 
/*     */   public static int getLoggingLevel(Properties props, String name)
/*     */   {
/* 152 */     int level = lookupLoggingLevel(props, name);
/* 153 */     if (level == -1)
/*     */     {
/* 155 */       level = lookupLoggingLevel(props, "log");
/* 156 */       if (level == -1)
/* 157 */         level = 2;
/*     */     }
/* 159 */     return level;
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
/*     */   public static StdErrLog getLogger(Class<?> clazz)
/*     */   {
/* 176 */     Logger log = Log.getLogger(clazz);
/* 177 */     if ((log instanceof StdErrLog))
/*     */     {
/* 179 */       return (StdErrLog)log;
/*     */     }
/* 181 */     throw new RuntimeException("Logger for " + clazz + " is not of type StdErrLog");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdErrLog()
/*     */   {
/* 191 */     this(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StdErrLog(String name)
/*     */   {
/* 202 */     this(name, null);
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
/*     */   public StdErrLog(String name, Properties props)
/*     */   {
/* 215 */     if ((props != null) && (props != Log.__props))
/* 216 */       Log.__props.putAll(props);
/* 217 */     this._name = (name == null ? "" : name);
/* 218 */     this._abbrevname = condensePackageString(this._name);
/* 219 */     this._level = getLoggingLevel(Log.__props, this._name);
/* 220 */     this._configuredLevel = this._level;
/*     */     
/*     */     try
/*     */     {
/* 224 */       String source = getLoggingProperty(Log.__props, this._name, "SOURCE");
/* 225 */       this._source = (source == null ? __source : Boolean.parseBoolean(source));
/*     */     }
/*     */     catch (AccessControlException ace)
/*     */     {
/* 229 */       this._source = __source;
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 235 */       String stacks = getLoggingProperty(Log.__props, this._name, "STACKS");
/* 236 */       this._hideStacks = (stacks != null);
/*     */     }
/*     */     catch (AccessControlException localAccessControlException1) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 246 */     return this._name;
/*     */   }
/*     */   
/*     */   public void setPrintLongNames(boolean printLongNames)
/*     */   {
/* 251 */     this._printLongNames = printLongNames;
/*     */   }
/*     */   
/*     */   public boolean isPrintLongNames()
/*     */   {
/* 256 */     return this._printLongNames;
/*     */   }
/*     */   
/*     */   public boolean isHideStacks()
/*     */   {
/* 261 */     return this._hideStacks;
/*     */   }
/*     */   
/*     */   public void setHideStacks(boolean hideStacks)
/*     */   {
/* 266 */     this._hideStacks = hideStacks;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSource()
/*     */   {
/* 277 */     return this._source;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSource(boolean source)
/*     */   {
/* 289 */     this._source = source;
/*     */   }
/*     */   
/*     */   public void warn(String msg, Object... args)
/*     */   {
/* 294 */     if (this._level <= 3)
/*     */     {
/* 296 */       StringBuilder buffer = new StringBuilder(64);
/* 297 */       format(buffer, ":WARN:", msg, args);
/* 298 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void warn(Throwable thrown)
/*     */   {
/* 304 */     warn("", thrown);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable thrown)
/*     */   {
/* 309 */     if (this._level <= 3)
/*     */     {
/* 311 */       StringBuilder buffer = new StringBuilder(64);
/* 312 */       format(buffer, ":WARN:", msg, thrown);
/* 313 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(String msg, Object... args)
/*     */   {
/* 319 */     if (this._level <= 2)
/*     */     {
/* 321 */       StringBuilder buffer = new StringBuilder(64);
/* 322 */       format(buffer, ":INFO:", msg, args);
/* 323 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void info(Throwable thrown)
/*     */   {
/* 329 */     info("", thrown);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable thrown)
/*     */   {
/* 334 */     if (this._level <= 2)
/*     */     {
/* 336 */       StringBuilder buffer = new StringBuilder(64);
/* 337 */       format(buffer, ":INFO:", msg, thrown);
/* 338 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   @ManagedAttribute("is debug enabled for root logger Log.LOG")
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 345 */     return this._level <= 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDebugEnabled(boolean enabled)
/*     */   {
/* 355 */     if (enabled)
/*     */     {
/* 357 */       this._level = 1;
/*     */       
/* 359 */       for (Logger log : Log.getLoggers().values())
/*     */       {
/* 361 */         if ((log.getName().startsWith(getName())) && ((log instanceof StdErrLog))) {
/* 362 */           ((StdErrLog)log).setLevel(1);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 367 */       this._level = this._configuredLevel;
/*     */       
/* 369 */       for (Logger log : Log.getLoggers().values())
/*     */       {
/* 371 */         if ((log.getName().startsWith(getName())) && ((log instanceof StdErrLog))) {
/* 372 */           ((StdErrLog)log).setLevel(((StdErrLog)log)._configuredLevel);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getLevel() {
/* 379 */     return this._level;
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
/*     */   public void setLevel(int level)
/*     */   {
/* 393 */     this._level = level;
/*     */   }
/*     */   
/*     */   public void setStdErrStream(PrintStream stream)
/*     */   {
/* 398 */     this._stderr = (stream == System.err ? null : stream);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Object... args)
/*     */   {
/* 403 */     if (this._level <= 1)
/*     */     {
/* 405 */       StringBuilder buffer = new StringBuilder(64);
/* 406 */       format(buffer, ":DBUG:", msg, args);
/* 407 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(String msg, long arg)
/*     */   {
/* 413 */     if (isDebugEnabled())
/*     */     {
/* 415 */       StringBuilder buffer = new StringBuilder(64);
/* 416 */       format(buffer, ":DBUG:", msg, new Object[] { Long.valueOf(arg) });
/* 417 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   public void debug(Throwable thrown)
/*     */   {
/* 423 */     debug("", thrown);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable thrown)
/*     */   {
/* 428 */     if (this._level <= 1)
/*     */     {
/* 430 */       StringBuilder buffer = new StringBuilder(64);
/* 431 */       format(buffer, ":DBUG:", msg, thrown);
/* 432 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */   
/*     */   private void format(StringBuilder buffer, String level, String msg, Object... args)
/*     */   {
/* 438 */     long now = System.currentTimeMillis();
/* 439 */     int ms = (int)(now % 1000L);
/* 440 */     String d = _dateCache.formatNow(now);
/* 441 */     tag(buffer, d, ms, level);
/* 442 */     format(buffer, msg, args);
/*     */   }
/*     */   
/*     */   private void format(StringBuilder buffer, String level, String msg, Throwable thrown)
/*     */   {
/* 447 */     format(buffer, level, msg, new Object[0]);
/* 448 */     if (isHideStacks())
/*     */     {
/* 450 */       format(buffer, ": " + String.valueOf(thrown), new Object[0]);
/*     */     }
/*     */     else
/*     */     {
/* 454 */       format(buffer, thrown);
/*     */     }
/*     */   }
/*     */   
/*     */   private void tag(StringBuilder buffer, String d, int ms, String tag)
/*     */   {
/* 460 */     buffer.setLength(0);
/* 461 */     buffer.append(d);
/* 462 */     if (ms > 99)
/*     */     {
/* 464 */       buffer.append('.');
/*     */     }
/* 466 */     else if (ms > 9)
/*     */     {
/* 468 */       buffer.append(".0");
/*     */     }
/*     */     else
/*     */     {
/* 472 */       buffer.append(".00");
/*     */     }
/* 474 */     buffer.append(ms).append(tag);
/*     */     
/* 476 */     String name = this._printLongNames ? this._name : this._abbrevname;
/* 477 */     String tname = Thread.currentThread().getName();
/*     */     
/* 479 */     int p = __tagpad > 0 ? name.length() + tname.length() - __tagpad : 0;
/*     */     
/* 481 */     if (p < 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 487 */       buffer.append(name).append(':').append("                                                  ", 0, -p).append(tname);
/*     */     }
/* 489 */     else if (p == 0)
/*     */     {
/* 491 */       buffer.append(name).append(':').append(tname);
/*     */     }
/* 493 */     buffer.append(':');
/*     */     
/* 495 */     if (this._source)
/*     */     {
/* 497 */       Throwable source = new Throwable();
/* 498 */       StackTraceElement[] frames = source.getStackTrace();
/* 499 */       for (int i = 0; i < frames.length; i++)
/*     */       {
/* 501 */         StackTraceElement frame = frames[i];
/* 502 */         String clazz = frame.getClassName();
/* 503 */         if ((!clazz.equals(StdErrLog.class.getName())) && (!clazz.equals(Log.class.getName())))
/*     */         {
/*     */ 
/*     */ 
/* 507 */           if ((!this._printLongNames) && (clazz.startsWith("com.facebook.presto.jdbc.internal.jetty.")))
/*     */           {
/* 509 */             buffer.append(condensePackageString(clazz));
/*     */           }
/*     */           else
/*     */           {
/* 513 */             buffer.append(clazz);
/*     */           }
/* 515 */           buffer.append('#').append(frame.getMethodName());
/* 516 */           if (frame.getFileName() != null)
/*     */           {
/* 518 */             buffer.append('(').append(frame.getFileName()).append(':').append(frame.getLineNumber()).append(')');
/*     */           }
/* 520 */           buffer.append(':');
/* 521 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 525 */     buffer.append(' ');
/*     */   }
/*     */   
/*     */   private void format(StringBuilder builder, String msg, Object... args)
/*     */   {
/* 530 */     if (msg == null)
/*     */     {
/* 532 */       msg = "";
/* 533 */       for (int i = 0; i < args.length; i++)
/*     */       {
/* 535 */         msg = msg + "{} ";
/*     */       }
/*     */     }
/* 538 */     String braces = "{}";
/* 539 */     int start = 0;
/* 540 */     for (Object arg : args)
/*     */     {
/* 542 */       int bracesIndex = msg.indexOf(braces, start);
/* 543 */       if (bracesIndex < 0)
/*     */       {
/* 545 */         escape(builder, msg.substring(start));
/* 546 */         builder.append(" ");
/* 547 */         builder.append(arg);
/* 548 */         start = msg.length();
/*     */       }
/*     */       else
/*     */       {
/* 552 */         escape(builder, msg.substring(start, bracesIndex));
/* 553 */         builder.append(String.valueOf(arg));
/* 554 */         start = bracesIndex + braces.length();
/*     */       }
/*     */     }
/* 557 */     escape(builder, msg.substring(start));
/*     */   }
/*     */   
/*     */   private void escape(StringBuilder builder, String string)
/*     */   {
/* 562 */     if (__escape)
/*     */     {
/* 564 */       for (int i = 0; i < string.length(); i++)
/*     */       {
/* 566 */         char c = string.charAt(i);
/* 567 */         if (Character.isISOControl(c))
/*     */         {
/* 569 */           if (c == '\n')
/*     */           {
/* 571 */             builder.append('|');
/*     */           }
/* 573 */           else if (c == '\r')
/*     */           {
/* 575 */             builder.append('<');
/*     */           }
/*     */           else
/*     */           {
/* 579 */             builder.append('?');
/*     */           }
/*     */           
/*     */         }
/*     */         else {
/* 584 */           builder.append(c);
/*     */         }
/*     */         
/*     */       }
/*     */     } else {
/* 589 */       builder.append(string);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void format(StringBuilder buffer, Throwable thrown) {
/* 594 */     format(buffer, thrown, "");
/*     */   }
/*     */   
/*     */   protected void format(StringBuilder buffer, Throwable thrown, String indent)
/*     */   {
/* 599 */     if (thrown == null)
/*     */     {
/* 601 */       buffer.append("null");
/*     */     }
/*     */     else
/*     */     {
/* 605 */       buffer.append(EOL).append(indent);
/* 606 */       format(buffer, thrown.toString(), new Object[0]);
/* 607 */       StackTraceElement[] elements = thrown.getStackTrace();
/* 608 */       for (int i = 0; (elements != null) && (i < elements.length); i++)
/*     */       {
/* 610 */         buffer.append(EOL).append(indent).append("\tat ");
/* 611 */         format(buffer, elements[i].toString(), new Object[0]);
/*     */       }
/*     */       
/* 614 */       for (Throwable suppressed : thrown.getSuppressed())
/*     */       {
/* 616 */         buffer.append(EOL).append(indent).append("Suppressed: ");
/* 617 */         format(buffer, suppressed, "\t|" + indent);
/*     */       }
/*     */       
/* 620 */       Throwable cause = thrown.getCause();
/* 621 */       if ((cause != null) && (cause != thrown))
/*     */       {
/* 623 */         buffer.append(EOL).append(indent).append("Caused by: ");
/* 624 */         format(buffer, cause, indent);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Logger newLogger(String fullname)
/*     */   {
/* 636 */     StdErrLog logger = new StdErrLog(fullname);
/*     */     
/* 638 */     logger.setPrintLongNames(this._printLongNames);
/* 639 */     logger._stderr = this._stderr;
/*     */     
/*     */ 
/* 642 */     if (this._level != this._configuredLevel) {
/* 643 */       logger._level = this._level;
/*     */     }
/* 645 */     return logger;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 651 */     StringBuilder s = new StringBuilder();
/* 652 */     s.append("StdErrLog:");
/* 653 */     s.append(this._name);
/* 654 */     s.append(":LEVEL=");
/* 655 */     switch (this._level)
/*     */     {
/*     */     case 0: 
/* 658 */       s.append("ALL");
/* 659 */       break;
/*     */     case 1: 
/* 661 */       s.append("DEBUG");
/* 662 */       break;
/*     */     case 2: 
/* 664 */       s.append("INFO");
/* 665 */       break;
/*     */     case 3: 
/* 667 */       s.append("WARN");
/* 668 */       break;
/*     */     default: 
/* 670 */       s.append("?");
/*     */     }
/*     */     
/* 673 */     return s.toString();
/*     */   }
/*     */   
/*     */   public void ignore(Throwable ignored)
/*     */   {
/* 678 */     if (this._level <= 0)
/*     */     {
/* 680 */       StringBuilder buffer = new StringBuilder(64);
/* 681 */       format(buffer, ":IGNORED:", "", ignored);
/* 682 */       (this._stderr == null ? System.err : this._stderr).println(buffer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\log\StdErrLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */