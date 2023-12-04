/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   private static final String ALLOWED_QUOTES = "\"'";
/*     */   private static final String REPLICATION_URL_PREFIX = "jdbc:mysql:replication://";
/*     */   private static final String URL_PREFIX = "jdbc:mysql://";
/*     */   private static final String MXJ_URL_PREFIX = "jdbc:mysql:mxj://";
/*     */   public static final String LOADBALANCE_URL_PREFIX = "jdbc:mysql:loadbalance://";
/*  71 */   protected static final ConcurrentHashMap<ConnectionPhantomReference, ConnectionPhantomReference> connectionPhantomRefs = new ConcurrentHashMap();
/*     */   
/*  73 */   protected static final ReferenceQueue<ConnectionImpl> refQueue = new ReferenceQueue();
/*     */   
/*  75 */   public static final String OS = getOSName();
/*  76 */   public static final String PLATFORM = getPlatform();
/*     */   public static final String LICENSE = "GPL";
/*  78 */   public static final String RUNTIME_VENDOR = System.getProperty("java.vendor");
/*  79 */   public static final String RUNTIME_VERSION = System.getProperty("java.version");
/*     */   public static final String VERSION = "5.1.39";
/*     */   public static final String NAME = "MySQL Connector Java";
/*     */   public static final String DBNAME_PROPERTY_KEY = "DBNAME";
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int HOST_NAME_INDEX = 0;
/*     */   public static final String HOST_PROPERTY_KEY = "HOST";
/*     */   public static final String NUM_HOSTS_PROPERTY_KEY = "NUM_HOSTS";
/*     */   public static final String PASSWORD_PROPERTY_KEY = "password";
/*     */   
/*     */   public static String getOSName() {
/*  90 */     return System.getProperty("os.name");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getPlatform()
/*     */   {
/* 100 */     return System.getProperty("os.arch");
/*     */   }
/*     */   
/*     */   static {
/* 104 */     AbandonedConnectionCleanupThread referenceThread = new AbandonedConnectionCleanupThread();
/* 105 */     referenceThread.setDaemon(true);
/* 106 */     referenceThread.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final int PORT_NUMBER_INDEX = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String PORT_PROPERTY_KEY = "PORT";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean TRACE = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String USER_PROPERTY_KEY = "user";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String PROTOCOL_PROPERTY_KEY = "PROTOCOL";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String PATH_PROPERTY_KEY = "PATH";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int getMajorVersionInternal()
/*     */   {
/* 167 */     return safeIntParse("5");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static int getMinorVersionInternal()
/*     */   {
/* 176 */     return safeIntParse("1");
/*     */   }
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
/*     */   protected static String[] parseHostPortPair(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 195 */     String[] splitValues = new String[2];
/*     */     
/* 197 */     if (StringUtils.startsWithIgnoreCaseAndWs(hostPortPair, "address")) {
/* 198 */       splitValues[0] = hostPortPair.trim();
/* 199 */       splitValues[1] = null;
/*     */       
/* 201 */       return splitValues;
/*     */     }
/*     */     
/* 204 */     int portIndex = hostPortPair.indexOf(":");
/*     */     
/* 206 */     String hostname = null;
/*     */     
/* 208 */     if (portIndex != -1) {
/* 209 */       if (portIndex + 1 < hostPortPair.length()) {
/* 210 */         String portAsString = hostPortPair.substring(portIndex + 1);
/* 211 */         hostname = hostPortPair.substring(0, portIndex);
/*     */         
/* 213 */         splitValues[0] = hostname;
/*     */         
/* 215 */         splitValues[1] = portAsString;
/*     */       } else {
/* 217 */         throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.37"), "01S00", null);
/*     */       }
/*     */     } else {
/* 220 */       splitValues[0] = hostPortPair;
/* 221 */       splitValues[1] = null;
/*     */     }
/*     */     
/* 224 */     return splitValues;
/*     */   }
/*     */   
/*     */   private static int safeIntParse(String intAsString) {
/*     */     try {
/* 229 */       return Integer.parseInt(intAsString);
/*     */     } catch (NumberFormatException nfe) {}
/* 231 */     return 0;
/*     */   }
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
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/* 261 */     if (url == null) {
/* 262 */       throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.1"), "08001", null);
/*     */     }
/* 264 */     return parseURL(url, null) != null;
/*     */   }
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
/*     */   public java.sql.Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 307 */     if (url == null) {
/* 308 */       throw SQLError.createSQLException(Messages.getString("NonRegisteringDriver.1"), "08001", null);
/*     */     }
/*     */     
/* 311 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://"))
/* 312 */       return connectLoadBalanced(url, info);
/* 313 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://")) {
/* 314 */       return connectReplicationConnection(url, info);
/*     */     }
/*     */     
/* 317 */     Properties props = null;
/*     */     
/* 319 */     if ((props = parseURL(url, info)) == null) {
/* 320 */       return null;
/*     */     }
/*     */     
/* 323 */     if (!"1".equals(props.getProperty("NUM_HOSTS"))) {
/* 324 */       return connectFailover(url, info);
/*     */     }
/*     */     try
/*     */     {
/* 328 */       return ConnectionImpl.getInstance(host(props), port(props), props, database(props), url);
/*     */ 
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*     */ 
/* 334 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 336 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001", null);
/*     */       
/*     */ 
/*     */ 
/* 340 */       sqlEx.initCause(ex);
/*     */       
/* 342 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static void trackConnection(Connection newConn)
/*     */   {
/* 348 */     ConnectionPhantomReference phantomRef = new ConnectionPhantomReference((ConnectionImpl)newConn, refQueue);
/* 349 */     connectionPhantomRefs.put(phantomRef, phantomRef);
/*     */   }
/*     */   
/*     */   private java.sql.Connection connectLoadBalanced(String url, Properties info) throws SQLException {
/* 353 */     Properties parsedProps = parseURL(url, info);
/*     */     
/* 355 */     if (parsedProps == null) {
/* 356 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 360 */     parsedProps.remove("roundRobinLoadBalance");
/*     */     
/* 362 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */     
/* 364 */     List<String> hostList = new ArrayList();
/*     */     
/* 366 */     for (int i = 0; i < numHosts; i++) {
/* 367 */       int index = i + 1;
/*     */       
/* 369 */       hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
/*     */     }
/*     */     
/* 372 */     return LoadBalancedConnectionProxy.createProxyInstance(hostList, parsedProps);
/*     */   }
/*     */   
/*     */   private java.sql.Connection connectFailover(String url, Properties info) throws SQLException {
/* 376 */     Properties parsedProps = parseURL(url, info);
/*     */     
/* 378 */     if (parsedProps == null) {
/* 379 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 383 */     parsedProps.remove("roundRobinLoadBalance");
/*     */     
/* 385 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */     
/* 387 */     List<String> hostList = new ArrayList();
/*     */     
/* 389 */     for (int i = 0; i < numHosts; i++) {
/* 390 */       int index = i + 1;
/*     */       
/* 392 */       hostList.add(parsedProps.getProperty(new StringBuilder().append("HOST.").append(index).toString()) + ":" + parsedProps.getProperty(new StringBuilder().append("PORT.").append(index).toString()));
/*     */     }
/*     */     
/* 395 */     return FailoverConnectionProxy.createProxyInstance(hostList, parsedProps);
/*     */   }
/*     */   
/*     */   protected java.sql.Connection connectReplicationConnection(String url, Properties info) throws SQLException {
/* 399 */     Properties parsedProps = parseURL(url, info);
/*     */     
/* 401 */     if (parsedProps == null) {
/* 402 */       return null;
/*     */     }
/*     */     
/* 405 */     Properties masterProps = (Properties)parsedProps.clone();
/* 406 */     Properties slavesProps = (Properties)parsedProps.clone();
/*     */     
/*     */ 
/*     */ 
/* 410 */     slavesProps.setProperty("com.mysql.jdbc.ReplicationConnection.isSlave", "true");
/*     */     
/* 412 */     int numHosts = Integer.parseInt(parsedProps.getProperty("NUM_HOSTS"));
/*     */     
/* 414 */     if (numHosts < 2) {
/* 415 */       throw SQLError.createSQLException("Must specify at least one slave host to connect to for master/slave replication load-balancing functionality", "01S00", null);
/*     */     }
/*     */     
/* 418 */     List<String> slaveHostList = new ArrayList();
/* 419 */     List<String> masterHostList = new ArrayList();
/*     */     
/* 421 */     String firstHost = masterProps.getProperty("HOST.1") + ":" + masterProps.getProperty("PORT.1");
/*     */     
/* 423 */     boolean usesExplicitServerType = isHostPropertiesList(firstHost);
/*     */     
/* 425 */     for (int i = 0; i < numHosts; i++) {
/* 426 */       int index = i + 1;
/*     */       
/* 428 */       masterProps.remove("HOST." + index);
/* 429 */       masterProps.remove("PORT." + index);
/* 430 */       slavesProps.remove("HOST." + index);
/* 431 */       slavesProps.remove("PORT." + index);
/*     */       
/* 433 */       String host = parsedProps.getProperty("HOST." + index);
/* 434 */       String port = parsedProps.getProperty("PORT." + index);
/* 435 */       if (usesExplicitServerType) {
/* 436 */         if (isHostMaster(host)) {
/* 437 */           masterHostList.add(host);
/*     */         } else {
/* 439 */           slaveHostList.add(host);
/*     */         }
/*     */       }
/* 442 */       else if (i == 0) {
/* 443 */         masterHostList.add(host + ":" + port);
/*     */       } else {
/* 445 */         slaveHostList.add(host + ":" + port);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 450 */     slavesProps.remove("NUM_HOSTS");
/* 451 */     masterProps.remove("NUM_HOSTS");
/* 452 */     masterProps.remove("HOST");
/* 453 */     masterProps.remove("PORT");
/* 454 */     slavesProps.remove("HOST");
/* 455 */     slavesProps.remove("PORT");
/*     */     
/* 457 */     return ReplicationConnectionProxy.createProxyInstance(masterHostList, masterProps, slaveHostList, slavesProps);
/*     */   }
/*     */   
/*     */   private boolean isHostMaster(String host) {
/* 461 */     if (isHostPropertiesList(host)) {
/* 462 */       Properties hostSpecificProps = expandHostKeyValues(host);
/* 463 */       if ((hostSpecificProps.containsKey("type")) && ("master".equalsIgnoreCase(hostSpecificProps.get("type").toString()))) {
/* 464 */         return true;
/*     */       }
/*     */     }
/* 467 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String database(Properties props)
/*     */   {
/* 479 */     return props.getProperty("DBNAME");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 488 */     return getMajorVersionInternal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 497 */     return getMinorVersionInternal();
/*     */   }
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
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 526 */     if (info == null) {
/* 527 */       info = new Properties();
/*     */     }
/*     */     
/* 530 */     if ((url != null) && (url.startsWith("jdbc:mysql://"))) {
/* 531 */       info = parseURL(url, info);
/*     */     }
/*     */     
/* 534 */     DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
/* 535 */     hostProp.required = true;
/* 536 */     hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */     
/* 538 */     DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
/* 539 */     portProp.required = false;
/* 540 */     portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */     
/* 542 */     DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
/* 543 */     dbProp.required = false;
/* 544 */     dbProp.description = "Database name";
/*     */     
/* 546 */     DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
/* 547 */     userProp.required = true;
/* 548 */     userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */     
/* 550 */     DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
/* 551 */     passwordProp.required = true;
/* 552 */     passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */     
/* 554 */     DriverPropertyInfo[] dpi = ConnectionPropertiesImpl.exposeAsDriverPropertyInfo(info, 5);
/*     */     
/* 556 */     dpi[0] = hostProp;
/* 557 */     dpi[1] = portProp;
/* 558 */     dpi[2] = dbProp;
/* 559 */     dpi[3] = userProp;
/* 560 */     dpi[4] = passwordProp;
/*     */     
/* 562 */     return dpi;
/*     */   }
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
/*     */   public String host(Properties props)
/*     */   {
/* 579 */     return props.getProperty("HOST", "localhost");
/*     */   }
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
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 595 */     return false;
/*     */   }
/*     */   
/*     */   public Properties parseURL(String url, Properties defaults) throws SQLException
/*     */   {
/* 600 */     Properties urlProps = defaults != null ? new Properties(defaults) : new Properties();
/*     */     
/* 602 */     if (url == null) {
/* 603 */       return null;
/*     */     }
/*     */     
/* 606 */     if ((!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:loadbalance://")) && (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:replication://")))
/*     */     {
/*     */ 
/* 609 */       return null;
/*     */     }
/*     */     
/* 612 */     int beginningOfSlashes = url.indexOf("//");
/*     */     
/* 614 */     if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:mxj://"))
/*     */     {
/* 616 */       urlProps.setProperty("socketFactory", "com.mysql.management.driverlaunched.ServerLauncherSocketFactory");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 623 */     int index = url.indexOf("?");
/*     */     
/* 625 */     if (index != -1) {
/* 626 */       String paramString = url.substring(index + 1, url.length());
/* 627 */       url = url.substring(0, index);
/*     */       
/* 629 */       StringTokenizer queryParams = new StringTokenizer(paramString, "&");
/*     */       
/* 631 */       while (queryParams.hasMoreTokens()) {
/* 632 */         String parameterValuePair = queryParams.nextToken();
/*     */         
/* 634 */         int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
/*     */         
/* 636 */         String parameter = null;
/* 637 */         String value = null;
/*     */         
/* 639 */         if (indexOfEquals != -1) {
/* 640 */           parameter = parameterValuePair.substring(0, indexOfEquals);
/*     */           
/* 642 */           if (indexOfEquals + 1 < parameterValuePair.length()) {
/* 643 */             value = parameterValuePair.substring(indexOfEquals + 1);
/*     */           }
/*     */         }
/*     */         
/* 647 */         if ((value != null) && (value.length() > 0) && (parameter != null) && (parameter.length() > 0)) {
/*     */           try {
/* 649 */             urlProps.setProperty(parameter, URLDecoder.decode(value, "UTF-8"));
/*     */           }
/*     */           catch (UnsupportedEncodingException badEncoding) {
/* 652 */             urlProps.setProperty(parameter, URLDecoder.decode(value));
/*     */           }
/*     */           catch (NoSuchMethodError nsme) {
/* 655 */             urlProps.setProperty(parameter, URLDecoder.decode(value));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 661 */     url = url.substring(beginningOfSlashes + 2);
/*     */     
/* 663 */     String hostStuff = null;
/*     */     
/* 665 */     int slashIndex = StringUtils.indexOfIgnoreCase(0, url, "/", "\"'", "\"'", StringUtils.SEARCH_MODE__ALL);
/*     */     
/* 667 */     if (slashIndex != -1) {
/* 668 */       hostStuff = url.substring(0, slashIndex);
/*     */       
/* 670 */       if (slashIndex + 1 < url.length()) {
/* 671 */         urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
/*     */       }
/*     */     } else {
/* 674 */       hostStuff = url;
/*     */     }
/*     */     
/* 677 */     int numHosts = 0;
/*     */     
/* 679 */     if ((hostStuff != null) && (hostStuff.trim().length() > 0)) {
/* 680 */       List<String> hosts = StringUtils.split(hostStuff, ",", "\"'", "\"'", false);
/*     */       
/* 682 */       for (String hostAndPort : hosts) {
/* 683 */         numHosts++;
/*     */         
/* 685 */         String[] hostPortPair = parseHostPortPair(hostAndPort);
/*     */         
/* 687 */         if ((hostPortPair[0] != null) && (hostPortPair[0].trim().length() > 0)) {
/* 688 */           urlProps.setProperty("HOST." + numHosts, hostPortPair[0]);
/*     */         } else {
/* 690 */           urlProps.setProperty("HOST." + numHosts, "localhost");
/*     */         }
/*     */         
/* 693 */         if (hostPortPair[1] != null) {
/* 694 */           urlProps.setProperty("PORT." + numHosts, hostPortPair[1]);
/*     */         } else {
/* 696 */           urlProps.setProperty("PORT." + numHosts, "3306");
/*     */         }
/*     */       }
/*     */     } else {
/* 700 */       numHosts = 1;
/* 701 */       urlProps.setProperty("HOST.1", "localhost");
/* 702 */       urlProps.setProperty("PORT.1", "3306");
/*     */     }
/*     */     
/* 705 */     urlProps.setProperty("NUM_HOSTS", String.valueOf(numHosts));
/* 706 */     urlProps.setProperty("HOST", urlProps.getProperty("HOST.1"));
/* 707 */     urlProps.setProperty("PORT", urlProps.getProperty("PORT.1"));
/*     */     
/* 709 */     String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
/*     */     
/* 711 */     if (propertiesTransformClassName != null) {
/*     */       try {
/* 713 */         ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/*     */         
/* 715 */         urlProps = propTransformer.transformProperties(urlProps);
/*     */       } catch (InstantiationException e) {
/* 717 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/* 721 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/* 725 */         throw SQLError.createSQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00", null);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 731 */     if ((Util.isColdFusion()) && (urlProps.getProperty("autoConfigureForColdFusion", "true").equalsIgnoreCase("true"))) {
/* 732 */       String configs = urlProps.getProperty("useConfigs");
/*     */       
/* 734 */       StringBuilder newConfigs = new StringBuilder();
/*     */       
/* 736 */       if (configs != null) {
/* 737 */         newConfigs.append(configs);
/* 738 */         newConfigs.append(",");
/*     */       }
/*     */       
/* 741 */       newConfigs.append("coldFusion");
/*     */       
/* 743 */       urlProps.setProperty("useConfigs", newConfigs.toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 748 */     String configNames = null;
/*     */     
/* 750 */     if (defaults != null) {
/* 751 */       configNames = defaults.getProperty("useConfigs");
/*     */     }
/*     */     
/* 754 */     if (configNames == null) {
/* 755 */       configNames = urlProps.getProperty("useConfigs");
/*     */     }
/*     */     
/* 758 */     if (configNames != null) {
/* 759 */       List<String> splitNames = StringUtils.split(configNames, ",", true);
/*     */       
/* 761 */       Properties configProps = new Properties();
/*     */       
/* 763 */       Iterator<String> namesIter = splitNames.iterator();
/*     */       
/* 765 */       while (namesIter.hasNext()) {
/* 766 */         String configName = (String)namesIter.next();
/*     */         try
/*     */         {
/* 769 */           InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
/*     */           
/* 771 */           if (configAsStream == null) {
/* 772 */             throw SQLError.createSQLException("Can't find configuration template named '" + configName + "'", "01S00", null);
/*     */           }
/*     */           
/* 775 */           configProps.load(configAsStream);
/*     */         } catch (IOException ioEx) {
/* 777 */           SQLException sqlEx = SQLError.createSQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00", null);
/*     */           
/*     */ 
/* 780 */           sqlEx.initCause(ioEx);
/*     */           
/* 782 */           throw sqlEx;
/*     */         }
/*     */       }
/*     */       
/* 786 */       Iterator<Object> propsIter = urlProps.keySet().iterator();
/*     */       
/* 788 */       while (propsIter.hasNext()) {
/* 789 */         String key = propsIter.next().toString();
/* 790 */         String property = urlProps.getProperty(key);
/* 791 */         configProps.setProperty(key, property);
/*     */       }
/*     */       
/* 794 */       urlProps = configProps;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 799 */     if (defaults != null) {
/* 800 */       Iterator<Object> propsIter = defaults.keySet().iterator();
/*     */       
/* 802 */       while (propsIter.hasNext()) {
/* 803 */         String key = propsIter.next().toString();
/* 804 */         if (!key.equals("NUM_HOSTS")) {
/* 805 */           String property = defaults.getProperty(key);
/* 806 */           urlProps.setProperty(key, property);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 811 */     return urlProps;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int port(Properties props)
/*     */   {
/* 823 */     return Integer.parseInt(props.getProperty("PORT", "3306"));
/*     */   }
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
/*     */   public String property(String name, Properties props)
/*     */   {
/* 837 */     return props.getProperty(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Properties expandHostKeyValues(String host)
/*     */   {
/* 846 */     Properties hostProps = new Properties();
/*     */     
/* 848 */     if (isHostPropertiesList(host)) {
/* 849 */       host = host.substring("address=".length() + 1);
/* 850 */       List<String> hostPropsList = StringUtils.split(host, ")", "'\"", "'\"", true);
/*     */       
/* 852 */       for (String propDef : hostPropsList) {
/* 853 */         if (propDef.startsWith("(")) {
/* 854 */           propDef = propDef.substring(1);
/*     */         }
/*     */         
/* 857 */         List<String> kvp = StringUtils.split(propDef, "=", "'\"", "'\"", true);
/*     */         
/* 859 */         String key = (String)kvp.get(0);
/* 860 */         String value = kvp.size() > 1 ? (String)kvp.get(1) : null;
/*     */         
/* 862 */         if ((value != null) && (((value.startsWith("\"")) && (value.endsWith("\""))) || ((value.startsWith("'")) && (value.endsWith("'"))))) {
/* 863 */           value = value.substring(1, value.length() - 1);
/*     */         }
/*     */         
/* 866 */         if (value != null) {
/* 867 */           if (("HOST".equalsIgnoreCase(key)) || ("DBNAME".equalsIgnoreCase(key)) || ("PORT".equalsIgnoreCase(key)) || ("PROTOCOL".equalsIgnoreCase(key)) || ("PATH".equalsIgnoreCase(key)))
/*     */           {
/* 869 */             key = key.toUpperCase(Locale.ENGLISH);
/* 870 */           } else if (("user".equalsIgnoreCase(key)) || ("password".equalsIgnoreCase(key))) {
/* 871 */             key = key.toLowerCase(Locale.ENGLISH);
/*     */           }
/*     */           
/* 874 */           hostProps.setProperty(key, value);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 879 */     return hostProps;
/*     */   }
/*     */   
/*     */ 
/* 883 */   public static boolean isHostPropertiesList(String host) { return (host != null) && (StringUtils.startsWithIgnoreCase(host, "address=")); }
/*     */   
/*     */   public NonRegisteringDriver() throws SQLException
/*     */   {}
/*     */   
/*     */   static class ConnectionPhantomReference extends PhantomReference<ConnectionImpl> { private NetworkResources io;
/*     */     
/* 890 */     ConnectionPhantomReference(ConnectionImpl connectionImpl, ReferenceQueue<ConnectionImpl> q) { super(q);
/*     */       try
/*     */       {
/* 893 */         this.io = connectionImpl.getIO().getNetworkResources();
/*     */       }
/*     */       catch (SQLException e) {}
/*     */     }
/*     */     
/*     */     void cleanup()
/*     */     {
/* 900 */       if (this.io != null) {
/*     */         try {
/* 902 */           this.io.forceClose();
/*     */         } finally {
/* 904 */           this.io = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NonRegisteringDriver.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */