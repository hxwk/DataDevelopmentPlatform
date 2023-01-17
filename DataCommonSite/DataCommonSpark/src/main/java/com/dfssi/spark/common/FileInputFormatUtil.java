package com.dfssi.spark.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.net.NodeBase;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class FileInputFormatUtil{

    public static final Log LOG = LogFactory.getLog(FileInputFormatUtil.class);
    private static long minSplitSize = 1L;

    private FileInputFormatUtil(){}

    private static boolean isSplitable(FileSystem fs, Path filename) {
        return true;
    }

    public static PathFilter getInputPathFilter(JobConf conf) {
        Class filterClass = conf.getClass("mapred.input.pathFilter.class", (Class)null, PathFilter.class);
        return filterClass != null?(PathFilter)ReflectionUtils.newInstance(filterClass, conf):null;
    }


    public static InputSplit[] getSplits(JobConf job, FileStatus[] files, int numSplits) throws IOException {

        job.setLong("mapreduce.input.num.files", (long)files.length);
        long totalSize = 0L;
        FileStatus[] goalSize = files;
        int len$ = files.length;

        for(int minSize = 0; minSize < len$; ++minSize) {
            FileStatus file = goalSize[minSize];
            if(file.isDirectory()) {
                throw new IOException("Not a file: " + file.getPath());
            }
            totalSize += file.getLen();
        }

        long var29 = totalSize / (long)(numSplits == 0?1:numSplits);
        long var30 = Math.max(job.getLong("mapred.min.split.size", 1L), minSplitSize);
        ArrayList splits = new ArrayList(numSplits);
        NetworkTopology clusterMap = new NetworkTopology();
        FileStatus[] arr$ = files;
        int len$1 = files.length;

        for(int i$ = 0; i$ < len$1; ++i$) {
            FileStatus file1 = arr$[i$];
            Path path = file1.getPath();
            FileSystem fs = path.getFileSystem(job);
            long length = file1.getLen();
            BlockLocation[] blkLocations = fs.getFileBlockLocations(file1, 0L, length);
            if(length != 0L && isSplitable(fs, path)) {
                long var28 = file1.getBlockSize();
                long splitSize = computeSplitSize(var29, var30, var28);

                long bytesRemaining;
                for(bytesRemaining = length; (double)bytesRemaining / (double)splitSize > 1.1D; bytesRemaining -= splitSize) {
                    String[] splitHosts1 = getSplitHosts(blkLocations, length - bytesRemaining, splitSize, clusterMap);
                    splits.add(new FileSplit(path, length - bytesRemaining, splitSize, splitHosts1));
                }

                if(bytesRemaining != 0L) {
                    splits.add(new FileSplit(path, length - bytesRemaining, bytesRemaining, blkLocations[blkLocations.length - 1].getHosts()));
                }
            } else if(length != 0L) {
                String[] splitHosts = getSplitHosts(blkLocations, 0L, length, clusterMap);
                splits.add(new FileSplit(path, 0L, length, splitHosts));
            } else {
                splits.add(new FileSplit(path, 0L, length, new String[0]));
            }
        }

        LOG.debug("Total # of splits: " + splits.size());
        return (InputSplit[])splits.toArray(new FileSplit[splits.size()]);
    }

    private  static long computeSplitSize(long goalSize, long minSize, long blockSize) {
        return Math.max(minSize, Math.min(goalSize, blockSize));
    }

    private static int getBlockIndex(BlockLocation[] blkLocations, long offset) {
        for(int last = 0; last < blkLocations.length; ++last) {
            if(blkLocations[last].getOffset() <= offset && offset < blkLocations[last].getOffset() + blkLocations[last].getLength()) {
                return last;
            }
        }

        BlockLocation var7 = blkLocations[blkLocations.length - 1];
        long fileLength = var7.getOffset() + var7.getLength() - 1L;
        throw new IllegalArgumentException("Offset " + offset + " is outside of file (0.." + fileLength + ")");
    }

    public static Path[] getInputPaths(JobConf conf) {
        String dirs = conf.get("mapred.input.dir", "");
        String[] list = StringUtils.split(dirs);
        Path[] result = new Path[list.length];

        for(int i = 0; i < list.length; ++i) {
            result[i] = new Path(StringUtils.unEscapeString(list[i]));
        }

        return result;
    }

    private static void sortInDescendingOrder(List<NodeInfo> mylist) {
        Collections.sort(mylist, new Comparator<NodeInfo>() {
            public int compare(NodeInfo obj1, NodeInfo obj2) {
                return obj1 != null && obj2 != null?(obj1.getValue() == obj2.getValue()?0:(obj1.getValue() < obj2.getValue()?1:-1)):-1;
            }
        });
    }

    private static String[] getSplitHosts(BlockLocation[] blkLocations, long offset, long splitSize, NetworkTopology clusterMap) throws IOException {
        int startIndex = getBlockIndex(blkLocations, offset);
        long bytesInThisBlock = blkLocations[startIndex].getOffset() + blkLocations[startIndex].getLength() - offset;
        if(bytesInThisBlock >= splitSize) {
            return blkLocations[startIndex].getHosts();
        } else {
            long bytesInFirstBlock = bytesInThisBlock;
            int index = startIndex + 1;

            for(splitSize -= bytesInThisBlock; splitSize > 0L; splitSize -= bytesInThisBlock) {
                bytesInThisBlock = Math.min(splitSize, blkLocations[index++].getLength());
            }

            long bytesInLastBlock = bytesInThisBlock;
            int endIndex = index - 1;
            IdentityHashMap hostsMap = new IdentityHashMap();
            IdentityHashMap racksMap = new IdentityHashMap();
            String[] allTopos = new String[0];

            for(index = startIndex; index <= endIndex; ++index) {
                if(index == startIndex) {
                    bytesInThisBlock = bytesInFirstBlock;
                } else if(index == endIndex) {
                    bytesInThisBlock = bytesInLastBlock;
                } else {
                    bytesInThisBlock = blkLocations[index].getLength();
                }

                allTopos = blkLocations[index].getTopologyPaths();
                if(allTopos.length == 0) {
                    allTopos = fakeRacks(blkLocations, index);
                }

                String[] arr$ = allTopos;
                int len$ = allTopos.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    String topo = arr$[i$];
                    Object node = clusterMap.getNode(topo);
                    if(node == null) {
                        node = new NodeBase(topo);
                        clusterMap.add((Node)node);
                    }

                   NodeInfo nodeInfo = (NodeInfo)hostsMap.get(node);
                    Node parentNode;
                    NodeInfo parentNodeInfo;
                    if(nodeInfo == null) {
                        nodeInfo = new NodeInfo((Node)node);
                        hostsMap.put(node, nodeInfo);
                        parentNode = ((Node)node).getParent();
                        parentNodeInfo = (NodeInfo)racksMap.get(parentNode);
                        if(parentNodeInfo == null) {
                            parentNodeInfo = new NodeInfo(parentNode);
                            racksMap.put(parentNode, parentNodeInfo);
                        }

                        parentNodeInfo.addLeaf(nodeInfo);
                    } else {
                        nodeInfo = (NodeInfo)hostsMap.get(node);
                        parentNode = ((Node)node).getParent();
                        parentNodeInfo = (NodeInfo)racksMap.get(parentNode);
                    }

                    nodeInfo.addValue(index, bytesInThisBlock);
                    parentNodeInfo.addValue(index, bytesInThisBlock);
                }
            }

            return identifyHosts(allTopos.length, racksMap);
        }
    }

    private static String[] identifyHosts(int replicationFactor, Map<Node, NodeInfo> racksMap) {
        String[] retVal = new String[replicationFactor];
        LinkedList rackList = new LinkedList();
        rackList.addAll(racksMap.values());
        sortInDescendingOrder(rackList);
        boolean done = false;
        int index = 0;
        Iterator i$ = rackList.iterator();

        while(i$.hasNext()) {
            NodeInfo ni = (NodeInfo)i$.next();
            Set hostSet = ni.getLeaves();
            LinkedList hostList = new LinkedList();
            hostList.addAll(hostSet);
            sortInDescendingOrder(hostList);
            Iterator i$1 = hostList.iterator();

            while(i$1.hasNext()) {
                NodeInfo host = (NodeInfo)i$1.next();
                retVal[index++] = host.node.getName().split(":")[0];
                if(index == replicationFactor) {
                    done = true;
                    break;
                }
            }

            if(done) {
                break;
            }
        }

        return retVal;
    }

    private static String[] fakeRacks(BlockLocation[] blkLocations, int index) throws IOException {
        String[] allHosts = blkLocations[index].getHosts();
        String[] allTopos = new String[allHosts.length];

        for(int i = 0; i < allHosts.length; ++i) {
            allTopos[i] = "/default-rack/" + allHosts[i];
        }

        return allTopos;
    }

    private static class NodeInfo {
        final Node node;
        final Set<Integer> blockIds;
        final Set<NodeInfo> leaves;
        private long value;

        NodeInfo(Node node) {
            this.node = node;
            this.blockIds = new HashSet();
            this.leaves = new HashSet();
        }

        long getValue() {
            return this.value;
        }

        void addValue(int blockIndex, long value) {
            if(this.blockIds.add(Integer.valueOf(blockIndex))) {
                this.value += value;
            }

        }

        Set<NodeInfo> getLeaves() {
            return this.leaves;
        }

        void addLeaf(NodeInfo nodeInfo) {
            this.leaves.add(nodeInfo);
        }
    }
}
