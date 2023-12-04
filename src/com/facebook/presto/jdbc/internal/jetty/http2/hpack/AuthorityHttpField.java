/*    */ package com.facebook.presto.jdbc.internal.jetty.http2.hpack;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HostPortHttpField;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
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
/*    */ public class AuthorityHttpField
/*    */   extends HostPortHttpField
/*    */ {
/* 31 */   public static final String AUTHORITY = HpackContext.STATIC_TABLE[1][0];
/*    */   
/*    */   public AuthorityHttpField(String authority)
/*    */   {
/* 35 */     super(HttpHeader.C_AUTHORITY, AUTHORITY, authority);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 41 */     return String.format("%s(preparsed h=%s p=%d)", new Object[] { super.toString(), getHost(), Integer.valueOf(getPort()) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\hpack\AuthorityHttpField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */