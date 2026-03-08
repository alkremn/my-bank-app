package co.kremnev.transfer.controller;

import co.kremnev.transfer.controller.dto.TransferRequestDto;
import co.kremnev.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public void transfer(@RequestBody TransferRequestDto request) {
        transferService.transfer(request.getFromLogin(), request.getToLogin(), request.getAmount());
    }
}
