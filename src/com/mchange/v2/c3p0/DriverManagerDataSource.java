/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.c3p0.impl.DriverManagerDataSourceBase;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
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
/*     */ public final class DriverManagerDataSource
/*     */   extends DriverManagerDataSourceBase
/*     */   implements DataSource
/*     */ {
/*  47 */   static final MLogger logger = MLog.getLogger(DriverManagerDataSource.class);
/*     */   
/*     */ 
/*     */   Driver driver;
/*     */   
/*     */ 
/*  53 */   boolean driver_class_loaded = false;
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*  56 */   public DriverManagerDataSource() { this(true); }
/*     */   
/*     */   public DriverManagerDataSource(boolean autoregister)
/*     */   {
/*  60 */     super(autoregister);
/*     */     
/*  62 */     setUpPropertyListeners();
/*     */     
/*  64 */     String user = C3P0Config.initializeStringPropertyVar("user", null);
/*  65 */     String password = C3P0Config.initializeStringPropertyVar("password", null);
/*     */     
/*  67 */     if (user != null) {
/*  68 */       setUser(user);
/*     */     }
/*  70 */     if (password != null) {
/*  71 */       setPassword(password);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setUpPropertyListeners() {
/*  76 */     PropertyChangeListener driverClassListener = new PropertyChangeListener()
/*     */     {
/*     */       public void propertyChange(PropertyChangeEvent evt)
/*     */       {
/*  80 */         if ("driverClass".equals(evt.getPropertyName()))
/*  81 */           DriverManagerDataSource.this.setDriverClassLoaded(false);
/*     */       }
/*  83 */     };
/*  84 */     addPropertyChangeListener(driverClassListener);
/*     */   }
/*     */   
/*     */   private synchronized boolean isDriverClassLoaded() {
/*  88 */     return this.driver_class_loaded;
/*     */   }
/*     */   
/*  91 */   private synchronized void setDriverClassLoaded(boolean dcl) { this.driver_class_loaded = dcl; }
/*     */   
/*     */   private void ensureDriverLoaded() throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  97 */       if (!isDriverClassLoaded())
/*     */       {
/*  99 */         if (this.driverClass != null)
/* 100 */           Class.forName(this.driverClass);
/* 101 */         setDriverClassLoaded(true);
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 106 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 107 */         logger.log(MLevel.WARNING, "Could not load driverClass " + this.driverClass, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 117 */     ensureDriverLoaded();
/*     */     
/* 119 */     Connection out = driver().connect(this.jdbcUrl, this.properties);
/* 120 */     if (out == null) {
/* 121 */       throw new SQLException("Apparently, jdbc URL '" + this.jdbcUrl + "' is not valid for the underlying " + "driver [" + driver() + "].");
/*     */     }
/* 123 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   public Connection getConnection(String username, String password)
/*     */     throws SQLException
/*     */   {
/* 132 */     ensureDriverLoaded();
/*     */     
/* 134 */     Connection out = driver().connect(this.jdbcUrl, overrideProps(username, password));
/* 135 */     if (out == null) {
/* 136 */       throw new SQLException("Apparently, jdbc URL '" + this.jdbcUrl + "' is not valid for the underlying " + "driver [" + driver() + "].");
/*     */     }
/* 138 */     return out;
/*     */   }
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 142 */     return DriverManager.getLogWriter();
/*     */   }
/*     */   
/* 145 */   public void setLogWriter(PrintWriter out) throws SQLException { DriverManager.setLogWriter(out); }
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 148 */     return DriverManager.getLoginTimeout();
/*     */   }
/*     */   
/* 151 */   public void setLoginTimeout(int seconds) throws SQLException { DriverManager.setLoginTimeout(seconds); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setJdbcUrl(String jdbcUrl)
/*     */   {
/* 158 */     super.setJdbcUrl(jdbcUrl);
/* 159 */     clearDriver();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void setUser(String user)
/*     */   {
/* 165 */     String oldUser = getUser();
/* 166 */     if (!eqOrBothNull(user, oldUser))
/*     */     {
/* 168 */       if (user != null) {
/* 169 */         this.properties.put("user", user);
/*     */       } else {
/* 171 */         this.properties.remove("user");
/*     */       }
/* 173 */       this.pcs.firePropertyChange("user", oldUser, user);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized String getUser()
/*     */   {
/* 182 */     return this.properties.getProperty("user");
/*     */   }
/*     */   
/*     */   public synchronized void setPassword(String password)
/*     */   {
/* 187 */     String oldPass = getPassword();
/* 188 */     if (!eqOrBothNull(password, oldPass))
/*     */     {
/* 190 */       if (password != null) {
/* 191 */         this.properties.put("password", password);
/*     */       } else {
/* 193 */         this.properties.remove("password");
/*     */       }
/* 195 */       this.pcs.firePropertyChange("password", oldPass, password);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized String getPassword() {
/* 200 */     return this.properties.getProperty("password");
/*     */   }
/*     */   
/*     */   private final Properties overrideProps(String user, String password) {
/* 204 */     Properties overriding = (Properties)this.properties.clone();
/*     */     
/* 206 */     if (user != null) {
/* 207 */       overriding.put("user", user);
/*     */     } else {
/* 209 */       overriding.remove("user");
/*     */     }
/* 211 */     if (password != null) {
/* 212 */       overriding.put("password", password);
/*     */     } else {
/* 214 */       overriding.remove("password");
/*     */     }
/* 216 */     return overriding;
/*     */   }
/*     */   
/*     */   private synchronized Driver driver()
/*     */     throws SQLException
/*     */   {
/* 222 */     if (this.driver == null)
/* 223 */       this.driver = DriverManager.getDriver(this.jdbcUrl);
/* 224 */     return this.driver;
/*     */   }
/*     */   
/*     */   private synchronized void clearDriver() {
/* 228 */     this.driver = null;
/*     */   }
/*     */   
/* 231 */   private static boolean eqOrBothNull(Object a, Object b) { return (a == b) || ((a != null) && (a.equals(b))); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 239 */     oos.writeShort(1);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 244 */     short version = ois.readShort();
/* 245 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 248 */       setUpPropertyListeners();
/* 249 */       break;
/*     */     default: 
/* 251 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\DriverManagerDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */