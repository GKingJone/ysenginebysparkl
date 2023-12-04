package com.facebook.presto.jdbc.internal.spi.security;

import com.facebook.presto.jdbc.internal.spi.SchemaTableName;

@Deprecated
public abstract interface ConnectorAccessControl
{
  public abstract void checkCanCreateSchema(Identity paramIdentity, String paramString);
  
  public abstract void checkCanDropSchema(Identity paramIdentity, String paramString);
  
  public abstract void checkCanRenameSchema(Identity paramIdentity, String paramString1, String paramString2);
  
  public abstract void checkCanCreateTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanDropTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanRenameTable(Identity paramIdentity, SchemaTableName paramSchemaTableName1, SchemaTableName paramSchemaTableName2);
  
  public abstract void checkCanAddColumn(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanRenameColumn(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanSelectFromTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanInsertIntoTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanDeleteFromTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanCreateView(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanDropView(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanSelectFromView(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanCreateViewWithSelectFromTable(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanCreateViewWithSelectFromView(Identity paramIdentity, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanSetCatalogSessionProperty(Identity paramIdentity, String paramString);
  
  public abstract void checkCanGrantTablePrivilege(Identity paramIdentity, Privilege paramPrivilege, SchemaTableName paramSchemaTableName);
  
  public abstract void checkCanRevokeTablePrivilege(Identity paramIdentity, Privilege paramPrivilege, SchemaTableName paramSchemaTableName);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\security\ConnectorAccessControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */