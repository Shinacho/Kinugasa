/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
package kinugasa.graphics;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kinugasa.resource.KImage;
import kinugasa.util.ArrayIndexModel;
import kinugasa.util.SimpleIndex;
import kinugasa.util.TimeCounter;

/**
 * 画像配列をアニメーションとして再生するための画像とインデックスを保持します.
 * <br>
 * アニメーション用画像はnullを許可します。それぞれのメソッドでは、アニメーションの要素となる
 * 画像が1つもない場合（つまりnullの場合）にnullを返します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_1:39:19<br>
 * @author Shinacho<br>
 */
public class Animation implements Iterable<KImage>, Cloneable {

	/**
	 * アニメーションの1つの要素が表示される時間間隔を指定するタイムカウンタです.
	 */
	private TimeCounter visibleTime;
	/**
	 * アニメーションの遷移条件を指定するための配列インデックスです.
	 */
	private ArrayIndexModel index;
	/**
	 * アニメーションとして再生される画像の配列です.
	 */
	private KImage[] images;
	private boolean repeat = true;
	private boolean stop;

	/**
	 * 新しいアニメーションを構築します. このコンストラクタでは、配列インデックスは＋方向へループするシーケンシャルなモデルになります。<br>
	 *
	 * @param visibleTime アニメーションの1枚の画像の表示時間を定義するタイムカウンタです。<br>
	 * @param images 表示する画像を1つ以上送信します。<br>
	 */
	public Animation(TimeCounter visibleTime, BufferedImage... images) {
		this(visibleTime, new SimpleIndex(), images);
	}

	public Animation(TimeCounter visibleTime, List<BufferedImage> images) {
		this(visibleTime, images.toArray(new BufferedImage[images.size()]));
	}

	/**
	 * 新しいアニメーションを構築します. このコンストラクタでは、配列インデックスは＋方向へループするシーケンシャルなモデルになります。<br>
	 *
	 * @param visibleTime アニメーションの1枚の画像の表示時間を定義するタイムカウンタです。<br>
	 * @param images 表示する画像を1つ以上送信します。<br>
	 */
	public Animation(TimeCounter visibleTime, KImage... images) {
		this(visibleTime, new SimpleIndex(), images);
	}

	/**
	 * 新しいアニメーションを構築します.
	 *
	 * @param visibleTime アニメーションの1枚の画像の表示時間を定義するタイムカウンタです。<br>
	 * @param index アニメーションの遷移順序を定義する配列のインデックスです。<br>
	 * @param images 表示する画像を1つ以上送信します。<br>
	 */
	public Animation(TimeCounter visibleTime, ArrayIndexModel index, BufferedImage... images) {
		this.visibleTime = visibleTime;
		this.index = index;
		this.images = new KImage[images.length];
		for (int i = 0; i < images.length; i++) {
			this.images[i] = new KImage(images[i]);
		}
	}

	/**
	 * 新しいアニメーションを構築します.
	 *
	 * @param visibleTime アニメーションの1枚の画像の表示時間を定義するタイムカウンタです。<br>
	 * @param index アニメーションの遷移順序を定義する配列のインデックスです。<br>
	 * @param images 表示する画像を1つ以上送信します。<br>
	 */
	public Animation(TimeCounter visibleTime, ArrayIndexModel index, KImage... images) {
		this.visibleTime = visibleTime;
		this.index = index;
		this.images = images;
	}

	/**
	 * このアニメーションに設定されている画像を取得します.
	 *
	 * @return このアニメーションの画像全てを取得します。この配列は防御的コピーされません。
	 * 画像が設定されていない場合nullを返します。<br>
	 */
	public KImage[] getImages() {
		return images;
	}

	/**
	 * 指定したインデックス位置のアニメーション要素を取得します.
	 *
	 * @param index インデックスを指定します。<br>
	 *
	 * @return 指定したインデックス位置のアニメーション要素となる画像を返します。 画像が設定されていない場合nullを返します。<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException 不正なインデックスを送信した場合に投げられます。<br>
	 */
	public KImage getImage(int index) throws ArrayIndexOutOfBoundsException {
		if (images == null) {
			return null;
		}
		return images[index];
	}

	/**
	 * このアニメーションの画像を変更します.
	 *
	 * @param images 新しい画像配列を送信します。<br>
	 */
	public void setImages(KImage... images) {
		this.images = images;
	}

	/**
	 * このアニメーションの画像を変更します.
	 *
	 * @param images 新しい画像配列を送信します。<br>
	 */
	public void setImages(BufferedImage... images) {
		this.images = new KImage[images.length];
		for (int i = 0; i < images.length; i++) {
			this.images[i] = new KImage(images[i]);
		}
	}

	/**
	 * このアニメーションの画像を変更します.
	 *
	 * @param index アニメーション要素を置き換える位置のインデックスを指定します。<br>
	 * @param image 新しい画像を送信します。<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException 不正なインデックスを送信した場合に投げられます。<br>
	 */
	public void setImage(int index, BufferedImage image) throws ArrayIndexOutOfBoundsException {
		setImage(index, new KImage(image));
	}

	/**
	 * このアニメーションの画像を変更します.
	 *
	 * @param index アニメーション要素を置き換える位置のインデックスを指定します。<br>
	 * @param image 新しい画像を送信します。<br>
	 *
	 * @throws ArrayIndexOutOfBoundsException 不正なインデックスを送信した場合に投げられます。<br>
	 */
	public void setImage(int index, KImage image) {
		images[index] = image;
	}

	/**
	 * アニメーションの遷移順序を変更します.
	 *
	 * @param index 新しい遷移アルゴリズムを送信します。<br>
	 */
	public void setIndex(ArrayIndexModel index) {
		this.index = index;
	}

	/**
	 * このアニメーションに設定されている配列のインデックスを取得します.
	 * このメソッドは、設定されているインデックスモデルをArrayIndexModelとして返します。<br>
	 * このメソッドを頻繁に使う場合は、キャストしたインデックスを返せるよう サブクラスを作成することができます。<br>
	 *
	 * @return このアニメーションに設定されているインデックスモデルを返します。<br>
	 */
	public ArrayIndexModel getIndex() {
		return index;
	}

	/**
	 * アニメーションの1つの要素の再生時間を指定するためのタイムカウンタを設定します.
	 *
	 * @param visibleTime 新しいタイムカウンタを送信します。<br>
	 */
	public void setVisibleTime(TimeCounter visibleTime) {
		this.visibleTime = visibleTime;
	}

	/**
	 * このアニメーションに設定されている表示時間カウンタを取得します.
	 * このメソッドは、設定されているタイムカウンタをTimeCounterとして返します。<br>
	 * このメソッドを頻繁に使う場合は、キャストしたカウンタを返せるよう サブクラスを作成することができます。<br>
	 *
	 * @return このアニメーションに設定されているタイムカウンタを返します。<br>
	 */
	public TimeCounter getVisibleTime() {
		return visibleTime;
	}

	/**
	 * 表示時間の判定を行います. 現在表示中の要素の表示時間が経過した場合には、 インデックスを更新し、描画すべき画像を変更します。<br>
	 */
	public void update() {
		if (stop) {
			return;
		}
		if (visibleTime.isReaching()) {
			if (index.getIndex() != images.length - 1 || repeat) {
				index.index(images == null ? 0 : images.length);
			}
		}
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isStop() {
		return stop;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public boolean isEnded() {
		if (repeat) {
			return false;
		}
		if (images == null) {
			return false;
		}
		return index.getIndex() >= images.length - 1;
	}

	/**
	 * このアニメーションで、現在表示すべき画像を返します.
	 *
	 * @return このアニメーションで現在表示される要素を返します。<br>
	 */
	public KImage getCurrentImage() {
		return images == null ? null : images[index.getIndex()];
	}

	/**
	 * このアニメーションで、現在表示すべき画像を返します.
	 *
	 * @return このアニメーションで現在表示される要素を返します。<br>
	 */
	public BufferedImage getCurrentBImage() {
		return images == null ? null : images[index.getIndex()].get();
	}

	/**
	 * 画像配列の要素数を返します.
	 *
	 * @return アニメーションとして再生される要素の数を返します。<br>
	 */
	public int length() {
		return images == null ? 0 : images.length;
	}

	@Override
	public Iterator<KImage> iterator() {
		return Arrays.asList(images).iterator();
	}

	@Override
	public Animation clone() {
		try {
			Animation result = (Animation) super.clone();
			if (images != null) {
				result.images = this.images.clone();
			}
			result.index = this.index.clone();
			result.visibleTime = this.visibleTime.clone();
			return result;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	@Override
	public String toString() {
		return "Animation{" + "visibleTime=" + visibleTime + ", index=" + index + ", images=" + images + ", repeat=" + repeat + ", stop=" + stop + '}';
	}

}
