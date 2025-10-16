package utfpr.edu.br.coleta.caminhao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaminhaoRepository extends JpaRepository<Caminhao, Long> {
    Optional<Caminhao> findByPlaca(String placa);
    boolean existsByPlaca(String placa);

    /**
     * Busca caminhões por modelo ou placa (case-insensitive).
     *
     * @param modelo termo de busca para modelo
     * @param placa termo de busca para placa
     * @param pageable informações de paginação
     * @return página de caminhões que correspondem à busca
     */
    Page<Caminhao> findByModeloContainingIgnoreCaseOrPlacaContainingIgnoreCase(String modelo, String placa, Pageable pageable);
}