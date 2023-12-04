/*    */ package com.facebook.presto.jdbc.internal.guava.html;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.escape.Escaper;
/*    */ import com.facebook.presto.jdbc.internal.guava.escape.Escapers;
/*    */ import com.facebook.presto.jdbc.internal.guava.escape.Escapers.Builder;
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
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class HtmlEscapers
/*    */ {
/*    */   public static Escaper htmlEscaper()
/*    */   {
/* 59 */     return HTML_ESCAPER;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 65 */   private static final Escaper HTML_ESCAPER = Escapers.builder().addEscape('"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").build();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\html\HtmlEscapers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */