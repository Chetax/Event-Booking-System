package com.meetmint.meetmint_backend.Repository;

import com.meetmint.meetmint_backend.Model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
   List<Ticket> findByHolderEmail(String email);
   List<Ticket> findByHolderEmailOrderByCreatedAtDesc(String email);
   List<Ticket> findByEventId(Long eventId);
}
