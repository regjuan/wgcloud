package com.wgcloud.task;

import com.wgcloud.entity.LogInfo;
import com.wgcloud.entity.ThreadState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public  class BatchData {
    public static List<LogInfo> LOG_INFO_LIST = Collections.synchronizedList(new ArrayList<LogInfo>());
    public static final List<ThreadState> THREAD_STATE_LIST = Collections.synchronizedList(new ArrayList<>());
}
