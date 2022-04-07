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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import kinugasa.game.GameLog;

/**
 * �Z�[�u�\�ȃR���e���c��IO�@�\��񋟂��܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_23:39:38<br>
 * @author Dra0211<br>
 */
public final class ContentsIO {

	private ContentsIO() {
	}

	/**
	 * �R���e���c���w�肳�ꂽ�t�@�C���p�X�ɔ��s���܂�.
	 *
	 * @param <T> ���s�\�ȃN���X���w�肵�܂��B<br>
	 * @param obj ���s����I�u�W�F�N�g���w�肵�܂��B<br>
	 * @param filePath ���s����t�@�C���p�X���w�肵�܂��B<br>
	 *
	 * @throws FileNotFoundException ���s�ł��Ȃ��t�@�C���p�X���w�肵���ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException ���̑��̗��R�ɂ���Ĕ��s�ł��Ȃ������ꍇ�ɓ������܂��B <br>
	 */
	public static <T extends Serializable> void save(T obj, String filePath)
			throws FileNotFoundException, ContentsIOException {
		save(obj, new File(filePath));
	}

	/**
	 * �R���e���c���w�肳�ꂽ�t�@�C���ɔ��s���܂�.
	 *
	 * @param <T> ���s�\�ȃN���X���w�肵�܂��B<br>
	 * @param obj ���s����I�u�W�F�N�g���w�肵�܂��B<br>
	 * @param file ���s����t�@�C�����w�肵�܂��B<br>
	 *
	 * @throws FileNotFoundException ���s�ł��Ȃ��t�@�C���p�X���w�肵���ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException ���̑��̗��R�ɂ���Ĕ��s�ł��Ȃ������ꍇ�ɓ������܂��B <br>
	 */
	public static <T extends Serializable> void save(T obj, File file)
			throws FileNotFoundException, ContentsIOException {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(file));
			stream.writeObject(obj);
		} catch (java.io.FileNotFoundException ex) {
			throw new FileNotFoundException(ex);
		} catch (IOException ex) {
			throw new ContentsIOException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new ContentsIOException(ex);
			} finally {
				GameLog.printInfoIfUsing("> ContentsIO : save : obj=[" + obj + "] file=[" + file.getPath() + "]");
			}
		}
	}

	/**
	 * �R���e���c���w�肳�ꂽ�t�@�C���p�X����ǂݍ��݁A��Ԃ𕜌����܂�.
	 *
	 * @param <T> ���s�\�ȃN���X���w�肵�܂��B<br>
	 * @param type �ǂݍ��ތ^���w�肵�܂��B<br>
	 * @param filePath �ǂݍ��ރt�@�C���p�X���w�肵�܂��B<br>
	 *
	 * @return �ǂݍ��܂ꂽ�R���e���c��Ԃ��܂��B<br>
	 *
	 * @throws FileNotFoundException �ǂݍ��߂Ȃ��t�@�C���p�X���w�肵���ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException ���̑��̗��R�ɂ���Ĕ��s�ł��Ȃ������ꍇ�ɓ������܂��B
	 * �^T���ǂݍ��܂ꂽ���ۂ̌^�ƈقȂ�ꍇ���������܂��B<br>
	 */
	public static <T extends Serializable> T load(Class<T> type, String filePath)
			throws FileNotFoundException, ContentsIOException {
		return load(type, new File(filePath));
	}

	/**
	 * �R���e���c���w�肳�ꂽ�t�@�C���p�X����ǂݍ��݁A��Ԃ𕜌����܂�.
	 *
	 * @param <T> ���s�\�ȃN���X���w�肵�܂��B<br>
	 * @param type �ǂݍ��ތ^���w�肵�܂��B<br>
	 * @param file �ǂݍ��ރt�@�C���p�X���w�肵�܂��B<br>
	 *
	 * @return �ǂݍ��܂ꂽ�R���e���c��Ԃ��܂��B<br>
	 *
	 * @throws FileNotFoundException �ǂݍ��߂Ȃ��t�@�C���p�X���w�肵���ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException ���̑��̗��R�ɂ���Ĕ��s�ł��Ȃ������ꍇ�ɓ������܂��B
	 * �^T���ǂݍ��܂ꂽ���ۂ̌^�ƈقȂ�ꍇ���������܂��B<br>
	 */
	public static <T extends Serializable> T load(Class<T> type, File file)
			throws FileNotFoundException, ContentsIOException {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(file));
			return type.cast(stream.readObject());
		} catch (java.io.FileNotFoundException ex) {
			throw new FileNotFoundException(ex);
		} catch (IOException ex) {
			throw new ContentsIOException(ex);
		} catch (ClassNotFoundException ex) {
			throw new ContentsIOException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new ContentsIOException(ex);
			} finally {
				GameLog.printInfoIfUsing("> ContentsIO : load : type=[" + type + "] file=[" + file.getPath() + "]");
			}
		}
	}
}
