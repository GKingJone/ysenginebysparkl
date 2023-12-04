package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract interface PeekingIterator<E>
  extends Iterator<E>
{
  public abstract E peek();
  
  public abstract E next();
  
  public abstract void remove();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\PeekingIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */