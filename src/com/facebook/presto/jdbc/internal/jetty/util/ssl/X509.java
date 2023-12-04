/*     */ package com.facebook.presto.jdbc.internal.jetty.util.ssl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.StringUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.security.cert.CertificateParsingException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.naming.InvalidNameException;
/*     */ import javax.naming.ldap.LdapName;
/*     */ import javax.naming.ldap.Rdn;
/*     */ import javax.security.auth.x500.X500Principal;
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
/*     */ public class X509
/*     */ {
/*  40 */   private static final Logger LOG = Log.getLogger(X509.class);
/*     */   
/*     */ 
/*     */   private static final int KEY_USAGE__KEY_CERT_SIGN = 5;
/*     */   
/*     */ 
/*     */   private static final int SUBJECT_ALTERNATIVE_NAMES__DNS_NAME = 2;
/*     */   
/*     */   private final X509Certificate _x509;
/*     */   
/*     */   private final String _alias;
/*     */   
/*     */ 
/*     */   public static boolean isCertSign(X509Certificate x509)
/*     */   {
/*  55 */     boolean[] key_usage = x509.getKeyUsage();
/*  56 */     return (key_usage != null) && (key_usage[5] != 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  61 */   private final List<String> _hosts = new ArrayList();
/*  62 */   private final List<String> _wilds = new ArrayList();
/*     */   
/*     */   public X509(String alias, X509Certificate x509) throws CertificateParsingException, InvalidNameException
/*     */   {
/*  66 */     this._alias = alias;
/*  67 */     this._x509 = x509;
/*     */     
/*     */ 
/*  70 */     boolean named = false;
/*  71 */     Collection<List<?>> altNames = x509.getSubjectAlternativeNames();
/*  72 */     Iterator localIterator; if (altNames != null)
/*     */     {
/*  74 */       for (localIterator = altNames.iterator(); localIterator.hasNext();) { list = (List)localIterator.next();
/*     */         
/*  76 */         if (((Number)list.get(0)).intValue() == 2)
/*     */         {
/*  78 */           String cn = list.get(1).toString();
/*  79 */           if (LOG.isDebugEnabled())
/*  80 */             LOG.debug("Certificate SAN alias={} CN={} in {}", new Object[] { alias, cn, this });
/*  81 */           if (cn != null)
/*     */           {
/*  83 */             named = true;
/*  84 */             addName(cn);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     List<?> list;
/*  91 */     if (!named)
/*     */     {
/*  93 */       LdapName name = new LdapName(x509.getSubjectX500Principal().getName("RFC2253"));
/*  94 */       for (Rdn rdn : name.getRdns())
/*     */       {
/*  96 */         if (rdn.getType().equalsIgnoreCase("CN"))
/*     */         {
/*  98 */           String cn = rdn.getValue().toString();
/*  99 */           if (LOG.isDebugEnabled())
/* 100 */             LOG.debug("Certificate CN alias={} CN={} in {}", new Object[] { alias, cn, this });
/* 101 */           if ((cn != null) && (cn.contains(".")) && (!cn.contains(" "))) {
/* 102 */             addName(cn);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void addName(String cn) {
/* 110 */     cn = StringUtil.asciiToLowerCase(cn);
/* 111 */     if (cn.startsWith("*.")) {
/* 112 */       this._wilds.add(cn.substring(2));
/*     */     } else {
/* 114 */       this._hosts.add(cn);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getAlias() {
/* 119 */     return this._alias;
/*     */   }
/*     */   
/*     */   public X509Certificate getCertificate()
/*     */   {
/* 124 */     return this._x509;
/*     */   }
/*     */   
/*     */   public Set<String> getHosts()
/*     */   {
/* 129 */     return new HashSet(this._hosts);
/*     */   }
/*     */   
/*     */   public Set<String> getWilds()
/*     */   {
/* 134 */     return new HashSet(this._wilds);
/*     */   }
/*     */   
/*     */   public boolean matches(String host)
/*     */   {
/* 139 */     host = StringUtil.asciiToLowerCase(host);
/* 140 */     if ((this._hosts.contains(host)) || (this._wilds.contains(host))) {
/* 141 */       return true;
/*     */     }
/* 143 */     int dot = host.indexOf('.');
/* 144 */     if (dot >= 0)
/*     */     {
/* 146 */       String domain = host.substring(dot + 1);
/* 147 */       if (this._wilds.contains(domain))
/* 148 */         return true;
/*     */     }
/* 150 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 156 */     return String.format("%s@%x(%s,h=%s,w=%s)", new Object[] {
/* 157 */       getClass().getSimpleName(), 
/* 158 */       Integer.valueOf(hashCode()), this._alias, this._hosts, this._wilds });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\ssl\X509.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */