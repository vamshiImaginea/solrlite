package com.subsolr.contextprocessor.model;

public class FieldDefinition {

	String fieldName;
	FieldTypeDefinition fieldTypeDefinition;
	boolean indexed;
	boolean stored;
	boolean analyzed;
	boolean mandatory;

	public FieldDefinition(String fieldName, FieldTypeDefinition fieldTypeDefinition, boolean indexed, boolean stored, boolean analyzed, boolean mandatory) {
		this.fieldName = fieldName;
		this.fieldTypeDefinition = fieldTypeDefinition;
		this.indexed = indexed;
		this.stored = stored;
		this.analyzed = analyzed;
		this.mandatory = mandatory;
	}

	public FieldDefinition(FieldDefinitionBuilder fieldDefinitionBuilder) {
		this.fieldName = fieldDefinitionBuilder.fieldName;
		this.fieldTypeDefinition = fieldDefinitionBuilder.fieldTypeDefinition;
		this.indexed = fieldDefinitionBuilder.indexed;
		this.stored = fieldDefinitionBuilder.stored;
		this.analyzed = fieldDefinitionBuilder.analyzed;
		this.mandatory = fieldDefinitionBuilder.mandatory;
	}

	public static class FieldDefinitionBuilder {
		String fieldName;
		FieldTypeDefinition fieldTypeDefinition;
		boolean indexed = true;
		boolean stored = true;
		boolean analyzed = false;
		boolean mandatory = true;

		public FieldDefinitionBuilder fieldName(String value) {
			this.fieldName = value;
			return this;
		}

		public FieldDefinitionBuilder fieldTypeDefinition(FieldTypeDefinition value) {
			this.fieldTypeDefinition = value;
			return this;
		}

		public FieldDefinitionBuilder stored(boolean value) {
			this.stored = value;
			return this;
		}

		public FieldDefinitionBuilder analyzed(boolean value) {
			this.analyzed = value;
			return this;
		}

		public FieldDefinitionBuilder mandatory(boolean value) {
			this.mandatory = value;
			return this;
		}
		public FieldDefinitionBuilder indexed(boolean value) {
			this.indexed = value;
			return this;
		}

		public FieldDefinition build() {
			return new FieldDefinition(this);
		}

	}
	
	public String getFieldName() {
		return fieldName;
	}

	public FieldTypeDefinition getFieldTypeDefinition() {
		return fieldTypeDefinition;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public boolean isStored() {
		return stored;
	}

	public boolean isAnalyzed() {
		return analyzed;
	}

	public boolean isMandatory() {
		return mandatory;
	}

}
