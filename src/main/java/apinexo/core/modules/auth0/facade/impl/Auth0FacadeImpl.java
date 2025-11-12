package apinexo.core.modules.auth0.facade.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import apinexo.common.dtos.AbstractService;
import apinexo.common.utils.ApinexoUtils;
import apinexo.core.modules.auth0.dto.Auth0ResendVerificationRequest;
import apinexo.core.modules.auth0.facade.Auth0Facade;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Auth0FacadeImpl extends AbstractService implements Auth0Facade {

    private final ApinexoUtils utils;

    @Override
    public ResponseEntity<Object> resendVerification(Auth0ResendVerificationRequest request) {
        try {
            return ResponseEntity.ok("");
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(utils.err(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(utils.err(ex.getMessage()));
        }
    }
}
