/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.field4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.system.CurrentQuest;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.EnemySetStorage;
import kinugasa.game.system.EnemySetStorageStorage;
import kinugasa.game.system.Flag;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemException;
import kinugasa.game.system.Item;
import kinugasa.game.system.ItemStorage;
import kinugasa.game.system.PlayerCharacter;
import kinugasa.game.system.Quest;
import kinugasa.game.system.QuestStorage;
import kinugasa.game.system.ScriptFormatException;
import kinugasa.game.system.Status;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ColorChanger;
import kinugasa.graphics.ColorTransitionModel;
import kinugasa.graphics.FadeCounter;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FadeEffect;
import kinugasa.object.FourDirection;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_21:23:15<br>
 * @author Shinacho<br>
 */
public enum FieldEventType {
	//調べられるイベント
	/**
	 * 通常、イベントはそのマスを踏むと自動起動しますが、
	 * スクリプトにこのイベントが入っていると、フィールド上で「調べる」コマンドを実行してから、イベントが起動します。
	 */
	MANUAL_EVENT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	//サウンドマップ名、サウンド名
	STOP_ALL_SOUND {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			if (e.getStorageName() == null) {
				throw new FieldEventScriptException("storage name is null  : " + e);
			}
			SoundStorage.getInstance().stopAll();
			return UserOperationRequire.CONTINUE;
		}
	},
	PLAY_SOUND {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			if (e.getTargetName() == null) {
				throw new FieldEventScriptException("target name is null  : " + e);
			}
			SoundStorage.getInstance().get(e.getTargetName()).load().stopAndPlay();
			return UserOperationRequire.CONTINUE;
		}
	},
	//アイテム名
	ADD_ITEM {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			Item item = ItemStorage.getInstance().get(e.getValue());
			int i = Integer.parseInt(e.getTargetName());
			GameSystem.getInstance().getParty().get(i).getStatus().getItemBag().add(item);
			return UserOperationRequire.GET_ITEAM;
		}
	},
	GET_ITEM {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setItem(e.getValue());
			return UserOperationRequire.GET_ITEAM;
		}
	},
	DROP_ITEM {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int i = Integer.parseInt(e.getTargetName());
			party.get(i).getItemBag().drop(ItemStorage.getInstance().get(e.getValue()));
			return UserOperationRequire.CONTINUE;
		}
	},
	//エネミーセットストレージ名（マップ内のランダムなセットになる。
	//マップ内を1つにすることで、1種類のエンカウントにできる
	START_BATTLE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			//フィールドイベントシステムにエンカウント情報を登録する
			EnemySetStorage sto = EnemySetStorageStorage.getInstance().get(e.getStorageName());
			Sound bgm = FieldMap.getCurrentInstance().getBgm();
			MapChipAttribute attr = FieldMap.getCurrentInstance().getCurrentTile().get0Attr();
			EncountInfo enc = new EncountInfo(bgm, sto, attr);
			FieldEventSystem.getInstance().setEncountInfo(enc);
			return UserOperationRequire.TO_BATTLE;
		}
	},
	SET_BEFORE_LAYER {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			BeforeLayerSprite s = BeforeLayerSpriteStorage.getInstance().get(e.getValue());
			FieldMap.getCurrentInstance().setBeforeLayerSprites(List.of(s));
			return UserOperationRequire.CONTINUE;
		}
	},
	ADD_BEFORE_LAYER {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			BeforeLayerSprite s = BeforeLayerSpriteStorage.getInstance().get(e.getValue());
			FieldMap.getCurrentInstance().getBeforeLayerSprites().add(s);
			return UserOperationRequire.CONTINUE;
		}
	},
	REMOVE_BEFORE_LAYER {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			BeforeLayerSprite s = BeforeLayerSpriteStorage.getInstance().get(e.getValue());
			FieldMap.getCurrentInstance().getBeforeLayerSprites().remove(s);
			return UserOperationRequire.CONTINUE;
		}
	},
	FADE_OUT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int w = GameOption.getInstance().getWindowSize().width;
			int h = GameOption.getInstance().getWindowSize().height;
			FieldEventSystem.getInstance().setEffect(new FadeEffect(w, h,
					new ColorChanger(
							ColorTransitionModel.valueOf(0),
							ColorTransitionModel.valueOf(0),
							ColorTransitionModel.valueOf(0),
							new FadeCounter(0, +6)
					)));
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	FADE_IN {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int w = GameOption.getInstance().getWindowSize().width;
			int h = GameOption.getInstance().getWindowSize().height;
			FieldEventSystem.getInstance().setEffect(new FadeEffect(w, h,
					new ColorChanger(
							ColorTransitionModel.valueOf(0),
							ColorTransitionModel.valueOf(0),
							ColorTransitionModel.valueOf(0),
							new FadeCounter(255, -6)
					)));
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	BLACKOUT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setBlackout(true);
			return UserOperationRequire.CONTINUE;
		}
	},
	UNSET_BLACKOUT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setBlackout(false);
			return UserOperationRequire.CONTINUE;
		}
	},
	//tgtのNPCをvalueの位置に移動させる。コンテニューさせる
	ALL_NPC_LOCK_LOCATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMap.getCurrentInstance().getNpcStorage().forEach(p -> p.notMove());
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_DIR_TO {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName()).to(FourDirection.valueOf(e.getValue()));
			return UserOperationRequire.CONTINUE;
		}
	},
	ALL_NPC_UNLOCK_LOCATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMap.getCurrentInstance().getNpcStorage().forEach(p -> p.canMove());
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_LOCK_LOCATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName()).setMoveStop(true);
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_UNLOCK_LOCATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName()).setMoveStop(false);
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_MOVE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			NPC n = FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName());
			n.setTargetIdx(new D2Idx(x, y));
			return UserOperationRequire.CONTINUE;
		}
	},
	//tgtのNPCをvalueの位置に移動させる。コンテニューさせない
	NPC_MOVE_AND_WAIT_THAT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			NPC n = FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName());
			n.setTargetIdx(new D2Idx(x, y));
			FieldEventSystem.getInstance().getWatchingPC().add(n);
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	NPC_REMOVE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			String name = FieldMap.getCurrentInstance().getName();
			if (FieldMap.getRemovedNPC().containsKey(name)) {
				FieldMap.getRemovedNPC().get(name).add(e.getTargetName());
			} else {
				List<String> list = new ArrayList<>();
				list.add(e.getTargetName());
				FieldMap.getRemovedNPC().put(name, list);
			}
			FieldMap.getCurrentInstance().getNpcStorage().remove(e.getTargetName());
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_ADD {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			NPC n = NPC.readFromXML(e.getValue());
			String name = FieldMap.getCurrentInstance().getName();
			if (FieldMap.getAddedNPC().containsKey(name)) {
				FieldMap.getAddedNPC().get(name).add(n);
			} else {
				List<NPC> list = new ArrayList<>();
				list.add(n);
				FieldMap.getAddedNPC().put(name, list);
			}
			FieldMap.getCurrentInstance().getNpcStorage().add(n);
			return UserOperationRequire.CONTINUE;
		}
	},
	//テキストストレージ名、テキストID
	SHOW_MESSAGE_FROM_TEXTSTORAGE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			TextStorageStorage.getInstance().dispose();
			TextStorage ts = TextStorageStorage.getInstance().get(e.getStorageName()).build();
			FieldEventSystem.getInstance().setTextStorage(ts);
			Text t = ts.get(e.getValue());
			FieldEventSystem.getInstance().setText(t);
			return UserOperationRequire.SHOW_MESSAGE;
		}
	},
	//テキスト
	SHOW_MESSAGE_DIRECT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setText(new Text(e.getValue()));
			return UserOperationRequire.SHOW_MESSAGE;
		}
	},
	END_AND_RESET_EVENT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	PC_DIR_TO {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int idx = Integer.parseInt(e.getTargetName());
			if (idx >= 0 && idx < party.size()) {
				GameSystem.getInstance().getPartySprite().get(idx).to(FourDirection.valueOf(e.getValue()));
			}
			return UserOperationRequire.CONTINUE;
		}
	},
	//waittime
	WAIT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setWaitTime(new FrameTimeCounter(Integer.valueOf(e.getValue())));
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	//フラグ名
	SET_FLG_DIRECT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FlagStorage fs = FlagStorage.getInstance();
			if (!fs.contains(e.getTargetName())) {
				fs.add(new Flag(e.getTargetName()));
			}
			Flag f = fs.get(e.getTargetName());
			f.set(FlagStatus.valueOf(e.getValue()));
			return UserOperationRequire.CONTINUE;
		}
	},
	SET_FLG_TMP {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setFlag(e.getTargetName(), FlagStatus.valueOf(e.getValue()));
			return UserOperationRequire.CONTINUE;
		}
	},
	COMMIT_FLG {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	//クエストID、ステージ値
	SET_QUEST {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int v = Integer.parseInt(e.getValue());
			//クエスト情報を設定
			Quest q = null;
			for (var t : QuestStorage.getInstance()) {
				System.out.println(t);
				if (e.getTargetName().equals(t.getId()) && t.getStage() == v && t.getType().equals(e.getStorageName())) {
					q = t;
					break;
				}
			}
			if (q == null) {
				throw new FieldEventScriptException("SET_QEUST not found : " + e);
			}
			Quest remove = null;
			for (Quest qq : CurrentQuest.getInstance()) {
				if (qq.getType().equals(q.getType())) {
					remove = qq;
					break;
				}
			}
			if (remove != null) {
				CurrentQuest.getInstance().remove(remove);
			}
			CurrentQuest.getInstance().add(q);
			return UserOperationRequire.CONTINUE;
		}
	},
	//値なし（FESに設定する
	ENABLE_OPERATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setUserOperation(true);
			return UserOperationRequire.CONTINUE;
		}
	},
	//値なし（FESに設定する
	DISABLE_OPERATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setUserOperation(false);
			return UserOperationRequire.CONTINUE;
		}
	},
	//対象座標（FMCを操作する
	MOVE_CAMERA_2 {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldMapCamera.getInstance().setTargetIdx(new D2Idx(x, y), 2);
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FREE);
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	MOVE_CAMERA_4 {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldMapCamera.getInstance().setTargetIdx(new D2Idx(x, y), 4);
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FREE);
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	MOVE_CAMERA_6 {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldMapCamera.getInstance().setTargetIdx(new D2Idx(x, y), 6);
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FREE);
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	MOVE_CAMERA_8 {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldMapCamera.getInstance().setTargetIdx(new D2Idx(x, y), 8);
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FREE);
			return UserOperationRequire.WAIT_FOR_EVENT;
		}
	},
	//値なし（FMCを操作する
	RESET_CAMERA {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FOLLOW_TO_CENTER);
			return UserOperationRequire.CONTINUE;
		}
	},
	//value
	MONEY_ADD {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int m = Integer.parseInt(e.getValue());
			GameSystem.getInstance().getMoneySystem().get(e.getTargetName()).add(m);
			return UserOperationRequire.CONTINUE;
		}
	},
	MONEY_SUB {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int m = Integer.parseInt(e.getValue());
			GameSystem.getInstance().getMoneySystem().get(e.getTargetName()).add(-m);
			return UserOperationRequire.CONTINUE;
		}
	},
	MONEY_TO {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int m = Integer.parseInt(e.getValue());
			GameSystem.getInstance().getMoneySystem().get(e.getTargetName()).setValue(m);
			return UserOperationRequire.CONTINUE;
		}
	},
	//CHANGELOGICを要求する
	GAME_OVER {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.GAME_OVER;
		}
	},
	//CHANGEMAPを要求する
	CHANGE_MAP {
		int v = 0;

		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FourDirection dir = FourDirection.valueOf(e.getValue().split(",")[2]);
			FieldEventSystem.getInstance().setNode(Node.ofOutNode("AUTO_NODE" + v++, e.getTargetName(), x, y, dir));
			return UserOperationRequire.CHANGE_MAP;
		}
	},
	//CONTINUE
	CHANGE_LOCATION {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldMap.getCurrentInstance().setCurrentIdx(new D2Idx(x, y));
			FieldMapCamera.getInstance().setMode(FieldMapCameraMode.FOLLOW_TO_CENTER);
			return UserOperationRequire.CONTINUE;
		}
	},
	//CONTINUE
	PC_REMOVE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			PlayerCharacter pc = GameSystem.getInstance().getParty().stream().filter(p -> p.getName().equals(e.getValue())).collect(Collectors.toList()).get(0);
			FieldMap.getPlayerCharacter().remove(pc.getSprite());
			GameSystem.getInstance().getParty().remove(pc);
			return UserOperationRequire.CONTINUE;
		}
	},
	//CONTINUE(valueの名前のステータスをロードしてFM、GSに追加する
	PC_ADD {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			PlayerCharacter pc = PlayerCharacter.readFromXML(e.getValue());
			FieldMap.getPlayerCharacter().add(pc.getSprite());
			GameSystem.getInstance().getParty().add(pc);
			return UserOperationRequire.CONTINUE;
		}
	},
	//END_IFが現れるまでのイベントに、このイベントのtermを適用します。適用するのはイベントがセットされたときです。
	IF {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	END_IF {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	END {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			return UserOperationRequire.CONTINUE;
		}
	},
	SET_ATTRIN {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			if (!e.getTargetName().contains(",")) {
				throw new ScriptFormatException("SET_ATTR_IN, but tgtName is missmatch : " + e);
			}
			try {
				int tgtIdx = Integer.parseInt(e.getTargetName().split(",")[0]);
				String key = e.getTargetName().split(",")[1];
				float value = Float.parseFloat(e.getValue());
				party.get(tgtIdx).getBaseAttrIn().get(key).set(value);
				party.get(tgtIdx).getBaseAttrIn().get(key).setInitial(value);
				party.get(tgtIdx).getBaseAttrIn().get(key).setMax(value);

				return UserOperationRequire.CONTINUE;
			} catch (NumberFormatException | NameNotFoundException ex) {
				throw new ScriptFormatException("SET_ATTR_IN, but tgtName is missmatch : " + e);
			}
		}

	},
	INIT_STATUS {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			if (!e.getTargetName().contains(",")) {
				throw new ScriptFormatException("SET_ATTR_IN, but tgtName is missmatch : " + e);
			}
			try {
				int tgtIdx = Integer.parseInt(e.getTargetName().split(",")[0]);
				String key = e.getTargetName().split(",")[1];
				float value = Float.parseFloat(e.getValue());
				party.get(tgtIdx).getBaseStatus().get(key).setMax(value);
				party.get(tgtIdx).getBaseStatus().get(key).set(value);
				party.get(tgtIdx).getBaseStatus().get(key).setInitial(value);

				return UserOperationRequire.CONTINUE;
			} catch (NumberFormatException | NameNotFoundException ex) {
				throw new ScriptFormatException("INIT_STATUS, but tgtName is missmatch : " + e);
			}
		}
	},
	SET_STATUS_MAX {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			if (!e.getTargetName().contains(",")) {
				throw new ScriptFormatException("SET_ATTR_IN, but tgtName is missmatch : " + e);
			}
			try {
				int tgtIdx = Integer.parseInt(e.getTargetName().split(",")[0]);
				String key = e.getTargetName().split(",")[1];
				float value = Float.parseFloat(e.getValue());
				party.get(tgtIdx).getBaseStatus().get(key).setMax(value);

				return UserOperationRequire.CONTINUE;
			} catch (NumberFormatException | NameNotFoundException ex) {
				throw new ScriptFormatException("SET_STATUS_MAX, but tgtName is missmatch : " + e);
			}
		}
	},
	EQIP_ITEM {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int i = Integer.parseInt(e.getTargetName());
			Item item = GameSystem.getInstance().getParty().get(i).getStatus().getItemBag().get(e.getValue());
			if (!GameSystem.getInstance().getParty().get(i).getStatus().getItemBag().contains(item)) {
				throw new GameSystemException("pc " + i + " is not have item :" + item);
			}
			if (!item.canEqip(party.get(i))) {
				throw new GameSystemException("item is can not eqip : " + item);
			}
			party.get(i).addEqip(item);
			return UserOperationRequire.GET_ITEAM;
		}
	},
	UPDATE_ACTION_OF_ALL_PC {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction());
			return UserOperationRequire.CONTINUE;
		}

	},
	NPC_ANIMATION_IDX_TO {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			NPC n = FieldMap.getCurrentInstance().getNpcStorage().get(e.getTargetName());
			Animation ani = n.getAnimation();
			n.setImage(ani.getImage(Integer.parseInt(e.getValue())));
			return UserOperationRequire.CONTINUE;
		}
	},
	PC_ANIMATION_IDX_TO {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			int idx = Integer.parseInt(e.getTargetName());
			if (idx >= 0 && idx < party.size()) {
				AnimationSprite s = GameSystem.getInstance().getPartySprite().get(idx);
				Animation ani = s.getAnimation();
				s.setImage(ani.getImage(Integer.parseInt(e.getValue())));
			}

			return UserOperationRequire.CONTINUE;
		}
	},
	DB_CONNECTION_OPEN {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			String fileName = e.getValue().split("/")[0];
			String user = e.getValue().split("/")[1];
			String pass = e.getValue().split("/")[2];
			DBConnection.getInstance().close();
			DBConnection.getInstance().open(fileName, user, pass);
			return UserOperationRequire.CONTINUE;
		}
	},
	DB_CONNECTION_CLOSE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			DBConnection.getInstance().close();
			return UserOperationRequire.CONTINUE;
		}
	},
	EXEC_SQL {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			String fileName = e.getValue();
			DBConnection.getInstance().execByFile(fileName);
			return UserOperationRequire.CONTINUE;
		}
	},
	EXEC_SQL_DIRECT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException {
			String sql = e.getValue();
			DBConnection.getInstance().execDirect(sql);
			return UserOperationRequire.CONTINUE;
		}
	};

	abstract UserOperationRequire exec(List<Status> party, FieldEvent e) throws FieldEventScriptException;

}
