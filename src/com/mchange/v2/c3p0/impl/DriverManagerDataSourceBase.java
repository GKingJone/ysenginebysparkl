/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0Registry;
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.naming.JavaBeanReferenceMaker;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyChangeSupport;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Properties;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
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
/*     */ public abstract class DriverManagerDataSourceBase
/*     */   extends IdentityTokenResolvable
/*     */   implements Referenceable, Serializable
/*     */ {
/*  33 */   protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
/*     */   protected String description;
/*     */   
/*  36 */   protected PropertyChangeSupport getPropertyChangeSupport() { return this.pcs; }
/*     */   
/*  38 */   protected String driverClass = C3P0Config.initializeStringPropertyVar("driverClass", C3P0Defaults.driverClass());
/*  39 */   protected String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
/*     */   private String identityToken;
/*  41 */   protected String jdbcUrl = C3P0Config.initializeStringPropertyVar("jdbcUrl", C3P0Defaults.jdbcUrl());
/*  42 */   protected Properties properties = new AuthMaskingProperties();
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*  45 */   public synchronized String getDescription() { return this.description; }
/*     */   
/*     */   public synchronized void setDescription(String description)
/*     */   {
/*  49 */     this.description = description;
/*     */   }
/*     */   
/*     */   public synchronized String getDriverClass() {
/*  53 */     return this.driverClass;
/*     */   }
/*     */   
/*     */   public synchronized void setDriverClass(String driverClass) {
/*  57 */     String oldVal = this.driverClass;
/*  58 */     this.driverClass = driverClass;
/*  59 */     if (!eqOrBothNull(oldVal, driverClass))
/*  60 */       this.pcs.firePropertyChange("driverClass", oldVal, driverClass);
/*     */   }
/*     */   
/*     */   public synchronized String getFactoryClassLocation() {
/*  64 */     return this.factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized void setFactoryClassLocation(String factoryClassLocation) {
/*  68 */     this.factoryClassLocation = factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized String getIdentityToken() {
/*  72 */     return this.identityToken;
/*     */   }
/*     */   
/*     */   public synchronized void setIdentityToken(String identityToken) {
/*  76 */     String oldVal = this.identityToken;
/*  77 */     this.identityToken = identityToken;
/*  78 */     if (!eqOrBothNull(oldVal, identityToken))
/*  79 */       this.pcs.firePropertyChange("identityToken", oldVal, identityToken);
/*     */   }
/*     */   
/*     */   public synchronized String getJdbcUrl() {
/*  83 */     return this.jdbcUrl;
/*     */   }
/*     */   
/*     */   public synchronized void setJdbcUrl(String jdbcUrl) {
/*  87 */     this.jdbcUrl = jdbcUrl;
/*     */   }
/*     */   
/*     */   public synchronized Properties getProperties() {
/*  91 */     return AuthMaskingProperties.fromAnyProperties(this.properties);
/*     */   }
/*     */   
/*     */   public synchronized void setProperties(Properties properties) {
/*  95 */     Properties oldVal = this.properties;
/*  96 */     this.properties = AuthMaskingProperties.fromAnyProperties(properties);
/*  97 */     if (!eqOrBothNull(oldVal, properties))
/*  98 */       this.pcs.firePropertyChange("properties", oldVal, properties);
/*     */   }
/*     */   
/*     */   public void addPropertyChangeListener(PropertyChangeListener pcl) {
/* 102 */     this.pcs.addPropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 105 */   public void addPropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.addPropertyChangeListener(propName, pcl); }
/*     */   
/*     */   public void removePropertyChangeListener(PropertyChangeListener pcl) {
/* 108 */     this.pcs.removePropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 111 */   public void removePropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.removePropertyChangeListener(propName, pcl); }
/*     */   
/*     */   private boolean eqOrBothNull(Object a, Object b)
/*     */   {
/* 115 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 125 */     oos.writeShort(1);
/* 126 */     oos.writeObject(this.description);
/* 127 */     oos.writeObject(this.driverClass);
/* 128 */     oos.writeObject(this.factoryClassLocation);
/* 129 */     oos.writeObject(this.identityToken);
/* 130 */     oos.writeObject(this.jdbcUrl);
/* 131 */     oos.writeObject(this.properties);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 136 */     short version = ois.readShort();
/* 137 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 140 */       this.description = ((String)ois.readObject());
/* 141 */       this.driverClass = ((String)ois.readObject());
/* 142 */       this.factoryClassLocation = ((String)ois.readObject());
/* 143 */       this.identityToken = ((String)ois.readObject());
/* 144 */       this.jdbcUrl = ((String)ois.readObject());
/* 145 */       this.properties = ((Properties)ois.readObject());
/* 146 */       this.pcs = new PropertyChangeSupport(this);
/* 147 */       break;
/*     */     default: 
/* 149 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 155 */     StringBuffer sb = new StringBuffer();
/* 156 */     sb.append(super.toString());
/* 157 */     sb.append(" [ ");
/* 158 */     sb.append("description -> " + this.description);
/* 159 */     sb.append(", ");
/* 160 */     sb.append("driverClass -> " + this.driverClass);
/* 161 */     sb.append(", ");
/* 162 */     sb.append("factoryClassLocation -> " + this.factoryClassLocation);
/* 163 */     sb.append(", ");
/* 164 */     sb.append("identityToken -> " + this.identityToken);
/* 165 */     sb.append(", ");
/* 166 */     sb.append("jdbcUrl -> " + this.jdbcUrl);
/* 167 */     sb.append(", ");
/* 168 */     sb.append("properties -> " + this.properties);
/*     */     
/* 170 */     String extraToStringInfo = extraToStringInfo();
/* 171 */     if (extraToStringInfo != null)
/* 172 */       sb.append(extraToStringInfo);
/* 173 */     sb.append(" ]");
/* 174 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/* 178 */   protected String extraToStringInfo() { return null; }
/*     */   
/* 180 */   static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();
/*     */   
/*     */   static
/*     */   {
/* 184 */     referenceMaker.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
/* 185 */     referenceMaker.addReferenceProperty("description");
/* 186 */     referenceMaker.addReferenceProperty("driverClass");
/* 187 */     referenceMaker.addReferenceProperty("factoryClassLocation");
/* 188 */     referenceMaker.addReferenceProperty("identityToken");
/* 189 */     referenceMaker.addReferenceProperty("jdbcUrl");
/* 190 */     referenceMaker.addReferenceProperty("properties");
/*     */   }
/*     */   
/*     */   public Reference getReference() throws NamingException
/*     */   {
/* 195 */     return referenceMaker.createReference(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DriverManagerDataSourceBase(boolean autoregister)
/*     */   {
/* 203 */     if (autoregister)
/*     */     {
/* 205 */       this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
/* 206 */       C3P0Registry.reregister(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private DriverManagerDataSourceBase() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\DriverManagerDataSourceBase.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */