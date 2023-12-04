/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.URIUtil;
/*     */ import java.util.Objects;
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
/*     */ public class Origin
/*     */ {
/*     */   private final String scheme;
/*     */   private final Address address;
/*     */   
/*     */   public Origin(String scheme, String host, int port)
/*     */   {
/*  32 */     this(scheme, new Address(host, port));
/*     */   }
/*     */   
/*     */   public Origin(String scheme, Address address)
/*     */   {
/*  37 */     this.scheme = ((String)Objects.requireNonNull(scheme));
/*  38 */     this.address = address;
/*     */   }
/*     */   
/*     */   public String getScheme()
/*     */   {
/*  43 */     return this.scheme;
/*     */   }
/*     */   
/*     */   public Address getAddress()
/*     */   {
/*  48 */     return this.address;
/*     */   }
/*     */   
/*     */   public String asString()
/*     */   {
/*  53 */     StringBuilder result = new StringBuilder();
/*  54 */     URIUtil.appendSchemeHostPort(result, this.scheme, this.address.host, this.address.port);
/*  55 */     return result.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  61 */     if (this == obj) return true;
/*  62 */     if ((obj == null) || (getClass() != obj.getClass())) return false;
/*  63 */     Origin that = (Origin)obj;
/*  64 */     return (this.scheme.equals(that.scheme)) && (this.address.equals(that.address));
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  70 */     int result = this.scheme.hashCode();
/*  71 */     result = 31 * result + this.address.hashCode();
/*  72 */     return result;
/*     */   }
/*     */   
/*     */   public static class Address
/*     */   {
/*     */     private final String host;
/*     */     private final int port;
/*     */     
/*     */     public Address(String host, int port)
/*     */     {
/*  82 */       this.host = ((String)Objects.requireNonNull(host));
/*  83 */       this.port = port;
/*     */     }
/*     */     
/*     */     public String getHost()
/*     */     {
/*  88 */       return this.host;
/*     */     }
/*     */     
/*     */     public int getPort()
/*     */     {
/*  93 */       return this.port;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/*  99 */       if (this == obj) return true;
/* 100 */       if ((obj == null) || (getClass() != obj.getClass())) return false;
/* 101 */       Address that = (Address)obj;
/* 102 */       return (this.host.equals(that.host)) && (this.port == that.port);
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 108 */       int result = this.host.hashCode();
/* 109 */       result = 31 * result + this.port;
/* 110 */       return result;
/*     */     }
/*     */     
/*     */     public String asString()
/*     */     {
/* 115 */       return String.format("%s:%d", new Object[] { this.host, Integer.valueOf(this.port) });
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 121 */       return asString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\Origin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */