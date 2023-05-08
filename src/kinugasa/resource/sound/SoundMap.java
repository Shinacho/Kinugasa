package kinugasa.resource.sound;

import kinugasa.resource.DynamicStorage;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileIOException;

/**
 * �T�E���h�̈ꎞ�I�ȕۑ��̈��񋟂��܂�.
 * <br>
 * ���̃X�g���[�W�̎����̓��W�b�N�̃v���Z�b�g�ɂ���āA ���ʉ���BGM���Đ����邽�߂̃L�[���w�肳��Ă���ꍇ������܂��B<br>
 *
 * �쐬���ꂽ�T�E���h�}�b�v�͎����I�ɃT�E���h�X�g���[�W�ɒǉ�����܂��B<br>
 * �T�E���h�}�b�v�̖��O���w�肵�Ȃ��ꍇ�́A�K���Ȗ��O�����蓖�Ă��܂��B<br>
 * <br>
 * �T�E���h�̋�ۃN���X�̌^�ɒ��ӂ��Ă��������B1�̃}�b�v�Ɋ܂܂��A�T�E���h�̌^�� ���ꂷ�邱�Ƃ𐄏����܂��B<br>
 * <br>
 * Freeable�̎����́A�}�b�v�ɒǉ�����Ă��邷�ׂẴT�E���h�ɍs���܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/06_7:52:47<br>
 * @author Shinacho<br>
 */
public final class SoundMap extends DynamicStorage<Sound> implements Nameable {

	/**
	 * ���̃T�E���h�}�b�v�̖��O�ł�. ���Ƃ��΁A�u��A�v��u�_���W����5�v�̂悤�Ȃ킩��₷�����O��t���邱�Ƃ��ł��܂��B
	 */
	private String name;
	/**
	 * �T�E���h�}�b�v�̃C���X�^���X���̃J�E���^�ł�.
	 */
	private static int counter;

	/**
	 * �K���Ȗ��O���w�肵�ĐV�����T�E���h�}�b�v���쐬���܂�.
	 */
	public SoundMap() {
		this("SoundMap_" + (counter++));
	}

	/**
	 * ���O�Ə����f�[�^���w�肵�āA�T�E���h�}�b�v���쐬���܂�.
	 *
	 * @param name �T�E���h�}�b�v�̖��O���w�肵�܂��B<br>
	 * @param sounds �����f�[�^���w�肵�܂��B<br>
	 */
	public SoundMap(String name, Sound... sounds) {
		this(name);
		super.addAll(sounds);
	}

	/**
	 * ���O���w�肵�ăT�E���h�}�b�v���쐬���܂�.
	 *
	 * @param name �T�E���h�}�b�v�̖��O���w�肵�܂��B<br>
	 */
	public SoundMap(String name) {
		this.name = name;
	}

	/**
	 * �T�E���h�r���_����A�L���b�V���T�E���h���쐬���A���̃}�b�v�ɒǉ����܂�.
	 *
	 * @param soundBuilder �쐬����T�E���h�̐ݒ���s���r���_�ł��B<br>
	 *
	 * @return �쐬���ꂽ�T�E���h��Ԃ��܂��B���̃}�b�v�ɒǉ�����Ă��܂��B<br>
	 */
	public CachedSound createCachedSound(SoundBuilder soundBuilder) {
		CachedSound sound = soundBuilder.builde();
		add(sound);
		return sound;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * �S�ẴT�E���h��������܂�.
	 */
	@Override
	public void dispose() {
		forEach(p -> p.dispose());
	}

	public void stopAll() {
		for (Sound sound : this) {
			sound.stop();
		}
	}

	@Override
	public SoundMap load() throws FileIOException {
		forEach(s -> s.load());
		return this;
	}

	@Override
	public InputStatus getStatus() {
		for (Sound s : this) {
			if (s.getStatus() == InputStatus.LOADED) {
				return InputStatus.LOADED;
			}
		}
		return InputStatus.NOT_LOADED;
	}
}
