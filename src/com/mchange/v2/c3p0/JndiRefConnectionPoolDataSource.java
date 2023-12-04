/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory;
/*     */ import com.mchange.v2.c3p0.impl.IdentityTokenResolvable;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.naming.JavaBeanReferenceMaker;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.PooledConnection;
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
/*     */ public final class JndiRefConnectionPoolDataSource
/*     */   extends IdentityTokenResolvable
/*     */   implements ConnectionPoolDataSource, Serializable, Referenceable
/*     */ {
/*  50 */   static final MLogger logger = MLog.getLogger(JndiRefConnectionPoolDataSource.class);
/*     */   
/*  52 */   static final Collection IGNORE_PROPS = Arrays.asList(new String[] { "reference", "pooledConnection" });
/*     */   
/*     */   JndiRefForwardingDataSource jrfds;
/*     */   WrapperConnectionPoolDataSource wcpds;
/*     */   String identityToken;
/*     */   
/*     */   public JndiRefConnectionPoolDataSource()
/*     */   {
/*  60 */     this(true);
/*     */   }
/*     */   
/*     */   public JndiRefConnectionPoolDataSource(boolean autoregister) {
/*  64 */     this.jrfds = new JndiRefForwardingDataSource();
/*  65 */     this.wcpds = new WrapperConnectionPoolDataSource();
/*  66 */     this.wcpds.setNestedDataSource(this.jrfds);
/*     */     
/*  68 */     if (autoregister)
/*     */     {
/*  70 */       this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
/*  71 */       C3P0Registry.reregister(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isJndiLookupCaching() {
/*  76 */     return this.jrfds.isCaching();
/*     */   }
/*     */   
/*  79 */   public void setJndiLookupCaching(boolean caching) { this.jrfds.setCaching(caching); }
/*     */   
/*     */   public Hashtable getJndiEnv() {
/*  82 */     return this.jrfds.getJndiEnv();
/*     */   }
/*     */   
/*  85 */   public void setJndiEnv(Hashtable jndiEnv) { this.jrfds.setJndiEnv(jndiEnv); }
/*     */   
/*     */   public Object getJndiName() {
/*  88 */     return this.jrfds.getJndiName();
/*     */   }
/*     */   
/*  91 */   public void setJndiName(Object jndiName) throws PropertyVetoException { this.jrfds.setJndiName(jndiName); }
/*     */   
/*     */   public int getAcquireIncrement() {
/*  94 */     return this.wcpds.getAcquireIncrement();
/*     */   }
/*     */   
/*  97 */   public void setAcquireIncrement(int acquireIncrement) { this.wcpds.setAcquireIncrement(acquireIncrement); }
/*     */   
/*     */   public int getAcquireRetryAttempts() {
/* 100 */     return this.wcpds.getAcquireRetryAttempts();
/*     */   }
/*     */   
/* 103 */   public void setAcquireRetryAttempts(int ara) { this.wcpds.setAcquireRetryAttempts(ara); }
/*     */   
/*     */   public int getAcquireRetryDelay() {
/* 106 */     return this.wcpds.getAcquireRetryDelay();
/*     */   }
/*     */   
/* 109 */   public void setAcquireRetryDelay(int ard) { this.wcpds.setAcquireRetryDelay(ard); }
/*     */   
/*     */   public boolean isAutoCommitOnClose() {
/* 112 */     return this.wcpds.isAutoCommitOnClose();
/*     */   }
/*     */   
/* 115 */   public void setAutoCommitOnClose(boolean autoCommitOnClose) { this.wcpds.setAutoCommitOnClose(autoCommitOnClose); }
/*     */   
/*     */   public void setAutomaticTestTable(String att) {
/* 118 */     this.wcpds.setAutomaticTestTable(att);
/*     */   }
/*     */   
/* 121 */   public String getAutomaticTestTable() { return this.wcpds.getAutomaticTestTable(); }
/*     */   
/*     */   public void setBreakAfterAcquireFailure(boolean baaf) {
/* 124 */     this.wcpds.setBreakAfterAcquireFailure(baaf);
/*     */   }
/*     */   
/* 127 */   public boolean isBreakAfterAcquireFailure() { return this.wcpds.isBreakAfterAcquireFailure(); }
/*     */   
/*     */   public void setCheckoutTimeout(int ct) {
/* 130 */     this.wcpds.setCheckoutTimeout(ct);
/*     */   }
/*     */   
/* 133 */   public int getCheckoutTimeout() { return this.wcpds.getCheckoutTimeout(); }
/*     */   
/*     */   public String getConnectionTesterClassName() {
/* 136 */     return this.wcpds.getConnectionTesterClassName();
/*     */   }
/*     */   
/* 139 */   public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException { this.wcpds.setConnectionTesterClassName(connectionTesterClassName); }
/*     */   
/*     */   public boolean isForceIgnoreUnresolvedTransactions() {
/* 142 */     return this.wcpds.isForceIgnoreUnresolvedTransactions();
/*     */   }
/*     */   
/* 145 */   public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) { this.wcpds.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions); }
/*     */   
/*     */   public String getIdentityToken() {
/* 148 */     return this.identityToken;
/*     */   }
/*     */   
/* 151 */   public void setIdentityToken(String identityToken) { this.identityToken = identityToken; }
/*     */   
/*     */   public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
/* 154 */     this.wcpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
/*     */   }
/*     */   
/* 157 */   public int getIdleConnectionTestPeriod() { return this.wcpds.getIdleConnectionTestPeriod(); }
/*     */   
/*     */   public int getInitialPoolSize() {
/* 160 */     return this.wcpds.getInitialPoolSize();
/*     */   }
/*     */   
/* 163 */   public void setInitialPoolSize(int initialPoolSize) { this.wcpds.setInitialPoolSize(initialPoolSize); }
/*     */   
/*     */   public int getMaxIdleTime() {
/* 166 */     return this.wcpds.getMaxIdleTime();
/*     */   }
/*     */   
/* 169 */   public void setMaxIdleTime(int maxIdleTime) { this.wcpds.setMaxIdleTime(maxIdleTime); }
/*     */   
/*     */   public int getMaxPoolSize() {
/* 172 */     return this.wcpds.getMaxPoolSize();
/*     */   }
/*     */   
/* 175 */   public void setMaxPoolSize(int maxPoolSize) { this.wcpds.setMaxPoolSize(maxPoolSize); }
/*     */   
/*     */   public int getMaxStatements() {
/* 178 */     return this.wcpds.getMaxStatements();
/*     */   }
/*     */   
/* 181 */   public void setMaxStatements(int maxStatements) { this.wcpds.setMaxStatements(maxStatements); }
/*     */   
/*     */   public int getMaxStatementsPerConnection() {
/* 184 */     return this.wcpds.getMaxStatementsPerConnection();
/*     */   }
/*     */   
/* 187 */   public void setMaxStatementsPerConnection(int mspc) { this.wcpds.setMaxStatementsPerConnection(mspc); }
/*     */   
/*     */   public int getMinPoolSize() {
/* 190 */     return this.wcpds.getMinPoolSize();
/*     */   }
/*     */   
/* 193 */   public void setMinPoolSize(int minPoolSize) { this.wcpds.setMinPoolSize(minPoolSize); }
/*     */   
/*     */   public String getPreferredTestQuery() {
/* 196 */     return this.wcpds.getPreferredTestQuery();
/*     */   }
/*     */   
/* 199 */   public void setPreferredTestQuery(String ptq) { this.wcpds.setPreferredTestQuery(ptq); }
/*     */   
/*     */   public int getPropertyCycle() {
/* 202 */     return this.wcpds.getPropertyCycle();
/*     */   }
/*     */   
/* 205 */   public void setPropertyCycle(int propertyCycle) { this.wcpds.setPropertyCycle(propertyCycle); }
/*     */   
/*     */   public boolean isTestConnectionOnCheckin() {
/* 208 */     return this.wcpds.isTestConnectionOnCheckin();
/*     */   }
/*     */   
/* 211 */   public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) { this.wcpds.setTestConnectionOnCheckin(testConnectionOnCheckin); }
/*     */   
/*     */   public boolean isTestConnectionOnCheckout() {
/* 214 */     return this.wcpds.isTestConnectionOnCheckout();
/*     */   }
/*     */   
/* 217 */   public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) { this.wcpds.setTestConnectionOnCheckout(testConnectionOnCheckout); }
/*     */   
/*     */   public boolean isUsesTraditionalReflectiveProxies() {
/* 220 */     return this.wcpds.isUsesTraditionalReflectiveProxies();
/*     */   }
/*     */   
/* 223 */   public void setUsesTraditionalReflectiveProxies(boolean utrp) { this.wcpds.setUsesTraditionalReflectiveProxies(utrp); }
/*     */   
/*     */   public String getFactoryClassLocation() {
/* 226 */     return this.jrfds.getFactoryClassLocation();
/*     */   }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) {
/* 230 */     this.jrfds.setFactoryClassLocation(factoryClassLocation);
/* 231 */     this.wcpds.setFactoryClassLocation(factoryClassLocation);
/*     */   }
/*     */   
/* 234 */   static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();
/*     */   
/*     */   static
/*     */   {
/* 238 */     referenceMaker.setFactoryClassName(C3P0JavaBeanObjectFactory.class.getName());
/* 239 */     referenceMaker.addReferenceProperty("acquireIncrement");
/* 240 */     referenceMaker.addReferenceProperty("acquireRetryAttempts");
/* 241 */     referenceMaker.addReferenceProperty("acquireRetryDelay");
/* 242 */     referenceMaker.addReferenceProperty("autoCommitOnClose");
/* 243 */     referenceMaker.addReferenceProperty("automaticTestTable");
/* 244 */     referenceMaker.addReferenceProperty("checkoutTimeout");
/* 245 */     referenceMaker.addReferenceProperty("connectionTesterClassName");
/* 246 */     referenceMaker.addReferenceProperty("factoryClassLocation");
/* 247 */     referenceMaker.addReferenceProperty("forceIgnoreUnresolvedTransactions");
/* 248 */     referenceMaker.addReferenceProperty("idleConnectionTestPeriod");
/* 249 */     referenceMaker.addReferenceProperty("identityToken");
/* 250 */     referenceMaker.addReferenceProperty("initialPoolSize");
/* 251 */     referenceMaker.addReferenceProperty("jndiEnv");
/* 252 */     referenceMaker.addReferenceProperty("jndiLookupCaching");
/* 253 */     referenceMaker.addReferenceProperty("jndiName");
/* 254 */     referenceMaker.addReferenceProperty("maxIdleTime");
/* 255 */     referenceMaker.addReferenceProperty("maxPoolSize");
/* 256 */     referenceMaker.addReferenceProperty("maxStatements");
/* 257 */     referenceMaker.addReferenceProperty("maxStatementsPerConnection");
/* 258 */     referenceMaker.addReferenceProperty("minPoolSize");
/* 259 */     referenceMaker.addReferenceProperty("preferredTestQuery");
/* 260 */     referenceMaker.addReferenceProperty("propertyCycle");
/* 261 */     referenceMaker.addReferenceProperty("testConnectionOnCheckin");
/* 262 */     referenceMaker.addReferenceProperty("testConnectionOnCheckout");
/* 263 */     referenceMaker.addReferenceProperty("usesTraditionalReflectiveProxies");
/*     */   }
/*     */   
/*     */   public Reference getReference() throws NamingException {
/* 267 */     return referenceMaker.createReference(this);
/*     */   }
/*     */   
/*     */   public PooledConnection getPooledConnection() throws SQLException
/*     */   {
/* 272 */     return this.wcpds.getPooledConnection();
/*     */   }
/*     */   
/*     */   public PooledConnection getPooledConnection(String user, String password) throws SQLException {
/* 276 */     return this.wcpds.getPooledConnection(user, password);
/*     */   }
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 280 */     return this.wcpds.getLogWriter();
/*     */   }
/*     */   
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/* 284 */     this.wcpds.setLogWriter(out);
/*     */   }
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/* 288 */     this.wcpds.setLoginTimeout(seconds);
/*     */   }
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 292 */     return this.wcpds.getLoginTimeout();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 296 */     StringBuffer sb = new StringBuffer(512);
/* 297 */     sb.append(super.toString());
/* 298 */     sb.append(" [");
/* 299 */     try { BeansUtils.appendPropNamesAndValues(sb, this, IGNORE_PROPS);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 303 */       if (logger.isLoggable(MLevel.FINE))
/* 304 */         logger.log(MLevel.FINE, "An exception occurred while extracting property names and values for toString()", e);
/* 305 */       sb.append(e.toString());
/*     */     }
/* 307 */     sb.append("]");
/* 308 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\JndiRefConnectionPoolDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */