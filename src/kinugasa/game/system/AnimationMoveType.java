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

import java.awt.geom.Point2D;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_21:45:01<br>
 * @author Shinacho<br>
 */
public enum AnimationMoveType {
	BEAM_WHITE(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_BLACK(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_RED(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_GREN(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_BLUE(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_WHITE_THICK(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_BLACK_THICK(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_RED_THICK(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_GREN_THICK(0) {

		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	BEAM_BLUE_THICK(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}

	},
	FIELD(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	TGT(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	USER(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	NONE(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	USER_TO_TGT_4(4) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_4(4) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_8(8) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_8(8) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_12(12) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_12(12) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_16(16) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_16(16) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_20(20) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_20(20) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_24(24) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_24(24) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_28(28) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_28(28) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_32(32) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	TGT_TO_USER_32(32) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(tgt, user);
			v.setSpeed(getSpeed());
			return v;
		}
	},
	USER_TO_TGT_64(64) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			KVector v = new KVector();
			v.setAngle(user, tgt);
			v.setSpeed(getSpeed());
			return v;
		}
	},;
	private float speed;

	public abstract KVector createVector(Point2D.Float user, Point2D.Float tgt);

	private AnimationMoveType(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

}
