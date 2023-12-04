/*     */ package com.facebook.presto.jdbc.internal.jetty.util.security;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Password
/*     */   extends Credential
/*     */ {
/*  59 */   private static final Logger LOG = Log.getLogger(Password.class);
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 5062906681431569445L;
/*     */   
/*     */ 
/*     */   public static final String __OBFUSCATE = "OBF:";
/*     */   
/*     */ 
/*     */   private String _pw;
/*     */   
/*     */ 
/*     */ 
/*     */   public Password(String password)
/*     */   {
/*  75 */     this._pw = password;
/*     */     
/*     */ 
/*  78 */     while ((this._pw != null) && (this._pw.startsWith("OBF:"))) {
/*  79 */       this._pw = deobfuscate(this._pw);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*  86 */     return this._pw;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toStarString()
/*     */   {
/*  92 */     return "*****************************************************".substring(0, this._pw.length());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean check(Object credentials)
/*     */   {
/*  99 */     if (this == credentials) { return true;
/*     */     }
/* 101 */     if ((credentials instanceof Password)) { return credentials.equals(this._pw);
/*     */     }
/* 103 */     if ((credentials instanceof String)) { return credentials.equals(this._pw);
/*     */     }
/* 105 */     if ((credentials instanceof char[])) { return Arrays.equals(this._pw.toCharArray(), (char[])credentials);
/*     */     }
/* 107 */     if ((credentials instanceof Credential)) { return ((Credential)credentials).check(this._pw);
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 116 */     if (this == o) {
/* 117 */       return true;
/*     */     }
/* 119 */     if (null == o) {
/* 120 */       return false;
/*     */     }
/* 122 */     if ((o instanceof Password))
/*     */     {
/* 124 */       Password p = (Password)o;
/*     */       
/* 126 */       return (p._pw == this._pw) || ((null != this._pw) && (this._pw.equals(p._pw)));
/*     */     }
/*     */     
/* 129 */     if ((o instanceof String)) {
/* 130 */       return o.equals(this._pw);
/*     */     }
/* 132 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 139 */     return null == this._pw ? super.hashCode() : this._pw.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */   public static String obfuscate(String s)
/*     */   {
/* 145 */     StringBuilder buf = new StringBuilder();
/* 146 */     byte[] b = s.getBytes(StandardCharsets.UTF_8);
/*     */     
/* 148 */     buf.append("OBF:");
/* 149 */     for (int i = 0; i < b.length; i++)
/*     */     {
/* 151 */       byte b1 = b[i];
/* 152 */       byte b2 = b[(b.length - (i + 1))];
/* 153 */       if ((b1 < 0) || (b2 < 0))
/*     */       {
/* 155 */         int i0 = (0xFF & b1) * 256 + (0xFF & b2);
/* 156 */         String x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH);
/* 157 */         buf.append("U0000", 0, 5 - x.length());
/* 158 */         buf.append(x);
/*     */       }
/*     */       else
/*     */       {
/* 162 */         int i1 = Byte.MAX_VALUE + b1 + b2;
/* 163 */         int i2 = Byte.MAX_VALUE + b1 - b2;
/* 164 */         int i0 = i1 * 256 + i2;
/* 165 */         String x = Integer.toString(i0, 36).toLowerCase(Locale.ENGLISH);
/*     */         
/* 167 */         int j0 = Integer.parseInt(x, 36);
/* 168 */         int j1 = i0 / 256;
/* 169 */         int j2 = i0 % 256;
/* 170 */         byte bx = (byte)((j1 + j2 - 254) / 2);
/*     */         
/* 172 */         buf.append("000", 0, 4 - x.length());
/* 173 */         buf.append(x);
/*     */       }
/*     */     }
/*     */     
/* 177 */     return buf.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String deobfuscate(String s)
/*     */   {
/* 184 */     if (s.startsWith("OBF:")) { s = s.substring(4);
/*     */     }
/* 186 */     byte[] b = new byte[s.length() / 2];
/* 187 */     int l = 0;
/* 188 */     for (int i = 0; i < s.length(); i += 4)
/*     */     {
/* 190 */       if (s.charAt(i) == 'U')
/*     */       {
/* 192 */         i++;
/* 193 */         String x = s.substring(i, i + 4);
/* 194 */         int i0 = Integer.parseInt(x, 36);
/* 195 */         byte bx = (byte)(i0 >> 8);
/* 196 */         b[(l++)] = bx;
/*     */       }
/*     */       else
/*     */       {
/* 200 */         String x = s.substring(i, i + 4);
/* 201 */         int i0 = Integer.parseInt(x, 36);
/* 202 */         int i1 = i0 / 256;
/* 203 */         int i2 = i0 % 256;
/* 204 */         byte bx = (byte)((i1 + i2 - 254) / 2);
/* 205 */         b[(l++)] = bx;
/*     */       }
/*     */     }
/*     */     
/* 209 */     return new String(b, 0, l, StandardCharsets.UTF_8);
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
/*     */   public static Password getPassword(String realm, String dft, String promptDft)
/*     */   {
/* 229 */     String passwd = System.getProperty(realm, dft);
/* 230 */     if ((passwd == null) || (passwd.length() == 0))
/*     */     {
/*     */       try
/*     */       {
/* 234 */         System.out.print(realm + ((promptDft != null) && (promptDft.length() > 0) ? " [dft]" : "") + " : ");
/* 235 */         System.out.flush();
/* 236 */         byte[] buf = new byte['Ȁ'];
/* 237 */         int len = System.in.read(buf);
/* 238 */         if (len > 0) passwd = new String(buf, 0, len).trim();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 242 */         LOG.warn("EXCEPTION ", e);
/*     */       }
/* 244 */       if ((passwd == null) || (passwd.length() == 0)) passwd = promptDft;
/*     */     }
/* 246 */     return new Password(passwd);
/*     */   }
/*     */   
/*     */   public static void main(String[] arg)
/*     */   {
/* 251 */     if ((arg.length != 1) && (arg.length != 2))
/*     */     {
/* 253 */       System.err.println("Usage - java " + Password.class.getName() + " [<user>] <password>");
/* 254 */       System.err.println("If the password is ?, the user will be prompted for the password");
/* 255 */       System.exit(1);
/*     */     }
/* 257 */     String p = arg[1];
/* 258 */     Password pw = new Password(p);
/* 259 */     System.err.println(pw.toString());
/* 260 */     System.err.println(obfuscate(pw.toString()));
/* 261 */     System.err.println(Credential.MD5.digest(p));
/* 262 */     if (arg.length == 2) System.err.println(Credential.Crypt.crypt(arg[0], pw.toString()));
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\security\Password.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */