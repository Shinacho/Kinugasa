/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
package hexwarsim.fieldmap;

import hexwarsim.game.Team;
import hexwarsim.game.GameConstants;
import hexwarsim.game.ControlPlayer;
import hexwarsim.game.PlayerStatus;
import hexwarsim.game.PlayerStatusStorage;
import hexwarsim.game.Resources;
import hexwarsim.game.ResourcesValue;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import kinugasa.contents.graphics.ARGBColor;
import kinugasa.contents.graphics.ImageEditor;
import kinugasa.contents.graphics.ImageUtil;
import kinugasa.contents.resource.Freeable;
import kinugasa.contents.resource.IllegalFormatException;
import kinugasa.contents.resource.Loadable;
import kinugasa.contents.resource.Nameable;
import kinugasa.contents.resource.NotYetLoadedException;
import kinugasa.contents.text.CSVReader;
import kinugasa.fieldObject.ChipSet;
import kinugasa.fieldObject.ChipSetStorage;
import kinugasa.game.GameLog;
import kinugasa.fieldObject.MapChip;
import kinugasa.game.PlayerConstants;
import kinugasa.util.StopWatch;
import kinugasa.util.StringUtil;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/10/04<br>
 * @author Dra<br>
 * <br>
 */
public class HexFieldMap implements Nameable, Loadable, Freeable {

	public HexFieldMap(String filePath) {
		this.fileName = filePath;
	}

	public HexFieldMap(int no, String filePath, String mapTitle, String mapInfo) {
		this.no = no;
		this.fileName = filePath;
		this.mapTitle = mapTitle;
		this.mapInfo = mapInfo;
	}

	private boolean loaded = false;
	private int no;
	private String fileName;
	private String mapTitle;
	private String mapInfo;
	private CSVReader reader;
	private BufferedImage miniFieldMapImage;
	private BufferedImage miniBuildingMapImage;
	private MapChip[][] mapData;
	private Building[][] buildingData;
	//private Util[][] units;
	private PlayerStatusStorage playerStatusStorage;
	//
	private static final String LABEL_FIELD_DATA = ":FIELD";
	private static final String LABEL_BUILDING_DATA = ":BUILDING";
	private static final String LABEL_RESOURCE_DATA = ":RESOURCE";
	private static final String LABEL_ALLIES_DATA = ":ALLIES";
	private static final int LABEL_NUM = 4;
	//
	private int prevMiniFieldMapSize;
	private int prevMiniBuildingMapSize;

	@Override
	public String getName() {
		return fileName;
	}

	public int getNo() {
		return no;
	}

	public String getMapInfo() {
		return mapInfo;
	}

