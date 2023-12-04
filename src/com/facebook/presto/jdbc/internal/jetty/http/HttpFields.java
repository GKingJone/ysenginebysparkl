/*     */ package com.facebook.presto.jdbc.internal.jetty.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ArrayTernaryTrie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.QuotedStringTokenizer;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Trie;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
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
/*     */ public class HttpFields
/*     */   implements Iterable<HttpField>
/*     */ {
/*     */   @Deprecated
/*     */   public static final String __separators = ", \t";
/*  55 */   private static final Logger LOG = Log.getLogger(HttpFields.class);
/*     */   
/*     */ 
/*     */   private HttpField[] _fields;
/*     */   
/*     */   private int _size;
/*     */   
/*     */ 
/*     */   public HttpFields()
/*     */   {
/*  65 */     this._fields = new HttpField[20];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpFields(int capacity)
/*     */   {
/*  75 */     this._fields = new HttpField[capacity];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpFields(HttpFields fields)
/*     */   {
/*  85 */     this._fields = ((HttpField[])Arrays.copyOf(fields._fields, fields._fields.length + 10));
/*  86 */     this._size = fields._size;
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  91 */     return this._size;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<HttpField> iterator()
/*     */   {
/*  97 */     return new Itr(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<String> getFieldNamesCollection()
/*     */   {
/* 106 */     Set<String> set = new HashSet(this._size);
/* 107 */     for (HttpField f : this)
/*     */     {
/* 109 */       if (f != null)
/* 110 */         set.add(f.getName());
/*     */     }
/* 112 */     return set;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getFieldNames()
/*     */   {
/* 122 */     return Collections.enumeration(getFieldNamesCollection());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpField getField(int index)
/*     */   {
/* 132 */     if (index >= this._size)
/* 133 */       throw new NoSuchElementException();
/* 134 */     return this._fields[index];
/*     */   }
/*     */   
/*     */   public HttpField getField(HttpHeader header)
/*     */   {
/* 139 */     for (int i = 0; i < this._size; i++)
/*     */     {
/* 141 */       HttpField f = this._fields[i];
/* 142 */       if (f.getHeader() == header)
/* 143 */         return f;
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */   
/*     */   public HttpField getField(String name)
/*     */   {
/* 150 */     for (int i = 0; i < this._size; i++)
/*     */     {
/* 152 */       HttpField f = this._fields[i];
/* 153 */       if (f.getName().equalsIgnoreCase(name))
/* 154 */         return f;
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */   
/*     */   public boolean contains(HttpField field)
/*     */   {
/* 161 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 163 */       HttpField f = this._fields[i];
/* 164 */       if ((f.isSameName(field)) && (f.contains(field.getValue())))
/* 165 */         return true;
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */   
/*     */   public boolean contains(HttpHeader header, String value)
/*     */   {
/* 172 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 174 */       HttpField f = this._fields[i];
/* 175 */       if ((f.getHeader() == header) && (f.contains(value)))
/* 176 */         return true;
/*     */     }
/* 178 */     return false;
/*     */   }
/*     */   
/*     */   public boolean contains(String name, String value)
/*     */   {
/* 183 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 185 */       HttpField f = this._fields[i];
/* 186 */       if ((f.getName().equalsIgnoreCase(name)) && (f.contains(value)))
/* 187 */         return true;
/*     */     }
/* 189 */     return false;
/*     */   }
/*     */   
/*     */   public boolean contains(HttpHeader header)
/*     */   {
/* 194 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 196 */       HttpField f = this._fields[i];
/* 197 */       if (f.getHeader() == header)
/* 198 */         return true;
/*     */     }
/* 200 */     return false;
/*     */   }
/*     */   
/*     */   public boolean containsKey(String name)
/*     */   {
/* 205 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 207 */       HttpField f = this._fields[i];
/* 208 */       if (f.getName().equalsIgnoreCase(name))
/* 209 */         return true;
/*     */     }
/* 211 */     return false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getStringField(HttpHeader header)
/*     */   {
/* 217 */     return get(header);
/*     */   }
/*     */   
/*     */   public String get(HttpHeader header)
/*     */   {
/* 222 */     for (int i = 0; i < this._size; i++)
/*     */     {
/* 224 */       HttpField f = this._fields[i];
/* 225 */       if (f.getHeader() == header)
/* 226 */         return f.getValue();
/*     */     }
/* 228 */     return null;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getStringField(String name)
/*     */   {
/* 234 */     return get(name);
/*     */   }
/*     */   
/*     */   public String get(String header)
/*     */   {
/* 239 */     for (int i = 0; i < this._size; i++)
/*     */     {
/* 241 */       HttpField f = this._fields[i];
/* 242 */       if (f.getName().equalsIgnoreCase(header))
/* 243 */         return f.getValue();
/*     */     }
/* 245 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getValuesList(HttpHeader header)
/*     */   {
/* 256 */     List<String> list = new ArrayList();
/* 257 */     for (HttpField f : this)
/* 258 */       if (f.getHeader() == header)
/* 259 */         list.add(f.getValue());
/* 260 */     return list;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getValuesList(String name)
/*     */   {
/* 271 */     List<String> list = new ArrayList();
/* 272 */     for (HttpField f : this)
/* 273 */       if (f.getName().equalsIgnoreCase(name))
/* 274 */         list.add(f.getValue());
/* 275 */     return list;
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
/*     */   public List<String> getCSV(HttpHeader header, boolean keepQuotes)
/*     */   {
/* 288 */     QuotedCSV values = new QuotedCSV(keepQuotes, new String[0]);
/* 289 */     for (HttpField f : this)
/* 290 */       if (f.getHeader() == header)
/* 291 */         values.addValue(f.getValue());
/* 292 */     return values.getValues();
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
/*     */   public List<String> getCSV(String name, boolean keepQuotes)
/*     */   {
/* 305 */     QuotedCSV values = new QuotedCSV(keepQuotes, new String[0]);
/* 306 */     for (HttpField f : this)
/* 307 */       if (f.getName().equalsIgnoreCase(name))
/* 308 */         values.addValue(f.getValue());
/* 309 */     return values.getValues();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getQualityCSV(HttpHeader header)
/*     */   {
/* 321 */     QuotedQualityCSV values = new QuotedQualityCSV(new String[0]);
/* 322 */     for (HttpField f : this)
/* 323 */       if (f.getHeader() == header)
/* 324 */         values.addValue(f.getValue());
/* 325 */     return values.getValues();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<String> getQualityCSV(String name)
/*     */   {
/* 337 */     QuotedQualityCSV values = new QuotedQualityCSV(new String[0]);
/* 338 */     for (HttpField f : this)
/* 339 */       if (f.getName().equalsIgnoreCase(name))
/* 340 */         values.addValue(f.getValue());
/* 341 */     return values.getValues();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<String> getValues(final String name)
/*     */   {
/* 352 */     for (int i = 0; i < this._size; i++)
/*     */     {
/* 354 */       final HttpField f = this._fields[i];
/*     */       
/* 356 */       if ((f.getName().equalsIgnoreCase(name)) && (f.getValue() != null))
/*     */       {
/* 358 */         final int first = i;
/* 359 */         new Enumeration()
/*     */         {
/* 361 */           HttpField field = f;
/* 362 */           int i = first + 1;
/*     */           
/*     */ 
/*     */           public boolean hasMoreElements()
/*     */           {
/* 367 */             if (this.field == null)
/*     */             {
/* 369 */               while (this.i < HttpFields.this._size)
/*     */               {
/* 371 */                 this.field = HttpFields.this._fields[(this.i++)];
/* 372 */                 if ((this.field.getName().equalsIgnoreCase(name)) && (this.field.getValue() != null))
/* 373 */                   return true;
/*     */               }
/* 375 */               this.field = null;
/* 376 */               return false;
/*     */             }
/* 378 */             return true;
/*     */           }
/*     */           
/*     */           public String nextElement()
/*     */             throws NoSuchElementException
/*     */           {
/* 384 */             if (hasMoreElements())
/*     */             {
/* 386 */               String value = this.field.getValue();
/* 387 */               this.field = null;
/* 388 */               return value;
/*     */             }
/* 390 */             throw new NoSuchElementException();
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */     
/* 396 */     List<String> empty = Collections.emptyList();
/* 397 */     return Collections.enumeration(empty);
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
/*     */   @Deprecated
/*     */   public Enumeration<String> getValues(String name, final String separators)
/*     */   {
/* 412 */     final Enumeration<String> e = getValues(name);
/* 413 */     if (e == null)
/* 414 */       return null;
/* 415 */     new Enumeration()
/*     */     {
/* 417 */       QuotedStringTokenizer tok = null;
/*     */       
/*     */ 
/*     */       public boolean hasMoreElements()
/*     */       {
/* 422 */         if ((this.tok != null) && (this.tok.hasMoreElements())) return true;
/* 423 */         while (e.hasMoreElements())
/*     */         {
/* 425 */           String value = (String)e.nextElement();
/* 426 */           if (value != null)
/*     */           {
/* 428 */             this.tok = new QuotedStringTokenizer(value, separators, false, false);
/* 429 */             if (this.tok.hasMoreElements()) return true;
/*     */           }
/*     */         }
/* 432 */         this.tok = null;
/* 433 */         return false;
/*     */       }
/*     */       
/*     */       public String nextElement()
/*     */         throws NoSuchElementException
/*     */       {
/* 439 */         if (!hasMoreElements()) throw new NoSuchElementException();
/* 440 */         String next = (String)this.tok.nextElement();
/* 441 */         if (next != null) next = next.trim();
/* 442 */         return next;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public void put(HttpField field)
/*     */   {
/* 449 */     boolean put = false;
/* 450 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 452 */       HttpField f = this._fields[i];
/* 453 */       if (f.isSameName(field))
/*     */       {
/* 455 */         if (put)
/*     */         {
/* 457 */           System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
/*     */         }
/*     */         else
/*     */         {
/* 461 */           this._fields[i] = field;
/* 462 */           put = true;
/*     */         }
/*     */       }
/*     */     }
/* 466 */     if (!put) {
/* 467 */       add(field);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(String name, String value)
/*     */   {
/* 478 */     if (value == null) {
/* 479 */       remove(name);
/*     */     } else {
/* 481 */       put(new HttpField(name, value));
/*     */     }
/*     */   }
/*     */   
/*     */   public void put(HttpHeader header, HttpHeaderValue value) {
/* 486 */     put(header, value.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(HttpHeader header, String value)
/*     */   {
/* 497 */     if (value == null) {
/* 498 */       remove(header);
/*     */     } else {
/* 500 */       put(new HttpField(header, value));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(String name, List<String> list)
/*     */   {
/* 511 */     remove(name);
/* 512 */     for (String v : list) {
/* 513 */       if (v != null) {
/* 514 */         add(name, v);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(String name, String value)
/*     */   {
/* 526 */     if (value == null) {
/* 527 */       return;
/*     */     }
/* 529 */     HttpField field = new HttpField(name, value);
/* 530 */     add(field);
/*     */   }
/*     */   
/*     */   public void add(HttpHeader header, HttpHeaderValue value)
/*     */   {
/* 535 */     add(header, value.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(HttpHeader header, String value)
/*     */   {
/* 547 */     if (value == null) { throw new IllegalArgumentException("null value");
/*     */     }
/* 549 */     HttpField field = new HttpField(header, value);
/* 550 */     add(field);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpField remove(HttpHeader name)
/*     */   {
/* 561 */     HttpField removed = null;
/* 562 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 564 */       HttpField f = this._fields[i];
/* 565 */       if (f.getHeader() == name)
/*     */       {
/* 567 */         removed = f;
/* 568 */         System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
/*     */       }
/*     */     }
/* 571 */     return removed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HttpField remove(String name)
/*     */   {
/* 582 */     HttpField removed = null;
/* 583 */     for (int i = this._size; i-- > 0;)
/*     */     {
/* 585 */       HttpField f = this._fields[i];
/* 586 */       if (f.getName().equalsIgnoreCase(name))
/*     */       {
/* 588 */         removed = f;
/* 589 */         System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
/*     */       }
/*     */     }
/* 592 */     return removed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getLongField(String name)
/*     */     throws NumberFormatException
/*     */   {
/* 605 */     HttpField field = getField(name);
/* 606 */     return field == null ? -1L : field.getLongValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getDateField(String name)
/*     */   {
/* 618 */     HttpField field = getField(name);
/* 619 */     if (field == null) {
/* 620 */       return -1L;
/*     */     }
/* 622 */     String val = valueParameters(field.getValue(), null);
/* 623 */     if (val == null) {
/* 624 */       return -1L;
/*     */     }
/* 626 */     long date = DateParser.parseDate(val);
/* 627 */     if (date == -1L)
/* 628 */       throw new IllegalArgumentException("Cannot convert date: " + val);
/* 629 */     return date;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putLongField(HttpHeader name, long value)
/*     */   {
/* 641 */     String v = Long.toString(value);
/* 642 */     put(name, v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putLongField(String name, long value)
/*     */   {
/* 653 */     String v = Long.toString(value);
/* 654 */     put(name, v);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putDateField(HttpHeader name, long date)
/*     */   {
/* 666 */     String d = DateGenerator.formatDate(date);
/* 667 */     put(name, d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putDateField(String name, long date)
/*     */   {
/* 678 */     String d = DateGenerator.formatDate(date);
/* 679 */     put(name, d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDateField(String name, long date)
/*     */   {
/* 690 */     String d = DateGenerator.formatDate(date);
/* 691 */     add(name, d);
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 697 */     int hash = 0;
/* 698 */     for (HttpField field : this._fields)
/* 699 */       hash += field.hashCode();
/* 700 */     return hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 706 */     if (this == o)
/* 707 */       return true;
/* 708 */     if (!(o instanceof HttpFields)) {
/* 709 */       return false;
/*     */     }
/* 711 */     HttpFields that = (HttpFields)o;
/*     */     
/*     */ 
/* 714 */     if (size() != that.size()) {
/* 715 */       return false;
/*     */     }
/* 717 */     Iterator localIterator1 = iterator(); if (localIterator1.hasNext()) { HttpField fi = (HttpField)localIterator1.next();
/*     */       
/* 719 */       Iterator localIterator2 = that.iterator(); for (;;) { if (!localIterator2.hasNext()) break label103; HttpField fa = (HttpField)localIterator2.next();
/*     */         
/* 721 */         if (fi.equals(fa)) break;
/*     */       }
/*     */       label103:
/* 724 */       return false;
/*     */     }
/* 726 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 734 */       StringBuilder buffer = new StringBuilder();
/* 735 */       for (HttpField field : this)
/*     */       {
/* 737 */         if (field != null)
/*     */         {
/* 739 */           String tmp = field.getName();
/* 740 */           if (tmp != null) buffer.append(tmp);
/* 741 */           buffer.append(": ");
/* 742 */           tmp = field.getValue();
/* 743 */           if (tmp != null) buffer.append(tmp);
/* 744 */           buffer.append("\r\n");
/*     */         }
/*     */       }
/* 747 */       buffer.append("\r\n");
/* 748 */       return buffer.toString();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 752 */       LOG.warn(e);
/* 753 */       return e.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/* 759 */     this._size = 0;
/*     */   }
/*     */   
/*     */   public void add(HttpField field)
/*     */   {
/* 764 */     if (field != null)
/*     */     {
/* 766 */       if (this._size == this._fields.length)
/* 767 */         this._fields = ((HttpField[])Arrays.copyOf(this._fields, this._size * 2));
/* 768 */       this._fields[(this._size++)] = field;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addAll(HttpFields fields)
/*     */   {
/* 774 */     for (int i = 0; i < fields._size; i++) {
/* 775 */       add(fields._fields[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(HttpFields fields)
/*     */   {
/* 786 */     if (fields == null) { return;
/*     */     }
/* 788 */     Enumeration<String> e = fields.getFieldNames();
/* 789 */     while (e.hasMoreElements())
/*     */     {
/* 791 */       String name = (String)e.nextElement();
/* 792 */       Enumeration<String> values = fields.getValues(name);
/* 793 */       while (values.hasMoreElements()) {
/* 794 */         add(name, (String)values.nextElement());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String stripParameters(String value)
/*     */   {
/* 813 */     if (value == null) { return null;
/*     */     }
/* 815 */     int i = value.indexOf(';');
/* 816 */     if (i < 0) return value;
/* 817 */     return value.substring(0, i).trim();
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
/*     */   public static String valueParameters(String value, Map<String, String> parameters)
/*     */   {
/* 836 */     if (value == null) { return null;
/*     */     }
/* 838 */     int i = value.indexOf(';');
/* 839 */     if (i < 0) return value;
/* 840 */     if (parameters == null) { return value.substring(0, i).trim();
/*     */     }
/* 842 */     StringTokenizer tok1 = new QuotedStringTokenizer(value.substring(i), ";", false, true);
/* 843 */     while (tok1.hasMoreTokens())
/*     */     {
/* 845 */       String token = tok1.nextToken();
/* 846 */       StringTokenizer tok2 = new QuotedStringTokenizer(token, "= ");
/* 847 */       if (tok2.hasMoreTokens())
/*     */       {
/* 849 */         String paramName = tok2.nextToken();
/* 850 */         String paramVal = null;
/* 851 */         if (tok2.hasMoreTokens()) paramVal = tok2.nextToken();
/* 852 */         parameters.put(paramName, paramVal);
/*     */       }
/*     */     }
/*     */     
/* 856 */     return value.substring(0, i).trim();
/*     */   }
/*     */   
/*     */   @Deprecated
/* 860 */   private static final Float __one = new Float("1.0");
/*     */   @Deprecated
/* 862 */   private static final Float __zero = new Float("0.0");
/*     */   @Deprecated
/* 864 */   private static final Trie<Float> __qualities = new ArrayTernaryTrie();
/*     */   
/*     */   static {
/* 867 */     __qualities.put("*", __one);
/* 868 */     __qualities.put("1.0", __one);
/* 869 */     __qualities.put("1", __one);
/* 870 */     __qualities.put("0.9", new Float("0.9"));
/* 871 */     __qualities.put("0.8", new Float("0.8"));
/* 872 */     __qualities.put("0.7", new Float("0.7"));
/* 873 */     __qualities.put("0.66", new Float("0.66"));
/* 874 */     __qualities.put("0.6", new Float("0.6"));
/* 875 */     __qualities.put("0.5", new Float("0.5"));
/* 876 */     __qualities.put("0.4", new Float("0.4"));
/* 877 */     __qualities.put("0.33", new Float("0.33"));
/* 878 */     __qualities.put("0.3", new Float("0.3"));
/* 879 */     __qualities.put("0.2", new Float("0.2"));
/* 880 */     __qualities.put("0.1", new Float("0.1"));
/* 881 */     __qualities.put("0", __zero);
/* 882 */     __qualities.put("0.0", __zero);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static Float getQuality(String value)
/*     */   {
/* 888 */     if (value == null) { return __zero;
/*     */     }
/* 890 */     int qe = value.indexOf(";");
/* 891 */     if ((qe++ < 0) || (qe == value.length())) { return __one;
/*     */     }
/* 893 */     if (value.charAt(qe++) == 'q')
/*     */     {
/* 895 */       qe++;
/* 896 */       Float q = (Float)__qualities.get(value, qe, value.length() - qe);
/* 897 */       if (q != null) {
/* 898 */         return q;
/*     */       }
/*     */     }
/* 901 */     Map<String, String> params = new HashMap(4);
/* 902 */     valueParameters(value, params);
/* 903 */     String qs = (String)params.get("q");
/* 904 */     if (qs == null)
/* 905 */       qs = "*";
/* 906 */     Float q = (Float)__qualities.get(qs);
/* 907 */     if (q == null)
/*     */     {
/*     */       try
/*     */       {
/* 911 */         q = new Float(qs);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 915 */         q = __one;
/*     */       }
/*     */     }
/* 918 */     return q;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public static List<String> qualityList(Enumeration<String> e)
/*     */   {
/* 930 */     if ((e == null) || (!e.hasMoreElements())) {
/* 931 */       return Collections.emptyList();
/*     */     }
/* 933 */     QuotedQualityCSV values = new QuotedQualityCSV(new String[0]);
/* 934 */     while (e.hasMoreElements())
/* 935 */       values.addValue((String)e.nextElement());
/* 936 */     return values.getValues();
/*     */   }
/*     */   
/*     */   private class Itr
/*     */     implements Iterator<HttpField>
/*     */   {
/*     */     int _cursor;
/* 943 */     int _last = -1;
/*     */     
/*     */     private Itr() {}
/*     */     
/* 947 */     public boolean hasNext() { return this._cursor != HttpFields.this._size; }
/*     */     
/*     */ 
/*     */     public HttpField next()
/*     */     {
/* 952 */       int i = this._cursor;
/* 953 */       if (i >= HttpFields.this._size)
/* 954 */         throw new NoSuchElementException();
/* 955 */       this._cursor = (i + 1);
/* 956 */       return HttpFields.this._fields[(this._last = i)];
/*     */     }
/*     */     
/*     */     public void remove()
/*     */     {
/* 961 */       if (this._last < 0) {
/* 962 */         throw new IllegalStateException();
/*     */       }
/* 964 */       System.arraycopy(HttpFields.this._fields, this._last + 1, HttpFields.this._fields, this._last, HttpFields.access$106(HttpFields.this) - this._last);
/* 965 */       this._cursor = this._last;
/* 966 */       this._last = -1;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */