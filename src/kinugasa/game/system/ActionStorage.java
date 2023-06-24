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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kinugasa.graphics.Animation;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;
import kinugasa.resource.sound.SoundStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_14:43:14<br>
 * @author Shinacho<br>
 */
public class ActionStorage extends DBStorage<Action> {

	private static final ActionStorage INSTANCE = new ActionStorage();

	private ActionStorage() {
	}

	public static ActionStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Action select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select "
					+ "ActionID, "
					+ "type, "
					+ "visibleName, "
					+ "desc, "
					+ "area, "
					+ "waitTime, "
					+ "SoundID, "
					+ "sortNo, "
					+ "spellTime, "
					+ "attackCount, "
					+ "dcsCSV, "
					+ "selectType, "
					+ "IFF, "
					+ "defaultTargetTeam, "
					+ "switchTeam, "
					+ "Targeting "
					+ "from action "
					+ "where actionID='" + id + "';";
			KResultSet rs = DBConnection.getInstance().execDirect(sql);
			if (rs.isEmpty()) {
				return null;
			}
			for (var v : rs) {
				//基本情報
				String actionID = v.get(0).get();
				ActionType type = v.get(1).of(ActionType.class);
				String visibleName = v.get(2).get();
				String desc = v.get(3).get();
				Action a = new Action(type, actionID, visibleName, desc);
				//area
				a.setArea(v.get(4).asInt());
				//waitTime
				a.setWaitTime(v.get(5).asInt());
				//Sound
				if (v.get(6).get() != null && !v.get(6).get().isEmpty()) {
					a.setSound(SoundStorage.getInstance().get(v.get(6).get()));
				}
				//sortID
				a.setSort(v.get(7).asInt());
				//spellTime
				a.setSpellTime(v.get(8).asInt());
				//atkCount
				a.setActionCount(v.get(9).asInt());
				//dcs
				Set<StatusKey> dcs = new HashSet<>();
				for (String s : v.get(10).safeSplit(",")) {
					StatusKey sk = StatusKeyStorage.getInstance().get(s);
					dcs.add(sk);
				}
				a.setDamageCalcStatusKey(dcs);
				//TARGET_OPTION
				if (v.get(11).get() != null && !v.get(11).get().isEmpty()) {
					TargetOption.SelectType selectType = v.get(11).of(TargetOption.SelectType.class);
					TargetOption.IFF iff = v.get(12).asBoolean() ? TargetOption.IFF.ON : TargetOption.IFF.OFF;
					TargetOption.DefaultTarget defaultTarget = v.get(13).of(TargetOption.DefaultTarget.class);
					TargetOption.SwitchTeam switchTeam = v.get(14).asBoolean() ? TargetOption.SwitchTeam.OK : TargetOption.SwitchTeam.NG;
					TargetOption.Targeting targeting = v.get(15).asBoolean() ? TargetOption.Targeting.ENABLE : TargetOption.Targeting.DISABLE;
					TargetOption targetOption = TargetOption.of(selectType, iff, defaultTarget, switchTeam, TargetOption.SelfTarget.YES, targeting);
					a.setTargetOption(targetOption);
				}
				//ACTION_TERM
				KResultSet akr = DBConnection.getInstance().execDirect("select"
						+ " t.ActionTermID, "
						+ " t.termType, "
						+ "t.val "
						+ "from Action_ActionTerm a, ActionTerm t "
						+ "where a.ActionTermID = t.ActionTermID "
						+ "and a.ActionID = '" + actionID + "';");
				if (!akr.isEmpty()) {
					for (var vv : akr) {
						String name = vv.get(0).get();
						TermType tt = vv.get(1).of(TermType.class);
						String val = vv.get(2).get();
						a.addTerm(new ActionTerm(name, tt, val));
					}
				}

				//ACTION_EVENT
				ActionEventStorage.ActionEvents events = ActionEventStorage.getInstance().getActionEvents(a);
				a.addBattleEvent(events.battle);
				a.addFieldEvent(events.field);

				return a;
			}

		}
		return null;
	}

	@Override
	protected List<Action> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select "
					+ "ActionID, "
					+ "type, "
					+ "visibleName, "
					+ "desc, "
					+ "area, "
					+ "waitTime, "
					+ "SoundID, "
					+ "sortNo, "
					+ "spellTime, "
					+ "attackCount, "
					+ "dcsCSV, "
					+ "selectType, "
					+ "IFF, "
					+ "defaultTargetTeam, "
					+ "switchTeam, "
					+ "Targeting "
					+ "from action;";
			KResultSet rs = DBConnection.getInstance().execDirect(sql);
			if (rs.isEmpty()) {
				return Collections.emptyList();
			}
			List<Action> res = new ArrayList<>();
			
			
			for (var v : rs) {
				//基本情報
				String actionID = v.get(0).get();
				ActionType type = v.get(1).of(ActionType.class);
				String visibleName = v.get(2).get();
				String desc = v.get(3).get();
				Action a = new Action(type, actionID, visibleName, desc);
				//area
				a.setArea(v.get(4).asInt());
				//waitTime
				a.setWaitTime(v.get(5).asInt());
				//Sound
				if (v.get(6).get() != null && !v.get(6).get().isEmpty()) {
					a.setSound(SoundStorage.getInstance().get(v.get(6).get()));
				}
				//sortID
				a.setSort(v.get(7).asInt());
				//spellTime
				a.setSpellTime(v.get(8).asInt());
				//atkCount
				a.setActionCount(v.get(9).asInt());
				//dcs
				Set<StatusKey> dcs = new HashSet<>();
				for (String s : v.get(10).safeSplit(",")) {
					StatusKey sk = StatusKeyStorage.getInstance().get(s);
					dcs.add(sk);
				}
				a.setDamageCalcStatusKey(dcs);
				//TARGET_OPTION
				if (v.get(11).get() != null && !v.get(11).get().isEmpty()) {
					TargetOption.SelectType selectType = v.get(11).of(TargetOption.SelectType.class);
					TargetOption.IFF iff = v.get(12).asBoolean() ? TargetOption.IFF.ON : TargetOption.IFF.OFF;
					TargetOption.DefaultTarget defaultTarget = v.get(13).of(TargetOption.DefaultTarget.class);
					TargetOption.SwitchTeam switchTeam = v.get(14).asBoolean() ? TargetOption.SwitchTeam.OK : TargetOption.SwitchTeam.NG;
					TargetOption.Targeting targeting = v.get(15).asBoolean() ? TargetOption.Targeting.ENABLE : TargetOption.Targeting.DISABLE;
					TargetOption targetOption = TargetOption.of(selectType, iff, defaultTarget, switchTeam, TargetOption.SelfTarget.YES, targeting);
					a.setTargetOption(targetOption);
				}

				//ACTION_TERM
				KResultSet akr = DBConnection.getInstance().execDirect("select"
						+ " t.visibleName, "
						+ " t.termType, "
						+ "t.val "
						+ "from Action_ActionTerm a, ActionTerm t "
						+ "where a.ActionTermID = t.ActionTermID "
						+ "and a.ActionID = '" + actionID + "';");
				if (!akr.isEmpty()) {
					for (var vv : akr) {
						String name = vv.get(0).get();
						TermType tt = vv.get(1).of(TermType.class);
						String val = vv.get(2).get();
						a.addTerm(new ActionTerm(name, tt, val));
					}
				}

				//ACTION_EVENT
				ActionEventStorage.ActionEvents events = ActionEventStorage.getInstance().getActionEvents(a);
				a.addBattleEvent(events.battle);
				a.addFieldEvent(events.field);

				res.add(a);
			}
			return res;

		}
		return Collections.emptyList();
	}
	

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from action;").cell(0, 0).asInt();
		}
		return 0;
	}

}
