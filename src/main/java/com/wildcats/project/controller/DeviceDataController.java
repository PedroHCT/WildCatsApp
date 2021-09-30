package com.wildcats.project.controller;

import com.wildcats.project.dto.DeviceDataDto;
import com.wildcats.project.entity.DeviceData;
import com.wildcats.project.mapper.DeviceDataMapper;
import com.wildcats.project.request.DeviceDataRequest;
import com.wildcats.project.response.DeviceDataResponse;
import com.wildcats.project.service.DeviceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/devicedata")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;
    private final DeviceDataMapper deviceDataMapper;

    @GetMapping
    public List<DeviceDataDto> getAll() {
        List<DeviceData> deviceDatas = this.deviceDataService.getAll();
        return deviceDataMapper.toListDto(deviceDatas);
    }

    @PostMapping
    public DeviceDataResponse save(@RequestBody @Valid DeviceDataRequest deviceDataRequest){
        DeviceData deviceData = this.deviceDataService.salvar(deviceDataRequest);
        return deviceDataMapper.toSingleResult(deviceData);
    }
}
