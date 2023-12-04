/*    */ package com.facebook.presto.jdbc.internal.spi.security;
/*    */ 
/*    */ import java.security.Principal;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Identity
/*    */ {
/*    */   private final String user;
/*    */   private final Optional<Principal> principal;
/*    */   
/*    */   public Identity(String user, Optional<Principal> principal)
/*    */   {
/* 29 */     this.user = ((String)Objects.requireNonNull(user, "user is null"));
/* 30 */     this.principal = ((Optional)Objects.requireNonNull(principal, "principal is null"));
/*    */   }
/*    */   
/*    */   public String getUser()
/*    */   {
/* 35 */     return this.user;
/*    */   }
/*    */   
/*    */   public Optional<Principal> getPrincipal()
/*    */   {
/* 40 */     return this.principal;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 46 */     if (this == o) {
/* 47 */       return true;
/*    */     }
/* 49 */     if ((o == null) || (getClass() != o.getClass())) {
/* 50 */       return false;
/*    */     }
/* 52 */     Identity identity = (Identity)o;
/* 53 */     return Objects.equals(this.user, identity.user);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return Objects.hash(new Object[] { this.user });
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 65 */     StringBuilder sb = new StringBuilder("Identity{");
/* 66 */     sb.append("user='").append(this.user).append('\'');
/* 67 */     if (this.principal.isPresent()) {
/* 68 */       sb.append(", principal=").append(this.principal.get());
/*    */     }
/* 70 */     sb.append('}');
/* 71 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\security\Identity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */