/*     */ package com.facebook.presto.jdbc.internal.spi.classloader;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ColumnMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayout;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayoutHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableLayoutResult;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorTableMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorViewDefinition;
/*     */ import com.facebook.presto.jdbc.internal.spi.Constraint;
/*     */ import com.facebook.presto.jdbc.internal.spi.SchemaTableName;
/*     */ import com.facebook.presto.jdbc.internal.spi.SchemaTablePrefix;
/*     */ import com.facebook.presto.jdbc.internal.spi.security.Privilege;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class ClassLoaderSafeConnectorMetadata
/*     */   implements ConnectorMetadata
/*     */ {
/*     */   private final ConnectorMetadata delegate;
/*     */   private final ClassLoader classLoader;
/*     */   
/*     */   public ClassLoaderSafeConnectorMetadata(ConnectorMetadata delegate, ClassLoader classLoader)
/*     */   {
/*  51 */     this.delegate = ((ConnectorMetadata)Objects.requireNonNull(delegate, "delegate is null"));
/*  52 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<ConnectorTableLayoutResult> getTableLayouts(ConnectorSession session, ConnectorTableHandle table, Constraint<ColumnHandle> constraint, Optional<Set<ColumnHandle>> desiredColumns)
/*     */   {
/*  62 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  63 */     try { return this.delegate.getTableLayouts(session, table, constraint, desiredColumns);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  62 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  64 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableLayout getTableLayout(ConnectorSession session, ConnectorTableLayoutHandle handle)
/*     */   {
/*  70 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  71 */     try { return this.delegate.getTableLayout(session, handle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  70 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  72 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> listSchemaNames(ConnectorSession session)
/*     */   {
/*  78 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  79 */     try { return this.delegate.listSchemaNames(session);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  78 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  80 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableHandle getTableHandle(ConnectorSession session, SchemaTableName tableName)
/*     */   {
/*  86 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  87 */     try { return this.delegate.getTableHandle(session, tableName);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  86 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  88 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle table)
/*     */   {
/*  94 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  95 */     try { return this.delegate.getTableMetadata(session, table);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  94 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  96 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Optional<Object> getInfo(ConnectorTableLayoutHandle table)
/*     */   {
/* 102 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 103 */     try { return this.delegate.getInfo(table);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 102 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 104 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<SchemaTableName> listTables(ConnectorSession session, String schemaNameOrNull)
/*     */   {
/* 110 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 111 */     try { return this.delegate.listTables(session, schemaNameOrNull);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 110 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 112 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnHandle getSampleWeightColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 118 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 119 */     try { return this.delegate.getSampleWeightColumnHandle(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 118 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 120 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean canCreateSampledTables(ConnectorSession session)
/*     */   {
/* 126 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 127 */     try { return this.delegate.canCreateSampledTables(session);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 126 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 128 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 134 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 135 */     try { return this.delegate.getColumnHandles(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 134 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 136 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
/*     */   {
/* 142 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 143 */     try { return this.delegate.getColumnMetadata(session, tableHandle, columnHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 142 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 144 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
/*     */   {
/* 150 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 151 */     try { return this.delegate.listTableColumns(session, prefix);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 150 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 152 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnMetadata column)
/*     */   {
/* 158 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 159 */     try { this.delegate.addColumn(session, tableHandle, column);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 158 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 160 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void createTable(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/* 166 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 167 */     try { this.delegate.createTable(session, tableMetadata);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 166 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 168 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dropTable(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 174 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 175 */     try { this.delegate.dropTable(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 174 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 176 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void renameColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle source, String target)
/*     */   {
/* 182 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 183 */     try { this.delegate.renameColumn(session, tableHandle, source, target);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 182 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 184 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void renameTable(ConnectorSession session, ConnectorTableHandle tableHandle, SchemaTableName newTableName)
/*     */   {
/* 190 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 191 */     try { this.delegate.renameTable(session, tableHandle, newTableName);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 190 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 192 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorOutputTableHandle beginCreateTable(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/* 198 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 199 */     try { return this.delegate.beginCreateTable(session, tableMetadata);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 198 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 200 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void commitCreateTable(ConnectorSession session, ConnectorOutputTableHandle tableHandle, Collection<Slice> fragments)
/*     */   {
/* 206 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 207 */     try { this.delegate.commitCreateTable(session, tableHandle, fragments);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 206 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 208 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorInsertTableHandle beginInsert(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 214 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 215 */     try { return this.delegate.beginInsert(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 214 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 216 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void commitInsert(ConnectorSession session, ConnectorInsertTableHandle insertHandle, Collection<Slice> fragments)
/*     */   {
/* 222 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 223 */     try { this.delegate.commitInsert(session, insertHandle, fragments);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 222 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 224 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void createView(ConnectorSession session, SchemaTableName viewName, String viewData, boolean replace)
/*     */   {
/* 230 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 231 */     try { this.delegate.createView(session, viewName, viewData, replace);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 230 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 232 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dropView(ConnectorSession session, SchemaTableName viewName)
/*     */   {
/* 238 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 239 */     try { this.delegate.dropView(session, viewName);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 238 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 240 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<SchemaTableName> listViews(ConnectorSession session, String schemaNameOrNull)
/*     */   {
/* 246 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 247 */     try { return this.delegate.listViews(session, schemaNameOrNull);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 246 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 248 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<SchemaTableName, ConnectorViewDefinition> getViews(ConnectorSession session, SchemaTablePrefix prefix)
/*     */   {
/* 254 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 255 */     try { return this.delegate.getViews(session, prefix);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 254 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 256 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnHandle getUpdateRowIdColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 262 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 263 */     try { return this.delegate.getUpdateRowIdColumnHandle(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 262 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 264 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableHandle beginDelete(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 270 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 271 */     try { return this.delegate.beginDelete(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 270 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 272 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean supportsMetadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 278 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 279 */     try { return this.delegate.supportsMetadataDelete(session, tableHandle, tableLayoutHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 278 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 280 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public OptionalLong metadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 286 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 287 */     try { return this.delegate.metadataDelete(session, tableHandle, tableLayoutHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 286 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 288 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void grantTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 294 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 295 */     try { this.delegate.grantTablePrivileges(session, tableName, privileges, grantee, grantOption);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 294 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 296 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void revokeTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 302 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 303 */     try { this.delegate.revokeTablePrivileges(session, tableName, privileges, grantee, grantOption);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 302 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 304 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 310 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 311 */     try { return this.delegate.toString();
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 310 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 312 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\classloader\ClassLoaderSafeConnectorMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */