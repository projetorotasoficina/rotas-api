package utfpr.edu.br.coleta.rota.validator;

import org.springframework.stereotype.Component;
import utfpr.edu.br.coleta.caminhao.Caminhao;
import utfpr.edu.br.coleta.rota.Rota;

/**
 * Validador responsável por verificar se um caminhão
 * pode atender uma determinada rota, com base no tipo
 * de coleta e no tipo de resíduo.
 */
@Component
public class RotaCaminhaoValidator {

    /**
     * Valida se um caminhão pode atender uma rota.
     *
     * @param caminhao caminhão a ser validado
     * @param rota rota a ser validada
     */
    public void validar(Caminhao caminhao, Rota rota) {

        if (caminhao == null) {
            throw new IllegalArgumentException("Caminhão não pode ser nulo.");
        }

        if (rota == null) {
            throw new IllegalArgumentException("Rota não pode ser nula.");
        }

        if (!Boolean.TRUE.equals(caminhao.getAtivo())) {
            throw new IllegalArgumentException(
                    String.format("Caminhão %s (placa %s) está inativo e não pode ser associado a rotas.",
                            caminhao.getModelo(), caminhao.getPlaca())
            );
        }

        if (!Boolean.TRUE.equals(rota.getAtivo())) {
            throw new IllegalArgumentException(
                    String.format("Rota %s está inativa e não pode ser atribuída a caminhões.",
                            rota.getNome())
            );
        }

        // Tipo de coleta deve ser igual
        if (caminhao.getTipoColeta() == null || rota.getTipoColeta() == null) {
            throw new IllegalArgumentException("Tipo de coleta deve estar definido para caminhão e rota.");
        }

        if (!caminhao.getTipoColeta().equals(rota.getTipoColeta())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Caminhão %s não é compatível com o tipo de coleta da rota %s.",
                            caminhao.getPlaca(), rota.getNome()
                    )
            );
        }

        // Tipo de resíduo deve ser igual
        if (caminhao.getResiduo() == null || rota.getTipoResiduo() == null) {
            throw new IllegalArgumentException("Tipo de resíduo deve estar definido para caminhão e rota.");
        }

        if (!caminhao.getResiduo().equals(rota.getTipoResiduo())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Caminhão %s não é compatível com o tipo de resíduo da rota %s.",
                            caminhao.getPlaca(), rota.getNome()
                    )
            );
        }
    }

    /**
     * Verifica se um caminhão pode atender uma rota (sem lançar exceção).
     *
     * @return true se puder atender, false caso contrário
     */
    public boolean podeAtender(Caminhao caminhao, Rota rota) {
        try {
            validar(caminhao, rota);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}