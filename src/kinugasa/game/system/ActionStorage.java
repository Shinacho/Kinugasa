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
import kinugasa.game.GameLog;
import kinugasa.game.I18N;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNull;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.NameNotFoundException;
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

	public enum InstanceType {
		ACTION, ITEM
	}

	public InstanceType getInstanceType(String id) {
		String sql = "select id from action where id = '" + id + "'";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (!r.isEmpty()) {
			return InstanceType.ACTION;
		}
		sql = "select id from item where id = '" + id + "'";
		r = DBConnection.getInstance().execDirect(sql);
		if (!r.isEmpty()) {
			return InstanceType.ITEM;
		}
		throw new NameNotFoundException("action/item is not found : " + id);

	}

	public void checkAll() throws GameSystemException {
		GameLog.print("----------ACTION_CHECK start --------------------");
		for (Action a : selectAll()) {
			String s
					= a.getType() == ActionType.アイテム
					? "this is"
					+ (((Item) a).isEqip() ? " eqip item " : " item")
					: "action";

			GameLog.print(">" + a.getId() + "[" + a.getVisibleName() + "] OK : " + s);
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
				sql = "select "
						+ "id,visibleName,description,"
						+ "field,battle,area,"
						+ "tgtType,tgtDead,price,"
						+ "eqipSlot,atkCount,weaponType,"
						+ "styleName,enchantName,DCS,"
						+ "upgradeNum,summary,eqipTermCSV,"
						+ "cndRegistCSV,attrInCSV,attrOutCSV,"
						+ "statusCSV,materialCSV,canSale,"
						+ "unEqip"
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
					//eqipTErm
					i.setTerms(getEqipTerms(l.get(17).get()));
					//cndRegist
					i.setConditionRegist(getCndRegist(l.get(18).get()));
					//attrIn
					i.setAttrIn(getAttrIn(l.get(19).get()));
					//attrOut
					i.setAttrOut(getAttrOut(l.get(20).get()));
					//Status
					i.setStatus(getStatus(l.get(21).get()));
					//mateirla
					i.setMaterial(getMaterial(l.get(22).get()));
					//
					i.setCanSale(l.get(23).asBoolean());
					i.setCanUnEqip(l.get(24).asBoolean());
					//event
					i.setMainEvents(getMainEvents(id));
					i.setUserEvents(getUserEvents(id));
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
			sql = "select "
					+ "id,visibleName,description,"
					+ "field,battle,area,"
					+ "tgtType,tgtDead,price,"
					+ "eqipSlot,atkCount,weaponType,"
					+ "styleName,enchantName,DCS,"
					+ "upgradeNum,summary,eqipTermCSV,"
					+ "cndRegistCSV,attrInCSV,attrOutCSV,"
					+ "statusCSV,materialCSV,canSale,"
					+ "unEqip"
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
				i.setWeaponType(l.get(11).orNull(WeaponType.class));
				i.setStyle(l.get(12).orNull(ItemStyle.class));
				i.setEnchant(l.get(13).orNull(ItemEnchant.class));
				i.setDcs(l.get(14).orNull(StatusKey.class));
				i.setCurrentUpgradeNum(l.get(15).asInt());
				i.setSummary(l.get(16).get());
				//eqipTErm
				i.setTerms(getEqipTerms(l.get(17).get()));
				//cndRegist
				i.setConditionRegist(getCndRegist(l.get(18).get()));
				//attrIn
				i.setAttrIn(getAttrIn(l.get(19).get()));
				//attrOut
				i.setAttrOut(getAttrOut(l.get(20).get()));
				//Status
				i.setStatus(getStatus(l.get(21).get()));
				//mateirla
				i.setMaterial(getMaterial(l.get(22).get()));
				//
				i.setCanSale(l.get(23).asBoolean());
				i.setCanUnEqip(l.get(24).asBoolean());
				//event
				i.setMainEvents(getMainEvents(l.get(0).get()));
				i.setUserEvents(getUserEvents(l.get(0).get()));
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
		res.addAll(getDirect().values());
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
		return res.stream().distinct().toList();
	}

	public List<Item> allItems() {
		List<Item> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select "
					+ "id,visibleName,description,"
					+ "field,battle,area,"
					+ "tgtType,tgtDead,price,"
					+ "eqipSlot,atkCount,weaponType,"
					+ "styleName,enchantName,DCS,"
					+ "upgradeNum,summary,eqipTermCSV,"
					+ "cndRegistCSV,attrInCSV,attrOutCSV,"
					+ "statusCSV,materialCSV,canSale,"
					+ "unEqip"
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
				i.setWeaponType(l.get(11).orNull(WeaponType.class));
				i.setStyle(l.get(12).orNull(ItemStyle.class));
				i.setEnchant(l.get(13).orNull(ItemEnchant.class));
				i.setDcs(l.get(14).orNull(StatusKey.class));
				i.setCurrentUpgradeNum(l.get(15).asInt());
				i.setSummary(l.get(16).get());
				//eqipTErm
				i.setTerms(getEqipTerms(l.get(17).get()));
				//cndRegist
				i.setConditionRegist(getCndRegist(l.get(18).get()));
				//attrIn
				i.setAttrIn(getAttrIn(l.get(19).get()));
				//attrOut
				i.setAttrOut(getAttrOut(l.get(20).get()));
				//Status
				i.setStatus(getStatus(l.get(21).get()));
				//mateirla
				i.setMaterial(getMaterial(l.get(22).get()));
				//
				i.setCanSale(l.get(23).asBoolean());
				i.setCanUnEqip(l.get(24).asBoolean());
				//event
				i.setMainEvents(getMainEvents(l.get(0).get()));
				i.setUserEvents(getUserEvents(l.get(0).get()));
				//pack
				res.add(i.pack());
			}
		}
		return res;
	}

	{
		//行動アクションのデータをメモリに搭載してしまう
		super.add(new Action(BattleConfig.ActionID.移動, I18N.get(GameSystemI18NKeys.移動), ActionType.行動) {
			{
				setBattle(true);
			}

			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.確定, I18N.get(GameSystemI18NKeys.確定), ActionType.行動) {
			{
				setBattle(true);
			}

			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.防御, I18N.get(GameSystemI18NKeys.防御), ActionType.行動) {
			{
				setBattle(true);
			}

			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.回避, I18N.get(GameSystemI18NKeys.回避), ActionType.行動) {
			{
				setBattle(true);
			}

			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.状態, I18N.get(GameSystemI18NKeys.状態), ActionType.行動) {
			{
				setBattle(true);
			}

			@Override
			public boolean canDo(Status a) {
				return true;
			}
		});
		super.add(new Action(BattleConfig.ActionID.逃走, I18N.get(GameSystemI18NKeys.逃走), ActionType.行動) {
			{
				setBattle(true);
			}

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

	private ConditionRegist getCndRegist(String val) {
		ConditionRegist res = new ConditionRegist();
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			ConditionKey k = ConditionKey.valueOf(s.split("=")[0]);
			if (k == null) {
				throw new NameNotFoundException("ConditionKey is not found :" + val);
			}
			float v = Float.parseFloat(s.split("=")[1]);
			res.put(k, v);
		}
		return res;
	}

	private AttributeValueSet getAttrOut(String val) {
		AttributeValueSet res = new AttributeValueSet();
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			AttributeKey k = AttributeKey.valueOf(s.split("=")[0]);
			if (k == null) {
				throw new NameNotFoundException("AttributeKey is not found :" + val);
			}
			float v = Float.parseFloat(s.split("=")[1]);
			res.add(new AttributeValue(k, v));
		}
		return res;
	}

	private AttributeValueSet getAttrIn(String val) {
		AttributeValueSet res = new AttributeValueSet();
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			AttributeKey k = AttributeKey.valueOf(s.split("=")[0]);
			if (k == null) {
				throw new NameNotFoundException("AttributeKey is not found :" + val);
			}
			float v = Float.parseFloat(s.split("=")[1]);
			res.add(new AttributeValue(k, v));
		}
		return res;
	}

	private StatusValueSet getStatus(String val) {
		StatusValueSet res = new StatusValueSet();
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			StatusKey k = StatusKey.valueOf(s.split("=")[0]);
			if (k == null) {
				throw new NameNotFoundException("StatusKey is not found :" + val);
			}
			float v = Float.parseFloat(s.split("=")[1]);
			res.add(new StatusValue(k, v));
		}
		return res;
	}

	private Map<Material, Integer> getMaterial(String val) {
		Map<Material, Integer> res = new HashMap<>();
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			Material k = MaterialStorage.getInstance().get(s.split("=")[0]);
			if (k == null) {
				throw new NameNotFoundException("Material key is not found :" + val);
			}
			int v = Integer.parseInt(s.split("=")[1]);
			res.put(k, v);
		}
		return res;
	}

	private EnumSet<ItemEqipTerm> getEqipTerms(String val) {
		EnumSet<ItemEqipTerm> res = EnumSet.noneOf(ItemEqipTerm.class);
		if (val == null || val.isEmpty()) {
			return res;
		}
		for (var s : StringUtil.safeSplit(val, ",")) {
			res.add(ItemEqipTerm.valueOf(s));
		}
		return res;
	}

	private HashMap<String, AnimationSprite> animationCache = new HashMap<>();

	public AnimationSprite getAnimation(String animationId) {
		if (animationId == null) {
			return null;
		}
		if (animationCache.containsKey(animationId)) {
			return animationCache.get(animationId).clone();
		}
		String sql = "select ID,FILENAME,WSIZE,HSIZE,SPEED"
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
		animationCache.put(id, s);
		return s;
	}

	private List<ActionEvent> getMainEvents(String actionID) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,"
				+ "P,CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME,userAnimationId,tgtAnimationId,otherAnimationId"
				+ " from action_mainEvent me left join actionEvent e on me.eventId = e.id"
				+ " where me.actionId = '" + actionID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return Collections.emptyList();
		}
		List<ActionEvent> res = new ArrayList<>();
		for (List<DBValue> l : r) {
			//光線イベントの判定
			if (BeamEffectEvents.getInstance().has(l.get(3).get())) {
				ActionEvent e = BeamEffectEvents.getInstance().of(l.get(3).of(BeamEffectEvents.Key.class));
				e.setEvent起動条件(l.get(17).of(ActionEvent.Event起動条件.class));
				res.add(e);
				continue;
			}
			ActionEvent e = createEvent(l);
			res.add(e);
		}
		return res;
	}

	private List<ActionEvent> getUserEvents(String actionID) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,P,"
				+ "CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME,userAnimationId,tgtAnimationId,otherAnimationId"
				+ " from action_userEvent ue left join actionEvent e on ue.eventId = e.id"
				+ " where ue.actionId = '" + actionID + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			return Collections.emptyList();
		}
		List<ActionEvent> res = new ArrayList<>();
		for (List<DBValue> l : r) {
			ActionEvent e = createEvent(l);
			res.add(e);
		}
		return res;
	}

	public ActionEvent eventOf(String eventId) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,P,"
				+ "CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME,userAnimationId,tgtAnimationId,otherAnimationId"
				+ " from action_userEvent ue left join actionEvent e on ue.eventId = e.id"
				+ " where e.id = '" + eventId + "';";
		KResultSet r = DBConnection.getInstance().execDirect(sql);
		if (r.isEmpty()) {
			sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,"
					+ "P,CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,TGTID,NOLIMIT,VAL,CALCMODE,SOUNDID,TRIGGEROPTION,WAITTIME,userAnimationId,tgtAnimationId,otherAnimationId"
					+ " from action_mainEvent me left join actionEvent e on me.eventId = e.id"
					+ " where e.id = '" + eventId + "';";
			r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				throw new NameNotFoundException("event not found : " + eventId);
			}
		}
		return createEvent(r.row(0));
	}

	private ActionEvent createEvent(List<DBValue> l) {
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
		e.setEvent起動条件(l.get(17).of(ActionEvent.Event起動条件.class));
		e.setWaitTime(l.get(18).asInt());
		//TERM
		e.setUser起動条件(getActor起動条件("EventUserTerm", e.getId()));
		e.setTgt起動条件(getActor起動条件("EventTgtTerm", e.getId()));
		//ANIMATION
		e.setUserAnimation(getAnimation(l.get(19).get()));
		e.setTgtAnimation(getAnimation(l.get(20).get()));
		e.setOtherAnimation(getAnimation(l.get(21).get()));
		//
		return e;
	}

	private List<ActionEvent.Actor起動条件> getActor起動条件(String tableName, String eventID) {
		String sql = "select "
				+ "id,typ,tgtName,Val"
				+ " from " + tableName
				+ " where eventid = '" + eventID + "';";
		KResultSet tr = DBConnection.getInstance().execDirect(sql);
		List<ActionEvent.Actor起動条件> terms = new ArrayList<>();
		for (List<DBValue> tl : tr) {
			String id = tl.get(0).get();
			ActionEvent.Actor起動条件.Type type = tl.get(1).of(ActionEvent.Actor起動条件.Type.class);
			String tgtName = tl.get(2).get();
			float val = tl.get(3).asFloat();
			terms.add(new ActionEvent.Actor起動条件(id, type, val, tgtName));
		}
		return terms;
	}

	public List<Item> itemOf(String... ids) {
		List<Item> res = new ArrayList<>();
		for (var v : ids) {
			res.add(itemOf(v));
		}
		return res;
	}

}
