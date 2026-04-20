package co.kremnev.transfer.controller;

import co.kremnev.transfer.controller.dto.TransferRequestDto;
import co.kremnev.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public void transfer(@RequestBody TransferRequestDto request, JwtAuthenticationToken auth) {
        String fromLogin = auth.getToken().getClaimAsString("preferred_username");
        log.info("Transfer request: from={}, to={}, amount={}", fromLogin, request.getToLogin(), request.getAmount());
        transferService.transfer(fromLogin, request.getToLogin(), request.getAmount());
    }
}
