/*    */ package com.facebook.presto.jdbc.internal.spi.transaction;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*    */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
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
/*    */ public enum IsolationLevel
/*    */ {
/* 23 */   SERIALIZABLE, 
/* 24 */   REPEATABLE_READ, 
/* 25 */   READ_COMMITTED, 
/* 26 */   READ_UNCOMMITTED;
/*    */   
/*    */   private IsolationLevel() {}
/*    */   
/* 30 */   public boolean meetsRequirementOf(IsolationLevel requirement) { switch (this) {
/*    */     case READ_UNCOMMITTED: 
/* 32 */       return requirement == READ_UNCOMMITTED;
/*    */     
/*    */     case READ_COMMITTED: 
/* 35 */       return (requirement == READ_UNCOMMITTED) || (requirement == READ_COMMITTED);
/*    */     
/*    */ 
/*    */     case REPEATABLE_READ: 
/* 39 */       return (requirement == READ_UNCOMMITTED) || (requirement == READ_COMMITTED) || (requirement == REPEATABLE_READ);
/*    */     
/*    */ 
/*    */ 
/*    */     case SERIALIZABLE: 
/* 44 */       return true;
/*    */     }
/*    */     
/* 47 */     throw new AssertionError("Unhandled isolation level: " + this);
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 53 */     return name().replace('_', ' ');
/*    */   }
/*    */   
/*    */   public static void checkConnectorSupports(IsolationLevel supportedLevel, IsolationLevel requestedLevel)
/*    */   {
/* 58 */     if (!supportedLevel.meetsRequirementOf(requestedLevel)) {
/* 59 */       throw new PrestoException(StandardErrorCode.UNSUPPORTED_ISOLATION_LEVEL, String.format("Connector supported isolation level %s does not meet requested isolation level %s", new Object[] { supportedLevel, requestedLevel }));
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\transaction\IsolationLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */