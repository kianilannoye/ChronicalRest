package be.ugent.entitity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "symptom")
public class Symptom implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int symptomID;
	private String name;
	private String description;
	private String category;
	private int duration;
		
	public Symptom() {
		// TODO Auto-generated constructor stub
	}

	public int getSymptomID() {
		return symptomID;
	}

	public void setSymptomID(int drugID) {
		this.symptomID = drugID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}	
	
}
