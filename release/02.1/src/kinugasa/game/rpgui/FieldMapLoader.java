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
package kinugasa.game.rpgui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;

/**
 * �t�B�[���h�}�b�v�Ɋւ���l�X�ȃt�@�C�������[�h���܂�.
 * <br>
 * ���̃N���X�𗘗p���āA�l�X��XML�t�@�C���𐳂������ԂŃ��[�h�ł��܂��B<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_10:57:37<br>
 * @author Dra0211<br>
 */
public final class FieldMapLoader {

	/**
	 * �V����FieldMapLoader���쐬���܂�.
	 */
	public FieldMapLoader() {
		attributeFileList = new ArrayList<String>();
		vehicleFileList = new ArrayList<String>();
		chipSetFileList = new ArrayList<String>();
		fieldMapBuiderFileList = new ArrayList<String>();
	}
	/** �`�b�v�����t�@�C���̃p�X. */
	private List<String> attributeFileList;
	/** �ړ���i�t�@�C���̃p�X. */
	private List<String> vehicleFileList;
	/** �`�b�v�Z�b�g�t�@�C���̃p�X. */
	private List<String> chipSetFileList;
	/** �t�B�[���h�}�b�v�r�����̃t�@�C���p�X. */
	private List<String> fieldMapBuiderFileList;

	/**
	 * �`�b�v�����t�@�C����ǉ����܂�.
	 * @param filePath �t�@�C���p�X���w�肵�܂��B<br>
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public FieldMapLoader attribute(String filePath) {
		attributeFileList.add(filePath);
		return this;
	}

	/**
	 * �ړ���i�t�@�C����ǉ����܂�.
	 * @param filePath �t�@�C���p�X���w�肵�܂��B<br>
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public FieldMapLoader vehicle(String filePath) {
		vehicleFileList.add(filePath);
		return this;
	}

	/**
	 * �`�b�v�Z�b�g�t�@�C����ǉ����܂�.
	 * @param filePath �t�@�C���p�X���w�肵�܂��B<br>
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public FieldMapLoader chipSet(String filePath) {
		chipSetFileList.add(filePath);
		return this;
	}

	/**
	 * �t�B�[���h�}�b�v�r���_�t�@�C����ǉ����܂�.
	 * @param filePath �t�@�C���p�X���w�肵�܂��B<br>
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public FieldMapLoader fieldMapBuilder(String filePath) {
		fieldMapBuiderFileList.add(filePath);
		return this;
	}

	/**
	 * �S�Ẵt�@�C�������[�h���܂�.
	 * @throws IllegalXMLFormatException XML�t�@�C���t�H�[�}�b�g�Ɋւ����O�ł��B<br>
	 * @throws ContentsFileNotFoundException �w�肳�ꂽ�t�@�C�������݂��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException �t�@�C�������[�h�ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws NumberFormatException ���l���ϊ��ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws DuplicateNameException ��ӓI�łȂ���΂Ȃ�Ȃ����O���d�������ۂɓ������܂��B<br>
	 */
	public void load()
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, NumberFormatException,
			DuplicateNameException {
		load(null);
	}

	/**
	 * �S�Ẵt�@�C�������[�h���܂�.
	 * @param stream null�łȂ��ꍇ�A�ǉ����ꂽ�I�u�W�F�N�g�̏�񂪑��M����܂��B<br>
	 * @throws IllegalXMLFormatException XML�t�@�C���t�H�[�}�b�g�Ɋւ����O�ł��B<br>
	 * @throws ContentsFileNotFoundException �w�肳�ꂽ�t�@�C�������݂��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException �t�@�C�������[�h�ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws NumberFormatException ���l���ϊ��ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws DuplicateNameException ��ӓI�łȂ���΂Ȃ�Ȃ����O���d�������ۂɓ������܂��B<br>
	 */
	public void load(PrintStream stream)
			throws IllegalXMLFormatException,FileNotFoundException,
			ContentsIOException, NumberFormatException,
			DuplicateNameException {

		for (int i = 0, size = attributeFileList.size(); i < size; i++) {
			ChipAttributeStorage.getInstance().readFromXML(attributeFileList.get(i));
		}
		for (int i = 0, size = vehicleFileList.size(); i < size; i++) {
			VehicleStorage.getInstance().readFromXML(vehicleFileList.get(i));
		}
		for (int i = 0, size = chipSetFileList.size(); i < size; i++) {
			ChipSetStorage.getInstance().readFromXML(chipSetFileList.get(i));
		}
		for (int i = 0, size = fieldMapBuiderFileList.size(); i < size; i++) {
			FieldMapBuilderStorage.getInstance().readFromXML(fieldMapBuiderFileList.get(i));
		}

		if (stream != null) {
			ChipAttributeStorage.getInstance().printAll(stream, true);
			VehicleStorage.getInstance().printAll(stream, true);
			ChipSetStorage.getInstance().printAll(stream, true);
			FieldMapBuilderStorage.getInstance().printAll(stream, true);
			stream.println("> " + FieldMapBuilderStorage.getInstance());
		}
	}

	@Override
	public String toString() {
		return "FieldMapLoader{" + "attributeFileList=" + attributeFileList
				+ ", vehicleFileList=" + vehicleFileList
				+ ", chipSetFileList=" + chipSetFileList + '}';
	}
}
