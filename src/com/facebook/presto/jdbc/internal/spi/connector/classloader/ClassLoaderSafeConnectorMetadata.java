/*     */ package com.facebook.presto.jdbc.internal.spi.connector.classloader;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ColumnMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorNewTableLayout;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
/*     */ import com.facebook.presto.jdbc.internal.spi.ConnectorResolvedIndex;
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
/*     */ import com.facebook.presto.jdbc.internal.spi.classloader.ThreadContextClassLoader;
/*     */ import com.facebook.presto.jdbc.internal.spi.connector.ConnectorMetadata;
/*     */ import com.facebook.presto.jdbc.internal.spi.predicate.TupleDomain;
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
/*  55 */     this.delegate = ((ConnectorMetadata)Objects.requireNonNull(delegate, "delegate is null"));
/*  56 */     this.classLoader = ((ClassLoader)Objects.requireNonNull(classLoader, "classLoader is null"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<ConnectorTableLayoutResult> getTableLayouts(ConnectorSession session, ConnectorTableHandle table, Constraint<ColumnHandle> constraint, Optional<Set<ColumnHandle>> desiredColumns)
/*     */   {
/*  66 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  67 */     try { return this.delegate.getTableLayouts(session, table, constraint, desiredColumns);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  66 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  68 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableLayout getTableLayout(ConnectorSession session, ConnectorTableLayoutHandle handle)
/*     */   {
/*  74 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  75 */     try { return this.delegate.getTableLayout(session, handle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  74 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  76 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Optional<ConnectorNewTableLayout> getNewTableLayout(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/*  82 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  83 */     try { return this.delegate.getNewTableLayout(session, tableMetadata);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  82 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  84 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Optional<ConnectorNewTableLayout> getInsertLayout(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/*  90 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  91 */     try { return this.delegate.getInsertLayout(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  90 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/*  92 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean schemaExists(ConnectorSession session, String schemaName)
/*     */   {
/*  98 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/*  99 */     try { return this.delegate.schemaExists(session, schemaName);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/*  98 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 100 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> listSchemaNames(ConnectorSession session)
/*     */   {
/* 106 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 107 */     try { return this.delegate.listSchemaNames(session);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 106 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 108 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableHandle getTableHandle(ConnectorSession session, SchemaTableName tableName)
/*     */   {
/* 114 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 115 */     try { return this.delegate.getTableHandle(session, tableName);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 114 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 116 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle table)
/*     */   {
/* 122 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 123 */     try { return this.delegate.getTableMetadata(session, table);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 122 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 124 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Optional<Object> getInfo(ConnectorTableLayoutHandle table)
/*     */   {
/* 130 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 131 */     try { return this.delegate.getInfo(table);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 130 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 132 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<SchemaTableName> listTables(ConnectorSession session, String schemaNameOrNull)
/*     */   {
/* 138 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 139 */     try { return this.delegate.listTables(session, schemaNameOrNull);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 138 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 140 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnHandle getSampleWeightColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 146 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 147 */     try { return this.delegate.getSampleWeightColumnHandle(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 146 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 148 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean canCreateSampledTables(ConnectorSession session)
/*     */   {
/* 154 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 155 */     try { return this.delegate.canCreateSampledTables(session);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 154 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 156 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 162 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 163 */     try { return this.delegate.getColumnHandles(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 162 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 164 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
/*     */   {
/* 170 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 171 */     try { return this.delegate.getColumnMetadata(session, tableHandle, columnHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 170 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 172 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
/*     */   {
/* 178 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 179 */     try { return this.delegate.listTableColumns(session, prefix);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 178 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 180 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnMetadata column)
/*     */   {
/* 186 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 187 */     try { this.delegate.addColumn(session, tableHandle, column);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 186 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 188 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void createSchema(ConnectorSession session, String schemaName, Map<String, Object> properties)
/*     */   {
/* 194 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 195 */     try { this.delegate.createSchema(session, schemaName, properties);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 194 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 196 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dropSchema(ConnectorSession session, String schemaName)
/*     */   {
/* 202 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 203 */     try { this.delegate.dropSchema(session, schemaName);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 202 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 204 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void renameSchema(ConnectorSession session, String source, String target)
/*     */   {
/* 210 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 211 */     try { this.delegate.renameSchema(session, source, target);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 210 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 212 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void createTable(ConnectorSession session, ConnectorTableMetadata tableMetadata)
/*     */   {
/* 218 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 219 */     try { this.delegate.createTable(session, tableMetadata);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 218 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 220 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dropTable(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 226 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 227 */     try { this.delegate.dropTable(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 226 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 228 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void renameColumn(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle source, String target)
/*     */   {
/* 234 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 235 */     try { this.delegate.renameColumn(session, tableHandle, source, target);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 234 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 236 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void renameTable(ConnectorSession session, ConnectorTableHandle tableHandle, SchemaTableName newTableName)
/*     */   {
/* 242 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 243 */     try { this.delegate.renameTable(session, tableHandle, newTableName);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 242 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 244 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorOutputTableHandle beginCreateTable(ConnectorSession session, ConnectorTableMetadata tableMetadata, Optional<ConnectorNewTableLayout> layout)
/*     */   {
/* 250 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 251 */     try { return this.delegate.beginCreateTable(session, tableMetadata, layout);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 250 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 252 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void finishCreateTable(ConnectorSession session, ConnectorOutputTableHandle tableHandle, Collection<Slice> fragments)
/*     */   {
/* 258 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 259 */     try { this.delegate.finishCreateTable(session, tableHandle, fragments);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 258 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 260 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorInsertTableHandle beginInsert(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 266 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 267 */     try { return this.delegate.beginInsert(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 266 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 268 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void finishInsert(ConnectorSession session, ConnectorInsertTableHandle insertHandle, Collection<Slice> fragments)
/*     */   {
/* 274 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 275 */     try { this.delegate.finishInsert(session, insertHandle, fragments);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 274 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 276 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void createView(ConnectorSession session, SchemaTableName viewName, String viewData, boolean replace)
/*     */   {
/* 282 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 283 */     try { this.delegate.createView(session, viewName, viewData, replace);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 282 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 284 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dropView(ConnectorSession session, SchemaTableName viewName)
/*     */   {
/* 290 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 291 */     try { this.delegate.dropView(session, viewName);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 290 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 292 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<SchemaTableName> listViews(ConnectorSession session, String schemaNameOrNull)
/*     */   {
/* 298 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 299 */     try { return this.delegate.listViews(session, schemaNameOrNull);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 298 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 300 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<SchemaTableName, ConnectorViewDefinition> getViews(ConnectorSession session, SchemaTablePrefix prefix)
/*     */   {
/* 306 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 307 */     try { return this.delegate.getViews(session, prefix);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 306 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 308 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ColumnHandle getUpdateRowIdColumnHandle(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 314 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 315 */     try { return this.delegate.getUpdateRowIdColumnHandle(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 314 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 316 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public ConnectorTableHandle beginDelete(ConnectorSession session, ConnectorTableHandle tableHandle)
/*     */   {
/* 322 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 323 */     try { return this.delegate.beginDelete(session, tableHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 322 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 324 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void finishDelete(ConnectorSession session, ConnectorTableHandle tableHandle, Collection<Slice> fragments)
/*     */   {
/* 330 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 331 */     try { this.delegate.finishDelete(session, tableHandle, fragments);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 330 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 332 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean supportsMetadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 338 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 339 */     try { return this.delegate.supportsMetadataDelete(session, tableHandle, tableLayoutHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 338 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 340 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public OptionalLong metadataDelete(ConnectorSession session, ConnectorTableHandle tableHandle, ConnectorTableLayoutHandle tableLayoutHandle)
/*     */   {
/* 346 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 347 */     try { return this.delegate.metadataDelete(session, tableHandle, tableLayoutHandle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 346 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 348 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public Optional<ConnectorResolvedIndex> resolveIndex(ConnectorSession session, ConnectorTableHandle tableHandle, Set<ColumnHandle> indexableColumns, Set<ColumnHandle> outputColumns, TupleDomain<ColumnHandle> tupleDomain)
/*     */   {
/* 354 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 355 */     try { return this.delegate.resolveIndex(session, tableHandle, indexableColumns, outputColumns, tupleDomain);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 354 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     } finally {
/* 356 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void grantTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 362 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 363 */     try { this.delegate.grantTablePrivileges(session, tableName, privileges, grantee, grantOption);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 362 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 364 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public void revokeTablePrivileges(ConnectorSession session, SchemaTableName tableName, Set<Privilege> privileges, String grantee, boolean grantOption)
/*     */   {
/* 370 */     ThreadContextClassLoader ignored = new ThreadContextClassLoader(this.classLoader);Throwable localThrowable3 = null;
/* 371 */     try { this.delegate.revokeTablePrivileges(session, tableName, privileges, grantee, grantOption);
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 370 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */     } finally {
/* 372 */       if (ignored != null) if (localThrowable3 != null) try { ignored.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else ignored.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\classloader\ClassLoaderSafeConnectorMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */