package kinugasa.resource.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.InputStatus;
import kinugasa.resource.db.*;
import static kinugasa.resource.sound.SoundType.BGM;
import static kinugasa.resource.sound.SoundType.SE;
import kinugasa.resource.text.FileIOException;
import kinugasa.util.StopWatch;

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
public final class SoundStorage extends DBStorage<Sound> {

	private static final SoundStorage INSTANCE = new SoundStorage();

	public static SoundStorage getInstance() {
		return INSTANCE;
	}

	public static float volumeBgm = 1.0f;
	public static float volumeSe = 1.0f;

	private SoundStorage() {
		super(true);
	}

	/**
	 * サウンドの音量を更新するため、サウンドを作成しなおします。
	 */
	public void rebuild() {
		GameLog.print("SoundStorage rebuild");
		getDirect().clear();
		addAll(selectAll());
	}

	/**
	 * 全てのサウンドを解放します.
	 */
	public void dispose() {
		getDirect().values().forEach(p -> p.stop());
		getDirect().values().forEach(p -> p.dispose());
		System.gc();
	}

	public void stopAll() {
		for (Sound sound : getDirect().values()) {
			sound.stop();
		}
	}

	@Override
	protected Sound select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select SoundID, desc, fileName, loopFrom, loopTo, mg, type  from sound where soundid='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			for (List<DBValue> line : kr) {
				String soundID = line.get(0).get();
				String desc = line.get(1).get();
				String fileName = line.get(2).get();
				int lpf = line.get(3).asInt();
				int lpt = line.get(4).asInt();
				float mg = line.get(5).asFloat();
				SoundType type = line.get(6).of(SoundType.class);
				switch (type) {
					case BGM:
						mg *= volumeBgm;
						break;
					case SE:
						mg *= volumeSe;
						break;
					default:
						throw new AssertionError("SoundStorage : undefined Type : " + line);
				}
				SoundBuilder sb = SoundBuilder.create(soundID, fileName, desc, lpf, lpt, mg, type);
				return sb.builde();
			}
		}
		return null;
	}

	@Override
	protected List<Sound> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select SoundID, desc, fileName, loopFrom, loopTo, mg, type from sound;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Sound> list = new ArrayList<>();
			for (List<DBValue> line : kr) {
				String soundID = line.get(0).get();
				String desc = line.get(1).get();
				String fileName = line.get(2).get();
				int lpf = line.get(3).asInt();
				int lpt = line.get(4).asInt();
				float mg = line.get(5).asFloat();
				SoundType type = line.get(6).of(SoundType.class);
				switch (type) {
					case BGM:
						mg *= volumeBgm;
						break;
					case SE:
						mg *= volumeSe;
						break;
					default:
						throw new AssertionError("SoundStorage : undefined Type : " + line);
				}
				SoundBuilder sb = SoundBuilder.create(soundID, fileName, desc, lpf, lpt, mg, type);
				list.add(sb.builde());
			}
			return list;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from sound").cell(0, 0).asInt();
		}
		return 0;

	}

}
