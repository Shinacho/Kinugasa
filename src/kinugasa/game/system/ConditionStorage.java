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
					+ "ce.time,"
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
				int time = v.get(3).asInt();
				EffectTargetType targetType = v.get(4).of(EffectTargetType.class);
				EffectSetType setType = v.get(5).of(EffectSetType.class);
				String targetName = v.get(6).get();
				float val = v.get(7).asFloat();
				float p = v.get(8).asFloat();
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
						+ "ce.time,"
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
						int time = v.get(3).asInt();
						EffectTargetType targetType = v.get(4).of(EffectTargetType.class);
						EffectSetType setType = v.get(5).of(EffectSetType.class);
						String targetName = v.get(6).get();
						float val = v.get(7).asFloat();
						float p = v.get(8).asFloat();
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
