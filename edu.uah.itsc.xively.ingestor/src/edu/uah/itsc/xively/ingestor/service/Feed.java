package edu.uah.itsc.xively.ingestor.service;

import java.util.Vector;

public class Feed {
	
	//Vairlables
	private String id;
	private String device_serial;
	private String title;
	private String description;
	private String feedUrl;
	private String status;
	private String updated;
	private String created;
	private String creator;
	private String version;
	Vector<Datastream> dataStream;
	private String product_id;
	private boolean private_status;
	private float lat;
	private float lon;
	
	//Constructor
	public Feed(){
		this.dataStream = new Vector<Datastream>();
	}
	
	//Add a new dataStream to feed
	public void addDatastream(Datastream datastream){
		this.dataStream.add(datastream);
	}
	
	//Get existing datastream
	public Datastream getDatastream(int index){
		return this.dataStream.get(index);
	}
	
	//Get the count of datastreams
	public int getDatastreamLength(){
		return this.dataStream.size();
	}
	
	//Getters and Setters
	public void setLat(float lat) {
		this.lat = lat;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFeedUrl() {
		return feedUrl;
	}
	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getDeviceSerial() {
		return this.device_serial;
	}

	public void setDeviceSerial(String string) {
		this.device_serial = string;		
	}
	public String getProductId() {
		return this.product_id;
	}
	public boolean getPrivate() {
		return this.private_status;
	}
	public void setProductId(String string) {
		this.product_id = string;
		
	}
	public void setPrivate(String string) {
		this.private_status = Boolean.parseBoolean(string);
	}
	public float getLat() {
		return this.lat;
	}
	public float getLon() {
		return this.lon;
	}

	//Get the list of all datastream Ids
	public Vector<String> getDatastreamIds() {
		Vector<String> dataStreamIds = new Vector<String>();
		for(Datastream ds : this.dataStream){
			dataStreamIds.add(ds.getStreamId().toLowerCase());
		}
		return dataStreamIds;
	}

	//Get the symbol for given datastream
	public String getUnitSymbolFor(String s) {
		for(Datastream ds : this.dataStream){
			if(ds.getStreamId().compareTo(s.toLowerCase()) == 0){
				return ds.getUnitSymbol();
			}
		}
		return null;
	}
	
	//Get the label for given datastream
	public String getUnitLabelFor(String s) {
		for(Datastream ds : this.dataStream){
			if(ds.getStreamId().compareTo(s.toLowerCase()) == 0){
				return ds.getUnitLabel();
			}
		}
		return null;
	}

}
