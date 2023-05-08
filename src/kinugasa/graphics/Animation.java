/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
package kinugasa.graphics;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kinugasa.resource.KImage;
import kinugasa.util.ArrayIndexModel;
import kinugasa.util.SimpleIndex;
import kinugasa.util.TimeCounter;

/**
 * �摜�z����A�j���[�V�����Ƃ��čĐ����邽�߂̉摜�ƃC���f�b�N�X��ێ����܂�.
 * <br>
 * �A�j���[�V�����p�摜��null�������܂��B���ꂼ��̃��\�b�h�ł́A�A�j���[�V�����̗v�f�ƂȂ�
 * �摜��1���Ȃ��ꍇ�i�܂�null�̏ꍇ�j��null��Ԃ��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_1:39:19<br>
 * @author Shinacho<br>
 */
public class Animation implements Iterable<KImage>, Cloneable {

	/**
	 * �A�j���[�V������1�̗v�f���\������鎞�ԊԊu���w�肷��^�C���J�E���^�ł�.
	 */
	private TimeCounter visibleTime;
	/**
	 * �A�j���[�V�����̑J�ڏ������w�肷�邽�߂̔z��C���f�b�N�X�ł�.
	 */
	private ArrayIndexModel index;
	/**
	 * �A�j���[�V�����Ƃ��čĐ������摜�̔z��ł�.
	 */
	private KImage[] images;
	private boolean repeat = true;
	private boolean stop;

	/**
	 * �V�����A�j���[�V�������\�z���܂�. ���̃R���X�g���N�^�ł́A�z��C���f�b�N�X�́{�����փ��[�v����V�[�P���V�����ȃ��f���ɂȂ�܂��B<br>
	 *
	 * @param visibleTime �A�j���[�V������1���̉摜�̕\�����Ԃ��`����^�C���J�E���^�ł��B<br>
	 * @param images �\������摜��1�ȏ㑗�M���܂��B<br>
	 */
	public Animation(TimeCounter visibleTime, BufferedImage... images) {
		this(visibleTime, new SimpleIndex(), images);
	}

	public Animation(TimeCounter visibleTime, List<BufferedImage> images) {
		this(visibleTime, images.toArray(new BufferedImage[images.size()]));
	}

	/**
	 * �V�����A�j���[�V�������\�z���܂�. ���̃R���X�g���N�^�ł́A�z��C���f�b�N�X�́{�����փ��[�v����V�[�P���V�����ȃ��f���ɂȂ�܂��B<br>
	 *
	 * @param visibleTime �A�j���[�V������1���̉摜�̕\�����Ԃ��`����^�C���J�E���^�ł��B<br>
	 * @param images �\������摜��1�ȏ㑗�M���܂��B<br>
	 */
	public Animation(TimeCounter visibleTime, KImage... images) {
		this(visibleTime, new SimpleIndex(), images);
	}

	/**
	 * �V�����A�j���[�V�������\�z���܂�.
	 *
	 * @param visibleTime �A�j���[�V������1���̉摜�̕\�����Ԃ��`����^�C���J�E���^�ł��B<br>
	 * @param index �A�j���[�V�����̑J�ڏ������`����z��̃C���f�b�N�X�ł��B<br>
	 * @param images �\������摜��1�ȏ㑗�M���܂��B<br>
	 */
	public Animation(TimeCounter visibleTime, ArrayIndexModel index, BufferedImage... images) {
		this.visibleTime = visibleTime;
		this.index = index;
		this.images = new KImage[images.length];
		for (int i = 0; i < images.length; i++) {
			this.images[i] = new KImage(images[i]);
		}
	}

	/**
	 * �V�����A�j���[�V�������\�z���܂�.
	 *
	 * @param visibleTime �A�j���[�V������1���̉摜�̕\�����Ԃ��`����^�C���J�E���^�ł��B<br>
	 * @param index �A�j���[�V�����̑J�ڏ������`����z��̃C���f�b�N�X�ł��B<br>
	 * @param images �\������摜��1�ȏ㑗�M���܂��B<br>
	 */
	public Animation(TimeCounter visibleTime, ArrayIndexModel index, KImage... images) {
		this.visibleTime = visibleTime;
		this.index = index;
		this.images = images;
	}

	/**
	 * ���̃A�j���[�V�����ɐݒ肳��Ă���摜���擾���܂�.
	 *
	 * @return ���̃A�j���[�V�����̉摜�S�Ă��擾���܂��B���̔z��͖h��I�R�s�[����܂���B
	 * �摜���ݒ肳��Ă��Ȃ��ꍇnull��Ԃ��܂��B<br>
	 */
	public KImage[] getImages() {
		return images;
	}

	/**
	 * �w�肵���C���f�b�N�X�ʒu�̃A�j���[�V�����v�f���擾���܂�.
	 *
	 * @param index �C���f�b�N�X���w�肵�܂��B<br>
	 *
	 * @return �w�肵���C���f�b�N�X�ʒu�̃A�j���[�V�����v�f�ƂȂ�摜��Ԃ��܂��B �摜���ݒ肳��Ă��Ȃ��ꍇnull��Ԃ��܂��B<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException �s���ȃC���f�b�N�X�𑗐M�����ꍇ�ɓ������܂��B<br>
	 */
	public KImage getImage(int index) throws ArrayIndexOutOfBoundsException {
		if (images == null) {
			return null;
		}
		return images[index];
	}

	/**
	 * ���̃A�j���[�V�����̉摜��ύX���܂�.
	 *
	 * @param images �V�����摜�z��𑗐M���܂��B<br>
	 */
	public void setImages(KImage... images) {
		this.images = images;
	}

	/**
	 * ���̃A�j���[�V�����̉摜��ύX���܂�.
	 *
	 * @param images �V�����摜�z��𑗐M���܂��B<br>
	 */
	public void setImages(BufferedImage... images) {
		this.images = new KImage[images.length];
		for (int i = 0; i < images.length; i++) {
			this.images[i] = new KImage(images[i]);
		}
	}

	/**
	 * ���̃A�j���[�V�����̉摜��ύX���܂�.
	 *
	 * @param index �A�j���[�V�����v�f��u��������ʒu�̃C���f�b�N�X���w�肵�܂��B<br>
	 * @param image �V�����摜�𑗐M���܂��B<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException �s���ȃC���f�b�N�X�𑗐M�����ꍇ�ɓ������܂��B<br>
	 */
	public void setImage(int index, BufferedImage image) throws ArrayIndexOutOfBoundsException {
		setImage(index, new KImage(image));
	}

	/**
	 * ���̃A�j���[�V�����̉摜��ύX���܂�.
	 *
	 * @param index �A�j���[�V�����v�f��u��������ʒu�̃C���f�b�N�X���w�肵�܂��B<br>
	 * @param image �V�����摜�𑗐M���܂��B<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException �s���ȃC���f�b�N�X�𑗐M�����ꍇ�ɓ������܂��B<br>
	 */
	public void setImage(int index, KImage image) {
		images[index] = image;
	}

	/**
	 * �A�j���[�V�����̑J�ڏ�����ύX���܂�.
	 *
	 * @param index �V�����J�ڃA���S���Y���𑗐M���܂��B<br>
	 */
	public void setIndex(ArrayIndexModel index) {
		this.index = index;
	}

	/**
	 * ���̃A�j���[�V�����ɐݒ肳��Ă���z��̃C���f�b�N�X���擾���܂�.
	 * ���̃��\�b�h�́A�ݒ肳��Ă���C���f�b�N�X���f����ArrayIndexModel�Ƃ��ĕԂ��܂��B<br>
	 * ���̃��\�b�h��p�ɂɎg���ꍇ�́A�L���X�g�����C���f�b�N�X��Ԃ���悤 �T�u�N���X���쐬���邱�Ƃ��ł��܂��B<br>
	 *
	 * @return ���̃A�j���[�V�����ɐݒ肳��Ă���C���f�b�N�X���f����Ԃ��܂��B<br>
	 */
	public ArrayIndexModel getIndex() {
		return index;
	}

	/**
	 * �A�j���[�V������1�̗v�f�̍Đ����Ԃ��w�肷�邽�߂̃^�C���J�E���^��ݒ肵�܂�.
	 *
	 * @param visibleTime �V�����^�C���J�E���^�𑗐M���܂��B<br>
	 */
	public void setVisibleTime(TimeCounter visibleTime) {
		this.visibleTime = visibleTime;
	}

	/**
	 * ���̃A�j���[�V�����ɐݒ肳��Ă���\�����ԃJ�E���^���擾���܂�.
	 * ���̃��\�b�h�́A�ݒ肳��Ă���^�C���J�E���^��TimeCounter�Ƃ��ĕԂ��܂��B<br>
	 * ���̃��\�b�h��p�ɂɎg���ꍇ�́A�L���X�g�����J�E���^��Ԃ���悤 �T�u�N���X���쐬���邱�Ƃ��ł��܂��B<br>
	 *
	 * @return ���̃A�j���[�V�����ɐݒ肳��Ă���^�C���J�E���^��Ԃ��܂��B<br>
	 */
	public TimeCounter getVisibleTime() {
		return visibleTime;
	}

	/**
	 * �\�����Ԃ̔�����s���܂�. ���ݕ\�����̗v�f�̕\�����Ԃ��o�߂����ꍇ�ɂ́A �C���f�b�N�X���X�V���A�`�悷�ׂ��摜��ύX���܂��B<br>
	 */
	public void update() {
		if (stop) {
			return;
		}
		if (visibleTime.isReaching()) {
			if (index.getIndex() != images.length - 1 || repeat) {
				index.index(images == null ? 0 : images.length);
			}
		}
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isStop() {
		return stop;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isEnded() {
		if (repeat) {
			return false;
		}
		if (images == null) {
			return false;
		}
		return index.getIndex() >= images.length - 1;
	}

	/**
	 * ���̃A�j���[�V�����ŁA���ݕ\�����ׂ��摜��Ԃ��܂�.
	 *
	 * @return ���̃A�j���[�V�����Ō��ݕ\�������v�f��Ԃ��܂��B<br>
	 */
	public KImage getCurrentImage() {
		return images == null ? null : images[index.getIndex()];
	}

	/**
	 * ���̃A�j���[�V�����ŁA���ݕ\�����ׂ��摜��Ԃ��܂�.
	 *
	 * @return ���̃A�j���[�V�����Ō��ݕ\�������v�f��Ԃ��܂��B<br>
	 */
	public BufferedImage getCurrentBImage() {
		return images == null ? null : images[index.getIndex()].get();
	}

	/**
	 * �摜�z��̗v�f����Ԃ��܂�.
	 *
	 * @return �A�j���[�V�����Ƃ��čĐ������v�f�̐���Ԃ��܂��B<br>
	 */
	public int length() {
		return images == null ? 0 : images.length;
	}

	@Override
	public Iterator<KImage> iterator() {
		return Arrays.asList(images).iterator();
	}

	@Override
	public Animation clone() {
		try {
			Animation result = (Animation) super.clone();
			if (images != null) {
				result.images = this.images.clone();
			}
			result.index = this.index.clone();
			result.visibleTime = this.visibleTime.clone();
			return result;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	@Override
	public String toString() {
		return "Animation{" + "visibleTime=" + visibleTime + ", index=" + index + ", images=" + images + ", repeat=" + repeat + ", stop=" + stop + '}';
	}

}
