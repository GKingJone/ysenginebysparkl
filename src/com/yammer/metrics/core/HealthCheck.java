/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HealthCheck
/*     */ {
/*     */   private final String name;
/*     */   
/*     */ 
/*     */   public static class Result
/*     */   {
/*  12 */     private static final Result HEALTHY = new Result(true, null, null);
/*     */     
/*     */     private static final int PRIME = 31;
/*     */     private final boolean healthy;
/*     */     private final String message;
/*     */     private final Throwable error;
/*     */     
/*     */     public static Result healthy()
/*     */     {
/*  21 */       return HEALTHY;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Result healthy(String message)
/*     */     {
/*  31 */       return new Result(true, message, null);
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
/*     */     public static Result healthy(String message, Object... args)
/*     */     {
/*  46 */       return healthy(String.format(message, args));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Result unhealthy(String message)
/*     */     {
/*  56 */       return new Result(false, message, null);
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
/*     */     public static Result unhealthy(String message, Object... args)
/*     */     {
/*  71 */       return unhealthy(String.format(message, args));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Result unhealthy(Throwable error)
/*     */     {
/*  81 */       return new Result(false, error.getMessage(), error);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Result(boolean isHealthy, String message, Throwable error)
/*     */     {
/*  89 */       this.healthy = isHealthy;
/*  90 */       this.message = message;
/*  91 */       this.error = error;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isHealthy()
/*     */     {
/* 100 */       return this.healthy;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getMessage()
/*     */     {
/* 110 */       return this.message;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Throwable getError()
/*     */     {
/* 119 */       return this.error;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/* 124 */       if (this == o) return true;
/* 125 */       if ((o == null) || (getClass() != o.getClass())) return false;
/* 126 */       Result result = (Result)o;
/* 127 */       if ((this.healthy == result.healthy) && (this.error != null ? this.error.equals(result.error) : result.error == null)) {} return this.message != null ? this.message.equals(result.message) : result.message == null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 134 */       int result = this.healthy ? 1 : 0;
/* 135 */       result = 31 * result + (this.message != null ? this.message.hashCode() : 0);
/* 136 */       result = 31 * result + (this.error != null ? this.error.hashCode() : 0);
/* 137 */       return result;
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 142 */       StringBuilder builder = new StringBuilder("Result{isHealthy=");
/* 143 */       builder.append(this.healthy);
/* 144 */       if (this.message != null) {
/* 145 */         builder.append(", message=").append(this.message);
/*     */       }
/* 147 */       if (this.error != null) {
/* 148 */         builder.append(", error=").append(this.error);
/*     */       }
/* 150 */       builder.append('}');
/* 151 */       return builder.toString();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected HealthCheck(String name)
/*     */   {
/* 164 */     this.name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 173 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract Result check()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Result execute()
/*     */   {
/*     */     try
/*     */     {
/* 195 */       return check();
/*     */     } catch (Error e) {
/* 197 */       throw e;
/*     */     } catch (Throwable e) {
/* 199 */       return Result.unhealthy(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\HealthCheck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */