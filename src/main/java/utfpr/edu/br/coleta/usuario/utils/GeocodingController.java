package utfpr.edu.br.coleta.usuario.utils;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

@RestController
@RequestMapping("/api/utils")
public class GeocodingController {

    @GetMapping("/geocode")
    public ResponseEntity<String> geocode(@RequestParam("q") String q) {
        var client = HttpClient.newHttpClient();
        var uri = "https://nominatim.openstreetmap.org/search?format=json&limit=1&countrycodes=br&q="
                + URLEncoder.encode(q, StandardCharsets.UTF_8);

        var req = HttpRequest.newBuilder(URI.create(uri))
                .header("User-Agent", "PortalCidadão/1.0 (contato@prefeitura.com)")
                .header("Accept-Language", "pt-BR")
                .build();
        try {
            var res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (Exception e) {
            return ResponseEntity.status(502).body("{\"error\":\"geocode failed\"}");
        }
    }
}