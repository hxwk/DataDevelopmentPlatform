package com.dfssi.dataplatform.ide.cleantransform.mvc.entity;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hongs on 2018/6/1.
 */
@Data
public class TaskCleanTransformModelEntity implements Serializable {

    @Valid
    private TaskCleanTransformEntity taskCleanTransformEntity;
    @Valid
    private List<TaskCleanTransformMapperEntity> taskCleanTransformMapperEntityys;

}
