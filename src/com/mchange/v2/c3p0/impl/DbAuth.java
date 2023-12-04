/*    */ package com.mchange.v2.c3p0.impl;
/*    */ 
/*    */ import com.mchange.v2.lang.ObjectUtils;
/*    */ import com.mchange.v2.ser.UnsupportedVersionException;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.Serializable;
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
/*    */ public final class DbAuth
/*    */   implements Serializable
/*    */ {
/*    */   transient String username;
/*    */   transient String password;
/*    */   static final long serialVersionUID = 1L;
/*    */   private static final short VERSION = 1;
/*    */   
/*    */   public DbAuth(String username, String password)
/*    */   {
/* 37 */     this.username = username;
/* 38 */     this.password = password;
/*    */   }
/*    */   
/*    */   public String getUser() {
/* 42 */     return this.username;
/*    */   }
/*    */   
/* 45 */   public String getPassword() { return this.password; }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 49 */     if (this == o)
/* 50 */       return true;
/* 51 */     if ((o != null) && (getClass() == o.getClass()))
/*    */     {
/* 53 */       DbAuth other = (DbAuth)o;
/* 54 */       return (ObjectUtils.eqOrBothNull(this.username, other.username)) && (ObjectUtils.eqOrBothNull(this.password, other.password));
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 59 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 64 */     return ObjectUtils.hashOrZero(this.username) ^ ObjectUtils.hashOrZero(this.password);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private void writeObject(ObjectOutputStream out)
/*    */     throws IOException
/*    */   {
/* 75 */     out.writeShort(1);
/* 76 */     out.writeObject(this.username);
/* 77 */     out.writeObject(this.password);
/*    */   }
/*    */   
/*    */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*    */   {
/* 82 */     short version = in.readShort();
/* 83 */     switch (version)
/*    */     {
/*    */     case 1: 
/* 86 */       this.username = ((String)in.readObject());
/* 87 */       this.password = ((String)in.readObject());
/* 88 */       break;
/*    */     default: 
/* 90 */       throw new UnsupportedVersionException(this, version);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\DbAuth.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */