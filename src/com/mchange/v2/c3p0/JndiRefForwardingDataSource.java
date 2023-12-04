/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.impl.JndiRefDataSourceBase;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyVetoException;
/*     */ import java.beans.VetoableChangeListener;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.NamingException;
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
/*     */ final class JndiRefForwardingDataSource
/*     */   extends JndiRefDataSourceBase
/*     */   implements DataSource
/*     */ {
/*  49 */   static final MLogger logger = MLog.getLogger(JndiRefForwardingDataSource.class);
/*     */   transient DataSource cachedInner;
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   public JndiRefForwardingDataSource() {
/*  55 */     this(true);
/*     */   }
/*     */   
/*     */   public JndiRefForwardingDataSource(boolean autoregister) {
/*  59 */     super(autoregister);
/*  60 */     setUpPropertyListeners();
/*     */   }
/*     */   
/*     */   private void setUpPropertyListeners()
/*     */   {
/*  65 */     VetoableChangeListener l = new VetoableChangeListener()
/*     */     {
/*     */       public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
/*     */       {
/*  69 */         Object val = evt.getNewValue();
/*  70 */         if ("jndiName".equals(evt.getPropertyName()))
/*     */         {
/*  72 */           if ((!(val instanceof Name)) && (!(val instanceof String)))
/*  73 */             throw new PropertyVetoException("jndiName must be a String or a javax.naming.Name", evt);
/*     */         }
/*     */       }
/*  76 */     };
/*  77 */     addVetoableChangeListener(l);
/*     */     
/*  79 */     PropertyChangeListener pcl = new PropertyChangeListener()
/*     */     {
/*     */ 
/*  82 */       public void propertyChange(PropertyChangeEvent evt) { JndiRefForwardingDataSource.this.cachedInner = null; }
/*  83 */     };
/*  84 */     addPropertyChangeListener(pcl);
/*     */   }
/*     */   
/*     */   private DataSource dereference()
/*     */     throws SQLException
/*     */   {
/*  90 */     Object jndiName = getJndiName();
/*  91 */     Hashtable jndiEnv = getJndiEnv();
/*     */     try {
/*     */       InitialContext ctx;
/*     */       InitialContext ctx;
/*  95 */       if (jndiEnv != null) {
/*  96 */         ctx = new InitialContext(jndiEnv);
/*     */       } else
/*  98 */         ctx = new InitialContext();
/*  99 */       if ((jndiName instanceof String))
/* 100 */         return (DataSource)ctx.lookup((String)jndiName);
/* 101 */       if ((jndiName instanceof Name)) {
/* 102 */         return (DataSource)ctx.lookup((Name)jndiName);
/*     */       }
/* 104 */       throw new SQLException("Could not find ConnectionPoolDataSource with JNDI name: " + jndiName);
/*     */ 
/*     */     }
/*     */     catch (NamingException e)
/*     */     {
/*     */ 
/* 110 */       if (logger.isLoggable(MLevel.WARNING))
/* 111 */         logger.log(MLevel.WARNING, "An Exception occurred while trying to look up a target DataSource via JNDI!", e);
/* 112 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized DataSource inner() throws SQLException
/*     */   {
/* 118 */     if (this.cachedInner != null) {
/* 119 */       return this.cachedInner;
/*     */     }
/*     */     
/* 122 */     DataSource out = dereference();
/* 123 */     if (isCaching())
/* 124 */       this.cachedInner = out;
/* 125 */     return out;
/*     */   }
/*     */   
/*     */   public Connection getConnection() throws SQLException
/*     */   {
/* 130 */     return inner().getConnection();
/*     */   }
/*     */   
/* 133 */   public Connection getConnection(String username, String password) throws SQLException { return inner().getConnection(username, password); }
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 136 */     return inner().getLogWriter();
/*     */   }
/*     */   
/* 139 */   public void setLogWriter(PrintWriter out) throws SQLException { inner().setLogWriter(out); }
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 142 */     return inner().getLoginTimeout();
/*     */   }
/*     */   
/* 145 */   public void setLoginTimeout(int seconds) throws SQLException { inner().setLoginTimeout(seconds); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 153 */     oos.writeShort(1);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 158 */     short version = ois.readShort();
/* 159 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 162 */       setUpPropertyListeners();
/* 163 */       break;
/*     */     default: 
/* 165 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\JndiRefForwardingDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */