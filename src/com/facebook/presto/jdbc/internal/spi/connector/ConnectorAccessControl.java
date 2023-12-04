/*     */ package com.facebook.presto.jdbc.internal.spi.connector;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.SchemaTableName;
/*     */ import com.facebook.presto.jdbc.internal.spi.security.AccessDeniedException;
/*     */ import com.facebook.presto.jdbc.internal.spi.security.Identity;
/*     */ import com.facebook.presto.jdbc.internal.spi.security.Privilege;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface ConnectorAccessControl
/*     */ {
/*     */   public void checkCanCreateSchema(ConnectorTransactionHandle transactionHandle, Identity identity, String schemaName)
/*     */   {
/*  48 */     AccessDeniedException.denyCreateSchema(schemaName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropSchema(ConnectorTransactionHandle transactionHandle, Identity identity, String schemaName)
/*     */   {
/*  58 */     AccessDeniedException.denyDropSchema(schemaName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameSchema(ConnectorTransactionHandle transactionHandle, Identity identity, String schemaName, String newSchemaName)
/*     */   {
/*  68 */     AccessDeniedException.denyRenameSchema(schemaName, newSchemaName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/*  78 */     AccessDeniedException.denyCreateTable(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/*  88 */     AccessDeniedException.denyDropTable(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName, SchemaTableName newTableName)
/*     */   {
/*  98 */     AccessDeniedException.denyRenameTable(tableName.toString(), newTableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanAddColumn(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 108 */     AccessDeniedException.denyAddColumn(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameColumn(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 118 */     AccessDeniedException.denyRenameColumn(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSelectFromTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 128 */     AccessDeniedException.denySelectTable(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanInsertIntoTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 138 */     AccessDeniedException.denyInsertTable(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDeleteFromTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 148 */     AccessDeniedException.denyDeleteTable(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateView(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName viewName)
/*     */   {
/* 158 */     AccessDeniedException.denyCreateView(viewName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropView(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName viewName)
/*     */   {
/* 168 */     AccessDeniedException.denyDropView(viewName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSelectFromView(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName viewName)
/*     */   {
/* 178 */     AccessDeniedException.denySelectView(viewName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateViewWithSelectFromTable(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName tableName)
/*     */   {
/* 188 */     AccessDeniedException.denyCreateViewWithSelect(tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateViewWithSelectFromView(ConnectorTransactionHandle transactionHandle, Identity identity, SchemaTableName viewName)
/*     */   {
/* 198 */     AccessDeniedException.denyCreateViewWithSelect(viewName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSetCatalogSessionProperty(Identity identity, String propertyName)
/*     */   {
/* 208 */     AccessDeniedException.denySetCatalogSessionProperty(propertyName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanGrantTablePrivilege(ConnectorTransactionHandle transactionHandle, Identity identity, Privilege privilege, SchemaTableName tableName)
/*     */   {
/* 218 */     AccessDeniedException.denyGrantTablePrivilege(privilege.toString(), tableName.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRevokeTablePrivilege(ConnectorTransactionHandle transactionHandle, Identity identity, Privilege privilege, SchemaTableName tableName)
/*     */   {
/* 228 */     AccessDeniedException.denyRevokeTablePrivilege(privilege.toString(), tableName.toString());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorAccessControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */