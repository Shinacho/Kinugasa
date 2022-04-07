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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 * "�ړ���i"�܂���"��蕨"�N���X�́A�}�b�v�`�b�v�ɑ΂���ړ��̉ۂ𔻒肵�܂�.
 * <br>
 * �ړ���i�́u�ړ��\�ȃ`�b�v�����v�������܂��B<br>
 * �}�b�v��ŃL�����N�^���ړ����悤�Ƃ���ƁA���ɏ��}�b�v�`�b�v�̎��� �����ƁA���݂̈ړ���i�iVehicleStorage.currentVehicle�j�̌������s���܂��B<br>
 * <br>
 * �ړ���i�́A���̈ړ���i���̂̈ړ����x�������܂��B<br>
 * �}�b�v�̃X�N���[�����x�́A�u�}�b�v���̂̈ړ����x�~�ړ���i�̈ړ����x�v���g�p����܂��B
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_21:43:17<br>
 * @author Dra0211<br>
 */
public class Vehicle implements Nameable, Serializable, Comparable<Vehicle> {

	private static final long serialVersionUID = -5624691892456676247L;
	/**
	 * �ړ���i�̈�ӓI�Ȗ��O�ł�.
	 */
	private String name;
	/**
	 * ���̈ړ���i���}�b�v���X�N���[�������鑬�x�ł�. �ړ����x�́u�}�b�v���̂̈ړ����x�~�ړ���i�̈ړ����x�v���g�p����܂��B
	 */
	private float speed;
	/**
	 * ���̈ړ���i���ړ��ł��鑮���ł�.
	 */
	private Storage<ChipAttribute> attributeStorage;

	/**
	 * �V�����ړ���i���쐬���܂�. �쐬���ꂽ�ړ���i�́A�����I��VehicleStorage�ɒǉ�����܂��B<br>
	 *
	 * @param name �ړ���i�̈�ӓI�Ȗ��O���w�肵�܂��B<br>
	 * @param speed �ړ���i�̈ړ����x���w�肵�܂��B1.0f���w�肷��ƁA �}�b�v�ɐݒ肳�ꂽ�ړ����x�ňړ����܂��B<br>
	 * @param attributeSet ���̈ړ���i���ړ��ł���`�b�v�����𑗐M���܂��B<br>
	 * @throws DuplicateNameException ���̈ړ���i�̖��O�����łɎg�p����Ă���Ƃ��� �������܂��B<br>
	 */
	public Vehicle(String name, float speed, ChipAttribute... attributeSet)
			throws DuplicateNameException {
		this(name, speed, Arrays.asList(attributeSet));
	}

	/**
	 * �V�����ړ���i���쐬���܂�. �쐬���ꂽ�ړ���i�́A�����I��VehicleStorage�ɒǉ�����܂��B<br>
	 *
	 * @param name �ړ���i�̈�ӓI�Ȗ��O���w�肵�܂��B<br>
	 * @param speed �ړ���i�̈ړ����x���w�肵�܂��B1.0f���w�肷��ƁA �}�b�v�ɐݒ肳�ꂽ�ړ����x�ňړ����܂��B<br>
	 * @param attributeSet ���̈ړ���i���ړ��ł���`�b�v�����𑗐M���܂��B<br>
	 * @throws DuplicateNameException ���̈ړ���i�̖��O�����łɎg�p����Ă���Ƃ��� �������܂��B<br>
	 */
	public Vehicle(String name, float speed, Collection<ChipAttribute> attributeSet)
			throws DuplicateNameException {
		if (VehicleStorage.getInstance().contains(name)) {
			throw new DuplicateNameException("duplicate name : " + name);
		}
		this.name = name;
		this.speed = speed;
		this.attributeStorage = new Storage<ChipAttribute>();
		this.attributeStorage.addAll(attributeSet);
		addThis();
	}

	/**
	 * this�C���X�^���X��VehicleStorage�ɒǉ����܂�.
	 */
	private void addThis() {
		VehicleStorage.getInstance().add(this);
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * �ړ���i�̈ړ����x���擾���܂�.
	 *
	 * @return �ړ����x��Ԃ��܂��B<br>
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * �ړ���i�̈ړ����x��ݒ肵�܂�.
	 *
	 * @param speed �ړ����x���w�肵�܂��B<Br>
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * ���̈ړ���i�����ړ��\�ȑ������i�[���ꂽ�X�g���[�W���擾���܂�.
	 *
	 * @return �ړ��\�ȑ������i�[���ꂽ�X�g���[�W��Ԃ��܂��B "VOID"�͒ǉ�����Ă��܂���B<br>
	 */
	public Storage<ChipAttribute> getAttributeStorage() {
		return attributeStorage;
	}

	/**
	 * ���̈ړ���i���w�肳�ꂽ�`�b�v�����Ɉړ��\�ł��邩���������܂�. ���̎菇�Ō�������܂��B<br>
	 * <ol>
	 * <li>CLOSE�����VOID�̌���</li>
	 * <br>
	 * ���M���ꂽ�`�b�v�������A"VOID"�̏ꍇ�A�K��true��Ԃ��܂��B<br>
	 * �܂��A���M���ꂽ�`�b�v�������A"CLOSE"�̏ꍇ�A�K��false��Ԃ��܂��B<br>
	 * <br>
	 * <li>�ړ��۔���</li>
	 * <br>
	 * ���̈ړ���i�����ړ��\�ȑ������ۊǂ��ꂽ�X�g���[�W�� ���M���ꂽ�����Ɠ������O�����C���X�^���X�����݂���ꍇ�Atrue��Ԃ��܂��B<ber>
	 * <br>
	 * </ol>
	 *
	 * @param attribute �L�����N�^�����ɏ��`�b�v�̑����𑗐M���܂��B<br>
	 * @return ���̈ړ���i��attribute�Ɉړ��ł���ꍇ��true�A�ړ��ł��Ȃ��ꍇ��false��Ԃ��܂��B<br>
	 */
	public boolean stepOn(ChipAttribute attribute) {
		if (ChipAttributeStorage.getInstance().get("CLOSE").equals(attribute)) {
			return false;
		}
		if (ChipAttributeStorage.getInstance().get("VOID").equals(attribute)) {
			return true;
		}
		return this.attributeStorage.contains(attribute);
	}

	//���ׂď���Ƃ���true��Ԃ�
	public boolean stepOn(List<ChipAttribute> attr) {
		for (int i = 0, size = attr.size(); i < size; i++) {
			if (!stepOn(attr.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Vehicle{" + "name=" + name + ", speed=" + speed + ", attributeStorage=" + attributeStorage + '}';
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Vehicle other = (Vehicle) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Vehicle o) {
		return this.name.compareTo(o.getName());
	}
}
