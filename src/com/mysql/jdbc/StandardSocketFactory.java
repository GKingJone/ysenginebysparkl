/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
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
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory, SocketMetadata
/*     */ {
/*     */   public static final String TCP_NO_DELAY_PROPERTY_NAME = "tcpNoDelay";
/*     */   public static final String TCP_KEEP_ALIVE_DEFAULT_VALUE = "true";
/*     */   public static final String TCP_KEEP_ALIVE_PROPERTY_NAME = "tcpKeepAlive";
/*     */   public static final String TCP_RCV_BUF_PROPERTY_NAME = "tcpRcvBuf";
/*     */   public static final String TCP_SND_BUF_PROPERTY_NAME = "tcpSndBuf";
/*     */   public static final String TCP_TRAFFIC_CLASS_PROPERTY_NAME = "tcpTrafficClass";
/*     */   public static final String TCP_RCV_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_SND_BUF_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_TRAFFIC_CLASS_DEFAULT_VALUE = "0";
/*     */   public static final String TCP_NO_DELAY_DEFAULT_VALUE = "true";
/*  61 */   protected String host = null;
/*     */   
/*     */ 
/*  64 */   protected int port = 3306;
/*     */   
/*     */ 
/*  67 */   protected Socket rawSocket = null;
/*     */   
/*     */ 
/*  70 */   protected int loginTimeoutCountdown = DriverManager.getLoginTimeout() * 1000;
/*     */   
/*     */ 
/*  73 */   protected long loginTimeoutCheckTimestamp = System.currentTimeMillis();
/*     */   
/*     */ 
/*  76 */   protected int socketTimeoutBackup = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/*  90 */     resetLoginTimeCountdown();
/*  91 */     this.rawSocket.setSoTimeout(this.socketTimeoutBackup);
/*  92 */     return this.rawSocket;
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
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 107 */     resetLoginTimeCountdown();
/* 108 */     this.socketTimeoutBackup = this.rawSocket.getSoTimeout();
/* 109 */     this.rawSocket.setSoTimeout(getRealTimeout(this.socketTimeoutBackup));
/* 110 */     return this.rawSocket;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Socket createSocket(Properties props)
/*     */   {
/* 120 */     return new Socket();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void configureSocket(Socket sock, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 132 */     sock.setTcpNoDelay(Boolean.valueOf(props.getProperty("tcpNoDelay", "true")).booleanValue());
/*     */     
/* 134 */     String keepAlive = props.getProperty("tcpKeepAlive", "true");
/*     */     
/* 136 */     if ((keepAlive != null) && (keepAlive.length() > 0)) {
/* 137 */       sock.setKeepAlive(Boolean.valueOf(keepAlive).booleanValue());
/*     */     }
/*     */     
/* 140 */     int receiveBufferSize = Integer.parseInt(props.getProperty("tcpRcvBuf", "0"));
/*     */     
/* 142 */     if (receiveBufferSize > 0) {
/* 143 */       sock.setReceiveBufferSize(receiveBufferSize);
/*     */     }
/*     */     
/* 146 */     int sendBufferSize = Integer.parseInt(props.getProperty("tcpSndBuf", "0"));
/*     */     
/* 148 */     if (sendBufferSize > 0) {
/* 149 */       sock.setSendBufferSize(sendBufferSize);
/*     */     }
/*     */     
/* 152 */     int trafficClass = Integer.parseInt(props.getProperty("tcpTrafficClass", "0"));
/*     */     
/* 154 */     if (trafficClass > 0) {
/* 155 */       sock.setTrafficClass(trafficClass);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Socket connect(String hostname, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 164 */     if (props != null) {
/* 165 */       this.host = hostname;
/*     */       
/* 167 */       this.port = portNumber;
/*     */       
/* 169 */       String localSocketHostname = props.getProperty("localSocketAddress");
/* 170 */       InetSocketAddress localSockAddr = null;
/* 171 */       if ((localSocketHostname != null) && (localSocketHostname.length() > 0)) {
/* 172 */         localSockAddr = new InetSocketAddress(InetAddress.getByName(localSocketHostname), 0);
/*     */       }
/*     */       
/* 175 */       String connectTimeoutStr = props.getProperty("connectTimeout");
/*     */       
/* 177 */       int connectTimeout = 0;
/*     */       
/* 179 */       if (connectTimeoutStr != null) {
/*     */         try {
/* 181 */           connectTimeout = Integer.parseInt(connectTimeoutStr);
/*     */         } catch (NumberFormatException nfe) {
/* 183 */           throw new SocketException("Illegal value '" + connectTimeoutStr + "' for connectTimeout");
/*     */         }
/*     */       }
/*     */       
/* 187 */       if (this.host != null) {
/* 188 */         InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */         
/* 190 */         if (possibleAddresses.length == 0) {
/* 191 */           throw new SocketException("No addresses for host");
/*     */         }
/*     */         
/*     */ 
/* 195 */         SocketException lastException = null;
/*     */         
/*     */ 
/*     */ 
/* 199 */         for (int i = 0; i < possibleAddresses.length; i++) {
/*     */           try {
/* 201 */             this.rawSocket = createSocket(props);
/*     */             
/* 203 */             configureSocket(this.rawSocket, props);
/*     */             
/* 205 */             InetSocketAddress sockAddr = new InetSocketAddress(possibleAddresses[i], this.port);
/*     */             
/* 207 */             if (localSockAddr != null) {
/* 208 */               this.rawSocket.bind(localSockAddr);
/*     */             }
/*     */             
/* 211 */             this.rawSocket.connect(sockAddr, getRealTimeout(connectTimeout));
/*     */           }
/*     */           catch (SocketException ex)
/*     */           {
/* 215 */             lastException = ex;
/* 216 */             resetLoginTimeCountdown();
/* 217 */             this.rawSocket = null;
/*     */           }
/*     */         }
/*     */         
/* 221 */         if ((this.rawSocket == null) && (lastException != null)) {
/* 222 */           throw lastException;
/*     */         }
/*     */         
/* 225 */         resetLoginTimeCountdown();
/*     */         
/* 227 */         return this.rawSocket;
/*     */       }
/*     */     }
/*     */     
/* 231 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */   
/*     */   public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException {
/* 235 */     return Helper.isLocallyConnected(conn);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void resetLoginTimeCountdown()
/*     */     throws SocketException
/*     */   {
/* 245 */     if (this.loginTimeoutCountdown > 0) {
/* 246 */       long now = System.currentTimeMillis();
/* 247 */       this.loginTimeoutCountdown = ((int)(this.loginTimeoutCountdown - (now - this.loginTimeoutCheckTimestamp)));
/* 248 */       if (this.loginTimeoutCountdown <= 0) {
/* 249 */         throw new SocketException(Messages.getString("Connection.LoginTimeout"));
/*     */       }
/* 251 */       this.loginTimeoutCheckTimestamp = now;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getRealTimeout(int expectedTimeout)
/*     */   {
/* 263 */     if ((this.loginTimeoutCountdown > 0) && ((expectedTimeout == 0) || (expectedTimeout > this.loginTimeoutCountdown))) {
/* 264 */       return this.loginTimeoutCountdown;
/*     */     }
/* 266 */     return expectedTimeout;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\StandardSocketFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */