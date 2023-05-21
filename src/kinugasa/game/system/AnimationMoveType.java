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

import java.awt.geom.Point2D;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_21:45:01<br>
 * @author Shinacho<br>
 */
public enum AnimationMoveType {
	FIELD(0) {
		@Override
		public KVector createVector(Point2D.Float user, Point2D.Float tgt) {
			return new KVector(0, 0);
		}
	},
	ROTATE_TGT_TO_USER(0) {
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
