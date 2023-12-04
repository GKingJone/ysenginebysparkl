/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SelectorManager
/*     */   extends AbstractLifeCycle
/*     */   implements Dumpable
/*     */ {
/*     */   public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
/*  47 */   protected static final Logger LOG = Log.getLogger(SelectorManager.class);
/*     */   
/*     */   private final Executor executor;
/*     */   private final Scheduler scheduler;
/*     */   private final ManagedSelector[] _selectors;
/*  52 */   private long _connectTimeout = 15000L;
/*     */   private long _selectorIndex;
/*     */   
/*     */   protected SelectorManager(Executor executor, Scheduler scheduler)
/*     */   {
/*  57 */     this(executor, scheduler, (Runtime.getRuntime().availableProcessors() + 1) / 2);
/*     */   }
/*     */   
/*     */   protected SelectorManager(Executor executor, Scheduler scheduler, int selectors)
/*     */   {
/*  62 */     if (selectors <= 0)
/*  63 */       throw new IllegalArgumentException("No selectors");
/*  64 */     this.executor = executor;
/*  65 */     this.scheduler = scheduler;
/*  66 */     this._selectors = new ManagedSelector[selectors];
/*     */   }
/*     */   
/*     */   public Executor getExecutor()
/*     */   {
/*  71 */     return this.executor;
/*     */   }
/*     */   
/*     */   public Scheduler getScheduler()
/*     */   {
/*  76 */     return this.scheduler;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getConnectTimeout()
/*     */   {
/*  86 */     return this._connectTimeout;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setConnectTimeout(long milliseconds)
/*     */   {
/*  96 */     this._connectTimeout = milliseconds;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public int getSelectorPriorityDelta()
/*     */   {
/* 106 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public void setSelectorPriorityDelta(int selectorPriorityDelta) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void execute(Runnable task)
/*     */   {
/* 125 */     this.executor.execute(task);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSelectorCount()
/*     */   {
/* 133 */     return this._selectors.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ManagedSelector chooseSelector(SocketChannel channel)
/*     */   {
/* 143 */     ManagedSelector candidate1 = null;
/* 144 */     if (channel != null)
/*     */     {
/*     */       try
/*     */       {
/* 148 */         SocketAddress remote = channel.getRemoteAddress();
/* 149 */         if ((remote instanceof InetSocketAddress))
/*     */         {
/* 151 */           byte[] addr = ((InetSocketAddress)remote).getAddress().getAddress();
/* 152 */           if (addr != null)
/*     */           {
/* 154 */             int s = addr[(addr.length - 1)] & 0xFF;
/* 155 */             candidate1 = this._selectors[(s % getSelectorCount())];
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (IOException x)
/*     */       {
/* 161 */         LOG.ignore(x);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 168 */     long s = this._selectorIndex++;
/* 169 */     int index = (int)(s % getSelectorCount());
/* 170 */     ManagedSelector candidate2 = this._selectors[index];
/*     */     
/* 172 */     if ((candidate1 == null) || (candidate1.size() >= candidate2.size() * 2))
/* 173 */       return candidate2;
/* 174 */     return candidate1;
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
/*     */   public void connect(SocketChannel channel, Object attachment)
/*     */   {
/* 189 */     ManagedSelector set = chooseSelector(channel); ManagedSelector 
/* 190 */       tmp12_11 = set;tmp12_11.getClass();set.submit(new ManagedSelector.Connect(tmp12_11, channel, attachment));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void accept(SocketChannel channel)
/*     */   {
/* 199 */     accept(channel, null);
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
/*     */   public void accept(SocketChannel channel, Object attachment)
/*     */   {
/* 214 */     ManagedSelector selector = chooseSelector(channel); ManagedSelector 
/* 215 */       tmp12_11 = selector;tmp12_11.getClass();selector.submit(new ManagedSelector.Accept(tmp12_11, channel, attachment));
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
/*     */   public void acceptor(ServerSocketChannel server)
/*     */   {
/* 228 */     ManagedSelector selector = chooseSelector(null); ManagedSelector 
/* 229 */       tmp12_11 = selector;tmp12_11.getClass();selector.submit(new ManagedSelector.Acceptor(tmp12_11, server));
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
/*     */   protected void accepted(SocketChannel channel)
/*     */     throws IOException
/*     */   {
/* 243 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/* 249 */     super.doStart();
/* 250 */     for (int i = 0; i < this._selectors.length; i++)
/*     */     {
/* 252 */       ManagedSelector selector = newSelector(i);
/* 253 */       this._selectors[i] = selector;
/* 254 */       selector.start();
/* 255 */       execute(selector);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ManagedSelector newSelector(int id)
/*     */   {
/* 267 */     return new ManagedSelector(this, id);
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 273 */     for (ManagedSelector selector : this._selectors)
/* 274 */       selector.stop();
/* 275 */     super.doStop();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void endPointOpened(EndPoint endpoint)
/*     */   {
/* 285 */     endpoint.onOpen();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void endPointClosed(EndPoint endpoint)
/*     */   {
/* 295 */     endpoint.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectionOpened(Connection connection)
/*     */   {
/*     */     try
/*     */     {
/* 307 */       connection.onOpen();
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 311 */       if (isRunning()) {
/* 312 */         LOG.warn("Exception while notifying connection " + connection, x);
/*     */       } else
/* 314 */         LOG.debug("Exception while notifying connection " + connection, x);
/* 315 */       throw x;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectionClosed(Connection connection)
/*     */   {
/*     */     try
/*     */     {
/* 328 */       connection.onClose();
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 332 */       LOG.debug("Exception while notifying connection " + connection, x);
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean finishConnect(SocketChannel channel) throws IOException
/*     */   {
/* 338 */     return channel.finishConnect();
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
/*     */   protected void connectionFailed(SocketChannel channel, Throwable ex, Object attachment)
/*     */   {
/* 351 */     LOG.warn(String.format("%s - %s", new Object[] { channel, attachment }), ex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract EndPoint newEndPoint(SocketChannel paramSocketChannel, ManagedSelector paramManagedSelector, SelectionKey paramSelectionKey)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Connection newConnection(SocketChannel paramSocketChannel, EndPoint paramEndPoint, Object paramObject)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String dump()
/*     */   {
/* 383 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 389 */     ContainerLifeCycle.dumpObject(out, this);
/* 390 */     ContainerLifeCycle.dump(out, indent, new Collection[] { TypeUtil.asList(this._selectors) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\SelectorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */