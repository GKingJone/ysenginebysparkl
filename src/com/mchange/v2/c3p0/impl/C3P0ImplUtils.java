/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.lang.ByteUtils;
/*     */ import com.mchange.v2.c3p0.ConnectionTester;
/*     */ import com.mchange.v2.c3p0.PoolConfig;
/*     */ import com.mchange.v2.cfg.MultiPropertiesConfig;
/*     */ import com.mchange.v2.encounter.EncounterCounter;
/*     */ import com.mchange.v2.encounter.EqualityEncounterCounter;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.ser.SerializableUtils;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.security.SecureRandom;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class C3P0ImplUtils
/*     */ {
/*     */   private static final boolean CONDITIONAL_LONG_TOKENS = false;
/*  61 */   static final MLogger logger = MLog.getLogger(C3P0ImplUtils.class);
/*     */   
/*  63 */   public static final DbAuth NULL_AUTH = new DbAuth(null, null);
/*     */   
/*  65 */   public static final Object[] NOARGS = new Object[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */   private static final EncounterCounter ID_TOKEN_COUNTER = new EqualityEncounterCounter();
/*     */   
/*     */   public static final String VMID_PROPKEY = "com.mchange.v2.c3p0.VMID";
/*     */   
/*     */   private static final String VMID_PFX;
/*     */   
/*     */   static
/*     */   {
/*  96 */     String vmid = MultiPropertiesConfig.readVmConfig().getProperty("com.mchange.v2.c3p0.VMID");
/*  97 */     if ((vmid == null) || ((vmid = vmid.trim()).equals("")) || (vmid.equals("AUTO"))) {
/*  98 */       VMID_PFX = generateVmId() + '|';
/*  99 */     } else if (vmid.equals("NONE")) {
/* 100 */       VMID_PFX = "";
/*     */     } else {
/* 102 */       VMID_PFX = vmid + "|";
/*     */     }
/*     */   }
/*     */   
/* 106 */   static String connectionTesterClassName = null;
/* 107 */   static ConnectionTester cachedTester = null;
/*     */   private static final String HASM_HEADER = "HexAsciiSerializedMap";
/*     */   
/*     */   private static String generateVmId() {
/* 111 */     DataOutputStream dos = null;
/* 112 */     DataInputStream dis = null;
/*     */     try
/*     */     {
/* 115 */       SecureRandom srand = new SecureRandom();
/* 116 */       baos = new ByteArrayOutputStream();
/* 117 */       dos = new DataOutputStream(baos);
/*     */       try
/*     */       {
/* 120 */         dos.write(InetAddress.getLocalHost().getAddress());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 124 */         if (logger.isLoggable(MLevel.INFO))
/* 125 */           logger.log(MLevel.INFO, "Failed to get local InetAddress for VMID. This is unlikely to matter. At all. We'll add some extra randomness", e);
/* 126 */         dos.write(srand.nextInt());
/*     */       }
/* 128 */       dos.writeLong(System.currentTimeMillis());
/* 129 */       dos.write(srand.nextInt());
/*     */       
/* 131 */       int remainder = baos.size() % 4;
/* 132 */       if (remainder > 0)
/*     */       {
/* 134 */         int pad = 4 - remainder;
/* 135 */         byte[] pad_bytes = new byte[pad];
/* 136 */         srand.nextBytes(pad_bytes);
/* 137 */         dos.write(pad_bytes);
/*     */       }
/*     */       
/* 140 */       StringBuffer sb = new StringBuffer(32);
/* 141 */       byte[] vmid_bytes = baos.toByteArray();
/* 142 */       dis = new DataInputStream(new ByteArrayInputStream(vmid_bytes));
/* 143 */       int i = 0; for (int num_ints = vmid_bytes.length / 4; i < num_ints; i++)
/*     */       {
/* 145 */         int signed = dis.readInt();
/* 146 */         long unsigned = signed & 0xFFFFFFFF;
/* 147 */         sb.append(Long.toString(unsigned, 36));
/*     */       }
/* 149 */       return sb.toString();
/*     */     }
/*     */     catch (IOException e) {
/*     */       ByteArrayOutputStream baos;
/* 153 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 154 */         logger.log(MLevel.WARNING, "Bizarro! IOException while reading/writing from ByteArray-based streams? We're skipping the VMID thing. It almost certainly doesn't matter, but please report the error.", e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 159 */       return "";
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 165 */         if (dos != null) dos.close();
/*     */       } catch (IOException e) {
/* 167 */         logger.log(MLevel.WARNING, "Huh? Exception close()ing a byte-array bound OutputStream.", e); }
/* 168 */       try { if (dis != null) dis.close();
/*     */       } catch (IOException e) {
/* 170 */         logger.log(MLevel.WARNING, "Huh? Exception close()ing a byte-array bound IntputStream.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String allocateIdentityToken(Object o)
/*     */   {
/* 180 */     if (o == null) {
/* 181 */       return null;
/*     */     }
/*     */     
/* 184 */     String shortIdToken = Integer.toString(System.identityHashCode(o), 16);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */     StringBuffer sb = new StringBuffer(128);
/* 191 */     sb.append(VMID_PFX);
/* 192 */     long count; if ((ID_TOKEN_COUNTER != null) && ((count = ID_TOKEN_COUNTER.encounter(shortIdToken)) > 0L))
/*     */     {
/* 194 */       sb.append(shortIdToken);
/* 195 */       sb.append('#');
/* 196 */       sb.append(count);
/*     */     }
/*     */     else {
/* 199 */       sb.append(shortIdToken);
/*     */     }
/* 201 */     String out = sb.toString().intern();
/*     */     
/* 203 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */   public static DbAuth findAuth(Object o)
/*     */     throws SQLException
/*     */   {
/* 210 */     if (o == null) {
/* 211 */       return NULL_AUTH;
/*     */     }
/* 213 */     String user = null;
/* 214 */     String password = null;
/*     */     
/* 216 */     String overrideDefaultUser = null;
/* 217 */     String overrideDefaultPassword = null;
/*     */     
/*     */     try
/*     */     {
/* 221 */       BeanInfo bi = Introspector.getBeanInfo(o.getClass());
/* 222 */       PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/* 223 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/* 225 */         PropertyDescriptor pd = pds[i];
/* 226 */         Class propCl = pd.getPropertyType();
/* 227 */         String propName = pd.getName();
/* 228 */         if (propCl == String.class)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 234 */           Method readMethod = pd.getReadMethod();
/* 235 */           if (readMethod != null)
/*     */           {
/* 237 */             Object propVal = readMethod.invoke(o, NOARGS);
/* 238 */             String value = (String)propVal;
/* 239 */             if ("user".equals(propName)) {
/* 240 */               user = value;
/* 241 */             } else if ("password".equals(propName)) {
/* 242 */               password = value;
/* 243 */             } else if ("overrideDefaultUser".equals(propName)) {
/* 244 */               overrideDefaultUser = value;
/* 245 */             } else if ("overrideDefaultPassword".equals(propName))
/* 246 */               overrideDefaultPassword = value;
/*     */           }
/*     */         }
/*     */       }
/* 250 */       if (overrideDefaultUser != null)
/* 251 */         return new DbAuth(overrideDefaultUser, overrideDefaultPassword);
/* 252 */       if (user != null) {
/* 253 */         return new DbAuth(user, password);
/*     */       }
/* 255 */       return NULL_AUTH;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 259 */       if (logger.isLoggable(MLevel.FINE))
/* 260 */         logger.log(MLevel.FINE, "An exception occurred while trying to extract the default authentification info from a bean.", e);
/* 261 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   static void resetTxnState(Connection pCon, boolean forceIgnoreUnresolvedTransactions, boolean autoCommitOnClose, boolean txnKnownResolved)
/*     */     throws SQLException
/*     */   {
/* 270 */     if ((!forceIgnoreUnresolvedTransactions) && (!pCon.getAutoCommit()))
/*     */     {
/* 272 */       if ((!autoCommitOnClose) && (!txnKnownResolved))
/*     */       {
/*     */ 
/* 275 */         pCon.rollback();
/*     */       }
/* 277 */       pCon.setAutoCommit(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized ConnectionTester defaultConnectionTester()
/*     */   {
/* 283 */     String dfltCxnTesterClassName = PoolConfig.defaultConnectionTesterClassName();
/* 284 */     if ((connectionTesterClassName != null) && (connectionTesterClassName.equals(dfltCxnTesterClassName))) {
/* 285 */       return cachedTester;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 290 */       cachedTester = (ConnectionTester)Class.forName(dfltCxnTesterClassName).newInstance();
/* 291 */       connectionTesterClassName = cachedTester.getClass().getName();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 296 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 297 */         logger.log(MLevel.WARNING, "Could not load ConnectionTester " + dfltCxnTesterClassName + ", using built in default.", e);
/*     */       }
/*     */       
/* 300 */       cachedTester = C3P0Defaults.connectionTester();
/* 301 */       connectionTesterClassName = cachedTester.getClass().getName();
/*     */     }
/* 303 */     return cachedTester;
/*     */   }
/*     */   
/*     */   public static boolean supportsMethod(Object target, String mname, Class[] argTypes)
/*     */   {
/*     */     try {
/* 309 */       return target.getClass().getMethod(mname, argTypes) != null;
/*     */     } catch (NoSuchMethodException e) {
/* 311 */       return false;
/*     */     }
/*     */     catch (SecurityException e) {
/* 314 */       if (logger.isLoggable(MLevel.FINE)) {
/* 315 */         logger.log(MLevel.FINE, "We were denied access in a check of whether " + target + " supports method " + mname + ". Prob means external clients have no access, returning false.", e);
/*     */       }
/*     */     }
/*     */     
/* 319 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String createUserOverridesAsString(Map userOverrides)
/*     */     throws IOException
/*     */   {
/* 327 */     StringBuffer sb = new StringBuffer();
/* 328 */     sb.append("HexAsciiSerializedMap");
/* 329 */     sb.append('[');
/* 330 */     sb.append(ByteUtils.toHexAscii(SerializableUtils.toByteArray(userOverrides)));
/* 331 */     sb.append(']');
/* 332 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static Map parseUserOverridesAsString(String userOverridesAsString) throws IOException, ClassNotFoundException
/*     */   {
/* 337 */     if (userOverridesAsString != null)
/*     */     {
/* 339 */       String hexAscii = userOverridesAsString.substring("HexAsciiSerializedMap".length() + 1, userOverridesAsString.length() - 1);
/* 340 */       byte[] serBytes = ByteUtils.fromHexAscii(hexAscii);
/* 341 */       return Collections.unmodifiableMap((Map)SerializableUtils.fromByteArray(serBytes));
/*     */     }
/*     */     
/* 344 */     return Collections.EMPTY_MAP;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0ImplUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */