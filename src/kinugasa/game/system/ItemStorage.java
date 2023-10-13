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
import java.util.HashMap;
import kinugasa.resource.db.DBStorage;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.StringUtil;

/**
 * アイテムの一覧を定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:41<br>
 * @author Shinacho<br>
 */
public class ItemStorage extends DBStorage<Item> {

	private static final ItemStorage INSTANCE = new ItemStorage();

	private ItemStorage() {
	}

	public static ItemStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Item select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql1 = "select "
					+ "ItemID, "
					+ "visibleName, "
					+ "desc, "
					+ "val, "
					+ "slotID, "
					+ "WeaponType, "
					+ "area, "
					+ "dcsCSV, "
					+ "waitTime, "
					+ "SoundID, "
					+ "attackCount, "
					+ "canSale, "
					+ "currentUpgradeNum, "
					+ "selectType, "
					+ "IFF, "
					+ "defaultTargetTeam, "
					+ "switchTeam, "
					+ "Targeting"
					+ " from Item"
					+ " where itemID='" + id + "';";
			KResultSet rs1 = DBConnection.getInstance().execDirect(sql1);
			if (rs1.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : rs1) {
				String itemId = v.get(0).get();
				String visibleName = v.get(1).get();
				String desc = v.get(2).get();
				int val = v.get(3).asInt();
				String slotID = v.get(4).get();
				String weaponType = v.get(5).get();
				int area = v.get(6).asInt();
				String[] dcs = v.get(7).safeSplit(",");
				int waitTime = v.get(8).asInt();
				String soundID = v.get(9).get();
				int atkCount = v.get(10).asInt();
				boolean canSale = v.get(11).asBoolean();
				int currentUpgradeNum = v.get(12).asInt();
				//TargetOption
				Item i = new Item(id, visibleName, desc);
				if (v.get(13).get() != null && !v.get(13).get().isEmpty()) {
					TargetOption.SelectType selectType = v.get(13).of(TargetOption.SelectType.class);
					TargetOption.IFF iff = v.get(14).asBoolean() ? TargetOption.IFF.ON : TargetOption.IFF.OFF;
					TargetOption.DefaultTarget defaultTarget = v.get(15).of(TargetOption.DefaultTarget.class);
					TargetOption.SwitchTeam switchTeam = v.get(16).asBoolean() ? TargetOption.SwitchTeam.OK : TargetOption.SwitchTeam.NG;
					TargetOption.Targeting targeting = v.get(17).asBoolean() ? TargetOption.Targeting.ENABLE : TargetOption.Targeting.DISABLE;
					TargetOption targetOption = TargetOption.of(selectType, iff, defaultTarget, switchTeam, TargetOption.SelfTarget.YES, targeting);
					i.setTargetOption(targetOption);
				}
				i.setValue(val);
				//スロット
				if (slotID != null && !slotID.isEmpty()) {
					ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(slotID);
					i.setEqipmentSlot(slot);
				}
				//WEaponType
				if (weaponType != null && !weaponType.isEmpty()) {
					WeaponType type = WeaponTypeStorage.getInstance().get(weaponType.toUpperCase());
					i.setWeaponMagicType(type);
				}
				//DCS
				if (dcs != null && dcs.length > 0) {
					Set<StatusKey> dcsKeys = new HashSet<>();
					for (String sk : dcs) {
						dcsKeys.add(StatusKeyStorage.getInstance().get(sk.toUpperCase()));
					}
					i.setDamageCalcStatusKey(dcsKeys);
				}
				i.setWaitTime(waitTime);
				//サウンド
				if (soundID != null && !soundID.equals("")) {
					i.setSound(SoundStorage.getInstance().get(soundID));
				}
				//ACTION_EVENT
				ActionEventStorage.ActionEvents events = ActionEventStorage.getInstance().getActionEvents(i);
				i.addBattleEvent(events.battle);
				i.addFieldEvent(events.field);
				//EQIPSTATUS
				KResultSet eqipStatusKS = DBConnection.getInstance().execDirect("select"
						+ " targetName, val from eqipStatus where itemID='" + itemId + "';");
				StatusValueSet svs = new StatusValueSet();
				svs.clear();
				for (var vv : eqipStatusKS) {
					StatusKey tgt = StatusKeyStorage.getInstance().get(vv.get(0).get());
					float value = vv.get(1).asFloat();
					svs.add(new StatusValue(tgt, value, value, value, value));
				}
				i.setEqStatus(svs);
				//EQIPATTR
				KResultSet eqipAttrKS = DBConnection.getInstance().execDirect("select"
						+ " targetName, val from eqipAttr where itemID='" + itemId + "';");
				AttributeValueSet avs = new AttributeValueSet();
				avs.clear();
				for (var vv : eqipAttrKS) {
					AttributeKey tgt = AttributeKeyStorage.getInstance().get(vv.get(0).get());
					float value = vv.get(1).asFloat();
					avs.add(new AttributeValue(tgt, value, value, value, value));
				}
				i.setEqAttr(avs);
				//EQIPTERM
				KResultSet tks = DBConnection.getInstance().execDirect("select"
						+ " type, targetName, val from EqipTerm where itemID='" + itemId + "';");
				if (tks.isEmpty()) {
					i.setEqipTerm(List.of(ItemEqipTerm.ANY));
				} else {
					List<ItemEqipTerm> terms = new ArrayList<>();
					for (var vv : tks) {
						String type = vv.get(0).get();
						String tgtName = vv.get(1).get();
						switch (type.toUpperCase()) {
							case "STATUS_IS":
								terms.add(ItemEqipTerm.statusIs(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asFloat()));
								break;
							case "RACE_IS":
								terms.add(ItemEqipTerm.raceIs(RaceStorage.getInstance().get(tgtName)));
								break;
							case "STATUS_IS_OVER":
								terms.add(ItemEqipTerm.statusIsOver(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asFloat()));
								break;
							default:
								throw new AssertionError("undefined eqipTermType : " + vv);
						}
					}
					i.setEqipTerm(terms);
				}
				//MATERIAL
				KResultSet mks = DBConnection.getInstance().execDirect("select"
						+ " materialID, num"
						+ " from"
						+ " item_material"
						+ " where itemID = '" + itemId + "';");
				if (!mks.isEmpty()) {
					Map<Material, Integer> map = new HashMap<>();
					for (var vv : mks) {
						Material m = MaterialStorage.getInstance().get(vv.get(0).get());
						int num = vv.get(1).asInt();
						map.put(m, num);
					}
					i.setDisasseMaterials(map);
				}
				//UPGRADE
				//上昇効果、価格、アップグレードマテリアルの最も小さいものを取る（基本は一致している）
				KResultSet ur1 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeEffect where itemID='" + itemId + "';");
				KResultSet ur2 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeMaterial where itemID='" + itemId + "';");
				KResultSet ur3 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeValue where itemID='" + itemId + "';");
				if (!ur1.isEmpty() && !ur2.isEmpty() && !ur3.isEmpty()) {
					int effectMax = ur1.cell(0, 0).asInt();
					int materialMax = ur2.cell(0, 0).asInt();
					int valueMax = ur3.cell(0, 0).asInt();
					int maxUpgradeNum = Math.min(valueMax, Math.min(effectMax, materialMax));

					//value取得
					KResultSet vrs = DBConnection.getInstance().execDirect("select od, val from ItemUpgradeValue where itemID='" + itemId + "' and od <= " + maxUpgradeNum + ";");
					//効果取得
					KResultSet ers = DBConnection.getInstance().execDirect("select od, tgtNameCSV, valCSV from ItemUpgradeEffect where itemID='" + itemId + "' and od <= " + maxUpgradeNum + ";");
					assert (vrs.size() == ers.size()) : "item upgrade size is missmatch(1) : " + i;
					List<ItemUpgrade> list = new ArrayList<>();
					for (int j = 0; j < maxUpgradeNum; j++) {
						int value = vrs.row(j).get(1).asInt();
						ItemUpgrade u = new ItemUpgrade(j, value);

						//効果
						String[] effectKey = ers.row(j).get(1).safeSplit(",");
						float[] effectValue = ers.row(j).get(2).asFloatArray(",");
						if (effectKey.length != effectValue.length) {
							throw new KSQLException("wrong data : " + i + " / " + ers);
						}
						for (int k = 0; k < effectKey.length; k++) {
							String key = effectKey[k];
							float valu = effectValue[k];
							if (StatusKeyStorage.getInstance().contains(key)) {
								StatusKey sk = StatusKeyStorage.getInstance().get(key);
								u.addStatus(sk, valu);
							} else if (AttributeKeyStorage.getInstance().contains(key)) {
								AttributeKey ak = AttributeKeyStorage.getInstance().get(key);
								u.addAttrIn(ak, valu);
							} else {
								throw new KSQLException("wrong data : " + i + " / " + ers);
							}
						}

						//素材取得
						KResultSet mrs = DBConnection.getInstance().execDirect("select od, materialID, num from ItemUpgradeMaterial where itemID='" + itemId + "' and od = " + j + ";");
						//マテリアル畳み込み
						Map<Material, Integer> material = new HashMap<>();
						for (var vv : mrs) {
							Material m = MaterialStorage.getInstance().get(vv.get(1).get());
							int num = vv.get(2).asInt();
							material.put(m, value);
						}
						u.setMaterials(material);
						list.add(u);
					}
					i.setUpgradeMaterials(list);
				}
				return i;
			}
		}
		return null;
	}

	@Override
	protected List<Item> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql1 = "select "
					+ "ItemID, "
					+ "visibleName, "
					+ "desc, "
					+ "val, "
					+ "slotID, "
					+ "WeaponType, "
					+ "area, "
					+ "dcsCSV, "
					+ "waitTime, "
					+ "SoundID, "
					+ "attackCount, "
					+ "canSale, "
					+ "currentUpgradeNum, "
					+ "selectType, "
					+ "IFF, "
					+ "defaultTargetTeam, "
					+ "switchTeam, "
					+ "Targeting "
					+ "from Item;";
			KResultSet rs1 = DBConnection.getInstance().execDirect(sql1);
			if (rs1.isEmpty()) {
				return Collections.emptyList();
			}
			List<Item> res = new ArrayList<>();
			for (List<DBValue> v : rs1) {
				String itemId = v.get(0).get();
				String visibleName = v.get(1).get();
				String desc = v.get(2).get();
				int val = v.get(3).asInt();
				String slotID = v.get(4).get();
				String weaponType = v.get(5).get();
				int area = v.get(6).asInt();
				String[] dcs = v.get(7).safeSplit(",");
				int waitTime = v.get(8).asInt();
				String soundID = v.get(9).get();
				int atkCount = v.get(10).asInt();
				boolean canSale = v.get(11).asBoolean();
				int currentUpgradeNum = v.get(12).asInt();
				//TargetOption
				Item i = new Item(itemId, visibleName, desc);
				if (v.get(13).get() != null && !v.get(13).get().isEmpty()) {
					TargetOption.SelectType selectType = v.get(13).of(TargetOption.SelectType.class);
					TargetOption.IFF iff = v.get(14).asBoolean() ? TargetOption.IFF.ON : TargetOption.IFF.OFF;
					TargetOption.DefaultTarget defaultTarget = v.get(15).of(TargetOption.DefaultTarget.class);
					TargetOption.SwitchTeam switchTeam = v.get(16).asBoolean() ? TargetOption.SwitchTeam.OK : TargetOption.SwitchTeam.NG;
					TargetOption.Targeting targeting = v.get(17).asBoolean() ? TargetOption.Targeting.ENABLE : TargetOption.Targeting.DISABLE;
					TargetOption targetOption = TargetOption.of(selectType, iff, defaultTarget, switchTeam, TargetOption.SelfTarget.YES, targeting);
					i.setTargetOption(targetOption);
				}
				i.setValue(val);
				//スロット
				if (slotID != null && !slotID.isEmpty()) {
					ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(slotID);
					i.setEqipmentSlot(slot);
				}
				//WEaponType
				if (weaponType != null && !weaponType.isEmpty()) {
					WeaponType type = WeaponTypeStorage.getInstance().get(weaponType);
					i.setWeaponMagicType(type);
				}
				//DCS
				if (dcs != null && dcs.length > 0) {
					Set<StatusKey> dcsKeys = new HashSet<>();
					for (String sk : dcs) {
						dcsKeys.add(StatusKeyStorage.getInstance().get(sk));
					}
					i.setDamageCalcStatusKey(dcsKeys);
				}
				i.setWaitTime(waitTime);
				//サウンド
				if (soundID != null && !soundID.equals("")) {
					i.setSound(SoundStorage.getInstance().get(soundID));
				}
				//ACTION_EVENT
				ActionEventStorage.ActionEvents events = ActionEventStorage.getInstance().getActionEvents(i);
				i.addBattleEvent(events.battle);
				i.addFieldEvent(events.field);
				//EQIPSTATUS
				KResultSet eqipStatusKS = DBConnection.getInstance().execDirect("select"
						+ " targetName, val from eqipStatus where itemID='" + itemId + "';");
				StatusValueSet svs = new StatusValueSet();
				svs.clear();
				for (var vv : eqipStatusKS) {
					StatusKey tgt = StatusKeyStorage.getInstance().get(vv.get(0).get());
					float value = vv.get(1).asFloat();
					svs.add(new StatusValue(tgt, value, value, value, value));
				}
				i.setEqStatus(svs);
				//EQIPATTR
				KResultSet eqipAttrKS = DBConnection.getInstance().execDirect("select"
						+ " targetName, val from eqipAttr where itemID='" + itemId + "';");
				AttributeValueSet avs = new AttributeValueSet();
				avs.clear();
				for (var vv : eqipAttrKS) {
					AttributeKey tgt = AttributeKeyStorage.getInstance().get(vv.get(0).get());
					float value = vv.get(1).asFloat();
					avs.add(new AttributeValue(tgt, value, value, value, value));
				}
				i.setEqAttr(avs);
				//EQIPTERM
				KResultSet tks = DBConnection.getInstance().execDirect("select"
						+ " type, targetName, val from EqipTerm where itemID='" + itemId + "';");
				if (tks.isEmpty()) {
					i.setEqipTerm(List.of(ItemEqipTerm.ANY));
				} else {
					List<ItemEqipTerm> terms = new ArrayList<>();
					for (var vv : tks) {
						String type = vv.get(0).get();
						String tgtName = vv.get(1).get();
						switch (type.toUpperCase()) {
							case "STATUS_IS":
								terms.add(ItemEqipTerm.statusIs(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asFloat()));
								break;
							case "RACE_IS":
								terms.add(ItemEqipTerm.raceIs(RaceStorage.getInstance().get(tgtName)));
								break;
							case "STATUS_IS_OVER":
								terms.add(ItemEqipTerm.statusIsOver(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asFloat()));
								break;
							default:
								throw new AssertionError("undefined eqipTermType : " + vv);
						}
					}
					i.setEqipTerm(terms);
				}
				//DISSASE MATERIAL
				KResultSet mks = DBConnection.getInstance().execDirect("select"
						+ " m.materialID,"
						+ "count(*)"
						+ " from"
						+ " item_material i,material m"
						+ " where i.materialID = m.MaterialID"
						+ " and i.itemID = '" + itemId + "'"
						+ " group by m.materialID;");
				if (!mks.isEmpty()) {
					Map<Material, Integer> map = new HashMap<>();
					for (var vv : mks) {
						Material m = MaterialStorage.getInstance().get(vv.get(0).get());
						int num = vv.get(1).asInt();
						map.put(m, val);
					}
					i.setDisasseMaterials(map);
				}
				//UPGRADE
				//上昇効果、価格、アップグレードマテリアルの最も小さいものを取る（基本は一致している）
				KResultSet ur1 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeEffect where itemID='" + itemId + "';");
				KResultSet ur2 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeMaterial where itemID='" + itemId + "';");
				KResultSet ur3 = DBConnection.getInstance().execDirect("select max(od) from ItemUpgradeValue where itemID='" + itemId + "';");
				if (!ur1.isEmpty() && !ur2.isEmpty() && !ur3.isEmpty()) {
					int effectMax = ur1.cell(0, 0).asInt();
					int materialMax = ur2.cell(0, 0).asInt();
					int valueMax = ur3.cell(0, 0).asInt();
					int maxUpgradeNum = Math.min(valueMax, Math.min(effectMax, materialMax));

					//value取得
					KResultSet vrs = DBConnection.getInstance().execDirect("select od, val from ItemUpgradeValue where itemID='" + itemId + "' and od <= " + maxUpgradeNum + ";");
					//効果取得
					KResultSet ers = DBConnection.getInstance().execDirect("select od, tgtNameCSV, valCSV from ItemUpgradeEffect where itemID='" + itemId + "' and od <= " + maxUpgradeNum + ";");
					assert (vrs.size() == ers.size()) : "item upgrade size is missmatch(1) : " + i;
					List<ItemUpgrade> list = new ArrayList<>();
					for (int j = 0; j < maxUpgradeNum; j++) {
						int value = vrs.row(j).get(1).asInt();
						ItemUpgrade u = new ItemUpgrade(j, value);

						//効果
						String[] effectKey = ers.row(j).get(1).safeSplit(",");
						float[] effectValue = ers.row(j).get(2).asFloatArray(",");
						if (effectKey.length != effectValue.length) {
							throw new KSQLException("wrong data : " + i + " / " + ers);
						}
						for (int k = 0; k < effectKey.length; k++) {
							String key = effectKey[k];
							float valu = effectValue[k];
							if (StatusKeyStorage.getInstance().contains(key)) {
								StatusKey sk = StatusKeyStorage.getInstance().get(key);
								u.addStatus(sk, valu);
							} else if (AttributeKeyStorage.getInstance().contains(key)) {
								AttributeKey ak = AttributeKeyStorage.getInstance().get(key);
								u.addAttrIn(ak, valu);
							} else {
								throw new KSQLException("wrong data : " + i + " / " + ers);
							}
						}

						//素材取得
						KResultSet mrs = DBConnection.getInstance().execDirect("select od, materialID, num from ItemUpgradeMaterial where itemID='" + itemId + "' and od = " + j + ";");
						//マテリアル畳み込み
						Map<Material, Integer> material = new HashMap<>();
						for (var vv : mrs) {
							Material m = MaterialStorage.getInstance().get(vv.get(1).get());
							int num = vv.get(2).asInt();
							material.put(m, value);
						}
						u.setMaterials(material);
						list.add(u);
					}
					i.setUpgradeMaterials(list);
				}
				res.add(i);
			}
			return res;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from Item;").cell(0, 0).asInt();
		}
		return 0;
	}

	public void insert(Item i) {
		if (!DBConnection.getInstance().isUsing()) {
			throw new GameSystemException("DB CONNECTION IS NOT OPEN");
		}
		//ID変更前にアップグレード情報とアクションイベント紐づけをコピー
		int next = asList().stream().mapToInt(p -> Integer.parseInt(p.getName().substring(1))).max().getAsInt() + 1;
		String nid = "I" + StringUtil.zeroUme(next + "", 5);
		{
			String oid = i.getID();
			DBConnection.getInstance().execDirect("insert into itemUpgradeValue (select '" + nid + "', od, val from itemUpgradeValue where itemID='" + oid + "');");
			DBConnection.getInstance().execDirect("insert into ItemUpgradeEffect (select '" + nid + "', od, tgtNameCSV, valCSV from itemUpgradeValue where itemID='" + oid + "');");
			DBConnection.getInstance().execDirect("insert into ITEMUPGRADEMaterial (select '" + nid + "', od, MaterialID, num from itemUpgradeValue where itemID='" + oid + "');");
		}
		//ID変更
		i.setName(nid);
		//eqipStatus
		{
			StatusValueSet eqipStatus = i.getEqStatus();
			//0を消す
			Set<StatusValue> remove = new HashSet<>();
			for (StatusValue v : eqipStatus) {
				if (v.isZero()) {
					remove.add(v);
				}
			}
			eqipStatus.removeAll(remove);
			//delete
			DBConnection.getInstance().execDirect("delete from EqipStatus where itemID='" + i.getID() + "';");
			//insert
			for (StatusValue v : eqipStatus) {
				DBConnection.getInstance().execDirect("insert into EqipStatus values('" + i.getID() + "', '" + v.getKey().getName() + "', " + v.getValue() + ");");
			}
		}
		//eqipAttr
		{
			AttributeValueSet eqipAttr = i.getEqAttr();
			//0を消す
			Set<AttributeValue> remove = new HashSet<>();
			for (AttributeValue v : eqipAttr) {
				if (v.isZero()) {
					remove.add(v);
				}
			}
			eqipAttr.removeAll(remove);
			//delete
			DBConnection.getInstance().execDirect("delete from EqipAttr where itemID='" + i.getID() + "';");
			//insert
			for (AttributeValue v : eqipAttr) {
				DBConnection.getInstance().execDirect("insert into EqipAttr values('" + i.getID() + "', '" + v.getKey().getName() + "', " + v.getValue() + ");");
			}
		}
		//eqipTerm
		{
			DBConnection.getInstance().execDirect("delete from eqipTerm where itemID='" + i.getID() + "';");
			for (ItemEqipTerm t : i.getEqipTerm()) {
				String itemID = i.getID();
				String type = t.getType().toString();
				String tgtName = t.getTgtName();
				float val = t.getValue();
				DBConnection.getInstance().execDirect("insert into eqipTerm values('" + itemID + "','" + type + "','" + tgtName + "'," + val + ");");
			}
		}
		//Item_Material
		{
			DBConnection.getInstance().execDirect("delete from Item_Material where itemID='" + i.getID() + "';");
			Map<Material, Integer> material = i.getDisasseMaterials();
			for (Map.Entry<Material, Integer> e : material.entrySet()) {
				for (int j = 0; j < e.getValue(); j++) {
					DBConnection.getInstance().execDirect("insert into Item_Material values('" + i.getID() + "','" + e.getKey().getName() + "');");
				}
			}
		}
		{
			for (ActionEvent e : i.getBattleEvent()) {
				DBConnection.getInstance().execDirect("insert into item_actionEvent values('" + i.getID() + "', '" + e.getName() + "');");
			}
			for (ActionEvent e : i.getFieldEvent()) {
				DBConnection.getInstance().execDirect("insert into item_actionEvent values('" + i.getID() + "', '" + e.getName() + "');");
			}
		}
		String itemID = i.getID();
		String visibleName = i.getVisibleName();
		String desc = i.getDesc();
		int val = i.getValue();
		String slotID = i.getEqipmentSlot() == null ? "" : i.getEqipmentSlot().getName();
		String weaponType = i.getWeaponMagicType() == null ? "" : i.getWeaponMagicType().getName();
		int area = i.getArea();
		boolean canSale = i.canSale();
		int currentUpgradeNum = i.getCurrentUpgrade();
		String dcsCSv = i.getDamageCalcStatusKey() == null ? "" : String.join(",", i.getDamageCalcStatusKey().stream().map(p -> p.getName()).collect(Collectors.toSet()));
		int waitTime = i.getWaitTime();
		String soundID = i.getSound() == null ? "" : i.getSound().getName();
		int atkCount = i.getActionCount();
		String selectType = i.getTargetOption() == null ? "" : i.getTargetOption().getSelectType().toString();
		boolean iff = i.getTargetOption() == null ? false : i.getTargetOption().getIff() == TargetOption.IFF.ON;
		String defaultTgtTeam = i.getTargetOption() == null ? "" : i.getTargetOption().getDefaultTarget().toString();
		boolean switchTeam = i.getTargetOption() == null ? false : i.getTargetOption().getSwitchTeam() == TargetOption.SwitchTeam.OK;
		boolean selfTarge = i.getTargetOption() == null ? false : i.getTargetOption().getSelfTarget() == TargetOption.SelfTarget.YES;
		boolean targeting = i.getTargetOption() == null ? false : i.getTargetOption().getTargeting() == TargetOption.Targeting.ENABLE;
		String sql = "insert into Item values("
				+ "'" + itemID + "', "
				+ "'" + visibleName + "', "
				+ val + ", "
				+ "'" + slotID + "', "
				+ "'" + weaponType + "', "
				+ area + ","
				+ canSale + ", "
				+ currentUpgradeNum + ","
				+ "'" + dcsCSv + "', "
				+ waitTime + ","
				+ "'" + soundID + "', "
				+ atkCount + ","
				+ "'" + selectType + "', "
				+ iff + ", "
				+ "'" + defaultTgtTeam + "', "
				+ switchTeam + ", "
				+ selfTarge + ", "
				+ targeting
				+ ");";
		DBConnection.getInstance().execDirect(sql);
	}

	/**
	 * バッグに分類されるアイテムを登録する。 キー：アイテムのID：value：この装備で追加されるアイテム数
	 */
	public static Map<String, Integer> bagItems = new HashMap<>();

}
