/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.annotations.GwtIncompatible;
/*    */ import java.nio.charset.Charset;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ public final class Charsets
/*    */ {
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 45 */   public static final Charset US_ASCII = Charset.forName("US-ASCII");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 51 */   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 56 */   public static final Charset UTF_8 = Charset.forName("UTF-8");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 62 */   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 68 */   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 75 */   public static final Charset UTF_16 = Charset.forName("UTF-16");
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\base\Charsets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */