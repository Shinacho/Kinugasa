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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import kinugasa.game.GameLog;
import kinugasa.game.I18N;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNull;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/10/15_14:51:25<br>
 * @author Shinacho<br>
 */
public class ActionStorage extends DBStorage<Action> {

	private static final ActionStorage INSTANCE = new ActionStorage();

	private ActionStorage() {
	}
	public final Item 両手持ち
			= new Item("TWO_HAND", I18N.get(GameSystemI18NKeys.両手持ち))
					.setSlot(EqipSlot.左手)
					.setDesc(I18N.get(GameSystemI18NKeys.両手持ちすると右手の効果が２倍になる));//no pack!;
	public final Item 両手持ち_弓
			= new Item("TWO_HAND_BOW", I18N.get(GameSystemI18NKeys.両手持ち))
					.setSlot(EqipSlot.右手)
					.setDesc(I18N.get(GameSystemI18NKeys.弓は両手で持つ必要がある));//no pack!;

	public static ActionStorage getInstance() {
		return INSTANCE;
	}

	private static final Map<String, Integer> ITEM_BAG_ITEM = new HashMap<>();
	private static final Map<String, Integer> BOOK_BAG_ITEM = new HashMap<>();

	public static void addItemBagItem(String id, int addMax) {
		ITEM_BAG_ITEM.put(id, addMax);
	}

	public static void addBookBagItem(String id, int addMax) {
		BOOK_BAG_ITEM.put(id, addMax);
	}

	public static boolean isItemBagItem(String id) {
		return ITEM_BAG_ITEM.containsKey(id);
	}

	public static int getItemBagAddSize(String id) {
		return ITEM_BAG_ITEM.get(id);
	}

	public static boolean isBookBagItem(String id) {
		return BOOK_BAG_ITEM.containsKey(id);
	}

	public static int getBookBagAddSize(String id) {
		return BOOK_BAG_ITEM.get(id);
	}

	public void checkAll() throws GameSystemException {
		GameLog.print("----------ACTION_CHECK start --------------------");
		for (Action a : selectAll()) {
			GameLog.print(a.getId() + "[" + a.getVisibleName() + "] OK");
		}
		GameLog.print("----------ACTION_CHECK end --------------------");
	}

	@Override
	protected Action select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select ID,VISIBLENAME,DESCRIPTION,ACTIONTYPE,FIELD,BATTLE,AREA,CASTTIME,TGTTYPE,TGTDEAD,SUMMARY"
					+ " from action"
					+ " where id = '" + id + "';";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				sql = "select ID,VISIBLENAME,"
						+ "DESCRIPTION,FIELD,BATTLE,AREA,"
						+ "TGTTYPE,TGTDEAD,PRICE,EQIPSLOT,"
						+ "ATKCOUNT,WEAPONTYPE,STYLENAME,ENCHANTNAME,DCS,UPGRADENUM,SUMMARY"
						+ " from item"
						+ " where id = '" + id + "';";
				r = DBConnection.getInstance().execDirect(sql);
				if (r.isEmpty()) {
					throw new NameNotFoundException("action / item not found : " + id);
				}
				//item
				for (List<DBValue> l : r) {
					Item i = new Item(id, l.get(1).get());
					i.setDesc(l.get(2).get());
					i.setField(l.get(3).asBoolean());
					i.setBattle(l.get(4).asBoolean());
					i.setArea(l.get(5).asInt());
					i.setTgtType(l.get(6).of(Action.ターゲットモード.class));
					i.set死亡者ターゲティング(l.get(7).orNull(Action.死亡者ターゲティング.class));
					i.setPrice(l.get(8).asInt());
					i.setSlot(l.get(9).orNull(EqipSlot.class));
					i.setAtkCount(l.get(10).asInt());
					i.setWeaponType(l.get(11).of(WeaponType.class));
					i.setStyle(l.get(12).orNull(ItemStyle.class));
					i.setEnchant(l.get(13).orNull(ItemEnchant.class));
					i.setDcs(l.get(14).orNull(StatusKey.class));
					i.setCurrentUpgradeNum(l.get(15).asInt());
					i.setSummary(l.get(16).get());
					//event
					i.setMainEvents(getMainEvents(id));
					i.setUserEvents(getUserEvents(id));
					//eqipTErm
					i.setTerms(getTerms(i.getId()));
					//mateirla
					i.setMaterial(getMaterial(i.getId()));
					//Status
					i.setStatus(getStatus(i.getId()));
					//attrIn
					i.setAttrIn(getAttrIn(i.getId()));
					//attrOut
					i.setAttrOut(getAttrOut(i.getId()));
					//cndRegist
					i.setConditionRegist(getCndRegist(i.getId()));
					//pack
					return i.pack();
				}
			}
			//action
			for (List<DBValue> l : r) {
				Action a = new Action(id, l.get(1).get(), l.get(3).of(ActionType.class));
				a.setDesc(l.get(2).get());
				a.setField(l.get(4).asBoolean());
				a.setBattle(l.get(5).asBoolean());
				a.setArea(l.get(6).asInt());
				a.setCastTime(l.get(7).asInt());
				a.setTgtType(l.get(8).orNull(Action.ターゲットモード.class));
				a.set死亡者ターゲティング(l.get(9).orNull(Action.死亡者ターゲティング.class));
				a.setSummary(l.get(10).get());
				//event
				a.setMainEvents(getMainEvents(id));
				a.setUserEvents(getUserEvents(id));
				//pack
				return a.pack();
			}
		}
		throw new NameNotFoundException("action / item not found : " + id);
	}

	@Override
	protected List<Action> selectAll() throws KSQLException {
		List<Action> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select ID,VISIBLENAME,DESCRIPTION,ACTIONTYPE,FIELD,BATTLE,AREA,CASTTIME,TGTTYPE,TGTDEAD,SUMMARY"
					+ " from action;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			//action
			for (List<DBValue> l : r) {
				Action a = new Action(l.get(0).get(), l.get(1).get(), l.get(3).of(ActionType.class));
				a.setDesc(l.get(2).get());
				a.setField(l.get(4).asBoolean());
				a.setBattle(l.get(5).asBoolean());
				a.setArea(l.get(6).asInt());
				a.setCastTime(l.get(7).asInt());
				a.setTgtType(l.get(8).orNull(Action.ターゲットモード.class));
				a.set死亡者ターゲティング(l.get(9).orNull(Action.死亡者ターゲティング.class));
				a.setSummary(l.get(10).get());
				//event
				a.setMainEvents(getMainEvents(l.get(0).get()));
				a.setUserEvents(getUserEvents(l.get(0).get()));
				//pack
				res.add(a.pack());
			}
			sql = "select ID,VISIBLENAME,"
					+ "DESCRIPTION,FIELD,BATTLE,AREA,"
					+ "TGTTYPE,TGTDEAD,PRICE,EQIPSLOT,"
					+ "ATKCOUNT,WEAPONTYPE,STYLENAME,ENCHANTNAME,DCS,UPGRADENUM,SUMMARY"
					+ " from item;";
			r = DBConnection.getInstance().execDirect(sql);
			//item
			for (List<DBValue> l : r) {
				Item i = new Item(l.get(0).get(), l.get(1).get());
				i.setDesc(l.get(2).get());
				i.setField(l.get(3).asBoolean());
				i.setBattle(l.get(4).asBoolean());
				i.setArea(l.get(5).asInt());
				i.setTgtType(l.get(6).of(Action.ターゲットモード.class));
				i.set死亡者ターゲティング(l.get(7).orNull(Action.死亡者ターゲティング.class));
				i.setPrice(l.get(8).asInt());
				i.setSlot(l.get(9).orNull(EqipSlot.class));
				i.setAtkCount(l.get(10).asInt());
				i.setWeaponType(l.get(11).of(WeaponType.class));
				i.setStyle(l.get(12).orNull(ItemStyle.class));
				i.setEnchant(l.get(13).orNull(ItemEnchant.class));
				i.setDcs(l.get(14).orNull(StatusKey.class));
				i.setCurrentUpgradeNum(l.get(15).asInt());
				i.setSummary(l.get(16).get());
				//event
				i.setMainEvents(getMainEvents(l.get(0).get()));
				i.setUserEvents(getUserEvents(l.get(0).get()));
				//eqipTErm
				i.setTerms(getTerms(i.getId()));
				//mateirla
				i.setMaterial(getMaterial(i.getId()));
				//Status
				i.setStatus(getStatus(i.getId()));
				//attrIn
				i.setAttrIn(getAttrIn(i.getId()));
				//attrOut
				i.setAttrOut(getAttrOut(i.getId()));
				//cndRegist
				i.setConditionRegist(getCndRegist(i.getId()));
				//pack
				res.add(i.pack());
			}
		}
		return res;
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select count(*) from action;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			int c = r.cell(0, 0).asInt();
			sql = "select count(*) from item;";
			r = DBConnection.getInstance().execDirect(sql);
			c += r.cell(0, 0).asInt();
			return c;
		}
		return 0;
	}

	public List<Action> allActions() {
		List<Action> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select ID,VISIBLENAME,DESCRIPTION,ACTIONTYPE,FIELD,BATTLE,AREA,CASTTIME,TGTTYPE,TGTDEAD,SUMMARY"
					+ " from action;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			//action
			for (List<DBValue> l : r) {
				Action a = new Action(l.get(0).get(), l.get(1).get(), l.get(3).of(ActionType.class));
				a.setDesc(l.get(2).get());
				a.setField(l.get(4).asBoolean());
				a.setBattle(l.get(5).asBoolean());
				a.setArea(l.get(6).asInt());
				a.setCastTime(l.get(7).asInt());
				a.setTgtType(l.get(8).orNull(Action.ターゲットモード.class));
				a.set死亡者ターゲティング(l.get(9).orNull(Action.死亡者ターゲティング.class));
				a.setSummary(l.get(10).get());
				//event
				a.setMainEvents(getMainEvents(l.get(0).get()));
				a.setUserEvents(getUserEvents(l.get(0).get()));
				//pack
				res.add(a.pack());
			}
		}
		return res;
	}

	public List<Item> allItems() {
		List<Item> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select ID,VISIBLENAME,"
					+ "DESCRIPTION,FIELD,BATTLE,AREA,"
					+ "TGTTYPE,TGTDEAD,PRICE,EQIPSLOT,"
					+ "ATKCOUNT,WEAPONTYPE,STYLENAME,ENCHANTNAME,DCS,UPGRADENUM,SUMMARY"
					+ " from item;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			//item
			for (List<DBValue> l : r) {
				Item i = new Item(l.get(0).get(), l.get(1).get());
				i.setDesc(l.get(2).get());
				i.setField(l.get(3).asBoolean());
				i.setBattle(l.get(4).asBoolean());
				i.setArea(l.get(5).asInt());
				i.setTgtType(l.get(6).of(Action.ターゲットモード.class));
				i.set死亡者ターゲティング(l.get(7).orNull(Action.死亡者ターゲティング.class));
				i.setPrice(l.get(8).asInt());
				i.setSlot(l.get(9).orNull(EqipSlot.class));
				i.setAtkCount(l.get(10).asInt());
				i.setWeaponType(l.get(11).of(WeaponType.class));
				i.setStyle(l.get(12).orNull(ItemStyle.class));
				i.setEnchant(l.get(13).orNull(ItemEnchant.class));
				i.setDcs(l.get(14).orNull(StatusKey.class));
				i.setCurrentUpgradeNum(l.get(15).asInt());
				i.setSummary(l.get(16).get());
				//event
				i.setMainEvents(getMainEvents(l.get(0).get()));
				i.setUserEvents(getUserEvents(l.get(0).get()));
				//eqipTErm
				i.setTerms(getTerms(i.getId()));
				//mateirla
				i.setMaterial(getMaterial(i.getId()));
				//Status
				i.setStatus(getStatus(i.getId()));
				//attrIn
				i.setAttrIn(getAttrIn(i.getId()));
				//attrOut
				i.setAttrOut(getAttrOut(i.getId()));
				//cndRegist
				i.setConditionRegist(getCndRegist(i.getId()));
				//pack
				res.add(i.pack());
			}
		}
		return res;
	}

	{
		//行動アクションのデータをメモリに搭載してしまう
		super.add(new Action(BattleConfig.ActionID.移動, I18N.get(GameSystemI18NKeys.移動), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.確定, I18N.get(GameSystemI18NKeys.確定), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.防御, I18N.get(GameSystemI18NKeys.防御), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.回避, I18N.get(GameSystemI18NKeys.回避), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.状態, I18N.get(GameSystemI18NKeys.状態), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.逃走, I18N.get(GameSystemI18NKeys.逃走), ActionType.行動) {
			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		//item
		super.add(両手持ち);
		super.add(両手持ち_弓);
	}

	@NewInstance
	public Action actionOf(String id) throws NameNotFoundException {
		//メモリ探索
		for (Actor a : GameSystem.getInstance().getParty()) {
			if (a.getStatus().getActions().contains(id)) {
				return a.getStatus().getActions().get(id).clone();
			}
		}
		//DB検索
		Action a = get(id);
		if (a != null) {
			return a;
		}
		throw new NameNotFoundException(id + " is not found");
	}

	@NewInstance
	public Item itemOf(String id) throws NameNotFoundException {
		//メモリ探索
		for (Actor a : GameSystem.getInstance().getParty()) {
			if (a.getStatus().getItemBag().contains(id)) {
				return a.getStatus().getItemBag().get(id).clone();
			}
		}
		//DB検索
		Action a = get(id);
		if (a instanceof Item) {
			return (Item) a;
		}
		throw new NameNotFoundException(id + " is not found");
	}

	@NewInstance
	public Book bookOf(String id) {
		//メモリ探索
		for (Actor a : GameSystem.getInstance().getParty()) {
			if (a.getStatus().getBookBag().contains(id)) {
				return a.getStatus().getBookBag().get(id).clone();
			}
		}
		//DB検索
		Action a = get(id);
		if (a.getType() != ActionType.魔法) {
			throw new GameSystemException("book action is not magic : " + id);
		}
		return new Book(a);
	}

	private boolean loaded = false;

	@NewInstance
	@NotNull
	public List<Action> allOf(ActionType t) {
		//初回呼び出しでメモリ搭載する。行動アクションは最初から乗ってる。
		List<Action> l = stream().filter(p -> p.getType() == t).collect(Collectors.toList());
		if (!loaded && (t == ActionType.攻撃)) {
			super.addAll(l);
			loaded = true;
		}
		return l;
	}

	public static class AnimationData implements Nameable {

		public final String id;
		public final String fileName;
		public final int w, h;
		public final int[] tc;

		public AnimationData(String id, String fileName, int w, int h, int[] tc) {
			this.id = id;
			this.fileName = fileName;
			this.w = w;
			this.h = h;
			this.tc = tc;
		}

		@Override
		public String getName() {
			return id;
		}

		public String getId() {
			return id;
		}

	}

	private ConditionRegist getCndRegist(String itemID) {
		String sql = "select id, cndKey, val"
				+ " from item_cndregist ic left join cndEffect c on ic.cndid = c.id"
				+ " where ic.itemID = '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return new ConditionRegist();
		}
		ConditionRegist res = new ConditionRegist();
		for (List<DBValue> l : r) {
			ConditionKey k = l.get(1).of(ConditionKey.class);
			float val = l.get(2).asFloat();
			res.put(k, val);
		}
		return res;
	}

	private AttributeValueSet getAttrOut(String itemID) {
		String sql = "select id,attrkey,val"
				+ " from item_attrOut ia left join attrEffect e on ia.attrID = e.id"
				+ " where ia.itemID= '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return new AttributeValueSet();
		}
		AttributeValueSet vs = new AttributeValueSet();
		for (List<DBValue> l : r) {
			AttributeKey k = l.get(1).of(AttributeKey.class);
			float val = l.get(2).asFloat();
			vs.add(new AttributeValue(k, val));
		}
		return vs;
	}

	private AttributeValueSet getAttrIn(String itemID) {
		String sql = "select id,attrkey,val"
				+ " from item_attrIn ia left join attrEffect e on ia.attrID = e.id"
				+ " where ia.itemID= '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return new AttributeValueSet();
		}
		AttributeValueSet vs = new AttributeValueSet();
		for (List<DBValue> l : r) {
			AttributeKey k = l.get(1).of(AttributeKey.class);
			float val = l.get(2).asFloat();
			vs.add(new AttributeValue(k, val));
		}
		return vs;
	}

	private StatusValueSet getStatus(String itemID) {
		String sql = "select id,statusKEy,val"
				+ " from item_status ss left join statusEffect e on ss.statusID = e.id"
				+ " where ss.itemID= '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return new StatusValueSet();//no keys
		}
		StatusValueSet vs = new StatusValueSet();
		for (List<DBValue> l : r) {
			StatusKey sk = l.get(1).of(StatusKey.class);
			float val = l.get(2).asFloat();
			vs.add(new StatusValue(sk, val));
		}
		return vs;
	}

	private Map<Material, Integer> getMaterial(String itemID) {
		String sql = "select materialID, num "
				+ " from item_material im left join material m on im.materialid = m.id"
				+ " where im.itemid = '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		Map<Material, Integer> res = new HashMap<>();
		for (List<DBValue> l : r) {
			String materialID = l.get(0).get();
			int num = l.get(1).asInt();
			Material m = MaterialStorage.getInstance().get(materialID);
			res.put(m, num);
		}
		return res;
	}

	private EnumSet<ItemEqipTerm> getTerms(String itemID) {
		String sql = "select eqipTermID,Name"
				+ " from item_eqipterm it"
				+ " where it.itemid = '" + itemID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		EnumSet<ItemEqipTerm> res = EnumSet.noneOf(ItemEqipTerm.class);
		for (List<DBValue> l : r) {
			ItemEqipTerm t = l.get(1).orNull(ItemEqipTerm.class);
			if (t != null) {
				res.add(t);
			}
		}
		return res;
	}

	private AnimationSprite getAnimation(String actionID, String tableName) {
		String sql = "select ANIMATIONID,FILENAME,WSIZE,HSIZE,SPEED"
				+ " from " + tableName + " ea left join animation a on ea.animationid = a.id"
				+ " where ea.actionid = '" + actionID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return null;
		}
		//1件しか入っていない
		List<DBValue> l = r.row(0);
		//0,0
		String id = l.get(0).get();
		String fileName = l.get(1).get();
		int w = l.get(2).asInt();
		int h = l.get(3).asInt();
		int[] speed = l.get(4).asSafeIntArray(",");
		Animation a = new Animation(new FrameTimeCounter(speed), new SpriteSheet(fileName).rows(0, w, h).images());
		a.setRepeat(false);
		AnimationSprite s = new AnimationSprite(a);
		s.setId(id);
		return s;
	}

	public AnimationSprite getAnimation(String animationId) {
		String sql = "select ANIMATIONID,FILENAME,WSIZE,HSIZE,SPEED"
				+ " from animation a "
				+ " where a.id = '" + animationId + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return null;
		}
		//1件しか入っていない
		List<DBValue> l = r.row(0);
		//0,0
		String id = l.get(0).get();
		String fileName = l.get(1).get();
		int w = l.get(2).asInt();
		int h = l.get(3).asInt();
		int[] speed = l.get(4).asSafeIntArray(",");
		Animation a = new Animation(new FrameTimeCounter(speed), new SpriteSheet(fileName).rows(0, w, h).images());
		a.setRepeat(false);
		AnimationSprite s = new AnimationSprite(a);
		s.setId(id);
		return s;
	}

	private List<ActionEvent> getMainEvents(String actionID) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,"
				+ "P,CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME"
				+ " from action_mainEvent me left join actionEvent e on me.eventId = e.id"
				+ " where me.actionId = '" + actionID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return Collections.emptyList();
		}
		List<ActionEvent> res = new ArrayList<>();
		for (List<DBValue> l : r) {
			//光線イベントの判定
			if (BeamEffectEvents.getInstance().has(l.get(1).get())) {
				ActionEvent e = BeamEffectEvents.getInstance().of(l.get(1).of(BeamEffectEvents.Key.class));
				e.set起動条件(l.get(17).of(ActionEvent.起動条件.class));
				res.add(e);
				continue;
			}

			ActionEvent e = new ActionEvent(l.get(1).get());
			e.setSort(l.get(2).asInt());
			e.setEventType(l.get(3).of(ActionEventType.class));
			e.setTgtStatusKey(l.get(4).orNull(StatusKey.class));
			e.setP(l.get(5).asFloat());
			e.setTgtConditionKey(l.get(6).orNull(ConditionKey.class));
			e.setCndTime(l.get(7).asInt());
			e.setAtkAttr(l.get(8).orNull(AttributeKey.class));
			e.setTgtAttrKeyin(l.get(9).orNull(AttributeKey.class));
			e.setTgtAttrKeyOut(l.get(10).orNull(AttributeKey.class));
			e.setCndRegist(l.get(11).orNull(ConditionKey.class));
			e.setTgtID(l.get(12).get());
			e.setNoLimit(l.get(13).asBoolean());
			e.setValue(l.get(14).asFloat());
			e.setCalcMode(l.get(15).orNull(ActionEvent.CalcMode.class));
			String soundID = l.get(16).get();
			if (soundID != null) {
				e.setSuccessSound(SoundStorage.getInstance().get(soundID));
			}
			e.set起動条件(l.get(17).of(ActionEvent.起動条件.class));
			e.setWaitTime(l.get(18).asInt());
			//TERM
			sql = "select "
					+ "id,typ,tgtName,Val"
					+ " from event_term et left join eventterm t on et.termid = t.id"
					+ " where et.eventid = '" + e.getId() + "';";
			KResultSet tr = DBConnection.getInstance().execDirect(sql);
			List<ActionEvent.Term> terms = new ArrayList<>();
			for (List<DBValue> tl : tr) {
				String id = tl.get(0).get();
				ActionEvent.Term.Type type = tl.get(1).of(ActionEvent.Term.Type.class);
				String tgtName = tl.get(2).get();
				float val = tl.get(3).asFloat();
				terms.add(new ActionEvent.Term(id, type, val, tgtName));
			}
			e.setTerms(terms);
			//ANIMATION
			e.setTgtAnimation(getAnimation(e.getId(), "Event_TgtAnimation"));
			e.setOtherAnimation(getAnimation(e.getId(), "Event_OtherAnimation"));
			e.setUserAnimation(getAnimation(e.getId(), "Event_UserAnimation"));
			//
			res.add(e);
		}
		return res;
	}

	private List<ActionEvent> getUserEvents(String actionID) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,P,"
				+ "CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME"
				+ " from action_userEvent ue left join actionEvent e on ue.eventId = e.id"
				+ " where ue.actionId = '" + actionID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return Collections.emptyList();
		}
		List<ActionEvent> res = new ArrayList<>();
		for (List<DBValue> l : r) {
			ActionEvent e = new ActionEvent(l.get(1).get());
			e.setSort(l.get(2).asInt());
			e.setEventType(l.get(3).of(ActionEventType.class));
			e.setTgtStatusKey(l.get(4).orNull(StatusKey.class));
			e.setP(l.get(5).asFloat());
			e.setTgtConditionKey(l.get(6).orNull(ConditionKey.class));
			e.setCndTime(l.get(7).asInt());
			e.setAtkAttr(l.get(8).orNull(AttributeKey.class));
			e.setTgtAttrKeyin(l.get(9).orNull(AttributeKey.class));
			e.setTgtAttrKeyOut(l.get(10).orNull(AttributeKey.class));
			e.setCndRegist(l.get(11).orNull(ConditionKey.class));
			e.setTgtID(l.get(12).get());
			e.setNoLimit(l.get(13).asBoolean());
			e.setValue(l.get(14).asFloat());
			e.setCalcMode(l.get(15).orNull(ActionEvent.CalcMode.class));
			String soundID = l.get(16).get();
			if (soundID != null) {
				e.setSuccessSound(SoundStorage.getInstance().get(soundID));
			}
			e.set起動条件(l.get(17).of(ActionEvent.起動条件.class));
			e.setWaitTime(l.get(18).asInt());
			//TERM
			sql = "select "
					+ "id,typ,tgtName,Val"
					+ " from event_term et left join eventterm t on et.termid = t.id"
					+ " where et.eventid = '" + e.getId() + "';";
			KResultSet tr = DBConnection.getInstance().execDirect(sql);
			List<ActionEvent.Term> terms = new ArrayList<>();
			for (List<DBValue> tl : tr) {
				String id = tl.get(0).get();
				ActionEvent.Term.Type type = tl.get(1).of(ActionEvent.Term.Type.class);
				String tgtName = tl.get(2).get();
				float val = tl.get(3).asFloat();
				terms.add(new ActionEvent.Term(id, type, val, tgtName));
			}
			e.setTerms(terms);
			//ANIMATION
			e.setTgtAnimation(getAnimation(e.getId(), "Event_TgtAnimation"));
			e.setOtherAnimation(getAnimation(e.getId(), "Event_OtherAnimation"));
			e.setUserAnimation(getAnimation(e.getId(), "Event_UserAnimation"));
			//
			res.add(e);
		}
		return res;
	}

}
