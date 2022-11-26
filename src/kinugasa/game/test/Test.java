/*
 * The MIT License
 *
 * Copyright 2021 shin211.
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
package kinugasa.game.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameLogicStorage;
import kinugasa.game.GameManager;
import kinugasa.game.GameOption;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LockUtil;
import kinugasa.game.field4.D2Idx;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.FieldMapXMLLoader;
import kinugasa.game.field4.FourDirAnimation;
import kinugasa.game.system.BattleActionStorage;
import kinugasa.game.system.BattleCharacter;
import kinugasa.game.system.BattleCommandMessageWindow;
import kinugasa.game.system.BattleConfig;
import kinugasa.game.system.BattleResult;
import kinugasa.game.system.BattleSystem;
import kinugasa.game.system.BattleWinLoseLogic;
import kinugasa.game.system.BookStorage;
import kinugasa.game.system.Enemy;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemXMLLoader;
import kinugasa.game.system.ItemStorage;
import kinugasa.game.system.PartyLocation;
import kinugasa.game.system.PlayerCharacter;
import kinugasa.game.system.RaceStorage;
import kinugasa.game.system.SpeedCalcModelStorage;
import kinugasa.game.system.Status;
import kinugasa.game.system.StatusWindows;
import kinugasa.game.ui.FPSLabel;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.FourDirection;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 * ゲームのテスト実装です.
 *
 * @author shin211
 */
public class Test extends GameManager {

	public static void main(String... args) {
		LockUtil.deleteAllLockFile();
		new Test().gameStart(args);
	}

	private Test() {
		super(GameOption.defaultOption().setUseGamePad(true).setCenterOfScreen());
	}

	@Override
	protected void startUp() {

		new FieldMapXMLLoader()
				.addSound("resource/bgm/BGM.csv")
				.addSound("resource/se/SE.csv")
				.addTextStorage("resource/field/data/text/000.xml")
				.addMapChipAttr("resource/field/data/attr/ChipAttributes.xml")
				.addVehicle("resource/field/data/vehicle/01.xml")
				.setInitialVehicleName("WALK")
				.addMapChipSet("resource/field/data/chipSet/01.xml")
				.addMapChipSet("resource/field/data/chipSet/02.xml")
				.addFieldMapStorage("resource/field/data/mapBuilder/builder.xml")
				.setInitialFieldMapName("ズシ")
				.setInitialLocation(new D2Idx(9, 9))
				.load();
		new GameSystemXMLLoader()
				.addWeaponMagicTypeStorage("resource/field/data/item/weaponMagicType.xml")
				.addStatusKeyStorage("resource/field/data/battle/status.xml")
				.addAttrKeyStorage("resource/field/data/battle/attribute.xml")
				.addConditionValueStorage("resource/field/data/battle/condition.xml")
				.addItemActionStorage("resource/field/data/item/itemAction.xml")
				.addItemEqipmentSlotStorage("resource/field/data/item/itemSlotList.xml")
				.addItemStorage("resource/field/data/item/itemList.xml")
				.addRaceStorage("resource/field/data/race/raceList.xml")
				.addBattleActionStorage("resource/field/data/battle/battleAction.xml")
				.addBattleField("resource/field/data/battle/battleField.xml")
				.addEnemyList("resource/field/data/enemy/enemyList.xml")
				.addEnemySet("resource/field/data/enemy/enemySet.xml")
				.addBookList("resource/field/data/item/bookList.xml")
				.load();
		// プレイヤーキャラクターの表示座標計算
		int screenW = FieldMapStorage.getScreenWidth();
		int screenH = FieldMapStorage.getScreenHeight();
		float x = screenW / 2 - 16;
		float y = screenH / 2 - 16;
		PlayerCharacterSprite c1 = new PlayerCharacterSprite(x, y, 32, 32, new D2Idx(21, 21),
				new FourDirAnimation(
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(0, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(32, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(64, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(96, 32, 32).images())
				),
				FourDirection.NORTH
		);
		PlayerCharacterSprite c2 = new PlayerCharacterSprite(x, y, 32, 32, new D2Idx(21, 21),
				new FourDirAnimation(
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(0, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(32, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(64, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(96, 32, 32).images())
				),
				FourDirection.NORTH
		);
		PlayerCharacterSprite c3 = new PlayerCharacterSprite(x, y, 32, 32, new D2Idx(21, 21),
				new FourDirAnimation(
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(0, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(32, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(64, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(96, 32, 32).images())
				),
				FourDirection.NORTH
		);
		PlayerCharacterSprite c4 = new PlayerCharacterSprite(x, y, 32, 32, new D2Idx(21, 21),
				new FourDirAnimation(
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(0, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(32, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(64, 32, 32).images()),
						new Animation(new FrameTimeCounter(12), new SpriteSheet("resource/char/chara2.png").rows(96, 32, 32).images())
				),
				FourDirection.NORTH
		);
		List<PlayerCharacter> pcList = new ArrayList<>();

		Status s1 = new Status("メンバ１", RaceStorage.getInstance().get("人間"));
		Status s2 = new Status("メンバ２", RaceStorage.getInstance().get("人間"));
		Status s3 = new Status("メンバ３", RaceStorage.getInstance().get("人間"));
		Status s4 = new Status("メンバ４", RaceStorage.getInstance().get("人間"));
		s2.setPartyLocation(PartyLocation.BACK);
		s4.setPartyLocation(PartyLocation.BACK);
		pcList.add(new PlayerCharacter(s1, c1));
		pcList.add(new PlayerCharacter(s2, c2));
		pcList.add(new PlayerCharacter(s3, c3));
		pcList.add(new PlayerCharacter(s4, c4));
		GameSystem.getInstance().initBattleSystem(pcList);
		GameSystem.getInstance().initFieldSystem(pcList);
		s1.getItemBag().add(ItemStorage.getInstance().get("鉄の剣"));
		s2.getItemBag().add(ItemStorage.getInstance().get("鉄の剣"));
		s3.getItemBag().add(ItemStorage.getInstance().get("鉄の剣"));
		s4.getItemBag().add(ItemStorage.getInstance().get("鉄の剣"));
		s1.addEqip(ItemStorage.getInstance().get("鉄の剣"));
		s2.addEqip(ItemStorage.getInstance().get("鉄の剣"));
		s3.addEqip(ItemStorage.getInstance().get("鉄の剣"));
		s4.addEqip(ItemStorage.getInstance().get("鉄の剣"));
		s1.getBookBag().add(BookStorage.getInstance().get("火炎の書"));
		s2.getBookBag().add(BookStorage.getInstance().get("火炎の書"));
		s3.getBookBag().add(BookStorage.getInstance().get("火炎の書"));
		s4.getBookBag().add(BookStorage.getInstance().get("火炎の書"));
		s1.getBaseStatus().get("HP").set(123);
		s2.getBaseStatus().get("HP").set(123);
		s3.getBaseStatus().get("HP").set(123);
		s4.getBaseStatus().get("HP").set(123);
		s1.getBaseStatus().get("MP").set(45);
		s2.getBaseStatus().get("MP").set(45);
		s3.getBaseStatus().get("MP").set(45);
		s4.getBaseStatus().get("MP").set(45);
		s1.getBaseStatus().get("SAN").set(67);
		s2.getBaseStatus().get("SAN").set(67);
		s3.getBaseStatus().get("SAN").set(67);
		s4.getBaseStatus().get("SAN").set(67);
		s1.getBaseStatus().get("MOV").set(Random.randomAbsInt(128) + 98);
		s2.getBaseStatus().get("MOV").set(Random.randomAbsInt(128) + 98);
		s3.getBaseStatus().get("MOV").set(Random.randomAbsInt(128) + 98);
		s4.getBaseStatus().get("MOV").set(Random.randomAbsInt(128) + 98);
		//テスト用にすべてのアクションを追加
		for (BattleCharacter pc : GameSystem.getInstance().getParty()) {
			pc.getStatus().getBattleActions().clear();
			pc.getStatus().getBattleActions().addAll(BattleActionStorage.getInstance());
		}

		//
		//----------------------------------------------------------------------
		//
		SpeedCalcModelStorage.getInstance().setCurrent("SPD_20%");
		Enemy.setProgressBarKey("HP");
		BattleConfig.outputLogStatusKey = "HP";
		BattleConfig.moveStatusKey = "MOV";
		BattleConfig.expStatisKey = "EXP";
		BattleConfig.addUntargetConditionNames("DEAD");
		BattleConfig.addUntargetConditionNames("DESTROY");
		BattleConfig.undeadDebugMode = true;
		BattleConfig.addWinLoseLogic((List<Status> party, List<Status> enemy) -> {
			// パーティのコンディションを確認
			boolean lose = true;
			for (Status s : party) {
				lose &= (s.hasCondition("DEAD") || s.hasCondition("DESTROY"));
			}
			if (lose) {
				return BattleResult.LOSE;
			}
			boolean win = true;
			for (Status s : enemy) {
				win &= (s.hasCondition("DEAD") || s.hasCondition("DESTROY"));
			}
			if (win) {
				return BattleResult.LOSE;
			}
			return BattleResult.NOT_YET;
		});
		BattleCommandMessageWindow.setStatusKey(Arrays.asList("MP", "SAN"));
		StatusWindows.setVisibleStatus(Arrays.asList("HP ", "MP ", "SAN"));

		GameSystem.setDebugMode(true);

		gls.add(new FieldLogic(this));
		gls.add(new BattleLogic(this));

		gls.changeTo("FIELD");
	}
	GameLogicStorage gls = GameLogicStorage.getInstance();
	FPSLabel fps = new FPSLabel(0, 12);

	@Override
	protected void dispose() {
	}

	@Override
	protected void update(GameTimeManager gtm) {
		fps.setGtm(gtm);
		gls.getCurrent().update(gtm);
	}

	@Override
	protected void draw(GraphicsContext gc) {
		gls.getCurrent().draw(gc);
		fps.draw(gc);
	}

}
