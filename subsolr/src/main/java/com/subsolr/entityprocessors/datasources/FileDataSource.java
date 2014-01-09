package com.subsolr.entityprocessors.datasources;

import java.io.Reader;

public abstract class FileDataSource implements DataSource {

    public abstract Reader getFileReader(String fileName);

}
