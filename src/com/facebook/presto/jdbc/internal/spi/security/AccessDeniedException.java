/*     */ package com.facebook.presto.jdbc.internal.spi.security;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.spi.PrestoException;
/*     */ import com.facebook.presto.jdbc.internal.spi.StandardErrorCode;
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
/*     */ public class AccessDeniedException
/*     */   extends PrestoException
/*     */ {
/*     */   public AccessDeniedException(String message)
/*     */   {
/*  28 */     super(StandardErrorCode.PERMISSION_DENIED, "Access Denied: " + message);
/*     */   }
/*     */   
/*     */   public static void denySetUser(Principal principal, String userName)
/*     */   {
/*  33 */     denySetUser(principal, userName, null);
/*     */   }
/*     */   
/*     */   public static void denySetUser(Principal principal, String userName, String extraInfo)
/*     */   {
/*  38 */     throw new AccessDeniedException(String.format("Principal %s cannot become user %s%s", new Object[] { principal, userName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyCreateSchema(String schemaName)
/*     */   {
/*  43 */     denyCreateSchema(schemaName, null);
/*     */   }
/*     */   
/*     */   public static void denyCreateSchema(String schemaName, String extraInfo)
/*     */   {
/*  48 */     throw new AccessDeniedException(String.format("Cannot create schema %s%s", new Object[] { schemaName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyDropSchema(String schemaName)
/*     */   {
/*  53 */     denyDropSchema(schemaName, null);
/*     */   }
/*     */   
/*     */   public static void denyDropSchema(String schemaName, String extraInfo)
/*     */   {
/*  58 */     throw new AccessDeniedException(String.format("Cannot drop schema %s%s", new Object[] { schemaName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyRenameSchema(String schemaName, String newSchemaName)
/*     */   {
/*  63 */     denyRenameSchema(schemaName, newSchemaName, null);
/*     */   }
/*     */   
/*     */   public static void denyRenameSchema(String schemaName, String newSchemaName, String extraInfo)
/*     */   {
/*  68 */     throw new AccessDeniedException(String.format("Cannot rename schema from %s to %s%s", new Object[] { schemaName, newSchemaName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyCreateTable(String tableName)
/*     */   {
/*  73 */     denyCreateTable(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyCreateTable(String tableName, String extraInfo)
/*     */   {
/*  78 */     throw new AccessDeniedException(String.format("Cannot create table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyDropTable(String tableName)
/*     */   {
/*  83 */     denyDropTable(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyDropTable(String tableName, String extraInfo)
/*     */   {
/*  88 */     throw new AccessDeniedException(String.format("Cannot drop table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyRenameTable(String tableName, String newTableName)
/*     */   {
/*  93 */     denyRenameTable(tableName, newTableName, null);
/*     */   }
/*     */   
/*     */   public static void denyRenameTable(String tableName, String newTableName, String extraInfo)
/*     */   {
/*  98 */     throw new AccessDeniedException(String.format("Cannot rename table from %s to %s%s", new Object[] { tableName, newTableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyAddColumn(String tableName)
/*     */   {
/* 103 */     denyAddColumn(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyAddColumn(String tableName, String extraInfo)
/*     */   {
/* 108 */     throw new AccessDeniedException(String.format("Cannot add a column to table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyRenameColumn(String tableName)
/*     */   {
/* 113 */     denyRenameColumn(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyRenameColumn(String tableName, String extraInfo)
/*     */   {
/* 118 */     throw new AccessDeniedException(String.format("Cannot rename a column in table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denySelectTable(String tableName)
/*     */   {
/* 123 */     denySelectTable(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denySelectTable(String tableName, String extraInfo)
/*     */   {
/* 128 */     throw new AccessDeniedException(String.format("Cannot select from table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyInsertTable(String tableName)
/*     */   {
/* 133 */     denyInsertTable(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyInsertTable(String tableName, String extraInfo)
/*     */   {
/* 138 */     throw new AccessDeniedException(String.format("Cannot insert into table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyDeleteTable(String tableName)
/*     */   {
/* 143 */     denyDeleteTable(tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyDeleteTable(String tableName, String extraInfo)
/*     */   {
/* 148 */     throw new AccessDeniedException(String.format("Cannot delete from table %s%s", new Object[] { tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyCreateView(String viewName)
/*     */   {
/* 153 */     denyCreateView(viewName, null);
/*     */   }
/*     */   
/*     */   public static void denyCreateView(String viewName, String extraInfo)
/*     */   {
/* 158 */     throw new AccessDeniedException(String.format("Cannot create view %s%s", new Object[] { viewName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyCreateViewWithSelect(String sourceName)
/*     */   {
/* 163 */     denyCreateViewWithSelect(sourceName, null);
/*     */   }
/*     */   
/*     */   public static void denyCreateViewWithSelect(String sourceName, String extraInfo)
/*     */   {
/* 168 */     throw new AccessDeniedException(String.format("Cannot create view that selects from %s%s", new Object[] { sourceName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyDropView(String viewName)
/*     */   {
/* 173 */     denyDropView(viewName, null);
/*     */   }
/*     */   
/*     */   public static void denyDropView(String viewName, String extraInfo)
/*     */   {
/* 178 */     throw new AccessDeniedException(String.format("Cannot drop view %s%s", new Object[] { viewName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denySelectView(String viewName)
/*     */   {
/* 183 */     denySelectView(viewName, null);
/*     */   }
/*     */   
/*     */   public static void denySelectView(String viewName, String extraInfo)
/*     */   {
/* 188 */     throw new AccessDeniedException(String.format("Cannot select from view %s%s", new Object[] { viewName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyGrantTablePrivilege(String privilege, String tableName)
/*     */   {
/* 193 */     denyGrantTablePrivilege(privilege, tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyGrantTablePrivilege(String privilege, String tableName, String extraInfo)
/*     */   {
/* 198 */     throw new AccessDeniedException(String.format("Cannot grant privilege %s on table %s%s", new Object[] { privilege, tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denyRevokeTablePrivilege(String privilege, String tableName)
/*     */   {
/* 203 */     denyRevokeTablePrivilege(privilege, tableName, null);
/*     */   }
/*     */   
/*     */   public static void denyRevokeTablePrivilege(String privilege, String tableName, String extraInfo)
/*     */   {
/* 208 */     throw new AccessDeniedException(String.format("Cannot revoke privilege %s on table %s%s", new Object[] { privilege, tableName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denySetSystemSessionProperty(String propertyName)
/*     */   {
/* 213 */     denySetSystemSessionProperty(propertyName, null);
/*     */   }
/*     */   
/*     */   public static void denySetSystemSessionProperty(String propertyName, String extraInfo)
/*     */   {
/* 218 */     throw new AccessDeniedException(String.format("Cannot set system session property %s%s", new Object[] { propertyName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denySetCatalogSessionProperty(String catalogName, String propertyName)
/*     */   {
/* 223 */     denySetCatalogSessionProperty(catalogName, propertyName, null);
/*     */   }
/*     */   
/*     */   public static void denySetCatalogSessionProperty(String catalogName, String propertyName, String extraInfo)
/*     */   {
/* 228 */     throw new AccessDeniedException(String.format("Cannot set catalog session property %s.%s%s", new Object[] { catalogName, propertyName, formatExtraInfo(extraInfo) }));
/*     */   }
/*     */   
/*     */   public static void denySetCatalogSessionProperty(String propertyName)
/*     */   {
/* 233 */     throw new AccessDeniedException(String.format("Cannot set catalog session property %s", new Object[] { propertyName }));
/*     */   }
/*     */   
/*     */   private static Object formatExtraInfo(String extraInfo)
/*     */   {
/* 238 */     if ((extraInfo == null) || (extraInfo.isEmpty())) {
/* 239 */       return "";
/*     */     }
/* 241 */     return ": " + extraInfo;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\security\AccessDeniedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */