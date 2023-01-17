package com.dfssi.dataplatform.plugin.tcpnesource.net.codec;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * Created by yanghs on 2018/9/29.
 */
public class ProtoDivDecoder extends ByteToMessageDecoder {

    private final ByteBuf[] delimiters;
    private final int maxFrameLength;
    private final boolean stripDelimiter;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private int tooLongFrameLength;
    /** Set only when decoding with "\n" and "\r\n" as the delimiter.  */
    private final LineBasedFrameDecoder lineBasedDecoder;
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    public void printBuf(ByteBuf protoBuf){
        int  count= protoBuf.readableBytes();
        byte[] out = new byte[count];
        protoBuf.readBytes(out);
        protoBuf.resetReaderIndex();
        System.out.println("---------------------------"+hex.encode(out));
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param delimiter  the delimiter
     */
    public ProtoDivDecoder(int maxFrameLength, ByteBuf delimiter) {
        this(maxFrameLength, true, delimiter);
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param delimiter  the delimiter
     */
    public ProtoDivDecoder(
            int maxFrameLength, boolean stripDelimiter, ByteBuf delimiter) {
        this(maxFrameLength, stripDelimiter, true, delimiter);
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param failFast  If <tt>true</tt>, a {@link TooLongFrameException} is
     *                  thrown as soon as the decoder notices the length of the
     *                  frame will exceed <tt>maxFrameLength</tt> regardless of
     *                  whether the entire frame has been read.
     *                  If <tt>false</tt>, a {@link TooLongFrameException} is
     *                  thrown after the entire frame that exceeds
     *                  <tt>maxFrameLength</tt> has been read.
     * @param delimiter  the delimiter
     */
    public ProtoDivDecoder(
            int maxFrameLength, boolean stripDelimiter, boolean failFast,
            ByteBuf delimiter) {
        this(maxFrameLength, stripDelimiter, failFast, new ByteBuf[] {
                delimiter.slice()});
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param delimiters  the delimiters
     */
    public ProtoDivDecoder(int maxFrameLength, ByteBuf... delimiters) {
        this(maxFrameLength, true, delimiters);
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param delimiters  the delimiters
     */
    public ProtoDivDecoder(
            int maxFrameLength, boolean stripDelimiter, ByteBuf... delimiters) {
        this(maxFrameLength, stripDelimiter, true, delimiters);
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength  the maximum length of the decoded frame.
     *                        A {@link TooLongFrameException} is thrown if
     *                        the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param failFast  If <tt>true</tt>, a {@link TooLongFrameException} is
     *                  thrown as soon as the decoder notices the length of the
     *                  frame will exceed <tt>maxFrameLength</tt> regardless of
     *                  whether the entire frame has been read.
     *                  If <tt>false</tt>, a {@link TooLongFrameException} is
     *                  thrown after the entire frame that exceeds
     *                  <tt>maxFrameLength</tt> has been read.
     * @param delimiters  the delimiters
     */
    public ProtoDivDecoder(
            int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf... delimiters) {
        validateMaxFrameLength(maxFrameLength);
        if (delimiters == null) {
            throw new NullPointerException("delimiters");
        }
        if (delimiters.length == 0) {
            throw new IllegalArgumentException("empty delimiters");
        }

        if (isLineBased(delimiters) && !isSubclass()) {
            lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
            this.delimiters = null;
        } else {
            this.delimiters = new ByteBuf[delimiters.length];
            for (int i = 0; i < delimiters.length; i ++) {
                ByteBuf d = delimiters[i];
                validateDelimiter(d);
                this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
            }
            lineBasedDecoder = null;
        }
        this.maxFrameLength = maxFrameLength;
        this.stripDelimiter = stripDelimiter;
        this.failFast = failFast;
    }

    /** Returns true if the delimiters are "\n" and "\r\n".  */
    private static boolean isLineBased(final ByteBuf[] delimiters) {
        if (delimiters.length != 2) {
            return false;
        }
        ByteBuf a = delimiters[0];
        ByteBuf b = delimiters[1];
        if (a.capacity() < b.capacity()) {
            a = delimiters[1];
            b = delimiters[0];
        }
        return a.capacity() == 2 && b.capacity() == 1
                && a.getByte(0) == '\r' && a.getByte(1) == '\n'
                && b.getByte(0) == '\n';
    }

    /**
     * Return {@code true} if the current instance is a subclass of DelimiterBasedFrameDecoder
     */
    private boolean isSubclass() {
        return getClass() != ProtoDivDecoder.class;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    /**
     * Create a frame out of the {@link ByteBuf} and return it.
     *
     * @param   ctx             the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param   buffer          the {@link ByteBuf} from which to read data
     * @return  frame           the {@link ByteBuf} which represent the frame or {@code null} if no frame could
     *                          be created.
     */
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if (lineBasedDecoder != null) {
            return null;
        }
        // Try all delimiters and choose the delimiter which yields the shortest frame.
        int minFrameLength = Integer.MAX_VALUE;
        ByteBuf minDelim = null;
        for (ByteBuf delim: delimiters) {
            int frameLength = indexOf(buffer, delim);
            if (frameLength >= 0 && frameLength < minFrameLength) {
                minFrameLength = frameLength;
                minDelim = delim;
            }
        }

        if (minDelim != null) {
            int minDelimLength = minDelim.capacity();
            ByteBuf frame;

            if (discardingTooLongFrame) {
                // We've just finished discarding a very large frame.
                // Go back to the initial state.
                discardingTooLongFrame = false;
                buffer.skipBytes(minFrameLength + minDelimLength);

                int tooLongFrameLength = this.tooLongFrameLength;
                this.tooLongFrameLength = 0;
                if (!failFast) {
                    fail(tooLongFrameLength);
                }
                return null;
            }

            if (minFrameLength > maxFrameLength) {
                // Discard read frame.
                buffer.skipBytes(minFrameLength + minDelimLength);
                fail(minFrameLength);
                return null;
            }
            if (stripDelimiter) {
                ByteBuf databuf=  buffer.readRetainedSlice(minFrameLength);//获取数据
                if(databuf.capacity()>0){//数据不为空
                    frame = Unpooled.copiedBuffer(minDelim,databuf) ;//还原成包含##前缀的完整报文
                    if(!checkPack(frame)){//报文完整性检查它不通过时，通过报文中数据长度位获取数据
                        buffer.resetReaderIndex();
                        buffer.skipBytes(22);
                        ByteBuf datalengthbuf=  buffer.readBytes(2);
                        int datalength=datalengthbuf.readShort();
                        buffer.resetReaderIndex();
                        int temp=23+datalength;//20+2+datalength+1 不包含##前缀
                        if(temp>buffer.readableBytes()){
                            temp=buffer.readableBytes();
                        }
                        frame=Unpooled.copiedBuffer(minDelim,buffer.readBytes(temp));
                        if(!checkPack(frame)){//报文完整性检查它不通过时，证明收到的错误的报文，获取错误报文内容，让链路中其他处理器处理
                            buffer.resetReaderIndex();
                            databuf=  buffer.readRetainedSlice(minFrameLength);//获取数据
                            frame = Unpooled.copiedBuffer(minDelim,databuf) ;//还原成包含##前缀的完整报文
                        }
                    }
                    buffer.skipBytes(minDelimLength);
                }else{
//                    System.out.println("------------             -------------");
//                    buffer.skipBytes(minDelimLength);
//                    return null;


                    ByteBuf protoBuf=  Unpooled.copiedBuffer(buffer);
                    if(indexOf(protoBuf, minDelim)==-1){
                        frame = Unpooled.copiedBuffer(minDelim,buffer.readBytes(buffer.readableBytes()));
                    }else{
                        if(protoBuf.readableBytes()<25){
                            return null;
                        }
                        if(checkStartChar(protoBuf)){
                            protoBuf.markReaderIndex();
                            protoBuf.skipBytes(22);
                            ByteBuf datalengthbuf=  protoBuf.readBytes(2);
                            int datalength=datalengthbuf.readShort();
                            int temp=25+datalength;//22+2+datalength+1 包含##前缀
                            protoBuf.resetReaderIndex();
                            if(temp>buffer.readableBytes()){
                                return null;
                            }
                            frame=buffer.readBytes(temp);
                            if(buffer.readableBytes()>minDelimLength){
                                protoBuf.skipBytes(minDelimLength);
                            }
                        }else{
                            protoBuf.markReaderIndex();
                            protoBuf.skipBytes(20);
                            ByteBuf datalengthbuf=  protoBuf.readBytes(2);
                            int datalength=datalengthbuf.readShort();
                            int temp=23+datalength;//20+2+datalength+1 不包含##前缀
                            if(temp>buffer.readableBytes()){
                                return null;
                            }
                            protoBuf.resetReaderIndex();
                            frame=Unpooled.copiedBuffer(minDelim,buffer.readBytes(temp));
                        }
                    }
                }
            } else {
                frame = buffer.readRetainedSlice(minFrameLength + minDelimLength);
            }
            return frame;
        } else {
            if (!discardingTooLongFrame) {
                if (buffer.readableBytes() > maxFrameLength) {
                    // Discard the content of the buffer until a delimiter is found.
                    tooLongFrameLength = buffer.readableBytes();
                    buffer.skipBytes(buffer.readableBytes());
                    discardingTooLongFrame = true;
                    if (failFast) {
                        fail(tooLongFrameLength);
                    }
                }
            } else {
                // Still discarding the buffer since a delimiter is not found.
                tooLongFrameLength += buffer.readableBytes();
                buffer.skipBytes(buffer.readableBytes());
            }
            return null;
        }
    }

    private boolean checkPack(ByteBuf buf) {
        int bytes = buf.readableBytes();
        if (bytes < 2) {
            return false;
        }

        buf.markReaderIndex();

        byte checkSum = 0;
        for (int i = 0; i < bytes - 1; i++) {
            checkSum ^= buf.readByte();
        }

        if (checkSum == buf.readByte()) {
            buf.resetReaderIndex();
            return true;
        }
        return false;
    }

    /**
     * 检查是否以##（及0x23，0x23）开头
     * @param in
     * @return
     */
    private boolean checkStartChar(ByteBuf in) {
        boolean flag = false;

        in.markReaderIndex();

        byte b0 = in.readByte();
        byte b1 = in.readByte();

        if (0x23 == b0 && 0x23 == b1) {
            flag = true;
        }

        in.resetReaderIndex();

        return flag;
    }

    private void fail(long frameLength) {
        if (frameLength > 0) {
            throw new TooLongFrameException(
                    "frame length exceeds " + maxFrameLength +
                            ": " + frameLength + " - discarded");
        } else {
            throw new TooLongFrameException(
                    "frame length exceeds " + maxFrameLength +
                            " - discarding");
        }
    }

    /**
     * Returns the number of bytes between the readerIndex of the haystack and
     * the first needle found in the haystack.  -1 is returned if no needle is
     * found in the haystack.
     */
    private static int indexOf(ByteBuf haystack, ByteBuf needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i ++) {
            int haystackIndex = i;
            int needleIndex;
            for (needleIndex = 0; needleIndex < needle.capacity(); needleIndex ++) {
                if (haystack.getByte(haystackIndex) != needle.getByte(needleIndex)) {
                    break;
                } else {
                    haystackIndex ++;
                    if (haystackIndex == haystack.writerIndex() &&
                            needleIndex != needle.capacity() - 1) {
                        return -1;
                    }
                }
            }

            if (needleIndex == needle.capacity()) {
                // Found the needle from the haystack!
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }

    private static void validateDelimiter(ByteBuf delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter");
        }
        if (!delimiter.isReadable()) {
            throw new IllegalArgumentException("empty delimiter");
        }
    }

    private static void validateMaxFrameLength(int maxFrameLength) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException(
                    "maxFrameLength must be a positive integer: " +
                            maxFrameLength);
        }
    }
}
