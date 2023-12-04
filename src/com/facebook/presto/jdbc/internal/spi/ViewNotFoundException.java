/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public class ViewNotFoundException
/*    */   extends NotFoundException
/*    */ {
/*    */   private final SchemaTableName viewName;
/*    */   
/*    */   public ViewNotFoundException(SchemaTableName viewName)
/*    */   {
/* 26 */     this(viewName, String.format("View '%s' not found", new Object[] { viewName }));
/*    */   }
/*    */   
/*    */   public ViewNotFoundException(SchemaTableName viewName, String message)
/*    */   {
/* 31 */     super(message);
/* 32 */     this.viewName = ((SchemaTableName)Objects.requireNonNull(viewName, "viewName is null"));
/*    */   }
/*    */   
/*    */   public ViewNotFoundException(SchemaTableName viewName, Throwable cause)
/*    */   {
/* 37 */     this(viewName, String.format("View '%s' not found", new Object[] { viewName }), cause);
/*    */   }
/*    */   
/*    */   public ViewNotFoundException(SchemaTableName viewName, String message, Throwable cause)
/*    */   {
/* 42 */     super(message, cause);
/* 43 */     this.viewName = ((SchemaTableName)Objects.requireNonNull(viewName, "viewName is null"));
/*    */   }
/*    */   
/*    */   public SchemaTableName getViewName()
/*    */   {
/* 48 */     return this.viewName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ViewNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */