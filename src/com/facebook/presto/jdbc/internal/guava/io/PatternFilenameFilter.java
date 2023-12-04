/*    */ package com.facebook.presto.jdbc.internal.guava.io;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.io.File;
/*    */ import java.io.FilenameFilter;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.annotation.Nullable;
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
/*    */ public final class PatternFilenameFilter
/*    */   implements FilenameFilter
/*    */ {
/*    */   private final Pattern pattern;
/*    */   
/*    */   public PatternFilenameFilter(String patternStr)
/*    */   {
/* 48 */     this(Pattern.compile(patternStr));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PatternFilenameFilter(Pattern pattern)
/*    */   {
/* 56 */     this.pattern = ((Pattern)Preconditions.checkNotNull(pattern));
/*    */   }
/*    */   
/*    */   public boolean accept(@Nullable File dir, String fileName) {
/* 60 */     return this.pattern.matcher(fileName).matches();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\io\PatternFilenameFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */