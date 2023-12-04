/*     */ package com.facebook.presto.jdbc.internal.spi.security;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.CatalogSchemaName;
/*     */ import com.facebook.presto.jdbc.internal.spi.CatalogSchemaTableName;
/*     */ import java.security.Principal;
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
/*     */ public abstract interface SystemAccessControl
/*     */ {
/*     */   public abstract void checkCanSetUser(Principal paramPrincipal, String paramString);
/*     */   
/*     */   public abstract void checkCanSetSystemSessionProperty(Identity paramIdentity, String paramString);
/*     */   
/*     */   public void checkCanCreateSchema(Identity identity, CatalogSchemaName schema)
/*     */   {
/*  63 */     AccessDeniedException.denyCreateSchema(schema.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropSchema(Identity identity, CatalogSchemaName schema)
/*     */   {
/*  73 */     AccessDeniedException.denyDropSchema(schema.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameSchema(Identity identity, CatalogSchemaName schema, String newSchemaName)
/*     */   {
/*  83 */     AccessDeniedException.denyRenameSchema(schema.toString(), newSchemaName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/*  93 */     AccessDeniedException.denyCreateTable(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 103 */     AccessDeniedException.denyDropTable(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameTable(Identity identity, CatalogSchemaTableName table, CatalogSchemaTableName newTable)
/*     */   {
/* 113 */     AccessDeniedException.denyRenameTable(table.toString(), newTable.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanAddColumn(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 123 */     AccessDeniedException.denyAddColumn(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRenameColumn(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 133 */     AccessDeniedException.denyRenameColumn(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSelectFromTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 143 */     AccessDeniedException.denySelectTable(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanInsertIntoTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 153 */     AccessDeniedException.denyInsertTable(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDeleteFromTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 163 */     AccessDeniedException.denyDeleteTable(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateView(Identity identity, CatalogSchemaTableName view)
/*     */   {
/* 173 */     AccessDeniedException.denyCreateView(view.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanDropView(Identity identity, CatalogSchemaTableName view)
/*     */   {
/* 183 */     AccessDeniedException.denyDropView(view.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSelectFromView(Identity identity, CatalogSchemaTableName view)
/*     */   {
/* 193 */     AccessDeniedException.denySelectView(view.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateViewWithSelectFromTable(Identity identity, CatalogSchemaTableName table)
/*     */   {
/* 203 */     AccessDeniedException.denyCreateViewWithSelect(table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanCreateViewWithSelectFromView(Identity identity, CatalogSchemaTableName view)
/*     */   {
/* 213 */     AccessDeniedException.denyCreateViewWithSelect(view.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanSetCatalogSessionProperty(Identity identity, String catalogName, String propertyName)
/*     */   {
/* 223 */     AccessDeniedException.denySetCatalogSessionProperty(propertyName);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanGrantTablePrivilege(Identity identity, Privilege privilege, CatalogSchemaTableName table)
/*     */   {
/* 233 */     AccessDeniedException.denyGrantTablePrivilege(privilege.toString(), table.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void checkCanRevokeTablePrivilege(Identity identity, Privilege privilege, CatalogSchemaTableName table)
/*     */   {
/* 243 */     AccessDeniedException.denyRevokeTablePrivilege(privilege.toString(), table.toString());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\security\SystemAccessControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */