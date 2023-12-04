/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.authentication.MysqlClearPasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.MysqlNativePasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.MysqlOldPasswordPlugin;
/*      */ import com.mysql.jdbc.authentication.Sha256PasswordPlugin;
/*      */ import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;
/*      */ import com.mysql.jdbc.exceptions.MySQLTimeoutException;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogUtils;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import com.mysql.jdbc.util.ReadAheadInputStream;
/*      */ import com.mysql.jdbc.util.ResultSetUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.lang.management.ThreadInfo;
/*      */ import java.lang.management.ThreadMXBean;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.URL;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.Deflater;
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
/*      */ public class MysqlIO
/*      */ {
/*      */   private static final String CODE_PAGE_1252 = "Cp1252";
/*      */   protected static final int NULL_LENGTH = -1;
/*      */   protected static final int COMP_HEADER_LENGTH = 3;
/*      */   protected static final int MIN_COMPRESS_LEN = 50;
/*      */   protected static final int HEADER_LENGTH = 4;
/*      */   protected static final int AUTH_411_OVERHEAD = 33;
/*      */   public static final int SEED_LENGTH = 20;
/*   80 */   private static int maxBufferSize = 65535;
/*      */   
/*      */   private static final String NONE = "none";
/*      */   
/*      */   private static final int CLIENT_LONG_PASSWORD = 1;
/*      */   
/*      */   private static final int CLIENT_FOUND_ROWS = 2;
/*      */   
/*      */   private static final int CLIENT_LONG_FLAG = 4;
/*      */   
/*      */   protected static final int CLIENT_CONNECT_WITH_DB = 8;
/*      */   
/*      */   private static final int CLIENT_COMPRESS = 32;
/*      */   private static final int CLIENT_LOCAL_FILES = 128;
/*      */   private static final int CLIENT_PROTOCOL_41 = 512;
/*      */   private static final int CLIENT_INTERACTIVE = 1024;
/*      */   protected static final int CLIENT_SSL = 2048;
/*      */   private static final int CLIENT_TRANSACTIONS = 8192;
/*      */   protected static final int CLIENT_RESERVED = 16384;
/*      */   protected static final int CLIENT_SECURE_CONNECTION = 32768;
/*      */   private static final int CLIENT_MULTI_STATEMENTS = 65536;
/*      */   private static final int CLIENT_MULTI_RESULTS = 131072;
/*      */   private static final int CLIENT_PLUGIN_AUTH = 524288;
/*      */   private static final int CLIENT_CONNECT_ATTRS = 1048576;
/*      */   private static final int CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA = 2097152;
/*      */   private static final int CLIENT_CAN_HANDLE_EXPIRED_PASSWORD = 4194304;
/*      */   private static final int CLIENT_SESSION_TRACK = 8388608;
/*      */   private static final int CLIENT_DEPRECATE_EOF = 16777216;
/*      */   private static final int SERVER_STATUS_IN_TRANS = 1;
/*      */   private static final int SERVER_STATUS_AUTOCOMMIT = 2;
/*      */   static final int SERVER_MORE_RESULTS_EXISTS = 8;
/*      */   private static final int SERVER_QUERY_NO_GOOD_INDEX_USED = 16;
/*      */   private static final int SERVER_QUERY_NO_INDEX_USED = 32;
/*      */   private static final int SERVER_QUERY_WAS_SLOW = 2048;
/*      */   private static final int SERVER_STATUS_CURSOR_EXISTS = 64;
/*      */   private static final String FALSE_SCRAMBLE = "xxxxxxxx";
/*      */   protected static final int MAX_QUERY_SIZE_TO_LOG = 1024;
/*      */   protected static final int MAX_QUERY_SIZE_TO_EXPLAIN = 1048576;
/*      */   protected static final int INITIAL_PACKET_SIZE = 1024;
/*  119 */   private static String jvmPlatformCharset = null;
/*      */   
/*      */ 
/*      */   protected static final String ZERO_DATE_VALUE_MARKER = "0000-00-00";
/*      */   
/*      */   protected static final String ZERO_DATETIME_VALUE_MARKER = "0000-00-00 00:00:00";
/*      */   
/*      */   private static final String EXPLAINABLE_STATEMENT = "SELECT";
/*      */   
/*  128 */   private static final String[] EXPLAINABLE_STATEMENT_EXTENSION = { "INSERT", "UPDATE", "REPLACE", "DELETE" };
/*      */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*      */   
/*  131 */   static { OutputStreamWriter outWriter = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  138 */       outWriter = new OutputStreamWriter(new ByteArrayOutputStream());
/*  139 */       jvmPlatformCharset = outWriter.getEncoding();
/*      */     } finally {
/*      */       try {
/*  142 */         if (outWriter != null) {
/*  143 */           outWriter.close();
/*      */         }
/*      */       }
/*      */       catch (IOException ioEx) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  153 */   private boolean packetSequenceReset = false;
/*      */   
/*      */ 
/*      */   protected int serverCharsetIndex;
/*      */   
/*      */ 
/*  159 */   private Buffer reusablePacket = null;
/*  160 */   private Buffer sendPacket = null;
/*  161 */   private Buffer sharedSendPacket = null;
/*      */   
/*      */ 
/*  164 */   protected BufferedOutputStream mysqlOutput = null;
/*      */   protected MySQLConnection connection;
/*  166 */   private Deflater deflater = null;
/*  167 */   protected InputStream mysqlInput = null;
/*  168 */   private LinkedList<StringBuilder> packetDebugRingBuffer = null;
/*  169 */   private RowData streamingData = null;
/*      */   
/*      */ 
/*  172 */   public Socket mysqlConnection = null;
/*  173 */   protected SocketFactory socketFactory = null;
/*      */   
/*      */ 
/*      */ 
/*      */   private SoftReference<Buffer> loadFileBufRef;
/*      */   
/*      */ 
/*      */ 
/*      */   private SoftReference<Buffer> splitBufRef;
/*      */   
/*      */ 
/*      */ 
/*      */   private SoftReference<Buffer> compressBufRef;
/*      */   
/*      */ 
/*  188 */   protected String host = null;
/*      */   protected String seed;
/*  190 */   private String serverVersion = null;
/*  191 */   private String socketFactoryClassName = null;
/*  192 */   private byte[] packetHeaderBuf = new byte[4];
/*  193 */   private boolean colDecimalNeedsBump = false;
/*  194 */   private boolean hadWarnings = false;
/*  195 */   private boolean has41NewNewProt = false;
/*      */   
/*      */ 
/*  198 */   private boolean hasLongColumnInfo = false;
/*  199 */   private boolean isInteractiveClient = false;
/*  200 */   private boolean logSlowQueries = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  206 */   private boolean platformDbCharsetMatches = true;
/*  207 */   private boolean profileSql = false;
/*  208 */   private boolean queryBadIndexUsed = false;
/*  209 */   private boolean queryNoIndexUsed = false;
/*  210 */   private boolean serverQueryWasSlow = false;
/*      */   
/*      */ 
/*  213 */   private boolean use41Extensions = false;
/*  214 */   private boolean useCompression = false;
/*  215 */   private boolean useNewLargePackets = false;
/*  216 */   private boolean useNewUpdateCounts = false;
/*  217 */   private byte packetSequence = 0;
/*  218 */   private byte compressedPacketSequence = 0;
/*  219 */   private byte readPacketSequence = -1;
/*  220 */   private boolean checkPacketSequence = false;
/*  221 */   private byte protocolVersion = 0;
/*  222 */   private int maxAllowedPacket = 1048576;
/*  223 */   protected int maxThreeBytes = 16581375;
/*  224 */   protected int port = 3306;
/*      */   protected int serverCapabilities;
/*  226 */   private int serverMajorVersion = 0;
/*  227 */   private int serverMinorVersion = 0;
/*  228 */   private int oldServerStatus = 0;
/*  229 */   private int serverStatus = 0;
/*  230 */   private int serverSubMinorVersion = 0;
/*  231 */   private int warningCount = 0;
/*  232 */   protected long clientParam = 0L;
/*  233 */   protected long lastPacketSentTimeMs = 0L;
/*  234 */   protected long lastPacketReceivedTimeMs = 0L;
/*  235 */   private boolean traceProtocol = false;
/*  236 */   private boolean enablePacketDebug = false;
/*      */   private boolean useConnectWithDb;
/*      */   private boolean needToGrabQueryFromPacket;
/*      */   private boolean autoGenerateTestcaseScript;
/*      */   private long threadId;
/*      */   private boolean useNanosForElapsedTime;
/*      */   private long slowQueryThreshold;
/*      */   private String queryTimingUnits;
/*  244 */   private boolean useDirectRowUnpack = true;
/*      */   private int useBufferRowSizeThreshold;
/*  246 */   private int commandCount = 0;
/*      */   private List<StatementInterceptorV2> statementInterceptors;
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*  249 */   private int authPluginDataLength = 0;
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
/*      */   public MysqlIO(String host, int port, Properties props, String socketFactoryClassName, MySQLConnection conn, int socketTimeout, int useBufferRowSizeThreshold)
/*      */     throws IOException, SQLException
/*      */   {
/*  275 */     this.connection = conn;
/*      */     
/*  277 */     if (this.connection.getEnablePacketDebug()) {
/*  278 */       this.packetDebugRingBuffer = new LinkedList();
/*      */     }
/*  280 */     this.traceProtocol = this.connection.getTraceProtocol();
/*      */     
/*  282 */     this.useAutoSlowLog = this.connection.getAutoSlowLog();
/*      */     
/*  284 */     this.useBufferRowSizeThreshold = useBufferRowSizeThreshold;
/*  285 */     this.useDirectRowUnpack = this.connection.getUseDirectRowUnpack();
/*      */     
/*  287 */     this.logSlowQueries = this.connection.getLogSlowQueries();
/*      */     
/*  289 */     this.reusablePacket = new Buffer(1024);
/*  290 */     this.sendPacket = new Buffer(1024);
/*      */     
/*  292 */     this.port = port;
/*  293 */     this.host = host;
/*      */     
/*  295 */     this.socketFactoryClassName = socketFactoryClassName;
/*  296 */     this.socketFactory = createSocketFactory();
/*  297 */     this.exceptionInterceptor = this.connection.getExceptionInterceptor();
/*      */     try
/*      */     {
/*  300 */       this.mysqlConnection = this.socketFactory.connect(this.host, this.port, props);
/*      */       
/*  302 */       if (socketTimeout != 0) {
/*      */         try {
/*  304 */           this.mysqlConnection.setSoTimeout(socketTimeout);
/*      */         }
/*      */         catch (Exception ex) {}
/*      */       }
/*      */       
/*      */ 
/*  310 */       this.mysqlConnection = this.socketFactory.beforeHandshake();
/*      */       
/*  312 */       if (this.connection.getUseReadAheadInput()) {
/*  313 */         this.mysqlInput = new ReadAheadInputStream(this.mysqlConnection.getInputStream(), 16384, this.connection.getTraceProtocol(), this.connection.getLog());
/*      */       }
/*  315 */       else if (this.connection.useUnbufferedInput()) {
/*  316 */         this.mysqlInput = this.mysqlConnection.getInputStream();
/*      */       } else {
/*  318 */         this.mysqlInput = new BufferedInputStream(this.mysqlConnection.getInputStream(), 16384);
/*      */       }
/*      */       
/*  321 */       this.mysqlOutput = new BufferedOutputStream(this.mysqlConnection.getOutputStream(), 16384);
/*      */       
/*  323 */       this.isInteractiveClient = this.connection.getInteractiveClient();
/*  324 */       this.profileSql = this.connection.getProfileSql();
/*  325 */       this.autoGenerateTestcaseScript = this.connection.getAutoGenerateTestcaseScript();
/*      */       
/*  327 */       this.needToGrabQueryFromPacket = ((this.profileSql) || (this.logSlowQueries) || (this.autoGenerateTestcaseScript));
/*      */       
/*  329 */       if ((this.connection.getUseNanosForElapsedTime()) && (TimeUtil.nanoTimeAvailable())) {
/*  330 */         this.useNanosForElapsedTime = true;
/*      */         
/*  332 */         this.queryTimingUnits = Messages.getString("Nanoseconds");
/*      */       } else {
/*  334 */         this.queryTimingUnits = Messages.getString("Milliseconds");
/*      */       }
/*      */       
/*  337 */       if (this.connection.getLogSlowQueries()) {
/*  338 */         calculateSlowQueryThreshold();
/*      */       }
/*      */     } catch (IOException ioEx) {
/*  341 */       throw SQLError.createCommunicationsException(this.connection, 0L, 0L, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasLongColumnInfo()
/*      */   {
/*  351 */     return this.hasLongColumnInfo;
/*      */   }
/*      */   
/*      */   protected boolean isDataAvailable() throws SQLException {
/*      */     try {
/*  356 */       return this.mysqlInput.available() > 0;
/*      */     } catch (IOException ioEx) {
/*  358 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getLastPacketSentTimeMs()
/*      */   {
/*  367 */     return this.lastPacketSentTimeMs;
/*      */   }
/*      */   
/*      */   protected long getLastPacketReceivedTimeMs() {
/*  371 */     return this.lastPacketReceivedTimeMs;
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
/*      */   protected ResultSetImpl getResultSet(StatementImpl callingStatement, long columnCount, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, boolean isBinaryEncoded, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/*  407 */     Field[] fields = null;
/*      */     
/*      */ 
/*      */ 
/*  411 */     if (metadataFromCache == null) {
/*  412 */       fields = new Field[(int)columnCount];
/*      */       
/*  414 */       for (int i = 0; i < columnCount; i++) {
/*  415 */         Buffer fieldPacket = null;
/*      */         
/*  417 */         fieldPacket = readPacket();
/*  418 */         fields[i] = unpackField(fieldPacket, false);
/*      */       }
/*      */     } else {
/*  421 */       for (int i = 0; i < columnCount; i++) {
/*  422 */         skipPacket();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  427 */     if ((!isEOFDeprecated()) || ((this.connection.versionMeetsMinimum(5, 0, 2)) && (callingStatement != null) && (isBinaryEncoded) && (callingStatement.isCursorRequired())))
/*      */     {
/*      */ 
/*      */ 
/*  431 */       Buffer packet = reuseAndReadPacket(this.reusablePacket);
/*  432 */       readServerStatusForResultSets(packet);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  439 */     if ((this.connection.versionMeetsMinimum(5, 0, 2)) && (this.connection.getUseCursorFetch()) && (isBinaryEncoded) && (callingStatement != null) && (callingStatement.getFetchSize() != 0) && (callingStatement.getResultSetType() == 1003))
/*      */     {
/*  441 */       ServerPreparedStatement prepStmt = (ServerPreparedStatement)callingStatement;
/*      */       
/*  443 */       boolean usingCursor = true;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  450 */       if (this.connection.versionMeetsMinimum(5, 0, 5)) {
/*  451 */         usingCursor = (this.serverStatus & 0x40) != 0;
/*      */       }
/*      */       
/*  454 */       if (usingCursor) {
/*  455 */         RowData rows = new RowDataCursor(this, prepStmt, fields);
/*      */         
/*  457 */         ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, fields, rows, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */         
/*  459 */         if (usingCursor) {
/*  460 */           rs.setFetchSize(callingStatement.getFetchSize());
/*      */         }
/*      */         
/*  463 */         return rs;
/*      */       }
/*      */     }
/*      */     
/*  467 */     RowData rowData = null;
/*      */     
/*  469 */     if (!streamResults) {
/*  470 */       rowData = readSingleRowSet(columnCount, maxRows, resultSetConcurrency, isBinaryEncoded, metadataFromCache == null ? fields : metadataFromCache);
/*      */     } else {
/*  472 */       rowData = new RowDataDynamic(this, (int)columnCount, metadataFromCache == null ? fields : metadataFromCache, isBinaryEncoded);
/*  473 */       this.streamingData = rowData;
/*      */     }
/*      */     
/*  476 */     ResultSetImpl rs = buildResultSetWithRows(callingStatement, catalog, metadataFromCache == null ? fields : metadataFromCache, rowData, resultSetType, resultSetConcurrency, isBinaryEncoded);
/*      */     
/*      */ 
/*  479 */     return rs;
/*      */   }
/*      */   
/*      */ 
/*      */   protected NetworkResources getNetworkResources()
/*      */   {
/*  485 */     return new NetworkResources(this.mysqlConnection, this.mysqlInput, this.mysqlOutput);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final void forceClose()
/*      */   {
/*      */     try
/*      */     {
/*  493 */       getNetworkResources().forceClose();
/*      */     } finally {
/*  495 */       this.mysqlConnection = null;
/*  496 */       this.mysqlInput = null;
/*  497 */       this.mysqlOutput = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final void skipPacket()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  511 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */       
/*  513 */       if (lengthRead < 4) {
/*  514 */         forceClose();
/*  515 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       }
/*      */       
/*  518 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       
/*  520 */       if (this.traceProtocol) {
/*  521 */         StringBuilder traceMessageBuf = new StringBuilder();
/*      */         
/*  523 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  524 */         traceMessageBuf.append(packetLength);
/*  525 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  526 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */         
/*  528 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */       
/*  531 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */       
/*  533 */       if (!this.packetSequenceReset) {
/*  534 */         if ((this.enablePacketDebug) && (this.checkPacketSequence)) {
/*  535 */           checkPacketSequencing(multiPacketSeq);
/*      */         }
/*      */       } else {
/*  538 */         this.packetSequenceReset = false;
/*      */       }
/*      */       
/*  541 */       this.readPacketSequence = multiPacketSeq;
/*      */       
/*  543 */       skipFully(this.mysqlInput, packetLength);
/*      */     } catch (IOException ioEx) {
/*  545 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/*      */       try {
/*  549 */         this.connection.realClose(false, false, true, oom);
/*      */       }
/*      */       catch (Exception ex) {}
/*  552 */       throw oom;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Buffer readPacket()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  567 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */       
/*  569 */       if (lengthRead < 4) {
/*  570 */         forceClose();
/*  571 */         throw new IOException(Messages.getString("MysqlIO.1"));
/*      */       }
/*      */       
/*  574 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       
/*  576 */       if (packetLength > this.maxAllowedPacket) {
/*  577 */         throw new PacketTooBigException(packetLength, this.maxAllowedPacket);
/*      */       }
/*      */       
/*  580 */       if (this.traceProtocol) {
/*  581 */         StringBuilder traceMessageBuf = new StringBuilder();
/*      */         
/*  583 */         traceMessageBuf.append(Messages.getString("MysqlIO.2"));
/*  584 */         traceMessageBuf.append(packetLength);
/*  585 */         traceMessageBuf.append(Messages.getString("MysqlIO.3"));
/*  586 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */         
/*  588 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */       
/*  591 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */       
/*  593 */       if (!this.packetSequenceReset) {
/*  594 */         if ((this.enablePacketDebug) && (this.checkPacketSequence)) {
/*  595 */           checkPacketSequencing(multiPacketSeq);
/*      */         }
/*      */       } else {
/*  598 */         this.packetSequenceReset = false;
/*      */       }
/*      */       
/*  601 */       this.readPacketSequence = multiPacketSeq;
/*      */       
/*      */ 
/*  604 */       byte[] buffer = new byte[packetLength];
/*  605 */       int numBytesRead = readFully(this.mysqlInput, buffer, 0, packetLength);
/*      */       
/*  607 */       if (numBytesRead != packetLength) {
/*  608 */         throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
/*      */       }
/*      */       
/*  611 */       Buffer packet = new Buffer(buffer);
/*      */       
/*  613 */       if (this.traceProtocol) {
/*  614 */         StringBuilder traceMessageBuf = new StringBuilder();
/*      */         
/*  616 */         traceMessageBuf.append(Messages.getString("MysqlIO.4"));
/*  617 */         traceMessageBuf.append(getPacketDumpToLog(packet, packetLength));
/*      */         
/*  619 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */       
/*  622 */       if (this.enablePacketDebug) {
/*  623 */         enqueuePacketForDebugging(false, false, 0, this.packetHeaderBuf, packet);
/*      */       }
/*      */       
/*  626 */       if (this.connection.getMaintainTimeStats()) {
/*  627 */         this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/*      */       }
/*      */       
/*  630 */       return packet;
/*      */     } catch (IOException ioEx) {
/*  632 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom) {
/*      */       try {
/*  636 */         this.connection.realClose(false, false, true, oom);
/*      */       }
/*      */       catch (Exception ex) {}
/*  639 */       throw oom;
/*      */     }
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
/*      */   protected final Field unpackField(Buffer packet, boolean extractDefaultValues)
/*      */     throws SQLException
/*      */   {
/*  657 */     if (this.use41Extensions)
/*      */     {
/*      */ 
/*  660 */       if (this.has41NewNewProt)
/*      */       {
/*  662 */         int catalogNameStart = packet.getPosition() + 1;
/*  663 */         int catalogNameLength = packet.fastSkipLenString();
/*  664 */         catalogNameStart = adjustStartForFieldLength(catalogNameStart, catalogNameLength);
/*      */       }
/*      */       
/*  667 */       int databaseNameStart = packet.getPosition() + 1;
/*  668 */       int databaseNameLength = packet.fastSkipLenString();
/*  669 */       databaseNameStart = adjustStartForFieldLength(databaseNameStart, databaseNameLength);
/*      */       
/*  671 */       int tableNameStart = packet.getPosition() + 1;
/*  672 */       int tableNameLength = packet.fastSkipLenString();
/*  673 */       tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */       
/*      */ 
/*  676 */       int originalTableNameStart = packet.getPosition() + 1;
/*  677 */       int originalTableNameLength = packet.fastSkipLenString();
/*  678 */       originalTableNameStart = adjustStartForFieldLength(originalTableNameStart, originalTableNameLength);
/*      */       
/*      */ 
/*  681 */       int nameStart = packet.getPosition() + 1;
/*  682 */       int nameLength = packet.fastSkipLenString();
/*      */       
/*  684 */       nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */       
/*      */ 
/*  687 */       int originalColumnNameStart = packet.getPosition() + 1;
/*  688 */       int originalColumnNameLength = packet.fastSkipLenString();
/*  689 */       originalColumnNameStart = adjustStartForFieldLength(originalColumnNameStart, originalColumnNameLength);
/*      */       
/*  691 */       packet.readByte();
/*      */       
/*  693 */       short charSetNumber = (short)packet.readInt();
/*      */       
/*  695 */       long colLength = 0L;
/*      */       
/*  697 */       if (this.has41NewNewProt) {
/*  698 */         colLength = packet.readLong();
/*      */       } else {
/*  700 */         colLength = packet.readLongInt();
/*      */       }
/*      */       
/*  703 */       int colType = packet.readByte() & 0xFF;
/*      */       
/*  705 */       short colFlag = 0;
/*      */       
/*  707 */       if (this.hasLongColumnInfo) {
/*  708 */         colFlag = (short)packet.readInt();
/*      */       } else {
/*  710 */         colFlag = (short)(packet.readByte() & 0xFF);
/*      */       }
/*      */       
/*  713 */       int colDecimals = packet.readByte() & 0xFF;
/*      */       
/*  715 */       int defaultValueStart = -1;
/*  716 */       int defaultValueLength = -1;
/*      */       
/*  718 */       if (extractDefaultValues) {
/*  719 */         defaultValueStart = packet.getPosition() + 1;
/*  720 */         defaultValueLength = packet.fastSkipLenString();
/*      */       }
/*      */       
/*  723 */       Field field = new Field(this.connection, packet.getByteBuffer(), databaseNameStart, databaseNameLength, tableNameStart, tableNameLength, originalTableNameStart, originalTableNameLength, nameStart, nameLength, originalColumnNameStart, originalColumnNameLength, colLength, colType, colFlag, colDecimals, defaultValueStart, defaultValueLength, charSetNumber);
/*      */       
/*      */ 
/*      */ 
/*  727 */       return field;
/*      */     }
/*      */     
/*  730 */     int tableNameStart = packet.getPosition() + 1;
/*  731 */     int tableNameLength = packet.fastSkipLenString();
/*  732 */     tableNameStart = adjustStartForFieldLength(tableNameStart, tableNameLength);
/*      */     
/*  734 */     int nameStart = packet.getPosition() + 1;
/*  735 */     int nameLength = packet.fastSkipLenString();
/*  736 */     nameStart = adjustStartForFieldLength(nameStart, nameLength);
/*      */     
/*  738 */     int colLength = packet.readnBytes();
/*  739 */     int colType = packet.readnBytes();
/*  740 */     packet.readByte();
/*      */     
/*  742 */     short colFlag = 0;
/*      */     
/*  744 */     if (this.hasLongColumnInfo) {
/*  745 */       colFlag = (short)packet.readInt();
/*      */     } else {
/*  747 */       colFlag = (short)(packet.readByte() & 0xFF);
/*      */     }
/*      */     
/*  750 */     int colDecimals = packet.readByte() & 0xFF;
/*      */     
/*  752 */     if (this.colDecimalNeedsBump) {
/*  753 */       colDecimals++;
/*      */     }
/*      */     
/*  756 */     Field field = new Field(this.connection, packet.getByteBuffer(), nameStart, nameLength, tableNameStart, tableNameLength, colLength, colType, colFlag, colDecimals);
/*      */     
/*      */ 
/*  759 */     return field;
/*      */   }
/*      */   
/*      */   private int adjustStartForFieldLength(int nameStart, int nameLength) {
/*  763 */     if (nameLength < 251) {
/*  764 */       return nameStart;
/*      */     }
/*      */     
/*  767 */     if ((nameLength >= 251) && (nameLength < 65536)) {
/*  768 */       return nameStart + 2;
/*      */     }
/*      */     
/*  771 */     if ((nameLength >= 65536) && (nameLength < 16777216)) {
/*  772 */       return nameStart + 3;
/*      */     }
/*      */     
/*  775 */     return nameStart + 8;
/*      */   }
/*      */   
/*      */   protected boolean isSetNeededForAutoCommitMode(boolean autoCommitFlag) {
/*  779 */     if ((this.use41Extensions) && (this.connection.getElideSetAutoCommits())) {
/*  780 */       boolean autoCommitModeOnServer = (this.serverStatus & 0x2) != 0;
/*      */       
/*  782 */       if ((!autoCommitFlag) && (versionMeetsMinimum(5, 0, 0)))
/*      */       {
/*      */ 
/*      */ 
/*  786 */         boolean inTransactionOnServer = (this.serverStatus & 0x1) != 0;
/*      */         
/*  788 */         return !inTransactionOnServer;
/*      */       }
/*      */       
/*  791 */       return autoCommitModeOnServer != autoCommitFlag;
/*      */     }
/*      */     
/*  794 */     return true;
/*      */   }
/*      */   
/*      */   protected boolean inTransactionOnServer() {
/*  798 */     return (this.serverStatus & 0x1) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void changeUser(String userName, String password, String database)
/*      */     throws SQLException
/*      */   {
/*  811 */     this.packetSequence = -1;
/*  812 */     this.compressedPacketSequence = -1;
/*      */     
/*  814 */     int passwordLength = 16;
/*  815 */     int userLength = userName != null ? userName.length() : 0;
/*  816 */     int databaseLength = database != null ? database.length() : 0;
/*      */     
/*  818 */     int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
/*      */     
/*  820 */     if ((this.serverCapabilities & 0x80000) != 0)
/*      */     {
/*  822 */       proceedHandshakeWithPluggableAuthentication(userName, password, database, null);
/*      */     }
/*  824 */     else if ((this.serverCapabilities & 0x8000) != 0) {
/*  825 */       Buffer changeUserPacket = new Buffer(packLength + 1);
/*  826 */       changeUserPacket.writeByte((byte)17);
/*      */       
/*  828 */       if (versionMeetsMinimum(4, 1, 1)) {
/*  829 */         secureAuth411(changeUserPacket, packLength, userName, password, database, false);
/*      */       } else {
/*  831 */         secureAuth(changeUserPacket, packLength, userName, password, database, false);
/*      */       }
/*      */     }
/*      */     else {
/*  835 */       Buffer packet = new Buffer(packLength);
/*  836 */       packet.writeByte((byte)17);
/*      */       
/*      */ 
/*  839 */       packet.writeString(userName);
/*      */       
/*  841 */       if (this.protocolVersion > 9) {
/*  842 */         packet.writeString(Util.newCrypt(password, this.seed, this.connection.getPasswordCharacterEncoding()));
/*      */       } else {
/*  844 */         packet.writeString(Util.oldCrypt(password, this.seed));
/*      */       }
/*      */       
/*  847 */       boolean localUseConnectWithDb = (this.useConnectWithDb) && (database != null) && (database.length() > 0);
/*      */       
/*  849 */       if (localUseConnectWithDb) {
/*  850 */         packet.writeString(database);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  856 */       send(packet, packet.getPosition());
/*  857 */       checkErrorPacket();
/*      */       
/*  859 */       if (!localUseConnectWithDb) {
/*  860 */         changeDatabaseTo(database);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Buffer checkErrorPacket()
/*      */     throws SQLException
/*      */   {
/*  875 */     return checkErrorPacket(-1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkForCharsetMismatch()
/*      */   {
/*  882 */     if ((this.connection.getUseUnicode()) && (this.connection.getEncoding() != null)) {
/*  883 */       String encodingToCheck = jvmPlatformCharset;
/*      */       
/*  885 */       if (encodingToCheck == null) {
/*  886 */         encodingToCheck = System.getProperty("file.encoding");
/*      */       }
/*      */       
/*  889 */       if (encodingToCheck == null) {
/*  890 */         this.platformDbCharsetMatches = false;
/*      */       } else {
/*  892 */         this.platformDbCharsetMatches = encodingToCheck.equals(this.connection.getEncoding());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void clearInputStream()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       int len;
/*  903 */       while (((len = this.mysqlInput.available()) > 0) && (this.mysqlInput.skip(len) > 0L)) {}
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/*  907 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   protected void resetReadPacketSequence()
/*      */   {
/*  913 */     this.readPacketSequence = 0;
/*      */   }
/*      */   
/*      */   protected void dumpPacketRingBuffer() throws SQLException {
/*  917 */     if ((this.packetDebugRingBuffer != null) && (this.connection.getEnablePacketDebug())) {
/*  918 */       StringBuilder dumpBuffer = new StringBuilder();
/*      */       
/*  920 */       dumpBuffer.append("Last " + this.packetDebugRingBuffer.size() + " packets received from server, from oldest->newest:\n");
/*  921 */       dumpBuffer.append("\n");
/*      */       
/*  923 */       for (Iterator<StringBuilder> ringBufIter = this.packetDebugRingBuffer.iterator(); ringBufIter.hasNext();) {
/*  924 */         dumpBuffer.append((CharSequence)ringBufIter.next());
/*  925 */         dumpBuffer.append("\n");
/*      */       }
/*      */       
/*  928 */       this.connection.getLog().logTrace(dumpBuffer.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void explainSlowQuery(byte[] querySQL, String truncatedQuery)
/*      */     throws SQLException
/*      */   {
/*  941 */     if ((StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, "SELECT")) || ((versionMeetsMinimum(5, 6, 3)) && (StringUtils.startsWithIgnoreCaseAndWs(truncatedQuery, EXPLAINABLE_STATEMENT_EXTENSION) != -1)))
/*      */     {
/*      */ 
/*  944 */       PreparedStatement stmt = null;
/*  945 */       ResultSet rs = null;
/*      */       try
/*      */       {
/*  948 */         stmt = (PreparedStatement)this.connection.clientPrepareStatement("EXPLAIN ?");
/*  949 */         stmt.setBytesNoEscapeNoQuotes(1, querySQL);
/*  950 */         rs = stmt.executeQuery();
/*      */         
/*  952 */         StringBuilder explainResults = new StringBuilder(Messages.getString("MysqlIO.8") + truncatedQuery + Messages.getString("MysqlIO.9"));
/*      */         
/*  954 */         ResultSetUtil.appendResultSetSlashGStyle(explainResults, rs);
/*      */         
/*  956 */         this.connection.getLog().logWarn(explainResults.toString());
/*      */       }
/*      */       catch (SQLException sqlEx) {}finally {
/*  959 */         if (rs != null) {
/*  960 */           rs.close();
/*      */         }
/*      */         
/*  963 */         if (stmt != null) {
/*  964 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static int getMaxBuf() {
/*  971 */     return maxBufferSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   final int getServerMajorVersion()
/*      */   {
/*  978 */     return this.serverMajorVersion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   final int getServerMinorVersion()
/*      */   {
/*  985 */     return this.serverMinorVersion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   final int getServerSubMinorVersion()
/*      */   {
/*  992 */     return this.serverSubMinorVersion;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   String getServerVersion()
/*      */   {
/*  999 */     return this.serverVersion;
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
/*      */   void doHandshake(String user, String password, String database)
/*      */     throws SQLException
/*      */   {
/* 1015 */     this.checkPacketSequence = false;
/* 1016 */     this.readPacketSequence = 0;
/*      */     
/* 1018 */     Buffer buf = readPacket();
/*      */     
/*      */ 
/* 1021 */     this.protocolVersion = buf.readByte();
/*      */     
/* 1023 */     if (this.protocolVersion == -1) {
/*      */       try {
/* 1025 */         this.mysqlConnection.close();
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/* 1030 */       int errno = 2000;
/*      */       
/* 1032 */       errno = buf.readInt();
/*      */       
/* 1034 */       String serverErrorMessage = buf.readString("ASCII", getExceptionInterceptor());
/*      */       
/* 1036 */       StringBuilder errorBuf = new StringBuilder(Messages.getString("MysqlIO.10"));
/* 1037 */       errorBuf.append(serverErrorMessage);
/* 1038 */       errorBuf.append("\"");
/*      */       
/* 1040 */       String xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */       
/* 1042 */       throw SQLError.createSQLException(SQLError.get(xOpen) + ", " + errorBuf.toString(), xOpen, errno, getExceptionInterceptor());
/*      */     }
/*      */     
/* 1045 */     this.serverVersion = buf.readString("ASCII", getExceptionInterceptor());
/*      */     
/*      */ 
/* 1048 */     int point = this.serverVersion.indexOf('.');
/*      */     
/* 1050 */     if (point != -1) {
/*      */       try {
/* 1052 */         int n = Integer.parseInt(this.serverVersion.substring(0, point));
/* 1053 */         this.serverMajorVersion = n;
/*      */       }
/*      */       catch (NumberFormatException NFE1) {}
/*      */       
/*      */ 
/* 1058 */       String remaining = this.serverVersion.substring(point + 1, this.serverVersion.length());
/* 1059 */       point = remaining.indexOf('.');
/*      */       
/* 1061 */       if (point != -1) {
/*      */         try {
/* 1063 */           int n = Integer.parseInt(remaining.substring(0, point));
/* 1064 */           this.serverMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe) {}
/*      */         
/*      */ 
/* 1069 */         remaining = remaining.substring(point + 1, remaining.length());
/*      */         
/* 1071 */         int pos = 0;
/*      */         
/* 1073 */         while ((pos < remaining.length()) && 
/* 1074 */           (remaining.charAt(pos) >= '0') && (remaining.charAt(pos) <= '9'))
/*      */         {
/*      */ 
/*      */ 
/* 1078 */           pos++;
/*      */         }
/*      */         try
/*      */         {
/* 1082 */           int n = Integer.parseInt(remaining.substring(0, pos));
/* 1083 */           this.serverSubMinorVersion = n;
/*      */         }
/*      */         catch (NumberFormatException nfe) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1090 */     if (versionMeetsMinimum(4, 0, 8)) {
/* 1091 */       this.maxThreeBytes = 16777215;
/* 1092 */       this.useNewLargePackets = true;
/*      */     } else {
/* 1094 */       this.maxThreeBytes = 16581375;
/* 1095 */       this.useNewLargePackets = false;
/*      */     }
/*      */     
/* 1098 */     this.colDecimalNeedsBump = versionMeetsMinimum(3, 23, 0);
/* 1099 */     this.colDecimalNeedsBump = (!versionMeetsMinimum(3, 23, 15));
/* 1100 */     this.useNewUpdateCounts = versionMeetsMinimum(3, 22, 5);
/*      */     
/*      */ 
/* 1103 */     this.threadId = buf.readLong();
/*      */     
/* 1105 */     if (this.protocolVersion > 9)
/*      */     {
/* 1107 */       this.seed = buf.readString("ASCII", getExceptionInterceptor(), 8);
/*      */       
/* 1109 */       buf.readByte();
/*      */     }
/*      */     else {
/* 1112 */       this.seed = buf.readString("ASCII", getExceptionInterceptor());
/*      */     }
/*      */     
/* 1115 */     this.serverCapabilities = 0;
/*      */     
/*      */ 
/* 1118 */     if (buf.getPosition() < buf.getBufLength()) {
/* 1119 */       this.serverCapabilities = buf.readInt();
/*      */     }
/*      */     
/* 1122 */     if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0)))
/*      */     {
/*      */ 
/*      */ 
/* 1126 */       this.serverCharsetIndex = (buf.readByte() & 0xFF);
/*      */       
/* 1128 */       this.serverStatus = buf.readInt();
/* 1129 */       checkTransactionState(0);
/*      */       
/*      */ 
/* 1132 */       this.serverCapabilities |= buf.readInt() << 16;
/*      */       
/* 1134 */       if ((this.serverCapabilities & 0x80000) != 0)
/*      */       {
/* 1136 */         this.authPluginDataLength = (buf.readByte() & 0xFF);
/*      */       }
/*      */       else {
/* 1139 */         buf.readByte();
/*      */       }
/*      */       
/* 1142 */       buf.setPosition(buf.getPosition() + 10);
/*      */       
/* 1144 */       if ((this.serverCapabilities & 0x8000) != 0) {
/*      */         StringBuilder newSeed;
/*      */         String seedPart2;
/*      */         StringBuilder newSeed;
/* 1148 */         if (this.authPluginDataLength > 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1155 */           String seedPart2 = buf.readString("ASCII", getExceptionInterceptor(), this.authPluginDataLength - 8);
/* 1156 */           newSeed = new StringBuilder(this.authPluginDataLength);
/*      */         } else {
/* 1158 */           seedPart2 = buf.readString("ASCII", getExceptionInterceptor());
/* 1159 */           newSeed = new StringBuilder(20);
/*      */         }
/* 1161 */         newSeed.append(this.seed);
/* 1162 */         newSeed.append(seedPart2);
/* 1163 */         this.seed = newSeed.toString();
/*      */       }
/*      */     }
/*      */     
/* 1167 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression())) {
/* 1168 */       this.clientParam |= 0x20;
/*      */     }
/*      */     
/* 1171 */     this.useConnectWithDb = ((database != null) && (database.length() > 0) && (!this.connection.getCreateDatabaseIfNotExist()));
/*      */     
/* 1173 */     if (this.useConnectWithDb) {
/* 1174 */       this.clientParam |= 0x8;
/*      */     }
/*      */     
/*      */ 
/* 1178 */     if ((versionMeetsMinimum(5, 7, 0)) && (!this.connection.getUseSSL()) && (!this.connection.isUseSSLExplicit())) {
/* 1179 */       this.connection.setUseSSL(true);
/* 1180 */       this.connection.setVerifyServerCertificate(false);
/* 1181 */       this.connection.getLog().logWarn(Messages.getString("MysqlIO.SSLWarning"));
/*      */     }
/*      */     
/*      */ 
/* 1185 */     if (((this.serverCapabilities & 0x800) == 0) && (this.connection.getUseSSL())) {
/* 1186 */       if (this.connection.getRequireSSL()) {
/* 1187 */         this.connection.close();
/* 1188 */         forceClose();
/* 1189 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.15"), "08001", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1193 */       this.connection.setUseSSL(false);
/*      */     }
/*      */     
/* 1196 */     if ((this.serverCapabilities & 0x4) != 0)
/*      */     {
/* 1198 */       this.clientParam |= 0x4;
/* 1199 */       this.hasLongColumnInfo = true;
/*      */     }
/*      */     
/*      */ 
/* 1203 */     if (!this.connection.getUseAffectedRows()) {
/* 1204 */       this.clientParam |= 0x2;
/*      */     }
/*      */     
/* 1207 */     if (this.connection.getAllowLoadLocalInfile()) {
/* 1208 */       this.clientParam |= 0x80;
/*      */     }
/*      */     
/* 1211 */     if (this.isInteractiveClient) {
/* 1212 */       this.clientParam |= 0x400;
/*      */     }
/*      */     
/* 1215 */     if (((this.serverCapabilities & 0x800000) == 0) || 
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1220 */       ((this.serverCapabilities & 0x1000000) != 0)) {
/* 1221 */       this.clientParam |= 0x1000000;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1227 */     if ((this.serverCapabilities & 0x80000) != 0) {
/* 1228 */       proceedHandshakeWithPluggableAuthentication(user, password, database, buf);
/* 1229 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1233 */     if (this.protocolVersion > 9) {
/* 1234 */       this.clientParam |= 1L;
/*      */     } else {
/* 1236 */       this.clientParam &= 0xFFFFFFFFFFFFFFFE;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1242 */     if ((versionMeetsMinimum(4, 1, 0)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x4000) != 0))) {
/* 1243 */       if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1244 */         this.clientParam |= 0x200;
/* 1245 */         this.has41NewNewProt = true;
/*      */         
/*      */ 
/* 1248 */         this.clientParam |= 0x2000;
/*      */         
/*      */ 
/* 1251 */         this.clientParam |= 0x20000;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1256 */         if (this.connection.getAllowMultiQueries()) {
/* 1257 */           this.clientParam |= 0x10000;
/*      */         }
/*      */       } else {
/* 1260 */         this.clientParam |= 0x4000;
/* 1261 */         this.has41NewNewProt = false;
/*      */       }
/*      */       
/* 1264 */       this.use41Extensions = true;
/*      */     }
/*      */     
/* 1267 */     int passwordLength = 16;
/* 1268 */     int userLength = user != null ? user.length() : 0;
/* 1269 */     int databaseLength = database != null ? database.length() : 0;
/*      */     
/* 1271 */     int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
/*      */     
/* 1273 */     Buffer packet = null;
/*      */     
/* 1275 */     if (!this.connection.getUseSSL()) {
/* 1276 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1277 */         this.clientParam |= 0x8000;
/*      */         
/* 1279 */         if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1280 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         } else {
/* 1282 */           secureAuth(null, packLength, user, password, database, true);
/*      */         }
/*      */       }
/*      */       else {
/* 1286 */         packet = new Buffer(packLength);
/*      */         
/* 1288 */         if ((this.clientParam & 0x4000) != 0L) {
/* 1289 */           if ((versionMeetsMinimum(4, 1, 1)) || ((this.protocolVersion > 9) && ((this.serverCapabilities & 0x200) != 0))) {
/* 1290 */             packet.writeLong(this.clientParam);
/* 1291 */             packet.writeLong(this.maxThreeBytes);
/*      */             
/*      */ 
/* 1294 */             packet.writeByte((byte)8);
/*      */             
/*      */ 
/* 1297 */             packet.writeBytesNoNull(new byte[23]);
/*      */           } else {
/* 1299 */             packet.writeLong(this.clientParam);
/* 1300 */             packet.writeLong(this.maxThreeBytes);
/*      */           }
/*      */         } else {
/* 1303 */           packet.writeInt((int)this.clientParam);
/* 1304 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */         
/*      */ 
/* 1308 */         packet.writeString(user, "Cp1252", this.connection);
/*      */         
/* 1310 */         if (this.protocolVersion > 9) {
/* 1311 */           packet.writeString(Util.newCrypt(password, this.seed, this.connection.getPasswordCharacterEncoding()), "Cp1252", this.connection);
/*      */         } else {
/* 1313 */           packet.writeString(Util.oldCrypt(password, this.seed), "Cp1252", this.connection);
/*      */         }
/*      */         
/* 1316 */         if (this.useConnectWithDb) {
/* 1317 */           packet.writeString(database, "Cp1252", this.connection);
/*      */         }
/*      */         
/* 1320 */         send(packet, packet.getPosition());
/*      */       }
/*      */     } else {
/* 1323 */       negotiateSSLConnection(user, password, database, packLength);
/*      */       
/* 1325 */       if ((this.serverCapabilities & 0x8000) != 0) {
/* 1326 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 1327 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         } else {
/* 1329 */           secureAuth411(null, packLength, user, password, database, true);
/*      */         }
/*      */       }
/*      */       else {
/* 1333 */         packet = new Buffer(packLength);
/*      */         
/* 1335 */         if (this.use41Extensions) {
/* 1336 */           packet.writeLong(this.clientParam);
/* 1337 */           packet.writeLong(this.maxThreeBytes);
/*      */         } else {
/* 1339 */           packet.writeInt((int)this.clientParam);
/* 1340 */           packet.writeLongInt(this.maxThreeBytes);
/*      */         }
/*      */         
/*      */ 
/* 1344 */         packet.writeString(user);
/*      */         
/* 1346 */         if (this.protocolVersion > 9) {
/* 1347 */           packet.writeString(Util.newCrypt(password, this.seed, this.connection.getPasswordCharacterEncoding()));
/*      */         } else {
/* 1349 */           packet.writeString(Util.oldCrypt(password, this.seed));
/*      */         }
/*      */         
/* 1352 */         if (((this.serverCapabilities & 0x8) != 0) && (database != null) && (database.length() > 0)) {
/* 1353 */           packet.writeString(database);
/*      */         }
/*      */         
/* 1356 */         send(packet, packet.getPosition());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1361 */     if ((!versionMeetsMinimum(4, 1, 1)) || (this.protocolVersion <= 9) || ((this.serverCapabilities & 0x200) == 0)) {
/* 1362 */       checkErrorPacket();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1368 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()) && (!(this.mysqlInput instanceof CompressedInputStream)))
/*      */     {
/* 1370 */       this.deflater = new Deflater();
/* 1371 */       this.useCompression = true;
/* 1372 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */     
/* 1375 */     if (!this.useConnectWithDb) {
/* 1376 */       changeDatabaseTo(database);
/*      */     }
/*      */     try
/*      */     {
/* 1380 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/*      */     } catch (IOException ioEx) {
/* 1382 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1392 */   private Map<String, AuthenticationPlugin> authenticationPlugins = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1397 */   private List<String> disabledAuthenticationPlugins = null;
/*      */   
/*      */ 
/*      */ 
/* 1401 */   private String clientDefaultAuthenticationPlugin = null;
/*      */   
/*      */ 
/*      */ 
/* 1405 */   private String clientDefaultAuthenticationPluginName = null;
/*      */   
/*      */ 
/*      */ 
/* 1409 */   private String serverDefaultAuthenticationPluginName = null;
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
/*      */   private void loadAuthenticationPlugins()
/*      */     throws SQLException
/*      */   {
/* 1430 */     this.clientDefaultAuthenticationPlugin = this.connection.getDefaultAuthenticationPlugin();
/* 1431 */     if ((this.clientDefaultAuthenticationPlugin == null) || ("".equals(this.clientDefaultAuthenticationPlugin.trim()))) {
/* 1432 */       throw SQLError.createSQLException(Messages.getString("Connection.BadDefaultAuthenticationPlugin", new Object[] { this.clientDefaultAuthenticationPlugin }), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1438 */     String disabledPlugins = this.connection.getDisabledAuthenticationPlugins();
/* 1439 */     if ((disabledPlugins != null) && (!"".equals(disabledPlugins))) {
/* 1440 */       this.disabledAuthenticationPlugins = new ArrayList();
/* 1441 */       List<String> pluginsToDisable = StringUtils.split(disabledPlugins, ",", true);
/* 1442 */       Iterator<String> iter = pluginsToDisable.iterator();
/* 1443 */       while (iter.hasNext()) {
/* 1444 */         this.disabledAuthenticationPlugins.add(iter.next());
/*      */       }
/*      */     }
/*      */     
/* 1448 */     this.authenticationPlugins = new HashMap();
/*      */     
/*      */ 
/* 1451 */     AuthenticationPlugin plugin = new MysqlOldPasswordPlugin();
/* 1452 */     plugin.init(this.connection, this.connection.getProperties());
/* 1453 */     boolean defaultIsFound = addAuthenticationPlugin(plugin);
/*      */     
/* 1455 */     plugin = new MysqlNativePasswordPlugin();
/* 1456 */     plugin.init(this.connection, this.connection.getProperties());
/* 1457 */     if (addAuthenticationPlugin(plugin)) {
/* 1458 */       defaultIsFound = true;
/*      */     }
/*      */     
/* 1461 */     plugin = new MysqlClearPasswordPlugin();
/* 1462 */     plugin.init(this.connection, this.connection.getProperties());
/* 1463 */     if (addAuthenticationPlugin(plugin)) {
/* 1464 */       defaultIsFound = true;
/*      */     }
/*      */     
/* 1467 */     plugin = new Sha256PasswordPlugin();
/* 1468 */     plugin.init(this.connection, this.connection.getProperties());
/* 1469 */     if (addAuthenticationPlugin(plugin)) {
/* 1470 */       defaultIsFound = true;
/*      */     }
/*      */     
/*      */ 
/* 1474 */     String authenticationPluginClasses = this.connection.getAuthenticationPlugins();
/* 1475 */     if ((authenticationPluginClasses != null) && (!"".equals(authenticationPluginClasses)))
/*      */     {
/* 1477 */       List<Extension> plugins = Util.loadExtensions(this.connection, this.connection.getProperties(), authenticationPluginClasses, "Connection.BadAuthenticationPlugin", getExceptionInterceptor());
/*      */       
/*      */ 
/* 1480 */       for (Extension object : plugins) {
/* 1481 */         plugin = (AuthenticationPlugin)object;
/* 1482 */         if (addAuthenticationPlugin(plugin)) {
/* 1483 */           defaultIsFound = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1489 */     if (!defaultIsFound) {
/* 1490 */       throw SQLError.createSQLException(Messages.getString("Connection.DefaultAuthenticationPluginIsNotListed", new Object[] { this.clientDefaultAuthenticationPlugin }), getExceptionInterceptor());
/*      */     }
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
/*      */   private boolean addAuthenticationPlugin(AuthenticationPlugin plugin)
/*      */     throws SQLException
/*      */   {
/* 1508 */     boolean isDefault = false;
/* 1509 */     String pluginClassName = plugin.getClass().getName();
/* 1510 */     String pluginProtocolName = plugin.getProtocolPluginName();
/* 1511 */     boolean disabledByClassName = (this.disabledAuthenticationPlugins != null) && (this.disabledAuthenticationPlugins.contains(pluginClassName));
/* 1512 */     boolean disabledByMechanism = (this.disabledAuthenticationPlugins != null) && (this.disabledAuthenticationPlugins.contains(pluginProtocolName));
/*      */     
/* 1514 */     if ((disabledByClassName) || (disabledByMechanism))
/*      */     {
/* 1516 */       if (this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
/* 1517 */         throw SQLError.createSQLException(Messages.getString("Connection.BadDisabledAuthenticationPlugin", new Object[] { disabledByClassName ? pluginClassName : pluginProtocolName }), getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     else {
/* 1521 */       this.authenticationPlugins.put(pluginProtocolName, plugin);
/* 1522 */       if (this.clientDefaultAuthenticationPlugin.equals(pluginClassName)) {
/* 1523 */         this.clientDefaultAuthenticationPluginName = pluginProtocolName;
/* 1524 */         isDefault = true;
/*      */       }
/*      */     }
/* 1527 */     return isDefault;
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
/*      */   private AuthenticationPlugin getAuthenticationPlugin(String pluginName)
/*      */     throws SQLException
/*      */   {
/* 1548 */     AuthenticationPlugin plugin = (AuthenticationPlugin)this.authenticationPlugins.get(pluginName);
/*      */     
/* 1550 */     if ((plugin != null) && (!plugin.isReusable())) {
/*      */       try {
/* 1552 */         plugin = (AuthenticationPlugin)plugin.getClass().newInstance();
/* 1553 */         plugin.init(this.connection, this.connection.getProperties());
/*      */       } catch (Throwable t) {
/* 1555 */         SQLException sqlEx = SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[] { plugin.getClass().getName() }), getExceptionInterceptor());
/*      */         
/* 1557 */         sqlEx.initCause(t);
/* 1558 */         throw sqlEx;
/*      */       }
/*      */     }
/*      */     
/* 1562 */     return plugin;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkConfidentiality(AuthenticationPlugin plugin)
/*      */     throws SQLException
/*      */   {
/* 1572 */     if ((plugin.requiresConfidentiality()) && (!isSSLEstablished())) {
/* 1573 */       throw SQLError.createSQLException(Messages.getString("Connection.AuthenticationPluginRequiresSSL", new Object[] { plugin.getProtocolPluginName() }), getExceptionInterceptor());
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void proceedHandshakeWithPluggableAuthentication(String user, String password, String database, Buffer challenge)
/*      */     throws SQLException
/*      */   {
/* 1603 */     if (this.authenticationPlugins == null) {
/* 1604 */       loadAuthenticationPlugins();
/*      */     }
/*      */     
/* 1607 */     boolean skipPassword = false;
/* 1608 */     int passwordLength = 16;
/* 1609 */     int userLength = user != null ? user.length() : 0;
/* 1610 */     int databaseLength = database != null ? database.length() : 0;
/*      */     
/* 1612 */     int packLength = (userLength + passwordLength + databaseLength) * 3 + 7 + 4 + 33;
/*      */     
/* 1614 */     AuthenticationPlugin plugin = null;
/* 1615 */     Buffer fromServer = null;
/* 1616 */     ArrayList<Buffer> toServer = new ArrayList();
/* 1617 */     boolean done = false;
/* 1618 */     Buffer last_sent = null;
/*      */     
/* 1620 */     boolean old_raw_challenge = false;
/*      */     
/* 1622 */     int counter = 100;
/*      */     
/* 1624 */     while (0 < counter--)
/*      */     {
/* 1626 */       if (!done)
/*      */       {
/* 1628 */         if (challenge != null)
/*      */         {
/* 1630 */           if (challenge.isOKPacket()) {
/* 1631 */             throw SQLError.createSQLException(Messages.getString("Connection.UnexpectedAuthenticationApproval", new Object[] { plugin.getProtocolPluginName() }), getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1638 */           this.clientParam |= 0xAA201;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1643 */           if (this.connection.getAllowMultiQueries()) {
/* 1644 */             this.clientParam |= 0x10000;
/*      */           }
/*      */           
/* 1647 */           if (((this.serverCapabilities & 0x400000) != 0) && (!this.connection.getDisconnectOnExpiredPasswords())) {
/* 1648 */             this.clientParam |= 0x400000;
/*      */           }
/* 1650 */           if (((this.serverCapabilities & 0x100000) != 0) && (!"none".equals(this.connection.getConnectionAttributes()))) {
/* 1651 */             this.clientParam |= 0x100000;
/*      */           }
/* 1653 */           if ((this.serverCapabilities & 0x200000) != 0) {
/* 1654 */             this.clientParam |= 0x200000;
/*      */           }
/*      */           
/* 1657 */           this.has41NewNewProt = true;
/* 1658 */           this.use41Extensions = true;
/*      */           
/* 1660 */           if (this.connection.getUseSSL()) {
/* 1661 */             negotiateSSLConnection(user, password, database, packLength);
/*      */           }
/*      */           
/* 1664 */           String pluginName = null;
/*      */           
/* 1666 */           if ((this.serverCapabilities & 0x80000) != 0) {
/* 1667 */             if ((!versionMeetsMinimum(5, 5, 10)) || ((versionMeetsMinimum(5, 6, 0)) && (!versionMeetsMinimum(5, 6, 2)))) {
/* 1668 */               pluginName = challenge.readString("ASCII", getExceptionInterceptor(), this.authPluginDataLength);
/*      */             } else {
/* 1670 */               pluginName = challenge.readString("ASCII", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           
/* 1674 */           plugin = getAuthenticationPlugin(pluginName);
/* 1675 */           if (plugin == null)
/*      */           {
/*      */ 
/*      */ 
/* 1679 */             plugin = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
/* 1680 */           } else if ((pluginName.equals(Sha256PasswordPlugin.PLUGIN_NAME)) && (!isSSLEstablished()) && (this.connection.getServerRSAPublicKeyFile() == null) && (!this.connection.getAllowPublicKeyRetrieval()))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1688 */             plugin = getAuthenticationPlugin(this.clientDefaultAuthenticationPluginName);
/* 1689 */             skipPassword = !this.clientDefaultAuthenticationPluginName.equals(pluginName);
/*      */           }
/*      */           
/* 1692 */           this.serverDefaultAuthenticationPluginName = plugin.getProtocolPluginName();
/*      */           
/* 1694 */           checkConfidentiality(plugin);
/* 1695 */           fromServer = new Buffer(StringUtils.getBytes(this.seed));
/*      */         }
/*      */         else {
/* 1698 */           plugin = getAuthenticationPlugin(this.serverDefaultAuthenticationPluginName == null ? this.clientDefaultAuthenticationPluginName : this.serverDefaultAuthenticationPluginName);
/*      */           
/*      */ 
/* 1701 */           checkConfidentiality(plugin);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1706 */           fromServer = new Buffer(StringUtils.getBytes(this.seed));
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 1712 */         challenge = checkErrorPacket();
/* 1713 */         old_raw_challenge = false;
/* 1714 */         this.packetSequence = ((byte)(this.packetSequence + 1));
/* 1715 */         this.compressedPacketSequence = ((byte)(this.compressedPacketSequence + 1));
/*      */         
/* 1717 */         if (plugin == null)
/*      */         {
/*      */ 
/* 1720 */           plugin = getAuthenticationPlugin(this.serverDefaultAuthenticationPluginName != null ? this.serverDefaultAuthenticationPluginName : this.clientDefaultAuthenticationPluginName);
/*      */         }
/*      */         
/*      */ 
/* 1724 */         if (challenge.isOKPacket())
/*      */         {
/* 1726 */           plugin.destroy();
/* 1727 */           break;
/*      */         }
/* 1729 */         if (challenge.isAuthMethodSwitchRequestPacket()) {
/* 1730 */           skipPassword = false;
/*      */           
/*      */ 
/* 1733 */           String pluginName = challenge.readString("ASCII", getExceptionInterceptor());
/*      */           
/*      */ 
/* 1736 */           if (!plugin.getProtocolPluginName().equals(pluginName)) {
/* 1737 */             plugin.destroy();
/* 1738 */             plugin = getAuthenticationPlugin(pluginName);
/*      */             
/* 1740 */             if (plugin == null) {
/* 1741 */               throw SQLError.createSQLException(Messages.getString("Connection.BadAuthenticationPlugin", new Object[] { pluginName }), getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1746 */           checkConfidentiality(plugin);
/* 1747 */           fromServer = new Buffer(StringUtils.getBytes(challenge.readString("ASCII", getExceptionInterceptor())));
/*      */ 
/*      */ 
/*      */         }
/* 1751 */         else if (versionMeetsMinimum(5, 5, 16)) {
/* 1752 */           fromServer = new Buffer(challenge.getBytes(challenge.getPosition(), challenge.getBufLength() - challenge.getPosition()));
/*      */         } else {
/* 1754 */           old_raw_challenge = true;
/* 1755 */           fromServer = new Buffer(challenge.getBytes(challenge.getPosition() - 1, challenge.getBufLength() - challenge.getPosition() + 1));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1763 */         plugin.setAuthenticationParameters(user, skipPassword ? null : password);
/* 1764 */         done = plugin.nextAuthenticationStep(fromServer, toServer);
/*      */       } catch (SQLException e) {
/* 1766 */         throw SQLError.createSQLException(e.getMessage(), e.getSQLState(), e, getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 1770 */       if (toServer.size() > 0) {
/* 1771 */         if (challenge == null) {
/* 1772 */           String enc = getEncodingForHandshake();
/*      */           
/*      */ 
/* 1775 */           last_sent = new Buffer(packLength + 1);
/* 1776 */           last_sent.writeByte((byte)17);
/*      */           
/*      */ 
/* 1779 */           last_sent.writeString(user, enc, this.connection);
/*      */           
/*      */ 
/* 1782 */           if (((Buffer)toServer.get(0)).getBufLength() < 256)
/*      */           {
/* 1784 */             last_sent.writeByte((byte)((Buffer)toServer.get(0)).getBufLength());
/* 1785 */             last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/*      */           } else {
/* 1787 */             last_sent.writeByte((byte)0);
/*      */           }
/*      */           
/* 1790 */           if (this.useConnectWithDb) {
/* 1791 */             last_sent.writeString(database, enc, this.connection);
/*      */           }
/*      */           else {
/* 1794 */             last_sent.writeByte((byte)0);
/*      */           }
/*      */           
/* 1797 */           appendCharsetByteForHandshake(last_sent, enc);
/*      */           
/* 1799 */           last_sent.writeByte((byte)0);
/*      */           
/*      */ 
/* 1802 */           if ((this.serverCapabilities & 0x80000) != 0) {
/* 1803 */             last_sent.writeString(plugin.getProtocolPluginName(), enc, this.connection);
/*      */           }
/*      */           
/*      */ 
/* 1807 */           if ((this.clientParam & 0x100000) != 0L) {
/* 1808 */             sendConnectionAttributes(last_sent, enc, this.connection);
/* 1809 */             last_sent.writeByte((byte)0);
/*      */           }
/*      */           
/* 1812 */           send(last_sent, last_sent.getPosition());
/*      */         }
/* 1814 */         else if (challenge.isAuthMethodSwitchRequestPacket())
/*      */         {
/* 1816 */           last_sent = new Buffer(((Buffer)toServer.get(0)).getBufLength() + 4);
/* 1817 */           last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/* 1818 */           send(last_sent, last_sent.getPosition());
/*      */         }
/* 1820 */         else if ((challenge.isRawPacket()) || (old_raw_challenge))
/*      */         {
/* 1822 */           for (Buffer buffer : toServer) {
/* 1823 */             last_sent = new Buffer(buffer.getBufLength() + 4);
/* 1824 */             last_sent.writeBytesNoNull(buffer.getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/* 1825 */             send(last_sent, last_sent.getPosition());
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1830 */           String enc = getEncodingForHandshake();
/*      */           
/* 1832 */           last_sent = new Buffer(packLength);
/* 1833 */           last_sent.writeLong(this.clientParam);
/* 1834 */           last_sent.writeLong(this.maxThreeBytes);
/*      */           
/* 1836 */           appendCharsetByteForHandshake(last_sent, enc);
/*      */           
/* 1838 */           last_sent.writeBytesNoNull(new byte[23]);
/*      */           
/*      */ 
/* 1841 */           last_sent.writeString(user, enc, this.connection);
/*      */           
/* 1843 */           if ((this.serverCapabilities & 0x200000) != 0)
/*      */           {
/* 1845 */             last_sent.writeLenBytes(((Buffer)toServer.get(0)).getBytes(((Buffer)toServer.get(0)).getBufLength()));
/*      */           }
/*      */           else {
/* 1848 */             last_sent.writeByte((byte)((Buffer)toServer.get(0)).getBufLength());
/* 1849 */             last_sent.writeBytesNoNull(((Buffer)toServer.get(0)).getByteBuffer(), 0, ((Buffer)toServer.get(0)).getBufLength());
/*      */           }
/*      */           
/* 1852 */           if (this.useConnectWithDb) {
/* 1853 */             last_sent.writeString(database, enc, this.connection);
/*      */           }
/*      */           else {
/* 1856 */             last_sent.writeByte((byte)0);
/*      */           }
/*      */           
/* 1859 */           if ((this.serverCapabilities & 0x80000) != 0) {
/* 1860 */             last_sent.writeString(plugin.getProtocolPluginName(), enc, this.connection);
/*      */           }
/*      */           
/*      */ 
/* 1864 */           if ((this.clientParam & 0x100000) != 0L) {
/* 1865 */             sendConnectionAttributes(last_sent, enc, this.connection);
/*      */           }
/*      */           
/* 1868 */           send(last_sent, last_sent.getPosition());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1875 */     if (counter == 0) {
/* 1876 */       throw SQLError.createSQLException(Messages.getString("CommunicationsException.TooManyAuthenticationPluginNegotiations"), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1882 */     if (((this.serverCapabilities & 0x20) != 0) && (this.connection.getUseCompression()) && (!(this.mysqlInput instanceof CompressedInputStream)))
/*      */     {
/* 1884 */       this.deflater = new Deflater();
/* 1885 */       this.useCompression = true;
/* 1886 */       this.mysqlInput = new CompressedInputStream(this.connection, this.mysqlInput);
/*      */     }
/*      */     
/* 1889 */     if (!this.useConnectWithDb) {
/* 1890 */       changeDatabaseTo(database);
/*      */     }
/*      */     try
/*      */     {
/* 1894 */       this.mysqlConnection = this.socketFactory.afterHandshake();
/*      */     } catch (IOException ioEx) {
/* 1896 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   private Properties getConnectionAttributesAsProperties(String atts)
/*      */     throws SQLException
/*      */   {
/* 1903 */     Properties props = new Properties();
/*      */     
/* 1905 */     if (atts != null) {
/* 1906 */       String[] pairs = atts.split(",");
/* 1907 */       for (String pair : pairs) {
/* 1908 */         int keyEnd = pair.indexOf(":");
/* 1909 */         if ((keyEnd > 0) && (keyEnd + 1 < pair.length())) {
/* 1910 */           props.setProperty(pair.substring(0, keyEnd), pair.substring(keyEnd + 1));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1918 */     props.setProperty("_client_name", "MySQL Connector Java");
/* 1919 */     props.setProperty("_client_version", "5.1.39");
/* 1920 */     props.setProperty("_runtime_vendor", NonRegisteringDriver.RUNTIME_VENDOR);
/* 1921 */     props.setProperty("_runtime_version", NonRegisteringDriver.RUNTIME_VERSION);
/* 1922 */     props.setProperty("_client_license", "GPL");
/*      */     
/* 1924 */     return props;
/*      */   }
/*      */   
/*      */   private void sendConnectionAttributes(Buffer buf, String enc, MySQLConnection conn) throws SQLException {
/* 1928 */     String atts = conn.getConnectionAttributes();
/*      */     
/* 1930 */     Buffer lb = new Buffer(100);
/*      */     Properties props;
/*      */     try {
/* 1933 */       props = getConnectionAttributesAsProperties(atts);
/*      */       
/* 1935 */       for (Object key : props.keySet()) {
/* 1936 */         lb.writeLenString((String)key, enc, conn.getServerCharset(), null, conn.parserKnowsUnicode(), conn);
/* 1937 */         lb.writeLenString(props.getProperty((String)key), enc, conn.getServerCharset(), null, conn.parserKnowsUnicode(), conn);
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*      */ 
/*      */ 
/* 1944 */     buf.writeByte((byte)(lb.getPosition() - 4));
/* 1945 */     buf.writeBytesNoNull(lb.getByteBuffer(), 4, lb.getBufLength() - 4);
/*      */   }
/*      */   
/*      */   private void changeDatabaseTo(String database) throws SQLException
/*      */   {
/* 1950 */     if ((database == null) || (database.length() == 0)) {
/* 1951 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1955 */       sendCommand(2, database, null, false, null, 0);
/*      */     } catch (Exception ex) {
/* 1957 */       if (this.connection.getCreateDatabaseIfNotExist()) {
/* 1958 */         sendCommand(3, "CREATE DATABASE IF NOT EXISTS " + database, null, false, null, 0);
/* 1959 */         sendCommand(2, database, null, false, null, 0);
/*      */       } else {
/* 1961 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */     }
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
/*      */   final ResultSetRow nextRow(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacketForBufferRow, Buffer existingRowPacket)
/*      */     throws SQLException
/*      */   {
/* 1983 */     if ((this.useDirectRowUnpack) && (existingRowPacket == null) && (!isBinaryEncoded) && (!useBufferRowIfPossible) && (!useBufferRowExplicit)) {
/* 1984 */       return nextRowFast(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacketForBufferRow);
/*      */     }
/*      */     
/*      */ 
/* 1988 */     Buffer rowPacket = null;
/*      */     
/* 1990 */     if (existingRowPacket == null) {
/* 1991 */       rowPacket = checkErrorPacket();
/*      */       
/* 1993 */       if ((!useBufferRowExplicit) && (useBufferRowIfPossible) && 
/* 1994 */         (rowPacket.getBufLength() > this.useBufferRowSizeThreshold)) {
/* 1995 */         useBufferRowExplicit = true;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 2000 */       rowPacket = existingRowPacket;
/* 2001 */       checkErrorPacket(existingRowPacket);
/*      */     }
/*      */     
/* 2004 */     if (!isBinaryEncoded)
/*      */     {
/*      */ 
/*      */ 
/* 2008 */       rowPacket.setPosition(rowPacket.getPosition() - 1);
/*      */       
/* 2010 */       if (((isEOFDeprecated()) || (!rowPacket.isEOFPacket())) && ((!isEOFDeprecated()) || (!rowPacket.isResultSetOKPacket()))) {
/* 2011 */         if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit)))
/*      */         {
/* 2013 */           byte[][] rowData = new byte[columnCount][];
/*      */           
/* 2015 */           for (int i = 0; i < columnCount; i++) {
/* 2016 */             rowData[i] = rowPacket.readLenByteArray(0);
/*      */           }
/*      */           
/* 2019 */           return new ByteArrayRow(rowData, getExceptionInterceptor());
/*      */         }
/*      */         
/* 2022 */         if (!canReuseRowPacketForBufferRow) {
/* 2023 */           this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */         }
/*      */         
/* 2026 */         return new BufferRow(rowPacket, fields, false, getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2030 */       readServerStatusForResultSets(rowPacket);
/*      */       
/* 2032 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2038 */     if (((isEOFDeprecated()) || (!rowPacket.isEOFPacket())) && ((!isEOFDeprecated()) || (!rowPacket.isResultSetOKPacket()))) {
/* 2039 */       if ((resultSetConcurrency == 1008) || ((!useBufferRowIfPossible) && (!useBufferRowExplicit))) {
/* 2040 */         return unpackBinaryResultSetRow(fields, rowPacket, resultSetConcurrency);
/*      */       }
/*      */       
/* 2043 */       if (!canReuseRowPacketForBufferRow) {
/* 2044 */         this.reusablePacket = new Buffer(rowPacket.getBufLength());
/*      */       }
/*      */       
/* 2047 */       return new BufferRow(rowPacket, fields, true, getExceptionInterceptor());
/*      */     }
/*      */     
/* 2050 */     rowPacket.setPosition(rowPacket.getPosition() - 1);
/* 2051 */     readServerStatusForResultSets(rowPacket);
/*      */     
/* 2053 */     return null;
/*      */   }
/*      */   
/*      */   final ResultSetRow nextRowFast(Field[] fields, int columnCount, boolean isBinaryEncoded, int resultSetConcurrency, boolean useBufferRowIfPossible, boolean useBufferRowExplicit, boolean canReuseRowPacket) throws SQLException
/*      */   {
/*      */     try {
/* 2059 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */       
/* 2061 */       if (lengthRead < 4) {
/* 2062 */         forceClose();
/* 2063 */         throw new RuntimeException(Messages.getString("MysqlIO.43"));
/*      */       }
/*      */       
/* 2066 */       int packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       
/*      */ 
/* 2069 */       if (packetLength == this.maxThreeBytes) {
/* 2070 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */         
/*      */ 
/* 2073 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, useBufferRowIfPossible, useBufferRowExplicit, canReuseRowPacket, this.reusablePacket);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2079 */       if (packetLength > this.useBufferRowSizeThreshold) {
/* 2080 */         reuseAndReadPacket(this.reusablePacket, packetLength);
/*      */         
/*      */ 
/* 2083 */         return nextRow(fields, columnCount, isBinaryEncoded, resultSetConcurrency, true, true, false, this.reusablePacket);
/*      */       }
/*      */       
/* 2086 */       int remaining = packetLength;
/*      */       
/* 2088 */       boolean firstTime = true;
/*      */       
/* 2090 */       byte[][] rowData = (byte[][])null;
/*      */       
/* 2092 */       for (int i = 0; i < columnCount; i++)
/*      */       {
/* 2094 */         int sw = this.mysqlInput.read() & 0xFF;
/* 2095 */         remaining--;
/*      */         
/* 2097 */         if (firstTime) {
/* 2098 */           if (sw == 255)
/*      */           {
/*      */ 
/* 2101 */             Buffer errorPacket = new Buffer(packetLength + 4);
/* 2102 */             errorPacket.setPosition(0);
/* 2103 */             errorPacket.writeByte(this.packetHeaderBuf[0]);
/* 2104 */             errorPacket.writeByte(this.packetHeaderBuf[1]);
/* 2105 */             errorPacket.writeByte(this.packetHeaderBuf[2]);
/* 2106 */             errorPacket.writeByte((byte)1);
/* 2107 */             errorPacket.writeByte((byte)sw);
/* 2108 */             readFully(this.mysqlInput, errorPacket.getByteBuffer(), 5, packetLength - 1);
/* 2109 */             errorPacket.setPosition(4);
/* 2110 */             checkErrorPacket(errorPacket);
/*      */           }
/*      */           
/* 2113 */           if ((sw == 254) && (packetLength < 16777215))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2120 */             if (this.use41Extensions) {
/* 2121 */               if (isEOFDeprecated())
/*      */               {
/* 2123 */                 remaining -= skipLengthEncodedInteger(this.mysqlInput);
/* 2124 */                 remaining -= skipLengthEncodedInteger(this.mysqlInput);
/*      */                 
/* 2126 */                 this.oldServerStatus = this.serverStatus;
/* 2127 */                 this.serverStatus = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/* 2128 */                 checkTransactionState(this.oldServerStatus);
/* 2129 */                 remaining -= 2;
/*      */                 
/* 2131 */                 this.warningCount = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/* 2132 */                 remaining -= 2;
/*      */                 
/* 2134 */                 if (this.warningCount > 0) {
/* 2135 */                   this.hadWarnings = true;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 2140 */                 this.warningCount = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/* 2141 */                 remaining -= 2;
/*      */                 
/* 2143 */                 if (this.warningCount > 0) {
/* 2144 */                   this.hadWarnings = true;
/*      */                 }
/*      */                 
/* 2147 */                 this.oldServerStatus = this.serverStatus;
/*      */                 
/* 2149 */                 this.serverStatus = (this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8);
/* 2150 */                 checkTransactionState(this.oldServerStatus);
/*      */                 
/* 2152 */                 remaining -= 2;
/*      */               }
/*      */               
/* 2155 */               setServerSlowQueryFlags();
/*      */               
/* 2157 */               if (remaining > 0) {
/* 2158 */                 skipFully(this.mysqlInput, remaining);
/*      */               }
/*      */             }
/*      */             
/* 2162 */             return null;
/*      */           }
/*      */           
/* 2165 */           rowData = new byte[columnCount][];
/*      */           
/* 2167 */           firstTime = false;
/*      */         }
/*      */         
/* 2170 */         int len = 0;
/*      */         
/* 2172 */         switch (sw) {
/*      */         case 251: 
/* 2174 */           len = -1;
/* 2175 */           break;
/*      */         
/*      */         case 252: 
/* 2178 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8;
/* 2179 */           remaining -= 2;
/* 2180 */           break;
/*      */         
/*      */         case 253: 
/* 2183 */           len = this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16;
/*      */           
/* 2185 */           remaining -= 3;
/* 2186 */           break;
/*      */         
/*      */         case 254: 
/* 2189 */           len = (int)(this.mysqlInput.read() & 0xFF | (this.mysqlInput.read() & 0xFF) << 8 | (this.mysqlInput.read() & 0xFF) << 16 | (this.mysqlInput.read() & 0xFF) << 24 | (this.mysqlInput.read() & 0xFF) << 32 | (this.mysqlInput.read() & 0xFF) << 40 | (this.mysqlInput.read() & 0xFF) << 48 | (this.mysqlInput.read() & 0xFF) << 56);
/*      */           
/*      */ 
/*      */ 
/* 2193 */           remaining -= 8;
/* 2194 */           break;
/*      */         
/*      */         default: 
/* 2197 */           len = sw;
/*      */         }
/*      */         
/* 2200 */         if (len == -1) {
/* 2201 */           rowData[i] = null;
/* 2202 */         } else if (len == 0) {
/* 2203 */           rowData[i] = Constants.EMPTY_BYTE_ARRAY;
/*      */         } else {
/* 2205 */           rowData[i] = new byte[len];
/*      */           
/* 2207 */           int bytesRead = readFully(this.mysqlInput, rowData[i], 0, len);
/*      */           
/* 2209 */           if (bytesRead != len) {
/* 2210 */             throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException(Messages.getString("MysqlIO.43")), getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 2214 */           remaining -= bytesRead;
/*      */         }
/*      */       }
/*      */       
/* 2218 */       if (remaining > 0) {
/* 2219 */         skipFully(this.mysqlInput, remaining);
/*      */       }
/*      */       
/* 2222 */       return new ByteArrayRow(rowData, getExceptionInterceptor());
/*      */     } catch (IOException ioEx) {
/* 2224 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final void quit()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/* 2239 */         if (!this.mysqlConnection.isClosed()) {
/*      */           try {
/* 2241 */             this.mysqlConnection.shutdownInput();
/*      */           }
/*      */           catch (UnsupportedOperationException ex) {}
/*      */         }
/*      */       }
/*      */       catch (IOException ioEx) {
/* 2247 */         this.connection.getLog().logWarn("Caught while disconnecting...", ioEx);
/*      */       }
/*      */       
/* 2250 */       Buffer packet = new Buffer(6);
/* 2251 */       this.packetSequence = -1;
/* 2252 */       this.compressedPacketSequence = -1;
/* 2253 */       packet.writeByte((byte)1);
/* 2254 */       send(packet, packet.getPosition());
/*      */     } finally {
/* 2256 */       forceClose();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Buffer getSharedSendPacket()
/*      */   {
/* 2267 */     if (this.sharedSendPacket == null) {
/* 2268 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */     
/* 2271 */     return this.sharedSendPacket;
/*      */   }
/*      */   
/*      */   void closeStreamer(RowData streamer) throws SQLException {
/* 2275 */     if (this.streamingData == null) {
/* 2276 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.17") + streamer + Messages.getString("MysqlIO.18"), getExceptionInterceptor());
/*      */     }
/*      */     
/* 2279 */     if (streamer != this.streamingData) {
/* 2280 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.19") + streamer + Messages.getString("MysqlIO.20") + Messages.getString("MysqlIO.21") + Messages.getString("MysqlIO.22"), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 2284 */     this.streamingData = null;
/*      */   }
/*      */   
/*      */   boolean tackOnMoreStreamingResults(ResultSetImpl addingTo) throws SQLException {
/* 2288 */     if ((this.serverStatus & 0x8) != 0)
/*      */     {
/* 2290 */       boolean moreRowSetsExist = true;
/* 2291 */       ResultSetImpl currentResultSet = addingTo;
/* 2292 */       boolean firstTime = true;
/*      */       
/* 2294 */       while ((moreRowSetsExist) && (
/* 2295 */         (firstTime) || (!currentResultSet.reallyResult())))
/*      */       {
/*      */ 
/*      */ 
/* 2299 */         firstTime = false;
/*      */         
/* 2301 */         Buffer fieldPacket = checkErrorPacket();
/* 2302 */         fieldPacket.setPosition(0);
/*      */         
/* 2304 */         java.sql.Statement owningStatement = addingTo.getStatement();
/*      */         
/* 2306 */         int maxRows = owningStatement.getMaxRows();
/*      */         
/*      */ 
/*      */ 
/* 2310 */         ResultSetImpl newResultSet = readResultsForQueryOrUpdate((StatementImpl)owningStatement, maxRows, owningStatement.getResultSetType(), owningStatement.getResultSetConcurrency(), true, owningStatement.getConnection().getCatalog(), fieldPacket, addingTo.isBinaryEncoded, -1L, null);
/*      */         
/*      */ 
/*      */ 
/* 2314 */         currentResultSet.setNextResultSet(newResultSet);
/*      */         
/* 2316 */         currentResultSet = newResultSet;
/*      */         
/* 2318 */         moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */         
/* 2320 */         if ((!currentResultSet.reallyResult()) && (!moreRowSetsExist))
/*      */         {
/* 2322 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 2326 */       return true;
/*      */     }
/*      */     
/* 2329 */     return false;
/*      */   }
/*      */   
/*      */   ResultSetImpl readAllResults(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache) throws SQLException
/*      */   {
/* 2334 */     resultPacket.setPosition(resultPacket.getPosition() - 1);
/*      */     
/* 2336 */     ResultSetImpl topLevelResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */     
/*      */ 
/* 2339 */     ResultSetImpl currentResultSet = topLevelResultSet;
/*      */     
/* 2341 */     boolean checkForMoreResults = (this.clientParam & 0x20000) != 0L;
/*      */     
/* 2343 */     boolean serverHasMoreResults = (this.serverStatus & 0x8) != 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2348 */     if ((serverHasMoreResults) && (streamResults))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2353 */       if (topLevelResultSet.getUpdateCount() != -1L) {
/* 2354 */         tackOnMoreStreamingResults(topLevelResultSet);
/*      */       }
/*      */       
/* 2357 */       reclaimLargeReusablePacket();
/*      */       
/* 2359 */       return topLevelResultSet;
/*      */     }
/*      */     
/* 2362 */     boolean moreRowSetsExist = checkForMoreResults & serverHasMoreResults;
/*      */     
/* 2364 */     while (moreRowSetsExist) {
/* 2365 */       Buffer fieldPacket = checkErrorPacket();
/* 2366 */       fieldPacket.setPosition(0);
/*      */       
/* 2368 */       ResultSetImpl newResultSet = readResultsForQueryOrUpdate(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, fieldPacket, isBinaryEncoded, preSentColumnCount, metadataFromCache);
/*      */       
/*      */ 
/* 2371 */       currentResultSet.setNextResultSet(newResultSet);
/*      */       
/* 2373 */       currentResultSet = newResultSet;
/*      */       
/* 2375 */       moreRowSetsExist = (this.serverStatus & 0x8) != 0;
/*      */     }
/*      */     
/* 2378 */     if (!streamResults) {
/* 2379 */       clearInputStream();
/*      */     }
/*      */     
/* 2382 */     reclaimLargeReusablePacket();
/*      */     
/* 2384 */     return topLevelResultSet;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void resetMaxBuf()
/*      */   {
/* 2391 */     this.maxAllowedPacket = this.connection.getMaxAllowedPacket();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final Buffer sendCommand(int command, String extraData, Buffer queryPacket, boolean skipCheck, String extraDataCharEncoding, int timeoutMillis)
/*      */     throws SQLException
/*      */   {
/* 2422 */     this.commandCount += 1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2428 */     this.enablePacketDebug = this.connection.getEnablePacketDebug();
/* 2429 */     this.readPacketSequence = 0;
/*      */     
/* 2431 */     int oldTimeout = 0;
/*      */     
/* 2433 */     if (timeoutMillis != 0) {
/*      */       try {
/* 2435 */         oldTimeout = this.mysqlConnection.getSoTimeout();
/* 2436 */         this.mysqlConnection.setSoTimeout(timeoutMillis);
/*      */       } catch (SocketException e) {
/* 2438 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 2445 */       checkForOutstandingStreamingData();
/*      */       
/*      */ 
/* 2448 */       this.oldServerStatus = this.serverStatus;
/* 2449 */       this.serverStatus = 0;
/* 2450 */       this.hadWarnings = false;
/* 2451 */       this.warningCount = 0;
/*      */       
/* 2453 */       this.queryNoIndexUsed = false;
/* 2454 */       this.queryBadIndexUsed = false;
/* 2455 */       this.serverQueryWasSlow = false;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2460 */       if (this.useCompression) {
/* 2461 */         int bytesLeft = this.mysqlInput.available();
/*      */         
/* 2463 */         if (bytesLeft > 0) {
/* 2464 */           this.mysqlInput.skip(bytesLeft);
/*      */         }
/*      */       }
/*      */       long id;
/*      */       try {
/* 2469 */         clearInputStream();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2476 */         if (queryPacket == null) {
/* 2477 */           int packLength = 8 + (extraData != null ? extraData.length() : 0) + 2;
/*      */           
/* 2479 */           if (this.sendPacket == null) {
/* 2480 */             this.sendPacket = new Buffer(packLength);
/*      */           }
/*      */           
/* 2483 */           this.packetSequence = -1;
/* 2484 */           this.compressedPacketSequence = -1;
/* 2485 */           this.readPacketSequence = 0;
/* 2486 */           this.checkPacketSequence = true;
/* 2487 */           this.sendPacket.clear();
/*      */           
/* 2489 */           this.sendPacket.writeByte((byte)command);
/*      */           
/* 2491 */           if ((command == 2) || (command == 5) || (command == 6) || (command == 3) || (command == 22))
/*      */           {
/* 2493 */             if (extraDataCharEncoding == null) {
/* 2494 */               this.sendPacket.writeStringNoNull(extraData);
/*      */             } else {
/* 2496 */               this.sendPacket.writeStringNoNull(extraData, extraDataCharEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), this.connection);
/*      */             }
/*      */           }
/* 2499 */           else if (command == 12) {
/* 2500 */             id = Long.parseLong(extraData);
/* 2501 */             this.sendPacket.writeLong(id);
/*      */           }
/*      */           
/* 2504 */           send(this.sendPacket, this.sendPacket.getPosition());
/*      */         } else {
/* 2506 */           this.packetSequence = -1;
/* 2507 */           this.compressedPacketSequence = -1;
/* 2508 */           send(queryPacket, queryPacket.getPosition());
/*      */         }
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 2512 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 2514 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ex, getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 2518 */       Buffer returnPacket = null;
/*      */       
/* 2520 */       if (!skipCheck) {
/* 2521 */         if ((command == 23) || (command == 26)) {
/* 2522 */           this.readPacketSequence = 0;
/* 2523 */           this.packetSequenceReset = true;
/*      */         }
/*      */         
/* 2526 */         returnPacket = checkErrorPacket(command);
/*      */       }
/*      */       
/* 2529 */       return returnPacket;
/*      */     } catch (IOException ioEx) {
/* 2531 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     finally {
/* 2534 */       if (timeoutMillis != 0) {
/*      */         try {
/* 2536 */           this.mysqlConnection.setSoTimeout(oldTimeout);
/*      */         } catch (SocketException e) {
/* 2538 */           throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, e, getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/* 2545 */   private int statementExecutionDepth = 0;
/*      */   private boolean useAutoSlowLog;
/*      */   
/*      */   protected boolean shouldIntercept() {
/* 2549 */     return this.statementInterceptors != null;
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
/*      */   final ResultSetInternalMethods sqlQueryDirect(StatementImpl callingStatement, String query, String characterEncoding, Buffer queryPacket, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Field[] cachedMetadata)
/*      */     throws Exception
/*      */   {
/* 2572 */     this.statementExecutionDepth += 1;
/*      */     try
/*      */     {
/* 2575 */       if (this.statementInterceptors != null) {
/* 2576 */         ResultSetInternalMethods interceptedResults = invokeStatementInterceptorsPre(query, callingStatement, false);
/*      */         
/* 2578 */         if (interceptedResults != null) {
/* 2579 */           return interceptedResults;
/*      */         }
/*      */       }
/*      */       
/* 2583 */       long queryStartTime = 0L;
/* 2584 */       long queryEndTime = 0L;
/*      */       
/* 2586 */       String statementComment = this.connection.getStatementComment();
/*      */       
/* 2588 */       if (this.connection.getIncludeThreadNamesAsStatementComment()) {
/* 2589 */         statementComment = (statementComment != null ? statementComment + ", " : "") + "java thread: " + Thread.currentThread().getName();
/*      */       }
/*      */       
/* 2592 */       if (query != null)
/*      */       {
/*      */ 
/* 2595 */         int packLength = 5 + query.length() * 3 + 2;
/*      */         
/* 2597 */         byte[] commentAsBytes = null;
/*      */         
/* 2599 */         if (statementComment != null) {
/* 2600 */           commentAsBytes = StringUtils.getBytes(statementComment, null, characterEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor());
/*      */           
/*      */ 
/* 2603 */           packLength += commentAsBytes.length;
/* 2604 */           packLength += 6;
/*      */         }
/*      */         
/* 2607 */         if (this.sendPacket == null) {
/* 2608 */           this.sendPacket = new Buffer(packLength);
/*      */         } else {
/* 2610 */           this.sendPacket.clear();
/*      */         }
/*      */         
/* 2613 */         this.sendPacket.writeByte((byte)3);
/*      */         
/* 2615 */         if (commentAsBytes != null) {
/* 2616 */           this.sendPacket.writeBytesNoNull(Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 2617 */           this.sendPacket.writeBytesNoNull(commentAsBytes);
/* 2618 */           this.sendPacket.writeBytesNoNull(Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*      */         }
/*      */         
/* 2621 */         if (characterEncoding != null) {
/* 2622 */           if (this.platformDbCharsetMatches) {
/* 2623 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), this.connection);
/*      */ 
/*      */           }
/* 2626 */           else if (StringUtils.startsWithIgnoreCaseAndWs(query, "LOAD DATA")) {
/* 2627 */             this.sendPacket.writeBytesNoNull(StringUtils.getBytes(query));
/*      */           } else {
/* 2629 */             this.sendPacket.writeStringNoNull(query, characterEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), this.connection);
/*      */           }
/*      */           
/*      */         }
/*      */         else {
/* 2634 */           this.sendPacket.writeStringNoNull(query);
/*      */         }
/*      */         
/* 2637 */         queryPacket = this.sendPacket;
/*      */       }
/*      */       
/* 2640 */       byte[] queryBuf = null;
/* 2641 */       int oldPacketPosition = 0;
/*      */       
/* 2643 */       if (this.needToGrabQueryFromPacket) {
/* 2644 */         queryBuf = queryPacket.getByteBuffer();
/*      */         
/*      */ 
/* 2647 */         oldPacketPosition = queryPacket.getPosition();
/*      */         
/* 2649 */         queryStartTime = getCurrentTimeNanosOrMillis();
/*      */       }
/*      */       
/* 2652 */       if (this.autoGenerateTestcaseScript) {
/* 2653 */         String testcaseQuery = null;
/*      */         
/* 2655 */         if (query != null) {
/* 2656 */           if (statementComment != null) {
/* 2657 */             testcaseQuery = "/* " + statementComment + " */ " + query;
/*      */           } else {
/* 2659 */             testcaseQuery = query;
/*      */           }
/*      */         } else {
/* 2662 */           testcaseQuery = StringUtils.toString(queryBuf, 5, oldPacketPosition - 5);
/*      */         }
/*      */         
/* 2665 */         StringBuilder debugBuf = new StringBuilder(testcaseQuery.length() + 32);
/* 2666 */         this.connection.generateConnectionCommentBlock(debugBuf);
/* 2667 */         debugBuf.append(testcaseQuery);
/* 2668 */         debugBuf.append(';');
/* 2669 */         this.connection.dumpTestcaseQuery(debugBuf.toString());
/*      */       }
/*      */       
/*      */ 
/* 2673 */       Buffer resultPacket = sendCommand(3, null, queryPacket, false, null, 0);
/*      */       
/* 2675 */       long fetchBeginTime = 0L;
/* 2676 */       long fetchEndTime = 0L;
/*      */       
/* 2678 */       String profileQueryToLog = null;
/*      */       
/* 2680 */       boolean queryWasSlow = false;
/*      */       
/* 2682 */       if ((this.profileSql) || (this.logSlowQueries)) {
/* 2683 */         queryEndTime = getCurrentTimeNanosOrMillis();
/*      */         
/* 2685 */         boolean shouldExtractQuery = false;
/*      */         
/* 2687 */         if (this.profileSql) {
/* 2688 */           shouldExtractQuery = true;
/* 2689 */         } else if (this.logSlowQueries) {
/* 2690 */           long queryTime = queryEndTime - queryStartTime;
/*      */           
/* 2692 */           boolean logSlow = false;
/*      */           
/* 2694 */           if (!this.useAutoSlowLog) {
/* 2695 */             logSlow = queryTime > this.connection.getSlowQueryThresholdMillis();
/*      */           } else {
/* 2697 */             logSlow = this.connection.isAbonormallyLongQuery(queryTime);
/*      */             
/* 2699 */             this.connection.reportQueryTime(queryTime);
/*      */           }
/*      */           
/* 2702 */           if (logSlow) {
/* 2703 */             shouldExtractQuery = true;
/* 2704 */             queryWasSlow = true;
/*      */           }
/*      */         }
/*      */         
/* 2708 */         if (shouldExtractQuery)
/*      */         {
/* 2710 */           boolean truncated = false;
/*      */           
/* 2712 */           int extractPosition = oldPacketPosition;
/*      */           
/* 2714 */           if (oldPacketPosition > this.connection.getMaxQuerySizeToLog()) {
/* 2715 */             extractPosition = this.connection.getMaxQuerySizeToLog() + 5;
/* 2716 */             truncated = true;
/*      */           }
/*      */           
/* 2719 */           profileQueryToLog = StringUtils.toString(queryBuf, 5, extractPosition - 5);
/*      */           
/* 2721 */           if (truncated) {
/* 2722 */             profileQueryToLog = profileQueryToLog + Messages.getString("MysqlIO.25");
/*      */           }
/*      */         }
/*      */         
/* 2726 */         fetchBeginTime = queryEndTime;
/*      */       }
/*      */       
/* 2729 */       ResultSetInternalMethods rs = readAllResults(callingStatement, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, resultPacket, false, -1L, cachedMetadata);
/*      */       
/*      */ 
/* 2732 */       if ((queryWasSlow) && (!this.serverQueryWasSlow)) {
/* 2733 */         StringBuilder mesgBuf = new StringBuilder(48 + profileQueryToLog.length());
/*      */         
/* 2735 */         mesgBuf.append(Messages.getString("MysqlIO.SlowQuery", new Object[] { String.valueOf(this.useAutoSlowLog ? " 95% of all queries " : Long.valueOf(this.slowQueryThreshold)), this.queryTimingUnits, Long.valueOf(queryEndTime - queryStartTime) }));
/*      */         
/*      */ 
/* 2738 */         mesgBuf.append(profileQueryToLog);
/*      */         
/* 2740 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2742 */         eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), (int)(queryEndTime - queryStartTime), this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), mesgBuf.toString()));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2747 */         if (this.connection.getExplainSlowQueries()) {
/* 2748 */           if (oldPacketPosition < 1048576) {
/* 2749 */             explainSlowQuery(queryPacket.getBytes(5, oldPacketPosition - 5), profileQueryToLog);
/*      */           } else {
/* 2751 */             this.connection.getLog().logWarn(Messages.getString("MysqlIO.28") + 1048576 + Messages.getString("MysqlIO.29"));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2756 */       if (this.logSlowQueries)
/*      */       {
/* 2758 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2760 */         if ((this.queryBadIndexUsed) && (this.profileSql)) {
/* 2761 */           eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.33") + profileQueryToLog));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2767 */         if ((this.queryNoIndexUsed) && (this.profileSql)) {
/* 2768 */           eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.35") + profileQueryToLog));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2774 */         if ((this.serverQueryWasSlow) && (this.profileSql)) {
/* 2775 */           eventSink.consumeEvent(new ProfilerEvent((byte)6, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), Messages.getString("MysqlIO.ServerSlowQuery") + profileQueryToLog));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2782 */       if (this.profileSql) {
/* 2783 */         fetchEndTime = getCurrentTimeNanosOrMillis();
/*      */         
/* 2785 */         ProfilerEventHandler eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */         
/* 2787 */         eventSink.consumeEvent(new ProfilerEvent((byte)3, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), queryEndTime - queryStartTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), profileQueryToLog));
/*      */         
/*      */ 
/*      */ 
/* 2791 */         eventSink.consumeEvent(new ProfilerEvent((byte)5, "", catalog, this.connection.getId(), callingStatement != null ? callingStatement.getId() : 999, ((ResultSetImpl)rs).resultId, System.currentTimeMillis(), fetchEndTime - fetchBeginTime, this.queryTimingUnits, null, LogUtils.findCallingClassAndMethod(new Throwable()), null));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2796 */       if (this.hadWarnings) {
/* 2797 */         scanForAndThrowDataTruncation();
/*      */       }
/*      */       ResultSetInternalMethods interceptedResults;
/* 2800 */       if (this.statementInterceptors != null) {
/* 2801 */         interceptedResults = invokeStatementInterceptorsPost(query, callingStatement, rs, false, null);
/*      */         
/* 2803 */         if (interceptedResults != null) {
/* 2804 */           rs = interceptedResults;
/*      */         }
/*      */       }
/*      */       
/* 2808 */       return rs;
/*      */     } catch (SQLException sqlEx) {
/* 2810 */       if (this.statementInterceptors != null) {
/* 2811 */         invokeStatementInterceptorsPost(query, callingStatement, null, false, sqlEx);
/*      */       }
/*      */       
/* 2814 */       if (callingStatement != null) {
/* 2815 */         synchronized (callingStatement.cancelTimeoutMutex) {
/* 2816 */           if (callingStatement.wasCancelled) {
/* 2817 */             SQLException cause = null;
/*      */             
/* 2819 */             if (callingStatement.wasCancelledByTimeout) {
/* 2820 */               cause = new MySQLTimeoutException();
/*      */             } else {
/* 2822 */               cause = new MySQLStatementCancelledException();
/*      */             }
/*      */             
/* 2825 */             callingStatement.resetCancelledState();
/*      */             
/* 2827 */             throw cause;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2832 */       throw sqlEx;
/*      */     } finally {
/* 2834 */       this.statementExecutionDepth -= 1;
/*      */     }
/*      */   }
/*      */   
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPre(String sql, Statement interceptedStatement, boolean forceExecute) throws SQLException {
/* 2839 */     ResultSetInternalMethods previousResultSet = null;
/*      */     
/* 2841 */     int i = 0; for (int s = this.statementInterceptors.size(); i < s; i++) {
/* 2842 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)this.statementInterceptors.get(i);
/*      */       
/* 2844 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2845 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */       
/* 2847 */       if (shouldExecute) {
/* 2848 */         String sqlToInterceptor = sql;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2855 */         ResultSetInternalMethods interceptedResultSet = interceptor.preProcess(sqlToInterceptor, interceptedStatement, this.connection);
/*      */         
/* 2857 */         if (interceptedResultSet != null) {
/* 2858 */           previousResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2863 */     return previousResultSet;
/*      */   }
/*      */   
/*      */   ResultSetInternalMethods invokeStatementInterceptorsPost(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, boolean forceExecute, SQLException statementException)
/*      */     throws SQLException
/*      */   {
/* 2869 */     int i = 0; for (int s = this.statementInterceptors.size(); i < s; i++) {
/* 2870 */       StatementInterceptorV2 interceptor = (StatementInterceptorV2)this.statementInterceptors.get(i);
/*      */       
/* 2872 */       boolean executeTopLevelOnly = interceptor.executeTopLevelOnly();
/* 2873 */       boolean shouldExecute = ((executeTopLevelOnly) && ((this.statementExecutionDepth == 1) || (forceExecute))) || (!executeTopLevelOnly);
/*      */       
/* 2875 */       if (shouldExecute) {
/* 2876 */         String sqlToInterceptor = sql;
/*      */         
/* 2878 */         ResultSetInternalMethods interceptedResultSet = interceptor.postProcess(sqlToInterceptor, interceptedStatement, originalResultSet, this.connection, this.warningCount, this.queryNoIndexUsed, this.queryBadIndexUsed, statementException);
/*      */         
/*      */ 
/* 2881 */         if (interceptedResultSet != null) {
/* 2882 */           originalResultSet = interceptedResultSet;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2887 */     return originalResultSet;
/*      */   }
/*      */   
/*      */   private void calculateSlowQueryThreshold() {
/* 2891 */     this.slowQueryThreshold = this.connection.getSlowQueryThresholdMillis();
/*      */     
/* 2893 */     if (this.connection.getUseNanosForElapsedTime()) {
/* 2894 */       long nanosThreshold = this.connection.getSlowQueryThresholdNanos();
/*      */       
/* 2896 */       if (nanosThreshold != 0L) {
/* 2897 */         this.slowQueryThreshold = nanosThreshold;
/*      */       } else {
/* 2899 */         this.slowQueryThreshold *= 1000000L;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected long getCurrentTimeNanosOrMillis() {
/* 2905 */     if (this.useNanosForElapsedTime) {
/* 2906 */       return TimeUtil.getCurrentTimeNanosOrMillis();
/*      */     }
/*      */     
/* 2909 */     return System.currentTimeMillis();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   String getHost()
/*      */   {
/* 2916 */     return this.host;
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
/*      */   boolean isVersion(int major, int minor, int subminor)
/*      */   {
/* 2934 */     return (major == getServerMajorVersion()) && (minor == getServerMinorVersion()) && (subminor == getServerSubMinorVersion());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean versionMeetsMinimum(int major, int minor, int subminor)
/*      */   {
/* 2946 */     if (getServerMajorVersion() >= major) {
/* 2947 */       if (getServerMajorVersion() == major) {
/* 2948 */         if (getServerMinorVersion() >= minor) {
/* 2949 */           if (getServerMinorVersion() == minor) {
/* 2950 */             return getServerSubMinorVersion() >= subminor;
/*      */           }
/*      */           
/*      */ 
/* 2954 */           return true;
/*      */         }
/*      */         
/*      */ 
/* 2958 */         return false;
/*      */       }
/*      */       
/*      */ 
/* 2962 */       return true;
/*      */     }
/*      */     
/* 2965 */     return false;
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
/*      */   private static final String getPacketDumpToLog(Buffer packetToDump, int packetLength)
/*      */   {
/* 2980 */     if (packetLength < 1024) {
/* 2981 */       return packetToDump.dump(packetLength);
/*      */     }
/*      */     
/* 2984 */     StringBuilder packetDumpBuf = new StringBuilder(4096);
/* 2985 */     packetDumpBuf.append(packetToDump.dump(1024));
/* 2986 */     packetDumpBuf.append(Messages.getString("MysqlIO.36"));
/* 2987 */     packetDumpBuf.append(1024);
/* 2988 */     packetDumpBuf.append(Messages.getString("MysqlIO.37"));
/*      */     
/* 2990 */     return packetDumpBuf.toString();
/*      */   }
/*      */   
/*      */   private final int readFully(InputStream in, byte[] b, int off, int len) throws IOException {
/* 2994 */     if (len < 0) {
/* 2995 */       throw new IndexOutOfBoundsException();
/*      */     }
/*      */     
/* 2998 */     int n = 0;
/*      */     
/* 3000 */     while (n < len) {
/* 3001 */       int count = in.read(b, off + n, len - n);
/*      */       
/* 3003 */       if (count < 0) {
/* 3004 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Integer.valueOf(len), Integer.valueOf(n) }));
/*      */       }
/*      */       
/* 3007 */       n += count;
/*      */     }
/*      */     
/* 3010 */     return n;
/*      */   }
/*      */   
/*      */   private final long skipFully(InputStream in, long len) throws IOException {
/* 3014 */     if (len < 0L) {
/* 3015 */       throw new IOException("Negative skip length not allowed");
/*      */     }
/*      */     
/* 3018 */     long n = 0L;
/*      */     
/* 3020 */     while (n < len) {
/* 3021 */       long count = in.skip(len - n);
/*      */       
/* 3023 */       if (count < 0L) {
/* 3024 */         throw new EOFException(Messages.getString("MysqlIO.EOF", new Object[] { Long.valueOf(len), Long.valueOf(n) }));
/*      */       }
/*      */       
/* 3027 */       n += count;
/*      */     }
/*      */     
/* 3030 */     return n;
/*      */   }
/*      */   
/*      */   private final int skipLengthEncodedInteger(InputStream in) throws IOException {
/* 3034 */     int sw = in.read() & 0xFF;
/*      */     
/* 3036 */     switch (sw) {
/*      */     case 252: 
/* 3038 */       return (int)skipFully(in, 2L) + 1;
/*      */     
/*      */     case 253: 
/* 3041 */       return (int)skipFully(in, 3L) + 1;
/*      */     
/*      */     case 254: 
/* 3044 */       return (int)skipFully(in, 8L) + 1;
/*      */     }
/*      */     
/* 3047 */     return 1;
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
/*      */   protected final ResultSetImpl readResultsForQueryOrUpdate(StatementImpl callingStatement, int maxRows, int resultSetType, int resultSetConcurrency, boolean streamResults, String catalog, Buffer resultPacket, boolean isBinaryEncoded, long preSentColumnCount, Field[] metadataFromCache)
/*      */     throws SQLException
/*      */   {
/* 3084 */     long columnCount = resultPacket.readFieldLength();
/*      */     
/* 3086 */     if (columnCount == 0L)
/* 3087 */       return buildResultSetWithUpdates(callingStatement, resultPacket);
/* 3088 */     if (columnCount == -1L) {
/* 3089 */       String charEncoding = null;
/*      */       
/* 3091 */       if (this.connection.getUseUnicode()) {
/* 3092 */         charEncoding = this.connection.getEncoding();
/*      */       }
/*      */       
/* 3095 */       String fileName = null;
/*      */       
/* 3097 */       if (this.platformDbCharsetMatches) {
/* 3098 */         fileName = charEncoding != null ? resultPacket.readString(charEncoding, getExceptionInterceptor()) : resultPacket.readString();
/*      */       } else {
/* 3100 */         fileName = resultPacket.readString();
/*      */       }
/*      */       
/* 3103 */       return sendFileToServer(callingStatement, fileName);
/*      */     }
/* 3105 */     ResultSetImpl results = getResultSet(callingStatement, columnCount, maxRows, resultSetType, resultSetConcurrency, streamResults, catalog, isBinaryEncoded, metadataFromCache);
/*      */     
/*      */ 
/* 3108 */     return results;
/*      */   }
/*      */   
/*      */   private int alignPacketSize(int a, int l)
/*      */   {
/* 3113 */     return a + l - 1 & (l - 1 ^ 0xFFFFFFFF);
/*      */   }
/*      */   
/*      */   private ResultSetImpl buildResultSetWithRows(StatementImpl callingStatement, String catalog, Field[] fields, RowData rows, int resultSetType, int resultSetConcurrency, boolean isBinaryEncoded) throws SQLException
/*      */   {
/* 3118 */     ResultSetImpl rs = null;
/*      */     
/* 3120 */     switch (resultSetConcurrency) {
/*      */     case 1007: 
/* 3122 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */       
/* 3124 */       if (isBinaryEncoded) {
/* 3125 */         rs.setBinaryEncoded();
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 1008: 
/* 3131 */       rs = ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, true);
/*      */       
/* 3133 */       break;
/*      */     
/*      */     default: 
/* 3136 */       return ResultSetImpl.getInstance(catalog, fields, rows, this.connection, callingStatement, false);
/*      */     }
/*      */     
/* 3139 */     rs.setResultSetType(resultSetType);
/* 3140 */     rs.setResultSetConcurrency(resultSetConcurrency);
/*      */     
/* 3142 */     return rs;
/*      */   }
/*      */   
/*      */   private ResultSetImpl buildResultSetWithUpdates(StatementImpl callingStatement, Buffer resultPacket) throws SQLException {
/* 3146 */     long updateCount = -1L;
/* 3147 */     long updateID = -1L;
/* 3148 */     String info = null;
/*      */     try
/*      */     {
/* 3151 */       if (this.useNewUpdateCounts) {
/* 3152 */         updateCount = resultPacket.newReadLength();
/* 3153 */         updateID = resultPacket.newReadLength();
/*      */       } else {
/* 3155 */         updateCount = resultPacket.readLength();
/* 3156 */         updateID = resultPacket.readLength();
/*      */       }
/*      */       
/* 3159 */       if (this.use41Extensions)
/*      */       {
/* 3161 */         this.serverStatus = resultPacket.readInt();
/*      */         
/* 3163 */         checkTransactionState(this.oldServerStatus);
/*      */         
/* 3165 */         this.warningCount = resultPacket.readInt();
/*      */         
/* 3167 */         if (this.warningCount > 0) {
/* 3168 */           this.hadWarnings = true;
/*      */         }
/*      */         
/* 3171 */         resultPacket.readByte();
/*      */         
/* 3173 */         setServerSlowQueryFlags();
/*      */       }
/*      */       
/* 3176 */       if (this.connection.isReadInfoMsgEnabled()) {
/* 3177 */         info = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */       }
/*      */     } catch (Exception ex) {
/* 3180 */       SQLException sqlEx = SQLError.createSQLException(SQLError.get("S1000"), "S1000", -1, getExceptionInterceptor());
/*      */       
/* 3182 */       sqlEx.initCause(ex);
/*      */       
/* 3184 */       throw sqlEx;
/*      */     }
/*      */     
/* 3187 */     ResultSetInternalMethods updateRs = ResultSetImpl.getInstance(updateCount, updateID, this.connection, callingStatement);
/*      */     
/* 3189 */     if (info != null) {
/* 3190 */       ((ResultSetImpl)updateRs).setServerInfo(info);
/*      */     }
/*      */     
/* 3193 */     return (ResultSetImpl)updateRs;
/*      */   }
/*      */   
/*      */   private void setServerSlowQueryFlags() {
/* 3197 */     this.queryBadIndexUsed = ((this.serverStatus & 0x10) != 0);
/* 3198 */     this.queryNoIndexUsed = ((this.serverStatus & 0x20) != 0);
/* 3199 */     this.serverQueryWasSlow = ((this.serverStatus & 0x800) != 0);
/*      */   }
/*      */   
/*      */   private void checkForOutstandingStreamingData() throws SQLException {
/* 3203 */     if (this.streamingData != null) {
/* 3204 */       boolean shouldClobber = this.connection.getClobberStreamingResults();
/*      */       
/* 3206 */       if (!shouldClobber) {
/* 3207 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.39") + this.streamingData + Messages.getString("MysqlIO.40") + Messages.getString("MysqlIO.41") + Messages.getString("MysqlIO.42"), getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3212 */       this.streamingData.getOwner().realClose(false);
/*      */       
/*      */ 
/* 3215 */       clearInputStream();
/*      */     }
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
/*      */   private Buffer compressPacket(Buffer packet, int offset, int packetLen)
/*      */     throws SQLException
/*      */   {
/* 3232 */     int compressedLength = packetLen;
/* 3233 */     int uncompressedLength = 0;
/* 3234 */     byte[] compressedBytes = null;
/* 3235 */     int offsetWrite = offset;
/*      */     
/* 3237 */     if (packetLen < 50) {
/* 3238 */       compressedBytes = packet.getByteBuffer();
/*      */     }
/*      */     else {
/* 3241 */       byte[] bytesToCompress = packet.getByteBuffer();
/* 3242 */       compressedBytes = new byte[bytesToCompress.length * 2];
/*      */       
/* 3244 */       if (this.deflater == null) {
/* 3245 */         this.deflater = new Deflater();
/*      */       }
/* 3247 */       this.deflater.reset();
/* 3248 */       this.deflater.setInput(bytesToCompress, offset, packetLen);
/* 3249 */       this.deflater.finish();
/*      */       
/* 3251 */       compressedLength = this.deflater.deflate(compressedBytes);
/*      */       
/* 3253 */       if (compressedLength > packetLen)
/*      */       {
/* 3255 */         compressedBytes = packet.getByteBuffer();
/* 3256 */         compressedLength = packetLen;
/*      */       } else {
/* 3258 */         uncompressedLength = packetLen;
/* 3259 */         offsetWrite = 0;
/*      */       }
/*      */     }
/*      */     
/* 3263 */     Buffer compressedPacket = new Buffer(7 + compressedLength);
/*      */     
/* 3265 */     compressedPacket.setPosition(0);
/* 3266 */     compressedPacket.writeLongInt(compressedLength);
/* 3267 */     compressedPacket.writeByte(this.compressedPacketSequence);
/* 3268 */     compressedPacket.writeLongInt(uncompressedLength);
/* 3269 */     compressedPacket.writeBytesNoNull(compressedBytes, offsetWrite, compressedLength);
/*      */     
/* 3271 */     return compressedPacket;
/*      */   }
/*      */   
/*      */   private final void readServerStatusForResultSets(Buffer rowPacket) throws SQLException {
/* 3275 */     if (this.use41Extensions) {
/* 3276 */       rowPacket.readByte();
/*      */       
/* 3278 */       if (isEOFDeprecated())
/*      */       {
/* 3280 */         rowPacket.newReadLength();
/* 3281 */         rowPacket.newReadLength();
/*      */         
/* 3283 */         this.oldServerStatus = this.serverStatus;
/* 3284 */         this.serverStatus = rowPacket.readInt();
/* 3285 */         checkTransactionState(this.oldServerStatus);
/*      */         
/* 3287 */         this.warningCount = rowPacket.readInt();
/* 3288 */         if (this.warningCount > 0) {
/* 3289 */           this.hadWarnings = true;
/*      */         }
/*      */         
/* 3292 */         rowPacket.readByte();
/*      */         
/* 3294 */         if (this.connection.isReadInfoMsgEnabled()) {
/* 3295 */           rowPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 3300 */         this.warningCount = rowPacket.readInt();
/* 3301 */         if (this.warningCount > 0) {
/* 3302 */           this.hadWarnings = true;
/*      */         }
/*      */         
/* 3305 */         this.oldServerStatus = this.serverStatus;
/* 3306 */         this.serverStatus = rowPacket.readInt();
/* 3307 */         checkTransactionState(this.oldServerStatus);
/*      */       }
/*      */       
/* 3310 */       setServerSlowQueryFlags();
/*      */     }
/*      */   }
/*      */   
/*      */   private SocketFactory createSocketFactory() throws SQLException {
/*      */     try {
/* 3316 */       if (this.socketFactoryClassName == null) {
/* 3317 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.75"), "08001", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 3321 */       return (SocketFactory)Class.forName(this.socketFactoryClassName).newInstance();
/*      */     } catch (Exception ex) {
/* 3323 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.76") + this.socketFactoryClassName + Messages.getString("MysqlIO.77"), "08001", getExceptionInterceptor());
/*      */       
/*      */ 
/* 3326 */       sqlEx.initCause(ex);
/*      */       
/* 3328 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   private void enqueuePacketForDebugging(boolean isPacketBeingSent, boolean isPacketReused, int sendLength, byte[] header, Buffer packet) throws SQLException
/*      */   {
/* 3334 */     if (this.packetDebugRingBuffer.size() + 1 > this.connection.getPacketDebugBufferSize()) {
/* 3335 */       this.packetDebugRingBuffer.removeFirst();
/*      */     }
/*      */     
/* 3338 */     StringBuilder packetDump = null;
/*      */     
/* 3340 */     if (!isPacketBeingSent) {
/* 3341 */       int bytesToDump = Math.min(1024, packet.getBufLength());
/*      */       
/* 3343 */       Buffer packetToDump = new Buffer(4 + bytesToDump);
/*      */       
/* 3345 */       packetToDump.setPosition(0);
/* 3346 */       packetToDump.writeBytesNoNull(header);
/* 3347 */       packetToDump.writeBytesNoNull(packet.getBytes(0, bytesToDump));
/*      */       
/* 3349 */       String packetPayload = packetToDump.dump(bytesToDump);
/*      */       
/* 3351 */       packetDump = new StringBuilder(96 + packetPayload.length());
/*      */       
/* 3353 */       packetDump.append("Server ");
/*      */       
/* 3355 */       packetDump.append(isPacketReused ? "(re-used) " : "(new) ");
/*      */       
/* 3357 */       packetDump.append(packet.toSuperString());
/* 3358 */       packetDump.append(" --------------------> Client\n");
/* 3359 */       packetDump.append("\nPacket payload:\n\n");
/* 3360 */       packetDump.append(packetPayload);
/*      */       
/* 3362 */       if (bytesToDump == 1024) {
/* 3363 */         packetDump.append("\nNote: Packet of " + packet.getBufLength() + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */     } else {
/* 3366 */       int bytesToDump = Math.min(1024, sendLength);
/*      */       
/* 3368 */       String packetPayload = packet.dump(bytesToDump);
/*      */       
/* 3370 */       packetDump = new StringBuilder(68 + packetPayload.length());
/*      */       
/* 3372 */       packetDump.append("Client ");
/* 3373 */       packetDump.append(packet.toSuperString());
/* 3374 */       packetDump.append("--------------------> Server\n");
/* 3375 */       packetDump.append("\nPacket payload:\n\n");
/* 3376 */       packetDump.append(packetPayload);
/*      */       
/* 3378 */       if (bytesToDump == 1024) {
/* 3379 */         packetDump.append("\nNote: Packet of " + sendLength + " bytes truncated to " + 1024 + " bytes.\n");
/*      */       }
/*      */     }
/*      */     
/* 3383 */     this.packetDebugRingBuffer.addLast(packetDump);
/*      */   }
/*      */   
/*      */   private RowData readSingleRowSet(long columnCount, int maxRows, int resultSetConcurrency, boolean isBinaryEncoded, Field[] fields) throws SQLException
/*      */   {
/* 3388 */     ArrayList<ResultSetRow> rows = new ArrayList();
/*      */     
/* 3390 */     boolean useBufferRowExplicit = useBufferRowExplicit(fields);
/*      */     
/*      */ 
/* 3393 */     ResultSetRow row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */     
/* 3395 */     int rowCount = 0;
/*      */     
/* 3397 */     if (row != null) {
/* 3398 */       rows.add(row);
/* 3399 */       rowCount = 1;
/*      */     }
/*      */     
/* 3402 */     while (row != null) {
/* 3403 */       row = nextRow(fields, (int)columnCount, isBinaryEncoded, resultSetConcurrency, false, useBufferRowExplicit, false, null);
/*      */       
/* 3405 */       if ((row != null) && (
/* 3406 */         (maxRows == -1) || (rowCount < maxRows))) {
/* 3407 */         rows.add(row);
/* 3408 */         rowCount++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3413 */     RowData rowData = new RowDataStatic(rows);
/*      */     
/* 3415 */     return rowData;
/*      */   }
/*      */   
/*      */   public static boolean useBufferRowExplicit(Field[] fields) {
/* 3419 */     if (fields == null) {
/* 3420 */       return false;
/*      */     }
/*      */     
/* 3423 */     for (int i = 0; i < fields.length; i++) {
/* 3424 */       switch (fields[i].getSQLType()) {
/*      */       case -4: 
/*      */       case -1: 
/*      */       case 2004: 
/*      */       case 2005: 
/* 3429 */         return true;
/*      */       }
/*      */       
/*      */     }
/* 3433 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void reclaimLargeReusablePacket()
/*      */   {
/* 3440 */     if ((this.reusablePacket != null) && (this.reusablePacket.getCapacity() > 1048576)) {
/* 3441 */       this.reusablePacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse)
/*      */     throws SQLException
/*      */   {
/* 3452 */     return reuseAndReadPacket(reuse, -1);
/*      */   }
/*      */   
/*      */   private final Buffer reuseAndReadPacket(Buffer reuse, int existingPacketLength) throws SQLException
/*      */   {
/*      */     try {
/* 3458 */       reuse.setWasMultiPacket(false);
/* 3459 */       int packetLength = 0;
/*      */       
/* 3461 */       if (existingPacketLength == -1) {
/* 3462 */         int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/*      */         
/* 3464 */         if (lengthRead < 4) {
/* 3465 */           forceClose();
/* 3466 */           throw new IOException(Messages.getString("MysqlIO.43"));
/*      */         }
/*      */         
/* 3469 */         packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/*      */       } else {
/* 3471 */         packetLength = existingPacketLength;
/*      */       }
/*      */       
/* 3474 */       if (this.traceProtocol) {
/* 3475 */         StringBuilder traceMessageBuf = new StringBuilder();
/*      */         
/* 3477 */         traceMessageBuf.append(Messages.getString("MysqlIO.44"));
/* 3478 */         traceMessageBuf.append(packetLength);
/* 3479 */         traceMessageBuf.append(Messages.getString("MysqlIO.45"));
/* 3480 */         traceMessageBuf.append(StringUtils.dumpAsHex(this.packetHeaderBuf, 4));
/*      */         
/* 3482 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */       
/* 3485 */       byte multiPacketSeq = this.packetHeaderBuf[3];
/*      */       
/* 3487 */       if (!this.packetSequenceReset) {
/* 3488 */         if ((this.enablePacketDebug) && (this.checkPacketSequence)) {
/* 3489 */           checkPacketSequencing(multiPacketSeq);
/*      */         }
/*      */       } else {
/* 3492 */         this.packetSequenceReset = false;
/*      */       }
/*      */       
/* 3495 */       this.readPacketSequence = multiPacketSeq;
/*      */       
/*      */ 
/* 3498 */       reuse.setPosition(0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3504 */       if (reuse.getByteBuffer().length <= packetLength) {
/* 3505 */         reuse.setByteBuffer(new byte[packetLength + 1]);
/*      */       }
/*      */       
/*      */ 
/* 3509 */       reuse.setBufLength(packetLength);
/*      */       
/*      */ 
/* 3512 */       int numBytesRead = readFully(this.mysqlInput, reuse.getByteBuffer(), 0, packetLength);
/*      */       
/* 3514 */       if (numBytesRead != packetLength) {
/* 3515 */         throw new IOException("Short read, expected " + packetLength + " bytes, only read " + numBytesRead);
/*      */       }
/*      */       
/* 3518 */       if (this.traceProtocol) {
/* 3519 */         StringBuilder traceMessageBuf = new StringBuilder();
/*      */         
/* 3521 */         traceMessageBuf.append(Messages.getString("MysqlIO.46"));
/* 3522 */         traceMessageBuf.append(getPacketDumpToLog(reuse, packetLength));
/*      */         
/* 3524 */         this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */       }
/*      */       
/* 3527 */       if (this.enablePacketDebug) {
/* 3528 */         enqueuePacketForDebugging(false, true, 0, this.packetHeaderBuf, reuse);
/*      */       }
/*      */       
/* 3531 */       boolean isMultiPacket = false;
/*      */       
/* 3533 */       if (packetLength == this.maxThreeBytes) {
/* 3534 */         reuse.setPosition(this.maxThreeBytes);
/*      */         
/*      */ 
/* 3537 */         isMultiPacket = true;
/*      */         
/* 3539 */         packetLength = readRemainingMultiPackets(reuse, multiPacketSeq);
/*      */       }
/*      */       
/* 3542 */       if (!isMultiPacket) {
/* 3543 */         reuse.getByteBuffer()[packetLength] = 0;
/*      */       }
/*      */       
/* 3546 */       if (this.connection.getMaintainTimeStats()) {
/* 3547 */         this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/*      */       }
/*      */       
/* 3550 */       return reuse;
/*      */     } catch (IOException ioEx) {
/* 3552 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */     catch (OutOfMemoryError oom)
/*      */     {
/*      */       try {
/* 3557 */         clearInputStream();
/*      */       }
/*      */       catch (Exception ex) {}
/*      */       try {
/* 3561 */         this.connection.realClose(false, false, true, oom);
/*      */       }
/*      */       catch (Exception ex) {}
/* 3564 */       throw oom;
/*      */     }
/*      */   }
/*      */   
/*      */   private int readRemainingMultiPackets(Buffer reuse, byte multiPacketSeq) throws IOException, SQLException
/*      */   {
/* 3570 */     int packetLength = -1;
/* 3571 */     Buffer multiPacket = null;
/*      */     do
/*      */     {
/* 3574 */       int lengthRead = readFully(this.mysqlInput, this.packetHeaderBuf, 0, 4);
/* 3575 */       if (lengthRead < 4) {
/* 3576 */         forceClose();
/* 3577 */         throw new IOException(Messages.getString("MysqlIO.47"));
/*      */       }
/*      */       
/* 3580 */       packetLength = (this.packetHeaderBuf[0] & 0xFF) + ((this.packetHeaderBuf[1] & 0xFF) << 8) + ((this.packetHeaderBuf[2] & 0xFF) << 16);
/* 3581 */       if (multiPacket == null) {
/* 3582 */         multiPacket = new Buffer(packetLength);
/*      */       }
/*      */       
/* 3585 */       if ((!this.useNewLargePackets) && (packetLength == 1)) {
/* 3586 */         clearInputStream();
/* 3587 */         break;
/*      */       }
/*      */       
/* 3590 */       multiPacketSeq = (byte)(multiPacketSeq + 1);
/* 3591 */       if (multiPacketSeq != this.packetHeaderBuf[3]) {
/* 3592 */         throw new IOException(Messages.getString("MysqlIO.49"));
/*      */       }
/*      */       
/*      */ 
/* 3596 */       multiPacket.setPosition(0);
/*      */       
/*      */ 
/* 3599 */       multiPacket.setBufLength(packetLength);
/*      */       
/*      */ 
/* 3602 */       byte[] byteBuf = multiPacket.getByteBuffer();
/* 3603 */       int lengthToWrite = packetLength;
/*      */       
/* 3605 */       int bytesRead = readFully(this.mysqlInput, byteBuf, 0, packetLength);
/*      */       
/* 3607 */       if (bytesRead != lengthToWrite) {
/* 3608 */         throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, SQLError.createSQLException(Messages.getString("MysqlIO.50") + lengthToWrite + Messages.getString("MysqlIO.51") + bytesRead + ".", getExceptionInterceptor()), getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3614 */       reuse.writeBytesNoNull(byteBuf, 0, lengthToWrite);
/* 3615 */     } while (packetLength == this.maxThreeBytes);
/*      */     
/* 3617 */     reuse.setPosition(0);
/* 3618 */     reuse.setWasMultiPacket(true);
/* 3619 */     return packetLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkPacketSequencing(byte multiPacketSeq)
/*      */     throws SQLException
/*      */   {
/* 3627 */     if ((multiPacketSeq == Byte.MIN_VALUE) && (this.readPacketSequence != Byte.MAX_VALUE)) {
/* 3628 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -128, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3632 */     if ((this.readPacketSequence == -1) && (multiPacketSeq != 0)) {
/* 3633 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # -1, but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3637 */     if ((multiPacketSeq != Byte.MIN_VALUE) && (this.readPacketSequence != -1) && (multiPacketSeq != this.readPacketSequence + 1)) {
/* 3638 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, new IOException("Packets out of order, expected packet # " + (this.readPacketSequence + 1) + ", but received packet # " + multiPacketSeq), getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   void enableMultiQueries()
/*      */     throws SQLException
/*      */   {
/* 3645 */     Buffer buf = getSharedSendPacket();
/*      */     
/* 3647 */     buf.clear();
/* 3648 */     buf.writeByte((byte)27);
/* 3649 */     buf.writeInt(0);
/* 3650 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */   
/*      */   void disableMultiQueries() throws SQLException {
/* 3654 */     Buffer buf = getSharedSendPacket();
/*      */     
/* 3656 */     buf.clear();
/* 3657 */     buf.writeByte((byte)27);
/* 3658 */     buf.writeInt(1);
/* 3659 */     sendCommand(27, null, buf, false, null, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void send(Buffer packet, int packetLen)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3670 */       if ((this.maxAllowedPacket > 0) && (packetLen > this.maxAllowedPacket)) {
/* 3671 */         throw new PacketTooBigException(packetLen, this.maxAllowedPacket);
/*      */       }
/*      */       
/* 3674 */       if ((this.serverMajorVersion >= 4) && ((packetLen - 4 >= this.maxThreeBytes) || ((this.useCompression) && (packetLen - 4 >= this.maxThreeBytes - 3))))
/*      */       {
/* 3676 */         sendSplitPackets(packet, packetLen);
/*      */       }
/*      */       else {
/* 3679 */         this.packetSequence = ((byte)(this.packetSequence + 1));
/*      */         
/* 3681 */         Buffer packetToSend = packet;
/* 3682 */         packetToSend.setPosition(0);
/* 3683 */         packetToSend.writeLongInt(packetLen - 4);
/* 3684 */         packetToSend.writeByte(this.packetSequence);
/*      */         
/* 3686 */         if (this.useCompression) {
/* 3687 */           this.compressedPacketSequence = ((byte)(this.compressedPacketSequence + 1));
/* 3688 */           int originalPacketLen = packetLen;
/*      */           
/* 3690 */           packetToSend = compressPacket(packetToSend, 0, packetLen);
/* 3691 */           packetLen = packetToSend.getPosition();
/*      */           
/* 3693 */           if (this.traceProtocol) {
/* 3694 */             StringBuilder traceMessageBuf = new StringBuilder();
/*      */             
/* 3696 */             traceMessageBuf.append(Messages.getString("MysqlIO.57"));
/* 3697 */             traceMessageBuf.append(getPacketDumpToLog(packetToSend, packetLen));
/* 3698 */             traceMessageBuf.append(Messages.getString("MysqlIO.58"));
/* 3699 */             traceMessageBuf.append(getPacketDumpToLog(packet, originalPacketLen));
/*      */             
/* 3701 */             this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */           }
/*      */           
/*      */         }
/* 3705 */         else if (this.traceProtocol) {
/* 3706 */           StringBuilder traceMessageBuf = new StringBuilder();
/*      */           
/* 3708 */           traceMessageBuf.append(Messages.getString("MysqlIO.59"));
/* 3709 */           traceMessageBuf.append("host: '");
/* 3710 */           traceMessageBuf.append(this.host);
/* 3711 */           traceMessageBuf.append("' threadId: '");
/* 3712 */           traceMessageBuf.append(this.threadId);
/* 3713 */           traceMessageBuf.append("'\n");
/* 3714 */           traceMessageBuf.append(packetToSend.dump(packetLen));
/*      */           
/* 3716 */           this.connection.getLog().logTrace(traceMessageBuf.toString());
/*      */         }
/*      */         
/*      */ 
/* 3720 */         this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, packetLen);
/* 3721 */         this.mysqlOutput.flush();
/*      */       }
/*      */       
/* 3724 */       if (this.enablePacketDebug) {
/* 3725 */         enqueuePacketForDebugging(true, false, packetLen + 5, this.packetHeaderBuf, packet);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3731 */       if (packet == this.sharedSendPacket) {
/* 3732 */         reclaimLargeSharedSendPacket();
/*      */       }
/*      */       
/* 3735 */       if (this.connection.getMaintainTimeStats()) {
/* 3736 */         this.lastPacketSentTimeMs = System.currentTimeMillis();
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3739 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
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
/*      */   private final ResultSetImpl sendFileToServer(StatementImpl callingStatement, String fileName)
/*      */     throws SQLException
/*      */   {
/* 3755 */     if (this.useCompression) {
/* 3756 */       this.compressedPacketSequence = ((byte)(this.compressedPacketSequence + 1));
/*      */     }
/*      */     
/* 3759 */     Buffer filePacket = this.loadFileBufRef == null ? null : (Buffer)this.loadFileBufRef.get();
/*      */     
/* 3761 */     int bigPacketLength = Math.min(this.connection.getMaxAllowedPacket() - 12, alignPacketSize(this.connection.getMaxAllowedPacket() - 16, 4096) - 12);
/*      */     
/*      */ 
/* 3764 */     int oneMeg = 1048576;
/*      */     
/* 3766 */     int smallerPacketSizeAligned = Math.min(oneMeg - 12, alignPacketSize(oneMeg - 16, 4096) - 12);
/*      */     
/* 3768 */     int packetLength = Math.min(smallerPacketSizeAligned, bigPacketLength);
/*      */     
/* 3770 */     if (filePacket == null) {
/*      */       try {
/* 3772 */         filePacket = new Buffer(packetLength + 4);
/* 3773 */         this.loadFileBufRef = new SoftReference(filePacket);
/*      */       } catch (OutOfMemoryError oom) {
/* 3775 */         throw SQLError.createSQLException("Could not allocate packet of " + packetLength + " bytes required for LOAD DATA LOCAL INFILE operation." + " Try increasing max heap allocation for JVM or decreasing server variable 'max_allowed_packet'", "S1001", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3783 */     filePacket.clear();
/* 3784 */     send(filePacket, 0);
/*      */     
/* 3786 */     byte[] fileBuf = new byte[packetLength];
/*      */     
/* 3788 */     BufferedInputStream fileIn = null;
/*      */     try
/*      */     {
/* 3791 */       if (!this.connection.getAllowLoadLocalInfile()) {
/* 3792 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.LoadDataLocalNotAllowed"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       
/*      */ 
/* 3796 */       InputStream hookedStream = null;
/*      */       
/* 3798 */       if (callingStatement != null) {
/* 3799 */         hookedStream = callingStatement.getLocalInfileInputStream();
/*      */       }
/*      */       
/* 3802 */       if (hookedStream != null) {
/* 3803 */         fileIn = new BufferedInputStream(hookedStream);
/* 3804 */       } else if (!this.connection.getAllowUrlInLocalInfile()) {
/* 3805 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */ 
/*      */       }
/* 3808 */       else if (fileName.indexOf(':') != -1) {
/*      */         try {
/* 3810 */           URL urlFromFileName = new URL(fileName);
/* 3811 */           fileIn = new BufferedInputStream(urlFromFileName.openStream());
/*      */         }
/*      */         catch (MalformedURLException badUrlEx) {
/* 3814 */           fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */         }
/*      */       } else {
/* 3817 */         fileIn = new BufferedInputStream(new FileInputStream(fileName));
/*      */       }
/*      */       
/*      */ 
/* 3821 */       int bytesRead = 0;
/*      */       
/* 3823 */       while ((bytesRead = fileIn.read(fileBuf)) != -1) {
/* 3824 */         filePacket.clear();
/* 3825 */         filePacket.writeBytesNoNull(fileBuf, 0, bytesRead);
/* 3826 */         send(filePacket, filePacket.getPosition());
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 3829 */       StringBuilder messageBuf = new StringBuilder(Messages.getString("MysqlIO.60"));
/*      */       
/* 3831 */       if ((fileName != null) && (!this.connection.getParanoid())) {
/* 3832 */         messageBuf.append("'");
/* 3833 */         messageBuf.append(fileName);
/* 3834 */         messageBuf.append("'");
/*      */       }
/*      */       
/* 3837 */       messageBuf.append(Messages.getString("MysqlIO.63"));
/*      */       
/* 3839 */       if (!this.connection.getParanoid()) {
/* 3840 */         messageBuf.append(Messages.getString("MysqlIO.64"));
/* 3841 */         messageBuf.append(Util.stackTraceToString(ioEx));
/*      */       }
/*      */       
/* 3844 */       throw SQLError.createSQLException(messageBuf.toString(), "S1009", getExceptionInterceptor());
/*      */     } finally {
/* 3846 */       if (fileIn != null) {
/*      */         try {
/* 3848 */           fileIn.close();
/*      */         } catch (Exception ex) {
/* 3850 */           SQLException sqlEx = SQLError.createSQLException(Messages.getString("MysqlIO.65"), "S1000", ex, getExceptionInterceptor());
/*      */           
/*      */ 
/* 3853 */           throw sqlEx;
/*      */         }
/*      */         
/* 3856 */         fileIn = null;
/*      */       }
/*      */       else {
/* 3859 */         filePacket.clear();
/* 3860 */         send(filePacket, filePacket.getPosition());
/* 3861 */         checkErrorPacket();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3866 */     filePacket.clear();
/* 3867 */     send(filePacket, filePacket.getPosition());
/*      */     
/* 3869 */     Buffer resultPacket = checkErrorPacket();
/*      */     
/* 3871 */     return buildResultSetWithUpdates(callingStatement, resultPacket);
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
/*      */   private Buffer checkErrorPacket(int command)
/*      */     throws SQLException
/*      */   {
/* 3887 */     Buffer resultPacket = null;
/* 3888 */     this.serverStatus = 0;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 3893 */       resultPacket = reuseAndReadPacket(this.reusablePacket);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/* 3896 */       throw sqlEx;
/*      */     } catch (Exception fallThru) {
/* 3898 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, fallThru, getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 3902 */     checkErrorPacket(resultPacket);
/*      */     
/* 3904 */     return resultPacket;
/*      */   }
/*      */   
/*      */   private void checkErrorPacket(Buffer resultPacket) throws SQLException
/*      */   {
/* 3909 */     int statusCode = resultPacket.readByte();
/*      */     
/*      */ 
/* 3912 */     if (statusCode == -1)
/*      */     {
/* 3914 */       int errno = 2000;
/*      */       
/* 3916 */       if (this.protocolVersion > 9) {
/* 3917 */         errno = resultPacket.readInt();
/*      */         
/* 3919 */         String xOpen = null;
/*      */         
/* 3921 */         String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/*      */         
/* 3923 */         if (serverErrorMessage.charAt(0) == '#')
/*      */         {
/*      */ 
/* 3926 */           if (serverErrorMessage.length() > 6) {
/* 3927 */             xOpen = serverErrorMessage.substring(1, 6);
/* 3928 */             serverErrorMessage = serverErrorMessage.substring(6);
/*      */             
/* 3930 */             if (xOpen.equals("HY000")) {
/* 3931 */               xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */             }
/*      */           } else {
/* 3934 */             xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */           }
/*      */         } else {
/* 3937 */           xOpen = SQLError.mysqlToSqlState(errno, this.connection.getUseSqlStateCodes());
/*      */         }
/*      */         
/* 3940 */         clearInputStream();
/*      */         
/* 3942 */         StringBuilder errorBuf = new StringBuilder();
/*      */         
/* 3944 */         String xOpenErrorMessage = SQLError.get(xOpen);
/*      */         
/* 3946 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 3947 */           (xOpenErrorMessage != null)) {
/* 3948 */           errorBuf.append(xOpenErrorMessage);
/* 3949 */           errorBuf.append(Messages.getString("MysqlIO.68"));
/*      */         }
/*      */         
/*      */ 
/* 3953 */         errorBuf.append(serverErrorMessage);
/*      */         
/* 3955 */         if ((!this.connection.getUseOnlyServerErrorMessages()) && 
/* 3956 */           (xOpenErrorMessage != null)) {
/* 3957 */           errorBuf.append("\"");
/*      */         }
/*      */         
/*      */ 
/* 3961 */         appendDeadlockStatusInformation(xOpen, errorBuf);
/*      */         
/* 3963 */         if ((xOpen != null) && (xOpen.startsWith("22"))) {
/* 3964 */           throw new MysqlDataTruncation(errorBuf.toString(), 0, true, false, 0, 0, errno);
/*      */         }
/* 3966 */         throw SQLError.createSQLException(errorBuf.toString(), xOpen, errno, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */       
/*      */ 
/* 3970 */       String serverErrorMessage = resultPacket.readString(this.connection.getErrorMessageEncoding(), getExceptionInterceptor());
/* 3971 */       clearInputStream();
/*      */       
/* 3973 */       if (serverErrorMessage.indexOf(Messages.getString("MysqlIO.70")) != -1) {
/* 3974 */         throw SQLError.createSQLException(SQLError.get("S0022") + ", " + serverErrorMessage, "S0022", -1, false, getExceptionInterceptor(), this.connection);
/*      */       }
/*      */       
/*      */ 
/* 3978 */       StringBuilder errorBuf = new StringBuilder(Messages.getString("MysqlIO.72"));
/* 3979 */       errorBuf.append(serverErrorMessage);
/* 3980 */       errorBuf.append("\"");
/*      */       
/* 3982 */       throw SQLError.createSQLException(SQLError.get("S1000") + ", " + errorBuf.toString(), "S1000", -1, false, getExceptionInterceptor(), this.connection);
/*      */     }
/*      */   }
/*      */   
/*      */   private void appendDeadlockStatusInformation(String xOpen, StringBuilder errorBuf) throws SQLException
/*      */   {
/* 3988 */     if ((this.connection.getIncludeInnodbStatusInDeadlockExceptions()) && (xOpen != null) && ((xOpen.startsWith("40")) || (xOpen.startsWith("41"))) && (this.streamingData == null))
/*      */     {
/* 3990 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 3993 */         rs = sqlQueryDirect(null, "SHOW ENGINE INNODB STATUS", this.connection.getEncoding(), null, -1, 1003, 1007, false, this.connection.getCatalog(), null);
/*      */         
/*      */ 
/* 3996 */         if (rs.next()) {
/* 3997 */           errorBuf.append("\n\n");
/* 3998 */           errorBuf.append(rs.getString("Status"));
/*      */         } else {
/* 4000 */           errorBuf.append("\n\n");
/* 4001 */           errorBuf.append(Messages.getString("MysqlIO.NoInnoDBStatusFound"));
/*      */         }
/*      */       } catch (Exception ex) {
/* 4004 */         errorBuf.append("\n\n");
/* 4005 */         errorBuf.append(Messages.getString("MysqlIO.InnoDBStatusFailed"));
/* 4006 */         errorBuf.append("\n\n");
/* 4007 */         errorBuf.append(Util.stackTraceToString(ex));
/*      */       } finally {
/* 4009 */         if (rs != null) {
/* 4010 */           rs.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4015 */     if (this.connection.getIncludeThreadDumpInDeadlockExceptions()) {
/* 4016 */       errorBuf.append("\n\n*** Java threads running at time of deadlock ***\n\n");
/*      */       
/* 4018 */       ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
/* 4019 */       long[] threadIds = threadMBean.getAllThreadIds();
/*      */       
/* 4021 */       ThreadInfo[] threads = threadMBean.getThreadInfo(threadIds, Integer.MAX_VALUE);
/* 4022 */       Object activeThreads = new ArrayList();
/*      */       
/* 4024 */       for (ThreadInfo info : threads) {
/* 4025 */         if (info != null) {
/* 4026 */           ((List)activeThreads).add(info);
/*      */         }
/*      */       }
/*      */       
/* 4030 */       for (ThreadInfo threadInfo : (List)activeThreads)
/*      */       {
/*      */ 
/* 4033 */         errorBuf.append('"');
/* 4034 */         errorBuf.append(threadInfo.getThreadName());
/* 4035 */         errorBuf.append("\" tid=");
/* 4036 */         errorBuf.append(threadInfo.getThreadId());
/* 4037 */         errorBuf.append(" ");
/* 4038 */         errorBuf.append(threadInfo.getThreadState());
/*      */         
/* 4040 */         if (threadInfo.getLockName() != null) {
/* 4041 */           errorBuf.append(" on lock=" + threadInfo.getLockName());
/*      */         }
/* 4043 */         if (threadInfo.isSuspended()) {
/* 4044 */           errorBuf.append(" (suspended)");
/*      */         }
/* 4046 */         if (threadInfo.isInNative()) {
/* 4047 */           errorBuf.append(" (running in native)");
/*      */         }
/*      */         
/* 4050 */         StackTraceElement[] stackTrace = threadInfo.getStackTrace();
/*      */         
/* 4052 */         if (stackTrace.length > 0) {
/* 4053 */           errorBuf.append(" in ");
/* 4054 */           errorBuf.append(stackTrace[0].getClassName());
/* 4055 */           errorBuf.append(".");
/* 4056 */           errorBuf.append(stackTrace[0].getMethodName());
/* 4057 */           errorBuf.append("()");
/*      */         }
/*      */         
/* 4060 */         errorBuf.append("\n");
/*      */         
/* 4062 */         if (threadInfo.getLockOwnerName() != null) {
/* 4063 */           errorBuf.append("\t owned by " + threadInfo.getLockOwnerName() + " Id=" + threadInfo.getLockOwnerId());
/* 4064 */           errorBuf.append("\n");
/*      */         }
/*      */         
/* 4067 */         for (int j = 0; j < stackTrace.length; j++) {
/* 4068 */           StackTraceElement ste = stackTrace[j];
/* 4069 */           errorBuf.append("\tat " + ste.toString());
/* 4070 */           errorBuf.append("\n");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final void sendSplitPackets(Buffer packet, int packetLen)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4086 */       Buffer packetToSend = this.splitBufRef == null ? null : (Buffer)this.splitBufRef.get();
/* 4087 */       Buffer toCompress = (!this.useCompression) || (this.compressBufRef == null) ? null : (Buffer)this.compressBufRef.get();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4093 */       if (packetToSend == null) {
/* 4094 */         packetToSend = new Buffer(this.maxThreeBytes + 4);
/* 4095 */         this.splitBufRef = new SoftReference(packetToSend);
/*      */       }
/* 4097 */       if (this.useCompression) {
/* 4098 */         int cbuflen = packetLen + (packetLen / this.maxThreeBytes + 1) * 4;
/* 4099 */         if (toCompress == null) {
/* 4100 */           toCompress = new Buffer(cbuflen);
/* 4101 */           this.compressBufRef = new SoftReference(toCompress);
/* 4102 */         } else if (toCompress.getBufLength() < cbuflen) {
/* 4103 */           toCompress.setPosition(toCompress.getBufLength());
/* 4104 */           toCompress.ensureCapacity(cbuflen - toCompress.getBufLength());
/*      */         }
/*      */       }
/*      */       
/* 4108 */       int len = packetLen - 4;
/* 4109 */       int splitSize = this.maxThreeBytes;
/* 4110 */       int originalPacketPos = 4;
/* 4111 */       byte[] origPacketBytes = packet.getByteBuffer();
/*      */       
/* 4113 */       int toCompressPosition = 0;
/*      */       
/*      */ 
/* 4116 */       while (len >= 0) {
/* 4117 */         this.packetSequence = ((byte)(this.packetSequence + 1));
/*      */         
/* 4119 */         if (len < splitSize) {
/* 4120 */           splitSize = len;
/*      */         }
/*      */         
/* 4123 */         packetToSend.setPosition(0);
/* 4124 */         packetToSend.writeLongInt(splitSize);
/* 4125 */         packetToSend.writeByte(this.packetSequence);
/* 4126 */         if (len > 0) {
/* 4127 */           System.arraycopy(origPacketBytes, originalPacketPos, packetToSend.getByteBuffer(), 4, splitSize);
/*      */         }
/*      */         
/* 4130 */         if (this.useCompression) {
/* 4131 */           System.arraycopy(packetToSend.getByteBuffer(), 0, toCompress.getByteBuffer(), toCompressPosition, 4 + splitSize);
/* 4132 */           toCompressPosition += 4 + splitSize;
/*      */         } else {
/* 4134 */           this.mysqlOutput.write(packetToSend.getByteBuffer(), 0, 4 + splitSize);
/* 4135 */           this.mysqlOutput.flush();
/*      */         }
/*      */         
/* 4138 */         originalPacketPos += splitSize;
/* 4139 */         len -= this.maxThreeBytes;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4144 */       if (this.useCompression) {
/* 4145 */         len = toCompressPosition;
/* 4146 */         toCompressPosition = 0;
/* 4147 */         splitSize = this.maxThreeBytes - 3;
/* 4148 */         while (len >= 0) {
/* 4149 */           this.compressedPacketSequence = ((byte)(this.compressedPacketSequence + 1));
/*      */           
/* 4151 */           if (len < splitSize) {
/* 4152 */             splitSize = len;
/*      */           }
/*      */           
/* 4155 */           Buffer compressedPacketToSend = compressPacket(toCompress, toCompressPosition, splitSize);
/* 4156 */           packetLen = compressedPacketToSend.getPosition();
/* 4157 */           this.mysqlOutput.write(compressedPacketToSend.getByteBuffer(), 0, packetLen);
/* 4158 */           this.mysqlOutput.flush();
/*      */           
/* 4160 */           toCompressPosition += splitSize;
/* 4161 */           len -= this.maxThreeBytes - 3;
/*      */         }
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 4165 */       throw SQLError.createCommunicationsException(this.connection, this.lastPacketSentTimeMs, this.lastPacketReceivedTimeMs, ioEx, getExceptionInterceptor());
/*      */     }
/*      */   }
/*      */   
/*      */   private void reclaimLargeSharedSendPacket()
/*      */   {
/* 4171 */     if ((this.sharedSendPacket != null) && (this.sharedSendPacket.getCapacity() > 1048576)) {
/* 4172 */       this.sharedSendPacket = new Buffer(1024);
/*      */     }
/*      */   }
/*      */   
/*      */   boolean hadWarnings() {
/* 4177 */     return this.hadWarnings;
/*      */   }
/*      */   
/*      */   void scanForAndThrowDataTruncation() throws SQLException {
/* 4181 */     if ((this.streamingData == null) && (versionMeetsMinimum(4, 1, 0)) && (this.connection.getJdbcCompliantTruncation()) && (this.warningCount > 0)) {
/* 4182 */       SQLError.convertShowWarningsToSQLWarnings(this.connection, this.warningCount, true);
/*      */     }
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
/*      */   private void secureAuth(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 4200 */     if (packet == null) {
/* 4201 */       packet = new Buffer(packLength);
/*      */     }
/*      */     
/* 4204 */     if (writeClientParams) {
/* 4205 */       if (this.use41Extensions) {
/* 4206 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 4207 */           packet.writeLong(this.clientParam);
/* 4208 */           packet.writeLong(this.maxThreeBytes);
/*      */           
/*      */ 
/* 4211 */           packet.writeByte((byte)8);
/*      */           
/*      */ 
/* 4214 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 4216 */           packet.writeLong(this.clientParam);
/* 4217 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 4220 */         packet.writeInt((int)this.clientParam);
/* 4221 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4226 */     packet.writeString(user, "Cp1252", this.connection);
/*      */     
/* 4228 */     if (password.length() != 0)
/*      */     {
/* 4230 */       packet.writeString("xxxxxxxx", "Cp1252", this.connection);
/*      */     }
/*      */     else {
/* 4233 */       packet.writeString("", "Cp1252", this.connection);
/*      */     }
/*      */     
/* 4236 */     if (this.useConnectWithDb) {
/* 4237 */       packet.writeString(database, "Cp1252", this.connection);
/*      */     }
/*      */     
/* 4240 */     send(packet, packet.getPosition());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 4245 */     if (password.length() > 0) {
/* 4246 */       Buffer b = readPacket();
/*      */       
/* 4248 */       b.setPosition(0);
/*      */       
/* 4250 */       byte[] replyAsBytes = b.getByteBuffer();
/*      */       
/* 4252 */       if ((replyAsBytes.length == 24) && (replyAsBytes[0] != 0))
/*      */       {
/* 4254 */         if (replyAsBytes[0] != 42) {
/*      */           try
/*      */           {
/* 4257 */             byte[] buff = Security.passwordHashStage1(password);
/*      */             
/*      */ 
/* 4260 */             byte[] passwordHash = new byte[buff.length];
/* 4261 */             System.arraycopy(buff, 0, passwordHash, 0, buff.length);
/*      */             
/*      */ 
/* 4264 */             passwordHash = Security.passwordHashStage2(passwordHash, replyAsBytes);
/*      */             
/* 4266 */             byte[] packetDataAfterSalt = new byte[replyAsBytes.length - 4];
/*      */             
/* 4268 */             System.arraycopy(replyAsBytes, 4, packetDataAfterSalt, 0, replyAsBytes.length - 4);
/*      */             
/* 4270 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */             
/*      */ 
/* 4273 */             Security.xorString(packetDataAfterSalt, mysqlScrambleBuff, passwordHash, 20);
/*      */             
/*      */ 
/* 4276 */             Security.xorString(mysqlScrambleBuff, buff, buff, 20);
/*      */             
/* 4278 */             Buffer packet2 = new Buffer(25);
/* 4279 */             packet2.writeBytesNoNull(buff);
/*      */             
/* 4281 */             this.packetSequence = ((byte)(this.packetSequence + 1));
/*      */             
/* 4283 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 4285 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */         } else {
/*      */           try
/*      */           {
/* 4291 */             byte[] passwordHash = Security.createKeyFromOldPassword(password);
/*      */             
/*      */ 
/* 4294 */             byte[] netReadPos4 = new byte[replyAsBytes.length - 4];
/*      */             
/* 4296 */             System.arraycopy(replyAsBytes, 4, netReadPos4, 0, replyAsBytes.length - 4);
/*      */             
/* 4298 */             byte[] mysqlScrambleBuff = new byte[20];
/*      */             
/*      */ 
/* 4301 */             Security.xorString(netReadPos4, mysqlScrambleBuff, passwordHash, 20);
/*      */             
/*      */ 
/* 4304 */             String scrambledPassword = Util.scramble(StringUtils.toString(mysqlScrambleBuff), password);
/*      */             
/* 4306 */             Buffer packet2 = new Buffer(packLength);
/* 4307 */             packet2.writeString(scrambledPassword, "Cp1252", this.connection);
/* 4308 */             this.packetSequence = ((byte)(this.packetSequence + 1));
/*      */             
/* 4310 */             send(packet2, 24);
/*      */           } catch (NoSuchAlgorithmException nse) {
/* 4312 */             throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
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
/*      */   void secureAuth411(Buffer packet, int packLength, String user, String password, String database, boolean writeClientParams)
/*      */     throws SQLException
/*      */   {
/* 4333 */     String enc = getEncodingForHandshake();
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
/* 4352 */     if (packet == null) {
/* 4353 */       packet = new Buffer(packLength);
/*      */     }
/*      */     
/* 4356 */     if (writeClientParams) {
/* 4357 */       if (this.use41Extensions) {
/* 4358 */         if (versionMeetsMinimum(4, 1, 1)) {
/* 4359 */           packet.writeLong(this.clientParam);
/* 4360 */           packet.writeLong(this.maxThreeBytes);
/*      */           
/* 4362 */           appendCharsetByteForHandshake(packet, enc);
/*      */           
/*      */ 
/* 4365 */           packet.writeBytesNoNull(new byte[23]);
/*      */         } else {
/* 4367 */           packet.writeLong(this.clientParam);
/* 4368 */           packet.writeLong(this.maxThreeBytes);
/*      */         }
/*      */       } else {
/* 4371 */         packet.writeInt((int)this.clientParam);
/* 4372 */         packet.writeLongInt(this.maxThreeBytes);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4377 */     if (user != null) {
/* 4378 */       packet.writeString(user, enc, this.connection);
/*      */     }
/*      */     
/* 4381 */     if (password.length() != 0) {
/* 4382 */       packet.writeByte((byte)20);
/*      */       try
/*      */       {
/* 4385 */         packet.writeBytesNoNull(Security.scramble411(password, this.seed, this.connection.getPasswordCharacterEncoding()));
/*      */       } catch (NoSuchAlgorithmException nse) {
/* 4387 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {
/* 4390 */         throw SQLError.createSQLException(Messages.getString("MysqlIO.91") + Messages.getString("MysqlIO.92"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 4395 */       packet.writeByte((byte)0);
/*      */     }
/*      */     
/* 4398 */     if (this.useConnectWithDb) {
/* 4399 */       packet.writeString(database, enc, this.connection);
/*      */     }
/*      */     else {
/* 4402 */       packet.writeByte((byte)0);
/*      */     }
/*      */     
/*      */ 
/* 4406 */     if ((this.serverCapabilities & 0x100000) != 0) {
/* 4407 */       sendConnectionAttributes(packet, enc, this.connection);
/*      */     }
/*      */     
/* 4410 */     send(packet, packet.getPosition());
/*      */     
/* 4412 */     byte savePacketSequence = this.packetSequence++;
/*      */     
/* 4414 */     Buffer reply = checkErrorPacket();
/*      */     
/* 4416 */     if (reply.isAuthMethodSwitchRequestPacket())
/*      */     {
/*      */ 
/*      */ 
/* 4420 */       savePacketSequence = (byte)(savePacketSequence + 1);this.packetSequence = savePacketSequence;
/* 4421 */       packet.clear();
/*      */       
/* 4423 */       String seed323 = this.seed.substring(0, 8);
/* 4424 */       packet.writeString(Util.newCrypt(password, seed323, this.connection.getPasswordCharacterEncoding()));
/* 4425 */       send(packet, packet.getPosition());
/*      */       
/*      */ 
/* 4428 */       checkErrorPacket();
/*      */     }
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
/*      */   private final ResultSetRow unpackBinaryResultSetRow(Field[] fields, Buffer binaryData, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4444 */     int numFields = fields.length;
/*      */     
/* 4446 */     byte[][] unpackedRowData = new byte[numFields][];
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4452 */     int nullCount = (numFields + 9) / 8;
/* 4453 */     int nullMaskPos = binaryData.getPosition();
/* 4454 */     binaryData.setPosition(nullMaskPos + nullCount);
/* 4455 */     int bit = 4;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4461 */     for (int i = 0; i < numFields; i++) {
/* 4462 */       if ((binaryData.readByte(nullMaskPos) & bit) != 0) {
/* 4463 */         unpackedRowData[i] = null;
/*      */       }
/* 4465 */       else if (resultSetConcurrency != 1008) {
/* 4466 */         extractNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       } else {
/* 4468 */         unpackNativeEncodedColumn(binaryData, fields, i, unpackedRowData);
/*      */       }
/*      */       
/*      */ 
/* 4472 */       if ((bit <<= 1 & 0xFF) == 0) {
/* 4473 */         bit = 1;
/*      */         
/* 4475 */         nullMaskPos++;
/*      */       }
/*      */     }
/*      */     
/* 4479 */     return new ByteArrayRow(unpackedRowData, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */   private final void extractNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData) throws SQLException {
/* 4483 */     Field curField = fields[columnIndex];
/*      */     int length;
/* 4485 */     switch (curField.getMysqlType())
/*      */     {
/*      */     case 6: 
/*      */       break;
/*      */     
/*      */     case 1: 
/* 4491 */       unpackedRowData[columnIndex] = { binaryData.readByte() };
/* 4492 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/*      */     case 13: 
/* 4497 */       unpackedRowData[columnIndex] = binaryData.getBytes(2);
/* 4498 */       break;
/*      */     
/*      */     case 3: 
/*      */     case 9: 
/* 4502 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4503 */       break;
/*      */     
/*      */     case 8: 
/* 4506 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4507 */       break;
/*      */     
/*      */     case 4: 
/* 4510 */       unpackedRowData[columnIndex] = binaryData.getBytes(4);
/* 4511 */       break;
/*      */     
/*      */     case 5: 
/* 4514 */       unpackedRowData[columnIndex] = binaryData.getBytes(8);
/* 4515 */       break;
/*      */     
/*      */     case 11: 
/* 4518 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4520 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */       
/* 4522 */       break;
/*      */     
/*      */     case 10: 
/* 4525 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4527 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/*      */       
/* 4529 */       break;
/*      */     case 7: 
/*      */     case 12: 
/* 4532 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4534 */       unpackedRowData[columnIndex] = binaryData.getBytes(length);
/* 4535 */       break;
/*      */     case 0: 
/*      */     case 15: 
/*      */     case 16: 
/*      */     case 245: 
/*      */     case 246: 
/*      */     case 249: 
/*      */     case 250: 
/*      */     case 251: 
/*      */     case 252: 
/*      */     case 253: 
/*      */     case 254: 
/*      */     case 255: 
/* 4548 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */       
/* 4550 */       break;
/*      */     default: 
/* 4552 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */   private final void unpackNativeEncodedColumn(Buffer binaryData, Field[] fields, int columnIndex, byte[][] unpackedRowData)
/*      */     throws SQLException
/*      */   {
/* 4560 */     Field curField = fields[columnIndex];
/*      */     int length;
/* 4562 */     int hour; int minute; int seconds; int year; int month; int day; int after1000; int after100; switch (curField.getMysqlType())
/*      */     {
/*      */     case 6: 
/*      */       break;
/*      */     
/*      */     case 1: 
/* 4568 */       byte tinyVal = binaryData.readByte();
/*      */       
/* 4570 */       if (!curField.isUnsigned()) {
/* 4571 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(tinyVal));
/*      */       } else {
/* 4573 */         short unsignedTinyVal = (short)(tinyVal & 0xFF);
/*      */         
/* 4575 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(unsignedTinyVal));
/*      */       }
/*      */       
/* 4578 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/*      */     case 13: 
/* 4583 */       short shortVal = (short)binaryData.readInt();
/*      */       
/* 4585 */       if (!curField.isUnsigned()) {
/* 4586 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(shortVal));
/*      */       } else {
/* 4588 */         int unsignedShortVal = shortVal & 0xFFFF;
/*      */         
/* 4590 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(unsignedShortVal));
/*      */       }
/*      */       
/* 4593 */       break;
/*      */     
/*      */ 
/*      */     case 3: 
/*      */     case 9: 
/* 4598 */       int intVal = (int)binaryData.readLong();
/*      */       
/* 4600 */       if (!curField.isUnsigned()) {
/* 4601 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(intVal));
/*      */       } else {
/* 4603 */         long longVal = intVal & 0xFFFFFFFF;
/*      */         
/* 4605 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(longVal));
/*      */       }
/*      */       
/* 4608 */       break;
/*      */     
/*      */ 
/*      */     case 8: 
/* 4612 */       long longVal = binaryData.readLongLong();
/*      */       
/* 4614 */       if (!curField.isUnsigned()) {
/* 4615 */         unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(longVal));
/*      */       } else {
/* 4617 */         BigInteger asBigInteger = ResultSetImpl.convertLongToUlong(longVal);
/*      */         
/* 4619 */         unpackedRowData[columnIndex] = StringUtils.getBytes(asBigInteger.toString());
/*      */       }
/*      */       
/* 4622 */       break;
/*      */     
/*      */ 
/*      */     case 4: 
/* 4626 */       float floatVal = Float.intBitsToFloat(binaryData.readIntAsLong());
/*      */       
/* 4628 */       unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(floatVal));
/*      */       
/* 4630 */       break;
/*      */     
/*      */ 
/*      */     case 5: 
/* 4634 */       double doubleVal = Double.longBitsToDouble(binaryData.readLongLong());
/*      */       
/* 4636 */       unpackedRowData[columnIndex] = StringUtils.getBytes(String.valueOf(doubleVal));
/*      */       
/* 4638 */       break;
/*      */     
/*      */ 
/*      */     case 11: 
/* 4642 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4644 */       hour = 0;
/* 4645 */       minute = 0;
/* 4646 */       seconds = 0;
/*      */       
/* 4648 */       if (length != 0) {
/* 4649 */         binaryData.readByte();
/* 4650 */         binaryData.readLong();
/* 4651 */         hour = binaryData.readByte();
/* 4652 */         minute = binaryData.readByte();
/* 4653 */         seconds = binaryData.readByte();
/*      */         
/* 4655 */         if (length > 8) {
/* 4656 */           binaryData.readLong();
/*      */         }
/*      */       }
/*      */       
/* 4660 */       byte[] timeAsBytes = new byte[8];
/*      */       
/* 4662 */       timeAsBytes[0] = ((byte)Character.forDigit(hour / 10, 10));
/* 4663 */       timeAsBytes[1] = ((byte)Character.forDigit(hour % 10, 10));
/*      */       
/* 4665 */       timeAsBytes[2] = 58;
/*      */       
/* 4667 */       timeAsBytes[3] = ((byte)Character.forDigit(minute / 10, 10));
/* 4668 */       timeAsBytes[4] = ((byte)Character.forDigit(minute % 10, 10));
/*      */       
/* 4670 */       timeAsBytes[5] = 58;
/*      */       
/* 4672 */       timeAsBytes[6] = ((byte)Character.forDigit(seconds / 10, 10));
/* 4673 */       timeAsBytes[7] = ((byte)Character.forDigit(seconds % 10, 10));
/*      */       
/* 4675 */       unpackedRowData[columnIndex] = timeAsBytes;
/*      */       
/* 4677 */       break;
/*      */     
/*      */     case 10: 
/* 4680 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4682 */       year = 0;
/* 4683 */       month = 0;
/* 4684 */       day = 0;
/*      */       
/* 4686 */       hour = 0;
/* 4687 */       minute = 0;
/* 4688 */       seconds = 0;
/*      */       
/* 4690 */       if (length != 0) {
/* 4691 */         year = binaryData.readInt();
/* 4692 */         month = binaryData.readByte();
/* 4693 */         day = binaryData.readByte();
/*      */       }
/*      */       
/* 4696 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4697 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/* 4698 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4701 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior())) {
/* 4702 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 4706 */           year = 1;
/* 4707 */           month = 1;
/* 4708 */           day = 1;
/*      */         }
/*      */       } else {
/* 4711 */         byte[] dateAsBytes = new byte[10];
/*      */         
/* 4713 */         dateAsBytes[0] = ((byte)Character.forDigit(year / 1000, 10));
/*      */         
/* 4715 */         after1000 = year % 1000;
/*      */         
/* 4717 */         dateAsBytes[1] = ((byte)Character.forDigit(after1000 / 100, 10));
/*      */         
/* 4719 */         after100 = after1000 % 100;
/*      */         
/* 4721 */         dateAsBytes[2] = ((byte)Character.forDigit(after100 / 10, 10));
/* 4722 */         dateAsBytes[3] = ((byte)Character.forDigit(after100 % 10, 10));
/*      */         
/* 4724 */         dateAsBytes[4] = 45;
/*      */         
/* 4726 */         dateAsBytes[5] = ((byte)Character.forDigit(month / 10, 10));
/* 4727 */         dateAsBytes[6] = ((byte)Character.forDigit(month % 10, 10));
/*      */         
/* 4729 */         dateAsBytes[7] = 45;
/*      */         
/* 4731 */         dateAsBytes[8] = ((byte)Character.forDigit(day / 10, 10));
/* 4732 */         dateAsBytes[9] = ((byte)Character.forDigit(day % 10, 10));
/*      */         
/* 4734 */         unpackedRowData[columnIndex] = dateAsBytes;
/*      */       }
/* 4736 */       break;
/*      */     
/*      */     case 7: 
/*      */     case 12: 
/* 4740 */       length = (int)binaryData.readFieldLength();
/*      */       
/* 4742 */       year = 0;
/* 4743 */       month = 0;
/* 4744 */       day = 0;
/*      */       
/* 4746 */       hour = 0;
/* 4747 */       minute = 0;
/* 4748 */       seconds = 0;
/*      */       
/* 4750 */       int nanos = 0;
/*      */       
/* 4752 */       if (length != 0) {
/* 4753 */         year = binaryData.readInt();
/* 4754 */         month = binaryData.readByte();
/* 4755 */         day = binaryData.readByte();
/*      */         
/* 4757 */         if (length > 4) {
/* 4758 */           hour = binaryData.readByte();
/* 4759 */           minute = binaryData.readByte();
/* 4760 */           seconds = binaryData.readByte();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4768 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4769 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior())) {
/* 4770 */           unpackedRowData[columnIndex] = null;
/*      */         }
/*      */         else {
/* 4773 */           if ("exception".equals(this.connection.getZeroDateTimeBehavior())) {
/* 4774 */             throw SQLError.createSQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 4778 */           year = 1;
/* 4779 */           month = 1;
/* 4780 */           day = 1;
/*      */         }
/*      */       } else {
/* 4783 */         int stringLength = 19;
/*      */         
/* 4785 */         byte[] nanosAsBytes = StringUtils.getBytes(Integer.toString(nanos));
/*      */         
/* 4787 */         stringLength += 1 + nanosAsBytes.length;
/*      */         
/* 4789 */         byte[] datetimeAsBytes = new byte[stringLength];
/*      */         
/* 4791 */         datetimeAsBytes[0] = ((byte)Character.forDigit(year / 1000, 10));
/*      */         
/* 4793 */         after1000 = year % 1000;
/*      */         
/* 4795 */         datetimeAsBytes[1] = ((byte)Character.forDigit(after1000 / 100, 10));
/*      */         
/* 4797 */         after100 = after1000 % 100;
/*      */         
/* 4799 */         datetimeAsBytes[2] = ((byte)Character.forDigit(after100 / 10, 10));
/* 4800 */         datetimeAsBytes[3] = ((byte)Character.forDigit(after100 % 10, 10));
/*      */         
/* 4802 */         datetimeAsBytes[4] = 45;
/*      */         
/* 4804 */         datetimeAsBytes[5] = ((byte)Character.forDigit(month / 10, 10));
/* 4805 */         datetimeAsBytes[6] = ((byte)Character.forDigit(month % 10, 10));
/*      */         
/* 4807 */         datetimeAsBytes[7] = 45;
/*      */         
/* 4809 */         datetimeAsBytes[8] = ((byte)Character.forDigit(day / 10, 10));
/* 4810 */         datetimeAsBytes[9] = ((byte)Character.forDigit(day % 10, 10));
/*      */         
/* 4812 */         datetimeAsBytes[10] = 32;
/*      */         
/* 4814 */         datetimeAsBytes[11] = ((byte)Character.forDigit(hour / 10, 10));
/* 4815 */         datetimeAsBytes[12] = ((byte)Character.forDigit(hour % 10, 10));
/*      */         
/* 4817 */         datetimeAsBytes[13] = 58;
/*      */         
/* 4819 */         datetimeAsBytes[14] = ((byte)Character.forDigit(minute / 10, 10));
/* 4820 */         datetimeAsBytes[15] = ((byte)Character.forDigit(minute % 10, 10));
/*      */         
/* 4822 */         datetimeAsBytes[16] = 58;
/*      */         
/* 4824 */         datetimeAsBytes[17] = ((byte)Character.forDigit(seconds / 10, 10));
/* 4825 */         datetimeAsBytes[18] = ((byte)Character.forDigit(seconds % 10, 10));
/*      */         
/* 4827 */         datetimeAsBytes[19] = 46;
/*      */         
/* 4829 */         int nanosOffset = 20;
/*      */         
/* 4831 */         System.arraycopy(nanosAsBytes, 0, datetimeAsBytes, 20, nanosAsBytes.length);
/*      */         
/* 4833 */         unpackedRowData[columnIndex] = datetimeAsBytes;
/*      */       }
/* 4835 */       break;
/*      */     
/*      */     case 0: 
/*      */     case 15: 
/*      */     case 16: 
/*      */     case 246: 
/*      */     case 249: 
/*      */     case 250: 
/*      */     case 251: 
/*      */     case 252: 
/*      */     case 253: 
/*      */     case 254: 
/* 4847 */       unpackedRowData[columnIndex] = binaryData.readLenByteArray(0);
/*      */       
/* 4849 */       break;
/*      */     
/*      */     default: 
/* 4852 */       throw SQLError.createSQLException(Messages.getString("MysqlIO.97") + curField.getMysqlType() + Messages.getString("MysqlIO.98") + columnIndex + Messages.getString("MysqlIO.99") + fields.length + Messages.getString("MysqlIO.100"), "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
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
/*      */   private void negotiateSSLConnection(String user, String password, String database, int packLength)
/*      */     throws SQLException
/*      */   {
/* 4871 */     if (!ExportControlled.enabled()) {
/* 4872 */       throw new ConnectionFeatureNotAvailableException(this.connection, this.lastPacketSentTimeMs, null);
/*      */     }
/*      */     
/* 4875 */     if ((this.serverCapabilities & 0x8000) != 0) {
/* 4876 */       this.clientParam |= 0x8000;
/*      */     }
/*      */     
/* 4879 */     this.clientParam |= 0x800;
/*      */     
/* 4881 */     Buffer packet = new Buffer(packLength);
/*      */     
/* 4883 */     if (this.use41Extensions) {
/* 4884 */       packet.writeLong(this.clientParam);
/* 4885 */       packet.writeLong(this.maxThreeBytes);
/* 4886 */       appendCharsetByteForHandshake(packet, getEncodingForHandshake());
/* 4887 */       packet.writeBytesNoNull(new byte[23]);
/*      */     } else {
/* 4889 */       packet.writeInt((int)this.clientParam);
/*      */     }
/*      */     
/* 4892 */     send(packet, packet.getPosition());
/*      */     
/* 4894 */     ExportControlled.transformSocketToSSLSocket(this);
/*      */   }
/*      */   
/*      */   public boolean isSSLEstablished() {
/* 4898 */     return (ExportControlled.enabled()) && (ExportControlled.isSSLEstablished(this));
/*      */   }
/*      */   
/*      */   protected int getServerStatus() {
/* 4902 */     return this.serverStatus;
/*      */   }
/*      */   
/*      */   protected List<ResultSetRow> fetchRowsViaCursor(List<ResultSetRow> fetchedRows, long statementId, Field[] columnTypes, int fetchSize, boolean useBufferRowExplicit)
/*      */     throws SQLException
/*      */   {
/* 4908 */     if (fetchedRows == null) {
/* 4909 */       fetchedRows = new ArrayList(fetchSize);
/*      */     } else {
/* 4911 */       fetchedRows.clear();
/*      */     }
/*      */     
/* 4914 */     this.sharedSendPacket.clear();
/*      */     
/* 4916 */     this.sharedSendPacket.writeByte((byte)28);
/* 4917 */     this.sharedSendPacket.writeLong(statementId);
/* 4918 */     this.sharedSendPacket.writeLong(fetchSize);
/*      */     
/* 4920 */     sendCommand(28, null, this.sharedSendPacket, true, null, 0);
/*      */     
/* 4922 */     ResultSetRow row = null;
/*      */     
/* 4924 */     while ((row = nextRow(columnTypes, columnTypes.length, true, 1007, false, useBufferRowExplicit, false, null)) != null) {
/* 4925 */       fetchedRows.add(row);
/*      */     }
/*      */     
/* 4928 */     return fetchedRows;
/*      */   }
/*      */   
/*      */   protected long getThreadId() {
/* 4932 */     return this.threadId;
/*      */   }
/*      */   
/*      */   protected boolean useNanosForElapsedTime() {
/* 4936 */     return this.useNanosForElapsedTime;
/*      */   }
/*      */   
/*      */   protected long getSlowQueryThreshold() {
/* 4940 */     return this.slowQueryThreshold;
/*      */   }
/*      */   
/*      */   protected String getQueryTimingUnits() {
/* 4944 */     return this.queryTimingUnits;
/*      */   }
/*      */   
/*      */   protected int getCommandCount() {
/* 4948 */     return this.commandCount;
/*      */   }
/*      */   
/*      */   private void checkTransactionState(int oldStatus) throws SQLException {
/* 4952 */     boolean previouslyInTrans = (oldStatus & 0x1) != 0;
/* 4953 */     boolean currentlyInTrans = (this.serverStatus & 0x1) != 0;
/*      */     
/* 4955 */     if ((previouslyInTrans) && (!currentlyInTrans)) {
/* 4956 */       this.connection.transactionCompleted();
/* 4957 */     } else if ((!previouslyInTrans) && (currentlyInTrans)) {
/* 4958 */       this.connection.transactionBegun();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void setStatementInterceptors(List<StatementInterceptorV2> statementInterceptors) {
/* 4963 */     this.statementInterceptors = (statementInterceptors.isEmpty() ? null : statementInterceptors);
/*      */   }
/*      */   
/*      */   protected ExceptionInterceptor getExceptionInterceptor() {
/* 4967 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */   protected void setSocketTimeout(int milliseconds) throws SQLException {
/*      */     try {
/* 4972 */       this.mysqlConnection.setSoTimeout(milliseconds);
/*      */     } catch (SocketException e) {
/* 4974 */       SQLException sqlEx = SQLError.createSQLException("Invalid socket timeout value or state", "S1009", getExceptionInterceptor());
/*      */       
/* 4976 */       sqlEx.initCause(e);
/*      */       
/* 4978 */       throw sqlEx;
/*      */     }
/*      */   }
/*      */   
/*      */   protected void releaseResources() {
/* 4983 */     if (this.deflater != null) {
/* 4984 */       this.deflater.end();
/* 4985 */       this.deflater = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   String getEncodingForHandshake()
/*      */   {
/* 4994 */     String enc = this.connection.getEncoding();
/* 4995 */     if (enc == null) {
/* 4996 */       enc = "UTF-8";
/*      */     }
/* 4998 */     return enc;
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
/*      */   private void appendCharsetByteForHandshake(Buffer packet, String enc)
/*      */     throws SQLException
/*      */   {
/* 5018 */     int charsetIndex = 0;
/* 5019 */     if (enc != null) {
/* 5020 */       charsetIndex = CharsetMapping.getCollationIndexForJavaEncoding(enc, this.connection);
/*      */     }
/* 5022 */     if (charsetIndex == 0) {
/* 5023 */       charsetIndex = 33;
/*      */     }
/* 5025 */     if (charsetIndex > 255) {
/* 5026 */       throw SQLError.createSQLException("Invalid character set index for encoding: " + enc, "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 5029 */     packet.writeByte((byte)charsetIndex);
/*      */   }
/*      */   
/*      */   public boolean isEOFDeprecated() {
/* 5033 */     return (this.clientParam & 0x1000000) != 0L;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MysqlIO.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */