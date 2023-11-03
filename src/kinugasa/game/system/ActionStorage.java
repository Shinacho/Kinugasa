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
import kinugasa.game.I18N;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNull;
import kinugasa.game.Nullable;
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
					//animation
					i.setUserAnimation(getAnimation(id, "Action_UserAnimation"));
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
				//animation
				a.setUserAnimation(getAnimation(id, "Action_UserAnimation"));
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
				//animation
				a.setUserAnimation(getAnimation(l.get(0).get(), "Action_UserAnimation"));
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
				//animation
				i.setUserAnimation(getAnimation(l.get(0).get(), "Action_UserAnimation"));
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
				//animation
				a.setUserAnimation(getAnimation(l.get(0).get(), "Action_UserAnimation"));
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
				//animation
				i.setUserAnimation(getAnimation(l.get(0).get(), "Action_UserAnimation"));
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
		add(new Action(BattleConfig.ActionID.移動, I18N.get(GameSystemI18NKeys.移動), ActionType.行動));
		add(new Action(BattleConfig.ActionID.確定, I18N.get(GameSystemI18NKeys.確定), ActionType.行動));
		add(new Action(BattleConfig.ActionID.防御, I18N.get(GameSystemI18NKeys.防御), ActionType.行動));
		add(new Action(BattleConfig.ActionID.回避, I18N.get(GameSystemI18NKeys.回避), ActionType.行動));
		add(new Action(BattleConfig.ActionID.状態, I18N.get(GameSystemI18NKeys.状態), ActionType.行動));
		add(new Action(BattleConfig.ActionID.逃走, I18N.get(GameSystemI18NKeys.逃走), ActionType.行動));
		add(両手持ち);
		add(両手持ち_弓);
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
		//初回呼び出しでメモリ搭載する
		List<Action> l = stream().filter(p -> p.getType() == t).collect(Collectors.toList());
		if (!loaded) {
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

	public Map<String, List<String>> createInsertCSV(Action a, Storage<AnimationData> animations) {
		if (a instanceof Item) {
			return createInsertCSV((Item) a, animations);
		}
		Map<String, List<String>> res = new HashMap<>();
		//ACTION
		List<String> v = new ArrayList<>();
		v.add(val(a.getId()));
		v.add(val(a.getVisibleName()));
		v.add(val(a.getDesc()));
		v.add(val(a.getType()));
		v.add(val(a.isField()));
		v.add(val(a.isBattle()));
		v.add(val(a.getArea()));
		v.add(val(a.getCastTime()));
		v.add(val(a.getTgtType()));
		v.add(val(a.getDeadTgt()));
		v.add(val(a.getSummary()));
		res.put("ACTION", List.of(String.join(",", v)));

		//Action_MainEvent
		for (ActionEvent e : a.getMainEvents()) {
			v = new ArrayList<>();
			v.add(val(a.getId()));
			v.add(val(e.getId()));
			if (res.containsKey("ACTION_MAINEVENT")) {
				res.get("ACTION_MAINEVENT").add(String.join(",", v));
			} else {
				res.put("ACTION_MAINEVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Action_UserEvent
		for (ActionEvent e : a.getMainEvents()) {
			v = new ArrayList<>();
			v.add(val(a.getId()));
			v.add(val(e.getId()));
			if (res.containsKey("ACTION_USEREVENT")) {
				res.get("ACTION_USEREVENT").add(String.join(",", v));
			} else {
				res.put("ACTION_USEREVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//ActionEvent
		for (ActionEvent e : a.getAllEvents()) {
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getSort()));
			v.add(val(e.getEventType()));
			v.add(val(e.getTgtStatusKey()));
			v.add(val(e.getP()));
			v.add(val(e.getTgtConditionKey()));
			v.add(val(e.getCndTime()));
			v.add(val(e.getAtkAttr()));
			v.add(val(e.getTgtAttrIn()));
			v.add(val(e.getTgtAttrOut()));
			v.add(val(e.getTgtCndRegist()));
			v.add(val(e.getTgtItemID()));
			v.add(val(e.isNoLimit()));
			v.add(val(e.getValue()));
			v.add(val(e.getCalcMode()));
			v.add(val(e.getSuccessSound().getName()));
			if (res.containsKey("ACTIONEVENT")) {
				res.get("ACTIONEVENT").add(String.join(",", v));
			} else {
				res.put("ACTIONEVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Event_term
		for (ActionEvent e : a.getAllEvents()) {
			for (ActionEvent.Term t : e.getTerms()) {
				v = new ArrayList<>();
				v.add(val(e.getId()));
				v.add(val(t.id));
				if (res.containsKey("EVENT_TERM")) {
					res.get("EVENT_TERM").add(String.join(",", v));
				} else {
					res.put("EVENT_TERM", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//EventTerm
		for (ActionEvent e : a.getAllEvents()) {
			for (ActionEvent.Term t : e.getTerms()) {
				v = new ArrayList<>();
				v.add(val(t.id));
				v.add(val(t.type));
				v.add(val(t.tgtName));
				v.add(val(t.value));
				if (res.containsKey("EVENTTERM")) {
					res.get("EVENTTERM").add(String.join(",", v));
				} else {
					res.put("EVENTTERM", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//アニメーション整合性チェック
		if (a.getUserAnimation() != null) {
			if (!animations.contains(a.getUserAnimation().getId())) {
				throw new IllegalArgumentException("user animation not found : " + a.getUserAnimation().getId());
			}
		}
		for (ActionEvent e : a.getAllEvents()) {
			if (e.getTgtAnimation() != null) {
				if (!animations.contains(e.getTgtAnimation().getId())) {
					throw new IllegalArgumentException("tgt animation not found : " + e.getTgtAnimation().getId());
				}
			}
			if (e.getOtherAnimation() != null) {
				if (!animations.contains(e.getOtherAnimation().getId())) {
					throw new IllegalArgumentException("other animation not found : " + e.getOtherAnimation().getId());
				}
			}
		}

		//Action_UserAnimation
		if (a.getUserAnimation() != null) {
			v = new ArrayList<>();
			v.add(val(a.getId()));
			v.add(val(a.getUserAnimation().getId()));
			res.put("ACTION_USERANIMATION", List.of(String.join(",", v)));
		}
		//Event_TgtAnimation
		for (ActionEvent e : a.getAllEvents()) {
			if (e.getTgtAnimation() == null) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getTgtAnimation().getId()));
			if (res.containsKey("EVENT_TGTANIMATION")) {
				res.get("EVENT_TGTANIMATION").add(String.join(",", v));
			} else {
				res.put("EVENT_TGTANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Event_OtherAnimation
		for (ActionEvent e : a.getAllEvents()) {
			if (e.getOtherAnimation() == null) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getOtherAnimation().getId()));
			if (res.containsKey("EVENT_OTHERANIMATION")) {
				res.get("EVENT_OTHERANIMATION").add(String.join(",", v));
			} else {
				res.put("EVENT_OTHERANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}

		//ANIMATION
		if (a.getUserAnimation() != null) {
			AnimationData d = animations.get(a.getUserAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		for (ActionEvent e : a.getAllEvents()) {
			if (e.getTgtAnimation() == null) {
				continue;
			}
			AnimationData d = animations.get(e.getTgtAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		for (ActionEvent e : a.getAllEvents()) {
			if (e.getOtherAnimation() == null) {
				continue;
			}
			AnimationData d = animations.get(e.getOtherAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		return res;
	}

	//マテリアルの重複に注意せよ！
	public Map<String, List<String>> createInsertCSV(Item i, Storage<AnimationData> animations) {
		Map<String, List<String>> res = new HashMap<>();
		//ACTION
		List<String> v = new ArrayList<>();
		v.add(val(i.getId()));
		v.add(val(i.getVisibleName()));
		v.add(val(i.getDesc()));
		v.add(val(i.isField()));
		v.add(val(i.isBattle()));
		v.add(val(i.getArea()));
		v.add(val(i.getTgtType()));
		v.add(val(i.getDeadTgt()));
		v.add(val(i.getPrice()));
		v.add(val(i.getSlot()));
		v.add(val(i.getAtkCount()));
		v.add(val(i.getWeaponType()));
		v.add(val(i.getStyle()));
		v.add(val(i.getEnchant()));
		v.add(val(i.getDcs()));
		v.add(val(i.getCurrentUpgradeNum()));
		v.add(val(i.getSummary()));
		res.put("ITEM", List.of(String.join(",", v)));

		//Action_MainEvent
		for (ActionEvent e : i.getMainEvents()) {
			v = new ArrayList<>();
			v.add(val(i.getId()));
			v.add(val(e.getId()));
			if (res.containsKey("ACTION_MAINEVENT")) {
				res.get("ACTION_MAINEVENT").add(String.join(",", v));
			} else {
				res.put("ACTION_MAINEVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Action_UserEvent
		for (ActionEvent e : i.getMainEvents()) {
			v = new ArrayList<>();
			v.add(val(i.getId()));
			v.add(val(e.getId()));
			if (res.containsKey("ACTION_USEREVENT")) {
				res.get("ACTION_USEREVENT").add(String.join(",", v));
			} else {
				res.put("ACTION_USEREVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//ActionEvent
		for (ActionEvent e : i.getAllEvents()) {
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getSort()));
			v.add(val(e.getEventType()));
			v.add(val(e.getTgtStatusKey()));
			v.add(val(e.getP()));
			v.add(val(e.getTgtConditionKey()));
			v.add(val(e.getCndTime()));
			v.add(val(e.getAtkAttr()));
			v.add(val(e.getTgtAttrIn()));
			v.add(val(e.getTgtAttrOut()));
			v.add(val(e.getTgtCndRegist()));
			v.add(val(e.getTgtItemID()));
			v.add(val(e.isNoLimit()));
			v.add(val(e.getValue()));
			v.add(val(e.getCalcMode()));
			v.add(val(e.getSuccessSound().getName()));
			if (res.containsKey("ACTIONEVENT")) {
				res.get("ACTIONEVENT").add(String.join(",", v));
			} else {
				res.put("ACTIONEVENT", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Event_term
		for (ActionEvent e : i.getAllEvents()) {
			for (ActionEvent.Term t : e.getTerms()) {
				v = new ArrayList<>();
				v.add(val(e.getId()));
				v.add(val(t.id));
				if (res.containsKey("EVENT_TERM")) {
					res.get("EVENT_TERM").add(String.join(",", v));
				} else {
					res.put("EVENT_TERM", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//EventTerm
		for (ActionEvent e : i.getAllEvents()) {
			for (ActionEvent.Term t : e.getTerms()) {
				v = new ArrayList<>();
				v.add(val(t.id));
				v.add(val(t.type));
				v.add(val(t.tgtName));
				v.add(val(t.value));
				if (res.containsKey("EVENTTERM")) {
					res.get("EVENTTERM").add(String.join(",", v));
				} else {
					res.put("EVENTTERM", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//アニメーション整合性チェック
		if (i.getUserAnimation() != null) {
			if (!animations.contains(i.getUserAnimation().getId())) {
				throw new IllegalArgumentException("user animation not found : " + i.getUserAnimation().getId());
			}
		}
		for (ActionEvent e : i.getAllEvents()) {
			if (e.getTgtAnimation() != null) {
				if (!animations.contains(e.getTgtAnimation().getId())) {
					throw new IllegalArgumentException("tgt animation not found : " + e.getTgtAnimation().getId());
				}
			}
			if (e.getOtherAnimation() != null) {
				if (!animations.contains(e.getOtherAnimation().getId())) {
					throw new IllegalArgumentException("other animation not found : " + e.getOtherAnimation().getId());
				}
			}
		}

		//Action_UserAnimation
		if (i.getUserAnimation() != null) {
			v = new ArrayList<>();
			v.add(val(i.getId()));
			v.add(val(i.getUserAnimation().getId()));
			res.put("ACTION_USERANIMATION", List.of(String.join(",", v)));
		}
		//Event_TgtAnimation
		for (ActionEvent e : i.getAllEvents()) {
			if (e.getTgtAnimation() == null) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getTgtAnimation().getId()));
			if (res.containsKey("EVENT_TGTANIMATION")) {
				res.get("EVENT_TGTANIMATION").add(String.join(",", v));
			} else {
				res.put("EVENT_TGTANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//Event_OtherAnimation
		for (ActionEvent e : i.getAllEvents()) {
			if (e.getOtherAnimation() == null) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(e.getId()));
			v.add(val(e.getOtherAnimation().getId()));
			if (res.containsKey("EVENT_OTHERANIMATION")) {
				res.get("EVENT_OTHERANIMATION").add(String.join(",", v));
			} else {
				res.put("EVENT_OTHERANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}

		//ANIMATION
		if (i.getUserAnimation() != null) {
			AnimationData d = animations.get(i.getUserAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		for (ActionEvent e : i.getAllEvents()) {
			if (e.getTgtAnimation() == null) {
				continue;
			}
			AnimationData d = animations.get(e.getTgtAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		for (ActionEvent e : i.getAllEvents()) {
			if (e.getOtherAnimation() == null) {
				continue;
			}
			AnimationData d = animations.get(e.getOtherAnimation().getId());
			v = new ArrayList<>();
			v.add(val(d.id));
			v.add(val(d.fileName));
			v.add(val(d.w));
			v.add(val(d.h));
			v.add(val(String.join(",", IntStream.of(d.tc).mapToObj(p -> Integer.toString(p)).collect(Collectors.toList()))));
			if (res.containsKey("ANIMATION")) {
				res.get("ANIMATION").add(String.join(",", v));
			} else {
				res.put("ANIMATION", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//アイテム
		//Item_eqipTerm
		for (ItemEqipTerm t : i.getEqipTerms()) {
			v = new ArrayList<>();
			v.add(val(i.getId()));
			v.add(val(t.toString()));
			if (res.containsKey("ITEM_EQIPTERM")) {
				res.get("ITEM_EQIPTERM").add(String.join(",", v));
			} else {
				res.put("ITEM_EQIPTERM", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//item_material
		for (Map.Entry<Material, Integer> m : i.getMaterial().entrySet()) {
			if (m.getValue() == 0) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(i.getId()));
			v.add(val(m.getKey().getId()));
			v.add(val(m.getValue()));
			if (res.containsKey("ITEM_MATERIAL")) {
				res.get("ITEM_MATERIAL").add(String.join(",", v));
			} else {
				res.put("ITEM_MATERIAL", new ArrayList<>(List.of(String.join(",", v))));
			}
		}

		//material
		for (Map.Entry<Material, Integer> m : i.getMaterial().entrySet()) {
			if (m.getValue() == 0) {
				continue;
			}
			v = new ArrayList<>();
			v.add(val(m.getKey().getId()));
			v.add(val(m.getValue()));
			v.add(val(m.getKey().getPrice()));
			if (res.containsKey("MATERIAL")) {
				res.get("MATERIAL").add(String.join(",", v));
			} else {
				res.put("MATERIAL", new ArrayList<>(List.of(String.join(",", v))));
			}
		}
		//item_cndregist
		if (i.getConditionRegist() != null && !i.getConditionRegist().isEmpty()) {
			ConditionRegist c = i.getConditionRegist();
			int cndRegistNo = 0;
			for (ConditionKey k : c.keySet()) {
				if (c.get(k) == 0) {
					continue;
				}
				String cndRegistID = i.getId() + StringUtil.zeroUme(cndRegistNo++ + "", 2);
				v = new ArrayList<>();
				v.add(val(i.getId()));
				v.add(val(cndRegistID));
				if (res.containsKey("ITEM_CNDREGIST")) {
					res.get("ITEM_CNDREGIST").add(String.join(",", v));
				} else {
					res.put("ITEM_CNDREGIST", new ArrayList<>(List.of(String.join(",", v))));
				}
				//cndEffect
				v = new ArrayList<>();
				v.add(val(cndRegistID));
				v.add(val(k.toString()));
				v.add(val(c.get(k)));
				if (res.containsKey("CNDEFFECT")) {
					res.get("CNDEFFECT").add(String.join(",", v));
				} else {
					res.put("CNDEFFECT", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//item_attrin
		int attrNo = 0;
		if (i.getAttrIn() != null && !i.getAttrIn().isEmpty()) {
			AttributeValueSet vs = i.getAttrIn();
			for (AttributeValue av : vs) {
				if (av.getValue() == 0) {
					continue;
				}
				String attrID = i.getId() + StringUtil.zeroUme(attrNo++ + "", 2);
				v = new ArrayList<>();
				v.add(val(i.getId()));
				v.add(val(attrID));
				if (res.containsKey("ITEM_ATTRIN")) {
					res.get("ITEM_ATTRIN").add(String.join(",", v));
				} else {
					res.put("ITEM_ATTRIN", new ArrayList<>(List.of(String.join(",", v))));
				}
				//attrEffect
				v = new ArrayList<>();
				v.add(val(attrID));
				v.add(val(av.getKey().toString()));
				v.add(val(av.getValue()));
				if (res.containsKey("ATTREFFECT")) {
					res.get("ATTREFFECT").add(String.join(",", v));
				} else {
					res.put("ATTREFFECT", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//item_attrin
		if (i.getAttrOut() != null && !i.getAttrOut().isEmpty()) {
			AttributeValueSet vs = i.getAttrOut();
			for (AttributeValue av : vs) {
				if (av.getValue() == 0) {
					continue;
				}
				String attrID = i.getId() + StringUtil.zeroUme(attrNo++ + "", 2);
				v = new ArrayList<>();
				v.add(val(i.getId()));
				v.add(val(attrID));
				if (res.containsKey("ITEM_ATTROUT")) {
					res.get("ITEM_ATTROUT").add(String.join(",", v));
				} else {
					res.put("ITEM_ATTROUT", new ArrayList<>(List.of(String.join(",", v))));
				}
				//attrEffect
				v = new ArrayList<>();
				v.add(val(attrID));
				v.add(val(av.getKey().toString()));
				v.add(val(av.getValue()));
				if (res.containsKey("ATTREFFECT")) {
					res.get("ATTREFFECT").add(String.join(",", v));
				} else {
					res.put("ATTREFFECT", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		//item_status
		if (i.getStatus() != null && !i.getStatus().isEmpty()) {
			StatusValueSet vs = i.getStatus();
			int statusNo = 0;
			for (StatusValue sv : vs) {
				if (sv.getValue() == 0) {
					continue;
				}
				String statusID = i.getId() + StringUtil.zeroUme(statusNo++ + "", 2);
				v = new ArrayList<>();
				v.add(val(i.getId()));
				v.add(val(statusID));
				if (res.containsKey("ITEM_STATUS")) {
					res.get("ITEM_STATUS").add(String.join(",", v));
				} else {
					res.put("ITEM_STATUS", new ArrayList<>(List.of(String.join(",", v))));
				}
				//statusEffect
				v = new ArrayList<>();
				v.add(val(statusID));
				v.add(val(sv.getKey().toString()));
				v.add(val(sv.getValue()));
				if (res.containsKey("STATUSEFFECT")) {
					res.get("STATUSEFFECT").add(String.join(",", v));
				} else {
					res.put("STATUSEFFECT", new ArrayList<>(List.of(String.join(",", v))));
				}
			}
		}
		return res;
	}

	private static String val(Object v) {
		if (v == null) {
			return "\"\"";
		}
		return "\"" + v + "\"";
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
				+ "P,CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,ITEMID,ITEMADDNOLIMIT,VAL,CALCMODE,SOUNDID"
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
				res.add(e);
				continue;
			}

			ActionEvent e = new ActionEvent(l.get(1).get());
			e.setSort(l.get(2).asInt());
			e.setEventType(l.get(3).of(ActionEvent.EventType.class));
			e.setTgtStatusKey(l.get(4).orNull(StatusKey.class));
			e.setP(l.get(5).asFloat());
			e.setTgtConditionKey(l.get(6).orNull(ConditionKey.class));
			e.setCndTime(l.get(7).asInt());
			e.setAtkAttr(l.get(8).orNull(AttributeKey.class));
			e.setTgtAttrKeyin(l.get(9).orNull(AttributeKey.class));
			e.setTgtAttrKeyOut(l.get(10).orNull(AttributeKey.class));
			e.setCndRegist(l.get(11).orNull(ConditionKey.class));
			e.setTgtItemID(l.get(12).get());
			e.setNoLimit(l.get(13).asBoolean());
			e.setValue(l.get(14).asFloat());
			e.setCalcMode(l.get(15).orNull(ActionEvent.CalcMode.class));
			String soundID = l.get(16).get();
			if (soundID != null) {
				e.setSuccessSound(SoundStorage.getInstance().get(soundID));
			}
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
			//
			res.add(e);
		}
		return res;
	}

	private List<ActionEvent> getUserEvents(String actionID) {
		String sql = "select ACTIONID,EVENTID,SORT,EVENTTYPE,STATUSKEYNAME,P,"
				+ "CONDITIONKEY,CNDTIME,ATKATTR,ATTRIN,ATTROUT,CNDREGIST,ITEMID,ITEMADDNOLIMIT,VAL,CALCMODE,SOUNDID"
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
			e.setEventType(l.get(3).of(ActionEvent.EventType.class));
			e.setTgtStatusKey(l.get(4).orNull(StatusKey.class));
			e.setP(l.get(5).asFloat());
			e.setTgtConditionKey(l.get(6).orNull(ConditionKey.class));
			e.setCndTime(l.get(7).asInt());
			e.setAtkAttr(l.get(8).orNull(AttributeKey.class));
			e.setTgtAttrKeyin(l.get(9).orNull(AttributeKey.class));
			e.setTgtAttrKeyOut(l.get(10).orNull(AttributeKey.class));
			e.setCndRegist(l.get(11).orNull(ConditionKey.class));
			e.setTgtItemID(l.get(12).get());
			e.setNoLimit(l.get(13).asBoolean());
			e.setValue(l.get(14).asFloat());
			e.setCalcMode(l.get(15).orNull(ActionEvent.CalcMode.class));
			String soundID = l.get(16).get();
			if (soundID != null) {
				e.setSuccessSound(SoundStorage.getInstance().get(soundID));
			}
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
			//
			res.add(e);
		}
		return res;
	}

}
