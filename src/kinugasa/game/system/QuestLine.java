/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import java.util.HashMap;
import java.util.Map;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_16:56:15<br>
 * @author Dra211<br>
 */
public class QuestLine implements Nameable {

	private String name;
	private Map<Integer, String> desc;
	private FlagStorage flag;
	private QuestStage stage;

	public QuestLine(String name) {
		this.name = name;
		this.desc = new HashMap<>();
		flag = new FlagStorage(name);
		this.stage = new QuestStage();
	}

	public Map<Integer, String> getDesc() {
		return desc;
	}

	public void setDesc(Map<Integer, String> desc) {
		this.desc = desc;
	}

	public QuestStage getStage() {
		return stage;
	}

	public FlagStorage getFlag() {
		return flag;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "QuestLine{" + "name=" + name + ", desc=" + desc + ", flag=" + flag + ", stage=" + stage + '}';
	}

}