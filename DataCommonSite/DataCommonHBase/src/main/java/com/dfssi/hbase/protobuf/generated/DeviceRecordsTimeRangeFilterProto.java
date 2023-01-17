// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: DeviceRecordsTimeRangeFilter.proto

package com.dfssi.hbase.protobuf.generated;

public final class DeviceRecordsTimeRangeFilterProto {
  private DeviceRecordsTimeRangeFilterProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface DeviceRecordsTimeRangeFilterOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required bytes columnFamily = 1;</code>
     *
     * <pre>
     *版本过滤的列族
     * </pre>
     */
    boolean hasColumnFamily();
    /**
     * <code>required bytes columnFamily = 1;</code>
     *
     * <pre>
     *版本过滤的列族
     * </pre>
     */
    com.google.protobuf.ByteString getColumnFamily();

    /**
     * <code>required bytes columnQualifier = 2;</code>
     *
     * <pre>
     *版本过滤字段
     * </pre>
     */
    boolean hasColumnQualifier();
    /**
     * <code>required bytes columnQualifier = 2;</code>
     *
     * <pre>
     *版本过滤字段
     * </pre>
     */
    com.google.protobuf.ByteString getColumnQualifier();

    /**
     * <code>optional sint64 upperBound = 3;</code>
     *
     * <pre>
     *版本上界
     * </pre>
     */
    boolean hasUpperBound();
    /**
     * <code>optional sint64 upperBound = 3;</code>
     *
     * <pre>
     *版本上界
     * </pre>
     */
    long getUpperBound();

    /**
     * <code>optional sint64 lowerBound = 4;</code>
     *
     * <pre>
     *版本下界
     * </pre>
     */
    boolean hasLowerBound();
    /**
     * <code>optional sint64 lowerBound = 4;</code>
     *
     * <pre>
     *版本下界
     * </pre>
     */
    long getLowerBound();

    /**
     * <code>optional bool filterOnMatch = 5;</code>
     *
     * <pre>
     *当条件满足则马上过滤,不再往下匹配
     * </pre>
     */
    boolean hasFilterOnMatch();
    /**
     * <code>optional bool filterOnMatch = 5;</code>
     *
     * <pre>
     *当条件满足则马上过滤,不再往下匹配
     * </pre>
     */
    boolean getFilterOnMatch();

    /**
     * <code>optional bool filterIfMissing = 6;</code>
     *
     * <pre>
     *不满足条件是否过滤整行
     * </pre>
     */
    boolean hasFilterIfMissing();
    /**
     * <code>optional bool filterIfMissing = 6;</code>
     *
     * <pre>
     *不满足条件是否过滤整行
     * </pre>
     */
    boolean getFilterIfMissing();
  }
  /**
   * Protobuf type {@code com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter}
   */
  public static final class DeviceRecordsTimeRangeFilter extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter)
      DeviceRecordsTimeRangeFilterOrBuilder {
    // Use DeviceRecordsTimeRangeFilter.newBuilder() to construct.
    private DeviceRecordsTimeRangeFilter(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private DeviceRecordsTimeRangeFilter(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final DeviceRecordsTimeRangeFilter defaultInstance;
    public static DeviceRecordsTimeRangeFilter getDefaultInstance() {
      return defaultInstance;
    }

    public DeviceRecordsTimeRangeFilter getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private DeviceRecordsTimeRangeFilter(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              columnFamily_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              columnQualifier_ = input.readBytes();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              upperBound_ = input.readSInt64();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              lowerBound_ = input.readSInt64();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              filterOnMatch_ = input.readBool();
              break;
            }
            case 48: {
              bitField0_ |= 0x00000020;
              filterIfMissing_ = input.readBool();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return DeviceRecordsTimeRangeFilterProto.internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return DeviceRecordsTimeRangeFilterProto.internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              DeviceRecordsTimeRangeFilter.class, Builder.class);
    }

    public static com.google.protobuf.Parser<DeviceRecordsTimeRangeFilter> PARSER =
        new com.google.protobuf.AbstractParser<DeviceRecordsTimeRangeFilter>() {
      public DeviceRecordsTimeRangeFilter parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new DeviceRecordsTimeRangeFilter(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<DeviceRecordsTimeRangeFilter> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int COLUMNFAMILY_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString columnFamily_;
    /**
     * <code>required bytes columnFamily = 1;</code>
     *
     * <pre>
     *版本过滤的列族
     * </pre>
     */
    public boolean hasColumnFamily() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required bytes columnFamily = 1;</code>
     *
     * <pre>
     *版本过滤的列族
     * </pre>
     */
    public com.google.protobuf.ByteString getColumnFamily() {
      return columnFamily_;
    }

    public static final int COLUMNQUALIFIER_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString columnQualifier_;
    /**
     * <code>required bytes columnQualifier = 2;</code>
     *
     * <pre>
     *版本过滤字段
     * </pre>
     */
    public boolean hasColumnQualifier() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required bytes columnQualifier = 2;</code>
     *
     * <pre>
     *版本过滤字段
     * </pre>
     */
    public com.google.protobuf.ByteString getColumnQualifier() {
      return columnQualifier_;
    }

    public static final int UPPERBOUND_FIELD_NUMBER = 3;
    private long upperBound_;
    /**
     * <code>optional sint64 upperBound = 3;</code>
     *
     * <pre>
     *版本上界
     * </pre>
     */
    public boolean hasUpperBound() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional sint64 upperBound = 3;</code>
     *
     * <pre>
     *版本上界
     * </pre>
     */
    public long getUpperBound() {
      return upperBound_;
    }

    public static final int LOWERBOUND_FIELD_NUMBER = 4;
    private long lowerBound_;
    /**
     * <code>optional sint64 lowerBound = 4;</code>
     *
     * <pre>
     *版本下界
     * </pre>
     */
    public boolean hasLowerBound() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>optional sint64 lowerBound = 4;</code>
     *
     * <pre>
     *版本下界
     * </pre>
     */
    public long getLowerBound() {
      return lowerBound_;
    }

    public static final int FILTERONMATCH_FIELD_NUMBER = 5;
    private boolean filterOnMatch_;
    /**
     * <code>optional bool filterOnMatch = 5;</code>
     *
     * <pre>
     *当条件满足则马上过滤,不再往下匹配
     * </pre>
     */
    public boolean hasFilterOnMatch() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>optional bool filterOnMatch = 5;</code>
     *
     * <pre>
     *当条件满足则马上过滤,不再往下匹配
     * </pre>
     */
    public boolean getFilterOnMatch() {
      return filterOnMatch_;
    }

    public static final int FILTERIFMISSING_FIELD_NUMBER = 6;
    private boolean filterIfMissing_;
    /**
     * <code>optional bool filterIfMissing = 6;</code>
     *
     * <pre>
     *不满足条件是否过滤整行
     * </pre>
     */
    public boolean hasFilterIfMissing() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }
    /**
     * <code>optional bool filterIfMissing = 6;</code>
     *
     * <pre>
     *不满足条件是否过滤整行
     * </pre>
     */
    public boolean getFilterIfMissing() {
      return filterIfMissing_;
    }

    private void initFields() {
      columnFamily_ = com.google.protobuf.ByteString.EMPTY;
      columnQualifier_ = com.google.protobuf.ByteString.EMPTY;
      upperBound_ = 0L;
      lowerBound_ = 0L;
      filterOnMatch_ = false;
      filterIfMissing_ = false;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasColumnFamily()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasColumnQualifier()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, columnFamily_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, columnQualifier_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeSInt64(3, upperBound_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeSInt64(4, lowerBound_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeBool(5, filterOnMatch_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeBool(6, filterIfMissing_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, columnFamily_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, columnQualifier_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeSInt64Size(3, upperBound_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeSInt64Size(4, lowerBound_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(5, filterOnMatch_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(6, filterIfMissing_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static DeviceRecordsTimeRangeFilter parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static DeviceRecordsTimeRangeFilter parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static DeviceRecordsTimeRangeFilter parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static DeviceRecordsTimeRangeFilter parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(DeviceRecordsTimeRangeFilter prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter)
        DeviceRecordsTimeRangeFilterOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return DeviceRecordsTimeRangeFilterProto.internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return DeviceRecordsTimeRangeFilterProto.internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                DeviceRecordsTimeRangeFilter.class, Builder.class);
      }

      // Construct using com.bh.d406.bigdata.hbase.protobuf.generated.DeviceRecordsTimeRangeFilterProto.DeviceRecordsTimeRangeFilter.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        columnFamily_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        columnQualifier_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        upperBound_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000004);
        lowerBound_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000008);
        filterOnMatch_ = false;
        bitField0_ = (bitField0_ & ~0x00000010);
        filterIfMissing_ = false;
        bitField0_ = (bitField0_ & ~0x00000020);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return DeviceRecordsTimeRangeFilterProto.internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor;
      }

      public DeviceRecordsTimeRangeFilter getDefaultInstanceForType() {
        return DeviceRecordsTimeRangeFilter.getDefaultInstance();
      }

      public DeviceRecordsTimeRangeFilter build() {
        DeviceRecordsTimeRangeFilter result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public DeviceRecordsTimeRangeFilter buildPartial() {
        DeviceRecordsTimeRangeFilter result = new DeviceRecordsTimeRangeFilter(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.columnFamily_ = columnFamily_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.columnQualifier_ = columnQualifier_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.upperBound_ = upperBound_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.lowerBound_ = lowerBound_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.filterOnMatch_ = filterOnMatch_;
        if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
          to_bitField0_ |= 0x00000020;
        }
        result.filterIfMissing_ = filterIfMissing_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof DeviceRecordsTimeRangeFilter) {
          return mergeFrom((DeviceRecordsTimeRangeFilter)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(DeviceRecordsTimeRangeFilter other) {
        if (other == DeviceRecordsTimeRangeFilter.getDefaultInstance()) return this;
        if (other.hasColumnFamily()) {
          setColumnFamily(other.getColumnFamily());
        }
        if (other.hasColumnQualifier()) {
          setColumnQualifier(other.getColumnQualifier());
        }
        if (other.hasUpperBound()) {
          setUpperBound(other.getUpperBound());
        }
        if (other.hasLowerBound()) {
          setLowerBound(other.getLowerBound());
        }
        if (other.hasFilterOnMatch()) {
          setFilterOnMatch(other.getFilterOnMatch());
        }
        if (other.hasFilterIfMissing()) {
          setFilterIfMissing(other.getFilterIfMissing());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasColumnFamily()) {
          
          return false;
        }
        if (!hasColumnQualifier()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        DeviceRecordsTimeRangeFilter parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (DeviceRecordsTimeRangeFilter) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.ByteString columnFamily_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes columnFamily = 1;</code>
       *
       * <pre>
       *版本过滤的列族
       * </pre>
       */
      public boolean hasColumnFamily() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required bytes columnFamily = 1;</code>
       *
       * <pre>
       *版本过滤的列族
       * </pre>
       */
      public com.google.protobuf.ByteString getColumnFamily() {
        return columnFamily_;
      }
      /**
       * <code>required bytes columnFamily = 1;</code>
       *
       * <pre>
       *版本过滤的列族
       * </pre>
       */
      public Builder setColumnFamily(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        columnFamily_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes columnFamily = 1;</code>
       *
       * <pre>
       *版本过滤的列族
       * </pre>
       */
      public Builder clearColumnFamily() {
        bitField0_ = (bitField0_ & ~0x00000001);
        columnFamily_ = getDefaultInstance().getColumnFamily();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString columnQualifier_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes columnQualifier = 2;</code>
       *
       * <pre>
       *版本过滤字段
       * </pre>
       */
      public boolean hasColumnQualifier() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required bytes columnQualifier = 2;</code>
       *
       * <pre>
       *版本过滤字段
       * </pre>
       */
      public com.google.protobuf.ByteString getColumnQualifier() {
        return columnQualifier_;
      }
      /**
       * <code>required bytes columnQualifier = 2;</code>
       *
       * <pre>
       *版本过滤字段
       * </pre>
       */
      public Builder setColumnQualifier(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        columnQualifier_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes columnQualifier = 2;</code>
       *
       * <pre>
       *版本过滤字段
       * </pre>
       */
      public Builder clearColumnQualifier() {
        bitField0_ = (bitField0_ & ~0x00000002);
        columnQualifier_ = getDefaultInstance().getColumnQualifier();
        onChanged();
        return this;
      }

      private long upperBound_ ;
      /**
       * <code>optional sint64 upperBound = 3;</code>
       *
       * <pre>
       *版本上界
       * </pre>
       */
      public boolean hasUpperBound() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional sint64 upperBound = 3;</code>
       *
       * <pre>
       *版本上界
       * </pre>
       */
      public long getUpperBound() {
        return upperBound_;
      }
      /**
       * <code>optional sint64 upperBound = 3;</code>
       *
       * <pre>
       *版本上界
       * </pre>
       */
      public Builder setUpperBound(long value) {
        bitField0_ |= 0x00000004;
        upperBound_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional sint64 upperBound = 3;</code>
       *
       * <pre>
       *版本上界
       * </pre>
       */
      public Builder clearUpperBound() {
        bitField0_ = (bitField0_ & ~0x00000004);
        upperBound_ = 0L;
        onChanged();
        return this;
      }

      private long lowerBound_ ;
      /**
       * <code>optional sint64 lowerBound = 4;</code>
       *
       * <pre>
       *版本下界
       * </pre>
       */
      public boolean hasLowerBound() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>optional sint64 lowerBound = 4;</code>
       *
       * <pre>
       *版本下界
       * </pre>
       */
      public long getLowerBound() {
        return lowerBound_;
      }
      /**
       * <code>optional sint64 lowerBound = 4;</code>
       *
       * <pre>
       *版本下界
       * </pre>
       */
      public Builder setLowerBound(long value) {
        bitField0_ |= 0x00000008;
        lowerBound_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional sint64 lowerBound = 4;</code>
       *
       * <pre>
       *版本下界
       * </pre>
       */
      public Builder clearLowerBound() {
        bitField0_ = (bitField0_ & ~0x00000008);
        lowerBound_ = 0L;
        onChanged();
        return this;
      }

      private boolean filterOnMatch_ ;
      /**
       * <code>optional bool filterOnMatch = 5;</code>
       *
       * <pre>
       *当条件满足则马上过滤,不再往下匹配
       * </pre>
       */
      public boolean hasFilterOnMatch() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>optional bool filterOnMatch = 5;</code>
       *
       * <pre>
       *当条件满足则马上过滤,不再往下匹配
       * </pre>
       */
      public boolean getFilterOnMatch() {
        return filterOnMatch_;
      }
      /**
       * <code>optional bool filterOnMatch = 5;</code>
       *
       * <pre>
       *当条件满足则马上过滤,不再往下匹配
       * </pre>
       */
      public Builder setFilterOnMatch(boolean value) {
        bitField0_ |= 0x00000010;
        filterOnMatch_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bool filterOnMatch = 5;</code>
       *
       * <pre>
       *当条件满足则马上过滤,不再往下匹配
       * </pre>
       */
      public Builder clearFilterOnMatch() {
        bitField0_ = (bitField0_ & ~0x00000010);
        filterOnMatch_ = false;
        onChanged();
        return this;
      }

      private boolean filterIfMissing_ ;
      /**
       * <code>optional bool filterIfMissing = 6;</code>
       *
       * <pre>
       *不满足条件是否过滤整行
       * </pre>
       */
      public boolean hasFilterIfMissing() {
        return ((bitField0_ & 0x00000020) == 0x00000020);
      }
      /**
       * <code>optional bool filterIfMissing = 6;</code>
       *
       * <pre>
       *不满足条件是否过滤整行
       * </pre>
       */
      public boolean getFilterIfMissing() {
        return filterIfMissing_;
      }
      /**
       * <code>optional bool filterIfMissing = 6;</code>
       *
       * <pre>
       *不满足条件是否过滤整行
       * </pre>
       */
      public Builder setFilterIfMissing(boolean value) {
        bitField0_ |= 0x00000020;
        filterIfMissing_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional bool filterIfMissing = 6;</code>
       *
       * <pre>
       *不满足条件是否过滤整行
       * </pre>
       */
      public Builder clearFilterIfMissing() {
        bitField0_ = (bitField0_ & ~0x00000020);
        filterIfMissing_ = false;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter)
    }

    static {
      defaultInstance = new DeviceRecordsTimeRangeFilter(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.bh.d406.bigdata.hbase.protobuf.DeviceRecordsTimeRangeFilter)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\"DeviceRecordsTimeRangeFilter.proto\022\"co" +
      "m.bh.d406.bigdata.hbase.protobuf\"\245\001\n\034Dev" +
      "iceRecordsTimeRangeFilter\022\024\n\014columnFamil" +
      "y\030\001 \002(\014\022\027\n\017columnQualifier\030\002 \002(\014\022\022\n\nuppe" +
      "rBound\030\003 \001(\022\022\022\n\nlowerBound\030\004 \001(\022\022\025\n\rfilt" +
      "erOnMatch\030\005 \001(\010\022\027\n\017filterIfMissing\030\006 \001(\010" +
      "BQ\n,com.bh.d406.bigdata.hbase.protobuf.g" +
      "eneratedB!DeviceRecordsTimeRangeFilterPr" +
      "oto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_bh_d406_bigdata_hbase_protobuf_DeviceRecordsTimeRangeFilter_descriptor,
        new String[] { "ColumnFamily", "ColumnQualifier", "UpperBound", "LowerBound", "FilterOnMatch", "FilterIfMissing", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
