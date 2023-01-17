package com.dfssi.dataplatform.ide.security.mapper;

import com.dfssi.dataplatform.ide.security.entity.SecurityAuth;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/3 18:39
 */
@Mapper
public interface SecurityAuthMapper {

    @Insert("INSERT INTO security_auth(auth_name, auth_desc, auth_type, auth_user, auth_password, creator, editor) VALUES(#{auth_name}, #{auth_desc}, #{auth_type},  #{auth_user},  #{auth_password}, #{creator}, #{creator})")
    int insertByEntity(SecurityAuth securityAuth);

    @Insert("INSERT INTO security_auth(auth_name, auth_desc, auth_type, auth_user, auth_password, creator, editor) VALUES(#{auth_name}, #{auth_desc}, #{auth_type},  #{auth_user},  #{auth_password}, #{creator}, #{creator})")
    int insert(@Param("auth_name") String auth_name,
               @Param("auth_desc") String auth_desc,
               @Param("auth_type") String auth_type,
               @Param("auth_user") String auth_user,
               @Param("auth_password") String auth_password,
               @Param("creator") String creator);



    @Update("UPDATE security_auth SET auth_name=#{auth_name}, auth_desc=#{auth_desc}, auth_type=#{auth_type}, auth_user=#{auth_user},  auth_password=#{auth_password}, editor=#{editor}, update_date=current_timestamp WHERE id=#{id}")
    void updateByEntity(SecurityAuth securityAuth);

    @SelectProvider(type = AuthDaoProvider.class, method = "updateWithId")
    void updateWithId(@Param("id") int id,
                      @Param("auth_name") String auth_name,
                      @Param("auth_desc") String auth_desc,
                      @Param("auth_type") String auth_type,
                      @Param("auth_user") String auth_user,
                      @Param("auth_password") String auth_password,
                      @Param("editor") String editor);

    @Delete("DELETE FROM security_auth WHERE id =#{id}")
    void delete(int id);

    @SelectProvider(type = AuthDaoProvider.class, method = "findAllSQL")
    List<SecurityAuth> findAll(@Param("orderField")String orderField,
                               @Param("orderType")String orderType);

    @SelectProvider(type = AuthDaoProvider.class, method = "findByCondition")
    List<SecurityAuth> findByCondition(@Param("auth_name") String auth_name,
                                       @Param("auth_type") String auth_type,
                                       @Param("starttime") String starttime,
                                       @Param("endtime") String endtime,
                                       @Param("orderField")String orderField,
                                       @Param("orderType")String orderType);

    class AuthDaoProvider {

        public String findAllSQL(@Param("orderField")String orderField,
                                 @Param("orderType")String orderType){

            return new SQL(){
                {
                    SELECT("*");
                    FROM("security_auth");

                    if(orderField != null){
                        ORDER_BY(String.format("%s %s", orderField, orderType));
                    }else{
                        ORDER_BY("create_date desc");
                    }
                }
            }.toString();
        }

        public String findByCondition(@Param("auth_name") String auth_name,
                                      @Param("auth_type") String auth_type,
                                      @Param("starttime") String starttime,
                                      @Param("endtime") String endtime,
                                      @Param("orderField")String orderField,
                                      @Param("orderType")String orderType) {
            return new SQL() {
                {
                    SELECT("*");
                    FROM("security_auth");
                    if (auth_name != null) {
                        WHERE("auth_name like '%" + auth_name + "%'");
                    }
                    if (auth_type != null) {
                        WHERE("auth_type = #{auth_type}");
                    }

                    if (starttime != null) {
                        WHERE("create_date >= #{starttime}");
                    }

                    if (endtime != null) {
                        WHERE("create_date <= #{endtime}");
                    }

                    if(orderField != null){
                        ORDER_BY(String.format("%s %s", orderField, orderType));
                    }else{
                        ORDER_BY("create_date desc");
                    }
                }
            }.toString();

        }

        public String updateWithId(@Param("id") int id,
                                   @Param("auth_name") String auth_name,
                                   @Param("auth_desc") String auth_desc,
                                   @Param("auth_type") String auth_type,
                                   @Param("auth_user") String auth_user,
                                   @Param("auth_password") String auth_password,
                                   @Param("editor") String editor) {
            return new SQL() {
                {
                    UPDATE("security_auth");

                    if (auth_name != null)
                        SET("auth_name=#{auth_name}");

                    if (auth_desc != null)
                        SET("auth_desc=#{auth_desc}");

                    if (auth_type != null)
                        SET("auth_type=#{auth_type}");

                    if(auth_user != null)
                        SET("auth_user=#{auth_user}");

                    if(auth_password != null)
                        SET("auth_password=#{auth_password}");


                    SET("editor=#{editor}");
                    SET("update_date=current_timestamp");

                    WHERE("id = #{id}");
                }
            }.toString();

        }
    }
}
