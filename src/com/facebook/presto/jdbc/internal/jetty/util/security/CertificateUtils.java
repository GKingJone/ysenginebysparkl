/*    */ package com.facebook.presto.jdbc.internal.jetty.util.security;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.resource.Resource;
/*    */ import java.io.InputStream;
/*    */ import java.security.KeyStore;
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
/*    */ public class CertificateUtils
/*    */ {
/*    */   public static KeyStore getKeyStore(Resource store, String storeType, String storeProvider, String storePassword)
/*    */     throws Exception
/*    */   {
/* 34 */     KeyStore keystore = null;
/*    */     
/* 36 */     if (store != null)
/*    */     {
/* 38 */       if (storeProvider != null)
/*    */       {
/* 40 */         keystore = KeyStore.getInstance(storeType, storeProvider);
/*    */       }
/*    */       else
/*    */       {
/* 44 */         keystore = KeyStore.getInstance(storeType);
/*    */       }
/*    */       
/* 47 */       if (!store.exists()) {
/* 48 */         throw new IllegalStateException("no valid keystore");
/*    */       }
/* 50 */       InputStream inStream = store.getInputStream();Throwable localThrowable3 = null;
/*    */       try {
/* 52 */         keystore.load(inStream, storePassword == null ? null : storePassword.toCharArray());
/*    */       }
/*    */       catch (Throwable localThrowable1)
/*    */       {
/* 50 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*    */       }
/*    */       finally {
/* 53 */         if (inStream != null) if (localThrowable3 != null) try { inStream.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else inStream.close();
/*    */       }
/*    */     }
/* 56 */     return keystore;
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public static java.util.Collection<? extends java.security.cert.CRL> loadCRL(String crlPath)
/*    */     throws Exception
/*    */   {
/*    */     // Byte code:
/*    */     //   0: aconst_null
/*    */     //   1: astore_1
/*    */     //   2: aload_0
/*    */     //   3: ifnull +45 -> 48
/*    */     //   6: aconst_null
/*    */     //   7: astore_2
/*    */     //   8: aload_0
/*    */     //   9: invokestatic 80	com/facebook/presto/jdbc/internal/jetty/util/resource/Resource:newResource	(Ljava/lang/String;)Lcom/facebook/presto/jdbc/internal/jetty/util/resource/Resource;
/*    */     //   12: invokevirtual 43	com/facebook/presto/jdbc/internal/jetty/util/resource/Resource:getInputStream	()Ljava/io/InputStream;
/*    */     //   15: astore_2
/*    */     //   16: ldc 82
/*    */     //   18: invokestatic 87	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
/*    */     //   21: aload_2
/*    */     //   22: invokevirtual 91	java/security/cert/CertificateFactory:generateCRLs	(Ljava/io/InputStream;)Ljava/util/Collection;
/*    */     //   25: astore_1
/*    */     //   26: aload_2
/*    */     //   27: ifnull +21 -> 48
/*    */     //   30: aload_2
/*    */     //   31: invokevirtual 60	java/io/InputStream:close	()V
/*    */     //   34: goto +14 -> 48
/*    */     //   37: astore_3
/*    */     //   38: aload_2
/*    */     //   39: ifnull +7 -> 46
/*    */     //   42: aload_2
/*    */     //   43: invokevirtual 60	java/io/InputStream:close	()V
/*    */     //   46: aload_3
/*    */     //   47: athrow
/*    */     //   48: aload_1
/*    */     //   49: areturn
/*    */     // Line number table:
/*    */     //   Java source line #62	-> byte code offset #0
/*    */     //   Java source line #64	-> byte code offset #2
/*    */     //   Java source line #66	-> byte code offset #6
/*    */     //   Java source line #69	-> byte code offset #8
/*    */     //   Java source line #70	-> byte code offset #16
/*    */     //   Java source line #74	-> byte code offset #26
/*    */     //   Java source line #76	-> byte code offset #30
/*    */     //   Java source line #74	-> byte code offset #37
/*    */     //   Java source line #76	-> byte code offset #42
/*    */     //   Java source line #81	-> byte code offset #48
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   0	50	0	crlPath	String
/*    */     //   1	48	1	crlList	java.util.Collection<? extends java.security.cert.CRL>
/*    */     //   7	36	2	in	InputStream
/*    */     //   37	10	3	localObject	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   8	26	37	finally
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\security\CertificateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */