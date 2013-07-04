package com.subsolr.fieldprocessors;

import java.util.List;

public interface FieldProcessor<T> {
	List<T> generateTerms();
}
