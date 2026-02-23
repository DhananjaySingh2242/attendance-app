package ampliedtech.com.attendanceApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves /favicon.ico so the browser request does not hit the static resource handler
 * and trigger NoResourceFoundException (which would otherwise return 500).
 */
@RestController
public class FaviconController {

    @GetMapping("favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
