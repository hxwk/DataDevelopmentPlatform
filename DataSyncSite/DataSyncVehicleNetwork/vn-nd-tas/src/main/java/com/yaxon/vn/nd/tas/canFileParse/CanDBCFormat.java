package com.yaxon.vn.nd.tas.canFileParse;

/**
 * x0705 CAN T38 DBC file format type
 * @author jianKang
 * @date 2017/12/26
 */
public enum CanDBCFormat {
    /**
     * old template format, such as
     * BO_ 2365522225 DEC1: 8 MYECU
     SG_ 1034◎VECU伪油门_W : 8|16@1+(0.0390625,0)[0|100] "%" Vector__XXX
     */
    OLD,
    /**
     * current template format, such as
     * BO_ 205848358 VCU_BCA_4: 8 Vector__XXX
     SG_ 269◎制动踏板开度 : 0|8@0+ (1,0) [0|255] "%" Vector__XXX
     SG_ 1793◎最高报警等级 : 8|8@0+ (1,0) [0|255] "" Vector__XXX
     */
    CURRENT
}
