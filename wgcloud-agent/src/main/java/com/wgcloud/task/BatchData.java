package com.wgcloud.task;

import com.wgcloud.entity.LogInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public  class BatchData {
    public static List<LogInfo> LOG_INFO_LIST = Collections.synchronizedList(new ArrayList<LogInfo>());
}
