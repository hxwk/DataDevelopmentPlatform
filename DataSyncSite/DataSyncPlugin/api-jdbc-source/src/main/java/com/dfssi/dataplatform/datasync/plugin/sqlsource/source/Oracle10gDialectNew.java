package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * Created by jian on 2017/12/22.
 */
public class Oracle10gDialectNew  extends Oracle10gDialect {
    public Oracle10gDialectNew() {
        super();
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
    }

    @Override
    protected void registerCharacterTypeMappings() {
        registerColumnType( Types.NVARCHAR, 4000, "varchar2($l char)" );
    }
}
