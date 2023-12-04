/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ public class Scanner
/*     */   extends AbstractLifeCycle
/*     */ {
/*  51 */   private static final Logger LOG = Log.getLogger(Scanner.class);
/*  52 */   private static int __scannerId = 0;
/*     */   private int _scanInterval;
/*  54 */   private int _scanCount = 0;
/*  55 */   private final List<Listener> _listeners = new ArrayList();
/*  56 */   private final Map<String, TimeNSize> _prevScan = new HashMap();
/*  57 */   private final Map<String, TimeNSize> _currentScan = new HashMap();
/*     */   private FilenameFilter _filter;
/*  59 */   private final List<File> _scanDirs = new ArrayList();
/*  60 */   private volatile boolean _running = false;
/*  61 */   private boolean _reportExisting = true;
/*  62 */   private boolean _reportDirs = true;
/*     */   private Timer _timer;
/*     */   private TimerTask _task;
/*  65 */   private int _scanDepth = 0;
/*     */   
/*  67 */   public static enum Notification { ADDED,  CHANGED,  REMOVED;
/*  68 */     private Notification() {} } private final Map<String, Notification> _notifications = new HashMap();
/*     */   
/*     */   static class TimeNSize
/*     */   {
/*     */     final long _lastModified;
/*     */     final long _size;
/*     */     
/*     */     public TimeNSize(long lastModified, long size)
/*     */     {
/*  77 */       this._lastModified = lastModified;
/*  78 */       this._size = size;
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  84 */       return (int)this._lastModified ^ (int)this._size;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/*  90 */       if ((o instanceof TimeNSize))
/*     */       {
/*  92 */         TimeNSize tns = (TimeNSize)o;
/*  93 */         return (tns._lastModified == this._lastModified) && (tns._size == this._size);
/*     */       }
/*  95 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 101 */       return "[lm=" + this._lastModified + ",s=" + this._size + "]";
/*     */     }
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
/*     */   public synchronized int getScanInterval()
/*     */   {
/* 154 */     return this._scanInterval;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setScanInterval(int scanInterval)
/*     */   {
/* 163 */     this._scanInterval = scanInterval;
/* 164 */     schedule();
/*     */   }
/*     */   
/*     */   public void setScanDirs(List<File> dirs)
/*     */   {
/* 169 */     this._scanDirs.clear();
/* 170 */     this._scanDirs.addAll(dirs);
/*     */   }
/*     */   
/*     */   public synchronized void addScanDir(File dir)
/*     */   {
/* 175 */     this._scanDirs.add(dir);
/*     */   }
/*     */   
/*     */   public List<File> getScanDirs()
/*     */   {
/* 180 */     return Collections.unmodifiableList(this._scanDirs);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRecursive(boolean recursive)
/*     */   {
/* 190 */     this._scanDepth = (recursive ? -1 : 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getRecursive()
/*     */   {
/* 200 */     return this._scanDepth == -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getScanDepth()
/*     */   {
/* 209 */     return this._scanDepth;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScanDepth(int scanDepth)
/*     */   {
/* 218 */     this._scanDepth = scanDepth;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFilenameFilter(FilenameFilter filter)
/*     */   {
/* 228 */     this._filter = filter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FilenameFilter getFilenameFilter()
/*     */   {
/* 237 */     return this._filter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReportExistingFilesOnStartup(boolean reportExisting)
/*     */   {
/* 249 */     this._reportExisting = reportExisting;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getReportExistingFilesOnStartup()
/*     */   {
/* 255 */     return this._reportExisting;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setReportDirs(boolean dirs)
/*     */   {
/* 264 */     this._reportDirs = dirs;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getReportDirs()
/*     */   {
/* 270 */     return this._reportDirs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addListener(Listener listener)
/*     */   {
/* 280 */     if (listener == null)
/* 281 */       return;
/* 282 */     this._listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void removeListener(Listener listener)
/*     */   {
/* 292 */     if (listener == null)
/* 293 */       return;
/* 294 */     this._listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void doStart()
/*     */   {
/* 304 */     if (this._running) {
/* 305 */       return;
/*     */     }
/* 307 */     this._running = true;
/*     */     
/* 309 */     if (this._reportExisting)
/*     */     {
/*     */ 
/* 312 */       scan();
/* 313 */       scan();
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 318 */       scanFiles();
/* 319 */       this._prevScan.putAll(this._currentScan);
/*     */     }
/* 321 */     schedule();
/*     */   }
/*     */   
/*     */   public TimerTask newTimerTask()
/*     */   {
/* 326 */     new TimerTask()
/*     */     {
/*     */       public void run() {
/* 329 */         Scanner.this.scan();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public Timer newTimer() {
/* 335 */     return new Timer("Scanner-" + __scannerId++, true);
/*     */   }
/*     */   
/*     */   public void schedule()
/*     */   {
/* 340 */     if (this._running)
/*     */     {
/* 342 */       if (this._timer != null)
/* 343 */         this._timer.cancel();
/* 344 */       if (this._task != null)
/* 345 */         this._task.cancel();
/* 346 */       if (getScanInterval() > 0)
/*     */       {
/* 348 */         this._timer = newTimer();
/* 349 */         this._task = newTimerTask();
/* 350 */         this._timer.schedule(this._task, 1010L * getScanInterval(), 1010L * getScanInterval());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void doStop()
/*     */   {
/* 360 */     if (this._running)
/*     */     {
/* 362 */       this._running = false;
/* 363 */       if (this._timer != null)
/* 364 */         this._timer.cancel();
/* 365 */       if (this._task != null)
/* 366 */         this._task.cancel();
/* 367 */       this._task = null;
/* 368 */       this._timer = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean exists(String path)
/*     */   {
/* 378 */     for (File dir : this._scanDirs)
/* 379 */       if (new File(dir, path).exists())
/* 380 */         return true;
/* 381 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void scan()
/*     */   {
/* 390 */     reportScanStart(++this._scanCount);
/* 391 */     scanFiles();
/* 392 */     reportDifferences(this._currentScan, this._prevScan);
/* 393 */     this._prevScan.clear();
/* 394 */     this._prevScan.putAll(this._currentScan);
/* 395 */     reportScanEnd(this._scanCount);
/*     */     
/* 397 */     for (Listener l : this._listeners)
/*     */     {
/*     */       try
/*     */       {
/* 401 */         if ((l instanceof ScanListener)) {
/* 402 */           ((ScanListener)l).scan();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 406 */         LOG.warn(e);
/*     */       }
/*     */       catch (Error e)
/*     */       {
/* 410 */         LOG.warn(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void scanFiles()
/*     */   {
/* 420 */     if (this._scanDirs == null) {
/* 421 */       return;
/*     */     }
/* 423 */     this._currentScan.clear();
/* 424 */     Iterator<File> itor = this._scanDirs.iterator();
/* 425 */     while (itor.hasNext())
/*     */     {
/* 427 */       File dir = (File)itor.next();
/*     */       
/* 429 */       if ((dir != null) && (dir.exists())) {
/*     */         try
/*     */         {
/* 432 */           scanFile(dir.getCanonicalFile(), this._currentScan, 0);
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 436 */           LOG.warn("Error scanning files.", e);
/*     */         }
/*     */       }
/*     */     }
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
/*     */   public synchronized void reportDifferences(Map<String, TimeNSize> currentScan, Map<String, TimeNSize> oldScan)
/*     */   {
/* 452 */     Set<String> oldScanKeys = new HashSet(oldScan.keySet());
/*     */     
/*     */ 
/* 455 */     for (Entry<String, TimeNSize> entry : currentScan.entrySet())
/*     */     {
/* 457 */       String file = (String)entry.getKey();
/* 458 */       if (!oldScanKeys.contains(file))
/*     */       {
/* 460 */         Notification old = (Notification)this._notifications.put(file, Notification.ADDED);
/* 461 */         if (old != null)
/*     */         {
/* 463 */           switch (old)
/*     */           {
/*     */           case REMOVED: 
/*     */           case CHANGED: 
/* 467 */             this._notifications.put(file, Notification.CHANGED);
/*     */           }
/*     */         }
/*     */       }
/* 471 */       else if (!((TimeNSize)oldScan.get(file)).equals(currentScan.get(file)))
/*     */       {
/* 473 */         Notification old = (Notification)this._notifications.put(file, Notification.CHANGED);
/* 474 */         if (old != null)
/*     */         {
/* 476 */           switch (old)
/*     */           {
/*     */           case ADDED: 
/* 479 */             this._notifications.put(file, Notification.ADDED);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 486 */     for (String file : oldScan.keySet())
/*     */     {
/* 488 */       if (!currentScan.containsKey(file))
/*     */       {
/* 490 */         Notification old = (Notification)this._notifications.put(file, Notification.REMOVED);
/* 491 */         if (old != null)
/*     */         {
/* 493 */           switch (old)
/*     */           {
/*     */           case ADDED: 
/* 496 */             this._notifications.remove(file);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/* 502 */     if (LOG.isDebugEnabled()) {
/* 503 */       LOG.debug("scanned " + this._scanDirs + ": " + this._notifications, new Object[0]);
/*     */     }
/*     */     
/*     */ 
/* 507 */     Object bulkChanges = new ArrayList();
/* 508 */     for (Iterator<Entry<String, Notification>> iter = this._notifications.entrySet().iterator(); iter.hasNext();)
/*     */     {
/* 510 */       Entry<String, Notification> entry = (Entry)iter.next();
/* 511 */       String file = (String)entry.getKey();
/*     */       
/*     */ 
/* 514 */       if (oldScan.containsKey(file) ? 
/*     */       
/* 516 */         ((TimeNSize)oldScan.get(file)).equals(currentScan.get(file)) : 
/*     */         
/*     */ 
/* 519 */         !currentScan.containsKey(file))
/*     */       {
/*     */ 
/*     */ 
/* 523 */         Notification notification = (Notification)entry.getValue();
/* 524 */         iter.remove();
/* 525 */         ((List)bulkChanges).add(file);
/* 526 */         switch (notification)
/*     */         {
/*     */         case ADDED: 
/* 529 */           reportAddition(file);
/* 530 */           break;
/*     */         case CHANGED: 
/* 532 */           reportChange(file);
/* 533 */           break;
/*     */         case REMOVED: 
/* 535 */           reportRemoval(file);
/*     */         }
/*     */       }
/*     */     }
/* 539 */     if (!((List)bulkChanges).isEmpty()) {
/* 540 */       reportBulkChanges((List)bulkChanges);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void scanFile(File f, Map<String, TimeNSize> scanInfoMap, int depth)
/*     */   {
/*     */     try
/*     */     {
/* 554 */       if (!f.exists()) {
/* 555 */         return;
/*     */       }
/* 557 */       if ((f.isFile()) || ((depth > 0) && (this._reportDirs) && (f.isDirectory())))
/*     */       {
/* 559 */         if ((this._filter == null) || ((this._filter != null) && (this._filter.accept(f.getParentFile(), f.getName()))))
/*     */         {
/* 561 */           if (LOG.isDebugEnabled())
/* 562 */             LOG.debug("scan accepted {}", new Object[] { f });
/* 563 */           String name = f.getCanonicalPath();
/* 564 */           scanInfoMap.put(name, new TimeNSize(f.lastModified(), f.isDirectory() ? 0L : f.length()));
/*     */ 
/*     */ 
/*     */         }
/* 568 */         else if (LOG.isDebugEnabled()) {
/* 569 */           LOG.debug("scan rejected {}", new Object[] { f });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 574 */       if ((f.isDirectory()) && ((depth < this._scanDepth) || (this._scanDepth == -1) || (this._scanDirs.contains(f))))
/*     */       {
/* 576 */         File[] files = f.listFiles();
/* 577 */         if (files != null)
/*     */         {
/* 579 */           for (int i = 0; i < files.length; i++) {
/* 580 */             scanFile(files[i], scanInfoMap, depth + 1);
/*     */           }
/*     */         } else {
/* 583 */           LOG.warn("Error listing files in directory {}", new Object[] { f });
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 588 */       LOG.warn("Error scanning watched files", e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void warn(Object listener, String filename, Throwable th)
/*     */   {
/* 594 */     LOG.warn(listener + " failed on '" + filename, th);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportAddition(String filename)
/*     */   {
/* 603 */     Iterator<Listener> itor = this._listeners.iterator();
/* 604 */     while (itor.hasNext())
/*     */     {
/* 606 */       Listener l = (Listener)itor.next();
/*     */       try
/*     */       {
/* 609 */         if ((l instanceof DiscreteListener)) {
/* 610 */           ((DiscreteListener)l).fileAdded(filename);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 614 */         warn(l, filename, e);
/*     */       }
/*     */       catch (Error e)
/*     */       {
/* 618 */         warn(l, filename, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportRemoval(String filename)
/*     */   {
/* 630 */     Iterator<Listener> itor = this._listeners.iterator();
/* 631 */     while (itor.hasNext())
/*     */     {
/* 633 */       Object l = itor.next();
/*     */       try
/*     */       {
/* 636 */         if ((l instanceof DiscreteListener)) {
/* 637 */           ((DiscreteListener)l).fileRemoved(filename);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 641 */         warn(l, filename, e);
/*     */       }
/*     */       catch (Error e)
/*     */       {
/* 645 */         warn(l, filename, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportChange(String filename)
/*     */   {
/* 657 */     Iterator<Listener> itor = this._listeners.iterator();
/* 658 */     while (itor.hasNext())
/*     */     {
/* 660 */       Listener l = (Listener)itor.next();
/*     */       try
/*     */       {
/* 663 */         if ((l instanceof DiscreteListener)) {
/* 664 */           ((DiscreteListener)l).fileChanged(filename);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 668 */         warn(l, filename, e);
/*     */       }
/*     */       catch (Error e)
/*     */       {
/* 672 */         warn(l, filename, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void reportBulkChanges(List<String> filenames)
/*     */   {
/* 679 */     Iterator<Listener> itor = this._listeners.iterator();
/* 680 */     while (itor.hasNext())
/*     */     {
/* 682 */       Listener l = (Listener)itor.next();
/*     */       try
/*     */       {
/* 685 */         if ((l instanceof BulkListener)) {
/* 686 */           ((BulkListener)l).filesChanged(filenames);
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 690 */         warn(l, filenames.toString(), e);
/*     */       }
/*     */       catch (Error e)
/*     */       {
/* 694 */         warn(l, filenames.toString(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportScanStart(int cycle)
/*     */   {
/* 704 */     for (Listener listener : this._listeners)
/*     */     {
/*     */       try
/*     */       {
/* 708 */         if ((listener instanceof ScanCycleListener))
/*     */         {
/* 710 */           ((ScanCycleListener)listener).scanStarted(cycle);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 715 */         LOG.warn(listener + " failed on scan start for cycle " + cycle, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reportScanEnd(int cycle)
/*     */   {
/* 725 */     for (Listener listener : this._listeners)
/*     */     {
/*     */       try
/*     */       {
/* 729 */         if ((listener instanceof ScanCycleListener))
/*     */         {
/* 731 */           ((ScanCycleListener)listener).scanEnded(cycle);
/*     */         }
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 736 */         LOG.warn(listener + " failed on scan end for cycle " + cycle, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface Listener {}
/*     */   
/*     */   public static abstract interface ScanListener
/*     */     extends Listener
/*     */   {
/*     */     public abstract void scan();
/*     */   }
/*     */   
/*     */   public static abstract interface DiscreteListener
/*     */     extends Listener
/*     */   {
/*     */     public abstract void fileChanged(String paramString)
/*     */       throws Exception;
/*     */     
/*     */     public abstract void fileAdded(String paramString)
/*     */       throws Exception;
/*     */     
/*     */     public abstract void fileRemoved(String paramString)
/*     */       throws Exception;
/*     */   }
/*     */   
/*     */   public static abstract interface BulkListener
/*     */     extends Listener
/*     */   {
/*     */     public abstract void filesChanged(List<String> paramList)
/*     */       throws Exception;
/*     */   }
/*     */   
/*     */   public static abstract interface ScanCycleListener
/*     */     extends Listener
/*     */   {
/*     */     public abstract void scanStarted(int paramInt)
/*     */       throws Exception;
/*     */     
/*     */     public abstract void scanEnded(int paramInt)
/*     */       throws Exception;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Scanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */