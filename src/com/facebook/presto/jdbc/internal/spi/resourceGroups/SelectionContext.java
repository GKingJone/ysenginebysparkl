/*    */ package com.facebook.presto.jdbc.internal.spi.resourceGroups;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
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
/*    */ public final class SelectionContext
/*    */ {
/*    */   private final boolean authenticated;
/*    */   private final String user;
/*    */   private final Optional<String> source;
/*    */   private final int queryPriority;
/*    */   
/*    */   public SelectionContext(boolean authenticated, String user, Optional<String> source, int queryPriority)
/*    */   {
/* 29 */     this.authenticated = authenticated;
/* 30 */     this.user = ((String)Objects.requireNonNull(user, "user is null"));
/* 31 */     this.source = ((Optional)Objects.requireNonNull(source, "source is null"));
/* 32 */     this.queryPriority = queryPriority;
/*    */   }
/*    */   
/*    */   public boolean isAuthenticated()
/*    */   {
/* 37 */     return this.authenticated;
/*    */   }
/*    */   
/*    */   public String getUser()
/*    */   {
/* 42 */     return this.user;
/*    */   }
/*    */   
/*    */   public Optional<String> getSource()
/*    */   {
/* 47 */     return this.source;
/*    */   }
/*    */   
/*    */   public int getQueryPriority()
/*    */   {
/* 52 */     return this.queryPriority;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\resourceGroups\SelectionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */