/*     */ package com.mchange.v2.naming;
/*     */ 
/*     */ import com.mchange.v2.beans.BeansUtils;
/*     */ import com.mchange.v2.lang.Coerce;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.ser.SerializableUtils;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.naming.BinaryRefAddr;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaBeanObjectFactory
/*     */   implements ObjectFactory
/*     */ {
/*  38 */   private static final MLogger logger = MLog.getLogger(JavaBeanObjectFactory.class);
/*     */   
/*  40 */   static final Object NULL_TOKEN = new Object();
/*     */   
/*     */   public Object getObjectInstance(Object refObj, Name name, Context nameCtx, Hashtable env)
/*     */     throws Exception
/*     */   {
/*  45 */     if ((refObj instanceof Reference))
/*     */     {
/*  47 */       Reference ref = (Reference)refObj;
/*  48 */       Map refAddrsMap = new HashMap();
/*  49 */       for (Enumeration e = ref.getAll(); e.hasMoreElements();)
/*     */       {
/*  51 */         RefAddr addr = (RefAddr)e.nextElement();
/*  52 */         refAddrsMap.put(addr.getType(), addr);
/*     */       }
/*  54 */       Class beanClass = Class.forName(ref.getClassName());
/*  55 */       Set refProps = null;
/*  56 */       RefAddr refPropsRefAddr = (BinaryRefAddr)refAddrsMap.remove("com.mchange.v2.naming.JavaBeanReferenceMaker.REF_PROPS_KEY");
/*  57 */       if (refPropsRefAddr != null)
/*  58 */         refProps = (Set)SerializableUtils.fromByteArray((byte[])refPropsRefAddr.getContent());
/*  59 */       Map propMap = createPropertyMap(beanClass, refAddrsMap);
/*  60 */       return findBean(beanClass, propMap, refProps);
/*     */     }
/*     */     
/*  63 */     return null;
/*     */   }
/*     */   
/*     */   private Map createPropertyMap(Class beanClass, Map refAddrsMap) throws Exception
/*     */   {
/*  68 */     BeanInfo bi = Introspector.getBeanInfo(beanClass);
/*  69 */     PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/*     */     
/*  71 */     Map out = new HashMap();
/*  72 */     int i = 0; for (int len = pds.length; i < len; i++)
/*     */     {
/*  74 */       PropertyDescriptor pd = pds[i];
/*  75 */       String propertyName = pd.getName();
/*  76 */       Class propertyType = pd.getPropertyType();
/*  77 */       Object addr = refAddrsMap.remove(propertyName);
/*  78 */       if (addr != null)
/*     */       {
/*  80 */         if ((addr instanceof StringRefAddr))
/*     */         {
/*  82 */           String content = (String)((StringRefAddr)addr).getContent();
/*  83 */           if (Coerce.canCoerce(propertyType)) {
/*  84 */             out.put(propertyName, Coerce.toObject(content, propertyType));
/*     */           }
/*     */           else {
/*  87 */             PropertyEditor pe = BeansUtils.findPropertyEditor(pd);
/*  88 */             pe.setAsText(content);
/*  89 */             out.put(propertyName, pe.getValue());
/*     */           }
/*     */         }
/*  92 */         else if ((addr instanceof BinaryRefAddr))
/*     */         {
/*  94 */           byte[] content = (byte[])((BinaryRefAddr)addr).getContent();
/*  95 */           if (content.length == 0) {
/*  96 */             out.put(propertyName, NULL_TOKEN);
/*     */           } else {
/*  98 */             out.put(propertyName, SerializableUtils.fromByteArray(content));
/*     */           }
/*     */           
/*     */         }
/* 102 */         else if (logger.isLoggable(MLevel.WARNING)) {
/* 103 */           logger.warning(getClass().getName() + " -- unknown RefAddr subclass: " + addr.getClass().getName());
/*     */         }
/*     */       }
/*     */     }
/* 107 */     for (Iterator ii = refAddrsMap.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 109 */       String type = (String)ii.next();
/* 110 */       if (logger.isLoggable(MLevel.WARNING))
/* 111 */         logger.warning(getClass().getName() + " -- RefAddr for unknown property: " + type);
/*     */     }
/* 113 */     return out;
/*     */   }
/*     */   
/*     */   protected Object createBlankInstance(Class beanClass) throws Exception {
/* 117 */     return beanClass.newInstance();
/*     */   }
/*     */   
/*     */   protected Object findBean(Class beanClass, Map propertyMap, Set refProps) throws Exception {
/* 121 */     Object bean = createBlankInstance(beanClass);
/* 122 */     BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
/* 123 */     PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/*     */     
/* 125 */     int i = 0; for (int len = pds.length; i < len; i++)
/*     */     {
/* 127 */       PropertyDescriptor pd = pds[i];
/* 128 */       String propertyName = pd.getName();
/* 129 */       Object value = propertyMap.get(propertyName);
/* 130 */       Method setter = pd.getWriteMethod();
/* 131 */       if (value != null)
/*     */       {
/* 133 */         if (setter != null) {
/* 134 */           setter.invoke(bean, new Object[] { value == NULL_TOKEN ? null : value });
/*     */ 
/*     */ 
/*     */         }
/* 138 */         else if (logger.isLoggable(MLevel.WARNING)) {
/* 139 */           logger.warning(getClass().getName() + ": Could not restore read-only property '" + propertyName + "'.");
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 144 */       else if (setter != null)
/*     */       {
/* 146 */         if ((refProps == null) || (refProps.contains(propertyName)))
/*     */         {
/*     */ 
/*     */ 
/* 150 */           if (logger.isLoggable(MLevel.WARNING)) {
/* 151 */             logger.warning(getClass().getName() + " -- Expected writable property ''" + propertyName + "'' left at default value");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 157 */     return bean;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\naming\JavaBeanObjectFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */