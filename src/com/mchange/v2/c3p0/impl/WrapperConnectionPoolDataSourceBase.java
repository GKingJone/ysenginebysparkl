/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0Registry;
/*     */ import com.mchange.v2.c3p0.ConnectionCustomizer;
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.naming.JavaBeanReferenceMaker;
/*     */ import com.mchange.v2.naming.ReferenceIndirector;
/*     */ import com.mchange.v2.ser.IndirectlySerialized;
/*     */ import com.mchange.v2.ser.Indirector;
/*     */ import com.mchange.v2.ser.SerializableUtils;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.beans.VetoableChangeListener;
/*     */ import java.beans.VetoableChangeSupport;
/*     */ import java.io.IOException;
/*     */ import java.io.NotSerializableException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.sql.SQLException;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.sql.DataSource;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class WrapperConnectionPoolDataSourceBase
/*     */   extends IdentityTokenResolvable
/*     */   implements Referenceable, Serializable
/*     */ {
/*  38 */   protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
/*     */   
/*     */ 
/*  41 */   protected PropertyChangeSupport getPropertyChangeSupport() { return this.pcs; }
/*  42 */   protected VetoableChangeSupport vcs = new VetoableChangeSupport(this);
/*     */   
/*     */ 
/*  45 */   protected VetoableChangeSupport getVetoableChangeSupport() { return this.vcs; }
/*  46 */   private int acquireIncrement = C3P0Config.initializeIntPropertyVar("acquireIncrement", C3P0Defaults.acquireIncrement());
/*  47 */   private int acquireRetryAttempts = C3P0Config.initializeIntPropertyVar("acquireRetryAttempts", C3P0Defaults.acquireRetryAttempts());
/*  48 */   private int acquireRetryDelay = C3P0Config.initializeIntPropertyVar("acquireRetryDelay", C3P0Defaults.acquireRetryDelay());
/*  49 */   private boolean autoCommitOnClose = C3P0Config.initializeBooleanPropertyVar("autoCommitOnClose", C3P0Defaults.autoCommitOnClose());
/*  50 */   private String automaticTestTable = C3P0Config.initializeStringPropertyVar("automaticTestTable", C3P0Defaults.automaticTestTable());
/*  51 */   private boolean breakAfterAcquireFailure = C3P0Config.initializeBooleanPropertyVar("breakAfterAcquireFailure", C3P0Defaults.breakAfterAcquireFailure());
/*  52 */   private int checkoutTimeout = C3P0Config.initializeIntPropertyVar("checkoutTimeout", C3P0Defaults.checkoutTimeout());
/*  53 */   private String connectionCustomizerClassName = C3P0Config.initializeStringPropertyVar("connectionCustomizerClassName", C3P0Defaults.connectionCustomizerClassName());
/*  54 */   private String connectionTesterClassName = C3P0Config.initializeStringPropertyVar("connectionTesterClassName", C3P0Defaults.connectionTesterClassName());
/*  55 */   private boolean debugUnreturnedConnectionStackTraces = C3P0Config.initializeBooleanPropertyVar("debugUnreturnedConnectionStackTraces", C3P0Defaults.debugUnreturnedConnectionStackTraces());
/*  56 */   private String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
/*  57 */   private boolean forceIgnoreUnresolvedTransactions = C3P0Config.initializeBooleanPropertyVar("forceIgnoreUnresolvedTransactions", C3P0Defaults.forceIgnoreUnresolvedTransactions());
/*     */   private String identityToken;
/*  59 */   private int idleConnectionTestPeriod = C3P0Config.initializeIntPropertyVar("idleConnectionTestPeriod", C3P0Defaults.idleConnectionTestPeriod());
/*  60 */   private int initialPoolSize = C3P0Config.initializeIntPropertyVar("initialPoolSize", C3P0Defaults.initialPoolSize());
/*  61 */   private int maxAdministrativeTaskTime = C3P0Config.initializeIntPropertyVar("maxAdministrativeTaskTime", C3P0Defaults.maxAdministrativeTaskTime());
/*  62 */   private int maxConnectionAge = C3P0Config.initializeIntPropertyVar("maxConnectionAge", C3P0Defaults.maxConnectionAge());
/*  63 */   private int maxIdleTime = C3P0Config.initializeIntPropertyVar("maxIdleTime", C3P0Defaults.maxIdleTime());
/*  64 */   private int maxIdleTimeExcessConnections = C3P0Config.initializeIntPropertyVar("maxIdleTimeExcessConnections", C3P0Defaults.maxIdleTimeExcessConnections());
/*  65 */   private int maxPoolSize = C3P0Config.initializeIntPropertyVar("maxPoolSize", C3P0Defaults.maxPoolSize());
/*  66 */   private int maxStatements = C3P0Config.initializeIntPropertyVar("maxStatements", C3P0Defaults.maxStatements());
/*  67 */   private int maxStatementsPerConnection = C3P0Config.initializeIntPropertyVar("maxStatementsPerConnection", C3P0Defaults.maxStatementsPerConnection());
/*  68 */   private int minPoolSize = C3P0Config.initializeIntPropertyVar("minPoolSize", C3P0Defaults.minPoolSize());
/*     */   private DataSource nestedDataSource;
/*  70 */   private String overrideDefaultPassword = C3P0Config.initializeStringPropertyVar("overrideDefaultPassword", C3P0Defaults.overrideDefaultPassword());
/*  71 */   private String overrideDefaultUser = C3P0Config.initializeStringPropertyVar("overrideDefaultUser", C3P0Defaults.overrideDefaultUser());
/*  72 */   private String preferredTestQuery = C3P0Config.initializeStringPropertyVar("preferredTestQuery", C3P0Defaults.preferredTestQuery());
/*  73 */   private int propertyCycle = C3P0Config.initializeIntPropertyVar("propertyCycle", C3P0Defaults.propertyCycle());
/*  74 */   private boolean testConnectionOnCheckin = C3P0Config.initializeBooleanPropertyVar("testConnectionOnCheckin", C3P0Defaults.testConnectionOnCheckin());
/*  75 */   private boolean testConnectionOnCheckout = C3P0Config.initializeBooleanPropertyVar("testConnectionOnCheckout", C3P0Defaults.testConnectionOnCheckout());
/*  76 */   private int unreturnedConnectionTimeout = C3P0Config.initializeIntPropertyVar("unreturnedConnectionTimeout", C3P0Defaults.unreturnedConnectionTimeout());
/*  77 */   private String userOverridesAsString = C3P0Config.initializeUserOverridesAsString();
/*  78 */   private boolean usesTraditionalReflectiveProxies = C3P0Config.initializeBooleanPropertyVar("usesTraditionalReflectiveProxies", C3P0Defaults.usesTraditionalReflectiveProxies());
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*  81 */   public synchronized int getAcquireIncrement() { return this.acquireIncrement; }
/*     */   
/*     */   public synchronized void setAcquireIncrement(int acquireIncrement)
/*     */   {
/*  85 */     this.acquireIncrement = acquireIncrement;
/*     */   }
/*     */   
/*     */   public synchronized int getAcquireRetryAttempts() {
/*  89 */     return this.acquireRetryAttempts;
/*     */   }
/*     */   
/*     */   public synchronized void setAcquireRetryAttempts(int acquireRetryAttempts) {
/*  93 */     this.acquireRetryAttempts = acquireRetryAttempts;
/*     */   }
/*     */   
/*     */   public synchronized int getAcquireRetryDelay() {
/*  97 */     return this.acquireRetryDelay;
/*     */   }
/*     */   
/*     */   public synchronized void setAcquireRetryDelay(int acquireRetryDelay) {
/* 101 */     this.acquireRetryDelay = acquireRetryDelay;
/*     */   }
/*     */   
/*     */   public synchronized boolean isAutoCommitOnClose() {
/* 105 */     return this.autoCommitOnClose;
/*     */   }
/*     */   
/*     */   public synchronized void setAutoCommitOnClose(boolean autoCommitOnClose) {
/* 109 */     this.autoCommitOnClose = autoCommitOnClose;
/*     */   }
/*     */   
/*     */   public synchronized String getAutomaticTestTable() {
/* 113 */     return this.automaticTestTable;
/*     */   }
/*     */   
/*     */   public synchronized void setAutomaticTestTable(String automaticTestTable) {
/* 117 */     this.automaticTestTable = automaticTestTable;
/*     */   }
/*     */   
/*     */   public synchronized boolean isBreakAfterAcquireFailure() {
/* 121 */     return this.breakAfterAcquireFailure;
/*     */   }
/*     */   
/*     */   public synchronized void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
/* 125 */     this.breakAfterAcquireFailure = breakAfterAcquireFailure;
/*     */   }
/*     */   
/*     */   public synchronized int getCheckoutTimeout() {
/* 129 */     return this.checkoutTimeout;
/*     */   }
/*     */   
/*     */   public synchronized void setCheckoutTimeout(int checkoutTimeout) {
/* 133 */     this.checkoutTimeout = checkoutTimeout;
/*     */   }
/*     */   
/*     */   public synchronized String getConnectionCustomizerClassName() {
/* 137 */     return this.connectionCustomizerClassName;
/*     */   }
/*     */   
/*     */   public synchronized void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
/* 141 */     this.connectionCustomizerClassName = connectionCustomizerClassName;
/*     */   }
/*     */   
/*     */   public synchronized String getConnectionTesterClassName() {
/* 145 */     return this.connectionTesterClassName;
/*     */   }
/*     */   
/*     */   public synchronized void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException {
/* 149 */     String oldVal = this.connectionTesterClassName;
/* 150 */     if (!eqOrBothNull(oldVal, connectionTesterClassName))
/* 151 */       this.vcs.fireVetoableChange("connectionTesterClassName", oldVal, connectionTesterClassName);
/* 152 */     this.connectionTesterClassName = connectionTesterClassName;
/*     */   }
/*     */   
/*     */   public synchronized boolean isDebugUnreturnedConnectionStackTraces() {
/* 156 */     return this.debugUnreturnedConnectionStackTraces;
/*     */   }
/*     */   
/*     */   public synchronized void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
/* 160 */     this.debugUnreturnedConnectionStackTraces = debugUnreturnedConnectionStackTraces;
/*     */   }
/*     */   
/*     */   public synchronized String getFactoryClassLocation() {
/* 164 */     return this.factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized void setFactoryClassLocation(String factoryClassLocation) {
/* 168 */     this.factoryClassLocation = factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized boolean isForceIgnoreUnresolvedTransactions() {
/* 172 */     return this.forceIgnoreUnresolvedTransactions;
/*     */   }
/*     */   
/*     */   public synchronized void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
/* 176 */     this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
/*     */   }
/*     */   
/*     */   public synchronized String getIdentityToken() {
/* 180 */     return this.identityToken;
/*     */   }
/*     */   
/*     */   public synchronized void setIdentityToken(String identityToken) {
/* 184 */     String oldVal = this.identityToken;
/* 185 */     this.identityToken = identityToken;
/* 186 */     if (!eqOrBothNull(oldVal, identityToken))
/* 187 */       this.pcs.firePropertyChange("identityToken", oldVal, identityToken);
/*     */   }
/*     */   
/*     */   public synchronized int getIdleConnectionTestPeriod() {
/* 191 */     return this.idleConnectionTestPeriod;
/*     */   }
/*     */   
/*     */   public synchronized void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
/* 195 */     this.idleConnectionTestPeriod = idleConnectionTestPeriod;
/*     */   }
/*     */   
/*     */   public synchronized int getInitialPoolSize() {
/* 199 */     return this.initialPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized void setInitialPoolSize(int initialPoolSize) {
/* 203 */     this.initialPoolSize = initialPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxAdministrativeTaskTime() {
/* 207 */     return this.maxAdministrativeTaskTime;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
/* 211 */     this.maxAdministrativeTaskTime = maxAdministrativeTaskTime;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxConnectionAge() {
/* 215 */     return this.maxConnectionAge;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxConnectionAge(int maxConnectionAge) {
/* 219 */     this.maxConnectionAge = maxConnectionAge;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxIdleTime() {
/* 223 */     return this.maxIdleTime;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxIdleTime(int maxIdleTime) {
/* 227 */     this.maxIdleTime = maxIdleTime;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxIdleTimeExcessConnections() {
/* 231 */     return this.maxIdleTimeExcessConnections;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
/* 235 */     this.maxIdleTimeExcessConnections = maxIdleTimeExcessConnections;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxPoolSize() {
/* 239 */     return this.maxPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxPoolSize(int maxPoolSize) {
/* 243 */     this.maxPoolSize = maxPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxStatements() {
/* 247 */     return this.maxStatements;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxStatements(int maxStatements) {
/* 251 */     this.maxStatements = maxStatements;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxStatementsPerConnection() {
/* 255 */     return this.maxStatementsPerConnection;
/*     */   }
/*     */   
/*     */   public synchronized void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
/* 259 */     this.maxStatementsPerConnection = maxStatementsPerConnection;
/*     */   }
/*     */   
/*     */   public synchronized int getMinPoolSize() {
/* 263 */     return this.minPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized void setMinPoolSize(int minPoolSize) {
/* 267 */     this.minPoolSize = minPoolSize;
/*     */   }
/*     */   
/*     */   public synchronized DataSource getNestedDataSource() {
/* 271 */     return this.nestedDataSource;
/*     */   }
/*     */   
/*     */   public synchronized void setNestedDataSource(DataSource nestedDataSource) {
/* 275 */     DataSource oldVal = this.nestedDataSource;
/* 276 */     this.nestedDataSource = nestedDataSource;
/* 277 */     if (!eqOrBothNull(oldVal, nestedDataSource))
/* 278 */       this.pcs.firePropertyChange("nestedDataSource", oldVal, nestedDataSource);
/*     */   }
/*     */   
/*     */   public synchronized String getOverrideDefaultPassword() {
/* 282 */     return this.overrideDefaultPassword;
/*     */   }
/*     */   
/*     */   public synchronized void setOverrideDefaultPassword(String overrideDefaultPassword) {
/* 286 */     this.overrideDefaultPassword = overrideDefaultPassword;
/*     */   }
/*     */   
/*     */   public synchronized String getOverrideDefaultUser() {
/* 290 */     return this.overrideDefaultUser;
/*     */   }
/*     */   
/*     */   public synchronized void setOverrideDefaultUser(String overrideDefaultUser) {
/* 294 */     this.overrideDefaultUser = overrideDefaultUser;
/*     */   }
/*     */   
/*     */   public synchronized String getPreferredTestQuery() {
/* 298 */     return this.preferredTestQuery;
/*     */   }
/*     */   
/*     */   public synchronized void setPreferredTestQuery(String preferredTestQuery) {
/* 302 */     this.preferredTestQuery = preferredTestQuery;
/*     */   }
/*     */   
/*     */   public synchronized int getPropertyCycle() {
/* 306 */     return this.propertyCycle;
/*     */   }
/*     */   
/*     */   public synchronized void setPropertyCycle(int propertyCycle) {
/* 310 */     this.propertyCycle = propertyCycle;
/*     */   }
/*     */   
/*     */   public synchronized boolean isTestConnectionOnCheckin() {
/* 314 */     return this.testConnectionOnCheckin;
/*     */   }
/*     */   
/*     */   public synchronized void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
/* 318 */     this.testConnectionOnCheckin = testConnectionOnCheckin;
/*     */   }
/*     */   
/*     */   public synchronized boolean isTestConnectionOnCheckout() {
/* 322 */     return this.testConnectionOnCheckout;
/*     */   }
/*     */   
/*     */   public synchronized void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
/* 326 */     this.testConnectionOnCheckout = testConnectionOnCheckout;
/*     */   }
/*     */   
/*     */   public synchronized int getUnreturnedConnectionTimeout() {
/* 330 */     return this.unreturnedConnectionTimeout;
/*     */   }
/*     */   
/*     */   public synchronized void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
/* 334 */     this.unreturnedConnectionTimeout = unreturnedConnectionTimeout;
/*     */   }
/*     */   
/*     */   public synchronized String getUserOverridesAsString() {
/* 338 */     return this.userOverridesAsString;
/*     */   }
/*     */   
/*     */   public synchronized void setUserOverridesAsString(String userOverridesAsString) throws PropertyVetoException {
/* 342 */     String oldVal = this.userOverridesAsString;
/* 343 */     if (!eqOrBothNull(oldVal, userOverridesAsString))
/* 344 */       this.vcs.fireVetoableChange("userOverridesAsString", oldVal, userOverridesAsString);
/* 345 */     this.userOverridesAsString = userOverridesAsString;
/*     */   }
/*     */   
/*     */   public synchronized boolean isUsesTraditionalReflectiveProxies() {
/* 349 */     return this.usesTraditionalReflectiveProxies;
/*     */   }
/*     */   
/*     */   public synchronized void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
/* 353 */     this.usesTraditionalReflectiveProxies = usesTraditionalReflectiveProxies;
/*     */   }
/*     */   
/*     */   public void addPropertyChangeListener(PropertyChangeListener pcl) {
/* 357 */     this.pcs.addPropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 360 */   public void addPropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.addPropertyChangeListener(propName, pcl); }
/*     */   
/*     */   public void removePropertyChangeListener(PropertyChangeListener pcl) {
/* 363 */     this.pcs.removePropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 366 */   public void removePropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.removePropertyChangeListener(propName, pcl); }
/*     */   
/*     */   public void addVetoableChangeListener(VetoableChangeListener vcl)
/*     */   {
/* 370 */     this.vcs.addVetoableChangeListener(vcl);
/*     */   }
/*     */   
/* 373 */   public void removeVetoableChangeListener(VetoableChangeListener vcl) { this.vcs.removeVetoableChangeListener(vcl); }
/*     */   
/*     */   private boolean eqOrBothNull(Object a, Object b)
/*     */   {
/* 377 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 387 */     oos.writeShort(1);
/* 388 */     oos.writeInt(this.acquireIncrement);
/* 389 */     oos.writeInt(this.acquireRetryAttempts);
/* 390 */     oos.writeInt(this.acquireRetryDelay);
/* 391 */     oos.writeBoolean(this.autoCommitOnClose);
/* 392 */     oos.writeObject(this.automaticTestTable);
/* 393 */     oos.writeBoolean(this.breakAfterAcquireFailure);
/* 394 */     oos.writeInt(this.checkoutTimeout);
/* 395 */     oos.writeObject(this.connectionCustomizerClassName);
/* 396 */     oos.writeObject(this.connectionTesterClassName);
/* 397 */     oos.writeBoolean(this.debugUnreturnedConnectionStackTraces);
/* 398 */     oos.writeObject(this.factoryClassLocation);
/* 399 */     oos.writeBoolean(this.forceIgnoreUnresolvedTransactions);
/* 400 */     oos.writeObject(this.identityToken);
/* 401 */     oos.writeInt(this.idleConnectionTestPeriod);
/* 402 */     oos.writeInt(this.initialPoolSize);
/* 403 */     oos.writeInt(this.maxAdministrativeTaskTime);
/* 404 */     oos.writeInt(this.maxConnectionAge);
/* 405 */     oos.writeInt(this.maxIdleTime);
/* 406 */     oos.writeInt(this.maxIdleTimeExcessConnections);
/* 407 */     oos.writeInt(this.maxPoolSize);
/* 408 */     oos.writeInt(this.maxStatements);
/* 409 */     oos.writeInt(this.maxStatementsPerConnection);
/* 410 */     oos.writeInt(this.minPoolSize);
/*     */     
/*     */     try
/*     */     {
/* 414 */       SerializableUtils.toByteArray(this.nestedDataSource);
/* 415 */       oos.writeObject(this.nestedDataSource);
/*     */     }
/*     */     catch (NotSerializableException nse)
/*     */     {
/*     */       try
/*     */       {
/* 421 */         Indirector indirector = new ReferenceIndirector();
/* 422 */         oos.writeObject(indirector.indirectForm(this.nestedDataSource));
/*     */       }
/*     */       catch (IOException indirectionIOException) {
/* 425 */         throw indirectionIOException;
/*     */       } catch (Exception indirectionOtherException) {
/* 427 */         throw new IOException("Problem indirectly serializing nestedDataSource: " + indirectionOtherException.toString());
/*     */       } }
/* 429 */     oos.writeObject(this.overrideDefaultPassword);
/* 430 */     oos.writeObject(this.overrideDefaultUser);
/* 431 */     oos.writeObject(this.preferredTestQuery);
/* 432 */     oos.writeInt(this.propertyCycle);
/* 433 */     oos.writeBoolean(this.testConnectionOnCheckin);
/* 434 */     oos.writeBoolean(this.testConnectionOnCheckout);
/* 435 */     oos.writeInt(this.unreturnedConnectionTimeout);
/* 436 */     oos.writeObject(this.userOverridesAsString);
/* 437 */     oos.writeBoolean(this.usesTraditionalReflectiveProxies);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 442 */     short version = ois.readShort();
/* 443 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 446 */       this.acquireIncrement = ois.readInt();
/* 447 */       this.acquireRetryAttempts = ois.readInt();
/* 448 */       this.acquireRetryDelay = ois.readInt();
/* 449 */       this.autoCommitOnClose = ois.readBoolean();
/* 450 */       this.automaticTestTable = ((String)ois.readObject());
/* 451 */       this.breakAfterAcquireFailure = ois.readBoolean();
/* 452 */       this.checkoutTimeout = ois.readInt();
/* 453 */       this.connectionCustomizerClassName = ((String)ois.readObject());
/* 454 */       this.connectionTesterClassName = ((String)ois.readObject());
/* 455 */       this.debugUnreturnedConnectionStackTraces = ois.readBoolean();
/* 456 */       this.factoryClassLocation = ((String)ois.readObject());
/* 457 */       this.forceIgnoreUnresolvedTransactions = ois.readBoolean();
/* 458 */       this.identityToken = ((String)ois.readObject());
/* 459 */       this.idleConnectionTestPeriod = ois.readInt();
/* 460 */       this.initialPoolSize = ois.readInt();
/* 461 */       this.maxAdministrativeTaskTime = ois.readInt();
/* 462 */       this.maxConnectionAge = ois.readInt();
/* 463 */       this.maxIdleTime = ois.readInt();
/* 464 */       this.maxIdleTimeExcessConnections = ois.readInt();
/* 465 */       this.maxPoolSize = ois.readInt();
/* 466 */       this.maxStatements = ois.readInt();
/* 467 */       this.maxStatementsPerConnection = ois.readInt();
/* 468 */       this.minPoolSize = ois.readInt();
/* 469 */       Object o = ois.readObject();
/* 470 */       if ((o instanceof IndirectlySerialized)) o = ((IndirectlySerialized)o).getObject();
/* 471 */       this.nestedDataSource = ((DataSource)o);
/* 472 */       this.overrideDefaultPassword = ((String)ois.readObject());
/* 473 */       this.overrideDefaultUser = ((String)ois.readObject());
/* 474 */       this.preferredTestQuery = ((String)ois.readObject());
/* 475 */       this.propertyCycle = ois.readInt();
/* 476 */       this.testConnectionOnCheckin = ois.readBoolean();
/* 477 */       this.testConnectionOnCheckout = ois.readBoolean();
/* 478 */       this.unreturnedConnectionTimeout = ois.readInt();
/* 479 */       this.userOverridesAsString = ((String)ois.readObject());
/* 480 */       this.usesTraditionalReflectiveProxies = ois.readBoolean();
/* 481 */       this.pcs = new PropertyChangeSupport(this);
/* 482 */       this.vcs = new VetoableChangeSupport(this);
/* 483 */       break;
/*     */     default: 
/* 485 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 491 */     StringBuffer sb = new StringBuffer();
/* 492 */     sb.append(super.toString());
/* 493 */     sb.append(" [ ");
/* 494 */     sb.append("acquireIncrement -> " + this.acquireIncrement);
/* 495 */     sb.append(", ");
/* 496 */     sb.append("acquireRetryAttempts -> " + this.acquireRetryAttempts);
/* 497 */     sb.append(", ");
/* 498 */     sb.append("acquireRetryDelay -> " + this.acquireRetryDelay);
/* 499 */     sb.append(", ");
/* 500 */     sb.append("autoCommitOnClose -> " + this.autoCommitOnClose);
/* 501 */     sb.append(", ");
/* 502 */     sb.append("automaticTestTable -> " + this.automaticTestTable);
/* 503 */     sb.append(", ");
/* 504 */     sb.append("breakAfterAcquireFailure -> " + this.breakAfterAcquireFailure);
/* 505 */     sb.append(", ");
/* 506 */     sb.append("checkoutTimeout -> " + this.checkoutTimeout);
/* 507 */     sb.append(", ");
/* 508 */     sb.append("connectionCustomizerClassName -> " + this.connectionCustomizerClassName);
/* 509 */     sb.append(", ");
/* 510 */     sb.append("connectionTesterClassName -> " + this.connectionTesterClassName);
/* 511 */     sb.append(", ");
/* 512 */     sb.append("debugUnreturnedConnectionStackTraces -> " + this.debugUnreturnedConnectionStackTraces);
/* 513 */     sb.append(", ");
/* 514 */     sb.append("factoryClassLocation -> " + this.factoryClassLocation);
/* 515 */     sb.append(", ");
/* 516 */     sb.append("forceIgnoreUnresolvedTransactions -> " + this.forceIgnoreUnresolvedTransactions);
/* 517 */     sb.append(", ");
/* 518 */     sb.append("identityToken -> " + this.identityToken);
/* 519 */     sb.append(", ");
/* 520 */     sb.append("idleConnectionTestPeriod -> " + this.idleConnectionTestPeriod);
/* 521 */     sb.append(", ");
/* 522 */     sb.append("initialPoolSize -> " + this.initialPoolSize);
/* 523 */     sb.append(", ");
/* 524 */     sb.append("maxAdministrativeTaskTime -> " + this.maxAdministrativeTaskTime);
/* 525 */     sb.append(", ");
/* 526 */     sb.append("maxConnectionAge -> " + this.maxConnectionAge);
/* 527 */     sb.append(", ");
/* 528 */     sb.append("maxIdleTime -> " + this.maxIdleTime);
/* 529 */     sb.append(", ");
/* 530 */     sb.append("maxIdleTimeExcessConnections -> " + this.maxIdleTimeExcessConnections);
/* 531 */     sb.append(", ");
/* 532 */     sb.append("maxPoolSize -> " + this.maxPoolSize);
/* 533 */     sb.append(", ");
/* 534 */     sb.append("maxStatements -> " + this.maxStatements);
/* 535 */     sb.append(", ");
/* 536 */     sb.append("maxStatementsPerConnection -> " + this.maxStatementsPerConnection);
/* 537 */     sb.append(", ");
/* 538 */     sb.append("minPoolSize -> " + this.minPoolSize);
/* 539 */     sb.append(", ");
/* 540 */     sb.append("nestedDataSource -> " + this.nestedDataSource);
/* 541 */     sb.append(", ");
/* 542 */     sb.append("preferredTestQuery -> " + this.preferredTestQuery);
/* 543 */     sb.append(", ");
/* 544 */     sb.append("propertyCycle -> " + this.propertyCycle);
/* 545 */     sb.append(", ");
/* 546 */     sb.append("testConnectionOnCheckin -> " + this.testConnectionOnCheckin);
/* 547 */     sb.append(", ");
/* 548 */     sb.append("testConnectionOnCheckout -> " + this.testConnectionOnCheckout);
/* 549 */     sb.append(", ");
/* 550 */     sb.append("unreturnedConnectionTimeout -> " + this.unreturnedConnectionTimeout);
/* 551 */     sb.append(", ");
/* 552 */     sb.append("usesTraditionalReflectiveProxies -> " + this.usesTraditionalReflectiveProxies);
/*     */     
/* 554 */     String extraToStringInfo = extraToStringInfo();
/* 555 */     if (extraToStringInfo != null)
/* 556 */       sb.append(extraToStringInfo);
/* 557 */     sb.append(" ]");
/* 558 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/* 562 */   protected String extraToStringInfo() { return null; }
/*     */   
/* 564 */   static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();
/*     */   
/*     */   static
/*     */   {
/* 568 */     referenceMaker.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
/* 569 */     referenceMaker.addReferenceProperty("acquireIncrement");
/* 570 */     referenceMaker.addReferenceProperty("acquireRetryAttempts");
/* 571 */     referenceMaker.addReferenceProperty("acquireRetryDelay");
/* 572 */     referenceMaker.addReferenceProperty("autoCommitOnClose");
/* 573 */     referenceMaker.addReferenceProperty("automaticTestTable");
/* 574 */     referenceMaker.addReferenceProperty("breakAfterAcquireFailure");
/* 575 */     referenceMaker.addReferenceProperty("checkoutTimeout");
/* 576 */     referenceMaker.addReferenceProperty("connectionCustomizerClassName");
/* 577 */     referenceMaker.addReferenceProperty("connectionTesterClassName");
/* 578 */     referenceMaker.addReferenceProperty("debugUnreturnedConnectionStackTraces");
/* 579 */     referenceMaker.addReferenceProperty("factoryClassLocation");
/* 580 */     referenceMaker.addReferenceProperty("forceIgnoreUnresolvedTransactions");
/* 581 */     referenceMaker.addReferenceProperty("identityToken");
/* 582 */     referenceMaker.addReferenceProperty("idleConnectionTestPeriod");
/* 583 */     referenceMaker.addReferenceProperty("initialPoolSize");
/* 584 */     referenceMaker.addReferenceProperty("maxAdministrativeTaskTime");
/* 585 */     referenceMaker.addReferenceProperty("maxConnectionAge");
/* 586 */     referenceMaker.addReferenceProperty("maxIdleTime");
/* 587 */     referenceMaker.addReferenceProperty("maxIdleTimeExcessConnections");
/* 588 */     referenceMaker.addReferenceProperty("maxPoolSize");
/* 589 */     referenceMaker.addReferenceProperty("maxStatements");
/* 590 */     referenceMaker.addReferenceProperty("maxStatementsPerConnection");
/* 591 */     referenceMaker.addReferenceProperty("minPoolSize");
/* 592 */     referenceMaker.addReferenceProperty("nestedDataSource");
/* 593 */     referenceMaker.addReferenceProperty("overrideDefaultPassword");
/* 594 */     referenceMaker.addReferenceProperty("overrideDefaultUser");
/* 595 */     referenceMaker.addReferenceProperty("preferredTestQuery");
/* 596 */     referenceMaker.addReferenceProperty("propertyCycle");
/* 597 */     referenceMaker.addReferenceProperty("testConnectionOnCheckin");
/* 598 */     referenceMaker.addReferenceProperty("testConnectionOnCheckout");
/* 599 */     referenceMaker.addReferenceProperty("unreturnedConnectionTimeout");
/* 600 */     referenceMaker.addReferenceProperty("userOverridesAsString");
/* 601 */     referenceMaker.addReferenceProperty("usesTraditionalReflectiveProxies");
/*     */   }
/*     */   
/*     */   public Reference getReference() throws NamingException
/*     */   {
/* 606 */     return referenceMaker.createReference(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public WrapperConnectionPoolDataSourceBase(boolean autoregister)
/*     */   {
/* 614 */     if (autoregister)
/*     */     {
/* 616 */       this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
/* 617 */       C3P0Registry.reregister(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private WrapperConnectionPoolDataSourceBase() {}
/*     */   
/*     */   protected abstract PooledConnection getPooledConnection(ConnectionCustomizer paramConnectionCustomizer, String paramString)
/*     */     throws SQLException;
/*     */   
/*     */   protected abstract PooledConnection getPooledConnection(String paramString1, String paramString2, ConnectionCustomizer paramConnectionCustomizer, String paramString3)
/*     */     throws SQLException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\WrapperConnectionPoolDataSourceBase.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */