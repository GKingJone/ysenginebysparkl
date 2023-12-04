/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.security.Privilege;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.OptionalLong;
/*     */ import java.util.Set;
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
/*     */ @Deprecated
/*     */ public abstract interface ConnectorMetadata
/*     */ {
/*     */   public abstract List<String> listSchemaNames(ConnectorSession paramConnectorSession);
/*     */   
/*     */   public abstract ConnectorTableHandle getTableHandle(ConnectorSession paramConnectorSession, SchemaTableName paramSchemaTableName);
/*     */   
/*     */   public List<ConnectorTableLayoutResult> getTableLayouts(ConnectorSession session, ConnectorTableHandle table, Constraint<ColumnHandle> constraint, Optional<Set<ColumnHandle>> desiredColumns)
/*     */   {
/*  55 */     throw new UnsupportedOperationException("not yet implemented");
/*     */   }
/*     */   
/*     */   public ConnectorTableLayout getTableLayout(ConnectorSession session, ConnectorTableLayoutHandle handle)
/*     */   {
/*  60 */     throw new UnsupportedOperationException("not yet implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ConnectorTableMetadata getTableMetadata(ConnectorSession paramConnectorSession, ConnectorTableHandle paramConnectorTableHandle);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Optional<Object> getInfo(ConnectorTableLayoutHandle layoutHandle)
/*     */   {
/*  77 */     return Optional.empty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract List<SchemaTableName> listTables(ConnectorSession paramConnectorSession, String paramString);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnHandle getSampleWeightColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canCreateSampledTables(ConnectorSession session)
/*     */   {
/* 100 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Map<String, ColumnHandle> getColumnHandles(ConnectorSession paramConnectorSession, ConnectorTableHandle paramConnectorTableHandle);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract ColumnMetadata getColumnMetadata(ConnectorSession paramConnectorSession, ConnectorTableHandle paramConnectorTableHandle, ColumnHandle paramColumnHandle);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession paramConnectorSession, SchemaTablePrefix paramSchemaTablePrefix);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createTable(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/* 127 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support creating tables");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dropTable(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 137 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support dropping tables");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void renameTable(ConnectorSession session, ConnectorTableHandle tableHandle, SchemaTableName newTableName)
/*     */   {
/* 145 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support renaming tables");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnMetadata column)
/*     */   {
/* 153 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support adding columns");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void renameColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle source, String target)
/*     */   {
/* 161 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support renaming columns");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorOutputTableHandle beginCreateTable(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/* 169 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support creating tables with data");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void commitCreateTable(ConnectorSession session, ConnectorOutputTableHandle tableHandle, Collection<Slice> fragments)
/*     */   {
/* 177 */     throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, "ConnectorMetadata beginCreateTable() is implemented without commitCreateTable()");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void rollbackCreateTable(ConnectorSession session, ConnectorOutputTableHandle tableHandle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorInsertTableHandle beginInsert(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 190 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support inserts");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void commitInsert(ConnectorSession session, ConnectorInsertTableHandle insertHandle, Collection<Slice> fragments)
/*     */   {
/* 198 */     throw new PrestoException(StandardErrorCode.GENERIC_INTERNAL_ERROR, "ConnectorMetadata beginInsert() is implemented without commitInsert()");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void rollbackInsert(ConnectorSession session, ConnectorInsertTableHandle insertHandle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnHandle getUpdateRowIdColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 213 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support updates or deletes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConnectorTableHandle beginDelete(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 221 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support deletes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void commitDelete(ConnectorSession session, ConnectorTableHandle tableHandle, Collection<Slice> fragments)
/*     */   {
/* 231 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support deletes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void rollbackDelete(ConnectorSession session, ConnectorTableHandle tableHandle) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createView(ConnectorSession session, SchemaTableName viewName, String viewData, boolean replace)
/*     */   {
/* 244 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support creating views");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dropView(ConnectorSession session, SchemaTableName viewName)
/*     */   {
/* 252 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support dropping views");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<SchemaTableName> listViews(ConnectorSession session, String schemaNameOrNull)
/*     */   {
/* 260 */     return Collections.emptyList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<SchemaTableName, ConnectorViewDefinition> getViews(ConnectorSession session, SchemaTablePrefix prefix)
/*     */   {
/* 268 */     return Collections.emptyMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean supportsMetadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 276 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support deletes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OptionalLong metadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 286 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support deletes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void grantTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 294 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support grants");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void revokeTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 302 */     throw new PrestoException(StandardErrorCode.NOT_SUPPORTED, "This connector does not support revokes");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */