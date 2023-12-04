/*     */ package com.facebook.presto.jdbc.internal.spi;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*     */ import com.facebook.presto.jdbc.internal.airlift.slice.Slices;
/*     */ import com.facebook.presto.jdbc.internal.spi.block.Block;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.BigintType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.BooleanType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.DateType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.DoubleType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.IntegerType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TimestampType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TimestampWithTimeZoneType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeSignature;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.VarbinaryType;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.VarcharType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class InMemoryRecordSet
/*     */   implements RecordSet
/*     */ {
/*     */   private final List<Type> types;
/*     */   private final Iterable<? extends List<?>> records;
/*     */   private final long totalBytes;
/*     */   
/*     */   public InMemoryRecordSet(Collection<? extends Type> types, Collection<? extends List<?>> records)
/*     */   {
/*  47 */     this.types = Collections.unmodifiableList(new ArrayList(types));
/*  48 */     this.records = records;
/*     */     
/*  50 */     long totalBytes = 0L;
/*  51 */     for (List<?> record : records) {
/*  52 */       totalBytes += sizeOf(record);
/*     */     }
/*  54 */     this.totalBytes = totalBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<Type> getColumnTypes()
/*     */   {
/*  60 */     return this.types;
/*     */   }
/*     */   
/*     */ 
/*     */   public RecordCursor cursor()
/*     */   {
/*  66 */     return new InMemoryRecordCursor(this.types, this.records.iterator(), this.totalBytes, null);
/*     */   }
/*     */   
/*     */   private static class InMemoryRecordCursor
/*     */     implements RecordCursor
/*     */   {
/*     */     private final List<Type> types;
/*     */     private final Iterator<? extends List<?>> records;
/*     */     private final long totalBytes;
/*     */     private List<?> record;
/*     */     private long completedBytes;
/*     */     
/*     */     private InMemoryRecordCursor(List<Type> types, Iterator<? extends List<?>> records, long totalBytes)
/*     */     {
/*  80 */       this.types = types;
/*     */       
/*  82 */       this.records = records;
/*     */       
/*  84 */       this.totalBytes = totalBytes;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getTotalBytes()
/*     */     {
/*  90 */       return this.totalBytes;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getCompletedBytes()
/*     */     {
/*  96 */       return this.completedBytes;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getReadTimeNanos()
/*     */     {
/* 102 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/*     */     public Type getType(int field)
/*     */     {
/* 108 */       return (Type)this.types.get(field);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean advanceNextPosition()
/*     */     {
/* 114 */       if (!this.records.hasNext()) {
/* 115 */         this.record = null;
/* 116 */         return false;
/*     */       }
/* 118 */       this.record = ((List)this.records.next());
/* 119 */       this.completedBytes += InMemoryRecordSet.sizeOf(this.record);
/*     */       
/* 121 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getBoolean(int field)
/*     */     {
/* 127 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 128 */       InMemoryRecordSet.checkNotNull(this.record.get(field), "value is null");
/* 129 */       return ((Boolean)this.record.get(field)).booleanValue();
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLong(int field)
/*     */     {
/* 135 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 136 */       InMemoryRecordSet.checkNotNull(this.record.get(field), "value is null");
/* 137 */       return ((Number)this.record.get(field)).longValue();
/*     */     }
/*     */     
/*     */ 
/*     */     public double getDouble(int field)
/*     */     {
/* 143 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 144 */       InMemoryRecordSet.checkNotNull(this.record.get(field), "value is null");
/* 145 */       return ((Double)this.record.get(field)).doubleValue();
/*     */     }
/*     */     
/*     */ 
/*     */     public Slice getSlice(int field)
/*     */     {
/* 151 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 152 */       Object value = this.record.get(field);
/* 153 */       InMemoryRecordSet.checkNotNull(value, "value is null");
/* 154 */       if ((value instanceof byte[])) {
/* 155 */         return Slices.wrappedBuffer((byte[])value);
/*     */       }
/* 157 */       if ((value instanceof String)) {
/* 158 */         return Slices.utf8Slice((String)value);
/*     */       }
/* 160 */       if ((value instanceof Slice)) {
/* 161 */         return (Slice)value;
/*     */       }
/* 163 */       throw new IllegalArgumentException("Field " + field + " is not a String, but is a " + value.getClass().getName());
/*     */     }
/*     */     
/*     */ 
/*     */     public Object getObject(int field)
/*     */     {
/* 169 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 170 */       Object value = this.record.get(field);
/* 171 */       InMemoryRecordSet.checkNotNull(value, "value is null");
/* 172 */       return value;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isNull(int field)
/*     */     {
/* 178 */       InMemoryRecordSet.checkState(this.record != null, "no current record");
/* 179 */       return this.record.get(field) == null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close() {}
/*     */   }
/*     */   
/*     */ 
/*     */   public static Builder builder(ConnectorTableMetadata tableMetadata)
/*     */   {
/* 190 */     return builder(tableMetadata.getColumns());
/*     */   }
/*     */   
/*     */   public static Builder builder(List<ColumnMetadata> columns)
/*     */   {
/* 195 */     List<Type> columnTypes = new ArrayList();
/* 196 */     for (ColumnMetadata column : columns) {
/* 197 */       columnTypes.add(column.getType());
/*     */     }
/* 199 */     return builder(columnTypes);
/*     */   }
/*     */   
/*     */   public static Builder builder(Collection<Type> columnsTypes)
/*     */   {
/* 204 */     return new Builder(columnsTypes, null);
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private final List<Type> types;
/* 210 */     private final List<List<Object>> records = new ArrayList();
/*     */     
/*     */     private Builder(Collection<Type> types)
/*     */     {
/* 214 */       InMemoryRecordSet.checkNotNull(types, "types is null");
/* 215 */       this.types = Collections.unmodifiableList(new ArrayList(types));
/* 216 */       InMemoryRecordSet.checkArgument(!this.types.isEmpty(), "types is empty", new Object[0]);
/*     */     }
/*     */     
/*     */     public Builder addRow(Object... values)
/*     */     {
/* 221 */       InMemoryRecordSet.checkNotNull(values, "values is null");
/* 222 */       InMemoryRecordSet.checkArgument(values.length == this.types.size(), "Expected %s values in row, but got %s values", new Object[] { Integer.valueOf(this.types.size()), Integer.valueOf(values.length) });
/* 223 */       for (int i = 0; i < values.length; i++) {
/* 224 */         Object value = values[i];
/* 225 */         if (value != null)
/*     */         {
/*     */ 
/*     */ 
/* 229 */           Type type = (Type)this.types.get(i);
/* 230 */           if (BooleanType.BOOLEAN.equals(type)) {
/* 231 */             InMemoryRecordSet.checkArgument(value instanceof Boolean, "Expected value %d to be an instance of Boolean, but is a %s", new Object[] { Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 233 */           else if (IntegerType.INTEGER.equals(type)) {
/* 234 */             InMemoryRecordSet.checkArgument(value instanceof Integer, "Expected value %d to be an instance of Integer, but is a %s", new Object[] { Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 236 */           else if ((BigintType.BIGINT.equals(type)) || (DateType.DATE.equals(type)) || (TimestampType.TIMESTAMP.equals(type)) || (TimestampWithTimeZoneType.TIMESTAMP_WITH_TIME_ZONE.equals(type))) {
/* 237 */             InMemoryRecordSet.checkArgument(((value instanceof Integer)) || ((value instanceof Long)), "Expected value %d to be an instance of Integer or Long, but is a %s", new Object[] {
/* 238 */               Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 240 */           else if (DoubleType.DOUBLE.equals(type)) {
/* 241 */             InMemoryRecordSet.checkArgument(value instanceof Double, "Expected value %d to be an instance of Double, but is a %s", new Object[] { Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 243 */           else if (VarcharType.VARCHAR.equals(type)) {
/* 244 */             InMemoryRecordSet.checkArgument(((value instanceof String)) || ((value instanceof byte[])), "Expected value %d to be an instance of String or byte[], but is a %s", new Object[] {
/* 245 */               Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 247 */           else if (VarbinaryType.VARBINARY.equals(type)) {
/* 248 */             InMemoryRecordSet.checkArgument(value instanceof Slice, "Expected value %d to be an instance of Slice, but is a %s", new Object[] {
/* 249 */               Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/* 251 */           else if (type.getTypeSignature().getBase().equals("array")) {
/* 252 */             InMemoryRecordSet.checkArgument(value instanceof Block, "Expected value %d to be an instance of Block, but is a %s", new Object[] {
/* 253 */               Integer.valueOf(i), value.getClass().getSimpleName() });
/*     */           }
/*     */           else {
/* 256 */             throw new IllegalStateException("Unsupported column type " + this.types.get(i));
/*     */           }
/*     */         }
/*     */       }
/* 260 */       this.records.add(Collections.unmodifiableList(new ArrayList(Arrays.asList(values))));
/* 261 */       return this;
/*     */     }
/*     */     
/*     */     public InMemoryRecordSet build()
/*     */     {
/* 266 */       return new InMemoryRecordSet(this.types, this.records);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkArgument(boolean test, String message, Object... args)
/*     */   {
/* 272 */     if (!test) {
/* 273 */       throw new IllegalArgumentException(String.format(message, args));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkNotNull(Object value, String message)
/*     */   {
/* 279 */     if (value == null) {
/* 280 */       throw new NullPointerException(message);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkState(boolean test, String message)
/*     */   {
/* 286 */     if (!test) {
/* 287 */       throw new IllegalStateException(message);
/*     */     }
/*     */   }
/*     */   
/*     */   private static long sizeOf(List<?> record)
/*     */   {
/* 293 */     long completedBytes = 0L;
/* 294 */     for (Object value : record) {
/* 295 */       if (value != null)
/*     */       {
/*     */ 
/* 298 */         if ((value instanceof Boolean)) {
/* 299 */           completedBytes += 1L;
/*     */         }
/* 301 */         else if ((value instanceof Number)) {
/* 302 */           completedBytes += 8L;
/*     */         }
/* 304 */         else if ((value instanceof String)) {
/* 305 */           completedBytes += ((String)value).length();
/*     */         }
/* 307 */         else if ((value instanceof byte[])) {
/* 308 */           completedBytes += ((byte[])value).length;
/*     */         }
/* 310 */         else if ((value instanceof Block)) {
/* 311 */           completedBytes += ((Block)value).getSizeInBytes();
/*     */         }
/* 313 */         else if ((value instanceof Slice)) {
/* 314 */           completedBytes += ((Slice)value).getBytes().length;
/*     */         }
/*     */         else
/* 317 */           throw new IllegalArgumentException("Unknown type: " + value.getClass());
/*     */       }
/*     */     }
/* 320 */     return completedBytes;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\InMemoryRecordSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */