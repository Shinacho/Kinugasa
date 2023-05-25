/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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

import java.util.List;

/**
 *
 * @vesion 1.0.0 - May 23, 2023_12:29:16 PM<br>
 * @author Shinacho<br>
 */
public abstract class UserDefinedCmdAction extends CmdAction {

	public UserDefinedCmdAction(ActionType t, String name, String desc) {
		super(t, name, desc);
	}

	public final void ActionStorageAddThis() {
		ActionStorage.getInstance().add(this);
	}

	@Override
	public abstract ActionResult exec(ActionTarget tgt);

	@Override
	public abstract TargetOption getTargetOption();

	@Override
	public abstract int getArea();

	@Override
	public abstract int getSpellTime();

	@Override
	public abstract List<ActionTerm> getTerms();
}
