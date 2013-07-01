package com.subsolr.entityprocessors;

import java.util.List;

import com.subsolr.entityprocessors.model.Record;

public interface EntityProcessor {
	List<Record> getRecords();
}
