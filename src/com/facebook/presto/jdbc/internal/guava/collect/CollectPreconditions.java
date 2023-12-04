/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
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
/*    */ @GwtCompatible
/*    */ final class CollectPreconditions
/*    */ {
/*    */   static void checkEntryNotNull(Object key, Object value)
/*    */   {
/*    */     String str;
/* 30 */     if (key == null) {
/* 31 */       str = String.valueOf(String.valueOf(value));throw new NullPointerException(24 + str.length() + "null key in entry: null=" + str); }
/* 32 */     if (value == null) {
/* 33 */       str = String.valueOf(String.valueOf(key));throw new NullPointerException(26 + str.length() + "null value in entry: " + str + "=null");
/*    */     }
/*    */   }
/*    */   
/*    */   static int checkNonnegative(int value, String name) {
/* 38 */     if (value < 0) {
/* 39 */       String str = String.valueOf(String.valueOf(name));int i = value;throw new IllegalArgumentException(40 + str.length() + str + " cannot be negative but was: " + i);
/*    */     }
/* 41 */     return value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   static void checkRemove(boolean canRemove)
/*    */   {
/* 49 */     Preconditions.checkState(canRemove, "no calls to next() since the last call to remove()");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\CollectPreconditions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */