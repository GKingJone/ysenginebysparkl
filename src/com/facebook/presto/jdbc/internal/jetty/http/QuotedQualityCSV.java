/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QuotedQualityCSV
/*     */   implements Iterable<String>
/*     */ {
/*  37 */   private static final Double ZERO = new Double(0.0D);
/*  38 */   private static final Double ONE = new Double(1.0D);
/*  39 */   private static enum State { VALUE,  PARAM_NAME,  PARAM_VALUE,  Q_VALUE;
/*     */     private State() {} }
/*  41 */   private final List<String> _values = new ArrayList();
/*  42 */   private final List<Double> _quality = new ArrayList();
/*  43 */   private boolean _sorted = false;
/*     */   
/*     */ 
/*     */   public QuotedQualityCSV(String... values)
/*     */   {
/*  48 */     for (String v : values) {
/*  49 */       addValue(v);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addValue(String value)
/*     */   {
/*  56 */     StringBuffer buffer = new StringBuffer();
/*     */     
/*  58 */     int l = value.length();
/*  59 */     State state = State.VALUE;
/*  60 */     boolean quoted = false;
/*  61 */     boolean sloshed = false;
/*  62 */     int nws_length = 0;
/*  63 */     int last_length = 0;
/*  64 */     Double q = ONE;
/*  65 */     for (int i = 0; i <= l; i++)
/*     */     {
/*  67 */       char c = i == l ? '\000' : value.charAt(i);
/*     */       
/*     */ 
/*  70 */       if ((quoted) && (c != 0))
/*     */       {
/*  72 */         if (sloshed) {
/*  73 */           sloshed = false;
/*     */         }
/*     */         else {
/*  76 */           switch (c)
/*     */           {
/*     */           case '\\': 
/*  79 */             sloshed = true;
/*  80 */             break;
/*     */           case '"': 
/*  82 */             quoted = false;
/*  83 */             if (state == State.Q_VALUE) {
/*     */               continue;
/*     */             }
/*     */           }
/*     */           
/*     */         }
/*  89 */         buffer.append(c);
/*  90 */         nws_length = buffer.length();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  95 */         switch (c)
/*     */         {
/*     */         case '\t': 
/*     */         case ' ': 
/*  99 */           if (buffer.length() > last_length) {
/* 100 */             buffer.append(c);
/*     */           }
/*     */           break;
/*     */         case '"': 
/* 104 */           quoted = true;
/* 105 */           if (state != State.Q_VALUE)
/*     */           {
/*     */ 
/* 108 */             buffer.append(c);
/* 109 */             nws_length = buffer.length(); }
/* 110 */           break;
/*     */         
/*     */         case ';': 
/* 113 */           if (state == State.Q_VALUE)
/*     */           {
/*     */             try
/*     */             {
/* 117 */               q = new Double(buffer.substring(last_length));
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 121 */               q = ZERO;
/*     */             }
/* 123 */             nws_length = last_length;
/*     */           }
/*     */           
/* 126 */           buffer.setLength(nws_length);
/* 127 */           buffer.append(c);
/* 128 */           nws_length++;last_length = nws_length;
/* 129 */           state = State.PARAM_NAME;
/* 130 */           break;
/*     */         
/*     */         case '\000': 
/*     */         case ',': 
/* 134 */           if (state == State.Q_VALUE)
/*     */           {
/*     */             try
/*     */             {
/* 138 */               q = new Double(buffer.substring(last_length));
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 142 */               q = ZERO;
/*     */             }
/* 144 */             nws_length = last_length;
/*     */           }
/* 146 */           buffer.setLength(nws_length);
/* 147 */           if ((q.doubleValue() > 0.0D) && (nws_length > 0))
/*     */           {
/* 149 */             this._values.add(buffer.toString());
/* 150 */             this._quality.add(q);
/* 151 */             this._sorted = false;
/*     */           }
/* 153 */           buffer.setLength(0);
/* 154 */           last_length = 0;
/* 155 */           nws_length = 0;
/* 156 */           q = ONE;
/* 157 */           state = State.VALUE;
/* 158 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 162 */           switch (state)
/*     */           {
/*     */ 
/*     */           case VALUE: 
/* 166 */             buffer.append(c);
/* 167 */             nws_length = buffer.length();
/* 168 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */           case PARAM_NAME: 
/* 173 */             if (c == '=')
/*     */             {
/* 175 */               buffer.setLength(nws_length);
/* 176 */               if ((nws_length - last_length == 1) && (Character.toLowerCase(buffer.charAt(last_length)) == 'q'))
/*     */               {
/* 178 */                 buffer.setLength(last_length - 1);
/* 179 */                 nws_length = buffer.length();
/* 180 */                 last_length = nws_length;
/* 181 */                 state = State.Q_VALUE;
/*     */               }
/*     */               else {
/* 184 */                 buffer.append(c);
/* 185 */                 nws_length++;last_length = nws_length;
/* 186 */                 state = State.PARAM_VALUE;
/*     */               }
/*     */             } else {
/* 189 */               buffer.append(c);
/* 190 */               nws_length = buffer.length(); }
/* 191 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */           case PARAM_VALUE: 
/*     */           case Q_VALUE: 
/* 197 */             buffer.append(c);
/* 198 */             nws_length = buffer.length();
/*     */           }
/*     */           
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> getValues()
/*     */   {
/* 209 */     if (!this._sorted)
/* 210 */       sort();
/* 211 */     return this._values;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<String> iterator()
/*     */   {
/* 217 */     if (!this._sorted)
/* 218 */       sort();
/* 219 */     return this._values.iterator();
/*     */   }
/*     */   
/*     */   protected void sort()
/*     */   {
/* 224 */     this._sorted = true;
/*     */     
/* 226 */     Double last = ZERO;
/* 227 */     int len = Integer.MIN_VALUE;
/*     */     
/* 229 */     for (int i = this._values.size(); i-- > 0;)
/*     */     {
/* 231 */       String v = (String)this._values.get(i);
/* 232 */       Double q = (Double)this._quality.get(i);
/*     */       
/* 234 */       int compare = last.compareTo(q);
/* 235 */       if ((compare > 0) || ((compare == 0) && (v.length() < len)))
/*     */       {
/* 237 */         this._values.set(i, this._values.get(i + 1));
/* 238 */         this._values.set(i + 1, v);
/* 239 */         this._quality.set(i, this._quality.get(i + 1));
/* 240 */         this._quality.set(i + 1, q);
/* 241 */         last = ZERO;
/* 242 */         len = 0;
/* 243 */         i = this._values.size();
/*     */       }
/*     */       else
/*     */       {
/* 247 */         last = q;
/* 248 */         len = v.length();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\QuotedQualityCSV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */