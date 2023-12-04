/*    */ package com.mchange.v2.holders;
/*    */ 
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
/*    */ public class SynchronizedIntHolder
/*    */   implements ThreadSafeIntHolder, Serializable
/*    */ {
/*    */   transient int value;
/*    */   static final long serialVersionUID = 1L;
/*    */   private static final short VERSION = 1;
/*    */   
/*    */   public SynchronizedIntHolder(int value)
/*    */   {
/* 34 */     this.value = value;
/*    */   }
/*    */   
/* 37 */   public SynchronizedIntHolder() { this(0); }
/*    */   
/*    */   public synchronized int getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */   
/* 43 */   public synchronized void setValue(int value) { this.value = value; }
/*    */   
/*    */   public synchronized void increment() {
/* 46 */     this.value += 1;
/*    */   }
/*    */   
/* 49 */   public synchronized void decrement() { this.value -= 1; }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private void writeObject(ObjectOutputStream out)
/*    */     throws IOException
/*    */   {
/* 57 */     out.writeShort(1);
/* 58 */     out.writeInt(this.value);
/*    */   }
/*    */   
/*    */   private void readObject(ObjectInputStream in) throws IOException
/*    */   {
/* 63 */     short version = in.readShort();
/* 64 */     switch (version)
/*    */     {
/*    */     case 1: 
/* 67 */       this.value = in.readInt();
/* 68 */       break;
/*    */     default: 
/* 70 */       throw new UnsupportedVersionException(this, version);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\holders\SynchronizedIntHolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */