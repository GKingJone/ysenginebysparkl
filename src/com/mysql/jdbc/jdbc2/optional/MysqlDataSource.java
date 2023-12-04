/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ConnectionPropertiesImpl;
/*     */ import com.mysql.jdbc.NonRegisteringDriver;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.sql.DataSource;
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
/*     */ public class MysqlDataSource
/*     */   extends ConnectionPropertiesImpl
/*     */   implements DataSource, Referenceable, Serializable
/*     */ {
/*     */   static final long serialVersionUID = -5515846944416881264L;
/*     */   protected static final NonRegisteringDriver mysqlDriver;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  53 */       mysqlDriver = new NonRegisteringDriver();
/*     */     } catch (Exception E) {
/*  55 */       throw new RuntimeException("Can not load Driver class com.mysql.jdbc.Driver");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*  60 */   protected transient PrintWriter logWriter = null;
/*     */   
/*     */ 
/*  63 */   protected String databaseName = null;
/*     */   
/*     */ 
/*  66 */   protected String encoding = null;
/*     */   
/*     */ 
/*  69 */   protected String hostName = null;
/*     */   
/*     */ 
/*  72 */   protected String password = null;
/*     */   
/*     */ 
/*  75 */   protected String profileSql = "false";
/*     */   
/*     */ 
/*  78 */   protected String url = null;
/*     */   
/*     */ 
/*  81 */   protected String user = null;
/*     */   
/*     */ 
/*  84 */   protected boolean explicitUrl = false;
/*     */   
/*     */ 
/*  87 */   protected int port = 3306;
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
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 105 */     return getConnection(this.user, this.password);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public Connection getConnection(String userID, String pass)
/*     */     throws SQLException
/*     */   {
/* 122 */     Properties props = new Properties();
/*     */     
/* 124 */     if (userID != null) {
/* 125 */       props.setProperty("user", userID);
/*     */     }
/*     */     
/* 128 */     if (pass != null) {
/* 129 */       props.setProperty("password", pass);
/*     */     }
/*     */     
/* 132 */     exposeAsProperties(props);
/*     */     
/* 134 */     return getConnection(props);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDatabaseName(String dbName)
/*     */   {
/* 144 */     this.databaseName = dbName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDatabaseName()
/*     */   {
/* 153 */     return this.databaseName != null ? this.databaseName : "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLogWriter(PrintWriter output)
/*     */     throws SQLException
/*     */   {
/* 162 */     this.logWriter = output;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PrintWriter getLogWriter()
/*     */   {
/* 171 */     return this.logWriter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLoginTimeout()
/*     */   {
/* 188 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPassword(String pass)
/*     */   {
/* 198 */     this.password = pass;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPort(int p)
/*     */   {
/* 208 */     this.port = p;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 217 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPortNumber(int p)
/*     */   {
/* 229 */     setPort(p);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPortNumber()
/*     */   {
/* 238 */     return getPort();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPropertiesViaRef(Reference ref)
/*     */     throws SQLException
/*     */   {
/* 247 */     super.initializeFromRef(ref);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Reference getReference()
/*     */     throws NamingException
/*     */   {
/* 259 */     String factoryName = "com.mysql.jdbc.jdbc2.optional.MysqlDataSourceFactory";
/* 260 */     Reference ref = new Reference(getClass().getName(), factoryName, null);
/* 261 */     ref.add(new StringRefAddr("user", getUser()));
/* 262 */     ref.add(new StringRefAddr("password", this.password));
/* 263 */     ref.add(new StringRefAddr("serverName", getServerName()));
/* 264 */     ref.add(new StringRefAddr("port", "" + getPort()));
/* 265 */     ref.add(new StringRefAddr("databaseName", getDatabaseName()));
/* 266 */     ref.add(new StringRefAddr("url", getUrl()));
/* 267 */     ref.add(new StringRefAddr("explicitUrl", String.valueOf(this.explicitUrl)));
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 273 */       storeToRef(ref);
/*     */     } catch (SQLException sqlEx) {
/* 275 */       throw new NamingException(sqlEx.getMessage());
/*     */     }
/*     */     
/* 278 */     return ref;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setServerName(String serverName)
/*     */   {
/* 288 */     this.hostName = serverName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServerName()
/*     */   {
/* 297 */     return this.hostName != null ? this.hostName : "";
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
/*     */ 
/*     */   public void setURL(String url)
/*     */   {
/* 311 */     setUrl(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getURL()
/*     */   {
/* 320 */     return getUrl();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUrl(String url)
/*     */   {
/* 332 */     this.url = url;
/* 333 */     this.explicitUrl = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 342 */     if (!this.explicitUrl) {
/* 343 */       String builtUrl = "jdbc:mysql://";
/* 344 */       builtUrl = builtUrl + getServerName() + ":" + getPort() + "/" + getDatabaseName();
/*     */       
/* 346 */       return builtUrl;
/*     */     }
/*     */     
/* 349 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUser(String userID)
/*     */   {
/* 359 */     this.user = userID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 368 */     return this.user;
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
/*     */ 
/*     */   protected Connection getConnection(Properties props)
/*     */     throws SQLException
/*     */   {
/* 383 */     String jdbcUrlToUse = null;
/*     */     
/* 385 */     if (!this.explicitUrl) {
/* 386 */       StringBuilder jdbcUrl = new StringBuilder("jdbc:mysql://");
/*     */       
/* 388 */       if (this.hostName != null) {
/* 389 */         jdbcUrl.append(this.hostName);
/*     */       }
/*     */       
/* 392 */       jdbcUrl.append(":");
/* 393 */       jdbcUrl.append(this.port);
/* 394 */       jdbcUrl.append("/");
/*     */       
/* 396 */       if (this.databaseName != null) {
/* 397 */         jdbcUrl.append(this.databaseName);
/*     */       }
/*     */       
/* 400 */       jdbcUrlToUse = jdbcUrl.toString();
/*     */     } else {
/* 402 */       jdbcUrlToUse = this.url;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 409 */     Properties urlProps = mysqlDriver.parseURL(jdbcUrlToUse, null);
/* 410 */     urlProps.remove("DBNAME");
/* 411 */     urlProps.remove("HOST");
/* 412 */     urlProps.remove("PORT");
/*     */     
/* 414 */     Iterator<Object> keys = urlProps.keySet().iterator();
/*     */     
/* 416 */     while (keys.hasNext()) {
/* 417 */       String key = (String)keys.next();
/*     */       
/* 419 */       props.setProperty(key, urlProps.getProperty(key));
/*     */     }
/*     */     
/* 422 */     return mysqlDriver.connect(jdbcUrlToUse, props);
/*     */   }
/*     */   
/*     */   public void setLoginTimeout(int seconds)
/*     */     throws SQLException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlDataSource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */