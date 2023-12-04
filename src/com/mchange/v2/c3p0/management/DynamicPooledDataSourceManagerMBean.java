/*     */ package com.mchange.v2.c3p0.management;
/*     */ 
/*     */ import com.mchange.v1.lang.ClassUtils;
/*     */ import com.mchange.v2.c3p0.ComboPooledDataSource;
/*     */ import com.mchange.v2.c3p0.DriverManagerDataSource;
/*     */ import com.mchange.v2.c3p0.PooledDataSource;
/*     */ import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
/*     */ import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.management.ManagementUtils;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.DynamicMBean;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.sql.ConnectionPoolDataSource;
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
/*     */ public class DynamicPooledDataSourceManagerMBean
/*     */   implements DynamicMBean
/*     */ {
/*     */   static final MLogger logger;
/*     */   static final Set HIDE_PROPS;
/*     */   static final Set HIDE_OPS;
/*     */   static final Set FORCE_OPS;
/*     */   static final Set FORCE_READ_ONLY_PROPS;
/*     */   
/*     */   static
/*     */   {
/*  64 */     logger = MLog.getLogger(DynamicPooledDataSourceManagerMBean.class);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */     Set hpTmp = new HashSet();
/*  75 */     hpTmp.add("connectionPoolDataSource");
/*  76 */     hpTmp.add("nestedDataSource");
/*  77 */     hpTmp.add("reference");
/*  78 */     hpTmp.add("connection");
/*  79 */     hpTmp.add("password");
/*  80 */     hpTmp.add("pooledConnection");
/*  81 */     hpTmp.add("properties");
/*  82 */     hpTmp.add("logWriter");
/*  83 */     hpTmp.add("lastAcquisitionFailureDefaultUser");
/*  84 */     hpTmp.add("lastCheckoutFailureDefaultUser");
/*  85 */     hpTmp.add("lastCheckinFailureDefaultUser");
/*  86 */     hpTmp.add("lastIdleTestFailureDefaultUser");
/*  87 */     hpTmp.add("lastConnectionTestFailureDefaultUser");
/*  88 */     HIDE_PROPS = Collections.unmodifiableSet(hpTmp);
/*     */     
/*  90 */     Class[] userPassArgs = { String.class, String.class };
/*  91 */     Set hoTmp = new HashSet();
/*     */     try
/*     */     {
/*  94 */       hoTmp.add(class$com$mchange$v2$c3p0$PooledDataSource.getMethod("close", new Class[] { Boolean.TYPE }));
/*  95 */       hoTmp.add(PooledDataSource.class.getMethod("getConnection", userPassArgs));
/*     */       
/*  97 */       hoTmp.add(PooledDataSource.class.getMethod("getLastAcquisitionFailure", userPassArgs));
/*  98 */       hoTmp.add(PooledDataSource.class.getMethod("getLastCheckinFailure", userPassArgs));
/*  99 */       hoTmp.add(PooledDataSource.class.getMethod("getLastCheckoutFailure", userPassArgs));
/* 100 */       hoTmp.add(PooledDataSource.class.getMethod("getLastIdleTestFailure", userPassArgs));
/* 101 */       hoTmp.add(PooledDataSource.class.getMethod("getLastConnectionTestFailure", userPassArgs));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 105 */       logger.log(MLevel.WARNING, "Tried to hide an operation from being exposed by mbean, but failed to find the operation!", e);
/*     */     }
/* 107 */     HIDE_OPS = Collections.unmodifiableSet(hoTmp);
/*     */     
/* 109 */     Set fropTmp = new HashSet();
/* 110 */     fropTmp.add("identityToken");
/* 111 */     FORCE_READ_ONLY_PROPS = Collections.unmodifiableSet(fropTmp);
/*     */     
/* 113 */     Set foTmp = new HashSet();
/* 114 */     FORCE_OPS = Collections.unmodifiableSet(foTmp);
/*     */   }
/*     */   
/* 117 */   static final MBeanOperationInfo[] OP_INFS = extractOpInfs();
/*     */   
/* 119 */   MBeanInfo info = null;
/*     */   
/*     */   PooledDataSource pds;
/*     */   
/*     */   String mbeanName;
/*     */   
/*     */   MBeanServer mbs;
/*     */   
/*     */   ConnectionPoolDataSource cpds;
/*     */   
/*     */   DataSource unpooledDataSource;
/*     */   Map pdsAttrInfos;
/*     */   Map cpdsAttrInfos;
/*     */   Map unpooledDataSourceAttrInfos;
/* 133 */   PropertyChangeListener pcl = new PropertyChangeListener()
/*     */   {
/*     */     public void propertyChange(PropertyChangeEvent evt)
/*     */     {
/* 137 */       String propName = evt.getPropertyName();
/* 138 */       Object val = evt.getNewValue();
/*     */       
/* 140 */       if (("nestedDataSource".equals(propName)) || ("connectionPoolDataSource".equals(propName))) {
/* 141 */         DynamicPooledDataSourceManagerMBean.this.reinitialize();
/*     */       }
/*     */     }
/*     */   };
/*     */   
/*     */   public DynamicPooledDataSourceManagerMBean(PooledDataSource pds, String mbeanName, MBeanServer mbs) throws Exception
/*     */   {
/* 148 */     this.pds = pds;
/* 149 */     this.mbeanName = mbeanName;
/* 150 */     this.mbs = mbs;
/*     */     
/* 152 */     if (!(pds instanceof ComboPooledDataSource))
/*     */     {
/* 154 */       if ((pds instanceof AbstractPoolBackedDataSource)) {
/* 155 */         ((AbstractPoolBackedDataSource)pds).addPropertyChangeListener(this.pcl);
/*     */       } else
/* 157 */         logger.warning(this + "managing an unexpected PooledDataSource. Only top-level attributes will be available. PooledDataSource: " + pds);
/*     */     }
/* 159 */     Exception e = reinitialize();
/* 160 */     if (e != null) {
/* 161 */       throw e;
/*     */     }
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
/*     */   private static MBeanOperationInfo[] extractOpInfs()
/*     */   {
/* 269 */     MBeanParameterInfo user = new MBeanParameterInfo("user", "java.lang.String", "The database username of a pool-owner.");
/* 270 */     MBeanParameterInfo pwd = new MBeanParameterInfo("password", "java.lang.String", "The database password of a pool-owner.");
/* 271 */     MBeanParameterInfo[] userPass = { user, pwd };
/* 272 */     MBeanParameterInfo[] empty = new MBeanParameterInfo[0];
/*     */     
/* 274 */     Method[] meths = PooledDataSource.class.getMethods();
/* 275 */     Set attrInfos = new TreeSet(ManagementUtils.OP_INFO_COMPARATOR);
/*     */     
/* 277 */     for (int i = 0; i < meths.length; i++)
/*     */     {
/* 279 */       Method meth = meths[i];
/* 280 */       if (!HIDE_OPS.contains(meth))
/*     */       {
/*     */ 
/* 283 */         String mname = meth.getName();
/* 284 */         Class[] params = meth.getParameterTypes();
/*     */         
/* 286 */         if (!FORCE_OPS.contains(mname))
/*     */         {
/*     */ 
/* 289 */           if ((!mname.startsWith("set")) || (params.length != 1))
/*     */           {
/* 291 */             if (((mname.startsWith("get")) || (mname.startsWith("is"))) && (params.length == 0)) {}
/*     */           }
/*     */         }
/*     */         else {
/* 295 */           Class retType = meth.getReturnType();
/* 296 */           int impact = retType == Void.TYPE ? 1 : 0;
/*     */           MBeanParameterInfo[] pi;
/* 298 */           MBeanParameterInfo[] pi; if ((params.length == 2) && (params[0] == String.class) && (params[1] == String.class)) {
/* 299 */             pi = userPass; } else { MBeanParameterInfo[] pi;
/* 300 */             if (params.length == 0) {
/* 301 */               pi = empty;
/*     */             } else
/* 303 */               pi = null; }
/*     */           MBeanOperationInfo opi;
/*     */           MBeanOperationInfo opi;
/* 306 */           if (pi != null) {
/* 307 */             opi = new MBeanOperationInfo(mname, null, pi, retType.getName(), impact);
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*     */ 
/* 315 */             opi = new MBeanOperationInfo(meth.toString(), meth);
/*     */           }
/*     */           
/*     */ 
/* 319 */           attrInfos.add(opi);
/*     */         }
/*     */       } }
/* 322 */     return (MBeanOperationInfo[])attrInfos.toArray(new MBeanOperationInfo[attrInfos.size()]);
/*     */   }
/*     */   
/*     */   public synchronized Object getAttribute(String attr) throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/*     */     try
/*     */     {
/* 329 */       AttrRec rec = attrRecForAttribute(attr);
/* 330 */       if (rec == null) {
/* 331 */         throw new AttributeNotFoundException(attr);
/*     */       }
/*     */       
/* 334 */       MBeanAttributeInfo ai = rec.attrInfo;
/* 335 */       if (!ai.isReadable()) {
/* 336 */         throw new IllegalArgumentException(attr + " not readable.");
/*     */       }
/*     */       
/* 339 */       String name = ai.getName();
/* 340 */       String pfx = ai.isIs() ? "is" : "get";
/* 341 */       String mname = pfx + Character.toUpperCase(name.charAt(0)) + name.substring(1);
/* 342 */       Object target = rec.target;
/* 343 */       Method m = target.getClass().getMethod(mname, null);
/* 344 */       return m.invoke(target, null);
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/* 350 */       if (logger.isLoggable(MLevel.WARNING))
/* 351 */         logger.log(MLevel.WARNING, "Failed to get requested attribute: " + attr, e);
/* 352 */       throw new MBeanException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized AttributeList getAttributes(String[] attrs)
/*     */   {
/* 358 */     AttributeList al = new AttributeList();
/* 359 */     int i = 0; for (int len = attrs.length; i < len; i++)
/*     */     {
/* 361 */       String attr = attrs[i];
/*     */       try
/*     */       {
/* 364 */         Object val = getAttribute(attr);
/* 365 */         al.add(new Attribute(attr, val));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 369 */         if (logger.isLoggable(MLevel.WARNING))
/* 370 */           logger.log(MLevel.WARNING, "Failed to get requested attribute (for list): " + attr, e);
/*     */       }
/*     */     }
/* 373 */     return al;
/*     */   }
/*     */   
/*     */   private AttrRec attrRecForAttribute(String attr)
/*     */   {
/* 378 */     assert (Thread.holdsLock(this));
/*     */     
/* 380 */     if (this.pdsAttrInfos.containsKey(attr))
/* 381 */       return new AttrRec(this.pds, (MBeanAttributeInfo)this.pdsAttrInfos.get(attr));
/* 382 */     if (this.cpdsAttrInfos.containsKey(attr))
/* 383 */       return new AttrRec(this.cpds, (MBeanAttributeInfo)this.cpdsAttrInfos.get(attr));
/* 384 */     if (this.unpooledDataSourceAttrInfos.containsKey(attr)) {
/* 385 */       return new AttrRec(this.unpooledDataSource, (MBeanAttributeInfo)this.unpooledDataSourceAttrInfos.get(attr));
/*     */     }
/* 387 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized MBeanInfo getMBeanInfo()
/*     */   {
/* 392 */     if (this.info == null)
/* 393 */       reinitialize();
/* 394 */     return this.info;
/*     */   }
/*     */   
/*     */   public synchronized Object invoke(String operation, Object[] paramVals, String[] signature) throws MBeanException, ReflectionException
/*     */   {
/*     */     try
/*     */     {
/* 401 */       int slen = signature.length;
/* 402 */       Class[] paramTypes = new Class[slen];
/* 403 */       for (int i = 0; i < slen; i++) {
/* 404 */         paramTypes[i] = ClassUtils.forName(signature[i]);
/*     */       }
/*     */       
/* 407 */       Method m = this.pds.getClass().getMethod(operation, paramTypes);
/* 408 */       return m.invoke(this.pds, paramVals);
/*     */ 
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 417 */         boolean two = false;
/* 418 */         if ((signature.length == 0) && ((operation.startsWith("get")) || ((two = operation.startsWith("is")))))
/*     */         {
/* 420 */           int i = two ? 2 : 3;
/* 421 */           String attr = Character.toLowerCase(operation.charAt(i)) + operation.substring(i + 1);
/* 422 */           return getAttribute(attr);
/*     */         }
/* 424 */         if ((signature.length == 1) && (operation.startsWith("set")))
/*     */         {
/* 426 */           setAttribute(new Attribute(Character.toLowerCase(operation.charAt(3)) + operation.substring(4), paramVals[0]));
/* 427 */           return null;
/*     */         }
/*     */         
/* 430 */         throw new MBeanException(e);
/*     */       }
/*     */       catch (Exception e2) {
/* 433 */         throw new MBeanException(e2);
/*     */       }
/*     */     } catch (Exception e) {
/* 436 */       throw new MBeanException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setAttribute(Attribute attrObj) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/*     */     try {
/* 443 */       String attr = attrObj.getName();
/*     */       
/* 445 */       if (attr == "factoryClassLocation")
/*     */       {
/* 447 */         if ((this.pds instanceof ComboPooledDataSource))
/*     */         {
/* 449 */           ((ComboPooledDataSource)this.pds).setFactoryClassLocation((String)attrObj.getValue());
/* 450 */           return;
/*     */         }
/* 452 */         if ((this.pds instanceof AbstractPoolBackedDataSource))
/*     */         {
/* 454 */           String val = (String)attrObj.getValue();
/* 455 */           AbstractPoolBackedDataSource apbds = (AbstractPoolBackedDataSource)this.pds;
/* 456 */           apbds.setFactoryClassLocation(val);
/* 457 */           ConnectionPoolDataSource checkDs1 = apbds.getConnectionPoolDataSource();
/* 458 */           if ((checkDs1 instanceof WrapperConnectionPoolDataSource))
/*     */           {
/* 460 */             WrapperConnectionPoolDataSource wcheckDs1 = (WrapperConnectionPoolDataSource)checkDs1;
/* 461 */             wcheckDs1.setFactoryClassLocation(val);
/* 462 */             DataSource checkDs2 = wcheckDs1.getNestedDataSource();
/* 463 */             if ((checkDs2 instanceof DriverManagerDataSource))
/* 464 */               ((DriverManagerDataSource)checkDs2).setFactoryClassLocation(val);
/*     */           }
/* 466 */           return;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 474 */       AttrRec rec = attrRecForAttribute(attr);
/* 475 */       if (rec == null) {
/* 476 */         throw new AttributeNotFoundException(attr);
/*     */       }
/*     */       
/* 479 */       MBeanAttributeInfo ai = rec.attrInfo;
/* 480 */       if (!ai.isWritable()) {
/* 481 */         throw new IllegalArgumentException(attr + " not writable.");
/*     */       }
/*     */       
/* 484 */       Class attrType = ClassUtils.forName(rec.attrInfo.getType());
/* 485 */       String name = ai.getName();
/* 486 */       String pfx = "set";
/* 487 */       String mname = pfx + Character.toUpperCase(name.charAt(0)) + name.substring(1);
/* 488 */       Object target = rec.target;
/* 489 */       Method m = target.getClass().getMethod(mname, new Class[] { attrType });
/* 490 */       m.invoke(target, new Object[] { attrObj.getValue() });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 496 */       if (target != this.pds)
/*     */       {
/* 498 */         if ((this.pds instanceof AbstractPoolBackedDataSource)) {
/* 499 */           ((AbstractPoolBackedDataSource)this.pds).resetPoolManager(false);
/* 500 */         } else if (logger.isLoggable(MLevel.WARNING)) {
/* 501 */           logger.warning("MBean set a nested ConnectionPoolDataSource or DataSource parameter on an unknown PooledDataSource type. Could not reset the pool manager, so the changes may not take effect. c3p0 may need to be updated for PooledDataSource type " + this.pds.getClass() + ".");
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/* 511 */       if (logger.isLoggable(MLevel.WARNING))
/* 512 */         logger.log(MLevel.WARNING, "Failed to set requested attribute: " + attrObj, e);
/* 513 */       throw new MBeanException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized AttributeList setAttributes(AttributeList al)
/*     */   {
/* 519 */     AttributeList out = new AttributeList();
/* 520 */     int i = 0; for (int len = al.size(); i < len; i++)
/*     */     {
/* 522 */       Attribute attrObj = (Attribute)al.get(i);
/*     */       
/*     */       try
/*     */       {
/* 526 */         setAttribute(attrObj);
/* 527 */         out.add(attrObj);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 531 */         if (logger.isLoggable(MLevel.WARNING))
/* 532 */           logger.log(MLevel.WARNING, "Failed to set requested attribute (from list): " + attrObj, e);
/*     */       }
/*     */     }
/* 535 */     return out;
/*     */   }
/*     */   
/*     */   private static Map extractAttributeInfos(Object bean)
/*     */   {
/* 540 */     if (bean != null)
/*     */     {
/*     */       try
/*     */       {
/* 544 */         Map out = new HashMap();
/* 545 */         BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
/* 546 */         PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
/*     */         
/* 548 */         int i = 0; for (int len = pds.length; i < len; i++)
/*     */         {
/* 550 */           PropertyDescriptor pd = pds[i];
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 557 */           String name = pd.getName();
/*     */           
/* 559 */           if (!HIDE_PROPS.contains(name))
/*     */           {
/*     */ 
/* 562 */             String desc = getDescription(name);
/* 563 */             Method getter = pd.getReadMethod();
/* 564 */             Method setter = pd.getWriteMethod();
/*     */             
/* 566 */             if (FORCE_READ_ONLY_PROPS.contains(name)) {
/* 567 */               setter = null;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             try
/*     */             {
/* 580 */               out.put(name, new MBeanAttributeInfo(name, desc, getter, setter));
/*     */             }
/*     */             catch (javax.management.IntrospectionException e)
/*     */             {
/* 584 */               if (logger.isLoggable(MLevel.WARNING))
/* 585 */                 logger.log(MLevel.WARNING, "IntrospectionException while setting up MBean attribute '" + name + "'", e);
/*     */             }
/*     */           }
/*     */         }
/* 589 */         return Collections.synchronizedMap(out);
/*     */       }
/*     */       catch (java.beans.IntrospectionException e)
/*     */       {
/* 593 */         if (logger.isLoggable(MLevel.WARNING))
/* 594 */           logger.log(MLevel.WARNING, "IntrospectionException while setting up MBean attributes for " + bean, e);
/* 595 */         return Collections.EMPTY_MAP;
/*     */       }
/*     */     }
/*     */     
/* 599 */     return Collections.EMPTY_MAP;
/*     */   }
/*     */   
/*     */ 
/*     */   private static String getDescription(String attrName)
/*     */   {
/* 605 */     return null;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private synchronized Exception reinitialize()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 12	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pds	Lcom/mchange/v2/c3p0/PooledDataSource;
/*     */     //   4: instanceof 15
/*     */     //   7: ifne +99 -> 106
/*     */     //   10: aload_0
/*     */     //   11: getfield 12	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pds	Lcom/mchange/v2/c3p0/PooledDataSource;
/*     */     //   14: instanceof 16
/*     */     //   17: ifeq +89 -> 106
/*     */     //   20: aload_0
/*     */     //   21: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   24: instanceof 27
/*     */     //   27: ifeq +17 -> 44
/*     */     //   30: aload_0
/*     */     //   31: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   34: checkcast 27	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource
/*     */     //   37: aload_0
/*     */     //   38: getfield 11	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pcl	Ljava/beans/PropertyChangeListener;
/*     */     //   41: invokevirtual 28	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource:removePropertyChangeListener	(Ljava/beans/PropertyChangeListener;)V
/*     */     //   44: aload_0
/*     */     //   45: aconst_null
/*     */     //   46: putfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   49: aload_0
/*     */     //   50: aconst_null
/*     */     //   51: putfield 29	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSource	Ljavax/sql/DataSource;
/*     */     //   54: aload_0
/*     */     //   55: aload_0
/*     */     //   56: getfield 12	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pds	Lcom/mchange/v2/c3p0/PooledDataSource;
/*     */     //   59: checkcast 16	com/mchange/v2/c3p0/impl/AbstractPoolBackedDataSource
/*     */     //   62: invokevirtual 30	com/mchange/v2/c3p0/impl/AbstractPoolBackedDataSource:getConnectionPoolDataSource	()Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   65: putfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   68: aload_0
/*     */     //   69: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   72: instanceof 27
/*     */     //   75: ifeq +31 -> 106
/*     */     //   78: aload_0
/*     */     //   79: aload_0
/*     */     //   80: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   83: checkcast 27	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource
/*     */     //   86: invokevirtual 31	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource:getNestedDataSource	()Ljavax/sql/DataSource;
/*     */     //   89: putfield 29	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSource	Ljavax/sql/DataSource;
/*     */     //   92: aload_0
/*     */     //   93: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   96: checkcast 27	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource
/*     */     //   99: aload_0
/*     */     //   100: getfield 11	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pcl	Ljava/beans/PropertyChangeListener;
/*     */     //   103: invokevirtual 32	com/mchange/v2/c3p0/WrapperConnectionPoolDataSource:addPropertyChangeListener	(Ljava/beans/PropertyChangeListener;)V
/*     */     //   106: aload_0
/*     */     //   107: aload_0
/*     */     //   108: getfield 12	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pds	Lcom/mchange/v2/c3p0/PooledDataSource;
/*     */     //   111: invokestatic 33	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:extractAttributeInfos	(Ljava/lang/Object;)Ljava/util/Map;
/*     */     //   114: putfield 34	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pdsAttrInfos	Ljava/util/Map;
/*     */     //   117: aload_0
/*     */     //   118: aload_0
/*     */     //   119: getfield 26	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpds	Ljavax/sql/ConnectionPoolDataSource;
/*     */     //   122: invokestatic 33	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:extractAttributeInfos	(Ljava/lang/Object;)Ljava/util/Map;
/*     */     //   125: putfield 35	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpdsAttrInfos	Ljava/util/Map;
/*     */     //   128: aload_0
/*     */     //   129: aload_0
/*     */     //   130: getfield 29	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSource	Ljavax/sql/DataSource;
/*     */     //   133: invokestatic 33	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:extractAttributeInfos	(Ljava/lang/Object;)Ljava/util/Map;
/*     */     //   136: putfield 36	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSourceAttrInfos	Ljava/util/Map;
/*     */     //   139: new 37	java/util/HashSet
/*     */     //   142: dup
/*     */     //   143: invokespecial 38	java/util/HashSet:<init>	()V
/*     */     //   146: astore_1
/*     */     //   147: aload_1
/*     */     //   148: aload_0
/*     */     //   149: getfield 34	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pdsAttrInfos	Ljava/util/Map;
/*     */     //   152: invokeinterface 39 1 0
/*     */     //   157: invokeinterface 40 2 0
/*     */     //   162: pop
/*     */     //   163: aload_1
/*     */     //   164: aload_0
/*     */     //   165: getfield 35	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpdsAttrInfos	Ljava/util/Map;
/*     */     //   168: invokeinterface 39 1 0
/*     */     //   173: invokeinterface 40 2 0
/*     */     //   178: pop
/*     */     //   179: aload_1
/*     */     //   180: aload_0
/*     */     //   181: getfield 36	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSourceAttrInfos	Ljava/util/Map;
/*     */     //   184: invokeinterface 39 1 0
/*     */     //   189: invokeinterface 40 2 0
/*     */     //   194: pop
/*     */     //   195: new 37	java/util/HashSet
/*     */     //   198: dup
/*     */     //   199: invokespecial 38	java/util/HashSet:<init>	()V
/*     */     //   202: astore_2
/*     */     //   203: aload_1
/*     */     //   204: invokeinterface 41 1 0
/*     */     //   209: astore_3
/*     */     //   210: aload_3
/*     */     //   211: invokeinterface 42 1 0
/*     */     //   216: ifeq +75 -> 291
/*     */     //   219: aload_3
/*     */     //   220: invokeinterface 43 1 0
/*     */     //   225: checkcast 44	java/lang/String
/*     */     //   228: astore 4
/*     */     //   230: aload_0
/*     */     //   231: getfield 34	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:pdsAttrInfos	Ljava/util/Map;
/*     */     //   234: aload 4
/*     */     //   236: invokeinterface 45 2 0
/*     */     //   241: astore 5
/*     */     //   243: aload 5
/*     */     //   245: ifnonnull +16 -> 261
/*     */     //   248: aload_0
/*     */     //   249: getfield 35	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:cpdsAttrInfos	Ljava/util/Map;
/*     */     //   252: aload 4
/*     */     //   254: invokeinterface 45 2 0
/*     */     //   259: astore 5
/*     */     //   261: aload 5
/*     */     //   263: ifnonnull +16 -> 279
/*     */     //   266: aload_0
/*     */     //   267: getfield 36	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:unpooledDataSourceAttrInfos	Ljava/util/Map;
/*     */     //   270: aload 4
/*     */     //   272: invokeinterface 45 2 0
/*     */     //   277: astore 5
/*     */     //   279: aload_2
/*     */     //   280: aload 5
/*     */     //   282: invokeinterface 46 2 0
/*     */     //   287: pop
/*     */     //   288: goto -78 -> 210
/*     */     //   291: aload_0
/*     */     //   292: invokevirtual 47	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   295: invokevirtual 48	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   298: astore_3
/*     */     //   299: aload_2
/*     */     //   300: aload_2
/*     */     //   301: invokeinterface 49 1 0
/*     */     //   306: anewarray 50	javax/management/MBeanAttributeInfo
/*     */     //   309: invokeinterface 51 2 0
/*     */     //   314: checkcast 52	[Ljavax/management/MBeanAttributeInfo;
/*     */     //   317: checkcast 52	[Ljavax/management/MBeanAttributeInfo;
/*     */     //   320: astore 4
/*     */     //   322: iconst_3
/*     */     //   323: anewarray 53	java/lang/Class
/*     */     //   326: dup
/*     */     //   327: iconst_0
/*     */     //   328: getstatic 54	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$com$mchange$v2$c3p0$PooledDataSource	Ljava/lang/Class;
/*     */     //   331: ifnonnull +15 -> 346
/*     */     //   334: ldc 55
/*     */     //   336: invokestatic 56	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   339: dup
/*     */     //   340: putstatic 54	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$com$mchange$v2$c3p0$PooledDataSource	Ljava/lang/Class;
/*     */     //   343: goto +6 -> 349
/*     */     //   346: getstatic 54	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$com$mchange$v2$c3p0$PooledDataSource	Ljava/lang/Class;
/*     */     //   349: aastore
/*     */     //   350: dup
/*     */     //   351: iconst_1
/*     */     //   352: getstatic 57	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$java$lang$String	Ljava/lang/Class;
/*     */     //   355: ifnonnull +15 -> 370
/*     */     //   358: ldc 58
/*     */     //   360: invokestatic 56	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   363: dup
/*     */     //   364: putstatic 57	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$java$lang$String	Ljava/lang/Class;
/*     */     //   367: goto +6 -> 373
/*     */     //   370: getstatic 57	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$java$lang$String	Ljava/lang/Class;
/*     */     //   373: aastore
/*     */     //   374: dup
/*     */     //   375: iconst_2
/*     */     //   376: getstatic 59	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$javax$management$MBeanServer	Ljava/lang/Class;
/*     */     //   379: ifnonnull +15 -> 394
/*     */     //   382: ldc 60
/*     */     //   384: invokestatic 56	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$	(Ljava/lang/String;)Ljava/lang/Class;
/*     */     //   387: dup
/*     */     //   388: putstatic 59	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$javax$management$MBeanServer	Ljava/lang/Class;
/*     */     //   391: goto +6 -> 397
/*     */     //   394: getstatic 59	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:class$javax$management$MBeanServer	Ljava/lang/Class;
/*     */     //   397: aastore
/*     */     //   398: astore 5
/*     */     //   400: iconst_1
/*     */     //   401: anewarray 61	javax/management/MBeanConstructorInfo
/*     */     //   404: dup
/*     */     //   405: iconst_0
/*     */     //   406: new 61	javax/management/MBeanConstructorInfo
/*     */     //   409: dup
/*     */     //   410: ldc 62
/*     */     //   412: aload_0
/*     */     //   413: invokevirtual 47	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   416: aload 5
/*     */     //   418: invokevirtual 63	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
/*     */     //   421: invokespecial 64	javax/management/MBeanConstructorInfo:<init>	(Ljava/lang/String;Ljava/lang/reflect/Constructor;)V
/*     */     //   424: aastore
/*     */     //   425: astore 6
/*     */     //   427: aload_0
/*     */     //   428: new 65	javax/management/MBeanInfo
/*     */     //   431: dup
/*     */     //   432: aload_0
/*     */     //   433: invokevirtual 47	java/lang/Object:getClass	()Ljava/lang/Class;
/*     */     //   436: invokevirtual 48	java/lang/Class:getName	()Ljava/lang/String;
/*     */     //   439: ldc 66
/*     */     //   441: aload 4
/*     */     //   443: aload 6
/*     */     //   445: getstatic 67	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:OP_INFS	[Ljavax/management/MBeanOperationInfo;
/*     */     //   448: aconst_null
/*     */     //   449: invokespecial 68	javax/management/MBeanInfo:<init>	(Ljava/lang/String;Ljava/lang/String;[Ljavax/management/MBeanAttributeInfo;[Ljavax/management/MBeanConstructorInfo;[Ljavax/management/MBeanOperationInfo;[Ljavax/management/MBeanNotificationInfo;)V
/*     */     //   452: putfield 8	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:info	Ljavax/management/MBeanInfo;
/*     */     //   455: aload_0
/*     */     //   456: getfield 13	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbeanName	Ljava/lang/String;
/*     */     //   459: invokestatic 69	javax/management/ObjectName:getInstance	(Ljava/lang/String;)Ljavax/management/ObjectName;
/*     */     //   462: astore 7
/*     */     //   464: aload_0
/*     */     //   465: getfield 14	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbs	Ljavax/management/MBeanServer;
/*     */     //   468: aload 7
/*     */     //   470: invokeinterface 70 2 0
/*     */     //   475: ifeq +66 -> 541
/*     */     //   478: aload_0
/*     */     //   479: getfield 14	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbs	Ljavax/management/MBeanServer;
/*     */     //   482: aload 7
/*     */     //   484: invokeinterface 71 2 0
/*     */     //   489: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   492: getstatic 72	com/mchange/v2/log/MLevel:FINER	Lcom/mchange/v2/log/MLevel;
/*     */     //   495: invokeinterface 73 2 0
/*     */     //   500: ifeq +41 -> 541
/*     */     //   503: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   506: getstatic 72	com/mchange/v2/log/MLevel:FINER	Lcom/mchange/v2/log/MLevel;
/*     */     //   509: new 19	java/lang/StringBuffer
/*     */     //   512: dup
/*     */     //   513: invokespecial 20	java/lang/StringBuffer:<init>	()V
/*     */     //   516: ldc 74
/*     */     //   518: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   521: aload_0
/*     */     //   522: getfield 13	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbeanName	Ljava/lang/String;
/*     */     //   525: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   528: ldc 75
/*     */     //   530: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   533: invokevirtual 24	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   536: invokeinterface 76 3 0
/*     */     //   541: aload_0
/*     */     //   542: getfield 14	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbs	Ljavax/management/MBeanServer;
/*     */     //   545: aload_0
/*     */     //   546: aload 7
/*     */     //   548: invokeinterface 77 3 0
/*     */     //   553: pop
/*     */     //   554: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   557: getstatic 72	com/mchange/v2/log/MLevel:FINER	Lcom/mchange/v2/log/MLevel;
/*     */     //   560: invokeinterface 73 2 0
/*     */     //   565: ifeq +41 -> 606
/*     */     //   568: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   571: getstatic 72	com/mchange/v2/log/MLevel:FINER	Lcom/mchange/v2/log/MLevel;
/*     */     //   574: new 19	java/lang/StringBuffer
/*     */     //   577: dup
/*     */     //   578: invokespecial 20	java/lang/StringBuffer:<init>	()V
/*     */     //   581: ldc 74
/*     */     //   583: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   586: aload_0
/*     */     //   587: getfield 13	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbeanName	Ljava/lang/String;
/*     */     //   590: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   593: ldc 78
/*     */     //   595: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   598: invokevirtual 24	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   601: invokeinterface 76 3 0
/*     */     //   606: aconst_null
/*     */     //   607: areturn
/*     */     //   608: astore 7
/*     */     //   610: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   613: getstatic 80	com/mchange/v2/log/MLevel:WARNING	Lcom/mchange/v2/log/MLevel;
/*     */     //   616: invokeinterface 73 2 0
/*     */     //   621: ifeq +43 -> 664
/*     */     //   624: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   627: getstatic 80	com/mchange/v2/log/MLevel:WARNING	Lcom/mchange/v2/log/MLevel;
/*     */     //   630: new 19	java/lang/StringBuffer
/*     */     //   633: dup
/*     */     //   634: invokespecial 20	java/lang/StringBuffer:<init>	()V
/*     */     //   637: ldc 81
/*     */     //   639: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   642: aload_0
/*     */     //   643: getfield 13	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:mbeanName	Ljava/lang/String;
/*     */     //   646: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   649: ldc 82
/*     */     //   651: invokevirtual 23	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*     */     //   654: invokevirtual 24	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*     */     //   657: aload 7
/*     */     //   659: invokeinterface 83 4 0
/*     */     //   664: aload 7
/*     */     //   666: areturn
/*     */     //   667: astore_1
/*     */     //   668: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   671: getstatic 85	com/mchange/v2/log/MLevel:SEVERE	Lcom/mchange/v2/log/MLevel;
/*     */     //   674: invokeinterface 73 2 0
/*     */     //   679: ifeq +17 -> 696
/*     */     //   682: getstatic 18	com/mchange/v2/c3p0/management/DynamicPooledDataSourceManagerMBean:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   685: getstatic 85	com/mchange/v2/log/MLevel:SEVERE	Lcom/mchange/v2/log/MLevel;
/*     */     //   688: ldc 86
/*     */     //   690: aload_1
/*     */     //   691: invokeinterface 83 4 0
/*     */     //   696: aload_1
/*     */     //   697: areturn
/*     */     // Line number table:
/*     */     //   Java source line #170	-> byte code offset #0
/*     */     //   Java source line #172	-> byte code offset #20
/*     */     //   Java source line #173	-> byte code offset #30
/*     */     //   Java source line #177	-> byte code offset #44
/*     */     //   Java source line #178	-> byte code offset #49
/*     */     //   Java source line #180	-> byte code offset #54
/*     */     //   Java source line #182	-> byte code offset #68
/*     */     //   Java source line #184	-> byte code offset #78
/*     */     //   Java source line #185	-> byte code offset #92
/*     */     //   Java source line #189	-> byte code offset #106
/*     */     //   Java source line #190	-> byte code offset #117
/*     */     //   Java source line #191	-> byte code offset #128
/*     */     //   Java source line #193	-> byte code offset #139
/*     */     //   Java source line #194	-> byte code offset #147
/*     */     //   Java source line #195	-> byte code offset #163
/*     */     //   Java source line #196	-> byte code offset #179
/*     */     //   Java source line #198	-> byte code offset #195
/*     */     //   Java source line #199	-> byte code offset #203
/*     */     //   Java source line #201	-> byte code offset #219
/*     */     //   Java source line #203	-> byte code offset #230
/*     */     //   Java source line #204	-> byte code offset #243
/*     */     //   Java source line #205	-> byte code offset #248
/*     */     //   Java source line #206	-> byte code offset #261
/*     */     //   Java source line #207	-> byte code offset #266
/*     */     //   Java source line #208	-> byte code offset #279
/*     */     //   Java source line #209	-> byte code offset #288
/*     */     //   Java source line #211	-> byte code offset #291
/*     */     //   Java source line #212	-> byte code offset #299
/*     */     //   Java source line #213	-> byte code offset #322
/*     */     //   Java source line #214	-> byte code offset #400
/*     */     //   Java source line #216	-> byte code offset #427
/*     */     //   Java source line #227	-> byte code offset #455
/*     */     //   Java source line #228	-> byte code offset #464
/*     */     //   Java source line #230	-> byte code offset #478
/*     */     //   Java source line #231	-> byte code offset #489
/*     */     //   Java source line #232	-> byte code offset #503
/*     */     //   Java source line #234	-> byte code offset #541
/*     */     //   Java source line #235	-> byte code offset #554
/*     */     //   Java source line #236	-> byte code offset #568
/*     */     //   Java source line #238	-> byte code offset #606
/*     */     //   Java source line #240	-> byte code offset #608
/*     */     //   Java source line #242	-> byte code offset #610
/*     */     //   Java source line #243	-> byte code offset #624
/*     */     //   Java source line #247	-> byte code offset #664
/*     */     //   Java source line #250	-> byte code offset #667
/*     */     //   Java source line #252	-> byte code offset #668
/*     */     //   Java source line #253	-> byte code offset #682
/*     */     //   Java source line #256	-> byte code offset #696
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	698	0	this	DynamicPooledDataSourceManagerMBean
/*     */     //   146	58	1	allAttrNames	Set
/*     */     //   667	30	1	e	NoSuchMethodException
/*     */     //   202	99	2	allAttrs	Set
/*     */     //   209	11	3	ii	java.util.Iterator
/*     */     //   298	2	3	className	String
/*     */     //   228	43	4	name	String
/*     */     //   320	122	4	attrInfos	MBeanAttributeInfo[]
/*     */     //   241	40	5	attrInfo	Object
/*     */     //   398	19	5	ctorArgClasses	Class[]
/*     */     //   425	19	6	constrInfos	javax.management.MBeanConstructorInfo[]
/*     */     //   462	85	7	oname	javax.management.ObjectName
/*     */     //   608	57	7	e	Exception
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   455	607	608	java/lang/Exception
/*     */     //   0	607	667	java/lang/NoSuchMethodException
/*     */     //   608	666	667	java/lang/NoSuchMethodException
/*     */   }
/*     */   
/*     */   private static class AttrRec
/*     */   {
/*     */     Object target;
/*     */     MBeanAttributeInfo attrInfo;
/*     */     
/*     */     AttrRec(Object target, MBeanAttributeInfo attrInfo)
/*     */     {
/* 614 */       this.target = target;
/* 615 */       this.attrInfo = attrInfo;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\DynamicPooledDataSourceManagerMBean.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */