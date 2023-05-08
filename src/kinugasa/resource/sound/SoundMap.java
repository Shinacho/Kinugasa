package kinugasa.resource.sound;

import kinugasa.resource.DynamicStorage;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileIOException;

/**
 * サウンドの一時的な保存領域を提供します.
 * <br>
 * このストレージの実装はロジックのプリセットによって、 効果音やBGMを再生するためのキーが指定されている場合があります。<br>
 *
 * 作成されたサウンドマップは自動的にサウンドストレージに追加されます。<br>
 * サウンドマップの名前を指定しない場合は、適当な名前が割り当てられます。<br>
 * <br>
 * サウンドの具象クラスの型に注意してください。1つのマップに含まれる、サウンドの型は 統一することを推奨します。<br>
 * <br>
 * Freeableの実装は、マップに追加されているすべてのサウンドに行われます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/06_7:52:47<br>
 * @author Shinacho<br>
 */
public final class SoundMap extends DynamicStorage<Sound> implements Nameable {

	/**
	 * このサウンドマップの名前です. たとえば、「町A」や「ダンジョン5」のようなわかりやすい名前を付けることができます。
	 */
	private String name;
	/**
	 * サウンドマップのインスタンス数のカウンタです.
	 */
	private static int counter;

	/**
	 * 適当な名前を指定して新しいサウンドマップを作成します.
	 */
	public SoundMap() {
		this("SoundMap_" + (counter++));
	}

	/**
	 * 名前と初期データを指定して、サウンドマップを作成します.
	 *
	 * @param name サウンドマップの名前を指定します。<br>
	 * @param sounds 初期データを指定します。<br>
	 */
	public SoundMap(String name, Sound... sounds) {
		this(name);
		super.addAll(sounds);
	}

	/**
	 * 名前を指定してサウンドマップを作成します.
	 *
	 * @param name サウンドマップの名前を指定します。<br>
	 */
	public SoundMap(String name) {
		this.name = name;
	}

	/**
	 * サウンドビルダから、キャッシュサウンドを作成し、このマップに追加します.
	 *
	 * @param soundBuilder 作成するサウンドの設定を行うビルダです。<br>
	 *
	 * @return 作成されたサウンドを返します。このマップに追加されています。<br>
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
	 * 全てのサウンドを解放します.
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
