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
package kinugasa.game.event.fb;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImagePainterStorage;
import kinugasa.object.ImageSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.KVector;
import kinugasa.object.movemodel.AngleChange;
import kinugasa.object.movemodel.BasicMoving;
import kinugasa.object.movemodel.CompositeMove;
import kinugasa.object.movemodel.SpeedChange;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_8:08:29<br>
 * @author Shinacho<br>
 */
public class FrameTimeEventManager implements XMLFileSupport {

	private HashMap<Long, List<FrameTimeEvent<?>>> events = new HashMap<>();

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(filePath + " is not found");
		}
		for (XMLElement e : file.load().getFirst().getElement("EVENT")) {
			//frame
			long frame = Long.parseLong(e.getAttributes().get("frame").getValue());

			//CREATE
			for (XMLElement c : e.getElement("CREATE")) {
				//type
				String type = c.getAttributes().get("type").getValue();
				switch (type) {
					case "IMAGESPRITE":
						//location,size
						float x = c.getAttributes().get("x").getFloatValue();
						float y = c.getAttributes().get("y").getFloatValue();
						float w = c.getAttributes().get("w").getFloatValue();
						float h = c.getAttributes().get("h").getFloatValue();

						//dir,speed
						float dir = c.getAttributes().get("dir").getFloatValue();
						float spd = c.getAttributes().get("spd").getFloatValue();

						MovingModel mv = createMovingModel(c.getElement("MV").get(0));
						//ip
						ImagePainter ip = ImagePainterStorage.getInstance().get(c.getAttributes().get("ip").getValue());

						//image
						BufferedImage image = ImageUtil.load(c.getAttributes().get("image").getValue());
						if(!events.containsKey(frame))events.put(frame, new ArrayList<FrameTimeEvent<?>>());
						events.get(frame).add(new CreateEvent<ImageSprite>(frame, () -> new ImageSprite(x, y, w, h, new KVector(dir, spd), mv, image, ip)));

						break;
				}

			}

		}
	}

	public void print(){
		// イベント投入完了
		kinugasa.game.GameLog.print("event list");
		List<Long> key = new ArrayList<>(events.keySet());
		Collections.sort(key);
		for(Long l : key) kinugasa.game.GameLog.print(l + ":" + events.get(l));
		
	}
	public boolean hasEvent(long frame){
		return events.containsKey(frame);
	}
	
	public List<FrameTimeEvent<?>> getEvents(long frame){
		return events.get(frame);
	}
	
	public void add(FrameTimeEvent<?>... e){
		for (FrameTimeEvent<?> e1 : e) {
			if (events.containsKey(e1.getFrame())) {
				events.get(e1.getFrame()).add(e1);
			} else {
				events.put(e1.getFrame(), new ArrayList<>(Arrays.asList(e1)));
			}
		}
	}
	

	private MovingModel createMovingModel(XMLElement mv) {
		String type = mv.getAttributes().get("type").getValue();
		switch(type){
			case "AngleChange":
				float avalue = mv.getAttributes().get("value").getFloatValue();
				return  new CompositeMove(BasicMoving.getInstance(), new AngleChange(avalue));
			case "SpeedChange":
				float svalue = mv.getAttributes().get("value").getFloatValue();
				return  new CompositeMove(BasicMoving.getInstance(), new SpeedChange(svalue));
			default:
				throw new IllegalArgumentException("illegal type :" + type);
		}
	}

}
