/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
		// ƒCƒxƒ“ƒg“Š“üŠ®—¹
		System.out.println("event list");
		List<Long> key = new ArrayList<>(events.keySet());
		Collections.sort(key);
		for(Long l : key) System.out.println(l + ":" + events.get(l));
		
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
