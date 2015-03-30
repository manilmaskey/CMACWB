package edu.uah.itsc.radar.services;

public class FieldTypeInfoHeader {

	String fieldName;
	String fieldDescription;
	int keyboardAccelerator;
	String units;
	int fieldNumber;
	int factor;
	int scale;
	int bias;
	int maxFactorScaledValue;
	int minFactorScaledValue;
	short fieldDataFlags;
	short colorMapType;

	//Constructor
	public FieldTypeInfoHeader(){}

	//Getter and Setters
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDescription() {
		return fieldDescription;
	}

	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	public int getKeyboardAccelerator() {
		return keyboardAccelerator;
	}

	public void setKeyboardAccelerator(int keyboardAccelerator) {
		this.keyboardAccelerator = keyboardAccelerator;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public int getFieldNumber() {
		return fieldNumber;
	}

	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	public int getFactor() {
		return factor;
	}

	public void setFactor(int factor) {
		this.factor = factor;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getBias() {
		return bias;
	}

	public void setBias(int bias) {
		this.bias = bias;
	}

	public int getMaxFactorScaledValue() {
		return maxFactorScaledValue;
	}

	public void setMaxFactorScaledValue(int maxFactorScaledValue) {
		this.maxFactorScaledValue = maxFactorScaledValue;
	}

	public int getMinFactorScaledValue() {
		return minFactorScaledValue;
	}

	public void setMinFactorScaledValue(int minFactorScaledValue) {
		this.minFactorScaledValue = minFactorScaledValue;
	}

	public short getFieldDataFlags() {
		return fieldDataFlags;
	}

	public void setFieldDataFlags(short fieldDataFlags) {
		this.fieldDataFlags = fieldDataFlags;
	}

	public short getColorMapType() {
		return colorMapType;
	}

	public void setColorMapType(short colorMapType) {
		this.colorMapType = colorMapType;
	}

	public float getValue(byte b){
		return ((b * scale) + bias) / ((float)factor);
	}
	
	@Override
	public String toString() {
		return "FieldTypeInfoHeader [fieldName=" + fieldName
				+ ", fieldDescription=" + fieldDescription
				+ ", keyboardAccelerator=" + keyboardAccelerator + ", units="
				+ units + ", fieldNumber=" + fieldNumber + ", factor=" + factor
				+ ", scale=" + scale + ", bias=" + bias
				+ ", maxFactorScaledValue=" + maxFactorScaledValue
				+ ", minFactorScaledValue=" + minFactorScaledValue
				+ ", fieldDataFlags=" + fieldDataFlags + ", colorMapType="
				+ colorMapType + "]";
	}

	public void display() {
		System.out.println("fieldName=" + fieldName);
		System.out.println("fieldDescription=" + fieldDescription);
		System.out.println("keyboardAccelerator=" + keyboardAccelerator);
		System.out.println("units=" + units);
		System.out.println("fieldNumber=" + fieldNumber);
		System.out.println("factor=" + factor);
		System.out.println("scale=" + scale);
		System.out.println("bias=" + bias);
		System.out.println("maxFactorScaledValue=" + maxFactorScaledValue);
		System.out.println("minFactorScaledValue=" + minFactorScaledValue);
		System.out.println("fieldDataFlags=" + fieldDataFlags);
		System.out.println("colorMapType=" + colorMapType);
	}

}
