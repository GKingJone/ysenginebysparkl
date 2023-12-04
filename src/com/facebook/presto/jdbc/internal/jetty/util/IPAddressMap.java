/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPAddressMap<TYPE>
/*     */   extends HashMap<String, TYPE>
/*     */ {
/*  48 */   private final HashMap<String, IPAddrPattern> _patterns = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPAddressMap()
/*     */   {
/*  55 */     super(11);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPAddressMap(int capacity)
/*     */   {
/*  65 */     super(capacity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TYPE put(String addrSpec, TYPE object)
/*     */     throws IllegalArgumentException
/*     */   {
/*  78 */     if ((addrSpec == null) || (addrSpec.trim().length() == 0)) {
/*  79 */       throw new IllegalArgumentException("Invalid IP address pattern: " + addrSpec);
/*     */     }
/*  81 */     String spec = addrSpec.trim();
/*  82 */     if (this._patterns.get(spec) == null) {
/*  83 */       this._patterns.put(spec, new IPAddrPattern(spec));
/*     */     }
/*  85 */     return (TYPE)super.put(spec, object);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TYPE get(Object key)
/*     */   {
/*  97 */     return (TYPE)super.get(key);
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
/*     */   public TYPE match(String addr)
/*     */   {
/* 110 */     Map.Entry<String, TYPE> entry = getMatch(addr);
/* 111 */     return entry == null ? null : entry.getValue();
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
/*     */   public Map.Entry<String, TYPE> getMatch(String addr)
/*     */   {
/* 124 */     if (addr != null)
/*     */     {
/* 126 */       for (Map.Entry<String, TYPE> entry : super.entrySet())
/*     */       {
/* 128 */         if (((IPAddrPattern)this._patterns.get(entry.getKey())).match(addr))
/*     */         {
/* 130 */           return entry;
/*     */         }
/*     */       }
/*     */     }
/* 134 */     return null;
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
/*     */   public Object getLazyMatches(String addr)
/*     */   {
/* 147 */     if (addr == null) {
/* 148 */       return LazyList.getList(super.entrySet());
/*     */     }
/* 150 */     Object entries = null;
/* 151 */     for (Map.Entry<String, TYPE> entry : super.entrySet())
/*     */     {
/* 153 */       if (((IPAddrPattern)this._patterns.get(entry.getKey())).match(addr))
/*     */       {
/* 155 */         entries = LazyList.add(entries, entry);
/*     */       }
/*     */     }
/* 158 */     return entries;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class IPAddrPattern
/*     */   {
/* 170 */     private final OctetPattern[] _octets = new OctetPattern[4];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public IPAddrPattern(String value)
/*     */       throws IllegalArgumentException
/*     */     {
/* 181 */       if ((value == null) || (value.trim().length() == 0)) {
/* 182 */         throw new IllegalArgumentException("Invalid IP address pattern: " + value);
/*     */       }
/*     */       try
/*     */       {
/* 186 */         StringTokenizer parts = new StringTokenizer(value, ".");
/*     */         
/*     */ 
/* 189 */         for (int idx = 0; idx < 4; idx++)
/*     */         {
/* 191 */           String part = parts.hasMoreTokens() ? parts.nextToken().trim() : "0-255";
/*     */           
/* 193 */           int len = part.length();
/* 194 */           if ((len == 0) && (parts.hasMoreTokens())) {
/* 195 */             throw new IllegalArgumentException("Invalid IP address pattern: " + value);
/*     */           }
/* 197 */           this._octets[idx] = new OctetPattern(len == 0 ? "0-255" : part);
/*     */         }
/*     */       }
/*     */       catch (IllegalArgumentException ex)
/*     */       {
/* 202 */         throw new IllegalArgumentException("Invalid IP address pattern: " + value, ex);
/*     */       }
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
/*     */     public boolean match(String value)
/*     */       throws IllegalArgumentException
/*     */     {
/* 218 */       if ((value == null) || (value.trim().length() == 0)) {
/* 219 */         throw new IllegalArgumentException("Invalid IP address: " + value);
/*     */       }
/*     */       try
/*     */       {
/* 223 */         StringTokenizer parts = new StringTokenizer(value, ".");
/*     */         
/* 225 */         boolean result = true;
/* 226 */         for (int idx = 0; idx < 4; idx++)
/*     */         {
/* 228 */           if (!parts.hasMoreTokens()) {
/* 229 */             throw new IllegalArgumentException("Invalid IP address: " + value);
/*     */           }
/* 231 */           if (!(result &= this._octets[idx].match(parts.nextToken())))
/*     */             break;
/*     */         }
/* 234 */         return result;
/*     */       }
/*     */       catch (IllegalArgumentException ex)
/*     */       {
/* 238 */         throw new IllegalArgumentException("Invalid IP address: " + value, ex);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class OctetPattern
/*     */     extends BitSet
/*     */   {
/* 252 */     private final BitSet _mask = new BitSet(256);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public OctetPattern(String octetSpec)
/*     */       throws IllegalArgumentException
/*     */     {
/*     */       try
/*     */       {
/* 266 */         if (octetSpec != null)
/*     */         {
/* 268 */           String spec = octetSpec.trim();
/* 269 */           if (spec.length() == 0)
/*     */           {
/* 271 */             this._mask.set(0, 255);
/*     */           }
/*     */           else
/*     */           {
/* 275 */             StringTokenizer parts = new StringTokenizer(spec, ",");
/* 276 */             while (parts.hasMoreTokens())
/*     */             {
/* 278 */               String part = parts.nextToken().trim();
/* 279 */               if (part.length() > 0)
/*     */               {
/* 281 */                 if (part.indexOf('-') < 0)
/*     */                 {
/* 283 */                   Integer value = Integer.valueOf(part);
/* 284 */                   this._mask.set(value.intValue());
/*     */                 }
/*     */                 else
/*     */                 {
/* 288 */                   int low = 0;int high = 255;
/*     */                   
/* 290 */                   String[] bounds = part.split("-", -2);
/* 291 */                   if (bounds.length != 2)
/*     */                   {
/* 293 */                     throw new IllegalArgumentException("Invalid octet spec: " + octetSpec);
/*     */                   }
/*     */                   
/* 296 */                   if (bounds[0].length() > 0)
/*     */                   {
/* 298 */                     low = Integer.parseInt(bounds[0]);
/*     */                   }
/* 300 */                   if (bounds[1].length() > 0)
/*     */                   {
/* 302 */                     high = Integer.parseInt(bounds[1]);
/*     */                   }
/*     */                   
/* 305 */                   if (low > high)
/*     */                   {
/* 307 */                     throw new IllegalArgumentException("Invalid octet spec: " + octetSpec);
/*     */                   }
/*     */                   
/* 310 */                   this._mask.set(low, high + 1);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/* 319 */         throw new IllegalArgumentException("Invalid octet spec: " + octetSpec, ex);
/*     */       }
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
/*     */     public boolean match(String value)
/*     */       throws IllegalArgumentException
/*     */     {
/* 334 */       if ((value == null) || (value.trim().length() == 0)) {
/* 335 */         throw new IllegalArgumentException("Invalid octet: " + value);
/*     */       }
/*     */       try
/*     */       {
/* 339 */         int number = Integer.parseInt(value);
/* 340 */         return match(number);
/*     */       }
/*     */       catch (NumberFormatException ex)
/*     */       {
/* 344 */         throw new IllegalArgumentException("Invalid octet: " + value);
/*     */       }
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
/*     */     public boolean match(int number)
/*     */       throws IllegalArgumentException
/*     */     {
/* 359 */       if ((number < 0) || (number > 255)) {
/* 360 */         throw new IllegalArgumentException("Invalid octet: " + number);
/*     */       }
/* 362 */       return this._mask.get(number);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\IPAddressMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */