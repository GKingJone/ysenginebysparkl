/*     */ package com.facebook.presto.jdbc.internal.jetty.util.ssl;
/*     */ 
/*     */ import java.net.Socket;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.X509ExtendedKeyManager;
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
/*     */ public class AliasedX509ExtendedKeyManager
/*     */   extends X509ExtendedKeyManager
/*     */ {
/*     */   private final String _alias;
/*     */   private final X509ExtendedKeyManager _delegate;
/*     */   
/*     */   public AliasedX509ExtendedKeyManager(X509ExtendedKeyManager keyManager, String keyAlias)
/*     */   {
/*  41 */     this._alias = keyAlias;
/*  42 */     this._delegate = keyManager;
/*     */   }
/*     */   
/*     */   public X509ExtendedKeyManager getDelegate()
/*     */   {
/*  47 */     return this._delegate;
/*     */   }
/*     */   
/*     */ 
/*     */   public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket)
/*     */   {
/*  53 */     if (this._alias == null) {
/*  54 */       return this._delegate.chooseClientAlias(keyType, issuers, socket);
/*     */     }
/*  56 */     for (String kt : keyType)
/*     */     {
/*  58 */       String[] aliases = this._delegate.getClientAliases(kt, issuers);
/*  59 */       if (aliases != null)
/*     */       {
/*  61 */         for (String a : aliases) {
/*  62 */           if (this._alias.equals(a))
/*  63 */             return this._alias;
/*     */         }
/*     */       }
/*     */     }
/*  67 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket)
/*     */   {
/*  73 */     if (this._alias == null) {
/*  74 */       return this._delegate.chooseServerAlias(keyType, issuers, socket);
/*     */     }
/*  76 */     String[] aliases = this._delegate.getServerAliases(keyType, issuers);
/*  77 */     if (aliases != null)
/*     */     {
/*  79 */       for (String a : aliases) {
/*  80 */         if (this._alias.equals(a))
/*  81 */           return this._alias;
/*     */       }
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getClientAliases(String keyType, Principal[] issuers)
/*     */   {
/*  90 */     return this._delegate.getClientAliases(keyType, issuers);
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getServerAliases(String keyType, Principal[] issuers)
/*     */   {
/*  96 */     return this._delegate.getServerAliases(keyType, issuers);
/*     */   }
/*     */   
/*     */ 
/*     */   public X509Certificate[] getCertificateChain(String alias)
/*     */   {
/* 102 */     return this._delegate.getCertificateChain(alias);
/*     */   }
/*     */   
/*     */ 
/*     */   public PrivateKey getPrivateKey(String alias)
/*     */   {
/* 108 */     return this._delegate.getPrivateKey(alias);
/*     */   }
/*     */   
/*     */ 
/*     */   public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine)
/*     */   {
/* 114 */     if (this._alias == null) {
/* 115 */       return this._delegate.chooseEngineServerAlias(keyType, issuers, engine);
/*     */     }
/* 117 */     String[] aliases = this._delegate.getServerAliases(keyType, issuers);
/* 118 */     if (aliases != null)
/*     */     {
/* 120 */       for (String a : aliases) {
/* 121 */         if (this._alias.equals(a))
/* 122 */           return this._alias;
/*     */       }
/*     */     }
/* 125 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine)
/*     */   {
/* 131 */     if (this._alias == null) {
/* 132 */       return this._delegate.chooseEngineClientAlias(keyType, issuers, engine);
/*     */     }
/* 134 */     for (String kt : keyType)
/*     */     {
/* 136 */       String[] aliases = this._delegate.getClientAliases(kt, issuers);
/* 137 */       if (aliases != null)
/*     */       {
/* 139 */         for (String a : aliases) {
/* 140 */           if (this._alias.equals(a))
/* 141 */             return this._alias;
/*     */         }
/*     */       }
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ssl\AliasedX509ExtendedKeyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */