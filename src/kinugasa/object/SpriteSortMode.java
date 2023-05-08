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
package kinugasa.object;

import java.util.Comparator;
import kinugasa.util.Random;

/**
 * CompositeSprite�̃\�[�g�������w�肷��R���p���[�^�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_16:20:44<br>
 * @author Shinacho<br>
 */
public enum SpriteSortMode implements Comparator<Sprite> {

	/** ���̗񋓂��w�肷��ƁA�`�揇��Z�����W�̑傫�����ɂȂ�܂�.
	 * ���������āAZ�����W�̏����ȃX�v���C�g����O�ɕ\������܂�. */
	FRONT_TO_BACK {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return java.lang.Float.compare(s2.getZ(), s1.getZ());
		}
	},
	/** ���̗񋓂��w�肷��ƁA�`�揇��Z�����W�̏��������ɂȂ�܂�.
	 * ���������āAZ�����W�̑傫�ȃX�v���C�g����O�ɕ\������܂�. */
	BACK_TO_FRONT {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return java.lang.Float.compare(s1.getZ(), s2.getZ());
		}
	},
	/** ���̗񋓂��w�肷��ƁA�g�p���邽�тɈقȂ�Œ��ꒃ�ȏ��Ԃɕ��ёւ��܂�.
	 * ���̗񋓂�21�����W�F�l���[�^�itwoone.util.Random�j���g�p���܂�. */
	RANDOM {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return Random.randomAbsInt() % 3 - 1;
		}
	};

	@Override
	public abstract int compare(Sprite s1, Sprite s2);
}
