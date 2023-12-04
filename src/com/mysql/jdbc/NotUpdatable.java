/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
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
/*    */ public class NotUpdatable
/*    */   extends SQLException
/*    */ {
/*    */   private static final long serialVersionUID = 8084742846039782258L;
/* 41 */   public static final String NOT_UPDATEABLE_MESSAGE = Messages.getString("NotUpdatable.0") + Messages.getString("NotUpdatable.1") + Messages.getString("NotUpdatable.2") + Messages.getString("NotUpdatable.3") + Messages.getString("NotUpdatable.4") + Messages.getString("NotUpdatable.5");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public NotUpdatable()
/*    */   {
/* 49 */     this(NOT_UPDATEABLE_MESSAGE);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public NotUpdatable(String reason)
/*    */   {
/* 57 */     super(reason + Messages.getString("NotUpdatable.1") + Messages.getString("NotUpdatable.2") + Messages.getString("NotUpdatable.3") + Messages.getString("NotUpdatable.4") + Messages.getString("NotUpdatable.5"), "S1000");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NotUpdatable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */