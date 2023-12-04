/*     */ package com.facebook.presto.jdbc.internal.jetty.util.component;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedOperation;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*     */ @ManagedObject("Implementation of Container and LifeCycle")
/*     */ public class ContainerLifeCycle
/*     */   extends AbstractLifeCycle
/*     */   implements Container, Destroyable, Dumpable
/*     */ {
/*  77 */   private static final Logger LOG = Log.getLogger(ContainerLifeCycle.class);
/*  78 */   private final List<Bean> _beans = new CopyOnWriteArrayList();
/*  79 */   private final List<Container.Listener> _listeners = new CopyOnWriteArrayList();
/*  80 */   private boolean _doStarted = false;
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
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  94 */     this._doStarted = true;
/*     */     
/*     */ 
/*  97 */     for (Bean b : this._beans)
/*     */     {
/*  99 */       if ((b._bean instanceof LifeCycle))
/*     */       {
/* 101 */         LifeCycle l = (LifeCycle)b._bean;
/* 102 */         switch (b._managed)
/*     */         {
/*     */         case MANAGED: 
/* 105 */           if (!l.isRunning())
/* 106 */             start(l);
/*     */           break;
/*     */         case AUTO: 
/* 109 */           if (l.isRunning()) {
/* 110 */             unmanage(b);
/*     */           }
/*     */           else {
/* 113 */             manage(b);
/* 114 */             start(l);
/*     */           }
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 121 */     super.doStart();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void start(LifeCycle l)
/*     */     throws Exception
/*     */   {
/* 132 */     l.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void stop(LifeCycle l)
/*     */     throws Exception
/*     */   {
/* 143 */     l.stop();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 152 */     this._doStarted = false;
/* 153 */     super.doStop();
/* 154 */     List<Bean> reverse = new ArrayList(this._beans);
/* 155 */     Collections.reverse(reverse);
/* 156 */     for (Bean b : reverse)
/*     */     {
/* 158 */       if ((b._managed == Managed.MANAGED) && ((b._bean instanceof LifeCycle)))
/*     */       {
/* 160 */         LifeCycle l = (LifeCycle)b._bean;
/* 161 */         stop(l);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 172 */     List<Bean> reverse = new ArrayList(this._beans);
/* 173 */     Collections.reverse(reverse);
/* 174 */     for (Bean b : reverse)
/*     */     {
/* 176 */       if (((b._bean instanceof Destroyable)) && ((b._managed == Managed.MANAGED) || (b._managed == Managed.POJO)))
/*     */       {
/* 178 */         Destroyable d = (Destroyable)b._bean;
/* 179 */         d.destroy();
/*     */       }
/*     */     }
/* 182 */     this._beans.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean contains(Object bean)
/*     */   {
/* 192 */     for (Bean b : this._beans)
/* 193 */       if (b._bean == bean)
/* 194 */         return true;
/* 195 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isManaged(Object bean)
/*     */   {
/* 204 */     for (Bean b : this._beans)
/* 205 */       if (b._bean == bean)
/* 206 */         return b.isManaged();
/* 207 */     return false;
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
/*     */   public boolean addBean(Object o)
/*     */   {
/* 224 */     if ((o instanceof LifeCycle))
/*     */     {
/* 226 */       LifeCycle l = (LifeCycle)o;
/* 227 */       return addBean(o, l.isRunning() ? Managed.UNMANAGED : Managed.AUTO);
/*     */     }
/*     */     
/* 230 */     return addBean(o, Managed.POJO);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean addBean(Object o, boolean managed)
/*     */   {
/* 242 */     if ((o instanceof LifeCycle))
/* 243 */       return addBean(o, managed ? Managed.MANAGED : Managed.UNMANAGED);
/* 244 */     return addBean(o, managed ? Managed.POJO : Managed.UNMANAGED);
/*     */   }
/*     */   
/*     */   public boolean addBean(Object o, Managed managed)
/*     */   {
/* 249 */     if (contains(o)) {
/* 250 */       return false;
/*     */     }
/* 252 */     Bean new_bean = new Bean(o, null);
/*     */     
/*     */ 
/* 255 */     if ((o instanceof Container.Listener)) {
/* 256 */       addEventListener((Container.Listener)o);
/*     */     }
/*     */     
/* 259 */     this._beans.add(new_bean);
/*     */     
/*     */ 
/* 262 */     for (Container.Listener l : this._listeners) {
/* 263 */       l.beanAdded(this, o);
/*     */     }
/*     */     try
/*     */     {
/* 267 */       switch (managed)
/*     */       {
/*     */       case UNMANAGED: 
/* 270 */         unmanage(new_bean);
/* 271 */         break;
/*     */       
/*     */       case MANAGED: 
/* 274 */         manage(new_bean);
/*     */         
/* 276 */         if ((isStarting()) && (this._doStarted))
/*     */         {
/* 278 */           LifeCycle l = (LifeCycle)o;
/* 279 */           if (!l.isRunning())
/* 280 */             start(l); }
/* 281 */         break;
/*     */       
/*     */ 
/*     */       case AUTO: 
/* 285 */         if ((o instanceof LifeCycle))
/*     */         {
/* 287 */           LifeCycle l = (LifeCycle)o;
/* 288 */           if (isStarting())
/*     */           {
/* 290 */             if (l.isRunning()) {
/* 291 */               unmanage(new_bean);
/* 292 */             } else if (this._doStarted)
/*     */             {
/* 294 */               manage(new_bean);
/* 295 */               start(l);
/*     */             }
/*     */             else {
/* 298 */               new_bean._managed = Managed.AUTO;
/*     */             }
/* 300 */           } else if (isStarted()) {
/* 301 */             unmanage(new_bean);
/*     */           } else {
/* 303 */             new_bean._managed = Managed.AUTO;
/*     */           }
/*     */         } else {
/* 306 */           new_bean._managed = Managed.POJO; }
/* 307 */         break;
/*     */       
/*     */       case POJO: 
/* 310 */         new_bean._managed = Managed.POJO;
/*     */       }
/*     */     }
/*     */     catch (RuntimeException|Error e)
/*     */     {
/* 315 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 319 */       throw new RuntimeException(e);
/*     */     }
/*     */     
/* 322 */     if (LOG.isDebugEnabled()) {
/* 323 */       LOG.debug("{} added {}", new Object[] { this, new_bean });
/*     */     }
/* 325 */     return true;
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
/*     */   public void addManaged(LifeCycle lifecycle)
/*     */   {
/* 339 */     addBean(lifecycle, true);
/*     */     try
/*     */     {
/* 342 */       if ((isRunning()) && (!lifecycle.isRunning())) {
/* 343 */         start(lifecycle);
/*     */       }
/*     */     }
/*     */     catch (RuntimeException|Error e) {
/* 347 */       throw e;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 351 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addEventListener(Container.Listener listener)
/*     */   {
/* 358 */     if (this._listeners.contains(listener)) {
/* 359 */       return;
/*     */     }
/* 361 */     this._listeners.add(listener);
/*     */     
/*     */ 
/* 364 */     for (Bean b : this._beans)
/*     */     {
/* 366 */       listener.beanAdded(this, b._bean);
/*     */       
/*     */ 
/* 369 */       if (((listener instanceof InheritedListener)) && (b.isManaged()) && ((b._bean instanceof Container)))
/*     */       {
/* 371 */         if ((b._bean instanceof ContainerLifeCycle)) {
/* 372 */           ((ContainerLifeCycle)b._bean).addBean(listener, false);
/*     */         } else {
/* 374 */           ((Container)b._bean).addBean(listener);
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
/*     */   public void manage(Object bean)
/*     */   {
/* 387 */     for (Bean b : this._beans)
/*     */     {
/* 389 */       if (b._bean == bean)
/*     */       {
/* 391 */         manage(b);
/* 392 */         return;
/*     */       }
/*     */     }
/* 395 */     throw new IllegalArgumentException("Unknown bean " + bean);
/*     */   }
/*     */   
/*     */   private void manage(Bean bean)
/*     */   {
/* 400 */     if (bean._managed != Managed.MANAGED)
/*     */     {
/* 402 */       bean._managed = Managed.MANAGED;
/*     */       
/* 404 */       if ((bean._bean instanceof Container))
/*     */       {
/* 406 */         for (Container.Listener l : this._listeners)
/*     */         {
/* 408 */           if ((l instanceof InheritedListener))
/*     */           {
/* 410 */             if ((bean._bean instanceof ContainerLifeCycle)) {
/* 411 */               ((ContainerLifeCycle)bean._bean).addBean(l, false);
/*     */             } else {
/* 413 */               ((Container)bean._bean).addBean(l);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 418 */       if ((bean._bean instanceof AbstractLifeCycle))
/*     */       {
/* 420 */         ((AbstractLifeCycle)bean._bean).setStopTimeout(getStopTimeout());
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
/*     */   public void unmanage(Object bean)
/*     */   {
/* 433 */     for (Bean b : this._beans)
/*     */     {
/* 435 */       if (b._bean == bean)
/*     */       {
/* 437 */         unmanage(b);
/* 438 */         return;
/*     */       }
/*     */     }
/* 441 */     throw new IllegalArgumentException("Unknown bean " + bean);
/*     */   }
/*     */   
/*     */   private void unmanage(Bean bean)
/*     */   {
/* 446 */     if (bean._managed != Managed.UNMANAGED)
/*     */     {
/* 448 */       if ((bean._managed == Managed.MANAGED) && ((bean._bean instanceof Container)))
/*     */       {
/* 450 */         for (Container.Listener l : this._listeners)
/*     */         {
/* 452 */           if ((l instanceof InheritedListener))
/* 453 */             ((Container)bean._bean).removeBean(l);
/*     */         }
/*     */       }
/* 456 */       bean._managed = Managed.UNMANAGED;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Collection<Object> getBeans()
/*     */   {
/* 463 */     return getBeans(Object.class);
/*     */   }
/*     */   
/*     */   public void setBeans(Collection<Object> beans)
/*     */   {
/* 468 */     for (Object bean : beans) {
/* 469 */       addBean(bean);
/*     */     }
/*     */   }
/*     */   
/*     */   public <T> Collection<T> getBeans(Class<T> clazz)
/*     */   {
/* 475 */     ArrayList<T> beans = new ArrayList();
/* 476 */     for (Bean b : this._beans)
/*     */     {
/* 478 */       if (clazz.isInstance(b._bean))
/* 479 */         beans.add(clazz.cast(b._bean));
/*     */     }
/* 481 */     return beans;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T getBean(Class<T> clazz)
/*     */   {
/* 487 */     for (Bean b : this._beans)
/*     */     {
/* 489 */       if (clazz.isInstance(b._bean))
/* 490 */         return (T)clazz.cast(b._bean);
/*     */     }
/* 492 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeBeans()
/*     */   {
/* 500 */     ArrayList<Bean> beans = new ArrayList(this._beans);
/* 501 */     for (Bean b : beans) {
/* 502 */       remove(b);
/*     */     }
/*     */   }
/*     */   
/*     */   private Bean getBean(Object o) {
/* 507 */     for (Bean b : this._beans)
/*     */     {
/* 509 */       if (b._bean == o)
/* 510 */         return b;
/*     */     }
/* 512 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean removeBean(Object o)
/*     */   {
/* 518 */     Bean b = getBean(o);
/* 519 */     return (b != null) && (remove(b));
/*     */   }
/*     */   
/*     */   private boolean remove(Bean bean)
/*     */   {
/* 524 */     if (this._beans.remove(bean))
/*     */     {
/* 526 */       boolean wasManaged = bean.isManaged();
/*     */       
/* 528 */       unmanage(bean);
/*     */       
/* 530 */       for (Container.Listener l : this._listeners) {
/* 531 */         l.beanRemoved(this, bean._bean);
/*     */       }
/* 533 */       if ((bean._bean instanceof Container.Listener)) {
/* 534 */         removeEventListener((Container.Listener)bean._bean);
/*     */       }
/*     */       
/* 537 */       if ((wasManaged) && ((bean._bean instanceof LifeCycle)))
/*     */       {
/*     */         try
/*     */         {
/* 541 */           stop((LifeCycle)bean._bean);
/*     */         }
/*     */         catch (RuntimeException|Error e)
/*     */         {
/* 545 */           throw e;
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 549 */           throw new RuntimeException(e);
/*     */         }
/*     */       }
/* 552 */       return true;
/*     */     }
/* 554 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeEventListener(Container.Listener listener)
/*     */   {
/* 560 */     if (this._listeners.remove(listener))
/*     */     {
/*     */ 
/* 563 */       for (Bean b : this._beans)
/*     */       {
/* 565 */         listener.beanRemoved(this, b._bean);
/*     */         
/* 567 */         if (((listener instanceof InheritedListener)) && (b.isManaged()) && ((b._bean instanceof Container))) {
/* 568 */           ((Container)b._bean).removeBean(listener);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setStopTimeout(long stopTimeout)
/*     */   {
/* 576 */     super.setStopTimeout(stopTimeout);
/* 577 */     for (Bean bean : this._beans)
/*     */     {
/* 579 */       if ((bean.isManaged()) && ((bean._bean instanceof AbstractLifeCycle))) {
/* 580 */         ((AbstractLifeCycle)bean._bean).setStopTimeout(stopTimeout);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedOperation("Dump the object to stderr")
/*     */   public void dumpStdErr()
/*     */   {
/*     */     try
/*     */     {
/* 593 */       dump(System.err, "");
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 597 */       LOG.warn(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @ManagedOperation("Dump the object to a string")
/*     */   public String dump()
/*     */   {
/* 605 */     return dump(this);
/*     */   }
/*     */   
/*     */   public static String dump(Dumpable dumpable)
/*     */   {
/* 610 */     StringBuilder b = new StringBuilder();
/*     */     try
/*     */     {
/* 613 */       dumpable.dump(b, "");
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 617 */       LOG.warn(e);
/*     */     }
/* 619 */     return b.toString();
/*     */   }
/*     */   
/*     */   public void dump(Appendable out) throws IOException
/*     */   {
/* 624 */     dump(out, "");
/*     */   }
/*     */   
/*     */   protected void dumpThis(Appendable out) throws IOException
/*     */   {
/* 629 */     out.append(String.valueOf(this)).append(" - ").append(getState()).append("\n");
/*     */   }
/*     */   
/*     */   public static void dumpObject(Appendable out, Object o) throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 636 */       if ((o instanceof LifeCycle)) {
/* 637 */         out.append(String.valueOf(o)).append(" - ").append(AbstractLifeCycle.getState((LifeCycle)o)).append("\n");
/*     */       } else {
/* 639 */         out.append(String.valueOf(o)).append("\n");
/*     */       }
/*     */     }
/*     */     catch (Throwable th) {
/* 643 */       out.append(" => ").append(th.toString()).append('\n');
/*     */     }
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 650 */     dumpBeans(out, indent, new Collection[0]);
/*     */   }
/*     */   
/*     */   protected void dumpBeans(Appendable out, String indent, Collection<?>... collections) throws IOException
/*     */   {
/* 655 */     dumpThis(out);
/* 656 */     int size = this._beans.size();
/* 657 */     for (c : collections)
/* 658 */       size += c.size();
/* 659 */     if (size == 0)
/* 660 */       return;
/* 661 */     int i = 0;
/* 662 */     for (Object localObject1 = this._beans.iterator(); ((Iterator)localObject1).hasNext();) { b = (Bean)((Iterator)localObject1).next();
/*     */       
/* 664 */       i++;
/*     */       
/* 666 */       switch (b._managed)
/*     */       {
/*     */       case POJO: 
/* 669 */         out.append(indent).append(" +- ");
/* 670 */         if ((b._bean instanceof Dumpable)) {
/* 671 */           ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
/*     */         } else
/* 673 */           dumpObject(out, b._bean);
/* 674 */         break;
/*     */       
/*     */       case MANAGED: 
/* 677 */         out.append(indent).append(" += ");
/* 678 */         if ((b._bean instanceof Dumpable)) {
/* 679 */           ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
/*     */         } else
/* 681 */           dumpObject(out, b._bean);
/* 682 */         break;
/*     */       
/*     */       case UNMANAGED: 
/* 685 */         out.append(indent).append(" +~ ");
/* 686 */         dumpObject(out, b._bean);
/* 687 */         break;
/*     */       
/*     */       case AUTO: 
/* 690 */         out.append(indent).append(" +? ");
/* 691 */         if ((b._bean instanceof Dumpable)) {
/* 692 */           ((Dumpable)b._bean).dump(out, indent + (i == size ? "    " : " |  "));
/*     */         } else {
/* 694 */           dumpObject(out, b._bean);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */     Bean b;
/* 700 */     if (i < size) {
/* 701 */       out.append(indent).append(" |\n");
/*     */     }
/* 703 */     localObject1 = collections;Collection<?> localCollection1 = localObject1.length; for (Collection<?> c = 0; c < localCollection1; c++) { Collection<?> c = localObject1[c];
/*     */       
/* 705 */       for (Object o : c)
/*     */       {
/* 707 */         i++;
/* 708 */         out.append(indent).append(" +> ");
/*     */         
/* 710 */         if ((o instanceof Dumpable)) {
/* 711 */           ((Dumpable)o).dump(out, indent + (i == size ? "    " : " |  "));
/*     */         } else {
/* 713 */           dumpObject(out, o);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void dump(Appendable out, String indent, Collection<?>... collections) throws IOException {
/* 720 */     if (collections.length == 0)
/* 721 */       return;
/* 722 */     int size = 0;
/* 723 */     Collection<?>[] arrayOfCollection1 = collections;int i = arrayOfCollection1.length; for (Collection<?> localCollection1 = 0; localCollection1 < i; localCollection1++) { c = arrayOfCollection1[localCollection1];
/* 724 */       size += c.size(); }
/* 725 */     if (size == 0) {
/* 726 */       return;
/*     */     }
/* 728 */     int i = 0;
/* 729 */     Collection<?>[] arrayOfCollection2 = collections;localCollection1 = arrayOfCollection2.length; for (Collection<?> c = 0; c < localCollection1; c++) { Collection<?> c = arrayOfCollection2[c];
/*     */       
/* 731 */       for (Object o : c)
/*     */       {
/* 733 */         i++;
/* 734 */         out.append(indent).append(" +- ");
/*     */         
/* 736 */         if ((o instanceof Dumpable)) {
/* 737 */           ((Dumpable)o).dump(out, indent + (i == size ? "    " : " |  "));
/*     */         } else
/* 739 */           dumpObject(out, o);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static enum Managed {
/* 745 */     POJO,  MANAGED,  UNMANAGED,  AUTO;
/*     */     
/*     */     private Managed() {} }
/*     */   
/*     */   private static class Bean { private final Object _bean;
/* 750 */     private volatile Managed _managed = Managed.POJO;
/*     */     
/*     */     private Bean(Object b)
/*     */     {
/* 754 */       this._bean = b;
/*     */     }
/*     */     
/*     */     public boolean isManaged()
/*     */     {
/* 759 */       return this._managed == Managed.MANAGED;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 765 */       return String.format("{%s,%s}", new Object[] { this._bean, this._managed });
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateBean(Object oldBean, Object newBean)
/*     */   {
/* 771 */     if (newBean != oldBean)
/*     */     {
/* 773 */       if (oldBean != null)
/* 774 */         removeBean(oldBean);
/* 775 */       if (newBean != null) {
/* 776 */         addBean(newBean);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateBean(Object oldBean, Object newBean, boolean managed) {
/* 782 */     if (newBean != oldBean)
/*     */     {
/* 784 */       if (oldBean != null)
/* 785 */         removeBean(oldBean);
/* 786 */       if (newBean != null) {
/* 787 */         addBean(newBean, managed);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateBeans(Object[] oldBeans, Object[] newBeans)
/*     */   {
/* 794 */     if (oldBeans != null) {
/*     */       label78:
/* 796 */       for (Object o : oldBeans)
/*     */       {
/* 798 */         if (newBeans != null)
/*     */         {
/* 800 */           for (Object n : newBeans)
/* 801 */             if (o == n)
/*     */               break label78;
/*     */         }
/* 804 */         removeBean(o);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 809 */     if (newBeans != null) {
/*     */       label162:
/* 811 */       for (Object n : newBeans)
/*     */       {
/* 813 */         if (oldBeans != null)
/*     */         {
/* 815 */           for (Object o : oldBeans)
/* 816 */             if (o == n)
/*     */               break label162;
/*     */         }
/* 819 */         addBean(n);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\ContainerLifeCycle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */