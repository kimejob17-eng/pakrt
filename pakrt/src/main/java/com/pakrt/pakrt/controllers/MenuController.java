package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.models.menu.Menu;
import com.pakrt.pakrt.request.menu.MenuRequest;
import com.pakrt.pakrt.responses.BasicResponse;
import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.MenuService;
import com.pakrt.pakrt.utils.DataMap;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * POST /menu/create
     * Sesuai standar Nexus Multilateral
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MenuRequest request) {
        try {
            Menu created = menuService.create(request);
            return ResponseBuilder.buildSuccessResponse(created);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * PUT /menu/update
     * Sesuai standar Nexus Multilateral
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody MenuRequest request) {
        try {
            Menu updated = menuService.update(request);
            return ResponseBuilder.buildSuccessResponse(updated);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400204", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * GET /menu/list
     * Query: pn (page number), ps (page size), n (name filter)
     * Sesuai standar Nexus Multilateral
     */
    @GetMapping("/list")
    public ResponseEntity<?> list(
            @RequestParam(required = false) Integer pn,
            @RequestParam(required = false) Integer ps,
            @RequestParam(required = false, name = "n") String name,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyEnabled) {
        try {
            List<Menu> resultList = menuService.getAll(name, onlyEnabled);
            return ResponseEntity.ok(ResultResponse.Get("200100", DataMap.Mapped(resultList)));
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * GET /menu/tree
     * Mengambil struktur menu induk beserta sub-menu aktif untuk navigasi frontend dinamis.
     */
    @GetMapping("/tree")
    public ResponseEntity<?> tree() {
        try {
            List<Menu> tree = menuService.getMenuTree();
            return ResponseEntity.ok(ResultResponse.Get("200100", tree));
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * GET /menu/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Menu detail = menuService.getById(id);
            return ResponseBuilder.buildSuccessResponse(detail);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * POST /menu/delete
     * Sesuai standar Nexus Multilateral
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestBody MenuRequest request) {
        try {
            menuService.delete(request.getId());
            return ResponseEntity.ok(BasicResponse.Get("200100", "Menu berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }

    /**
     * DELETE /menu/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            menuService.delete(id);
            return ResponseEntity.ok(BasicResponse.Get("200100", "Menu berhasil dihapus"));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("400404", e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}
