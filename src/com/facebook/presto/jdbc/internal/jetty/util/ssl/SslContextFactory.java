/*      */ package com.facebook.presto.jdbc.internal.jetty.util.ssl;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.security.CertificateUtils;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.security.CertificateValidator;
/*      */ import com.facebook.presto.jdbc.internal.jetty.util.security.Password;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.security.KeyStore;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.Security;
/*      */ import java.security.cert.CRL;
/*      */ import java.security.cert.CertStore;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateFactory;
/*      */ import java.security.cert.CollectionCertStoreParameters;
/*      */ import java.security.cert.PKIXBuilderParameters;
/*      */ import java.security.cert.X509CertSelector;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.net.ssl.CertPathTrustManagerParameters;
/*      */ import javax.net.ssl.KeyManager;
/*      */ import javax.net.ssl.KeyManagerFactory;
/*      */ import javax.net.ssl.SNIHostName;
/*      */ import javax.net.ssl.SNIMatcher;
/*      */ import javax.net.ssl.SNIServerName;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLEngine;
/*      */ import javax.net.ssl.SSLParameters;
/*      */ import javax.net.ssl.SSLPeerUnverifiedException;
/*      */ import javax.net.ssl.SSLServerSocket;
/*      */ import javax.net.ssl.SSLServerSocketFactory;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSocket;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import javax.net.ssl.TrustManagerFactory;
/*      */ import javax.net.ssl.X509ExtendedKeyManager;
/*      */ import javax.net.ssl.X509TrustManager;
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
/*      */ 
/*      */ 
/*      */ public class SslContextFactory
/*      */   extends AbstractLifeCycle
/*      */ {
/*   92 */   public static final TrustManager[] TRUST_ALL_CERTS = { new X509TrustManager()
/*      */   {
/*      */     public X509Certificate[] getAcceptedIssuers()
/*      */     {
/*   96 */       return new X509Certificate[0];
/*      */     }
/*      */     
/*      */     public void checkClientTrusted(X509Certificate[] certs, String authType) {}
/*      */     
/*      */     public void checkServerTrusted(X509Certificate[] certs, String authType) {}
/*   92 */   } };
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
/*  108 */   static final Logger LOG = Log.getLogger(SslContextFactory.class);
/*      */   
/*      */ 
/*  111 */   public static final String DEFAULT_KEYMANAGERFACTORY_ALGORITHM = Security.getProperty("ssl.KeyManagerFactory.algorithm") == null ? 
/*  112 */     KeyManagerFactory.getDefaultAlgorithm() : Security.getProperty("ssl.KeyManagerFactory.algorithm");
/*      */   
/*      */ 
/*  115 */   public static final String DEFAULT_TRUSTMANAGERFACTORY_ALGORITHM = Security.getProperty("ssl.TrustManagerFactory.algorithm") == null ? 
/*  116 */     TrustManagerFactory.getDefaultAlgorithm() : Security.getProperty("ssl.TrustManagerFactory.algorithm");
/*      */   
/*      */ 
/*      */   public static final String KEYPASSWORD_PROPERTY = "com.facebook.presto.jdbc.internal.jetty.ssl.keypassword";
/*      */   
/*      */ 
/*      */   public static final String PASSWORD_PROPERTY = "com.facebook.presto.jdbc.internal.jetty.ssl.password";
/*      */   
/*      */ 
/*  125 */   private final Set<String> _excludeProtocols = new LinkedHashSet();
/*      */   
/*      */ 
/*  128 */   private final Set<String> _includeProtocols = new LinkedHashSet();
/*      */   
/*      */ 
/*      */   private String[] _selectedProtocols;
/*      */   
/*      */ 
/*  134 */   private final Set<String> _excludeCipherSuites = new LinkedHashSet();
/*      */   
/*      */ 
/*  137 */   private final List<String> _includeCipherSuites = new ArrayList();
/*  138 */   private boolean _useCipherSuitesOrder = true;
/*      */   
/*      */ 
/*      */   Comparator<String> _cipherComparator;
/*      */   
/*      */ 
/*      */   private String[] _selectedCipherSuites;
/*      */   
/*      */ 
/*      */   private Resource _keyStoreResource;
/*      */   
/*      */   private String _keyStoreProvider;
/*      */   
/*  151 */   private String _keyStoreType = "JKS";
/*      */   
/*      */   private String _certAlias;
/*      */   
/*  155 */   private final Map<String, X509> _aliasX509 = new HashMap();
/*  156 */   private final Map<String, X509> _certHosts = new HashMap();
/*  157 */   private final Map<String, X509> _certWilds = new HashMap();
/*      */   
/*      */ 
/*      */   private Resource _trustStoreResource;
/*      */   
/*      */   private String _trustStoreProvider;
/*      */   
/*  164 */   private String _trustStoreType = "JKS";
/*      */   
/*      */ 
/*  167 */   private boolean _needClientAuth = false;
/*      */   
/*  169 */   private boolean _wantClientAuth = false;
/*      */   
/*      */ 
/*      */   private Password _keyStorePassword;
/*      */   
/*      */ 
/*      */   private Password _keyManagerPassword;
/*      */   
/*      */   private Password _trustStorePassword;
/*      */   
/*      */   private String _sslProvider;
/*      */   
/*  181 */   private String _sslProtocol = "TLS";
/*      */   
/*      */ 
/*      */   private String _secureRandomAlgorithm;
/*      */   
/*  186 */   private String _keyManagerFactoryAlgorithm = DEFAULT_KEYMANAGERFACTORY_ALGORITHM;
/*      */   
/*  188 */   private String _trustManagerFactoryAlgorithm = DEFAULT_TRUSTMANAGERFACTORY_ALGORITHM;
/*      */   
/*      */ 
/*      */   private boolean _validateCerts;
/*      */   
/*      */   private boolean _validatePeerCerts;
/*      */   
/*  195 */   private int _maxCertPathLength = -1;
/*      */   
/*      */   private String _crlPath;
/*      */   
/*  199 */   private boolean _enableCRLDP = false;
/*      */   
/*  201 */   private boolean _enableOCSP = false;
/*      */   
/*      */ 
/*      */   private String _ocspResponderURL;
/*      */   
/*      */   private KeyStore _setKeyStore;
/*      */   
/*      */   private KeyStore _setTrustStore;
/*      */   
/*  210 */   private boolean _sessionCachingEnabled = true;
/*      */   
/*      */ 
/*      */   private int _sslSessionCacheSize;
/*      */   
/*      */ 
/*      */   private int _sslSessionTimeout;
/*      */   
/*      */   private SSLContext _setContext;
/*      */   
/*  220 */   private String _endpointIdentificationAlgorithm = null;
/*      */   
/*      */ 
/*      */   private boolean _trustAll;
/*      */   
/*      */ 
/*  226 */   private boolean _renegotiationAllowed = true;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Factory _factory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SslContextFactory()
/*      */   {
/*  239 */     this(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SslContextFactory(boolean trustAll)
/*      */   {
/*  250 */     setTrustAll(trustAll);
/*  251 */     addExcludeProtocols(new String[] { "SSL", "SSLv2", "SSLv2Hello", "SSLv3" });
/*  252 */     setExcludeCipherSuites(new String[] { "^.*_RSA_.*_(MD5|SHA|SHA1)$", "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA" });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SslContextFactory(String keyStorePath)
/*      */   {
/*  264 */     setKeyStorePath(keyStorePath);
/*      */   }
/*      */   
/*      */   public String[] getSelectedProtocols()
/*      */   {
/*  269 */     return (String[])Arrays.copyOf(this._selectedProtocols, this._selectedProtocols.length);
/*      */   }
/*      */   
/*      */   public String[] getSelectedCipherSuites()
/*      */   {
/*  274 */     return (String[])Arrays.copyOf(this._selectedCipherSuites, this._selectedCipherSuites.length);
/*      */   }
/*      */   
/*      */   public Comparator<String> getCipherComparator()
/*      */   {
/*  279 */     return this._cipherComparator;
/*      */   }
/*      */   
/*      */   public void setCipherComparator(Comparator<String> cipherComparator)
/*      */   {
/*  284 */     if (cipherComparator != null)
/*  285 */       setUseCipherSuitesOrder(true);
/*  286 */     this._cipherComparator = cipherComparator;
/*      */   }
/*      */   
/*      */   public Set<String> getAliases()
/*      */   {
/*  291 */     return Collections.unmodifiableSet(this._aliasX509.keySet());
/*      */   }
/*      */   
/*      */   public X509 getX509(String alias)
/*      */   {
/*  296 */     return (X509)this._aliasX509.get(alias);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void doStart()
/*      */     throws Exception
/*      */   {
/*  306 */     SSLContext context = this._setContext;
/*  307 */     KeyStore keyStore = this._setKeyStore;
/*  308 */     KeyStore trustStore = this._setTrustStore;
/*      */     
/*  310 */     if (context == null)
/*      */     {
/*      */       SecureRandom secureRandom;
/*  313 */       if ((keyStore == null) && (this._keyStoreResource == null) && (trustStore == null) && (this._trustStoreResource == null))
/*      */       {
/*  315 */         TrustManager[] trust_managers = null;
/*      */         
/*  317 */         if (this._trustAll)
/*      */         {
/*  319 */           if (LOG.isDebugEnabled()) {
/*  320 */             LOG.debug("No keystore or trust store configured.  ACCEPTING UNTRUSTED CERTIFICATES!!!!!", new Object[0]);
/*      */           }
/*  322 */           trust_managers = TRUST_ALL_CERTS;
/*      */         }
/*      */         
/*  325 */         secureRandom = this._secureRandomAlgorithm == null ? null : SecureRandom.getInstance(this._secureRandomAlgorithm);
/*  326 */         context = this._sslProvider == null ? SSLContext.getInstance(this._sslProtocol) : SSLContext.getInstance(this._sslProtocol, this._sslProvider);
/*  327 */         context.init(null, trust_managers, secureRandom);
/*      */       }
/*      */       else
/*      */       {
/*  331 */         if (keyStore == null)
/*  332 */           keyStore = loadKeyStore(this._keyStoreResource);
/*  333 */         if (trustStore == null) {
/*  334 */           trustStore = loadTrustStore(this._trustStoreResource);
/*      */         }
/*  336 */         Collection<? extends CRL> crls = loadCRL(this._crlPath);
/*      */         
/*      */ 
/*  339 */         this._certHosts.clear();
/*  340 */         if (keyStore != null)
/*      */         {
/*  342 */           for (String alias : Collections.list(keyStore.aliases()))
/*      */           {
/*  344 */             Certificate certificate = keyStore.getCertificate(alias);
/*  345 */             if ((certificate != null) && ("X.509".equals(certificate.getType())))
/*      */             {
/*  347 */               X509Certificate x509C = (X509Certificate)certificate;
/*      */               
/*      */ 
/*  350 */               if (X509.isCertSign(x509C))
/*      */               {
/*  352 */                 if (LOG.isDebugEnabled()) {
/*  353 */                   LOG.debug("Skipping " + x509C, new Object[0]);
/*      */                 }
/*      */               } else {
/*  356 */                 x509 = new X509(alias, x509C);
/*  357 */                 this._aliasX509.put(alias, x509);
/*      */                 
/*  359 */                 if (this._validateCerts)
/*      */                 {
/*  361 */                   validator = new CertificateValidator(trustStore, crls);
/*  362 */                   validator.setMaxCertPathLength(this._maxCertPathLength);
/*  363 */                   validator.setEnableCRLDP(this._enableCRLDP);
/*  364 */                   validator.setEnableOCSP(this._enableOCSP);
/*  365 */                   validator.setOcspResponderURL(this._ocspResponderURL);
/*  366 */                   validator.validate(keyStore, x509C);
/*      */                 }
/*      */                 
/*  369 */                 LOG.info("x509={} for {}", new Object[] { x509, this });
/*      */                 
/*  371 */                 for (String h : x509.getHosts())
/*  372 */                   this._certHosts.put(h, x509);
/*  373 */                 for (String w : x509.getWilds())
/*  374 */                   this._certWilds.put(w, x509);
/*      */               }
/*      */             }
/*      */           } }
/*      */         X509 x509;
/*      */         CertificateValidator validator;
/*  380 */         KeyManager[] keyManagers = getKeyManagers(keyStore);
/*  381 */         TrustManager[] trustManagers = getTrustManagers(trustStore, crls);
/*      */         
/*      */ 
/*  384 */         SecureRandom secureRandom = this._secureRandomAlgorithm == null ? null : SecureRandom.getInstance(this._secureRandomAlgorithm);
/*  385 */         context = this._sslProvider == null ? SSLContext.getInstance(this._sslProtocol) : SSLContext.getInstance(this._sslProtocol, this._sslProvider);
/*  386 */         context.init(keyManagers, trustManagers, secureRandom);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  391 */     SSLEngine sslEngine = context.createSSLEngine();
/*  392 */     selectCipherSuites(sslEngine
/*  393 */       .getEnabledCipherSuites(), sslEngine
/*  394 */       .getSupportedCipherSuites());
/*  395 */     selectProtocols(sslEngine.getEnabledProtocols(), sslEngine.getSupportedProtocols());
/*      */     
/*  397 */     this._factory = new Factory(keyStore, trustStore, context);
/*  398 */     if (LOG.isDebugEnabled())
/*      */     {
/*  400 */       LOG.debug("Selected Protocols {} of {}", new Object[] { Arrays.asList(this._selectedProtocols), Arrays.asList(sslEngine.getSupportedProtocols()) });
/*  401 */       LOG.debug("Selected Ciphers   {} of {}", new Object[] { Arrays.asList(this._selectedCipherSuites), Arrays.asList(sslEngine.getSupportedCipherSuites()) });
/*      */     }
/*      */   }
/*      */   
/*      */   protected void doStop()
/*      */     throws Exception
/*      */   {
/*  408 */     this._factory = null;
/*  409 */     super.doStop();
/*  410 */     this._certHosts.clear();
/*  411 */     this._certWilds.clear();
/*  412 */     this._aliasX509.clear();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getExcludeProtocols()
/*      */   {
/*  421 */     return (String[])this._excludeProtocols.toArray(new String[this._excludeProtocols.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExcludeProtocols(String... protocols)
/*      */   {
/*  431 */     checkNotStarted();
/*  432 */     this._excludeProtocols.clear();
/*  433 */     this._excludeProtocols.addAll(Arrays.asList(protocols));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addExcludeProtocols(String... protocol)
/*      */   {
/*  441 */     checkNotStarted();
/*  442 */     this._excludeProtocols.addAll(Arrays.asList(protocol));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getIncludeProtocols()
/*      */   {
/*  451 */     return (String[])this._includeProtocols.toArray(new String[this._includeProtocols.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setIncludeProtocols(String... protocols)
/*      */   {
/*  461 */     checkNotStarted();
/*  462 */     this._includeProtocols.clear();
/*  463 */     this._includeProtocols.addAll(Arrays.asList(protocols));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getExcludeCipherSuites()
/*      */   {
/*  472 */     return (String[])this._excludeCipherSuites.toArray(new String[this._excludeCipherSuites.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExcludeCipherSuites(String... cipherSuites)
/*      */   {
/*  483 */     checkNotStarted();
/*  484 */     this._excludeCipherSuites.clear();
/*  485 */     this._excludeCipherSuites.addAll(Arrays.asList(cipherSuites));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addExcludeCipherSuites(String... cipher)
/*      */   {
/*  493 */     checkNotStarted();
/*  494 */     this._excludeCipherSuites.addAll(Arrays.asList(cipher));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getIncludeCipherSuites()
/*      */   {
/*  503 */     return (String[])this._includeCipherSuites.toArray(new String[this._includeCipherSuites.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setIncludeCipherSuites(String... cipherSuites)
/*      */   {
/*  514 */     checkNotStarted();
/*  515 */     this._includeCipherSuites.clear();
/*  516 */     this._includeCipherSuites.addAll(Arrays.asList(cipherSuites));
/*      */   }
/*      */   
/*      */   public boolean isUseCipherSuitesOrder()
/*      */   {
/*  521 */     return this._useCipherSuitesOrder;
/*      */   }
/*      */   
/*      */   public void setUseCipherSuitesOrder(boolean useCipherSuitesOrder)
/*      */   {
/*  526 */     this._useCipherSuitesOrder = useCipherSuitesOrder;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getKeyStorePath()
/*      */   {
/*  534 */     return this._keyStoreResource.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyStorePath(String keyStorePath)
/*      */   {
/*  543 */     checkNotStarted();
/*      */     try
/*      */     {
/*  546 */       this._keyStoreResource = Resource.newResource(keyStorePath);
/*      */     }
/*      */     catch (MalformedURLException e)
/*      */     {
/*  550 */       throw new IllegalArgumentException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getKeyStoreProvider()
/*      */   {
/*  559 */     return this._keyStoreProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyStoreProvider(String keyStoreProvider)
/*      */   {
/*  568 */     checkNotStarted();
/*  569 */     this._keyStoreProvider = keyStoreProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getKeyStoreType()
/*      */   {
/*  577 */     return this._keyStoreType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyStoreType(String keyStoreType)
/*      */   {
/*  586 */     checkNotStarted();
/*  587 */     this._keyStoreType = keyStoreType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCertAlias()
/*      */   {
/*  595 */     return this._certAlias;
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
/*      */   public void setCertAlias(String certAlias)
/*      */   {
/*  609 */     checkNotStarted();
/*  610 */     this._certAlias = certAlias;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustStorePath(String trustStorePath)
/*      */   {
/*  619 */     checkNotStarted();
/*      */     try
/*      */     {
/*  622 */       this._trustStoreResource = Resource.newResource(trustStorePath);
/*      */     }
/*      */     catch (MalformedURLException e)
/*      */     {
/*  626 */       throw new IllegalArgumentException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustStoreProvider()
/*      */   {
/*  635 */     return this._trustStoreProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustStoreProvider(String trustStoreProvider)
/*      */   {
/*  644 */     checkNotStarted();
/*  645 */     this._trustStoreProvider = trustStoreProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustStoreType()
/*      */   {
/*  653 */     return this._trustStoreType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustStoreType(String trustStoreType)
/*      */   {
/*  662 */     checkNotStarted();
/*  663 */     this._trustStoreType = trustStoreType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getNeedClientAuth()
/*      */   {
/*  672 */     return this._needClientAuth;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setNeedClientAuth(boolean needClientAuth)
/*      */   {
/*  682 */     checkNotStarted();
/*  683 */     this._needClientAuth = needClientAuth;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean getWantClientAuth()
/*      */   {
/*  692 */     return this._wantClientAuth;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setWantClientAuth(boolean wantClientAuth)
/*      */   {
/*  702 */     checkNotStarted();
/*  703 */     this._wantClientAuth = wantClientAuth;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isValidateCerts()
/*      */   {
/*  711 */     return this._validateCerts;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setValidateCerts(boolean validateCerts)
/*      */   {
/*  720 */     checkNotStarted();
/*  721 */     this._validateCerts = validateCerts;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isValidatePeerCerts()
/*      */   {
/*  729 */     return this._validatePeerCerts;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setValidatePeerCerts(boolean validatePeerCerts)
/*      */   {
/*  738 */     checkNotStarted();
/*  739 */     this._validatePeerCerts = validatePeerCerts;
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
/*      */   public void setKeyStorePassword(String password)
/*      */   {
/*  752 */     checkNotStarted();
/*  753 */     if (password == null)
/*      */     {
/*  755 */       if (this._keyStoreResource != null) {
/*  756 */         this._keyStorePassword = Password.getPassword("com.facebook.presto.jdbc.internal.jetty.ssl.password", null, null);
/*      */       } else {
/*  758 */         this._keyStorePassword = null;
/*      */       }
/*      */     } else {
/*  761 */       this._keyStorePassword = new Password(password);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyManagerPassword(String password)
/*      */   {
/*  773 */     checkNotStarted();
/*  774 */     if (password == null)
/*      */     {
/*  776 */       if (System.getProperty("com.facebook.presto.jdbc.internal.jetty.ssl.keypassword") != null) {
/*  777 */         this._keyManagerPassword = Password.getPassword("com.facebook.presto.jdbc.internal.jetty.ssl.keypassword", null, null);
/*      */       } else {
/*  779 */         this._keyManagerPassword = null;
/*      */       }
/*      */     } else {
/*  782 */       this._keyManagerPassword = new Password(password);
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
/*      */   public void setTrustStorePassword(String password)
/*      */   {
/*  795 */     checkNotStarted();
/*  796 */     if (password == null)
/*      */     {
/*      */ 
/*  799 */       if ((this._trustStoreResource != null) && (!this._trustStoreResource.equals(this._keyStoreResource))) {
/*  800 */         this._trustStorePassword = Password.getPassword("com.facebook.presto.jdbc.internal.jetty.ssl.password", null, null);
/*      */       } else {
/*  802 */         this._trustStorePassword = null;
/*      */       }
/*      */     } else {
/*  805 */       this._trustStorePassword = new Password(password);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getProvider()
/*      */   {
/*  814 */     return this._sslProvider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setProvider(String provider)
/*      */   {
/*  824 */     checkNotStarted();
/*  825 */     this._sslProvider = provider;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getProtocol()
/*      */   {
/*  834 */     return this._sslProtocol;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setProtocol(String protocol)
/*      */   {
/*  844 */     checkNotStarted();
/*  845 */     this._sslProtocol = protocol;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSecureRandomAlgorithm()
/*      */   {
/*  855 */     return this._secureRandomAlgorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSecureRandomAlgorithm(String algorithm)
/*      */   {
/*  866 */     checkNotStarted();
/*  867 */     this._secureRandomAlgorithm = algorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSslKeyManagerFactoryAlgorithm()
/*      */   {
/*  875 */     return this._keyManagerFactoryAlgorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSslKeyManagerFactoryAlgorithm(String algorithm)
/*      */   {
/*  884 */     checkNotStarted();
/*  885 */     this._keyManagerFactoryAlgorithm = algorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrustManagerFactoryAlgorithm()
/*      */   {
/*  893 */     return this._trustManagerFactoryAlgorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isTrustAll()
/*      */   {
/*  901 */     return this._trustAll;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustAll(boolean trustAll)
/*      */   {
/*  909 */     this._trustAll = trustAll;
/*  910 */     if (trustAll) {
/*  911 */       setEndpointIdentificationAlgorithm(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustManagerFactoryAlgorithm(String algorithm)
/*      */   {
/*  921 */     checkNotStarted();
/*  922 */     this._trustManagerFactoryAlgorithm = algorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isRenegotiationAllowed()
/*      */   {
/*  930 */     return this._renegotiationAllowed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRenegotiationAllowed(boolean renegotiationAllowed)
/*      */   {
/*  938 */     this._renegotiationAllowed = renegotiationAllowed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCrlPath()
/*      */   {
/*  946 */     return this._crlPath;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCrlPath(String crlPath)
/*      */   {
/*  955 */     checkNotStarted();
/*  956 */     this._crlPath = crlPath;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxCertPathLength()
/*      */   {
/*  965 */     return this._maxCertPathLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMaxCertPathLength(int maxCertPathLength)
/*      */   {
/*  975 */     checkNotStarted();
/*  976 */     this._maxCertPathLength = maxCertPathLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SSLContext getSslContext()
/*      */   {
/*  984 */     return isStarted() ? this._factory._context : this._setContext;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSslContext(SSLContext sslContext)
/*      */   {
/*  993 */     checkNotStarted();
/*  994 */     this._setContext = sslContext;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEndpointIdentificationAlgorithm(String endpointIdentificationAlgorithm)
/*      */   {
/* 1004 */     this._endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected KeyStore loadKeyStore(Resource resource)
/*      */     throws Exception
/*      */   {
/* 1016 */     return CertificateUtils.getKeyStore(resource, this._keyStoreType, this._keyStoreProvider, this._keyStorePassword == null ? null : this._keyStorePassword.toString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected KeyStore loadTrustStore(Resource resource)
/*      */     throws Exception
/*      */   {
/* 1028 */     String type = this._trustStoreType;
/* 1029 */     String provider = this._trustStoreProvider;
/* 1030 */     String passwd = this._trustStorePassword == null ? null : this._trustStorePassword.toString();
/* 1031 */     if ((resource == null) || (resource.equals(this._keyStoreResource)))
/*      */     {
/* 1033 */       resource = this._keyStoreResource;
/* 1034 */       if (type == null)
/* 1035 */         type = this._keyStoreType;
/* 1036 */       if (provider == null)
/* 1037 */         provider = this._keyStoreProvider;
/* 1038 */       if (passwd == null) {
/* 1039 */         passwd = this._keyStorePassword == null ? null : this._keyStorePassword.toString();
/*      */       }
/*      */     }
/* 1042 */     return CertificateUtils.getKeyStore(resource, type, provider, passwd);
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
/*      */   protected Collection<? extends CRL> loadCRL(String crlPath)
/*      */     throws Exception
/*      */   {
/* 1057 */     return CertificateUtils.loadCRL(crlPath);
/*      */   }
/*      */   
/*      */   protected KeyManager[] getKeyManagers(KeyStore keyStore) throws Exception
/*      */   {
/* 1062 */     KeyManager[] managers = null;
/*      */     
/* 1064 */     if (keyStore != null)
/*      */     {
/* 1066 */       KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this._keyManagerFactoryAlgorithm);
/* 1067 */       keyManagerFactory.init(keyStore, this._keyManagerPassword == null ? this._keyStorePassword.toString().toCharArray() : this._keyStorePassword == null ? null : this._keyManagerPassword.toString().toCharArray());
/* 1068 */       managers = keyManagerFactory.getKeyManagers();
/*      */       
/* 1070 */       if (managers != null)
/*      */       {
/* 1072 */         if (this._certAlias != null)
/*      */         {
/* 1074 */           for (int idx = 0; idx < managers.length; idx++)
/*      */           {
/* 1076 */             if ((managers[idx] instanceof X509ExtendedKeyManager)) {
/* 1077 */               managers[idx] = new AliasedX509ExtendedKeyManager((X509ExtendedKeyManager)managers[idx], this._certAlias);
/*      */             }
/*      */           }
/*      */         }
/* 1081 */         if ((!this._certHosts.isEmpty()) || (!this._certWilds.isEmpty()))
/*      */         {
/* 1083 */           for (int idx = 0; idx < managers.length; idx++)
/*      */           {
/* 1085 */             if ((managers[idx] instanceof X509ExtendedKeyManager)) {
/* 1086 */               managers[idx] = new SniX509ExtendedKeyManager((X509ExtendedKeyManager)managers[idx]);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1092 */     LOG.debug("managers={} for {}", new Object[] { managers, this });
/*      */     
/* 1094 */     return managers;
/*      */   }
/*      */   
/*      */   protected TrustManager[] getTrustManagers(KeyStore trustStore, Collection<? extends CRL> crls) throws Exception
/*      */   {
/* 1099 */     TrustManager[] managers = null;
/* 1100 */     if (trustStore != null)
/*      */     {
/*      */ 
/* 1103 */       if ((this._validatePeerCerts) && (this._trustManagerFactoryAlgorithm.equalsIgnoreCase("PKIX")))
/*      */       {
/* 1105 */         PKIXBuilderParameters pbParams = new PKIXBuilderParameters(trustStore, new X509CertSelector());
/*      */         
/*      */ 
/* 1108 */         pbParams.setMaxPathLength(this._maxCertPathLength);
/*      */         
/*      */ 
/* 1111 */         pbParams.setRevocationEnabled(true);
/*      */         
/* 1113 */         if ((crls != null) && (!crls.isEmpty()))
/*      */         {
/* 1115 */           pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(crls)));
/*      */         }
/*      */         
/* 1118 */         if (this._enableCRLDP)
/*      */         {
/*      */ 
/* 1121 */           System.setProperty("com.sun.security.enableCRLDP", "true");
/*      */         }
/*      */         
/* 1124 */         if (this._enableOCSP)
/*      */         {
/*      */ 
/* 1127 */           Security.setProperty("ocsp.enable", "true");
/*      */           
/* 1129 */           if (this._ocspResponderURL != null)
/*      */           {
/*      */ 
/* 1132 */             Security.setProperty("ocsp.responderURL", this._ocspResponderURL);
/*      */           }
/*      */         }
/*      */         
/* 1136 */         TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this._trustManagerFactoryAlgorithm);
/* 1137 */         trustManagerFactory.init(new CertPathTrustManagerParameters(pbParams));
/*      */         
/* 1139 */         managers = trustManagerFactory.getTrustManagers();
/*      */       }
/*      */       else
/*      */       {
/* 1143 */         TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this._trustManagerFactoryAlgorithm);
/* 1144 */         trustManagerFactory.init(trustStore);
/*      */         
/* 1146 */         managers = trustManagerFactory.getTrustManagers();
/*      */       }
/*      */     }
/*      */     
/* 1150 */     return managers;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void selectProtocols(String[] enabledProtocols, String[] supportedProtocols)
/*      */   {
/* 1162 */     Set<String> selected_protocols = new LinkedHashSet();
/*      */     
/*      */ 
/* 1165 */     if (!this._includeProtocols.isEmpty())
/*      */     {
/*      */ 
/* 1168 */       for (String protocol : this._includeProtocols)
/*      */       {
/* 1170 */         if (Arrays.asList(supportedProtocols).contains(protocol)) {
/* 1171 */           selected_protocols.add(protocol);
/*      */         } else {
/* 1173 */           LOG.info("Protocol {} not supported in {}", new Object[] { protocol, Arrays.asList(supportedProtocols) });
/*      */         }
/*      */       }
/*      */     } else {
/* 1177 */       selected_protocols.addAll(Arrays.asList(enabledProtocols));
/*      */     }
/*      */     
/*      */ 
/* 1181 */     selected_protocols.removeAll(this._excludeProtocols);
/*      */     
/*      */ 
/* 1184 */     if (selected_protocols.isEmpty()) {
/* 1185 */       LOG.warn("No selected protocols from {}", new Object[] { Arrays.asList(supportedProtocols) });
/*      */     }
/* 1187 */     this._selectedProtocols = ((String[])selected_protocols.toArray(new String[selected_protocols.size()]));
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
/*      */   protected void selectCipherSuites(String[] enabledCipherSuites, String[] supportedCipherSuites)
/*      */   {
/* 1202 */     List<String> selected_ciphers = new ArrayList();
/*      */     
/*      */ 
/* 1205 */     if (this._includeCipherSuites.isEmpty()) {
/* 1206 */       selected_ciphers.addAll(Arrays.asList(enabledCipherSuites));
/*      */     } else {
/* 1208 */       processIncludeCipherSuites(supportedCipherSuites, selected_ciphers);
/*      */     }
/* 1210 */     removeExcludedCipherSuites(selected_ciphers);
/*      */     
/* 1212 */     if (selected_ciphers.isEmpty()) {
/* 1213 */       LOG.warn("No supported ciphers from {}", new Object[] { Arrays.asList(supportedCipherSuites) });
/*      */     }
/* 1215 */     if (this._cipherComparator != null)
/*      */     {
/* 1217 */       if (LOG.isDebugEnabled())
/* 1218 */         LOG.debug("Sorting selected ciphers with {}", new Object[] { this._cipherComparator });
/* 1219 */       Collections.sort(selected_ciphers, this._cipherComparator);
/*      */     }
/*      */     
/* 1222 */     this._selectedCipherSuites = ((String[])selected_ciphers.toArray(new String[selected_ciphers.size()]));
/*      */   }
/*      */   
/*      */   protected void processIncludeCipherSuites(String[] supportedCipherSuites, List<String> selected_ciphers)
/*      */   {
/* 1227 */     for (String cipherSuite : this._includeCipherSuites)
/*      */     {
/* 1229 */       Pattern p = Pattern.compile(cipherSuite);
/* 1230 */       boolean added = false;
/* 1231 */       for (String supportedCipherSuite : supportedCipherSuites)
/*      */       {
/* 1233 */         Matcher m = p.matcher(supportedCipherSuite);
/* 1234 */         if (m.matches())
/*      */         {
/* 1236 */           added = true;
/* 1237 */           selected_ciphers.add(supportedCipherSuite);
/*      */         }
/*      */       }
/*      */       
/* 1241 */       if (!added) {
/* 1242 */         LOG.info("No Cipher matching '{}' is supported", new Object[] { cipherSuite });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void removeExcludedCipherSuites(List<String> selected_ciphers) {
/* 1248 */     for (String excludeCipherSuite : this._excludeCipherSuites)
/*      */     {
/* 1250 */       excludeCipherPattern = Pattern.compile(excludeCipherSuite);
/* 1251 */       for (i = selected_ciphers.iterator(); i.hasNext();)
/*      */       {
/* 1253 */         String selectedCipherSuite = (String)i.next();
/* 1254 */         Matcher m = excludeCipherPattern.matcher(selectedCipherSuite);
/* 1255 */         if (m.matches()) {
/* 1256 */           i.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     Pattern excludeCipherPattern;
/*      */     Iterator<String> i;
/*      */   }
/*      */   
/*      */   protected void checkNotStarted()
/*      */   {
/* 1266 */     if (isStarted()) {
/* 1267 */       throw new IllegalStateException("Cannot modify configuration when " + getState());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkIsStarted()
/*      */   {
/* 1275 */     if (!isStarted()) {
/* 1276 */       throw new IllegalStateException("!STARTED: " + this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkIsRunning()
/*      */   {
/* 1284 */     if (!isRunning()) {
/* 1285 */       throw new IllegalStateException("!RUNNING: " + this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isEnableCRLDP()
/*      */   {
/* 1293 */     return this._enableCRLDP;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEnableCRLDP(boolean enableCRLDP)
/*      */   {
/* 1301 */     checkNotStarted();
/* 1302 */     this._enableCRLDP = enableCRLDP;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEnableOCSP()
/*      */   {
/* 1310 */     return this._enableOCSP;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEnableOCSP(boolean enableOCSP)
/*      */   {
/* 1318 */     checkNotStarted();
/* 1319 */     this._enableOCSP = enableOCSP;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getOcspResponderURL()
/*      */   {
/* 1327 */     return this._ocspResponderURL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setOcspResponderURL(String ocspResponderURL)
/*      */   {
/* 1335 */     checkNotStarted();
/* 1336 */     this._ocspResponderURL = ocspResponderURL;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyStore(KeyStore keyStore)
/*      */   {
/* 1344 */     checkNotStarted();
/* 1345 */     this._setKeyStore = keyStore;
/*      */   }
/*      */   
/*      */   public KeyStore getKeyStore()
/*      */   {
/* 1350 */     return isStarted() ? this._factory._keyStore : this._setKeyStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustStore(KeyStore trustStore)
/*      */   {
/* 1358 */     checkNotStarted();
/* 1359 */     this._setTrustStore = trustStore;
/*      */   }
/*      */   
/*      */   public KeyStore getTrustStore()
/*      */   {
/* 1364 */     return isStarted() ? this._factory._trustStore : this._setTrustStore;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setKeyStoreResource(Resource resource)
/*      */   {
/* 1372 */     checkNotStarted();
/* 1373 */     this._keyStoreResource = resource;
/*      */   }
/*      */   
/*      */   public Resource getKeyStoreResource()
/*      */   {
/* 1378 */     return this._keyStoreResource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrustStoreResource(Resource resource)
/*      */   {
/* 1386 */     checkNotStarted();
/* 1387 */     this._trustStoreResource = resource;
/*      */   }
/*      */   
/*      */   public Resource getTrustStoreResource()
/*      */   {
/* 1392 */     return this._trustStoreResource;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isSessionCachingEnabled()
/*      */   {
/* 1400 */     return this._sessionCachingEnabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSessionCachingEnabled(boolean enableSessionCaching)
/*      */   {
/* 1408 */     this._sessionCachingEnabled = enableSessionCaching;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSslSessionCacheSize()
/*      */   {
/* 1416 */     return this._sslSessionCacheSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSslSessionCacheSize(int sslSessionCacheSize)
/*      */   {
/* 1424 */     this._sslSessionCacheSize = sslSessionCacheSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSslSessionTimeout()
/*      */   {
/* 1432 */     return this._sslSessionTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSslSessionTimeout(int sslSessionTimeout)
/*      */   {
/* 1440 */     this._sslSessionTimeout = sslSessionTimeout;
/*      */   }
/*      */   
/*      */   public SSLServerSocket newSslServerSocket(String host, int port, int backlog)
/*      */     throws IOException
/*      */   {
/* 1446 */     checkIsStarted();
/*      */     
/* 1448 */     SSLServerSocketFactory factory = this._factory._context.getServerSocketFactory();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1453 */     SSLServerSocket socket = (SSLServerSocket)(host == null ? factory.createServerSocket(port, backlog) : factory.createServerSocket(port, backlog, InetAddress.getByName(host)));
/*      */     
/* 1455 */     if (getWantClientAuth())
/* 1456 */       socket.setWantClientAuth(getWantClientAuth());
/* 1457 */     if (getNeedClientAuth()) {
/* 1458 */       socket.setNeedClientAuth(getNeedClientAuth());
/*      */     }
/* 1460 */     socket.setEnabledCipherSuites(this._selectedCipherSuites);
/* 1461 */     socket.setEnabledProtocols(this._selectedProtocols);
/*      */     
/* 1463 */     return socket;
/*      */   }
/*      */   
/*      */   public SSLSocket newSslSocket() throws IOException
/*      */   {
/* 1468 */     checkIsStarted();
/*      */     
/* 1470 */     SSLSocketFactory factory = this._factory._context.getSocketFactory();
/*      */     
/* 1472 */     SSLSocket socket = (SSLSocket)factory.createSocket();
/*      */     
/* 1474 */     if (getWantClientAuth())
/* 1475 */       socket.setWantClientAuth(getWantClientAuth());
/* 1476 */     if (getNeedClientAuth()) {
/* 1477 */       socket.setNeedClientAuth(getNeedClientAuth());
/*      */     }
/* 1479 */     socket.setEnabledCipherSuites(this._selectedCipherSuites);
/* 1480 */     socket.setEnabledProtocols(this._selectedProtocols);
/*      */     
/* 1482 */     return socket;
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
/*      */   public SSLEngine newSSLEngine()
/*      */   {
/* 1496 */     checkIsRunning();
/* 1497 */     SSLEngine sslEngine = this._factory._context.createSSLEngine();
/* 1498 */     customize(sslEngine);
/* 1499 */     return sslEngine;
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
/*      */   public SSLEngine newSSLEngine(String host, int port)
/*      */   {
/* 1512 */     checkIsStarted();
/*      */     
/*      */ 
/* 1515 */     SSLEngine sslEngine = isSessionCachingEnabled() ? this._factory._context.createSSLEngine(host, port) : this._factory._context.createSSLEngine();
/* 1516 */     customize(sslEngine);
/* 1517 */     return sslEngine;
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
/*      */   public SSLEngine newSSLEngine(InetSocketAddress address)
/*      */   {
/* 1540 */     if (address == null) {
/* 1541 */       return newSSLEngine();
/*      */     }
/* 1543 */     boolean useHostName = getNeedClientAuth();
/* 1544 */     String hostName = useHostName ? address.getHostName() : address.getAddress().getHostAddress();
/* 1545 */     return newSSLEngine(hostName, address.getPort());
/*      */   }
/*      */   
/*      */   public void customize(SSLEngine sslEngine)
/*      */   {
/* 1550 */     if (LOG.isDebugEnabled()) {
/* 1551 */       LOG.debug("Customize {}", new Object[] { sslEngine });
/*      */     }
/* 1553 */     SSLParameters sslParams = sslEngine.getSSLParameters();
/* 1554 */     sslParams.setEndpointIdentificationAlgorithm(this._endpointIdentificationAlgorithm);
/* 1555 */     sslParams.setUseCipherSuitesOrder(this._useCipherSuitesOrder);
/* 1556 */     if ((!this._certHosts.isEmpty()) || (!this._certWilds.isEmpty()))
/*      */     {
/* 1558 */       if (LOG.isDebugEnabled())
/* 1559 */         LOG.debug("Enable SNI matching {}", new Object[] { sslEngine });
/* 1560 */       sslParams.setSNIMatchers(Collections.singletonList(new AliasSNIMatcher()));
/*      */     }
/* 1562 */     sslParams.setCipherSuites(this._selectedCipherSuites);
/* 1563 */     sslParams.setProtocols(this._selectedProtocols);
/*      */     
/* 1565 */     if (getWantClientAuth())
/* 1566 */       sslParams.setWantClientAuth(true);
/* 1567 */     if (getNeedClientAuth()) {
/* 1568 */       sslParams.setNeedClientAuth(true);
/*      */     }
/* 1570 */     sslEngine.setSSLParameters(sslParams);
/*      */   }
/*      */   
/*      */   public static X509Certificate[] getCertChain(SSLSession sslSession)
/*      */   {
/*      */     try
/*      */     {
/* 1577 */       Certificate[] javaxCerts = sslSession.getPeerCertificates();
/* 1578 */       if ((javaxCerts == null) || (javaxCerts.length == 0)) {
/* 1579 */         return null;
/*      */       }
/* 1581 */       int length = javaxCerts.length;
/* 1582 */       X509Certificate[] javaCerts = new X509Certificate[length];
/*      */       
/* 1584 */       CertificateFactory cf = CertificateFactory.getInstance("X.509");
/* 1585 */       for (int i = 0; i < length; i++)
/*      */       {
/* 1587 */         byte[] bytes = javaxCerts[i].getEncoded();
/* 1588 */         ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
/* 1589 */         javaCerts[i] = ((X509Certificate)cf.generateCertificate(stream));
/*      */       }
/*      */       
/* 1592 */       return javaCerts;
/*      */     }
/*      */     catch (SSLPeerUnverifiedException pue)
/*      */     {
/* 1596 */       return null;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1600 */       LOG.warn("EXCEPTION ", e); }
/* 1601 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int deduceKeyLength(String cipherSuite)
/*      */   {
/* 1634 */     if (cipherSuite == null)
/* 1635 */       return 0;
/* 1636 */     if (cipherSuite.contains("WITH_AES_256_"))
/* 1637 */       return 256;
/* 1638 */     if (cipherSuite.contains("WITH_RC4_128_"))
/* 1639 */       return 128;
/* 1640 */     if (cipherSuite.contains("WITH_AES_128_"))
/* 1641 */       return 128;
/* 1642 */     if (cipherSuite.contains("WITH_RC4_40_"))
/* 1643 */       return 40;
/* 1644 */     if (cipherSuite.contains("WITH_3DES_EDE_CBC_"))
/* 1645 */       return 168;
/* 1646 */     if (cipherSuite.contains("WITH_IDEA_CBC_"))
/* 1647 */       return 128;
/* 1648 */     if (cipherSuite.contains("WITH_RC2_CBC_40_"))
/* 1649 */       return 40;
/* 1650 */     if (cipherSuite.contains("WITH_DES40_CBC_"))
/* 1651 */       return 40;
/* 1652 */     if (cipherSuite.contains("WITH_DES_CBC_")) {
/* 1653 */       return 56;
/*      */     }
/* 1655 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1661 */     return String.format("%s@%x(%s,%s)", new Object[] {
/* 1662 */       getClass().getSimpleName(), 
/* 1663 */       Integer.valueOf(hashCode()), this._keyStoreResource, this._trustStoreResource });
/*      */   }
/*      */   
/*      */ 
/*      */   protected class Factory
/*      */   {
/*      */     final KeyStore _keyStore;
/*      */     
/*      */     final KeyStore _trustStore;
/*      */     
/*      */     final SSLContext _context;
/*      */     
/*      */     public Factory(KeyStore keyStore, KeyStore trustStore, SSLContext context)
/*      */     {
/* 1677 */       this._keyStore = keyStore;
/* 1678 */       this._trustStore = trustStore;
/* 1679 */       this._context = context;
/*      */     }
/*      */     
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1685 */       return String.format("SslFactory@%x{%s}", new Object[] { Integer.valueOf(System.identityHashCode(this)), SslContextFactory.this });
/*      */     }
/*      */   }
/*      */   
/*      */   class AliasSNIMatcher extends SNIMatcher
/*      */   {
/*      */     private String _host;
/*      */     private X509 _x509;
/*      */     
/*      */     AliasSNIMatcher()
/*      */     {
/* 1696 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean matches(SNIServerName serverName)
/*      */     {
/* 1702 */       if (SslContextFactory.LOG.isDebugEnabled()) {
/* 1703 */         SslContextFactory.LOG.debug("SNI matching for {}", new Object[] { serverName });
/*      */       }
/* 1705 */       if ((serverName instanceof SNIHostName))
/*      */       {
/* 1707 */         String host = this._host = ((SNIHostName)serverName).getAsciiName();
/* 1708 */         host = StringUtil.asciiToLowerCase(host);
/*      */         
/*      */ 
/* 1711 */         this._x509 = ((X509)SslContextFactory.this._certHosts.get(host));
/*      */         
/*      */ 
/* 1714 */         if (this._x509 == null)
/*      */         {
/* 1716 */           this._x509 = ((X509)SslContextFactory.this._certWilds.get(host));
/*      */           
/*      */ 
/* 1719 */           if (this._x509 == null)
/*      */           {
/* 1721 */             int dot = host.indexOf('.');
/* 1722 */             if (dot >= 0)
/*      */             {
/* 1724 */               String domain = host.substring(dot + 1);
/* 1725 */               this._x509 = ((X509)SslContextFactory.this._certWilds.get(domain));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1730 */         if (SslContextFactory.LOG.isDebugEnabled()) {
/* 1731 */           SslContextFactory.LOG.debug("SNI matched {}->{}", new Object[] { host, this._x509 });
/*      */         }
/*      */         
/*      */       }
/* 1735 */       else if (SslContextFactory.LOG.isDebugEnabled()) {
/* 1736 */         SslContextFactory.LOG.debug("SNI no match for {}", new Object[] { serverName });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1742 */       return true;
/*      */     }
/*      */     
/*      */     public String getHost()
/*      */     {
/* 1747 */       return this._host;
/*      */     }
/*      */     
/*      */     public X509 getX509()
/*      */     {
/* 1752 */       return this._x509;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ssl\SslContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */