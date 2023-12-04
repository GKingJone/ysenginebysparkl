/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.io.Writer;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import javax.xml.stream.XMLOutputFactory;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import javax.xml.transform.Result;
/*     */ import javax.xml.transform.Source;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMResult;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.sax.SAXResult;
/*     */ import javax.xml.transform.sax.SAXSource;
/*     */ import javax.xml.transform.stax.StAXResult;
/*     */ import javax.xml.transform.stax.StAXSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4MysqlSQLXML
/*     */   implements SQLXML
/*     */ {
/*     */   private XMLInputFactory inputFactory;
/*     */   private XMLOutputFactory outputFactory;
/*     */   private String stringRep;
/*     */   private ResultSetInternalMethods owningResultSet;
/*     */   private int columnIndexOfXml;
/*     */   private boolean fromResultSet;
/*  84 */   private boolean isClosed = false;
/*     */   
/*     */   private boolean workingWithResult;
/*     */   
/*     */   private DOMResult asDOMResult;
/*     */   
/*     */   private SAXResult asSAXResult;
/*     */   
/*     */   private SimpleSaxToReader saxToReaderConverter;
/*     */   
/*     */   private StringWriter asStringWriter;
/*     */   
/*     */   private ByteArrayOutputStream asByteArrayOutputStream;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   protected JDBC4MysqlSQLXML(ResultSetInternalMethods owner, int index, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/* 101 */     this.owningResultSet = owner;
/* 102 */     this.columnIndexOfXml = index;
/* 103 */     this.fromResultSet = true;
/* 104 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   protected JDBC4MysqlSQLXML(ExceptionInterceptor exceptionInterceptor) {
/* 108 */     this.fromResultSet = false;
/* 109 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public synchronized void free() throws SQLException {
/* 113 */     this.stringRep = null;
/* 114 */     this.asDOMResult = null;
/* 115 */     this.asSAXResult = null;
/* 116 */     this.inputFactory = null;
/* 117 */     this.outputFactory = null;
/* 118 */     this.owningResultSet = null;
/* 119 */     this.workingWithResult = false;
/* 120 */     this.isClosed = true;
/*     */   }
/*     */   
/*     */   public synchronized String getString() throws SQLException
/*     */   {
/* 125 */     checkClosed();
/* 126 */     checkWorkingWithResult();
/*     */     
/* 128 */     if (this.fromResultSet) {
/* 129 */       return this.owningResultSet.getString(this.columnIndexOfXml);
/*     */     }
/*     */     
/* 132 */     return this.stringRep;
/*     */   }
/*     */   
/*     */   private synchronized void checkClosed() throws SQLException {
/* 136 */     if (this.isClosed) {
/* 137 */       throw SQLError.createSQLException("SQLXMLInstance has been free()d", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void checkWorkingWithResult() throws SQLException {
/* 142 */     if (this.workingWithResult) {
/* 143 */       throw SQLError.createSQLException("Can't perform requested operation after getResult() has been called to write XML data", "S1009", this.exceptionInterceptor);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setString(String str)
/*     */     throws SQLException
/*     */   {
/* 174 */     checkClosed();
/* 175 */     checkWorkingWithResult();
/*     */     
/* 177 */     this.stringRep = str;
/* 178 */     this.fromResultSet = false;
/*     */   }
/*     */   
/*     */   public synchronized boolean isEmpty() throws SQLException {
/* 182 */     checkClosed();
/* 183 */     checkWorkingWithResult();
/*     */     
/* 185 */     if (!this.fromResultSet) {
/* 186 */       return (this.stringRep == null) || (this.stringRep.length() == 0);
/*     */     }
/*     */     
/* 189 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized InputStream getBinaryStream() throws SQLException {
/* 193 */     checkClosed();
/* 194 */     checkWorkingWithResult();
/*     */     
/* 196 */     return this.owningResultSet.getBinaryStream(this.columnIndexOfXml);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Reader getCharacterStream()
/*     */     throws SQLException
/*     */   {
/* 224 */     checkClosed();
/* 225 */     checkWorkingWithResult();
/*     */     
/* 227 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized <T extends Source> T getSource(Class<T> clazz)
/*     */     throws SQLException
/*     */   {
/* 276 */     checkClosed();
/* 277 */     checkWorkingWithResult();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 282 */     if ((clazz == null) || (clazz.equals(SAXSource.class)))
/*     */     {
/* 284 */       InputSource inputSource = null;
/*     */       
/* 286 */       if (this.fromResultSet) {
/* 287 */         inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */       } else {
/* 289 */         inputSource = new InputSource(new StringReader(this.stringRep));
/*     */       }
/*     */       
/* 292 */       return new SAXSource(inputSource); }
/* 293 */     if (clazz.equals(DOMSource.class)) {
/*     */       try {
/* 295 */         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
/* 296 */         builderFactory.setNamespaceAware(true);
/* 297 */         DocumentBuilder builder = builderFactory.newDocumentBuilder();
/*     */         
/* 299 */         InputSource inputSource = null;
/*     */         
/* 301 */         if (this.fromResultSet) {
/* 302 */           inputSource = new InputSource(this.owningResultSet.getCharacterStream(this.columnIndexOfXml));
/*     */         } else {
/* 304 */           inputSource = new InputSource(new StringReader(this.stringRep));
/*     */         }
/*     */         
/* 307 */         return new DOMSource(builder.parse(inputSource));
/*     */       } catch (Throwable t) {
/* 309 */         SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", this.exceptionInterceptor);
/* 310 */         sqlEx.initCause(t);
/*     */         
/* 312 */         throw sqlEx;
/*     */       }
/*     */     }
/* 315 */     if (clazz.equals(StreamSource.class)) {
/* 316 */       Reader reader = null;
/*     */       
/* 318 */       if (this.fromResultSet) {
/* 319 */         reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */       } else {
/* 321 */         reader = new StringReader(this.stringRep);
/*     */       }
/*     */       
/* 324 */       return new StreamSource(reader); }
/* 325 */     if (clazz.equals(StAXSource.class)) {
/*     */       try {
/* 327 */         Reader reader = null;
/*     */         
/* 329 */         if (this.fromResultSet) {
/* 330 */           reader = this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */         } else {
/* 332 */           reader = new StringReader(this.stringRep);
/*     */         }
/*     */         
/* 335 */         return new StAXSource(this.inputFactory.createXMLStreamReader(reader));
/*     */       } catch (XMLStreamException ex) {
/* 337 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", this.exceptionInterceptor);
/* 338 */         sqlEx.initCause(ex);
/*     */         
/* 340 */         throw sqlEx;
/*     */       }
/*     */     }
/* 343 */     throw SQLError.createSQLException("XML Source of type \"" + clazz.toString() + "\" Not supported.", "S1009", this.exceptionInterceptor);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized OutputStream setBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 367 */     checkClosed();
/* 368 */     checkWorkingWithResult();
/*     */     
/* 370 */     this.workingWithResult = true;
/*     */     
/* 372 */     return setBinaryStreamInternal();
/*     */   }
/*     */   
/*     */   private synchronized OutputStream setBinaryStreamInternal() throws SQLException {
/* 376 */     this.asByteArrayOutputStream = new ByteArrayOutputStream();
/*     */     
/* 378 */     return this.asByteArrayOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Writer setCharacterStream()
/*     */     throws SQLException
/*     */   {
/* 406 */     checkClosed();
/* 407 */     checkWorkingWithResult();
/*     */     
/* 409 */     this.workingWithResult = true;
/*     */     
/* 411 */     return setCharacterStreamInternal();
/*     */   }
/*     */   
/*     */   private synchronized Writer setCharacterStreamInternal() throws SQLException {
/* 415 */     this.asStringWriter = new StringWriter();
/*     */     
/* 417 */     return this.asStringWriter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized <T extends Result> T setResult(Class<T> clazz)
/*     */     throws SQLException
/*     */   {
/* 464 */     checkClosed();
/* 465 */     checkWorkingWithResult();
/*     */     
/* 467 */     this.workingWithResult = true;
/* 468 */     this.asDOMResult = null;
/* 469 */     this.asSAXResult = null;
/* 470 */     this.saxToReaderConverter = null;
/* 471 */     this.stringRep = null;
/* 472 */     this.asStringWriter = null;
/* 473 */     this.asByteArrayOutputStream = null;
/*     */     
/* 475 */     if ((clazz == null) || (clazz.equals(SAXResult.class))) {
/* 476 */       this.saxToReaderConverter = new SimpleSaxToReader();
/*     */       
/* 478 */       this.asSAXResult = new SAXResult(this.saxToReaderConverter);
/*     */       
/* 480 */       return this.asSAXResult; }
/* 481 */     if (clazz.equals(DOMResult.class))
/*     */     {
/* 483 */       this.asDOMResult = new DOMResult();
/* 484 */       return this.asDOMResult;
/*     */     }
/* 486 */     if (clazz.equals(StreamResult.class))
/* 487 */       return new StreamResult(setCharacterStreamInternal());
/* 488 */     if (clazz.equals(StAXResult.class)) {
/*     */       try {
/* 490 */         if (this.outputFactory == null) {
/* 491 */           this.outputFactory = XMLOutputFactory.newInstance();
/*     */         }
/*     */         
/* 494 */         return new StAXResult(this.outputFactory.createXMLEventWriter(setCharacterStreamInternal()));
/*     */       } catch (XMLStreamException ex) {
/* 496 */         SQLException sqlEx = SQLError.createSQLException(ex.getMessage(), "S1009", this.exceptionInterceptor);
/* 497 */         sqlEx.initCause(ex);
/*     */         
/* 499 */         throw sqlEx;
/*     */       }
/*     */     }
/* 502 */     throw SQLError.createSQLException("XML Result of type \"" + clazz.toString() + "\" Not supported.", "S1009", this.exceptionInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Reader binaryInputStreamStreamToReader(ByteArrayOutputStream out)
/*     */   {
/*     */     try
/*     */     {
/* 513 */       String encoding = "UTF-8";
/*     */       try
/*     */       {
/* 516 */         ByteArrayInputStream bIn = new ByteArrayInputStream(out.toByteArray());
/* 517 */         XMLStreamReader reader = this.inputFactory.createXMLStreamReader(bIn);
/*     */         
/* 519 */         int eventType = 0;
/*     */         
/* 521 */         while ((eventType = reader.next()) != 8) {
/* 522 */           if (eventType == 7) {
/* 523 */             String possibleEncoding = reader.getEncoding();
/*     */             
/* 525 */             if (possibleEncoding != null) {
/* 526 */               encoding = possibleEncoding;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable localThrowable) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 536 */       return new StringReader(new String(out.toByteArray(), encoding));
/*     */     } catch (UnsupportedEncodingException badEnc) {
/* 538 */       throw new RuntimeException(badEnc);
/*     */     }
/*     */   }
/*     */   
/*     */   protected String readerToString(Reader reader) throws SQLException {
/* 543 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 545 */     int charsRead = 0;
/*     */     
/* 547 */     char[] charBuf = new char['Ȁ'];
/*     */     try
/*     */     {
/* 550 */       while ((charsRead = reader.read(charBuf)) != -1) {
/* 551 */         buf.append(charBuf, 0, charsRead);
/*     */       }
/*     */     } catch (IOException ioEx) {
/* 554 */       SQLException sqlEx = SQLError.createSQLException(ioEx.getMessage(), "S1009", this.exceptionInterceptor);
/* 555 */       sqlEx.initCause(ioEx);
/*     */       
/* 557 */       throw sqlEx;
/*     */     }
/*     */     
/* 560 */     return buf.toString();
/*     */   }
/*     */   
/*     */   protected synchronized Reader serializeAsCharacterStream() throws SQLException {
/* 564 */     checkClosed();
/* 565 */     if (this.workingWithResult)
/*     */     {
/* 567 */       if (this.stringRep != null) {
/* 568 */         return new StringReader(this.stringRep);
/*     */       }
/*     */       
/* 571 */       if (this.asDOMResult != null) {
/* 572 */         return new StringReader(domSourceToString());
/*     */       }
/*     */       
/* 575 */       if (this.asStringWriter != null) {
/* 576 */         return new StringReader(this.asStringWriter.toString());
/*     */       }
/*     */       
/* 579 */       if (this.asSAXResult != null) {
/* 580 */         return this.saxToReaderConverter.toReader();
/*     */       }
/*     */       
/* 583 */       if (this.asByteArrayOutputStream != null) {
/* 584 */         return binaryInputStreamStreamToReader(this.asByteArrayOutputStream);
/*     */       }
/*     */     }
/*     */     
/* 588 */     return this.owningResultSet.getCharacterStream(this.columnIndexOfXml);
/*     */   }
/*     */   
/*     */   protected String domSourceToString() throws SQLException {
/*     */     try {
/* 593 */       DOMSource source = new DOMSource(this.asDOMResult.getNode());
/* 594 */       Transformer identity = TransformerFactory.newInstance().newTransformer();
/* 595 */       StringWriter stringOut = new StringWriter();
/* 596 */       Result result = new StreamResult(stringOut);
/* 597 */       identity.transform(source, result);
/*     */       
/* 599 */       return stringOut.toString();
/*     */     } catch (Throwable t) {
/* 601 */       SQLException sqlEx = SQLError.createSQLException(t.getMessage(), "S1009", this.exceptionInterceptor);
/* 602 */       sqlEx.initCause(t);
/*     */       
/* 604 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   protected synchronized String serializeAsString() throws SQLException {
/* 609 */     checkClosed();
/* 610 */     if (this.workingWithResult)
/*     */     {
/* 612 */       if (this.stringRep != null) {
/* 613 */         return this.stringRep;
/*     */       }
/*     */       
/* 616 */       if (this.asDOMResult != null) {
/* 617 */         return domSourceToString();
/*     */       }
/*     */       
/* 620 */       if (this.asStringWriter != null) {
/* 621 */         return this.asStringWriter.toString();
/*     */       }
/*     */       
/* 624 */       if (this.asSAXResult != null) {
/* 625 */         return readerToString(this.saxToReaderConverter.toReader());
/*     */       }
/*     */       
/* 628 */       if (this.asByteArrayOutputStream != null) {
/* 629 */         return readerToString(binaryInputStreamStreamToReader(this.asByteArrayOutputStream));
/*     */       }
/*     */     }
/*     */     
/* 633 */     return this.owningResultSet.getString(this.columnIndexOfXml);
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
/*     */   class SimpleSaxToReader
/*     */     extends DefaultHandler
/*     */   {
/*     */     SimpleSaxToReader() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 658 */     StringBuilder buf = new StringBuilder();
/*     */     
/*     */     public void startDocument() throws SAXException {
/* 661 */       this.buf.append("<?xml version='1.0' encoding='UTF-8'?>"); }
/*     */     
/*     */     public void endDocument()
/*     */       throws SAXException
/*     */     {}
/*     */     
/*     */     public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
/*     */       throws SAXException
/*     */     {
/* 670 */       this.buf.append("<");
/* 671 */       this.buf.append(qName);
/*     */       
/* 673 */       if (attrs != null) {
/* 674 */         for (int i = 0; i < attrs.getLength(); i++) {
/* 675 */           this.buf.append(" ");
/* 676 */           this.buf.append(attrs.getQName(i)).append("=\"");
/* 677 */           escapeCharsForXml(attrs.getValue(i), true);
/* 678 */           this.buf.append("\"");
/*     */         }
/*     */       }
/*     */       
/* 682 */       this.buf.append(">");
/*     */     }
/*     */     
/*     */     public void characters(char[] buf, int offset, int len) throws SAXException {
/* 686 */       if (!this.inCDATA) {
/* 687 */         escapeCharsForXml(buf, offset, len, false);
/*     */       } else {
/* 689 */         this.buf.append(buf, offset, len);
/*     */       }
/*     */     }
/*     */     
/*     */     public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
/* 694 */       characters(ch, start, length);
/*     */     }
/*     */     
/* 697 */     private boolean inCDATA = false;
/*     */     
/*     */     public void startCDATA() throws SAXException {
/* 700 */       this.buf.append("<![CDATA[");
/* 701 */       this.inCDATA = true;
/*     */     }
/*     */     
/*     */     public void endCDATA() throws SAXException {
/* 705 */       this.inCDATA = false;
/* 706 */       this.buf.append("]]>");
/*     */     }
/*     */     
/*     */     public void comment(char[] ch, int start, int length) throws SAXException
/*     */     {
/* 711 */       this.buf.append("<!--");
/* 712 */       for (int i = 0; i < length; i++) {
/* 713 */         this.buf.append(ch[(start + i)]);
/*     */       }
/* 715 */       this.buf.append("-->");
/*     */     }
/*     */     
/*     */     Reader toReader()
/*     */     {
/* 720 */       return new StringReader(this.buf.toString());
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(String str, boolean isAttributeData) {
/* 724 */       if (str == null) {
/* 725 */         return;
/*     */       }
/*     */       
/* 728 */       int strLen = str.length();
/*     */       
/* 730 */       for (int i = 0; i < strLen; i++) {
/* 731 */         escapeCharsForXml(str.charAt(i), isAttributeData);
/*     */       }
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(char[] buf, int offset, int len, boolean isAttributeData)
/*     */     {
/* 737 */       if (buf == null) {
/* 738 */         return;
/*     */       }
/*     */       
/* 741 */       for (int i = 0; i < len; i++) {
/* 742 */         escapeCharsForXml(buf[(offset + i)], isAttributeData);
/*     */       }
/*     */     }
/*     */     
/*     */     private void escapeCharsForXml(char c, boolean isAttributeData) {
/* 747 */       switch (c) {
/*     */       case '<': 
/* 749 */         this.buf.append("&lt;");
/* 750 */         break;
/*     */       
/*     */       case '>': 
/* 753 */         this.buf.append("&gt;");
/* 754 */         break;
/*     */       
/*     */       case '&': 
/* 757 */         this.buf.append("&amp;");
/* 758 */         break;
/*     */       
/*     */ 
/*     */       case '"': 
/* 762 */         if (!isAttributeData) {
/* 763 */           this.buf.append("\"");
/*     */         } else {
/* 765 */           this.buf.append("&quot;");
/*     */         }
/*     */         
/* 768 */         break;
/*     */       
/*     */       case '\r': 
/* 771 */         this.buf.append("&#xD;");
/* 772 */         break;
/*     */       
/*     */ 
/*     */       default: 
/* 776 */         if (((c >= '\001') && (c <= '\037') && (c != '\t') && (c != '\n')) || ((c >= '') && (c <= '')) || (c == ' ') || ((isAttributeData) && ((c == '\t') || (c == '\n'))))
/*     */         {
/* 778 */           this.buf.append("&#x");
/* 779 */           this.buf.append(Integer.toHexString(c).toUpperCase());
/* 780 */           this.buf.append(";");
/*     */         } else {
/* 782 */           this.buf.append(c);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4MysqlSQLXML.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */