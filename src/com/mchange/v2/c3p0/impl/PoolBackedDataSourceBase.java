/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0Registry;
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
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PoolBackedDataSourceBase
/*     */   extends IdentityTokenResolvable
/*     */   implements Referenceable, Serializable
/*     */ {
/*  36 */   protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
/*     */   
/*     */ 
/*  39 */   protected PropertyChangeSupport getPropertyChangeSupport() { return this.pcs; }
/*  40 */   protected VetoableChangeSupport vcs = new VetoableChangeSupport(this);
/*     */   private ConnectionPoolDataSource connectionPoolDataSource;
/*     */   
/*  43 */   protected VetoableChangeSupport getVetoableChangeSupport() { return this.vcs; }
/*     */   
/*  45 */   private String dataSourceName = null;
/*  46 */   private String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
/*     */   private String identityToken;
/*  48 */   private int numHelperThreads = C3P0Config.initializeIntPropertyVar("numHelperThreads", C3P0Defaults.numHelperThreads());
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*  51 */   public synchronized ConnectionPoolDataSource getConnectionPoolDataSource() { return this.connectionPoolDataSource; }
/*     */   
/*     */   public synchronized void setConnectionPoolDataSource(ConnectionPoolDataSource connectionPoolDataSource) throws PropertyVetoException
/*     */   {
/*  55 */     ConnectionPoolDataSource oldVal = this.connectionPoolDataSource;
/*  56 */     if (!eqOrBothNull(oldVal, connectionPoolDataSource))
/*  57 */       this.vcs.fireVetoableChange("connectionPoolDataSource", oldVal, connectionPoolDataSource);
/*  58 */     this.connectionPoolDataSource = connectionPoolDataSource;
/*  59 */     if (!eqOrBothNull(oldVal, connectionPoolDataSource))
/*  60 */       this.pcs.firePropertyChange("connectionPoolDataSource", oldVal, connectionPoolDataSource);
/*     */   }
/*     */   
/*     */   public synchronized String getDataSourceName() {
/*  64 */     return this.dataSourceName;
/*     */   }
/*     */   
/*     */   public synchronized void setDataSourceName(String dataSourceName) {
/*  68 */     this.dataSourceName = dataSourceName;
/*     */   }
/*     */   
/*     */   public synchronized String getFactoryClassLocation() {
/*  72 */     return this.factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized void setFactoryClassLocation(String factoryClassLocation) {
/*  76 */     this.factoryClassLocation = factoryClassLocation;
/*     */   }
/*     */   
/*     */   public synchronized String getIdentityToken() {
/*  80 */     return this.identityToken;
/*     */   }
/*     */   
/*     */   public synchronized void setIdentityToken(String identityToken) {
/*  84 */     String oldVal = this.identityToken;
/*  85 */     this.identityToken = identityToken;
/*  86 */     if (!eqOrBothNull(oldVal, identityToken))
/*  87 */       this.pcs.firePropertyChange("identityToken", oldVal, identityToken);
/*     */   }
/*     */   
/*     */   public synchronized int getNumHelperThreads() {
/*  91 */     return this.numHelperThreads;
/*     */   }
/*     */   
/*     */   public synchronized void setNumHelperThreads(int numHelperThreads) {
/*  95 */     int oldVal = this.numHelperThreads;
/*  96 */     this.numHelperThreads = numHelperThreads;
/*  97 */     if (oldVal != numHelperThreads)
/*  98 */       this.pcs.firePropertyChange("numHelperThreads", oldVal, numHelperThreads);
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
/*     */   public void addVetoableChangeListener(VetoableChangeListener vcl)
/*     */   {
/* 115 */     this.vcs.addVetoableChangeListener(vcl);
/*     */   }
/*     */   
/* 118 */   public void removeVetoableChangeListener(VetoableChangeListener vcl) { this.vcs.removeVetoableChangeListener(vcl); }
/*     */   
/*     */   private boolean eqOrBothNull(Object a, Object b)
/*     */   {
/* 122 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 132 */     oos.writeShort(1);
/*     */     
/*     */     try
/*     */     {
/* 136 */       SerializableUtils.toByteArray(this.connectionPoolDataSource);
/* 137 */       oos.writeObject(this.connectionPoolDataSource);
/*     */     }
/*     */     catch (NotSerializableException nse)
/*     */     {
/*     */       try
/*     */       {
/* 143 */         Indirector indirector = new ReferenceIndirector();
/* 144 */         oos.writeObject(indirector.indirectForm(this.connectionPoolDataSource));
/*     */       }
/*     */       catch (IOException indirectionIOException) {
/* 147 */         throw indirectionIOException;
/*     */       } catch (Exception indirectionOtherException) {
/* 149 */         throw new IOException("Problem indirectly serializing connectionPoolDataSource: " + indirectionOtherException.toString());
/*     */       } }
/* 151 */     oos.writeObject(this.dataSourceName);
/* 152 */     oos.writeObject(this.factoryClassLocation);
/* 153 */     oos.writeObject(this.identityToken);
/* 154 */     oos.writeInt(this.numHelperThreads);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 159 */     short version = ois.readShort();
/* 160 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 163 */       Object o = ois.readObject();
/* 164 */       if ((o instanceof IndirectlySerialized)) o = ((IndirectlySerialized)o).getObject();
/* 165 */       this.connectionPoolDataSource = ((ConnectionPoolDataSource)o);
/* 166 */       this.dataSourceName = ((String)ois.readObject());
/* 167 */       this.factoryClassLocation = ((String)ois.readObject());
/* 168 */       this.identityToken = ((String)ois.readObject());
/* 169 */       this.numHelperThreads = ois.readInt();
/* 170 */       this.pcs = new PropertyChangeSupport(this);
/* 171 */       this.vcs = new VetoableChangeSupport(this);
/* 172 */       break;
/*     */     default: 
/* 174 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 180 */     StringBuffer sb = new StringBuffer();
/* 181 */     sb.append(super.toString());
/* 182 */     sb.append(" [ ");
/* 183 */     sb.append("connectionPoolDataSource -> " + this.connectionPoolDataSource);
/* 184 */     sb.append(", ");
/* 185 */     sb.append("dataSourceName -> " + this.dataSourceName);
/* 186 */     sb.append(", ");
/* 187 */     sb.append("factoryClassLocation -> " + this.factoryClassLocation);
/* 188 */     sb.append(", ");
/* 189 */     sb.append("identityToken -> " + this.identityToken);
/* 190 */     sb.append(", ");
/* 191 */     sb.append("numHelperThreads -> " + this.numHelperThreads);
/*     */     
/* 193 */     String extraToStringInfo = extraToStringInfo();
/* 194 */     if (extraToStringInfo != null)
/* 195 */       sb.append(extraToStringInfo);
/* 196 */     sb.append(" ]");
/* 197 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/* 201 */   protected String extraToStringInfo() { return null; }
/*     */   
/* 203 */   static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();
/*     */   
/*     */   static
/*     */   {
/* 207 */     referenceMaker.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
/* 208 */     referenceMaker.addReferenceProperty("connectionPoolDataSource");
/* 209 */     referenceMaker.addReferenceProperty("dataSourceName");
/* 210 */     referenceMaker.addReferenceProperty("factoryClassLocation");
/* 211 */     referenceMaker.addReferenceProperty("identityToken");
/* 212 */     referenceMaker.addReferenceProperty("numHelperThreads");
/*     */   }
/*     */   
/*     */   public Reference getReference() throws NamingException
/*     */   {
/* 217 */     return referenceMaker.createReference(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PoolBackedDataSourceBase(boolean autoregister)
/*     */   {
/* 225 */     if (autoregister)
/*     */     {
/* 227 */       this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
/* 228 */       C3P0Registry.reregister(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private PoolBackedDataSourceBase() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\PoolBackedDataSourceBase.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */