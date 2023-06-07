/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.field4.*;

/**
 * ステータス管理系のマスターです。
 *
 * @vesion 1.0.0 - 2022/11/16_15:45:53<br>
 * @author Shinacho<br>
 */
public class GameSystem {

	private static boolean debugMode = false;

	public static void setDebugMode(boolean debugMode) {
		GameSystem.debugMode = debugMode;
		FieldMap.setDebugMode(debugMode);
	}

	public static boolean isDebugMode() {
		return debugMode;
	}
	private static final GameSystem INSTANCE = new GameSystem();

	private GameSystem() {
		moneySystem = MoneySystem.getInstance();
		battleSystem = BattleSystem.getInstance();
	}

	public static GameSystem getInstance() {
		return INSTANCE;
	}

	public MoneySystem getMoneySystem() {
		return moneySystem;
	}

	public BattleSystem getBattleSystem() {
		return battleSystem;
	}

	//
	//--------------------------------------------------------------------------
	//
	private List<PlayerCharacter> party = new ArrayList();

	private final MoneySystem moneySystem;
	;
	private final BattleSystem battleSystem;
	private GameMode mode = GameMode.FIELD;

	public void initBattleSystem(List<PlayerCharacter> chara) {
		party = new ArrayList<>(chara);
		for (int i = 0; i < chara.size(); i++) {
			chara.get(i).setOrder(i);
		}
	}

	public void initFieldSystem(List<PlayerCharacter> chara) {
		FieldMap.setPlayerCharacter(chara.stream().map(v -> v.getSprite()).collect(Collectors.toList()));
	}

	public List<PlayerCharacter> getParty() {
		return party;
	}

	public void updateParty() {
		for (PlayerCharacter pc : party) {
			if (!FieldMap.getPlayerCharacter().contains(pc.getSprite())) {
				FieldMap.getPlayerCharacter().add(pc.getSprite());
			}
		}
	}

	public List<PlayerCharacterSprite> getPartySprite() {
		return party.stream().map(v -> v.getSprite()).collect(Collectors.toList());
	}

	public List<Status> getPartyStatus() {
		return party.stream().map(v -> v.getStatus()).collect(Collectors.toList());
	}

	public GameMode getMode() {
		return mode;
	}

	private void setMode(GameMode mode) {
		this.mode = mode;
	}

	private String icon;

	public void battleStart(EncountInfo enc) {
		setMode(GameMode.BATTLE);
		battleSystem.encountInit(enc);
	}

	public BattleResultValues battleEnd() {
		setMode(GameMode.FIELD);
		battleSystem.endBattle();
		return battleSystem.getBattleResultValue();
	}
	private PageBag pageBag = new PageBag();

	public PageBag getPageBag() {
		return pageBag;
	}
	private MaterialBag materialBag = new MaterialBag();

	public MaterialBag getMaterialBag() {
		return materialBag;
	}

	public void save(int dataNO) throws GameSystemException {
		//TODO:
	}

	public Counts getCountSystem() {
		return Counts.getInstance();
	}

}
