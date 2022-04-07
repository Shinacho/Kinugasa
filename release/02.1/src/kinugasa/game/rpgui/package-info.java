/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
/**
 * マップチップ方式によるフィールドマップの構築と描画に関する機能およびそれに関連するGUI機能を提供します.
 * <br>
 * <br>
 * このパッケージでは、XMLファイルを利用して多重レイヤフィールドマップを構築できます。<br>
 * ダイナミックロードやチップセットのメモリ管理など、高度な機能は備わっていません。<br>
 * 比較的小規模のゲーム向けです。<br>
 * <br>
 * マップ構築の手順を以下に示します。<br>
 * <br>
 * <ol>
 * <li>ChipAttributeのロード</li>
 * ChipAttributeStorageクラスにチップ属性をXMLからロードします。
 * <br><br>
 * <li>Vehicleのロード</li>
 * VehicleStorageクラスに移動手段と移動可能な属性をXMLからロードします。
 * <br><br>
 * <li>ChipSetのロード</li>
 * ChipSetStorageクラスにチップセットをXMLからロードします。
 * <br><br>
 * <li>FieldMapBuilderのロード</li>
 * FieldMapBuilderStorageクラスにマップビルダをXMLからロードします。
 * <br><br>
 * <li>FieldMapDataのロード</li>
 * フィールドマップを実際に使用する段階で、FieldMapBuilderからFieldMapDataをXMLからロードします。<br>
 * <br><br>
 * </ol>
 * <br>
 * それぞれのXMLではDTDを使用できます。DTDファイルはresource/dtdに格納されています。<br>
 * <br>
 * @version 1.0.0 - 2013/04/21_17:20:51<br>
 * @author Dra0211<br>
 */
package kinugasa.game.rpgui;
