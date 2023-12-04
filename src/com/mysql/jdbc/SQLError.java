/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.exceptions.MySQLDataException;
/*      */ import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
/*      */ import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
/*      */ import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException;
/*      */ import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransactionRollbackException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTransientConnectionException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.net.BindException;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.DataTruncation;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SQLError
/*      */ {
/*      */   static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
/*      */   private static Map<Integer, String> mysqlToSql99State;
/*      */   private static Map<Integer, String> mysqlToSqlState;
/*      */   public static final String SQL_STATE_WARNING = "01000";
/*      */   public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
/*      */   public static final String SQL_STATE_DATE_TRUNCATED = "01004";
/*      */   public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
/*      */   public static final String SQL_STATE_NO_DATA = "02000";
/*      */   public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
/*      */   public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
/*      */   public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
/*      */   public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
/*      */   public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
/*      */   public static final String SQL_STATE_CONNECTION_FAILURE = "08006";
/*      */   public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
/*      */   public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
/*      */   public static final String SQL_STATE_FEATURE_NOT_SUPPORTED = "0A000";
/*      */   public static final String SQL_STATE_CARDINALITY_VIOLATION = "21000";
/*      */   public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
/*      */   public static final String SQL_STATE_STRING_DATA_RIGHT_TRUNCATION = "22001";
/*      */   public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
/*      */   public static final String SQL_STATE_INVALID_DATETIME_FORMAT = "22007";
/*      */   public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
/*      */   public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
/*      */   public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
/*      */   public static final String SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION = "23000";
/*      */   public static final String SQL_STATE_INVALID_CURSOR_STATE = "24000";
/*      */   public static final String SQL_STATE_INVALID_TRANSACTION_STATE = "25000";
/*      */   public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
/*      */   public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
/*      */   public static final String SQL_STATE_INVALID_CONDITION_NUMBER = "35000";
/*      */   public static final String SQL_STATE_INVALID_CATALOG_NAME = "3D000";
/*      */   public static final String SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE = "40001";
/*      */   public static final String SQL_STATE_SYNTAX_ERROR = "42000";
/*      */   public static final String SQL_STATE_ER_TABLE_EXISTS_ERROR = "42S01";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
/*      */   public static final String SQL_STATE_ER_NO_SUCH_INDEX = "42S12";
/*      */   public static final String SQL_STATE_ER_DUP_FIELDNAME = "42S21";
/*      */   public static final String SQL_STATE_ER_BAD_FIELD_ERROR = "42S22";
/*      */   public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
/*      */   public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
/*      */   public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
/*      */   public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
/*      */   public static final String SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE = "0K000";
/*      */   public static final String SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER = "0Z002";
/*      */   public static final String SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT = "20000";
/*      */   public static final String SQL_STATE_NULL_VALUE_NOT_ALLOWED = "22004";
/*      */   public static final String SQL_STATE_INVALID_LOGARITHM_ARGUMENT = "2201E";
/*      */   public static final String SQL_STATE_ACTIVE_SQL_TRANSACTION = "25001";
/*      */   public static final String SQL_STATE_READ_ONLY_SQL_TRANSACTION = "25006";
/*      */   public static final String SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "2F003";
/*      */   public static final String SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT = "2F005";
/*      */   public static final String SQL_STATE_ER_QUERY_INTERRUPTED = "70100";
/*      */   public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
/*      */   public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
/*      */   public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
/*      */   public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
/*      */   public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
/*      */   public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
/*      */   public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
/*      */   public static final String SQL_STATE_GENERAL_ERROR = "S1000";
/*      */   public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
/*      */   public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
/*      */   public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
/*      */   public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
/*      */   public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
/*      */   public static final String SQL_STATE_CLI_SPECIFIC_CONDITION = "HY000";
/*      */   public static final String SQL_STATE_MEMORY_ALLOCATION_ERROR = "HY001";
/*      */   public static final String SQL_STATE_XA_RBROLLBACK = "XA100";
/*      */   public static final String SQL_STATE_XA_RBDEADLOCK = "XA102";
/*      */   public static final String SQL_STATE_XA_RBTIMEOUT = "XA106";
/*      */   public static final String SQL_STATE_XA_RMERR = "XAE03";
/*      */   public static final String SQL_STATE_XAER_NOTA = "XAE04";
/*      */   public static final String SQL_STATE_XAER_INVAL = "XAE05";
/*      */   public static final String SQL_STATE_XAER_RMFAIL = "XAE07";
/*      */   public static final String SQL_STATE_XAER_DUPID = "XAE08";
/*      */   public static final String SQL_STATE_XAER_OUTSIDE = "XAE09";
/*      */   private static Map<String, String> sqlStateMessages;
/*      */   private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
/*      */   private static final int DUE_TO_TIMEOUT_FALSE = 0;
/*      */   private static final int DUE_TO_TIMEOUT_MAYBE = 2;
/*      */   private static final int DUE_TO_TIMEOUT_TRUE = 1;
/*      */   private static final Constructor<?> JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR;
/*      */   
/*      */   static
/*      */   {
/*  146 */     if (Util.isJdbc4()) {
/*      */       try {
/*  148 */         JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = Class.forName("com.mysql.jdbc.exceptions.jdbc4.CommunicationsException").getConstructor(new Class[] { MySQLConnection.class, Long.TYPE, Long.TYPE, Exception.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*  151 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  153 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  155 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  158 */       JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR = null;
/*      */     }
/*      */     
/*  161 */     sqlStateMessages = new HashMap();
/*  162 */     sqlStateMessages.put("01002", Messages.getString("SQLError.35"));
/*  163 */     sqlStateMessages.put("01004", Messages.getString("SQLError.36"));
/*  164 */     sqlStateMessages.put("01006", Messages.getString("SQLError.37"));
/*  165 */     sqlStateMessages.put("01S00", Messages.getString("SQLError.38"));
/*  166 */     sqlStateMessages.put("01S01", Messages.getString("SQLError.39"));
/*  167 */     sqlStateMessages.put("01S03", Messages.getString("SQLError.40"));
/*  168 */     sqlStateMessages.put("01S04", Messages.getString("SQLError.41"));
/*  169 */     sqlStateMessages.put("07001", Messages.getString("SQLError.42"));
/*  170 */     sqlStateMessages.put("08001", Messages.getString("SQLError.43"));
/*  171 */     sqlStateMessages.put("08002", Messages.getString("SQLError.44"));
/*  172 */     sqlStateMessages.put("08003", Messages.getString("SQLError.45"));
/*  173 */     sqlStateMessages.put("08004", Messages.getString("SQLError.46"));
/*  174 */     sqlStateMessages.put("08007", Messages.getString("SQLError.47"));
/*  175 */     sqlStateMessages.put("08S01", Messages.getString("SQLError.48"));
/*  176 */     sqlStateMessages.put("21S01", Messages.getString("SQLError.49"));
/*  177 */     sqlStateMessages.put("22003", Messages.getString("SQLError.50"));
/*  178 */     sqlStateMessages.put("22008", Messages.getString("SQLError.51"));
/*  179 */     sqlStateMessages.put("22012", Messages.getString("SQLError.52"));
/*  180 */     sqlStateMessages.put("40001", Messages.getString("SQLError.53"));
/*  181 */     sqlStateMessages.put("28000", Messages.getString("SQLError.54"));
/*  182 */     sqlStateMessages.put("42000", Messages.getString("SQLError.55"));
/*  183 */     sqlStateMessages.put("42S02", Messages.getString("SQLError.56"));
/*  184 */     sqlStateMessages.put("S0001", Messages.getString("SQLError.57"));
/*  185 */     sqlStateMessages.put("S0002", Messages.getString("SQLError.58"));
/*  186 */     sqlStateMessages.put("S0011", Messages.getString("SQLError.59"));
/*  187 */     sqlStateMessages.put("S0012", Messages.getString("SQLError.60"));
/*  188 */     sqlStateMessages.put("S0021", Messages.getString("SQLError.61"));
/*  189 */     sqlStateMessages.put("S0022", Messages.getString("SQLError.62"));
/*  190 */     sqlStateMessages.put("S0023", Messages.getString("SQLError.63"));
/*  191 */     sqlStateMessages.put("S1000", Messages.getString("SQLError.64"));
/*  192 */     sqlStateMessages.put("S1001", Messages.getString("SQLError.65"));
/*  193 */     sqlStateMessages.put("S1002", Messages.getString("SQLError.66"));
/*  194 */     sqlStateMessages.put("S1009", Messages.getString("SQLError.67"));
/*  195 */     sqlStateMessages.put("S1C00", Messages.getString("SQLError.68"));
/*  196 */     sqlStateMessages.put("S1T00", Messages.getString("SQLError.69"));
/*      */     
/*  198 */     mysqlToSqlState = new Hashtable();
/*      */     
/*  200 */     mysqlToSqlState.put(Integer.valueOf(1249), "01000");
/*  201 */     mysqlToSqlState.put(Integer.valueOf(1261), "01000");
/*  202 */     mysqlToSqlState.put(Integer.valueOf(1262), "01000");
/*  203 */     mysqlToSqlState.put(Integer.valueOf(1265), "01000");
/*  204 */     mysqlToSqlState.put(Integer.valueOf(1311), "01000");
/*  205 */     mysqlToSqlState.put(Integer.valueOf(1642), "01000");
/*  206 */     mysqlToSqlState.put(Integer.valueOf(1040), "08004");
/*  207 */     mysqlToSqlState.put(Integer.valueOf(1251), "08004");
/*  208 */     mysqlToSqlState.put(Integer.valueOf(1042), "08004");
/*  209 */     mysqlToSqlState.put(Integer.valueOf(1043), "08004");
/*  210 */     mysqlToSqlState.put(Integer.valueOf(1129), "08004");
/*  211 */     mysqlToSqlState.put(Integer.valueOf(1130), "08004");
/*  212 */     mysqlToSqlState.put(Integer.valueOf(1047), "08S01");
/*  213 */     mysqlToSqlState.put(Integer.valueOf(1053), "08S01");
/*  214 */     mysqlToSqlState.put(Integer.valueOf(1080), "08S01");
/*  215 */     mysqlToSqlState.put(Integer.valueOf(1081), "08S01");
/*  216 */     mysqlToSqlState.put(Integer.valueOf(1152), "08S01");
/*  217 */     mysqlToSqlState.put(Integer.valueOf(1153), "08S01");
/*  218 */     mysqlToSqlState.put(Integer.valueOf(1154), "08S01");
/*  219 */     mysqlToSqlState.put(Integer.valueOf(1155), "08S01");
/*  220 */     mysqlToSqlState.put(Integer.valueOf(1156), "08S01");
/*  221 */     mysqlToSqlState.put(Integer.valueOf(1157), "08S01");
/*  222 */     mysqlToSqlState.put(Integer.valueOf(1158), "08S01");
/*  223 */     mysqlToSqlState.put(Integer.valueOf(1159), "08S01");
/*  224 */     mysqlToSqlState.put(Integer.valueOf(1160), "08S01");
/*  225 */     mysqlToSqlState.put(Integer.valueOf(1161), "08S01");
/*  226 */     mysqlToSqlState.put(Integer.valueOf(1184), "08S01");
/*  227 */     mysqlToSqlState.put(Integer.valueOf(1189), "08S01");
/*  228 */     mysqlToSqlState.put(Integer.valueOf(1190), "08S01");
/*  229 */     mysqlToSqlState.put(Integer.valueOf(1218), "08S01");
/*  230 */     mysqlToSqlState.put(Integer.valueOf(1312), "0A000");
/*  231 */     mysqlToSqlState.put(Integer.valueOf(1314), "0A000");
/*  232 */     mysqlToSqlState.put(Integer.valueOf(1335), "0A000");
/*  233 */     mysqlToSqlState.put(Integer.valueOf(1336), "0A000");
/*  234 */     mysqlToSqlState.put(Integer.valueOf(1415), "0A000");
/*  235 */     mysqlToSqlState.put(Integer.valueOf(1845), "0A000");
/*  236 */     mysqlToSqlState.put(Integer.valueOf(1846), "0A000");
/*  237 */     mysqlToSqlState.put(Integer.valueOf(1044), "42000");
/*  238 */     mysqlToSqlState.put(Integer.valueOf(1049), "42000");
/*  239 */     mysqlToSqlState.put(Integer.valueOf(1055), "S1009");
/*  240 */     mysqlToSqlState.put(Integer.valueOf(1056), "S1009");
/*  241 */     mysqlToSqlState.put(Integer.valueOf(1057), "S1009");
/*  242 */     mysqlToSqlState.put(Integer.valueOf(1059), "S1009");
/*  243 */     mysqlToSqlState.put(Integer.valueOf(1060), "S1009");
/*  244 */     mysqlToSqlState.put(Integer.valueOf(1061), "S1009");
/*  245 */     mysqlToSqlState.put(Integer.valueOf(1062), "S1009");
/*  246 */     mysqlToSqlState.put(Integer.valueOf(1063), "S1009");
/*  247 */     mysqlToSqlState.put(Integer.valueOf(1064), "42000");
/*  248 */     mysqlToSqlState.put(Integer.valueOf(1065), "42000");
/*  249 */     mysqlToSqlState.put(Integer.valueOf(1066), "S1009");
/*  250 */     mysqlToSqlState.put(Integer.valueOf(1067), "S1009");
/*  251 */     mysqlToSqlState.put(Integer.valueOf(1068), "S1009");
/*  252 */     mysqlToSqlState.put(Integer.valueOf(1069), "S1009");
/*  253 */     mysqlToSqlState.put(Integer.valueOf(1070), "S1009");
/*  254 */     mysqlToSqlState.put(Integer.valueOf(1071), "S1009");
/*  255 */     mysqlToSqlState.put(Integer.valueOf(1072), "S1009");
/*  256 */     mysqlToSqlState.put(Integer.valueOf(1073), "S1009");
/*  257 */     mysqlToSqlState.put(Integer.valueOf(1074), "S1009");
/*  258 */     mysqlToSqlState.put(Integer.valueOf(1075), "S1009");
/*  259 */     mysqlToSqlState.put(Integer.valueOf(1082), "S1009");
/*  260 */     mysqlToSqlState.put(Integer.valueOf(1083), "S1009");
/*  261 */     mysqlToSqlState.put(Integer.valueOf(1084), "S1009");
/*  262 */     mysqlToSqlState.put(Integer.valueOf(1090), "42000");
/*  263 */     mysqlToSqlState.put(Integer.valueOf(1091), "42000");
/*  264 */     mysqlToSqlState.put(Integer.valueOf(1101), "42000");
/*  265 */     mysqlToSqlState.put(Integer.valueOf(1102), "42000");
/*  266 */     mysqlToSqlState.put(Integer.valueOf(1103), "42000");
/*  267 */     mysqlToSqlState.put(Integer.valueOf(1104), "42000");
/*  268 */     mysqlToSqlState.put(Integer.valueOf(1106), "42000");
/*  269 */     mysqlToSqlState.put(Integer.valueOf(1107), "42000");
/*  270 */     mysqlToSqlState.put(Integer.valueOf(1110), "42000");
/*  271 */     mysqlToSqlState.put(Integer.valueOf(1112), "42000");
/*  272 */     mysqlToSqlState.put(Integer.valueOf(1113), "42000");
/*  273 */     mysqlToSqlState.put(Integer.valueOf(1115), "42000");
/*  274 */     mysqlToSqlState.put(Integer.valueOf(1118), "42000");
/*  275 */     mysqlToSqlState.put(Integer.valueOf(1120), "42000");
/*  276 */     mysqlToSqlState.put(Integer.valueOf(1121), "42000");
/*  277 */     mysqlToSqlState.put(Integer.valueOf(1131), "42000");
/*  278 */     mysqlToSqlState.put(Integer.valueOf(1132), "42000");
/*  279 */     mysqlToSqlState.put(Integer.valueOf(1133), "42000");
/*  280 */     mysqlToSqlState.put(Integer.valueOf(1139), "42000");
/*  281 */     mysqlToSqlState.put(Integer.valueOf(1140), "42000");
/*  282 */     mysqlToSqlState.put(Integer.valueOf(1141), "42000");
/*  283 */     mysqlToSqlState.put(Integer.valueOf(1142), "42000");
/*  284 */     mysqlToSqlState.put(Integer.valueOf(1143), "42000");
/*  285 */     mysqlToSqlState.put(Integer.valueOf(1144), "42000");
/*  286 */     mysqlToSqlState.put(Integer.valueOf(1145), "42000");
/*  287 */     mysqlToSqlState.put(Integer.valueOf(1147), "42000");
/*  288 */     mysqlToSqlState.put(Integer.valueOf(1148), "42000");
/*  289 */     mysqlToSqlState.put(Integer.valueOf(1149), "42000");
/*  290 */     mysqlToSqlState.put(Integer.valueOf(1162), "42000");
/*  291 */     mysqlToSqlState.put(Integer.valueOf(1163), "42000");
/*  292 */     mysqlToSqlState.put(Integer.valueOf(1164), "42000");
/*  293 */     mysqlToSqlState.put(Integer.valueOf(1166), "42000");
/*  294 */     mysqlToSqlState.put(Integer.valueOf(1167), "42000");
/*  295 */     mysqlToSqlState.put(Integer.valueOf(1170), "42000");
/*  296 */     mysqlToSqlState.put(Integer.valueOf(1171), "42000");
/*  297 */     mysqlToSqlState.put(Integer.valueOf(1172), "42000");
/*  298 */     mysqlToSqlState.put(Integer.valueOf(1173), "42000");
/*  299 */     mysqlToSqlState.put(Integer.valueOf(1176), "42000");
/*  300 */     mysqlToSqlState.put(Integer.valueOf(1177), "42000");
/*  301 */     mysqlToSqlState.put(Integer.valueOf(1178), "42000");
/*  302 */     mysqlToSqlState.put(Integer.valueOf(1203), "42000");
/*  303 */     mysqlToSqlState.put(Integer.valueOf(1211), "42000");
/*  304 */     mysqlToSqlState.put(Integer.valueOf(1226), "42000");
/*  305 */     mysqlToSqlState.put(Integer.valueOf(1227), "42000");
/*  306 */     mysqlToSqlState.put(Integer.valueOf(1230), "42000");
/*  307 */     mysqlToSqlState.put(Integer.valueOf(1231), "42000");
/*  308 */     mysqlToSqlState.put(Integer.valueOf(1232), "42000");
/*  309 */     mysqlToSqlState.put(Integer.valueOf(1234), "42000");
/*  310 */     mysqlToSqlState.put(Integer.valueOf(1235), "42000");
/*  311 */     mysqlToSqlState.put(Integer.valueOf(1239), "42000");
/*  312 */     mysqlToSqlState.put(Integer.valueOf(1248), "42000");
/*  313 */     mysqlToSqlState.put(Integer.valueOf(1250), "42000");
/*  314 */     mysqlToSqlState.put(Integer.valueOf(1252), "42000");
/*  315 */     mysqlToSqlState.put(Integer.valueOf(1253), "42000");
/*  316 */     mysqlToSqlState.put(Integer.valueOf(1280), "42000");
/*  317 */     mysqlToSqlState.put(Integer.valueOf(1281), "42000");
/*  318 */     mysqlToSqlState.put(Integer.valueOf(1286), "42000");
/*  319 */     mysqlToSqlState.put(Integer.valueOf(1304), "42000");
/*  320 */     mysqlToSqlState.put(Integer.valueOf(1305), "42000");
/*  321 */     mysqlToSqlState.put(Integer.valueOf(1308), "42000");
/*  322 */     mysqlToSqlState.put(Integer.valueOf(1309), "42000");
/*  323 */     mysqlToSqlState.put(Integer.valueOf(1310), "42000");
/*  324 */     mysqlToSqlState.put(Integer.valueOf(1313), "42000");
/*  325 */     mysqlToSqlState.put(Integer.valueOf(1315), "42000");
/*  326 */     mysqlToSqlState.put(Integer.valueOf(1316), "42000");
/*  327 */     mysqlToSqlState.put(Integer.valueOf(1318), "42000");
/*  328 */     mysqlToSqlState.put(Integer.valueOf(1319), "42000");
/*  329 */     mysqlToSqlState.put(Integer.valueOf(1320), "42000");
/*  330 */     mysqlToSqlState.put(Integer.valueOf(1322), "42000");
/*  331 */     mysqlToSqlState.put(Integer.valueOf(1323), "42000");
/*  332 */     mysqlToSqlState.put(Integer.valueOf(1324), "42000");
/*  333 */     mysqlToSqlState.put(Integer.valueOf(1327), "42000");
/*  334 */     mysqlToSqlState.put(Integer.valueOf(1330), "42000");
/*  335 */     mysqlToSqlState.put(Integer.valueOf(1331), "42000");
/*  336 */     mysqlToSqlState.put(Integer.valueOf(1332), "42000");
/*  337 */     mysqlToSqlState.put(Integer.valueOf(1333), "42000");
/*  338 */     mysqlToSqlState.put(Integer.valueOf(1337), "42000");
/*  339 */     mysqlToSqlState.put(Integer.valueOf(1338), "42000");
/*  340 */     mysqlToSqlState.put(Integer.valueOf(1370), "42000");
/*  341 */     mysqlToSqlState.put(Integer.valueOf(1403), "42000");
/*  342 */     mysqlToSqlState.put(Integer.valueOf(1407), "42000");
/*  343 */     mysqlToSqlState.put(Integer.valueOf(1410), "42000");
/*  344 */     mysqlToSqlState.put(Integer.valueOf(1413), "42000");
/*  345 */     mysqlToSqlState.put(Integer.valueOf(1414), "42000");
/*  346 */     mysqlToSqlState.put(Integer.valueOf(1425), "42000");
/*  347 */     mysqlToSqlState.put(Integer.valueOf(1426), "42000");
/*  348 */     mysqlToSqlState.put(Integer.valueOf(1427), "42000");
/*  349 */     mysqlToSqlState.put(Integer.valueOf(1437), "42000");
/*  350 */     mysqlToSqlState.put(Integer.valueOf(1439), "42000");
/*  351 */     mysqlToSqlState.put(Integer.valueOf(1453), "42000");
/*  352 */     mysqlToSqlState.put(Integer.valueOf(1458), "42000");
/*  353 */     mysqlToSqlState.put(Integer.valueOf(1460), "42000");
/*  354 */     mysqlToSqlState.put(Integer.valueOf(1461), "42000");
/*  355 */     mysqlToSqlState.put(Integer.valueOf(1463), "42000");
/*  356 */     mysqlToSqlState.put(Integer.valueOf(1582), "42000");
/*  357 */     mysqlToSqlState.put(Integer.valueOf(1583), "42000");
/*  358 */     mysqlToSqlState.put(Integer.valueOf(1584), "42000");
/*  359 */     mysqlToSqlState.put(Integer.valueOf(1630), "42000");
/*  360 */     mysqlToSqlState.put(Integer.valueOf(1641), "42000");
/*  361 */     mysqlToSqlState.put(Integer.valueOf(1687), "42000");
/*  362 */     mysqlToSqlState.put(Integer.valueOf(1701), "42000");
/*  363 */     mysqlToSqlState.put(Integer.valueOf(1222), "21000");
/*  364 */     mysqlToSqlState.put(Integer.valueOf(1241), "21000");
/*  365 */     mysqlToSqlState.put(Integer.valueOf(1242), "21000");
/*  366 */     mysqlToSqlState.put(Integer.valueOf(1022), "23000");
/*  367 */     mysqlToSqlState.put(Integer.valueOf(1048), "23000");
/*  368 */     mysqlToSqlState.put(Integer.valueOf(1052), "23000");
/*  369 */     mysqlToSqlState.put(Integer.valueOf(1169), "23000");
/*  370 */     mysqlToSqlState.put(Integer.valueOf(1216), "23000");
/*  371 */     mysqlToSqlState.put(Integer.valueOf(1217), "23000");
/*  372 */     mysqlToSqlState.put(Integer.valueOf(1451), "23000");
/*  373 */     mysqlToSqlState.put(Integer.valueOf(1452), "23000");
/*  374 */     mysqlToSqlState.put(Integer.valueOf(1557), "23000");
/*  375 */     mysqlToSqlState.put(Integer.valueOf(1586), "23000");
/*  376 */     mysqlToSqlState.put(Integer.valueOf(1761), "23000");
/*  377 */     mysqlToSqlState.put(Integer.valueOf(1762), "23000");
/*  378 */     mysqlToSqlState.put(Integer.valueOf(1859), "23000");
/*  379 */     mysqlToSqlState.put(Integer.valueOf(1406), "22001");
/*  380 */     mysqlToSqlState.put(Integer.valueOf(1264), "01000");
/*  381 */     mysqlToSqlState.put(Integer.valueOf(1416), "22003");
/*  382 */     mysqlToSqlState.put(Integer.valueOf(1690), "22003");
/*  383 */     mysqlToSqlState.put(Integer.valueOf(1292), "22007");
/*  384 */     mysqlToSqlState.put(Integer.valueOf(1367), "22007");
/*  385 */     mysqlToSqlState.put(Integer.valueOf(1441), "22008");
/*  386 */     mysqlToSqlState.put(Integer.valueOf(1365), "22012");
/*  387 */     mysqlToSqlState.put(Integer.valueOf(1325), "24000");
/*  388 */     mysqlToSqlState.put(Integer.valueOf(1326), "24000");
/*  389 */     mysqlToSqlState.put(Integer.valueOf(1179), "25000");
/*  390 */     mysqlToSqlState.put(Integer.valueOf(1207), "25000");
/*  391 */     mysqlToSqlState.put(Integer.valueOf(1045), "28000");
/*  392 */     mysqlToSqlState.put(Integer.valueOf(1698), "28000");
/*  393 */     mysqlToSqlState.put(Integer.valueOf(1873), "28000");
/*  394 */     mysqlToSqlState.put(Integer.valueOf(1758), "35000");
/*  395 */     mysqlToSqlState.put(Integer.valueOf(1046), "3D000");
/*  396 */     mysqlToSqlState.put(Integer.valueOf(1058), "21S01");
/*  397 */     mysqlToSqlState.put(Integer.valueOf(1136), "21S01");
/*  398 */     mysqlToSqlState.put(Integer.valueOf(1050), "42S01");
/*  399 */     mysqlToSqlState.put(Integer.valueOf(1051), "42S02");
/*  400 */     mysqlToSqlState.put(Integer.valueOf(1109), "42S02");
/*  401 */     mysqlToSqlState.put(Integer.valueOf(1146), "42S02");
/*  402 */     mysqlToSqlState.put(Integer.valueOf(1054), "S0022");
/*  403 */     mysqlToSqlState.put(Integer.valueOf(1247), "42S22");
/*  404 */     mysqlToSqlState.put(Integer.valueOf(1037), "S1001");
/*  405 */     mysqlToSqlState.put(Integer.valueOf(1038), "S1001");
/*  406 */     mysqlToSqlState.put(Integer.valueOf(1205), "40001");
/*  407 */     mysqlToSqlState.put(Integer.valueOf(1213), "40001");
/*      */     
/*  409 */     mysqlToSql99State = new HashMap();
/*      */     
/*  411 */     mysqlToSql99State.put(Integer.valueOf(1249), "01000");
/*  412 */     mysqlToSql99State.put(Integer.valueOf(1261), "01000");
/*  413 */     mysqlToSql99State.put(Integer.valueOf(1262), "01000");
/*  414 */     mysqlToSql99State.put(Integer.valueOf(1265), "01000");
/*  415 */     mysqlToSql99State.put(Integer.valueOf(1263), "01000");
/*  416 */     mysqlToSql99State.put(Integer.valueOf(1264), "01000");
/*  417 */     mysqlToSql99State.put(Integer.valueOf(1311), "01000");
/*  418 */     mysqlToSql99State.put(Integer.valueOf(1642), "01000");
/*  419 */     mysqlToSql99State.put(Integer.valueOf(1329), "02000");
/*  420 */     mysqlToSql99State.put(Integer.valueOf(1643), "02000");
/*  421 */     mysqlToSql99State.put(Integer.valueOf(1040), "08004");
/*  422 */     mysqlToSql99State.put(Integer.valueOf(1251), "08004");
/*  423 */     mysqlToSql99State.put(Integer.valueOf(1042), "08S01");
/*  424 */     mysqlToSql99State.put(Integer.valueOf(1043), "08S01");
/*  425 */     mysqlToSql99State.put(Integer.valueOf(1047), "08S01");
/*  426 */     mysqlToSql99State.put(Integer.valueOf(1053), "08S01");
/*  427 */     mysqlToSql99State.put(Integer.valueOf(1080), "08S01");
/*  428 */     mysqlToSql99State.put(Integer.valueOf(1081), "08S01");
/*  429 */     mysqlToSql99State.put(Integer.valueOf(1152), "08S01");
/*  430 */     mysqlToSql99State.put(Integer.valueOf(1153), "08S01");
/*  431 */     mysqlToSql99State.put(Integer.valueOf(1154), "08S01");
/*  432 */     mysqlToSql99State.put(Integer.valueOf(1155), "08S01");
/*  433 */     mysqlToSql99State.put(Integer.valueOf(1156), "08S01");
/*  434 */     mysqlToSql99State.put(Integer.valueOf(1157), "08S01");
/*  435 */     mysqlToSql99State.put(Integer.valueOf(1158), "08S01");
/*  436 */     mysqlToSql99State.put(Integer.valueOf(1159), "08S01");
/*  437 */     mysqlToSql99State.put(Integer.valueOf(1160), "08S01");
/*  438 */     mysqlToSql99State.put(Integer.valueOf(1161), "08S01");
/*  439 */     mysqlToSql99State.put(Integer.valueOf(1184), "08S01");
/*  440 */     mysqlToSql99State.put(Integer.valueOf(1189), "08S01");
/*  441 */     mysqlToSql99State.put(Integer.valueOf(1190), "08S01");
/*  442 */     mysqlToSql99State.put(Integer.valueOf(1218), "08S01");
/*  443 */     mysqlToSql99State.put(Integer.valueOf(1312), "0A000");
/*  444 */     mysqlToSql99State.put(Integer.valueOf(1314), "0A000");
/*  445 */     mysqlToSql99State.put(Integer.valueOf(1335), "0A000");
/*  446 */     mysqlToSql99State.put(Integer.valueOf(1336), "0A000");
/*  447 */     mysqlToSql99State.put(Integer.valueOf(1415), "0A000");
/*  448 */     mysqlToSql99State.put(Integer.valueOf(1845), "0A000");
/*  449 */     mysqlToSql99State.put(Integer.valueOf(1846), "0A000");
/*  450 */     mysqlToSql99State.put(Integer.valueOf(1044), "42000");
/*  451 */     mysqlToSql99State.put(Integer.valueOf(1049), "42000");
/*  452 */     mysqlToSql99State.put(Integer.valueOf(1055), "42000");
/*  453 */     mysqlToSql99State.put(Integer.valueOf(1056), "42000");
/*  454 */     mysqlToSql99State.put(Integer.valueOf(1057), "42000");
/*  455 */     mysqlToSql99State.put(Integer.valueOf(1059), "42000");
/*  456 */     mysqlToSql99State.put(Integer.valueOf(1061), "42000");
/*  457 */     mysqlToSql99State.put(Integer.valueOf(1063), "42000");
/*  458 */     mysqlToSql99State.put(Integer.valueOf(1064), "42000");
/*  459 */     mysqlToSql99State.put(Integer.valueOf(1065), "42000");
/*  460 */     mysqlToSql99State.put(Integer.valueOf(1066), "42000");
/*  461 */     mysqlToSql99State.put(Integer.valueOf(1067), "42000");
/*  462 */     mysqlToSql99State.put(Integer.valueOf(1068), "42000");
/*  463 */     mysqlToSql99State.put(Integer.valueOf(1069), "42000");
/*  464 */     mysqlToSql99State.put(Integer.valueOf(1070), "42000");
/*  465 */     mysqlToSql99State.put(Integer.valueOf(1071), "42000");
/*  466 */     mysqlToSql99State.put(Integer.valueOf(1072), "42000");
/*  467 */     mysqlToSql99State.put(Integer.valueOf(1073), "42000");
/*  468 */     mysqlToSql99State.put(Integer.valueOf(1074), "42000");
/*  469 */     mysqlToSql99State.put(Integer.valueOf(1075), "42000");
/*  470 */     mysqlToSql99State.put(Integer.valueOf(1083), "42000");
/*  471 */     mysqlToSql99State.put(Integer.valueOf(1084), "42000");
/*  472 */     mysqlToSql99State.put(Integer.valueOf(1090), "42000");
/*  473 */     mysqlToSql99State.put(Integer.valueOf(1091), "42000");
/*  474 */     mysqlToSql99State.put(Integer.valueOf(1101), "42000");
/*  475 */     mysqlToSql99State.put(Integer.valueOf(1102), "42000");
/*  476 */     mysqlToSql99State.put(Integer.valueOf(1103), "42000");
/*  477 */     mysqlToSql99State.put(Integer.valueOf(1104), "42000");
/*  478 */     mysqlToSql99State.put(Integer.valueOf(1106), "42000");
/*  479 */     mysqlToSql99State.put(Integer.valueOf(1107), "42000");
/*  480 */     mysqlToSql99State.put(Integer.valueOf(1110), "42000");
/*  481 */     mysqlToSql99State.put(Integer.valueOf(1112), "42000");
/*  482 */     mysqlToSql99State.put(Integer.valueOf(1113), "42000");
/*  483 */     mysqlToSql99State.put(Integer.valueOf(1115), "42000");
/*  484 */     mysqlToSql99State.put(Integer.valueOf(1118), "42000");
/*  485 */     mysqlToSql99State.put(Integer.valueOf(1120), "42000");
/*  486 */     mysqlToSql99State.put(Integer.valueOf(1121), "42000");
/*  487 */     mysqlToSql99State.put(Integer.valueOf(1131), "42000");
/*  488 */     mysqlToSql99State.put(Integer.valueOf(1132), "42000");
/*  489 */     mysqlToSql99State.put(Integer.valueOf(1133), "42000");
/*  490 */     mysqlToSql99State.put(Integer.valueOf(1139), "42000");
/*  491 */     mysqlToSql99State.put(Integer.valueOf(1140), "42000");
/*  492 */     mysqlToSql99State.put(Integer.valueOf(1141), "42000");
/*  493 */     mysqlToSql99State.put(Integer.valueOf(1142), "42000");
/*  494 */     mysqlToSql99State.put(Integer.valueOf(1143), "42000");
/*  495 */     mysqlToSql99State.put(Integer.valueOf(1144), "42000");
/*  496 */     mysqlToSql99State.put(Integer.valueOf(1145), "42000");
/*  497 */     mysqlToSql99State.put(Integer.valueOf(1147), "42000");
/*  498 */     mysqlToSql99State.put(Integer.valueOf(1148), "42000");
/*  499 */     mysqlToSql99State.put(Integer.valueOf(1149), "42000");
/*  500 */     mysqlToSql99State.put(Integer.valueOf(1162), "42000");
/*  501 */     mysqlToSql99State.put(Integer.valueOf(1163), "42000");
/*  502 */     mysqlToSql99State.put(Integer.valueOf(1164), "42000");
/*  503 */     mysqlToSql99State.put(Integer.valueOf(1166), "42000");
/*  504 */     mysqlToSql99State.put(Integer.valueOf(1167), "42000");
/*  505 */     mysqlToSql99State.put(Integer.valueOf(1170), "42000");
/*  506 */     mysqlToSql99State.put(Integer.valueOf(1171), "42000");
/*  507 */     mysqlToSql99State.put(Integer.valueOf(1172), "42000");
/*  508 */     mysqlToSql99State.put(Integer.valueOf(1173), "42000");
/*  509 */     mysqlToSql99State.put(Integer.valueOf(1176), "42000");
/*  510 */     mysqlToSql99State.put(Integer.valueOf(1177), "42000");
/*  511 */     mysqlToSql99State.put(Integer.valueOf(1178), "42000");
/*  512 */     mysqlToSql99State.put(Integer.valueOf(1203), "42000");
/*  513 */     mysqlToSql99State.put(Integer.valueOf(1211), "42000");
/*  514 */     mysqlToSql99State.put(Integer.valueOf(1226), "42000");
/*  515 */     mysqlToSql99State.put(Integer.valueOf(1227), "42000");
/*  516 */     mysqlToSql99State.put(Integer.valueOf(1230), "42000");
/*  517 */     mysqlToSql99State.put(Integer.valueOf(1231), "42000");
/*  518 */     mysqlToSql99State.put(Integer.valueOf(1232), "42000");
/*  519 */     mysqlToSql99State.put(Integer.valueOf(1234), "42000");
/*  520 */     mysqlToSql99State.put(Integer.valueOf(1235), "42000");
/*  521 */     mysqlToSql99State.put(Integer.valueOf(1239), "42000");
/*  522 */     mysqlToSql99State.put(Integer.valueOf(1248), "42000");
/*  523 */     mysqlToSql99State.put(Integer.valueOf(1250), "42000");
/*  524 */     mysqlToSql99State.put(Integer.valueOf(1252), "42000");
/*  525 */     mysqlToSql99State.put(Integer.valueOf(1253), "42000");
/*  526 */     mysqlToSql99State.put(Integer.valueOf(1280), "42000");
/*  527 */     mysqlToSql99State.put(Integer.valueOf(1281), "42000");
/*  528 */     mysqlToSql99State.put(Integer.valueOf(1286), "42000");
/*  529 */     mysqlToSql99State.put(Integer.valueOf(1304), "42000");
/*  530 */     mysqlToSql99State.put(Integer.valueOf(1305), "42000");
/*  531 */     mysqlToSql99State.put(Integer.valueOf(1308), "42000");
/*  532 */     mysqlToSql99State.put(Integer.valueOf(1309), "42000");
/*  533 */     mysqlToSql99State.put(Integer.valueOf(1310), "42000");
/*  534 */     mysqlToSql99State.put(Integer.valueOf(1313), "42000");
/*  535 */     mysqlToSql99State.put(Integer.valueOf(1315), "42000");
/*  536 */     mysqlToSql99State.put(Integer.valueOf(1316), "42000");
/*  537 */     mysqlToSql99State.put(Integer.valueOf(1318), "42000");
/*  538 */     mysqlToSql99State.put(Integer.valueOf(1319), "42000");
/*  539 */     mysqlToSql99State.put(Integer.valueOf(1320), "42000");
/*  540 */     mysqlToSql99State.put(Integer.valueOf(1322), "42000");
/*  541 */     mysqlToSql99State.put(Integer.valueOf(1323), "42000");
/*  542 */     mysqlToSql99State.put(Integer.valueOf(1324), "42000");
/*  543 */     mysqlToSql99State.put(Integer.valueOf(1327), "42000");
/*  544 */     mysqlToSql99State.put(Integer.valueOf(1330), "42000");
/*  545 */     mysqlToSql99State.put(Integer.valueOf(1331), "42000");
/*  546 */     mysqlToSql99State.put(Integer.valueOf(1332), "42000");
/*  547 */     mysqlToSql99State.put(Integer.valueOf(1333), "42000");
/*  548 */     mysqlToSql99State.put(Integer.valueOf(1337), "42000");
/*  549 */     mysqlToSql99State.put(Integer.valueOf(1338), "42000");
/*  550 */     mysqlToSql99State.put(Integer.valueOf(1370), "42000");
/*  551 */     mysqlToSql99State.put(Integer.valueOf(1403), "42000");
/*  552 */     mysqlToSql99State.put(Integer.valueOf(1407), "42000");
/*  553 */     mysqlToSql99State.put(Integer.valueOf(1410), "42000");
/*  554 */     mysqlToSql99State.put(Integer.valueOf(1413), "42000");
/*  555 */     mysqlToSql99State.put(Integer.valueOf(1414), "42000");
/*  556 */     mysqlToSql99State.put(Integer.valueOf(1425), "42000");
/*  557 */     mysqlToSql99State.put(Integer.valueOf(1426), "42000");
/*  558 */     mysqlToSql99State.put(Integer.valueOf(1427), "42000");
/*  559 */     mysqlToSql99State.put(Integer.valueOf(1437), "42000");
/*  560 */     mysqlToSql99State.put(Integer.valueOf(1439), "42000");
/*  561 */     mysqlToSql99State.put(Integer.valueOf(1453), "42000");
/*  562 */     mysqlToSql99State.put(Integer.valueOf(1458), "42000");
/*  563 */     mysqlToSql99State.put(Integer.valueOf(1460), "42000");
/*  564 */     mysqlToSql99State.put(Integer.valueOf(1461), "42000");
/*  565 */     mysqlToSql99State.put(Integer.valueOf(1463), "42000");
/*  566 */     mysqlToSql99State.put(Integer.valueOf(1582), "42000");
/*  567 */     mysqlToSql99State.put(Integer.valueOf(1583), "42000");
/*  568 */     mysqlToSql99State.put(Integer.valueOf(1584), "42000");
/*  569 */     mysqlToSql99State.put(Integer.valueOf(1630), "42000");
/*  570 */     mysqlToSql99State.put(Integer.valueOf(1641), "42000");
/*  571 */     mysqlToSql99State.put(Integer.valueOf(1687), "42000");
/*  572 */     mysqlToSql99State.put(Integer.valueOf(1701), "42000");
/*  573 */     mysqlToSql99State.put(Integer.valueOf(1222), "21000");
/*  574 */     mysqlToSql99State.put(Integer.valueOf(1241), "21000");
/*  575 */     mysqlToSql99State.put(Integer.valueOf(1242), "21000");
/*  576 */     mysqlToSql99State.put(Integer.valueOf(1022), "23000");
/*  577 */     mysqlToSql99State.put(Integer.valueOf(1048), "23000");
/*  578 */     mysqlToSql99State.put(Integer.valueOf(1052), "23000");
/*  579 */     mysqlToSql99State.put(Integer.valueOf(1062), "23000");
/*  580 */     mysqlToSql99State.put(Integer.valueOf(1169), "23000");
/*  581 */     mysqlToSql99State.put(Integer.valueOf(1216), "23000");
/*  582 */     mysqlToSql99State.put(Integer.valueOf(1217), "23000");
/*  583 */     mysqlToSql99State.put(Integer.valueOf(1451), "23000");
/*  584 */     mysqlToSql99State.put(Integer.valueOf(1452), "23000");
/*  585 */     mysqlToSql99State.put(Integer.valueOf(1557), "23000");
/*  586 */     mysqlToSql99State.put(Integer.valueOf(1586), "23000");
/*  587 */     mysqlToSql99State.put(Integer.valueOf(1761), "23000");
/*  588 */     mysqlToSql99State.put(Integer.valueOf(1762), "23000");
/*  589 */     mysqlToSql99State.put(Integer.valueOf(1859), "23000");
/*  590 */     mysqlToSql99State.put(Integer.valueOf(1406), "22001");
/*  591 */     mysqlToSql99State.put(Integer.valueOf(1416), "22003");
/*  592 */     mysqlToSql99State.put(Integer.valueOf(1690), "22003");
/*  593 */     mysqlToSql99State.put(Integer.valueOf(1292), "22007");
/*  594 */     mysqlToSql99State.put(Integer.valueOf(1367), "22007");
/*  595 */     mysqlToSql99State.put(Integer.valueOf(1441), "22008");
/*  596 */     mysqlToSql99State.put(Integer.valueOf(1365), "22012");
/*  597 */     mysqlToSql99State.put(Integer.valueOf(1325), "24000");
/*  598 */     mysqlToSql99State.put(Integer.valueOf(1326), "24000");
/*  599 */     mysqlToSql99State.put(Integer.valueOf(1179), "25000");
/*  600 */     mysqlToSql99State.put(Integer.valueOf(1207), "25000");
/*  601 */     mysqlToSql99State.put(Integer.valueOf(1045), "28000");
/*  602 */     mysqlToSql99State.put(Integer.valueOf(1698), "28000");
/*  603 */     mysqlToSql99State.put(Integer.valueOf(1873), "28000");
/*  604 */     mysqlToSql99State.put(Integer.valueOf(1758), "35000");
/*  605 */     mysqlToSql99State.put(Integer.valueOf(1046), "3D000");
/*  606 */     mysqlToSql99State.put(Integer.valueOf(1645), "0K000");
/*  607 */     mysqlToSql99State.put(Integer.valueOf(1887), "0Z002");
/*  608 */     mysqlToSql99State.put(Integer.valueOf(1339), "20000");
/*  609 */     mysqlToSql99State.put(Integer.valueOf(1058), "21S01");
/*  610 */     mysqlToSql99State.put(Integer.valueOf(1136), "21S01");
/*  611 */     mysqlToSql99State.put(Integer.valueOf(1138), "42000");
/*  612 */     mysqlToSql99State.put(Integer.valueOf(1903), "2201E");
/*  613 */     mysqlToSql99State.put(Integer.valueOf(1568), "25001");
/*  614 */     mysqlToSql99State.put(Integer.valueOf(1792), "25006");
/*  615 */     mysqlToSql99State.put(Integer.valueOf(1303), "2F003");
/*  616 */     mysqlToSql99State.put(Integer.valueOf(1321), "2F005");
/*  617 */     mysqlToSql99State.put(Integer.valueOf(1050), "42S01");
/*  618 */     mysqlToSql99State.put(Integer.valueOf(1051), "42S02");
/*  619 */     mysqlToSql99State.put(Integer.valueOf(1109), "42S02");
/*  620 */     mysqlToSql99State.put(Integer.valueOf(1146), "42S02");
/*  621 */     mysqlToSql99State.put(Integer.valueOf(1082), "42S12");
/*  622 */     mysqlToSql99State.put(Integer.valueOf(1060), "42S21");
/*  623 */     mysqlToSql99State.put(Integer.valueOf(1054), "42S22");
/*  624 */     mysqlToSql99State.put(Integer.valueOf(1247), "42S22");
/*  625 */     mysqlToSql99State.put(Integer.valueOf(1317), "70100");
/*  626 */     mysqlToSql99State.put(Integer.valueOf(1037), "HY001");
/*  627 */     mysqlToSql99State.put(Integer.valueOf(1038), "HY001");
/*  628 */     mysqlToSql99State.put(Integer.valueOf(1402), "XA100");
/*  629 */     mysqlToSql99State.put(Integer.valueOf(1614), "XA102");
/*  630 */     mysqlToSql99State.put(Integer.valueOf(1613), "XA106");
/*  631 */     mysqlToSql99State.put(Integer.valueOf(1401), "XAE03");
/*  632 */     mysqlToSql99State.put(Integer.valueOf(1397), "XAE04");
/*  633 */     mysqlToSql99State.put(Integer.valueOf(1398), "XAE05");
/*  634 */     mysqlToSql99State.put(Integer.valueOf(1399), "XAE07");
/*  635 */     mysqlToSql99State.put(Integer.valueOf(1440), "XAE08");
/*  636 */     mysqlToSql99State.put(Integer.valueOf(1400), "XAE09");
/*  637 */     mysqlToSql99State.put(Integer.valueOf(1205), "40001");
/*  638 */     mysqlToSql99State.put(Integer.valueOf(1213), "40001");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection)
/*      */     throws SQLException
/*      */   {
/*  656 */     return convertShowWarningsToSQLWarnings(connection, 0, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static SQLWarning convertShowWarningsToSQLWarnings(Connection connection, int warningCountIfKnown, boolean forTruncationOnly)
/*      */     throws SQLException
/*      */   {
/*  679 */     Statement stmt = null;
/*  680 */     ResultSet warnRs = null;
/*      */     
/*  682 */     SQLWarning currentWarning = null;
/*      */     try
/*      */     {
/*  685 */       if (warningCountIfKnown < 100) {
/*  686 */         stmt = connection.createStatement();
/*      */         
/*  688 */         if (stmt.getMaxRows() != 0) {
/*  689 */           stmt.setMaxRows(0);
/*      */         }
/*      */       }
/*      */       else {
/*  693 */         stmt = connection.createStatement(1003, 1007);
/*  694 */         stmt.setFetchSize(Integer.MIN_VALUE);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  704 */       warnRs = stmt.executeQuery("SHOW WARNINGS");
/*      */       int code;
/*  706 */       while (warnRs.next()) {
/*  707 */         code = warnRs.getInt("Code");
/*      */         
/*  709 */         if (forTruncationOnly) {
/*  710 */           if ((code == 1265) || (code == 1264)) {
/*  711 */             DataTruncation newTruncation = new MysqlDataTruncation(warnRs.getString("Message"), 0, false, false, 0, 0, code);
/*      */             
/*  713 */             if (currentWarning == null) {
/*  714 */               currentWarning = newTruncation;
/*      */             } else {
/*  716 */               currentWarning.setNextWarning(newTruncation);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  721 */           String message = warnRs.getString("Message");
/*      */           
/*  723 */           SQLWarning newWarning = new SQLWarning(message, mysqlToSqlState(code, connection.getUseSqlStateCodes()), code);
/*      */           
/*  725 */           if (currentWarning == null) {
/*  726 */             currentWarning = newWarning;
/*      */           } else {
/*  728 */             currentWarning.setNextWarning(newWarning);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  733 */       if ((forTruncationOnly) && (currentWarning != null)) {
/*  734 */         throw currentWarning;
/*      */       }
/*      */       
/*  737 */       return currentWarning;
/*      */     } finally {
/*  739 */       SQLException reThrow = null;
/*      */       
/*  741 */       if (warnRs != null) {
/*      */         try {
/*  743 */           warnRs.close();
/*      */         } catch (SQLException sqlEx) {
/*  745 */           reThrow = sqlEx;
/*      */         }
/*      */       }
/*      */       
/*  749 */       if (stmt != null) {
/*      */         try {
/*  751 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlEx) {
/*  754 */           reThrow = sqlEx;
/*      */         }
/*      */       }
/*      */       
/*  758 */       if (reThrow != null) {
/*  759 */         throw reThrow;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void dumpSqlStatesMappingsAsXml() throws Exception {
/*  765 */     TreeMap<Integer, Integer> allErrorNumbers = new TreeMap();
/*  766 */     Map<Object, String> mysqlErrorNumbersToNames = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  773 */     for (Integer errorNumber : mysqlToSql99State.keySet()) {
/*  774 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     }
/*      */     
/*  777 */     for (Integer errorNumber : mysqlToSqlState.keySet()) {
/*  778 */       allErrorNumbers.put(errorNumber, errorNumber);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  784 */     Field[] possibleFields = MysqlErrorNumbers.class.getDeclaredFields();
/*      */     
/*  786 */     for (int i = 0; i < possibleFields.length; i++) {
/*  787 */       String fieldName = possibleFields[i].getName();
/*      */       
/*  789 */       if (fieldName.startsWith("ER_")) {
/*  790 */         mysqlErrorNumbersToNames.put(possibleFields[i].get(null), fieldName);
/*      */       }
/*      */     }
/*      */     
/*  794 */     System.out.println("<ErrorMappings>");
/*      */     
/*  796 */     for (Integer errorNumber : allErrorNumbers.keySet()) {
/*  797 */       String sql92State = mysqlToSql99(errorNumber.intValue());
/*  798 */       String oldSqlState = mysqlToXOpen(errorNumber.intValue());
/*      */       
/*  800 */       System.out.println("   <ErrorMapping mysqlErrorNumber=\"" + errorNumber + "\" mysqlErrorName=\"" + (String)mysqlErrorNumbersToNames.get(errorNumber) + "\" legacySqlState=\"" + (oldSqlState == null ? "" : oldSqlState) + "\" sql92SqlState=\"" + (sql92State == null ? "" : sql92State) + "\"/>");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  805 */     System.out.println("</ErrorMappings>");
/*      */   }
/*      */   
/*      */   static String get(String stateCode) {
/*  809 */     return (String)sqlStateMessages.get(stateCode);
/*      */   }
/*      */   
/*      */   private static String mysqlToSql99(int errno) {
/*  813 */     Integer err = Integer.valueOf(errno);
/*      */     
/*  815 */     if (mysqlToSql99State.containsKey(err)) {
/*  816 */       return (String)mysqlToSql99State.get(err);
/*      */     }
/*      */     
/*  819 */     return "HY000";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static String mysqlToSqlState(int errno, boolean useSql92States)
/*      */   {
/*  831 */     if (useSql92States) {
/*  832 */       return mysqlToSql99(errno);
/*      */     }
/*      */     
/*  835 */     return mysqlToXOpen(errno);
/*      */   }
/*      */   
/*      */   private static String mysqlToXOpen(int errno) {
/*  839 */     Integer err = Integer.valueOf(errno);
/*      */     
/*  841 */     if (mysqlToSqlState.containsKey(err)) {
/*  842 */       return (String)mysqlToSqlState.get(err);
/*      */     }
/*      */     
/*  845 */     return "S1000";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor)
/*      */   {
/*  860 */     return createSQLException(message, sqlState, 0, interceptor);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
/*  864 */     return createSQLException(message, interceptor, null);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor, Connection conn) {
/*  868 */     SQLException sqlEx = new SQLException(message);
/*  869 */     return runThroughExceptionInterceptor(interceptor, sqlEx, conn);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
/*  873 */     return createSQLException(message, sqlState, cause, interceptor, null);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor, Connection conn) {
/*  877 */     SQLException sqlEx = createSQLException(message, sqlState, interceptor);
/*      */     
/*  879 */     sqlEx.initCause(cause);
/*      */     
/*  881 */     return runThroughExceptionInterceptor(interceptor, sqlEx, conn);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor) {
/*  885 */     return createSQLException(message, sqlState, vendorErrorCode, false, interceptor);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor)
/*      */   {
/*  896 */     return createSQLException(message, sqlState, vendorErrorCode, isTransient, interceptor, null);
/*      */   }
/*      */   
/*      */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor, Connection conn)
/*      */   {
/*      */     try {
/*  902 */       SQLException sqlEx = null;
/*      */       
/*  904 */       if (sqlState != null) {
/*  905 */         if (sqlState.startsWith("08")) {
/*  906 */           if (isTransient) {
/*  907 */             if (!Util.isJdbc4()) {
/*  908 */               sqlEx = new MySQLTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */             } else {
/*  910 */               sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransientConnectionException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */             }
/*      */             
/*      */           }
/*  914 */           else if (!Util.isJdbc4()) {
/*  915 */             sqlEx = new MySQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  917 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*  921 */         else if (sqlState.startsWith("22")) {
/*  922 */           if (!Util.isJdbc4()) {
/*  923 */             sqlEx = new MySQLDataException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  925 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLDataException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*  929 */         else if (sqlState.startsWith("23"))
/*      */         {
/*  931 */           if (!Util.isJdbc4()) {
/*  932 */             sqlEx = new MySQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  934 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*  938 */         else if (sqlState.startsWith("42")) {
/*  939 */           if (!Util.isJdbc4()) {
/*  940 */             sqlEx = new MySQLSyntaxErrorException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  942 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*  946 */         else if (sqlState.startsWith("40")) {
/*  947 */           if (!Util.isJdbc4()) {
/*  948 */             sqlEx = new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  950 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*  954 */         else if (sqlState.startsWith("70100")) {
/*  955 */           if (!Util.isJdbc4()) {
/*  956 */             sqlEx = new MySQLQueryInterruptedException(message, sqlState, vendorErrorCode);
/*      */           } else {
/*  958 */             sqlEx = (SQLException)Util.getInstance("com.mysql.jdbc.exceptions.jdbc4.MySQLQueryInterruptedException", new Class[] { String.class, String.class, Integer.TYPE }, new Object[] { message, sqlState, Integer.valueOf(vendorErrorCode) }, interceptor);
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/*  963 */           sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*      */         }
/*      */       } else {
/*  966 */         sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*      */       }
/*      */       
/*  969 */       return runThroughExceptionInterceptor(interceptor, sqlEx, conn);
/*      */     } catch (SQLException sqlEx) {
/*  971 */       SQLException unexpectedEx = new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(sqlEx), "S1000");
/*      */       
/*      */ 
/*      */ 
/*  975 */       return runThroughExceptionInterceptor(interceptor, unexpectedEx, conn);
/*      */     }
/*      */   }
/*      */   
/*      */   public static SQLException createCommunicationsException(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException, ExceptionInterceptor interceptor)
/*      */   {
/*  981 */     SQLException exToReturn = null;
/*      */     
/*  983 */     if (!Util.isJdbc4()) {
/*  984 */       exToReturn = new CommunicationsException(conn, lastPacketSentTimeMs, lastPacketReceivedTimeMs, underlyingException);
/*      */     } else {
/*      */       try
/*      */       {
/*  988 */         exToReturn = (SQLException)Util.handleNewInstance(JDBC_4_COMMUNICATIONS_EXCEPTION_CTOR, new Object[] { conn, Long.valueOf(lastPacketSentTimeMs), Long.valueOf(lastPacketReceivedTimeMs), underlyingException }, interceptor);
/*      */ 
/*      */       }
/*      */       catch (SQLException sqlEx)
/*      */       {
/*  993 */         return sqlEx;
/*      */       }
/*      */     }
/*      */     
/*  997 */     return runThroughExceptionInterceptor(interceptor, exToReturn, conn);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String createLinkFailureMessageBasedOnHeuristics(MySQLConnection conn, long lastPacketSentTimeMs, long lastPacketReceivedTimeMs, Exception underlyingException)
/*      */   {
/* 1011 */     long serverTimeoutSeconds = 0L;
/* 1012 */     boolean isInteractiveClient = false;
/*      */     
/* 1014 */     if (conn != null) {
/* 1015 */       isInteractiveClient = conn.getInteractiveClient();
/*      */       
/* 1017 */       String serverTimeoutSecondsStr = null;
/*      */       
/* 1019 */       if (isInteractiveClient) {
/* 1020 */         serverTimeoutSecondsStr = conn.getServerVariable("interactive_timeout");
/*      */       } else {
/* 1022 */         serverTimeoutSecondsStr = conn.getServerVariable("wait_timeout");
/*      */       }
/*      */       
/* 1025 */       if (serverTimeoutSecondsStr != null) {
/*      */         try {
/* 1027 */           serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
/*      */         } catch (NumberFormatException nfe) {
/* 1029 */           serverTimeoutSeconds = 0L;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1034 */     StringBuilder exceptionMessageBuf = new StringBuilder();
/*      */     
/* 1036 */     long nowMs = System.currentTimeMillis();
/*      */     
/* 1038 */     if (lastPacketSentTimeMs == 0L) {
/* 1039 */       lastPacketSentTimeMs = nowMs;
/*      */     }
/*      */     
/* 1042 */     long timeSinceLastPacketSentMs = nowMs - lastPacketSentTimeMs;
/* 1043 */     long timeSinceLastPacketSeconds = timeSinceLastPacketSentMs / 1000L;
/*      */     
/* 1045 */     long timeSinceLastPacketReceivedMs = nowMs - lastPacketReceivedTimeMs;
/*      */     
/* 1047 */     int dueToTimeout = 0;
/*      */     
/* 1049 */     StringBuilder timeoutMessageBuf = null;
/*      */     
/* 1051 */     if (serverTimeoutSeconds != 0L) {
/* 1052 */       if (timeSinceLastPacketSeconds > serverTimeoutSeconds) {
/* 1053 */         dueToTimeout = 1;
/*      */         
/* 1055 */         timeoutMessageBuf = new StringBuilder();
/*      */         
/* 1057 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
/*      */         
/* 1059 */         if (!isInteractiveClient) {
/* 1060 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.3"));
/*      */         } else {
/* 1062 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.4"));
/*      */         }
/*      */       }
/*      */     }
/* 1066 */     else if (timeSinceLastPacketSeconds > 28800L) {
/* 1067 */       dueToTimeout = 2;
/*      */       
/* 1069 */       timeoutMessageBuf = new StringBuilder();
/*      */       
/* 1071 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
/* 1072 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
/* 1073 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
/* 1074 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
/*      */     }
/*      */     
/* 1077 */     if ((dueToTimeout == 1) || (dueToTimeout == 2))
/*      */     {
/* 1079 */       if (lastPacketReceivedTimeMs != 0L) {
/* 1080 */         Object[] timingInfo = { Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs) };
/* 1081 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfo", timingInfo));
/*      */       } else {
/* 1083 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
/*      */       }
/*      */       
/*      */ 
/* 1087 */       if (timeoutMessageBuf != null) {
/* 1088 */         exceptionMessageBuf.append(timeoutMessageBuf);
/*      */       }
/*      */       
/* 1091 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
/* 1092 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
/* 1093 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/* 1100 */     else if ((underlyingException instanceof BindException)) {
/* 1101 */       if ((conn.getLocalSocketAddress() != null) && (!Util.interfaceExists(conn.getLocalSocketAddress()))) {
/* 1102 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.LocalSocketAddressNotAvailable"));
/*      */       }
/*      */       else {
/* 1105 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.TooManyClientConnections"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1110 */     if (exceptionMessageBuf.length() == 0)
/*      */     {
/* 1112 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
/*      */       
/* 1114 */       if ((conn != null) && (conn.getMaintainTimeStats()) && (!conn.getParanoid())) {
/* 1115 */         exceptionMessageBuf.append("\n\n");
/* 1116 */         if (lastPacketReceivedTimeMs != 0L) {
/* 1117 */           Object[] timingInfo = { Long.valueOf(timeSinceLastPacketReceivedMs), Long.valueOf(timeSinceLastPacketSentMs) };
/* 1118 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfo", timingInfo));
/*      */         } else {
/* 1120 */           exceptionMessageBuf.append(Messages.getString("CommunicationsException.ServerPacketTimingInfoNoRecv", new Object[] { Long.valueOf(timeSinceLastPacketSentMs) }));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1126 */     return exceptionMessageBuf.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static SQLException runThroughExceptionInterceptor(ExceptionInterceptor exInterceptor, SQLException sqlEx, Connection conn)
/*      */   {
/* 1138 */     if (exInterceptor != null) {
/* 1139 */       SQLException interceptedEx = exInterceptor.interceptException(sqlEx, conn);
/*      */       
/* 1141 */       if (interceptedEx != null) {
/* 1142 */         return interceptedEx;
/*      */       }
/*      */     }
/* 1145 */     return sqlEx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static SQLException createBatchUpdateException(SQLException underlyingEx, long[] updateCounts, ExceptionInterceptor interceptor)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException newEx;
/*      */     
/*      */ 
/*      */     SQLException newEx;
/*      */     
/*      */ 
/* 1160 */     if (Util.isJdbc42()) {
/* 1161 */       newEx = (SQLException)Util.getInstance("java.sql.BatchUpdateException", new Class[] { String.class, String.class, Integer.TYPE, long[].class, Throwable.class }, new Object[] { underlyingEx.getMessage(), underlyingEx.getSQLState(), Integer.valueOf(underlyingEx.getErrorCode()), updateCounts, underlyingEx }, interceptor);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1166 */       newEx = new BatchUpdateException(underlyingEx.getMessage(), underlyingEx.getSQLState(), underlyingEx.getErrorCode(), Util.truncateAndConvertToInt(updateCounts));
/*      */       
/* 1168 */       newEx.initCause(underlyingEx);
/*      */     }
/* 1170 */     return runThroughExceptionInterceptor(interceptor, newEx, null);
/*      */   }
/*      */   
/*      */ 
/*      */   public static SQLException createSQLFeatureNotSupportedException()
/*      */     throws SQLException
/*      */   {
/*      */     SQLException newEx;
/*      */     SQLException newEx;
/* 1179 */     if (Util.isJdbc4()) {
/* 1180 */       newEx = (SQLException)Util.getInstance("java.sql.SQLFeatureNotSupportedException", null, null, null);
/*      */     } else {
/* 1182 */       newEx = new NotImplemented();
/*      */     }
/*      */     
/* 1185 */     return newEx;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static SQLException createSQLFeatureNotSupportedException(String message, String sqlState, ExceptionInterceptor interceptor)
/*      */     throws SQLException
/*      */   {
/*      */     SQLException newEx;
/*      */     
/*      */ 
/*      */     SQLException newEx;
/*      */     
/* 1198 */     if (Util.isJdbc4()) {
/* 1199 */       newEx = (SQLException)Util.getInstance("java.sql.SQLFeatureNotSupportedException", new Class[] { String.class, String.class }, new Object[] { message, sqlState }, interceptor);
/*      */     }
/*      */     else {
/* 1202 */       newEx = new NotImplemented();
/*      */     }
/*      */     
/* 1205 */     return runThroughExceptionInterceptor(interceptor, newEx, null);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\SQLError.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */