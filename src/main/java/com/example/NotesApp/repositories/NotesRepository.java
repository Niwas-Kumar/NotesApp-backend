package com.example.NotesApp.repositories;

import com.example.NotesApp.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotesRepository extends JpaRepository<Notes, Long> {
    Optional<Notes> findByLink(String link);


}
