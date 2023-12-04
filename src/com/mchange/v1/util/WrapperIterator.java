/*    */ package com.mchange.v1.util;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class WrapperIterator
/*    */   implements Iterator
/*    */ {
/* 35 */   protected static final Object SKIP_TOKEN = new Object();
/*    */   
/*    */   static final boolean DEBUG = true;
/*    */   
/*    */   Iterator inner;
/*    */   boolean supports_remove;
/* 41 */   Object lastOut = null;
/* 42 */   Object nextOut = SKIP_TOKEN;
/*    */   
/*    */   public WrapperIterator(Iterator inner, boolean supports_remove)
/*    */   {
/* 46 */     this.inner = inner;
/* 47 */     this.supports_remove = supports_remove;
/*    */   }
/*    */   
/*    */   public WrapperIterator(Iterator inner) {
/* 51 */     this(inner, false);
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 55 */     findNext();
/* 56 */     return this.nextOut != SKIP_TOKEN;
/*    */   }
/*    */   
/*    */   private void findNext()
/*    */   {
/* 61 */     if (this.nextOut == SKIP_TOKEN)
/*    */     {
/* 63 */       while ((this.inner.hasNext()) && (this.nextOut == SKIP_TOKEN)) {
/* 64 */         this.nextOut = transformObject(this.inner.next());
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public Object next() {
/* 70 */     findNext();
/* 71 */     if (this.nextOut != SKIP_TOKEN)
/*    */     {
/* 73 */       this.lastOut = this.nextOut;
/* 74 */       this.nextOut = SKIP_TOKEN;
/*    */     }
/*    */     else {
/* 77 */       throw new NoSuchElementException();
/*    */     }
/*    */     
/*    */ 
/* 81 */     DebugUtils.myAssert((this.nextOut == SKIP_TOKEN) && (this.lastOut != SKIP_TOKEN));
/* 82 */     return this.lastOut;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 87 */     if (this.supports_remove)
/*    */     {
/* 89 */       if (this.nextOut != SKIP_TOKEN) {
/* 90 */         throw new UnsupportedOperationException(getClass().getName() + " cannot support remove after" + " hasNext() has been called!");
/*    */       }
/*    */       
/* 93 */       if (this.lastOut != SKIP_TOKEN) {
/* 94 */         this.inner.remove();
/*    */       } else {
/* 96 */         throw new NoSuchElementException();
/*    */       }
/*    */     } else {
/* 99 */       throw new UnsupportedOperationException();
/*    */     }
/*    */   }
/*    */   
/*    */   protected abstract Object transformObject(Object paramObject);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\WrapperIterator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */