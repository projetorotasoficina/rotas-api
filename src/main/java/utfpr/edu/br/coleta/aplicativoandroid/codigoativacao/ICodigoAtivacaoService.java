package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import utfpr.edu.br.coleta.generics.ICrudService;

/**
 * Interface de serviço para operações com códigos de ativação.
 * 
 * Estende ICrudService para herdar operações CRUD básicas e adiciona
 * métodos específicos para o gerenciamento de códigos de ativação.
 * 
 * @author Luiz Alberto dos Passos
 */
public interface ICodigoAtivacaoService extends ICrudService<CodigoAtivacao, Long> {

    /**
     * Gera um novo código de ativação único.
     * 
     * @return o código de ativação gerado
     */
    CodigoAtivacao gerarNovoCodigo();

    /**
     * Busca um código de ativação pelo código e que esteja ativo.
     * 
     * @param codigo o código a ser buscado
     * @return o código de ativação encontrado, ou null se não existir ou não estiver ativo
     */
    CodigoAtivacao buscarPorCodigo(String codigo);

    /**
     * Marca um código como usado.
     * 
     * @param codigo o código a ser marcado como usado
     * @param deviceId o ID do dispositivo que usou o código
     */
    void marcarComoUsado(String codigo, String deviceId);
}

