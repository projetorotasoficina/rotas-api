package utfpr.edu.br.coleta.tiporesiduo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TipoResiduoService extends CrudServiceImpl<TipoResiduo, Long> {

    private final TipoResiduoRepository repository;
    private final EntityManager entityManager;

    public TipoResiduoService(TipoResiduoRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    protected TipoResiduoRepository getRepository() {
        return repository;
    }

    public void validateUniqueName(TipoResiduo entity) {
        Optional<TipoResiduo> byName = repository.findByNome(entity.getNome());
        if (byName.isPresent()) {
            Long existingId = byName.get().getId();
            if (entity.getId() == null || !existingId.equals(entity.getId())) {
                throw new IllegalArgumentException("Já existe um TipoResiduo com esse nome.");
            }
        }
    }

    @Transactional
    public TipoResiduo saveWithValidation(TipoResiduo entity) {
        validateUniqueName(entity);
        return repository.save(entity);
    }


    @Transactional
    public void deleteByIdBlockingIfReferenced(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("TipoResiduo não encontrado para id = " + id);
        }

        List<String> checks = Arrays.asList(
                "tb_caminhao",
                "tb_rota",
                "tb_trajeto",
                "tb_mapa"
        );

        for (String table : checks) {
            String sql = String.format("SELECT COUNT(1) FROM %s WHERE fk_id_tipo_residuo = :id", table);
            try {
                Query q = entityManager.createNativeQuery(sql);
                q.setParameter("id", id);
                Number count = (Number) q.getSingleResult();
                if (count != null && count.longValue() > 0) {
                    throw new IllegalStateException(
                            String.format("Não é possível excluir: TipoResiduo id=%d é referenciado pela tabela %s.", id, table)
                    );
                }
            } catch (Exception ex) {

            }
        }

        repository.deleteById(id);
    }

    @Override
    public Page<TipoResiduo> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByNomeContainingIgnoreCase(search, pageable);
    }
}
