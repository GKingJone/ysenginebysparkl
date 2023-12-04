/*     */ package com.mchange.v2.c3p0.stmt;
/*     */ 
/*     */ import com.mchange.v1.db.sql.StatementUtils;
/*     */ import com.mchange.v2.async.AsynchronousRunner;
/*     */ import com.mchange.v2.io.IndentedWriter;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import com.mchange.v2.util.ResourceClosedException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ public abstract class GooGooStatementCache
/*     */ {
/*  42 */   private static final MLogger logger = MLog.getLogger(GooGooStatementCache.class);
/*     */   
/*     */ 
/*     */   private static final int DESTROY_NEVER = 0;
/*     */   
/*     */ 
/*     */   private static final int DESTROY_IF_CHECKED_IN = 1;
/*     */   
/*     */ 
/*     */   private static final int DESTROY_IF_CHECKED_OUT = 2;
/*     */   
/*     */   private static final int DESTROY_ALWAYS = 3;
/*     */   
/*     */   ConnectionStatementManager cxnStmtMgr;
/*     */   
/*  57 */   HashMap stmtToKey = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  62 */   HashMap keyToKeyRec = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  67 */   HashSet checkedOut = new HashSet();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   AsynchronousRunner blockingTaskAsyncRunner;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  82 */   HashSet removalPending = new HashSet();
/*     */   
/*     */ 
/*     */ 
/*     */   public GooGooStatementCache(AsynchronousRunner blockingTaskAsyncRunner)
/*     */   {
/*  88 */     this.blockingTaskAsyncRunner = blockingTaskAsyncRunner;
/*  89 */     this.cxnStmtMgr = createConnectionStatementManager();
/*     */   }
/*     */   
/*     */   public synchronized int getNumStatements() {
/*  93 */     return isClosed() ? -1 : countCachedStatements();
/*     */   }
/*     */   
/*  96 */   public synchronized int getNumStatementsCheckedOut() { return isClosed() ? -1 : this.checkedOut.size(); }
/*     */   
/*     */   public synchronized int getNumConnectionsWithCachedStatements() {
/*  99 */     return isClosed() ? -1 : this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
/*     */   }
/*     */   
/*     */   public synchronized String dumpStatementCacheStatus() {
/* 103 */     if (isClosed()) {
/* 104 */       return this + "status: Closed.";
/*     */     }
/*     */     
/* 107 */     StringWriter sw = new StringWriter(2048);
/* 108 */     IndentedWriter iw = new IndentedWriter(sw);
/*     */     try
/*     */     {
/* 111 */       iw.print(this);
/* 112 */       iw.println(" status:");
/* 113 */       iw.upIndent();
/* 114 */       iw.println("core stats:");
/* 115 */       iw.upIndent();
/* 116 */       iw.print("num cached statements: ");
/* 117 */       iw.println(countCachedStatements());
/* 118 */       iw.print("num cached statements in use: ");
/* 119 */       iw.println(this.checkedOut.size());
/* 120 */       iw.print("num connections with cached statements: ");
/* 121 */       iw.println(this.cxnStmtMgr.getNumConnectionsWithCachedStatements());
/* 122 */       iw.downIndent();
/* 123 */       iw.println("cached statement dump:");
/* 124 */       iw.upIndent();
/* 125 */       for (Iterator ii = this.cxnStmtMgr.connectionSet().iterator(); ii.hasNext();)
/*     */       {
/* 127 */         Connection pcon = (Connection)ii.next();
/* 128 */         iw.print(pcon);
/* 129 */         iw.println(':');
/* 130 */         iw.upIndent();
/* 131 */         for (Iterator jj = this.cxnStmtMgr.statementSet(pcon).iterator(); jj.hasNext();)
/* 132 */           iw.println(jj.next());
/* 133 */         iw.downIndent();
/*     */       }
/*     */       
/* 136 */       iw.downIndent();
/* 137 */       iw.downIndent();
/* 138 */       return sw.toString();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 142 */       if (logger.isLoggable(MLevel.SEVERE))
/* 143 */         logger.log(MLevel.SEVERE, "Huh? We've seen an IOException writing to s StringWriter?!", e);
/* 144 */       return e.toString();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   abstract ConnectionStatementManager createConnectionStatementManager();
/*     */   
/*     */ 
/*     */   public synchronized Object checkoutStatement(Connection physicalConnection, Method stmtProducingMethod, Object[] args)
/*     */     throws SQLException, ResourceClosedException
/*     */   {
/*     */     try
/*     */     {
/* 158 */       Object out = null;
/*     */       
/* 160 */       StatementCacheKey key = StatementCacheKey.find(physicalConnection, stmtProducingMethod, args);
/*     */       
/*     */ 
/* 163 */       LinkedList l = checkoutQueue(key);
/* 164 */       if ((l == null) || (l.isEmpty()))
/*     */       {
/*     */ 
/*     */ 
/* 168 */         out = acquireStatement(physicalConnection, stmtProducingMethod, args);
/*     */         
/* 170 */         if (prepareAssimilateNewStatement(physicalConnection)) {
/* 171 */           assimilateNewCheckedOutStatement(key, physicalConnection, out);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 179 */         logger.finest(getClass().getName() + " ----> CACHE HIT");
/*     */         
/*     */ 
/* 182 */         out = l.get(0);
/* 183 */         l.remove(0);
/* 184 */         if (!this.checkedOut.add(out)) {
/* 185 */           throw new RuntimeException("Internal inconsistency: Checking out a statement marked as already checked out!");
/*     */         }
/*     */         
/* 188 */         removeStatementFromDeathmarches(out, physicalConnection);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 195 */       if (logger.isLoggable(MLevel.FINEST)) {
/* 196 */         logger.finest("checkoutStatement: " + statsString());
/*     */       }
/*     */       
/* 199 */       return out;
/*     */     }
/*     */     catch (NullPointerException npe)
/*     */     {
/* 203 */       if (this.checkedOut == null)
/*     */       {
/* 205 */         if (logger.isLoggable(MLevel.FINE)) {
/* 206 */           logger.log(MLevel.FINE, "A client attempted to work with a closed Statement cache, provoking a NullPointerException. c3p0 recovers, but this should be rare.", npe);
/*     */         }
/*     */         
/*     */ 
/* 210 */         throw new ResourceClosedException(npe);
/*     */       }
/*     */       
/* 213 */       throw npe;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void checkinStatement(Object pstmt)
/*     */     throws SQLException
/*     */   {
/* 220 */     if (this.checkedOut == null)
/*     */     {
/* 222 */       synchronousDestroyStatement(pstmt);
/*     */       
/* 224 */       return;
/*     */     }
/* 226 */     if (!this.checkedOut.remove(pstmt))
/*     */     {
/* 228 */       if (!ourResource(pstmt)) {
/* 229 */         destroyStatement(pstmt);
/*     */       }
/*     */       
/* 232 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 236 */       refreshStatement((PreparedStatement)pstmt);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/* 243 */       if (logger.isLoggable(MLevel.INFO)) {
/* 244 */         logger.log(MLevel.INFO, "Problem with checked-in Statement, discarding.", e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 251 */       this.checkedOut.add(pstmt);
/*     */       
/* 253 */       removeStatement(pstmt, 3);
/* 254 */       return;
/*     */     }
/*     */     
/* 257 */     StatementCacheKey key = (StatementCacheKey)this.stmtToKey.get(pstmt);
/* 258 */     if (key == null) {
/* 259 */       throw new RuntimeException("Internal inconsistency: A checked-out statement has no key associated with it!");
/*     */     }
/*     */     
/* 262 */     LinkedList l = checkoutQueue(key);
/* 263 */     l.add(pstmt);
/* 264 */     addStatementToDeathmarches(pstmt, key.physicalConnection);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 270 */     if (logger.isLoggable(MLevel.FINEST)) {
/* 271 */       logger.finest("checkinStatement(): " + statsString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void checkinAll(Connection pcon)
/*     */     throws SQLException
/*     */   {
/* 281 */     Set stmtSet = this.cxnStmtMgr.statementSet(pcon);
/* 282 */     Iterator ii; if (stmtSet != null)
/*     */     {
/* 284 */       for (ii = stmtSet.iterator(); ii.hasNext();)
/*     */       {
/* 286 */         Object stmt = ii.next();
/* 287 */         if (this.checkedOut.contains(stmt)) {
/* 288 */           checkinStatement(stmt);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 296 */     if (logger.isLoggable(MLevel.FINEST)) {
/* 297 */       logger.log(MLevel.FINEST, "checkinAll(): " + statsString());
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
/*     */   public void closeAll(Connection pcon)
/*     */     throws SQLException
/*     */   {
/* 313 */     if (!isClosed())
/*     */     {
/*     */ 
/*     */ 
/* 317 */       if (logger.isLoggable(MLevel.FINEST))
/*     */       {
/* 319 */         logger.log(MLevel.FINEST, "ENTER METHOD: closeAll( " + pcon + " )! -- num_connections: " + this.cxnStmtMgr.getNumConnectionsWithCachedStatements());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 325 */       Set stmtSet = null;
/* 326 */       Iterator ii; synchronized (this)
/*     */       {
/* 328 */         Set cSet = this.cxnStmtMgr.statementSet(pcon);
/*     */         
/* 330 */         if (cSet != null)
/*     */         {
/*     */ 
/* 333 */           stmtSet = new HashSet(cSet);
/*     */           
/*     */ 
/* 336 */           for (ii = stmtSet.iterator(); ii.hasNext();)
/*     */           {
/* 338 */             Object stmt = ii.next();
/*     */             
/*     */ 
/* 341 */             removeStatement(stmt, 0);
/*     */           }
/*     */         }
/*     */       }
/*     */       Iterator ii;
/* 346 */       if (stmtSet != null)
/*     */       {
/* 348 */         for (ii = stmtSet.iterator(); ii.hasNext();)
/*     */         {
/* 350 */           Object stmt = ii.next();
/* 351 */           synchronousDestroyStatement(stmt);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 357 */       if (logger.isLoggable(MLevel.FINEST)) {
/* 358 */         logger.finest("closeAll(): " + statsString());
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
/*     */ 
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/* 375 */     if (!isClosed())
/*     */     {
/* 377 */       for (Iterator ii = this.stmtToKey.keySet().iterator(); ii.hasNext();) {
/* 378 */         synchronousDestroyStatement(ii.next());
/*     */       }
/* 380 */       this.cxnStmtMgr = null;
/* 381 */       this.stmtToKey = null;
/* 382 */       this.keyToKeyRec = null;
/* 383 */       this.checkedOut = null;
/*     */ 
/*     */ 
/*     */     }
/* 387 */     else if (logger.isLoggable(MLevel.FINE)) {
/* 388 */       logger.log(MLevel.FINE, this + ": duplicate call to close() [not harmful! -- debug only!]", new Exception("DUPLICATE CLOSE DEBUG STACK TRACE."));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized boolean isClosed()
/*     */   {
/* 395 */     return this.cxnStmtMgr == null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void destroyStatement(final Object pstmt)
/*     */   {
/* 407 */     Runnable r = new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 404 */         StatementUtils.attemptClose((PreparedStatement)pstmt);
/*     */       }
/*     */       
/*     */ 
/* 408 */     };
/* 409 */     this.blockingTaskAsyncRunner.postRunnable(r);
/*     */   }
/*     */   
/*     */   private void synchronousDestroyStatement(Object pstmt) {
/* 413 */     StatementUtils.attemptClose((PreparedStatement)pstmt);
/*     */   }
/*     */   
/*     */ 
/*     */   abstract boolean prepareAssimilateNewStatement(Connection paramConnection);
/*     */   
/*     */ 
/*     */   abstract void addStatementToDeathmarches(Object paramObject, Connection paramConnection);
/*     */   
/*     */ 
/*     */   abstract void removeStatementFromDeathmarches(Object paramObject, Connection paramConnection);
/*     */   
/*     */   final int countCachedStatements()
/*     */   {
/* 427 */     return this.stmtToKey.size();
/*     */   }
/*     */   
/*     */ 
/*     */   private void assimilateNewCheckedOutStatement(StatementCacheKey key, Connection pConn, Object ps)
/*     */   {
/* 433 */     this.stmtToKey.put(ps, key);
/* 434 */     HashSet ks = keySet(key);
/* 435 */     if (ks == null) {
/* 436 */       this.keyToKeyRec.put(key, new KeyRec(null));
/*     */     }
/*     */     else
/*     */     {
/* 440 */       if (logger.isLoggable(MLevel.INFO))
/* 441 */         logger.info("Multiply prepared statement! " + key.stmtText);
/* 442 */       if (logger.isLoggable(MLevel.FINE)) {
/* 443 */         logger.fine("(The same statement has already been prepared by this Connection, and that other instance has not yet been closed, so the statement pool has to prepare a second PreparedStatement object rather than reusing the previously-cached Statement. The new Statement will be cached, in case you frequently need multiple copies of this Statement.)");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 449 */     keySet(key).add(ps);
/* 450 */     this.cxnStmtMgr.addStatementForConnection(ps, pConn);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 456 */     if (logger.isLoggable(MLevel.FINEST)) {
/* 457 */       logger.finest("cxnStmtMgr.statementSet( " + pConn + " ).size(): " + this.cxnStmtMgr.statementSet(pConn).size());
/*     */     }
/*     */     
/*     */ 
/* 461 */     this.checkedOut.add(ps);
/*     */   }
/*     */   
/*     */   private void removeStatement(Object ps, int destruction_policy)
/*     */   {
/* 466 */     synchronized (this.removalPending)
/*     */     {
/* 468 */       if (this.removalPending.contains(ps)) {
/* 469 */         return;
/*     */       }
/* 471 */       this.removalPending.add(ps);
/*     */     }
/*     */     
/* 474 */     StatementCacheKey sck = (StatementCacheKey)this.stmtToKey.remove(ps);
/* 475 */     removeFromKeySet(sck, ps);
/* 476 */     Connection pConn = sck.physicalConnection;
/*     */     
/* 478 */     boolean checked_in = !this.checkedOut.contains(ps);
/*     */     
/* 480 */     if (checked_in)
/*     */     {
/* 482 */       removeStatementFromDeathmarches(ps, pConn);
/* 483 */       removeFromCheckoutQueue(sck, ps);
/* 484 */       if ((destruction_policy & 0x1) != 0) {
/* 485 */         destroyStatement(ps);
/*     */       }
/*     */     }
/*     */     else {
/* 489 */       this.checkedOut.remove(ps);
/* 490 */       if ((destruction_policy & 0x2) != 0) {
/* 491 */         destroyStatement(ps);
/*     */       }
/*     */     }
/*     */     
/* 495 */     boolean check = this.cxnStmtMgr.removeStatementForConnection(ps, pConn);
/* 496 */     if (!check)
/*     */     {
/*     */ 
/* 499 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 500 */         logger.log(MLevel.WARNING, this + " removed a statement that apparently wasn't in a statement set!!!", new Exception("LOG STACK TRACE"));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 505 */     synchronized (this.removalPending) {
/* 506 */       this.removalPending.remove(ps);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private Object acquireStatement(final Connection pConn, final Method stmtProducingMethod, final Object[] args)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 516 */       final Object[] outHolder = new Object[1];
/* 517 */       final SQLException[] exceptionHolder = new SQLException[1];
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
/* 548 */       Runnable r = new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 525 */             outHolder[0] = stmtProducingMethod.invoke(pConn, args);
/*     */ 
/*     */           }
/*     */           catch (InvocationTargetException e)
/*     */           {
/*     */ 
/* 531 */             Throwable targetException = e.getTargetException();
/* 532 */             if ((targetException instanceof SQLException)) {
/* 533 */               exceptionHolder[0] = ((SQLException)targetException);
/*     */             } else {
/* 535 */               exceptionHolder[0] = SqlUtils.toSQLException(targetException);
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/* 539 */             exceptionHolder[0] = SqlUtils.toSQLException(e);
/*     */           }
/*     */           finally {
/* 542 */             synchronized (GooGooStatementCache.this) {
/* 543 */               GooGooStatementCache.this.notifyAll();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 548 */       };
/* 549 */       this.blockingTaskAsyncRunner.postRunnable(r);
/*     */       
/* 551 */       while ((outHolder[0] == null) && (exceptionHolder[0] == null))
/* 552 */         wait();
/* 553 */       if (exceptionHolder[0] != null) {
/* 554 */         throw exceptionHolder[0];
/*     */       }
/*     */       
/* 557 */       return outHolder[0];
/*     */ 
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 562 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/* 566 */   private KeyRec keyRec(StatementCacheKey key) { return (KeyRec)this.keyToKeyRec.get(key); }
/*     */   
/*     */   private HashSet keySet(StatementCacheKey key)
/*     */   {
/* 570 */     KeyRec rec = keyRec(key);
/* 571 */     return rec == null ? null : rec.allStmts;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean removeFromKeySet(StatementCacheKey key, Object pstmt)
/*     */   {
/* 577 */     HashSet stmtSet = keySet(key);
/* 578 */     boolean out = stmtSet.remove(pstmt);
/* 579 */     if ((stmtSet.isEmpty()) && (checkoutQueue(key).isEmpty()))
/* 580 */       this.keyToKeyRec.remove(key);
/* 581 */     return out;
/*     */   }
/*     */   
/*     */   private LinkedList checkoutQueue(StatementCacheKey key)
/*     */   {
/* 586 */     KeyRec rec = keyRec(key);
/* 587 */     return rec == null ? null : rec.checkoutQueue;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean removeFromCheckoutQueue(StatementCacheKey key, Object pstmt)
/*     */   {
/* 593 */     LinkedList q = checkoutQueue(key);
/* 594 */     boolean out = q.remove(pstmt);
/* 595 */     if ((q.isEmpty()) && (keySet(key).isEmpty()))
/* 596 */       this.keyToKeyRec.remove(key);
/* 597 */     return out;
/*     */   }
/*     */   
/*     */   private boolean ourResource(Object ps) {
/* 601 */     return this.stmtToKey.keySet().contains(ps);
/*     */   }
/*     */   
/* 604 */   private void refreshStatement(PreparedStatement ps) throws Exception { ps.clearParameters(); }
/*     */   
/*     */ 
/*     */   private void printStats()
/*     */   {
/* 609 */     int total_size = countCachedStatements();
/* 610 */     int checked_out_size = this.checkedOut.size();
/* 611 */     int num_connections = this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
/* 612 */     int num_keys = this.keyToKeyRec.size();
/* 613 */     System.err.print(getClass().getName() + " stats -- ");
/* 614 */     System.err.print("total size: " + total_size);
/* 615 */     System.err.print("; checked out: " + checked_out_size);
/* 616 */     System.err.print("; num connections: " + num_connections);
/* 617 */     System.err.println("; num keys: " + num_keys);
/*     */   }
/*     */   
/*     */   private String statsString()
/*     */   {
/* 622 */     int total_size = countCachedStatements();
/* 623 */     int checked_out_size = this.checkedOut.size();
/* 624 */     int num_connections = this.cxnStmtMgr.getNumConnectionsWithCachedStatements();
/* 625 */     int num_keys = this.keyToKeyRec.size();
/*     */     
/* 627 */     StringBuffer sb = new StringBuffer(255);
/* 628 */     sb.append(getClass().getName());
/* 629 */     sb.append(" stats -- ");
/* 630 */     sb.append("total size: ");
/* 631 */     sb.append(total_size);
/* 632 */     sb.append("; checked out: ");
/* 633 */     sb.append(checked_out_size);
/* 634 */     sb.append("; num connections: ");
/* 635 */     sb.append(num_connections);
/* 636 */     sb.append("; num keys: ");
/* 637 */     sb.append(num_keys);
/* 638 */     return sb.toString(); }
/*     */   
/*     */   private static class KeyRec { private KeyRec() {}
/*     */     
/* 642 */     KeyRec(GooGooStatementCache.1 x0) { this(); }
/*     */     
/* 644 */     HashSet allStmts = new HashSet();
/* 645 */     LinkedList checkoutQueue = new LinkedList();
/*     */   }
/*     */   
/*     */   protected class Deathmarch
/*     */   {
/* 650 */     TreeMap longsToStmts = new TreeMap();
/* 651 */     HashMap stmtsToLongs = new HashMap();
/*     */     
/* 653 */     long last_long = -1L;
/*     */     
/*     */ 
/*     */     protected Deathmarch() {}
/*     */     
/*     */     public void deathmarchStatement(Object ps)
/*     */     {
/* 660 */       Long old = (Long)this.stmtsToLongs.get(ps);
/* 661 */       if (old != null) {
/* 662 */         throw new RuntimeException("Internal inconsistency: A statement is being double-deathmatched. no checked-out statements should be in a deathmarch already; no already checked-in statement should be deathmarched!");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 667 */       Long youth = getNextLong();
/* 668 */       this.stmtsToLongs.put(ps, youth);
/* 669 */       this.longsToStmts.put(youth, ps);
/*     */     }
/*     */     
/*     */     public void undeathmarchStatement(Object ps)
/*     */     {
/* 674 */       Long old = (Long)this.stmtsToLongs.remove(ps);
/* 675 */       if (old == null) {
/* 676 */         throw new RuntimeException("Internal inconsistency: A (not new) checking-out statement is not in deathmarch.");
/*     */       }
/* 678 */       Object check = this.longsToStmts.remove(old);
/* 679 */       if (old == null) {
/* 680 */         throw new RuntimeException("Internal inconsistency: A (not new) checking-out statement is not in deathmarch.");
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean cullNext()
/*     */     {
/* 686 */       if (this.longsToStmts.isEmpty()) {
/* 687 */         return false;
/*     */       }
/*     */       
/* 690 */       Long l = (Long)this.longsToStmts.firstKey();
/* 691 */       Object ps = this.longsToStmts.get(l);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 696 */       if (GooGooStatementCache.logger.isLoggable(MLevel.FINEST)) {
/* 697 */         GooGooStatementCache.logger.finest("CULLING: " + ((StatementCacheKey)GooGooStatementCache.this.stmtToKey.get(ps)).stmtText);
/*     */       }
/*     */       
/*     */ 
/* 701 */       GooGooStatementCache.this.removeStatement(ps, 3);
/* 702 */       if (contains(ps))
/* 703 */         throw new RuntimeException("Inconsistency!!! Statement culled from deathmarch failed to be removed by removeStatement( ... )!");
/* 704 */       return true;
/*     */     }
/*     */     
/*     */     public boolean contains(Object ps)
/*     */     {
/* 709 */       return this.stmtsToLongs.keySet().contains(ps);
/*     */     }
/*     */     
/* 712 */     public int size() { return this.longsToStmts.size(); }
/*     */     
/*     */     private Long getNextLong() {
/* 715 */       return new Long(++this.last_long);
/*     */     }
/*     */   }
/*     */   
/*     */   protected static abstract class ConnectionStatementManager {
/* 720 */     Map cxnToStmtSets = new HashMap();
/*     */     
/*     */     public int getNumConnectionsWithCachedStatements() {
/* 723 */       return this.cxnToStmtSets.size();
/*     */     }
/*     */     
/* 726 */     public Set connectionSet() { return this.cxnToStmtSets.keySet(); }
/*     */     
/*     */     public Set statementSet(Connection pcon) {
/* 729 */       return (Set)this.cxnToStmtSets.get(pcon);
/*     */     }
/*     */     
/*     */     public int getNumStatementsForConnection(Connection pcon) {
/* 733 */       Set stmtSet = statementSet(pcon);
/* 734 */       return stmtSet == null ? 0 : stmtSet.size();
/*     */     }
/*     */     
/*     */     public void addStatementForConnection(Object ps, Connection pcon)
/*     */     {
/* 739 */       Set stmtSet = statementSet(pcon);
/* 740 */       if (stmtSet == null)
/*     */       {
/* 742 */         stmtSet = new HashSet();
/* 743 */         this.cxnToStmtSets.put(pcon, stmtSet);
/*     */       }
/* 745 */       stmtSet.add(ps);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean removeStatementForConnection(Object ps, Connection pcon)
/*     */     {
/* 752 */       Set stmtSet = statementSet(pcon);
/* 753 */       boolean out; if (stmtSet != null)
/*     */       {
/* 755 */         boolean out = stmtSet.remove(ps);
/* 756 */         if (stmtSet.isEmpty()) {
/* 757 */           this.cxnToStmtSets.remove(pcon);
/*     */         }
/*     */       } else {
/* 760 */         out = false;
/*     */       }
/* 762 */       return out;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static final class SimpleConnectionStatementManager
/*     */     extends ConnectionStatementManager
/*     */   {}
/*     */   
/*     */   protected final class DeathmarchConnectionStatementManager
/*     */     extends ConnectionStatementManager
/*     */   {
/* 773 */     Map cxnsToDms = new HashMap();
/*     */     
/*     */     protected DeathmarchConnectionStatementManager() {}
/*     */     
/* 777 */     public void addStatementForConnection(Object ps, Connection pcon) { super.addStatementForConnection(ps, pcon);
/* 778 */       Deathmarch dm = (Deathmarch)this.cxnsToDms.get(pcon);
/* 779 */       if (dm == null)
/*     */       {
/* 781 */         dm = new Deathmarch(GooGooStatementCache.this);
/* 782 */         this.cxnsToDms.put(pcon, dm);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean removeStatementForConnection(Object ps, Connection pcon)
/*     */     {
/* 788 */       boolean out = super.removeStatementForConnection(ps, pcon);
/* 789 */       if (out)
/*     */       {
/* 791 */         if (statementSet(pcon) == null)
/* 792 */           this.cxnsToDms.remove(pcon);
/*     */       }
/* 794 */       return out;
/*     */     }
/*     */     
/*     */     public Deathmarch getDeathmarch(Connection pcon) {
/* 798 */       return (Deathmarch)this.cxnsToDms.get(pcon);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\GooGooStatementCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */