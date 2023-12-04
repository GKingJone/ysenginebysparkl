/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.charset.Charset;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ public class Util
/*     */ {
/*  61 */   private static Util enclosingInstance = new Util();
/*     */   
/*     */   private static boolean isJdbc4;
/*     */   
/*     */   private static boolean isJdbc42;
/*     */   
/*  67 */   private static int jvmVersion = -1;
/*     */   
/*  69 */   private static boolean isColdFusion = false;
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
/*     */   private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache;
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
/*     */   private static final String MYSQL_JDBC_PACKAGE_ROOT;
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
/*     */   public static boolean isJdbc4()
/*     */   {
/* 118 */     return isJdbc4;
/*     */   }
/*     */   
/*     */   public static boolean isJdbc42() {
/* 122 */     return isJdbc42;
/*     */   }
/*     */   
/*     */   public static int getJVMVersion() {
/* 126 */     return jvmVersion;
/*     */   }
/*     */   
/*     */   public static boolean isColdFusion() {
/* 130 */     return isColdFusion;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isCommunityEdition(String serverVersion)
/*     */   {
/* 137 */     return !isEnterpriseEdition(serverVersion);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isEnterpriseEdition(String serverVersion)
/*     */   {
/* 144 */     return (serverVersion.contains("enterprise")) || (serverVersion.contains("commercial")) || (serverVersion.contains("advanced"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String newCrypt(String password, String seed, String encoding)
/*     */   {
/* 152 */     if ((password == null) || (password.length() == 0)) {
/* 153 */       return password;
/*     */     }
/*     */     
/* 156 */     long[] pw = newHash(seed.getBytes());
/* 157 */     long[] msg = hashPre41Password(password, encoding);
/* 158 */     long max = 1073741823L;
/* 159 */     long seed1 = (pw[0] ^ msg[0]) % max;
/* 160 */     long seed2 = (pw[1] ^ msg[1]) % max;
/* 161 */     char[] chars = new char[seed.length()];
/*     */     
/* 163 */     for (int i = 0; i < seed.length(); i++) {
/* 164 */       seed1 = (seed1 * 3L + seed2) % max;
/* 165 */       seed2 = (seed1 + seed2 + 33L) % max;
/* 166 */       double d = seed1 / max;
/* 167 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 168 */       chars[i] = ((char)b);
/*     */     }
/*     */     
/* 171 */     seed1 = (seed1 * 3L + seed2) % max;
/* 172 */     seed2 = (seed1 + seed2 + 33L) % max;
/* 173 */     double d = seed1 / max;
/* 174 */     byte b = (byte)(int)Math.floor(d * 31.0D);
/*     */     
/* 176 */     for (int i = 0; i < seed.length(); tmp213_211++) {
/* 177 */       int tmp213_211 = i; char[] tmp213_209 = chars;tmp213_209[tmp213_211] = ((char)(tmp213_209[tmp213_211] ^ (char)b));
/*     */     }
/*     */     
/* 180 */     return new String(chars);
/*     */   }
/*     */   
/*     */   public static long[] hashPre41Password(String password, String encoding)
/*     */   {
/*     */     try {
/* 186 */       return newHash(password.replaceAll("\\s", "").getBytes(encoding));
/*     */     } catch (UnsupportedEncodingException e) {}
/* 188 */     return new long[0];
/*     */   }
/*     */   
/*     */   public static long[] hashPre41Password(String password)
/*     */   {
/* 193 */     return hashPre41Password(password, Charset.defaultCharset().name());
/*     */   }
/*     */   
/*     */   static long[] newHash(byte[] password) {
/* 197 */     long nr = 1345345333L;
/* 198 */     long add = 7L;
/* 199 */     long nr2 = 305419889L;
/*     */     
/*     */ 
/* 202 */     for (byte b : password) {
/* 203 */       long tmp = 0xFF & b;
/* 204 */       nr ^= ((nr & 0x3F) + add) * tmp + (nr << 8);
/* 205 */       nr2 += (nr2 << 8 ^ nr);
/* 206 */       add += tmp;
/*     */     }
/*     */     
/* 209 */     long[] result = new long[2];
/* 210 */     result[0] = (nr & 0x7FFFFFFF);
/* 211 */     result[1] = (nr2 & 0x7FFFFFFF);
/*     */     
/* 213 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String oldCrypt(String password, String seed)
/*     */   {
/* 221 */     long max = 33554431L;
/*     */     
/*     */ 
/*     */ 
/* 225 */     if ((password == null) || (password.length() == 0)) {
/* 226 */       return password;
/*     */     }
/*     */     
/* 229 */     long hp = oldHash(seed);
/* 230 */     long hm = oldHash(password);
/*     */     
/* 232 */     long nr = hp ^ hm;
/* 233 */     nr %= max;
/* 234 */     long s1 = nr;
/* 235 */     long s2 = nr / 2L;
/*     */     
/* 237 */     char[] chars = new char[seed.length()];
/*     */     
/* 239 */     for (int i = 0; i < seed.length(); i++) {
/* 240 */       s1 = (s1 * 3L + s2) % max;
/* 241 */       s2 = (s1 + s2 + 33L) % max;
/* 242 */       double d = s1 / max;
/* 243 */       byte b = (byte)(int)Math.floor(d * 31.0D + 64.0D);
/* 244 */       chars[i] = ((char)b);
/*     */     }
/*     */     
/* 247 */     return new String(chars);
/*     */   }
/*     */   
/*     */   static long oldHash(String password) {
/* 251 */     long nr = 1345345333L;
/* 252 */     long nr2 = 7L;
/*     */     
/*     */ 
/* 255 */     for (int i = 0; i < password.length(); i++) {
/* 256 */       if ((password.charAt(i) != ' ') && (password.charAt(i) != '\t'))
/*     */       {
/*     */ 
/*     */ 
/* 260 */         long tmp = password.charAt(i);
/* 261 */         nr ^= ((nr & 0x3F) + nr2) * tmp + (nr << 8);
/* 262 */         nr2 += tmp;
/*     */       }
/*     */     }
/* 265 */     return nr & 0x7FFFFFFF;
/*     */   }
/*     */   
/*     */   private static RandStructcture randomInit(long seed1, long seed2) {
/* 269 */     Util tmp7_4 = enclosingInstance;tmp7_4.getClass();RandStructcture randStruct = new RandStructcture(tmp7_4);
/*     */     
/* 271 */     randStruct.maxValue = 1073741823L;
/* 272 */     randStruct.maxValueDbl = randStruct.maxValue;
/* 273 */     randStruct.seed1 = (seed1 % randStruct.maxValue);
/* 274 */     randStruct.seed2 = (seed2 % randStruct.maxValue);
/*     */     
/* 276 */     return randStruct;
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
/*     */   public static Object readObject(ResultSet resultSet, int index)
/*     */     throws Exception
/*     */   {
/* 293 */     ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
/* 294 */     Object obj = objIn.readObject();
/* 295 */     objIn.close();
/*     */     
/* 297 */     return obj;
/*     */   }
/*     */   
/*     */   private static double rnd(RandStructcture randStruct) {
/* 301 */     randStruct.seed1 = ((randStruct.seed1 * 3L + randStruct.seed2) % randStruct.maxValue);
/* 302 */     randStruct.seed2 = ((randStruct.seed1 + randStruct.seed2 + 33L) % randStruct.maxValue);
/*     */     
/* 304 */     return randStruct.seed1 / randStruct.maxValueDbl;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String scramble(String message, String password)
/*     */   {
/* 314 */     byte[] to = new byte[8];
/* 315 */     String val = "";
/*     */     
/* 317 */     message = message.substring(0, 8);
/*     */     
/* 319 */     if ((password != null) && (password.length() > 0)) {
/* 320 */       long[] hashPass = hashPre41Password(password);
/* 321 */       long[] hashMessage = newHash(message.getBytes());
/*     */       
/* 323 */       RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);
/*     */       
/* 325 */       int msgPos = 0;
/* 326 */       int msgLength = message.length();
/* 327 */       int toPos = 0;
/*     */       
/* 329 */       while (msgPos++ < msgLength) {
/* 330 */         to[(toPos++)] = ((byte)(int)(Math.floor(rnd(randStruct) * 31.0D) + 64.0D));
/*     */       }
/*     */       
/*     */ 
/* 334 */       byte extra = (byte)(int)Math.floor(rnd(randStruct) * 31.0D);
/*     */       
/* 336 */       for (int i = 0; i < to.length; i++) {
/* 337 */         int tmp143_141 = i; byte[] tmp143_139 = to;tmp143_139[tmp143_141] = ((byte)(tmp143_139[tmp143_141] ^ extra));
/*     */       }
/*     */       
/* 340 */       val = StringUtils.toString(to);
/*     */     }
/*     */     
/* 343 */     return val;
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
/*     */   public static String stackTraceToString(Throwable ex)
/*     */   {
/* 356 */     StringBuilder traceBuf = new StringBuilder();
/* 357 */     traceBuf.append(Messages.getString("Util.1"));
/*     */     
/* 359 */     if (ex != null) {
/* 360 */       traceBuf.append(ex.getClass().getName());
/*     */       
/* 362 */       String message = ex.getMessage();
/*     */       
/* 364 */       if (message != null) {
/* 365 */         traceBuf.append(Messages.getString("Util.2"));
/* 366 */         traceBuf.append(message);
/*     */       }
/*     */       
/* 369 */       StringWriter out = new StringWriter();
/*     */       
/* 371 */       PrintWriter printOut = new PrintWriter(out);
/*     */       
/* 373 */       ex.printStackTrace(printOut);
/*     */       
/* 375 */       traceBuf.append(Messages.getString("Util.3"));
/* 376 */       traceBuf.append(out.toString());
/*     */     }
/*     */     
/* 379 */     traceBuf.append(Messages.getString("Util.4"));
/*     */     
/* 381 */     return traceBuf.toString();
/*     */   }
/*     */   
/*     */   public static Object getInstance(String className, Class<?>[] argTypes, Object[] args, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*     */   {
/*     */     try {
/* 387 */       return handleNewInstance(Class.forName(className).getConstructor(argTypes), args, exceptionInterceptor);
/*     */     } catch (SecurityException e) {
/* 389 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     } catch (NoSuchMethodException e) {
/* 391 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     } catch (ClassNotFoundException e) {
/* 393 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static final Object handleNewInstance(Constructor<?> ctor, Object[] args, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 404 */       return ctor.newInstance(args);
/*     */     } catch (IllegalArgumentException e) {
/* 406 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     } catch (InstantiationException e) {
/* 408 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     } catch (IllegalAccessException e) {
/* 410 */       throw SQLError.createSQLException("Can't instantiate required class", "S1000", e, exceptionInterceptor);
/*     */     } catch (InvocationTargetException e) {
/* 412 */       Throwable target = e.getTargetException();
/*     */       
/* 414 */       if ((target instanceof SQLException)) {
/* 415 */         throw ((SQLException)target);
/*     */       }
/*     */       
/* 418 */       if ((target instanceof ExceptionInInitializerError)) {
/* 419 */         target = ((ExceptionInInitializerError)target).getException();
/*     */       }
/*     */       
/* 422 */       throw SQLError.createSQLException(target.toString(), "S1000", target, exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean interfaceExists(String hostname)
/*     */   {
/*     */     try
/*     */     {
/* 436 */       Class<?> networkInterfaceClass = Class.forName("java.net.NetworkInterface");
/* 437 */       return networkInterfaceClass.getMethod("getByName", (Class[])null).invoke(networkInterfaceClass, new Object[] { hostname }) != null;
/*     */     } catch (Throwable t) {}
/* 439 */     return false;
/*     */   }
/*     */   
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs)
/*     */     throws SQLException
/*     */   {
/* 445 */     while (rs.next()) {
/* 446 */       mappedValues.put(rs.getObject(1), rs.getObject(2));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs, int key, int value) throws SQLException
/*     */   {
/* 452 */     while (rs.next()) {
/* 453 */       mappedValues.put(rs.getObject(key), rs.getObject(value));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void resultSetToMap(Map mappedValues, ResultSet rs, String key, String value) throws SQLException
/*     */   {
/* 459 */     while (rs.next()) {
/* 460 */       mappedValues.put(rs.getObject(key), rs.getObject(value));
/*     */     }
/*     */   }
/*     */   
/*     */   public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
/* 465 */     Map<Object, Object> diffMap = new HashMap();
/*     */     
/* 467 */     for (Entry<?, ?> entry : map1.entrySet()) {
/* 468 */       Object key = entry.getKey();
/*     */       
/* 470 */       Number value1 = null;
/* 471 */       Number value2 = null;
/*     */       
/* 473 */       if ((entry.getValue() instanceof Number))
/*     */       {
/* 475 */         value1 = (Number)entry.getValue();
/* 476 */         value2 = (Number)map2.get(key);
/*     */       } else {
/*     */         try {
/* 479 */           value1 = new Double(entry.getValue().toString());
/* 480 */           value2 = new Double(map2.get(key).toString());
/*     */         } catch (NumberFormatException nfe) {}
/* 482 */         continue;
/*     */       }
/*     */       
/*     */ 
/* 486 */       if (!value1.equals(value2))
/*     */       {
/*     */ 
/*     */ 
/* 490 */         if ((value1 instanceof Byte)) {
/* 491 */           diffMap.put(key, Byte.valueOf((byte)(((Byte)value2).byteValue() - ((Byte)value1).byteValue())));
/* 492 */         } else if ((value1 instanceof Short)) {
/* 493 */           diffMap.put(key, Short.valueOf((short)(((Short)value2).shortValue() - ((Short)value1).shortValue())));
/* 494 */         } else if ((value1 instanceof Integer)) {
/* 495 */           diffMap.put(key, Integer.valueOf(((Integer)value2).intValue() - ((Integer)value1).intValue()));
/* 496 */         } else if ((value1 instanceof Long)) {
/* 497 */           diffMap.put(key, Long.valueOf(((Long)value2).longValue() - ((Long)value1).longValue()));
/* 498 */         } else if ((value1 instanceof Float)) {
/* 499 */           diffMap.put(key, Float.valueOf(((Float)value2).floatValue() - ((Float)value1).floatValue()));
/* 500 */         } else if ((value1 instanceof Double)) {
/* 501 */           diffMap.put(key, Double.valueOf(((Double)value2).shortValue() - ((Double)value1).shortValue()));
/* 502 */         } else if ((value1 instanceof BigDecimal)) {
/* 503 */           diffMap.put(key, ((BigDecimal)value2).subtract((BigDecimal)value1));
/* 504 */         } else if ((value1 instanceof BigInteger)) {
/* 505 */           diffMap.put(key, ((BigInteger)value2).subtract((BigInteger)value1));
/*     */         }
/*     */       }
/*     */     }
/* 509 */     return diffMap;
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
/*     */   public static List<Extension> loadExtensions(Connection conn, Properties props, String extensionClassNames, String errorMessageKey, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/* 525 */     List<Extension> extensionList = new LinkedList();
/*     */     
/* 527 */     List<String> interceptorsToCreate = StringUtils.split(extensionClassNames, ",", true);
/*     */     
/* 529 */     String className = null;
/*     */     try
/*     */     {
/* 532 */       int i = 0; for (int s = interceptorsToCreate.size(); i < s; i++) {
/* 533 */         className = (String)interceptorsToCreate.get(i);
/* 534 */         Extension extensionInstance = (Extension)Class.forName(className).newInstance();
/* 535 */         extensionInstance.init(conn, props);
/*     */         
/* 537 */         extensionList.add(extensionInstance);
/*     */       }
/*     */     } catch (Throwable t) {
/* 540 */       SQLException sqlEx = SQLError.createSQLException(Messages.getString(errorMessageKey, new Object[] { className }), exceptionInterceptor);
/* 541 */       sqlEx.initCause(t);
/*     */       
/* 543 */       throw sqlEx;
/*     */     }
/*     */     
/* 546 */     return extensionList;
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
/*     */   public static boolean isJdbcInterface(Class<?> clazz)
/*     */   {
/* 559 */     if (isJdbcInterfaceCache.containsKey(clazz)) {
/* 560 */       return ((Boolean)isJdbcInterfaceCache.get(clazz)).booleanValue();
/*     */     }
/*     */     
/* 563 */     if (clazz.isInterface()) {
/*     */       try {
/* 565 */         if (isJdbcPackage(clazz.getPackage().getName())) {
/* 566 */           isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 567 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Exception ex) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 577 */     for (Class<?> iface : clazz.getInterfaces()) {
/* 578 */       if (isJdbcInterface(iface)) {
/* 579 */         isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 580 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 584 */     if ((clazz.getSuperclass() != null) && (isJdbcInterface(clazz.getSuperclass()))) {
/* 585 */       isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(true));
/* 586 */       return true;
/*     */     }
/*     */     
/* 589 */     isJdbcInterfaceCache.putIfAbsent(clazz, Boolean.valueOf(false));
/* 590 */     return false;
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  73 */       Class.forName("java.sql.NClob");
/*  74 */       isJdbc4 = true;
/*     */     } catch (ClassNotFoundException e) {
/*  76 */       isJdbc4 = false;
/*     */     }
/*     */     try
/*     */     {
/*  80 */       Class.forName("java.sql.JDBCType");
/*  81 */       isJdbc42 = true;
/*     */     } catch (Throwable t) {
/*  83 */       isJdbc42 = false;
/*     */     }
/*     */     
/*  86 */     String jvmVersionString = System.getProperty("java.version");
/*  87 */     int startPos = jvmVersionString.indexOf('.');
/*  88 */     int endPos = startPos + 1;
/*  89 */     if (startPos != -1) {
/*  90 */       do { if (!Character.isDigit(jvmVersionString.charAt(endPos))) break; endPos++; } while (endPos < jvmVersionString.length());
/*     */     }
/*     */     
/*     */ 
/*  94 */     startPos++;
/*  95 */     if (endPos > startPos) {
/*  96 */       jvmVersion = Integer.parseInt(jvmVersionString.substring(startPos, endPos));
/*     */     }
/*     */     else {
/*  99 */       jvmVersion = isJdbc4 ? 6 : isJdbc42 ? 8 : 5;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 108 */     String loadedFrom = stackTraceToString(new Throwable());
/*     */     
/* 110 */     if (loadedFrom != null) {
/* 111 */       isColdFusion = loadedFrom.indexOf("coldfusion") != -1;
/*     */     } else {
/* 113 */       isColdFusion = false;
/*     */     }
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
/* 550 */     isJdbcInterfaceCache = new ConcurrentHashMap();
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
/* 597 */     String packageName = MultiHostConnectionProxy.class.getPackage().getName();
/*     */     
/* 599 */     MYSQL_JDBC_PACKAGE_ROOT = packageName.substring(0, packageName.indexOf("jdbc") + 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isJdbcPackage(String packageName)
/*     */   {
/* 609 */     return (packageName != null) && ((packageName.startsWith("java.sql")) || (packageName.startsWith("javax.sql")) || (packageName.startsWith(MYSQL_JDBC_PACKAGE_ROOT)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 614 */   private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Class<?>[] getImplementedInterfaces(Class<?> clazz)
/*     */   {
/* 626 */     Class<?>[] implementedInterfaces = (Class[])implementedInterfacesCache.get(clazz);
/* 627 */     if (implementedInterfaces != null) {
/* 628 */       return implementedInterfaces;
/*     */     }
/*     */     
/* 631 */     Set<Class<?>> interfaces = new LinkedHashSet();
/* 632 */     Class<?> superClass = clazz;
/*     */     do {
/* 634 */       Collections.addAll(interfaces, (Class[])superClass.getInterfaces());
/* 635 */     } while ((superClass = superClass.getSuperclass()) != null);
/*     */     
/* 637 */     implementedInterfaces = (Class[])interfaces.toArray(new Class[interfaces.size()]);
/* 638 */     Class<?>[] oldValue = (Class[])implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
/* 639 */     if (oldValue != null) {
/* 640 */       implementedInterfaces = oldValue;
/*     */     }
/* 642 */     return implementedInterfaces;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long secondsSinceMillis(long timeInMillis)
/*     */   {
/* 654 */     return (System.currentTimeMillis() - timeInMillis) / 1000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int truncateAndConvertToInt(long longValue)
/*     */   {
/* 664 */     return longValue < -2147483648L ? Integer.MIN_VALUE : longValue > 2147483647L ? Integer.MAX_VALUE : (int)longValue;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int[] truncateAndConvertToInt(long[] longArray)
/*     */   {
/* 674 */     int[] intArray = new int[longArray.length];
/*     */     
/* 676 */     for (int i = 0; i < longArray.length; i++) {
/* 677 */       intArray[i] = (longArray[i] < -2147483648L ? Integer.MIN_VALUE : longArray[i] > 2147483647L ? Integer.MAX_VALUE : (int)longArray[i]);
/*     */     }
/* 679 */     return intArray;
/*     */   }
/*     */   
/*     */   class RandStructcture
/*     */   {
/*     */     long maxValue;
/*     */     double maxValueDbl;
/*     */     long seed1;
/*     */     long seed2;
/*     */     
/*     */     RandStructcture() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Util.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */