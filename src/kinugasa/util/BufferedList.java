/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * 要素をバッファリングし、効率的に削除できるリストです.
 * <br>
 * このリストは、コミットされた時点で削除を行います。
 * 削除をする際には設定されたタイムカウンタのクロックが判定されます。<br>
 * 追加は直ちに行われます。<br>
 * <br>
 *
 * @param <T> リストの型を指定します。<br>
 *
 * @version 1.0.0 - 2013/02/10_0:06:34<br>
 * @author Shinacho<br>
 */
public final class BufferedList<T> implements List<T>, Cloneable, Serializable, RandomAccess {

	private static final long serialVersionUID = -8845399102208579167L;
	private ArrayList<T> list;
	private ArrayList<Object> removeList;
	private TimeCounter delayTime;

	public BufferedList() {
		this(512, TimeCounter.TRUE);
	}

	public BufferedList(int initialSize) {
		this(initialSize, TimeCounter.TRUE);
	}

	public BufferedList(int initialSize, TimeCounter delayTimeCounter) {
		list = new ArrayList<T>(initialSize);
		removeList = new ArrayList<Object>(initialSize);
		this.delayTime = delayTimeCounter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public BufferedList<T> clone() {
		try {
			BufferedList<T> result = (BufferedList<T>) super.clone();
			result.list = (ArrayList<T>) this.list.clone();
			result.removeList = (ArrayList<Object>) this.removeList.clone();
			result.delayTime = this.delayTime.clone();
			return result;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	public void addAll(T... obj) {
		addAll(Arrays.asList(obj));
	}

	public void remove(T... obj) {
		remove(Arrays.asList(obj));
	}

	public void remove(Collection<? extends T> obj) {
		removeList.addAll(obj);
	}

	public void remove() {
		list.removeAll(removeList);
		removeList.clear();
	}

	public void commit() {
		if (delayTime.isReaching()) {
			remove();
		}
	}

	public ArrayList<T> getList() {
		return list;
	}

	public void setList(ArrayList<T> list) {
		this.list = list;
	}

	public int listSize() {
		return list.size();
	}

	public ArrayList<Object> getRemoveList() {
		return removeList;
	}

	public void setRemoveList(ArrayList<Object> removeList) {
		this.removeList = removeList;
	}

	public int removeListSize() {
		return removeList.size();
	}

	public TimeCounter getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(TimeCounter delayTime) {
		this.delayTime = delayTime;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		return list.toArray(ts);
	}

	@Override
	public boolean add(T e) {
		list.add(e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		int removeListSize = removeListSize();
		removeList.add(o);
		return removeListSize != removeList.size();
	}

	@Override
	public boolean containsAll(Collection<?> clctn) {
		return list.containsAll(clctn);
	}

	@Override
	public boolean addAll(Collection<? extends T> clctn) {
		return list.addAll(clctn);
	}

	@Override
	public boolean addAll(int i, Collection<? extends T> clctn) {
		return list.addAll(i, clctn);
	}

	@Override
	public boolean removeAll(Collection<?> clctn) {
		int removeListSize = removeListSize();
		removeList.addAll(clctn);
		return removeListSize != removeList.size();
	}

	@Override
	public boolean retainAll(Collection<?> clctn) {
		int removeListSize = removeListSize();
		for (int i = 0, size = list.size(); i < size; i++) {
			if (!clctn.contains(get(i))) {
				remove(get(i));
			}
		}
		return removeListSize != removeList.size();
	}

	@Override
	public void clear() {
		list.clear();
	}

	public void clearAll() {
		list.clear();
		removeList.clear();
	}

	@Override
	public T get(int i) {
		return list.get(i);
	}

	@Override
	public T set(int i, T e) {
		return list.set(i, e);
	}

	@Override
	public void add(int i, T e) {
		list.add(i, e);
	}

	@Override
	public T remove(int i) {
		return list.remove(i);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int i) {
		return list.listIterator(i);
	}

	@Override
	public List<T> subList(int i, int i1) {
		return list.subList(i, i1);
	}

	@Override
	public String toString() {
		return "BufferedList{" + "list=" + list + ", removeList=" + removeList + ", delayTime=" + delayTime + '}';
	}
}