	public String getMapTitle() {
		return mapTitle;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public HexFieldMap load() {
		GameLog.printInfo("フィールドマップのロードを開始：" + getName());
		StopWatch watch = new StopWatch().start();
		reader = new CSVReader(GameConstants.FIELDMAP_DIR + fileName).load();
		if (reader.size() == 0) {
			throw new IllegalFormatException("ファイルが空です");
		}
		List<String[]> data = reader.getData();
		ChipSet fieldChipSet = null, buildingChipSet = null;
		Map<String, Integer> startRowMap = new HashMap<>();
		//ラベル位置の保存
		for (int i = 0, size = data.size(); i < size; i++) {
			String[] line = data.get(i);
			switch (line[0]) {
				case LABEL_FIELD_DATA:
					if (line.length != 2) {
						throw new IllegalFormatException("ラベルの後に、チップセット名が必要です:" + Arrays.toString(line));
					}
					startRowMap.put(line[0], i);
					fieldChipSet = ChipSetStorage.getInstance().get(line[1]);
					break;
				case LABEL_BUILDING_DATA:
					if (line.length != 2) {
						throw new IllegalFormatException("ラベルの後に、チップセット名が必要です:" + Arrays.toString(line));
					}
					startRowMap.put(line[0], i);
					buildingChipSet = ChipSetStorage.getInstance().get(line[1]);
					break;
				case LABEL_RESOURCE_DATA:
					startRowMap.put(line[0], i);
					break;
				case LABEL_ALLIES_DATA:
					startRowMap.put(line[0], i);
					break;
			}
		}
		assert fieldChipSet != null && buildingChipSet != null : "チップセットが特定できていない";
		if (startRowMap.size() != LABEL_NUM) {
			throw new IllegalFormatException("ラベルが不足しています：\n"
					+ LABEL_FIELD_DATA + "=" + (startRowMap.containsKey(LABEL_FIELD_DATA) ? "OK" : "NG") + ", "
					+ LABEL_BUILDING_DATA + "=" + (startRowMap.containsKey(LABEL_BUILDING_DATA) ? "OK" : "NG") + ", "
					+ LABEL_RESOURCE_DATA + "=" + (startRowMap.containsKey(LABEL_RESOURCE_DATA) ? "OK" : "NG") + ", "
					+ LABEL_ALLIES_DATA + "=" + (startRowMap.containsKey(LABEL_ALLIES_DATA) ? "OK" : "NG")
			);
		}
		GameLog.printInfo(" ラベル位置の特定完了");

		//地形データのパース
		int y = 0, x = 0;
		int fieldDataHeight = startRowMap.get(LABEL_BUILDING_DATA) - startRowMap.get(LABEL_FIELD_DATA) - 1;
		if (fieldDataHeight <= 0) {
			throw new IllegalFormatException("地形データが定義されていません");
		}
		mapData = new MapChip[fieldDataHeight][];
		buildingData = new Building[fieldDataHeight][];
		for (int i = startRowMap.get(LABEL_FIELD_DATA) + 1; i < startRowMap.get(LABEL_BUILDING_DATA); i++) {
			String[] line = data.get(i);
			mapData[y] = new MapChip[line.length];
			buildingData[y] = new Building[line.length];
			for (int j = 0; j < buildingData[y].length; j++) {
				buildingData[y][j] = Building.createDummyChip(fieldChipSet.getCutWidth(), fieldChipSet.getCutHeight());
			}
			for (String chip : line) {
				mapData[y][x] = fieldChipSet.get(chip);
				x++;
			}
			y++;
			x = 0;
		}
		GameLog.printInfo(" 地形データのパース完了");
		//建物データのパース
		Set<Point> buildingLocationSet = new HashSet<>();
		for (int i = startRowMap.get(LABEL_BUILDING_DATA) + 1; i < startRowMap.get(LABEL_RESOURCE_DATA); i++) {
			String[] line = data.get(i);
			if (line.length != 2) {
				throw new IllegalFormatException("建物データの定義が誤っています:" + Arrays.toString(line));
			}
			Point location = StringUtil.createPoint(line[0], "-");
			if (buildingLocationSet.contains(location)) {
				throw new IllegalFormatException("建物データの座標が重複しています:" + Arrays.toString(line));
			}
			MapChip chip = buildingChipSet.get(line[1].split("-")[0]);
			Team team = Team.valueOf(line[1].split("-")[1]);
			buildingLocationSet.add(location);
			this.buildingData[location.y][location.x] = new Building(chip, team);
		}
		GameLog.printInfo(" 建物データのパース完了");
		//司令部の存在確認
		Map<Team, Boolean> hqCheckMap = new EnumMap<>(Team.class);
		List<Team> teamList = getTeamList();
		for (Team t : teamList) {
			hqCheckMap.put(t, false);
		}
		for (Building[] line : buildingData) {
			for (Building buil : line) {
				Team team = buil.getTeam();
				if (!Team.NOTHING.equals(team) && !Team.NEUTRAL.equals(team)) {
					if (GameConstants.CHIP_ATTR_HQ.equals(buil.getMapChip().getAttribute().getName())) {
						hqCheckMap.put(team, true);
					}
				}
			}
		}
		for (Team team : hqCheckMap.keySet()) {
			if (!hqCheckMap.get(team)) {
				throw new IllegalFormatException("「" + team + "」陣営に「" + GameConstants.CHIP_ATTR_HQ + "」の建物がありません");
			}
		}
		GameLog.printInfo(" HQ存在確認完了");
		//資源データのパース
		playerStatusStorage = new PlayerStatusStorage();
		for (int i = startRowMap.get(LABEL_RESOURCE_DATA) + 1; i < startRowMap.get(LABEL_ALLIES_DATA); i++) {
			String[] line = data.get(i);
			if (line.length != 5) {
				throw new IllegalFormatException("資源データの定義が誤っています:" + Arrays.toString(line));
			}
			playerStatusStorage.add(new PlayerStatus(Team.valueOf(line[0]), playerStatusStorage)
					.setResouce(Resources.MONEY, new ResourcesValue(Integer.parseInt(line[1])))
					.setResouce(Resources.METAL, new ResourcesValue(Integer.parseInt(line[2])))
					.setResouce(Resources.OIL, new ResourcesValue(Integer.parseInt(line[3])))
					.setResouce(Resources.MANPOWER, new ResourcesValue(Integer.parseInt(line[4]))));
		}
		GameLog.printInfo(" 資源データのパース完了");
		//資源とデータ定義の陣営突合せ
		{
			List<Team> builTeamList = getTeamList();
			if (builTeamList.contains(Team.NEUTRAL)) {
				builTeamList.remove(Team.NEUTRAL);
			}
			if (builTeamList.contains(Team.NOTHING)) {
				builTeamList.remove(Team.NOTHING);
			}
			Collections.sort(builTeamList);
			List<Team> resTeamList = playerStatusStorage.teamList();
			Collections.sort(resTeamList);
			if (!builTeamList.equals(resTeamList)) {
				throw new IllegalFormatException("建物と資源の陣営が一致しません");
			}
		}
		GameLog.printInfo(" 資源データと建物の陣営突合せ完了");
		//同盟データのパース
		for (int i = startRowMap.get(LABEL_ALLIES_DATA) + 1; i < data.size(); i++) {
			String[] line = data.get(i);
			if (line.length == 1) {
				continue;
			}
			for (int index = 0; index < line.length; index++) {
				Team team = Team.valueOf(line[index]);
				if (!playerStatusStorage.contains(team)) {
					throw new IllegalFormatException("建物・資源が登録されていない陣営の同盟関係が定義されています");
				}
				PlayerStatus stat = playerStatusStorage.get(team);
				for (int target = 0; target < line.length; target++) {
					if (index == target) {
						continue;
					}
					stat.addAllies(Team.valueOf(line[target]));
				}
			}
		}
		GameLog.printInfo(" 同盟データのパース完了");
		//PlayerとPlayerName設定
		boolean playerSet = false;
		int comCount = 1;
		for (Team t : Team.values()) {
			if (Team.NOTHING.equals(t) || Team.NEUTRAL.equals(t)) {
				continue;
			}
			if (!playerStatusStorage.contains(t)) {
				continue;
			}
			PlayerStatus stat = playerStatusStorage.get(t);
			if (!playerSet) {
				stat.setControlPlayer(ControlPlayer.YOUR);
				stat.setPlayerName(PlayerConstants.getInstance().USER_NAME);
				playerSet = true;
			} else {
				stat.setControlPlayer(ControlPlayer.COM);
				stat.setPlayerName(ControlPlayer.COM.toString() + "-" + (comCount++));
			}
		}
		GameLog.printInfo(" Player設定完了");
		reader.free();
		watch.stop();
		GameLog.printInfo("フィールドマップのロードが完了:" + getName() + " size=[" + mapData[0].length + "," + mapData.length + "](" + watch.getTime() + "ms)");
		loaded = true;
		return this;
	}

	@Override
	public HexFieldMap free() {
		loaded = false;
		mapData = null;
		miniFieldMapImage = null;
		return this;
	}

	public BufferedImage getMiniBuildingMap(int size, boolean update) {
		if (!isLoaded()) {
			GameLog.print(Level.WARNING, "ロードされていないマップのミニマップが要求されました");
			throw new NotYetLoadedException("ミニマップが要求されましたが、ロードされていません");
		}
		if (size == prevMiniBuildingMapSize && !update && miniBuildingMapImage != null) {
			return miniBuildingMapImage;
		}
		miniBuildingMapImage = ImageUtil.newImage(buildingData[0].length, buildingData.length);
		int[][] pix = ImageUtil.getPixel2D(miniBuildingMapImage);
		for (int y = 0; y < buildingData.length; y++) {
			for (int x = 0; x < buildingData[y].length; x++) {
				pix[y][x] = ARGBColor.toARGB(buildingData[y][x].getTeam().getColor());
			}
		}
		ImageUtil.setPixel2D(miniBuildingMapImage, pix);
		miniBuildingMapImage = ImageEditor.resize(miniBuildingMapImage, ((float) size / (float) Math.max(buildingData.length, buildingData[0].length)));
		prevMiniBuildingMapSize = size;
		return miniBuildingMapImage;
	}

	public BufferedImage getMiniFieldMap(int size, boolean update) {
		if (!isLoaded()) {
			GameLog.print(Level.WARNING, "ロードされていないマップのミニマップが要求されました");
			throw new NotYetLoadedException("ミニマップが要求されましたが、ロードされていません");
		}
		if (size == prevMiniFieldMapSize && !update && miniFieldMapImage != null) {
			return miniFieldMapImage;
		}
		miniFieldMapImage = ImageUtil.newImage(mapData[0].length, mapData.length);
		int[][] pix = ImageUtil.getPixel2D(miniFieldMapImage);
		for (int y = 0; y < mapData.length; y++) {
			for (int x = 0; x < mapData[y].length; x++) {
				BufferedImage chipImage = mapData[y][x].getImage();
				int w = chipImage.getWidth() / 2;
				int h = chipImage.getHeight() / 2;
				pix[y][x] = ImageUtil.getPixel(chipImage, w, h);
			}
		}
		ImageUtil.setPixel2D(miniFieldMapImage, pix);
		miniFieldMapImage = ImageEditor.resize(miniFieldMapImage, ((float) size / (float) Math.max(mapData.length, mapData[0].length)));
		prevMiniFieldMapSize = size;
		return miniFieldMapImage;
	}

	public Building[][] getBuilding() {
		return buildingData;
	}

	public MapChip[][] getMapData() {
		return mapData;
	}

	public PlayerStatusStorage getPlayerStatusStorage() {
		return playerStatusStorage;
	}

	public int getMapWidth() {
		return mapData[0].length;
	}

	public int getMapHeight() {
		return mapData.length;
	}

	public int getTeamNum() {
		int team = 0;
		Set<Team> teamSet = new HashSet<>();
		for (Building[] buildingData1 : buildingData) {
			for (Building item : buildingData1) {
				Team t = item.getTeam();
				if (!teamSet.contains(t) && !Team.NEUTRAL.equals(t) && !Team.NOTHING.equals(t)) {
					teamSet.add(t);
					team++;
				}
			}
		}
		return team;
	}

	public List<Team> getTeamList() {
		List<Team> result = new ArrayList<>();
		for (Building[] buildingData1 : buildingData) {
			for (Building item : buildingData1) {
				Team t = item.getTeam();
				if (!result.contains(t) && !Team.NEUTRAL.equals(t) && !Team.NOTHING.equals(t)) {
					result.add(t);
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	public String toString() {
		return "HexFieldMap{" + "fileName=" + fileName + ", mapTitle=" + mapTitle + ", mapInfo=" + mapInfo + ", miniMapImage=" + miniFieldMapImage + ", loaded=" + loaded + '}';
	}

}
