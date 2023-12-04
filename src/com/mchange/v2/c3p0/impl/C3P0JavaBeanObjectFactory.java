/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import com.mchange.v2.c3p0.C3P0Registry;
/*    */ import com.mchange.v2.naming.JavaBeanObjectFactory;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class C3P0JavaBeanObjectFactory
/*    */   extends JavaBeanObjectFactory
/*    */ {
/* 34 */   private static final Class[] CTOR_ARG_TYPES = { Boolean.TYPE };
/* 35 */   private static final Object[] CTOR_ARGS = { Boolean.FALSE };
/*    */   
/*    */   protected Object createBlankInstance(Class beanClass) throws Exception
/*    */   {
/* 39 */     if (IdentityTokenized.class.isAssignableFrom(beanClass))
/*    */     {
/* 41 */       Constructor ctor = beanClass.getConstructor(CTOR_ARG_TYPES);
/* 42 */       return ctor.newInstance(CTOR_ARGS);
/*    */     }
/*    */     
/* 45 */     return super.createBlankInstance(beanClass);
/*    */   }
/*    */   
/*    */   protected Object findBean(Class beanClass, Map propertyMap, Set refProps) throws Exception
/*    */   {
/* 50 */     Object out = super.findBean(beanClass, propertyMap, refProps);
/* 51 */     if ((out instanceof IdentityTokenized)) {
/* 52 */       out = C3P0Registry.reregister((IdentityTokenized)out);
/*    */     }
/*    */     
/* 55 */     return out;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0JavaBeanObjectFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */