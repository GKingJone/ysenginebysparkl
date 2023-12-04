/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class ProxyConfiguration
/*     */ {
/*  42 */   private final List<Proxy> proxies = new ArrayList();
/*     */   
/*     */   public List<Proxy> getProxies()
/*     */   {
/*  46 */     return this.proxies;
/*     */   }
/*     */   
/*     */   public Proxy match(Origin origin)
/*     */   {
/*  51 */     for (Proxy proxy : getProxies())
/*     */     {
/*  53 */       if (proxy.matches(origin))
/*  54 */         return proxy;
/*     */     }
/*  56 */     return null;
/*     */   }
/*     */   
/*     */   public static abstract class Proxy
/*     */   {
/*  61 */     private final Set<String> included = new HashSet();
/*  62 */     private final Set<String> excluded = new HashSet();
/*     */     private final Origin.Address address;
/*     */     private final boolean secure;
/*     */     
/*     */     protected Proxy(Origin.Address address, boolean secure)
/*     */     {
/*  68 */       this.address = address;
/*  69 */       this.secure = secure;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Origin.Address getAddress()
/*     */     {
/*  77 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isSecure()
/*     */     {
/*  85 */       return this.secure;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Set<String> getIncludedAddresses()
/*     */     {
/*  95 */       return this.included;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Set<String> getExcludedAddresses()
/*     */     {
/* 105 */       return this.excluded;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public URI getURI()
/*     */     {
/* 113 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean matches(Origin origin)
/*     */     {
/* 125 */       boolean result = this.included.isEmpty();
/* 126 */       Origin.Address address = origin.getAddress();
/* 127 */       for (String included : this.included)
/*     */       {
/* 129 */         if (matches(address, included))
/*     */         {
/* 131 */           result = true;
/* 132 */           break;
/*     */         }
/*     */       }
/* 135 */       for (String excluded : this.excluded)
/*     */       {
/* 137 */         if (matches(address, excluded))
/*     */         {
/* 139 */           result = false;
/* 140 */           break;
/*     */         }
/*     */       }
/* 143 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean matches(Origin.Address address, String pattern)
/*     */     {
/* 149 */       int colon = pattern.indexOf(':');
/* 150 */       if (colon < 0)
/* 151 */         return pattern.equals(address.getHost());
/* 152 */       String host = pattern.substring(0, colon);
/* 153 */       String port = pattern.substring(colon + 1);
/* 154 */       return (host.equals(address.getHost())) && (port.equals(String.valueOf(address.getPort())));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public abstract ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory paramClientConnectionFactory);
/*     */     
/*     */ 
/*     */ 
/*     */     public String toString()
/*     */     {
/* 166 */       return this.address.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ProxyConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */