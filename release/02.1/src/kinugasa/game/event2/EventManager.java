/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.event2;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImagePainterStorage;
import kinugasa.object.ImageSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.TVector;
import kinugasa.object.movemodel.AngleChange;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_8:08:29<br>
 * @author Dra211<br>
 */
public class EventManager implements XMLFileSupport {

	private HashMap<Long, List<Event<?>>> events = new HashMap<>();

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

						//mv
						;
						MovingModel mv = createMovingModel(c.getElement("MV").get(0));
						//ip
						ImagePainter ip = ImagePainterStorage.getInstance().get(c.getAttributes().get("ip").getValue());

						//image
						BufferedImage image = ImageUtil.load(c.getAttributes().get("image").getValue());
						if(!events.containsKey(frame))events.put(frame, new ArrayList<Event<?>>());
						events.get(frame).add(new CreateEvent<ImageSprite>(frame, () -> new ImageSprite(x, y, w, h, new TVector(dir, spd), mv, image, ip)));

						break;
				}

			}

		}
		
		// ƒCƒxƒ“ƒg“Š“üŠ®—¹
		System.out.println("event list");
		List<Long> key = new ArrayList<>(events.keySet());
		Collections.sort(key);
		for(Long l : key) System.out.println(l + ":" + events.get(l));
		
		

	}
	
	public boolean hasEvent(long frame){
		return events.containsKey(frame);
	}
	
	public List<Event<?>> getEvents(long frame){
		return events.get(frame);
	}
	
	

	private MovingModel createMovingModel(XMLElement mv) {
		String type = mv.getAttributes().get("type").getValue();
		switch(type){
			case "AngleChange":
				float value = mv.getAttributes().get("value").getFloatValue();
				return new AngleChange(value);

			default:
				throw new IllegalArgumentException("illegal type :" + type);
		}
	}

}
