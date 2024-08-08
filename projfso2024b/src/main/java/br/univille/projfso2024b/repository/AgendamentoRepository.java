package br.univille.projfso2024b.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.univille.projfso2024b.entity.Agendamento;
import br.univille.projfso2024b.entity.Cliente;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByCliente(Cliente cliente);
}
