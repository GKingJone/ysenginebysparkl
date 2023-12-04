/*     */ package com.facebook.presto.jdbc.internal.jetty.util.security;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.TypeUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Credential
/*     */   implements Serializable
/*     */ {
/*  46 */   private static final Logger LOG = Log.getLogger(Credential.class);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = -7760551052768181572L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract boolean check(Object paramObject);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Credential getCredential(String credential)
/*     */   {
/*  73 */     if (credential.startsWith("CRYPT:")) return new Crypt(credential);
/*  74 */     if (credential.startsWith("MD5:")) { return new MD5(credential);
/*     */     }
/*  76 */     return new Password(credential);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Crypt
/*     */     extends Credential
/*     */   {
/*     */     private static final long serialVersionUID = -2027792997664744210L;
/*     */     
/*     */     public static final String __TYPE = "CRYPT:";
/*     */     
/*     */     private final String _cooked;
/*     */     
/*     */ 
/*     */     Crypt(String cooked)
/*     */     {
/*  93 */       this._cooked = (cooked.startsWith("CRYPT:") ? cooked.substring("CRYPT:".length()) : cooked);
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean check(Object credentials)
/*     */     {
/*  99 */       if ((credentials instanceof char[]))
/* 100 */         credentials = new String((char[])credentials);
/* 101 */       if ((!(credentials instanceof String)) && (!(credentials instanceof Password))) {
/* 102 */         Credential.LOG.warn("Can't check " + credentials.getClass() + " against CRYPT", new Object[0]);
/*     */       }
/* 104 */       String passwd = credentials.toString();
/* 105 */       return this._cooked.equals(UnixCrypt.crypt(passwd, this._cooked));
/*     */     }
/*     */     
/*     */     public static String crypt(String user, String pw)
/*     */     {
/* 110 */       return "CRYPT:" + UnixCrypt.crypt(pw, user);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class MD5
/*     */     extends Credential
/*     */   {
/*     */     private static final long serialVersionUID = 5533846540822684240L;
/*     */     
/*     */ 
/*     */     public static final String __TYPE = "MD5:";
/*     */     
/* 124 */     public static final Object __md5Lock = new Object();
/*     */     
/*     */     private static MessageDigest __md;
/*     */     
/*     */     private final byte[] _digest;
/*     */     
/*     */ 
/*     */     MD5(String digest)
/*     */     {
/* 133 */       digest = digest.startsWith("MD5:") ? digest.substring("MD5:".length()) : digest;
/* 134 */       this._digest = TypeUtil.parseBytes(digest, 16);
/*     */     }
/*     */     
/*     */ 
/*     */     public byte[] getDigest()
/*     */     {
/* 140 */       return this._digest;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean check(Object credentials)
/*     */     {
/*     */       try
/*     */       {
/* 149 */         byte[] digest = null;
/*     */         
/* 151 */         if ((credentials instanceof char[]))
/* 152 */           credentials = new String((char[])credentials);
/* 153 */         if (((credentials instanceof Password)) || ((credentials instanceof String)))
/*     */         {
/* 155 */           synchronized (__md5Lock)
/*     */           {
/* 157 */             if (__md == null) __md = MessageDigest.getInstance("MD5");
/* 158 */             __md.reset();
/* 159 */             __md.update(credentials.toString().getBytes(StandardCharsets.ISO_8859_1));
/* 160 */             digest = __md.digest();
/*     */           }
/* 162 */           if ((digest == null) || (digest.length != this._digest.length)) return false;
/* 163 */           boolean digestMismatch = false;
/* 164 */           for (int i = 0; i < digest.length; i++)
/* 165 */             digestMismatch |= digest[i] != this._digest[i];
/* 166 */           return !digestMismatch;
/*     */         }
/* 168 */         if ((credentials instanceof MD5))
/*     */         {
/* 170 */           MD5 md5 = (MD5)credentials;
/* 171 */           if (this._digest.length != md5._digest.length) return false;
/* 172 */           boolean digestMismatch = false;
/* 173 */           for (int i = 0; i < this._digest.length; i++)
/* 174 */             digestMismatch |= this._digest[i] != md5._digest[i];
/* 175 */           return !digestMismatch;
/*     */         }
/* 177 */         if ((credentials instanceof Credential))
/*     */         {
/*     */ 
/*     */ 
/* 181 */           return ((Credential)credentials).check(this);
/*     */         }
/*     */         
/*     */ 
/* 185 */         Credential.LOG.warn("Can't check " + credentials.getClass() + " against MD5", new Object[0]);
/* 186 */         return false;
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 191 */         Credential.LOG.warn(e); }
/* 192 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public static String digest(String password)
/*     */     {
/*     */       try
/*     */       {
/*     */         byte[] digest;
/*     */         
/* 202 */         synchronized (__md5Lock)
/*     */         {
/* 204 */           if (__md == null)
/*     */           {
/*     */             try
/*     */             {
/* 208 */               __md = MessageDigest.getInstance("MD5");
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 212 */               Credential.LOG.warn(e);
/* 213 */               return null;
/*     */             }
/*     */           }
/*     */           
/* 217 */           __md.reset();
/* 218 */           __md.update(password.getBytes(StandardCharsets.ISO_8859_1));
/* 219 */           digest = __md.digest();
/*     */         }
/*     */         byte[] digest;
/* 222 */         return "MD5:" + TypeUtil.toString(digest, 16);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 226 */         Credential.LOG.warn(e); }
/* 227 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\security\Credential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */