/*     */ package com.mchange.v2.naming;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.ser.IndirectlySerialized;
/*     */ import com.mchange.v2.ser.Indirector;
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.util.Hashtable;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.InitialContext;
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
/*     */ public class ReferenceIndirector
/*     */   implements Indirector
/*     */ {
/*  43 */   static final MLogger logger = MLog.getLogger(ReferenceIndirector.class);
/*     */   Name name;
/*     */   Name contextName;
/*     */   Hashtable environmentProperties;
/*     */   
/*     */   public Name getName()
/*     */   {
/*  50 */     return this.name;
/*     */   }
/*     */   
/*  53 */   public void setName(Name name) { this.name = name; }
/*     */   
/*     */   public Name getNameContextName() {
/*  56 */     return this.contextName;
/*     */   }
/*     */   
/*  59 */   public void setNameContextName(Name contextName) { this.contextName = contextName; }
/*     */   
/*     */   public Hashtable getEnvironmentProperties() {
/*  62 */     return this.environmentProperties;
/*     */   }
/*     */   
/*  65 */   public void setEnvironmentProperties(Hashtable environmentProperties) { this.environmentProperties = environmentProperties; }
/*     */   
/*     */   public IndirectlySerialized indirectForm(Object orig) throws Exception
/*     */   {
/*  69 */     Reference ref = ((Referenceable)orig).getReference();
/*  70 */     return new ReferenceSerialized(ref, this.name, this.contextName, this.environmentProperties);
/*     */   }
/*     */   
/*     */ 
/*     */   private static class ReferenceSerialized
/*     */     implements IndirectlySerialized
/*     */   {
/*     */     Reference reference;
/*     */     
/*     */     Name name;
/*     */     Name contextName;
/*     */     Hashtable env;
/*     */     
/*     */     ReferenceSerialized(Reference reference, Name name, Name contextName, Hashtable env)
/*     */     {
/*  85 */       this.reference = reference;
/*  86 */       this.name = name;
/*  87 */       this.contextName = contextName;
/*  88 */       this.env = env;
/*     */     }
/*     */     
/*     */     public Object getObject() throws ClassNotFoundException, IOException
/*     */     {
/*     */       try
/*     */       {
/*     */         Context initialContext;
/*     */         Context initialContext;
/*  97 */         if (this.env == null) {
/*  98 */           initialContext = new InitialContext();
/*     */         } else {
/* 100 */           initialContext = new InitialContext(this.env);
/*     */         }
/* 102 */         Context nameContext = null;
/* 103 */         if (this.contextName != null) {
/* 104 */           nameContext = (Context)initialContext.lookup(this.contextName);
/*     */         }
/* 106 */         return ReferenceableUtils.referenceToObject(this.reference, this.name, nameContext, this.env);
/*     */ 
/*     */       }
/*     */       catch (NamingException e)
/*     */       {
/* 111 */         if (ReferenceIndirector.logger.isLoggable(MLevel.WARNING))
/* 112 */           ReferenceIndirector.logger.log(MLevel.WARNING, "Failed to acquire the Context necessary to lookup an Object.", e);
/* 113 */         throw new InvalidObjectException("Failed to acquire the Context necessary to lookup an Object: " + e.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\naming\ReferenceIndirector.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */