/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.springboot.starter.event;

import cn.hippo4j.common.function.NoArgsConsumer;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.AVAILABLE_PROCESSORS;

/**
 * 动态线程池监控事件执行器.
 *
 * @author chen.ma
 * @date 2021/11/8 23:44
 */
@Slf4j
public class MonitorEventExecutor {

    private static final ExecutorService EVENT_EXECUTOR = ThreadPoolBuilder.builder()
            .threadFactory("client.monitor.event.executor")
            .corePoolSize(AVAILABLE_PROCESSORS)
            .maxPoolNum(AVAILABLE_PROCESSORS)
            .workQueue(QueueTypeEnum.LINKED_BLOCKING_QUEUE)
            .capacity(4096)
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    public static void publishEvent(NoArgsConsumer consumer) {
        try {
            EVENT_EXECUTOR.execute(consumer::accept);
        } catch (RejectedExecutionException ex) {
            log.error("Monitoring thread pool run events exceeded load.");
        }
    }
}
