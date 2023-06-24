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
import java.util.List;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_13:06:44<br>
 * @author Shinacho<br>
 */
public class ConditionStorage extends DBStorage<Condition> {

	private static final ConditionStorage INSTANCE = new ConditionStorage();

	private ConditionStorage() {
	}

	public static ConditionStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Condition select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select conditionID,desc,priority from condition where conditionID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			ConditionKey key = new ConditionKey(id, kr.row(0).get(1).get(), kr.row(0).get(2).asInt());
			//Effectの取得
			String sql = "select"
					+ " ce.ConditionEffectID,"
					+ "ce.EffectContinueType,"
					+ "ce.tim,"
					+ "ce.EffectTargetType,"
					+ "ce.EffectSetType,"
					+ "ce.targetName,"
					+ "ce.val,"
					+ "ce.p"
					+ " from ConditionEffect ce,Condition_ConditionEffect cc"
					+ " where ce.ConditionEffectID = cc.ConditionEffectID"
					+ " and cc.ConditionID='" + id + "';";
			KResultSet cc = DBConnection.getInstance().execDirect(sql);
			Condition c = new Condition(key);
			if (cc.isEmpty()) {
				return c;
			}
			List<ConditionEffect> effects = new ArrayList<>();
			for (List<DBValue> v : cc) {
				String eid = v.get(0).get();
				EffectContinueType continueType = v.get(1).of(EffectContinueType.class);
				int time = v.get(2).asInt();
				EffectTargetType targetType = v.get(3).of(EffectTargetType.class);
				EffectSetType setType = v.get(4).of(EffectSetType.class);
				String targetName = v.get(5).get();
				float val = v.get(6).asFloat();
				float p = v.get(7).asFloat();
				effects.add(new ConditionEffect(eid, continueType, targetType, setType, targetName, val, p, time));
			}
			c.setEffects(effects);
			return c;
		}
		return null;
	}

	@Override
	protected List<Condition> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select conditionID,desc,priority from condition;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Condition> res = new ArrayList<>();
			for (List<DBValue> vv : kr) {
				String conditionID = vv.get(0).get();
				String desc = vv.get(1).get();
				int pri = vv.get(2).asInt();
				ConditionKey key = new ConditionKey(conditionID, desc, pri);
				//Effectの取得
				String sql = "select"
						+ " ce.ConditionEffectID,"
						+ "ce.EffectContinueType,"
						+ "ce.tim,"
						+ "ce.EffectTargetType,"
						+ "ce.EffectSetType,"
						+ "ce.targetName,"
						+ "ce.val,"
						+ "ce.p"
						+ " from ConditionEffect ce,Condition_ConditionEffect cc"
						+ " where ce.ConditionEffectID = cc.ConditionEffectID"
						+ " and cc.ConditionID='" + conditionID + "';";
				KResultSet cc = DBConnection.getInstance().execDirect(sql);
				Condition c = new Condition(key);
				if (!cc.isEmpty()) {
					List<ConditionEffect> effects = new ArrayList<>();
					for (List<DBValue> v : cc) {
						String eid = v.get(0).get();
						EffectContinueType continueType = v.get(1).of(EffectContinueType.class);
						int time = v.get(2).asInt();
						EffectTargetType targetType = v.get(3).of(EffectTargetType.class);
						EffectSetType setType = v.get(4).of(EffectSetType.class);
						String targetName = v.get(5).get();
						float val = v.get(6).asFloat();
						float p = v.get(7).asFloat();
						effects.add(new ConditionEffect(eid, continueType, targetType, setType, targetName, val, p, time));
					}
					c.setEffects(effects);
				}
				res.add(c);
			}
			return res;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from condition").row(0).get(0).asInt();
		}
		return 0;
	}

}
