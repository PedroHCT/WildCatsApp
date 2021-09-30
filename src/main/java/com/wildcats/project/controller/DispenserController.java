package com.wildcats.project.controller;

import com.wildcats.project.dto.DispenserDto;
import com.wildcats.project.entity.Dispenser;
import com.wildcats.project.mapper.DispenserMapper;
import com.wildcats.project.request.DispenserRequest;
import com.wildcats.project.response.DispenserResponse;
import com.wildcats.project.response.DispenserResponseList;
import com.wildcats.project.service.DispenserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dispenser")
public class DispenserController {

    private final DispenserService dispenserService;
    private final DispenserMapper dispenserMapper;

    @GetMapping
    public List<DispenserDto> getAll() {
        List<Dispenser> dispensers = this.dispenserService.getAll();
        return dispenserMapper.toListDto(dispensers);
    }

    @PostMapping
    public DispenserResponse salvar(@RequestBody @Valid DispenserRequest request){
        Dispenser dispenser = this.dispenserService.salvar(request);
        return dispenserMapper.toSingleResult(dispenser);
    }

    @GetMapping("/{local}")
    public DispenserResponseList getByLocal(@PathVariable String local){
        List<Dispenser> dispensers = this.dispenserService.finByLocal(local);
        return dispenserMapper.toListResult(dispensers);
    }


}
