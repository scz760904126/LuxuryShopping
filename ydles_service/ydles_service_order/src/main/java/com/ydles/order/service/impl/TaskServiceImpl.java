package com.ydles.order.service.impl;

import com.ydles.order.dao.TaskHisMapper;
import com.ydles.order.dao.TaskMapper;
import com.ydles.order.pojo.Task;
import com.ydles.order.pojo.TaskHis;
import com.ydles.order.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Scz
 * @date 2022/4/21 11:03
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    TaskMapper taskMapper;
    @Autowired
    TaskHisMapper taskHisMapper;

    @Override
    @Transactional
    public void delTask(Task task) {
        // 历史积分表tb_task_his添加数据
        TaskHis taskHis = new TaskHis();
        BeanUtils.copyProperties(task, taskHis);
        taskHis.setId(null);
        taskHis.setDeleteTime(new Date());
        taskHisMapper.insertSelective(taskHis);
        // 任务表中 tb_task 删除数据
        taskMapper.deleteByPrimaryKey(task.getId());
        System.out.println("任务表处理完毕");
    }
}
