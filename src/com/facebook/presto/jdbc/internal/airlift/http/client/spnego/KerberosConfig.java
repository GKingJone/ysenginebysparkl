/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.spnego;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.configuration.Config;
/*    */ import com.facebook.presto.jdbc.internal.airlift.configuration.ConfigDescription;
/*    */ import java.io.File;
/*    */ 
/*    */ 
/*    */ public class KerberosConfig
/*    */ {
/*    */   private File credentialCache;
/*    */   private File keytab;
/*    */   private File config;
/* 13 */   private boolean useCanonicalHostname = true;
/*    */   
/*    */   public File getCredentialCache()
/*    */   {
/* 17 */     return this.credentialCache;
/*    */   }
/*    */   
/*    */   @Config("http.authentication.krb5.credential-cache")
/*    */   @ConfigDescription("Set kerberos credential cache path")
/*    */   public KerberosConfig setCredentialCache(File credentialCache)
/*    */   {
/* 24 */     this.credentialCache = credentialCache;
/* 25 */     return this;
/*    */   }
/*    */   
/*    */   public File getKeytab()
/*    */   {
/* 30 */     return this.keytab;
/*    */   }
/*    */   
/*    */   @Config("http.authentication.krb5.keytab")
/*    */   @ConfigDescription("Set kerberos key table path")
/*    */   public KerberosConfig setKeytab(File keytab)
/*    */   {
/* 37 */     this.keytab = keytab;
/* 38 */     return this;
/*    */   }
/*    */   
/*    */   public File getConfig()
/*    */   {
/* 43 */     return this.config;
/*    */   }
/*    */   
/*    */   @Config("http.authentication.krb5.config")
/*    */   @ConfigDescription("Set kerberos configuration path")
/*    */   public KerberosConfig setConfig(File config)
/*    */   {
/* 50 */     this.config = config;
/* 51 */     return this;
/*    */   }
/*    */   
/*    */   public boolean isUseCanonicalHostname()
/*    */   {
/* 56 */     return this.useCanonicalHostname;
/*    */   }
/*    */   
/*    */   @Config("http.authentication.krb5.use-canonical-hostname")
/*    */   @ConfigDescription("Canonicalize service hostname using the DNS reverse lookup")
/*    */   public KerberosConfig setUseCanonicalHostname(boolean useCanonicalHostname)
/*    */   {
/* 63 */     this.useCanonicalHostname = useCanonicalHostname;
/* 64 */     return this;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\spnego\KerberosConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */