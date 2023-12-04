/*     */ package com.mchange.v2.resourcepool;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
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
/*     */ public class ResourcePoolEventSupport
/*     */ {
/*     */   ResourcePool source;
/*  31 */   Set mlisteners = new HashSet();
/*     */   
/*     */   public ResourcePoolEventSupport(ResourcePool source) {
/*  34 */     this.source = source;
/*     */   }
/*     */   
/*  37 */   public synchronized void addResourcePoolListener(ResourcePoolListener mlistener) { this.mlisteners.add(mlistener); }
/*     */   
/*     */   public synchronized void removeResourcePoolListener(ResourcePoolListener mlistener) {
/*  40 */     this.mlisteners.remove(mlistener);
/*     */   }
/*     */   
/*     */   public synchronized void fireResourceAcquired(Object resc, int pool_size, int available_size, int removed_but_unreturned_size)
/*     */   {
/*     */     ResourcePoolEvent evt;
/*     */     Iterator i;
/*  47 */     if (!this.mlisteners.isEmpty())
/*     */     {
/*  49 */       evt = new ResourcePoolEvent(this.source, resc, false, pool_size, available_size, removed_but_unreturned_size);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  55 */       for (i = this.mlisteners.iterator(); i.hasNext();)
/*     */       {
/*  57 */         ResourcePoolListener rpl = (ResourcePoolListener)i.next();
/*  58 */         rpl.resourceAcquired(evt);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void fireResourceCheckedIn(Object resc, int pool_size, int available_size, int removed_but_unreturned_size)
/*     */   {
/*     */     ResourcePoolEvent evt;
/*     */     Iterator i;
/*  68 */     if (!this.mlisteners.isEmpty())
/*     */     {
/*  70 */       evt = new ResourcePoolEvent(this.source, resc, false, pool_size, available_size, removed_but_unreturned_size);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */       for (i = this.mlisteners.iterator(); i.hasNext();)
/*     */       {
/*  78 */         ResourcePoolListener rpl = (ResourcePoolListener)i.next();
/*  79 */         rpl.resourceCheckedIn(evt);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void fireResourceCheckedOut(Object resc, int pool_size, int available_size, int removed_but_unreturned_size)
/*     */   {
/*     */     ResourcePoolEvent evt;
/*     */     Iterator i;
/*  89 */     if (!this.mlisteners.isEmpty())
/*     */     {
/*  91 */       evt = new ResourcePoolEvent(this.source, resc, true, pool_size, available_size, removed_but_unreturned_size);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */       for (i = this.mlisteners.iterator(); i.hasNext();)
/*     */       {
/*  99 */         ResourcePoolListener rpl = (ResourcePoolListener)i.next();
/* 100 */         rpl.resourceCheckedOut(evt);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void fireResourceRemoved(Object resc, boolean checked_out_resource, int pool_size, int available_size, int removed_but_unreturned_size)
/*     */   {
/*     */     ResourcePoolEvent evt;
/*     */     
/*     */     Iterator i;
/* 111 */     if (!this.mlisteners.isEmpty())
/*     */     {
/* 113 */       evt = new ResourcePoolEvent(this.source, resc, checked_out_resource, pool_size, available_size, removed_but_unreturned_size);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 119 */       for (i = this.mlisteners.iterator(); i.hasNext();)
/*     */       {
/* 121 */         ResourcePoolListener rpl = (ResourcePoolListener)i.next();
/* 122 */         rpl.resourceRemoved(evt);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolEventSupport.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */