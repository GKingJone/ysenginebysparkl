/*    */ package com.facebook.presto.jdbc.internal.jetty.util.preventers;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractLeakPreventer
/*    */   extends AbstractLifeCycle
/*    */ {
/* 40 */   protected static final Logger LOG = Log.getLogger(AbstractLeakPreventer.class);
/*    */   
/*    */   public abstract void prevent(ClassLoader paramClassLoader);
/*    */   
/*    */   /* Error */
/*    */   protected void doStart()
/*    */     throws Exception
/*    */   {
/*    */     // Byte code:
/*    */     //   0: invokestatic 24	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*    */     //   3: invokevirtual 28	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
/*    */     //   6: astore_1
/*    */     //   7: invokestatic 24	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*    */     //   10: aload_0
/*    */     //   11: invokevirtual 34	java/lang/Object:getClass	()Ljava/lang/Class;
/*    */     //   14: invokevirtual 39	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
/*    */     //   17: invokevirtual 42	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
/*    */     //   20: aload_0
/*    */     //   21: aload_0
/*    */     //   22: invokevirtual 34	java/lang/Object:getClass	()Ljava/lang/Class;
/*    */     //   25: invokevirtual 39	java/lang/Class:getClassLoader	()Ljava/lang/ClassLoader;
/*    */     //   28: invokevirtual 44	com/facebook/presto/jdbc/internal/jetty/util/preventers/AbstractLeakPreventer:prevent	(Ljava/lang/ClassLoader;)V
/*    */     //   31: aload_0
/*    */     //   32: invokespecial 46	com/facebook/presto/jdbc/internal/jetty/util/component/AbstractLifeCycle:doStart	()V
/*    */     //   35: invokestatic 24	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*    */     //   38: aload_1
/*    */     //   39: invokevirtual 42	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
/*    */     //   42: goto +13 -> 55
/*    */     //   45: astore_2
/*    */     //   46: invokestatic 24	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*    */     //   49: aload_1
/*    */     //   50: invokevirtual 42	java/lang/Thread:setContextClassLoader	(Ljava/lang/ClassLoader;)V
/*    */     //   53: aload_2
/*    */     //   54: athrow
/*    */     //   55: return
/*    */     // Line number table:
/*    */     //   Java source line #50	-> byte code offset #0
/*    */     //   Java source line #53	-> byte code offset #7
/*    */     //   Java source line #54	-> byte code offset #20
/*    */     //   Java source line #55	-> byte code offset #31
/*    */     //   Java source line #59	-> byte code offset #35
/*    */     //   Java source line #60	-> byte code offset #42
/*    */     //   Java source line #59	-> byte code offset #45
/*    */     //   Java source line #61	-> byte code offset #55
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	56	0	this	AbstractLeakPreventer
/*    */     //   6	44	1	loader	ClassLoader
/*    */     //   45	9	2	localObject	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   7	35	45	finally
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\preventers\AbstractLeakPreventer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */