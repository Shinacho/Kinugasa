/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.resource;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * �A���S���Y���Ȃǂ̖����\�ȃI�u�W�F�N�g���i�[����}�b�v�ł�.
 * <br>
 * ���̃N���X�́ANameable�����������N���X��HashMap�ɓo�^���A�p�ӂɃA�N�Z�X�ł���悤�ɂ��܂��B<br>
 * �X�g���[�W�ɂ́A�������O�̃I�u�W�F�N�g��o�^���邱�Ƃ͏o���܂���B �X�g���[�W�̗e�ʂ́A�����I�Ɋg�傳��܂��B<br>
 * <br>
 * �Q�[�����A1�̏ꏊ��Nameable�̎�����ۑ��������ꍇ�́A ���̃N���X���p�����邱�ƂŁA�B��̕ۑ��̈���쐬���邱�Ƃ��o���܂��B<br>
 * ���̃N���X�́A�V���A���C�Y�\�ł͂���܂���B���̂悤�ȋ@�\�́A�T�u�N���X�� ��`����K�v������܂��B<br>
 * <br>
 *
 * @param <T> ���̃X�g���[�W���g�p���閽���\�ȃI�u�W�F�N�g���w�肵�܂��B<br>
 *
 * @version 1.0.0 - 2012/11/18_0:14:31<br>
 * @version 1.0.2 - 2013/01/12_22:16:16<br>
 * @version 1.1.0 - 2013/02/19_00:49<br>
 * @version 1.1.2 - 2013/04/13_19:31<br>
 * @version 2.0.0 - 2013/04/20_17:57<br>
 * @author Dra0211<br>
 */
public class Storage<T extends Nameable> implements Iterable<T> {

	/**
	 * T��ۊǂ���}�b�v�ł�.
	 */
	private HashMap<String, T> map;

	/**
	 * �V�����X�g���[�W���쐬���܂�.
	 */
	public Storage() {
		map = new HashMap<String, T>(32);
	}

	/**
	 * �V�����X�g���[�W���쐬���܂�.
	 *
	 * @param initialSize �}�b�v�̏����e�ʂ��w�肵�܂��B<br>
	 */
	public Storage(int initialSize) {
		map = new HashMap<String, T>(initialSize);
	}

	/**
	 * �w�肵�����O�̃I�u�W�F�N�g���擾���܂�.
	 *
	 * @param key �擾����I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 *
	 * @return �w�肵�����O�����I�u�W�F�N�g��Ԃ��܂��B<br>
	 *
	 * @throws NameNotFoundException ���݂��Ȃ����O���w�肵���ꍇ�ɓ������܂��B<br>
	 */
	public T get(String key) throws NameNotFoundException {
		if (!contains(key)) {
			throw new NameNotFoundException("! > Storage(" + getClass() + ") : get : not found : key=[" + key.toString() + "]");
		}
		return map.get(key);
	}

	/**
	 * �w�肵���L�[�̗v�f���܂܂�Ă���ꍇ�ɁA������擾���܂�.<br>
	 *
	 * @param key �擾����I�u�W�F�N�g�̃L�[���w�肵�܂��B<br>
	 *
	 * @return �w�肵���L�[�̃I�u�W�F�N�g���܂܂�Ă���΂�����A�܂܂�Ă��Ȃ����null��Ԃ��܂��B<br>
	 */
	public T getIfContains(String key) {
		return map.get(key);
	}

	/**
	 * ���̃X�g���[�W�ɒǉ�����Ă���I�u�W�F�N�g�����ׂĎ擾���܂�.
	 * ���̃��\�b�h�̖߂�l�͎Q�Ƃł͂���܂���B�V�����쐬���ꂽ�R���N�V�����ł��B<br>
	 *
	 * @return �ۊǂ���Ă��邷�ׂẴI�u�W�F�N�g�̃R���N�V������Ԃ��܂��B�R���N�V�����Ɋi�[����鏇�Ԃ�
	 * �X�g���[�W�ɒǉ����ꂽ���Ԃƈ�v���܂���B<br>
	 */
	public Collection<T> getAll() {
		return map.values();
	}

	/**
	 * ���̃X�g���[�W�ɒǉ�����Ă���I�u�W�F�N�g�����ׂĎ擾���܂�. ���̃��\�b�h�̖߂�l�͎Q�Ƃł͂���܂���B�V�����쐬���ꂽ���X�g�ł��B<br>
	 *
	 * @return �ۊǂ���Ă��邷�ׂẴI�u�W�F�N�g�̃��X�g��Ԃ��܂��B���X�g�Ɋi�[����鏇�Ԃ� �X�g���[�W�ɒǉ����ꂽ���Ԃƈ�v���܂���B<br>
	 */
	public List<T> asList() {
		return new ArrayList<T>(getAll());
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���i�[����Ă��邩�𒲂ׂ܂�.
	 *
	 * @param key ��������I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 *
	 * @return �w�肵�����O�̃I�u�W�F�N�g���܂܂�Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���A���ׂĊi�[����Ă��邩�𒲂ׂ܂�.
	 *
	 * @param keys ��������I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 *
	 * @return �w�肵�����O���S�Ċ܂܂�Ă���ꍇ�Ɍ���Atrue��Ԃ��܂��B<br>
	 */
	public boolean containsAll(String... keys) {
		for (String key : keys) {
			if (!contains(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �w�肵���I�u�W�F�N�g���i�[����Ă��邩�𒲂ׂ܂�.
	 *
	 * @param obj ��������I�u�W�F�N�g���w�肵�܂��B<br>
	 *
	 * @return �w�肵���I�u�W�F�N�g���܂܂�Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean contains(T obj) {
		return contains(obj.getName());
	}

	/**
	 * �V�����I�u�W�F�N�g���}�b�v�ɒǉ����܂�.
	 *
	 * @param val �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 *
	 * @throws DuplicateNameException val�̖��O�����Ɏg�p����Ă���Ƃ��ɓ������܂��B<br>
	 */
	public void add(T val) throws DuplicateNameException {
		if (contains(val.getName())) {
			throw new DuplicateNameException("! > Storage : add : duplicate name : name=[" + val.getName() + "]");
		}
		map.put(val.getName(), val);
	}

	/**
	 * �V�����I�u�W�F�N�g���}�b�v�ɒǉ����܂�.
	 *
	 * @param values �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 *
	 * @throws DuplicateNameException val�̖��O�����Ɏg�p����Ă���Ƃ��ɓ������܂��B<br>
	 */
	public void addAll(T... values) throws DuplicateNameException {
		addAll(Arrays.asList(values));
	}

	/**
	 * �V�����I�u�W�F�N�g���}�b�v�ɒǉ����܂�.
	 *
	 * @param values �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 *
	 * @throws DuplicateNameException val�̖��O�����Ɏg�p����Ă���Ƃ��ɓ������܂��B<br>
	 */
	public void addAll(Collection<? extends T> values) throws DuplicateNameException {
		for (T value : values) {
			add(value);
		}
	}

	/**
	 * �I�u�W�F�N�g���A�㏑���Œǉ����܂�. ���̃��\�b�h�͓������O�����I�u�W�F�N�g���o�^����Ă���ꍇ�ɏ㏑�����܂��B<br>
	 *
	 * @param val �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void put(T val) {
		map.put(val.getName(), val);
	}

	/**
	 * �����̃I�u�W�F�N�g���㏑���Œǉ����܂�.
	 *
	 * @param values �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void putAll(T... values) {
		putAll(Arrays.asList(values));
	}

	/**
	 * �����̃I�u�W�F�N�g���㏑���Œǉ����܂�.
	 *
	 * @param values �ǉ�����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void putAll(Collection<? extends T> values) {
		for (T value : values) {
			put(value);
		}
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���}�b�v����폜���܂�.
	 *
	 * @param key �폜����I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 */
	public void remove(String key) {
		map.remove(key);
	}

	/**
	 * �I�u�W�F�N�g���}�b�v����폜���܂�.
	 *
	 * @param val �폜����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void remove(T val) {
		remove(val.getName());
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���}�b�v����폜���܂�.
	 *
	 * @param keys �폜����I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 */
	public void removeAll(String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * �I�u�W�F�N�g���}�b�v����폜���܂�.
	 *
	 * @param values �폜����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void removeAll(T... values) {
		for (T key : values) {
			remove(key.getName());
		}
	}

	/**
	 * �I�u�W�F�N�g���}�b�v����폜���܂�.
	 *
	 * @param values �폜����I�u�W�F�N�g���w�肵�܂��B<br>
	 */
	public void removeAll(Collection<? extends T> values) {
		for (T key : values) {
			remove(key.getName());
		}
	}

	/**
	 * �}�b�v�ɒǉ�����Ă���I�u�W�F�N�g�̐����擾���܂�.
	 *
	 * @return �}�b�v�̗v�f����Ԃ��܂��B<br>
	 */
	public int size() {
		return map.size();
	}

	/**
	 * �}�b�v���炷�ׂẴI�u�W�F�N�g���폜���܂�.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * �}�b�v�̗v�f������ł��邩�𒲂ׂ܂�.
	 *
	 * @return �}�b�v����̏ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * ���ݕێ����Ă���S�ẴI�u�W�F�N�g���X�g���[���ɏo�͂��܂�. ���̃��\�b�h�̓f�o�b�O�p�ł��B<br>
	 *
	 * @param stream �����o���X�g���[�����w�肵�܂��B<br>
	 */
	public void printAll(PrintStream stream) {
		stream.println("> Storage : class=[" + getClass() + "]");
		for (T obj : map.values()) {
			stream.println("> Storage : printAll : " + obj.getName());
		}
		stream.println("> Storage : ------------------------");
	}

	/**
	 * ���ݕێ����Ă���S�ẴI�u�W�F�N�g���X�g���[���ɏo�͂��܂�. ���̃��\�b�h�̓f�o�b�O�p�ł��B<br>
	 *
	 * @param stream �����o���X�g���[�����w�肵�܂��B<br>
	 * @param valueOut true���w�肷��ƒl���o�͂��܂��B<br>
	 */
	public void printAll(PrintStream stream, boolean valueOut) {
		stream.println("> Storage : class=[" + getClass() + "]");
		for (T obj : map.values()) {
			stream.print("> Storage : printAll : " + obj.getName());
			if (valueOut) {
				stream.print(" : " + obj);
			}
			stream.println();
		}
		stream.println("> Storage : ------------------------");
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g��V�����}�b�v�Ɋi�[���ĕԂ��܂�. ���݂��Ȃ����O���w�肵���ꍇ�́A���̖��O�͖�������܂��B�߂�l��
	 * �}�b�v�ɂ́A���݂��m�F���ꂽ�I�u�W�F�N�g�������i�[����܂��B<br>
	 *
	 * @param names �߂�l�ɒǉ�����I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 *
	 * @return �w�肵�����O�����I�u�W�F�N�g��V�����}�b�v�Ɋi�[���ĕԂ��܂��B<br>
	 */
	public Map<String, T> getProperties(String... names) {
		Map<String, T> result = new HashMap<String, T>(names.length);
		for (String name : names) {
			if (contains(name)) {
				result.put(name, get(name));
			}
		}
		return result;
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	/**
	 * �S�Ă̗v�f���Q�Ƃł���C�e���[�^��Ԃ��܂�. �v�f�̏��Ԃ́AHashSet�Ɉˑ����܂��B���я���ݒ肷��K�v������ꍇ��
	 * asList���g�p���Ă��������B<br>
	 *
	 * @return ���̃X�g���[�W�̗v�f���Q�Ƃ���C�e���[�^��Ԃ��܂��B<br>
	 */
	@Override
	public Iterator<T> iterator() {
		return map.values().iterator();
	}

	public List<T> sort(Comparator<? super T> c) {
		List<T> list = new ArrayList<>(asList());
		Collections.sort(list, c);
		return list;
	}

	@Override
	public void forEach(Consumer<? super T> c) {
		asList().forEach(c);
	}

	@Override
	public String toString() {
		return "Storage{" + "map=" + map + '}';
	}
}
