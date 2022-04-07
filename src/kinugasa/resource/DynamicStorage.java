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

import java.util.Collection;
import java.util.function.Consumer;
import kinugasa.util.ArrayUtil;

/**
 * �v�f�̃��[�h�^�J���@�\��ǉ������X�g���[�W�̎����ł�.
 * <br>
 * ���̃X�g���[�W�̊g���́AFreeable���������܂��BFreeable�̋@�\�́A�S�Ă̗v�f�ɓK�p����܂��B
 * isLoaded��1�ȏ�̗v�f�����[�h����Ă���ꍇ��true��Ԃ��܂��B�S�Ă̗v�f�����[�h����Ă��邩����������ɂ�
 * isLoadedAll���g�p���܂��B<br>
 * <br>
 *
 * @param <T> ���̃X�g���[�W���ۑ����閽���\�ŊJ���\�Ȍ^���w�肵�܂��B<br>
 *
 * @version 1.0.0 - 2012/11/18_0:14:31<br>
 * @version 1.0.2 - 2013/01/12_22:16:16<br>
 * @version 1.1.0 - 2013/02/19_00:49<br>
 * @version 1.1.2 - 2013/04/13_19:31<br>
 * @version 1.4.0 - 2013/04/28_23:40<br>
 * @author Dra0211<br>
 */
public abstract class DynamicStorage<T extends Nameable & Input>
		extends Storage<T> implements Input {

	/**
	 * �V�����X�g���[�W���쐬���܂�.
	 *
	 * @param initialSize �X�g���[�W�̏����e�ʂ��w�肵�܂��B<br>
	 */
	public DynamicStorage(int initialSize) {
		super(initialSize);
	}

	/**
	 * �V�����X�g���[�W���쐬���܂�.
	 */
	public DynamicStorage() {
	}

	/**
	 * �S�Ă̗v�f�����[�h���܂�.
	 *
	 * @return ���̃X�g���[�W��Ԃ��܂��B<br>
	 */
	@Override
	public DynamicStorage<T> load() {
		for (T obj : this) {
			obj.load();
		}
		return this;
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���A���[�h���Ă���擾���܂�.
	 *
	 * @param name �I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 * @return ���[�h���ꂽ�I�u�W�F�N�g��Ԃ��܂��B<br>
	 * @throws NameNotFoundException �w�肵�����O�����I�u�W�F�N�g�����̃X�g���[�W�Ɋ܂܂�Ă��Ȃ�
	 * ���ɓ������܂��B<br>
	 */
	public T load(String name) throws NameNotFoundException {
		T obj = get(name);
		obj.load();
		return obj;
	}

	/**
	 * �S�Ă̗v�f���J�����܂�.
	 *
	 */
	@Override
	public void dispose() {
		for (T obj : this) {
			obj.dispose();
		}
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���A�J������擾���܂�.
	 *
	 * @param name �I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 * @return �J�����ꂽ�I�u�W�F�N�g��Ԃ��܂��B<br>
	 * @throws NameNotFoundException �w�肵�����O�����I�u�W�F�N�g�����̃X�g���[�W�Ɋ܂܂�Ă��Ȃ�
	 * ���ɓ������܂��B<br>
	 */
	public T dispose(String name) throws NameNotFoundException {
		T obj = get(name);
		obj.dispose();
		return obj;
	}

	/**
	 * �w�肳�ꂽ�S�Ă̗v�f�����[�h���܂�.
	 *
	 * @param names ���[�h����v�f�̖��O���w�肵�܂��B<br>
	 */
	public void loadAll(String... names) {
		for (T obj : this) {
			if (ArrayUtil.contains(names, obj.getName())) {
				obj.load();
			}
		}
	}

	/**
	 * �w�肳�ꂽ�S�Ă̗v�f���J�����܂�.
	 *
	 * @param names �J������v�f�̖��O���w�肵�܂��B<br>
	 */
	public void freeAll(String... names) {
		for (T obj : this) {
			if (ArrayUtil.contains(names, obj.getName())) {
				obj.dispose();
			}
		}
	}

	/**
	 * �w�肳�ꂽ���O�����I�u�W�F�N�g�ȊO��S�ĊJ�����܂�.
	 *
	 * @param names �J�����Ȃ��I�u�W�F�N�g�̖��O�𑗐M���܂��B<br>
	 */
	public void exFree(String... names) {
		for (T obj : this) {
			if (!ArrayUtil.contains(names, obj.getName())) {
				obj.dispose();
			}
		}
	}

	@Override
	public InputStatus getStatus() {
		return isEmpty() ? InputStatus.NOT_LOADED : InputStatus.LOADED;
	}

	/**
	 * �S�Ă̗v�f�����[�h����Ă��邩���������܂�.
	 *
	 * @return �S�Ă̗v�f�����[�h����Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isLoadedAll() {
		for (T obj : this) {
			if (!(obj.getStatus() == InputStatus.LOADED)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �w�肵�����O�����I�u�W�F�N�g���A���[�h����Ă��邩�𒲂ׂ܂�.
	 *
	 * @param name �I�u�W�F�N�g�̖��O���w�肵�܂��B<br>
	 * @return �w�肵�����O�����I�u�W�F�N�g��isLoaded��Ԃ��܂��B<br>
	 * @throws NameNotFoundException �w�肵�����O�����I�u�W�F�N�g�����̃X�g���[�W�Ɋ܂܂�Ă��Ȃ�
	 * ���ɓ������܂��B<br>
	 */
	public boolean isLoaded(String name) throws NameNotFoundException {
		return contains(name) ? get(name).getStatus() == InputStatus.LOADED : false;
	}

	@Override
	public void forEach(Consumer<? super T> c) {
		super.forEach(c); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addAll(Collection<? extends T> values) throws DuplicateNameException {
		super.addAll(values); //To change body of generated methods, choose Tools | Templates.
	}

	
}
