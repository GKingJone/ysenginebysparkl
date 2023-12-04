/*    */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Fields;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Fields.Field;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ import java.net.URLEncoder;
/*    */ import java.nio.charset.Charset;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.nio.charset.UnsupportedCharsetException;
/*    */ import java.util.Iterator;
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
/*    */ public class FormContentProvider
/*    */   extends StringContentProvider
/*    */ {
/*    */   public FormContentProvider(Fields fields)
/*    */   {
/* 38 */     this(fields, StandardCharsets.UTF_8);
/*    */   }
/*    */   
/*    */   public FormContentProvider(Fields fields, Charset charset)
/*    */   {
/* 43 */     super("application/x-www-form-urlencoded", convert(fields, charset), charset);
/*    */   }
/*    */   
/*    */   public static String convert(Fields fields)
/*    */   {
/* 48 */     return convert(fields, StandardCharsets.UTF_8);
/*    */   }
/*    */   
/*    */ 
/*    */   public static String convert(Fields fields, Charset charset)
/*    */   {
/* 54 */     StringBuilder builder = new StringBuilder(fields.getSize() * 32);
/* 55 */     for (Iterator localIterator1 = fields.iterator(); localIterator1.hasNext();) { field = (Field)localIterator1.next();
/*    */       
/* 57 */       for (String value : field.getValues())
/*    */       {
/* 59 */         if (builder.length() > 0)
/* 60 */           builder.append("&");
/* 61 */         builder.append(encode(field.getName(), charset)).append("=").append(encode(value, charset));
/*    */       } }
/*    */     Field field;
/* 64 */     return builder.toString();
/*    */   }
/*    */   
/*    */   private static String encode(String value, Charset charset)
/*    */   {
/*    */     try
/*    */     {
/* 71 */       return URLEncoder.encode(value, charset.name());
/*    */     }
/*    */     catch (UnsupportedEncodingException x)
/*    */     {
/* 75 */       throw new UnsupportedCharsetException(charset.name());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\FormContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */