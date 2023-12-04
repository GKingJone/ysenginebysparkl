/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.util.Base64Decoder;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.X509EncodedKeySpec;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExportControlled
/*     */ {
/*     */   private static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
/*     */   
/*     */   protected static boolean enabled()
/*     */   {
/*  68 */     return true;
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
/*     */   protected static void transformSocketToSSLSocket(MysqlIO mysqlIO)
/*     */     throws SQLException
/*     */   {
/*  85 */     SocketFactory sslFact = new StandardSSLSocketFactory(getSSLSocketFactoryDefaultOrConfigured(mysqlIO), mysqlIO.socketFactory, mysqlIO.mysqlConnection);
/*     */     try
/*     */     {
/*  88 */       mysqlIO.mysqlConnection = sslFact.connect(mysqlIO.host, mysqlIO.port, null);
/*     */       
/*  90 */       List<String> allowedProtocols = new ArrayList();
/*  91 */       List<String> supportedProtocols = Arrays.asList(((SSLSocket)mysqlIO.mysqlConnection).getSupportedProtocols());
/*  92 */       for (String protocol : new String[] { "TLSv1.1", (mysqlIO.versionMeetsMinimum(5, 6, 0)) && (Util.isEnterpriseEdition(mysqlIO.getServerVersion())) ? new String[] { "TLSv1.2", "TLSv1.1", "TLSv1" } : "TLSv1" })
/*     */       {
/*  94 */         if (supportedProtocols.contains(protocol)) {
/*  95 */           allowedProtocols.add(protocol);
/*     */         }
/*     */       }
/*  98 */       ((SSLSocket)mysqlIO.mysqlConnection).setEnabledProtocols((String[])allowedProtocols.toArray(new String[0]));
/*     */       
/*     */ 
/* 101 */       String enabledSSLCipherSuites = mysqlIO.connection.getEnabledSSLCipherSuites();
/* 102 */       boolean overrideCiphers = (enabledSSLCipherSuites != null) && (enabledSSLCipherSuites.length() > 0);
/*     */       
/* 104 */       List<String> allowedCiphers = null;
/* 105 */       if (overrideCiphers)
/*     */       {
/*     */ 
/* 108 */         allowedCiphers = new ArrayList();
/* 109 */         List<String> availableCiphers = Arrays.asList(((SSLSocket)mysqlIO.mysqlConnection).getEnabledCipherSuites());
/* 110 */         for (String cipher : enabledSSLCipherSuites.split("\\s*,\\s*")) {
/* 111 */           if (availableCiphers.contains(cipher)) {
/* 112 */             allowedCiphers.add(cipher);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 118 */         boolean disableDHAlgorithm = false;
/* 119 */         if (((mysqlIO.versionMeetsMinimum(5, 5, 45)) && (!mysqlIO.versionMeetsMinimum(5, 6, 0))) || ((mysqlIO.versionMeetsMinimum(5, 6, 26)) && (!mysqlIO.versionMeetsMinimum(5, 7, 0))) || (mysqlIO.versionMeetsMinimum(5, 7, 6)))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 125 */           if (Util.getJVMVersion() < 8) {
/* 126 */             disableDHAlgorithm = true;
/*     */           }
/* 128 */         } else if (Util.getJVMVersion() >= 8)
/*     */         {
/*     */ 
/* 131 */           disableDHAlgorithm = true;
/*     */         }
/*     */         
/* 134 */         if (disableDHAlgorithm) {
/* 135 */           allowedCiphers = new ArrayList();
/* 136 */           for (String cipher : ((SSLSocket)mysqlIO.mysqlConnection).getEnabledCipherSuites()) {
/* 137 */             if ((!disableDHAlgorithm) || ((cipher.indexOf("_DHE_") <= -1) && (cipher.indexOf("_DH_") <= -1))) {
/* 138 */               allowedCiphers.add(cipher);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 145 */       if (allowedCiphers != null) {
/* 146 */         ((SSLSocket)mysqlIO.mysqlConnection).setEnabledCipherSuites((String[])allowedCiphers.toArray(new String[0]));
/*     */       }
/*     */       
/* 149 */       ((SSLSocket)mysqlIO.mysqlConnection).startHandshake();
/*     */       
/* 151 */       if (mysqlIO.connection.getUseUnbufferedInput()) {
/* 152 */         mysqlIO.mysqlInput = mysqlIO.mysqlConnection.getInputStream();
/*     */       } else {
/* 154 */         mysqlIO.mysqlInput = new BufferedInputStream(mysqlIO.mysqlConnection.getInputStream(), 16384);
/*     */       }
/*     */       
/* 157 */       mysqlIO.mysqlOutput = new BufferedOutputStream(mysqlIO.mysqlConnection.getOutputStream(), 16384);
/*     */       
/* 159 */       mysqlIO.mysqlOutput.flush();
/*     */       
/* 161 */       mysqlIO.socketFactory = sslFact;
/*     */     }
/*     */     catch (IOException ioEx) {
/* 164 */       throw SQLError.createCommunicationsException(mysqlIO.connection, mysqlIO.getLastPacketSentTimeMs(), mysqlIO.getLastPacketReceivedTimeMs(), ioEx, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class StandardSSLSocketFactory
/*     */     implements SocketFactory, SocketMetadata
/*     */   {
/* 173 */     private SSLSocket rawSocket = null;
/*     */     private final SSLSocketFactory sslFact;
/*     */     private final SocketFactory existingSocketFactory;
/*     */     private final Socket existingSocket;
/*     */     
/*     */     public StandardSSLSocketFactory(SSLSocketFactory sslFact, SocketFactory existingSocketFactory, Socket existingSocket) {
/* 179 */       this.sslFact = sslFact;
/* 180 */       this.existingSocketFactory = existingSocketFactory;
/* 181 */       this.existingSocket = existingSocket;
/*     */     }
/*     */     
/*     */     public Socket afterHandshake() throws SocketException, IOException {
/* 185 */       this.existingSocketFactory.afterHandshake();
/* 186 */       return this.rawSocket;
/*     */     }
/*     */     
/*     */     public Socket beforeHandshake() throws SocketException, IOException {
/* 190 */       return this.rawSocket;
/*     */     }
/*     */     
/*     */     public Socket connect(String host, int portNumber, Properties props) throws SocketException, IOException {
/* 194 */       this.rawSocket = ((SSLSocket)this.sslFact.createSocket(this.existingSocket, host, portNumber, true));
/* 195 */       return this.rawSocket;
/*     */     }
/*     */     
/*     */     public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException {
/* 199 */       return Helper.isLocallyConnected(conn);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static SSLSocketFactory getSSLSocketFactoryDefaultOrConfigured(MysqlIO mysqlIO)
/*     */     throws SQLException
/*     */   {
/* 208 */     String clientCertificateKeyStoreUrl = mysqlIO.connection.getClientCertificateKeyStoreUrl();
/* 209 */     String trustCertificateKeyStoreUrl = mysqlIO.connection.getTrustCertificateKeyStoreUrl();
/* 210 */     String clientCertificateKeyStoreType = mysqlIO.connection.getClientCertificateKeyStoreType();
/* 211 */     String clientCertificateKeyStorePassword = mysqlIO.connection.getClientCertificateKeyStorePassword();
/* 212 */     String trustCertificateKeyStoreType = mysqlIO.connection.getTrustCertificateKeyStoreType();
/* 213 */     String trustCertificateKeyStorePassword = mysqlIO.connection.getTrustCertificateKeyStorePassword();
/*     */     
/* 215 */     if ((StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) && (StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl)) && 
/* 216 */       (mysqlIO.connection.getVerifyServerCertificate())) {
/* 217 */       return (SSLSocketFactory)SSLSocketFactory.getDefault();
/*     */     }
/*     */     
/*     */ 
/* 221 */     TrustManagerFactory tmf = null;
/* 222 */     KeyManagerFactory kmf = null;
/*     */     try
/*     */     {
/* 225 */       tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
/* 226 */       kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 228 */       throw SQLError.createSQLException("Default algorithm definitions for TrustManager and/or KeyManager are invalid.  Check java security properties file.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 233 */     if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl)) {
/* 234 */       InputStream ksIS = null;
/*     */       try {
/* 236 */         if (!StringUtils.isNullOrEmpty(clientCertificateKeyStoreType)) {
/* 237 */           KeyStore clientKeyStore = KeyStore.getInstance(clientCertificateKeyStoreType);
/* 238 */           URL ksURL = new URL(clientCertificateKeyStoreUrl);
/* 239 */           char[] password = clientCertificateKeyStorePassword == null ? new char[0] : clientCertificateKeyStorePassword.toCharArray();
/* 240 */           ksIS = ksURL.openStream();
/* 241 */           clientKeyStore.load(ksIS, password);
/* 242 */           kmf.init(clientKeyStore, password);
/*     */         }
/*     */       } catch (UnrecoverableKeyException uke) {
/* 245 */         throw SQLError.createSQLException("Could not recover keys from client keystore.  Check password?", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (NoSuchAlgorithmException nsae) {
/* 248 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (KeyStoreException kse) {
/* 251 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (CertificateException nsae) {
/* 254 */         throw SQLError.createSQLException("Could not load client" + clientCertificateKeyStoreType + " keystore from " + clientCertificateKeyStoreUrl, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (MalformedURLException mue) {
/* 257 */         throw SQLError.createSQLException(clientCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (IOException ioe) {
/* 260 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + clientCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */         
/* 262 */         sqlEx.initCause(ioe);
/*     */         
/* 264 */         throw sqlEx;
/*     */       } finally {
/* 266 */         if (ksIS != null) {
/*     */           try {
/* 268 */             ksIS.close();
/*     */           }
/*     */           catch (IOException e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 276 */     if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreUrl))
/*     */     {
/* 278 */       InputStream ksIS = null;
/*     */       try {
/* 280 */         if (!StringUtils.isNullOrEmpty(trustCertificateKeyStoreType)) {
/* 281 */           KeyStore trustKeyStore = KeyStore.getInstance(trustCertificateKeyStoreType);
/* 282 */           URL ksURL = new URL(trustCertificateKeyStoreUrl);
/*     */           
/* 284 */           char[] password = trustCertificateKeyStorePassword == null ? new char[0] : trustCertificateKeyStorePassword.toCharArray();
/* 285 */           ksIS = ksURL.openStream();
/* 286 */           trustKeyStore.load(ksIS, password);
/* 287 */           tmf.init(trustKeyStore);
/*     */         }
/*     */       } catch (NoSuchAlgorithmException nsae) {
/* 290 */         throw SQLError.createSQLException("Unsupported keystore algorithm [" + nsae.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (KeyStoreException kse) {
/* 293 */         throw SQLError.createSQLException("Could not create KeyStore instance [" + kse.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (CertificateException nsae) {
/* 296 */         throw SQLError.createSQLException("Could not load trust" + trustCertificateKeyStoreType + " keystore from " + trustCertificateKeyStoreUrl, "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (MalformedURLException mue) {
/* 299 */         throw SQLError.createSQLException(trustCertificateKeyStoreUrl + " does not appear to be a valid URL.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */       }
/*     */       catch (IOException ioe) {
/* 302 */         SQLException sqlEx = SQLError.createSQLException("Cannot open " + trustCertificateKeyStoreUrl + " [" + ioe.getMessage() + "]", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */         
/*     */ 
/* 305 */         sqlEx.initCause(ioe);
/*     */         
/* 307 */         throw sqlEx;
/*     */       } finally {
/* 309 */         if (ksIS != null) {
/*     */           try {
/* 311 */             ksIS.close();
/*     */           }
/*     */           catch (IOException e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 319 */     SSLContext sslContext = null;
/*     */     try
/*     */     {
/* 322 */       sslContext = SSLContext.getInstance("TLS");
/* 323 */       sslContext.init(StringUtils.isNullOrEmpty(clientCertificateKeyStoreUrl) ? null : kmf.getKeyManagers(), new X509TrustManager[] { mysqlIO.connection.getVerifyServerCertificate() ? tmf.getTrustManagers() : new X509TrustManager()
/*     */       {
/*     */         public void checkClientTrusted(X509Certificate[] chain, String authType) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void checkServerTrusted(X509Certificate[] chain, String authType)
/*     */           throws CertificateException
/*     */         {}
/*     */         
/*     */ 
/* 334 */         public X509Certificate[] getAcceptedIssuers() { return null; } } }, null);
/*     */       
/*     */ 
/*     */ 
/* 338 */       return sslContext.getSocketFactory();
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 340 */       throw SQLError.createSQLException("TLS is not a valid SSL protocol.", "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */     } catch (KeyManagementException kme) {
/* 342 */       throw SQLError.createSQLException("KeyManagementException: " + kme.getMessage(), "08000", 0, false, mysqlIO.getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isSSLEstablished(MysqlIO mysqlIO)
/*     */   {
/* 348 */     return SSLSocket.class.isAssignableFrom(mysqlIO.mysqlConnection.getClass());
/*     */   }
/*     */   
/*     */   public static RSAPublicKey decodeRSAPublicKey(String key, ExceptionInterceptor interceptor) throws SQLException
/*     */   {
/*     */     try {
/* 354 */       if (key == null) {
/* 355 */         throw new SQLException("key parameter is null");
/*     */       }
/*     */       
/* 358 */       int offset = key.indexOf("\n") + 1;
/* 359 */       int len = key.indexOf("-----END PUBLIC KEY-----") - offset;
/*     */       
/*     */ 
/* 362 */       byte[] certificateData = Base64Decoder.decode(key.getBytes(), offset, len);
/*     */       
/* 364 */       X509EncodedKeySpec spec = new X509EncodedKeySpec(certificateData);
/* 365 */       KeyFactory kf = KeyFactory.getInstance("RSA");
/* 366 */       return (RSAPublicKey)kf.generatePublic(spec);
/*     */     } catch (Exception ex) {
/* 368 */       throw SQLError.createSQLException("Unable to decode public key", "S1009", ex, interceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   public static byte[] encryptWithRSAPublicKey(byte[] source, RSAPublicKey key, ExceptionInterceptor interceptor) throws SQLException {
/*     */     try {
/* 374 */       Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
/* 375 */       cipher.init(1, key);
/* 376 */       return cipher.doFinal(source);
/*     */     } catch (Exception ex) {
/* 378 */       throw SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ExportControlled.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */