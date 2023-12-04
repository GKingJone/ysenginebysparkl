/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class DecoratedObjectFactory
/*     */   implements Iterable<Decorator>
/*     */ {
/*  41 */   private static final Logger LOG = Log.getLogger(DecoratedObjectFactory.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  46 */   public static final String ATTR = DecoratedObjectFactory.class.getName();
/*     */   
/*  48 */   private List<Decorator> decorators = new ArrayList();
/*     */   
/*     */   public void addDecorator(Decorator decorator)
/*     */   {
/*  52 */     LOG.debug("Adding Decorator: {}", new Object[] { decorator });
/*  53 */     this.decorators.add(decorator);
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*  58 */     this.decorators.clear();
/*     */   }
/*     */   
/*     */   public <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException
/*     */   {
/*  63 */     if (LOG.isDebugEnabled())
/*     */     {
/*  65 */       LOG.debug("Creating Instance: " + clazz, new Object[0]);
/*     */     }
/*  67 */     T o = clazz.newInstance();
/*  68 */     return (T)decorate(o);
/*     */   }
/*     */   
/*     */   public <T> T decorate(T obj)
/*     */   {
/*  73 */     T f = obj;
/*     */     
/*  75 */     for (int i = this.decorators.size() - 1; i >= 0; i--)
/*     */     {
/*  77 */       f = ((Decorator)this.decorators.get(i)).decorate(f);
/*     */     }
/*  79 */     return f;
/*     */   }
/*     */   
/*     */   public void destroy(Object obj)
/*     */   {
/*  84 */     for (Decorator decorator : this.decorators)
/*     */     {
/*  86 */       decorator.destroy(obj);
/*     */     }
/*     */   }
/*     */   
/*     */   public List<Decorator> getDecorators()
/*     */   {
/*  92 */     return Collections.unmodifiableList(this.decorators);
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<Decorator> iterator()
/*     */   {
/*  98 */     return this.decorators.iterator();
/*     */   }
/*     */   
/*     */   public void setDecorators(List<? extends Decorator> decorators)
/*     */   {
/* 103 */     this.decorators.clear();
/* 104 */     if (decorators != null)
/*     */     {
/* 106 */       this.decorators.addAll(decorators);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 113 */     StringBuilder str = new StringBuilder();
/* 114 */     str.append(getClass().getName()).append("[decorators=");
/* 115 */     str.append(Integer.toString(this.decorators.size()));
/* 116 */     str.append("]");
/* 117 */     return str.toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\DecoratedObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */