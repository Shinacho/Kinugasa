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
package kinugasa.game.field4;

import kinugasa.game.I18N;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.object.FourDirection;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_18:35:10<br>
 * @author Shinacho<br>
 */
public class Node implements Nameable {

	private Mode mode;
	private String name;
	private int x, y;
	private String exitFieldMapName;
	private String exitNodeName;
	private String tooltip;
	private Sound se = null;
	private NodeAccepter accepter;
	private FourDirection outDir;

	public enum Mode {
		INOUT,
		OUT,
	}

	private Node() {
	}

	public static Node ofInOutNode(String name, String exitFieldMapName,
			String exitNodeName, int x, int y, String tooltip, NodeAccepter accepter, FourDirection outDir) {
		Node n = new Node();
		n.name = name;
		n.exitFieldMapName = exitFieldMapName;
		n.exitNodeName = exitNodeName;
		n.x = x;
		n.y = y;
		n.tooltip = tooltip;
		n.accepter = accepter;
		n.mode = Mode.INOUT;
		n.outDir = outDir;
		return n;
	}

	public static Node ofOutNode(String name, String outMapName, int x, int y, FourDirection outDir) {
		Node n = ofInOutNode(name, outMapName, null, x, y, null, null, null);
		n.mode = Mode.OUT;
		n.outDir = outDir;
		return n;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getExitNodeName() {
		return exitNodeName;
	}

	public String getExitFieldMapName() {
		return exitFieldMapName;
	}

	public void setSe(Sound se) {
		this.se = se;
	}

	public Sound getSe() {
		return se;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public D2Idx getIdx() {
		return new D2Idx(x, y);
	}

	public String getTooltip() {
		return tooltip;
	}

	public String getTooltipI18Nd() {
		return I18N.get(tooltip);
	}

	public NodeAccepter getAccepter() {
		return accepter;
	}

	public FourDirection getOutDir() {
		return outDir;
	}

	@Override
	public String toString() {
		return "FieldMapNode{" + "mode=" + mode + ", name=" + name + ", x=" + x + ", y=" + y + ", exitFieldMapName=" + exitFieldMapName + ", exitNodeName=" + exitNodeName + ", tooltip=" + tooltip + ", se=" + se + ", accepter=" + accepter + '}';
	}

}
