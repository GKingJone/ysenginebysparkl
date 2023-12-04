/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import javax.transaction.xa.Xid;
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
/*     */ public class MysqlXid
/*     */   implements Xid
/*     */ {
/*  33 */   int hash = 0;
/*     */   
/*     */   byte[] myBqual;
/*     */   
/*     */   int myFormatId;
/*     */   byte[] myGtrid;
/*     */   
/*     */   public MysqlXid(byte[] gtrid, byte[] bqual, int formatId)
/*     */   {
/*  42 */     this.myGtrid = gtrid;
/*  43 */     this.myBqual = bqual;
/*  44 */     this.myFormatId = formatId;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object another)
/*     */   {
/*  50 */     if ((another instanceof Xid)) {
/*  51 */       Xid anotherAsXid = (Xid)another;
/*     */       
/*  53 */       if (this.myFormatId != anotherAsXid.getFormatId()) {
/*  54 */         return false;
/*     */       }
/*     */       
/*  57 */       byte[] otherBqual = anotherAsXid.getBranchQualifier();
/*  58 */       byte[] otherGtrid = anotherAsXid.getGlobalTransactionId();
/*     */       
/*  60 */       if ((otherGtrid != null) && (otherGtrid.length == this.myGtrid.length)) {
/*  61 */         int length = otherGtrid.length;
/*     */         
/*  63 */         for (int i = 0; i < length; i++) {
/*  64 */           if (otherGtrid[i] != this.myGtrid[i]) {
/*  65 */             return false;
/*     */           }
/*     */         }
/*     */         
/*  69 */         if ((otherBqual != null) && (otherBqual.length == this.myBqual.length)) {
/*  70 */           length = otherBqual.length;
/*     */           
/*  72 */           for (int i = 0; i < length; i++) {
/*  73 */             if (otherBqual[i] != this.myBqual[i]) {
/*  74 */               return false;
/*     */             }
/*     */           }
/*     */         } else {
/*  78 */           return false;
/*     */         }
/*     */         
/*  81 */         return true;
/*     */       }
/*     */     }
/*     */     
/*  85 */     return false;
/*     */   }
/*     */   
/*     */   public byte[] getBranchQualifier() {
/*  89 */     return this.myBqual;
/*     */   }
/*     */   
/*     */   public int getFormatId() {
/*  93 */     return this.myFormatId;
/*     */   }
/*     */   
/*     */   public byte[] getGlobalTransactionId() {
/*  97 */     return this.myGtrid;
/*     */   }
/*     */   
/*     */   public synchronized int hashCode()
/*     */   {
/* 102 */     if (this.hash == 0) {
/* 103 */       for (int i = 0; i < this.myGtrid.length; i++) {
/* 104 */         this.hash = (33 * this.hash + this.myGtrid[i]);
/*     */       }
/*     */     }
/*     */     
/* 108 */     return this.hash;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\MysqlXid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */