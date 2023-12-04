/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ public final class MysqlDefs
/*     */ {
/*     */   static final int COM_BINLOG_DUMP = 18;
/*     */   static final int COM_CHANGE_USER = 17;
/*     */   static final int COM_CLOSE_STATEMENT = 25;
/*     */   static final int COM_CONNECT_OUT = 20;
/*     */   static final int COM_END = 29;
/*     */   static final int COM_EXECUTE = 23;
/*     */   static final int COM_FETCH = 28;
/*     */   static final int COM_LONG_DATA = 24;
/*     */   static final int COM_PREPARE = 22;
/*     */   static final int COM_REGISTER_SLAVE = 21;
/*     */   static final int COM_RESET_STMT = 26;
/*     */   static final int COM_SET_OPTION = 27;
/*     */   static final int COM_TABLE_DUMP = 19;
/*     */   static final int CONNECT = 11;
/*     */   static final int CREATE_DB = 5;
/*     */   static final int DEBUG = 13;
/*     */   static final int DELAYED_INSERT = 16;
/*     */   static final int DROP_DB = 6;
/*     */   static final int FIELD_LIST = 4;
/*     */   static final int FIELD_TYPE_BIT = 16;
/*     */   public static final int FIELD_TYPE_BLOB = 252;
/*     */   static final int FIELD_TYPE_DATE = 10;
/*     */   static final int FIELD_TYPE_DATETIME = 12;
/*     */   static final int FIELD_TYPE_DECIMAL = 0;
/*     */   static final int FIELD_TYPE_DOUBLE = 5;
/*     */   static final int FIELD_TYPE_ENUM = 247;
/*     */   static final int FIELD_TYPE_FLOAT = 4;
/*     */   static final int FIELD_TYPE_GEOMETRY = 255;
/*     */   static final int FIELD_TYPE_INT24 = 9;
/*     */   static final int FIELD_TYPE_LONG = 3;
/*     */   static final int FIELD_TYPE_LONG_BLOB = 251;
/*     */   static final int FIELD_TYPE_LONGLONG = 8;
/*     */   static final int FIELD_TYPE_MEDIUM_BLOB = 250;
/*     */   static final int FIELD_TYPE_NEW_DECIMAL = 246;
/*     */   static final int FIELD_TYPE_NEWDATE = 14;
/*     */   static final int FIELD_TYPE_NULL = 6;
/*     */   static final int FIELD_TYPE_SET = 248;
/*     */   static final int FIELD_TYPE_SHORT = 2;
/*     */   static final int FIELD_TYPE_STRING = 254;
/*     */   static final int FIELD_TYPE_TIME = 11;
/*     */   static final int FIELD_TYPE_TIMESTAMP = 7;
/*     */   static final int FIELD_TYPE_TINY = 1;
/*     */   static final int FIELD_TYPE_TINY_BLOB = 249;
/*     */   static final int FIELD_TYPE_VAR_STRING = 253;
/*     */   static final int FIELD_TYPE_VARCHAR = 15;
/*     */   static final int FIELD_TYPE_YEAR = 13;
/*     */   static final int FIELD_TYPE_JSON = 245;
/*     */   static final int INIT_DB = 2;
/*     */   static final long LENGTH_BLOB = 65535L;
/*     */   static final long LENGTH_LONGBLOB = 4294967295L;
/*     */   static final long LENGTH_MEDIUMBLOB = 16777215L;
/*     */   static final long LENGTH_TINYBLOB = 255L;
/*     */   static final int MAX_ROWS = 50000000;
/*     */   public static final int NO_CHARSET_INFO = -1;
/*     */   static final byte OPEN_CURSOR_FLAG = 1;
/*     */   static final int PING = 14;
/*     */   static final int PROCESS_INFO = 10;
/*     */   static final int PROCESS_KILL = 12;
/*     */   static final int QUERY = 3;
/*     */   static final int QUIT = 1;
/*     */   static final int RELOAD = 7;
/*     */   static final int SHUTDOWN = 8;
/*     */   static final int SLEEP = 0;
/*     */   static final int STATISTICS = 9;
/*     */   static final int TIME = 15;
/*     */   
/*     */   static int mysqlToJavaType(int mysqlType)
/*     */   {
/*     */     int jdbcType;
/* 182 */     switch (mysqlType) {
/*     */     case 0: 
/*     */     case 246: 
/* 185 */       jdbcType = 3;
/*     */       
/* 187 */       break;
/*     */     
/*     */     case 1: 
/* 190 */       jdbcType = -6;
/*     */       
/* 192 */       break;
/*     */     
/*     */     case 2: 
/* 195 */       jdbcType = 5;
/*     */       
/* 197 */       break;
/*     */     
/*     */     case 3: 
/* 200 */       jdbcType = 4;
/*     */       
/* 202 */       break;
/*     */     
/*     */     case 4: 
/* 205 */       jdbcType = 7;
/*     */       
/* 207 */       break;
/*     */     
/*     */     case 5: 
/* 210 */       jdbcType = 8;
/*     */       
/* 212 */       break;
/*     */     
/*     */     case 6: 
/* 215 */       jdbcType = 0;
/*     */       
/* 217 */       break;
/*     */     
/*     */     case 7: 
/* 220 */       jdbcType = 93;
/*     */       
/* 222 */       break;
/*     */     
/*     */     case 8: 
/* 225 */       jdbcType = -5;
/*     */       
/* 227 */       break;
/*     */     
/*     */     case 9: 
/* 230 */       jdbcType = 4;
/*     */       
/* 232 */       break;
/*     */     
/*     */     case 10: 
/* 235 */       jdbcType = 91;
/*     */       
/* 237 */       break;
/*     */     
/*     */     case 11: 
/* 240 */       jdbcType = 92;
/*     */       
/* 242 */       break;
/*     */     
/*     */     case 12: 
/* 245 */       jdbcType = 93;
/*     */       
/* 247 */       break;
/*     */     
/*     */     case 13: 
/* 250 */       jdbcType = 91;
/*     */       
/* 252 */       break;
/*     */     
/*     */     case 14: 
/* 255 */       jdbcType = 91;
/*     */       
/* 257 */       break;
/*     */     
/*     */     case 247: 
/* 260 */       jdbcType = 1;
/*     */       
/* 262 */       break;
/*     */     
/*     */     case 248: 
/* 265 */       jdbcType = 1;
/*     */       
/* 267 */       break;
/*     */     
/*     */     case 249: 
/* 270 */       jdbcType = -3;
/*     */       
/* 272 */       break;
/*     */     
/*     */     case 250: 
/* 275 */       jdbcType = -4;
/*     */       
/* 277 */       break;
/*     */     
/*     */     case 251: 
/* 280 */       jdbcType = -4;
/*     */       
/* 282 */       break;
/*     */     
/*     */     case 252: 
/* 285 */       jdbcType = -4;
/*     */       
/* 287 */       break;
/*     */     
/*     */     case 15: 
/*     */     case 253: 
/* 291 */       jdbcType = 12;
/*     */       
/* 293 */       break;
/*     */     
/*     */     case 245: 
/*     */     case 254: 
/* 297 */       jdbcType = 1;
/*     */       
/* 299 */       break;
/*     */     case 255: 
/* 301 */       jdbcType = -2;
/*     */       
/* 303 */       break;
/*     */     case 16: 
/* 305 */       jdbcType = -7;
/*     */       
/* 307 */       break;
/*     */     default: 
/* 309 */       jdbcType = 12;
/*     */     }
/*     */     
/* 312 */     return jdbcType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static int mysqlToJavaType(String mysqlType)
/*     */   {
/* 319 */     if (mysqlType.equalsIgnoreCase("BIT"))
/* 320 */       return mysqlToJavaType(16);
/* 321 */     if (mysqlType.equalsIgnoreCase("TINYINT"))
/* 322 */       return mysqlToJavaType(1);
/* 323 */     if (mysqlType.equalsIgnoreCase("SMALLINT"))
/* 324 */       return mysqlToJavaType(2);
/* 325 */     if (mysqlType.equalsIgnoreCase("MEDIUMINT"))
/* 326 */       return mysqlToJavaType(9);
/* 327 */     if ((mysqlType.equalsIgnoreCase("INT")) || (mysqlType.equalsIgnoreCase("INTEGER")))
/* 328 */       return mysqlToJavaType(3);
/* 329 */     if (mysqlType.equalsIgnoreCase("BIGINT"))
/* 330 */       return mysqlToJavaType(8);
/* 331 */     if (mysqlType.equalsIgnoreCase("INT24"))
/* 332 */       return mysqlToJavaType(9);
/* 333 */     if (mysqlType.equalsIgnoreCase("REAL"))
/* 334 */       return mysqlToJavaType(5);
/* 335 */     if (mysqlType.equalsIgnoreCase("FLOAT"))
/* 336 */       return mysqlToJavaType(4);
/* 337 */     if (mysqlType.equalsIgnoreCase("DECIMAL"))
/* 338 */       return mysqlToJavaType(0);
/* 339 */     if (mysqlType.equalsIgnoreCase("NUMERIC"))
/* 340 */       return mysqlToJavaType(0);
/* 341 */     if (mysqlType.equalsIgnoreCase("DOUBLE"))
/* 342 */       return mysqlToJavaType(5);
/* 343 */     if (mysqlType.equalsIgnoreCase("CHAR"))
/* 344 */       return mysqlToJavaType(254);
/* 345 */     if (mysqlType.equalsIgnoreCase("VARCHAR"))
/* 346 */       return mysqlToJavaType(253);
/* 347 */     if (mysqlType.equalsIgnoreCase("DATE"))
/* 348 */       return mysqlToJavaType(10);
/* 349 */     if (mysqlType.equalsIgnoreCase("TIME"))
/* 350 */       return mysqlToJavaType(11);
/* 351 */     if (mysqlType.equalsIgnoreCase("YEAR"))
/* 352 */       return mysqlToJavaType(13);
/* 353 */     if (mysqlType.equalsIgnoreCase("TIMESTAMP"))
/* 354 */       return mysqlToJavaType(7);
/* 355 */     if (mysqlType.equalsIgnoreCase("DATETIME"))
/* 356 */       return mysqlToJavaType(12);
/* 357 */     if (mysqlType.equalsIgnoreCase("TINYBLOB"))
/* 358 */       return -2;
/* 359 */     if (mysqlType.equalsIgnoreCase("BLOB"))
/* 360 */       return -4;
/* 361 */     if (mysqlType.equalsIgnoreCase("MEDIUMBLOB"))
/* 362 */       return -4;
/* 363 */     if (mysqlType.equalsIgnoreCase("LONGBLOB"))
/* 364 */       return -4;
/* 365 */     if (mysqlType.equalsIgnoreCase("TINYTEXT"))
/* 366 */       return 12;
/* 367 */     if (mysqlType.equalsIgnoreCase("TEXT"))
/* 368 */       return -1;
/* 369 */     if (mysqlType.equalsIgnoreCase("MEDIUMTEXT"))
/* 370 */       return -1;
/* 371 */     if (mysqlType.equalsIgnoreCase("LONGTEXT"))
/* 372 */       return -1;
/* 373 */     if (mysqlType.equalsIgnoreCase("ENUM"))
/* 374 */       return mysqlToJavaType(247);
/* 375 */     if (mysqlType.equalsIgnoreCase("SET"))
/* 376 */       return mysqlToJavaType(248);
/* 377 */     if (mysqlType.equalsIgnoreCase("GEOMETRY"))
/* 378 */       return mysqlToJavaType(255);
/* 379 */     if (mysqlType.equalsIgnoreCase("BINARY"))
/* 380 */       return -2;
/* 381 */     if (mysqlType.equalsIgnoreCase("VARBINARY"))
/* 382 */       return -3;
/* 383 */     if (mysqlType.equalsIgnoreCase("BIT"))
/* 384 */       return mysqlToJavaType(16);
/* 385 */     if (mysqlType.equalsIgnoreCase("JSON")) {
/* 386 */       return mysqlToJavaType(245);
/*     */     }
/*     */     
/*     */ 
/* 390 */     return 1111;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String typeToName(int mysqlType)
/*     */   {
/* 397 */     switch (mysqlType) {
/*     */     case 0: 
/* 399 */       return "FIELD_TYPE_DECIMAL";
/*     */     
/*     */     case 1: 
/* 402 */       return "FIELD_TYPE_TINY";
/*     */     
/*     */     case 2: 
/* 405 */       return "FIELD_TYPE_SHORT";
/*     */     
/*     */     case 3: 
/* 408 */       return "FIELD_TYPE_LONG";
/*     */     
/*     */     case 4: 
/* 411 */       return "FIELD_TYPE_FLOAT";
/*     */     
/*     */     case 5: 
/* 414 */       return "FIELD_TYPE_DOUBLE";
/*     */     
/*     */     case 6: 
/* 417 */       return "FIELD_TYPE_NULL";
/*     */     
/*     */     case 7: 
/* 420 */       return "FIELD_TYPE_TIMESTAMP";
/*     */     
/*     */     case 8: 
/* 423 */       return "FIELD_TYPE_LONGLONG";
/*     */     
/*     */     case 9: 
/* 426 */       return "FIELD_TYPE_INT24";
/*     */     
/*     */     case 10: 
/* 429 */       return "FIELD_TYPE_DATE";
/*     */     
/*     */     case 11: 
/* 432 */       return "FIELD_TYPE_TIME";
/*     */     
/*     */     case 12: 
/* 435 */       return "FIELD_TYPE_DATETIME";
/*     */     
/*     */     case 13: 
/* 438 */       return "FIELD_TYPE_YEAR";
/*     */     
/*     */     case 14: 
/* 441 */       return "FIELD_TYPE_NEWDATE";
/*     */     
/*     */     case 247: 
/* 444 */       return "FIELD_TYPE_ENUM";
/*     */     
/*     */     case 248: 
/* 447 */       return "FIELD_TYPE_SET";
/*     */     
/*     */     case 249: 
/* 450 */       return "FIELD_TYPE_TINY_BLOB";
/*     */     
/*     */     case 250: 
/* 453 */       return "FIELD_TYPE_MEDIUM_BLOB";
/*     */     
/*     */     case 251: 
/* 456 */       return "FIELD_TYPE_LONG_BLOB";
/*     */     
/*     */     case 252: 
/* 459 */       return "FIELD_TYPE_BLOB";
/*     */     
/*     */     case 253: 
/* 462 */       return "FIELD_TYPE_VAR_STRING";
/*     */     
/*     */     case 254: 
/* 465 */       return "FIELD_TYPE_STRING";
/*     */     
/*     */     case 15: 
/* 468 */       return "FIELD_TYPE_VARCHAR";
/*     */     
/*     */     case 255: 
/* 471 */       return "FIELD_TYPE_GEOMETRY";
/*     */     
/*     */     case 245: 
/* 474 */       return "FIELD_TYPE_JSON";
/*     */     }
/*     */     
/* 477 */     return " Unknown MySQL Type # " + mysqlType;
/*     */   }
/*     */   
/*     */ 
/* 481 */   private static Map<String, Integer> mysqlToJdbcTypesMap = new HashMap();
/*     */   
/*     */   static {
/* 484 */     mysqlToJdbcTypesMap.put("BIT", Integer.valueOf(mysqlToJavaType(16)));
/*     */     
/* 486 */     mysqlToJdbcTypesMap.put("TINYINT", Integer.valueOf(mysqlToJavaType(1)));
/* 487 */     mysqlToJdbcTypesMap.put("SMALLINT", Integer.valueOf(mysqlToJavaType(2)));
/* 488 */     mysqlToJdbcTypesMap.put("MEDIUMINT", Integer.valueOf(mysqlToJavaType(9)));
/* 489 */     mysqlToJdbcTypesMap.put("INT", Integer.valueOf(mysqlToJavaType(3)));
/* 490 */     mysqlToJdbcTypesMap.put("INTEGER", Integer.valueOf(mysqlToJavaType(3)));
/* 491 */     mysqlToJdbcTypesMap.put("BIGINT", Integer.valueOf(mysqlToJavaType(8)));
/* 492 */     mysqlToJdbcTypesMap.put("INT24", Integer.valueOf(mysqlToJavaType(9)));
/* 493 */     mysqlToJdbcTypesMap.put("REAL", Integer.valueOf(mysqlToJavaType(5)));
/* 494 */     mysqlToJdbcTypesMap.put("FLOAT", Integer.valueOf(mysqlToJavaType(4)));
/* 495 */     mysqlToJdbcTypesMap.put("DECIMAL", Integer.valueOf(mysqlToJavaType(0)));
/* 496 */     mysqlToJdbcTypesMap.put("NUMERIC", Integer.valueOf(mysqlToJavaType(0)));
/* 497 */     mysqlToJdbcTypesMap.put("DOUBLE", Integer.valueOf(mysqlToJavaType(5)));
/* 498 */     mysqlToJdbcTypesMap.put("CHAR", Integer.valueOf(mysqlToJavaType(254)));
/* 499 */     mysqlToJdbcTypesMap.put("VARCHAR", Integer.valueOf(mysqlToJavaType(253)));
/* 500 */     mysqlToJdbcTypesMap.put("DATE", Integer.valueOf(mysqlToJavaType(10)));
/* 501 */     mysqlToJdbcTypesMap.put("TIME", Integer.valueOf(mysqlToJavaType(11)));
/* 502 */     mysqlToJdbcTypesMap.put("YEAR", Integer.valueOf(mysqlToJavaType(13)));
/* 503 */     mysqlToJdbcTypesMap.put("TIMESTAMP", Integer.valueOf(mysqlToJavaType(7)));
/* 504 */     mysqlToJdbcTypesMap.put("DATETIME", Integer.valueOf(mysqlToJavaType(12)));
/* 505 */     mysqlToJdbcTypesMap.put("TINYBLOB", Integer.valueOf(-2));
/* 506 */     mysqlToJdbcTypesMap.put("BLOB", Integer.valueOf(-4));
/* 507 */     mysqlToJdbcTypesMap.put("MEDIUMBLOB", Integer.valueOf(-4));
/* 508 */     mysqlToJdbcTypesMap.put("LONGBLOB", Integer.valueOf(-4));
/* 509 */     mysqlToJdbcTypesMap.put("TINYTEXT", Integer.valueOf(12));
/* 510 */     mysqlToJdbcTypesMap.put("TEXT", Integer.valueOf(-1));
/* 511 */     mysqlToJdbcTypesMap.put("MEDIUMTEXT", Integer.valueOf(-1));
/* 512 */     mysqlToJdbcTypesMap.put("LONGTEXT", Integer.valueOf(-1));
/* 513 */     mysqlToJdbcTypesMap.put("ENUM", Integer.valueOf(mysqlToJavaType(247)));
/* 514 */     mysqlToJdbcTypesMap.put("SET", Integer.valueOf(mysqlToJavaType(248)));
/* 515 */     mysqlToJdbcTypesMap.put("GEOMETRY", Integer.valueOf(mysqlToJavaType(255)));
/* 516 */     mysqlToJdbcTypesMap.put("JSON", Integer.valueOf(mysqlToJavaType(245)));
/*     */   }
/*     */   
/*     */   static final void appendJdbcTypeMappingQuery(StringBuilder buf, String mysqlTypeColumnName)
/*     */   {
/* 521 */     buf.append("CASE ");
/* 522 */     Map<String, Integer> typesMap = new HashMap();
/* 523 */     typesMap.putAll(mysqlToJdbcTypesMap);
/* 524 */     typesMap.put("BINARY", Integer.valueOf(-2));
/* 525 */     typesMap.put("VARBINARY", Integer.valueOf(-3));
/*     */     
/* 527 */     Iterator<String> mysqlTypes = typesMap.keySet().iterator();
/*     */     
/* 529 */     while (mysqlTypes.hasNext()) {
/* 530 */       String mysqlTypeName = (String)mysqlTypes.next();
/* 531 */       buf.append(" WHEN ");
/* 532 */       buf.append(mysqlTypeColumnName);
/* 533 */       buf.append("='");
/* 534 */       buf.append(mysqlTypeName);
/* 535 */       buf.append("' THEN ");
/* 536 */       buf.append(typesMap.get(mysqlTypeName));
/*     */       
/* 538 */       if ((mysqlTypeName.equalsIgnoreCase("DOUBLE")) || (mysqlTypeName.equalsIgnoreCase("FLOAT")) || (mysqlTypeName.equalsIgnoreCase("DECIMAL")) || (mysqlTypeName.equalsIgnoreCase("NUMERIC")))
/*     */       {
/* 540 */         buf.append(" WHEN ");
/* 541 */         buf.append(mysqlTypeColumnName);
/* 542 */         buf.append("='");
/* 543 */         buf.append(mysqlTypeName);
/* 544 */         buf.append(" unsigned' THEN ");
/* 545 */         buf.append(typesMap.get(mysqlTypeName));
/*     */       }
/*     */     }
/*     */     
/* 549 */     buf.append(" ELSE ");
/* 550 */     buf.append(1111);
/* 551 */     buf.append(" END ");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MysqlDefs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */