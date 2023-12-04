/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList.Builder;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeSignature;
/*     */ import com.facebook.presto.jdbc.internal.spi.type.TypeSignatureParameter;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ class ColumnInfo
/*     */ {
/*     */   private static final int VARCHAR_MAX = 1073741824;
/*     */   private static final int VARBINARY_MAX = 1073741824;
/*     */   private static final int TIME_ZONE_MAX = 40;
/*  30 */   private static final int TIME_MAX = "HH:mm:ss.SSS".length();
/*  31 */   private static final int TIME_WITH_TIME_ZONE_MAX = TIME_MAX + 40;
/*  32 */   private static final int TIMESTAMP_MAX = "yyyy-MM-dd HH:mm:ss.SSS".length();
/*  33 */   private static final int TIMESTAMP_WITH_TIME_ZONE_MAX = TIMESTAMP_MAX + 40;
/*  34 */   private static final int DATE_MAX = "yyyy-MM-dd".length();
/*     */   
/*     */   private final int columnType;
/*     */   private final List<Integer> columnParameterTypes;
/*     */   private final TypeSignature columnTypeSignature;
/*     */   private final Nullable nullable;
/*     */   private final boolean currency;
/*     */   private final boolean signed;
/*     */   private final int precision;
/*     */   private final int scale;
/*     */   private final int columnDisplaySize;
/*     */   private final String columnLabel;
/*     */   private final String columnName;
/*     */   private final String tableName;
/*     */   private final String schemaName;
/*     */   private final String catalogName;
/*     */   
/*     */   public static enum Nullable
/*     */   {
/*  53 */     NO_NULLS,  NULLABLE,  UNKNOWN;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Nullable() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ColumnInfo(int columnType, List<Integer> columnParameterTypes, TypeSignature columnTypeSignature, Nullable nullable, boolean currency, boolean signed, int precision, int scale, int columnDisplaySize, String columnLabel, String columnName, String tableName, String schemaName, String catalogName)
/*     */   {
/*  72 */     this.columnType = columnType;
/*  73 */     this.columnParameterTypes = ImmutableList.copyOf((Collection)Objects.requireNonNull(columnParameterTypes, "columnParameterTypes is null"));
/*  74 */     this.columnTypeSignature = ((TypeSignature)Objects.requireNonNull(columnTypeSignature, "columnTypeName is null"));
/*  75 */     this.nullable = ((Nullable)Objects.requireNonNull(nullable, "nullable is null"));
/*  76 */     this.currency = currency;
/*  77 */     this.signed = signed;
/*  78 */     this.precision = precision;
/*  79 */     this.scale = scale;
/*  80 */     this.columnDisplaySize = columnDisplaySize;
/*  81 */     this.columnLabel = ((String)Objects.requireNonNull(columnLabel, "columnLabel is null"));
/*  82 */     this.columnName = ((String)Objects.requireNonNull(columnName, "columnName is null"));
/*  83 */     this.tableName = ((String)Objects.requireNonNull(tableName, "tableName is null"));
/*  84 */     this.schemaName = ((String)Objects.requireNonNull(schemaName, "schemaName is null"));
/*  85 */     this.catalogName = ((String)Objects.requireNonNull(catalogName, "catalogName is null"));
/*     */   }
/*     */   
/*     */   public static void setTypeInfo(Builder builder, TypeSignature type)
/*     */   {
/*  90 */     builder.setColumnType(getType(type));
/*  91 */     ImmutableList.Builder<Integer> parameterTypes = ImmutableList.builder();
/*  92 */     for (Object localObject = type.getParameters().iterator(); ((Iterator)localObject).hasNext();) { parameter = (TypeSignatureParameter)((Iterator)localObject).next();
/*  93 */       parameterTypes.add(Integer.valueOf(getType(parameter)));
/*     */     }
/*  95 */     builder.setColumnParameterTypes(parameterTypes.build());
/*  96 */     localObject = type.toString();TypeSignatureParameter parameter = -1; switch (((String)localObject).hashCode()) {case 64711720:  if (((String)localObject).equals("boolean")) parameter = 0; break; case -1389167889:  if (((String)localObject).equals("bigint")) parameter = 1; break; case 1958052158:  if (((String)localObject).equals("integer")) parameter = 2; break; case -606531192:  if (((String)localObject).equals("smallint")) parameter = 3; break; case -1312398097:  if (((String)localObject).equals("tinyint")) parameter = 4; break; case 3496350:  if (((String)localObject).equals("real")) parameter = 5; break; case -1325958191:  if (((String)localObject).equals("double")) parameter = 6; break; case 236613373:  if (((String)localObject).equals("varchar")) parameter = 7; break; case -275146264:  if (((String)localObject).equals("varbinary")) parameter = 8; break; case 3560141:  if (((String)localObject).equals("time")) parameter = 9; break; case -1233260552:  if (((String)localObject).equals("time with time zone")) parameter = 10; break; case 55126294:  if (((String)localObject).equals("timestamp")) parameter = 11; break; case 792501903:  if (((String)localObject).equals("timestamp with time zone")) parameter = 12; break; case 3076014:  if (((String)localObject).equals("date")) parameter = 13; break; case -1014209277:  if (((String)localObject).equals("interval year to month")) parameter = 14; break; case -1558241766:  if (((String)localObject).equals("interval day to second")) parameter = 15; break; case 1542263633:  if (((String)localObject).equals("decimal")) parameter = 16; break; } switch (parameter) {
/*     */     case 0: 
/*  98 */       builder.setColumnDisplaySize(5);
/*  99 */       break;
/*     */     case 1: 
/* 101 */       builder.setSigned(true);
/* 102 */       builder.setPrecision(19);
/* 103 */       builder.setScale(0);
/* 104 */       builder.setColumnDisplaySize(20);
/* 105 */       break;
/*     */     case 2: 
/* 107 */       builder.setSigned(true);
/* 108 */       builder.setPrecision(10);
/* 109 */       builder.setScale(0);
/* 110 */       builder.setColumnDisplaySize(11);
/* 111 */       break;
/*     */     case 3: 
/* 113 */       builder.setSigned(true);
/* 114 */       builder.setPrecision(5);
/* 115 */       builder.setScale(0);
/* 116 */       builder.setColumnDisplaySize(6);
/* 117 */       break;
/*     */     case 4: 
/* 119 */       builder.setSigned(true);
/* 120 */       builder.setPrecision(3);
/* 121 */       builder.setScale(0);
/* 122 */       builder.setColumnDisplaySize(4);
/* 123 */       break;
/*     */     case 5: 
/* 125 */       builder.setSigned(true);
/* 126 */       builder.setPrecision(9);
/* 127 */       builder.setScale(0);
/* 128 */       builder.setColumnDisplaySize(16);
/* 129 */       break;
/*     */     case 6: 
/* 131 */       builder.setSigned(true);
/* 132 */       builder.setPrecision(17);
/* 133 */       builder.setScale(0);
/* 134 */       builder.setColumnDisplaySize(24);
/* 135 */       break;
/*     */     case 7: 
/* 137 */       builder.setSigned(true);
/* 138 */       builder.setPrecision(1073741824);
/* 139 */       builder.setScale(0);
/* 140 */       builder.setColumnDisplaySize(1073741824);
/* 141 */       break;
/*     */     case 8: 
/* 143 */       builder.setSigned(true);
/* 144 */       builder.setPrecision(1073741824);
/* 145 */       builder.setScale(0);
/* 146 */       builder.setColumnDisplaySize(1073741824);
/* 147 */       break;
/*     */     case 9: 
/* 149 */       builder.setSigned(true);
/* 150 */       builder.setPrecision(3);
/* 151 */       builder.setScale(0);
/* 152 */       builder.setColumnDisplaySize(TIME_MAX);
/* 153 */       break;
/*     */     case 10: 
/* 155 */       builder.setSigned(true);
/* 156 */       builder.setPrecision(3);
/* 157 */       builder.setScale(0);
/* 158 */       builder.setColumnDisplaySize(TIME_WITH_TIME_ZONE_MAX);
/* 159 */       break;
/*     */     case 11: 
/* 161 */       builder.setSigned(true);
/* 162 */       builder.setPrecision(3);
/* 163 */       builder.setScale(0);
/* 164 */       builder.setColumnDisplaySize(TIMESTAMP_MAX);
/* 165 */       break;
/*     */     case 12: 
/* 167 */       builder.setSigned(true);
/* 168 */       builder.setPrecision(3);
/* 169 */       builder.setScale(0);
/* 170 */       builder.setColumnDisplaySize(TIMESTAMP_WITH_TIME_ZONE_MAX);
/* 171 */       break;
/*     */     case 13: 
/* 173 */       builder.setSigned(true);
/* 174 */       builder.setScale(0);
/* 175 */       builder.setColumnDisplaySize(DATE_MAX);
/* 176 */       break;
/*     */     case 14: 
/* 178 */       builder.setColumnDisplaySize(TIMESTAMP_MAX);
/* 179 */       break;
/*     */     case 15: 
/* 181 */       builder.setColumnDisplaySize(TIMESTAMP_MAX);
/* 182 */       break;
/*     */     case 16: 
/* 184 */       builder.setSigned(true);
/* 185 */       builder.setColumnDisplaySize(((TypeSignatureParameter)type.getParameters().get(0)).getLongLiteral().intValue() + 2);
/* 186 */       builder.setPrecision(((TypeSignatureParameter)type.getParameters().get(0)).getLongLiteral().intValue());
/* 187 */       builder.setScale(((TypeSignatureParameter)type.getParameters().get(1)).getLongLiteral().intValue());
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   private static int getType(TypeSignatureParameter typeParameter)
/*     */   {
/* 194 */     switch (typeParameter.getKind()) {
/*     */     case TYPE: 
/* 196 */       return getType(typeParameter.getTypeSignature());
/*     */     }
/* 198 */     return 2000;
/*     */   }
/*     */   
/*     */ 
/*     */   private static int getType(TypeSignature type)
/*     */   {
/* 204 */     if (type.getBase().equals("array")) {
/* 205 */       return 2003;
/*     */     }
/* 207 */     switch (type.getBase()) {
/*     */     case "boolean": 
/* 209 */       return 16;
/*     */     case "bigint": 
/* 211 */       return -5;
/*     */     case "integer": 
/* 213 */       return 4;
/*     */     case "smallint": 
/* 215 */       return 5;
/*     */     case "tinyint": 
/* 217 */       return -6;
/*     */     case "real": 
/* 219 */       return 7;
/*     */     case "double": 
/* 221 */       return 8;
/*     */     case "varchar": 
/* 223 */       return -16;
/*     */     case "char": 
/* 225 */       return 1;
/*     */     case "varbinary": 
/* 227 */       return -4;
/*     */     case "time": 
/* 229 */       return 92;
/*     */     case "time with time zone": 
/* 231 */       return 92;
/*     */     case "timestamp": 
/* 233 */       return 93;
/*     */     case "timestamp with time zone": 
/* 235 */       return 93;
/*     */     case "date": 
/* 237 */       return 91;
/*     */     case "decimal": 
/* 239 */       return 3;
/*     */     }
/* 241 */     return 2000;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getColumnType()
/*     */   {
/* 247 */     return this.columnType;
/*     */   }
/*     */   
/*     */   public List<Integer> getColumnParameterTypes()
/*     */   {
/* 252 */     return this.columnParameterTypes;
/*     */   }
/*     */   
/*     */   public String getColumnTypeName()
/*     */   {
/* 257 */     return this.columnTypeSignature.toString();
/*     */   }
/*     */   
/*     */   public TypeSignature getColumnTypeSignature()
/*     */   {
/* 262 */     return this.columnTypeSignature;
/*     */   }
/*     */   
/*     */   public Nullable getNullable()
/*     */   {
/* 267 */     return this.nullable;
/*     */   }
/*     */   
/*     */   public boolean isCurrency()
/*     */   {
/* 272 */     return this.currency;
/*     */   }
/*     */   
/*     */   public boolean isSigned()
/*     */   {
/* 277 */     return this.signed;
/*     */   }
/*     */   
/*     */   public int getPrecision()
/*     */   {
/* 282 */     return this.precision;
/*     */   }
/*     */   
/*     */   public int getScale()
/*     */   {
/* 287 */     return this.scale;
/*     */   }
/*     */   
/*     */   public int getColumnDisplaySize()
/*     */   {
/* 292 */     return this.columnDisplaySize;
/*     */   }
/*     */   
/*     */   public String getColumnLabel()
/*     */   {
/* 297 */     return this.columnLabel;
/*     */   }
/*     */   
/*     */   public String getColumnName()
/*     */   {
/* 302 */     return this.columnName;
/*     */   }
/*     */   
/*     */   public String getTableName()
/*     */   {
/* 307 */     return this.tableName;
/*     */   }
/*     */   
/*     */   public String getSchemaName()
/*     */   {
/* 312 */     return this.schemaName;
/*     */   }
/*     */   
/*     */   public String getCatalogName()
/*     */   {
/* 317 */     return this.catalogName;
/*     */   }
/*     */   
/*     */   static class Builder
/*     */   {
/*     */     private int columnType;
/*     */     private List<Integer> columnParameterTypes;
/*     */     private TypeSignature columnTypeSignature;
/*     */     private Nullable nullable;
/*     */     private boolean currency;
/*     */     private boolean signed;
/*     */     private int precision;
/*     */     private int scale;
/*     */     private int columnDisplaySize;
/*     */     private String columnLabel;
/*     */     private String columnName;
/*     */     private String tableName;
/*     */     private String schemaName;
/*     */     private String catalogName;
/*     */     
/*     */     public Builder setColumnType(int columnType)
/*     */     {
/* 339 */       this.columnType = columnType;
/* 340 */       return this;
/*     */     }
/*     */     
/*     */     public void setColumnParameterTypes(List<Integer> columnParameterTypes)
/*     */     {
/* 345 */       this.columnParameterTypes = ImmutableList.copyOf((Collection)Objects.requireNonNull(columnParameterTypes, "columnParameterTypes is null"));
/*     */     }
/*     */     
/*     */     public Builder setColumnTypeSignature(TypeSignature columnTypeSignature)
/*     */     {
/* 350 */       this.columnTypeSignature = columnTypeSignature;
/* 351 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setNullable(Nullable nullable)
/*     */     {
/* 356 */       this.nullable = nullable;
/* 357 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCurrency(boolean currency)
/*     */     {
/* 362 */       this.currency = currency;
/* 363 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSigned(boolean signed)
/*     */     {
/* 368 */       this.signed = signed;
/* 369 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setPrecision(int precision)
/*     */     {
/* 374 */       this.precision = precision;
/* 375 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setScale(int scale)
/*     */     {
/* 380 */       this.scale = scale;
/* 381 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setColumnDisplaySize(int columnDisplaySize)
/*     */     {
/* 386 */       this.columnDisplaySize = columnDisplaySize;
/* 387 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setColumnLabel(String columnLabel)
/*     */     {
/* 392 */       this.columnLabel = columnLabel;
/* 393 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setColumnName(String columnName)
/*     */     {
/* 398 */       this.columnName = columnName;
/* 399 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTableName(String tableName)
/*     */     {
/* 404 */       this.tableName = tableName;
/* 405 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSchemaName(String schemaName)
/*     */     {
/* 410 */       this.schemaName = schemaName;
/* 411 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCatalogName(String catalogName)
/*     */     {
/* 416 */       this.catalogName = catalogName;
/* 417 */       return this;
/*     */     }
/*     */     
/*     */     public ColumnInfo build()
/*     */     {
/* 422 */       return new ColumnInfo(this.columnType, this.columnParameterTypes, this.columnTypeSignature, this.nullable, this.currency, this.signed, this.precision, this.scale, this.columnDisplaySize, this.columnLabel, this.columnName, this.tableName, this.schemaName, this.catalogName);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\ColumnInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */