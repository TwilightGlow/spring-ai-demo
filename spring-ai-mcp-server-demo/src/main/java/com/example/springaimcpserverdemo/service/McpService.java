package com.example.springaimcpserverdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class McpService {

    @Tool(description = "获取城市的天气信息")
    public String getWeatherByCity(@ToolParam(required = true, description = "城市名称") String city) {
        log.info("获取城市天气信息：{}", city);
        return city + "天气信息：晴空万里，气温25°C";
    }
}
