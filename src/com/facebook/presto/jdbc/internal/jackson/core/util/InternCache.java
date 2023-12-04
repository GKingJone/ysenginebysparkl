/*    */ package com.facebook.presto.jdbc.internal.jackson.core.util;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public final class InternCache
/*    */   extends ConcurrentHashMap<String, String>
/*    */ {
/*    */   private static final int MAX_ENTRIES = 180;
/* 28 */   public static final InternCache instance = new InternCache();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 35 */   private final Object lock = new Object();
/*    */   
/* 37 */   private InternCache() { super(180, 0.8F, 4); }
/*    */   
/*    */   public String intern(String input) {
/* 40 */     String result = (String)get(input);
/* 41 */     if (result != null) { return result;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 48 */     if (size() >= 180)
/*    */     {
/*    */ 
/*    */ 
/*    */ 
/* 53 */       synchronized (this.lock) {
/* 54 */         if (size() >= 180) {
/* 55 */           clear();
/*    */         }
/*    */       }
/*    */     }
/* 59 */     result = input.intern();
/* 60 */     put(result, result);
/* 61 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\util\InternCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */