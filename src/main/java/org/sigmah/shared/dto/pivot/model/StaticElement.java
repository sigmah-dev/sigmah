package org.sigmah.shared.dto.pivot.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import org.sigmah.shared.dto.pivot.content.TableContent;


public class StaticElement extends ReportElement<TableContent> implements Serializable {
	
	private String text;
	private String img;
	
	@XmlElement
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement
	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
