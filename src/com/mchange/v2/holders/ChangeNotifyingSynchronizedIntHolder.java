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
/*    */ 
/*    */ public final class ChangeNotifyingSynchronizedIntHolder
/*    */   implements ThreadSafeIntHolder, Serializable
/*    */ {
/*    */   transient int value;
/*    */   transient boolean notify_all;
/*    */   static final long serialVersionUID = 1L;
/*    */   private static final short VERSION = 1;
/*    */   
/*    */   public ChangeNotifyingSynchronizedIntHolder(int value, boolean notify_all)
/*    */   {
/* 36 */     this.value = value;
/* 37 */     this.notify_all = notify_all;
/*    */   }
/*    */   
/*    */   public ChangeNotifyingSynchronizedIntHolder() {
/* 41 */     this(0, true);
/*    */   }
/*    */   
/* 44 */   public synchronized int getValue() { return this.value; }
/*    */   
/*    */   public synchronized void setValue(int value)
/*    */   {
/* 48 */     if (value != this.value)
/*    */     {
/* 50 */       this.value = value;
/* 51 */       doNotify();
/*    */     }
/*    */   }
/*    */   
/*    */   public synchronized void increment()
/*    */   {
/* 57 */     this.value += 1;
/* 58 */     doNotify();
/*    */   }
/*    */   
/*    */   public synchronized void decrement()
/*    */   {
/* 63 */     this.value -= 1;
/* 64 */     doNotify();
/*    */   }
/*    */   
/*    */ 
/*    */   private void doNotify()
/*    */   {
/* 70 */     if (this.notify_all) notifyAll(); else {
/* 71 */       notify();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   private void writeObject(ObjectOutputStream out)
/*    */     throws IOException
/*    */   {
/* 80 */     out.writeShort(1);
/* 81 */     out.writeInt(this.value);
/* 82 */     out.writeBoolean(this.notify_all);
/*    */   }
/*    */   
/*    */   private void readObject(ObjectInputStream in) throws IOException
/*    */   {
/* 87 */     short version = in.readShort();
/* 88 */     switch (version)
/*    */     {
/*    */     case 1: 
/* 91 */       this.value = in.readInt();
/* 92 */       this.notify_all = in.readBoolean();
/* 93 */       break;
/*    */     default: 
/* 95 */       throw new UnsupportedVersionException(this, version);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\holders\ChangeNotifyingSynchronizedIntHolder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */