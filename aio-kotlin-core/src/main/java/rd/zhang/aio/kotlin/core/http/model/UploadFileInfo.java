package rd.zhang.aio.kotlin.core.http.model;

import java.io.File;

/**
 * Created by Richard on 2017/6/23.
 */

public class UploadFileInfo {

    private File file;
    private String name;
    private boolean compress;

    public File getFile() {
        return file;
    }

    public UploadFileInfo setFile(File file) {
        this.file = file;
        return this;
    }

    public String getName() {
        return name;
    }

    public UploadFileInfo setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isCompress() {
        return compress;
    }

    public UploadFileInfo setCompress(boolean compress) {
        this.compress = compress;
        return this;
    }
}
