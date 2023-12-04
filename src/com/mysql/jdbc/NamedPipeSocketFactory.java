/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NamedPipeSocketFactory
/*     */   implements SocketFactory, SocketMetadata
/*     */ {
/*     */   public static final String NAMED_PIPE_PROP_NAME = "namedPipePath";
/*     */   private Socket namedPipeSocket;
/*     */   
/*     */   class NamedPipeSocket
/*     */     extends Socket
/*     */   {
/*  43 */     private boolean isClosed = false;
/*     */     private RandomAccessFile namedPipeFile;
/*     */     
/*     */     NamedPipeSocket(String filePath) throws IOException
/*     */     {
/*  48 */       if ((filePath == null) || (filePath.length() == 0)) {
/*  49 */         throw new IOException(Messages.getString("NamedPipeSocketFactory.4"));
/*     */       }
/*     */       
/*  52 */       this.namedPipeFile = new RandomAccessFile(filePath, "rw");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public synchronized void close()
/*     */       throws IOException
/*     */     {
/*  60 */       this.namedPipeFile.close();
/*  61 */       this.isClosed = true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public InputStream getInputStream()
/*     */       throws IOException
/*     */     {
/*  69 */       return new RandomAccessFileInputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public OutputStream getOutputStream()
/*     */       throws IOException
/*     */     {
/*  77 */       return new RandomAccessFileOutputStream(NamedPipeSocketFactory.this, this.namedPipeFile);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isClosed()
/*     */     {
/*  85 */       return this.isClosed;
/*     */     }
/*     */   }
/*     */   
/*     */   class RandomAccessFileInputStream
/*     */     extends InputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */     
/*     */     RandomAccessFileInputStream(RandomAccessFile file)
/*     */     {
/*  96 */       this.raFile = file;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int available()
/*     */       throws IOException
/*     */     {
/* 104 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 112 */       this.raFile.close();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int read()
/*     */       throws IOException
/*     */     {
/* 120 */       return this.raFile.read();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int read(byte[] b)
/*     */       throws IOException
/*     */     {
/* 128 */       return this.raFile.read(b);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int read(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 136 */       return this.raFile.read(b, off, len);
/*     */     }
/*     */   }
/*     */   
/*     */   class RandomAccessFileOutputStream
/*     */     extends OutputStream
/*     */   {
/*     */     RandomAccessFile raFile;
/*     */     
/*     */     RandomAccessFileOutputStream(RandomAccessFile file)
/*     */     {
/* 147 */       this.raFile = file;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 155 */       this.raFile.close();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void write(byte[] b)
/*     */       throws IOException
/*     */     {
/* 163 */       this.raFile.write(b);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void write(byte[] b, int off, int len)
/*     */       throws IOException
/*     */     {
/* 171 */       this.raFile.write(b, off, len);
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
/*     */     public void write(int b)
/*     */       throws IOException
/*     */     {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Socket afterHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 197 */     return this.namedPipeSocket;
/*     */   }
/*     */   
/*     */ 
/*     */   public Socket beforeHandshake()
/*     */     throws SocketException, IOException
/*     */   {
/* 204 */     return this.namedPipeSocket;
/*     */   }
/*     */   
/*     */ 
/*     */   public Socket connect(String host, int portNumber, Properties props)
/*     */     throws SocketException, IOException
/*     */   {
/* 211 */     String namedPipePath = props.getProperty("namedPipePath");
/*     */     
/* 213 */     if (namedPipePath == null) {
/* 214 */       namedPipePath = "\\\\.\\pipe\\MySQL";
/* 215 */     } else if (namedPipePath.length() == 0) {
/* 216 */       throw new SocketException(Messages.getString("NamedPipeSocketFactory.2") + "namedPipePath" + Messages.getString("NamedPipeSocketFactory.3"));
/*     */     }
/*     */     
/* 219 */     this.namedPipeSocket = new NamedPipeSocket(namedPipePath);
/*     */     
/* 221 */     return this.namedPipeSocket;
/*     */   }
/*     */   
/*     */   public boolean isLocallyConnected(ConnectionImpl conn) throws SQLException
/*     */   {
/* 226 */     return true;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NamedPipeSocketFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */