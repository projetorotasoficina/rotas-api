package utfpr.edu.br.coleta.motorista.validator;

import org.springframework.stereotype.Component;
import utfpr.edu.br.coleta.caminhao.Caminhao;
import utfpr.edu.br.coleta.caminhao.enums.TipoVeiculo;
import utfpr.edu.br.coleta.motorista.Motorista;
import utfpr.edu.br.coleta.motorista.enums.CategoriaCNH;

/**
 * Validador responsável por verificar a compatibilidade entre
 * a categoria de CNH do motorista e o tipo de veículo do caminhão.
 *
 * Implementa as regras de negócio conforme legislação brasileira
 * (Resolução CONTRAN nº 168/2004).
 *
 * Autor: Sistema
 */
@Component
public class CNHVeiculoValidator {

    /**
     * Valida se um motorista pode dirigir um determinado caminhão.
     *
     * @param motorista Motorista a ser validado
     * @param caminhao Caminhão a ser validado
     * @throws IllegalArgumentException se a validação falhar
     */
    public void validar(Motorista motorista, Caminhao caminhao) {
        // Validação 1: Motorista não pode ser nulo
        if (motorista == null) {
            throw new IllegalArgumentException("Motorista não pode ser nulo.");
        }

        // Validação 2: Caminhão não pode ser nulo
        if (caminhao == null) {
            throw new IllegalArgumentException("Caminhão não pode ser nulo.");
        }

        // Validação 3: Motorista deve estar ativo
        if (!Boolean.TRUE.equals(motorista.getAtivo())) {
            throw new IllegalArgumentException(
                String.format("Motorista %s está inativo e não pode ser associado a veículos.", 
                    motorista.getNome())
            );
        }

        // Validação 4: Caminhão deve estar ativo
        if (!Boolean.TRUE.equals(caminhao.getAtivo())) {
            throw new IllegalArgumentException(
                String.format("Caminhão %s (placa %s) está inativo e não pode ser associado a motoristas.", 
                    caminhao.getModelo(), caminhao.getPlaca())
            );
        }

        // Validação 5: Motorista deve ter CNH cadastrada
        if (motorista.getCnhCategoria() == null) {
            throw new IllegalArgumentException(
                String.format("Motorista %s não possui categoria de CNH cadastrada.", 
                    motorista.getNome())
            );
        }

        // Validação 6: Caminhão deve ter tipo de veículo cadastrado
        if (caminhao.getTipoVeiculo() == null) {
            throw new IllegalArgumentException(
                String.format("Caminhão %s (placa %s) não possui tipo de veículo cadastrado.", 
                    caminhao.getModelo(), caminhao.getPlaca())
            );
        }

        // Validação 7: CNH deve estar dentro da validade
        if (motorista.getCnhValidade() != null && 
            motorista.getCnhValidade().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                String.format("CNH do motorista %s está vencida (validade: %s).", 
                    motorista.getNome(), motorista.getCnhValidade())
            );
        }

        // Validação 8: Categoria da CNH deve ser compatível com o tipo de veículo
        CategoriaCNH categoriaCNH = motorista.getCnhCategoria();
        TipoVeiculo tipoVeiculo = caminhao.getTipoVeiculo();

        if (!tipoVeiculo.permiteCategoriaCNH(categoriaCNH)) {
            throw new IllegalArgumentException(
                String.format(
                    "Motorista %s com CNH categoria %s não pode dirigir %s (placa %s). %s",
                    motorista.getNome(),
                    categoriaCNH.name(),
                    caminhao.getModelo(),
                    caminhao.getPlaca(),
                    tipoVeiculo.getMensagemErroIncompatibilidade(categoriaCNH)
                )
            );
        }
    }

    /**
     * Verifica se um motorista pode dirigir um determinado caminhão (sem lançar exceção).
     *
     * @param motorista Motorista a ser validado
     * @param caminhao Caminhão a ser validado
     * @return true se o motorista pode dirigir o caminhão, false caso contrário
     */
    public boolean podeConduzir(Motorista motorista, Caminhao caminhao) {
        try {
            validar(motorista, caminhao);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retorna a mensagem de erro caso a validação falhe.
     *
     * @param motorista Motorista a ser validado
     * @param caminhao Caminhão a ser validado
     * @return Mensagem de erro ou null se a validação passar
     */
    public String getMensagemErro(Motorista motorista, Caminhao caminhao) {
        try {
            validar(motorista, caminhao);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}

