package org.example.panda.feignClient.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReniecResponse {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String tipoDocumento;
    private String numeroDocumento;
    private String digitoVerificador;
}
