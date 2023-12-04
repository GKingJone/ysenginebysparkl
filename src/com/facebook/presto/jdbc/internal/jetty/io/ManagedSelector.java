/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Factory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Producer;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Rejectable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker.Lock;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler.Task;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ManagedSelector
/*     */   extends AbstractLifeCycle
/*     */   implements Runnable, Dumpable
/*     */ {
/*  59 */   private static final Logger LOG = Log.getLogger(ManagedSelector.class);
/*     */   
/*  61 */   private final Locker _locker = new Locker();
/*  62 */   private boolean _selecting = false;
/*  63 */   private final Queue<Runnable> _actions = new ArrayDeque();
/*     */   private final SelectorManager _selectorManager;
/*     */   private final int _id;
/*     */   private final ExecutionStrategy _strategy;
/*     */   private Selector _selector;
/*     */   
/*     */   public ManagedSelector(SelectorManager selectorManager, int id)
/*     */   {
/*  71 */     this._selectorManager = selectorManager;
/*  72 */     this._id = id;
/*  73 */     this._strategy = ExecutionStrategy.Factory.instanceFor(new SelectorProducer(null), selectorManager.getExecutor());
/*  74 */     setStopTimeout(5000L);
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  80 */     super.doStart();
/*  81 */     this._selector = newSelector();
/*     */   }
/*     */   
/*     */   protected Selector newSelector() throws IOException
/*     */   {
/*  86 */     return Selector.open();
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  91 */     Selector s = this._selector;
/*  92 */     if (s == null)
/*  93 */       return 0;
/*  94 */     return s.keys().size();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 100 */     if (LOG.isDebugEnabled())
/* 101 */       LOG.debug("Stopping {}", new Object[] { this });
/* 102 */     CloseEndPoints close_endps = new CloseEndPoints(null);
/* 103 */     submit(close_endps);
/* 104 */     close_endps.await(getStopTimeout());
/* 105 */     super.doStop();
/* 106 */     CloseSelector close_selector = new CloseSelector(null);
/* 107 */     submit(close_selector);
/* 108 */     close_selector.await(getStopTimeout());
/*     */     
/* 110 */     if (LOG.isDebugEnabled()) {
/* 111 */       LOG.debug("Stopped {}", new Object[] { this });
/*     */     }
/*     */   }
/*     */   
/*     */   public void submit(Runnable change) {
/* 116 */     if (LOG.isDebugEnabled()) {
/* 117 */       LOG.debug("Queued change {} on {}", new Object[] { change, this });
/*     */     }
/* 119 */     Selector selector = null;
/* 120 */     Locker.Lock lock = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 122 */       this._actions.offer(change);
/* 123 */       if (this._selecting)
/*     */       {
/* 125 */         selector = this._selector;
/*     */         
/* 127 */         this._selecting = false;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 120 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/* 129 */       if (lock != null) if (localThrowable3 != null) try { lock.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else lock.close(); }
/* 130 */     if (selector != null) {
/* 131 */       selector.wakeup();
/*     */     }
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 137 */     this._strategy.execute();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static abstract interface SelectableEndPoint
/*     */     extends EndPoint
/*     */   {
/*     */     public abstract Runnable onSelected();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public abstract void updateKey();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private class SelectorProducer
/*     */     implements ExecutionStrategy.Producer
/*     */   {
/* 163 */     private Set<SelectionKey> _keys = Collections.emptySet();
/* 164 */     private Iterator<SelectionKey> _cursor = Collections.emptyIterator();
/*     */     
/*     */     private SelectorProducer() {}
/*     */     
/*     */     public Runnable produce()
/*     */     {
/*     */       for (;;) {
/* 171 */         Runnable task = processSelected();
/* 172 */         if (task != null) {
/* 173 */           return task;
/*     */         }
/* 175 */         Runnable action = runActions();
/* 176 */         if (action != null) {
/* 177 */           return action;
/*     */         }
/* 179 */         update();
/*     */         
/* 181 */         if (!select()) {
/* 182 */           return null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     private Runnable runActions()
/*     */     {
/*     */       for (;;)
/*     */       {
/* 191 */         Locker.Lock lock = ManagedSelector.this._locker.lock();Throwable localThrowable4 = null;
/*     */         try {
/* 193 */           Runnable action = (Runnable)ManagedSelector.this._actions.poll();
/* 194 */           if (action == null)
/*     */           {
/*     */ 
/* 197 */             ManagedSelector.this._selecting = true;
/* 198 */             return null;
/*     */           }
/*     */         }
/*     */         catch (Throwable localThrowable6)
/*     */         {
/* 191 */           localThrowable4 = localThrowable6;throw localThrowable6;
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/*     */ 
/*     */ 
/* 200 */           if (lock != null) if (localThrowable4 != null) try { lock.close(); } catch (Throwable localThrowable3) { localThrowable4.addSuppressed(localThrowable3); } else lock.close(); }
/*     */         Runnable action;
/* 202 */         if ((action instanceof Product)) {
/* 203 */           return action;
/*     */         }
/*     */         
/* 206 */         runChange(action);
/*     */       }
/*     */     }
/*     */     
/*     */     private void runChange(Runnable change)
/*     */     {
/*     */       try
/*     */       {
/* 214 */         if (ManagedSelector.LOG.isDebugEnabled())
/* 215 */           ManagedSelector.LOG.debug("Running change {}", new Object[] { change });
/* 216 */         change.run();
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 220 */         ManagedSelector.LOG.debug("Could not run change " + change, x);
/*     */       }
/*     */     }
/*     */     
/*     */     private boolean select()
/*     */     {
/*     */       try
/*     */       {
/* 228 */         Selector selector = ManagedSelector.this._selector;
/* 229 */         if ((selector != null) && (selector.isOpen()))
/*     */         {
/* 231 */           if (ManagedSelector.LOG.isDebugEnabled())
/* 232 */             ManagedSelector.LOG.debug("Selector loop waiting on select", new Object[0]);
/* 233 */           int selected = selector.select();
/* 234 */           if (ManagedSelector.LOG.isDebugEnabled()) {
/* 235 */             ManagedSelector.LOG.debug("Selector loop woken up from select, {}/{} selected", new Object[] { Integer.valueOf(selected), Integer.valueOf(selector.keys().size()) });
/*     */           }
/* 237 */           Locker.Lock lock = ManagedSelector.this._locker.lock();Throwable localThrowable4 = null;
/*     */           try
/*     */           {
/* 240 */             ManagedSelector.this._selecting = false;
/*     */           }
/*     */           catch (Throwable localThrowable2)
/*     */           {
/* 237 */             localThrowable4 = localThrowable2;throw localThrowable2;
/*     */           }
/*     */           finally
/*     */           {
/* 241 */             if (lock != null) if (localThrowable4 != null) try { lock.close(); } catch (Throwable localThrowable3) { localThrowable4.addSuppressed(localThrowable3); } else lock.close();
/*     */           }
/* 243 */           this._keys = selector.selectedKeys();
/* 244 */           this._cursor = this._keys.iterator();
/*     */           
/* 246 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 251 */         ManagedSelector.this.closeNoExceptions(ManagedSelector.this._selector);
/* 252 */         if (ManagedSelector.this.isRunning()) {
/* 253 */           ManagedSelector.LOG.warn(x);
/*     */         } else
/* 255 */           ManagedSelector.LOG.debug(x);
/*     */       }
/* 257 */       return false;
/*     */     }
/*     */     
/*     */     private Runnable processSelected()
/*     */     {
/* 262 */       while (this._cursor.hasNext())
/*     */       {
/* 264 */         SelectionKey key = (SelectionKey)this._cursor.next();
/* 265 */         if (key.isValid())
/*     */         {
/* 267 */           Object attachment = key.attachment();
/*     */           try
/*     */           {
/* 270 */             if ((attachment instanceof SelectableEndPoint))
/*     */             {
/*     */ 
/* 273 */               Runnable task = ((SelectableEndPoint)attachment).onSelected();
/* 274 */               if (task != null) {
/* 275 */                 return task;
/*     */               }
/* 277 */             } else if (key.isConnectable())
/*     */             {
/* 279 */               Runnable task = ManagedSelector.this.processConnect(key, (Connect)attachment);
/* 280 */               if (task != null) {
/* 281 */                 return task;
/*     */               }
/* 283 */             } else if (key.isAcceptable())
/*     */             {
/* 285 */               ManagedSelector.this.processAccept(key);
/*     */             }
/*     */             else
/*     */             {
/* 289 */               throw new IllegalStateException("key=" + key + ", att=" + attachment + ", iOps=" + key.interestOps() + ", rOps=" + key.readyOps());
/*     */             }
/*     */           }
/*     */           catch (CancelledKeyException x)
/*     */           {
/* 294 */             ManagedSelector.LOG.debug("Ignoring cancelled key for channel {}", new Object[] { key.channel() });
/* 295 */             if ((attachment instanceof EndPoint)) {
/* 296 */               ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
/*     */             }
/*     */           }
/*     */           catch (Throwable x) {
/* 300 */             ManagedSelector.LOG.warn("Could not process key for channel " + key.channel(), x);
/* 301 */             if ((attachment instanceof EndPoint)) {
/* 302 */               ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 307 */           if (ManagedSelector.LOG.isDebugEnabled())
/* 308 */             ManagedSelector.LOG.debug("Selector loop ignoring invalid key for channel {}", new Object[] { key.channel() });
/* 309 */           Object attachment = key.attachment();
/* 310 */           if ((attachment instanceof EndPoint))
/* 311 */             ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
/*     */         }
/*     */       }
/* 314 */       return null;
/*     */     }
/*     */     
/*     */     private void update()
/*     */     {
/* 319 */       for (SelectionKey key : this._keys)
/* 320 */         updateKey(key);
/* 321 */       this._keys.clear();
/*     */     }
/*     */     
/*     */     private void updateKey(SelectionKey key)
/*     */     {
/* 326 */       Object attachment = key.attachment();
/* 327 */       if ((attachment instanceof SelectableEndPoint)) {
/* 328 */         ((SelectableEndPoint)attachment).updateKey();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Runnable processConnect(SelectionKey key, final Connect connect)
/*     */   {
/* 338 */     SocketChannel channel = (SocketChannel)key.channel();
/*     */     try
/*     */     {
/* 341 */       key.attach(connect.attachment);
/* 342 */       boolean connected = this._selectorManager.finishConnect(channel);
/* 343 */       if (LOG.isDebugEnabled())
/* 344 */         LOG.debug("Connected {} {}", new Object[] { Boolean.valueOf(connected), channel });
/* 345 */       if (connected)
/*     */       {
/* 347 */         if (connect.timeout.cancel())
/*     */         {
/* 349 */           key.interestOps(0);
/* 350 */           new CreateEndPoint(channel, key, connect)
/*     */           {
/*     */ 
/*     */             protected void failed(Throwable failure)
/*     */             {
/* 355 */               super.failed(failure);
/* 356 */               connect.failed(failure);
/*     */             }
/*     */           };
/*     */         }
/*     */         
/*     */ 
/* 362 */         throw new SocketTimeoutException("Concurrent Connect Timeout");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 367 */       throw new ConnectException();
/*     */ 
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 372 */       connect.failed(x); }
/* 373 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private void processAccept(SelectionKey key)
/*     */   {
/* 379 */     ServerSocketChannel server = (ServerSocketChannel)key.channel();
/* 380 */     SocketChannel channel = null;
/*     */     try
/*     */     {
/* 383 */       while ((channel = server.accept()) != null)
/*     */       {
/* 385 */         this._selectorManager.accepted(channel);
/*     */       }
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 390 */       closeNoExceptions(channel);
/* 391 */       LOG.warn("Accept failed for channel " + channel, x);
/*     */     }
/*     */   }
/*     */   
/*     */   private void closeNoExceptions(Closeable closeable)
/*     */   {
/*     */     try
/*     */     {
/* 399 */       if (closeable != null) {
/* 400 */         closeable.close();
/*     */       }
/*     */     }
/*     */     catch (Throwable x) {
/* 404 */       LOG.ignore(x);
/*     */     }
/*     */   }
/*     */   
/*     */   private EndPoint createEndPoint(SocketChannel channel, SelectionKey selectionKey) throws IOException
/*     */   {
/* 410 */     EndPoint endPoint = this._selectorManager.newEndPoint(channel, this, selectionKey);
/* 411 */     this._selectorManager.endPointOpened(endPoint);
/* 412 */     Connection connection = this._selectorManager.newConnection(channel, endPoint, selectionKey.attachment());
/* 413 */     endPoint.setConnection(connection);
/* 414 */     selectionKey.attach(endPoint);
/* 415 */     this._selectorManager.connectionOpened(connection);
/* 416 */     if (LOG.isDebugEnabled())
/* 417 */       LOG.debug("Created {}", new Object[] { endPoint });
/* 418 */     return endPoint;
/*     */   }
/*     */   
/*     */   public void destroyEndPoint(final EndPoint endPoint)
/*     */   {
/* 423 */     final Connection connection = endPoint.getConnection();
/* 424 */     submit(new Product()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 429 */         if (ManagedSelector.LOG.isDebugEnabled())
/* 430 */           ManagedSelector.LOG.debug("Destroyed {}", new Object[] { endPoint });
/* 431 */         if (connection != null)
/* 432 */           ManagedSelector.this._selectorManager.connectionClosed(connection);
/* 433 */         ManagedSelector.this._selectorManager.endPointClosed(endPoint);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public String dump()
/*     */   {
/* 441 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 447 */     out.append(String.valueOf(this)).append(" id=").append(String.valueOf(this._id)).append(System.lineSeparator());
/*     */     
/* 449 */     Selector selector = this._selector;
/* 450 */     if ((selector != null) && (selector.isOpen()))
/*     */     {
/* 452 */       ArrayList<Object> dump = new ArrayList(selector.keys().size() * 2);
/*     */       
/* 454 */       DumpKeys dumpKeys = new DumpKeys(dump, null);
/* 455 */       submit(dumpKeys);
/* 456 */       dumpKeys.await(5L, TimeUnit.SECONDS);
/*     */       
/* 458 */       ContainerLifeCycle.dump(out, indent, new Collection[] { dump });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 465 */     Selector selector = this._selector;
/* 466 */     return String.format("%s id=%s keys=%d selected=%d", new Object[] {
/* 467 */       super.toString(), 
/* 468 */       Integer.valueOf(this._id), 
/* 469 */       Integer.valueOf((selector != null) && (selector.isOpen()) ? selector.keys().size() : -1), 
/* 470 */       Integer.valueOf((selector != null) && (selector.isOpen()) ? selector.selectedKeys().size() : -1) }); }
/*     */   
/*     */   private static abstract interface Product extends Runnable
/*     */   {}
/*     */   
/* 475 */   private class DumpKeys implements Runnable { private final CountDownLatch latch = new CountDownLatch(1);
/*     */     private final List<Object> _dumps;
/*     */     
/*     */     private DumpKeys()
/*     */     {
/* 480 */       this._dumps = dumps;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 486 */       Selector selector = ManagedSelector.this._selector;
/* 487 */       if ((selector != null) && (selector.isOpen()))
/*     */       {
/* 489 */         Set<SelectionKey> keys = selector.keys();
/* 490 */         this._dumps.add(selector + " keys=" + keys.size());
/* 491 */         for (SelectionKey key : keys)
/*     */         {
/*     */           try
/*     */           {
/* 495 */             this._dumps.add(String.format("SelectionKey@%x{i=%d}->%s", new Object[] { Integer.valueOf(key.hashCode()), Integer.valueOf(key.interestOps()), key.attachment() }));
/*     */           }
/*     */           catch (Throwable x)
/*     */           {
/* 499 */             ManagedSelector.LOG.ignore(x);
/*     */           }
/*     */         }
/*     */       }
/* 503 */       this.latch.countDown();
/*     */     }
/*     */     
/*     */     public boolean await(long timeout, TimeUnit unit)
/*     */     {
/*     */       try
/*     */       {
/* 510 */         return this.latch.await(timeout, unit);
/*     */       }
/*     */       catch (InterruptedException x) {}
/*     */       
/* 514 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   class Acceptor
/*     */     implements Runnable
/*     */   {
/*     */     private final ServerSocketChannel _channel;
/*     */     
/*     */     public Acceptor(ServerSocketChannel channel)
/*     */     {
/* 525 */       this._channel = channel;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 533 */         SelectionKey key = this._channel.register(ManagedSelector.this._selector, 16, null);
/* 534 */         if (ManagedSelector.LOG.isDebugEnabled()) {
/* 535 */           ManagedSelector.LOG.debug("{} acceptor={}", new Object[] { this, key });
/*     */         }
/*     */       }
/*     */       catch (Throwable x) {
/* 539 */         ManagedSelector.this.closeNoExceptions(this._channel);
/* 540 */         ManagedSelector.LOG.warn(x);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class Accept implements Runnable, ExecutionStrategy.Rejectable
/*     */   {
/*     */     private final SocketChannel channel;
/*     */     private final Object attachment;
/*     */     
/*     */     Accept(SocketChannel channel, Object attachment)
/*     */     {
/* 552 */       this.channel = channel;
/* 553 */       this.attachment = attachment;
/*     */     }
/*     */     
/*     */ 
/*     */     public void reject()
/*     */     {
/* 559 */       ManagedSelector.LOG.debug("rejected accept {}", new Object[] { this.channel });
/* 560 */       ManagedSelector.this.closeNoExceptions(this.channel);
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 568 */         SelectionKey key = this.channel.register(ManagedSelector.this._selector, 0, this.attachment);
/* 569 */         ManagedSelector.this.submit(new CreateEndPoint(ManagedSelector.this, this.channel, key));
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 573 */         ManagedSelector.this.closeNoExceptions(this.channel);
/* 574 */         ManagedSelector.LOG.debug(x);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class CreateEndPoint implements Product, ExecutionStrategy.Rejectable
/*     */   {
/*     */     private final SocketChannel channel;
/*     */     private final SelectionKey key;
/*     */     
/*     */     public CreateEndPoint(SocketChannel channel, SelectionKey key)
/*     */     {
/* 586 */       this.channel = channel;
/* 587 */       this.key = key;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 595 */         ManagedSelector.this.createEndPoint(this.channel, this.key);
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 599 */         ManagedSelector.LOG.debug(x);
/* 600 */         failed(x);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void reject()
/*     */     {
/* 607 */       ManagedSelector.LOG.debug("rejected create {}", new Object[] { this.channel });
/* 608 */       ManagedSelector.this.closeNoExceptions(this.channel);
/*     */     }
/*     */     
/*     */     protected void failed(Throwable failure)
/*     */     {
/* 613 */       ManagedSelector.this.closeNoExceptions(this.channel);
/* 614 */       ManagedSelector.LOG.debug(failure);
/*     */     }
/*     */   }
/*     */   
/*     */   class Connect implements Runnable
/*     */   {
/* 620 */     private final AtomicBoolean failed = new AtomicBoolean();
/*     */     private final SocketChannel channel;
/*     */     private final Object attachment;
/*     */     private final Scheduler.Task timeout;
/*     */     
/*     */     Connect(SocketChannel channel, Object attachment)
/*     */     {
/* 627 */       this.channel = channel;
/* 628 */       this.attachment = attachment;
/* 629 */       this.timeout = ManagedSelector.this._selectorManager.getScheduler().schedule(new ConnectTimeout(ManagedSelector.this, this, null), ManagedSelector.this._selectorManager.getConnectTimeout(), TimeUnit.MILLISECONDS);
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 637 */         this.channel.register(ManagedSelector.this._selector, 8, this);
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 641 */         failed(x);
/*     */       }
/*     */     }
/*     */     
/*     */     private void failed(Throwable failure)
/*     */     {
/* 647 */       if (this.failed.compareAndSet(false, true))
/*     */       {
/* 649 */         this.timeout.cancel();
/* 650 */         ManagedSelector.this.closeNoExceptions(this.channel);
/* 651 */         ManagedSelector.this._selectorManager.connectionFailed(this.channel, failure, this.attachment);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class ConnectTimeout implements Runnable
/*     */   {
/*     */     private final Connect connect;
/*     */     
/*     */     private ConnectTimeout(Connect connect)
/*     */     {
/* 662 */       this.connect = connect;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 668 */       SocketChannel channel = Connect.access$1800(this.connect);
/* 669 */       if (channel.isConnectionPending())
/*     */       {
/* 671 */         if (ManagedSelector.LOG.isDebugEnabled())
/* 672 */           ManagedSelector.LOG.debug("Channel {} timed out while connecting, closing it", new Object[] { channel });
/* 673 */         Connect.access$1300(this.connect, new SocketTimeoutException("Connect Timeout"));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class CloseEndPoints implements Runnable
/*     */   {
/* 680 */     private final CountDownLatch _latch = new CountDownLatch(1);
/*     */     private CountDownLatch _allClosed;
/*     */     
/*     */     private CloseEndPoints() {}
/*     */     
/*     */     public void run() {
/* 686 */       List<EndPoint> end_points = new ArrayList();
/* 687 */       for (Iterator localIterator = ManagedSelector.this._selector.keys().iterator(); localIterator.hasNext();) { key = (SelectionKey)localIterator.next();
/*     */         
/* 689 */         if (key.isValid())
/*     */         {
/* 691 */           Object attachment = key.attachment();
/* 692 */           if ((attachment instanceof EndPoint))
/* 693 */             end_points.add((EndPoint)attachment);
/*     */         }
/*     */       }
/*     */       SelectionKey key;
/* 697 */       int size = end_points.size();
/* 698 */       if (ManagedSelector.LOG.isDebugEnabled()) {
/* 699 */         ManagedSelector.LOG.debug("Closing {} endPoints on {}", new Object[] { Integer.valueOf(size), ManagedSelector.this });
/*     */       }
/* 701 */       this._allClosed = new CountDownLatch(size);
/* 702 */       this._latch.countDown();
/*     */       
/* 704 */       for (EndPoint endp : end_points) {
/* 705 */         ManagedSelector.this.submit(new EndPointCloser(ManagedSelector.this, endp, this._allClosed, null));
/*     */       }
/* 707 */       if (ManagedSelector.LOG.isDebugEnabled()) {
/* 708 */         ManagedSelector.LOG.debug("Closed {} endPoints on {}", new Object[] { Integer.valueOf(size), ManagedSelector.this });
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean await(long timeout)
/*     */     {
/*     */       try
/*     */       {
/* 716 */         return (this._latch.await(timeout, TimeUnit.MILLISECONDS)) && (this._allClosed.await(timeout, TimeUnit.MILLISECONDS));
/*     */       }
/*     */       catch (InterruptedException x) {}
/*     */       
/* 720 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   private class EndPointCloser
/*     */     implements Product
/*     */   {
/*     */     private final EndPoint _endPoint;
/*     */     private final CountDownLatch _latch;
/*     */     
/*     */     private EndPointCloser(EndPoint endPoint, CountDownLatch latch)
/*     */     {
/* 732 */       this._endPoint = endPoint;
/* 733 */       this._latch = latch;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 739 */       ManagedSelector.this.closeNoExceptions(this._endPoint.getConnection());
/* 740 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */   private class CloseSelector implements Runnable
/*     */   {
/* 746 */     private CountDownLatch _latch = new CountDownLatch(1);
/*     */     
/*     */     private CloseSelector() {}
/*     */     
/*     */     public void run() {
/* 751 */       Selector selector = ManagedSelector.this._selector;
/* 752 */       ManagedSelector.this._selector = null;
/* 753 */       ManagedSelector.this.closeNoExceptions(selector);
/* 754 */       this._latch.countDown();
/*     */     }
/*     */     
/*     */     public boolean await(long timeout)
/*     */     {
/*     */       try
/*     */       {
/* 761 */         return this._latch.await(timeout, TimeUnit.MILLISECONDS);
/*     */       }
/*     */       catch (InterruptedException x) {}
/*     */       
/* 765 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ManagedSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */