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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.Nullable;
import kinugasa.graphics.Animation;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.game.NotNull;

/**
 *
 * @vesion 1.0.0 - 2023/10/22_23:12:59<br>
 * @author Shinacho<br>
 */
public class BeamEffectEvents {

	private static final BeamEffectEvents INSTANCE = new BeamEffectEvents();

	private BeamEffectEvents() {
	}

	public static BeamEffectEvents getInstance() {
		return INSTANCE;
	}

	public boolean has(String id) {
		for (Key k : Key.values()) {
			if (k.toString().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public 光線エフェクトイベント of(String id) {
		for (Key k : Key.values()) {
			if (k.toString().equals(id)) {
				return new 光線エフェクトイベント(k);
			}
		}
		return null;
	}

	@NotNull
	public 光線エフェクトイベント of(Key k) {
		return new 光線エフェクトイベント(k);
	}

	public enum Key {
		光線_白_3PT(GraphicsUtil.transparent(Color.WHITE, 128), 3f),
		光線_黒_3PT(GraphicsUtil.transparent(Color.BLACK, 128), 3f),
		光線_青_3PT(GraphicsUtil.transparent(Color.BLUE, 128), 3f),
		光線_緑_3PT(GraphicsUtil.transparent(Color.GREEN, 128), 3f),
		光線_黄_3PT(GraphicsUtil.transparent(Color.YELLOW, 128), 3f),
		光線_赤_3PT(GraphicsUtil.transparent(Color.RED, 128), 3f),
		光線_白_6PT(GraphicsUtil.transparent(Color.WHITE, 128), 6f),
		光線_黒_6PT(GraphicsUtil.transparent(Color.BLACK, 128), 6f),
		光線_青_6PT(GraphicsUtil.transparent(Color.BLUE, 128), 6f),
		光線_緑_6PT(GraphicsUtil.transparent(Color.GREEN, 128), 6f),
		光線_黄_6PT(GraphicsUtil.transparent(Color.YELLOW, 128), 6f),
		光線_赤_6PT(GraphicsUtil.transparent(Color.RED, 128), 6f),
		光線_白_12PT(GraphicsUtil.transparent(Color.WHITE, 128), 12f),
		光線_黒_12PT(GraphicsUtil.transparent(Color.BLACK, 128), 12f),
		光線_青_12PT(GraphicsUtil.transparent(Color.BLUE, 128), 12f),
		光線_緑_12PT(GraphicsUtil.transparent(Color.GREEN, 128), 12f),
		光線_黄_12PT(GraphicsUtil.transparent(Color.YELLOW, 128), 12),
		光線_赤_12PT(GraphicsUtil.transparent(Color.RED, 128), 12f),
		光線_白_24PT(GraphicsUtil.transparent(Color.WHITE, 128), 24f),
		光線_黒_24PT(GraphicsUtil.transparent(Color.BLACK, 128), 24f),
		光線_青_24PT(GraphicsUtil.transparent(Color.BLUE, 128), 24f),
		光線_緑_24PT(GraphicsUtil.transparent(Color.GREEN, 128), 24f),
		光線_黄_24PT(GraphicsUtil.transparent(Color.YELLOW, 128), 24),
		光線_赤_24PT(GraphicsUtil.transparent(Color.RED, 128), 24f),;

		private Color color;
		private float size;

		private Key(Color color, float size) {
			this.color = color;
			this.size = size;
		}

		public Color getColor() {
			return color;
		}

		public float getSize() {
			return size;
		}

	}

	private static class 光線エフェクトイベント extends ActionEvent {

		private Key key;

		public 光線エフェクトイベント(Key key) {
			super(key.toString());
			this.key = key;
			setP(1f);
			setEventType(ActionEventType.ビームエフェクト);
			setEvent起動条件(Event起動条件.前段がないか直前のイベント成功時のみ起動);
		}

		public Key getKey() {
			return key;
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, final ActionResult ar, boolean isUserEvent) {
			if (isUserEvent) {
				throw new GameSystemException("cannot use for user ivents : " + this);
			}
			if (!起動条件判定＿起動OK(tgt, ar, isUserEvent)) {
				return;
			}
			List<Sprite> sp = new ArrayList<>();
			final int max = 64;
			for (int i = 0; i < max; i++) {
				final int w = i;
				sp.add(new BasicSprite(
						0, 0,
						GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize(),
						GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize()) {

					Point2D.Float p1 = user.getSprite().getCenter();
					Point2D.Float p2 = tgt.getSprite().getCenter();

					@Override
					public void draw(GraphicsContext g) {
						Graphics2D g2 = g.create();
						g2.setColor(key.color);
						g2.setStroke(new BasicStroke(Random.randomFloat() + key.size));
						g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
						g2.dispose();
					}
				});
			}
			Animation ani = Animation.of(new FrameTimeCounter(3), sp);
			ani.setRepeat(false);

			ActionResult.EventActorResult r = new ActionResult.EventActorResult(tgt, this);
			r.otherAnimation = new AnimationSprite(ani);
			r.msgI18Nd = null;//重要
			Map<Actor, ActionResult.EventActorResult> map = new HashMap<>();
			map.put(tgt, r);
			ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.成功, map));
		}
	}

}
