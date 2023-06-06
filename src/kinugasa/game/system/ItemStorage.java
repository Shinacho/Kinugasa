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
import java.util.Collections;
import java.util.HashMap;
import kinugasa.resource.db.DBStorage;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;
import kinugasa.resource.sound.SoundStorage;

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
						switch (type) {
							case "statusIs":
								terms.add(ItemEqipTerm.statusIs(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asInt()));
								break;
							case "raceIs":
								terms.add(ItemEqipTerm.raceIs(RaceStorage.getInstance().get(tgtName)));
								break;
							case "statusIsOver":
								terms.add(ItemEqipTerm.statusIsOver(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asInt()));
								break;
							default:
								throw new AssertionError("undefined eqipTermType : " + vv);
						}
					}
					i.setEqipTerm(terms);
				}
				//MATERIAL
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
						switch (type) {
							case "statusIs":
								terms.add(ItemEqipTerm.statusIs(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asInt()));
								break;
							case "raceIs":
								terms.add(ItemEqipTerm.raceIs(RaceStorage.getInstance().get(tgtName)));
								break;
							case "statusIsOver":
								terms.add(ItemEqipTerm.statusIsOver(StatusKeyStorage.getInstance().get(tgtName), vv.get(2).asInt()));
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

	/**
	 * バッグに分類されるアイテムを登録する。 キー：アイテムのID：value：この装備で追加されるアイテム数
	 */
	public static Map<String, Integer> bagItems = new HashMap<>();

}
