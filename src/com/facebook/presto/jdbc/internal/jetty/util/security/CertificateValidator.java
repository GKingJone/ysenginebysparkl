/*     */ package com.facebook.presto.jdbc.internal.jetty.util.security;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.InvalidParameterException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.Security;
/*     */ import java.security.cert.CRL;
/*     */ import java.security.cert.CertPathBuilder;
/*     */ import java.security.cert.CertPathBuilderResult;
/*     */ import java.security.cert.CertPathValidator;
/*     */ import java.security.cert.CertStore;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CollectionCertStoreParameters;
/*     */ import java.security.cert.PKIXBuilderParameters;
/*     */ import java.security.cert.X509CertSelector;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class CertificateValidator
/*     */ {
/*  57 */   private static final Logger LOG = Log.getLogger(CertificateValidator.class);
/*  58 */   private static AtomicLong __aliasCount = new AtomicLong();
/*     */   
/*     */   private KeyStore _trustStore;
/*     */   
/*     */   private Collection<? extends CRL> _crls;
/*     */   
/*  64 */   private int _maxCertPathLength = -1;
/*     */   
/*  66 */   private boolean _enableCRLDP = false;
/*     */   
/*  68 */   private boolean _enableOCSP = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String _ocspResponderURL;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificateValidator(KeyStore trustStore, Collection<? extends CRL> crls)
/*     */   {
/*  80 */     if (trustStore == null)
/*     */     {
/*  82 */       throw new InvalidParameterException("TrustStore must be specified for CertificateValidator.");
/*     */     }
/*     */     
/*  85 */     this._trustStore = trustStore;
/*  86 */     this._crls = crls;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void validate(KeyStore keyStore)
/*     */     throws CertificateException
/*     */   {
/*     */     try
/*     */     {
/*  99 */       Enumeration<String> aliases = keyStore.aliases();
/*     */       
/* 101 */       while (aliases.hasMoreElements())
/*     */       {
/* 103 */         String alias = (String)aliases.nextElement();
/*     */         
/* 105 */         validate(keyStore, alias);
/*     */       }
/*     */       
/*     */     }
/*     */     catch (KeyStoreException kse)
/*     */     {
/* 111 */       throw new CertificateException("Unable to retrieve aliases from keystore", kse);
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
/*     */   public String validate(KeyStore keyStore, String keyAlias)
/*     */     throws CertificateException
/*     */   {
/* 126 */     String result = null;
/*     */     
/* 128 */     if (keyAlias != null)
/*     */     {
/*     */       try
/*     */       {
/* 132 */         validate(keyStore, keyStore.getCertificate(keyAlias));
/*     */       }
/*     */       catch (KeyStoreException kse)
/*     */       {
/* 136 */         LOG.debug(kse);
/*     */         
/* 138 */         throw new CertificateException("Unable to validate certificate for alias [" + keyAlias + "]: " + kse.getMessage(), kse);
/*     */       }
/* 140 */       result = keyAlias;
/*     */     }
/*     */     
/* 143 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void validate(KeyStore keyStore, Certificate cert)
/*     */     throws CertificateException
/*     */   {
/* 155 */     Certificate[] certChain = null;
/*     */     
/* 157 */     if ((cert != null) && ((cert instanceof X509Certificate)))
/*     */     {
/* 159 */       ((X509Certificate)cert).checkValidity();
/*     */       
/* 161 */       String certAlias = null;
/*     */       try
/*     */       {
/* 164 */         if (keyStore == null)
/*     */         {
/* 166 */           throw new InvalidParameterException("Keystore cannot be null");
/*     */         }
/*     */         
/* 169 */         certAlias = keyStore.getCertificateAlias((X509Certificate)cert);
/* 170 */         if (certAlias == null)
/*     */         {
/* 172 */           certAlias = "JETTY" + String.format("%016X", new Object[] { Long.valueOf(__aliasCount.incrementAndGet()) });
/* 173 */           keyStore.setCertificateEntry(certAlias, cert);
/*     */         }
/*     */         
/* 176 */         certChain = keyStore.getCertificateChain(certAlias);
/* 177 */         if ((certChain == null) || (certChain.length == 0))
/*     */         {
/* 179 */           throw new IllegalStateException("Unable to retrieve certificate chain");
/*     */         }
/*     */       }
/*     */       catch (KeyStoreException kse)
/*     */       {
/* 184 */         LOG.debug(kse);
/*     */         
/* 186 */         throw new CertificateException("Unable to validate certificate" + (certAlias == null ? "" : new StringBuilder().append(" for alias [").append(certAlias).append("]").toString()) + ": " + kse.getMessage(), kse);
/*     */       }
/*     */       
/* 189 */       validate(certChain);
/*     */     }
/*     */   }
/*     */   
/*     */   public void validate(Certificate[] certChain) throws CertificateException
/*     */   {
/*     */     try
/*     */     {
/* 197 */       ArrayList<X509Certificate> certList = new ArrayList();
/* 198 */       for (Certificate item : certChain)
/*     */       {
/* 200 */         if (item != null)
/*     */         {
/*     */ 
/* 203 */           if (!(item instanceof X509Certificate))
/*     */           {
/* 205 */             throw new IllegalStateException("Invalid certificate type in chain");
/*     */           }
/*     */           
/* 208 */           certList.add((X509Certificate)item);
/*     */         }
/*     */       }
/* 211 */       if (certList.isEmpty())
/*     */       {
/* 213 */         throw new IllegalStateException("Invalid certificate chain");
/*     */       }
/*     */       
/*     */ 
/* 217 */       X509CertSelector certSelect = new X509CertSelector();
/* 218 */       certSelect.setCertificate((X509Certificate)certList.get(0));
/*     */       
/*     */ 
/* 221 */       PKIXBuilderParameters pbParams = new PKIXBuilderParameters(this._trustStore, certSelect);
/* 222 */       pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList)));
/*     */       
/*     */ 
/* 225 */       pbParams.setMaxPathLength(this._maxCertPathLength);
/*     */       
/*     */ 
/* 228 */       pbParams.setRevocationEnabled(true);
/*     */       
/*     */ 
/* 231 */       if ((this._crls != null) && (!this._crls.isEmpty()))
/*     */       {
/* 233 */         pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(this._crls)));
/*     */       }
/*     */       
/*     */ 
/* 237 */       if (this._enableOCSP)
/*     */       {
/* 239 */         Security.setProperty("ocsp.enable", "true");
/*     */       }
/*     */       
/* 242 */       if (this._enableCRLDP)
/*     */       {
/* 244 */         System.setProperty("com.sun.security.enableCRLDP", "true");
/*     */       }
/*     */       
/*     */ 
/* 248 */       CertPathBuilderResult buildResult = CertPathBuilder.getInstance("PKIX").build(pbParams);
/*     */       
/*     */ 
/* 251 */       CertPathValidator.getInstance("PKIX").validate(buildResult.getCertPath(), pbParams);
/*     */     }
/*     */     catch (GeneralSecurityException gse)
/*     */     {
/* 255 */       LOG.debug(gse);
/* 256 */       throw new CertificateException("Unable to validate certificate: " + gse.getMessage(), gse);
/*     */     }
/*     */   }
/*     */   
/*     */   public KeyStore getTrustStore()
/*     */   {
/* 262 */     return this._trustStore;
/*     */   }
/*     */   
/*     */   public Collection<? extends CRL> getCrls()
/*     */   {
/* 267 */     return this._crls;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaxCertPathLength()
/*     */   {
/* 276 */     return this._maxCertPathLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setMaxCertPathLength(int maxCertPathLength)
/*     */   {
/* 287 */     this._maxCertPathLength = maxCertPathLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEnableCRLDP()
/*     */   {
/* 296 */     return this._enableCRLDP;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEnableCRLDP(boolean enableCRLDP)
/*     */   {
/* 305 */     this._enableCRLDP = enableCRLDP;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEnableOCSP()
/*     */   {
/* 314 */     return this._enableOCSP;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEnableOCSP(boolean enableOCSP)
/*     */   {
/* 323 */     this._enableOCSP = enableOCSP;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getOcspResponderURL()
/*     */   {
/* 332 */     return this._ocspResponderURL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setOcspResponderURL(String ocspResponderURL)
/*     */   {
/* 341 */     this._ocspResponderURL = ocspResponderURL;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\security\CertificateValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */