/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
/*     */ import com.mchange.v2.c3p0.impl.C3P0PooledConnection;
/*     */ import com.mchange.v2.c3p0.impl.DbAuth;
/*     */ import com.mchange.v2.c3p0.impl.NewPooledConnection;
/*     */ import com.mchange.v2.c3p0.impl.WrapperConnectionPoolDataSourceBase;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.beans.VetoableChangeListener;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.DataSource;
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
/*     */ public final class WrapperConnectionPoolDataSource
/*     */   extends WrapperConnectionPoolDataSourceBase
/*     */   implements ConnectionPoolDataSource
/*     */ {
/*  44 */   static final MLogger logger = MLog.getLogger(WrapperConnectionPoolDataSource.class);
/*     */   
/*     */ 
/*  47 */   ConnectionTester connectionTester = C3P0ImplUtils.defaultConnectionTester();
/*     */   Map userOverrides;
/*     */   
/*     */   public WrapperConnectionPoolDataSource(boolean autoregister)
/*     */   {
/*  52 */     super(autoregister);
/*     */     
/*  54 */     setUpPropertyListeners();
/*     */     
/*     */     try
/*     */     {
/*  58 */       this.userOverrides = C3P0ImplUtils.parseUserOverridesAsString(getUserOverridesAsString());
/*     */     }
/*     */     catch (Exception e) {
/*  61 */       if (logger.isLoggable(MLevel.WARNING))
/*  62 */         logger.log(MLevel.WARNING, "Failed to parse stringified userOverrides. " + getUserOverridesAsString(), e);
/*     */     }
/*     */   }
/*     */   
/*     */   public WrapperConnectionPoolDataSource() {
/*  67 */     this(true);
/*     */   }
/*     */   
/*     */   private void setUpPropertyListeners() {
/*  71 */     VetoableChangeListener setConnectionTesterListener = new VetoableChangeListener()
/*     */     {
/*     */       public void vetoableChange(PropertyChangeEvent evt)
/*     */         throws PropertyVetoException
/*     */       {
/*  76 */         String propName = evt.getPropertyName();
/*  77 */         Object val = evt.getNewValue();
/*     */         
/*  79 */         if ("connectionTesterClassName".equals(propName)) {
/*     */           try
/*     */           {
/*  82 */             WrapperConnectionPoolDataSource.this.recreateConnectionTester((String)val);
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*  86 */             if (WrapperConnectionPoolDataSource.logger.isLoggable(MLevel.WARNING)) {
/*  87 */               WrapperConnectionPoolDataSource.logger.log(MLevel.WARNING, "Failed to create ConnectionTester of class " + val, e);
/*     */             }
/*  89 */             throw new PropertyVetoException("Could not instantiate connection tester class with name '" + val + "'.", evt);
/*     */           }
/*     */           
/*  92 */         } else if ("userOverridesAsString".equals(propName)) {
/*     */           try
/*     */           {
/*  95 */             WrapperConnectionPoolDataSource.this.userOverrides = C3P0ImplUtils.parseUserOverridesAsString((String)val);
/*     */           }
/*     */           catch (Exception e) {
/*  98 */             if (WrapperConnectionPoolDataSource.logger.isLoggable(MLevel.WARNING)) {
/*  99 */               WrapperConnectionPoolDataSource.logger.log(MLevel.WARNING, "Failed to parse stringified userOverrides. " + val, e);
/*     */             }
/* 101 */             throw new PropertyVetoException("Failed to parse stringified userOverrides. " + val, evt);
/*     */           }
/*     */         }
/*     */       }
/* 105 */     };
/* 106 */     addVetoableChangeListener(setConnectionTesterListener);
/*     */   }
/*     */   
/*     */   public WrapperConnectionPoolDataSource(String configName)
/*     */   {
/* 111 */     this();
/*     */     
/*     */     try
/*     */     {
/* 115 */       if (configName != null) {
/* 116 */         C3P0Config.bindNamedConfigToBean(this, configName);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 120 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 121 */         logger.log(MLevel.WARNING, "Error binding WrapperConnectionPoolDataSource to named-config '" + configName + "'. Some default-config values may be used.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PooledConnection getPooledConnection()
/*     */     throws SQLException
/*     */   {
/* 132 */     return getPooledConnection((ConnectionCustomizer)null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PooledConnection getPooledConnection(ConnectionCustomizer cc, String pdsIdt)
/*     */     throws SQLException
/*     */   {
/* 140 */     DataSource nds = getNestedDataSource();
/* 141 */     if (nds == null)
/* 142 */       throw new SQLException("No standard DataSource has been set beneath this wrapper! [ nestedDataSource == null ]");
/* 143 */     Connection conn = nds.getConnection();
/* 144 */     if (conn == null) {
/* 145 */       throw new SQLException("An (unpooled) DataSource returned null from its getConnection() method! DataSource: " + getNestedDataSource());
/*     */     }
/* 147 */     if (isUsesTraditionalReflectiveProxies())
/*     */     {
/*     */ 
/* 150 */       return new C3P0PooledConnection(conn, this.connectionTester, isAutoCommitOnClose(), isForceIgnoreUnresolvedTransactions(), cc, pdsIdt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 159 */     return new NewPooledConnection(conn, this.connectionTester, isAutoCommitOnClose(), isForceIgnoreUnresolvedTransactions(), getPreferredTestQuery(), cc, pdsIdt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PooledConnection getPooledConnection(String user, String password)
/*     */     throws SQLException
/*     */   {
/* 171 */     return getPooledConnection(user, password, null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected PooledConnection getPooledConnection(String user, String password, ConnectionCustomizer cc, String pdsIdt)
/*     */     throws SQLException
/*     */   {
/* 179 */     DataSource nds = getNestedDataSource();
/* 180 */     if (nds == null)
/* 181 */       throw new SQLException("No standard DataSource has been set beneath this wrapper! [ nestedDataSource == null ]");
/* 182 */     Connection conn = nds.getConnection(user, password);
/* 183 */     if (conn == null) {
/* 184 */       throw new SQLException("An (unpooled) DataSource returned null from its getConnection() method! DataSource: " + getNestedDataSource());
/*     */     }
/* 186 */     if (isUsesTraditionalReflectiveProxies())
/*     */     {
/*     */ 
/* 189 */       return new C3P0PooledConnection(conn, this.connectionTester, isAutoCommitOnClose(), isForceIgnoreUnresolvedTransactions(), cc, pdsIdt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */     return new NewPooledConnection(conn, this.connectionTester, isAutoCommitOnClose(), isForceIgnoreUnresolvedTransactions(), getPreferredTestQuery(), cc, pdsIdt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrintWriter getLogWriter()
/*     */     throws SQLException
/*     */   {
/* 210 */     return getNestedDataSource().getLogWriter();
/*     */   }
/*     */   
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/* 214 */     getNestedDataSource().setLogWriter(out);
/*     */   }
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/* 218 */     getNestedDataSource().setLoginTimeout(seconds);
/*     */   }
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 222 */     return getNestedDataSource().getLoginTimeout();
/*     */   }
/*     */   
/*     */   public String getUser()
/*     */   {
/*     */     try {
/* 228 */       return C3P0ImplUtils.findAuth(getNestedDataSource()).getUser();
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 232 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 233 */         logger.log(MLevel.WARNING, "An Exception occurred while trying to find the 'user' property from our nested DataSource. Defaulting to no specified username.", e);
/*     */       }
/*     */     }
/* 236 */     return null;
/*     */   }
/*     */   
/*     */   public String getPassword()
/*     */   {
/*     */     try {
/* 242 */       return C3P0ImplUtils.findAuth(getNestedDataSource()).getPassword();
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/* 246 */       if (logger.isLoggable(MLevel.WARNING))
/* 247 */         logger.log(MLevel.WARNING, "An Exception occurred while trying to find the 'password' property from our nested DataSource. Defaulting to no specified password.", e);
/*     */     }
/* 249 */     return null;
/*     */   }
/*     */   
/*     */   public Map getUserOverrides()
/*     */   {
/* 254 */     return this.userOverrides;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 258 */     StringBuffer sb = new StringBuffer();
/* 259 */     sb.append(super.toString());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 264 */     return sb.toString();
/*     */   }
/*     */   
/*     */   protected String extraToStringInfo()
/*     */   {
/* 269 */     if (this.userOverrides != null) {
/* 270 */       return "; userOverrides: " + this.userOverrides.toString();
/*     */     }
/* 272 */     return null;
/*     */   }
/*     */   
/*     */   private synchronized void recreateConnectionTester(String className)
/*     */     throws Exception
/*     */   {
/* 278 */     if (className != null)
/*     */     {
/* 280 */       ConnectionTester ct = (ConnectionTester)Class.forName(className).newInstance();
/* 281 */       this.connectionTester = ct;
/*     */     }
/*     */     else {
/* 284 */       this.connectionTester = C3P0ImplUtils.defaultConnectionTester();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\WrapperConnectionPoolDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */