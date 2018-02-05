package rd.zhang.aio.kotlin.core.http;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rd.zhang.aio.kotlin.core.http.model.UploadFileInfo;


/**
 * Created by Richard on 2017/8/23.
 */

public class FilePorts {

    private List<UploadFileInfo> files = new ArrayList<>();

    public int getSize() {
        return files.size();
    }

    public FilePorts file(List<String> files) {
        for (String str : files) {
            UploadFileInfo fileInfo = new UploadFileInfo();
            fileInfo.setCompress(false);
            File file = new File(str);
            fileInfo.setFile(file);
            fileInfo.setName(file.getName());
            this.files.add(fileInfo);
        }
        return this;
    }

    public FilePorts image(List<String> files, boolean compress) {
        for (String str : files) {
            UploadFileInfo fileInfo = new UploadFileInfo();
            fileInfo.setCompress(compress);
            File file = new File(str);
            fileInfo.setFile(file);
            fileInfo.setName(file.getName());
            this.files.add(fileInfo);
        }
        return this;
    }

    /**
     * 图片
     *
     * @param file
     * @param compress
     * @return
     */
    public FilePorts image(File file, boolean compress) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(compress);
        fileInfo.setFile(file);
        fileInfo.setName(file.getName());
        this.files.add(fileInfo);
        return this;
    }

    /**
     * 图片
     *
     * @param path
     * @param compress
     * @return
     */
    public FilePorts image(String path, boolean compress) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(compress);
        File file = new File(path);
        fileInfo.setFile(file);
        fileInfo.setName(file.getName());
        this.files.add(fileInfo);
        return this;
    }

    /**
     * 图片
     *
     * @param file
     * @return
     */
    public FilePorts image(File file) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(false);
        fileInfo.setFile(file);
        fileInfo.setName(file.getName());
        this.files.add(fileInfo);
        return this;
    }

    /**
     * 图片
     *
     * @param path
     * @return
     */
    public FilePorts image(String path) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(false);
        File file = new File(path);
        fileInfo.setFile(file);
        fileInfo.setName(file.getName());
        this.files.add(fileInfo);
        return this;
    }

    /**
     * 文件
     *
     * @param file
     * @return
     */
    public FilePorts file(File file) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(false);
        fileInfo.setName(file.getName());
        fileInfo.setFile(file);
        this.files.add(fileInfo);
        return this;
    }

    /**
     * 文件
     *
     * @param path
     * @return
     */
    public FilePorts file(String path) {
        UploadFileInfo fileInfo = new UploadFileInfo();
        fileInfo.setCompress(false);
        File file = new File(path);
        fileInfo.setFile(file);
        fileInfo.setName(file.getName());
        this.files.add(fileInfo);
        return this;
    }

    public UploadFileInfo get(int index) {
        return files.get(index);
    }
}
