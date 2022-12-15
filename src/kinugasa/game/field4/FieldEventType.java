/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.field4;

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.EnemySetStorage;
import kinugasa.game.system.EnemySetStorageStorage;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorageStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.PlayerCharacter;
import kinugasa.game.system.QuestLineStorage;
import kinugasa.game.system.Status;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.ColorChanger;
import kinugasa.graphics.ColorTransitionModel;
import kinugasa.graphics.FadeCounter;
import kinugasa.object.FadeEffect;
import kinugasa.object.FourDirection;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_21:23:15<br>
 * @author Dra211<br>
 */
public enum FieldEventType {
	//サウンドマップ名、サウンド名
	STOP_ALL_SOUND {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			SoundStorage.getInstance().get(e.getStorageName()).stopAll();
			return UserOperationRequire.CONTINUE;
		}
	},
	PLAY_SOUND {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			SoundStorage.getInstance().get(e.getStorageName()).get(e.getTargetName()).load().stopAndPlay();
			return UserOperationRequire.CONTINUE;
		}
	},
	PLAY_SOUND_DIRECT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			new SoundBuilder(e.getValue()).builde().load().stopAndPlay();
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
			FieldMap.getCurrentInstance().getNpcStorage().remove(e.getTargetName());
			return UserOperationRequire.CONTINUE;
		}
	},
	NPC_ADD {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			NPC n = NPC.readFromXML(e.getValue());
			FieldMap.getCurrentInstance().getNpcStorage().add(n);
			return UserOperationRequire.CONTINUE;
		}
	},
	//テキストストレージ名、テキストID
	SHOW_MESSAGE_FROM_TEXTSTORAGE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			TextStorage ts = TextStorageStorage.getInstance().get(e.getStorageName());
			FieldEventSystem.getInstance().setTextStorage(ts);
			Text t = ts.get(e.getValue());
			FieldEventSystem.getInstance().setText(t);
			return UserOperationRequire.SHOW_MESSAGE;
		}
	},
	//テキスト
	@Deprecated
	SHOW_MESSAGE_DIRECT {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FieldEventSystem.getInstance().setText(new Text(e.getValue()));
			return UserOperationRequire.SHOW_MESSAGE;
		}
	},
	SET_PC_DIR_TO {
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
	//フラグストレージ名、フラグ名
	SET_FLG {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			FlagStorageStorage.getInstance().get(e.getStorageName()).get(e.getTargetName()).set(FlagStatus.valueOf(e.getValue()));
			return UserOperationRequire.CONTINUE;
		}
	},
	//クエストラインストレージ名、クエストID、値
	SET_QUEST_LINE {
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int v = Integer.parseInt(e.getValue());
			QuestLineStorage.getInstance().get(e.getStorageName()).getStage().setValue(v);
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
		@Override
		UserOperationRequire exec(List<Status> party, FieldEvent e) {
			int x = Integer.parseInt(e.getValue().split(",")[0]);
			int y = Integer.parseInt(e.getValue().split(",")[1]);
			FieldEventSystem.getInstance().setNode(Node.ofOutNode(e.getTargetName(), x, y));
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
	};

	abstract UserOperationRequire exec(List<Status> party, FieldEvent e);

}
