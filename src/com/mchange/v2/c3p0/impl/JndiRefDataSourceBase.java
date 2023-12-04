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
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Name;
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
/*     */ public class JndiRefDataSourceBase
/*     */   extends IdentityTokenResolvable
/*     */   implements Referenceable, Serializable
/*     */ {
/*  36 */   protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
/*     */   
/*     */ 
/*  39 */   protected PropertyChangeSupport getPropertyChangeSupport() { return this.pcs; }
/*  40 */   protected VetoableChangeSupport vcs = new VetoableChangeSupport(this);
/*     */   
/*     */ 
/*  43 */   protected VetoableChangeSupport getVetoableChangeSupport() { return this.vcs; }
/*  44 */   private boolean caching = true;
/*  45 */   private String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
/*     */   private String identityToken;
/*     */   private Hashtable jndiEnv;
/*     */   
/*     */   public boolean isCaching()
/*     */   {
/*  51 */     return this.caching;
/*     */   }
/*     */   
/*     */   public void setCaching(boolean caching) {
/*  55 */     boolean oldVal = this.caching;
/*  56 */     this.caching = caching;
/*  57 */     if (oldVal != caching)
/*  58 */       this.pcs.firePropertyChange("caching", oldVal, caching);
/*     */   }
/*     */   
/*     */   public String getFactoryClassLocation() {
/*  62 */     return this.factoryClassLocation;
/*     */   }
/*     */   
/*     */   public void setFactoryClassLocation(String factoryClassLocation) {
/*  66 */     String oldVal = this.factoryClassLocation;
/*  67 */     this.factoryClassLocation = factoryClassLocation;
/*  68 */     if (!eqOrBothNull(oldVal, factoryClassLocation))
/*  69 */       this.pcs.firePropertyChange("factoryClassLocation", oldVal, factoryClassLocation);
/*     */   }
/*     */   
/*     */   public synchronized String getIdentityToken() {
/*  73 */     return this.identityToken;
/*     */   }
/*     */   
/*     */   public synchronized void setIdentityToken(String identityToken) {
/*  77 */     String oldVal = this.identityToken;
/*  78 */     this.identityToken = identityToken;
/*  79 */     if (!eqOrBothNull(oldVal, identityToken))
/*  80 */       this.pcs.firePropertyChange("identityToken", oldVal, identityToken);
/*     */   }
/*     */   
/*     */   public Hashtable getJndiEnv() {
/*  84 */     return this.jndiEnv != null ? (Hashtable)this.jndiEnv.clone() : null;
/*     */   }
/*     */   
/*     */   public void setJndiEnv(Hashtable jndiEnv) {
/*  88 */     Hashtable oldVal = this.jndiEnv;
/*  89 */     this.jndiEnv = (jndiEnv != null ? (Hashtable)jndiEnv.clone() : null);
/*  90 */     if (!eqOrBothNull(oldVal, jndiEnv))
/*  91 */       this.pcs.firePropertyChange("jndiEnv", oldVal, jndiEnv);
/*     */   }
/*     */   
/*     */   public Object getJndiName() {
/*  95 */     return (this.jndiName instanceof Name) ? ((Name)this.jndiName).clone() : this.jndiName;
/*     */   }
/*     */   
/*     */   public void setJndiName(Object jndiName) throws PropertyVetoException {
/*  99 */     Object oldVal = this.jndiName;
/* 100 */     if (!eqOrBothNull(oldVal, jndiName))
/* 101 */       this.vcs.fireVetoableChange("jndiName", oldVal, jndiName);
/* 102 */     this.jndiName = ((jndiName instanceof Name) ? ((Name)jndiName).clone() : jndiName);
/* 103 */     if (!eqOrBothNull(oldVal, jndiName))
/* 104 */       this.pcs.firePropertyChange("jndiName", oldVal, jndiName);
/*     */   }
/*     */   
/*     */   public void addPropertyChangeListener(PropertyChangeListener pcl) {
/* 108 */     this.pcs.addPropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 111 */   public void addPropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.addPropertyChangeListener(propName, pcl); }
/*     */   
/*     */   public void removePropertyChangeListener(PropertyChangeListener pcl) {
/* 114 */     this.pcs.removePropertyChangeListener(pcl);
/*     */   }
/*     */   
/* 117 */   public void removePropertyChangeListener(String propName, PropertyChangeListener pcl) { this.pcs.removePropertyChangeListener(propName, pcl); }
/*     */   
/*     */   public void addVetoableChangeListener(VetoableChangeListener vcl)
/*     */   {
/* 121 */     this.vcs.addVetoableChangeListener(vcl);
/*     */   }
/*     */   
/* 124 */   public void removeVetoableChangeListener(VetoableChangeListener vcl) { this.vcs.removeVetoableChangeListener(vcl); }
/*     */   
/*     */   private boolean eqOrBothNull(Object a, Object b)
/*     */   {
/* 128 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */   
/*     */ 
/*     */   private Object jndiName;
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final short VERSION = 1;
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 138 */     oos.writeShort(1);
/* 139 */     oos.writeBoolean(this.caching);
/* 140 */     oos.writeObject(this.factoryClassLocation);
/* 141 */     oos.writeObject(this.identityToken);
/* 142 */     oos.writeObject(this.jndiEnv);
/*     */     
/*     */     try
/*     */     {
/* 146 */       SerializableUtils.toByteArray(this.jndiName);
/* 147 */       oos.writeObject(this.jndiName);
/*     */     }
/*     */     catch (NotSerializableException nse)
/*     */     {
/*     */       try
/*     */       {
/* 153 */         Indirector indirector = new ReferenceIndirector();
/* 154 */         oos.writeObject(indirector.indirectForm(this.jndiName));
/*     */       }
/*     */       catch (IOException indirectionIOException) {
/* 157 */         throw indirectionIOException;
/*     */       } catch (Exception indirectionOtherException) {
/* 159 */         throw new IOException("Problem indirectly serializing jndiName: " + indirectionOtherException.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
/* 165 */     short version = ois.readShort();
/* 166 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 169 */       this.caching = ois.readBoolean();
/* 170 */       this.factoryClassLocation = ((String)ois.readObject());
/* 171 */       this.identityToken = ((String)ois.readObject());
/* 172 */       this.jndiEnv = ((Hashtable)ois.readObject());
/* 173 */       Object o = ois.readObject();
/* 174 */       if ((o instanceof IndirectlySerialized)) o = ((IndirectlySerialized)o).getObject();
/* 175 */       this.jndiName = o;
/* 176 */       this.pcs = new PropertyChangeSupport(this);
/* 177 */       this.vcs = new VetoableChangeSupport(this);
/* 178 */       break;
/*     */     default: 
/* 180 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 186 */     StringBuffer sb = new StringBuffer();
/* 187 */     sb.append(super.toString());
/* 188 */     sb.append(" [ ");
/* 189 */     sb.append("caching -> " + this.caching);
/* 190 */     sb.append(", ");
/* 191 */     sb.append("factoryClassLocation -> " + this.factoryClassLocation);
/* 192 */     sb.append(", ");
/* 193 */     sb.append("identityToken -> " + this.identityToken);
/* 194 */     sb.append(", ");
/* 195 */     sb.append("jndiEnv -> " + this.jndiEnv);
/* 196 */     sb.append(", ");
/* 197 */     sb.append("jndiName -> " + this.jndiName);
/*     */     
/* 199 */     String extraToStringInfo = extraToStringInfo();
/* 200 */     if (extraToStringInfo != null)
/* 201 */       sb.append(extraToStringInfo);
/* 202 */     sb.append(" ]");
/* 203 */     return sb.toString();
/*     */   }
/*     */   
/*     */ 
/* 207 */   protected String extraToStringInfo() { return null; }
/*     */   
/* 209 */   static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();
/*     */   
/*     */   static
/*     */   {
/* 213 */     referenceMaker.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
/* 214 */     referenceMaker.addReferenceProperty("caching");
/* 215 */     referenceMaker.addReferenceProperty("factoryClassLocation");
/* 216 */     referenceMaker.addReferenceProperty("identityToken");
/* 217 */     referenceMaker.addReferenceProperty("jndiEnv");
/* 218 */     referenceMaker.addReferenceProperty("jndiName");
/*     */   }
/*     */   
/*     */   public Reference getReference() throws NamingException
/*     */   {
/* 223 */     return referenceMaker.createReference(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JndiRefDataSourceBase(boolean autoregister)
/*     */   {
/* 231 */     if (autoregister)
/*     */     {
/* 233 */       this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
/* 234 */       C3P0Registry.reregister(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private JndiRefDataSourceBase() {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\JndiRefDataSourceBase.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */