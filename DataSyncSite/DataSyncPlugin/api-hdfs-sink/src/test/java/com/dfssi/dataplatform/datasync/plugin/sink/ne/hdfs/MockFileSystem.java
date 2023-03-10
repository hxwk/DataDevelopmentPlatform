package com.dfssi.dataplatform.datasync.plugin.sink.ne.hdfs;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockFileSystem extends FileSystem {

    private static final Logger logger =
            LoggerFactory.getLogger(MockFileSystem.class);

    FileSystem fs;
    int numberOfRetriesRequired;
    MockFsDataOutputStream latestOutputStream;
    int currentRenameAttempts;
    boolean closeSucceed = true;

    public MockFileSystem(FileSystem fs, int numberOfRetriesRequired) {
        this.fs = fs;
        this.numberOfRetriesRequired = numberOfRetriesRequired;
    }

    public MockFileSystem(FileSystem fs,
                          int numberOfRetriesRequired, boolean closeSucceed) {
        this.fs = fs;
        this.numberOfRetriesRequired = numberOfRetriesRequired;
        this.closeSucceed = closeSucceed;
    }

    @Override
    public FSDataOutputStream append(Path arg0, int arg1, Progressable arg2)
            throws IOException {

        latestOutputStream = new MockFsDataOutputStream(
                fs.append(arg0, arg1, arg2), closeSucceed);

        return latestOutputStream;
    }

    @Override
    public FSDataOutputStream create(Path arg0) throws IOException {
        latestOutputStream = new MockFsDataOutputStream(fs.create(arg0), closeSucceed);
        return latestOutputStream;
    }

    @Override
    public FSDataOutputStream create(Path arg0, FsPermission arg1, boolean arg2, int arg3,
                                     short arg4, long arg5, Progressable arg6)
            throws IOException {
        throw new IOException("Not a real file system");
    }

    @Override
    @Deprecated
    public boolean delete(Path arg0) throws IOException {
        return fs.delete(arg0);
    }

    @Override
    public boolean delete(Path arg0, boolean arg1) throws IOException {
        return fs.delete(arg0, arg1);
    }

    @Override
    public FileStatus getFileStatus(Path arg0) throws IOException {
        return fs.getFileStatus(arg0);
    }

    @Override
    public URI getUri() {
        return fs.getUri();
    }

    @Override
    public Path getWorkingDirectory() {
        return fs.getWorkingDirectory();
    }

    @Override
    public FileStatus[] listStatus(Path arg0) throws IOException {
        return fs.listStatus(arg0);
    }

    @Override
    public boolean mkdirs(Path arg0, FsPermission arg1) throws IOException {
        // TODO Auto-generated method stub
        return fs.mkdirs(arg0, arg1);
    }

    @Override
    public FSDataInputStream open(Path arg0, int arg1) throws IOException {
        return fs.open(arg0, arg1);
    }

    @Override
    public boolean rename(Path arg0, Path arg1) throws IOException {
        currentRenameAttempts++;
        logger.info("Attempting to Rename: '" + currentRenameAttempts + "' of '" +
                numberOfRetriesRequired + "'");
        if (currentRenameAttempts >= numberOfRetriesRequired || numberOfRetriesRequired == 0) {
            logger.info("Renaming file");
            return fs.rename(arg0, arg1);
        } else {
            throw new IOException("MockIOException");
        }
    }

    @Override
    public void setWorkingDirectory(Path arg0) {
        fs.setWorkingDirectory(arg0);
    }
}
