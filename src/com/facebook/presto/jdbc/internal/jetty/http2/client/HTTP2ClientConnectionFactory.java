/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.FlowControlStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.FlowControlStrategy.Factory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.HTTP2Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ISession;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.Frame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PrefaceFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.SettingsFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.WindowUpdateFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.generator.Generator;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.parser.Parser;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.Connection.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.LifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
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
/*     */ public class HTTP2ClientConnectionFactory
/*     */   implements ClientConnectionFactory
/*     */ {
/*     */   public static final String CLIENT_CONTEXT_KEY = "http2.client";
/*     */   public static final String BYTE_BUFFER_POOL_CONTEXT_KEY = "http2.client.byteBufferPool";
/*     */   public static final String EXECUTOR_CONTEXT_KEY = "http2.client.executor";
/*     */   public static final String SCHEDULER_CONTEXT_KEY = "http2.client.scheduler";
/*     */   public static final String SESSION_LISTENER_CONTEXT_KEY = "http2.client.sessionListener";
/*     */   public static final String SESSION_PROMISE_CONTEXT_KEY = "http2.client.sessionPromise";
/*  53 */   private final Connection.Listener connectionListener = new ConnectionListener(null);
/*  54 */   private int initialSessionRecvWindow = 65535;
/*     */   
/*     */   public Connection newConnection(EndPoint endPoint, Map<String, Object> context)
/*     */     throws IOException
/*     */   {
/*  59 */     HTTP2Client client = (HTTP2Client)context.get("http2.client");
/*  60 */     ByteBufferPool byteBufferPool = (ByteBufferPool)context.get("http2.client.byteBufferPool");
/*  61 */     Executor executor = (Executor)context.get("http2.client.executor");
/*  62 */     Scheduler scheduler = (Scheduler)context.get("http2.client.scheduler");
/*  63 */     Session.Listener listener = (Session.Listener)context.get("http2.client.sessionListener");
/*     */     
/*  65 */     Promise<Session> promise = (Promise)context.get("http2.client.sessionPromise");
/*     */     
/*  67 */     Generator generator = new Generator(byteBufferPool);
/*  68 */     FlowControlStrategy flowControl = newFlowControlStrategy();
/*  69 */     if (flowControl == null)
/*  70 */       flowControl = client.getFlowControlStrategyFactory().newFlowControlStrategy();
/*  71 */     HTTP2ClientSession session = new HTTP2ClientSession(scheduler, endPoint, generator, listener, flowControl);
/*  72 */     Parser parser = new Parser(byteBufferPool, session, 4096, 8192);
/*  73 */     HTTP2ClientConnection connection = new HTTP2ClientConnection(client, byteBufferPool, executor, endPoint, parser, session, client.getInputBufferSize(), promise, listener);
/*  74 */     connection.addListener(this.connectionListener);
/*  75 */     return connection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   protected FlowControlStrategy newFlowControlStrategy()
/*     */   {
/*  84 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public int getInitialSessionRecvWindow()
/*     */   {
/*  93 */     return this.initialSessionRecvWindow;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public void setInitialSessionRecvWindow(int initialSessionRecvWindow)
/*     */   {
/* 102 */     this.initialSessionRecvWindow = initialSessionRecvWindow;
/*     */   }
/*     */   
/*     */   private class HTTP2ClientConnection extends HTTP2Connection implements Callback
/*     */   {
/*     */     private final HTTP2Client client;
/*     */     private final Promise<Session> promise;
/*     */     private final Session.Listener listener;
/*     */     
/*     */     public HTTP2ClientConnection(ByteBufferPool client, Executor byteBufferPool, EndPoint executor, Parser endpoint, ISession parser, int session, Promise<Session> bufferSize, Session.Listener promise)
/*     */     {
/* 113 */       super(executor, endpoint, parser, session, bufferSize);
/* 114 */       this.client = client;
/* 115 */       this.promise = promise;
/* 116 */       this.listener = listener;
/*     */     }
/*     */     
/*     */ 
/*     */     public void onOpen()
/*     */     {
/* 122 */       Map<Integer, Integer> settings = this.listener.onPreface(getSession());
/* 123 */       if (settings == null) {
/* 124 */         settings = Collections.emptyMap();
/*     */       }
/* 126 */       PrefaceFrame prefaceFrame = new PrefaceFrame();
/* 127 */       SettingsFrame settingsFrame = new SettingsFrame(settings, false);
/*     */       
/* 129 */       ISession session = getSession();
/*     */       
/* 131 */       int sessionRecv = this.client.getInitialSessionRecvWindow();
/* 132 */       if (sessionRecv == 65535) {
/* 133 */         sessionRecv = HTTP2ClientConnectionFactory.this.initialSessionRecvWindow;
/*     */       }
/* 135 */       int windowDelta = sessionRecv - 65535;
/* 136 */       if (windowDelta > 0)
/*     */       {
/* 138 */         session.updateRecvWindow(windowDelta);
/* 139 */         session.frames(null, this, prefaceFrame, new Frame[] { settingsFrame, new WindowUpdateFrame(0, windowDelta) });
/*     */       }
/*     */       else
/*     */       {
/* 143 */         session.frames(null, this, prefaceFrame, new Frame[] { settingsFrame });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 148 */       super.onOpen();
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 154 */       this.promise.succeeded(getSession());
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/* 160 */       close();
/* 161 */       this.promise.failed(x);
/*     */     }
/*     */   }
/*     */   
/*     */   private class ConnectionListener implements Connection.Listener
/*     */   {
/*     */     private ConnectionListener() {}
/*     */     
/*     */     public void onOpened(Connection connection) {
/* 170 */       HTTP2ClientConnection http2Connection = (HTTP2ClientConnection)connection;
/* 171 */       HTTP2ClientConnection.access$200(http2Connection).addManaged((LifeCycle)http2Connection.getSession());
/*     */     }
/*     */     
/*     */ 
/*     */     public void onClosed(Connection connection)
/*     */     {
/* 177 */       HTTP2ClientConnection http2Connection = (HTTP2ClientConnection)connection;
/* 178 */       HTTP2ClientConnection.access$200(http2Connection).removeBean(http2Connection.getSession());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\HTTP2ClientConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */