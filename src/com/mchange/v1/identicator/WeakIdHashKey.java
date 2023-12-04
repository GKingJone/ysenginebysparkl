/*    */ package com.mchange.v1.identicator;
/*    */ 
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.lang.ref.WeakReference;
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
/*    */ final class WeakIdHashKey
/*    */   extends IdHashKey
/*    */ {
/*    */   Ref keyRef;
/*    */   int hash;
/*    */   
/*    */   public WeakIdHashKey(Object keyObj, Identicator id, ReferenceQueue rq)
/*    */   {
/* 37 */     super(id);
/*    */     
/* 39 */     if (keyObj == null) {
/* 40 */       throw new UnsupportedOperationException("Collection does not accept nulls!");
/*    */     }
/* 42 */     this.keyRef = new Ref(keyObj, rq);
/* 43 */     this.hash = id.hash(keyObj);
/*    */   }
/*    */   
/*    */   public Ref getInternalRef() {
/* 47 */     return this.keyRef;
/*    */   }
/*    */   
/* 50 */   public Object getKeyObj() { return this.keyRef.get(); }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 55 */     if ((o instanceof WeakIdHashKey))
/*    */     {
/* 57 */       WeakIdHashKey other = (WeakIdHashKey)o;
/* 58 */       if (this.keyRef == other.keyRef) {
/* 59 */         return true;
/*    */       }
/*    */       
/* 62 */       Object myKeyObj = this.keyRef.get();
/* 63 */       Object oKeyObj = other.keyRef.get();
/* 64 */       if ((myKeyObj == null) || (oKeyObj == null)) {
/* 65 */         return false;
/*    */       }
/* 67 */       return this.id.identical(myKeyObj, oKeyObj);
/*    */     }
/*    */     
/*    */ 
/* 71 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 75 */     return this.hash;
/*    */   }
/*    */   
/*    */   class Ref extends WeakReference {
/*    */     public Ref(Object referant, ReferenceQueue rq) {
/* 80 */       super(rq);
/*    */     }
/*    */     
/* 83 */     WeakIdHashKey getKey() { return WeakIdHashKey.this; }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\identicator\WeakIdHashKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */