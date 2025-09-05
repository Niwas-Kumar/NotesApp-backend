package com.example.NotesApp.controllers;

import com.example.NotesApp.entity.Notes;
import com.example.NotesApp.repositories.NotesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class NotesController {
    private final NotesRepository repo;

    public NotesController(NotesRepository repo){
        this.repo = repo;
    }

    // --- CRUD ---
    @GetMapping("/notes")
    public List<Notes> list() { return repo.findAll(); }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Notes> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/notes")
    public ResponseEntity<Notes> create(@RequestBody Notes note) {
        note.setId(null);
        Notes saved = repo.save(note);
        return ResponseEntity.created(URI.create("/api/notes/" + saved.getId())).body(saved);
    }

    @PutMapping("/notes/{id}")
    public ResponseEntity<Notes> update(@PathVariable Long id, @RequestBody Notes incoming) {
        return repo.findById(id).map(existing -> {
            existing.setTitle(incoming.getTitle());
            existing.setContent(incoming.getContent());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Share Note ---
    // --- Share Note ---
    @PostMapping("/notes/{id}/share")
    public ResponseEntity<?> share(@PathVariable Long id) {
        Optional<Notes> opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Notes note = opt.get();
        if (note.getLink() == null) {
            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            note.setLink(token);
            repo.save(note);
        }

        // Always return full frontend URL
        String frontendUrl = System.getenv().getOrDefault(
                "APP_FRONTEND_URL",
                "https://notes-app-frontend-five-azure.vercel.app" // fallback
        );
        String publicUrl = frontendUrl + "/n/" + note.getLink();

        return ResponseEntity.ok(java.util.Map.of("publicUrl", publicUrl));
    }



    // --- Public access via share token ---
    @GetMapping("/public/{link}")
    public ResponseEntity<Notes> publicByLink(@PathVariable String link) {
        return repo.findByLink(link)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
