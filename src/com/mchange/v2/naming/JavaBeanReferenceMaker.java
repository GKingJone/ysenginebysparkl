/*     */ package com.mchange.v2.naming;
/*     */ 
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.lang.Coerce;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.ser.IndirectPolicy;
/*     */ import com.mchange.v2.ser.SerializableUtils;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.naming.BinaryRefAddr;
/*     */ import javax.naming.NamingException;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.StringRefAddr;
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
/*     */ public class JavaBeanReferenceMaker
/*     */   implements ReferenceMaker
/*     */ {
/*  39 */   private static final MLogger logger = MLog.getLogger(JavaBeanReferenceMaker.class);
/*     */   
/*     */   static final String REF_PROPS_KEY = "com.mchange.v2.naming.JavaBeanReferenceMaker.REF_PROPS_KEY";
/*     */   
/*  43 */   static final Object[] EMPTY_ARGS = new Object[0];
/*     */   
/*  45 */   static final byte[] NULL_TOKEN_BYTES = new byte[0];
/*     */   
/*  47 */   String factoryClassName = "com.mchange.v2.naming.JavaBeanObjectFactory";
/*  48 */   String defaultFactoryClassLocation = null;
/*     */   
/*  50 */   Set referenceProperties = new HashSet();
/*     */   
/*  52 */   ReferenceIndirector indirector = new ReferenceIndirector();
/*     */   
/*     */   public Hashtable getEnvironmentProperties() {
/*  55 */     return this.indirector.getEnvironmentProperties();
/*     */   }
/*     */   
/*  58 */   public void setEnvironmentProperties(Hashtable environmentProperties) { this.indirector.setEnvironmentProperties(environmentProperties); }
/*     */   
/*     */   public void setFactoryClassName(String factoryClassName) {
/*  61 */     this.factoryClassName = factoryClassName;
/*     */   }
/*     */   
/*  64 */   public String getFactoryClassName() { return this.factoryClassName; }
/*     */   
/*     */   public String getDefaultFactoryClassLocation() {
/*  67 */     return this.defaultFactoryClassLocation;
/*     */   }
/*     */   
/*  70 */   public void setDefaultFactoryClassLocation(String defaultFactoryClassLocation) { this.defaultFactoryClassLocation = defaultFactoryClassLocation; }
/*     */   
/*     */   public void addReferenceProperty(String propName) {
/*  73 */     this.referenceProperties.add(propName);
/*     */   }
/*     */   
/*  76 */   public void removeReferenceProperty(String propName) { this.referenceProperties.remove(propName); }
/*     */   
/*     */   public Reference createReference(Object bean)
/*     */     throws NamingException
/*     */   {
/*     */     try
/*     */     {
/*  83 */       BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
/*  84 */       PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/*  85 */       List refAddrs = new ArrayList();
/*  86 */       String factoryClassLocation = this.defaultFactoryClassLocation;
/*     */       
/*  88 */       boolean using_ref_props = this.referenceProperties.size() > 0;
/*     */       
/*     */ 
/*  91 */       if (using_ref_props) {
/*  92 */         refAddrs.add(new BinaryRefAddr("com.mchange.v2.naming.JavaBeanReferenceMaker.REF_PROPS_KEY", SerializableUtils.toByteArray(this.referenceProperties)));
/*     */       }
/*  94 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/*  96 */         PropertyDescriptor pd = pds[i];
/*  97 */         String propertyName = pd.getName();
/*     */         
/*     */ 
/* 100 */         if ((!using_ref_props) || (this.referenceProperties.contains(propertyName)))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */           Class propertyType = pd.getPropertyType();
/* 107 */           Method getter = pd.getReadMethod();
/* 108 */           Method setter = pd.getWriteMethod();
/* 109 */           if ((getter != null) && (setter != null))
/*     */           {
/* 111 */             Object val = getter.invoke(bean, EMPTY_ARGS);
/*     */             
/* 113 */             if (propertyName.equals("factoryClassLocation"))
/*     */             {
/* 115 */               if (String.class != propertyType) {
/* 116 */                 throw new NamingException(getClass().getName() + " requires a factoryClassLocation property to be a string, " + propertyType.getName() + " is not valid.");
/*     */               }
/* 118 */               factoryClassLocation = (String)val;
/*     */             }
/*     */             
/* 121 */             if (val == null)
/*     */             {
/* 123 */               RefAddr addMe = new BinaryRefAddr(propertyName, NULL_TOKEN_BYTES);
/* 124 */               refAddrs.add(addMe);
/*     */             }
/* 126 */             else if (Coerce.canCoerce(propertyType))
/*     */             {
/* 128 */               RefAddr addMe = new StringRefAddr(propertyName, String.valueOf(val));
/* 129 */               refAddrs.add(addMe);
/*     */             }
/*     */             else
/*     */             {
/* 133 */               RefAddr addMe = null;
/* 134 */               PropertyEditor pe = BeansUtils.findPropertyEditor(pd);
/* 135 */               if (pe != null)
/*     */               {
/* 137 */                 pe.setValue(val);
/* 138 */                 String textValue = pe.getAsText();
/* 139 */                 if (textValue != null)
/* 140 */                   addMe = new StringRefAddr(propertyName, textValue);
/*     */               }
/* 142 */               if (addMe == null) {
/* 143 */                 addMe = new BinaryRefAddr(propertyName, SerializableUtils.toByteArray(val, this.indirector, IndirectPolicy.INDIRECT_ON_EXCEPTION));
/*     */               }
/*     */               
/* 146 */               refAddrs.add(addMe);
/*     */ 
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */           }
/* 154 */           else if (logger.isLoggable(MLevel.WARNING)) {
/* 155 */             logger.warning(getClass().getName() + ": Skipping " + propertyName + " because it is " + (setter == null ? "read-only." : "write-only."));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 160 */       Reference out = new Reference(bean.getClass().getName(), this.factoryClassName, factoryClassLocation);
/* 161 */       for (Iterator ii = refAddrs.iterator(); ii.hasNext();)
/* 162 */         out.add((RefAddr)ii.next());
/* 163 */       return out;
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 168 */       if (logger.isLoggable(MLevel.FINE)) {
/* 169 */         logger.log(MLevel.FINE, "Exception trying to create Reference.", e);
/*     */       }
/* 171 */       throw new NamingException("Could not create reference from bean: " + e.toString());
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\naming\JavaBeanReferenceMaker.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */