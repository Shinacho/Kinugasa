/*
 * The MIT License
 *
 * Copyright 2023 Dra.
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
import kinugasa.game.OneceTime;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2023/05/06_9:25:23<br>
 * @author Dra211<br>
 */
public class QuestStageStorage implements XMLFileSupport {

	private Map<String, Map<Integer, QuestStage>> stageMap = new HashMap<>();

	private static final QuestStageStorage INSTANCE = new QuestStageStorage();

	public static QuestStageStorage getInstance() {
		return INSTANCE;
	}

	@OneceTime
	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath).load();
		
		XMLElement root = file.getFirst();
		for(XMLElement line : root.getElement("line")){
			String id = line.getAttributes().get("id").getValue();
			int stage = line.getAttributes().get("stage").getIntValue();
			String title = line.getAttributes().get("title").getValue();
			String desc = line.getAttributes().get("desc").getValue();
			
			QuestStage qs = new QuestStage(stage, title, desc);
			
			if(!stageMap.containsKey(id)){
				stageMap.put(id, new HashMap<Integer, QuestStage>());
			}
			stageMap.get(id).put(stage, qs);
			
			//TODO:removeNPCèÓïÒÇ±Ç±
		}
		
		file.dispose();
	}

	public QuestStage get(String name, int stageId) {
		return stageMap.get(name).get(stageId);
	}

}
