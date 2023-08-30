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
package kinugasa.game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 * スクリーンエフェクトのプリセットを構築するクラスです。 エフェクトは時間を持っており、その経過時間分のフレームが経過すると破棄されます。
 *
 * @vesion 1.0.0 - 2023/07/19_20:13:22<br>
 * @author Shinacho<br>
 */
public final class ScreenEffects {

	private ScreenEffects() {
	}

	public static ScreenEffect reverseColor(int time) {

		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				BufferedImage res = src;
				res = ImageUtil.reverseColor(res, null);

				if (tc.isReaching()) {
					ended = true;
				}
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};
	}

	public static ScreenEffect reverseColor(BufferedImage mask, int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;
			private BufferedImage maskImage = null;
			private int[][] pix2 = null;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				BufferedImage res = src;
				res = ImageUtil.reverseColorByMaskedArea(res, mask);

				if (tc.isReaching()) {
					ended = true;
					pix2 = null;
					maskImage = null;
				}
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};
	}

	public static ScreenEffect horizontalReverseImage(int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}

				BufferedImage res = src;
				Graphics2D g2 = ImageUtil.createGraphics2D(res, RenderingQuality.NOT_USE);
				g2.drawImage(res, res.getWidth(), 0, -res.getWidth(), res.getHeight(), null);
				g2.dispose();

				if (tc.isReaching()) {
					ended = true;
				}
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};
	}

	public static ScreenEffect verticalReverseImage(int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}

				BufferedImage res = src;
				int[][] pix = ImageUtil.getPixel2D(res);
				int[][] newPix = new int[pix.length][];

				for (int y = pix.length - 1, yy = 0; y >= 0; y--, yy++) {
					newPix[yy] = pix[y];
				}

				ImageUtil.setPixel2D(res, newPix);

				if (tc.isReaching()) {
					ended = true;
				}
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};

	}

	public static ScreenEffect reverseImage(int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}

				if (tc.isReaching()) {
					ended = true;
				}
				BufferedImage res = src;
				Graphics2D g2 = ImageUtil.createGraphics2D(res, RenderingQuality.NOT_USE);
				g2.drawImage(res, res.getWidth(), 0, -res.getWidth(), res.getHeight(), null);
				g2.dispose();

				int[][] pix = ImageUtil.getPixel2D(res);
				int[][] newPix = new int[pix.length][];

				for (int y = pix.length - 1, yy = 0; y >= 0; y--, yy++) {
					newPix[yy] = pix[y];
				}

				ImageUtil.setPixel2D(res, newPix);

				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};
	}

	public static ScreenEffect monochrome(int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}

				if (tc.isReaching()) {
					ended = true;
				}
				return ImageUtil.monochrome(src, 128, null);
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};

	}

	public static ScreenEffect mozaic(int size, int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				if (tc.isReaching()) {
					ended = true;
				}
				return ImageUtil.mosaic(src, size, null);
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};
	}

	public static ScreenEffect rotate(float r, int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				if (tc.isReaching()) {
					ended = true;
				}
				BufferedImage res = ImageUtil.copy(src);
				Graphics2D g = ImageUtil.createGraphics2D(res, RenderingQuality.NOT_USE);
				g.setColor(GameOption.getInstance().getBackColor());
				g.fillRect(0, 0, res.getWidth(), res.getHeight());
				g.rotate(Math.toRadians(r), res.getWidth() / 2, res.getHeight() / 2);
				g.drawImage(src, 0, 0, null);
				g.dispose();
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};

	}

	public static ScreenEffect shake(int r, int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				if (tc.isReaching()) {
					ended = true;
				}
				BufferedImage res = ImageUtil.copy(src);
				Graphics2D g = ImageUtil.createGraphics2D(res, RenderingQuality.NOT_USE);
				g.setColor(GameOption.getInstance().getBackColor());
				g.fillRect(0, 0, res.getWidth(), res.getHeight());
				int x = Random.randomBool()
						? -Random.randomAbsInt(r)
						: Random.randomAbsInt(r);
				int y = Random.randomBool()
						? -Random.randomAbsInt(r)
						: Random.randomAbsInt(r);

				g.drawImage(src, x, y, null);
				g.dispose();
				return res;
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};

	}

	public static ScreenEffect grayScale(int time) {
		return new ScreenEffect() {
			private FrameTimeCounter tc = new FrameTimeCounter(time);
			private boolean ended = false;

			@Override
			public BufferedImage doIt(BufferedImage src) {
				if (ended) {
					return src;
				}
				if (tc.isReaching()) {
					ended = true;
				}
				return ImageUtil.grayScale(src, null);
			}

			@Override
			public boolean isEnded() {
				return ended;
			}

			@Override
			public boolean isRunning() {
				return !ended;
			}

		};

	}

	//強制的に上書きする
	public static ScreenEffect blackout(int speed) {
		throw new UnsupportedOperationException("そのうち対応");

	}

	//強制的に上書きする
	public static ScreenEffect whiteOut(int speed) {
		throw new UnsupportedOperationException("そのうち対応");

	}

	//徐々に上書きする
	public static ScreenEffect fadeoutBlack(int speed) {
		throw new UnsupportedOperationException("そのうち対応");

	}

	//徐々に上書きする
	public static ScreenEffect fadeoutWhite(int speed) {
		throw new UnsupportedOperationException("そのうち対応");

	}

	public static ScreenEffect fadeinBlack(int speed) {
		throw new UnsupportedOperationException("そのうち対応");
	}

	public static ScreenEffect fadeinWhite(int speed) {
		throw new UnsupportedOperationException("そのうち対応");
	}

}
