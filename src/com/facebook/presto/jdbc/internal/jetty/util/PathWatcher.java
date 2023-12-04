/*      */ package com.facebook.presto.jdbc.internal.jetty.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.nio.file.ClosedWatchServiceException;
/*      */ import java.nio.file.FileSystem;
/*      */ import java.nio.file.FileSystems;
/*      */ import java.nio.file.FileVisitResult;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.LinkOption;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.PathMatcher;
/*      */ import java.nio.file.SimpleFileVisitor;
/*      */ import java.nio.file.StandardWatchEventKinds;
/*      */ import java.nio.file.WatchEvent;
/*      */ import java.nio.file.WatchEvent.Kind;
/*      */ import java.nio.file.WatchEvent.Modifier;
/*      */ import java.nio.file.WatchKey;
/*      */ import java.nio.file.WatchService;
/*      */ import java.nio.file.attribute.BasicFileAttributes;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.EventListener;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PathWatcher
/*      */   extends AbstractLifeCycle
/*      */   implements Runnable
/*      */ {
/*      */   private static final boolean IS_WINDOWS;
/*      */   
/*      */   public static class Config
/*      */   {
/*      */     public static final int UNLIMITED_DEPTH = -9999;
/*      */     private static final String PATTERN_SEP;
/*      */     protected final Path dir;
/*      */     
/*      */     static
/*      */     {
/*   79 */       String sep = File.separator;
/*   80 */       if (File.separatorChar == '\\')
/*      */       {
/*   82 */         sep = "\\\\";
/*      */       }
/*   84 */       PATTERN_SEP = sep;
/*      */     }
/*      */     
/*      */ 
/*   88 */     protected int recurseDepth = 0;
/*      */     protected List<PathMatcher> includes;
/*      */     protected List<PathMatcher> excludes;
/*   91 */     protected boolean excludeHidden = false;
/*      */     
/*      */     public Config(Path path)
/*      */     {
/*   95 */       this.dir = path;
/*   96 */       this.includes = new ArrayList();
/*   97 */       this.excludes = new ArrayList();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addExclude(PathMatcher matcher)
/*      */     {
/*  108 */       this.excludes.add(matcher);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addExclude(String syntaxAndPattern)
/*      */     {
/*  122 */       if (PathWatcher.LOG.isDebugEnabled())
/*      */       {
/*  124 */         PathWatcher.LOG.debug("Adding exclude: [{}]", new Object[] { syntaxAndPattern });
/*      */       }
/*  126 */       addExclude(this.dir.getFileSystem().getPathMatcher(syntaxAndPattern));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addExcludeGlobRelative(String pattern)
/*      */     {
/*  148 */       addExclude(toGlobPattern(this.dir, pattern));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addExcludeHidden()
/*      */     {
/*  156 */       if (!this.excludeHidden)
/*      */       {
/*  158 */         if (PathWatcher.LOG.isDebugEnabled())
/*      */         {
/*  160 */           PathWatcher.LOG.debug("Adding hidden files and directories to exclusions", new Object[0]);
/*      */         }
/*  162 */         this.excludeHidden = true;
/*      */         
/*  164 */         addExclude("regex:^.*" + PATTERN_SEP + "\\..*$");
/*  165 */         addExclude("regex:^.*" + PATTERN_SEP + "\\..*" + PATTERN_SEP + ".*$");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addExcludes(List<String> syntaxAndPatterns)
/*      */     {
/*  178 */       for (String syntaxAndPattern : syntaxAndPatterns)
/*      */       {
/*  180 */         addExclude(syntaxAndPattern);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addInclude(PathMatcher matcher)
/*      */     {
/*  192 */       this.includes.add(matcher);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addInclude(String syntaxAndPattern)
/*      */     {
/*  204 */       if (PathWatcher.LOG.isDebugEnabled())
/*      */       {
/*  206 */         PathWatcher.LOG.debug("Adding include: [{}]", new Object[] { syntaxAndPattern });
/*      */       }
/*  208 */       addInclude(this.dir.getFileSystem().getPathMatcher(syntaxAndPattern));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addIncludeGlobRelative(String pattern)
/*      */     {
/*  230 */       addInclude(toGlobPattern(this.dir, pattern));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void addIncludes(List<String> syntaxAndPatterns)
/*      */     {
/*  242 */       for (String syntaxAndPattern : syntaxAndPatterns)
/*      */       {
/*  244 */         addInclude(syntaxAndPattern);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Config asSubConfig(Path dir)
/*      */     {
/*  259 */       Config subconfig = new Config(dir);
/*  260 */       subconfig.includes = this.includes;
/*  261 */       subconfig.excludes = this.excludes;
/*  262 */       if (dir == this.dir) {
/*  263 */         subconfig.recurseDepth = this.recurseDepth;
/*      */ 
/*      */       }
/*  266 */       else if (this.recurseDepth == 55537) {
/*  267 */         subconfig.recurseDepth = 55537;
/*      */       } else {
/*  269 */         this.recurseDepth -= dir.getNameCount() - this.dir.getNameCount();
/*      */       }
/*  271 */       return subconfig;
/*      */     }
/*      */     
/*      */     public int getRecurseDepth()
/*      */     {
/*  276 */       return this.recurseDepth;
/*      */     }
/*      */     
/*      */     public boolean isRecurseDepthUnlimited()
/*      */     {
/*  281 */       return this.recurseDepth == 55537;
/*      */     }
/*      */     
/*      */     public Path getPath()
/*      */     {
/*  286 */       return this.dir;
/*      */     }
/*      */     
/*      */     private boolean hasMatch(Path path, List<PathMatcher> matchers)
/*      */     {
/*  291 */       for (PathMatcher matcher : matchers)
/*      */       {
/*  293 */         if (matcher.matches(path))
/*      */         {
/*  295 */           return true;
/*      */         }
/*      */       }
/*  298 */       return false;
/*      */     }
/*      */     
/*      */     public boolean isExcluded(Path dir) throws IOException
/*      */     {
/*  303 */       if (this.excludeHidden)
/*      */       {
/*  305 */         if (Files.isHidden(dir))
/*      */         {
/*  307 */           if (PathWatcher.NOISY_LOG.isDebugEnabled())
/*      */           {
/*  309 */             PathWatcher.NOISY_LOG.debug("isExcluded [Hidden] on {}", new Object[] { dir });
/*      */           }
/*  311 */           return true;
/*      */         }
/*      */       }
/*      */       
/*  315 */       if (this.excludes.isEmpty())
/*      */       {
/*      */ 
/*  318 */         return false;
/*      */       }
/*      */       
/*  321 */       boolean matched = hasMatch(dir, this.excludes);
/*  322 */       if (PathWatcher.NOISY_LOG.isDebugEnabled())
/*      */       {
/*  324 */         PathWatcher.NOISY_LOG.debug("isExcluded [{}] on {}", new Object[] { Boolean.valueOf(matched), dir });
/*      */       }
/*  326 */       return matched;
/*      */     }
/*      */     
/*      */     public boolean isIncluded(Path dir)
/*      */     {
/*  331 */       if (this.includes.isEmpty())
/*      */       {
/*      */ 
/*  334 */         if (PathWatcher.NOISY_LOG.isDebugEnabled())
/*      */         {
/*  336 */           PathWatcher.NOISY_LOG.debug("isIncluded [All] on {}", new Object[] { dir });
/*      */         }
/*  338 */         return true;
/*      */       }
/*      */       
/*  341 */       boolean matched = hasMatch(dir, this.includes);
/*  342 */       if (PathWatcher.NOISY_LOG.isDebugEnabled())
/*      */       {
/*  344 */         PathWatcher.NOISY_LOG.debug("isIncluded [{}] on {}", new Object[] { Boolean.valueOf(matched), dir });
/*      */       }
/*  346 */       return matched;
/*      */     }
/*      */     
/*      */     public boolean matches(Path path)
/*      */     {
/*      */       try
/*      */       {
/*  353 */         return (!isExcluded(path)) && (isIncluded(path));
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  357 */         PathWatcher.LOG.warn("Unable to match path: " + path, e); }
/*  358 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setRecurseDepth(int depth)
/*      */     {
/*  372 */       this.recurseDepth = depth;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean shouldRecurseDirectory(Path child)
/*      */     {
/*  386 */       if (!child.startsWith(this.dir))
/*      */       {
/*      */ 
/*  389 */         return false;
/*      */       }
/*      */       
/*      */ 
/*  393 */       if (isRecurseDepthUnlimited()) {
/*  394 */         return true;
/*      */       }
/*      */       
/*  397 */       int childDepth = this.dir.relativize(child).getNameCount();
/*  398 */       return childDepth <= this.recurseDepth;
/*      */     }
/*      */     
/*      */     private String toGlobPattern(Path path, String subPattern)
/*      */     {
/*  403 */       StringBuilder s = new StringBuilder();
/*  404 */       s.append("glob:");
/*      */       
/*  406 */       boolean needDelim = false;
/*      */       
/*      */ 
/*  409 */       Path root = path.getRoot();
/*  410 */       if (root != null)
/*      */       {
/*  412 */         if (PathWatcher.NOISY_LOG.isDebugEnabled())
/*      */         {
/*  414 */           PathWatcher.NOISY_LOG.debug("Path: {} -> Root: {}", new Object[] { path, root });
/*      */         }
/*  416 */         for (char c : root.toString().toCharArray())
/*      */         {
/*  418 */           if (c == '\\')
/*      */           {
/*  420 */             s.append(PATTERN_SEP);
/*      */           }
/*      */           else
/*      */           {
/*  424 */             s.append(c);
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  430 */         needDelim = true;
/*      */       }
/*      */       
/*      */ 
/*  434 */       for (??? = path.iterator(); ((Iterator)???).hasNext();) { segment = (Path)((Iterator)???).next();
/*      */         
/*  436 */         if (needDelim)
/*      */         {
/*  438 */           s.append(PATTERN_SEP);
/*      */         }
/*  440 */         s.append(segment);
/*  441 */         needDelim = true;
/*      */       }
/*      */       
/*      */       Path segment;
/*  445 */       if ((subPattern != null) && (subPattern.length() > 0))
/*      */       {
/*  447 */         if (needDelim)
/*      */         {
/*  449 */           s.append(PATTERN_SEP);
/*      */         }
/*  451 */         for (char c : subPattern.toCharArray())
/*      */         {
/*  453 */           if (c == '/')
/*      */           {
/*  455 */             s.append(PATTERN_SEP);
/*      */           }
/*      */           else
/*      */           {
/*  459 */             s.append(c);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  464 */       return s.toString();
/*      */     }
/*      */     
/*      */ 
/*      */     public String toString()
/*      */     {
/*  470 */       StringBuilder s = new StringBuilder();
/*  471 */       s.append(this.dir);
/*  472 */       if (this.recurseDepth > 0)
/*      */       {
/*  474 */         s.append(" [depth=").append(this.recurseDepth).append("]");
/*      */       }
/*  476 */       return s.toString();
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DepthLimitedFileVisitor extends SimpleFileVisitor<Path>
/*      */   {
/*      */     private Config base;
/*      */     private PathWatcher watcher;
/*      */     
/*      */     public DepthLimitedFileVisitor(PathWatcher watcher, Config base)
/*      */     {
/*  487 */       this.base = base;
/*  488 */       this.watcher = watcher;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
/*      */       throws IOException
/*      */     {
/*  518 */       if (!this.base.isExcluded(dir))
/*      */       {
/*  520 */         if (this.base.isIncluded(dir))
/*      */         {
/*  522 */           if (this.watcher.isNotifiable())
/*      */           {
/*      */ 
/*      */ 
/*  526 */             PathWatchEvent event = new PathWatchEvent(dir, PathWatchEventType.ADDED);
/*  527 */             if (PathWatcher.LOG.isDebugEnabled())
/*      */             {
/*  529 */               PathWatcher.LOG.debug("Pending {}", new Object[] { event });
/*      */             }
/*  531 */             this.watcher.addToPendingList(dir, event);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  540 */         if (((this.base.getPath().equals(dir)) && ((this.base.isRecurseDepthUnlimited()) || (this.base.getRecurseDepth() >= 0))) || (this.base.shouldRecurseDirectory(dir))) {
/*  541 */           this.watcher.register(dir, this.base);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  549 */       if (((this.base.getPath().equals(dir)) && ((this.base.isRecurseDepthUnlimited()) || (this.base.getRecurseDepth() >= 0))) || (this.base.shouldRecurseDirectory(dir))) {
/*  550 */         return FileVisitResult.CONTINUE;
/*      */       }
/*  552 */       return FileVisitResult.SKIP_SUBTREE;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
/*      */       throws IOException
/*      */     {
/*  562 */       if ((this.base.matches(file)) && (this.watcher.isNotifiable()))
/*      */       {
/*  564 */         PathWatchEvent event = new PathWatchEvent(file, PathWatchEventType.ADDED);
/*  565 */         if (PathWatcher.LOG.isDebugEnabled())
/*      */         {
/*  567 */           PathWatcher.LOG.debug("Pending {}", new Object[] { event });
/*      */         }
/*  569 */         this.watcher.addToPendingList(file, event);
/*      */       }
/*      */       
/*  572 */       return FileVisitResult.CONTINUE;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface Listener
/*      */     extends EventListener
/*      */   {
/*      */     public abstract void onPathWatchEvent(PathWatchEvent paramPathWatchEvent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static abstract interface EventListListener
/*      */     extends EventListener
/*      */   {
/*      */     public abstract void onPathWatchEvents(List<PathWatchEvent> paramList);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class PathWatchEvent
/*      */   {
/*      */     private final Path path;
/*      */     
/*      */ 
/*      */     private final PathWatchEventType type;
/*      */     
/*      */ 
/*  604 */     private int count = 0;
/*      */     
/*      */     public PathWatchEvent(Path path, PathWatchEventType type)
/*      */     {
/*  608 */       this.path = path;
/*  609 */       this.count = 1;
/*  610 */       this.type = type;
/*      */     }
/*      */     
/*      */ 
/*      */     public PathWatchEvent(Path path, WatchEvent<Path> event)
/*      */     {
/*  616 */       this.path = path;
/*  617 */       this.count = event.count();
/*  618 */       if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
/*      */       {
/*  620 */         this.type = PathWatchEventType.ADDED;
/*      */       }
/*  622 */       else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
/*      */       {
/*  624 */         this.type = PathWatchEventType.DELETED;
/*      */       }
/*  626 */       else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
/*      */       {
/*  628 */         this.type = PathWatchEventType.MODIFIED;
/*      */       }
/*      */       else
/*      */       {
/*  632 */         this.type = PathWatchEventType.UNKNOWN;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  642 */       if (this == obj)
/*      */       {
/*  644 */         return true;
/*      */       }
/*  646 */       if (obj == null)
/*      */       {
/*  648 */         return false;
/*      */       }
/*  650 */       if (getClass() != obj.getClass())
/*      */       {
/*  652 */         return false;
/*      */       }
/*  654 */       PathWatchEvent other = (PathWatchEvent)obj;
/*  655 */       if (this.path == null)
/*      */       {
/*  657 */         if (other.path != null)
/*      */         {
/*  659 */           return false;
/*      */         }
/*      */       }
/*  662 */       else if (!this.path.equals(other.path))
/*      */       {
/*  664 */         return false;
/*      */       }
/*  666 */       if (this.type != other.type)
/*      */       {
/*  668 */         return false;
/*      */       }
/*  670 */       return true;
/*      */     }
/*      */     
/*      */     public Path getPath()
/*      */     {
/*  675 */       return this.path;
/*      */     }
/*      */     
/*      */     public PathWatchEventType getType()
/*      */     {
/*  680 */       return this.type;
/*      */     }
/*      */     
/*      */     public void incrementCount(int num)
/*      */     {
/*  685 */       this.count += num;
/*      */     }
/*      */     
/*      */     public int getCount()
/*      */     {
/*  690 */       return this.count;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  699 */       int prime = 31;
/*  700 */       int result = 1;
/*  701 */       result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
/*  702 */       result = 31 * result + (this.type == null ? 0 : this.type.hashCode());
/*  703 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String toString()
/*      */     {
/*  712 */       return String.format("PathWatchEvent[%s|%s]", new Object[] { this.type, this.path });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class PathPendingEvents
/*      */   {
/*      */     private Path _path;
/*      */     
/*      */ 
/*      */     private List<PathWatchEvent> _events;
/*      */     
/*      */ 
/*      */     private long _timestamp;
/*      */     
/*      */ 
/*  730 */     private long _lastFileSize = -1L;
/*      */     
/*      */     public PathPendingEvents(Path path)
/*      */     {
/*  734 */       this._path = path;
/*      */     }
/*      */     
/*      */     public PathPendingEvents(Path path, PathWatchEvent event)
/*      */     {
/*  739 */       this(path);
/*  740 */       addEvent(event);
/*      */     }
/*      */     
/*      */     public void addEvent(PathWatchEvent event)
/*      */     {
/*  745 */       long now = System.currentTimeMillis();
/*  746 */       this._timestamp = now;
/*      */       
/*  748 */       if (this._events == null)
/*      */       {
/*  750 */         this._events = new ArrayList();
/*  751 */         this._events.add(event);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  757 */         PathWatchEvent existingType = null;
/*  758 */         for (PathWatchEvent e : this._events)
/*      */         {
/*  760 */           if (e.getType() == event.getType())
/*      */           {
/*  762 */             existingType = e;
/*  763 */             break;
/*      */           }
/*      */         }
/*      */         
/*  767 */         if (existingType == null)
/*      */         {
/*  769 */           this._events.add(event);
/*      */         }
/*      */         else
/*      */         {
/*  773 */           existingType.incrementCount(event.getCount());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public List<PathWatchEvent> getEvents()
/*      */     {
/*  781 */       return this._events;
/*      */     }
/*      */     
/*      */     public long getTimestamp()
/*      */     {
/*  786 */       return this._timestamp;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean isQuiet(long now, long expiredDuration, TimeUnit expiredUnit)
/*      */     {
/*  808 */       long pastdue = this._timestamp + expiredUnit.toMillis(expiredDuration);
/*  809 */       this._timestamp = now;
/*      */       
/*  811 */       long fileSize = this._path.toFile().length();
/*  812 */       boolean fileSizeChanged = this._lastFileSize != fileSize;
/*  813 */       this._lastFileSize = fileSize;
/*      */       
/*  815 */       if ((now > pastdue) && (!fileSizeChanged))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  820 */         return true;
/*      */       }
/*      */       
/*  823 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static enum PathWatchEventType
/*      */   {
/*  835 */     ADDED,  DELETED,  MODIFIED,  UNKNOWN;
/*      */     
/*      */     private PathWatchEventType() {}
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  842 */     String os = System.getProperty("os.name");
/*  843 */     if (os == null)
/*      */     {
/*  845 */       IS_WINDOWS = false;
/*      */     }
/*      */     else
/*      */     {
/*  849 */       String osl = os.toLowerCase(Locale.ENGLISH);
/*  850 */       IS_WINDOWS = osl.contains("windows");
/*      */     }
/*      */   }
/*      */   
/*  854 */   private static final Logger LOG = Log.getLogger(PathWatcher.class);
/*      */   
/*      */ 
/*      */ 
/*  858 */   private static final Logger NOISY_LOG = Log.getLogger(PathWatcher.class.getName() + ".Noisy");
/*      */   
/*      */ 
/*      */   protected static <T> WatchEvent<T> cast(WatchEvent<?> event)
/*      */   {
/*  863 */     return event;
/*      */   }
/*      */   
/*  866 */   private static final Kind<?>[] WATCH_EVENT_KINDS = { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY };
/*      */   
/*      */   private WatchService watchService;
/*      */   
/*      */   private Modifier[] watchModifiers;
/*      */   private boolean nativeWatchService;
/*  872 */   private Map<WatchKey, Config> keys = new HashMap();
/*  873 */   private List<EventListener> listeners = new CopyOnWriteArrayList();
/*  874 */   private List<Config> configs = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  879 */   private long updateQuietTimeDuration = 1000L;
/*  880 */   private TimeUnit updateQuietTimeUnit = TimeUnit.MILLISECONDS;
/*      */   private Thread thread;
/*  882 */   private boolean _notifyExistingOnStart = true;
/*  883 */   private Map<Path, PathPendingEvents> pendingEvents = new LinkedHashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void watch(Path file)
/*      */   {
/*  906 */     Path abs = file;
/*  907 */     if (!abs.isAbsolute())
/*      */     {
/*  909 */       abs = file.toAbsolutePath();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  914 */     Config config = null;
/*  915 */     Path parent = abs.getParent();
/*  916 */     for (Config c : this.configs)
/*      */     {
/*  918 */       if (c.getPath().equals(parent))
/*      */       {
/*  920 */         config = c;
/*  921 */         break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  926 */     if (config == null)
/*      */     {
/*  928 */       config = new Config(abs.getParent());
/*      */       
/*  930 */       config.addIncludeGlobRelative("");
/*      */       
/*  932 */       config.addIncludeGlobRelative(file.getFileName().toString());
/*  933 */       watch(config);
/*      */     }
/*      */     else
/*      */     {
/*  937 */       config.addIncludeGlobRelative(file.getFileName().toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void watch(Config config)
/*      */   {
/*  949 */     this.configs.add(config);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void prepareConfig(Config baseDir)
/*      */     throws IOException
/*      */   {
/*  961 */     if (LOG.isDebugEnabled())
/*      */     {
/*  963 */       LOG.debug("Watching directory {}", new Object[] { baseDir });
/*      */     }
/*  965 */     Files.walkFileTree(baseDir.getPath(), new DepthLimitedFileVisitor(this, baseDir));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(EventListener listener)
/*      */   {
/*  977 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void appendConfigId(StringBuilder s)
/*      */   {
/*  987 */     List<Path> dirs = new ArrayList();
/*      */     
/*  989 */     for (Config config : this.keys.values())
/*      */     {
/*  991 */       dirs.add(config.dir);
/*      */     }
/*      */     
/*  994 */     Collections.sort(dirs);
/*      */     
/*  996 */     s.append("[");
/*  997 */     if (dirs.size() > 0)
/*      */     {
/*  999 */       s.append(dirs.get(0));
/* 1000 */       if (dirs.size() > 1)
/*      */       {
/* 1002 */         s.append(" (+").append(dirs.size() - 1).append(")");
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1007 */       s.append("<null>");
/*      */     }
/* 1009 */     s.append("]");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void doStart()
/*      */     throws Exception
/*      */   {
/* 1019 */     createWatchService();
/*      */     
/*      */ 
/* 1022 */     setUpdateQuietTime(getUpdateQuietTimeMillis(), TimeUnit.MILLISECONDS);
/*      */     
/*      */ 
/*      */ 
/* 1026 */     for (Config c : this.configs) {
/* 1027 */       prepareConfig(c);
/*      */     }
/*      */     
/* 1030 */     StringBuilder threadId = new StringBuilder();
/* 1031 */     threadId.append("PathWatcher-Thread");
/* 1032 */     appendConfigId(threadId);
/*      */     
/* 1034 */     this.thread = new Thread(this, threadId.toString());
/* 1035 */     this.thread.setDaemon(true);
/* 1036 */     this.thread.start();
/* 1037 */     super.doStart();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void doStop()
/*      */     throws Exception
/*      */   {
/* 1046 */     if (this.watchService != null)
/* 1047 */       this.watchService.close();
/* 1048 */     this.watchService = null;
/* 1049 */     this.thread = null;
/* 1050 */     this.keys.clear();
/* 1051 */     this.pendingEvents.clear();
/* 1052 */     super.doStop();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1061 */     if (!isStopped()) {
/* 1062 */       throw new IllegalStateException("PathWatcher must be stopped before reset.");
/*      */     }
/* 1064 */     this.configs.clear();
/* 1065 */     this.listeners.clear();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void createWatchService()
/*      */     throws IOException
/*      */   {
/* 1078 */     this.watchService = FileSystems.getDefault().newWatchService();
/*      */     
/* 1080 */     Modifier[] modifiers = null;
/* 1081 */     boolean nativeService = true;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1086 */       ClassLoader cl = Thread.currentThread().getContextClassLoader();
/* 1087 */       Class<?> pollingWatchServiceClass = Class.forName("sun.nio.fs.PollingWatchService", false, cl);
/* 1088 */       if (pollingWatchServiceClass.isAssignableFrom(this.watchService.getClass()))
/*      */       {
/* 1090 */         nativeService = false;
/* 1091 */         LOG.info("Using Non-Native Java {}", new Object[] { pollingWatchServiceClass.getName() });
/* 1092 */         Class<?> c = Class.forName("com.sun.nio.file.SensitivityWatchEventModifier");
/* 1093 */         Field f = c.getField("HIGH");
/*      */         
/*      */ 
/* 1096 */         modifiers = new Modifier[] {(Modifier)f.get(c) };
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1103 */       LOG.ignore(t);
/*      */     }
/*      */     
/* 1106 */     this.watchModifiers = modifiers;
/* 1107 */     this.nativeWatchService = nativeService;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isNotifiable()
/*      */   {
/* 1119 */     return (isStarted()) || ((!isStarted()) && (isNotifyExistingOnStart()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Iterator<EventListener> getListeners()
/*      */   {
/* 1129 */     return this.listeners.iterator();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getUpdateQuietTimeMillis()
/*      */   {
/* 1139 */     return TimeUnit.MILLISECONDS.convert(this.updateQuietTimeDuration, this.updateQuietTimeUnit);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void notifyOnPathWatchEvents(List<PathWatchEvent> events)
/*      */   {
/* 1149 */     if ((events == null) || (events.isEmpty())) {
/* 1150 */       return;
/*      */     }
/* 1152 */     for (EventListener listener : this.listeners)
/*      */     {
/* 1154 */       if ((listener instanceof EventListListener))
/*      */       {
/*      */         try
/*      */         {
/* 1158 */           ((EventListListener)listener).onPathWatchEvents(events);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/* 1162 */           LOG.warn(t);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1167 */         l = (Listener)listener;
/* 1168 */         for (PathWatchEvent event : events)
/*      */         {
/*      */           try
/*      */           {
/* 1172 */             l.onPathWatchEvent(event);
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/* 1176 */             LOG.warn(t);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     Listener l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void register(Path dir, Config root)
/*      */     throws IOException
/*      */   {
/* 1194 */     LOG.debug("Registering watch on {}", new Object[] { dir });
/* 1195 */     if (this.watchModifiers != null)
/*      */     {
/*      */ 
/* 1198 */       WatchKey key = dir.register(this.watchService, WATCH_EVENT_KINDS, this.watchModifiers);
/* 1199 */       this.keys.put(key, root.asSubConfig(dir));
/*      */     }
/*      */     else
/*      */     {
/* 1203 */       WatchKey key = dir.register(this.watchService, WATCH_EVENT_KINDS);
/* 1204 */       this.keys.put(key, root.asSubConfig(dir));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean removeListener(Listener listener)
/*      */   {
/* 1216 */     return this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void run()
/*      */   {
/* 1241 */     List<PathWatchEvent> notifiableEvents = new ArrayList();
/*      */     
/*      */ 
/* 1244 */     if (LOG.isDebugEnabled())
/*      */     {
/* 1246 */       LOG.debug("Starting java.nio file watching with {}", new Object[] { this.watchService });
/*      */     }
/*      */     
/* 1249 */     while ((this.watchService != null) && (this.thread == Thread.currentThread()))
/*      */     {
/* 1251 */       WatchKey key = null;
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1256 */         if (this.pendingEvents.isEmpty())
/*      */         {
/* 1258 */           if (NOISY_LOG.isDebugEnabled())
/* 1259 */             NOISY_LOG.debug("Waiting for take()", new Object[0]);
/* 1260 */           key = this.watchService.take();
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1266 */           if (NOISY_LOG.isDebugEnabled()) {
/* 1267 */             NOISY_LOG.debug("Waiting for poll({}, {})", new Object[] { Long.valueOf(this.updateQuietTimeDuration), this.updateQuietTimeUnit });
/*      */           }
/* 1269 */           key = this.watchService.poll(this.updateQuietTimeDuration, this.updateQuietTimeUnit);
/*      */           
/*      */ 
/* 1272 */           if (key == null)
/*      */           {
/* 1274 */             now = System.currentTimeMillis();
/*      */             
/* 1276 */             for (Path path : new HashSet(this.pendingEvents.keySet()))
/*      */             {
/* 1278 */               PathPendingEvents pending = (PathPendingEvents)this.pendingEvents.get(path);
/* 1279 */               if (pending.isQuiet(now, this.updateQuietTimeDuration, this.updateQuietTimeUnit))
/*      */               {
/*      */ 
/*      */ 
/* 1283 */                 for (PathWatchEvent p : pending.getEvents())
/*      */                 {
/* 1285 */                   notifiableEvents.add(p);
/*      */                 }
/*      */                 
/* 1288 */                 this.pendingEvents.remove(path);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (ClosedWatchServiceException e)
/*      */       {
/*      */         long now;
/* 1297 */         return;
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/* 1301 */         if (isRunning())
/*      */         {
/* 1303 */           LOG.warn(e);
/*      */         }
/*      */         else
/*      */         {
/* 1307 */           LOG.ignore(e);
/*      */         }
/*      */         
/*      */         return;
/*      */       }
/*      */       Config config;
/* 1313 */       if (key != null)
/*      */       {
/*      */ 
/* 1316 */         config = (Config)this.keys.get(key);
/* 1317 */         if (config == null)
/*      */         {
/* 1319 */           if (LOG.isDebugEnabled())
/*      */           {
/* 1321 */             LOG.debug("WatchKey not recognized: {}", new Object[] { key });
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/* 1326 */           for (Object event : key.pollEvents())
/*      */           {
/*      */ 
/* 1329 */             Kind<Path> kind = ((WatchEvent)event).kind();
/* 1330 */             WatchEvent<Path> ev = cast((WatchEvent)event);
/* 1331 */             Path name = (Path)ev.context();
/* 1332 */             Path child = config.dir.resolve(name);
/*      */             
/* 1334 */             if (kind == StandardWatchEventKinds.ENTRY_CREATE)
/*      */             {
/*      */ 
/*      */ 
/* 1338 */               if (Files.isDirectory(child, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
/*      */               {
/*      */                 try
/*      */                 {
/* 1342 */                   prepareConfig(config.asSubConfig(child));
/*      */                 }
/*      */                 catch (IOException e)
/*      */                 {
/* 1346 */                   LOG.warn(e);
/*      */                 }
/*      */                 
/* 1349 */               } else if (config.matches(child))
/*      */               {
/* 1351 */                 addToPendingList(child, new PathWatchEvent(child, ev));
/*      */               }
/*      */             }
/* 1354 */             else if (config.matches(child))
/*      */             {
/* 1356 */               addToPendingList(child, new PathWatchEvent(child, ev));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 1362 */         notifyOnPathWatchEvents(notifiableEvents);
/* 1363 */         notifiableEvents.clear();
/*      */         
/* 1365 */         if ((key != null) && (!key.reset()))
/*      */         {
/* 1367 */           this.keys.remove(key);
/* 1368 */           if (this.keys.isEmpty())
/*      */           {
/* 1370 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addToPendingList(Path path, PathWatchEvent event)
/*      */   {
/* 1386 */     PathPendingEvents pending = (PathPendingEvents)this.pendingEvents.get(path);
/*      */     
/*      */ 
/* 1389 */     if (pending == null)
/*      */     {
/*      */ 
/* 1392 */       this.pendingEvents.put(path, new PathPendingEvents(path, event));
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1397 */       pending.addEvent(event);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNotifyExistingOnStart(boolean notify)
/*      */   {
/* 1410 */     this._notifyExistingOnStart = notify;
/*      */   }
/*      */   
/*      */   public boolean isNotifyExistingOnStart()
/*      */   {
/* 1415 */     return this._notifyExistingOnStart;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUpdateQuietTime(long duration, TimeUnit unit)
/*      */   {
/* 1426 */     long desiredMillis = unit.toMillis(duration);
/*      */     
/* 1428 */     if ((this.watchService != null) && (!this.nativeWatchService) && (desiredMillis < 5000L))
/*      */     {
/* 1430 */       LOG.warn("Quiet Time is too low for non-native WatchService [{}]: {} < 5000 ms (defaulting to 5000 ms)", new Object[] { this.watchService.getClass().getName(), Long.valueOf(desiredMillis) });
/* 1431 */       this.updateQuietTimeDuration = 5000L;
/* 1432 */       this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
/* 1433 */       return;
/*      */     }
/*      */     
/* 1436 */     if ((IS_WINDOWS) && (desiredMillis < 1000L))
/*      */     {
/* 1438 */       LOG.warn("Quiet Time is too low for Microsoft Windows: {} < 1000 ms (defaulting to 1000 ms)", new Object[] { Long.valueOf(desiredMillis) });
/* 1439 */       this.updateQuietTimeDuration = 1000L;
/* 1440 */       this.updateQuietTimeUnit = TimeUnit.MILLISECONDS;
/* 1441 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1445 */     this.updateQuietTimeDuration = duration;
/* 1446 */     this.updateQuietTimeUnit = unit;
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1452 */     StringBuilder s = new StringBuilder(getClass().getName());
/* 1453 */     appendConfigId(s);
/* 1454 */     return s.toString();
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\PathWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */