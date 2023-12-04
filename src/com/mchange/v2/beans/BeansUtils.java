/*     */ package com.mchange.v2.beans;
/*     */ 
/*     */ import com.mchange.v2.lang.Coerce;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IndexedPropertyDescriptor;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.beans.PropertyEditorManager;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BeansUtils
/*     */ {
/*  35 */   static final MLogger logger = MLog.getLogger(BeansUtils.class);
/*     */   
/*  37 */   static final Object[] EMPTY_ARGS = new Object[0];
/*     */   
/*     */   public static PropertyEditor findPropertyEditor(PropertyDescriptor pd)
/*     */   {
/*  41 */     PropertyEditor out = null;
/*  42 */     Class editorClass = null;
/*     */     try
/*     */     {
/*  45 */       editorClass = pd.getPropertyEditorClass();
/*  46 */       if (editorClass != null) {
/*  47 */         out = (PropertyEditor)editorClass.newInstance();
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  54 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  55 */         logger.log(MLevel.WARNING, "Bad property editor class " + editorClass.getName() + " registered for property " + pd.getName(), e);
/*     */       }
/*     */     }
/*  58 */     if (out == null)
/*  59 */       out = PropertyEditorManager.findEditor(pd.getPropertyType());
/*  60 */     return out;
/*     */   }
/*     */   
/*     */   public static boolean equalsByAccessibleProperties(Object bean0, Object bean1) throws IntrospectionException
/*     */   {
/*  65 */     return equalsByAccessibleProperties(bean0, bean1, Collections.EMPTY_SET);
/*     */   }
/*     */   
/*     */   public static boolean equalsByAccessibleProperties(Object bean0, Object bean1, Collection ignoreProps) throws IntrospectionException
/*     */   {
/*  70 */     Map m0 = new HashMap();
/*  71 */     Map m1 = new HashMap();
/*  72 */     extractAccessiblePropertiesToMap(m0, bean0, ignoreProps);
/*  73 */     extractAccessiblePropertiesToMap(m1, bean1, ignoreProps);
/*     */     
/*     */ 
/*  76 */     return m0.equals(m1);
/*     */   }
/*     */   
/*     */   public static void overwriteAccessibleProperties(Object sourceBean, Object destBean) throws IntrospectionException
/*     */   {
/*  81 */     overwriteAccessibleProperties(sourceBean, destBean, Collections.EMPTY_SET);
/*     */   }
/*     */   
/*     */   public static void overwriteAccessibleProperties(Object sourceBean, Object destBean, Collection ignoreProps) throws IntrospectionException
/*     */   {
/*     */     try
/*     */     {
/*  88 */       BeanInfo beanInfo = Introspector.getBeanInfo(sourceBean.getClass(), Object.class);
/*  89 */       PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
/*  90 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/*  92 */         PropertyDescriptor pd = pds[i];
/*  93 */         if (!ignoreProps.contains(pd.getName()))
/*     */         {
/*     */ 
/*  96 */           Method getter = pd.getReadMethod();
/*  97 */           Method setter = pd.getWriteMethod();
/*     */           
/*  99 */           if ((getter == null) || (setter == null))
/*     */           {
/* 101 */             if ((pd instanceof IndexedPropertyDescriptor))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 109 */               if (logger.isLoggable(MLevel.WARNING)) {
/* 110 */                 logger.warning("BeansUtils.overwriteAccessibleProperties() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 118 */             if (logger.isLoggable(MLevel.INFO)) {
/* 119 */               logger.info("Property inaccessible for overwriting: " + pd.getName());
/*     */             }
/*     */           }
/*     */           else {
/* 123 */             Object value = getter.invoke(sourceBean, EMPTY_ARGS);
/* 124 */             setter.invoke(destBean, new Object[] { value });
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (IntrospectionException e) {
/* 129 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 133 */       if (logger.isLoggable(MLevel.FINE)) {
/* 134 */         logger.log(MLevel.FINE, "Converting exception to throwable IntrospectionException");
/*     */       }
/* 136 */       throw new IntrospectionException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public static void overwriteAccessiblePropertiesFromMap(Map sourceMap, Object destBean, boolean skip_nulls) throws IntrospectionException
/*     */   {
/* 142 */     overwriteAccessiblePropertiesFromMap(sourceMap, destBean, skip_nulls, Collections.EMPTY_SET);
/*     */   }
/*     */   
/*     */   public static void overwriteAccessiblePropertiesFromMap(Map sourceMap, Object destBean, boolean skip_nulls, Collection ignoreProps) throws IntrospectionException
/*     */   {
/* 147 */     overwriteAccessiblePropertiesFromMap(sourceMap, destBean, skip_nulls, ignoreProps, false, MLevel.WARNING, MLevel.WARNING, true);
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
/*     */   public static void overwriteAccessiblePropertiesFromMap(Map sourceMap, Object destBean, boolean skip_nulls, Collection ignoreProps, boolean coerce_strings, MLevel cantWriteLevel, MLevel cantCoerceLevel, boolean die_on_one_prop_failure)
/*     */     throws IntrospectionException
/*     */   {
/* 167 */     if (cantWriteLevel == null)
/* 168 */       cantWriteLevel = MLevel.WARNING;
/* 169 */     if (cantCoerceLevel == null) {
/* 170 */       cantCoerceLevel = MLevel.WARNING;
/*     */     }
/* 172 */     Set sourceMapProps = sourceMap.keySet();
/*     */     
/* 174 */     String propName = null;
/* 175 */     BeanInfo beanInfo = Introspector.getBeanInfo(destBean.getClass(), Object.class);
/* 176 */     PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
/*     */     
/* 178 */     int i = 0; for (int len = pds.length; i < len; i++)
/*     */     {
/* 180 */       PropertyDescriptor pd = pds[i];
/* 181 */       propName = pd.getName();
/*     */       
/* 183 */       if (sourceMapProps.contains(propName))
/*     */       {
/*     */ 
/* 186 */         if ((ignoreProps == null) || (!ignoreProps.contains(propName)))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 194 */           Object propVal = sourceMap.get(propName);
/* 195 */           if ((propVal != null) || 
/*     */           
/* 197 */             (!skip_nulls))
/*     */           {
/*     */ 
/*     */ 
/* 201 */             Method setter = pd.getWriteMethod();
/* 202 */             boolean rethrow = false;
/*     */             
/* 204 */             Class propType = pd.getPropertyType();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 209 */             if (setter == null)
/*     */             {
/* 211 */               if ((pd instanceof IndexedPropertyDescriptor))
/*     */               {
/* 213 */                 if (logger.isLoggable(MLevel.FINER)) {
/* 214 */                   logger.finer("BeansUtils.overwriteAccessiblePropertiesFromMap() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */               if (logger.isLoggable(cantWriteLevel))
/*     */               {
/* 224 */                 String msg = "Property inaccessible for overwriting: " + propName;
/* 225 */                 logger.log(cantWriteLevel, msg);
/* 226 */                 if (die_on_one_prop_failure)
/*     */                 {
/* 228 */                   rethrow = true;
/* 229 */                   throw new IntrospectionException(msg);
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/*     */ 
/*     */             }
/* 236 */             else if ((coerce_strings) && (propVal != null) && (propVal.getClass() == String.class) && ((propType = pd.getPropertyType()) != String.class) && (Coerce.canCoerce(propType)))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               try
/*     */               {
/*     */ 
/*     */ 
/* 245 */                 Object coercedPropVal = Coerce.toObject((String)propVal, propType);
/*     */                 
/* 247 */                 setter.invoke(destBean, new Object[] { coercedPropVal });
/*     */ 
/*     */               }
/*     */               catch (IllegalArgumentException e)
/*     */               {
/*     */ 
/* 253 */                 String msg = "Failed to coerce property: " + propName + " [propVal: " + propVal + "; propType: " + propType + "]";
/*     */                 
/*     */ 
/* 256 */                 if (logger.isLoggable(cantCoerceLevel))
/* 257 */                   logger.log(cantCoerceLevel, msg, e);
/* 258 */                 if (die_on_one_prop_failure)
/*     */                 {
/* 260 */                   rethrow = true;
/* 261 */                   throw new IntrospectionException(msg);
/*     */                 }
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/* 266 */                 String msg = "Failed to set property: " + propName + " [propVal: " + propVal + "; propType: " + propType + "]";
/*     */                 
/*     */ 
/* 269 */                 if (logger.isLoggable(cantWriteLevel))
/* 270 */                   logger.log(cantWriteLevel, msg, e);
/* 271 */                 if (die_on_one_prop_failure)
/*     */                 {
/* 273 */                   rethrow = true;
/* 274 */                   throw new IntrospectionException(msg);
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/*     */               try
/*     */               {
/* 283 */                 setter.invoke(destBean, new Object[] { propVal });
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/* 287 */                 String msg = "Failed to set property: " + propName + " [propVal: " + propVal + "; propType: " + propType + "]";
/*     */                 
/*     */ 
/* 290 */                 if (logger.isLoggable(cantWriteLevel))
/* 291 */                   logger.log(cantWriteLevel, msg, e);
/* 292 */                 if (die_on_one_prop_failure)
/*     */                 {
/* 294 */                   rethrow = true;
/* 295 */                   throw new IntrospectionException(msg);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
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
/*     */   public static void appendPropNamesAndValues(StringBuffer appendIntoMe, Object bean, Collection ignoreProps)
/*     */     throws IntrospectionException
/*     */   {
/* 323 */     Map tmp = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/* 324 */     extractAccessiblePropertiesToMap(tmp, bean, ignoreProps);
/* 325 */     boolean first = true;
/* 326 */     for (Iterator ii = tmp.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 328 */       String key = (String)ii.next();
/* 329 */       Object val = tmp.get(key);
/* 330 */       if (first) {
/* 331 */         first = false;
/*     */       } else
/* 333 */         appendIntoMe.append(", ");
/* 334 */       appendIntoMe.append(key);
/* 335 */       appendIntoMe.append(" -> ");
/* 336 */       appendIntoMe.append(val);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void extractAccessiblePropertiesToMap(Map fillMe, Object bean) throws IntrospectionException
/*     */   {
/* 342 */     extractAccessiblePropertiesToMap(fillMe, bean, Collections.EMPTY_SET);
/*     */   }
/*     */   
/*     */   public static void extractAccessiblePropertiesToMap(Map fillMe, Object bean, Collection ignoreProps) throws IntrospectionException {
/* 346 */     String propName = null;
/*     */     try
/*     */     {
/* 349 */       BeanInfo bi = Introspector.getBeanInfo(bean.getClass(), Object.class);
/* 350 */       PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/* 351 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/* 353 */         PropertyDescriptor pd = pds[i];
/* 354 */         propName = pd.getName();
/* 355 */         if (!ignoreProps.contains(propName))
/*     */         {
/*     */ 
/* 358 */           Method readMethod = pd.getReadMethod();
/* 359 */           Object propVal = readMethod.invoke(bean, EMPTY_ARGS);
/* 360 */           fillMe.put(propName, propVal);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     catch (IntrospectionException e)
/*     */     {
/* 367 */       if (logger.isLoggable(MLevel.WARNING))
/* 368 */         logger.warning("Problem occurred while overwriting property: " + propName);
/* 369 */       if (logger.isLoggable(MLevel.FINE)) {
/* 370 */         logger.logp(MLevel.FINE, class$com$mchange$v2$beans$BeansUtils.getName(), "extractAccessiblePropertiesToMap( Map fillMe, Object bean, Collection ignoreProps )", (propName != null ? "Problem occurred while overwriting property: " + propName : "") + " throwing...", e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 375 */       throw e;
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 380 */       if (logger.isLoggable(MLevel.FINE)) {
/* 381 */         logger.logp(MLevel.FINE, BeansUtils.class.getName(), "extractAccessiblePropertiesToMap( Map fillMe, Object bean, Collection ignoreProps )", "Caught unexpected Exception; Converting to IntrospectionException.", e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 386 */       throw new IntrospectionException(e.toString() + (propName == null ? "" : new StringBuffer().append(" [").append(propName).append(']').toString()));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void overwriteProperty(String propName, Object value, Method putativeSetter, Object target)
/*     */     throws Exception
/*     */   {
/* 393 */     if (putativeSetter.getDeclaringClass().isAssignableFrom(target.getClass())) {
/* 394 */       putativeSetter.invoke(target, new Object[] { value });
/*     */     }
/*     */     else {
/* 397 */       BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass(), Object.class);
/* 398 */       PropertyDescriptor pd = null;
/*     */       
/* 400 */       PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
/* 401 */       int i = 0; for (int len = pds.length; i < len; i++) {
/* 402 */         if (propName.equals(pds[i].getName()))
/*     */         {
/* 404 */           pd = pds[i];
/* 405 */           break;
/*     */         }
/*     */       }
/* 408 */       Method targetSetter = pd.getWriteMethod();
/* 409 */       targetSetter.invoke(target, new Object[] { value });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void overwriteSpecificAccessibleProperties(Object sourceBean, Object destBean, Collection props)
/*     */     throws IntrospectionException
/*     */   {
/*     */     try
/*     */     {
/* 419 */       Set _props = new HashSet(props);
/*     */       
/* 421 */       BeanInfo beanInfo = Introspector.getBeanInfo(sourceBean.getClass(), Object.class);
/* 422 */       PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
/* 423 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/* 425 */         PropertyDescriptor pd = pds[i];
/* 426 */         String name = pd.getName();
/* 427 */         if (_props.remove(name))
/*     */         {
/*     */ 
/* 430 */           Method getter = pd.getReadMethod();
/* 431 */           Method setter = pd.getWriteMethod();
/*     */           
/* 433 */           if ((getter == null) || (setter == null))
/*     */           {
/* 435 */             if ((pd instanceof IndexedPropertyDescriptor))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 443 */               if (logger.isLoggable(MLevel.WARNING)) {
/* 444 */                 logger.warning("BeansUtils.overwriteAccessibleProperties() does not support indexed properties that do not provide single-valued array getters and setters! [The indexed methods provide no means of modifying the size of the array in the destination bean if it does not match the source.]");
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 451 */             if (logger.isLoggable(MLevel.INFO)) {
/* 452 */               logger.info("Property inaccessible for overwriting: " + pd.getName());
/*     */             }
/*     */           }
/*     */           else {
/* 456 */             Object value = getter.invoke(sourceBean, EMPTY_ARGS);
/* 457 */             overwriteProperty(name, value, setter, destBean);
/*     */           }
/*     */         }
/*     */       }
/* 461 */       if (logger.isLoggable(MLevel.WARNING))
/*     */       {
/* 463 */         for (ii = _props.iterator(); ii.hasNext();) {
/* 464 */           logger.warning("failed to find expected property: " + ii.next());
/*     */         }
/*     */       }
/*     */     } catch (IntrospectionException e) {
/*     */       Iterator ii;
/* 469 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 473 */       if (logger.isLoggable(MLevel.FINE)) {
/* 474 */         logger.logp(MLevel.FINE, BeansUtils.class.getName(), "overwriteSpecificAccessibleProperties( Object sourceBean, Object destBean, Collection props )", "Caught unexpected Exception; Converting to IntrospectionException.", e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 479 */       throw new IntrospectionException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */   public static void debugShowPropertyChange(PropertyChangeEvent evt)
/*     */   {
/* 485 */     System.err.println("PropertyChangeEvent: [ propertyName -> " + evt.getPropertyName() + ", oldValue -> " + evt.getOldValue() + ", newValue -> " + evt.getNewValue() + " ]");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\beans\BeansUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */