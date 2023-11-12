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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.NotNewInstance;
import kinugasa.game.Nullable;
import kinugasa.game.field4.*;
import kinugasa.resource.NameNotFoundException;

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
	private List<Actor> party = new ArrayList();

	private final MoneySystem moneySystem;
	;
	private final BattleSystem battleSystem;
	private GameMode mode = GameMode.FIELD;

	public void initBattleSystem(List<Actor> chara) {
		party = new ArrayList<>(chara);
//		for (int i = 0; i < chara.size(); i++) {
//			chara.get(i).setOrder(i);
//		}
	}

	public void initFieldSystem(List<Actor> chara) {
		party = new ArrayList<>(chara);
		FieldMap.setPlayerCharacter(chara.stream().map(v -> v.getSprite()).collect(Collectors.toList()));
	}

	@NotNewInstance
	public List<Actor> getParty() {
		return party;
	}

	public void updateParty() {
		for (Actor pc : party) {
			if (!FieldMap.getPlayerCharacter().contains(pc.getSprite())) {
				FieldMap.getPlayerCharacter().add(pc.getSprite());
			}
		}
	}

	@Nullable
	public Actor getPCbyID(String id) {
		for (Actor a : party) {
			if (a.getId().equals(id)) {
				return a;
			}
		}
		return null;
	}

	public List<PCSprite> getPartySprite() {
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

	@Deprecated
	public void setModeOverride(GameMode m) {
		this.mode = m;
	}

	private String icon;

	public void battleStart(EncountInfo enc) {
		setMode(GameMode.BATTLE);
		battleSystem.encountInit(enc);
	}

	public BattleResultValues battleEnd() {
		setMode(GameMode.FIELD);
		battleSystem.endBattle();
		EnemyBlueprint.initEnemyNoMap();
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

	private EnchantBag enchantBag = new EnchantBag();

	public EnchantBag getEnchantBag() {
		return enchantBag;
	}

	public int getOrder(Actor a) {
		for (int i = 0; i < party.size(); i++) {
			if (party.get(i).equals(a)) {
				return i;
			}
		}
		return -1;
	}

}
