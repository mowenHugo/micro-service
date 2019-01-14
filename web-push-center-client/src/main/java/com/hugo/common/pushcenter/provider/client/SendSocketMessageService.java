package com.hugo.common.pushcenter.provider.client;

import com.hugo.api.PushInterface;
import com.hugo.api.api.model.PushMessage;
import com.hugo.api.api.model.RspData;
import com.hugo.api.api.service.SendSocketMessageRemoteService;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author wangweiguang
 */
@FeignClient(name = PushInterface.SERVICE_NAME, fallback = SendSocketMessageService.HystrixClientFallback.class)
public interface SendSocketMessageService extends SendSocketMessageRemoteService {

    @Component
    public static class HystrixClientFallback implements SendSocketMessageService {
        @Override
        public RspData<Integer> sendSocketMessage(PushMessage Message) {
            return null;
        }

        @Override
        public RspData<List<String>> sendSocketMessages(List<PushMessage> Messages) {
            return null;
        }
    }
}
