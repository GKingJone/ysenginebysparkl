/*     */ package com.facebook.presto.jdbc.internal.jetty.util.security;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Constraint
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   public static final String __BASIC_AUTH = "BASIC";
/*     */   public static final String __FORM_AUTH = "FORM";
/*     */   public static final String __DIGEST_AUTH = "DIGEST";
/*     */   public static final String __CERT_AUTH = "CLIENT_CERT";
/*     */   public static final String __CERT_AUTH2 = "CLIENT-CERT";
/*     */   public static final String __SPNEGO_AUTH = "SPNEGO";
/*     */   public static final String __NEGOTIATE_AUTH = "NEGOTIATE";
/*     */   public static final int DC_UNSET = -1;
/*     */   public static final int DC_NONE = 0;
/*     */   public static final int DC_INTEGRAL = 1;
/*     */   public static final int DC_CONFIDENTIAL = 2;
/*     */   public static final int DC_FORBIDDEN = 3;
/*     */   public static final String NONE = "NONE";
/*     */   public static final String ANY_ROLE = "*";
/*     */   public static final String ANY_AUTH = "**";
/*     */   private String _name;
/*     */   private String[] _roles;
/*     */   
/*     */   public static boolean validateMethod(String method)
/*     */   {
/*  48 */     if (method == null)
/*  49 */       return false;
/*  50 */     method = method.trim();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  57 */     return (method.equals("FORM")) || (method.equals("BASIC")) || (method.equals("DIGEST")) || (method.equals("CLIENT_CERT")) || (method.equals("CLIENT-CERT")) || (method.equals("SPNEGO")) || (method.equals("NEGOTIATE"));
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
/*  75 */   private int _dataConstraint = -1;
/*     */   
/*  77 */   private boolean _anyRole = false;
/*     */   
/*  79 */   private boolean _anyAuth = false;
/*     */   
/*  81 */   private boolean _authenticate = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Constraint() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Constraint(String name, String role)
/*     */   {
/* 100 */     setName(name);
/* 101 */     setRoles(new String[] { role });
/*     */   }
/*     */   
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/* 108 */     return super.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 117 */     this._name = name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 123 */     return this._name;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setRoles(String[] roles)
/*     */   {
/* 129 */     this._roles = roles;
/* 130 */     this._anyRole = false;
/* 131 */     this._anyAuth = false;
/* 132 */     int i; if (roles != null)
/*     */     {
/* 134 */       for (i = roles.length; i-- > 0;)
/*     */       {
/* 136 */         this._anyRole |= "*".equals(roles[i]);
/* 137 */         this._anyAuth |= "**".equals(roles[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAnyRole()
/*     */   {
/* 148 */     return this._anyRole;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAnyAuth()
/*     */   {
/* 158 */     return this._anyAuth;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] getRoles()
/*     */   {
/* 167 */     return this._roles;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasRole(String role)
/*     */   {
/* 177 */     if (this._anyRole) return true;
/* 178 */     int i; if (this._roles != null) for (i = this._roles.length; i-- > 0;)
/* 179 */         if (role.equals(this._roles[i])) return true;
/* 180 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthenticate(boolean authenticate)
/*     */   {
/* 189 */     this._authenticate = authenticate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getAuthenticate()
/*     */   {
/* 198 */     return this._authenticate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isForbidden()
/*     */   {
/* 207 */     return (this._authenticate) && (!this._anyRole) && ((this._roles == null) || (this._roles.length == 0));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDataConstraint(int c)
/*     */   {
/* 217 */     if ((c < 0) || (c > 2)) throw new IllegalArgumentException("Constraint out of range");
/* 218 */     this._dataConstraint = c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDataConstraint()
/*     */   {
/* 228 */     return this._dataConstraint;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasDataConstraint()
/*     */   {
/* 237 */     return this._dataConstraint >= 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 246 */     return "SC{" + this._name + "," + (this._roles == null ? "-" : this._anyRole ? "*" : Arrays.asList(this._roles).toString()) + "," + (this._dataConstraint == 1 ? "INTEGRAL}" : this._dataConstraint == 0 ? "NONE}" : this._dataConstraint == -1 ? "DC_UNSET}" : "CONFIDENTIAL}");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\security\Constraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */