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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kinugasa.game.GameLog;
import kinugasa.game.I18N;
import kinugasa.game.NotNewInstance;
import kinugasa.game.Nullable;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.game.NotNull;

/**
 * アクターはステータスとスプライトを持つ、登場人物のクラスです。<br>
 * フィールドマップ上のNPCはステータスがないので、このクラスは使用せず、スプライトだけです（→NPCSprite）。<br>
 * 戦闘時の敵キャラはステータスを持つので、このクラスの派生です。（→Enemy）
 *
 * @vesion 1.0.0 - 2023/10/14_22:15:56<br>
 * @author Shinacho<br>
 */
public sealed class Actor implements Nameable, XMLFileSupport, Comparable<Actor> permits Enemy {

	//テスト用。
	@Deprecated
	public static void printXMLFormat() {
		for (var v : StatusKey.values()) {
			System.out.println("\t<status key=\"" + v + "\" value=\"\" />");
		}
		System.out.println();
		for (var v : AttributeKey.values()) {
			System.out.println("\t<attrIn key=\"" + v + "\" value=\"\" />");
		}
		System.out.println();
		for (var v : AttributeKey.values()) {
			System.out.println("\t<attrOut key=\"" + v + "\" value=\"\" />");
		}
		System.out.println();
		for (var v : ConditionKey.values()) {
			if (v.isRegistOn()) {
				System.out.println("\t<cndRegist key=\"" + v + "\" value=\"\" />");
			}
		}
	}
	private String id;
	private String visibleName;
	private PCSprite sprite;
	private Status status;
	private String iniStatusFile;
	private BufferedImage faceImage;
	private boolean isSummoned = false;//召喚された人フラグ
	private StatusValueSet vs;
	private String visibleName退避;

	public Actor(String id, Actor a) {
		this(id, a.visibleName, a.status.getRace(), a.sprite);
		this.status = a.status.clone();
		this.sprite = a.sprite.clone();
	}

	public Actor(String id, String visibleName, Race r, PCSprite sprite) {
		if (id == null || visibleName == null || r == null || sprite == null) {
			throw new GameSystemException("actor value is null : " + id + " / " + visibleName);
		}
		if (id.isEmpty() || visibleName.isEmpty()) {
			throw new GameSystemException("actor value is empty : " + id + " / " + visibleName);
		}
		this.id = id;
		this.visibleName = visibleName;
		this.sprite = sprite;
		this.status = new Status(id, r);
	}

	public Actor(String fileName) {
		readFromXML(iniStatusFile = fileName);
	}

	public void setFaceImage(BufferedImage faceImage) {
		this.faceImage = faceImage;
	}

	@Nullable
	public BufferedImage getFaceImage() {
		return faceImage;
	}

	@NotNewInstance
	@NotNull
	public PCSprite getSprite() {
		return sprite;
	}

	@NotNewInstance
	@NotNull
	public Status getStatus() {
		return status;
	}

	@NotNewInstance
	@Nullable
	public String getStatusFile() {
		return iniStatusFile;
	}

	public void setSummoned(boolean isSummoned) {
		this.isSummoned = isSummoned;
	}

	public boolean isSummoned() {
		return isSummoned;
	}

	//注意：ファイルには反映されない
	public void setVisibleName(String name) {
		this.visibleName = name;
	}

	public void readFromXML() throws GameSystemException {
		if (this.iniStatusFile == null) {
			throw new GameSystemException("im not have xml : " + this);
		}
		readFromXML(iniStatusFile);
	}

	private PersonalBag<Book> bookBag;
	private PersonalBag<Item> itemBag;

	public void 退避＿ステータスの初期化されない項目() {
		vs = this.status.getBaseStatus().clone();
		List<StatusKey> list = new ArrayList<>();
		list.add(StatusKey.レベル);
		list.add(StatusKey.次のレベルの経験値);
		list.add(StatusKey.保有経験値);
		list.add(StatusKey.筋力);
		list.add(StatusKey.器用さ);
		list.add(StatusKey.素早さ);
		list.add(StatusKey.精神);
		list.add(StatusKey.信仰);
		list.add(StatusKey.詠唱);
		for (StatusKey k : list) {
			this.status.getBaseStatus().remove(k.getName());
		}
		visibleName退避 = visibleName;
		bookBag = getStatus().getBookBag().clone();
		itemBag = getStatus().getItemBag().clone();
	}

	public void 復元＿ステータスの初期化されない項目() {
		vs = this.status.getBaseStatus().clone();
		List<StatusKey> list = new ArrayList<>();
		list.add(StatusKey.レベル);
		list.add(StatusKey.次のレベルの経験値);
		list.add(StatusKey.保有経験値);
		list.add(StatusKey.筋力);
		list.add(StatusKey.器用さ);
		list.add(StatusKey.素早さ);
		list.add(StatusKey.精神);
		list.add(StatusKey.信仰);
		list.add(StatusKey.詠唱);
		for (StatusKey k : list) {
			this.status.getBaseStatus().remove(k.getName());
			this.status.getBaseStatus().add(vs.get(k));
		}
		visibleName = visibleName退避;
		getStatus().setItemBag(itemBag);
		getStatus().setBookBag(bookBag);
	}

	public void setIniStatusFile(String iniStatusFile) {
		this.iniStatusFile = iniStatusFile;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f);
		}
		try {
			XMLElement root = f.load().getFirst();
			String id = root.getAttributes().get("id").getValue();
			String visibleName = I18N.get(root.getAttributes().get("visibleName").getValue());
			Race r = root.getAttributes().get("race").of(Race.class);
			//ID
			this.id = id;
			this.visibleName = visibleName;
			this.status = new Status(id, r);
			//アビリティ
			if (root.hasAttribute("ability")) {
				CharaAbility ca = root.getAttributes().get("ability").of(CharaAbility.class);
				this.status.setAbility(ca);
			}
			//スプライトシート
			if (root.hasElement("spriteSheet")) {
				if (this instanceof Enemy) {
					sprite = new EnemySprite(root.getElement("spriteSheet").get(0));
				} else {
					sprite = new PCSprite(root.getElement("spriteSheet").get(0));
				}
			} else {
				throw new IllegalXMLFormatException("i dont have sprite : " + this);
			}
			//faceImage
			if (root.hasElement("faceImage")) {
				this.faceImage = ImageUtil.load(root.getElement("faceImage").get(0).getAttributes().get("fileName").getValue());
			}
			//reset
			this.status.getBaseStatus().clear();
			this.status.getAttrIn().clear();
			this.status.getAttrOut().clear();
			this.status.getConditionRegist().clear();
			this.status.clearCondition();//暫定
			//ATTR_IN
			for (XMLElement e : root.getElement("attrIn")) {
				AttributeKey k = e.getAttributes().get("key").of(AttributeKey.class);
				float val = e.getAttributes().get("value").getFloatValue();
				this.status.getAttrIn().add(new AttributeValue(k, val));
			}
			//ATTR_OUT
			for (XMLElement e : root.getElement("attrOut")) {
				AttributeKey k = e.getAttributes().get("key").of(AttributeKey.class);
				float val = e.getAttributes().get("value").getFloatValue();
				this.status.getAttrOut().add(new AttributeValue(k, val));
			}
			//CND_REGIST
			for (XMLElement e : root.getElement("cndRegist")) {
				ConditionKey k = e.getAttributes().get("key").of(ConditionKey.class);
				float val = e.getAttributes().get("value").getFloatValue();
				this.status.getConditionRegist().put(k, val);
			}
			//STATUS
			for (XMLElement e : root.getElement("status")) {
				StatusKey k = e.getAttributes().get("key").of(StatusKey.class);
				float val = e.getAttributes().get("value").getFloatValue();
				if (e.hasAttribute("max")) {
					float max = e.getAttributes().get("max").getFloatValue();
					this.status.getBaseStatus().add(new StatusValue(k, val, 0, max));
				} else {
					this.status.getBaseStatus().add(new StatusValue(k, val));
				}
			}
			//ITEM
			for (XMLElement e : root.getElement("item")) {
				String itemID = e.getAttributes().get("id").getValue();
				Item i = ActionStorage.getInstance().itemOf(itemID);
				this.status.getItemBag().add(i);
			}
			//EQIP
			for (XMLElement e : root.getElement("eqip")) {
				String itemID = e.getAttributes().get("id").getValue();
				if (e.hasAttribute("left")) {
					Item i = this.status.getItemBag().get(itemID);
					if (i.isWeapon()) {
						if (i.getWeaponType() == WeaponType.弓) {
							this.status.eqip(EqipSlot.左手, i);
							this.status.eqipLeftHand(ActionStorage.getInstance().両手持ち_弓);
						} else {
							this.status.eqip(i);
							this.status.eqipLeftHand(ActionStorage.getInstance().両手持ち);
						}
					} else {
						this.status.eqip(this.status.getItemBag().get(itemID));
					}
				} else {
					this.status.eqip(this.status.getItemBag().get(itemID));
				}
			}
			//BOOK
			for (XMLElement e : root.getElement("book")) {
				String actionID = e.getAttributes().get("id").getValue();
				Action a = ActionStorage.getInstance().actionOf(actionID);
				if (a.getType() != ActionType.魔法) {
					throw new GameSystemException("book id is not magic : " + this + " / " + e);
				}
				this.status.getBookBag().add(new Book(a));
			}
			f.dispose();

			//保有経験値とレベルの整合性チェック
			if ((int) this.status.getEffectedStatus().get(StatusKey.レベル).getValue()
					!= LevelSystem.経験値からレベル算出(this.status.getEffectedStatus().get(StatusKey.保有経験値).getValue())) {
				throw new GameSystemException("lv - exp missmatch : " + this);
			}

			//アイテム所持数の再計算
			getStatus().updateBagSize();

			if (GameSystem.isDebugMode()) {
				GameLog.print("Actor : " + this + " is loaded");
			}

		} catch (FileIOException | NameNotFoundException e) {
			throw new IllegalXMLFormatException(e.getMessage());
		}
	}

	@Deprecated
	@Override
	public final String getName() {
		return id;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public final String getId() {
		return id;
	}

	public boolean isPlayer() {
		return GameSystem.getInstance().getPartyStatus().contains(status);
	}

	@Override
	public String toString() {
		return "Actor{" + "id=" + id + ", visibleName=" + visibleName + ", iniStatusFile=" + iniStatusFile + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.id);
		hash = 37 * hash + Objects.hashCode(this.visibleName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Actor other = (Actor) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return Objects.equals(this.visibleName, other.visibleName);
	}

	@Override
	public int compareTo(Actor o) {
		return id.compareTo(o.id);
	}

}
