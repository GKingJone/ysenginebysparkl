package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@Beta
@GwtCompatible(emulated=true)
public abstract interface SortedMultiset<E>
  extends SortedMultisetBridge<E>, SortedIterable<E>
{
  public abstract Comparator<? super E> comparator();
  
  public abstract Multiset.Entry<E> firstEntry();
  
  public abstract Multiset.Entry<E> lastEntry();
  
  public abstract Multiset.Entry<E> pollFirstEntry();
  
  public abstract Multiset.Entry<E> pollLastEntry();
  
  public abstract NavigableSet<E> elementSet();
  
  public abstract Iterator<E> iterator();
  
  public abstract SortedMultiset<E> descendingMultiset();
  
  public abstract SortedMultiset<E> headMultiset(E paramE, BoundType paramBoundType);
  
  public abstract SortedMultiset<E> subMultiset(E paramE1, BoundType paramBoundType1, E paramE2, BoundType paramBoundType2);
  
  public abstract SortedMultiset<E> tailMultiset(E paramE, BoundType paramBoundType);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\SortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */