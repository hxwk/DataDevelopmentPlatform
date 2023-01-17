package com.dfssi.dataplatform.datasync.plugin.sink.hdfs;

/**
 * Created by jian on 2017/12/4.
 */
public enum SequenceFileSerializerType {
    Writable(HDFSWritableSerializer.Builder.class),
    Text(HDFSTextSerializer.Builder.class),
    Other(null);

    private final Class<? extends SequenceFileSerializer.Builder> builderClass;

    SequenceFileSerializerType(Class<? extends SequenceFileSerializer.Builder> builderClass) {
        this.builderClass = builderClass;
    }

    public Class<? extends SequenceFileSerializer.Builder> getBuilderClass() {
        return builderClass;
    }

}